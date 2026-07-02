# Senior SDE Interview Failure Modes — System Design & Depth

> **You are here**: Senior SDE — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [SDE2 Failure Modes](SDE2_Failure_Modes.md), [HLD Template](../../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md) | **Next**: [Staff Loop Expectations](Staff_Loop_Expectations.md)

Senior loops assume you **ship production systems** in Java/Spring microservices. Failure is rarely "cannot code" — it's **shallow trade-offs**, missing NFRs, and inability to go deep on failure domains.

---

## System design round — top eliminators

| Failure mode | What you did | What senior bar requires | Repo drill |
|--------------|--------------|--------------------------|------------|
| **Feature list architecture** | Boxes: API, DB, Cache | QPS, storage, read/write ratio first | [HLD Template § estimation](../../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md) |
| **No NFRs** | Skipped latency, consistency, availability | State p99, CAP choice for this product | [§07 System Design](../../01_TechGuide/07_System_Design.md) |
| **Single region blob** | One DB, no replication story | Multi-AZ, failover, RPO/RTO one-liner | [Payment System](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |
| **Cache without invalidation** | "Redis for reads" | Cache-aside, TTL, stampede — when to use | [§05 + §28 Redis](../../01_TechGuide/05_Database_Performance_Tuning.md) |
| **Kafka for everything** | Event bus as default | Sync vs async decision table | [§06 vs §19](../../01_TechGuide/06_Microservices_Distributed_Systems.md) |
| **No idempotency** | Double charge on retry | Idempotency keys, outbox | [Payment System](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |
| **Celebrity problem hand-wave** | "We'll shard" | Fan-out on write vs read — hybrid | [NewsFeed](../../04_SystemDesign/02_HighLevelDesign/NewsFeed/NewsFeed.md) |
| **Cannot deep dive** | Stays L4 diagram 40 min | Schema + one bottleneck + scale fix | Pick 2: [Chat](../../04_SystemDesign/02_HighLevelDesign/ChatSystem/ChatSystem.md), [Twitter](../../04_SystemDesign/02_HighLevelDesign/Twitter/Twitter.md) |

### NFR checklist (state aloud in first 10 min)

| NFR | Question to answer | Example (chat) |
|-----|-------------------|----------------|
| Scale | DAU, QPS peak, message size | 50M DAU, 1M concurrent WS |
| Latency | p99 delivery | < 200ms online |
| Consistency | Strong vs eventual | Per-conversation ordering strong; feed eventual |
| Durability | Message loss acceptable? | At-least-once + dedup |
| Security | AuthN/Z, encryption | JWT + TLS; E2EE = [WhatsApp HLD](../../04_SystemDesign/02_HighLevelDesign/WhatsApp/WhatsApp.md) |

---

## Coding round at senior bar

| Failure mode | Signal | Fix |
|--------------|--------|-----|
| Medium only | Struggles on hard follow-up | [StudyGuide Path 3](../../02_DSA/StudyGuide.md), [Tier3](../../02_DSA/Tier3_Differentiators.md) |
| No follow-up handling | "What if stream?" → stuck | [CrossReferences](../Core/CrossReferences.md) — heap, trie, segment tree |
| Cannot optimize further | O(n²) accepted for hard | [Advanced DP](../../02_DSA/13_Dynamic_Programming/AdvancedDP/AdvancedDP.md) |
| Design problem shallow | LRU without thread-safety discussion | [LRU Cache](../../02_DSA/05_Linked_Lists/LRUCache/LRUCache.md) + [ReadWriteLock](../../02_DSA/18_Concurrency_Multithreading/ReadWriteLock/ReadWriteLock.md) |

**Senior coding bar** (from [StudyGuide Path 3](../../02_DSA/StudyGuide.md)): 60% Hard, connect choice to system design ("this is consistent hashing in [Distributed Cache](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md)").

---

## Java / distributed systems depth

| Topic | Fail if you cannot… | Section |
|-------|---------------------|---------|
| Microservices | Compare SAGA choreography vs orchestration | [§06](../../01_TechGuide/06_Microservices_Distributed_Systems.md) |
| Resilience | Explain circuit breaker half-open | [§06 Resilience4j](../../01_TechGuide/06_Microservices_Distributed_Systems.md) |
| JVM prod | Read GC log, suggest flag | [§18 Performance](../../01_TechGuide/18_Performance_Engineering_JVM.md) |
| Observability | Define SLI/SLO for your service | [§11](../../01_TechGuide/11_Observability.md) |
| Events | When outbox vs dual write | [§19 Event-Driven](../../01_TechGuide/19_Event_Driven_Architecture.md) |

---

## LLD at senior bar (still asked — BookMyShow, Elevator)

| Failure mode | Fix |
|--------------|-----|
| Ignores concurrency | Seat hold TTL, `synchronized` vs DB lock — [BookMyShow](../../04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md) |
| No extension path | "Add waitlist" / "Add VIP pricing" — Strategy pattern |
| Missing observability | Log hold expiry, metrics on contention |

---

## Behavioral at senior (not optional)

| Failure mode | Senior expectation |
|--------------|-------------------|
| Only individual heroics | Show **multi-team** impact — incident commander, RFC driver |
| Blame other teams | "Disagree and commit" with data — [§14](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) |
| No post-mortem story | Blameless RCA, action items — [§20](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |

Prepare: **production outage**, **technical debt trade-off**, **cross-team dependency conflict**, **mentoring senior peer**.

---

## AI / RAG questions (2026 senior bar)

| Failure mode | Fix |
|--------------|-----|
| "Call OpenAI API" only | Chunking, retrieval, eval — [RAG Architecture](../../05_AI/03_RAG_Architecture.md) |
| No cost/latency | Token budget, semantic cache — [MLOps AI](../../05_AI/09_MLOps_AI_in_Production.md) |
| Ignores safety | PII, prompt injection — [AI Ethics](../../05_AI/11_AI_Ethics_Safety_Governance.md) |

---

## 2-week senior loop prep (this repo)

| Day | Focus |
|-----|-------|
| 1–3 | 2 HLD outlines/day from [System Design README](../../04_SystemDesign/README.md) flagship list |
| 4–5 | [§06 Microservices](../../01_TechGuide/06_Microservices_Distributed_Systems.md) + [§19 Events](../../01_TechGuide/19_Event_Driven_Architecture.md) |
| 6–7 | Path 3 hard graph/DP — [Dijkstra](../../02_DSA/11_Graphs/DijkstraAlgorithm/DijkstraAlgorithm.md), [Segment Tree](../../02_DSA/17_Advanced_Miscellaneous/SegmentTree/SegmentTree.md) |
| 8 | [Observability](../../01_TechGuide/11_Observability.md) + [Security](../../01_TechGuide/12_Security_OWASP_Cloud.md) |
| 9 | [05_AI](../../05_AI/) skim for integration interview |
| 10 | Full mock: 1 HLD + 1 hard coding — [Mock Rubric](../Mock/Interview_Rubric.md) |

**Pass signal**: [SelfAssessment](../Core/SelfAssessment.md) total ≥ 130, Sections 6–11 ≥ 4/5.
