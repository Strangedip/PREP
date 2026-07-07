# Section 28: Redis & Distributed Caching Deep Dive

> **Level**: MID+ (cache-aside) to SR+ (cluster, persistence, cache stampede, Redis as data structure server)
> **Depth**: Standard (textbook-style explanations with production patterns and interview walkthroughs)
> **Complements**: [05_Database_Performance_Tuning.md](./05_Database_Performance_Tuning.md), [Distributed Cache HLD](../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md)

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [27_NoSQL_Databases_Guide.md](27_NoSQL_Databases_Guide.md) | **Next**: [29_Advanced_Networking_Infrastructure.md](29_Advanced_Networking_Infrastructure.md)

---

## What Redis is (and is not)

**Redis** = **RE**mote **DI**ctionary **S**erver — an in-memory data structure store used as:

- Cache (most common in Java/Spring apps)
- Session store
- Rate limiter
- Pub/Sub message bus (lightweight)
- Distributed lock coordinator
- Leaderboard / real-time counters

**Redis is NOT**: a replacement for PostgreSQL for transactional data, a full message queue (use Kafka for that), or magically fast if you put entire DB in it without a caching strategy.

```
Client → TCP → Redis Server (single-threaded command loop per shard)
                    │
                    ├── Memory (hot data)
                    └── Optional persistence (RDB snapshots / AOF log)
```

**2026 deployments**: Redis OSS 7.x, **Redis Cluster**, **AWS ElastiCache**, **Azure Cache for Redis**, **GCP Memorystore**, **Redis Cloud**.

---

## 28.1 Architecture — why single-threaded is fast

Redis processes commands **one at a time per shard** (no lock contention on data structures). Throughput comes from:

- Everything in RAM (microsecond access)
- Efficient C implementations of data structures
- Pipelining (batch many commands without waiting for each reply)
- I/O threads (Redis 6+) for network read/write while main thread executes

| Myth | Reality |
|------|---------|
| "Single-threaded = slow" | 100K+ ops/sec per core is normal |
| "Redis fixes all DB slowness" | Wrong keys, hot keys, or no TTL still melt production |
| "Cluster = unlimited scale" | One hot key on one slot still bottlenecks |

**Interview answer**: "Redis is single-threaded per shard for command execution — that's why it's fast and why **hot key** design matters."

---

## 28.2 Data structures — beyond GET/SET

Each structure maps to real product features:

| Structure | Core commands | Real use case | Why not plain String |
|-----------|---------------|---------------|----------------------|
| **String** | `SET`, `GET`, `INCR`, `SETEX` | Session token, simple cache, counters | — |
| **Hash** | `HSET`, `HGET`, `HGETALL` | User profile fields (update one field) | Avoid serializing whole object on every field change |
| **List** | `LPUSH`, `RPOP`, `BLPOP` | Recent activity, simple work queue | Ordered FIFO |
| **Set** | `SADD`, `SISMEMBER`, `SINTER` | Unique tags, "users online" set | O(1) membership |
| **Sorted Set** | `ZADD`, `ZRANGE`, `ZINCRBY` | Leaderboards, rate limits, priority queues | Ordered by score |
| **Stream** | `XADD`, `XREADGROUP` | Event log, lightweight MQ with consumer groups | Kafka-lite for small scale |
| **HyperLogLog** | `PFADD`, `PFCOUNT` | Unique visitors (UV) with ~0.81% error | 12 KB vs billions of user IDs |
| **Bitmap** | `SETBIT`, `BITCOUNT` | Daily active users, feature flags | 1 bit per user per day |

### Worked example: leaderboard

```redis
ZADD game:leaderboard 9500 "player1" 8700 "player2" 7200 "player3"
ZRANGE game:leaderboard 0 9 WITHSCORES REV   # Top 10, highest first
ZINCRBY game:leaderboard 100 "player2"       # Add 100 points atomically
ZRANK game:leaderboard "player2"             # Rank of player2
```

### Worked example: session as Hash

```redis
HSET session:abc123 userId 42 role "USER" loginAt 1719398400
EXPIRE session:abc123 3600
HGET session:abc123 userId
```

**Why Hash over JSON string**: Update `role` without read-modify-write race on full JSON blob.

---

## 28.3 Caching patterns — the production playbook

### Cache-Aside (Lazy Loading) — default pattern

```
READ:
  1. Check Redis for key
  2. Cache HIT → return
  3. Cache MISS → read PostgreSQL → write Redis → return

WRITE:
  1. Update PostgreSQL (source of truth)
  2. DELETE Redis key (not update — avoids stale race)
```

