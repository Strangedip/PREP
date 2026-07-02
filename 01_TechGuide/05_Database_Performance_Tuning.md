# Section 5: Database & Performance Tuning

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [04_API_Design_REST.md](04_API_Design_REST.md) | **Next**: [06_Microservices_Distributed_Systems.md](06_Microservices_Distributed_Systems.md)

---

## 5.1 SQL Optimization: Indexing Strategies, EXPLAIN ANALYZE, and the N+1 Problem

---

### The "Why" & The Problem

The database is almost always the bottleneck in a web application. Your Spring Boot service can handle thousands of requests per second, but if each request executes a poorly optimized query that does a full table scan on a 100-million row table, response times will be measured in seconds, not milliseconds. And unlike application servers, you cannot simply "scale out" a relational database by adding more instances — writes must go to a single primary node.

A company pays you to know this because:
- **A single bad query can take down production**: One developer writes `SELECT * FROM orders WHERE customer_email = 'x'` without an index on `customer_email`. This query scans 100 million rows. The database CPU spikes to 100%. All other queries slow down. The entire platform is affected.
- **Database costs are the highest cloud expense**: RDS instances with 64 vCPUs and 512 GB RAM cost $20,000+/month. Proper indexing and query optimization can let you use a smaller instance, saving hundreds of thousands of dollars per year.
- **N+1 queries are the most common ORM performance bug**: Hibernate silently generates hundreds of queries instead of one. Load test passes with 10 rows, collapses with 10,000 rows.

---

### Interviewer Expectations

- **Indexing**: Know B-Tree (the default), Hash indexes, Composite indexes (column order matters!), Covering indexes, and Partial indexes. Know when an index HURTS performance (write-heavy tables, low-cardinality columns).
- **EXPLAIN ANALYZE**: Know how to read a query execution plan. Identify Seq Scan vs. Index Scan vs. Index Only Scan. Spot full table scans, sort operations, and hash joins.
- **N+1 Problem**: Define it, show the SQL it generates, and demonstrate the fix (JOIN FETCH, `@EntityGraph`, `@BatchSize`).
- **Keywords**: "B-Tree traversal", "composite index leftmost prefix rule", "covering index (index-only scan)", "query execution plan", "Seq Scan", "Index Scan", "bitmap heap scan", "N+1 problem", "JOIN FETCH", "query cost estimator".

---

### The Deep Dive & Solution

#### Indexing Strategies

An index is a separate data structure that allows the database to find rows without scanning the entire table. Think of it as the index at the back of a textbook — instead of reading every page to find "B-Tree", you look up "B-Tree" in the index, which tells you it's on page 42.

##### B-Tree Index (Default — Used in 95% of Cases)

A B-Tree (Balanced Tree) index organizes data in a sorted tree structure. The tree has a root node, internal nodes (branch nodes), and leaf nodes. Each leaf node contains a pointer to the actual table row.

```
                    ┌──────────────┐
                    │  Root Node    │
                    │  [50 | 100]  │
                    └──────┬───────┘
                           │
           ┌───────────────┼───────────────┐
           ▼               ▼               ▼
    ┌──────────┐    ┌──────────┐    ┌──────────┐
    │  < 50    │    │ 50 - 100 │    │  > 100   │
    │ [10|25|40]│   │[60|75|90]│    │[110|130] │
    └────┬─────┘    └────┬─────┘    └────┬─────┘
         │               │               │
    ┌────▼────┐     ┌────▼────┐     ┌────▼────┐
    │Leaf nodes│    │Leaf nodes│    │Leaf nodes│
    │→ row ptrs│    │→ row ptrs│    │→ row ptrs│
    └─────────┘    └─────────┘    └─────────┘
```

**B-Tree supports**: equality (`=`), range (`<`, `>`, `BETWEEN`), prefix `LIKE` (`LIKE 'abc%'`), `ORDER BY`, `MIN`/`MAX`.

**B-Tree does NOT efficiently support**: suffix `LIKE` (`LIKE '%abc'`), functions on indexed column (`WHERE UPPER(name) = 'JOHN'` — use a functional index instead), `!=` / `NOT IN` (must scan most of the index).

**Lookup cost**: O(log n). For a table with 100 million rows, a B-Tree index lookup requires ~7 node traversals (log base ~100 of 100,000,000) plus one I/O to fetch the actual row. Compare this to a full table scan of 100 million rows.

##### Hash Index

A hash index uses a hash function to map column values to bucket locations. It only supports **exact equality** lookups (`=`). No range queries, no sorting.

```sql
-- PostgreSQL hash index (rarely used — B-Tree is usually better even for equality)
CREATE INDEX idx_user_email_hash ON users USING HASH (email);

-- Only useful for: WHERE email = 'john@example.com'
-- Useless for: WHERE email LIKE 'john%' or WHERE email > 'john'
```

