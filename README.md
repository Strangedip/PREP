# Software Engineer Interview Preparation — Associate to Lead

> **Your Career Level Journey**: Associate SDE → Mid-Level SDE → Senior SDE → Lead Software Engineer
> **Tech Stack**: Java 17-21, Spring Boot 3.x, Angular 17+, Microservices, Kubernetes, Cloud-Native
> **Purpose**: One repo, all levels. Study it when you need to crack any engineering role.

---

## What This Repository Covers

This is your **lifetime reference** for software engineering interviews and daily revision. It is structured so that whether you are preparing for your first job or your tenth, you will find what you need.

| Career Level | What You Need | Where to Find It |
|-------------|---------------|-----------------|
| **Associate / Junior SDE** | Core Java OOP, CS basics, SQL, basic DSA, HTTP/REST | §00 Fundamentals, §35 SQL, 02_DSA Path 1 |
| **Mid-Level SDE** | Deep Java, Design Patterns, databases, Medium DSA | 01_TechGuide §01-05 + 02_DSA Path 2 |
| **Senior SDE** | Microservices, System Design, testing, Angular depth | 01_TechGuide §06-09 + 02_DSA Path 3 + 04_SystemDesign |
| **Lead / Staff Engineer** | All of the above + Leadership, DevOps, Observability, AI/RAG, Agents, MLOps | Everything in this repo (all 5 sections) |

---

## Repository Structure

