# Staff Engineer Interview Failure Modes

> **You are here**: Staff Engineer — Interview Prep
> **Depth**: Standard (failure patterns with example stories and recovery drills)
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

**Example failure story (architecture):**

> Candidate designed a new notification service with its own Kafka cluster, Redis, and custom scheduler — for 2 teams sending 500 emails/day. Interviewer asked about using existing platform event bus. Candidate doubled down: "We need full control."
>
> **Why eliminated**: Staff engineers reduce org complexity, not add silos. Missing build-vs-buy and platform leverage.
>
> **Better answer**: "I'd publish to the existing `domain-events` topic, use the platform email adapter, and only build custom logic for template routing. If volume hits 100K/day, we'd RFC a dedicated service with SRE sign-off."

---

### System design (platform scale)

| Failure mode | Fix |
|--------------|-----|
| Metrics system as afterthought | [Metrics Monitoring HLD](../../04_SystemDesign/02_HighLevelDesign/MetricsMonitoring/MetricsMonitoring.md) |
| Cache design without invalidation story | [Distributed Cache](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) |
| No multi-tenant isolation | Payment / SaaS patterns in [Payment System](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |
| Missing SLO/error budget | [§23 SRE](../../01_TechGuide/23_SRE_Reliability_Engineering.md) |

**Example failure story (cache):**

> Candidate proposed "put everything in Redis" for a product catalog with 2M SKUs. No TTL, no invalidation on product update, no cache stampede mitigation.
>
> **Recovery drill**: For any cache question, always answer: (1) what keys, (2) TTL, (3) invalidation trigger, (4) cache miss behavior, (5) thundering herd protection.

---

### Coding / design (60 min)

| Failure mode | Fix |
|--------------|-----|
| Cannot implement LFU / rate limiter | [LFU Cache](../../02_DSA/05_Linked_Lists/LFUCache/LFUCache.md), [Rate Limiter HLD](../../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) |
| Graph theory hand-wave | [Critical Connections](../../02_DSA/11_Graphs/CriticalConnections/CriticalConnections.md), [Alien Dictionary](../../02_DSA/11_Graphs/AlienDictionary/AlienDictionary.md) |
| Concurrency bugs in interview | [Producer-Consumer](../../02_DSA/18_Concurrency_Multithreading/ProducerConsumer/ProducerConsumer.md) |

---

### Behavioral (influence)

| Failure mode | Fix |
|--------------|-----|
| "I told them to use my design" | Influence story: pilot → metrics → adoption |
| No failure admission | Post-mortem where your decision contributed |
| Cannot say no to executive | Prioritization framework in §20 |
| Staff title inflation | Scope evidence: teams affected, not headcount |

**Example influence story (good):**

> "Three teams had divergent retry logic causing duplicate charges. I didn't mandate a library. I built a 2-week pilot with Payments team, measured 60% reduction in duplicate retries, presented at eng guild, and two other teams adopted voluntarily. Fourth team had valid edge cases — we extended the library together."

**Example influence story (bad):**

> "I sent an email saying everyone must use my retry library." → No pilot, no metrics, no adoption story.

---

## Staff vs Tech Lead — eliminator difference

| Eliminated at Staff because... | Still fine at Tech Lead |
|--------------------------------|-------------------------|
| Cannot design golden path for 5 teams | Designs one team's service well |
| No cross-team ADR examples | Team-level tech decisions only |
| Depth in product only, not primitives | Strong delivery leadership |
| Weak platform / infra narrative | Strong delivery leadership |

---

## Recovery checklist (4-week plan)

### Week 1 — Evidence audit
- [ ] List 3 decisions that affected **2+ teams** (RFCs, ADRs, standards)
- [ ] [SelfAssessment](../Core/SelfAssessment.md) ≥ 160; Sections 22–26 ≥ 4/5

### Week 2 — System design depth
- [ ] Whiteboard [Distributed Cache](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) with consistent hashing + failure domains
- [ ] Practice [Metrics Monitoring](../../04_SystemDesign/02_HighLevelDesign/MetricsMonitoring/MetricsMonitoring.md) end-to-end

### Week 3 — Behavioral stories
- [ ] Write 2 cross-team STAR stories (influence + failure admission)
- [ ] Rehearse "build vs buy" answer for your domain

### Week 4 — Mock
- [ ] [ARF template](Multi_Team_Architecture_Review.md) — run mock review on fictional RFC
- [ ] Tier 3 coding: [LFU Cache](../../02_DSA/05_Linked_Lists/LFUCache/LFUCache.md) in 45 min

**Next**: [Principal Interview Loop Guide](../Principal/Interview_Loop_Guide.md)
