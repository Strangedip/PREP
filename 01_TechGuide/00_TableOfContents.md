# Tech Guide — Complete Software Engineering Knowledge Base

> **Covers**: Associate SDE → Mid-Level SDE → Senior SDE → Lead Software Engineer
> **Tech Stack**: Java 17-21, Spring Boot 3.x, Angular 17+, Microservices, Kubernetes, Cloud-Native
> **Purpose**: Your lifetime technical reference. Read §00 fundamentals first (Associate), then sections 01 → 38 in order for full coverage.

---

## How to Use This Guide

Every topic follows the same three-part structure:
1. **The "Why" & The Problem** — Why this concept exists and what production problem it solves.
2. **Interviewer Expectations** — What keywords and depth the interviewer expects at each level.
3. **The Deep Dive & Solution** — Complete explanation with code, architecture diagrams, and trade-offs.

### Career Level Indicators

Each section below is tagged with the minimum level where the topic becomes relevant:

| Tag | Meaning |
|-----|---------|
| **ALL** | Fundamental — relevant from Associate to Lead |
| **MID+** | Expected from Mid-Level SDE and above |
| **SR+** | Expected from Senior SDE and above |
| **LEAD** | Expected specifically for Lead / Staff Engineer roles |

---

## Table of Contents

### [Section 00A: Java OOP Fundamentals](./00_Java_OOP_Fundamentals.md) `ALL`

- **00A.1 OOP Four Pillars**: Encapsulation, Abstraction, Inheritance, Polymorphism
- **00A.2 Classes, Objects, Memory**: heap vs stack references
- **00A.3 Inheritance vs Composition**: prefer composition
- **00A.4 Interfaces vs Abstract Classes**
- **00A.5 equals(), hashCode(), toString()**: HashMap contract
- **00A.6 Exception Handling**: checked vs unchecked, try-with-resources
- **00A.7 Collections Overview**: List, Set, Map, Queue

### [Section 00B: Web & HTTP Fundamentals](./00_Web_Fundamentals.md) `ALL`

- **00B.1 How the Web Works**: DNS, TCP, TLS, HTTPS
- **00B.2 HTTP Methods & Idempotency**
- **00B.3 Status Codes**: 200, 401, 403, 404, 429, 500
- **00B.4 Headers**: Content-Type, Authorization, Cache-Control
- **00B.5 Authentication Basics**: Session, JWT, OAuth2
- **00B.6 CORS Preview**
- **00B.7 REST at a Glance**

### [Section 00C: Computer Science Fundamentals](./00_Computer_Science_Fundamentals.md) `ALL`

- **00C.1 Big-O Complexity**
- **00C.2 Space Complexity**
- **00C.3 Core Data Structures**: array, list, stack, queue, hash, tree, heap
- **00C.4 Recursion Pattern**
- **00C.5 Sorting Algorithms Summary**
- **00C.6 Graph Basics**: BFS, DFS, Dijkstra
- **00C.7 Dynamic Programming Intuition**

### [Section 1: Modern Java Features (17-21)](./01_Modern_Java_Features.md) `MID+`

- **1.1 Project Loom**: Virtual Threads, Structured Concurrency
  - Virtual Threads vs. Platform Threads (scalability model)
  - Spring Boot 3.2+ Virtual Thread integration
  - When NOT to use Virtual Threads (CPU-bound tasks, synchronized blocks)
- **1.2 Records**: Immutable data carriers, pattern matching
- **1.3 Sealed Classes**: Exhaustive type hierarchies, domain modeling
- **1.4 Pattern Matching**: instanceof, switch expressions, guarded patterns
- **1.5 Text Blocks**: Multi-line strings for SQL, JSON, HTML templates

### [Section 2: Spring Boot & Java Internals](./02_Advanced_SpringBoot_Java_Internals.md) `MID+`

- **2.1 Java Memory Model**: Heap vs. Stack, Escape Analysis, Generational GC
  - G1GC vs. ZGC vs. Shenandoah — when to use each
  - Debugging Memory Leaks with Heap Dumps (Eclipse MAT, jmap, jcmd)
  - ThreadLocal leaks, ClassLoader leaks, static collection leaks
- **2.2 Concurrency**: CompletableFuture, Custom Thread Pools, Race Conditions
  - Fan-out/Fan-in pattern for parallel microservice calls
  - ThreadPoolExecutor parameters (core, max, queue, rejection policies)
  - Visibility vs. Atomicity — volatile, AtomicInteger, ConcurrentHashMap
  - The Happens-Before relationship (Java Memory Model)
- **2.3 Spring Magic**: @SpringBootApplication, AutoConfiguration, Bean Lifecycle
  - What happens when SpringApplication.run() is called
  - Conditional annotations (@ConditionalOnClass, @ConditionalOnMissingBean)
  - Full bean lifecycle (12 steps from instantiation to destruction)
  - Why @Transactional fails on self-invocation (proxy-based AOP)
- **2.4 Security**: OAuth2 Flows, JWT, Stateless vs. Stateful
  - OAuth2 Authorization Code + PKCE flow (diagram + implementation)
  - Client Credentials flow for service-to-service auth
  - JWT structure (Header.Payload.Signature), RS256 vs. HS256
  - Refresh token rotation and token blacklisting

### [Section 3: Design Patterns, SOLID & Clean Architecture](./03_Design_Patterns_SOLID_CleanArch.md) `MID+`

