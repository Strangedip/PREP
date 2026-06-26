# Section 27: NoSQL Databases — MongoDB, DynamoDB, Cassandra

> **Level**: MID+ (when to use NoSQL) to SR+ (partitioning, consistency models, production tuning)
> **Complements**: [05_Database_Performance_Tuning.md](./05_Database_Performance_Tuning.md) Section 5.2 (sharding concepts)

---

## 27.1 SQL vs NoSQL — Decision Framework

| Choose SQL (PostgreSQL) | Choose NoSQL |
|------------------------|--------------|
| Complex joins, ACID transactions | Flexible/evolving schema |
| Strong consistency required | Massive write throughput |
| Well-defined relational model | Document/nested data natural fit |
| Reporting with SQL | Geo-distributed, tunable consistency |

**Interview answer**: "Start with PostgreSQL. Add NoSQL when you have measured bottlenecks SQL can't solve — not because it's trendy."

---

## 27.2 NoSQL Categories

| Type | Examples | Best For |
|------|----------|----------|
| **Document** | MongoDB, Couchbase | JSON documents, content, catalogs |
| **Key-Value** | Redis, DynamoDB | Caching, session, high-throughput lookups |
| **Wide-Column** | Cassandra, HBase | Time-series, IoT, write-heavy logs |
| **Graph** | Neo4j, Amazon Neptune | Social graphs, recommendations, fraud rings |

---

## 27.3 MongoDB Deep Dive

### Data Model
```json
{
  "_id": ObjectId("..."),
  "userId": "u123",
  "items": [{ "sku": "A1", "qty": 2 }],
  "shipping": { "city": "Bangalore", "zip": "560001" }
}
```

**Embedded vs referenced**: Embed when data is read together; reference when large/growing arrays.

### Indexing
```javascript
db.orders.createIndex({ userId: 1, createdAt: -1 })
db.products.createIndex({ name: "text", description: "text" })
```

### Transactions (Multi-document ACID since 4.0)
```javascript
session.startTransaction();
orders.insertOne({...}, { session });
inventory.updateOne({ sku: "A1" }, { $inc: { stock: -1 } }, { session });
session.commitTransaction();
```

### Sharding
- **Shard key** choice is permanent — bad key causes hot shards
- Good: high cardinality, even distribution (`userId`, `hashed orderId`)
- Bad: low cardinality (`status`, `country` with skew)

### When MongoDB Fails Interviews
- Need complex multi-table joins across collections
- Strict ACID across many entities without careful design
- Analytics requiring SQL window functions

---

## 27.4 Amazon DynamoDB Deep Dive

### Core Concepts
| Concept | Description |
|---------|-------------|
| **Table** | Collection of items |
| **Partition Key** | Determines physical partition (hash) |
| **Sort Key** | Optional — enables range queries within partition |
| **GSI / LSI** | Global/Local Secondary Indexes for alternate access patterns |

### Access Pattern First Design
```
You CANNOT query by arbitrary fields — design keys around how you read data.

PK: USER#123  SK: PROFILE     → get user profile
PK: USER#123  SK: ORDER#456   → get specific order
PK: USER#123  SK: ORDER#*     → all orders for user (range query)
```

### Consistency
- **Eventually consistent reads** (default) — cheaper, may be stale
- **Strongly consistent reads** — 2x cost, reads latest write

### DynamoDB Streams + Lambda
Change data capture for event-driven architectures.

### Single-Table Design
Advanced pattern: one table, composite keys encode entity relationships (used heavily in AWS serverless).

---

## 27.5 Apache Cassandra Deep Dive

### Architecture
- **Peer-to-peer** — no master node
- **Partition key** → token ring → replica nodes
- **Replication factor** (RF=3) — data copied to N nodes

### Tunable Consistency
```
ONE   — fast, may read stale
QUORUM — majority of replicas (RF=3 → 2 nodes)
ALL   — all replicas — slowest, strongest
```

### Write Path
```
Client → any node (coordinator) → write to commit log + memtable → SSTable (LSM tree)
```

### Best For
- **Write-heavy** time-series (metrics, logs, IoT)
- **Multi-datacenter** replication native
- **High availability** — no single point of failure

### Not For
- Complex joins, ad-hoc analytics, strong multi-row transactions

---

## 27.6 CAP at the Database Level

| Database | Typical Position |
|----------|------------------|
| PostgreSQL | CP (consistency + partition tolerance) |
| MongoDB | Configurable (default strong in single doc) |
| Cassandra | AP (availability + partition tolerance) |
| DynamoDB | AP with optional strong reads |

**PACELC**: If Partition → choose A or C; Else → choose Latency or Consistency.

---

## 27.7 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | When NoSQL over SQL? | Schema flexibility, massive scale writes, geo-distribution — with trade-offs. |
| 2 | MongoDB shard key? | High cardinality, even distribution; immutable after sharding. |
| 3 | DynamoDB hot partition? | Bad partition key concentrates traffic — use random suffix or write sharding. |
| 4 | GSI vs LSI? | GSI: alternate partition key, own throughput. LSI: same partition key, different sort. |
| 5 | Cassandra write path? | Commit log → memtable → SSTable; LSM tree compaction. |
| 6 | QUORUM in Cassandra? | Majority of replicas acknowledge — balance consistency vs latency. |
| 7 | Document vs wide-column? | Document: flexible JSON. Wide-column: massive writes, time-series. |
| 8 | MongoDB transactions? | Multi-document ACID since 4.0; use when needed, not default pattern. |
| 9 | Single-table DynamoDB? | One table, composite keys encode entity types — fewer tables, complex keys. |
| 10 | Neo4j use case? | Graph traversals — friends-of-friends, shortest path, fraud detection. |

**Must-say keywords**: partition key, shard key, LSM tree, SSTable, GSI, tunable consistency, QUORUM, access pattern first, hot partition.
