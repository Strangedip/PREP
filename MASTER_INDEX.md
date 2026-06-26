# MASTER INDEX — Topic → File → Round → Level

> **Quick navigation**: Find any topic, which file covers it, which interview round it appears in, and minimum career level.
> **Companion**: [InterviewPlaybook.md](InterviewPlaybook.md), [00_TableOfContents.md](01_TechGuide/00_TableOfContents.md)

---

## Legend

| Round | Meaning |
|-------|---------|
| **COD** | Coding / DSA |
| **SD** | System Design (HLD/LLD) |
| **TECH** | Technical depth / domain |
| **BEH** | Behavioral / Leadership Principles |
| **ALL** | Any round |

| Level | Meaning |
|-------|---------|
| **ASSOC** | Associate / Junior |
| **MID** | Mid-Level |
| **SR** | Senior |
| **LEAD** | Lead / Staff |

---

## Fundamentals (Start Here — ASSOC)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| Java OOP (4 pillars, equals/hashCode) | [00_Java_OOP_Fundamentals.md](01_TechGuide/00_Java_OOP_Fundamentals.md) | TECH | ASSOC |
| Big-O, arrays, recursion | [00_Computer_Science_Fundamentals.md](01_TechGuide/00_Computer_Science_Fundamentals.md) | COD | ASSOC |
| HTTP, REST, status codes, JWT intro | [00_Web_Fundamentals.md](01_TechGuide/00_Web_Fundamentals.md) | TECH | ASSOC |
| SQL JOINs, window functions | [35_SQL_Fundamentals.md](01_TechGuide/35_SQL_Fundamentals.md) | TECH | ASSOC |
| DSA Path 1 (arrays, strings, trees) | [02_DSA/StudyGuide.md](02_DSA/StudyGuide.md) | COD | ASSOC |
| Interview prep schedule | [InterviewPlaybook.md](InterviewPlaybook.md) | ALL | ALL |
| Readiness checklist | [SelfAssessment.md](SelfAssessment.md) | ALL | ALL |

---

## Java & Spring (TECH)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| Records, Virtual Threads, Sealed | [01_Modern_Java_Features.md](01_TechGuide/01_Modern_Java_Features.md) | TECH | MID |
| GC, concurrency, Spring lifecycle | [02_Advanced_SpringBoot_Java_Internals.md](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) | TECH | MID |
| HashMap, ConcurrentHashMap, Streams | [15_Java_Collections_Concurrency_DeepDive.md](01_TechGuide/15_Java_Collections_Concurrency_DeepDive.md) | TECH | MID |
| JPA, WebFlux, Spring Cloud | [16_Spring_Ecosystem_DeepDive.md](01_TechGuide/16_Spring_Ecosystem_DeepDive.md) | TECH | SR |
| JVM tuning, JFR, profiling | [18_Performance_Engineering_JVM.md](01_TechGuide/18_Performance_Engineering_JVM.md) | TECH | SR |

---

## Architecture & Patterns (TECH / SD)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| SOLID, GoF, Clean Architecture | [03_Design_Patterns_SOLID_CleanArch.md](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md) | TECH/SD | MID |
| 25 GoF + Spring examples | [03_CodingPatterns/01_Patterns.md](03_CodingPatterns/01_Patterns.md) | TECH | MID |
| 16 algorithmic patterns | [03_CodingPatterns/02_AlgorithmicPatterns.md](03_CodingPatterns/02_AlgorithmicPatterns.md) | COD | MID |
| ADRs, estimation, post-mortems | [20_Technical_Leadership_Architecture.md](01_TechGuide/20_Technical_Leadership_Architecture.md) | BEH/TECH | LEAD |

---

## APIs & Frontend (TECH)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| REST, pagination, versioning | [04_API_Design_REST.md](01_TechGuide/04_API_Design_REST.md) | TECH | MID |
| GraphQL, federation | [21_GraphQL_and_Alternative_APIs.md](01_TechGuide/21_GraphQL_and_Alternative_APIs.md) | TECH | SR |
| Angular Signals, RxJS | [08_Angular_Frontend_Engineering.md](01_TechGuide/08_Angular_Frontend_Engineering.md) | TECH | MID |
| TypeScript, React vs Angular | [37_TypeScript_and_Frontend_Landscape.md](01_TechGuide/37_TypeScript_and_Frontend_Landscape.md) | TECH | MID |
| Python/Go interview syntax | [36_Polyglot_Interview_Python_and_Go.md](01_TechGuide/36_Polyglot_Interview_Python_and_Go.md) | COD | MID |

---