```
PREP/
│
├── README.md                          ← YOU ARE HERE (Master Guide)
├── MASTER_INDEX.md                    ← Topic → file → round → level (full navigation)
├── InterviewPlaybook.md               ← 12-week schedule, STAR, round protocols
│
├── 01_TechGuide/                      ← Core Knowledge Base (start here — learn concepts first)
│   ├── 00_TableOfContents.md          — Full index with career-level indicators
│   ├── 00_Java_OOP_Fundamentals.md    — OOP, collections intro, exceptions (ASSOC start)
│   ├── 00_Web_Fundamentals.md         — HTTP, REST, auth basics (ASSOC)
│   ├── 00_Computer_Science_Fundamentals.md — Big-O, DS overview, recursion (ASSOC)
│   ├── 01_Modern_Java_Features.md     — Java 17-21: Records, Sealed, Virtual Threads
│   ├── 02_Advanced_SpringBoot_Java_Internals.md — GC, Concurrency, Bean Lifecycle
│   ├── 03_Design_Patterns_SOLID_CleanArch.md — SOLID, GoF, Clean Architecture, DDD
│   ├── 04_API_Design_REST.md          — RESTful APIs, Versioning, Pagination, HATEOAS
│   ├── 05_Database_Performance_Tuning.md — Indexing, Sharding, Caching, Redis
│   ├── 06_Microservices_Distributed_Systems.md — Resilience, SAGA, Kafka, gRPC
│   ├── 07_System_Design.md            — Scalability, CAP, Load Balancing concepts
│   ├── 08_Angular_Frontend_Engineering.md — Signals, Standalone, RxJS, State
│   ├── 09_Testing_Strategies.md       — Test Pyramid, Testcontainers, WireMock
│   ├── 10_DevOps_CICD_Docker.md       — Docker, CI/CD, GitOps, Blue-Green, Canary
│   ├── 11_Observability.md            — Logs, Metrics, Traces, OpenTelemetry
│   ├── 12_Security_OWASP_Cloud.md     — OWASP Top 10, AWS, IAM, VPC
│   ├── 13_Modern_Trends_2026.md       — Latest industry trends
│   ├── 14_Leadership_Behavioral_SystemDesign.md — STAR, ADRs, Estimation
│   ├── 15_Java_Collections_Concurrency_DeepDive.md — HashMap, ConcurrentHashMap, Locks, Streams
│   ├── 16_Spring_Ecosystem_DeepDive.md — JPA, WebFlux, Cloud, Batch, AOP, Events
│   ├── 17_Networking_Protocols.md      — HTTP/2/3, TLS, DNS, WebSocket, gRPC, CDN
│   ├── 18_Performance_Engineering_JVM.md — JVM tuning, GC, JFR, profiling, JMH
│   ├── 19_Event_Driven_Architecture.md — Kafka deep dive, Event Sourcing, CQRS, Saga, CDC
│   ├── 20_Technical_Leadership_Architecture.md — ADRs, estimation, post-mortems, mentorship
│   ├── 21_GraphQL_and_Alternative_APIs.md  — GraphQL, Spring GraphQL, N+1, federation
│   ├── 22_Kotlin_for_Java_Developers.md    — Coroutines vs virtual threads, Spring Kotlin
│   ├── 23_SRE_Reliability_Engineering.md   — SLI/SLO, error budgets, incident response
│   ├── 24_Platform_Engineering_IDP.md      — Golden paths, Backstage, GitOps, DORA metrics
│   ├── 25_Data_Engineering_Fundamentals.md — Batch/stream, CDC, lakehouse, dbt, Spark/Flink
│   ├── 26_PostgreSQL_Relational_DB_Deep_Dive.md — MVCC, VACUUM, replication, pgvector
│   ├── 27_NoSQL_Databases_Guide.md         — MongoDB, DynamoDB, Cassandra
│   ├── 28_Redis_Distributed_Caching.md     — Data structures, cluster, stampede, locks
│   ├── 29_Advanced_Networking_Infrastructure.md — VPC, LB L4/L7, DNS, zero trust
│   ├── 30_Kubernetes_Deep_Dive.md          — Pods, Ingress, HPA, probes, security
│   ├── 31_Cloud_Computing_AWS_GCP_Azure.md   — IAM, S3, Lambda, Well-Architected
│   ├── 32_Operating_Systems_and_Linux.md     — Processes, debugging, epoll, OOM
│   ├── 33_Git_Version_Control_Workflow.md    — Trunk-based, merge vs rebase, PRs
│   ├── 34_Search_Engines_Elasticsearch.md      — Inverted index, BM25, ELK
│   ├── 35_SQL_Fundamentals.md                — JOINs, window functions, CTEs
│   ├── 36_Polyglot_Interview_Python_and_Go.md — Python/Go DSA for polyglot rounds
│   ├── 37_TypeScript_and_Frontend_Landscape.md — TS, React vs Angular breadth
│   └── 38_Compliance_and_Regulated_Systems.md  — GDPR, PCI, audit trails
│
├── 02_DSA/                            ← 100+ Coding Problems (study top to bottom)
│   ├── README.md                      — DSA overview and problem index
│   ├── StudyGuide.md                  — Learning paths by career level
│   ├── Tier3_Differentiators.md       — Hard patterns for Senior/Staff loops
│   ├── 01_Arrays_Matrix/              — 16 problems (Two Sum, Merge Intervals, etc.)
│   ├── 02_Strings/                    — 9 problems (Anagrams, Palindromes, KMP, etc.)
│   ├── 03_Sorting_Searching/          — 4 problems (Merge Sort, Binary Search, Koko Eating Bananas)
│   ├── 04_Sliding_Window_Two_Pointers/ — 5+ problems (Min Window, Max Subarray, etc.)
│   ├── 05_Linked_Lists/               — 8 problems (Reverse, Merge, Cycle, etc.)
│   ├── 06_Stacks_Queues/              — 6 problems (Valid Parentheses, etc.)
│   ├── 07_Recursion_Divide_Conquer/   — Master Theorem + problems
│   ├── 08_Trees_Binary_Trees/         — 8 problems (Traversal, LCA, Serialize, etc.)
│   ├── 09_Binary_Search_Tree/         — 4 problems (Validate, Kth Smallest, etc.)
│   ├── 10_Heaps_Priority_Queues/      — 4 problems (Top K, Median, Merge K, etc.)
│   ├── 11_Graphs/                     — 7 problems (BFS, DFS, Dijkstra, Union Find)
│   ├── 12_Backtracking/               — 5 problems (N-Queens, Permutations, etc.)
│   ├── 13_Dynamic_Programming/        — 10+ problems (Knapsack, LIS, Edit Distance)
│   ├── 14_Greedy_Algorithms/          — 4 problems (Task Scheduler, Jump Game, etc.)
│   ├── 15_Bit_Manipulation/           — 5 problems (Power of Two, Counting Bits)
│   ├── 16_Math_Algorithms/            — Number Theory (GCD, Primes, Modular Arith)
│   ├── 17_Advanced_Miscellaneous/     — Trie, Segment Tree
│   └── 18_Concurrency_Multithreading/ — Print In Order, Producer-Consumer, RW Lock
│
├── 03_CodingPatterns/                 ← Design Patterns + Algorithmic Patterns
│   ├── README.md                      — Pattern overview and study order
│   ├── 01_Patterns.md                — 25 GoF Design Patterns with Spring Boot examples
│   └── 02_AlgorithmicPatterns.md     — 16 coding interview patterns with templates
│
├── 04_SystemDesign/                   ← LLD + HLD Problems + Templates
│   ├── README.md                      — System design overview and study guide
│   ├── 00_Templates/                  — HLD & LLD interview templates
│   ├── 01_LowLevelDesign/            — 9 OOD problems (ParkingLot, ATM, Food Delivery, etc.)
│   └── 02_HighLevelDesign/           — 18 HLD problems (Chat, Twitter, Web Crawler, YouTube, etc.)
│
├── 05_AI/                             ← AI & Machine Learning for Developers (2026)
│   ├── README.md                      — Study path, career-level guide
│   ├── 01_AI_Fundamentals.md         — Core ML/DL/LLM/Transformer concepts
│   ├── 02_LLM_and_Prompt_Engineering.md — API calls, prompting, guardrails
│   ├── 03_RAG_Architecture.md        — RAG end-to-end with Spring Boot
│   ├── 04_Vector_Databases_Embeddings.md — pgvector, Pinecone, HNSW, similarity search
│   ├── 05_Spring_AI.md               — ChatClient, embedding, function calling, streaming
│   ├── 06_AI_Agents_and_Workflows.md — Agentic AI, MCP, ReAct, multi-agent
│   ├── 07_AI_Powered_Dev_Tools.md    — Copilot, Cursor, CodeRabbit, AI in SDLC
│   ├── 08_AI_in_Frontend.md          — AI-powered Angular UIs, streaming chat
│   ├── 09_MLOps_AI_in_Production.md  — Deploy, monitor, scale, cost-control AI
│   ├── 10_AI_System_Design.md        — AI system design interview problems
│   └── 11_AI_Ethics_Safety_Governance.md — Responsible AI, bias, PII, compliance
│
├── InterviewQuestions.md               ← Top 218 Interview Questions — rapid-fire Q&A
├── CodeQuality.md                     ← SOLID, Clean Code, Code Review Checklists
├── CrossReferences.md                 ← How DSA concepts map to System Design
├── SelfAssessment.md                  ← Topic-by-topic readiness checklists
└── Companies.md                       ← Company-specific interview guides & tips
```

