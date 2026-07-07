# Tech Lead Interview Failure Modes

> **You are here**: Tech Lead — Interview Prep
> **Depth**: Standard (failure patterns with example stories and recovery drills)
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md#tech-lead) | **Prerequisites**: [Senior Failure Modes](Senior_Failure_Modes.md), [Tech Lead Conflict & Performance](Tech_Lead_Conflict_and_Performance.md) | **Next**: [Staff Loop Expectations](Staff_Loop_Expectations.md)

Tech Lead loops test **team technical direction + delivery + people leadership** — not deeper graph algorithms than Senior. Failure is often **behavioral thin** or **system design without operational ownership**.

---

## Failure modes by round

### System design (45–60 min)

| Failure mode | What went wrong | Fix |
|--------------|-----------------|-----|
| **Individual contributor design** | No mention of team ownership, runbooks | [§20 Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |
| **No trade-off table** | Picked Kafka with no alternative | [HLD Template](../../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md) |
| **Ignores team skill matrix** | Designs Rust microservices for Java team | Staffing / risk section in §20 |
| **AI bolt-on** | "Add ChatGPT" with no cost/latency | [AI System Design](../../05_AI/10_AI_System_Design.md) |

**Example failure story (system design):**

> Candidate designed a perfect event-driven architecture for a 6-person Java team with zero Kafka experience. When asked "How will your team operate this?", they said "They'll learn."
>
> **Better answer**: "I'd start with synchronous REST + outbox table for our team's skill level. Phase 2 introduces Kafka for order events once we've run a pilot and have on-call runbooks. Here's the migration RFC outline."

---

### Behavioral / leadership (45 min)

| Failure mode | Fix |
|--------------|-----|
| All "we" — no personal decision | STAR with **your** call: [§14](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) |
| Conflict story = "I was right" | [Conflict & Performance guide](Tech_Lead_Conflict_and_Performance.md) |
| No perf management example | One coaching story: gap → plan → outcome |
| Cannot articulate tech debt trade-off | ADR format in §20 |
| Amazon LP mismatch | [Companies.md](../Core/Companies.md) LP→STAR map |

**Example STAR (conflict — good):**

> **Situation**: Two seniors disagreed on monolith vs microservices for payments rewrite.
> **Task**: As TL, I needed a decision in 2 weeks without team split.
> **Action**: I ran a 3-day spike — each side built a thin slice. We scored on team velocity, ops burden, and compliance audit trail. Monolith with modular boundaries won for phase 1.
> **Result**: Shipped in 8 weeks; revisited split at 10K TPS with data. Both seniors felt heard.

**Example STAR (bad):**

> "There was a conflict. I told them to do it my way." → No process, no metrics, no empathy signal.

---

### Coding (still happens at many companies)

| Failure mode | Fix |
|--------------|-----|
| Cannot do Medium in 35 min | Maintain with [Path 3 maintenance](../../02_DSA/StudyGuide.md) |
| Over-engineers utility class | YAGNI — [CodeQuality](../Core/CodeQuality.md) |
| No test discussion | Testing pyramid in [§09](../../01_TechGuide/09_Testing_Strategies.md) |

---

### Architecture / cross-functional (30–45 min)

| Failure mode | Fix |
|--------------|-----|
| Cannot estimate quarter of work | [§20 estimation](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |
| No incident story | Blameless post-mortem example — [On-Call Guide](../../06_On_The_Job/03_On_Call_Incident_Response.md) |
| PM conflict unresolved | Data-driven prioritization story |

**Example failure story (cross-functional):**

> PM demanded 5 features in one sprint. Candidate said "yes" to everything. When asked how, they had no trade-off framework.
>
> **Better answer**: "I mapped features to OKR impact and engineering cost. I proposed 3 for this sprint, 2 for next, with explicit risk if we force all 5 — quality and on-call load. PM agreed after seeing the capacity model."

---

## Tech Lead vs Senior — interview delta

| Area | Senior | Tech Lead |
|------|--------|-----------|
| **Scope** | Service / feature | Team roadmap + standards |
| **Behavioral weight** | ~20% | **40–50%** at Amazon, Meta, many Indian unicorns |
| **System design** | Deep NFRs | Deep NFRs **+ team operability** |
| **Coding** | Hard possible | Medium–Hard, less frequent |

---

## India + global companies (India-based roles)

| Company type | TL trap | Prep |
|--------------|---------|------|
| **Amazon** | LP in every round | 16 stories mapped |
| **Google** | Googleyness + collaboration | Cross-team influence story |
| **Flipkart / PhonePe** | Delivery pressure narrative | Sprint trade-off STAR |
| **GCC (Microsoft, Adobe)** | Process + mentorship | RFC / design review example |
| **Remote US startup** | Async communication | Written RFC sample — [RFC Guide](../../06_On_The_Job/04_RFC_ADR_Writing.md) |

---

## Recovery checklist (4-week plan)

### Week 1 — Behavioral bank
- [ ] Write 6 STAR stories: conflict, failure, mentoring, prioritization, incident, hiring
- [ ] Each story has **your** explicit action (not "we")

### Week 2 — System design with TL lens
- [ ] Facilitate 45-min HLD: [News Feed](../../04_SystemDesign/02_HighLevelDesign/NewsFeed/NewsFeed.md) with trade-off table
- [ ] Add section: "How my team would operate this" (on-call, runbooks, rollout)

### Week 3 — People leadership depth
- [ ] Read [Conflict & Performance guide](Tech_Lead_Conflict_and_Performance.md)
- [ ] Prepare one underperformance coaching story with outcome metrics

### Week 4 — Mock loop
- [ ] [Mock Rubric](../Mock/Interview_Rubric.md) — full TL loop simulation
- [ ] [SelfAssessment](../Core/SelfAssessment.md) Section 27 ≥ 4/5
- [ ] [Comp & Scope](Comp_and_Scope.md) — Staff vs TL track before negotiating

**Next**: [Staff Loop Expectations](Staff_Loop_Expectations.md)
