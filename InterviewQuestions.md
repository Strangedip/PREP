# 🎯 Top 218 Interview Questions — Lead Software Engineer

> **Purpose**: Rapid-fire reference. Review these the night before your interview.
> **Format**: Question → One-liner answer → Section reference for deep dive.

---

## Java Core & Modern Features

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 1 | What are Records in Java? | Immutable data carriers with auto-generated equals/hashCode/toString. | Section 1.1 |
| 2 | What are Sealed Classes? | Restrict which classes can extend a type — exhaustive pattern matching. | Section 1.1 |
| 3 | Virtual Threads vs Platform Threads? | Virtual threads are lightweight (JVM-managed, not OS threads). 1M+ concurrent. | Section 1.2 |
| 4 | What is Structured Concurrency? | Treat concurrent tasks as a unit — if one fails, all cancel. | Section 1.2 |
| 5 | Pattern Matching in Java? | `instanceof` checks + variable binding in one step. Works with switch. | Section 1.1 |
| 6 | String interning? | String pool in heap. `"hello" == "hello"` is true. `new String("hello")` creates new object. | Section 15.1 |
| 7 | HashMap internals? | Array of buckets + linked list/red-black tree. Hash → spread → bucket index. | Section 15.1 |
| 8 | HashMap vs ConcurrentHashMap? | HashMap: not thread-safe. CHM: per-bucket locking, atomic compute ops. | Section 15.5 |
| 9 | When does HashMap treeify? | When a bucket has ≥8 entries AND table size ≥64, linked list → red-black tree. | Section 15.1 |
| 10 | fail-fast vs fail-safe iterators? | fail-fast: ConcurrentModificationException. fail-safe: works on snapshot. | Section 15.1 |
| 11 | ArrayList vs LinkedList? | ArrayList wins 99%. O(1) random access, cache-friendly. LinkedList: O(n) access. | Section 15.1 |
| 12 | What is Stream lazy evaluation? | Intermediate operations don't execute until a terminal operation is invoked. | Section 15.2 |
| 13 | When to use parallel streams? | Large datasets (>10K), CPU-bound, splittable source, no shared mutable state. | Section 15.2 |
| 14 | Optional best practices? | Use as return type only. Never as method parameter or field. Use map/flatMap, not get(). | Section 15.3 |
| 15 | What is CompletableFuture? | Composable, async future with chaining, combining, timeout, and exception handling. | Section 15.7 |

## JVM & Performance

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 16 | JVM memory areas? | Heap (Young+Old), Metaspace, Code Cache, Thread Stacks, Direct Memory. | Section 18.1 |
| 17 | G1GC vs ZGC? | G1: general purpose ~200ms pauses. ZGC: <1ms pauses, slightly lower throughput. | Section 18.2 |
| 18 | How to diagnose a memory leak? | Heap dump → Eclipse MAT → Dominator Tree → find retained objects → trace to GC root. | Section 18.3 |
| 19 | What is a flame graph? | CPU profiling visualization. Wide bars = hotspots. X=samples, Y=call depth. | Section 18.3 |
| 20 | Thread pool sizing? | CPU-bound: cores. I/O-bound: cores × (1 + wait/compute). | Section 15.8 |
| 21 | What is JFR? | Java Flight Recorder — production-safe, zero-overhead JVM profiler. | Section 18.3 |
| 22 | Explain thread dump analysis | Look for BLOCKED (lock contention), WAITING (pool exhaustion), deadlocks. | Section 18.3 |
| 23 | What JVM flags do you use? | -Xms/-Xmx (equal), GC choice, HeapDumpOnOutOfMemoryError, GC logging. | Section 18.1 |

## Concurrency

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 24 | synchronized vs ReentrantLock? | ReentrantLock: fairness, tryLock, interruptible, multiple Conditions. | Section 15.4 |
| 25 | volatile vs AtomicInteger? | volatile: visibility only. Atomic: visibility + atomicity (CAS-based). | Section 15.6 |
| 26 | What is CAS? | Compare-And-Swap: hardware-level atomic operation. Read → Compare → Swap atomically. | Section 15.6 |
| 27 | CountDownLatch vs CyclicBarrier? | CDL: one-shot, wait for N events. CB: reusable, N threads wait for each other. | Section 15.4 |
| 28 | What is a Semaphore? | Controls access to N permits. Use: rate limiting, connection pooling. | Section 15.4 |
| 29 | LongAdder vs AtomicLong? | LongAdder: faster under high contention (striped cells). AtomicLong: single counter. | Section 15.6 |
| 30 | How does ThreadLocal work? | Per-thread storage map (Thread → value). Must clear() to avoid memory leaks. | Section 2.1 |
| 31 | What is a ReadWriteLock? | Multiple concurrent readers OR one exclusive writer. Better than synchronized for read-heavy. | Section 15.4 |

