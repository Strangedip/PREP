# SDE2 Interview Failure Modes — What Eliminates Mid-Level Candidates

> **You are here**: SDE2 — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [StudyGuide Path 2](../../02_DSA/StudyGuide.md), [Machine Coding Guide](../../03_CodingPatterns/Machine_Coding_Round_Guide.md) | **Next**: [Senior Loop Failure Modes](Senior_Failure_Modes.md)

Mid-level loops at **Flipkart, Amazon, Microsoft, Razorpay, PhonePe** test whether you can own a feature end-to-end: medium DSA, Spring Boot depth, REST/DB design, and clean code. This file maps **observed eliminators** to fixes in this repo — not generic interview advice.

---

## Round-by-round failure modes

### Coding (DSA) — 45 min

| Failure mode | What interviewer saw | Fix in this repo |
|--------------|---------------------|------------------|
| **Silent coding** | No clarification, no pattern named | [Interview Playbook §3](../Core/InterviewPlaybook.md) — talk through Understand → Plan → Code |
| **Brute force stuck** | Never reaches optimal after hint | [Algorithmic Patterns](../../03_CodingPatterns/02_AlgorithmicPatterns.md) — 2–3 min pattern ID |
| **Wrong complexity** | Says O(n) but nested loops | [CS Fundamentals § Big-O](../../01_TechGuide/00_Computer_Science_Fundamentals.md) |
| **No edge cases** | Empty input, duplicates, overflow | Every `02_DSA` problem "Edge Cases" section |
| **Java sloppy** | `null` NPE, raw types, no `long` for sums | [CodeQuality.md](../Core/CodeQuality.md) |
| **One-trick pony** | Only knows HashMap; misses two-pointer | Path 2 weeks 1–4 in [StudyGuide](../../02_DSA/StudyGuide.md) |

**Self-check before scheduling SDE2 loops**: Path 2 success metrics — 70% Medium in 30–40 min, pattern in 2–3 min.

---

### Java / Spring technical depth — 45–60 min

| Failure mode | Symptom | Repo fix |
|--------------|---------|----------|
| **Surface Spring** | "Spring is a framework" only | [§02 Bean lifecycle, @Transactional proxy](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) |
| **Concurrency hand-wave** | "I'd use threads" | [§02.2 CompletableFuture, CHM](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md), [§15 Deep Dive](../../01_TechGuide/15_Java_Collections_Concurrency_DeepDive.md) |
| **equals/hashCode blank** | Cannot explain HashMap bucket | [Java OOP Fundamentals](../../01_TechGuide/00_Java_OOP_Fundamentals.md) |
| **JWT/OAuth buzzwords** | No PKCE, no refresh rotation | [§02.4 Security](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) |
| **No production story** | Never mentions logs, metrics, idempotency | [§11 Observability](../../01_TechGuide/11_Observability.md) preview |

**SelfAssessment target**: Sections 1–5 average ≥ 3.5/5 before Amazon/Flipkart mid loops.

---

### Machine coding / LLD — 90 min

| Failure mode | Symptom | Repo fix |
|--------------|---------|----------|
| **Diagram only** | 60 min UML, 10 min code | [Machine Coding Guide](../../03_CodingPatterns/Machine_Coding_Round_Guide.md) time budget |
| **God service** | 800-line class | [Parking Lot](../../04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) layering |
| **No tests** | "I'd add tests later" | Machine guide — minimum 2 unit tests |
| **Inventory race** | Double booking in seat/room demo | [BookMyShow](../../04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md), [HotelBooking](../../04_SystemDesign/01_LowLevelDesign/HotelBooking/HotelBooking.md) |
| **Pattern name-drop** | Says "Strategy" with if-else | [GoF Patterns](../../03_CodingPatterns/01_Patterns.md) + justify in one sentence |

---

### System design (light / hybrid mid loops) — 45 min

| Failure mode | Symptom | Repo fix |
|--------------|---------|----------|
| **API-only design** | No data model or QPS | [LLD Template](../../04_SystemDesign/00_Templates/LLD_Template/LLD_Template.md) |
| **Cache afterthought** | "Add Redis" with no key/TTL | [§05 Database + Redis](../../01_TechGuide/05_Database_Performance_Tuning.md) |
| **No failure talk** | Happy path only | [URL Shortener](../../04_SystemDesign/02_HighLevelDesign/URLShortener/URLShortener.md) — cache miss, DB down |
| **Over-engineer** | Kafka for 100 QPS CRUD | [§07 System Design](../../01_TechGuide/07_System_Design.md) — start simple, scale triggers |

---

### REST / API design (common in Java product companies)

| Failure mode | Example bad answer | Good answer anchor |
|--------------|-------------------|-------------------|
| **POST for reads** | Search via POST without reason | [§04 REST verbs, idempotency](../../01_TechGuide/04_API_Design_REST.md) |
| **500 for validation** | No 400/422 distinction | RFC 7807 problem+json in §04 |
| **Unversioned breaking change** | New field breaks mobile | Versioning + deprecation in §04 |
| **Pagination hand-wave** | `offset` only at 10M rows | Cursor vs offset trade-off in §04 |

---

## Behavioral (mid-level still counts — 15–20%)

| Failure mode | Fix |
|--------------|-----|
| Hypothetical answers ("I would…") | STAR from real internship/project — [§14](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) |
| Only "we" | Use "I" for your commits, design doc, bug you fixed |
| No metric | "Reduced build time 40%" beats "improved CI" |

Prepare **4 stories**: tight deadline, production bug, disagreement with senior, mentored junior/intern.

---

## India-specific SDE2 loop patterns

| Company | Extra trap | Prep |
|---------|-----------|------|
| **Amazon** | LP every round | Map stories to 16 LPs — [Companies.md](../Core/Companies.md) |
| **Flipkart** | Machine coding + DSA same day | [Machine Coding Guide](../../03_CodingPatterns/Machine_Coding_Round_Guide.md) + Parking Lot timed |
| **PhonePe / Razorpay** | Payments idempotency, concurrency | [Payment System HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) concepts at LLD level |
| **Microsoft** | Clean code + moderate DSA | [CodeQuality.md](../Core/CodeQuality.md) + Path 2 |

---

## Recovery checklist (1 week before loop)

- [ ] 2 timed mediums from Path 2 — explain pattern before coding
- [ ] Explain `@Transactional` self-invocation failure with proxy diagram
- [ ] Whiteboard [Rate Limiter](../../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) at LLD depth (token bucket)
- [ ] [SelfAssessment](../Core/SelfAssessment.md) Sections 3–5 — no item below 3/5
- [ ] One machine coding dry run: [VendingMachine](../../04_SystemDesign/01_LowLevelDesign/VendingMachine/VendingMachine.md) in 90 min

**Ready to target Senior prep when**: Path 2 complete + [Senior Failure Modes](Senior_Failure_Modes.md) self-check passes.