**When to use**: Almost never. B-Tree handles equality lookups just as efficiently in practice due to caching, and also supports ranges. Hash indexes can be slightly more compact for very large tables with only equality lookups, but the gain is marginal.

##### Composite Index (Multi-Column Index) — The Leftmost Prefix Rule

A composite index indexes multiple columns together. **Column order matters critically**.

```sql
CREATE INDEX idx_orders_customer_date_status 
ON orders (customer_id, order_date, status);
```

This single index supports ALL of the following queries:
```sql
-- Uses the index (matches leftmost prefix: customer_id)
SELECT * FROM orders WHERE customer_id = 123;

-- Uses the index (matches leftmost prefix: customer_id + order_date)
SELECT * FROM orders WHERE customer_id = 123 AND order_date > '2025-01-01';

-- Uses the index (matches all three columns)
SELECT * FROM orders WHERE customer_id = 123 AND order_date > '2025-01-01' AND status = 'SHIPPED';
```

This index does NOT efficiently support:
```sql
-- CANNOT use the index (skips customer_id, starts with order_date)
SELECT * FROM orders WHERE order_date > '2025-01-01';

-- CANNOT use the index (skips customer_id, starts with status)
SELECT * FROM orders WHERE status = 'SHIPPED';

-- Can use index for customer_id, but NOT for status (skips order_date)
SELECT * FROM orders WHERE customer_id = 123 AND status = 'SHIPPED';
```

**The Leftmost Prefix Rule**: A composite index on `(A, B, C)` can be used for queries filtering on `(A)`, `(A, B)`, or `(A, B, C)`. It cannot be used for `(B)`, `(C)`, `(B, C)`, or `(A, C)` (for the C part — it can still use the A part).

**Column ordering heuristic**:
1. Put **equality conditions** first (high selectivity, narrows search quickly).
2. Put **range conditions** last (the index can only use one range condition).
3. Consider `ORDER BY` columns after WHERE columns for sort elimination.

```sql
-- Optimal for: WHERE customer_id = ? AND status = ? ORDER BY order_date DESC
CREATE INDEX idx_optimal ON orders (customer_id, status, order_date DESC);
-- customer_id (equality) → status (equality) → order_date (sort/range)
```

##### Covering Index (Index-Only Scan)

A covering index includes ALL columns needed by a query in the index itself. The database can satisfy the query entirely from the index without ever accessing the actual table row (the heap). This is called an **Index-Only Scan** and is significantly faster because it avoids the random I/O of fetching rows from the heap.

```sql
-- Query: SELECT order_id, total_amount FROM orders WHERE customer_id = ? AND status = 'SHIPPED'

-- Covering index: includes the WHERE columns AND the SELECT columns
CREATE INDEX idx_covering ON orders (customer_id, status) INCLUDE (order_id, total_amount);
-- The INCLUDE clause adds columns to the leaf nodes without including them in the sort order

-- PostgreSQL syntax: INCLUDE (available since PG11)
-- MySQL syntax: This is implicit — all columns in a composite index are available for covering
```

##### Partial Index (Conditional Index)

An index that only covers rows matching a condition. Smaller than a full index, faster to maintain.

```sql
-- Only index active orders (80% of orders might be completed/cancelled)
CREATE INDEX idx_active_orders ON orders (customer_id, order_date) 
WHERE status = 'ACTIVE';

-- Only index non-null values
CREATE INDEX idx_non_null_email ON users (email) WHERE email IS NOT NULL;
```

##### When Indexes HURT Performance

Indexes are not free. Every index must be updated on every INSERT, UPDATE (of indexed columns), and DELETE. This is called **write amplification**.

**Do NOT index**:
- **Low-cardinality columns**: A boolean column (`is_active`) has only 2 values. An index on it is useless — the database will scan 50% of the table anyway, and a sequential scan is more efficient than many random index lookups.
- **Write-heavy tables with many indexes**: Each INSERT must update every index. A table with 10 indexes on it will have INSERT performance ~10x slower than a table with no indexes.
- **Small tables**: If a table has < 1000 rows, a sequential scan is faster than an index lookup because the entire table fits in a few pages.

#### EXPLAIN ANALYZE — Reading Query Execution Plans

`EXPLAIN ANALYZE` executes the query and shows the actual execution plan with timing information. This is your primary tool for diagnosing slow queries.

```sql
EXPLAIN ANALYZE 
SELECT o.order_id, o.total_amount, c.name
FROM orders o
JOIN customers c ON o.customer_id = c.id
WHERE o.status = 'PENDING'
AND o.order_date > '2025-01-01'
ORDER BY o.order_date DESC
LIMIT 20;
```

