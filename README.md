# Software Engineer Interview Preparation — Associate to Lead

> **Your Career Level Journey**: Associate SDE → Mid-Level SDE → Senior SDE → Lead Software Engineer
> **Tech Stack**: Java 17-21, Spring Boot 3.x, Angular 17+, Microservices, Kubernetes, Cloud-Native
> **Purpose**: One repo, all levels. Study it when you need to crack any engineering role.

---

## What This Repository Covers

This is your **lifetime reference** for software engineering interviews and daily revision. It is structured so that whether you are preparing for your first job or your tenth, you will find what you need.

| Career Level | What You Need | Where to Find It |
|-------------|---------------|-----------------|
| **Associate / Junior SDE** | Core Java, basic DSA, simple Spring Boot, HTML/CSS/JS | TechGuide Sections 1, 7, 6 (basics) + DSA Path 1 |
| **Mid-Level SDE** | Deep Java, Design Patterns, databases, Medium DSA | TechGuide Sections 1-3, 7, 12 + DSA Path 2 |
| **Senior SDE** | Microservices, System Design, testing, Angular depth | TechGuide Sections 2-4, 6, 8, 11 + DSA Path 3 + SystemDesign |
| **Lead / Staff Engineer** | All of the above + Leadership, DevOps, Observability, AI/RAG, Agents, MLOps | Everything in this repo (including AI/) |

---

## Repository Structure

```
PREP/
│
├── README.md                       ← YOU ARE HERE (Master Guide)
│
├── AI/                             ← AI & Machine Learning for Developers (2026)
│   ├── README.md                   — Study path, career-level guide
│   ├── 01_AI_Fundamentals.md       — Core ML/DL/LLM/Transformer concepts
│   ├── 02_LLM_and_Prompt_Engineering.md — API calls, prompting, guardrails
│   ├── 03_RAG_Architecture.md      — RAG end-to-end with Spring Boot
│   ├── 04_Vector_Databases_Embeddings.md — pgvector, Pinecone, HNSW, similarity search
│   ├── 05_Spring_AI.md             — ChatClient, embedding, function calling, streaming
│   ├── 06_AI_Agents_and_Workflows.md — Agentic AI, MCP, ReAct, multi-agent
│   ├── 07_AI_Powered_Dev_Tools.md  — Copilot, Cursor, CodeRabbit, AI in SDLC
│   ├── 08_AI_in_Frontend.md        — AI-powered Angular UIs, streaming chat
│   ├── 09_MLOps_AI_in_Production.md — Deploy, monitor, scale, cost-control AI
│   ├── 10_AI_System_Design.md      — AI system design interview problems
│   └── 11_AI_Ethics_Safety_Governance.md — Responsible AI, bias, PII, compliance
│
├── TechGuide/                      ← Core Knowledge Base (14 Deep-Dive Sections)
│   ├── 00_TableOfContents.md       — Full index with career-level indicators
│   ├── 01_Advanced_SpringBoot_Java_Internals.md
│   ├── 02_Microservices_Distributed_Systems.md
│   ├── 03_Database_Performance_Tuning.md
│   ├── 04_System_Design.md
│   ├── 05_Modern_Trends_2026.md
│   ├── 06_Angular_Frontend_Engineering.md
│   ├── 07_Modern_Java_Features.md
│   ├── 08_Testing_Strategies.md
│   ├── 09_DevOps_CICD_Docker.md
│   ├── 10_Observability.md
│   ├── 11_API_Design_REST.md
│   ├── 12_Design_Patterns_SOLID_CleanArch.md
│   ├── 13_Leadership_Behavioral_SystemDesign.md
│   └── 14_Security_OWASP_Cloud.md
│
├── DSA/                            ← 100+ Coding Problems with Solutions
│   ├── 01_Arrays_Matrix/           — 16 problems
│   ├── 02_Strings/                 — 9 problems
│   ├── 03_Linked_Lists/            — 8 problems
│   ├── 04_Stacks_Queues/           — 6 problems
│   ├── 05_Trees_Binary_Trees/      — 8 problems
│   ├── 06_Binary_Search_Tree/      — 4 problems
│   ├── 07_Heaps_Priority_Queues/   — 4 problems
│   ├── 08_Graphs/                  — 7 problems (incl. Dijkstra, Union Find)
│   ├── 09_Backtracking/            — 5 problems
│   ├── 10_Dynamic_Programming/     — 10+ problems (incl. Advanced DP)
│   ├── 11_Sliding_Window_Two_Pointers/ — 5+ problems
│   ├── 12_Sorting_Searching/       — 3 problems
│   ├── 13_Recursion_Divide_Conquer/ — Master Theorem + problems
│   ├── 14_Greedy_Algorithms/       — 4 problems (incl. Task Scheduler)
│   ├── 15_Bit_Manipulation/        — 5 problems
│   ├── 16_Advanced_Miscellaneous/  — Trie, Segment Tree
│   ├── 17_Math_Algorithms/         — Number Theory
│   ├── 18_Concurrency_Multithreading/ — Print In Order, Producer-Consumer, Read-Write Lock
│   ├── StudyGuide.md               — Learning paths by career level
│   └── README.md                   — DSA overview and problem index
│
├── CodingPatterns/                 ← Design Patterns + Algorithmic Patterns
│   ├── AlgorithmicPatterns.md      — 16 coding interview patterns with templates
│   ├── patterns.md                 — 25 GoF Design Patterns with Spring Boot examples
│   └── README.md                   — Pattern overview and study order
│
├── SystemDesign/                   ← LLD + HLD Problems + Templates
│   ├── 01_LowLevelDesign/         — 6 OOD problems (ParkingLot, BookMyShow, etc.)
│   ├── 02_HighLevelDesign/        — 9 system design problems (URL Shortener, Chat, Twitter, Uber, Payment, etc.)
│   ├── Templates/                 — HLD & LLD interview templates
│   └── README.md                  — System design overview and study guide
│
├── CodeQuality.md                  ← SOLID, Clean Code, Code Review Checklists
├── CrossReferences.md              ← How DSA concepts map to System Design
├── SelfAssessment.md               ← Topic-by-topic readiness checklists
└── Companies.md                    ← Company-specific interview guides & tips
```

