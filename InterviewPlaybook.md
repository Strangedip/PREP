# Interview Playbook — From Prep to Offer

> **Use with**: [SelfAssessment.md](SelfAssessment.md), [Companies.md](Companies.md), [InterviewQuestions.md](InterviewQuestions.md), [MASTER_INDEX.md](MASTER_INDEX.md)

---

## 1. The Four Interview Dimensions

| Dimension | What They Test | Primary Resources |
|-----------|----------------|-------------------|
| **Coding (DSA)** | Problem solving, clean code, complexity | [02_DSA](02_DSA/StudyGuide.md), [03_CodingPatterns](03_CodingPatterns/02_AlgorithmicPatterns.md) |
| **System Design** | Scale, trade-offs, APIs, data model | [04_SystemDesign](04_SystemDesign/README.md), [§07](01_TechGuide/07_System_Design.md) |
| **Technical depth** | Java, Spring, DB, cloud, security | [01_TechGuide](01_TechGuide/00_TableOfContents.md) |
| **Behavioral / LP** | STAR stories, leadership, collaboration | [§14 Leadership](01_TechGuide/14_Leadership_Behavioral_SystemDesign.md), [Companies.md](Companies.md) |

**Associate/Junior**: Coding 50%, technical depth 30%, behavioral 20%, system design light.
**Senior+**: System design 35%, coding 25%, technical 25%, behavioral 15%.
**Lead**: System design + behavioral + architecture judgment dominate.

---

## 2. 12-Week Prep Schedule (Flexible)

| Weeks | Focus | Daily (weekdays) |
|-------|-------|------------------|
| 1-2 | Fundamentals §00, SQL §35, DSA Path 1 | 1 hr theory + 2 problems |
| 3-4 | Spring §02, patterns §03, DSA Path 2 | 1 hr + 2 medium problems |
| 5-6 | Microservices §06, security §12, HLD 1-3 | 1 hr + 1 HLD outline |
| 7-8 | DSA Path 3, LLD 2-3, mock coding | 2 problems + 1 LLD |
| 9-10 | HLD 4-8, observability §11, AI §05 | 1 HLD + 1 hr AI |
| 11 | Behavioral STAR bank, company-specific | 2 stories polished per LP |
| 12 | Mock interviews, weak-area review | Full mock every 2 days |

---

## 3. Coding Round Protocol (45-60 min)

### Minute 0-5: Clarify
- Input size, edge cases (empty, duplicates, negatives)
- Expected output format
- Constraints driving algorithm choice

### Minute 5-10: Examples + Brute Force
- Walk through 2 examples on paper
- State brute force and its complexity

### Minute 10-25: Optimize + Code
- Name the pattern (sliding window, BFS, DP table)
- Write clean code — meaningful names, no magic numbers
- **Talk while coding** — interviewer coaches on thought process

### Minute 25-35: Test + Complexity
- Test edge cases aloud
- State time and space complexity; justify

### Minute 35-45: Follow-ups
- "What if input is a stream?" "What if we need persistence?"
- Shows senior thinking without over-engineering

**Red flags**: Silent coding, no examples, ignoring hints, no complexity analysis.

---

## 4. System Design Round Protocol (45-60 min)

Use the **4-step framework** from [§14](01_TechGuide/14_Leadership_Behavioral_SystemDesign.md):

1. **Requirements** (5-10 min) — functional, non-functional, out of scope
2. **Estimation** (5 min) — QPS, storage, bandwidth
3. **High-level design** (15-20 min) — boxes and arrows, data flow
4. **Deep dive** (15-20 min) — DB schema, APIs, scaling, failure modes

**Always mention**: caching layer, async where appropriate, monitoring, idempotency for writes.

**Draw**: Client → LB → API → Cache → DB → Queue → Workers.

---

## 5. STAR Method (Behavioral)

| Letter | Meaning | Tip |
|--------|---------|-----|
| **S** | Situation — 1-2 sentences, context | Set the scene in 30 seconds |
| **T** | Task — your responsibility | What was expected of you |
| **A** | Action — what **you** did | Use "I", not "we" for your contributions |
| **R** | Result — measurable outcome | Numbers when possible |

**Prepare 8-10 stories** covering:
- Conflict / disagree and commit
- Failure and learning
- Mentorship / hiring
- Ambiguity / no spec
- Production incident / on-call
- Cross-team influence
- Technical decision with trade-offs
- Customer obsession / business impact

Map to Amazon LPs in [Companies.md](Companies.md).

---

## 6. Day Before & Day Of

| Day Before | Day Of |
|------------|--------|
| Review [InterviewQuestions.md](InterviewQuestions.md) rapid-fire | Light breakfast, no heavy cramming |
| One easy DSA warm-up | Test camera, mic, IDE (if virtual) |
| Sleep 7+ hours | Water, quiet room, backup internet |
| Skim company section in Companies.md | Join 5 min early |
| No new hard topics | Confidence > perfection |

---

## 7. Post-Interview

- Send thank-you note within 24 hours (recruiter or interviewer if allowed)
- Log questions asked — update weak areas in SelfAssessment
- If rejected: ask recruiter for feedback (often generic, still useful)

---

## 8. Quick Reference — Associate Start Path

**Do not start with Virtual Threads.** Start here:

1. [00_Java_OOP_Fundamentals.md](01_TechGuide/00_Java_OOP_Fundamentals.md)
2. [00_Computer_Science_Fundamentals.md](01_TechGuide/00_Computer_Science_Fundamentals.md)
3. [35_SQL_Fundamentals.md](01_TechGuide/35_SQL_Fundamentals.md)
4. [02_DSA StudyGuide Path 1](02_DSA/StudyGuide.md)
5. [00_Web_Fundamentals.md](01_TechGuide/00_Web_Fundamentals.md)
6. [04_API_Design_REST.md](01_TechGuide/04_API_Design_REST.md) (basics)
7. Then §02 Spring basics → §01 Modern Java

---

## 9. Mock Interview Checklist

- [ ] Timed coding (45 min, one problem)
- [ ] Timed HLD (45 min, one problem from [04_SystemDesign](04_SystemDesign/README.md))
- [ ] 3 behavioral questions with STAR (record yourself)
- [ ] "Explain your last project" (2 min elevator + 10 min deep dive)
- [ ] Java: concurrency + Spring transaction question
- [ ] Security: one OWASP 2025 category with example

**Ready when**: SelfAssessment total ≥ 95 and 3 clean mocks without major stalls.
