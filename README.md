# Developer Master Book — Fresher to Principal

> **Career ladder**: Fresher → SDE1 → SDE2 → Senior SDE → Tech Lead → Staff Engineer → Principal / Architect
> **Audience**: Developers based in **India**; companies include **global firms hiring into India** (GCC, remote-for-India, India offices) — not limited to Indian HQ companies.
> **Tech stack**: Java 17–21, Spring Boot 3.x, Angular 17+, Microservices, Kubernetes, Cloud-Native, AI/RAG
>
> **Start here**: [ROADMAP.md](ROADMAP.md) — map your level on the linear ladder, then follow the checklist

---

## What This Repository Covers

One self-contained repo: follow [ROADMAP.md](ROADMAP.md) level-by-level. Each topic file holds full depth for that subject — no hopping across folders for the same concept.

| Level | What You Need | Where to Start in ROADMAP |
|-------|---------------|---------------------------|
| **Fresher** | CS/Java basics, SQL, Easy DSA, OA prep | [Fresher](ROADMAP.md#fresher) |
| **SDE1** | Spring REST, intro Medium DSA, LLD | [SDE1](ROADMAP.md#sde1) |
| **SDE2** | Patterns, DB tuning, Medium DSA, light HLD | [SDE2](ROADMAP.md#sde2) |
| **Senior SDE** | Distributed systems, flagship HLD, Hard DSA | [Senior SDE](ROADMAP.md#senior-sde) |
| **Tech Lead** | Behavioral, team SD, AI product design | [Tech Lead](ROADMAP.md#tech-lead) |
| **Staff Engineer** | Platform, Tier 3 DSA, cross-team architecture | [Staff](ROADMAP.md#staff-engineer) |
| **Principal** | Vision, org design, DR, exec comms | [Principal](ROADMAP.md#principal-architect) |

**Mid-career?** Use [Where am I?](ROADMAP.md#where-am-i-map-yourself-on-the-ladder) — pick the level matching your scope, not years alone.

---

## Repository Structure

```
PREP/
│
├── README.md                    ← You are here (overview)
├── ROADMAP.md                   ← START HERE — level checklist (Fresher → Principal)
├── MASTER_INDEX.md              ← Topic → file → round → level lookup
├── Tracking_Iron_Protocol.xlsx  ← Daily prep tracker (generate: python tools/tracker_generator.py)
│
├── 00_Interview_Prep/           ← Career, mocks, companies, level guides, Principal strategy
│   ├── README.md                ← Hub for all interview-prep markdown (not DSA/SD)
│   ├── Career/                  ← Resume & portfolio (Fresher)
│   ├── Core/                    ← Playbook, SelfAssessment, Companies, CodeQuality, CrossRefs
│   ├── Mock/                    ← Mock interview rubric
│   ├── Levels/                  ← SDE2 → Staff failure modes, loops, comp, ARF
│   └── Principal/               ← EA frameworks, vision, org design, DR, integration, exec comms
│
├── 01_TechGuide/                ← Java, Spring, distributed systems, platform (§00–§38)
├── 02_DSA/                      ← 125+ problems + StudyGuide + Tier3
├── 03_CodingPatterns/           ← GoF patterns + algorithmic patterns + machine coding
├── 04_SystemDesign/             ← LLD + HLD + templates
├── 05_AI/                       ← RAG, agents, MLOps, AI system design
├── 06_On_The_Job/               ← Post-hire: onboarding, debugging, on-call, RFC/ADR, EM track
│
└── tools/
    └── tracker_generator.py     ← Regenerates Tracking_Iron_Protocol.xlsx
```

### How to navigate

| Step | Action |
|------|--------|
| 1 | Open [ROADMAP.md](ROADMAP.md) → find your level → work the checklist |
| 2 | Theory gaps → [01_TechGuide/](01_TechGuide/) |
| 3 | Coding → [02_DSA/StudyGuide.md](02_DSA/StudyGuide.md) |
| 4 | Interview process, companies, mocks → [00_Interview_Prep/README.md](00_Interview_Prep/README.md) |
| 5 | System design → [04_SystemDesign/README.md](04_SystemDesign/README.md) |
| 6 | Can't find a topic → [MASTER_INDEX.md](MASTER_INDEX.md) |
| 7 | Already hired / on the job → [06_On_The_Job/](06_On_The_Job/README.md) |

---

## Learning Path by Career Level

### Associate / Junior SDE — Weeks 1-16

> **ROADMAP level**: [Fresher](ROADMAP.md#fresher) → [SDE1](ROADMAP.md#sde1)

You are learning the fundamentals. Focus on understanding, not speed.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Java OOP, CS fundamentals, Big-O | [§00A Java OOP](01_TechGuide/00_Java_OOP_Fundamentals.md), [§00C CS](01_TechGuide/00_Computer_Science_Fundamentals.md) | CRITICAL |
| 3-4 | SQL fundamentals, Web/HTTP basics | [§35 SQL](01_TechGuide/35_SQL_Fundamentals.md), [§00B Web](01_TechGuide/00_Web_Fundamentals.md) | CRITICAL |
| 5-8 | DSA Core (Arrays, Strings, Lists, Stacks) | [02_DSA/StudyGuide.md — Path 1](02_DSA/StudyGuide.md) | CRITICAL |
| 9-10 | Trees, Sorting, Binary Search | [02_DSA/StudyGuide.md — Path 1](02_DSA/StudyGuide.md) | HIGH |
| 11-12 | Spring Boot Basics (DI, REST) | [§02 Spring Boot](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Sections 2.3-2.4) | CRITICAL |
| 13-14 | REST API design, Angular basics | [§04 API Design](01_TechGuide/04_API_Design_REST.md), [§08 Angular](01_TechGuide/08_Angular_Frontend_Engineering.md) (basics) | HIGH |
| 15-16 | Basic DP, Greedy + interview playbook | [02_DSA Path 1](02_DSA/StudyGuide.md), [InterviewPlaybook.md](00_Interview_Prep/Core/InterviewPlaybook.md) | HIGH |

**Key outcome**: Solve 80% of LeetCode Easy problems. Explain basic Spring Boot and REST.

---

### Mid-Level SDE — Weeks 1-16

> **ROADMAP level**: [SDE2](ROADMAP.md#sde2)

You know the basics. Now build depth and pattern recognition.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Java Memory Model, Concurrency | [§02 Spring Boot](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Sections 2.1-2.2) | CRITICAL |
| 3-4 | Design Patterns (SOLID, GoF) | [§03 Design Patterns](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md), [03_CodingPatterns/](03_CodingPatterns/) | CRITICAL |
| 5-8 | DSA Medium Problems (Trees, Graphs, DP) | [02_DSA/StudyGuide.md — Path 2](02_DSA/StudyGuide.md) | CRITICAL |
| 9-10 | Database Indexing, SQL Optimization | [§05 Database](01_TechGuide/05_Database_Performance_Tuning.md) | HIGH |
| 11-12 | Spring Boot Security (JWT, OAuth2) | [§02 Spring Boot](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Section 2.4) | HIGH |
| 13-14 | Angular State Management, RxJS | [§08 Angular](01_TechGuide/08_Angular_Frontend_Engineering.md) (Sections 8.2-8.3) | HIGH |
| 15-16 | Code Quality, Testing | [CodeQuality.md](00_Interview_Prep/Core/CodeQuality.md), [§09 Testing](01_TechGuide/09_Testing_Strategies.md) | HIGH |

**Key outcome**: Solve 70% of LeetCode Medium problems. Design clean APIs with proper patterns.

---

### Senior SDE — Weeks 1-12

> **ROADMAP level**: [Senior SDE](ROADMAP.md#senior-sde)

You build production systems. Now learn to design them at scale.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Microservices (Circuit Breaker, SAGA, Kafka) | [§06 Microservices](01_TechGuide/06_Microservices_Distributed_Systems.md) | CRITICAL |
| 3-4 | System Design Concepts (CAP, Scaling, LB) | [§07 System Design](01_TechGuide/07_System_Design.md) | CRITICAL |
| 5-6 | HLD Practice (URL Shortener, Chat System) | [04_SystemDesign/02_HighLevelDesign/](04_SystemDesign/02_HighLevelDesign/) | CRITICAL |
| 7-8 | LLD Practice (Parking Lot, Elevator) | [04_SystemDesign/01_LowLevelDesign/](04_SystemDesign/01_LowLevelDesign/) | HIGH |
| 9-10 | DB Scaling (Sharding, Caching, Redis) | [§05 Database](01_TechGuide/05_Database_Performance_Tuning.md) (Sections 5.2-5.3), then [§28 Redis](01_TechGuide/28_Redis_Distributed_Caching.md) | HIGH |
| 11-12 | DSA Hard Problems + Mock Interviews | [02_DSA/StudyGuide.md — Path 3](02_DSA/StudyGuide.md) | CRITICAL |

**Key outcome**: Design scalable distributed systems. Solve Hard DSA problems. Explain trade-offs.

---

### Tech Lead / Staff — Weeks 1-12

> **ROADMAP levels**: [Tech Lead](ROADMAP.md#tech-lead) and [Staff Engineer](ROADMAP.md#staff-engineer)

You own technical direction. Now master everything and lead.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1 | Modern Java (Virtual Threads, Records, Sealed) | [§01 Modern Java](01_TechGuide/01_Modern_Java_Features.md) | CRITICAL |
| 2 | Java Internals Deep Dive (GC, Memory, Concurrency) | [§02 Spring Boot](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) | CRITICAL |
| 3 | Clean Architecture + DDD + Design Patterns | [§03 Design Patterns](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md) | HIGH |
| 4 | API Design + REST Best Practices | [§04 API Design](01_TechGuide/04_API_Design_REST.md) | HIGH |
| 5 | Database Optimization + Caching Patterns | [§05 Database](01_TechGuide/05_Database_Performance_Tuning.md) | CRITICAL |
| 6 | Microservices Resilience + Data Consistency | [§06 Microservices](01_TechGuide/06_Microservices_Distributed_Systems.md) | CRITICAL |
| 7 | System Design Framework + Practice | [§07 System Design](01_TechGuide/07_System_Design.md), [04_SystemDesign/](04_SystemDesign/) | CRITICAL |
| 8 | Angular Signals, State, Performance | [§08 Angular](01_TechGuide/08_Angular_Frontend_Engineering.md) | HIGH |
| 9 | DevOps + CI/CD + Kubernetes | [§10 DevOps](01_TechGuide/10_DevOps_CICD_Docker.md), [§13 Trends](01_TechGuide/13_Modern_Trends_2026.md) | HIGH |
| 10 | Observability (Logging, Metrics, Tracing) | [§11 Observability](01_TechGuide/11_Observability.md) | HIGH |
| 11 | **AI/ML Complete** (LLM, RAG, Spring AI, Agents, MLOps) | [05_AI/](05_AI/) (all 11 sections) | **CRITICAL** |
| 12 | Leadership, Behavioral, Story Bank | [§14 Leadership](01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) | CRITICAL |

**Key outcome**: Pass any technical, system design, and behavioral round at FAANG-level companies.

---

## Section-by-Section Summary

### TechGuide — What Each Section Covers (read §00 first for Associate, then 01 → 38)

| # | Section | Key Topics | Relevant From |
|---|---------|-----------|---------------|
| 00A | [Java OOP Fundamentals](01_TechGuide/00_Java_OOP_Fundamentals.md) | OOP pillars, equals/hashCode, exceptions, collections intro | All Levels |
| 00B | [Web & HTTP Fundamentals](01_TechGuide/00_Web_Fundamentals.md) | HTTP methods, status codes, JWT/cookies, REST preview | All Levels |
| 00C | [CS Fundamentals](01_TechGuide/00_Computer_Science_Fundamentals.md) | Big-O, data structures, recursion, BFS/DFS, DP intuition | All Levels |
| 01 | [Modern Java (17-21)](01_TechGuide/01_Modern_Java_Features.md) | Records, Sealed Classes, Virtual Threads, Pattern Matching | Mid-Level+ |
| 02 | [Java & Spring Boot Internals](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) | Memory Model, GC (G1GC/ZGC), Concurrency, Bean Lifecycle, OAuth2/JWT | Mid-Level+ |
| 03 | [Design Patterns & Architecture](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md) | SOLID, GoF Patterns, CQRS, Clean Architecture, DDD | Mid-Level+ |
| 04 | [API Design & REST](01_TechGuide/04_API_Design_REST.md) | RESTful Design, RFC 7807, Pagination, Versioning, HATEOAS | Mid-Level+ |
| 05 | [Database & Performance Tuning](01_TechGuide/05_Database_Performance_Tuning.md) | Indexing, EXPLAIN ANALYZE, N+1, Sharding, Caching, Redis | Mid-Level+ |
| 06 | [Microservices & Distributed Systems](01_TechGuide/06_Microservices_Distributed_Systems.md) | Circuit Breaker, SAGA, Kafka, gRPC, Outbox Pattern | Senior+ |
| 07 | [System Design](01_TechGuide/07_System_Design.md) | CAP, Scaling, Load Balancers, API Gateway, Strangler Fig | Senior+ |
| 08 | [Angular & Frontend](01_TechGuide/08_Angular_Frontend_Engineering.md) | Signals, Standalone Components, NgRx Signal Store, RxJS, Guards | All Levels |
| 09 | [Testing Strategies](01_TechGuide/09_Testing_Strategies.md) | JUnit 5, Testcontainers, WireMock, Contract Testing | Mid-Level+ |
| 10 | [DevOps & CI/CD](01_TechGuide/10_DevOps_CICD_Docker.md) | Docker, GitHub Actions, Blue-Green, GitOps, Terraform | Senior+ |
| 11 | [Observability](01_TechGuide/11_Observability.md) | Structured Logging, Prometheus, OpenTelemetry, SLI/SLO | Senior+ |
| 12 | [Security, OWASP & Cloud](01_TechGuide/12_Security_OWASP_Cloud.md) | OWASP Top 10, JWT, CORS, STRIDE, AWS Services, IAM, VPC | Senior+ |
| 13 | [Modern Trends 2026](01_TechGuide/13_Modern_Trends_2026.md) | RAG/AI with Spring AI, Kubernetes for Engineers | Lead |
| 14 | [Leadership & Behavioral](01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) | STAR Method, Story Bank, System Design Framework, ADRs | Lead |
| 15 | [Java Collections & Concurrency](01_TechGuide/15_Java_Collections_Concurrency_DeepDive.md) | HashMap internals, ConcurrentHashMap, Locks, Streams, CompletableFuture | Mid-Level+ |
| 16 | [Spring Ecosystem Deep Dive](01_TechGuide/16_Spring_Ecosystem_DeepDive.md) | JPA Specifications, WebFlux, Cloud Gateway, Batch, AOP, Events | Mid-Level+ |
| 17 | [Networking & Protocols](01_TechGuide/17_Networking_Protocols.md) | HTTP/2/3, TLS, DNS, WebSocket, gRPC, CDN, Connection Pooling | Mid-Level+ |
| 18 | [Performance Engineering & JVM](01_TechGuide/18_Performance_Engineering_JVM.md) | JVM tuning, G1/ZGC, JFR, flame graphs, JMH, SLI/SLO | Senior+ |
| 19 | [Event-Driven Architecture](01_TechGuide/19_Event_Driven_Architecture.md) | Kafka deep dive, Event Sourcing, CQRS, Saga, CDC, Schema Evolution | Senior+ |
| 20 | [Technical Leadership](01_TechGuide/20_Technical_Leadership_Architecture.md) | ADRs, estimation, post-mortems, code review, mentorship, tech debt | Lead |
| 21 | [GraphQL & APIs](01_TechGuide/21_GraphQL_and_Alternative_APIs.md) | GraphQL vs REST, Spring GraphQL, DataLoader, federation | Mid-Level+ |
| 22 | [Kotlin for Java Devs](01_TechGuide/22_Kotlin_for_Java_Developers.md) | Null safety, coroutines, Spring Boot Kotlin, interop | Mid-Level+ |
| 23 | [SRE & Reliability](01_TechGuide/23_SRE_Reliability_Engineering.md) | SLI/SLO, error budgets, incidents, post-mortems, toil | Senior+ |
| 24 | [Platform Engineering](01_TechGuide/24_Platform_Engineering_IDP.md) | IDP, golden paths, Backstage, GitOps, DORA | Senior+ |
| 25 | [Data Engineering](01_TechGuide/25_Data_Engineering_Fundamentals.md) | ETL/ELT, Kafka, Spark, CDC, lakehouse, dbt | Senior+ |

### Databases & Caching (Deep Dives)

| # | Section | Key Topics | Relevant From |
|---|---------|-----------|---------------|
| 05 | [Database Performance](01_TechGuide/05_Database_Performance_Tuning.md) | Indexing, N+1, sharding, Redis cache-aside | Mid-Level+ |
| 26 | [PostgreSQL Deep Dive](01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md) | MVCC, VACUUM, replication, partitioning, pgvector | Mid-Level+ |
| 27 | [NoSQL Guide](01_TechGuide/27_NoSQL_Databases_Guide.md) | MongoDB, DynamoDB, Cassandra, CAP | Senior+ |
| 28 | [Redis Deep Dive](01_TechGuide/28_Redis_Distributed_Caching.md) | Sorted sets, cluster, stampede, distributed locks | Mid-Level+ |
| 35 | [SQL Fundamentals](01_TechGuide/35_SQL_Fundamentals.md) | JOINs, window functions, CTEs, normalization | All Levels |

### Networking, Cloud & Infrastructure

| # | Section | Key Topics | Relevant From |
|---|---------|-----------|---------------|
| 17 | [Networking Protocols](01_TechGuide/17_Networking_Protocols.md) | HTTP/2/3, TLS, DNS, WebSocket, gRPC | Mid-Level+ |
| 29 | [Advanced Networking](01_TechGuide/29_Advanced_Networking_Infrastructure.md) | VPC, NAT, L4/L7 LB, service mesh, zero trust | Senior+ |
| 30 | [Kubernetes](01_TechGuide/30_Kubernetes_Deep_Dive.md) | Pods, Ingress, HPA, probes, NetworkPolicy | Senior+ |
| 31 | [Cloud Computing](01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md) | AWS/GCP/Azure, IAM, S3, Lambda, cost optimization | Mid-Level+ |
| 32 | [OS & Linux](01_TechGuide/32_Operating_Systems_and_Linux.md) | Processes, debugging, epoll, OOM, ulimit | All Levels |

### Engineering Practice & Search

| # | Section | Key Topics | Relevant From |
|---|---------|-----------|---------------|
| 33 | [Git & Workflow](01_TechGuide/33_Git_Version_Control_Workflow.md) | Trunk-based, merge vs rebase, PR best practices | All Levels |
| 34 | [Search & Elasticsearch](01_TechGuide/34_Search_Engines_Elasticsearch.md) | Inverted index, BM25, analyzers, ELK | Senior+ |
| 36 | [Polyglot Python & Go](01_TechGuide/36_Polyglot_Interview_Python_and_Go.md) | Python/Go DSA syntax, goroutines, GIL | Mid-Level+ |
| 37 | [TypeScript & Frontend Landscape](01_TechGuide/37_TypeScript_and_Frontend_Landscape.md) | TS types, React vs Angular, SSR, browser security | All Levels |
| 38 | [Compliance & Regulated Systems](01_TechGuide/38_Compliance_and_Regulated_Systems.md) | GDPR, PCI-DSS, audit trails, PII handling | Senior+ |

### AI — What Each Section Covers

| # | Section | Key Topics | Relevant From |
|---|---------|-----------|---------------|
| 01 | [AI Fundamentals](05_AI/01_AI_Fundamentals.md) | Transformers, Tokens, Embeddings, LLM landscape, model comparison | All Levels |
| 02 | [LLM & Prompt Engineering](05_AI/02_LLM_and_Prompt_Engineering.md) | Few-shot, CoT, ReAct, structured output, prompt injection defense | All Levels |
| 03 | [RAG Architecture](05_AI/03_RAG_Architecture.md) | Ingestion, chunking strategies, retrieval, re-ranking, evaluation | Mid-Level+ |
| 04 | [Vector Databases](05_AI/04_Vector_Databases_Embeddings.md) | pgvector, Pinecone, HNSW, IVF, hybrid search, multi-tenancy | Mid-Level+ |
| 05 | [Spring AI](05_AI/05_Spring_AI.md) | ChatClient, EmbeddingModel, VectorStore, Function Calling, Advisors | Mid-Level+ |
| 06 | [AI Agents](05_AI/06_AI_Agents_and_Workflows.md) | ReAct, MCP, multi-agent, plan-and-execute, safety controls | Senior+ |
| 07 | [AI Dev Tools](05_AI/07_AI_Powered_Dev_Tools.md) | Copilot, Cursor, CodeRabbit, .cursorrules, AI in SDLC | All Levels |
| 08 | [AI in Frontend](05_AI/08_AI_in_Frontend.md) | Angular streaming chat, SSE, markdown rendering, AI UX patterns | Mid-Level+ |
| 09 | [MLOps](05_AI/09_MLOps_AI_in_Production.md) | LLM routing, cost management, A/B testing, quality monitoring | Senior+ |
| 10 | [AI System Design](05_AI/10_AI_System_Design.md) | AI customer support, knowledge base, code review, recommendation | Lead |
| 11 | [AI Ethics & Governance](05_AI/11_AI_Ethics_Safety_Governance.md) | Hallucination prevention, bias, PII, prompt injection, EU AI Act | Lead |

### Supporting Files

| File | Purpose | When to Use |
|------|---------|-------------|
| [00_Interview_Prep/README.md](00_Interview_Prep/README.md) | Hub for career, mocks, level guides, Principal strategy | Interview process & meta prep |
| [MASTER_INDEX.md](MASTER_INDEX.md) | Topic → file → round → level — full navigation | Finding any topic quickly |
| [InterviewPlaybook.md](00_Interview_Prep/Core/InterviewPlaybook.md) | 12-week schedule, STAR, coding/SD round protocols | Structured prep plan |
| [InterviewQuestions.md](00_Interview_Prep/Core/InterviewQuestions.md) | Top 218 interview questions with one-line answers + section references | Night before interviews |
| [CodeQuality.md](00_Interview_Prep/Core/CodeQuality.md) | SOLID, Clean Code, naming conventions, anti-patterns, code review checklist | Before any coding round |
| [CrossReferences.md](00_Interview_Prep/Core/CrossReferences.md) | How DSA concepts map to System Design (Arrays → Caches, Trees → DBs, Graphs → Networks) | When connecting DSA to design |
| [SelfAssessment.md](00_Interview_Prep/Core/SelfAssessment.md) | Topic-by-topic readiness checklists with confidence scoring | Weekly self-check |
| [Companies.md](00_Interview_Prep/Core/Companies.md) | Google, Amazon, Meta, Microsoft, Flipkart, Apple interview guides | Before company-specific prep |
| [02_DSA/StudyGuide.md](02_DSA/StudyGuide.md) | Learning paths by career level + difficulty progression per category | When planning DSA study |

---

## Quick Reference: Interview Keywords by Topic

Use these keywords in your answers to signal depth to the interviewer.

| Topic | Must-Say Keywords |
|-------|-------------------|
| **Java Memory** | Weak Generational Hypothesis, Escape Analysis, TLAB, Dominator Tree, GC Roots, Retained Heap |
| **Garbage Collection** | Concurrent Marking, Colored Pointers (ZGC), Stop-The-World, Region-based, G1GC pause target |
| **Concurrency** | Fan-out/Fan-in, CAS (Compare-And-Swap), Happens-Before, Work-Stealing, ForkJoinPool |
| **Spring Boot** | IoC, BeanPostProcessor, Conditional Beans, CGLIB Proxy, Component Scanning, AutoConfiguration |
| **Security** | OWASP Top 10, PKCE, IDOR, STRIDE, JWT Algorithm Confusion, CORS, CSP, HSTS, SSRF, BCrypt, PCI DSS |
| **Cloud / AWS** | VPC, Security Groups, IAM Least Privilege, EKS, RDS, S3, SQS, SNS, ALB, CloudWatch, Secrets Manager |
| **Resilience** | Cascading Failure, Fail Fast, Exponential Backoff + Jitter, Blast Radius, Circuit Breaker states |
| **SAGA Pattern** | Compensating Transaction, Orchestration vs Choreography, Outbox Pattern, Idempotency Key |
| **Database** | Covering Index, Partition Pruning, Leftmost Prefix Rule, N+1 Problem, MVCC, WAL, VACUUM |
| **NoSQL** | Partition Key, Hot Partition, GSI, LSM Tree, Tunable Consistency, QUORUM |
| **Redis** | Cache-Aside, Cache Stampede, Sorted Set, Hash Slots, SET NX, AOF |
| **Networking** | VPC, NAT, Security Group, L4/L7 LB, mTLS, Zero Trust, GeoDNS |
| **Kubernetes** | Reconciliation Loop, Readiness Probe, HPA, Ingress, NetworkPolicy, etcd |
| **Cloud** | IAM Least Privilege, S3 Presigned URL, SQS vs SNS, Shared Responsibility |
| **Linux** | Context Switch, epoll, OOM Killer, ulimit, File Descriptor |
| **SQL** | Window Functions, CTE, LEFT JOIN, HAVING vs WHERE, Covering Index |
| **Elasticsearch** | Inverted Index, BM25, Shard, Analyzer, Bool Query |
| **Caching** | Cache-Aside, Cache Stampede, TTL, Eviction Policy, Near-Cache, Write-Behind |
| **System Design** | CAP Theorem, PACELC, Consistent Hashing, Strangler Fig, Anti-Corruption Layer |
| **Kubernetes** | Desired State, Reconciliation Loop, Readiness Probe, HPA, Pod Disruption Budget, Graceful Shutdown |
| **AI / RAG** | Vector Embedding, Cosine Similarity, Semantic Search, Chunking, Context Window, HNSW Index, Spring AI ChatClient, Function Calling, MCP, AI Agents, ReAct, Prompt Injection, PII Redaction, Hallucination Prevention, Token Cost, Semantic Cache |
| **Angular** | OnPush, Signals, Lazy Loading, NgRx Signal Store, Zone.js, Change Detection, RxJS operators |
| **Docker / CI-CD** | Multi-stage Build, Layer Caching, GitOps, Canary, Feature Flags, Trunk-based Development |
| **Observability** | Three Pillars, RED Method, USE Method, SLI/SLO/SLA, OpenTelemetry, Tail Sampling |
| **API Design** | RFC 7807, Cursor Pagination, Idempotency Key, HATEOAS, OpenAPI, Versioning, Sunset Header |
| **Architecture** | Clean Architecture, Hexagonal, CQRS, Event Sourcing, Bounded Context, DIP, Ports and Adapters |
| **Leadership** | STAR Method, Ownership, Data-driven, Trade-offs, ADR, Error Budget, Disagree and Commit |
| **Modern Java** | Records, Sealed Classes, Pattern Matching, Virtual Threads, Structured Concurrency, Gatherers |
| **Testing** | Testing Pyramid, Testcontainers, WireMock, Contract Testing, Mutation Testing, Arrange-Act-Assert |

---

## The Engineer's Mindset — All Levels

### Junior Level Thinking
- "Does my code work correctly for the given examples?"
- "Can I explain what each line does?"

### Mid-Level Thinking
- "What is the time/space complexity?"
- "Are there edge cases I am missing?"
- "Is there a more efficient approach?"

### Senior Level Thinking
- "What are the trade-offs between approaches?"
- "How does this scale? What happens at 10x traffic?"
- "What are the failure modes?"

### Lead Level Thinking
- "Why this design over alternatives? Can I justify it?"
- "How does the team maintain this? What is the operational cost?"
- "What monitoring and alerting do we need?"
- "How does this evolve over the next 2 years?"
- "Would a new team member understand why we made this decision?"

---

## Daily Practice Routine (Any Level)

| Time | Activity | Duration |
|------|----------|----------|
| Morning | DSA problem (timed: Easy 20 min, Medium 35 min, Hard 45 min) | 30-60 min |
| Afternoon | Study one TechGuide section | 60-90 min |
| Evening | Review keywords + practice explaining one concept aloud | 30 min |
| Weekend | System Design mock (45 min) + Behavioral story practice (30 min) | 75 min |

---

**This repository is your complete engineering knowledge base. Study it systematically, practice consistently, and you will be ready — at every level of your career.**