---

## Learning Path by Career Level

### Associate / Junior SDE — Weeks 1-16

You are learning the fundamentals. Focus on understanding, not speed.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Java Basics (OOP, Collections, Streams) | [TechGuide/07](TechGuide/07_Modern_Java_Features.md) (start with basics) | CRITICAL |
| 3-4 | Spring Boot Basics (DI, REST, Bean Lifecycle) | [TechGuide/01](TechGuide/01_Advanced_SpringBoot_Java_Internals.md) (Sections 1.3-1.4) | CRITICAL |
| 5-8 | DSA Core (Arrays, Strings, Lists, Stacks) | [DSA/StudyGuide.md — Path 1](DSA/StudyGuide.md) | CRITICAL |
| 9-10 | Trees, Sorting, Binary Search | [DSA/StudyGuide.md — Path 1](DSA/StudyGuide.md) | HIGH |
| 11-12 | Basic SQL, REST API design | [TechGuide/03](TechGuide/03_Database_Performance_Tuning.md), [TechGuide/11](TechGuide/11_API_Design_REST.md) | HIGH |
| 13-14 | Angular Basics (Components, Routing) | [TechGuide/06](TechGuide/06_Angular_Frontend_Engineering.md) (basics) | MEDIUM |
| 15-16 | Basic DP, Greedy, Bit Manipulation | [DSA/StudyGuide.md — Path 1](DSA/StudyGuide.md) | HIGH |

**Key outcome**: Solve 80% of LeetCode Easy problems. Explain basic Spring Boot and REST.

---

### Mid-Level SDE — Weeks 1-16

You know the basics. Now build depth and pattern recognition.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Java Memory Model, Concurrency | [TechGuide/01](TechGuide/01_Advanced_SpringBoot_Java_Internals.md) (Sections 1.1-1.2) | CRITICAL |
| 3-4 | Design Patterns (SOLID, GoF) | [TechGuide/12](TechGuide/12_Design_Patterns_SOLID_CleanArch.md), [CodingPatterns/](CodingPatterns/) | CRITICAL |
| 5-8 | DSA Medium Problems (Trees, Graphs, DP) | [DSA/StudyGuide.md — Path 2](DSA/StudyGuide.md) | CRITICAL |
| 9-10 | Database Indexing, SQL Optimization | [TechGuide/03](TechGuide/03_Database_Performance_Tuning.md) | HIGH |
| 11-12 | Spring Boot Security (JWT, OAuth2) | [TechGuide/01](TechGuide/01_Advanced_SpringBoot_Java_Internals.md) (Section 1.4) | HIGH |
| 13-14 | Angular State Management, RxJS | [TechGuide/06](TechGuide/06_Angular_Frontend_Engineering.md) (Sections 6.2-6.3) | HIGH |
| 15-16 | Code Quality, Testing | [CodeQuality.md](CodeQuality.md), [TechGuide/08](TechGuide/08_Testing_Strategies.md) | HIGH |

**Key outcome**: Solve 70% of LeetCode Medium problems. Design clean APIs with proper patterns.

---

### Senior SDE — Weeks 1-12