---

## Learning Path by Career Level

### Associate / Junior SDE — Weeks 1-16

You are learning the fundamentals. Focus on understanding, not speed.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Java OOP, CS fundamentals, Big-O | [§00A Java OOP](01_TechGuide/00_Java_OOP_Fundamentals.md), [§00C CS](01_TechGuide/00_Computer_Science_Fundamentals.md) | CRITICAL |
| 3-4 | SQL fundamentals, Web/HTTP basics | [§35 SQL](01_TechGuide/35_SQL_Fundamentals.md), [§00B Web](01_TechGuide/00_Web_Fundamentals.md) | CRITICAL |
| 5-8 | DSA Core (Arrays, Strings, Lists, Stacks) | [02_DSA/StudyGuide.md — Path 1](02_DSA/StudyGuide.md) | CRITICAL |
| 9-10 | Trees, Sorting, Binary Search | [02_DSA/StudyGuide.md — Path 1](02_DSA/StudyGuide.md) | HIGH |
| 11-12 | Spring Boot Basics (DI, REST) | [§02 Spring Boot](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Sections 2.3-2.4) | CRITICAL |
| 13-14 | REST API design, Angular basics | [§04 API Design](01_TechGuide/04_API_Design_REST.md), [§08 Angular](01_TechGuide/08_Angular_Frontend_Engineering.md) (basics) | HIGH |
| 15-16 | Basic DP, Greedy + interview playbook | [02_DSA Path 1](02_DSA/StudyGuide.md), [InterviewPlaybook.md](InterviewPlaybook.md) | HIGH |

