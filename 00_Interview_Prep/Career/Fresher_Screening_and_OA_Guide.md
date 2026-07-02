# Fresher Screening & Online Assessment Guide

> **You are here**: Fresher — Career Prep
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md#fresher) | **Prerequisites**: [Resume & Portfolio](Resume_and_Portfolio.md) | **Next**: [Fresher Failure Modes](../Levels/Fresher_Failure_Modes.md)

Most fresher pipelines **never reach a human** if the OA fails. This guide covers platform behavior, time strategy, and what Indian and global companies (hiring into India) actually filter on.

---

## Typical fresher pipeline

```
Apply → Resume screen → OA / HackerRank → Technical phone → 1–2 technical rounds → HR
         (ATS)           (60–90 min)        (30–45 min)      (45 min each)      (offer)
```

**Cutoff reality**: For popular product companies, OA pass rates are often **5–15%**. Mass recruiters may take **30%+** if aptitude + basic coding clears bar.

---

## OA platform patterns

| Platform | Common at | Format | Strategy |
|----------|-----------|--------|----------|
| **HackerRank** | Amazon, many startups | 2–3 coding + MCQs | Read all problems first; do easiest first |
| **Codility** | European firms, some SaaS | 2–4 tasks, strict limits | Watch time per task; partial credit matters |
| **HackerEarth** | Indian startups, campus | Coding + aptitude | Don't skip aptitude section — tied to shortlist |
| **Mettl / AMCAT** | TCS, Infosys, campus drives | Aptitude heavy | Speed > perfection on quant |
| **CodeSignal** | Some US remote-for-India | GCA score | Consistency across 4 problems |
| **Internal (Google Docs)** | Small startups | Take-home 2–4 hrs | Submit clean README + tests |

### Time budget (90 min, 3 problems)

| Phase | Minutes | Action |
|-------|---------|--------|
| Read all | 5 | Note difficulty, pattern hints |
| Problem 1 (Easy) | 25 | Full solution + test |
| Problem 2 (Medium/Easy) | 35 | Working solution > perfect optimal |
| Problem 3 | 20 | Partial solution with comment on full approach |
| Review | 5 | Sample cases, off-by-one |

---

## What OA actually tests

| Skill | Weight | Repo path |
|-------|--------|-----------|
| Arrays, strings, hash maps | High | [Path 1 weeks 5–6](../../02_DSA/StudyGuide.md) |
| Stacks, queues, linked lists | Medium | Path 1 weeks 7–8 |
| Trees (BFS/DFS basics) | Medium | Path 1 weeks 9–10 |
| Sorting, binary search | Medium | Path 1 weeks 9–10 |
| Basic DP (stairs, robber) | Low at fresher | Path 1 weeks 15–16 |
| SQL MCQs | Medium at enterprise | [§35 SQL](../../01_TechGuide/35_SQL_Fundamentals.md) |
| OOP / output questions | Medium | [Java OOP](../../01_TechGuide/00_Java_OOP_Fundamentals.md) |

---

## Resume screen (before OA)

| Rejected because | Fix |
|------------------|-----|
| No GitHub / empty repo | One complete project with README, tests, API docs |
| Skills laundry list | Max 8 skills you can defend in interview |
| No measurable outcome | "Reduced API latency 30%" or "500+ users in beta" |
| PDF parsing failure | Simple format, no tables/columns for ATS |

See [Resume_and_Portfolio.md](Resume_and_Portfolio.md) for full template.

---

## Phone screen (after OA)

Expect: **1 Easy–Medium coding** OR **deep dive on resume project** OR both in 30–45 min.

**Script opening**: "I'll clarify constraints, propose approach with complexity, code, then test edge cases."

Problems at this stage: [Two Sum](../../02_DSA/01_Arrays_Matrix/TwoSum/TwoSum.md), [Valid Anagram](../../02_DSA/02_Strings/ValidAnagram/ValidAnagram.md), [Merge Two Sorted Lists](../../02_DSA/05_Linked_Lists/MergeTwoSortedLists/MergeTwoSortedLists.md).

---

## Applying while in final year

| Timeline | Action |
|----------|--------|
| **6 months before** | Path 1 phases 1–2; one project on GitHub |
| **3 months before** | 80% Easy rate; start OA on practice platforms |
| **1 month before** | Company-specific: [Companies.md](../Core/Companies.md) |
| **Offer season** | Compare CTC breakdown (fixed vs variable vs ESOP) |

---

## Related

- [Fresher Failure Modes](../Levels/Fresher_Failure_Modes.md)
- [Interview Playbook](../Core/InterviewPlaybook.md)
- [Companies — hiring from India](../Core/Companies.md#hiring-from-india-for-india-based-candidates)