You build production systems. Now learn to design them at scale.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1-2 | Microservices (Circuit Breaker, SAGA, Kafka) | [TechGuide/02](TechGuide/02_Microservices_Distributed_Systems.md) | CRITICAL |
| 3-4 | System Design Concepts (CAP, Scaling, LB) | [TechGuide/04](TechGuide/04_System_Design.md) | CRITICAL |
| 5-6 | HLD Practice (URL Shortener, Chat System) | [SystemDesign/02_HighLevelDesign/](SystemDesign/02_HighLevelDesign/) | CRITICAL |
| 7-8 | LLD Practice (Parking Lot, Elevator) | [SystemDesign/01_LowLevelDesign/](SystemDesign/01_LowLevelDesign/) | HIGH |
| 9-10 | DB Scaling (Sharding, Caching, Redis) | [TechGuide/03](TechGuide/03_Database_Performance_Tuning.md) (Sections 3.2-3.3) | HIGH |
| 11-12 | DSA Hard Problems + Mock Interviews | [DSA/StudyGuide.md — Path 3](DSA/StudyGuide.md) | CRITICAL |

**Key outcome**: Design scalable distributed systems. Solve Hard DSA problems. Explain trade-offs.

---

### Lead / Staff Engineer — Weeks 1-12

You own technical direction. Now master everything and lead.

| Week | Topic | Resource | Priority |
|------|-------|----------|----------|
| 1 | Java Internals Deep Dive (GC, Memory, Concurrency) | [TechGuide/01](TechGuide/01_Advanced_SpringBoot_Java_Internals.md) | CRITICAL |
| 2 | Modern Java (Virtual Threads, Records, Sealed) | [TechGuide/07](TechGuide/07_Modern_Java_Features.md) | HIGH |
| 3 | Microservices Resilience + Data Consistency | [TechGuide/02](TechGuide/02_Microservices_Distributed_Systems.md) | CRITICAL |
| 4 | Database Optimization + Caching Patterns | [TechGuide/03](TechGuide/03_Database_Performance_Tuning.md) | CRITICAL |
| 5 | System Design Framework + Practice | [TechGuide/04](TechGuide/04_System_Design.md), [SystemDesign/](SystemDesign/) | CRITICAL |
| 6 | Clean Architecture + DDD | [TechGuide/12](TechGuide/12_Design_Patterns_SOLID_CleanArch.md) | HIGH |
| 7 | Angular Signals, State, Performance | [TechGuide/06](TechGuide/06_Angular_Frontend_Engineering.md) | HIGH |
| 8 | API Design + REST Best Practices | [TechGuide/11](TechGuide/11_API_Design_REST.md) | HIGH |
| 9 | DevOps + CI/CD + Kubernetes | [TechGuide/09](TechGuide/09_DevOps_CICD_Docker.md), [TechGuide/05](TechGuide/05_Modern_Trends_2026.md) | HIGH |
| 10 | Observability (Logging, Metrics, Tracing) | [TechGuide/10](TechGuide/10_Observability.md) | HIGH |
| 11 | **AI/ML Complete** (LLM, RAG, Spring AI, Agents, MLOps) | [AI/](AI/) (all 11 sections) | **CRITICAL** |
| 12 | Leadership, Behavioral, Story Bank | [TechGuide/13](TechGuide/13_Leadership_Behavioral_SystemDesign.md) | CRITICAL |

**Key outcome**: Pass any technical, system design, and behavioral round at FAANG-level companies.

---

## Section-by-Section Summary

### TechGuide — What Each Section Covers

