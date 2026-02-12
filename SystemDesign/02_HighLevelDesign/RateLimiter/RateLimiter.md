# Rate Limiter — High-Level Design

## Problem Statement

Design a distributed rate limiter that can:

- **Throttle API requests** per client/user/IP based on configurable policies
- **Support multiple rate limiting algorithms** (Token Bucket, Sliding Window, Fixed Window)
- **Work in a distributed environment** (multiple API servers sharing rate limit state)
- **Handle millions of requests per second** with sub-millisecond latency overhead
- **Support different rate limits per API endpoint** (e.g., `/login` = 5/min, `/search` = 100/min)
- **Return appropriate HTTP 429 responses** with retry-after headers

---

## Requirements

### Functional Requirements

1. **Rate Limiting Rules**: Configure rules per client, per endpoint, per IP, or per API key.
2. **Multiple Algorithms**: Support Token Bucket, Sliding Window Log, Sliding Window Counter, and Fixed Window Counter.
3. **Distributed State**: Rate limit state is shared across all API server instances.
4. **Response Headers**: Return `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`.
5. **Graceful Throttling**: Return HTTP 429 Too Many Requests with `Retry-After` header.

### Non-Functional Requirements

1. **Latency**: Rate limiting check must add < 1ms to request latency.
2. **Scale**: Handle 10M+ requests per second across all servers.
3. **Availability**: Rate limiter failure should NOT block requests (fail-open).
4. **Accuracy**: Allow a small margin of error (±1-2%) for distributed counting.

---

## Capacity Estimation

```
Requests: 10M req/sec (peak)
Rules: 1M unique clients, 100 endpoints = 100M possible (client, endpoint) pairs
Memory per counter: ~100 bytes (key + count + timestamp + TTL)
Active counters (1% hot): 1M counters × 100 bytes = 100MB → fits in Redis
```

---

## High-Level Architecture

```
                    ┌──────────────────┐
                    │   Client Request  │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │   API Gateway     │  ← Rate limiting happens HERE
                    │  (or Middleware)  │     (before reaching application)
                    └────────┬─────────┘
                             │
                ┌────────────▼────────────┐
                │     Rate Limiter        │
                │     Middleware           │
                ├─────────────────────────┤
                │  1. Extract client key  │
                │  2. Lookup rules        │
                │  3. Check rate limit    │
                │  4. Allow or reject     │
                └────────────┬────────────┘
                             │
              ┌──────────────▼──────────────┐
              │     Redis Cluster           │  ← Distributed state
              │  (Rate limit counters)      │
              │                             │
              │  Key: "rl:{clientId}:{ep}"  │
              │  Value: token count / log   │
              │  TTL: window duration       │
              └─────────────────────────────┘
                             │
                ┌────────────▼────────────┐
                │   Rules Configuration    │
                │   Service / Database     │
                │                          │
                │  "GET /api/search" →     │
                │    100 req/min per user  │
                │  "POST /api/login" →     │
                │    5 req/min per IP      │
                └──────────────────────────┘
```

---

## Rate Limiting Algorithms — Deep Dive

### 1. Token Bucket

**How it works**: Each client has a "bucket" that holds tokens. Tokens are added at a fixed rate. Each request consumes one token. If the bucket is empty, the request is rejected.

**Parameters**:
- `bucketSize` (burst capacity): Maximum tokens the bucket can hold.
- `refillRate`: Tokens added per second.

**Example**: `bucketSize=10, refillRate=5/sec` → Allows burst of 10 requests, then sustained 5 req/sec.

```
Time:    0s    0.2s   0.4s   0.6s   0.8s   1.0s
Tokens:  10 → 9 → 8 → 7 → 6 → 5 → 6(refill) → 5 → ...
Request: ✓     ✓     ✓     ✓     ✓     ✓
```

**Redis Implementation (Lua Script for Atomicity)**:

