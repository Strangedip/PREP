# Tech Lead Interview Failure Modes

> **You are here**: Tech Lead — Interview Prep
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

### Behavioral / leadership (45 min)

| Failure mode | Fix |
|--------------|-----|
| All "we" — no personal decision | STAR with **your** call: [§14](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) |
| Conflict story = "I was right" | [Conflict & Performance guide](Tech_Lead_Conflict_and_Performance.md) |
| No perf management example | One coaching story: gap → plan → outcome |
| Cannot articulate tech debt trade-off | ADR format in §20 |
| Amazon LP mismatch | [Companies.md](../Core/Companies.md) LP→STAR map |

### Coding (still happens at many companies)

| Failure mode | Fix |
|--------------|-----|
| Cannot do Medium in 35 min | Maintain with [Path 3 maintenance](../../02_DSA/StudyGuide.md) |
| Over-engineers utility class | YAGNI — [CodeQuality](../Core/CodeQuality.md) |
| No test discussion | Testing pyramid in [§09](../../01_TechGuide/09_Testing_Strategies.md) |

### Architecture / cross-functional (30–45 min)

| Failure mode | Fix |
|--------------|-----|
| Cannot estimate quarter of work | [§20 estimation](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |
| No incident story | Blameless post-mortem example |
| PM conflict unresolved | Data-driven prioritization story |

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
| **Remote US startup** | Async communication | Written RFC sample |

---

## Recovery checklist

- [ ] 4+ polished STAR stories ([Mock Rubric](../Mock/Interview_Rubric.md))
- [ ] Facilitate 45-min HLD: [News Feed](../../04_SystemDesign/02_HighLevelDesign/NewsFeed/NewsFeed.md) with trade-off table
- [ ] [SelfAssessment](../Core/SelfAssessment.md) Section 27 ≥ 4/5
- [ ] [Comp & Scope](Comp_and_Scope.md) — know Staff vs TL track before negotiating

**Next**: [Staff Loop Expectations](Staff_Loop_Expectations.md)
