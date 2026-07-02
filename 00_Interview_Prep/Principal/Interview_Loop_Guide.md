# Principal / Architect Interview Loop Guide

> **You are here**: Principal / Architect — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [Staff Loop Expectations](../Levels/Staff_Loop_Expectations.md), [Multi-Team Architecture Review](../Levels/Multi_Team_Architecture_Review.md) | **Next**: [Comp & Scope](../Levels/Comp_and_Scope.md)

Principal loops differ from Staff: less coding, more **judgment across domains**, **regulatory awareness**, and **long-horizon bets** — using depth from this repo's Senior/Staff content and Principal guides below.

**Principal prep docs**: [Enterprise Architecture Frameworks](Enterprise_Architecture_Frameworks.md), [Multi-Year Vision](Multi_Year_Vision_Build_vs_Buy.md), [Org Design](Organization_Design_Conway_Team_Topologies.md), [Multi-Region DR](Multi_Region_Active_Active_DR.md), [Enterprise Integration](Enterprise_Integration_ESB_iPaaS_Event_Mesh.md), [Executive Communication](Executive_Communication_Board_Narrative.md), [Vendor Rubrics](Vendor_Evaluation_Rubrics.md).

---

## Typical loop structure (India product + FAANG)

| Round | Duration | What they probe | Repo prep |
|-------|----------|-----------------|-----------|
| **Domain architecture** | 60 min | End-to-end system across 5–10 services | [Payment](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md), [NewsFeed](../../04_SystemDesign/02_HighLevelDesign/NewsFeed/NewsFeed.md), [AI System Design](../../05_AI/10_AI_System_Design.md) |
| **Deep dive past system** | 45 min | System you built — trade-offs, mistakes | Your resume + [§20 ADRs](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |
| **Cross-cutting** | 45 min | Security, compliance, cost, org alignment | [§12](../../01_TechGuide/12_Security_OWASP_Cloud.md), [§38 Compliance](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md) |
| **Leadership / influence** | 45 min | Exec communication, saying no, roadmap | [Conflict & Performance](../Levels/Tech_Lead_Conflict_and_Performance.md), [Multi-Team Review](../Levels/Multi_Team_Architecture_Review.md) |
| **Optional coding** | 45 min | Hard design or architecture code (rate limiter, consistent hash) | [Distributed Cache](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md), [Tier3](../../02_DSA/Tier3_Differentiators.md) |

---

## Domain architecture round — how to run 60 min

### Minute 0–10: Scope the business domain

- Users, revenue path, regulatory context (payments → PCI; health → HIPAA overview in [§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md))
- **Do not** jump to microservices diagram first

### Minute 10–20: Capability map

```
Capabilities: Catalog | Cart | Payment | Fulfillment | Notification
Map each to: team owner, primary datastore, SLO
```

### Minute 20–35: Happy path + data flow

Draw critical path (e.g. checkout) with async boundaries — [§06](../../01_TechGuide/06_Microservices_Distributed_Systems.md).

### Minute 35–50: Deep dive on hardest constraint

Pick **one**: consistency (payment), scale (feed), privacy (E2EE — [WhatsApp HLD](../../04_SystemDesign/02_HighLevelDesign/WhatsApp/WhatsApp.md)), AI cost ([MLOps](../../05_AI/09_MLOps_AI_in_Production.md)).

### Minute 50–60: 3-year evolution

- Strangler migrations, deprecation, cost curve
- What you **won't** build (buy vs build judgment — state assumptions; detailed rubrics = SME gap)

---

## Principal-level questions (with repo answers)

| Question | Strong answer structure | Section |
|----------|------------------------|---------|
| "Design payments for India UPI + cards" | Idempotency, ledger, reconciliation, PCI scope | [Payment HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md), [§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md) |
| "Add RAG to support bot" | Ingestion, chunking, eval, fallback | [RAG](../../05_AI/03_RAG_Architecture.md), [AI SD](../../05_AI/10_AI_System_Design.md) |
| "Reduce cloud bill 30%" | Right-size K8s, cache, data lifecycle | [§31 Cloud](../../01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md), [§18 JVM tuning](../../01_TechGuide/18_Performance_Engineering_JVM.md) |
| "Monolith to microservices" | Strangler fig, don't big-bang | [§07](../../01_TechGuide/07_System_Design.md), [§20 ADR example](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |
| "Event vs REST between teams" | Coupling, ordering, debuggability table | [§06 vs §19](../../01_TechGuide/06_Microservices_Distributed_Systems.md) |

---

## "System you've built" round

### Structure

1. **Context** — Business, scale (QPS, teams), your role (Staff/Principal scope)
2. **Architecture diagram** — 3 layers max on whiteboard
3. **Hardest decision** — ADR format with rejected options
4. **Failure** — Incident or wrong bet; what changed
5. **Metrics** — Before/after latency, cost, deploy frequency

### Credibility signals

- Named **trade-offs**, not only successes
- Referenced **SLOs** and error budgets — [§11](../../01_TechGuide/11_Observability.md), [§23 SRE](../../01_TechGuide/23_SRE_Reliability_Engineering.md)
- Showed **org** impact (3+ teams adopted your RFC)

---

## Principal vs Staff — interview difference

| Staff interview | Principal interview |
|-----------------|---------------------|
| "Design rate limiter" | "Design platform rate limiting for 200 services" |
| "Fix team Kafka usage" | "Event strategy for commerce domain" |
| Deep in one service | Consistency across bounded contexts |
| Mentor seniors | Influence EMs and product directors |

Use [Advance Criteria](../Levels/Staff_Principal_Advance_Criteria.md) to self-score.

---

## 4-week prep (this repo only)

| Week | Focus |
|------|-------|
| 1 | 3 flagship HLDs whiteboard: Payment, Chat, NewsFeed |
| 2 | [§06](../../01_TechGuide/06_Microservices_Distributed_Systems.md) + [§19](../../01_TechGuide/19_Event_Driven_Architecture.md) + [§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md) |
| 3 | [05_AI](../../05_AI/) for AI-native products — [10 AI System Design](../../05_AI/10_AI_System_Design.md) |
| 4 | Stories: 2 org-level RFCs + [Mock Rubric](../Mock/Interview_Rubric.md) architecture section |

---

## What not to fake

If interviewer goes deep on **TOGAF, active-active multi-region DR, enterprise service bus** — acknowledge boundary: "I'd partner with infra SME; my approach would start with RPO/RTO requirements and [§07] scaling patterns." Do not invent framework detail (see ROADMAP SME gaps).