## Spring Boot & Ecosystem

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 32 | Spring Bean lifecycle? | Constructor → @PostConstruct → afterPropertiesSet → init → ... → @PreDestroy → destroy. | Section 2.2 |
| 33 | Spring AutoConfiguration? | Reads AutoConfiguration.imports, applies @Conditional annotations. | Section 2.2 |
| 34 | @Transactional self-invocation? | Self-calls bypass CGLIB proxy. Fix: inject self, use ObjectProvider. | Section 16.5 |
| 35 | CGLIB vs JDK Dynamic Proxy? | CGLIB: subclass-based. JDK: interface-based. Spring Boot defaults to CGLIB. | Section 16.5 |
| 36 | Spring Data JPA N+1 problem? | Use JOIN FETCH in JPQL, @EntityGraph, or @BatchSize. | Section 16.1 |
| 37 | Specifications in Spring Data? | Composable, dynamic query predicates. Use for complex search/filter. | Section 16.1 |
| 38 | Spring WebFlux vs MVC? | WebFlux: non-blocking, Netty, event-loop. MVC: thread-per-request, Tomcat. | Section 16.2 |
| 39 | Mono vs Flux? | Mono: 0-1 element. Flux: 0-N elements. Both lazy and subscribe-on-demand. | Section 16.2 |
| 40 | @EventListener vs @TransactionalEventListener? | @EventListener: same TX. @TransactionalEventListener: runs after TX commits. | Section 16.8 |
| 41 | What does @RefreshScope do? | Bean re-created when /actuator/refresh is called. For dynamic config. | Section 16.3 |
| 42 | Spring Batch chunk processing? | Read N → Process N → Write N in one transaction. | Section 16.4 |
| 43 | Spring Security OAuth2 flow? | Client → Auth Server → token → Resource Server validates JWT. | Section 12.1 |
| 44 | How to create a custom starter? | Auto-configuration class + spring.factories/imports + @ConditionalOn*. | Section 2.2 |

## Design Patterns & SOLID

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 45 | SOLID — Single Responsibility? | A class should have one reason to change. | Section 3.1 |
| 46 | SOLID — Open/Closed? | Open for extension, closed for modification (use interfaces, strategy pattern). | Section 3.1 |
| 47 | SOLID — Liskov Substitution? | Subtypes must be substitutable for their base types without breaking behavior. | Section 3.1 |
| 48 | SOLID — Interface Segregation? | No client should depend on methods it doesn't use. Small, focused interfaces. | Section 3.1 |
| 49 | SOLID — Dependency Inversion? | Depend on abstractions, not concretions. High-level modules don't depend on low-level. | Section 3.1 |
| 50 | Strategy Pattern? | Define a family of algorithms, encapsulate each, make them interchangeable. | Section 3.2 |
| 51 | Observer Pattern? | One-to-many dependency. When one changes, all dependents are notified. | Section 3.2 |
| 52 | Builder Pattern? | Step-by-step object construction. Use for objects with many optional parameters. | Section 3.2 |
| 53 | Factory Method vs Abstract Factory? | Factory Method: single product. Abstract Factory: family of related products. | Section 3.2 |
| 54 | Decorator Pattern? | Add behavior to objects dynamically without affecting other instances. | Section 3.2 |
| 55 | Singleton — why avoid? | Global state, hard to test, hides dependencies. Use DI framework instead. | Section 3.2 |