- **3.1 SOLID Principles**: Real-world examples with violations and fixes
  - SRP with Spring Events, OCP with Strategy pattern
  - LSP, ISP with segregated interfaces, DIP with Spring DI
- **3.2 GoF Patterns**: Factory, Builder, Decorator, Adapter, Strategy, Observer, Template Method
  - Each pattern with Spring Boot real-world examples
- **3.3 Microservices Patterns**: CQRS, Event Sourcing
  - CQRS implementation with separate read/write models
  - Event Sourcing with domain events and aggregate replay
- **3.4 Clean Architecture**: Hexagonal/Ports & Adapters
  - Full package structure with domain, application, adapter layers
  - Dependency Rule — inner layers never depend on outer layers
- **3.5 Domain-Driven Design**: Bounded Contexts, Aggregates, Value Objects

### [Section 4: API Design & REST Best Practices](./04_API_Design_REST.md) `MID+`

- **4.1 RESTful Design**: Resource-oriented URLs, HTTP methods, idempotency
  - Idempotency key implementation for safe retries
  - HTTP status codes cheat sheet
- **4.2 Error Handling**: RFC 7807 Problem Details, global exception handler
- **4.3 Pagination**: Cursor-based vs. offset-based, filtering, sorting
  - Performance comparison and implementation
- **4.4 API Versioning**: URL path vs. header, breaking vs. non-breaking changes
  - Deprecation strategy with Sunset headers
- **4.5 HATEOAS**: Self-describing APIs with hypermedia links
- **4.6 OpenAPI/Swagger**: Auto-generated documentation from code
- **4.7 API Security**: OAuth2, CORS, rate limiting, input validation

### [Section 5: Database & Performance Tuning](./05_Database_Performance_Tuning.md) `MID+`

- **5.1 SQL Optimization**: Indexing, EXPLAIN ANALYZE, N+1 Problem
  - B-Tree index internals and lookup cost (O(log n))
  - Composite index leftmost prefix rule (with examples)
  - Covering indexes (Index-Only Scan) and Partial indexes
  - Reading EXPLAIN ANALYZE output (Seq Scan, Index Scan, Bitmap Heap Scan)
  - N+1 Problem: detection and four fixes (JOIN FETCH, @EntityGraph, @BatchSize, DTO)
- **5.2 Scaling**: Sharding vs. Partitioning, Read Replicas
  - Table partitioning (range, list, hash) with PostgreSQL examples
  - Database sharding: shard key selection, routing, cross-shard queries
  - Consistent hashing for shard routing
  - Read replicas: replication lag and read-after-write consistency
- **5.3 Caching**: Redis/Hazelcast, Cache-Aside, Write-Through, Eviction
  - Cache-Aside pattern with Spring @Cacheable (full implementation)
  - Write-Through and Write-Behind patterns
  - Cache stampede prevention (locking, probabilistic early refresh)
  - Eviction policies: LRU, LFU, TTL
  - Redis vs. Hazelcast comparison (with near-cache example)

### [Section 6: Microservices & Distributed Systems](./06_Microservices_Distributed_Systems.md) `SR+`

- **6.1 Resilience Patterns**: Circuit Breaker, Bulkhead, Retry, Rate Limiting
  - Circuit Breaker state machine (CLOSED → OPEN → HALF_OPEN)
  - Resilience4j configuration with Spring Boot (full YAML + code)
  - Exponential backoff with jitter for retries
  - Semaphore vs. Thread-pool bulkheads
- **6.2 Data Consistency**: SAGA, 2PC, Eventual Consistency
  - **SAGA Choreography**: Event-driven, decoupled (full Kafka implementation)
  - **SAGA Orchestration**: Central coordinator (full Java implementation)
  - Compensating transactions — the undo mechanism
  - **Outbox Pattern**: Solving the dual-write problem with Debezium
  - Two-Phase Commit (2PC): How it works and why we avoid it
  - Eventual Consistency: Idempotency keys, read-your-writes, conflict resolution
- **6.3 Communication**: REST vs. gRPC, Kafka, RabbitMQ
  - REST vs. gRPC performance comparison (Protobuf, HTTP/2 multiplexing)
  - gRPC with Spring Boot (proto file + server implementation)
  - Kafka deep dive: partitions, consumer groups, offsets, idempotent producers
  - Kafka vs. RabbitMQ decision matrix
  - Dead Letter Queues (DLQ) for failed message handling

### [Section 7: System Design Concepts](./07_System_Design.md) `SR+`

- **7.1 Scalability Concepts**: Vertical vs. Horizontal Scaling, CAP, Load Balancers
  - Statelessness as a prerequisite for horizontal scaling (code example)
  - CAP Theorem explained simply (CP vs. AP with real-world examples)
  - PACELC extension (Latency vs. Consistency tradeoff)
  - L4 vs. L7 Load Balancers (NLB vs. ALB)
  - Load balancing algorithms (Round Robin, Least Connections, Consistent Hashing)
  - Consistent Hashing with virtual nodes for cache distribution
- **7.2 Design Patterns**: API Gateway, BFF, Strangler Fig
  - API Gateway responsibilities and Spring Cloud Gateway implementation
  - BFF (Backend for Frontend): separate APIs for web, mobile, TV
  - Strangler Fig Pattern: incremental monolith-to-microservices migration
  - Anti-Corruption Layer, feature flags, shadow traffic, canary releases