```lua
-- Token Bucket rate limiter as an atomic Redis Lua script
-- KEYS[1] = rate limit key
-- ARGV[1] = bucket size (max tokens)
-- ARGV[2] = refill rate (tokens per second)
-- ARGV[3] = current timestamp (milliseconds)
-- ARGV[4] = tokens to consume (usually 1)

local key = KEYS[1]
local bucketSize = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local now = tonumber(ARGV[3])
local requested = tonumber(ARGV[4])

-- Get current state
local data = redis.call('HMGET', key, 'tokens', 'lastRefill')
local tokens = tonumber(data[1])
local lastRefill = tonumber(data[2])

-- Initialize if first request
if tokens == nil then
    tokens = bucketSize
    lastRefill = now
end

-- Calculate token refill since last request
local elapsed = (now - lastRefill) / 1000.0  -- seconds
local refilled = elapsed * refillRate
tokens = math.min(bucketSize, tokens + refilled)

-- Check if enough tokens
local allowed = false
if tokens >= requested then
    tokens = tokens - requested
    allowed = true
end

-- Update state
redis.call('HMSET', key, 'tokens', tokens, 'lastRefill', now)
redis.call('EXPIRE', key, math.ceil(bucketSize / refillRate) * 2)

if allowed then
    return {1, math.floor(tokens)}  -- {allowed, remaining}
else
    return {0, 0}  -- {rejected, 0 remaining}
end
```

**Java Service**:

```java
@Service
public class TokenBucketRateLimiter {

    private final StringRedisTemplate redis;
    private final RedisScript<List> tokenBucketScript;

    public TokenBucketRateLimiter(StringRedisTemplate redis) {
        this.redis = redis;
        this.tokenBucketScript = new DefaultRedisScript<>(LUA_SCRIPT, List.class);
    }

    public RateLimitResult isAllowed(String clientId, String endpoint,
                                      int bucketSize, int refillRate) {
        String key = "rl:" + clientId + ":" + endpoint;
        long now = System.currentTimeMillis();

        List<Long> result = redis.execute(
            tokenBucketScript,
            List.of(key),
            String.valueOf(bucketSize),
            String.valueOf(refillRate),
            String.valueOf(now),
            "1"  // consume 1 token
        );

        boolean allowed = result.get(0) == 1L;
        long remaining = result.get(1);

        return new RateLimitResult(allowed, remaining, bucketSize);
    }
}

public record RateLimitResult(boolean allowed, long remaining, int limit) {
    public long retryAfterMs() {
        // If rejected, suggest waiting until 1 token refills
        return allowed ? 0 : 1000;
    }
}
```

### 2. Sliding Window Log

**How it works**: Store a timestamp for every request. To check the limit, count timestamps within the sliding window.

**Pros**: Exact counting, no boundary issues.
**Cons**: Memory-intensive (stores every timestamp), expensive counting.

```java
// Redis implementation using Sorted Set
public boolean isAllowed(String key, int limit, int windowSeconds) {
    long now = System.currentTimeMillis();
    long windowStart = now - (windowSeconds * 1000L);

    // Atomic Redis pipeline
    redis.executePipelined(connection -> {
        // Remove old entries outside the window
        connection.zRemRangeByScore(key.getBytes(), 0, windowStart);
        // Add current request
        connection.zAdd(key.getBytes(), now, String.valueOf(now).getBytes());
        // Count entries in window
        connection.zCard(key.getBytes());
        // Set TTL
        connection.expire(key.getBytes(), windowSeconds);
        return null;
    });

    Long count = redis.opsForZSet().size(key);
    return count != null && count <= limit;
}
```

### 3. Sliding Window Counter

**How it works**: Hybrid of Fixed Window Counter and Sliding Window Log. Uses two fixed windows and weights the count based on how far into the current window we are.

**Formula**: `count = (previous_window_count × overlap_percentage) + current_window_count`