## Database & Performance

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 56 | B-Tree vs B+Tree index? | B+Tree: all data in leaf nodes, linked. Better for range queries. Used by most DBs. | Section 5.1 |
| 57 | Clustered vs Non-clustered index? | Clustered: rows stored in index order (one per table). Non-clustered: separate pointer. | Section 5.1 |
| 58 | How to read EXPLAIN ANALYZE? | Seq Scan (bad), Index Scan (good), Nested Loop (small joins), Hash Join (large joins). | Section 5.2 |
| 59 | N+1 query problem? | 1 query for parents + N queries for children. Fix: JOIN, batch loading, entity graph. | Section 5.2 |
| 60 | Database sharding strategies? | Hash-based (even distribution), range-based (time series), geography-based. | Section 5.3 |
| 61 | ACID properties? | Atomicity, Consistency, Isolation, Durability. | Section 5.1 |
| 62 | Isolation levels? | Read Uncommitted → Read Committed → Repeatable Read → Serializable. Trade-off: consistency vs performance. | Section 5.1 |
| 63 | Optimistic vs Pessimistic locking? | Optimistic: version column, retry on conflict. Pessimistic: SELECT FOR UPDATE, blocks. | Section 5.2 |
| 64 | Redis cache patterns? | Cache-Aside, Read-Through, Write-Through, Write-Behind. | [§28 Redis](01_TechGuide/28_Redis_Distributed_Caching.md) |
| 65 | Cache invalidation strategies? | TTL, event-based invalidation, write-through. "Two hard problems in CS." | [§28 Redis](01_TechGuide/28_Redis_Distributed_Caching.md) |
| 66a | PostgreSQL MVCC? | Multi-version rows; readers don't block writers; snapshot per transaction. | [§26 PostgreSQL](01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md) |
| 66b | Why VACUUM in PostgreSQL? | Reclaim dead tuple space; prevent bloat; update visibility map. | [§26 PostgreSQL](01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md) |
| 66c | DynamoDB partition key design? | Access-pattern-first; high cardinality; avoid hot partitions. | [§27 NoSQL](01_TechGuide/27_NoSQL_Databases_Guide.md) |
| 66d | MongoDB shard key mistakes? | Low cardinality (status) causes hot shards; immutable after sharding. | [§27 NoSQL](01_TechGuide/27_NoSQL_Databases_Guide.md) |
| 66e | Cache stampede fix? | Mutex lock, probabilistic early refresh, background rebuild of hot keys. | [§28 Redis](01_TechGuide/28_Redis_Distributed_Caching.md) |
| 66f | Redis Cluster hash slots? | 16384 slots; CRC16(key); hash tags `{user}` for co-located keys. | [§28 Redis](01_TechGuide/28_Redis_Distributed_Caching.md) |
| 66g | SQL window functions? | RANK, LAG, SUM OVER — aggregates without collapsing rows. | [§35 SQL](01_TechGuide/35_SQL_Fundamentals.md) |
| 66h | LEFT JOIN vs INNER JOIN? | INNER: matches only. LEFT: all left rows, NULL for non-matching right. | [§35 SQL](01_TechGuide/35_SQL_Fundamentals.md) |

## Microservices & Distributed Systems

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 66 | CAP Theorem? | Choose 2 of 3: Consistency, Availability, Partition Tolerance. (Always need P). | Section 6.1 |
| 67 | Circuit Breaker pattern? | Closed → Open (on failures) → Half-Open (test recovery). Prevent cascading failure. | Section 6.2 |
| 68 | Saga pattern? | Distributed transaction via local transactions + compensating actions on failure. | Section 19.6 |
| 69 | Choreography vs Orchestration? | Choreography: event-driven, decentralized. Orchestration: central coordinator. | Section 19.6 |
| 70 | Service Discovery? | Services register themselves. Clients look up by name. Eureka, Consul, K8s DNS. | Section 16.3 |
| 71 | API Gateway? | Single entry point. Handles routing, auth, rate limiting, circuit breaking. | Section 16.3 |
| 72 | Eventual Consistency? | Data will be consistent eventually, not immediately. Trade-off for availability. | Section 6.3 |
| 73 | 2PC vs Saga? | 2PC: strong consistency, blocking. Saga: eventual consistency, non-blocking. | Section 6.3 |
| 74 | Idempotency? | Same request multiple times = same result. Essential for retries. Use idempotency key. | Section 4.2 |
| 75 | Rate Limiting algorithms? | Token Bucket, Leaky Bucket, Fixed/Sliding Window. | Section 6.2 |
| 76 | Bulkhead pattern? | Isolate failures by partitioning resources. Separate thread pools per dependency. | Section 6.2 |