## Data & Caching (TECH / SD)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| Indexing, sharding, Redis intro | [05_Database_Performance_Tuning.md](01_TechGuide/05_Database_Performance_Tuning.md) | TECH/SD | MID |
| PostgreSQL MVCC, replication | [26_PostgreSQL_Relational_DB_Deep_Dive.md](01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md) | TECH | SR |
| MongoDB, DynamoDB, Cassandra | [27_NoSQL_Databases_Guide.md](01_TechGuide/27_NoSQL_Databases_Guide.md) | TECH/SD | SR |
| Redis cluster, locks, stampede | [28_Redis_Distributed_Caching.md](01_TechGuide/28_Redis_Distributed_Caching.md) | TECH/SD | SR |
| Elasticsearch, BM25 | [34_Search_Engines_Elasticsearch.md](01_TechGuide/34_Search_Engines_Elasticsearch.md) | TECH/SD | SR |
| CDC, lakehouse, Spark | [25_Data_Engineering_Fundamentals.md](01_TechGuide/25_Data_Engineering_Fundamentals.md) | TECH | SR |

---

## Distributed Systems (SD / TECH)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| CAP, scaling, load balancing | [07_System_Design.md](01_TechGuide/07_System_Design.md) | SD | SR |
| Circuit breaker, SAGA, Kafka | [06_Microservices_Distributed_Systems.md](01_TechGuide/06_Microservices_Distributed_Systems.md) | SD/TECH | SR |
| Event sourcing, CQRS, CDC | [19_Event_Driven_Architecture.md](01_TechGuide/19_Event_Driven_Architecture.md) | SD | SR |
| DSA ↔ SD mapping | [CrossReferences.md](CrossReferences.md) | SD | SR |

---

## Infrastructure & Cloud (TECH)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| HTTP/2, TLS, WebSocket, gRPC | [17_Networking_Protocols.md](01_TechGuide/17_Networking_Protocols.md) | TECH | MID |
| VPC, LB, zero trust | [29_Advanced_Networking_Infrastructure.md](01_TechGuide/29_Advanced_Networking_Infrastructure.md) | TECH | SR |
| K8s pods, HPA, ingress | [30_Kubernetes_Deep_Dive.md](01_TechGuide/30_Kubernetes_Deep_Dive.md) | TECH | SR |
| AWS IAM, S3, Well-Architected | [31_Cloud_Computing_AWS_GCP_Azure.md](01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md) | TECH | SR |
| Linux, epoll, debugging | [32_Operating_Systems_and_Linux.md](01_TechGuide/32_Operating_Systems_and_Linux.md) | TECH | MID |
| Docker, CI/CD, GitOps | [10_DevOps_CICD_Docker.md](01_TechGuide/10_DevOps_CICD_Docker.md) | TECH | SR |
| Logs, metrics, traces | [11_Observability.md](01_TechGuide/11_Observability.md) | TECH | SR |
| SLI/SLO, incident response | [23_SRE_Reliability_Engineering.md](01_TechGuide/23_SRE_Reliability_Engineering.md) | TECH | SR |
| Platform engineering, IDP | [24_Platform_Engineering_IDP.md](01_TechGuide/24_Platform_Engineering_IDP.md) | TECH | LEAD |

---

## Security & Compliance (TECH)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| OWASP Top 10 **2025** | [12_Security_OWASP_Cloud.md](01_TechGuide/12_Security_OWASP_Cloud.md) | TECH | SR |
| GDPR, PCI, audit trails | [38_Compliance_and_Regulated_Systems.md](01_TechGuide/38_Compliance_and_Regulated_Systems.md) | TECH/BEH | SR |

---

## AI / ML (TECH / SD)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| LLM, transformers intro | [05_AI/01_AI_Fundamentals.md](05_AI/01_AI_Fundamentals.md) | TECH | SR |
| RAG architecture | [05_AI/03_RAG_Architecture.md](05_AI/03_RAG_Architecture.md) | SD/TECH | SR |
| Spring AI | [05_AI/05_Spring_AI.md](05_AI/05_Spring_AI.md) | TECH | SR |
| AI agents, MCP | [05_AI/06_AI_Agents_and_Workflows.md](05_AI/06_AI_Agents_and_Workflows.md) | TECH | LEAD |
| AI system design | [05_AI/10_AI_System_Design.md](05_AI/10_AI_System_Design.md) | SD | SR |
| Responsible AI | [05_AI/11_AI_Ethics_Safety_Governance.md](05_AI/11_AI_Ethics_Safety_Governance.md) | BEH/TECH | SR |

---

## Behavioral (BEH)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| STAR method, story bank | [14_Leadership_Behavioral_SystemDesign.md](01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) | BEH | ALL |
| Amazon LP ↔ STAR mapping | [Companies.md](Companies.md) | BEH | ALL |
| Company-specific processes | [Companies.md](Companies.md) | ALL | ALL |

---

## Coding / DSA (COD)

