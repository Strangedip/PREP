# Section 28: Redis & Distributed Caching Deep Dive

> **Level**: MID+ (cache-aside) to SR+ (cluster, persistence, cache stampede, Redis as data structure server)
> **Complements**: [05_Database_Performance_Tuning.md](./05_Database_Performance_Tuning.md) Section 5.3

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [27_NoSQL_Databases_Guide.md](27_NoSQL_Databases_Guide.md) | **Next**: [29_Advanced_Networking_Infrastructure.md](29_Advanced_Networking_Infrastructure.md)

---

## 28.1 Redis Architecture

Redis is **single-threaded** for command execution (per shard) — ultra-fast in-memory ops, no lock contention.

```
Client → Redis Server (memory) → optional RDB/AOF disk persistence
```

**2026 deployments**: Redis OSS, **Redis Cluster**, **ElastiCache**, **Redis Cloud**, **KeyDB** (multi-threaded fork).

---

## 28.2 Data Structures — Beyond Simple Cache

| Structure | Commands | Use Case |
|-----------|----------|----------|
| **String** | SET, GET, INCR | Counters, session tokens, simple cache |
| **Hash** | HSET, HGETALL | Object cache (user profile fields) |
| **List** | LPUSH, RPOP | Queues, recent activity feed |
| **Set** | SADD, SINTER | Tags, unique visitors |
| **Sorted Set** | ZADD, ZRANGE | Leaderboards, rate limiting windows |
| **Stream** | XADD, XREAD | Event log, lightweight message queue |
| **HyperLogLog** | PFADD, PFCOUNT | Unique count (UV) with ~0.81% error |
| **Bitmap** | SETBIT, BITCOUNT | Daily active users, feature flags |

```redis
ZADD leaderboard 9500 "player1" 8700 "player2"
ZRANGE leaderboard 0 9 WITHSCORES REV  # Top 10
```

---

## 28.3 Caching Patterns (Production)

### Cache-Aside (Lazy Loading)
```
1. Read: check cache → miss → read DB → write cache
2. Write: update DB → delete cache (not update — avoids race conditions)
```

### Read-Through / Write-Through
Cache layer handles DB reads/writes — more complex, used in Hazelcast.

### Write-Behind (Write-Back)
Write to cache immediately, async flush to DB — risk of data loss.

### Cache Stampede Prevention
When hot key expires, thousands of requests hit DB simultaneously.

| Solution | How |
|----------|-----|
| **Mutex** | Only one thread rebuilds; others wait or get stale |
| **Probabilistic early expiration** | Refresh before TTL with probability |
| **Never expire hot keys** | Background refresh job |
| **Redis SETNX lock** | `SET lock:key NX EX 10` before DB fetch |

```java
String lock = redis.set("lock:product:42", "1", "NX", "EX", 10);
if (lock != null) {
    Product p = db.findById(42);
    redis.setex("product:42", 3600, serialize(p));
    redis.del("lock:product:42");
}
```

---

## 28.4 Eviction Policies

| Policy | Behavior |
|--------|----------|
| **noeviction** | Return error when memory full |
| **allkeys-lru** | Evict any key — LRU approximate |
| **volatile-lru** | Evict keys with TTL set |
| **allkeys-lfu** | Evict least frequently used (Redis 4.0+) |
| **volatile-ttl** | Evict keys with shortest TTL |

**Interview**: LRU is **approximated** (sampling) — not exact LRU for performance.

---

## 28.5 Persistence — RDB vs AOF

| Mode | How | Trade-off |
|------|-----|-----------|
| **RDB** | Snapshot at intervals | Fast recovery, may lose last minutes |
| **AOF** | Log every write | More durable, larger files, `fsync` policy matters |
| **Both** | RDB + AOF | Production recommended |

`appendfsync everysec` — balance durability and performance.

---

## 28.6 Redis Cluster & High Availability

### Sentinel (HA for single master)
- Monitors master, auto-failover to replica
- Good for smaller deployments

### Redis Cluster
- **16384 hash slots** distributed across nodes
- `CRC16(key) % 16384` → slot → node
- **Multi-key ops** require same slot — use hash tags: `{user:123}.profile` and `{user:123}.orders`

### Replication
- Async by default — replica may lag
- Read from replicas for scaling reads (accept staleness)

---

## 28.7 Distributed Locking (Redisson / SET NX)

