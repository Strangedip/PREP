# System Design — Interview Practice Problems

> **Target**: Lead Software Engineer at FAANG-level companies
> **Scope**: Both Low-Level Design (OOD) and High-Level Design (Distributed Systems)
> **Approach**: Every problem is a complete, interview-ready walkthrough — requirements, design, code, trade-offs

---

## How to Use This Section

Each problem file follows a consistent structure modeled after what interviewers expect:

**Low-Level Design (LLD) Problems:**
1. Problem Statement & Requirements (Functional + Non-Functional)
2. Core Classes & Relationships (UML-style)
3. Design Patterns Used (with justification)
4. Complete Implementation (Java)
5. Algorithms & Business Logic
6. Edge Cases & Error Handling
7. Testing Strategy
8. Interview Discussion Points

**High-Level Design (HLD) Problems:**
1. Problem Statement & Requirements
2. Capacity Estimation (QPS, Storage, Bandwidth, Memory)
3. High-Level Architecture (components, data flow)
4. Detailed Component Design (each service broken down)
5. Database Schema & Data Modeling
6. API Design (REST endpoints, request/response)
7. Scaling Strategy (sharding, replication, caching)
8. Monitoring, Alerting & Operational Concerns
9. Security Considerations
10. Interview Discussion Points

**Templates:**
Use the templates when practicing new problems on your own. They give you a repeatable methodology.

---

## Directory Structure

```
04_SystemDesign/
├── 01_LowLevelDesign/
│   ├── ParkingLot/ParkingLot.md                 # Classic OOD — Strategy, Factory, State
│   ├── BookMyShow/BookMyShow.md                  # Ticket booking with seat locking
│   ├── ElevatorSystem/ElevatorSystem.md          # State machine, scheduling algorithms
│   ├── SnakeAndLadder/SnakeAndLadder.md          # Game design, turn-based mechanics
│   ├── LibraryManagement/LibraryManagement.md    # Domain modeling, CRUD, Observer
│   └── VendingMachine/VendingMachine.md          # State pattern, finite automata
│
├── 02_HighLevelDesign/
│   ├── URLShortener/URLShortener.md              # Encoding, caching, analytics
│   ├── RateLimiter/RateLimiter.md                # Token Bucket, Sliding Window, distributed
│   ├── ChatSystem/ChatSystem.md                  # WebSocket, presence, message delivery
│   ├── NotificationSystem/NotificationSystem.md  # Multi-channel fanout, priority, DLQ
│   ├── NewsFeed/NewsFeed.md                      # Fan-out on write/read, ranking
│   ├── DistributedCache/DistributedCache.md      # Consistent hashing, eviction, replication
│   ├── Twitter/Twitter.md                        # Timeline, fan-out, celebrity problem, search
│   ├── RideSharing/RideSharing.md                # Geospatial indexing, matching, ETA, surge
│   └── PaymentSystem/PaymentSystem.md            # ACID, idempotency, reconciliation, fraud
│
└── 00_Templates/
    ├── LLD_Template/LLD_Template.md              # Step-by-step LLD interview framework
    └── HLD_Template/HLD_Template.md              # Step-by-step HLD interview framework
```

---

## Low-Level Design Problems

### Problem Catalog

| # | Problem | Difficulty | Key Patterns | Key Concepts | Link |
|---|---------|-----------|-------------|-------------|------|
| 1 | **Parking Lot** | Medium | Strategy, Factory, Singleton | Resource allocation, pricing, multi-level | [ParkingLot.md](01_LowLevelDesign/ParkingLot/ParkingLot.md) |
| 2 | **BookMyShow** | Hard | Observer, Strategy, Repository | Seat locking, concurrent booking, ACID | [BookMyShow.md](01_LowLevelDesign/BookMyShow/BookMyShow.md) |
| 3 | **Elevator System** | Hard | State, Strategy, Observer | Scheduling (SCAN, LOOK), multi-elevator dispatch | [ElevatorSystem.md](01_LowLevelDesign/ElevatorSystem/ElevatorSystem.md) |
| 4 | **Snake & Ladder** | Medium | Factory, Builder, Command | Game loop, turn management, board generation | [SnakeAndLadder.md](01_LowLevelDesign/SnakeAndLadder/SnakeAndLadder.md) |
| 5 | **Library Management** | Medium | Observer, Strategy, Repository | Book tracking, member management, fines | [LibraryManagement.md](01_LowLevelDesign/LibraryManagement/LibraryManagement.md) |
| 6 | **Vending Machine** | Medium | State, Strategy | Finite state machine, inventory, payment | [VendingMachine.md](01_LowLevelDesign/VendingMachine/VendingMachine.md) |

### What Interviewers Evaluate in LLD

