# Section 26: PostgreSQL & Relational Databases Deep Dive

> **Level**: MID+ (SQL, indexes) to SR+ (MVCC, replication, partitioning, tuning)
> **Complements**: [05_Database_Performance_Tuning.md](./05_Database_Performance_Tuning.md) — Section 5 covers indexing and caching; this section covers PostgreSQL internals and production operations.

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [25_Data_Engineering_Fundamentals.md](25_Data_Engineering_Fundamentals.md) | **Next**: [27_NoSQL_Databases_Guide.md](27_NoSQL_Databases_Guide.md)

---

## 26.1 Why PostgreSQL in 2026

PostgreSQL is the default relational choice for new backends:
- **ACID**, rich SQL, JSONB, full-text search, **pgvector** for AI embeddings
- Strong ecosystem: RDS, Aurora, Cloud SQL, Supabase, Citus (distributed)
- Used by Instagram, Spotify, Apple, Netflix (alongside other stores)

---

## 26.2 ACID & Transaction Isolation Levels

| Level | Dirty Read | Non-repeatable Read | Phantom Read |
|-------|------------|---------------------|--------------|
| READ UNCOMMITTED | Possible | Possible | Possible |
| READ COMMITTED | No | Possible | Possible |
| REPEATABLE READ | No | No | Possible* |
| SERIALIZABLE | No | No | No |

*PostgreSQL REPEATABLE READ prevents phantoms via snapshot isolation.

```sql
BEGIN;
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ;
SELECT balance FROM accounts WHERE id = 1;  -- snapshot at start of transaction
-- Another transaction commits a change — you still see old snapshot
COMMIT;
```

**Spring Boot default**: `@Transactional` uses DB default — PostgreSQL is READ COMMITTED.

**Interview tip**: Explain **why** higher isolation costs performance (more locking / serialization conflicts).

---

## 26.3 MVCC (Multi-Version Concurrency Control)

PostgreSQL never blocks readers with writers. Each row has hidden columns:
- `xmin` — transaction ID that inserted the row
- `xmax` — transaction ID that deleted/updated the row (or 0)

Readers see a **snapshot** of rows visible at transaction start. Updates create **new row versions**; old versions become dead tuples.

```
UPDATE users SET name = 'Alice' WHERE id = 1;
→ Old row marked dead, new row inserted (not in-place update)
```

**Keywords**: snapshot isolation, tuple visibility, no reader-writer blocking.

---

## 26.4 VACUUM & Bloat — Production Critical

Dead tuples accumulate → table/index **bloat** → slower scans, wasted disk.

| Command | Purpose |
|---------|---------|
| `VACUUM` | Mark dead space reusable within table |
| `VACUUM FULL` | Rewrites entire table (locks table — avoid in prod) |
| `ANALYZE` | Update statistics for query planner |
| Autovacuum | Background process — tune `autovacuum_vacuum_scale_factor` |

**Warning signs**: `n_dead_tup` high in `pg_stat_user_tables`, queries slowing on large tables without row growth.

```sql
SELECT relname, n_live_tup, n_dead_tup, last_autovacuum
FROM pg_stat_user_tables ORDER BY n_dead_tup DESC;
```

---

## 26.5 Connection Pooling — PgBouncer / HikariCP

PostgreSQL uses **one process per connection** — expensive at 10,000 connections.

```
App (1000 threads) → PgBouncer (pool: 50 connections) → PostgreSQL
```

| Pool Mode | Behavior |
|-----------|----------|
| **Transaction** | Connection returned after each transaction (most common) |
| **Session** | Held for entire client session |
| **Statement** | Returned after each statement (rare) |

**HikariCP sizing**: `connections = (core_count * 2) + effective_spindle_count` — often 10-20 per app instance with PgBouncer in front.

---

## 26.6 Replication & High Availability

### Streaming Replication (Physical)
- Primary streams WAL (Write-Ahead Log) to standby(s)
- **Synchronous**: commit waits for standby ACK (zero data loss, higher latency)
- **Asynchronous**: commit without waiting (risk of lost transactions on failover)

### Read Replicas
- Standbys accept read-only queries
- **Replication lag** — reads may be stale (eventual consistency)

### Logical Replication
- Replicate specific tables/changes — useful for CDC, upgrades, cross-region

### Failover
- **Patroni**, **RDS Multi-AZ**, **Cloud SQL HA** — automatic primary promotion

---

## 26.7 Partitioning Strategies

| Type | Use Case | Example |
|------|----------|---------|
| **Range** | Time-series data | Orders by month |
| **List** | Geographic/category | Sales by region |
| **Hash** | Even distribution | User events by `user_id % 16` |

```sql
CREATE TABLE orders (
    id BIGSERIAL, created_at TIMESTAMPTZ, amount NUMERIC
) PARTITION BY RANGE (created_at);

CREATE TABLE orders_2026_01 PARTITION OF orders
    FOR VALUES FROM ('2026-01-01') TO ('2026-02-01');
```

