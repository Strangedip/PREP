# Distributed Cache — High-Level Design

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

## Problem Statement

Design a distributed caching system (like Redis, Memcached, or Hazelcast) that can:

- **Store key-value pairs** with sub-millisecond read/write latency
- **Scale horizontally** across multiple nodes
- **Handle node failures** without data loss (replication)
- **Support eviction policies** (LRU, LFU, TTL)
- **Handle cache invalidation** correctly
- **Support 1M+ operations per second** across the cluster

---

## Requirements

### Functional Requirements

1. **GET/SET/DELETE**: Basic key-value operations with O(1) average time complexity.
2. **TTL (Time-to-Live)**: Keys expire after a configurable duration.
3. **Eviction**: When memory is full, evict keys based on policy (LRU, LFU, TTL, Random).
4. **Data Types**: Strings, Hashes, Lists, Sets, Sorted Sets (like Redis).
5. **Atomic Operations**: INCR, DECR, CAS (Compare-and-Swap) for concurrency safety.
6. **Pub/Sub**: Publish-subscribe for cache invalidation events.

### Non-Functional Requirements

1. **Latency**: < 1ms for GET, < 2ms for SET (p99).
2. **Throughput**: 1M+ ops/sec per node.
3. **Availability**: 99.999% with automatic failover.
4. **Consistency**: Strong consistency within a shard, eventual consistency across replicas.
5. **Memory**: Efficient memory usage with configurable max memory per node.

---

## High-Level Architecture

```
                    ┌──────────────────┐
                    │     Client       │
                    │  (Application)   │
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────┐
                    │  Client Library  │  ← Handles routing, connection pooling
                    │  (Jedis/Lettuce) │     consistent hashing, retries
                    └────────┬─────────┘
                             │
                    ┌────────▼─────────────────────────────────────────┐
                    │              Cache Cluster                       │
                    │                                                  │
                    │  ┌─────────┐  ┌─────────┐  ┌─────────┐        │
                    │  │ Node 1  │  │ Node 2  │  │ Node 3  │  ...   │
                    │  │(Primary)│  │(Primary)│  │(Primary)│        │
                    │  │Shard 0-5│  │Shard 6-10│ │Shard11-16│       │
                    │  └────┬────┘  └────┬────┘  └────┬────┘        │
                    │       │            │            │               │
                    │  ┌────▼────┐  ┌────▼────┐  ┌────▼────┐        │
                    │  │Replica 1│  │Replica 2│  │Replica 3│        │
                    │  └─────────┘  └─────────┘  └─────────┘        │
                    └─────────────────────────────────────────────────┘
```

---

## Core Design Decisions

### 1. Data Partitioning: Consistent Hashing

**Problem**: How to distribute keys across N nodes so that adding/removing a node moves minimal keys.

**Solution**: Consistent Hashing with Virtual Nodes.

```
Hash Ring (0 to 2^32 - 1):

    0 ──────── Node A (vnode 1) ──── Node B (vnode 1) ──── Node C (vnode 1)
    │                                                                      │
    Node C (vnode 3) ──── Node A (vnode 2) ──── Node B (vnode 2)          │
    │                                                                      │
    Node B (vnode 3) ──── Node C (vnode 2) ──── Node A (vnode 3) ──── 2^32

Key "user:123" → hash("user:123") = 0x4F2B... → falls between Node A vnode 1 and Node B vnode 1
→ Stored on Node B

Virtual Nodes: Each physical node has 150+ virtual positions on the ring.
This ensures even distribution even with few physical nodes.
```

**Java Implementation**:

```java
public class ConsistentHashRing<T> {
    private final TreeMap<Long, T> ring = new TreeMap<>();
    private final int virtualNodes;
    private final MessageDigest md5;

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
        this.md5 = MessageDigest.getInstance("MD5");
    }

    public void addNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(node.toString() + "#" + i);
            ring.put(hash, node);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < virtualNodes; i++) {
            long hash = hash(node.toString() + "#" + i);
            ring.remove(hash);
        }
    }

    public T getNode(String key) {
        if (ring.isEmpty()) return null;
        long hash = hash(key);
        // Find the first node clockwise from the hash
        Map.Entry<Long, T> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            entry = ring.firstEntry(); // Wrap around
        }
        return entry.getValue();
    }

    private long hash(String key) {
        md5.reset();
        byte[] digest = md5.digest(key.getBytes(StandardCharsets.UTF_8));
        // Use first 8 bytes as long
        return ((long)(digest[0] & 0xFF) << 56) |
               ((long)(digest[1] & 0xFF) << 48) |
               ((long)(digest[2] & 0xFF) << 40) |
               ((long)(digest[3] & 0xFF) << 32) |
               ((long)(digest[4] & 0xFF) << 24) |
               ((long)(digest[5] & 0xFF) << 16) |
               ((long)(digest[6] & 0xFF) << 8)  |
               ((long)(digest[7] & 0xFF));
    }
}
```