| Topic | File | Round | Level |
|-------|------|-------|-------|
| 108+ problems with Java | [02_DSA/README.md](02_DSA/README.md) | COD | ALL |
| Tier 3 hard differentiators | [02_DSA/Tier3_Differentiators.md](02_DSA/Tier3_Differentiators.md) | COD | SR |
| Rapid-fire Q&A (~218) | [InterviewQuestions.md](InterviewQuestions.md) | TECH/COD | ALL |

---

## Low-Level Design (SD)

| Problem | File | Level |
|---------|------|-------|
| Parking Lot | [ParkingLot.md](04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) | MID |
| BookMyShow | [BookMyShow.md](04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md) | SR |
| Elevator | [ElevatorSystem.md](04_SystemDesign/01_LowLevelDesign/ElevatorSystem/ElevatorSystem.md) | SR |
| Vending Machine | [VendingMachine.md](04_SystemDesign/01_LowLevelDesign/VendingMachine/VendingMachine.md) | MID |
| Library | [LibraryManagement.md](04_SystemDesign/01_LowLevelDesign/LibraryManagement/LibraryManagement.md) | MID |
| Snake & Ladder | [SnakeAndLadder.md](04_SystemDesign/01_LowLevelDesign/SnakeAndLadder/SnakeAndLadder.md) | ASSOC |
| ATM | [ATM.md](04_SystemDesign/01_LowLevelDesign/ATM/ATM.md) | MID |
| Hotel Booking | [HotelBooking.md](04_SystemDesign/01_LowLevelDesign/HotelBooking/HotelBooking.md) | MID |
| Food Delivery | [FoodDelivery.md](04_SystemDesign/01_LowLevelDesign/FoodDelivery/FoodDelivery.md) | SR |

---

## High-Level Design (SD)

| Problem | File | Level |
|---------|------|-------|
| URL Shortener | [URLShortener.md](04_SystemDesign/02_HighLevelDesign/URLShortener/URLShortener.md) | MID |
| Rate Limiter | [RateLimiter.md](04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) | MID |
| Chat System | [ChatSystem.md](04_SystemDesign/02_HighLevelDesign/ChatSystem/ChatSystem.md) | SR |
| Notification System | [NotificationSystem.md](04_SystemDesign/02_HighLevelDesign/NotificationSystem/NotificationSystem.md) | SR |
| News Feed | [NewsFeed.md](04_SystemDesign/02_HighLevelDesign/NewsFeed/NewsFeed.md) | SR |
| Distributed Cache | [DistributedCache.md](04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) | SR |
| Twitter | [Twitter.md](04_SystemDesign/02_HighLevelDesign/Twitter/Twitter.md) | SR |
| Ride Sharing | [RideSharing.md](04_SystemDesign/02_HighLevelDesign/RideSharing/RideSharing.md) | SR |
| Payment System | [PaymentSystem.md](04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) | SR |
| Search Autocomplete | [SearchAutocomplete.md](04_SystemDesign/02_HighLevelDesign/SearchAutocomplete/SearchAutocomplete.md) | SR |
| File Storage | [FileStorage.md](04_SystemDesign/02_HighLevelDesign/FileStorage/FileStorage.md) | SR |
| Video Streaming | [VideoStreaming.md](04_SystemDesign/02_HighLevelDesign/VideoStreaming/VideoStreaming.md) | SR |
| WhatsApp | [WhatsApp.md](04_SystemDesign/02_HighLevelDesign/WhatsApp/WhatsApp.md) | SR |
| Instagram | [Instagram.md](04_SystemDesign/02_HighLevelDesign/Instagram/Instagram.md) | SR |
| Ticketmaster | [Ticketmaster.md](04_SystemDesign/02_HighLevelDesign/Ticketmaster/Ticketmaster.md) | SR |
| Web Crawler | [WebCrawler.md](04_SystemDesign/02_HighLevelDesign/WebCrawler/WebCrawler.md) | SR |
| Metrics / Monitoring | [MetricsMonitoring.md](04_SystemDesign/02_HighLevelDesign/MetricsMonitoring/MetricsMonitoring.md) | SR |
| YouTube | [YouTube.md](04_SystemDesign/02_HighLevelDesign/YouTube/YouTube.md) | SR |

---

## Round Prep — One Page

| Round | Top 5 Resources |
|-------|-----------------|
| **Coding** | StudyGuide → AlgorithmicPatterns → 3 problems/day → Tier3 → mock |
| **HLD** | §07 → HLD Template → URL Shortener → Chat → Payment |
| **LLD** | Parking Lot → Vending Machine → BookMyShow → Food Delivery |
| **Java depth** | §02 → §15 → §16 → InterviewQuestions §Java |
| **Behavioral** | §14 → Companies Amazon LP → 8 STAR stories → InterviewPlaybook |

---

*Last updated: June 2026 — 18 HLD, 9 LLD, TechGuide §00–§38*
