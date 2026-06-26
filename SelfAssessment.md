# Self-Assessment — Interview Readiness Checklist

> **How to use**: Go through each section and honestly rate yourself. Mark items as you master them.
> Focus your remaining study time on sections with the most unchecked items.
>
> **Rating Scale**: For each topic, give yourself a confidence score:
> - **1** = Never heard of it / Cannot explain
> - **2** = Know the concept but cannot explain in detail or code it
> - **3** = Can explain well and provide examples, but not production-level
> - **4** = Can explain deeply, code it, and discuss trade-offs
> - **5** = Can teach this topic, handle follow-up questions, and relate it to real-world systems

---

## Section 0: Fundamentals (Start Here — Associate)

**Guide Reference**: [00_Java_OOP_Fundamentals.md](01_TechGuide/00_Java_OOP_Fundamentals.md), [00_Computer_Science_Fundamentals.md](01_TechGuide/00_Computer_Science_Fundamentals.md), [00_Web_Fundamentals.md](01_TechGuide/00_Web_Fundamentals.md), [35_SQL_Fundamentals.md](01_TechGuide/35_SQL_Fundamentals.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 0.1 | OOP four pillars with Java examples | ☐ | ___ |
| 0.2 | equals(), hashCode(), and HashMap contract | ☐ | ___ |
| 0.3 | Big-O: O(1), O(n), O(n log n), O(n²) | ☐ | ___ |
| 0.4 | Array vs LinkedList trade-offs | ☐ | ___ |
| 0.5 | HTTP methods and idempotency (GET, POST, PUT, DELETE) | ☐ | ___ |
| 0.6 | HTTP status codes: 200, 401, 403, 404, 429, 500 | ☐ | ___ |
| 0.7 | Cookie vs JWT authentication basics | ☐ | ___ |
| 0.8 | SQL JOINs (INNER, LEFT) and basic WHERE | ☐ | ___ |
| 0.9 | Recursion: base case and recursive case | ☐ | ___ |
| 0.10 | BFS vs DFS — when to use each | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 1: Java Internals & Memory Model

**Guide Reference**: [02_Advanced_SpringBoot_Java_Internals.md](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 1.1 | Difference between Heap and Stack memory | ☐ | ___ |
| 1.2 | What is Escape Analysis and how does JVM use it | ☐ | ___ |
| 1.3 | Generational GC: Young Gen (Eden, Survivor) vs Old Gen | ☐ | ___ |
| 1.4 | G1GC: Region-based collection, pause time target, concurrent marking | ☐ | ___ |
| 1.5 | ZGC: Colored pointers, sub-millisecond pauses, when to use | ☐ | ___ |
| 1.6 | Shenandoah: Concurrent compaction, comparison with ZGC | ☐ | ___ |
| 1.7 | Memory leak sources: ThreadLocal, ClassLoader, static collections | ☐ | ___ |
| 1.8 | How to take and analyze a Heap Dump (jmap, jcmd, Eclipse MAT) | ☐ | ___ |
| 1.9 | GC Roots, Dominator Tree, Shallow Heap vs Retained Heap | ☐ | ___ |
| 1.10 | How to diagnose OOM in production (steps) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 2: Java Concurrency

**Guide Reference**: [02_Advanced_SpringBoot_Java_Internals.md](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Section 2.2)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 2.1 | CompletableFuture: thenApply, thenCompose, allOf, anyOf | ☐ | ___ |
| 2.2 | CompletableFuture: exceptionally, handle, orTimeout, completeOnTimeout | ☐ | ___ |
| 2.3 | ThreadPoolExecutor: 7 parameters (core, max, keepAlive, queue, factory, handler) | ☐ | ___ |
| 2.4 | Rejection policies: CallerRunsPolicy, AbortPolicy, DiscardPolicy, DiscardOldestPolicy | ☐ | ___ |
| 2.5 | Race conditions: Visibility problems vs Atomicity problems | ☐ | ___ |
| 2.6 | volatile keyword: what it guarantees and what it does NOT guarantee | ☐ | ___ |
| 2.7 | AtomicInteger and CAS (Compare-And-Swap) | ☐ | ___ |
| 2.8 | ConcurrentHashMap: lock striping, internal structure | ☐ | ___ |
| 2.9 | Happens-Before relationship: definition and examples | ☐ | ___ |
| 2.10 | ForkJoinPool and work-stealing algorithm | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 3: Spring Boot Internals

**Guide Reference**: [02_Advanced_SpringBoot_Java_Internals.md](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Section 2.3, 2.4)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 3.1 | @SpringBootApplication: what 3 annotations it combines | ☐ | ___ |
| 3.2 | SpringApplication.run() lifecycle (12 steps) | ☐ | ___ |
| 3.3 | AutoConfiguration: how Spring Boot discovers and applies auto-config classes | ☐ | ___ |
| 3.4 | @ConditionalOnClass, @ConditionalOnMissingBean, @ConditionalOnProperty | ☐ | ___ |
| 3.5 | Full Bean Lifecycle: Instantiation → DI → Aware → BeanPostProcessor → @PostConstruct → InitializingBean | ☐ | ___ |
| 3.6 | Why @Transactional fails on self-invocation (proxy-based AOP) | ☐ | ___ |
| 3.7 | Bean Scopes: singleton, prototype, request, session, application | ☐ | ___ |
| 3.8 | OAuth2 flows: Authorization Code + PKCE, Client Credentials | ☐ | ___ |
| 3.9 | JWT structure: Header.Payload.Signature, HS256 vs RS256 | ☐ | ___ |
| 3.10 | Refresh token rotation, token blacklisting strategies | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 4: Microservices Resilience Patterns

**Guide Reference**: [06_Microservices_Distributed_Systems.md](01_TechGuide/06_Microservices_Distributed_Systems.md) (Section 6.1)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 4.1 | Circuit Breaker state machine: CLOSED → OPEN → HALF_OPEN | ☐ | ___ |
| 4.2 | Circuit Breaker parameters: failureRateThreshold, waitDuration, permittedCallsInHalfOpen | ☐ | ___ |
| 4.3 | Bulkhead: Semaphore-based vs Thread-pool-based, when to use each | ☐ | ___ |
| 4.4 | Retry: Exponential backoff with jitter, retryExceptions vs ignoreExceptions | ☐ | ___ |
| 4.5 | Rate Limiting: Token Bucket vs Sliding Window algorithms | ☐ | ___ |
| 4.6 | How these patterns compose: Circuit Breaker wrapping Retry wrapping Bulkhead | ☐ | ___ |
| 4.7 | Resilience4j configuration and integration with Spring Boot | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 5: Data Consistency Patterns

**Guide Reference**: [06_Microservices_Distributed_Systems.md](01_TechGuide/06_Microservices_Distributed_Systems.md) (Section 6.2)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 5.1 | SAGA Pattern: Choreography vs Orchestration, trade-offs | ☐ | ___ |
| 5.2 | Compensating transactions: how to undo distributed operations | ☐ | ___ |
| 5.3 | SAGA with Kafka: event-driven choreography implementation | ☐ | ___ |
| 5.4 | Outbox Pattern: solving the dual-write problem | ☐ | ___ |
| 5.5 | CDC (Change Data Capture) with Debezium: how it reads the WAL | ☐ | ___ |
| 5.6 | Two-Phase Commit (2PC): why it exists and why we avoid it | ☐ | ___ |
| 5.7 | Eventual Consistency: idempotency keys, read-your-writes, conflict resolution | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 6: Communication (REST, gRPC, Kafka)

**Guide Reference**: [06_Microservices_Distributed_Systems.md](01_TechGuide/06_Microservices_Distributed_Systems.md) (Section 6.3)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 6.1 | REST vs gRPC: performance, protocol, serialization differences | ☐ | ___ |
| 6.2 | gRPC: Protocol Buffers, HTTP/2 multiplexing, streaming types | ☐ | ___ |
| 6.3 | Kafka: Topics, Partitions, Consumer Groups, Offsets | ☐ | ___ |
| 6.4 | Kafka: at-least-once, at-most-once, exactly-once delivery guarantees | ☐ | ___ |
| 6.5 | Kafka: Idempotent producers, transactional outbox | ☐ | ___ |
| 6.6 | Kafka vs RabbitMQ: architecture and use-case differences | ☐ | ___ |
| 6.7 | Dead Letter Queue (DLQ): purpose and implementation | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 7: Database & Performance Tuning

**Guide Reference**: [05_Database_Performance_Tuning.md](01_TechGuide/05_Database_Performance_Tuning.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 7.1 | B-Tree index structure and how it enables fast lookups | ☐ | ___ |
| 7.2 | Composite index and the Leftmost Prefix Rule | ☐ | ___ |
| 7.3 | Covering Index (Index-Only Scan) | ☐ | ___ |
| 7.4 | Reading EXPLAIN ANALYZE output: Seq Scan, Index Scan, Hash Join, Sort | ☐ | ___ |
| 7.5 | N+1 Problem: detection and 4 fixes (JOIN FETCH, @EntityGraph, @BatchSize, DTO) | ☐ | ___ |
| 7.6 | Table Partitioning: Range, List, Hash, partition pruning | ☐ | ___ |
| 7.7 | Database Sharding: shard key selection, routing, cross-shard queries | ☐ | ___ |
| 7.8 | Read Replicas: async replication, replication lag, read-after-write consistency | ☐ | ___ |
| 7.9 | Caching patterns: Cache-Aside, Write-Through, Write-Behind, Read-Through | ☐ | ___ |
| 7.10 | Cache Stampede: what it is and how to prevent it (locking, probabilistic early expiration) | ☐ | ___ |
| 7.11 | Redis vs Hazelcast: architecture, data structures, Near-Cache | ☐ | ___ |
| 7.12 | Eviction Policies: LRU, LFU, TTL, Random | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 8: System Design Concepts

**Guide Reference**: [07_System_Design.md](01_TechGuide/07_System_Design.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 8.1 | Vertical vs Horizontal Scaling: trade-offs, statelessness | ☐ | ___ |
| 8.2 | CAP Theorem: Consistency, Availability, Partition Tolerance | ☐ | ___ |
| 8.3 | CP vs AP systems: real-world examples (ZooKeeper CP, Cassandra AP) | ☐ | ___ |
| 8.4 | PACELC extension of CAP theorem | ☐ | ___ |
| 8.5 | Load Balancers: L4 vs L7, Round Robin, Least Connections, Consistent Hashing | ☐ | ___ |
| 8.6 | API Gateway: responsibilities, Spring Cloud Gateway, anti-patterns | ☐ | ___ |
| 8.7 | BFF (Backend for Frontend): when and why | ☐ | ___ |
| 8.8 | Strangler Fig Pattern: incremental migration, Anti-Corruption Layer | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 9: Modern Trends (AI, Kubernetes)

**Guide Reference**: [13_Modern_Trends_2026.md](01_TechGuide/13_Modern_Trends_2026.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 9.1 | RAG architecture: Ingestion (Chunking, Embedding, Vector DB) | ☐ | ___ |
| 9.2 | RAG architecture: Retrieval + Generation (Query Embedding, Similarity Search, LLM) | ☐ | ___ |
| 9.3 | Vector Embeddings and Cosine Similarity | ☐ | ___ |
| 9.4 | Spring AI: configuration, DocumentIngestionService, RagChatService | ☐ | ___ |
| 9.5 | pgvector: PostgreSQL extension, HNSW index | ☐ | ___ |
| 9.6 | Kubernetes: Pods, Deployments, Services, Ingress, ConfigMaps | ☐ | ___ |
| 9.7 | Health Probes: Startup, Liveness, Readiness (and Spring Boot Actuator integration) | ☐ | ___ |
| 9.8 | HPA: Horizontal Pod Autoscaler (CPU, memory, custom metrics) | ☐ | ___ |
| 9.9 | Graceful Shutdown: SIGTERM, PreStop hook, terminationGracePeriodSeconds | ☐ | ___ |
| 9.10 | Rolling Updates: maxSurge, maxUnavailable, rollback | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 10: Angular & Frontend Engineering

**Guide Reference**: [08_Angular_Frontend_Engineering.md](01_TechGuide/08_Angular_Frontend_Engineering.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 10.1 | Standalone Components: standalone: true, imports in component | ☐ | ___ |
| 10.2 | Signals: signal(), computed(), effect(), input(), output() | ☐ | ___ |
| 10.3 | Signals vs RxJS: when to use each, toSignal(), toObservable() | ☐ | ___ |
| 10.4 | Change Detection: Default vs OnPush, Zoneless Angular | ☐ | ___ |
| 10.5 | New Control Flow: @if, @for, @switch (Angular 17+) | ☐ | ___ |
| 10.6 | Deferred Views: @defer with on viewport, on interaction, on timer | ☐ | ___ |
| 10.7 | State Management: Service-based with Signals vs NgRx Signal Store | ☐ | ___ |
| 10.8 | RxJS: switchMap, mergeMap, concatMap, exhaustMap — explain the difference | ☐ | ___ |
| 10.9 | RxJS: combineLatest, forkJoin, withLatestFrom — when to use each | ☐ | ___ |
| 10.10 | Memory Leak Prevention: takeUntilDestroyed(), async pipe, toSignal() | ☐ | ___ |
| 10.11 | Lazy Loading: loadComponent, loadChildren in routes | ☐ | ___ |
| 10.12 | Functional Guards: canActivate, canDeactivate, canMatch | ☐ | ___ |
| 10.13 | Functional Interceptors: authInterceptor, errorHandlerInterceptor | ☐ | ___ |
| 10.14 | Performance: Virtual Scrolling, OnPush, trackBy | ☐ | ___ |
| 10.15 | Typed Reactive Forms: FormGroup<T>, NonNullableFormBuilder | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 11: Modern Java Features (Java 17-21)

**Guide Reference**: [01_Modern_Java_Features.md](01_TechGuide/01_Modern_Java_Features.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 11.1 | Records: auto-generated methods, compact constructors, limitations | ☐ | ___ |
| 11.2 | Sealed Classes: restrict inheritance, exhaustive switch expressions | ☐ | ___ |
| 11.3 | Pattern Matching: instanceof with pattern variables, switch patterns | ☐ | ___ |
| 11.4 | Record Patterns (deconstruction) and guarded patterns | ☐ | ___ |
| 11.5 | Virtual Threads: how they work, mounting/unmounting, scalability | ☐ | ___ |
| 11.6 | Virtual Thread Pinning: synchronized blocks, ReentrantLock alternative | ☐ | ___ |
| 11.7 | Spring Boot 3.2+ virtual thread integration | ☐ | ___ |
| 11.8 | Structured Concurrency: StructuredTaskScope.ShutdownOnFailure | ☐ | ___ |
| 11.9 | Text Blocks, Switch Expressions, Helpful NPE | ☐ | ___ |
| 11.10 | Sequenced Collections, Unnamed Variables, Stream.toList() | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 12: Testing Strategies

**Guide Reference**: [09_Testing_Strategies.md](01_TechGuide/09_Testing_Strategies.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 12.1 | Testing Pyramid: Unit (60-70%), Integration (20-30%), E2E (5-10%) | ☐ | ___ |
| 12.2 | Unit testing with Mockito: when, verify, argThat, never, spy | ☐ | ___ |
| 12.3 | @ParameterizedTest and @CsvSource | ☐ | ___ |
| 12.4 | Testcontainers: real database testing with Docker | ☐ | ___ |
| 12.5 | WireMock: mocking external HTTP services | ☐ | ___ |
| 12.6 | Contract Testing: Spring Cloud Contract (producer + consumer) | ☐ | ___ |
| 12.7 | Testing best practices: behavior over implementation, naming conventions | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 13: DevOps, CI/CD, Docker

**Guide Reference**: [10_DevOps_CICD_Docker.md](01_TechGuide/10_DevOps_CICD_Docker.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 13.1 | Multi-stage Dockerfile for Spring Boot (layertools, distroless, non-root) | ☐ | ___ |
| 13.2 | Multi-stage Dockerfile for Angular (nginx, SPA routing, security headers) | ☐ | ___ |
| 13.3 | CI/CD Pipeline stages: Build → Test → Analyze → Container → Deploy | ☐ | ___ |
| 13.4 | Deployment strategies: Blue-Green, Canary, Rolling Update | ☐ | ___ |
| 13.5 | Trunk-Based Development vs GitFlow | ☐ | ___ |
| 13.6 | GitOps with ArgoCD: pull-based CD, reconciliation loop, drift detection | ☐ | ___ |
| 13.7 | Feature Flags: decoupling deployment from release | ☐ | ___ |
| 13.8 | Terraform: providers, resources, state, modules (AWS EKS, RDS, ElastiCache) | ☐ | ___ |
| 13.9 | DevSecOps: SAST, DAST, SCA, Secrets Management (Vault) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 14: Observability

**Guide Reference**: [11_Observability.md](01_TechGuide/11_Observability.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 14.1 | Three Pillars: Logs, Metrics, Traces — how they relate | ☐ | ___ |
| 14.2 | Structured Logging: JSON format, MDC, correlation IDs | ☐ | ___ |
| 14.3 | Prometheus metric types: Counter, Gauge, Histogram, Summary | ☐ | ___ |
| 14.4 | RED Method: Rate, Errors, Duration | ☐ | ___ |
| 14.5 | USE Method: Utilization, Saturation, Errors | ☐ | ___ |
| 14.6 | SLI / SLO / SLA: definitions and relationship | ☐ | ___ |
| 14.7 | Distributed Tracing: Trace, Spans, Context Propagation | ☐ | ___ |
| 14.8 | OpenTelemetry: auto-instrumentation, @WithSpan, OTel Collector | ☐ | ___ |
| 14.9 | Sampling strategies: head-based, tail-based, parent-based | ☐ | ___ |
| 14.10 | Alerting best practices: symptom-based, error budgets, runbooks | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 15: API Design & REST

**Guide Reference**: [04_API_Design_REST.md](01_TechGuide/04_API_Design_REST.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 15.1 | Resource-oriented design: nouns over verbs, plural resources | ☐ | ___ |
| 15.2 | HTTP methods and idempotency (GET, POST, PUT, PATCH, DELETE) | ☐ | ___ |
| 15.3 | Idempotency Key implementation for safe retries | ☐ | ___ |
| 15.4 | RFC 7807 Problem Details: standard error response format | ☐ | ___ |
| 15.5 | Cursor-based vs Offset-based pagination: trade-offs and implementation | ☐ | ___ |
| 15.6 | API Versioning: URL path vs header vs query parameter | ☐ | ___ |
| 15.7 | HATEOAS: self-describing APIs with hypermedia links | ☐ | ___ |
| 15.8 | OpenAPI/Swagger: annotations, springdoc-openapi | ☐ | ___ |
| 15.9 | API Security: OAuth2, CORS, rate limiting, input validation, audit logging | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 16: Design Patterns, SOLID & Clean Architecture

**Guide Reference**: [03_Design_Patterns_SOLID_CleanArch.md](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 16.1 | SRP: Single Responsibility Principle with Spring example | ☐ | ___ |
| 16.2 | OCP: Open/Closed Principle with Strategy pattern example | ☐ | ___ |
| 16.3 | LSP: Liskov Substitution Principle — the classic Rectangle/Square violation | ☐ | ___ |
| 16.4 | ISP: Interface Segregation — breaking fat interfaces | ☐ | ___ |
| 16.5 | DIP: Dependency Inversion — depending on abstractions | ☐ | ___ |
| 16.6 | GoF Patterns: Factory, Builder, Decorator, Adapter, Strategy, Observer, Template Method | ☐ | ___ |
| 16.7 | CQRS: Command Query Responsibility Segregation | ☐ | ___ |
| 16.8 | Event Sourcing: storing events instead of current state | ☐ | ___ |
| 16.9 | Clean Architecture: layers, Dependency Rule, package structure | ☐ | ___ |
| 16.10 | DDD: Bounded Contexts, Aggregates, Value Objects, Ubiquitous Language | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 17: Leadership & Behavioral

**Guide Reference**: [14_Leadership_Behavioral_SystemDesign.md](01_TechGuide/14_Leadership_Behavioral_SystemDesign.md)

| # | Topic | Are You Prepared? | Confidence (1-5) |
|---|-------|-------------------|-------------------|
| 17.1 | STAR Method: Can you structure any story in Situation-Task-Action-Result | ☐ | ___ |
| 17.2 | Prepared story: A difficult technical decision you made | ☐ | ___ |
| 17.3 | Prepared story: Pushing back on a stakeholder | ☐ | ___ |
| 17.4 | Prepared story: Mentoring a junior engineer | ☐ | ___ |
| 17.5 | Prepared story: Resolving a team conflict | ☐ | ___ |
| 17.6 | Prepared story: Delivering under a tight deadline | ☐ | ___ |
| 17.7 | Prepared story: A project that failed and what you learned | ☐ | ___ |
| 17.8 | Prepared story: Introducing a new technology or process | ☐ | ___ |
| 17.9 | Leadership Principles: Ownership, Disagree and Commit, Earn Trust | ☐ | ___ |
| 17.10 | System Design Interview Framework: 4 steps in 45 minutes | ☐ | ___ |
| 17.11 | Back-of-the-envelope estimation: QPS, storage, bandwidth, servers | ☐ | ___ |
| 17.12 | Architecture Decision Records (ADRs): template and usage | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 18: System Design Practice

**Guide Reference**: [04_SystemDesign/](04_SystemDesign/)

| # | Problem | Can You Design It End-to-End in 45 min? | Confidence (1-5) |
|---|---------|----------------------------------------|-------------------|
| 18.1 | URL Shortener (HLD) | ☐ | ___ |
| 18.2 | Rate Limiter (HLD) | ☐ | ___ |
| 18.3 | Chat System (HLD) | ☐ | ___ |
| 18.4 | Notification System (HLD) | ☐ | ___ |
| 18.5 | News Feed (HLD) | ☐ | ___ |
| 18.6 | Distributed Cache (HLD) | ☐ | ___ |
| 18.7 | Twitter / Social Feed (HLD) | ☐ | ___ |
| 18.8 | Uber / Ride Sharing (HLD) | ☐ | ___ |
| 18.9 | Payment System (HLD) | ☐ | ___ |
| 18.10 | Parking Lot (LLD) | ☐ | ___ |
| 18.11 | BookMyShow / Ticket Booking (LLD) | ☐ | ___ |
| 18.12 | Elevator System (LLD) | ☐ | ___ |
| 18.13 | Vending Machine (LLD) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 19: AI, LLM & RAG

**Guide Reference**: [05_AI/](05_AI/)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 19.1 | Transformer architecture: self-attention, positional encoding, why it replaced RNNs | ☐ | ___ |
| 19.2 | Tokens, context windows, temperature, top-p, top-k — what each parameter controls | ☐ | ___ |
| 19.3 | Embeddings: what they are, cosine similarity, when to use which embedding model | ☐ | ___ |
| 19.4 | LLM providers: OpenAI, Anthropic, Google, Meta — trade-offs for enterprise use | ☐ | ___ |
| 19.5 | Prompt engineering: zero-shot, few-shot, chain-of-thought, ReAct, structured output | ☐ | ___ |
| 19.6 | Prompt injection: what it is, defense strategies (input validation, system prompt, guardrails) | ☐ | ___ |
| 19.7 | RAG architecture: document loading, chunking strategies, embedding, retrieval, generation | ☐ | ___ |
| 19.8 | RAG vs fine-tuning vs long context: decision flow and trade-offs | ☐ | ___ |
| 19.9 | Vector databases: pgvector, Pinecone, Weaviate — selection criteria, HNSW vs IVFFlat | ☐ | ___ |
| 19.10 | Hybrid search: combining keyword (BM25) + semantic search with RRF | ☐ | ___ |
| 19.11 | Query transformation: rewrite, HyDE, multi-query, step-back techniques | ☐ | ___ |
| 19.12 | Spring AI: ChatClient, function calling, Advisors, streaming, structured output | ☐ | ___ |
| 19.13 | AI Agents: ReAct pattern, function calling, MCP (Model Context Protocol) | ☐ | ___ |
| 19.14 | AI dev tools: GitHub Copilot, Cursor, CodeRabbit — effective usage patterns | ☐ | ___ |
| 19.15 | AI in frontend: streaming chat UI, SSE/EventSource, markdown rendering in Angular | ☐ | ___ |
| 19.16 | Hallucination causes and mitigation strategies | ☐ | ___ |
| 19.17 | AI ethics: bias detection, responsible AI, content filtering, data privacy | ☐ | ___ |
| 19.18 | RAG evaluation: RAGAS framework (faithfulness, relevance, context recall) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 20: Application Security & OWASP

**Guide Reference**: [01_TechGuide/12_Security_OWASP_Cloud.md](01_TechGuide/12_Security_OWASP_Cloud.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 20.1 | OWASP Top 10 **2025**: Can you name and explain all 10 risks | ☐ | ___ |
| 20.2 | Broken Access Control: IDOR, horizontal/vertical privilege escalation, prevention | ☐ | ___ |
| 20.3 | Injection: SQL injection, XSS — parameterized queries, input validation, CSP | ☐ | ___ |
| 20.4 | JWT Security: algorithm confusion, token storage, RS256 vs HS256, refresh rotation | ☐ | ___ |
| 20.5 | CORS: Same-Origin Policy, preflight requests, Spring Boot configuration | ☐ | ___ |
| 20.6 | Threat Modeling: STRIDE framework, security user stories | ☐ | ___ |
| 20.7 | Dependency scanning: OWASP Dependency Check, Snyk, Dependabot | ☐ | ___ |
| 20.8 | Security headers: HSTS, CSP, X-Frame-Options, Referrer-Policy | ☐ | ___ |
| 20.9 | Secrets management: Vault, AWS Secrets Manager, never in code | ☐ | ___ |
| 20.10 | Security logging: what to log, what NOT to log, PII masking | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 21: DSA

**Guide Reference**: [02_DSA/](02_DSA/)

| # | Topic Area | Can You Solve Medium Problems? | Confidence (1-5) |
|---|-----------|-------------------------------|-------------------|
| 21.1 | Arrays & Hash Maps (Two Sum, Sliding Window, Prefix Sum) | ☐ | ___ |
| 21.2 | Strings (Anagrams, Palindromes, Substrings) | ☐ | ___ |
| 21.3 | Linked Lists (Reverse, Merge, Cycle Detection, LRU Cache) | ☐ | ___ |
| 21.4 | Stacks & Queues (Valid Parentheses, Monotonic Stack) | ☐ | ___ |
| 21.5 | Trees (Traversals, LCA, Validate BST, Serialize/Deserialize) | ☐ | ___ |
| 21.6 | Graphs (BFS, DFS, Topological Sort, Dijkstra, Islands) | ☐ | ___ |
| 21.7 | Dynamic Programming (Knapsack, LCS, LIS, Coin Change, Grid DP) | ☐ | ___ |
| 21.8 | Backtracking (Permutations, Subsets, N-Queens, Sudoku Solver) | ☐ | ___ |
| 21.9 | Binary Search (Search Rotated, First/Last Position, Koko Bananas) | ☐ | ___ |
| 21.10 | Greedy Algorithms (Gas Station, Jump Game, Task Scheduler) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 22: Java Collections & Concurrency Deep Dive

**Guide Reference**: [15_Java_Collections_Concurrency_DeepDive.md](01_TechGuide/15_Java_Collections_Concurrency_DeepDive.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 22.1 | HashMap internals: hashing, treeification at 8, resizing, load factor 0.75 | ☐ | ___ |
| 22.2 | LinkedHashMap: insertion vs access order, building LRU cache | ☐ | ___ |
| 22.3 | TreeMap: Red-Black tree, NavigableMap operations (floor, ceiling, subMap) | ☐ | ___ |
| 22.4 | Fail-fast vs fail-safe iterators, ConcurrentModificationException | ☐ | ___ |
| 22.5 | Java Stream API: Collectors (groupingBy, partitioningBy, joining), flatMap, custom Collector | ☐ | ___ |
| 22.6 | Parallel streams: when they help, when they hurt, custom ForkJoinPool | ☐ | ___ |
| 22.7 | Optional best practices: map/flatMap, never as method parameter | ☐ | ___ |
| 22.8 | ReentrantLock vs synchronized: fairness, tryLock, interruptible | ☐ | ___ |
| 22.9 | ReadWriteLock, StampedLock (optimistic read), when to use each | ☐ | ___ |
| 22.10 | CountDownLatch, CyclicBarrier, Semaphore, Phaser — comparison | ☐ | ___ |
| 22.11 | ConcurrentHashMap internals: Java 8 CAS + synchronized on bucket head | ☐ | ___ |
| 22.12 | AtomicInteger, AtomicReference, LongAdder vs AtomicLong | ☐ | ___ |
| 22.13 | CompletableFuture patterns: fan-out/in, first-wins, retry | ☐ | ___ |
| 22.14 | ThreadPoolExecutor 7 parameters, sizing (CPU-bound vs I/O-bound) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 23: Spring Ecosystem Deep Dive

**Guide Reference**: [16_Spring_Ecosystem_DeepDive.md](01_TechGuide/16_Spring_Ecosystem_DeepDive.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 23.1 | Spring Data JPA: query method resolution, @Query, JOIN FETCH | ☐ | ___ |
| 23.2 | Specifications: dynamic composable queries for search/filter | ☐ | ___ |
| 23.3 | JPA Projections: interface-based, class-based, dynamic | ☐ | ___ |
| 23.4 | JPA Auditing: @CreatedDate, @LastModifiedDate, AuditorAware | ☐ | ___ |
| 23.5 | JPA batch operations: batch_size config, JDBC batch insert | ☐ | ___ |
| 23.6 | WebFlux: Mono, Flux, reactive controller, WebClient | ☐ | ___ |
| 23.7 | Spring Cloud: Eureka, Gateway, Config Server, @RefreshScope | ☐ | ___ |
| 23.8 | Spring Batch: chunk processing, ItemReader/Processor/Writer, skip/retry | ☐ | ___ |
| 23.9 | Spring AOP: CGLIB proxy, self-invocation problem, custom aspects | ☐ | ___ |
| 23.10 | Spring Events: @EventListener, @TransactionalEventListener (AFTER_COMMIT) | ☐ | ___ |
| 23.11 | Spring Actuator: custom health indicators, business metrics, Prometheus | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 24: Networking & Protocols

**Guide Reference**: [17_Networking_Protocols.md](01_TechGuide/17_Networking_Protocols.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 24.1 | TCP vs UDP: reliability, ordering, connection-oriented vs connectionless | ☐ | ___ |
| 24.2 | TCP three-way handshake, four-way termination, TIME_WAIT | ☐ | ___ |
| 24.3 | HTTP/1.1 vs HTTP/2: multiplexing, binary framing, HPACK | ☐ | ___ |
| 24.4 | HTTP/3 (QUIC): no TCP HOL blocking, 0-RTT, connection migration | ☐ | ___ |
| 24.5 | TLS 1.3: 1-RTT handshake, forward secrecy, AEAD | ☐ | ___ |
| 24.6 | mTLS: mutual authentication, service mesh integration | ☐ | ___ |
| 24.7 | DNS resolution flow, record types (A, CNAME, MX, SRV), GeoDNS | ☐ | ___ |
| 24.8 | WebSocket: full-duplex, upgrade handshake, Spring STOMP | ☐ | ___ |
| 24.9 | gRPC: 4 streaming patterns, Protocol Buffers, vs REST decision | ☐ | ___ |
| 24.10 | CDN: edge caching, Cache-Control, ETag, invalidation | ☐ | ___ |
| 24.11 | Connection pooling: HikariCP sizing formula, HTTP client pools | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 25: Performance Engineering & JVM Tuning

**Guide Reference**: [18_Performance_Engineering_JVM.md](01_TechGuide/18_Performance_Engineering_JVM.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 25.1 | JVM memory layout: Heap (Young+Old), Metaspace, Code Cache, Thread Stacks | ☐ | ___ |
| 25.2 | Essential JVM flags: -Xms, -Xmx, GC selection, HeapDumpOnOOM | ☐ | ___ |
| 25.3 | G1GC vs ZGC vs Shenandoah: decision matrix, when to use each | ☐ | ___ |
| 25.4 | Java Flight Recorder (JFR): production-safe profiling, what it captures | ☐ | ___ |
| 25.5 | Flame graphs: how to read them, async-profiler for CPU/alloc/lock | ☐ | ___ |
| 25.6 | Thread dump analysis: BLOCKED, WAITING, deadlock detection | ☐ | ___ |
| 25.7 | Heap dump analysis: Eclipse MAT, Dominator Tree, retained heap | ☐ | ___ |
| 25.8 | JMH microbenchmarking: warmup, fork, Blackhole, avoiding common traps | ☐ | ___ |
| 25.9 | Performance anti-patterns: N+1, chatty calls, sync I/O in event loops | ☐ | ___ |
| 25.10 | SLI/SLO/SLA/Error Budgets: definitions, examples, engineering culture | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 26: Event-Driven Architecture

**Guide Reference**: [19_Event_Driven_Architecture.md](01_TechGuide/19_Event_Driven_Architecture.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 26.1 | Event Notification vs Event-Carried State Transfer vs Event Sourcing | ☐ | ___ |
| 26.2 | Message vs Event vs Command: semantics and naming conventions | ☐ | ___ |
| 26.3 | Kafka architecture: topics, partitions, consumer groups, offsets | ☐ | ___ |
| 26.4 | Kafka delivery guarantees: at-most-once, at-least-once, exactly-once | ☐ | ___ |
| 26.5 | Spring Boot Kafka: producer config (acks, idempotence, batching) | ☐ | ___ |
| 26.6 | Spring Boot Kafka: consumer config (manual commit, error handling, DLT) | ☐ | ___ |
| 26.7 | Schema evolution: Avro, Schema Registry, backward/forward compatibility | ☐ | ___ |
| 26.8 | Event Sourcing + CQRS: Aggregate, event handler, projection | ☐ | ___ |
| 26.9 | Dead Letter Topic: retry strategy, poison pill handling | ☐ | ___ |
| 26.10 | Saga Pattern: choreography vs orchestration, compensating transactions | ☐ | ___ |
| 26.11 | Kafka vs RabbitMQ: architecture, throughput, routing, use cases | ☐ | ___ |
| 26.12 | CDC with Debezium: transaction log, event format, use cases | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 27: Technical Leadership & Architecture

**Guide Reference**: [20_Technical_Leadership_Architecture.md](01_TechGuide/20_Technical_Leadership_Architecture.md)

| # | Topic | Are You Prepared? | Confidence (1-5) |
|---|-------|-------------------|-------------------|
| 27.1 | ADR template: Status, Context, Decision, Consequences, Alternatives | ☐ | ___ |
| 27.2 | Technical estimation: T-shirt sizing, story points, three-point estimation | ☐ | ___ |
| 27.3 | Technical Design Document: requirements, architecture, rollout plan | ☐ | ___ |
| 27.4 | Code review leadership: architecture alignment, communication guidelines | ☐ | ___ |
| 27.5 | Mentorship framework: pairing, stretch assignments, feedback | ☐ | ___ |
| 27.6 | Incident management: severity levels, post-mortem template | ☐ | ___ |
| 27.7 | System Design interview framework: 4-step approach | ☐ | ___ |
| 27.8 | Back-of-the-envelope calculations: QPS, storage, latency numbers | ☐ | ___ |
| 27.9 | Stakeholder communication: translating tech to business impact | ☐ | ___ |
| 27.10 | Technical debt: quadrant, tracking, 20% rule, prevention | ☐ | ___ |
| 27.11 | Amazon Leadership Principles: Ownership, Dive Deep, Bias for Action, Earn Trust | ☐ | ___ |
| 27.12 | 8 STAR stories prepared: leadership, conflict, mentorship, failure, ambiguity | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 28: PostgreSQL & Relational DB Deep Dive

**Guide Reference**: [26_PostgreSQL_Relational_DB_Deep_Dive.md](01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 28.1 | MVCC: how PostgreSQL handles concurrent reads/writes | ☐ | ___ |
| 28.2 | VACUUM and bloat — why it matters | ☐ | ___ |
| 28.3 | Index types: B-tree, GIN, GiST — when to use | ☐ | ___ |
| 28.4 | Replication: streaming vs logical | ☐ | ___ |
| 28.5 | Connection pooling (PgBouncer) at scale | ☐ | ___ |
| 28.6 | pgvector for embeddings / similarity search | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 29: NoSQL Databases

**Guide Reference**: [27_NoSQL_Databases_Guide.md](01_TechGuide/27_NoSQL_Databases_Guide.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 29.1 | When SQL vs NoSQL — decision framework | ☐ | ___ |
| 29.2 | MongoDB: document model, sharding basics | ☐ | ___ |
| 29.3 | DynamoDB: partition key, sort key, GSI | ☐ | ___ |
| 29.4 | Cassandra: wide-column, partition key design | ☐ | ___ |
| 29.5 | CAP trade-offs in NoSQL systems | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 30: Redis & Distributed Caching

**Guide Reference**: [28_Redis_Distributed_Caching.md](01_TechGuide/28_Redis_Distributed_Caching.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 30.1 | Redis data structures: String, Hash, Set, ZSet, Stream | ☐ | ___ |
| 30.2 | Cache-aside vs read-through vs write-through | ☐ | ___ |
| 30.3 | Cache stampede and mitigation (lock, early expiry) | ☐ | ___ |
| 30.4 | Distributed lock with Redis (Redisson, TTL caveats) | ☐ | ___ |
| 30.5 | Redis Cluster vs Sentinel | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 31: Advanced Networking & Infrastructure

**Guide Reference**: [29_Advanced_Networking_Infrastructure.md](01_TechGuide/29_Advanced_Networking_Infrastructure.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 31.1 | VPC, subnets, public vs private | ☐ | ___ |
| 31.2 | Load balancer L4 vs L7 | ☐ | ___ |
| 31.3 | DNS resolution flow and TTL | ☐ | ___ |
| 31.4 | Zero trust networking basics | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 32: Kubernetes Deep Dive

**Guide Reference**: [30_Kubernetes_Deep_Dive.md](01_TechGuide/30_Kubernetes_Deep_Dive.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 32.1 | Pod, Deployment, Service, Ingress | ☐ | ___ |
| 32.2 | Liveness vs readiness probes | ☐ | ___ |
| 32.3 | HPA and resource requests/limits | ☐ | ___ |
| 32.4 | ConfigMap vs Secret | ☐ | ___ |
| 32.5 | Network policies and pod security | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 33: Cloud Computing (AWS/GCP/Azure)

**Guide Reference**: [31_Cloud_Computing_AWS_GCP_Azure.md](01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 33.1 | IAM: users, roles, policies — least privilege | ☐ | ___ |
| 33.2 | S3 storage classes and lifecycle | ☐ | ___ |
| 33.3 | Lambda use cases and cold start | ☐ | ___ |
| 33.4 | Well-Architected pillars (overview) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 34: Linux & Operating Systems

**Guide Reference**: [32_Operating_Systems_and_Linux.md](01_TechGuide/32_Operating_Systems_and_Linux.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 34.1 | Process vs thread | ☐ | ___ |
| 34.2 | Debugging high CPU / memory on Linux | ☐ | ___ |
| 34.3 | File descriptors and epoll (high level) | ☐ | ___ |
| 34.4 | OOM killer and container limits | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 35: Git & Version Control

**Guide Reference**: [33_Git_Version_Control_Workflow.md](01_TechGuide/33_Git_Version_Control_Workflow.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 35.1 | Merge vs rebase — when to use each | ☐ | ___ |
| 35.2 | Trunk-based development vs GitFlow | ☐ | ___ |
| 35.3 | PR best practices and code review | ☐ | ___ |
| 35.4 | git bisect and revert for incidents | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 36: Search (Elasticsearch)

**Guide Reference**: [34_Search_Engines_Elasticsearch.md](01_TechGuide/34_Search_Engines_Elasticsearch.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 36.1 | Inverted index — how search works | ☐ | ___ |
| 36.2 | BM25 vs TF-IDF (high level) | ☐ | ___ |
| 36.3 | ELK stack components | ☐ | ___ |
| 36.4 | When Elasticsearch vs database full-text | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Section 37: Polyglot, Frontend & Compliance

**Guide Reference**: [36_Polyglot_Interview_Python_and_Go.md](01_TechGuide/36_Polyglot_Interview_Python_and_Go.md), [37_TypeScript_and_Frontend_Landscape.md](01_TechGuide/37_TypeScript_and_Frontend_Landscape.md), [38_Compliance_and_Regulated_Systems.md](01_TechGuide/38_Compliance_and_Regulated_Systems.md)

| # | Topic | Can You Explain? | Confidence (1-5) |
|---|-------|-----------------|-------------------|
| 37.1 | Python DSA: dict, deque, list comprehensions | ☐ | ___ |
| 37.2 | Go: goroutines, channels, error handling | ☐ | ___ |
| 37.3 | TypeScript vs Java typing (structural vs nominal) | ☐ | ___ |
| 37.4 | Angular vs React — trade-offs for enterprise | ☐ | ___ |
| 37.5 | GDPR erasure — technical implementation | ☐ | ___ |
| 37.6 | PCI scope reduction (tokenization, hosted fields) | ☐ | ___ |

**Your Average Score**: ___ / 5

---

## Overall Readiness Score

| Section | Topic | Your Score |
|---------|-------|------------|
| 0 | Fundamentals (OOP, CS, Web, SQL basics) | ___ / 5 |
| 1 | Java Internals & Memory Model | ___ / 5 |
| 2 | Java Concurrency | ___ / 5 |
| 3 | Spring Boot Internals | ___ / 5 |
| 4 | Microservices Resilience | ___ / 5 |
| 5 | Data Consistency Patterns | ___ / 5 |
| 6 | Communication (REST, gRPC, Kafka) | ___ / 5 |
| 7 | Database & Performance Tuning | ___ / 5 |
| 8 | System Design Concepts | ___ / 5 |
| 9 | Modern Trends (AI, Kubernetes) | ___ / 5 |
| 10 | Angular & Frontend | ___ / 5 |
| 11 | Modern Java (17-21) | ___ / 5 |
| 12 | Testing Strategies | ___ / 5 |
| 13 | DevOps, CI/CD, Docker | ___ / 5 |
| 14 | Observability | ___ / 5 |
| 15 | API Design & REST | ___ / 5 |
| 16 | Design Patterns & Architecture | ___ / 5 |
| 17 | Leadership & Behavioral | ___ / 5 |
| 18 | System Design Practice | ___ / 5 |
| 19 | AI, LLM & RAG | ___ / 5 |
| 20 | Application Security & OWASP | ___ / 5 |
| 21 | DSA | ___ / 5 |
| 22 | Java Collections & Concurrency Deep Dive | ___ / 5 |
| 23 | Spring Ecosystem Deep Dive | ___ / 5 |
| 24 | Networking & Protocols | ___ / 5 |
| 25 | Performance Engineering & JVM Tuning | ___ / 5 |
| 26 | Event-Driven Architecture | ___ / 5 |
| 27 | Technical Leadership & Architecture | ___ / 5 |
| 28 | PostgreSQL Deep Dive | ___ / 5 |
| 29 | NoSQL Databases | ___ / 5 |
| 30 | Redis & Distributed Caching | ___ / 5 |
| 31 | Advanced Networking | ___ / 5 |
| 32 | Kubernetes Deep Dive | ___ / 5 |
| 33 | Cloud Computing | ___ / 5 |
| 34 | Linux & OS | ___ / 5 |
| 35 | Git & Version Control | ___ / 5 |
| 36 | Elasticsearch / Search | ___ / 5 |
| 37 | Polyglot, Frontend & Compliance | ___ / 5 |
| **TOTAL** | | **___ / 190** |

### Readiness Interpretation

| Total Score | Readiness Level | Recommendation |
|-------------|----------------|----------------|
| **160-190** | Interview-Ready | Schedule interviews with confidence |
| **130-159** | Almost Ready | Focus on weak sections for 1-2 more weeks |
| **95-129** | Making Progress | Need 3-4 more weeks of focused study |
| **65-94** | Building Foundation | Need 6-8 more weeks, follow the learning path |
| **< 65** | Starting Out | Start §00 fundamentals + DSA Path 1 — see [InterviewPlaybook.md](InterviewPlaybook.md) |

---

## Priority Focus Areas

If you have limited time, focus on these high-impact sections first:

1. **Critical (Interview eliminators)**: Sections 4, 5, 8, 17, 18, 21 (DSA), 22, 25
2. **High (Frequently asked)**: Sections 1, 2, 3, 7, 10, 16, 19 (AI), 23, 26
3. **Medium (Differentiators)**: Sections 6, 9, 11, 15, 20 (Security), 24, 27
4. **Good-to-have (Bonus points)**: Sections 12, 13, 14