## System Design

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 77 | Design a URL shortener? | Hash function → Base62 encoding → Key-Value store. Redirect with 301/302. | Section 7 |
| 78 | Design a chat system? | WebSocket + message queue + read receipts + online/offline status. | Section 7 |
| 79 | Design a rate limiter? | Token bucket (per-user) + Redis (distributed) + sliding window. | Section 7 |
| 80 | Design a notification system? | Fan-out service + multiple channels (push, SMS, email) + priority queue + DLQ. | Section 7 |
| 81 | Horizontal vs Vertical scaling? | Vertical: bigger machine. Horizontal: more machines. Horizontal is preferred. | Section 7.1 |
| 82 | Load Balancer algorithms? | Round Robin, Least Connections, IP Hash, Weighted, Consistent Hashing. | Section 7.1 |
| 83 | Consistent Hashing? | Hash ring where adding/removing nodes only affects neighbors. Used in distributed caches. | Section 7.1 |
| 84 | Database replication? | Primary (writes) → Replicas (reads). Async = faster but eventual consistency. | Section 7.2 |
| 85 | CDN? | Edge servers caching content close to users. Reduces latency and origin load. | Section 17.7 |

## Event-Driven & Kafka

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 86 | Kafka architecture? | Topics → Partitions → Brokers. Consumer Groups for parallelism. | Section 19.2 |
| 87 | Kafka ordering guarantee? | Per-partition only. Use message key to route related messages to same partition. | Section 19.2 |
| 88 | Exactly-once in Kafka? | Idempotent producer + transactions + read_committed consumers. | Section 19.2 |
| 89 | What is Event Sourcing? | Store events as source of truth. Replay to rebuild state. Full audit trail. | Section 19.4 |
| 90 | What is CQRS? | Separate write model (commands) from read model (queries/projections). | Section 19.4 |
| 91 | Dead Letter Queue? | Topic for messages that failed after all retries. Ops team investigates. | Section 19.5 |
| 92 | Kafka vs RabbitMQ? | Kafka: high-throughput, replay, streaming. RabbitMQ: low-latency, routing, tasks. | Section 19.7 |
| 93 | What is CDC? | Change Data Capture — capture DB changes as events via transaction log (Debezium). | Section 19.8 |
| 94 | Schema evolution? | Avro + Schema Registry with backward/forward compatibility. | Section 19.3 |
| 95 | Outbox pattern? | Write event to outbox table in same TX as data. Separate process publishes to Kafka. | Section 19.6 |

## API Design

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 96 | REST maturity levels? | L0: one URL. L1: resources. L2: HTTP verbs. L3: HATEOAS. | Section 4.1 |
| 97 | API versioning strategies? | URL (/v2/), Header (Accept), Query param. URL versioning most common. | Section 4.2 |
| 98 | HATEOAS? | API responses include links to related actions/resources. | Section 4.1 |
| 99 | Pagination strategies? | Offset (LIMIT/OFFSET), Cursor (keyset), Page token. Cursor best for large datasets. | Section 4.2 |
| 100 | REST vs GraphQL? | REST: resource URLs + HTTP caching. GraphQL: single endpoint, client-defined shape, N+1 risk. | [§21 GraphQL](01_TechGuide/21_GraphQL_and_Alternative_APIs.md) |

## Angular & Frontend

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 101 | Angular Signals vs RxJS? | Signals: synchronous, fine-grained reactivity. RxJS: async streams, operators. | Section 8.2 |
| 102 | Standalone Components? | No NgModule needed. Self-contained with imports array. | Section 8.1 |
| 103 | OnPush Change Detection? | Only runs when inputs change (by reference) or events fire. Better performance. | Section 8.3 |
| 104 | Angular Lazy Loading? | Load feature modules on demand via route config `loadChildren`. | Section 8.3 |
| 105 | RxJS switchMap vs mergeMap? | switchMap: cancel previous. mergeMap: run all concurrently. | Section 8.2 |
| 106 | Angular SSR? | Server-Side Rendering with Angular Universal. Better SEO and initial load. | Section 8.3 |
| 107 | State management approaches? | Signals (simple), NgRx (complex), Services+BehaviorSubject (medium). | Section 8.2 |