| Evaluation Criteria | What They Look For |
|--------------------|--------------------|
| **Requirements Gathering** | Did you ask clarifying questions? Did you define scope? |
| **Class Design** | Proper abstraction, encapsulation, single responsibility |
| **Design Patterns** | Did you use patterns naturally (not forced)? Can you justify the choice? |
| **Relationships** | Correct use of inheritance vs composition vs aggregation |
| **SOLID Principles** | SRP, OCP, LSP, ISP, DIP — are they followed? |
| **Extensibility** | Can the design accommodate new requirements without major rewrites? |
| **Error Handling** | Null checks, invalid input, concurrent access, edge cases |
| **Code Quality** | Clean, readable, consistent naming, proper access modifiers |

### Recommended Practice Order (LLD)

**Start with** (Pattern-focused, medium complexity):
1. Parking Lot — Strategy and Factory patterns, straightforward requirements
2. Vending Machine — State pattern, finite state machine
3. Snake & Ladder — Game design, command pattern

**Then advance to** (More complex domains):
4. Library Management — Domain modeling, multiple interacting entities
5. BookMyShow — Concurrency, seat locking, real-world complexity
6. Elevator System — Complex scheduling, state machines, multi-component design

---

## High-Level Design Problems

### Problem Catalog

| # | Problem | Difficulty | Key Components | Scale Target | Link |
|---|---------|-----------|---------------|-------------|------|
| 1 | **URL Shortener** | Medium | Encoding, Cache, Counter | 100M URLs/day | [URLShortener.md](02_HighLevelDesign/URLShortener/URLShortener.md) |
| 2 | **Rate Limiter** | Medium | Token Bucket, Sliding Window | 1M+ requests/sec | [RateLimiter.md](02_HighLevelDesign/RateLimiter/RateLimiter.md) |
| 3 | **Chat System** | Hard | WebSocket, Presence, MQ | 1B messages/day | [ChatSystem.md](02_HighLevelDesign/ChatSystem/ChatSystem.md) |
| 4 | **Notification System** | Hard | Fan-out, Priority, DLQ | 10B notifications/day | [NotificationSystem.md](02_HighLevelDesign/NotificationSystem/NotificationSystem.md) |
| 5 | **News Feed** | Hard | Fan-out, Ranking, Cache | 500M users | [NewsFeed.md](02_HighLevelDesign/NewsFeed/NewsFeed.md) |
| 6 | **Distributed Cache** | Hard | Consistent Hashing, Replication | Millions of ops/sec | [DistributedCache.md](02_HighLevelDesign/DistributedCache/DistributedCache.md) |
| 7 | **Twitter** | Hard | Fan-out, Timeline, Social Graph | 500M tweets/day | [Twitter.md](02_HighLevelDesign/Twitter/Twitter.md) |
| 8 | **Ride-Sharing (Uber)** | Hard | Geospatial, Matching, Surge | 20M rides/day | [RideSharing.md](02_HighLevelDesign/RideSharing/RideSharing.md) |
| 9 | **Payment System** | Hard | ACID, Idempotency, Reconciliation | $1B+ daily volume | [PaymentSystem.md](02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |

### What Interviewers Evaluate in HLD

| Evaluation Criteria | What They Look For |
|--------------------|--------------------|
| **Requirements** | Did you clarify functional AND non-functional? Did you prioritize? |
| **Estimation** | Can you do back-of-the-envelope math for QPS, storage, bandwidth? |
| **Architecture** | Is the high-level diagram clear? Are components well-defined? |
| **Data Model** | SQL vs NoSQL choice justified? Schema handles the scale? |
| **API Design** | RESTful, proper HTTP methods, pagination, error codes? |
| **Scaling** | Sharding strategy, caching layers, async processing? |
| **Trade-offs** | Can you articulate why you chose X over Y? CAP awareness? |
| **Reliability** | Fault tolerance, monitoring, graceful degradation? |
| **Communication** | Did you drive the conversation? Did you draw diagrams? |

### Recommended Practice Order (HLD)

**Start with** (Well-defined scope, proven patterns):
1. URL Shortener — Classic starter, covers encoding, caching, DB design
2. Rate Limiter — Algorithm-focused, distributed systems fundamentals

**Then advance to** (More complex, multiple subsystems):
3. Notification System — Fan-out patterns, multi-channel delivery, reliability
4. Chat System — Real-time, presence, message ordering, WebSocket
5. News Feed — Fan-out on write vs read, ranking, social graph

**Capstone** (Infra-level thinking):
6. Distributed Cache — Low-level distributed systems, consistent hashing, replication

**Advanced** (Multi-domain, Lead-level):
7. Twitter — Social graph, fan-out trade-offs, celebrity problem, search and trends
8. Ride-Sharing (Uber) — Geospatial indexing, real-time matching, dynamic pricing, ETA
9. Payment System — Financial transactions, ACID, idempotency, reconciliation, fraud

---

## Design Templates

Use these when practicing new problems that are not in this repo.

| Template | Purpose | Link |
|----------|---------|------|
| **LLD Template** | Step-by-step methodology for any OOD problem | [LLD_Template.md](00_Templates/LLD_Template/LLD_Template.md) |
| **HLD Template** | Step-by-step methodology for any distributed systems problem | [HLD_Template.md](00_Templates/HLD_Template/HLD_Template.md) |

### LLD Template Summary (5-Step Method)

| Step | Time | What to Do |
|------|------|-----------|
| 1. Understand | 5 min | Clarify requirements, identify actors, define scope |
| 2. High-Level | 5 min | Use case diagram, identify core components |
| 3. Class Design | 15 min | Class diagram, relationships, design patterns |
| 4. Implement | 15 min | Key methods, error handling, algorithms |
| 5. Discuss | 5 min | Trade-offs, extensibility, testing approach |

### HLD Template Summary (4-Step Method)

| Step | Time | What to Do |
|------|------|-----------|
| 1. Requirements | 5 min | Functional, non-functional, capacity estimation |
| 2. High-Level Architecture | 10 min | System diagram, core components, data flow |
| 3. Deep Dive | 20 min | DB schema, API design, caching, algorithms |
| 4. Operational | 10 min | Scaling, fault tolerance, monitoring, security |

---

## Common Patterns Across Problems

### LLD Patterns You Must Know

| Pattern | Where It Appears | Core Idea |
|---------|-----------------|-----------|
| **Strategy** | Parking Lot (pricing), BookMyShow (discount), Library (fine calculation) | Swap algorithms at runtime via interface |
| **State** | Vending Machine (states), Elevator (direction), BookMyShow (booking status) | Object behavior changes based on internal state |
| **Factory** | Parking Lot (vehicles), Snake & Ladder (board elements) | Centralize object creation, decouple client from concrete classes |
| **Observer** | Library (notifications), BookMyShow (waitlist), Elevator (floor arrival) | Notify dependents automatically when state changes |
| **Singleton** | Parking Lot (system), Vending Machine (system) | Single instance for resource managers |
| **Builder** | Snake & Ladder (board), BookMyShow (show configuration) | Step-by-step construction of complex objects |
| **Repository** | Library (book store), BookMyShow (show store) | Abstract data access behind a collection-like interface |

### HLD Patterns You Must Know

| Pattern | Where It Appears | Core Idea |
|---------|-----------------|-----------|
| **Cache-Aside** | URL Shortener, News Feed, Distributed Cache | Check cache first, load from DB on miss |
| **Write-Behind** | News Feed (timeline), Notification System (delivery log) | Write to cache immediately, async persist to DB |
| **Fan-out on Write** | News Feed, Notification System | Pre-compute and push to recipients when event occurs |
| **Fan-out on Read** | News Feed (celebrity followers) | Compute on-demand when recipient requests |
| **Consistent Hashing** | Distributed Cache, Chat System (server assignment) | Distribute data across nodes with minimal rehashing |
| **Pub/Sub** | Notification System, Chat System | Decouple producers from consumers via message broker |
| **Token Bucket** | Rate Limiter | Control throughput with fixed-rate token replenishment |
| **CQRS** | News Feed (write=posts DB, read=timeline cache) | Separate read and write models for different optimization |

---

## Capacity Estimation Cheat Sheet

Use these numbers for quick back-of-the-envelope calculations in HLD interviews.

### Traffic Estimation

| Metric | Formula |
|--------|---------|
| Requests per second (QPS) | Daily Active Users x Avg Requests per User / 86,400 |
| Peak QPS | QPS x 2 (or 3 for spiky traffic) |
| Write QPS | Total QPS x Write Ratio (typically 10-20%) |
| Read QPS | Total QPS x Read Ratio (typically 80-90%) |

### Storage Estimation

| Data Type | Typical Size |
|-----------|-------------|
| Short URL record | ~500 bytes |
| Chat message | ~200 bytes |
| User profile | ~1 KB |
| Social media post | ~1 KB (text) + 500 KB (media reference) |
| Notification record | ~500 bytes |

### Quick Math Shortcuts

| Number | Approximation |
|--------|---------------|
| 1 day | ~100,000 seconds (86,400) |
| 1 million | 10^6 |
| 1 billion | 10^9 |
| 1 TB | 10^12 bytes, ~1 million MB |
| 1 GB/day | ~12 KB/sec |
| 100 million daily requests | ~1,200 QPS |
| 1 billion daily requests | ~12,000 QPS |

### Memory Estimation (Caching)

| Scenario | Formula |
|----------|---------|
| Cache size for N items | N x Average Object Size |
| 80/20 rule | 20% of data serves 80% of reads |
| Cache memory | Daily Read QPS x 86,400 x 0.20 x Object Size |

---

## Additional HLD Problems to Practice (Not in Repo)

These are common in Lead Engineer interviews. Use the HLD Template to practice them:

| # | Problem | Key Concepts to Cover |
|---|---------|----------------------|
| 1 | **Design YouTube** | Video upload, transcoding, CDN, recommendations |
| 2 | **Design WhatsApp** | End-to-end encryption, group messaging, media, backup |
| 3 | **Design Google Drive** | File sync, chunking, dedup, conflict resolution |
| 4 | **Design Instagram** | Image storage, CDN, news feed, stories, explore |
| 5 | **Design Typeahead** | Trie, prefix search, ranking, distributed autocomplete |
| 6 | **Design Web Crawler** | BFS/DFS, politeness, dedup, distributed crawling |
| 7 | **Design Ticket Master** | Inventory, seat locking, payments, high concurrency |

---

## Additional LLD Problems to Practice (Not in Repo)

| # | Problem | Key Patterns |
|---|---------|-------------|
| 1 | **Design Chess** | State, Strategy, Command, Observer |
| 2 | **Design ATM** | State, Chain of Responsibility |
| 3 | **Design Hotel Booking** | Strategy (pricing), Observer, Repository |
| 4 | **Design File System** | Composite, Iterator |
| 5 | **Design Tic-Tac-Toe** | State, Strategy (AI), Observer |
| 6 | **Design Online Shopping Cart** | Strategy (discount), Observer, Factory |
| 7 | **Design Stack Overflow** | Observer, Strategy (ranking), Repository |
| 8 | **Design Splitwise** | Strategy, Observer, graph algorithms |
| 9 | **Design Car Rental** | Strategy (pricing), State, Factory |
| 10 | **Design Food Delivery** | State, Strategy, Observer, Factory |

---

## Interview Tips

### For LLD Interviews

1. **Always start by clarifying scope** — "Should I design the full system or focus on core booking flow?"
2. **Draw the class diagram first** — Visual structure before diving into code
3. **Name your patterns** — "I'm using Strategy here because pricing rules may change"
4. **Think about concurrency** — "What happens if two users book the same seat?"
5. **Mention extensibility** — "If we need to add a new vehicle type, we just extend the Vehicle class"
6. **Discuss testing** — "I'd unit test the pricing strategy independently"

### For HLD Interviews

1. **Never jump to the solution** — Ask questions, clarify requirements, estimate scale
2. **Draw the architecture diagram early** — Box-and-arrow diagram within first 10 minutes
3. **Estimate before designing** — "At 100M users, that's ~1,200 QPS, which one server can handle"
4. **Make trade-offs explicit** — "I'm choosing eventual consistency here because strong consistency would limit our write throughput"
5. **Think about failure modes** — "What if the cache goes down? We fall back to DB with degraded latency"
6. **Mention monitoring** — "I'd add latency percentiles (p99), error rates, and cache hit ratio dashboards"
7. **Lead the conversation** — Don't wait for the interviewer to ask; proactively discuss scaling, security, and reliability

---

## Cross-References

| Topic | Where to Study |
|-------|---------------|
| SOLID Principles deep dive | [03_Design_Patterns_SOLID_CleanArch.md](../01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md) |
| Caching patterns (Cache-Aside, Write-Through) | [05_Database_Performance_Tuning.md](../01_TechGuide/05_Database_Performance_Tuning.md) |
| CAP Theorem, Load Balancing, API Gateway | [07_System_Design.md](../01_TechGuide/07_System_Design.md) |
| Kafka, gRPC, Resilience patterns | [06_Microservices_Distributed_Systems.md](../01_TechGuide/06_Microservices_Distributed_Systems.md) |
| Database indexing, sharding, EXPLAIN ANALYZE | [05_Database_Performance_Tuning.md](../01_TechGuide/05_Database_Performance_Tuning.md) |
| Kubernetes, Docker, CI/CD deployment | [10_DevOps_CICD_Docker.md](../01_TechGuide/10_DevOps_CICD_Docker.md) |
| Observability (logs, metrics, traces) | [11_Observability.md](../01_TechGuide/11_Observability.md) |
| API Design, REST, pagination, error handling | [04_API_Design_REST.md](../01_TechGuide/04_API_Design_REST.md) |
| Behavioral & System Design interview framework | [14_Leadership_Behavioral_SystemDesign.md](../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) |