**Why delete on write, not update?**

```
Thread A: update DB to value=2
Thread B: update DB to value=3
Thread A: update cache to 2   ← stale after B's DB write
Thread B: delete cache        ← correct

If both only update cache, order can leave cache stale.
```

### Read-Through / Write-Through

Cache library handles DB automatically (Hazelcast, some Spring Cache providers). App talks only to cache layer.

| Pattern | App complexity | Consistency |
|---------|----------------|-------------|
| Cache-aside | App manages both | Good if delete-after-commit |
| Read-through | Lower | Cache library must be correct |
| Write-through | Lower on write path | Stronger but slower writes |

### Write-Behind (Write-Back)

Write to cache immediately; async flush to DB. **Risk**: cache crash before flush = data loss. Only for analytics, counters — not money.

### Pattern comparison table

| Pattern | Read path | Write path | Best for |
|---------|-----------|------------|----------|
| **Cache-aside** | App checks cache → DB on miss | App writes DB → deletes cache | **Most Spring apps** |
| **Read-through** | Cache fetches from DB on miss | App writes DB | Hazelcast, embedded cache |
| **Write-through** | Same as cache-aside | Cache writes DB synchronously | Strong consistency needs |
| **Write-behind** | Cache only | Async DB flush | High write throughput, lossy OK |

---

## 28.4 Cache stampede — the midnight sale problem

When a **hot key** expires, thousands of requests simultaneously miss cache and hammer PostgreSQL.

```
T=0:   product:flash-sale-TTL expires
T=0:   10,000 requests → all MISS → 10,000 identical DB queries
T=1:   Database melts
```

### Solutions (know all four for interviews)

| Solution | How it works | Trade-off |
|----------|--------------|-----------|
| **Distributed lock (single-flight)** | `SET lock:product:42 NX EX 10` — one thread rebuilds | Others wait or get stale |
| **Probabilistic early refresh** | Refresh before TTL with probability based on age | Complex tuning |
| **Background refresh** | Never expire hot keys; job refreshes every N min | Stale up to N min |
| **Request coalescing** | Guava `LoadingCache` / Caffeine single-flight locally | Per JVM only |

### Spring Boot + Redis lock (stampede-safe)

```java
@Service
public class ProductCacheService {

    private final StringRedisTemplate redis;
    private final ProductRepository db;

    public Product getProduct(long id) {
        String key = "product:" + id;
        String cached = redis.opsForValue().get(key);
        if (cached != null) return deserialize(cached);

        String lockKey = "lock:product:" + id;
        Boolean acquired = redis.opsForValue()
            .setIfAbsent(lockKey, "1", Duration.ofSeconds(10));

        if (Boolean.TRUE.equals(acquired)) {
            try {
                Product p = db.findById(id).orElseThrow();
                redis.opsForValue().set(key, serialize(p), Duration.ofMinutes(30));
                return p;
            } finally {
                redis.delete(lockKey);
            }
        }
        // Another thread is rebuilding — brief wait or return stale
        Thread.sleep(50);
        cached = redis.opsForValue().get(key);
        if (cached != null) return deserialize(cached);
        return db.findById(id).orElseThrow(); // fallback
    }

    @Transactional
    public Product updateProduct(Product product) {
        Product saved = db.save(product);
        redis.delete("product:" + product.getId()); // invalidate AFTER commit
        return saved;
    }
}
```

### L1 + L2 caching (Caffeine + Redis)

```
Request → Caffeine (local, 1ms) → Redis (1-5ms) → PostgreSQL (10-50ms)
```

Use for catalog pages where same SKU is hit thousands of times per JVM. Invalidate both layers on write.

---

## 28.5 TTL strategy — when keys should die

| Data type | TTL | Reason |
|-----------|-----|--------|
| Product catalog | 15–60 min | Changes infrequently; stampede risk on expiry |
| User session | 24h sliding | Security; refresh on activity |
| OTP code | 5 min | Security |
| Rate limit window | 1 min / 1 hour | Sliding window bucket |
| Flash sale inventory display | 5–10 sec | Accept stale; source of truth is DB |

**Anti-pattern**: No TTL on cache keys → memory fills → `allkeys-lru` evicts random keys including hot ones.

---

## 28.6 Eviction policies

When `maxmemory` is reached:

| Policy | Behavior | When to use |
|--------|----------|-------------|
| **noeviction** | Return errors on write | Cache + data store combined — dangerous |
| **allkeys-lru** | Evict any key (approximate LRU) | General purpose cache |
| **volatile-lru** | Evict only keys with TTL | Mix of permanent + cache keys |
| **allkeys-lfu** | Evict least frequently used (Redis 4.0+) | **Hot key skew** (flash sales, viral content) |
| **volatile-ttl** | Evict shortest TTL first | Mixed TTL workloads |

