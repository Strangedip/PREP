# Tech Lead — Conflict Resolution & Performance Management

> **You are here**: Tech Lead — Soft Skills
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§14 Leadership & STAR](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) | **Next**: [Mock Interview Rubric](../Mock/Interview_Rubric.md)

Tech Lead interviews at **Amazon (L6), Google (L5→L6), Microsoft (63→64), Flipkart (SDE3)** probe people leadership beyond STAR templates. This guide gives **frameworks + Java/Spring team scenarios** you can use in behavioral and "leadership" rounds.

---

## Conflict resolution framework

### 1. Technical disagreement (architecture)

**Scenario**: Two senior engineers want Kafka vs REST for order notifications.

| Step | Action | Example script |
|------|--------|----------------|
| **Align on goal** | NFRs first | "We need 50K orders/min and audit trail — let's list constraints." |
| **Data over opinion** | Spike or ADR | "48h spike: publish latency REST 80ms p99 vs Kafka 12ms async." |
| **Decision matrix** | Score options | Use template from [§20 ADRs](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |
| **Decide & document** | ADR + owner | "Choreography SAGA with Kafka — ADR-007, Priya owns rollout." |
| **Disagree & commit** | Losing side supports | "We'll revisit after Q20 if ops burden > 2h/week on-call." |

**Interview STAR hook**: Reference real RFC/ADR; cite metrics from [§11 Observability](../../01_TechGuide/11_Observability.md) (error rate, lag).

---

### 2. Product vs engineering (scope / deadline)

**Scenario**: PM wants feature in 2 weeks; you need 5 for safe [BookMyShow](../../04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md)-level concurrency.

| Approach | When | Script |
|----------|------|--------|
| **Scope cut** | Fixed date immovable | "MVP: single venue, no waitlist — full concurrency in v2." |
| **Phased delivery** | Need learning | Strangler fig from [§07](../../01_TechGuide/07_System_Design.md) |
| **Risk explicit** | PM insists on full scope | Written risk: "Without hold TTL, double-book probability ~X under load test." |
| **Trade-off table** | Data-driven PM | Date vs quality vs tech debt rows |

**Fail answer**: "I told PM no." **Pass answer**: "I proposed phased scope with load-test evidence."

---

### 3. Peer conflict (interpersonal)

| Situation | Lead behavior | Avoid |
|-----------|---------------|-------|
| Public Slack argument | Move to call within 24h | Taking sides in thread |
| Credit dispute | Clarify ownership in retro | Ignoring until escalation |
| Junior bullied by senior | 1:1 both; escalate to EM if pattern | Dismissing as "personality" |

**Framework**: SBI (Situation-Behavior-Impact) feedback — concrete behavior, not character.

```
"SBI: In yesterday's design review (S), you interrupted Arjun three times before he finished the sequence diagram (B), which shut down alternative DB sharding ideas (I). Can we use a round-robin in reviews?"
```

---

### 4. Cross-team dependency conflict

**Scenario**: Platform team delays Redis cluster; your team's [Rate Limiter](../../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) launch slips.

| Action | Detail |
|--------|--------|
| **Make dependency visible** | Shared roadmap doc — [Multi-Team Architecture Review](Multi_Team_Architecture_Review.md) |
| **Mitigation** | Local in-memory limiter with documented debt |
| **Escalation path** | EM + platform lead with customer impact (checkout 429s) |
| **No heroics** | Don't secretly run unmaintained Redis in prod |

---

## Performance management (IC lead, not always EM)

Tech Leads often **input to performance reviews** without being the rating manager.

### What you document (facts, not vibes)

| Category | Evidence to collect |
|----------|---------------------|
| **Delivery** | Shipped features, incident role, on-call participation |
| **Quality** | Post-review defect rate, test coverage on owned modules |
| **Collaboration** | PR review turnaround, design doc feedback |
| **Growth** | Skills gained (e.g. led first HLD), mentoring hours |
| **Impact** | Latency/cost metrics tied to their work |

Link to [CodeQuality.md](../Core/CodeQuality.md) and [§09 Testing](../../01_TechGuide/09_Testing_Strategies.md) for quality bar language.

---

### Underperformance conversation structure

1. **Private 1:1** — Specific missed commitments (dates, tickets)
2. **Expectations** — Reference team working agreement (PR size, review SLA)
3. **Support plan** — Pairing, reduced scope, training ([StudyGuide](../../02_DSA/StudyGuide.md) path if DSA gap)
4. **Timeline** — 30/60 day check with EM aligned
5. **Document** — Summary email to EM, not gossip

**Scenario**: Mid engineer repeatedly breaks prod with missing `@Transactional` on self-invocation.

- **Bad**: "You're careless."
- **Good**: "Last 3 incidents traced to self-invocation bypassing proxy ([§02](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md)). Expectation: service-layer entry points only; I'll pair on first two PRs."

---

### High performer retention

| Lever | Tech lead action |
|-------|------------------|
| **Scope** | Staff-leaning problems — platform, RFCs |
| **Visibility** | Present HLD to architecture council |
| **Learning** | [Tier3 DSA](../../02_DSA/Tier3_Differentiators.md), [AI track](../../05_AI/README.md) for 2026 |
| **Comp** | Advocate with EM using market data — [Comp & Scope](Comp_and_Scope.md) |

---

## Stakeholder communication (PM, EM, VP Eng)

| Audience | They care about | Your format |
|----------|-----------------|-------------|
| **PM** | Date, risk, user impact | 1-page: scope, risks, mitigations |
| **EM** | People, delivery, health | Weekly: RAG status, hiring, attrition risk |
| **VP / Director** | Strategy, cost, reliability | Quarterly: tech debt themes, SLO trends |

Use [§20 estimation](../../01_TechGuide/20_Technical_Leadership_Architecture.md) T-shirt sizing with assumptions explicit.

---

## Amazon LP mapping (India L6)

| LP | Conflict/perf story angle |
|----|---------------------------|
| **Have Backbone; Disagree and Commit** | Architecture disagreement → ADR → commit |
| **Earn Trust** | Underperformer treated fairly with clear plan |
| **Insist on Highest Standards** | Blocked release until load test passed |
| **Deliver Results** | Phased delivery met date with MVP |

Full LP list: [Companies.md](../Core/Companies.md).

---

## Behavioral prep checklist

- [ ] 2 conflict STAR stories (technical + interpersonal)
- [ ] 1 underperformance story with empathy + outcome
- [ ] 1 "raised the bar" story (testing, observability, security)
- [ ] 1 cross-team dependency story with escalation
- [ ] Practiced with [Mock Rubric](../Mock/Interview_Rubric.md) behavioral section
