# Hiring Loop Design — Running Interviews as the Company

> **You are here**: On the Job — Tech Lead / EM / Staff
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [Interview Rubric](../00_Interview_Prep/Mock/Interview_Rubric.md), [EM Track](06_Engineering_Manager_Track.md) | **Next**: [One-on-Ones & Performance](08_One_on_Ones_and_Performance_Reviews.md)

You have been on the candidate side of this repo. This guide is the **other side of the table**: designing loops that predict job performance, calibrating fairly, and avoiding the hiring mistakes that create years of team debt.

---

## Why loop design matters

| Bad loop | Cost |
|----------|------|
| Only LeetCode Hard | Misses Senior who designs systems; false-negatives strong India product engineers |
| Only resume chat | Hires confident talkers; production collapses |
| No rubric | Inconsistent bar; bias; cannot defend "no hire" |
| Same loop for SDE1 and Staff | Wastes Staff candidates; over-levels juniors |

**Principle**: Each round must measure a **distinct signal** mapped to the level's ROADMAP scope.

---

## Loop blueprints by level

### SDE1 (India product / GCC)

| Round | 45–60 min | Signal | Repo anchor |
|-------|-----------|--------|-------------|
| OA / screen | Timed DSA Easy–Medium | Problem solving | [StudyGuide Path 1–2](../02_DSA/StudyGuide.md) |
| Coding | 1 Medium + quality | Clean code, tests mindset | [CodeQuality](../00_Interview_Prep/Core/CodeQuality.md) |
| LLD / machine coding | 60–90 min | OOP, extensibility | [Machine Coding](../03_CodingPatterns/Machine_Coding_Round_Guide.md) |
| Manager | Behavioral | Ownership, learning | STAR |

### Senior SDE

| Round | Signal |
|-------|--------|
| Coding | Medium–Hard; complexity trade-offs |
| HLD | Capacity, NFRs, failure modes — one flagship |
| Deep dive | Past project; your role vs team's |
| Behavioral | Conflict, incident, mentorship |

### Staff / Principal

| Round | Signal |
|-------|--------|
| Cross-team design | Standards, migration, platform |
| Architecture review | Critique an RFC; Conway awareness |
| Exec / vision (Principal) | Build-vs-buy, narrative |
| Bar raiser / calibration | Consistency with level rubric |

Principal content: [Principal Loop Guide](../00_Interview_Prep/Principal/Interview_Loop_Guide.md).

---

## Rubric skeleton (score 1–4 per dimension)

| Score | Meaning |
|-------|---------|
| 1 | Clear no — fundamental gaps |
| 2 | Below bar — coachable but not for this level |
| 3 | Hire — meets level |
| 4 | Strong hire — raises team bar |

**Dimensions** (customize per round): Problem structuring | Correctness | Complexity | Communication | Testing/edge cases | (SD) Trade-offs & ops.

Full mock scoring: [Interview_Rubric.md](../00_Interview_Prep/Mock/Interview_Rubric.md).

---

## Calibration rules

1. **Write notes before debrief** — independent scores first
2. **Evidence > vibe** — quote what candidate said/did
3. **Level the role, not the person** — "strong SDE2" ≠ "weak Senior hire"
4. **One bar raiser** (or Staff) with veto on process fairness, not taste
5. **Same questions** within a role family for comparability

---

## Writing a job description that matches the loop

| JD claim | Must appear in loop |
|----------|---------------------|
| "Distributed systems" | HLD with partitions, failure |
| "Spring Boot" | Coding or deep dive in stack |
| "Mentorship" | Behavioral + review of how they teach |
| "Staff scope" | Cross-team design, not only coding |

If the JD promises Staff and the loop is only DSA, you will hire the wrong people and lose the right ones.

---

## Bias and fairness (practical)

| Risk | Mitigation |
|------|------------|
| Affinity bias | Structured rubric; diverse panel |
| Pedigree bias | Blind skills first when possible; weight work samples |
| Accent / English fluency | Separate communication score from technical; allow clarifying questions |
| LeetCode memorization | Prefer novel variants + explanation of trade-offs |

---

## Debrief agenda (30 min)

1. Level reminder (what "3" means for this role)
2. Round-by-round: score + 2 evidence bullets
3. Red flags (integrity, blame-only stories, cannot discuss failure)
4. Decision: Hire / No / Advance another level
5. Feedback to recruiter (candidate-safe summary)

---

## Giving candidate feedback (when policy allows)

> "Strong structuring on the design round; coding needs more edge-case coverage for this Senior bar. Recommend practicing timed Mediums with tests."

Never invent reasons. Never ghost after onsite if your process promises a response.

---

## Related

- [Interview Rubric](../00_Interview_Prep/Mock/Interview_Rubric.md)
- [EM Track](06_Engineering_Manager_Track.md)
- [Staff Loop Expectations](../00_Interview_Prep/Levels/Staff_Loop_Expectations.md)
- [Code Review Culture](05_Code_Review_Culture.md) — similar feedback craft