### 2. Replication

Each shard (partition) has 1 primary and N replicas:

```
Write path:  Client → Primary → Replicas (async or sync)
Read path:   Client → Primary (strong consistency)
         OR  Client → Replica (eventual consistency, lower latency)
```

**Replication modes**:

| Mode | How | Consistency | Latency | Use When |
|------|-----|-------------|---------|----------|
| **Synchronous** | Write to primary + all replicas before ACK | Strong | High | Financial data |
| **Asynchronous** | Write to primary, ACK, replicate in background | Eventual | Low | Caching (most cases) |
| **Semi-synchronous** | Write to primary + 1 replica before ACK | Bounded staleness | Medium | Good balance |

### 3. Eviction Policies

When memory reaches `maxmemory`, keys must be evicted:

**LRU (Least Recently Used)**:
```
Access order: A, B, C, D, E (memory full)
New key F arrives → Evict A (least recently used)

Implementation: Doubly-linked list + HashMap
- HashMap: key → Node (O(1) lookup)
- Linked list: Most recent at head, least recent at tail
- On access: move node to head (O(1))
- On eviction: remove tail (O(1))
```

**Approximate LRU (Redis approach)**:
```
Redis doesn't use exact LRU (too much memory for linked list).
Instead: Sample 5 random keys, evict the one with oldest access time.
Accuracy: ~95% of perfect LRU with 5 samples.
Config: maxmemory-samples 5
```

**LFU (Least Frequently Used)**:
```
Track access frequency per key.
Evict keys with lowest frequency.
Implementation: Min-heap keyed by frequency + HashMap.
Redis uses logarithmic frequency counter to save memory.
```

### 4. TTL Implementation

**Lazy Expiration**: Check TTL on every access. If expired, delete and return "not found."

**Active Expiration**: Background thread periodically scans keys and deletes expired ones.

**Redis approach**: Combination of both.
- Every 100ms, randomly sample 20 keys. Delete expired ones. If >25% expired, repeat immediately.
- On every GET, check TTL. Return null if expired.

### 5. Cache Invalidation

The hardest problem in computer science. Strategies:

```
1. TTL-based: Set TTL on all cache entries. After TTL, cache misses → re-fetch from DB.
   Pros: Simple, self-healing.
   Cons: Stale data during TTL window.

2. Event-based: On DB write, publish cache invalidation event.
   Pros: Near-instant invalidation.
   Cons: Complex, requires event infrastructure (Kafka/Redis Pub/Sub).

3. Write-through: Every DB write also updates cache.
   Pros: Cache always consistent.
   Cons: Higher write latency, wasted cache space for rarely-read data.

4. Cache-aside + Short TTL: Application manages cache. Short TTL (30-60 sec) as safety net.
   Pros: Balance of freshness and simplicity.
   Cons: Brief staleness acceptable.
```

---

## Cache Patterns for Applications

### Cache-Aside (Lazy Loading)

```java
public User getUser(String userId) {
    // 1. Check cache
    String cached = redis.get("user:" + userId);
    if (cached != null) {
        return deserialize(cached);
    }

    // 2. Cache miss → query database
    User user = userRepository.findById(userId);

    // 3. Populate cache
    if (user != null) {
        redis.setex("user:" + userId, 3600, serialize(user)); // TTL: 1 hour
    }

    return user;
}
```

### Write-Through

```java
public User updateUser(String userId, UserUpdateRequest request) {
    // 1. Update database
    User user = userRepository.save(toEntity(userId, request));

    // 2. Update cache (synchronously)
    redis.setex("user:" + userId, 3600, serialize(user));

    return user;
}
```

### Write-Behind (Write-Back)