```
Window size: 1 minute
Previous window (0:00-1:00): 42 requests
Current window (1:00-2:00): 18 requests (so far)
Current time: 1:15 (25% into current window → 75% overlap with previous)

Weighted count = 42 × 0.75 + 18 = 31.5 + 18 = 49.5
Limit: 50 → ALLOWED
```

**Redis Implementation**:

```lua
-- Sliding Window Counter
local key = KEYS[1]
local limit = tonumber(ARGV[1])
local windowMs = tonumber(ARGV[2])
local now = tonumber(ARGV[3])

-- Calculate window boundaries
local currentWindow = math.floor(now / windowMs)
local previousWindow = currentWindow - 1

local currentKey = key .. ":" .. currentWindow
local previousKey = key .. ":" .. previousWindow

-- Get counts
local previousCount = tonumber(redis.call('GET', previousKey) or "0")
local currentCount = tonumber(redis.call('GET', currentKey) or "0")

-- Calculate weight of previous window
local elapsedInCurrentWindow = now % windowMs
local previousWeight = 1.0 - (elapsedInCurrentWindow / windowMs)

-- Weighted count
local weightedCount = math.floor(previousCount * previousWeight) + currentCount

if weightedCount < limit then
    redis.call('INCR', currentKey)
    redis.call('EXPIRE', currentKey, math.ceil(windowMs / 1000) * 2)
    return {1, limit - weightedCount - 1}  -- allowed, remaining
else
    return {0, 0}  -- rejected
end
```

### 4. Fixed Window Counter

**How it works**: Divide time into fixed windows (e.g., 1 minute). Count requests in current window. Simple but has boundary burst issue.

**Boundary Issue**: If limit is 100/min and 100 requests come at 0:59 and 100 more at 1:01, we get 200 requests in 2 seconds while each window is under 100.

```java
// Simplest implementation — good enough for many use cases
public boolean isAllowed(String key, int limit, int windowSeconds) {
    String windowKey = key + ":" + (System.currentTimeMillis() / (windowSeconds * 1000));

    Long count = redis.opsForValue().increment(windowKey);
    if (count == 1) {
        redis.expire(windowKey, Duration.ofSeconds(windowSeconds * 2));
    }

    return count <= limit;
}
```

---

## Algorithm Comparison

| Algorithm | Memory | Accuracy | Burst Handling | Complexity |
|-----------|--------|----------|----------------|------------|
| **Token Bucket** | Low (2 values per key) | Excellent | Configurable burst | Medium |
| **Sliding Window Log** | High (every timestamp) | Perfect | No boundary issue | Low |
| **Sliding Window Counter** | Low (2 counters per key) | ~99.7% | Good | Medium |
| **Fixed Window Counter** | Lowest (1 counter per key) | ~95% (boundary issue) | Boundary burst | Lowest |

**Recommendation**: **Token Bucket** for most use cases. Simple, memory-efficient, and allows controlled bursts.

---

## Rate Limiter Middleware (Spring Boot)