**Interview**: LRU is **approximated** (samples random keys) — not exact LRU — for O(1) performance.

**Production default for pure cache**: `maxmemory-policy allkeys-lfu` or `allkeys-lru` with explicit TTLs on all cache keys.

---

## 28.7 Persistence — RDB vs AOF

Redis is in-memory but can survive restarts:

| Mode | How | Recovery speed | Data loss risk |
|------|-----|----------------|----------------|
| **RDB** | Snapshot every N minutes / N writes | Fast | Last minutes between snapshots |
| **AOF** | Append every write command | Slower replay | Depends on `fsync` policy |
| **Both** | RDB + AOF | Best production practice | Minimal |

### AOF fsync policies

| Policy | Durability | Performance |
|--------|------------|-------------|
| `always` | Every write synced to disk | Slowest |
| `everysec` | Sync once per second | **Production default** |
| `no` | OS decides | Fastest, risky |

**When you need persistence**: Sessions you can't lose, Redis as primary for inventory holds (with caution), rate limit state across restarts.

**When you don't**: Pure cache where DB is source of truth — persistence adds I/O overhead for no benefit.

---

## 28.8 Redis Cluster & high availability

### Sentinel (HA for single master)

```
Master ──replicates──▶ Replica 1, Replica 2
         ▲
    Sentinel monitors → auto-failover if master dies
```

Good for: smaller deployments, up to ~50GB, simpler ops.

### Redis Cluster (horizontal scale)

```
16384 hash slots distributed across nodes

CRC16(key) % 16384 → slot → node

Node A: slots 0-5460
Node B: slots 5461-10922
Node C: slots 10923-16383
```

**Multi-key operations** require same slot — use **hash tags**:

```
{user:123}:profile   → same slot
{user:123}:cart      → same slot
{user:123}:sessions  → same slot
```

### Replication caveats

- Replication is **async** by default — replica may lag
- Read from replica for scaling reads → accept **staleness**
- Never read-your-writes from replica for session/auth without routing

---

## 28.9 Distributed locking

### Basic pattern (good enough for cache rebuild)

```redis
SET resource:lock:order:42 unique_request_id NX EX 30
```