### [Section 8: Angular & Frontend Engineering](./08_Angular_Frontend_Engineering.md) `ALL`

- **8.1 Angular Architecture (Angular 17+)**: Standalone Components, Signals, New Control Flow
  - Standalone Components (standalone: true, direct imports in component decorator)
  - Signals: signal(), computed(), effect(), input(), output() — synchronous, glitch-free
  - Signals vs. RxJS: when to use each, toSignal(), toObservable() bridge
  - Change Detection (Default vs. OnPush), Zoneless Angular
  - New Control Flow syntax: @if, @for, @switch (Angular 17+ replacing *ngIf, *ngFor)
  - Deferred Views: @defer with on viewport, on interaction, on timer, prefetch
- **8.2 State Management**: Service-based Signals vs. NgRx Signal Store
  - Service-based state with Signals (signal, computed, asReadonly — small-medium apps)
  - NgRx Signal Store (signalStore, withState, withComputed, withMethods — enterprise apps)
  - Optimistic updates with error rollback
- **8.3 RxJS Mastery**: The Four Map Operators, Combination Operators, Memory Leaks
  - switchMap (cancel previous), mergeMap (concurrent), concatMap (sequential), exhaustMap (ignore)
  - combineLatest, forkJoin, withLatestFrom — use cases and differences
  - Memory leak prevention: takeUntilDestroyed(), async pipe, toSignal()
- **8.4 Routing, Lazy Loading, and Guards**
  - Lazy loading: loadComponent, loadChildren in routes
  - Functional guards: canActivate, canDeactivate, canMatch (Angular 15+)
  - Route Resolvers: ResolveFn for pre-fetching data
  - Preloading strategies: PreloadAllModules
- **8.5 HTTP Client, Interceptors, and Error Handling**
  - Functional Interceptors: HttpInterceptorFn (auth, error handling, logging, caching)
  - Global error handler with retry, token refresh for 401, rate limit for 429
- **8.6 Performance Optimization**: OnPush, Virtual Scrolling, Bundle Analysis
  - Tree shaking, lazy loading, preloading strategies
  - Virtual Scrolling with @angular/cdk/scrolling
  - trackBy for *ngFor / track for @for
- **8.7 Reactive Forms & Type-Safe Forms**
  - Typed Forms (Angular 14+): FormGroup<T>, NonNullableFormBuilder
  - Custom validators (sync and async), cross-field validation
- **8.8 Testing**: TestBed, ComponentHarness, HttpTestingController
  - Component testing with ComponentFixture
  - Service testing with HttpTestingController
  - Mocking services with Signals

### [Section 9: Testing Strategies](./09_Testing_Strategies.md) `MID+`

- **9.1 Test Pyramid**: Unit → Integration → E2E ratios and trade-offs
- **9.2 Unit Testing**: JUnit 5, Mockito, parameterized tests, BDD-style
- **9.3 Integration Testing**: Spring Boot Test, Testcontainers, real DB/Kafka
- **9.4 Contract Testing**: Spring Cloud Contract, consumer-driven contracts
- **9.5 Performance Testing**: JMeter, Gatling, load profiles, SLA validation
- **9.6 Security Testing**: OWASP ZAP, SonarQube, dependency scanning
- **9.7 E2E Testing**: Playwright, Cypress, Page Object Model
- **9.8 Testing Best Practices**: Quick reference, mutation testing, interview keywords

### [Section 10: DevOps, CI/CD, Docker & Containerization](./10_DevOps_CICD_Docker.md) `SR+`

- **10.1 Docker**: Multi-stage builds for Java/Angular, layer caching, distroless images
  - Production Dockerfiles with security best practices
  - Docker Compose for local development environments
- **10.2 CI/CD Pipelines**: GitHub Actions & GitLab CI complete examples
  - Pipeline stages: build → test → analyze → package → deploy
  - Blue-Green, Canary, and Rolling Update deployment strategies
  - Trunk-based development vs. GitFlow
- **10.3 GitOps**: ArgoCD, pull-based deployments, drift detection
  - Config repo separation, Kustomize overlays
- **10.4 Feature Flags**: Decoupling deployment from release
  - Implementation in Spring Boot and Angular
- **10.5 Infrastructure as Code**: Terraform for AWS (EKS, RDS, ElastiCache)
- **10.6 DevSecOps**: SAST/DAST/SCA, container scanning, secrets management

### [Section 11: Observability — Logging, Metrics & Distributed Tracing](./11_Observability.md) `SR+`

- **11.1 Three Pillars**: How logs, metrics, and traces work together
- **11.2 Structured Logging**: JSON logging, MDC, ELK Stack vs. Grafana Loki
  - Logback configuration for Spring Boot (dev vs. production)
  - Log aggregation architecture
- **11.3 Metrics**: Prometheus, Grafana, Micrometer, RED & USE methods
  - Custom business metrics, histogram buckets, SLI/SLO/SLA framework
  - Alerting rules and Alertmanager configuration
- **11.4 Distributed Tracing**: OpenTelemetry, Jaeger, context propagation
  - Auto-instrumentation and custom spans
  - Sampling strategies (head-based, tail-based, parent-based)
- **11.5 Grafana Dashboards**: Four essential dashboards for Lead Engineers
- **11.6 Alerting Strategy**: Symptom-based alerting, error budgets, runbooks

