# Career Prep — Resume, Portfolio & Job Search

> **You are here**: Fresher — Career Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [Java OOP Fundamentals](../../01_TechGuide/00_Java_OOP_Fundamentals.md) | **Next**: [DSA StudyGuide Path 1](../../02_DSA/StudyGuide.md)

## Who this is for

Students and career-switchers targeting your **first software engineering role** (internship, graduate program, or fresher SDE). Technical interview prep lives in [ROADMAP.md](../../ROADMAP.md); this file covers **how you get the interview**.

Already employed and switching? Use [Mid-Career Playbook](Mid_Career_Switch_Negotiate_Plateau.md) instead.

---

## Resume structure (one page)

| Section | What to include | What to skip |
|---------|-----------------|--------------|
| **Header** | Name, email, phone, GitHub, LinkedIn, city | Photo (unless local norm requires it) |
| **Education** | Degree, institution, graduation year, GPA if ≥ 3.5/8.0+ | High school details after 2+ years in college |
| **Skills** | Languages, frameworks you can **code in an interview** | 40-technology keyword dumps |
| **Projects** | 2–4 with problem, your role, tech, measurable outcome | "Todo app" with no differentiator |
| **Experience** | Internships, open source, teaching assistant | Unrelated jobs without transferable bullets |

### Project bullet formula

```
[Action verb] + [what you built] + [tech] + [result/metric]
```

Example: *Built a REST booking API with Spring Boot and PostgreSQL; load-tested to 500 RPS with p99 < 200ms using connection pooling and indexes.*

### Before / after bullets

| Weak | Strong |
|------|--------|
| Worked on a library management system | Built Spring Boot catalog API with JWT auth; 12 endpoints, Testcontainers integration tests, README with ER diagram |
| Know Java, C++, Python, JS, React, Docker… | Java 17, Spring Boot, PostgreSQL, Git — comfortable coding these in interviews |
| Participated in college fest website | Owned payment mock + order status UI; reduced form errors with server-side validation |

Link every project to **GitHub** with a clean README (setup steps, architecture diagram, test command).

**Structured capstone specs**: See [Capstone_Projects.md](Capstone_Projects.md) for three level-appropriate projects (Task API, Order Service, Observability Platform) with milestones and evaluation rubrics.

---

## ATS and India campus realities

| Reality | What to do |
|---------|------------|
| PDF vs DOCX | Follow the portal; keep a DOCX without complex tables if ATS strips formatting |
| Keyword mirror | Copy **honest** stack terms from JD (Java, Spring, SQL) — never invent experience |
| CGPA cutoffs | Many drives filter; apply off-campus + referrals in parallel |
| Mass apply portals | Volume helps practice; prioritize referrals for product companies |

---

## Portfolio checklist

- [ ] GitHub profile pinned repos (max 6) — best work only
- [ ] Each repo: README, build instructions, one architecture or flow diagram
- [ ] No committed secrets (`.env` in `.gitignore`; use `.env.example`)
- [ ] Consistent Java style — run through [CodeQuality.md](../Core/CodeQuality.md) checklist
- [ ] Optional: personal site with project summaries (not required for backend roles)
- [ ] Commit history shows **your** work (not one giant dump commit)

### README minimum for each project

```markdown
# Project name — one-line problem
## Run
## Architecture (diagram)
## Design choices (3 bullets)
## Tests (`mvn test` / equivalent)
```

---

## Internship vs full-time timeline (India-focused)

| Phase | Timing | Actions |
|-------|--------|---------|
| **Build** | 6–12 months before | Path 1 DSA + 1–2 solid projects + fundamentals §00 |
| **Apply** | Aug–Nov (campus); rolling (off-campus) | Tailor resume per role; apply via careers page + referrals |
| **Referrals** | Ongoing | Alumni on LinkedIn; contribute to company open source; meetups |
| **OA / screening** | After shortlist | [Fresher OA Guide](Fresher_Screening_and_OA_Guide.md) + [StudyGuide Path 1](../../02_DSA/StudyGuide.md) |

### Referral message (fresher)

> Hi [Name] — final-year CSE at [College]. Built [project] (Spring Boot + PostgreSQL) and practicing DSA Path 1. Could you refer me for [role / internship] at [Company]? Resume: [link]. Happy to send a 5-line blurb for the form.

---

## Trade-off: generalist vs specialist resume

| Approach | Pros | Cons | When |
|----------|------|------|------|
| **Generalist** (Java + SQL + basic frontend) | Fits most campus mass recruiters | Less memorable | Large service companies, campus drives |
| **Specialist** (e.g. backend + system design projects) | Stronger for product companies | Narrower job pool | Startups, backend-heavy roles |
| **AI-augmented projects** | 2026 differentiator | Risk of shallow "ChatGPT wrapper" | Only if you understand RAG/LLM basics from [05_AI](../../05_AI/01_AI_Fundamentals.md) |

---

## Application tracker (keep it boring)

| Column | Example |
|--------|---------|
| Company | Flipkart |
| Role / level | SDE Intern |
| Source | Referral / portal |
| Date applied | 2026-08-12 |
| Status | OA scheduled |
| Notes | Focus arrays + SQL |

Optional daily habit tracker: `python tools/tracker_generator.py` (see [tools/requirements.txt](../../tools/requirements.txt)).

---

## Common failure modes

| Mistake | Fix |
|---------|-----|
| Resume lists LeetCode count with no projects | Add 2 projects demonstrating OOP, APIs, tests |
| Same resume for every company | Mirror JD keywords (Java, Spring, AWS) honestly |
| No GitHub activity in last 6 months | One commit/week minimum on portfolio project |
| Applying only to FAANG | Include [Companies.md](../Core/Companies.md) Tier 2–3 for volume + practice |
| Lying about stack | You will be asked to code it — don't |

---

## Ready for interviews when

- [ ] Resume fits one page; every project link works
- [ ] Can explain any resume project for 5 minutes without slides
- [ ] [SelfAssessment](../Core/SelfAssessment.md) Section 0 average ≥ 3/5
- [ ] Completed [ROADMAP — Fresher](../../ROADMAP.md#fresher) Core technical items
- [ ] OA path clear: [Fresher Screening & OA](Fresher_Screening_and_OA_Guide.md)

**Next**: [Interview Playbook](../Core/InterviewPlaybook.md) when you have scheduled screens. Mid-career switchers: [Mid-Career Playbook](Mid_Career_Switch_Negotiate_Plateau.md).