## Networking & Protocols

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 108 | TCP vs UDP? | TCP: reliable, ordered. UDP: fast, unreliable. | Section 17.1 |
| 109 | HTTP/1.1 vs HTTP/2? | HTTP/2: multiplexing, binary framing, header compression, single TCP connection. | Section 17.2 |
| 110 | HTTP/2 vs HTTP/3? | HTTP/3: QUIC (UDP), no TCP HOL blocking, 0-RTT, connection migration. | Section 17.2 |
| 111 | What is TLS? | Encrypts data in transit. TLS 1.3: 1-RTT handshake, forward secrecy mandatory. | Section 17.3 |
| 112 | What is mTLS? | Mutual TLS — both sides present certificates. Used in service mesh. | Section 17.3 |
| 113 | How DNS resolution works? | Browser → OS → Resolver → Root → TLD → Authoritative → IP address. | Section 17.4 |
| 114 | WebSocket vs SSE? | WebSocket: bidirectional. SSE: server-to-client only, built-in reconnect. | Section 17.5 |
| 115 | When to use gRPC? | Internal microservices, high throughput, streaming, polyglot environments. | Section 17.6 |
| 116 | Connection pooling? | Reuse TCP connections instead of creating new ones. HikariCP, Apache HttpClient. | Section 17.8 |
| 116a | Security Group vs NACL? | SG: stateful, instance-level. NACL: stateless, subnet-level, allow+deny. | [§29 Networking](01_TechGuide/29_Advanced_Networking_Infrastructure.md) |
| 116b | L4 vs L7 load balancer? | L4: TCP/IP routing. L7: HTTP path/host routing, TLS termination. | [§29 Networking](01_TechGuide/29_Advanced_Networking_Infrastructure.md) |
| 116c | NAT Gateway purpose? | Private subnet outbound internet without public IPs on instances. | [§29 Networking](01_TechGuide/29_Advanced_Networking_Infrastructure.md) |
| 116d | K8s liveness vs readiness? | Liveness: restart if dead. Readiness: remove from Service if not ready. | [§30 Kubernetes](01_TechGuide/30_Kubernetes_Deep_Dive.md) |
| 116e | S3 vs EBS? | S3: object storage, HTTP API. EBS: block storage attached to EC2. | [§31 Cloud](01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md) |
| 116f | SQS vs SNS? | SQS: queue, one consumer per message. SNS: pub/sub fan-out. | [§31 Cloud](01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md) |
| 116g | Linux OOM Killer? | Kernel kills process when system runs out of memory. | [§32 Linux](01_TechGuide/32_Operating_Systems_and_Linux.md) |
| 116h | Elasticsearch inverted index? | Term → document list — fast full-text search vs B-Tree exact match. | [§34 Elasticsearch](01_TechGuide/34_Search_Engines_Elasticsearch.md) |

## Testing

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 117 | Testing pyramid? | Unit (many) → Integration (some) → E2E (few). Unit = fast, E2E = slow. | Section 9.1 |
| 118 | Mockito vs actual dependencies? | Mock external dependencies (DB, APIs). Don't mock simple value objects. | Section 9.2 |
| 119 | Testcontainers? | Spin up real Docker containers (DB, Kafka) for integration tests. | Section 9.3 |
| 120 | Contract testing? | Consumer-driven contracts ensure API changes don't break consumers. Pact, Spring Cloud Contract. | Section 9.4 |
| 121 | Performance testing tools? | JMeter, Gatling, k6. Gatling is code-based (Scala DSL). | Section 9.5 |
| 122 | What is mutation testing? | Modify code, check if tests catch changes. PITest for Java. | Section 9.2 |

## DevOps & Docker

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 123 | Docker multi-stage build? | Build in one stage, copy artifacts to minimal runtime image. Smaller images. | Section 10.1 |
| 124 | Docker layer caching? | Each instruction = layer. Put frequently-changing instructions last. | Section 10.1 |
| 125 | Blue-Green deployment? | Two identical envs. Switch traffic atomically. Instant rollback. | Section 10.2 |
| 126 | Canary deployment? | Route small % to new version. Monitor. Gradually increase. | Section 10.2 |
| 127 | Feature flags? | Toggle features at runtime without deployment. LaunchDarkly, Unleash. | Section 10.2 |
| 128 | GitOps? | Git as source of truth for infrastructure. Push to git → automated deployment. | Section 10.3 |