```redis
SET resource:lock:order:42 unique_value NX EX 30
```
- **NX** — only if not exists
- **EX 30** — auto-expire (prevent deadlock if holder crashes)
- Release with Lua script — only delete if value matches (prevent deleting another holder's lock)

**Caveat**: Not fully safe without fencing tokens in all scenarios — use Redisson or DynamoDB locks for critical paths.

---

## 28.8 Rate Limiting with Redis

### Sliding Window (Sorted Set)
```redis
ZADD rate:user:42 now now
ZREMRANGEBYSCORE rate:user:42 0 (now - window_ms)
ZCARD rate:user:42
```

### Token Bucket (Lua script)
Atomic refill + consume — see [RateLimiter System Design](../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md).

---

## 28.9 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Redis single-threaded? | One thread executes commands per shard — fast, no lock overhead. |
| 2 | Cache-aside write? | Update DB, **delete** cache (not update) to avoid stale races. |
| 3 | Cache stampede? | Hot key expiry → DB overload. Fix: lock, early refresh, background rebuild. |
| 4 | RDB vs AOF? | RDB snapshots; AOF logs every write. Use both in production. |
| 5 | Redis Cluster slots? | 16384 slots; hash tags `{user}` for co-located keys. |
| 6 | Sorted set use? | Leaderboards, sliding window rate limits, priority queues. |
| 7 | Redis Streams? | Append-only log — lightweight MQ, consumer groups. |
| 8 | Distributed lock safe? | SET NX EX + Lua unlock; use fencing tokens for strict correctness. |
| 9 | LRU vs LFU eviction? | LRU: recently used. LFU: frequently used — better for hot key patterns. |
| 10 | Redis vs Memcached? | Redis: data structures, persistence, replication. Memcached: simple, multi-threaded, no persistence. |

**Must-say keywords**: cache-aside, stampede, SET NX, hash slot, sorted set, AOF, eviction policy, single-threaded, Redisson.

---

## §28.10 Production & Interview Depth — Flash Sales, Sessions & Rate Limits

Redis is the first line of defense when Flipkart opens a flash SKU at midnight — **inventory display**, **session carts**, **API rate limits**, and **OTP throttling** all land here. ElastiCache in `ap-south-1` (Mumbai) is standard; interviewers probe cache-aside correctness and stampede math, not just `SET`/`GET`.

### Trade-off: Cache-Aside vs Read-Through for Product Pages

| Pattern | Consistency | Stampede risk | Flash sale fit |
|---------|-------------|---------------|----------------|
| Cache-aside + delete-on-write | Good if delete after DB commit | High on TTL expiry | Default — add mutex |
| Read-through (Spring Cache) | Simpler app code | Medium | Catalog browsing |
| Write-through | Stronger | Low | Session/profile |
| Redis as primary (inventory) | Fast but lossy | N/A | Only with AOF + fallback to PG |

### Spring Boot 3.x: Stampede-Safe Product Cache

```java
@Service
public class ProductCacheService {

  @Cacheable(value = "products", key = "#id", unless = "#result == null")
  public Product getProduct(long id) {
      return productRepository.findById(id).orElse(null);
  }

  @CacheEvict(value = "products", key = "#product.id")
  @Transactional
  public Product updateProduct(Product product) {
      return productRepository.save(product);  // evict AFTER commit
  }
}
```

Configure Caffeine L1 + Redis L2 for **sub-ms local hits** on hot SKUs; cluster mode with hash tags `{sku:123}` co-locate stock + price keys. Rate-limit checkout APIs during IPL ticket drops using sorted-set sliding window — see [RateLimiter System Design](../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md).

```java
// Redisson — distributed lock for single-flight cache rebuild
RLock lock = redisson.getLock("lock:product:" + skuId);
if (lock.tryLock(0, 10, TimeUnit.SECONDS)) {
    try { return loadAndCache(skuId); }
    finally { lock.unlock(); }
}
return getStaleOrThrow(skuId);  // graceful degradation
```

Cross-link: authoritative inventory in [26_PostgreSQL_Relational_DB_Deep_Dive.md](./26_PostgreSQL_Relational_DB_Deep_Dive.md); session fixation risks in [12_Security_OWASP_Cloud.md](./12_Security_OWASP_Cloud.md). Production checklist: `maxmemory-policy allkeys-lfu`, Multi-AZ replica for reads, monitor **evicted_keys** and **connected_clients** before festival — single-threaded Redis means **one hot key ≠ cluster fix** unless you shard keys by design.