**Key outcome**: Solve 80% of LeetCode Easy problems. Explain basic Spring Boot and REST.

---

### Mid-Level SDE — Weeks 1-16

You know the basics. Now build depth and pattern recognition.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Java Memory Model, Concurrency | [§02 Spring Boot](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Sections 2.1-2.2) | CRITICAL |
| 3-4 | Design Patterns (SOLID, GoF) | [§03 Design Patterns](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md), [03_CodingPatterns/](03_CodingPatterns/) | CRITICAL |
| 5-8 | DSA Medium Problems (Trees, Graphs, DP) | [02_DSA/StudyGuide.md — Path 2](02_DSA/StudyGuide.md) | CRITICAL |
| 9-10 | Database Indexing, SQL Optimization | [§05 Database](01_TechGuide/05_Database_Performance_Tuning.md) | HIGH |
| 11-12 | Spring Boot Security (JWT, OAuth2) | [§02 Spring Boot](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) (Section 2.4) | HIGH |
| 13-14 | Angular State Management, RxJS | [§08 Angular](01_TechGuide/08_Angular_Frontend_Engineering.md) (Sections 8.2-8.3) | HIGH |
| 15-16 | Code Quality, Testing | [CodeQuality.md](CodeQuality.md), [§09 Testing](01_TechGuide/09_Testing_Strategies.md) | HIGH |

**Key outcome**: Solve 70% of LeetCode Medium problems. Design clean APIs with proper patterns.

---

### Senior SDE — Weeks 1-12

You build production systems. Now learn to design them at scale.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Microservices (Circuit Breaker, SAGA, Kafka) | [§06 Microservices](01_TechGuide/06_Microservices_Distributed_Systems.md) | CRITICAL |
| 3-4 | System Design Concepts (CAP, Scaling, LB) | [§07 System Design](01_TechGuide/07_System_Design.md) | CRITICAL |
| 5-6 | HLD Practice (URL Shortener, Chat System) | [04_SystemDesign/02_HighLevelDesign/](04_SystemDesign/02_HighLevelDesign/) | CRITICAL |
| 7-8 | LLD Practice (Parking Lot, Elevator) | [04_SystemDesign/01_LowLevelDesign/](04_SystemDesign/01_LowLevelDesign/) | HIGH |
| 9-10 | DB Scaling (Sharding, Caching, Redis) | [§05 Database](01_TechGuide/05_Database_Performance_Tuning.md) (Sections 5.2-5.3) | HIGH |
| 11-12 | DSA Hard Problems + Mock Interviews | [02_DSA/StudyGuide.md — Path 3](02_DSA/StudyGuide.md) | CRITICAL |

**Key outcome**: Design scalable distributed systems. Solve Hard DSA problems. Explain trade-offs.

---

### Lead / Staff Engineer — Weeks 1-12

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
| [MASTER_INDEX.md](MASTER_INDEX.md) | Topic → file → round → level — full navigation | Finding any topic quickly |
| [InterviewPlaybook.md](InterviewPlaybook.md) | 12-week schedule, STAR, coding/SD round protocols | Structured prep plan |
| [InterviewQuestions.md](InterviewQuestions.md) | Top 218 interview questions with one-line answers + section references | Night before interviews |
| [CodeQuality.md](CodeQuality.md) | SOLID, Clean Code, naming conventions, anti-patterns, code review checklist | Before any coding round |
| [CrossReferences.md](CrossReferences.md) | How DSA concepts map to System Design (Arrays → Caches, Trees → DBs, Graphs → Networks) | When connecting DSA to design |
| [SelfAssessment.md](SelfAssessment.md) | Topic-by-topic readiness checklists with confidence scoring | Weekly self-check |
| [Companies.md](Companies.md) | Google, Amazon, Meta, Microsoft, Flipkart, Apple interview guides | Before company-specific prep |
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