**Partition pruning**: Query with `WHERE created_at = '2026-01-15'` scans only `orders_2026_01`.

---

## 26.8 JSONB, Full-Text Search, pgvector

```sql
-- JSONB with GIN index
CREATE INDEX idx_metadata ON products USING GIN (metadata jsonb_path_ops);
SELECT * FROM products WHERE metadata @> '{"color": "red"}';

-- Full-text search
CREATE INDEX idx_fts ON articles USING GIN (to_tsvector('english', body));
SELECT * FROM articles WHERE to_tsvector('english', body) @@ to_tsquery('postgresql & performance');

-- pgvector (AI embeddings)
CREATE EXTENSION vector;
CREATE INDEX ON documents USING hnsw (embedding vector_cosine_ops);
```

---

## 26.9 Query Planner & EXPLAIN (Advanced)

```sql
EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM orders WHERE customer_id = 42 AND status = 'PENDING';
```

| Node Type | Meaning |
|-----------|---------|
| Seq Scan | Full table scan — bad on large tables without filter |
| Index Scan | Uses index, fetches heap rows |
| Index Only Scan | Covering index — no heap fetch |
| Bitmap Heap Scan | Index + bitmap for multiple row fetches |
| Nested Loop | Join — good for small sets |
| Hash Join | Build hash table on smaller side |
| Merge Join | Both sides sorted — good for large joins |

**Cost vs actual**: Compare `cost=...` estimates with `actual time=` — bad stats → run `ANALYZE`.

---

## 26.10 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | MVCC? | Multiple row versions; readers don't block writers; snapshots per transaction. |
| 2 | Why VACUUM? | Reclaim space from dead tuples; prevent bloat; update visibility map. |
| 3 | Isolation levels? | Trade consistency vs concurrency; PG default READ COMMITTED. |
| 4 | WAL? | Write-Ahead Log — durability; all changes logged before commit; used for replication. |
| 5 | PgBouncer vs HikariCP? | PgBouncer pools to DB server; HikariCP pools in app — often use both. |
| 6 | Sync vs async replication? | Sync: no data loss on failover, higher latency. Async: faster, possible lag/loss. |
| 7 | Partition pruning? | Planner skips partitions not matching WHERE clause. |
| 8 | JSONB vs JSON? | JSONB binary, indexable, slower insert; JSON text, faster insert. |
| 9 | pgvector? | Vector similarity search inside PostgreSQL — RAG without separate vector DB. |
| 10 | Deadlock? | Two transactions wait on each other — PG detects and aborts one. |

**Must-say keywords**: MVCC, WAL, VACUUM, bloat, snapshot isolation, PgBouncer, read replica lag, partition pruning, EXPLAIN ANALYZE.

---

## §26.10 Production & Interview Depth — Order DB Under Festival Load

PostgreSQL is the backbone for Razorpay ledgers, Zomato orders, and catalog metadata at many Indian unicorns. The interview deep-dive: *"Checkout spikes 20× — what breaks first?"* Usually **connection exhaustion**, then **lock contention on hot rows** (inventory), then **replica lag** making post-order reads stale.

### Trade-off: PgBouncer + Hikari vs RDS Proxy

| Layer | Role | Flipkart-scale note |
|-------|------|---------------------|
| HikariCP (per pod) | 10–20 conns × 200 pods = still too many | Tune `maximumPoolSize`; avoid `threads = connections` |
| PgBouncer (transaction mode) | Multiplex thousands → ~100 DB conns | Required for microservices storm |
| RDS Proxy / Cloud SQL Auth | IAM auth, failover smoothing | Managed option on AWS/GCP India regions |
| Read replica | Offload reports, order history | Accept 1–5s lag; never read-your-writes for payment status |

### Pattern: Partitioned Orders + Optimistic Inventory

```sql
-- Monthly partitions — drop/archive old festivals without full-table VACUUM pain
CREATE TABLE orders_2026_10 PARTITION OF orders
    FOR VALUES FROM ('2026-10-01') TO ('2026-11-01');

CREATE INDEX CONCURRENTLY idx_orders_user_created
    ON orders (user_id, created_at DESC);
```

```java
@Transactional
public boolean reserveInventory(long skuId, int qty) {
    int updated = jdbcTemplate.update(
        "UPDATE inventory SET stock = stock - ? WHERE sku_id = ? AND stock >= ?",
        qty, skuId, qty);
    return updated == 1;  // optimistic — no SELECT FOR UPDATE on hot SKU row
}
```

Pair indexing strategy with [05_Database_Performance_Tuning.md](./05_Database_Performance_Tuning.md). For Big Billion Days prep: run `EXPLAIN (ANALYZE, BUFFERS)` on top 10 queries, verify **index-only scans** on `order_id` lookups, tune `autovacuum` on high-churn tables before sale week. Async inventory sync to Redis for display — authoritative stock stays in PG — see [28_Redis_Distributed_Caching.md](./28_Redis_Distributed_Caching.md). SERIALIZABLE isolation for wallet debits only; default READ COMMITTED for order inserts.
