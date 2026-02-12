# Tech Guide — Complete Software Engineering Knowledge Base

> **Covers**: Associate SDE → Mid-Level SDE → Senior SDE → Lead Software Engineer
> **Tech Stack**: Java 17-21, Spring Boot 3.x, Angular 17+, Microservices, Kubernetes, Cloud-Native
> **Purpose**: Your lifetime technical reference. Read sections 01 → 14 in order for a top-to-bottom learning path.

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

- **12.1 OWASP Top 10 (2021)**: Complete guide with Spring Boot prevention code
  - A01 Broken Access Control: IDOR, @PreAuthorize, SecurityFilterChain
  - A02 Cryptographic Failures: BCrypt, AES-256, TLS 1.3
  - A03 Injection: SQL injection, XSS, Content Security Policy
  - A04 Insecure Design: STRIDE threat modeling, security user stories
  - A05 Security Misconfiguration: Actuator lockdown, Docker non-root, error suppression
  - A06 Vulnerable Components: OWASP Dependency Check, Snyk, Dependabot
  - A07 Auth Failures: MFA, rate-limited login, session management
  - A08 Integrity Failures: Signed commits, CI/CD security, SRI
  - A09 Logging Failures: Security audit logging, PII masking
  - A10 SSRF: URL allowlisting, private IP blocking
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