**Reading the output (PostgreSQL)**:
```
Limit  (cost=0.85..42.36 rows=20 width=86) (actual time=0.123..0.456 rows=20 loops=1)
  -> Nested Loop  (cost=0.85..15234.56 rows=7312 width=86) (actual time=0.120..0.450 rows=20 loops=1)
       -> Index Scan Backward using idx_orders_date on orders o
          (cost=0.43..8234.56 rows=7312 width=54) (actual time=0.052..0.234 rows=20 loops=1)
            Filter: (status = 'PENDING')
            Rows Removed by Filter: 5
       -> Index Scan using customers_pkey on customers c
          (cost=0.42..0.96 rows=1 width=36) (actual time=0.008..0.008 rows=1 loops=20)
            Index Cond: (id = o.customer_id)
Planning Time: 0.245 ms
Execution Time: 0.512 ms
```

**Key things to look for**:

| Plan Node | What it means | Good or Bad? |
|-----------|--------------|--------------|
| `Seq Scan` | Full table scan — reads every row | BAD (for large tables). Add an index. |
| `Index Scan` | Uses an index, then fetches row from heap | GOOD |
| `Index Only Scan` | Answered entirely from index (covering index) | BEST |
| `Bitmap Heap Scan` | Uses index to find pages, then reads pages sequentially | GOOD for medium selectivity |
| `Nested Loop` | For each row from outer table, scans inner table | GOOD for small result sets, BAD for large |
| `Hash Join` | Builds hash table from one table, probes with other | GOOD for large tables |
| `Merge Join` | Merges two sorted inputs | GOOD when both inputs are already sorted |
| `Sort` | Sorts in memory or on disk | WATCH — expensive if on disk |
| `Rows Removed by Filter` | Rows read but discarded | WATCH — indicates index not selective enough |

**Red flags in EXPLAIN ANALYZE**:
1. `Seq Scan` on a large table → Add an appropriate index.
2. `actual rows` much higher than `rows` (estimate) → Statistics are stale. Run `ANALYZE tablename`.
3. `Rows Removed by Filter: 999999` → The index is returning too many rows, and a filter is discarding them. The index is not selective enough.
4. `Sort Method: external merge Disk` → Sort doesn't fit in `work_mem`. Increase `work_mem` or add an index that provides pre-sorted output.

#### The N+1 Problem

The N+1 problem occurs when an ORM (like Hibernate) executes 1 query to fetch a list of N parent entities, then N additional queries to fetch the associated child entities — resulting in N+1 total queries.

**Example**: Fetching 100 orders with their items.

```java
// Entity definitions
@Entity
public class Order {
    @Id private Long id;
    private String status;
    
    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)  // LAZY is the default
    private List<OrderItem> items;
}

@Entity
public class OrderItem {
    @Id private Long id;
    private String productName;
    private int quantity;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
```

**The N+1 queries generated**:
```sql
-- Query 1: Fetch all orders (1 query)
SELECT * FROM orders WHERE status = 'PENDING';
-- Returns 100 orders

-- Queries 2-101: For each order, fetch its items (100 queries!)
SELECT * FROM order_items WHERE order_id = 1;
SELECT * FROM order_items WHERE order_id = 2;
SELECT * FROM order_items WHERE order_id = 3;
... (97 more queries)
```

With 100 orders, this generates **101 SQL queries**. With 10,000 orders, that's **10,001 queries**. Each query has network round-trip overhead (~1ms), so 10,001 queries = ~10 seconds just in network overhead.

**Fix 1: JOIN FETCH (JPQL)**:
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.status = :status")
    List<Order> findByStatusWithItems(@Param("status") String status);
}

// Generated SQL: ONE query with a JOIN
// SELECT o.*, i.* FROM orders o 
// INNER JOIN order_items i ON o.id = i.order_id 
// WHERE o.status = 'PENDING';
```

**Fix 2: @EntityGraph**:
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @EntityGraph(attributePaths = {"items"})
    List<Order> findByStatus(String status);
    // Generates a LEFT JOIN to eagerly fetch items
}
```

