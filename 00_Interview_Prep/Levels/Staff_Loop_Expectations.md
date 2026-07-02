# Staff Engineer Loop — Expectations Guide

> **You are here**: Staff Engineer — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md), [Tier3 DSA](../../02_DSA/Tier3_Differentiators.md) | **Next**: [Platform Engineering](../../01_TechGuide/24_Platform_Engineering_IDP.md)

## How Staff differs from Tech Lead in interviews

| Area | Tech Lead | Staff Engineer |
|------|-----------|----------------|
| **Scope** | One team, delivery + mentoring | Multiple teams, org-wide technical direction |
| **System design** | Owns service architecture | Owns platform, standards, cross-cutting concerns |
| **Coding** | Strong medium/hard | Hard + **design** problems (cache, rate limiter) |
| **Behavioral** | Team leadership STAR | **Influence without authority**, roadmap, tech debt at scale |
| **Depth** | Deep in product domain | Deep in **primitives** reused by many teams |

This repo's **Lead** content maps to both; Staff loops weight [§24 Platform Engineering](../../01_TechGuide/24_Platform_Engineering_IDP.md), [Tier3 DSA](../../02_DSA/Tier3_Differentiators.md), and cross-team narratives more heavily.

---

## Typical Staff loop structure

| Round | Duration | Focus |
|-------|----------|-------|
| **Coding / design** | 60 min | Hard DSA or design data structure (LRU, rate limiter, segment tree) |
| **System design** | 60 min | Platform-scale (metrics, messaging, multi-tenant) |
| **Architecture / vision** | 45–60 min | Past org-level decisions, ADRs, build vs buy |
| **Behavioral / leadership** | 45 min | Influence, conflict across teams, technical strategy |

Not every company labels "Staff" the same — confirm level mapping with recruiter ([Companies.md](../Core/Companies.md)).

---

## Technical signals interviewers want

### Coding / design

- [ ] Optimal solution with proof sketch ([Dijkstra](../../02_DSA/11_Graphs/DijkstraAlgorithm/DijkstraAlgorithm.md), [Segment Tree](../../02_DSA/17_Advanced_Miscellaneous/SegmentTree/SegmentTree.md))
- [ ] Discuss memory, concurrency, failure modes unprompted
- [ ] Connect to production ([CrossReferences](../Core/CrossReferences.md))

### System design

- [ ] Draw **failure domains** and blast radius
- [ ] Capacity math with stated assumptions ([HLD Template](../../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md))
- [ ] Operability: metrics, alerts, rollout ([§11 Observability](../../01_TechGuide/11_Observability.md))

### Architecture discussion

- [ ] 2+ ADR examples with alternatives rejected ([§20](../../01_TechGuide/20_Technical_Leadership_Architecture.md))
- [ ] Platform vs product team trade-offs ([§24](../../01_TechGuide/24_Platform_Engineering_IDP.md))
- [ ] 12–18 month technical roadmap story (even if hypothetical)

---

## Behavioral questions (prepare 2 each)

1. **Influence**: Convinced another team to adopt your approach without reporting line
2. **Ambiguity**: Defined technical direction when product scope was unclear
3. **Standards**: Introduced golden path / lint / IDP that reduced toil
4. **Conflict**: Disagreed with architect or PM on build vs buy — outcome
5. **Mentorship at scale**: Grew senior engineers across teams (not just direct reports)

Use STAR; quantify impact (latency, deploy frequency, incident reduction).

---

## Trade-off: Staff IC vs Tech Lead manager track

| Track | Interview emphasis | This repo focus |
|-------|-------------------|-----------------|
| **Staff IC** | Depth, cross-team architecture | §24, Tier3, Payment/Metrics HLD |
| **Engineering Manager** | People, delivery, hiring | §14, §20, behavioral — **less DSA depth** |

Clarify track with recruiter; some companies combine titles.

---

## Ready for Staff loops when

- [ ] [SelfAssessment](../Core/SelfAssessment.md) total ≥ 160; Sections 22–26 ≥ 4/5
- [ ] [StudyGuide Path 3](../../02_DSA/StudyGuide.md) success metrics met
- [ ] Can whiteboard [Distributed Cache](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) or [Payment System](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) with ops depth
- [ ] 4+ cross-team STAR stories rehearsed

**Gap**: Principal-level content is in [Principal/](../Principal/) — see [Principal Failure Modes](Principal_Failure_Modes.md) and [Interview Loop Guide](../Principal/Interview_Loop_Guide.md).