```java
public void updateUser(String userId, UserUpdateRequest request) {
    // 1. Update cache immediately
    redis.setex("user:" + userId, 3600, serialize(request));

    // 2. Queue database write (asynchronous)
    writeQueue.add(new WriteOperation("user", userId, request));
}

// Background worker
@Scheduled(fixedRate = 1000)
public void flushWriteQueue() {
    List<WriteOperation> batch = writeQueue.drain(100);
    for (WriteOperation op : batch) {
        database.save(op);
    }
}
```

---

## Cache Stampede Prevention

**Problem**: Cache key expires → 100 concurrent requests all miss → 100 database queries for the same data.

**Solutions**:

### 1. Locking (Mutex)

```java
public User getUser(String userId) {
    String key = "user:" + userId;
    String lockKey = "lock:" + key;

    String cached = redis.get(key);
    if (cached != null) return deserialize(cached);

    // Try to acquire lock
    boolean locked = redis.setnx(lockKey, "1", Duration.ofSeconds(5));
    if (locked) {
        try {
            // Only one thread queries DB
            User user = userRepository.findById(userId);
            redis.setex(key, 3600, serialize(user));
            return user;
        } finally {
            redis.del(lockKey);
        }
    } else {
        // Other threads wait and retry
        Thread.sleep(50);
        return getUser(userId); // Retry (will hit cache)
    }
}
```

### 2. Probabilistic Early Expiration

```java
// Each cache entry stores actual_ttl and a "should refresh" flag
// As TTL approaches, probability of early refresh increases

public User getUser(String userId) {
    CacheEntry entry = redis.get("user:" + userId);
    if (entry == null) return fetchAndCache(userId);

    long ttlRemaining = entry.getExpiresAt() - System.currentTimeMillis();
    long totalTtl = entry.getTotalTtl();

    // Probability of early refresh increases as TTL decreases
    // When 10% TTL remains, 10% chance of early refresh per request
    double probability = 1.0 - ((double) ttlRemaining / totalTtl);
    if (Math.random() < probability * 0.1) {
        // Refresh in background
        CompletableFuture.runAsync(() -> fetchAndCache(userId));
    }

    return entry.getValue();
}
```

---

## Handling Node Failures

### Automatic Failover

```
Primary Node 2 fails
  → Sentinel/Cluster Manager detects failure (heartbeat timeout: 5 sec)
  → Promotes Replica 2 to new Primary
  → Updates routing table in all clients
  → New Primary starts accepting writes
  → Data loss: Only writes between last replication and failure (async rep)

Timeline:
  0s: Primary fails
  5s: Failure detected
  6s: Replica promoted
  7s: Clients redirected
  Total downtime: ~7 seconds (automatic)
```

### Data Recovery After Split-Brain

```
Split-brain: Network partition causes both Primary and Replica to accept writes.

Resolution:
  - When partition heals, one node's writes must be discarded.
  - Redis: Replica with more recent replication offset wins.
  - Application: Use CRDTs (Conflict-free Replicated Data Types) for merge.
```

---

## Interview Discussion Points

1. **Redis vs Memcached — when to use which?**
   - Redis: Data structures (hashes, sorted sets, lists), persistence (RDB/AOF), replication, pub/sub, Lua scripting, cluster mode. Use for: sessions, leaderboards, rate limiting, queues.
   - Memcached: Simpler, multi-threaded, slab allocator for memory efficiency. Use for: simple key-value caching, when you need pure speed and don't need data structures.

2. **How do you handle hot keys?**
   - A single key receiving disproportionate traffic (e.g., trending hashtag).
   - Solution 1: **Key replication** — Store copies with suffixed keys (e.g., `hot_key_1`, `hot_key_2`). Client randomly picks one.
   - Solution 2: **Local cache** — Cache hot keys in application memory (L1 cache) with short TTL (1-5 seconds).

3. **How does Redis Cluster handle resharding?**
   - Redis uses 16384 hash slots. Each node owns a range of slots.
   - Adding a node: Migrate slots from existing nodes to new node, one slot at a time.
   - During migration: Reads for a migrating key → MOVED/ASK redirect to the correct node.

4. **What consistency guarantees does a distributed cache provide?**
   - Within a single primary: Strong consistency (single-threaded Redis).
   - Across replicas: Eventual consistency (async replication).
   - Network partition: May lose recent writes if primary fails before replication.
   - Trade-off: Accept eventual consistency for caching (AP in CAP). Use database for source of truth.