**Fix 3: @BatchSize (for cases where JOIN FETCH isn't suitable)**:
```java
@Entity
public class Order {
    @OneToMany(mappedBy = "order")
    @BatchSize(size = 50)  // Fetch items in batches of 50 orders
    private List<OrderItem> items;
}

// Instead of 100 individual queries, generates:
// SELECT * FROM order_items WHERE order_id IN (1,2,3,...,50);  -- batch 1
// SELECT * FROM order_items WHERE order_id IN (51,52,...,100); -- batch 2
// Total: 3 queries instead of 101
```

**Fix 4: Use a DTO projection (best performance)**:
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    @Query("""
        SELECT new com.myapp.dto.OrderSummaryDTO(
            o.id, o.status, o.totalAmount, 
            i.productName, i.quantity
        )
        FROM Order o JOIN o.items i
        WHERE o.status = :status
    """)
    List<OrderSummaryDTO> findOrderSummaries(@Param("status") String status);
    // Fetches only the columns you need — no entity management overhead
}
```

**Detecting N+1 in production**:
```yaml
# application.yml — log all SQL queries (development only!)
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

For production, use tools like **Hibernate Statistics**, **p6spy**, or **datasource-proxy** to count queries per request and alert on N+1 patterns.

---

## 5.2 Scaling: Database Sharding vs. Partitioning, and Read Replicas

---

### The "Why" & The Problem

A single PostgreSQL or MySQL server can handle impressive workloads — millions of rows, thousands of queries per second. But eventually, you hit limits:
- **Write throughput**: A single server can only process so many writes per second. More CPU cores don't help linearly because of lock contention.
- **Storage**: A single disk can only hold so much data. When your `events` table reaches 5 billion rows and 2TB, even indexed queries slow down.
- **Read latency**: Heavy read traffic (reporting, analytics) competes with write traffic (transactional operations) for the same CPU and I/O.

Scaling strategies address these limits. A company pays you to know this because scaling databases incorrectly is catastrophic — you either over-invest in expensive hardware (vertical scaling), or you introduce complexity that your team can't manage (premature sharding).

---

### Interviewer Expectations

- **Partitioning**: Explain table partitioning (range, list, hash) within a SINGLE database. It reduces the amount of data scanned per query.
- **Sharding**: Explain distributing data across MULTIPLE database instances. Explain shard key selection, routing, cross-shard queries, and rebalancing challenges.
- **Read Replicas**: Explain async replication, replication lag, and when to read from primary vs. replica.
- **Keywords**: "Horizontal partitioning (sharding) vs. vertical partitioning", "shard key", "consistent hashing", "replication lag", "read-after-write consistency", "connection routing", "partition pruning".

---

### The Deep Dive & Solution

#### Table Partitioning (Within a Single Database)

Partitioning splits a large table into smaller physical pieces (partitions) that are still logically one table. Queries that filter on the partition key automatically scan only the relevant partition(s) — this is called **partition pruning**.

```sql
-- Range Partitioning: Split orders by date
CREATE TABLE orders (
    order_id BIGSERIAL,
    customer_id BIGINT NOT NULL,
    order_date DATE NOT NULL,
    total_amount DECIMAL(10,2),
    status VARCHAR(20)
) PARTITION BY RANGE (order_date);

-- Create partitions for each quarter
CREATE TABLE orders_2025_q1 PARTITION OF orders
    FOR VALUES FROM ('2025-01-01') TO ('2025-04-01');
CREATE TABLE orders_2025_q2 PARTITION OF orders
    FOR VALUES FROM ('2025-04-01') TO ('2025-07-01');
CREATE TABLE orders_2025_q3 PARTITION OF orders
    FOR VALUES FROM ('2025-07-01') TO ('2025-10-01');
CREATE TABLE orders_2025_q4 PARTITION OF orders
    FOR VALUES FROM ('2025-10-01') TO ('2026-01-01');
CREATE TABLE orders_2026_q1 PARTITION OF orders
    FOR VALUES FROM ('2026-01-01') TO ('2026-04-01');

-- Query automatically uses partition pruning:
EXPLAIN ANALYZE
SELECT * FROM orders WHERE order_date BETWEEN '2025-10-01' AND '2025-12-31';
-- Only scans orders_2025_q4, not the entire table!
```

**Partitioning types**:
- **Range**: Best for time-series data (logs, events, orders by date). Easy to add new partitions, easy to drop old data (just drop the partition).
- **List**: Best for categorical data. `PARTITION BY LIST (region)` with partitions for 'US', 'EU', 'APAC'.
- **Hash**: Best for even distribution when no natural range/list exists. `PARTITION BY HASH (customer_id)`. Distributes data evenly but no partition pruning for range queries.

**Benefits of partitioning**:
1. **Query performance**: Partition pruning reduces data scanned.
2. **Maintenance**: You can `VACUUM` or `REINDEX` a single partition without locking the entire table.
3. **Data lifecycle**: Drop old partitions instead of running expensive `DELETE` queries.
4. **Parallel query**: PostgreSQL can scan multiple partitions in parallel.

**Partitioning does NOT solve**: Write throughput limits (still a single database). Storage limits of a single server. Replication lag.