## Observability

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 129 | Three pillars of observability? | Logs (what happened), Metrics (how much), Traces (where/how long). | Section 11.1 |
| 130 | Structured logging? | JSON format with correlation IDs. Machine-parseable, searchable. | Section 11.1 |
| 131 | Distributed tracing? | Track request across services via trace ID. OpenTelemetry, Jaeger, Zipkin. | Section 11.3 |
| 132 | RED method? | Rate (requests/sec), Errors (error rate), Duration (latency). For request-driven services. | Section 11.2 |
| 133 | USE method? | Utilization, Saturation, Errors. For resource monitoring (CPU, memory, disk). | Section 11.2 |

## Security

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 134 | OWASP Top 10 (2025)? | A01 Access Control (+SSRF), A02 Misconfig, A03 Supply Chain, A04 Crypto, A05 Injection, A06 Insecure Design, A07 Auth, A08 Integrity, A09 Logging, A10 Exceptional Conditions. | [§12 Security](01_TechGuide/12_Security_OWASP_Cloud.md) |
| 135 | SQL Injection prevention? | Use parameterized queries / prepared statements. Never concatenate user input. | Section 12.1 |
| 136 | JWT structure? | Header.Payload.Signature. Base64URL encoded. Stateless but can't revoke easily. | Section 12.2 |
| 137 | CORS? | Browser security policy. Server specifies allowed origins, methods, headers. | Section 12.3 |
| 138 | OAuth2 grant types? | Authorization Code (web), Client Credentials (service-to-service), PKCE (mobile/SPA). | Section 12.2 |

## AI & Modern Trends (2026 — CRITICAL)

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 139 | What is RAG? | Retrieve relevant docs → inject as context → LLM generates grounded answer. | [05_AI/03](05_AI/03_RAG_Architecture.md) |
| 140 | Vector databases? | Store embeddings for ANN search. pgvector, Pinecone, Weaviate, HNSW index. | [05_AI/04](05_AI/04_Vector_Databases_Embeddings.md) |
| 141 | Spring AI? | ChatClient, EmbeddingModel, VectorStore, Advisors, function calling, streaming. | [05_AI/05](05_AI/05_Spring_AI.md) |
| 142 | AI in System Design? | LLM gateway, RAG pipeline, semantic cache, fallbacks, cost/latency budgets. | [05_AI/10](05_AI/10_AI_System_Design.md) |
| 143 | What is MCP? | Model Context Protocol — standard for AI agents to discover and call tools/resources. | [05_AI/06](05_AI/06_AI_Agents_and_Workflows.md) |
| 144 | ReAct pattern? | Reason + Act loop — LLM thinks, calls tool, observes result, repeats. | [05_AI/06](05_AI/06_AI_Agents_and_Workflows.md) |
| 145 | Fine-tuning vs RAG? | Fine-tune: change model weights (expensive, static). RAG: retrieve fresh data at query time. | [05_AI/01](05_AI/01_AI_Fundamentals.md) |
| 146 | Chunking strategies? | Fixed size, sentence, paragraph, semantic — trade-off context vs retrieval precision. | [05_AI/03](05_AI/03_RAG_Architecture.md) |
| 147 | Cosine similarity vs Euclidean? | Cosine: direction (typical for embeddings). Euclidean: magnitude-sensitive distance. | [05_AI/04](05_AI/04_Vector_Databases_Embeddings.md) |
| 148 | Prompt injection? | Malicious input manipulates LLM behavior. Defense: input sanitization, output validation, privilege separation. | [05_AI/11](05_AI/11_AI_Ethics_Safety_Governance.md) |
| 149 | Hallucination prevention? | RAG with citations, temperature control, confidence thresholds, human-in-the-loop. | [05_AI/11](05_AI/11_AI_Ethics_Safety_Governance.md) |
| 150 | Token economics? | Cost = tokens × price. Optimize: shorter prompts, caching, smaller models for simple tasks. | [05_AI/02](05_AI/02_LLM_and_Prompt_Engineering.md) |
| 151 | LLM routing? | Route simple queries to cheap/fast models; complex to GPT-4/Claude. Cascade pattern. | [05_AI/09](05_AI/09_MLOps_AI_in_Production.md) |
| 152 | Semantic caching? | Cache LLM responses by embedding similarity — not exact string match. | [05_AI/09](05_AI/09_MLOps_AI_in_Production.md) |
| 153 | RAG evaluation? | RAGAS metrics: faithfulness, answer relevance, context precision/recall. | [05_AI/09](05_AI/09_MLOps_AI_in_Production.md) |
| 154 | Function calling? | LLM returns structured tool invocation JSON → app executes → feeds result back. | [05_AI/05](05_AI/05_Spring_AI.md) |
| 155 | Multi-agent systems? | Specialized agents (researcher, coder, reviewer) orchestrated by supervisor agent. | [05_AI/06](05_AI/06_AI_Agents_and_Workflows.md) |
| 156 | GraphQL vs REST for AI APIs? | REST for simple inference endpoints; GraphQL when clients need flexible aggregated AI features. | [§21 GraphQL](01_TechGuide/21_GraphQL_and_Alternative_APIs.md) |
| 157 | PII in prompts? | Redact before sending to LLM; use on-prem models for sensitive data; audit logs. | [05_AI/11](05_AI/11_AI_Ethics_Safety_Governance.md) |