### [Section 12: Application Security, OWASP & Cloud Fundamentals](./12_Security_OWASP_Cloud.md) `SR+`

- **12.1 OWASP Top 10 (2025)**: Complete guide with 2021→2025 mapping and Spring Boot prevention code
  - A01 Broken Access Control (+ SSRF): IDOR, @PreAuthorize, URL allowlisting for outbound calls
  - A02 Security Misconfiguration (#2 in 2025): Actuator lockdown, Docker non-root, cloud/K8s hardening
  - A03 Software Supply Chain Failures: OWASP Dependency Check, Snyk, SBOM, signed images
  - A04 Cryptographic Failures: BCrypt, AES-256, TLS 1.3
  - A05 Injection: SQL injection, XSS, Content Security Policy
  - A06 Insecure Design: STRIDE threat modeling, security user stories
  - A07 Authentication Failures: MFA, rate-limited login, session management
  - A08 Integrity Failures: Signed commits, CI/CD security, SRI
  - A09 Logging & Alerting Failures: Security audit logging, PII masking
  - A10 Mishandling Exceptional Conditions: Fail closed, no stack traces to clients
- **12.2 JWT Security Deep Dive**: Algorithm confusion, token storage, RS256, refresh rotation
- **12.3 CORS**: Same-Origin Policy, preflight, Spring Boot configuration
- **12.4 Cloud Fundamentals for Backend Engineers**: AWS core services, deployment architecture
  - EC2, ECS/EKS, Lambda, RDS, DynamoDB, ElastiCache, S3, SQS, SNS, CloudFront
  - IAM least privilege, VPC network security, security groups
  - AWS services decision guide
- **12.5 Security in System Design Interviews**: 8-point security checklist
- **12.6 Interview Quick Reference**: OWASP one-liners, security headers table

### [Section 13: Modern Trends (2026 Focus)](./13_Modern_Trends_2026.md) `LEAD`

- **13.1 AI Integration**: RAG with Spring Boot & Vector Databases
  - Complete RAG architecture (ingestion + retrieval + generation)
  - Vector embeddings and cosine similarity explained
  - Chunking strategies (fixed-size, semantic, recursive)
  - Full Spring AI implementation (Spring AI + pgvector + OpenAI)
  - Document ingestion pipeline (PDF, database, multi-format)
  - Advanced RAG: hybrid search, re-ranking, metadata filtering
  - Vector database comparison (pgvector, Pinecone, Weaviate, Milvus)
- **13.2 Kubernetes for Leads**: Pods, Services, Ingress, Rolling Updates
  - Kubernetes architecture (Control Plane, Worker Nodes, etcd)
  - Production-ready Deployment manifest (complete YAML with probes, resources)
  - Three types of probes: Startup, Liveness, Readiness (with Spring Boot Actuator)
  - Services: ClusterIP, NodePort, LoadBalancer (DNS-based discovery)
  - Ingress: path-based routing, TLS termination, rate limiting
  - Rolling Updates: maxSurge, maxUnavailable, rollback
  - Horizontal Pod Autoscaler (HPA) with CPU, memory, and custom metrics
  - Graceful shutdown: SIGTERM handling, PreStop hooks, Spring Boot integration

### [Section 14: Leadership, Behavioral & System Design Interviews](./14_Leadership_Behavioral_SystemDesign.md) `LEAD`

- **14.1 STAR Method**: Structuring behavioral answers with concrete examples
  - Technical leadership stories
  - Mentoring and conflict resolution stories
  - Delivery under pressure and project failure stories
- **14.2 Leadership Principles**: Ownership, Bias for Action, Earn Trust
  - Preparing your story bank (10 stories covering all categories)
- **14.3 System Design Framework**: 4-step approach for 45-minute interviews
  - Requirements → High-Level Design → Deep Dive → Operational Concerns
  - Back-of-the-envelope estimation framework
  - Full example: Design a Notification System
- **14.4 Common System Design Questions**: Key components and trade-offs
- **14.5 Architecture Decision Records (ADRs)**: Documenting technical decisions
- **14.6 Estimation & Planning**: PERT estimation, technical roadmapping

### [Section 15: Java Collections & Concurrency Deep Dive](./15_Java_Collections_Concurrency_DeepDive.md) `MID+`

- **15.1 Collections Framework Internals**: HashMap internals (hashing, treeification, resizing), LinkedHashMap, TreeMap
  - HashMap Node structure, collision resolution, load factor 0.75
  - Fail-fast vs fail-safe iterators, Immutable Collections (Java 9+)
  - Collection performance comparison table
- **15.2 Java Stream API Deep Dive**: Collectors, flatMap, parallel streams, Gatherers
  - Four Collector categories: Reducing, Grouping, Partitioning, Joining
  - Custom Collector implementation
  - Parallel stream pitfalls and custom ForkJoinPool
- **15.3 Functional Programming in Java**: Function, Predicate, Consumer, Supplier, Optional best practices
- **15.4 Concurrency — Locks & Synchronizers**: ReentrantLock, ReadWriteLock, StampedLock
  - CountDownLatch, CyclicBarrier, Semaphore, Phaser — comparison table
- **15.5 ConcurrentHashMap Internals**: Java 7 segments vs Java 8 CAS+synchronized
  - Atomic compound operations, bulk parallel operations
- **15.6 Atomic Variables & CAS**: AtomicInteger, AtomicReference, LongAdder
- **15.7 CompletableFuture Advanced Patterns**: Fan-out/in, first-wins, pipeline, retry
- **15.8 Thread Pool Configuration**: ThreadPoolExecutor 7 parameters, sizing guidelines
- **15.9 Interview Quick Reference**: Top questions, one-line answers, keywords

### [Section 16: Spring Ecosystem Deep Dive](./16_Spring_Ecosystem_DeepDive.md) `MID+`

- **16.1 Spring Data JPA — Beyond Basics**: Query methods, @Query, Specifications, Projections, Auditing, Batch operations
- **16.2 Spring WebFlux & Project Reactor**: Mono, Flux, reactive REST controllers, WebClient
- **16.3 Spring Cloud**: Service Discovery (Eureka), API Gateway, Config Server
- **16.4 Spring Batch**: Chunk processing, readers/writers, fault tolerance, skip/retry
- **16.5 Spring AOP**: CGLIB proxy internals, self-invocation problem, custom aspects, @RateLimit
- **16.6 Spring Profiles & Configuration**: @ConfigurationProperties, type-safe config, HikariCP tuning
- **16.7 Spring Actuator**: Custom health indicators, business metrics, Prometheus export
- **16.8 Spring Events**: ApplicationEventPublisher, @TransactionalEventListener, async events
- **16.9 Interview Quick Reference**: Top questions, one-line answers, keywords

### [Section 17: Networking & Protocols](./17_Networking_Protocols.md) `MID+`

- **17.1 OSI Model & TCP/IP**: TCP vs UDP, three-way handshake, TIME_WAIT
- **17.2 HTTP Deep Dive**: HTTP/1.1 vs HTTP/2 vs HTTP/3 (QUIC), multiplexing, 0-RTT
- **17.3 TLS/SSL**: TLS 1.3 handshake, mTLS, forward secrecy, certificate pinning
- **17.4 DNS**: Resolution flow, record types, GeoDNS, DNS in system design
- **17.5 WebSocket Protocol**: Full-duplex, Spring Boot STOMP, WebSocket vs SSE vs Long Polling
- **17.6 gRPC & Protocol Buffers**: 4 streaming patterns, Protobuf, gRPC vs REST decision guide
- **17.7 CDN**: Edge caching, Cache-Control headers, ETag, invalidation
- **17.8 Connection Pooling**: HikariCP sizing, HTTP client pools, TCP reuse
- **17.9 Interview Quick Reference**: Top questions, one-line answers, keywords

### [Section 18: Performance Engineering & JVM Tuning](./18_Performance_Engineering_JVM.md) `SR+`

- **18.1 JVM Architecture**: Memory layout, essential JVM flags, GC selection
- **18.2 Garbage Collection Deep Dive**: G1GC vs ZGC vs Shenandoah decision matrix
- **18.3 Profiling**: Java Flight Recorder (JFR), async-profiler flame graphs, thread dump analysis, heap dump analysis
- **18.4 JMH — Microbenchmarking**: Proper Java benchmarking with warmup, fork, Blackhole
- **18.5 Common Performance Anti-Patterns**: N+1, chatty calls, inefficient serialization, sync I/O in event loops
- **18.6 Performance Testing Methodology**: Load, stress, soak, spike testing
- **18.7 Performance Budgets**: Backend and frontend budgets, SLI/SLO/SLA framework, error budgets
- **18.8 Interview Quick Reference**: Top questions, one-line answers, keywords

### [Section 19: Event-Driven Architecture & Messaging](./19_Event_Driven_Architecture.md) `SR+`

- **19.1 Event-Driven Patterns**: Event Notification, Event-Carried State Transfer, Event Sourcing
  - Message vs Event vs Command — naming conventions
- **19.2 Apache Kafka Deep Dive**: Architecture, Spring Boot producer/consumer, delivery guarantees
  - exactly-once semantics, idempotent producers, transactional API
- **19.3 Schema Evolution**: Avro, Schema Registry, backward/forward/full compatibility
- **19.4 Event Sourcing & CQRS**: Aggregate, event handler, projection, Axon Framework
- **19.5 Dead Letter Queues & Error Handling**: Retry strategies, DLT, poison pill handling
- **19.6 Saga Pattern**: Choreography vs Orchestration, compensating transactions, Outbox Pattern
- **19.7 RabbitMQ vs Kafka**: Architecture comparison, decision guide
- **19.8 Change Data Capture (CDC)**: Debezium, transaction log, use cases
- **19.9 Interview Quick Reference**: Top questions, one-line answers, keywords

### [Section 20: Technical Leadership & Software Architecture](./20_Technical_Leadership_Architecture.md) `LEAD`

- **20.1 Architecture Decision Records (ADRs)**: Template, when to write, examples
- **20.2 Technical Estimation & Planning**: T-shirt sizing, story points, three-point estimation
  - Technical Design Document template
- **20.3 Code Review Leadership**: Architecture alignment, production readiness, communication guidelines
- **20.4 Team Mentorship & Technical Growth**: Mentorship framework, career development
- **20.5 Incident Management & Post-Mortems**: Severity levels, post-mortem template, blameless culture
- **20.6 System Design Interview Framework**: 4-step approach, back-of-the-envelope calculations
- **20.7 Stakeholder Communication**: Translating technical decisions for non-technical audiences
- **20.8 Technical Debt Management**: Debt quadrant, tracking, payoff strategies
- **20.9 Behavioral Interview — STAR Stories**: 8 prepared story templates for Lead roles
- **20.10 Interview Quick Reference**: Amazon Leadership Principles, top questions

### [Section 21: GraphQL & Alternative API Styles](./21_GraphQL_and_Alternative_APIs.md) `MID+`

- **21.1 API Style Comparison**: REST vs GraphQL vs gRPC vs WebSocket vs SSE
- **21.2 GraphQL Fundamentals**: Schema, queries, mutations, resolvers
- **21.3 Spring GraphQL**: `@QueryMapping`, `@SchemaMapping`, GraphiQL
- **21.4 N+1 Problem & DataLoader**: Batch loading, request-scoped cache
- **21.5 GraphQL at Scale**: Persisted queries, field caching, security (depth/complexity limits)
- **21.6 GraphQL Federation**: Subgraphs, `@key`, gateway stitching
- **21.7 Interview Quick Reference**: REST vs GraphQL trade-offs, when NOT to use GraphQL

### [Section 22: Kotlin for Java Developers](./22_Kotlin_for_Java_Developers.md) `MID+`

- **22.1 Why Kotlin in 2026**: Android, Spring Boot, gradual adoption
- **22.2 Kotlin vs Java**: Null safety, data classes, extension functions, SAM
- **22.3 Coroutines vs Virtual Threads**: When to use each, structured concurrency
- **22.4 Spring Boot with Kotlin**: kotlin-spring plugin, Jackson module, DTO patterns
- **22.5 Java Interop**: `@JvmStatic`, companion objects, nullable types from Java
- **22.6 When to Choose Kotlin vs Java**: Decision guide for teams
- **22.7 Interview Quick Reference**: Null safety, coroutines, interop pitfalls

### [Section 23: SRE & Reliability Engineering](./23_SRE_Reliability_Engineering.md) `SR+`

- **23.1 SRE vs DevOps vs Platform Engineering**: Role boundaries
- **23.2 SLI → SLO → SLA → Error Budget**: Framework and examples
- **23.3 Four Golden Signals**: Latency, Traffic, Errors, Saturation (RED/USE methods)
- **23.4 Incident Management**: Severity levels, IC roles, mitigation-first response
- **23.5 Post-Mortems**: Blameless culture, template, action items
- **23.6 Toil Reduction**: Automation targets, runbooks, self-healing
- **23.7 Reliability Patterns**: Circuit breaker, canary rollback, chaos engineering
- **23.8 On-Call Best Practices**: Actionable alerts, escalation, runbooks
- **23.9 Interview Quick Reference**: Error budgets, golden signals, incident command

### [Section 24: Platform Engineering & IDP](./24_Platform_Engineering_IDP.md) `SR+`

- **24.1 Internal Developer Platforms**: Self-service, cognitive load reduction
- **24.2 IDP Components**: Backstage, golden paths, GitOps, policy-as-code
- **24.3 Golden Paths**: Templates for microservices, databases, feature flags
- **24.4 Backstage Developer Portal**: Catalog, TechDocs, software templates
- **24.5 Platform Metrics**: DORA metrics, developer satisfaction, self-service rate
- **24.6 GitOps**: Argo CD, PR-based infra changes
- **24.7 Security in IDPs**: Image scanning, Kyverno policies, SBOM
- **24.8 Interview Quick Reference**: Golden paths, thin platform, Backstage, DORA

### [Section 25: Data Engineering Fundamentals](./25_Data_Engineering_Fundamentals.md) `SR+`

- **25.1 Batch vs Stream Processing**: Latency trade-offs, Spark, Flink, Kafka
- **25.2 ETL vs ELT**: Modern data stack with dbt
- **25.3 Tools Landscape**: Airflow, Spark, Snowflake, Delta Lake, Debezium
- **25.4 Lambda vs Kappa Architecture**: Batch + speed layers vs stream-only
- **25.5 Change Data Capture (CDC)**: Debezium, WAL, event-driven sync
- **25.6 Warehouse vs Lake vs Lakehouse**: When to use each
- **25.7 Data Quality & Governance**: Schema evolution, lineage, PII
- **25.8 Backend Engineer Touchpoints**: Events, CDC, embedding pipelines
- **25.9 Interview Quick Reference**: ETL/ELT, CDC, lakehouse, data contracts

### [Section 26: PostgreSQL & Relational DB Deep Dive](./26_PostgreSQL_Relational_DB_Deep_Dive.md) `MID+`

- **26.1 Why PostgreSQL**: ACID, JSONB, pgvector, ecosystem
- **26.2 Transaction Isolation Levels**: READ COMMITTED through SERIALIZABLE
- **26.3 MVCC**: Tuple visibility, snapshot isolation, no reader-writer blocking
- **26.4 VACUUM & Bloat**: Dead tuples, autovacuum tuning
- **26.5 Connection Pooling**: PgBouncer, HikariCP sizing
- **26.6 Replication & HA**: Streaming replication, sync vs async, failover
- **26.7 Partitioning**: Range, list, hash — partition pruning
- **26.8 JSONB, FTS, pgvector**: Modern PostgreSQL features
- **26.9 Query Planner**: EXPLAIN ANALYZE deep reading
- **26.10 Interview Quick Reference**: MVCC, WAL, VACUUM, replication lag

### [Section 27: NoSQL Databases Guide](./27_NoSQL_Databases_Guide.md) `SR+`

- **27.1 SQL vs NoSQL Decision Framework**
- **27.2 NoSQL Categories**: Document, key-value, wide-column, graph
- **27.3 MongoDB**: Data model, sharding, transactions
- **27.4 DynamoDB**: Access-pattern-first design, GSI, single-table
- **27.5 Cassandra**: LSM writes, tunable consistency, QUORUM
- **27.6 CAP at Database Level**
- **27.7 Interview Quick Reference**: Partition keys, hot partitions, LSM

### [Section 28: Redis & Distributed Caching](./28_Redis_Distributed_Caching.md) `MID+`

- **28.1 Redis Architecture**: Single-threaded, in-memory
- **28.2 Data Structures**: Strings, hashes, sorted sets, streams
- **28.3 Caching Patterns**: Cache-aside, stampede prevention
- **28.4 Eviction Policies**: LRU, LFU, volatile-lru
- **28.5 Persistence**: RDB vs AOF
- **28.6 Redis Cluster**: Hash slots, hash tags
- **28.7 Distributed Locking**: SET NX EX, Redisson
- **28.8 Rate Limiting**: Sliding window with sorted sets
- **28.9 Interview Quick Reference**: Cache-aside, stampede, cluster slots

### [Section 29: Advanced Networking & Infrastructure](./29_Advanced_Networking_Infrastructure.md) `SR+`

- **29.1 IP Addressing & Subnets**: CIDR, private ranges
- **29.2 VPC Architecture**: Public/private/data tiers, IGW, NAT
- **29.3 Load Balancers**: L4 vs L7, algorithms
- **29.4 DNS Production**: GeoDNS, failover, TTL trade-offs
- **29.5 NAT, Reverse Proxy, Forward Proxy**
- **29.6 Service Mesh Networking**: Istio, mTLS, traffic splitting
- **29.7 DDoS & WAF**
- **29.8 Zero Trust Networking**
- **29.9 Interview Quick Reference**: SG vs NACL, VPC, L4/L7 LB

### [Section 30: Kubernetes Deep Dive](./30_Kubernetes_Deep_Dive.md) `SR+`

- **30.1 K8s Architecture**: Control plane, kubelet, etcd
- **30.2 Core Objects**: Pod, Deployment, StatefulSet, Service, Ingress
- **30.3 Networking**: ClusterIP, Ingress, NetworkPolicy
- **30.4 Health Probes**: Liveness, readiness, startup
- **30.5 Resources & HPA**: requests/limits, autoscaling
- **30.6 Deployment Strategies**: Rolling, blue-green, canary
- **30.7 Security**: RBAC, pod security, secrets
- **30.8 Interview Quick Reference**: probes, HPA, etcd, Ingress

### [Section 31: Cloud Computing — AWS, GCP, Azure](./31_Cloud_Computing_AWS_GCP_Azure.md) `MID+`

- **31.1 Service Models**: IaaS, PaaS, SaaS, FaaS
- **31.2 AWS Core Services Map**
- **31.3 GCP & Azure Equivalents**
- **31.4 IAM Least Privilege**
- **31.5 S3 Deep Dive**: Classes, presigned URLs, events
- **31.6 Serverless Lambda Patterns**
- **31.7 Cost Optimization**
- **31.8 Well-Architected Framework**
- **31.9 Interview Quick Reference**: S3 vs EBS, SQS vs SNS, IAM roles

### [Section 32: Operating Systems & Linux](./32_Operating_Systems_and_Linux.md) `ALL`

- **32.1 Process vs Thread**
- **32.2 Linux Process States**
- **32.3 Essential Commands**: top, strace, lsof, journalctl
- **32.4 Memory Management**: virtual memory, OOM, swap
- **32.5 File Descriptors & ulimit**
- **32.6 I/O Models**: blocking, epoll, io_uring
- **32.7 Containers & Namespaces**
- **32.8 Interview Quick Reference**: context switch, OOM, epoll, ulimit

### [Section 33: Git & Engineering Workflow](./33_Git_Version_Control_Workflow.md) `ALL`

- **33.1 Git Fundamentals**: add, commit, push, branch
- **33.2 Merge vs Rebase**
- **33.3 Branching Strategies**: trunk-based, Git Flow, GitHub Flow
- **33.4 Pull Request Best Practices**
- **33.5 Conflict Resolution**
- **33.6 Git Internals**: blob, tree, commit objects
- **33.7 Monorepo vs Polyrepo**
- **33.8 Interview Quick Reference**: trunk-based, rebase rule, revert vs reset

### [Section 34: Search Engines & Elasticsearch](./34_Search_Engines_Elasticsearch.md) `SR+`

- **34.1 Inverted Index**
- **34.2 Elasticsearch Architecture**: shards, replicas
- **34.3 Analysis Pipeline**: tokenizers, analyzers, ngram
- **34.4 Query Types**: match, term, bool
- **34.5 BM25 Relevance Scoring**
- **34.6 Aggregations**: faceted search
- **34.7 ELK Stack**
- **34.8 ES vs PostgreSQL Full-Text**
- **34.9 Interview Quick Reference**: inverted index, BM25, shards

### [Section 35: SQL Fundamentals](./35_SQL_Fundamentals.md) `ALL`

- **35.1 Core SQL Operations**: DDL, DML, TCL
- **35.2 SELECT Essentials**: execution order
- **35.3 JOIN Types**: INNER, LEFT, FULL, SELF
- **35.4 Subqueries vs JOINs**: EXISTS, correlated
- **35.5 Window Functions**: RANK, LAG, running totals
- **35.6 CTEs**: recursive hierarchies
- **35.7 Indexes Basics**
- **35.8 Normalization vs Denormalization**
- **35.9 Interview Quick Reference**: JOINs, window functions, HAVING

### [Section 36: Polyglot Interview Prep — Python & Go](./36_Polyglot_Interview_Python_and_Go.md) `MID+`

- **36.1 Why Polyglot Rounds Exist**
- **36.2 Python DSA Essentials**: dict, deque, BFS/DFS
- **36.3 Python Concurrency**: asyncio, GIL
- **36.4 Go DSA Essentials**: slices, maps, sort
- **36.5 Go Concurrency**: goroutines, channels, WaitGroup
- **36.6 When to Say "I'd Use Java Here"**

### [Section 37: TypeScript & Frontend Landscape](./37_TypeScript_and_Frontend_Landscape.md) `ALL`

- **37.1 Frontend Stack Landscape**: Angular, React, Vue, Svelte
- **37.2 TypeScript Essentials**: interfaces, generics, async
- **37.3 React vs Angular Comparison**
- **37.4 Modern Frontend Concerns**: SSR, bundling, testing
- **37.5 Browser Security**: XSS, CSRF, token storage

### [Section 38: Compliance & Regulated Systems](./38_Compliance_and_Regulated_Systems.md) `SR+`

- **38.1 Why Compliance Matters in Interviews**
- **38.2 Regulations Summary**: GDPR, PCI-DSS, HIPAA, SOC 2
- **38.3 PII Handling in Architecture**
- **38.4 GDPR Technical Implementation**
- **38.5 PCI-DSS for Engineers**
- **38.6 Audit & Compliance Logging**
- **38.7 Change Management (SOX)**

---

## Quick Reference: Keywords to Use in Interviews

| Topic | Must-Say Keywords |
|-------|-------------------|
| Memory | Weak Generational Hypothesis, Escape Analysis, TLAB, Dominator Tree, GC Roots |
| GC | Concurrent Marking, Colored Pointers (ZGC), Stop-The-World, Region-based |
| Concurrency | Fan-out/Fan-in, CAS (Compare-And-Swap), Happens-Before, Work-Stealing |
| Spring | IoC, BeanPostProcessor, Conditional Beans, CGLIB Proxy, Component Scanning |
| Security | PKCE, Refresh Token Rotation, JWKS, Opaque vs. Self-Contained Token |
| Resilience | Cascading Failure, Fail Fast, Exponential Backoff + Jitter, Blast Radius |
| SAGA | Compensating Transaction, Orchestration vs. Choreography, Outbox Pattern |
| Database | Covering Index, Partition Pruning, Leftmost Prefix Rule, N+1 Problem |
| Caching | Cache-Aside, Cache Stampede, TTL, Eviction Policy, Near-Cache |
| System Design | CAP Theorem, Consistent Hashing, Strangler Fig, Anti-Corruption Layer |
| Kubernetes | Desired State, Reconciliation Loop, Readiness Probe, HPA, Pod Disruption Budget |
| AI/RAG | Vector Embedding, Cosine Similarity, Semantic Search, Chunking, Context Window |
| Angular | Standalone Components, Signals, OnPush, Zoneless, @defer, NgRx Signal Store, switchMap, Functional Guards, Functional Interceptors |
| Docker/CI-CD | Multi-stage Build, Layer Caching, GitOps, Canary, Feature Flags, Trunk-based |
| Observability | Three Pillars, RED Method, USE Method, SLI/SLO, OpenTelemetry, Tail Sampling |
| API Design | RFC 7807, Cursor Pagination, Idempotency Key, HATEOAS, OpenAPI, Versioning |
| Architecture | Clean Architecture, Hexagonal, CQRS, Event Sourcing, Bounded Context, DIP |
| Security | OWASP Top 10, STRIDE, IDOR, JWT Algorithm Confusion, CORS, CSP, HSTS, PCI DSS, SSRF, BCrypt |
| Cloud/AWS | VPC, Security Groups, IAM Least Privilege, EKS, RDS, S3, SQS, SNS, ALB, CloudWatch |
| Leadership | STAR Method, Ownership, Data-driven, Trade-offs, ADR, Error Budget |
| PostgreSQL | MVCC, WAL, VACUUM, Bloat, PgBouncer, Read Replica Lag |
| NoSQL | Partition Key, Hot Partition, GSI, LSM Tree, QUORUM |
| Redis | Cache-Aside, Stampede, Sorted Set, Hash Slots, SET NX |
| Networking Infra | VPC, NAT, Security Group, L4/L7 LB, Zero Trust |
| Kubernetes | Reconciliation, Readiness Probe, HPA, Ingress, etcd |
| Cloud | IAM Least Privilege, S3 Presigned URL, Shared Responsibility |
| Linux | Context Switch, epoll, OOM, ulimit, File Descriptor |
| SQL | Window Functions, CTE, LEFT JOIN, HAVING vs WHERE |
| Elasticsearch | Inverted Index, BM25, Shard, Analyzer, Bool Query |
| Polyglot | Python defaultdict/deque, Go goroutine/channel, GIL |
| Compliance | GDPR erasure, PCI scope, tokenization, audit trail, data residency |