- **NX** — set only if not exists
- **EX 30** — auto-expire (holder crash won't deadlock forever)
- **unique value** — identify lock owner

### Safe unlock (Lua script)

```lua
if redis.call("GET", KEYS[1]) == ARGV[1] then
    return redis.call("DEL", KEYS[1])
else
    return 0
end
```

**Why**: Prevents deleting another process's lock after yours expired.

### Redisson (Java production)

```java
RLock lock = redisson.getLock("order:42");
if (lock.tryLock(0, 30, TimeUnit.SECONDS)) {
    try { /* critical section */ }
    finally { lock.unlock(); }
}
```

**Caveat for critical paths** (payments, inventory): locks alone aren't enough — need **fencing tokens** so stale lock holders can't write. See [Ticketmaster HLD](../04_SystemDesign/02_HighLevelDesign/Ticketmaster/Ticketmaster.md).

---

## 28.10 Rate limiting with Redis

### Fixed window (simple, boundary burst)

```redis
INCR rate:user:42:2024031514   # hour bucket
EXPIRE rate:user:42:2024031514 3600
```

**Problem**: 2× burst at window boundary (999 at 13:59 + 999 at 14:00).

### Sliding window (Sorted Set) — interview favorite

```redis
ZADD rate:user:42 <now_ms> <now_ms>
ZREMRANGEBYSCORE rate:user:42 0 <now_ms - window_ms>
ZCARD rate:user:42
```

Each request adds timestamp; count entries in window. Atomic via Lua script.

### Token bucket (Lua)

Atomic refill + consume — see [RateLimiter HLD](../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md).

---

## 28.11 Pub/Sub vs Streams vs Kafka

| Feature | Redis Pub/Sub | Redis Streams | Kafka |
|---------|---------------|---------------|-------|
| Persistence | No (fire-and-forget) | Yes | Yes |
| Consumer groups | No | Yes | Yes |
| Scale | Low thousands | Medium | Massive |
| Use case | Live notifications | Lightweight event log | Production event bus |

**Interview**: "Redis Pub/Sub for real-time presence; Kafka for order events that must not be lost."

---

## 28.12 Hot key problem

Even with Redis Cluster, **one key** on **one slot** = one CPU core.

```
Key: flash_sale:sku_999  → 500K ops/sec on one node → meltdown
```

**Mitigations**:
- Split into `flash_sale:sku_999:shard_{0..9}` — random read shard
- Local JVM cache (Caffeine) in front
- Read replicas for read-heavy hot keys (accept staleness)

---

## 28.13 Spring Boot integration

### Spring Cache + Redis

```java
@Cacheable(value = "products", key = "#id")
public Product findById(long id) { ... }

@CacheEvict(value = "products", key = "#product.id")
public Product update(Product product) { ... }
```

### RedisTemplate patterns

```java
// Session
redis.opsForHash().putAll("session:" + id, sessionMap);
redis.expire("session:" + id, Duration.ofHours(24));

// Counter
redis.opsForValue().increment("views:video:" + videoId);

// Leaderboard
redis.opsForZSet().incrementScore("leaderboard", userId, points);
```

---

## 28.14 Redis in system design — where it appears

| System design | Redis role |
|---------------|------------|
| [URL Shortener](../04_SystemDesign/02_HighLevelDesign/URLShortener/URLShortener.md) | Cache short URL → long URL mapping |
| [Ticketmaster](../04_SystemDesign/02_HighLevelDesign/Ticketmaster/Ticketmaster.md) | Seat hold state, atomic Lua scripts |
| [Search Autocomplete](../04_SystemDesign/02_HighLevelDesign/SearchAutocomplete/SearchAutocomplete.md) | Prefix → topK cache |
| [News Feed](../04_SystemDesign/02_HighLevelDesign/NewsFeed/NewsFeed.md) | Fan-out feed cache (sorted sets) |
| [Rate Limiter](../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) | Token bucket / sliding window |
| [Instagram](../04_SystemDesign/02_HighLevelDesign/Instagram/Instagram.md) | Feed timeline, stories TTL |
| [Distributed Cache](../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) | Full cache layer design |

---

## 28.15 Interview quick reference (full answers)

| # | Question | Answer |
|---|----------|--------|
| 1 | Redis single-threaded? | One command thread per shard — fast, no lock overhead; design around hot keys. |
| 2 | Cache-aside write? | Update DB first, **delete** cache key (not update). |
| 3 | Cache stampede? | Hot key expiry → DB overload. Fix: distributed lock, early refresh, background job. |
| 4 | RDB vs AOF? | RDB = periodic snapshots. AOF = command log. Use both in production. |
| 5 | Cluster slots? | 16384 slots; `CRC16(key) % 16384`; hash tags `{user}` for co-location. |
| 6 | Sorted set use? | Leaderboards, sliding window rate limits, feed timelines. |
| 7 | Redis Streams? | Persistent append log with consumer groups — lightweight MQ. |
| 8 | Distributed lock safe? | `SET NX EX` + Lua unlock + fencing token for strict correctness. |
| 9 | LRU vs LFU? | LRU: recently used. LFU: frequently used — better for stable hot keys. |
| 10 | Redis vs Memcached? | Redis: structures, persistence, replication. Memcached: simple strings, multi-threaded. |
| 11 | When NOT to use Redis? | Primary transactional store, large values (>512MB), cold data rarely accessed. |
| 12 | Session in Redis vs JWT? | Redis: revocable, stateful. JWT: stateless, hard to revoke. |

**Must-say keywords**: cache-aside, stampede, SET NX, hash slot, sorted set, AOF, eviction policy, single-threaded, hot key, single-flight.

---

## 28.16 Production checklist (India / flash sale context)

Redis is the first line of defense when Flipkart opens a flash SKU at midnight:

- [ ] All cache keys have TTL
- [ ] `maxmemory-policy` set (`allkeys-lfu` for skewed access)
- [ ] Monitor `evicted_keys`, `used_memory`, `connected_clients`, `instantaneous_ops_per_sec`
- [ ] Hot key alerts before festival sales
- [ ] Multi-AZ replica for read scaling (accept staleness)
- [ ] ElastiCache/Memorystore in `ap-south-1` for India latency
- [ ] Stampede protection on product/catalog keys
- [ ] Never store plaintext PINs, full card numbers, or PII without encryption

Cross-links: [PostgreSQL §26](./26_PostgreSQL_Relational_DB_Deep_Dive.md) (source of truth), [Security §12](./12_Security_OWASP_Cloud.md) (session fixation), [SRE §23](./23_SRE_Reliability_Engineering.md) (SLOs), [Ticketmaster HLD](../04_SystemDesign/02_HighLevelDesign/Ticketmaster/Ticketmaster.md) (Redis holds).