#### Database Sharding (Across Multiple Database Instances)

Sharding distributes data across multiple independent database instances (shards). Each shard holds a subset of the data. Together, all shards hold the complete dataset.

```
                    ┌──────────────────┐
                    │   Application     │
                    │   (Shard Router)  │
                    └────────┬─────────┘
                             │
           ┌─────────────────┼─────────────────┐
           │                 │                  │
           ▼                 ▼                  ▼
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │   Shard 0     │  │   Shard 1     │  │   Shard 2     │
    │ customer_id   │  │ customer_id   │  │ customer_id   │
    │   % 3 == 0    │  │   % 3 == 1    │  │   % 3 == 2    │
    │               │  │               │  │               │
    │  Customers:   │  │  Customers:   │  │  Customers:   │
    │  3, 6, 9, 12  │  │  1, 4, 7, 10  │  │  2, 5, 8, 11  │
    └──────────────┘  └──────────────┘  └──────────────┘
```

**Shard Key Selection** — The most critical decision:

The shard key determines which shard a row lives on. A bad shard key causes "hot spots" (all traffic going to one shard) and cross-shard queries (which are extremely slow).

**Good shard key properties**:
1. **High cardinality**: Many distinct values (customer_id, tenant_id — good. status, country — bad).
2. **Uniform distribution**: Approximately equal data and traffic across shards.
3. **Query locality**: Queries should be satisfiable from a single shard. If your most common query filters by `customer_id`, then `customer_id` is a good shard key.