```java
@Component
@Order(1) // Run before other filters
public class RateLimitFilter implements Filter {

    private final TokenBucketRateLimiter rateLimiter;
    private final RateLimitRuleRepository ruleRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 1. Extract client identifier
        String clientId = extractClientId(httpRequest);
        String endpoint = httpRequest.getMethod() + ":" + httpRequest.getRequestURI();

        // 2. Look up rate limit rule
        RateLimitRule rule = ruleRepository.findRule(endpoint);
        if (rule == null) {
            chain.doFilter(request, response); // No rule = no limit
            return;
        }

        // 3. Check rate limit
        try {
            RateLimitResult result = rateLimiter.isAllowed(
                clientId, endpoint, rule.getBucketSize(), rule.getRefillRate()
            );

            // 4. Set response headers (always, even when allowed)
            httpResponse.setHeader("X-RateLimit-Limit", String.valueOf(result.limit()));
            httpResponse.setHeader("X-RateLimit-Remaining", String.valueOf(result.remaining()));

            if (result.allowed()) {
                chain.doFilter(request, response); // Proceed
            } else {
                // 5. Reject with 429
                httpResponse.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
                httpResponse.setHeader("Retry-After",
                    String.valueOf(result.retryAfterMs() / 1000));
                httpResponse.getWriter().write(
                    "{\"error\":\"Rate limit exceeded\",\"retryAfterSeconds\":" +
                    (result.retryAfterMs() / 1000) + "}"
                );
            }
        } catch (Exception e) {
            // Fail-open: if Redis is down, allow the request
            chain.doFilter(request, response);
        }
    }

    private String extractClientId(HttpServletRequest request) {
        // Priority: API key > JWT user ID > IP address
        String apiKey = request.getHeader("X-API-Key");
        if (apiKey != null) return "api:" + apiKey;

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String userId = extractUserIdFromJwt(authHeader.substring(7));
            if (userId != null) return "user:" + userId;
        }

        return "ip:" + request.getRemoteAddr();
    }
}
```

---

## Distributed Rate Limiting — Handling Multiple Server Instances

### Challenge

With 10 API servers, each must check against a **shared counter** in Redis. The critical requirement is **atomicity**: reading the current count and incrementing it must be a single atomic operation.

### Solution: Redis Lua Scripts

Lua scripts in Redis are executed atomically (single-threaded Redis). This guarantees that no two servers can read the same count and both allow a request that should have been blocked.

### Handling Redis Failures (Fail-Open)

```java
// Fail-open: If Redis is unavailable, allow all requests
// This is better than blocking all traffic (fail-closed)
try {
    RateLimitResult result = rateLimiter.isAllowed(clientId, endpoint, ...);
    if (!result.allowed()) return 429;
} catch (RedisConnectionException e) {
    log.warn("Redis unavailable, rate limiting disabled");
    // Allow the request — availability over strict rate limiting
}
```

### Rate Limiting at Different Layers

```
Layer 1: CDN/Edge (Cloudflare)     — IP-based, DDoS protection, very coarse
Layer 2: API Gateway (Kong/Envoy)  — Per-client API key, medium granularity
Layer 3: Application (Spring Boot) — Per-user, per-endpoint, fine-grained
```

---

## Scaling Considerations

1. **Redis Cluster**: For 10M+ req/sec, use Redis Cluster with hash-based sharding. Each rate limit key consistently hashes to the same Redis node.
2. **Local Caching**: For extremely high throughput, cache rate limit decisions locally for 1 second (accept ±5% inaccuracy for massive throughput gains).
3. **Race Condition Tolerance**: At scale, exact rate limiting is less important than approximate. A ±2% margin is acceptable.
4. **Rule Caching**: Cache rate limit rules locally with a 30-second TTL to avoid hitting the rules database on every request.

---

## Interview Discussion Points

1. **Where should the rate limiter live?**
   - **API Gateway** (e.g., Kong, AWS API Gateway) for centralized control.
   - **Application Middleware** for fine-grained per-endpoint rules.
   - **CDN Edge** for DDoS protection.
   - Best: **Layered approach** — coarse at edge, fine-grained at application.

2. **Token Bucket vs Sliding Window — when to use which?**
   - Token Bucket: When you want to allow bursts (e.g., API with batch operations).
   - Sliding Window: When you need strict per-second/minute limits without burst.

3. **How to handle rate limiting in a microservices architecture?**
   - Centralized rate limiting at the API Gateway for external clients.
   - Per-service rate limiting for internal service-to-service calls using a shared Redis.
   - Alternatively, use a sidecar proxy (Envoy) with rate limiting built in.

4. **What about client-specific rate limits?**
   - Store per-client rules in a configuration database. Free tier: 100/min, Premium: 10000/min, Enterprise: custom.
   - Cache rules in Redis or locally to avoid DB lookups on every request.

