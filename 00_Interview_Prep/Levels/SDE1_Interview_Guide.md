# SDE1 Interview Guide — Loops, Expectations & Failure Modes

> **You are here**: SDE1 — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md#sde1) | **Prerequisites**: [Fresher Failure Modes](Fresher_Failure_Modes.md), [StudyGuide Path 1 complete](../../02_DSA/StudyGuide.md) | **Next**: [SDE2 Failure Modes](SDE2_Failure_Modes.md)

SDE1 (0–2 years) loops test **independent feature delivery**: Easy+ intro Medium DSA, basic Spring REST, simple LLD, and clean code. You are not expected to design Kafka clusters — you **are** expected to ship a working endpoint with sensible error handling.

---

## What SDE1 ownership looks like

| Dimension | SDE1 scope |
|-----------|------------|
| **Code** | Features in one service/repo with review |
| **Design** | Component-level; class diagrams for machine coding |
| **Decisions** | Implementation choices inside defined API contract |
| **On-call** | Rare or shadow only |
| **Mentoring** | None expected; learning from seniors |

---

## Typical SDE1 loop structure

| Round | Duration | Focus | Prep file |
|-------|----------|-------|-----------|
| **OA / screen** | 60–90 min | 1–2 Medium or 2 Easy | [Algorithmic Patterns](../../03_CodingPatterns/02_AlgorithmicPatterns.md) |
| **Coding** | 45 min | Easy–Medium, arrays/trees/stacks | Path 1 completion problems in [ROADMAP](../../ROADMAP.md#sde1) |
| **Java / Spring** | 45–60 min | DI, REST, JPA basics, exceptions | [§02 sections 2.3–2.4](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) |
| **Machine coding / LLD** | 60–90 min | Parking lot, vending machine, elevator | [Machine Coding Guide](../../03_CodingPatterns/Machine_Coding_Round_Guide.md) |
| **Manager / HR** | 30 min | Project ownership, collaboration | [§14 STAR basics](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) |

---

## Failure modes by round

### Coding

| Failure mode | Fix |
|--------------|-----|
| Only solves Easy; Medium blank | [Best Time to Buy Stock](../../02_DSA/01_Arrays_Matrix/BestTimeToBuyAndSellStock/BestTimeToBuyAndSellStock.md), [Coin Change](../../02_DSA/13_Dynamic_Programming/CoinChange/CoinChange.md) |
| No pattern in 5 min | [Pattern decision tree](../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-recognition-decision-tree) |
| Messy Java (no `Optional`, raw types) | [CodeQuality](../Core/CodeQuality.md) |

### Spring / backend

| Failure mode | Fix |
|--------------|-----|
| Cannot explain `@RestController` → JSON flow | [§02.3 Spring lifecycle](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) |
| No validation / error response shape | [§04 API Design](../../01_TechGuide/04_API_Design_REST.md) — RFC 7807 |
| `@Transactional` misunderstood | Self-invocation proxy trap in §02 |
| N+1 query in JPA example | [§05 N+1](../../01_TechGuide/05_Database_Performance_Tuning.md) |

### Machine coding / LLD

| Failure mode | Fix |
|--------------|-----|
| All logic in `main()` | [Parking Lot](../../04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) |
| No interface boundaries | [LLD Template](../../04_SystemDesign/00_Templates/LLD_Template/LLD_Template.md) |
| Runs out of time on boilerplate | [Machine Coding Guide](../../03_CodingPatterns/Machine_Coding_Round_Guide.md) Spring skeleton |

### Light system design (some SDE1 loops)

| Failure mode | Fix |
|--------------|-----|
| Jumps to microservices | Monolith + DB + cache for <1k QPS |
| No schema | Even 3 tables on whiteboard |
| Missing auth | JWT flow from [Web Fundamentals](../../01_TechGuide/00_Web_Fundamentals.md) |

---

## Companies hiring SDE1 into India

See [Companies.md](../Core/Companies.md) — filter by level column. Typical: product startups, GCCs (Google, Microsoft, Amazon), Indian unicorns, service companies with product benches.

**Hiring modes** (all India-based work): local office (BLR/HYD/Pune), hybrid, remote-for-India, occasional relocation-within-India. Global HQ roles that require emigration are out of scope for this guide.

---

## Self-assessment before SDE1 loops

- [ ] [SelfAssessment](../Core/SelfAssessment.md) Sections 0–3 average ≥ 3/5
- [ ] Explain REST CRUD + pagination without notes
- [ ] Timed: [Add Two Numbers](../../02_DSA/05_Linked_Lists/AddTwoNumbers/AddTwoNumbers.md) + [Validate BST](../../02_DSA/08_Trees_Binary_Trees/ValidateBinarySearchTree/ValidateBinarySearchTree.md) in 45 min total
- [ ] 90-min dry run: [Vending Machine](../../04_SystemDesign/01_LowLevelDesign/VendingMachine/VendingMachine.md)
- [ ] 2 STAR stories: bug you fixed, feature you owned end-to-end

**Ready for SDE2 prep when**: Path 1 complete + [SDE2 Failure Modes](SDE2_Failure_Modes.md) prerequisites met.
