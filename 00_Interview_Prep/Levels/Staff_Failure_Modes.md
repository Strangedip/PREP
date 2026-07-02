# Staff Engineer Interview Failure Modes

> **You are here**: Staff Engineer — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md#staff-engineer) | **Prerequisites**: [Staff Loop Expectations](Staff_Loop_Expectations.md), [Tier3 DSA](../../02_DSA/Tier3_Differentiators.md) | **Next**: [Principal Interview Loop](../Principal/Interview_Loop_Guide.md)

Staff loops eliminate candidates who are **strong Senior ICs** but cannot demonstrate **cross-team technical leverage** — platform thinking, standards, influence without authority, and org-level trade-offs.

---

## Failure modes by round

### Architecture / vision (45–60 min)

| Failure mode | Symptom | Fix |
|--------------|---------|-----|
| **Single-team myopia** | Design only their squad's service | [Multi-Team Architecture Review](Multi_Team_Architecture_Review.md) |
| **No 12-month roadmap** | Only solves today's incident | [§24 Platform Engineering](../../01_TechGuide/24_Platform_Engineering_IDP.md) |
| **Build everything** | No build-vs-buy matrix | [Staff/Principal Advance Criteria](Staff_Principal_Advance_Criteria.md) |
| **Ignores Conway's Law** | Org chart mismatch with architecture | [Org Design](../Principal/Organization_Design_Conway_Team_Topologies.md) |

### System design (platform scale)

| Failure mode | Fix |
|--------------|-----|
| Metrics system as afterthought | [Metrics Monitoring HLD](../../04_SystemDesign/02_HighLevelDesign/MetricsMonitoring/MetricsMonitoring.md) |
| Cache design without invalidation story | [Distributed Cache](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) |
| No multi-tenant isolation | Payment / SaaS patterns in [Payment System](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |
| Missing SLO/error budget | [§23 SRE](../../01_TechGuide/23_SRE_Reliability_Engineering.md) |

### Coding / design (60 min)

| Failure mode | Fix |
|--------------|-----|
| Cannot implement LFU / rate limiter | [LFU Cache](../../02_DSA/05_Linked_Lists/LFUCache/LFUCache.md), [Rate Limiter HLD](../../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) |
| Graph theory hand-wave | [Critical Connections](../../02_DSA/11_Graphs/CriticalConnections/CriticalConnections.md), [Alien Dictionary](../../02_DSA/11_Graphs/AlienDictionary/AlienDictionary.md) |
| Concurrency bugs in interview | [Producer-Consumer](../../02_DSA/18_Concurrency_Multithreading/ProducerConsumer/ProducerConsumer.md) |

### Behavioral (influence)

| Failure mode | Fix |
|--------------|-----|
| "I told them to use my design" | Influence story: pilot → metrics → adoption |
| No failure admission | Post-mortem where your decision contributed |
| Cannot say no to executive | Prioritization framework in §20 |
| Staff title inflation | Scope evidence: teams affected, not headcount |

---

## Staff vs Tech Lead — eliminator difference

| Eliminated at Staff because... | Still fine at Tech Lead |
|--------------------------------|-------------------------|
| Cannot design golden path for 5 teams | Designs one team's service well |
| No cross-team ADR examples | Team-level tech decisions only |
| Depth in product only, not primitives | Deep product domain |
| Weak platform / infra narrative | Strong delivery leadership |

---

## Recovery checklist

- [ ] [SelfAssessment](../Core/SelfAssessment.md) ≥ 160; Sections 22–26 ≥ 4/5
- [ ] Whiteboard [Distributed Cache](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) with consistent hashing + failure domains
- [ ] 2 cross-team STAR stories rehearsed
- [ ] [ARF template](Multi_Team_Architecture_Review.md) — run mock review on fictional RFC

**Next**: [Principal Interview Loop Guide](../Principal/Interview_Loop_Guide.md)