## Leadership & Behavioral

| # | Question | Quick Answer | Deep Dive |
|---|----------|-------------|-----------|
| 158 | Tell me about a technical decision you led | [STAR: Technical Leadership story — ADR, design reviews, phased delivery] | Section 20 |
| 159 | How do you handle tech debt? | Track, communicate in business terms, 20% sprint capacity, prevent with standards. | Section 20.8 |
| 160 | How do you mentor engineers? | Pair programming, stretch assignments, specific feedback, psychological safety. | Section 20.4 |
| 161 | How do you handle production incidents? | Mitigate → Communicate → RCA → Blameless post-mortem → Action items. | Section 20.5 |
| 162 | How do you estimate projects? | Three-point (O+4M+P)/6 + buffer for unknowns + track accuracy over time. | Section 20.2 |
| 163 | How do you make architectural decisions? | Requirements → Options → Trade-off analysis → Team discussion → ADR. | Section 20.1 |
| 164 | How do you handle disagreements? | Objective criteria + data + team discussion. Disagree-and-commit if needed. | Section 20.9 |
| 165 | What's your code review approach? | Architecture alignment, design quality, production readiness, teaching moments. | Section 20.3 |

---

## DSA Quick Pattern Reference

| # | Pattern | Key Technique | When to Use | Section |
|---|---------|--------------|-------------|---------|
| 166 | Two Pointers | Left/Right pointers moving inward | Sorted arrays, palindromes, pair sums | 02_DSA |
| 167 | Sliding Window | Expand/shrink window over array | Subarrays, substrings, max/min in window | 03_CodingPatterns |
| 168 | Binary Search | Divide search space in half | Sorted data, optimization problems | 02_DSA |
| 169 | BFS | Level-by-level traversal | Shortest path (unweighted), level order | 02_DSA |
| 170 | DFS | Go deep first, then backtrack | Path finding, connected components, trees | 02_DSA |
| 171 | Dynamic Programming | Memoize overlapping subproblems | Optimization, counting, decision problems | 02_DSA |
| 172 | Backtracking | Try all options, undo bad choices | Permutations, combinations, constraint satisfaction | 02_DSA |
| 173 | Greedy | Locally optimal → globally optimal | Intervals, scheduling, Huffman coding | 02_DSA |
| 174 | Union Find | Disjoint set with path compression | Connected components, cycle detection | 02_DSA |
| 175 | Topological Sort | Order DAG nodes | Task scheduling, dependency resolution | 02_DSA |
| 176 | Trie | Prefix tree for strings | Autocomplete, spell check, word search | 02_DSA |
| 177 | Monotonic Stack | Stack maintaining increasing/decreasing order | Next greater element, histogram, stock span | 03_CodingPatterns |
| 178 | Heap / Priority Queue | Min/Max extraction in O(log n) | Top K, merge K sorted, scheduling | 02_DSA |
| 179 | Bit Manipulation | XOR, AND, OR, shifts | Single number, power of 2, counting bits | 02_DSA |
| 180 | Hash Map | O(1) lookup | Frequency count, two sum, anagrams | 02_DSA |

---

## System Design Problems — Quick Approach

