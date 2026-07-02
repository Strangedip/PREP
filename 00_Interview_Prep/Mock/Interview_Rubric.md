# Mock Interview Rubric — Scorer Template

> **You are here**: Tech Lead — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [Interview Playbook](../Core/InterviewPlaybook.md) | **Next**: [Companies](../Core/Companies.md)

Use this rubric for **peer mocks** or self-review after recorded sessions. Score 1–4 per dimension; **3+ on all dimensions** = ready for target level.

---

## Scoring scale

| Score | Meaning |
|-------|---------|
| **1** | Missing or wrong; needed heavy hints |
| **2** | Partial; major gaps in communication or correctness |
| **3** | Solid hire signal at target level |
| **4** | Strong; would advocate in debrief |

---

## Coding round (45–60 min)

| Dimension | 1 | 3 | 4 |
|-----------|---|---|---|
| **Clarification** | Jumps to code | Asks constraints, examples | Surfaces edge cases before coding |
| **Approach** | No structure | Names pattern, states complexity | Compares 2 approaches with trade-offs |
| **Correctness** | Fails basic cases | Passes main + most edges | Handles follow-ups (stream, memory) |
| **Code quality** | Messy names, no structure | Clean, readable | Production-style decomposition |
| **Communication** | Silent coding | Explains while coding | Invites feedback, adapts to hints |

**Pass bar (SDE2)**: Average ≥ 2.5, no dimension at 1. **Pass bar (Senior)**: Average ≥ 3, Approach and Communication ≥ 3.

---

## System design round (45–60 min)

| Dimension | 1 | 3 | 4 |
|-----------|---|---|---|
| **Requirements** | Vague scope | Functional + NFR + out-of-scope | Quantified SLAs, read/write split |
| **Estimation** | Skips math | QPS + storage order-of-magnitude | Back-of-envelope with assumptions stated |
| **Architecture** | Monolith blob | Clear services, data flow | Failure domains, async where needed |
| **Deep dive** | Stays high level | Schema + 1 scaling bottleneck | Trade-off table (SQL vs NoSQL, cache) |
| **Ops** | Not mentioned | Monitoring + idempotency | SLO, rollout, incident path |

Reference: [HLD Template](../../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md), [§14 Leadership SD framework](../../01_TechGuide/14_Leadership_Behavioral_SystemDesign.md).

---

## Behavioral round (30–45 min)

| Dimension | 1 | 3 | 4 |
|-----------|---|---|---|
| **STAR structure** | Rambling | Clear S-T-A-R | Concise; quantified impact |
| **Ownership** | Blames team | Owns decisions | Shows learning from failure |
| **Leadership** | Individual only | Mentored juniors | Influenced without authority |
| **Fit** | Generic stories | Role-aligned | Company LP mapped ([Companies](../Core/Companies.md)) |

Prepare 6–8 stories covering: conflict, failure, deadline, mentoring, technical disagreement, cross-team delivery.

---

## Mock session logistics

| Item | Recommendation |
|------|----------------|
| Frequency | 2 coding + 1 SD + 1 behavioral per week in final month |
| Partner | Peer at same or +1 level; rotate interviewer |
| Recording | Optional; review communication only, not just solution |
| Feedback | Fill rubric within 5 min while fresh |

---

## After-action template

```
Date / Target level / Problem: ___
Coding: Clar __ / Approach __ / Correct __ / Code __ / Comm __  → Avg ___
SD:     Req __ / Est __ / Arch __ / Deep __ / Ops __  → Avg ___
Top 1 fix before next mock: ___
Link to weak ROADMAP section: ___
```

---

## Related

- [SelfAssessment](../Core/SelfAssessment.md) — topic-level confidence
- [InterviewQuestions](../Core/InterviewQuestions.md) — post-mock cram review
- [ROADMAP — Tech Lead](../../ROADMAP.md#tech-lead) — full checklist
