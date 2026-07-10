# Product Partnership — Working with PM, Design, and Roadmaps

> **You are here**: SDE2 → Principal — Cross-functional delivery
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§20 Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md), [RFC/ADR](../../06_On_The_Job/04_RFC_ADR_Writing.md) | **Next**: [Mid-Career Playbook](Mid_Career_Switch_Negotiate_Plateau.md)

Strong engineers who "just want tickets" stall at Senior. Promotion and Principal loops reward **product judgment**: sequencing, saying no with options, and translating tech risk into business language. This is not a PM textbook — it is the engineer side of the partnership.

---

## Roles (RACI for a feature)

| Decision | PM | Eng (you) | Design | EM |
|----------|----|-----------|--------|-----|
| Problem / outcome | **A** | C | C | I |
| Scope cut for deadline | C | **A** (feasibility) | C | A (capacity) |
| Technical approach | I | **A** | C (UX constraints) | C |
| Launch / rollback | A | **A** (prod readiness) | C | A |
| Tech debt allocation | C | **A** (proposal) | I | A (capacity) |

**A** = accountable, **C** = consulted, **I** = informed.

---

## Intake: turn a vague ask into an engineering problem

When PM says "We need faster checkout":

| Question you ask | Why |
|------------------|-----|
| What metric moves? (conversion, p99, drop-off step) | Avoid building the wrong thing |
| What is the deadline driver? (festival, contract, competitor) | Sequencing |
| What is explicitly out of scope? | Non-goals |
| What is the rollback story? | Production ownership |
| What data do we already have? | Avoid opinion wars |

**Output**: 1-page problem brief → then RFC if cross-team ([Multi-Team AR](../Levels/Multi_Team_Architecture_Review.md)).

---

## Prioritization frameworks engineers actually use

### 1. Cost of delay × risk

| Item | User value | Urgency | Eng cost | Risk if late | Score intuition |
|------|------------|---------|----------|--------------|-----------------|
| Payment idempotency fix | High | High | M | Sev-1 | Do now |
| New promo banner API | Med | High | S | Low | After fix |
| Rewrite notifications in Go | Low | Low | XL | None | Defer |

### 2. Capacity split (default healthy team)

| Bucket | % capacity | Examples |
|--------|------------|----------|
| Features / experiments | 50–60% | Roadmap commitments |
| Reliability / debt | 20–30% | SLO burn, migrations ([§39](../../01_TechGuide/39_Monolith_to_Microservices_Migration.md)) |
| Support / interrupts | 10–20% | Bugs, on-call follow-ups |
| Learning / spikes | 5–10% | Spikes with time box |

Negotiate the **%**, not every ticket. Document in quarterly planning.

### 3. Build vs buy / build vs configure

Same matrix as Principal vision — [Build-vs-Buy](../Principal/Multi_Year_Vision_Build_vs_Buy.md) — applied to a single epic.

---

## Saying no (with options)

Never "no" alone. Always **no + alternative + cost**.

| PM ask | Engineer response |
|--------|-------------------|
| "Ship multi-region this sprint" | "We can ship **DR runbook + failover drill** this sprint. Active-active writes need conflict model — 2 quarters. Options: (A) drill, (B) read-replica in second AZ, (C) full active-active." |
| "Add AI chatbot to every page" | "Pilot on Help Center with RAG eval gates ([05_AI](../../05_AI/README.md)). Org-wide rollout after hallucination rate < X%." |
| "Rewrite in two weeks" | "Strangler phase 1 extracts payment API in 6 weeks with shadow traffic. Big-bang rewrite fails festival freeze." |

Exec-facing version: [Executive Communication](../Principal/Executive_Communication_Board_Narrative.md).

---

## Design collaboration

| Topic | Engineer owns | Design owns |
|-------|---------------|-------------|
| Empty / error / loading states | API contracts, idempotency | UX copy and layout |
| Performance budget | p99, payload size | Perceived performance patterns |
| Accessibility | Semantic HTML / Angular a11y | Visual contrast, flows |
| Experimentation | Flag plumbing, metrics | Variant UX |

**Anti-pattern**: Backend ships API; design discovers edge cases in QA. Fix with **contract review** in the same sprint as mockups.

---

## Estimation that survives contact with reality

| Technique | Use when |
|-----------|----------|
| **T-shirt → split** | Epic unknown; spike 1–3 days then re-estimate |
| **Historical velocity** | Similar tickets in last 2 quarters |
| **Risk buffer** | First integration with vendor / new domain — +30–50% |
| **Festival freeze** | India retail: no risky deploys in freeze window |

Never give a single date without **assumptions**. Write them in the ticket/RFC.

---

## Stakeholder update (weekly, 5 bullets)

```
1. Outcome progress: [metric] — on track / at risk
2. Shipped: [1–2 items]
3. Next: [1–2 items]
4. Risk: [one sentence + ask]
5. Decision needed: [yes/no question for PM/EM]
```

Same spine as eng brief in [Executive Communication](../Principal/Executive_Communication_Board_Narrative.md).

---

## Interview signals (behavioral)

| Level | What interviewers listen for |
|-------|------------------------------|
| **SDE2** | Clarified requirements; pushed back on unclear AC |
| **Senior** | Cut scope to hit SLO/date; owned metric |
| **Tech Lead** | Negotiated debt %; coached team through PM conflict |
| **Staff+** | Multi-team roadmap; killed a project with data |

STAR bank: [§14 Leadership](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md), [Tech Lead Conflict](../Levels/Tech_Lead_Conflict_and_Performance.md).

---

## Failure modes

| Failure | Fix |
|---------|-----|
| Silent disagreement then slip | Surface risk in week 1 with options table |
| Over-engineering "platform" for one feature | Time-box; YAGNI until second consumer |
| Saying yes to everything | Capacity % agreement with EM |
| Ignoring design until UI polish | Contract + edge states in kickoff |

---

## Related

- [RFC/ADR Writing](../../06_On_The_Job/04_RFC_ADR_Writing.md)
- [Multi-Year Vision](../Principal/Multi_Year_Vision_Build_vs_Buy.md)
- [Mid-Career Playbook](Mid_Career_Switch_Negotiate_Plateau.md)
- [§20 Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md)