**Bad shard key example**: Sharding by `order_date`. Recent dates get all the writes (today's orders), while old date shards are idle. This creates a hot shard.

**Good shard key example**: Sharding by `tenant_id` in a multi-tenant SaaS application. Each tenant's data lives on one shard. All queries include `tenant_id` in the WHERE clause, so no cross-shard queries.

**Cross-shard queries** — the biggest pain point:
```sql
-- If sharded by customer_id, this query is efficient (single shard):
SELECT * FROM orders WHERE customer_id = 123;

-- But this query requires ALL shards (scatter-gather):
SELECT * FROM orders WHERE order_date > '2025-01-01' ORDER BY total_amount DESC LIMIT 10;
-- Must query ALL shards, collect results, sort again at the application level → SLOW
```

**Shard routing in Spring Boot**:
```java
// Abstract routing data source — determines which shard to use
public class ShardRoutingDataSource extends AbstractRoutingDataSource {
    
    @Override
    protected Object determineCurrentLookupKey() {
        // Get the current shard key from a ThreadLocal (set by a filter/interceptor)
        Long customerId = ShardContext.getCurrentCustomerId();
        if (customerId == null) {
            return "shard-default";  // or throw — depending on your policy
        }
        int shardIndex = (int) (customerId % 3);  // Simple modulo routing
        return "shard-" + shardIndex;
    }
}

@Configuration
public class DataSourceConfig {
    
    @Bean
    public DataSource dataSource() {
        ShardRoutingDataSource routingDS = new ShardRoutingDataSource();
        
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("shard-0", createDataSource("jdbc:postgresql://shard0:5432/mydb"));
        targetDataSources.put("shard-1", createDataSource("jdbc:postgresql://shard1:5432/mydb"));
        targetDataSources.put("shard-2", createDataSource("jdbc:postgresql://shard2:5432/mydb"));
        
        routingDS.setTargetDataSources(targetDataSources);
        routingDS.setDefaultTargetDataSource(targetDataSources.get("shard-0"));
        return routingDS;
    }
}
```

**Rebalancing** — the hardest operational challenge:
When you add a new shard (e.g., going from 3 shards to 4), you need to move data. With simple modulo routing (`customer_id % 3` → `customer_id % 4`), almost ALL data needs to move. **Consistent hashing** minimizes data movement — only ~1/n of data needs to move when adding the nth shard.

**When to shard**: Only when you have exhausted all other options (vertical scaling, read replicas, partitioning, query optimization, caching). Sharding adds enormous complexity: cross-shard joins, distributed transactions, rebalancing, operational overhead.

#### Read Replicas

Read replicas use **asynchronous replication** to copy data from the primary (write) database to one or more replica (read) databases. Write traffic goes to the primary. Read traffic is distributed across replicas.

```
                    ┌──────────────────┐
                    │   Application     │
                    └────────┬─────────┘
                             │
              Writes ────────┼──────── Reads
                    │                    │
                    ▼                    ▼
            ┌──────────────┐    ┌──────────────┐
            │   Primary     │───>│  Replica 1    │ (async replication)
            │   (Read/Write)│───>│  (Read-only)  │
            └──────────────┘    ├──────────────┤
                    │           │  Replica 2    │
                    └──────────>│  (Read-only)  │
                                └──────────────┘
```

**Replication Lag**: Since replication is asynchronous, there is a delay between when data is written to the primary and when it appears on the replica. This lag is typically milliseconds but can be seconds under heavy load. This means a replica may return **stale data**.

**The read-after-write consistency problem**:
1. User creates an order (write goes to primary).
2. User immediately views their order list (read goes to replica).
3. The order hasn't replicated yet → User doesn't see their new order.
4. User panics and creates another order → Duplicate!

**Solution — Read-your-writes consistency**:
```java
// Route reads to primary for a short window after a write
@Service
public class OrderService {
    
    @Autowired private OrderRepository orderRepository;
    
    @Transactional  // Writes go to primary
    public Order createOrder(CreateOrderRequest request) {
        Order order = orderRepository.save(Order.from(request));
        
        // Set a "read from primary" flag for this user for the next 5 seconds
        ReadReplicaRouter.forceReadFromPrimary(Duration.ofSeconds(5));
        
        return order;
    }
    
    @Transactional(readOnly = true)  // Reads go to replica (usually)
    public List<Order> getOrders(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }
}

// Routing DataSource that checks the flag
public class ReadWriteRoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()
            && !ReadReplicaRouter.shouldForceReadFromPrimary()) {
            return "replica";
        }
        return "primary";
    }
}
```

**Spring Boot read/write splitting configuration**:
```yaml
spring:
  datasource:
    primary:
      url: jdbc:postgresql://primary:5432/mydb
      username: app_user
      password: ${DB_PASSWORD}
    replica:
      url: jdbc:postgresql://replica:5432/mydb
      username: app_user_readonly
      password: ${DB_PASSWORD}
```

---

## 5.3 Caching: Redis/Hazelcast Strategies, Cache-Aside, Write-Through, Eviction Policies

---

### The "Why" & The Problem

Even with perfect indexes, a database query still requires:
1. Network round-trip to the database (~0.5-2ms in the same data center).
2. Query parsing and planning.
3. Disk I/O (if data is not in the database's buffer cache).
4. Result serialization and transfer.

For frequently accessed data that doesn't change often (product catalogs, user profiles, configuration), hitting the database every time is wasteful. A cache stores the result in memory (Redis, Hazelcast, or in-process like Caffeine) and serves subsequent requests in microseconds instead of milliseconds.

A company pays you to know this because:
- **Latency**: Cache hit: ~0.1ms. Database query: ~5ms. 50x improvement.
- **Database load**: A cache with a 95% hit rate means the database receives 20x fewer queries. This can be the difference between needing a $5,000/month database and a $20,000/month database.
- **Availability**: If the database goes down temporarily, the cache can serve stale data (graceful degradation).

But caching introduces its own problems: **cache invalidation** ("There are only two hard things in computer science: cache invalidation and naming things" — Phil Karlton), stale data, cache stampede (thundering herd), and memory management.

---

### Interviewer Expectations

- **Caching patterns**: Cache-Aside (Lazy Loading), Write-Through, Write-Behind (Write-Back), Read-Through. Know the trade-offs of each.
- **Cache invalidation**: How do you keep the cache consistent with the database? TTL-based, event-based, write-through.
- **Eviction policies**: LRU (Least Recently Used), LFU (Least Frequently Used), FIFO, TTL-based eviction. When to use each.
- **Cache stampede / thundering herd**: What happens when a popular cache entry expires and 1000 requests simultaneously hit the database? How to prevent it.
- **Keywords**: "Cache-aside", "write-through", "write-behind", "read-through", "cache stampede", "cache warming", "eviction policy", "TTL", "distributed cache", "cache coherence", "look-aside cache".

---

### The Deep Dive & Solution

#### Caching Patterns

##### Cache-Aside (Lazy Loading) — The Most Common Pattern

The application code manages the cache explicitly. On read: check cache first, if miss then query database and populate cache. On write: update database, then invalidate (delete) the cache entry.

```
Read Path:
┌─────────────┐     1. Get from cache     ┌──────────┐
│ Application  │ ─────────────────────────> │  Cache    │
│              │ <───────────── 2. Hit? ──── │ (Redis)  │
│              │                             └──────────┘
│              │     3. If MISS:
│              │ ─── query database ───────> ┌──────────┐
│              │ <── 4. Return data ──────── │ Database  │
│              │ ─── 5. Put in cache ──────> │          │
└─────────────┘                             └──────────┘

Write Path:
┌─────────────┐     1. Update database     ┌──────────┐
│ Application  │ ─────────────────────────> │ Database  │
│              │     2. Delete from cache   ┌──────────┐
│              │ ─────────────────────────> │  Cache    │
└─────────────┘                            └──────────┘
```

```java
@Service
public class ProductService {
    
    @Autowired private ProductRepository productRepository;
    @Autowired private RedisTemplate<String, Product> redisTemplate;
    
    private static final Duration CACHE_TTL = Duration.ofMinutes(30);
    
    // Cache-Aside Read
    public Product getProduct(String productId) {
        String cacheKey = "product:" + productId;
        
        // Step 1: Check cache
        Product cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;  // Cache HIT
        }
        
        // Step 2: Cache MISS — query database
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
        
        // Step 3: Populate cache
        redisTemplate.opsForValue().set(cacheKey, product, CACHE_TTL);
        
        return product;
    }
    
    // Cache-Aside Write
    @Transactional
    public Product updateProduct(String productId, UpdateProductRequest request) {
        // Step 1: Update database
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product = productRepository.save(product);
        
        // Step 2: Invalidate cache (delete, not update — avoids race conditions)
        redisTemplate.delete("product:" + productId);
        
        return product;
    }
}
```

**Using Spring's `@Cacheable` annotation** (simpler):
```java
@Service
public class ProductService {
    
    @Cacheable(value = "products", key = "#productId", unless = "#result == null")
    public Product getProduct(String productId) {
        // This method is only called on cache miss
        return productRepository.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
    }
    
    @CacheEvict(value = "products", key = "#productId")
    @Transactional
    public Product updateProduct(String productId, UpdateProductRequest request) {
        // Cache entry is automatically deleted after this method returns
        Product product = productRepository.findById(productId).orElseThrow();
        product.setName(request.getName());
        return productRepository.save(product);
    }
    
    @CachePut(value = "products", key = "#result.id")
    @Transactional
    public Product createProduct(CreateProductRequest request) {
        // CachePut: always executes the method and puts the result in cache
        return productRepository.save(Product.from(request));
    }
}

// Redis cache configuration
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(30))
            .serializeValuesWith(
                RedisSerializationContext.SerializationPair.fromSerializer(
                    new GenericJackson2JsonRedisSerializer()
                ))
            .disableCachingNullValues();
        
        return RedisCacheManager.builder(connectionFactory)
            .cacheDefaults(config)
            .withCacheConfiguration("products", 
                config.entryTtl(Duration.ofHours(1)))  // Different TTL per cache
            .withCacheConfiguration("user-sessions",
                config.entryTtl(Duration.ofMinutes(15)))
            .build();
    }
}
```

**Pros**: Simple, application controls the logic, cache is populated on demand (no wasted memory on unused data).
**Cons**: First request for each key is always a cache miss (cold start). Risk of stale data between write and cache invalidation.

##### Write-Through

Every write goes through the cache first, and the cache synchronously writes to the database. Reads always go to the cache, which is guaranteed to have the latest data.

```
Write: App → Cache → Database (synchronous)
Read:  App → Cache (always a hit if previously written)
```

**Pros**: Cache is always consistent with the database. No stale reads.
**Cons**: Write latency increases (write to cache + write to database). If data is written but never read, cache memory is wasted.

##### Write-Behind (Write-Back)

Every write goes to the cache, which asynchronously writes to the database in batches. This dramatically reduces database write load.

```
Write: App → Cache → (async, batched) → Database
Read:  App → Cache
```

**Pros**: Extremely fast writes (only to cache). Batching reduces database load (100 individual writes → 1 batch write).
**Cons**: Data loss risk — if the cache crashes before flushing to the database, writes are lost. Complex to implement correctly.

**Use case**: Write-behind is common in gaming (player scores), IoT (sensor data), and analytics (event counters) where temporary data loss is acceptable for the performance gain.

#### Cache Stampede (Thundering Herd) Prevention

**The problem**: A popular cache entry (e.g., the homepage product list viewed by 10,000 users/minute) expires. Suddenly, all 10,000 concurrent users experience a cache miss simultaneously and all query the database. The database is overwhelmed.

**Solution 1: Locking (only one thread refreshes)**:
```java
public Product getProduct(String productId) {
    String cacheKey = "product:" + productId;
    Product cached = redisTemplate.opsForValue().get(cacheKey);
    
    if (cached != null) {
        return cached;
    }
    
    // Try to acquire a lock — only one thread will refresh the cache
    String lockKey = "lock:product:" + productId;
    Boolean acquired = redisTemplate.opsForValue()
        .setIfAbsent(lockKey, "locked", Duration.ofSeconds(10));
    
    if (Boolean.TRUE.equals(acquired)) {
        try {
            // Double-check: another thread may have populated the cache
            cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) return cached;
            
            // Only THIS thread queries the database
            Product product = productRepository.findById(productId).orElseThrow();
            redisTemplate.opsForValue().set(cacheKey, product, Duration.ofMinutes(30));
            return product;
        } finally {
            redisTemplate.delete(lockKey);
        }
    } else {
        // Another thread is refreshing — wait and retry
        Thread.sleep(50);  // Brief wait
        return getProduct(productId);  // Retry — cache should be populated now
    }
}
```

**Solution 2: Probabilistic early expiration (proactive refresh)**:
```java
// Instead of all entries expiring at exactly TTL, each request has a small probability
// of refreshing the cache BEFORE it actually expires.
// As the entry approaches expiration, the probability of refresh increases.
public Product getProductWithEarlyRefresh(String productId) {
    String cacheKey = "product:" + productId;
    CacheEntry<Product> entry = cacheStore.get(cacheKey);
    
    if (entry != null) {
        double ttlRemaining = entry.getTtlRemainingSeconds();
        double totalTtl = entry.getTotalTtlSeconds();
        double refreshProbability = Math.exp(-ttlRemaining / (totalTtl * 0.1));
        
        if (Math.random() < refreshProbability) {
            // Proactively refresh in background
            CompletableFuture.runAsync(() -> refreshCache(productId));
        }
        return entry.getValue();
    }
    
    // Cache miss — synchronous refresh
    return refreshCache(productId);
}
```

#### Eviction Policies

When the cache is full and a new entry needs to be added, an eviction policy determines which existing entry to remove.

| Policy | How it works | Best for |
|--------|-------------|----------|
| **LRU (Least Recently Used)** | Evicts the entry that was accessed LEAST recently | General purpose. Works well when recent data is more likely to be accessed again |
| **LFU (Least Frequently Used)** | Evicts the entry with the LOWEST access count | Long-lived popular items (product catalog). Better than LRU when access patterns have long-term favorites |
| **FIFO (First In, First Out)** | Evicts the OLDEST entry regardless of access pattern | Simple caching where insertion order matters |
| **TTL (Time To Live)** | Entries automatically expire after a fixed duration | Data that has a known staleness tolerance (e.g., "product prices are valid for 1 hour") |
| **Random** | Evicts a random entry | Rarely used. Can be surprisingly effective and is O(1) |

**Redis eviction policies** (configured with `maxmemory-policy`):
- `allkeys-lru`: LRU across all keys (most common for general caches).
- `volatile-lru`: LRU only among keys with an expiry set.
- `allkeys-lfu`: LFU across all keys (better for skewed access patterns).
- `noeviction`: Returns errors when memory is full (use for critical data that must not be lost).

```
# Redis configuration
maxmemory 2gb
maxmemory-policy allkeys-lru
```

**Redis vs. Hazelcast**:

| Aspect | Redis | Hazelcast |
|--------|-------|-----------|
| **Architecture** | External server (client-server) | Embedded or client-server (JVM-native) |
| **Data structures** | Rich (strings, lists, sets, sorted sets, hashes, streams) | Java collections API (Map, Queue, List, Topic) |
| **Persistence** | RDB snapshots + AOF log | Backed by external DB or Hot Restart |
| **Language** | C (ultra-fast) | Java (easier integration with Spring) |
| **Clustering** | Redis Cluster (auto-sharding) | Built-in distributed data structures |
| **Use case** | General caching, session store, pub-sub, rate limiting | Near-cache, distributed computing, in-memory data grid |
| **Near-cache** | Not built-in (client-side caching in Redis 6+) | First-class near-cache (L1 in JVM, L2 in cluster) |

**Hazelcast Near-Cache** — L1 + L2 caching:
```java
// Hazelcast Near-Cache: Frequently accessed data is cached INSIDE the JVM (L1)
// Less frequently accessed data is in the Hazelcast cluster (L2)
// This eliminates the network round-trip for hot data

@Configuration
public class HazelcastConfig {
    @Bean
    public Config hazelcastConfig() {
        Config config = new Config();
        
        MapConfig mapConfig = new MapConfig("products");
        mapConfig.setTimeToLiveSeconds(3600);
        
        NearCacheConfig nearCacheConfig = new NearCacheConfig();
        nearCacheConfig.setMaxIdleSeconds(300);
        nearCacheConfig.setInMemoryFormat(InMemoryFormat.OBJECT);
        nearCacheConfig.setEvictionConfig(
            new EvictionConfig()
                .setEvictionPolicy(EvictionPolicy.LRU)
                .setMaxSizePolicy(MaxSizePolicy.ENTRY_COUNT)
                .setSize(10000)
        );
        mapConfig.setNearCacheConfig(nearCacheConfig);
        
        config.addMapConfig(mapConfig);
        return config;
    }
}
```