| # | Problem | Key Components | Section |
|---|---------|---------------|---------|
| 181 | URL Shortener | Hash → Base62 → KV store → 301 redirect | 04_SystemDesign |
| 182 | Twitter/News Feed | Fan-out on write vs read, Timeline cache, Social graph | 04_SystemDesign |
| 183 | Chat System | WebSocket, Message queue, Delivery status, Online/offline | 04_SystemDesign |
| 184 | Rate Limiter | Token bucket, Redis, Sliding window log | 04_SystemDesign |
| 185 | Notification System | Channels (push/SMS/email), Priority queue, Template, DLQ | 04_SystemDesign |
| 186 | Search Autocomplete | Trie, Frequency ranking, Caching, Typeahead | [SearchAutocomplete](04_SystemDesign/02_HighLevelDesign/SearchAutocomplete/SearchAutocomplete.md) |
| 187 | Distributed Cache | Consistent hashing, Replication, Eviction policies | 04_SystemDesign |
| 188 | Payment System | Idempotency, Saga, Reconciliation, Audit trail | 04_SystemDesign |
| 189 | File Storage (Dropbox) | Chunking, Deduplication, Sync protocol, Metadata service | [FileStorage](04_SystemDesign/02_HighLevelDesign/FileStorage/FileStorage.md) |
| 190 | Video Streaming | CDN, Adaptive bitrate, Transcoding, Storage tiers | [VideoStreaming](04_SystemDesign/02_HighLevelDesign/VideoStreaming/VideoStreaming.md) |
| 191 | WhatsApp (E2EE) | Signal protocol, encrypted envelopes, multi-device, media | [WhatsApp](04_SystemDesign/02_HighLevelDesign/WhatsApp/WhatsApp.md) |
| 192 | Instagram | Media pipeline, CDN, Stories TTL, fan-out hybrid | [Instagram](04_SystemDesign/02_HighLevelDesign/Instagram/Instagram.md) |
| 193 | Ticketmaster | Atomic seat holds, Redis TTL, flash sale queue, idempotent payment | [Ticketmaster](04_SystemDesign/02_HighLevelDesign/Ticketmaster/Ticketmaster.md) |

---

## Rapid Fire — True or False

| # | Statement | Answer |
|---|-----------|--------|
| 194 | HashMap allows null keys | ✅ True (one null key, multiple null values) |
| 195 | ConcurrentHashMap allows null keys | ❌ False (throws NPE) |
| 196 | String is thread-safe | ✅ True (immutable) |
| 197 | StringBuilder is thread-safe | ❌ False (use StringBuffer for thread safety) |
| 198 | ArrayList is synchronized | ❌ False (use Collections.synchronizedList or CopyOnWriteArrayList) |
| 199 | @Transactional works on private methods | ❌ False (proxy can't intercept private methods) |
| 200 | Spring beans are singleton by default | ✅ True |
| 201 | HTTP/2 requires TLS | ❌ False (spec allows h2c, but browsers require TLS) |
| 202 | Kafka guarantees global ordering | ❌ False (only per-partition ordering) |
| 203 | REST is a protocol | ❌ False (it's an architectural style) |
| 204 | GraphQL always uses POST | ❌ False (GET for queries is valid, though POST is common) |
| 205 | Microservices should share databases | ❌ False (database per service is the pattern) |
| 206 | ZGC has sub-millisecond pauses | ✅ True (as of Java 15+) |
| 207 | Records can be extended | ❌ False (records are implicitly final) |
| 208 | Sealed classes work with pattern matching | ✅ True (exhaustive switch) |
| 209 | Virtual threads should use synchronized | ❌ False (synchronized can pin virtual thread to carrier — use ReentrantLock) |
| 210 | LinkedHashMap can be used as LRU cache | ✅ True (access-order mode + removeEldestEntry) |
| 211 | @Async requires @EnableAsync | ✅ True |
| 212 | StampedLock is reentrant | ❌ False (unlike ReentrantLock, StampedLock is NOT reentrant) |
| 213 | Kafka consumers pull messages | ✅ True (pull model, unlike RabbitMQ's push) |
| 214 | CDN caches dynamic content | ❌ False (primarily static; edge compute can handle some dynamic) |
| 215 | DNS uses TCP | ✅ True (for large responses >512 bytes, zone transfers; UDP for normal queries) |
| 216 | gRPC uses HTTP/2 | ✅ True |
| 217 | Event Sourcing requires CQRS | ❌ False (they pair well but are independent patterns) |
| 218 | OWASP 2025 still lists SSRF as standalone A10 | ❌ False (SSRF merged into A01 Broken Access Control) |
| 219 | A Lead engineer's main job is coding | ❌ False (architecture, mentoring, decisions, communication are equally important) |