| # | Section | Key Topics | Relevant From |
|---|---------|-----------|---------------|
| 01 | [Java & Spring Boot Internals](TechGuide/01_Advanced_SpringBoot_Java_Internals.md) | Memory Model, GC (G1GC/ZGC), Concurrency, Bean Lifecycle, OAuth2/JWT | Mid-Level+ |
| 02 | [Microservices & Distributed Systems](TechGuide/02_Microservices_Distributed_Systems.md) | Circuit Breaker, SAGA, Kafka, gRPC, Outbox Pattern | Senior+ |
| 03 | [Database & Performance Tuning](TechGuide/03_Database_Performance_Tuning.md) | Indexing, EXPLAIN ANALYZE, N+1, Sharding, Caching, Redis | Mid-Level+ |
| 04 | [System Design](TechGuide/04_System_Design.md) | CAP, Scaling, Load Balancers, API Gateway, Strangler Fig | Senior+ |
| 05 | [Modern Trends 2026](TechGuide/05_Modern_Trends_2026.md) | RAG/AI with Spring AI, Kubernetes for Engineers | Lead |
| 06 | [Angular & Frontend](TechGuide/06_Angular_Frontend_Engineering.md) | Signals, Standalone Components, NgRx Signal Store, RxJS, Guards | All Levels |
| 07 | [Modern Java (17-21)](TechGuide/07_Modern_Java_Features.md) | Records, Sealed Classes, Virtual Threads, Pattern Matching | Mid-Level+ |
| 08 | [Testing Strategies](TechGuide/08_Testing_Strategies.md) | JUnit 5, Testcontainers, WireMock, Contract Testing | Mid-Level+ |
| 09 | [DevOps & CI/CD](TechGuide/09_DevOps_CICD_Docker.md) | Docker, GitHub Actions, Blue-Green, GitOps, Terraform | Senior+ |
| 10 | [Observability](TechGuide/10_Observability.md) | Structured Logging, Prometheus, OpenTelemetry, SLI/SLO | Senior+ |
| 11 | [API Design & REST](TechGuide/11_API_Design_REST.md) | RESTful Design, RFC 7807, Pagination, Versioning, HATEOAS | Mid-Level+ |
| 12 | [Design Patterns & Architecture](TechGuide/12_Design_Patterns_SOLID_CleanArch.md) | SOLID, GoF Patterns, CQRS, Clean Architecture, DDD | Mid-Level+ |
| 13 | [Leadership & Behavioral](TechGuide/13_Leadership_Behavioral_SystemDesign.md) | STAR Method, Story Bank, System Design Framework, ADRs | Lead |
| 14 | [Security, OWASP & Cloud](TechGuide/14_Security_OWASP_Cloud.md) | OWASP Top 10, JWT, CORS, STRIDE, AWS Services, IAM, VPC | Senior+ |

### AI — What Each Section Covers

| # | Section | Key Topics | Relevant From |
|---|---------|-----------|---------------|
| 01 | [AI Fundamentals](AI/01_AI_Fundamentals.md) | Transformers, Tokens, Embeddings, LLM landscape, model comparison | All Levels |
| 02 | [LLM & Prompt Engineering](AI/02_LLM_and_Prompt_Engineering.md) | Few-shot, CoT, ReAct, structured output, prompt injection defense | All Levels |
| 03 | [RAG Architecture](AI/03_RAG_Architecture.md) | Ingestion, chunking strategies, retrieval, re-ranking, evaluation | Mid-Level+ |
| 04 | [Vector Databases](AI/04_Vector_Databases_Embeddings.md) | pgvector, Pinecone, HNSW, IVF, hybrid search, multi-tenancy | Mid-Level+ |
| 05 | [Spring AI](AI/05_Spring_AI.md) | ChatClient, EmbeddingModel, VectorStore, Function Calling, Advisors | Mid-Level+ |
| 06 | [AI Agents](AI/06_AI_Agents_and_Workflows.md) | ReAct, MCP, multi-agent, plan-and-execute, safety controls | Senior+ |
| 07 | [AI Dev Tools](AI/07_AI_Powered_Dev_Tools.md) | Copilot, Cursor, CodeRabbit, .cursorrules, AI in SDLC | All Levels |
| 08 | [AI in Frontend](AI/08_AI_in_Frontend.md) | Angular streaming chat, SSE, markdown rendering, AI UX patterns | Mid-Level+ |
| 09 | [MLOps](AI/09_MLOps_AI_in_Production.md) | LLM routing, cost management, A/B testing, quality monitoring | Senior+ |
| 10 | [AI System Design](AI/10_AI_System_Design.md) | AI customer support, knowledge base, code review, recommendation | Lead |
| 11 | [AI Ethics & Governance](AI/11_AI_Ethics_Safety_Governance.md) | Hallucination prevention, bias, PII, prompt injection, EU AI Act | Lead |

### Supporting Files

| File | Purpose | When to Use |
|------|---------|-------------|
| [CodeQuality.md](CodeQuality.md) | SOLID, Clean Code, naming conventions, anti-patterns, code review checklist | Before any coding round |
| [CrossReferences.md](CrossReferences.md) | How DSA concepts map to System Design (Arrays → Caches, Trees → DBs, Graphs → Networks) | When connecting DSA to design |
| [SelfAssessment.md](SelfAssessment.md) | Topic-by-topic readiness checklists with confidence scoring | Weekly self-check |
| [Companies.md](Companies.md) | Google, Amazon, Meta, Microsoft, Flipkart, Apple interview guides | Before company-specific prep |
| [DSA/StudyGuide.md](DSA/StudyGuide.md) | Learning paths by career level + difficulty progression per category | When planning DSA study |

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
| **Database** | Covering Index, Partition Pruning, Leftmost Prefix Rule, N+1 Problem, EXPLAIN ANALYZE |
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
