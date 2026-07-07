# Fresher Interview Failure Modes — What Eliminates First-Job Candidates

> **You are here**: Fresher — Interview Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md#fresher) | **Prerequisites**: [Resume & Portfolio](../Career/Resume_and_Portfolio.md), [StudyGuide Path 1](../../02_DSA/StudyGuide.md) | **Next**: [SDE1 Interview Guide](SDE1_Interview_Guide.md)

First-job loops at **TCS/Infosys campus**, **product startups**, and **service-to-product transitions** test fundamentals — not Staff-level system design. This file maps **observed eliminators** to fixes in this repo.

---

## Round-by-round failure modes

### Online assessment (OA) / coding screen — 60–90 min

| Failure mode | What interviewer/platform saw | Fix in this repo |
|--------------|------------------------------|------------------|
| **Never finishes 2 problems** | Stuck on problem 1 for 50 min | [StudyGuide Path 1](../../02_DSA/StudyGuide.md) — 80% Easy before applying |
| **Wrong I/O format** | Correct logic, failed hidden tests | Practice on LeetCode/HackerRank; read constraints twice |
| **No complexity stated** | Code works but no Big-O | [CS Fundamentals](../../01_TechGuide/00_Computer_Science_Fundamentals.md) |
| **Brute force timeout** | O(n³) on Medium disguised as Easy | [Algorithmic Patterns §1–2](../../03_CodingPatterns/02_AlgorithmicPatterns.md) |
| **Syntax errors under pressure** | Java won't compile | Solve 20 Easies **in IDE off**, then 10 timed without autocomplete |

**Self-check**: Solve [Two Sum](../../02_DSA/01_Arrays_Matrix/TwoSum/TwoSum.md), [Valid Parentheses](../../02_DSA/06_Stacks_Queues/ValidParentheses/ValidParentheses.md), [Reverse Linked List](../../02_DSA/05_Linked_Lists/ReverseLinkedList/ReverseLinkedList.md) in 20 min each — explain approach first.

**Example failure story (OA):**

> Candidate solved problem 1 with O(n²) nested loops. Problem 2 was a two-pointer Medium disguised with Easy tags. Spent 55 minutes on problem 1; never started problem 2. Cutoff was "complete 2 problems."
>
> **Fix**: After 15 min stuck, switch problems. Always state complexity before coding. Complete [StudyGuide Path 1](../../02_DSA/StudyGuide.md) until 80% Easy before applying.

---

### Technical phone screen — 30–45 min

| Failure mode | Symptom | Repo fix |
|--------------|---------|----------|
| **Cannot explain project** | Vague "we built an app" | [Resume guide](../Career/Resume_and_Portfolio.md) — STAR for one project |
| **OOP blanks** | No inheritance vs composition | [Java OOP Fundamentals](../../01_TechGuide/00_Java_OOP_Fundamentals.md) |
| **SQL join confusion** | Only knows `SELECT *` | [SQL Fundamentals](../../01_TechGuide/35_SQL_Fundamentals.md) — INNER vs LEFT JOIN |
| **HTTP basics missing** | 200 vs 404 vs 500 same thing | [Web Fundamentals](../../01_TechGuide/00_Web_Fundamentals.md) |
| **No questions at end** | "No questions" signals low curiosity | Prepare 2: team stack, onboarding, code review process |

**Example failure story (phone screen):**

> Interviewer asked "Walk me through your final year project." Candidate said "We made a web app using Spring." No mention of their role, database choices, or challenges. Follow-up "What was hardest?" — silence.
>
> **Better answer (2 min)**: "I built the REST API for a library system — 12 endpoints, PostgreSQL, JWT auth. Hardest part was preventing duplicate book reservations; I used a DB unique constraint on (book_id, date). Load-tested to 200 concurrent users on college WiFi."
>
> Prep: [Resume guide](../Career/Resume_and_Portfolio.md) + [Capstone Projects](../Career/Capstone_Projects.md)

---

### Face-to-face / final technical — 45–60 min

| Failure mode | Symptom | Repo fix |
|--------------|---------|----------|
| **Silent coding** | No narration | [Interview Playbook §3](../Core/InterviewPlaybook.md) |
| **Ignores hints** | Interviewer says "think hash map" — still brute force | Accept hints; restate approach before coding |
| **No edge cases** | Empty array, single element | Every Easy problem "Edge Cases" in `02_DSA` |
| **Cannot dry-run** | Code has off-by-one | Trace on paper before saying "done" |
| **Overclaims stack** | Lists Kafka on fresher resume | [CodeQuality](../Core/CodeQuality.md) — honest depth |

**Example failure story (face-to-face):**

> Candidate coded Two Sum in silence for 8 minutes. Solution was correct but interviewer had to ask "what are you thinking?" three times. Final feedback: "Technically OK, communication risk on team."
>
> **Fix**: Narrate every 60–90 seconds: "I'll use a HashMap to store complements… I'm handling the empty array case… Let me trace with [2,7,11,15]…" See [Interview Playbook §3](../Core/InterviewPlaybook.md).

---

### HR / manager round — 20–30 min

| Failure mode | Fix |
|--------------|-----|
| **Salary anchor too early** | Let them quote first; research bands in [Companies.md](../Core/Companies.md) |
| **Negative about college/previous employer** | Frame as learning, not blame |
| **No "why this company"** | One specific product or engineering blog post |
| **Relocation confusion** | State preference clearly: India-based (office or remote) — see [Companies hiring modes](../Core/Companies.md#hiring-from-india-for-india-based-candidates) |

---

## Fresher-specific traps (India market)

| Context | Trap | Prep |
|---------|------|------|
| **Mass hiring (TCS, Infosys, Wipro)** | Aptitude + basic coding | Speed on Easy arrays/strings; [Path 1 weeks 5–8](../../02_DSA/StudyGuide.md) |
| **Product startup** | Take-home + live coding | [CodeQuality](../Core/CodeQuality.md) + one polished GitHub project |
| **Service company internal promotion** | Java + SQL + "explain current project" | OOP + one REST API story from work |
| **Off-campus flood** | OA cutoff very high | 2 months Path 1 before bulk applying |

---

## Recovery checklist (2 weeks before interviews)

- [ ] [SelfAssessment](../Core/SelfAssessment.md) Section 0 — all items ≥ 3/5
- [ ] 5 timed Easy problems — pattern named in first 3 min
- [ ] Explain one resume project: problem → your role → tech → outcome
- [ ] Whiteboard: HTTP request lifecycle + one SQL JOIN example
- [ ] [Screening & OA guide](../Career/Fresher_Screening_and_OA_Guide.md) — platform-specific tips

**Ready for SDE1 prep when**: Path 1 phases 1–2 complete + [SDE1 Interview Guide](SDE1_Interview_Guide.md) self-check passes.

---

## 4-week recovery plan (if failing screens)

| Week | Focus | Deliverable |
|------|-------|-------------|
| 1 | OA speed | 20 Easy problems timed (25 min each) |
| 2 | Project story | 5-min project pitch recorded on phone |
| 3 | Fundamentals | [CS](../../01_TechGuide/00_Computer_Science_Fundamentals.md), [OOP](../../01_TechGuide/00_Java_OOP_Fundamentals.md), [SQL](../../01_TechGuide/35_SQL_Fundamentals.md) — explain each aloud |
| 4 | Mock | 1 timed OA simulation + 1 project + 1 coding round with friend |

**Portfolio boost**: Complete [Capstone Project 1](../Career/Capstone_Projects.md) if resume projects are weak.
