# First 90 Days — Onboarding Playbook for New Developers

> **You are here**: On the Job — New Hire
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Next**: [Production Debugging](02_Production_Debugging.md)

---

## The goal of your first 90 days

You are not hired to **know everything on Day 1**. You are hired to **learn fast, deliver small wins, and build trust**. Managers evaluate:

1. **Ramp speed** — How quickly you navigate codebase, tools, and processes
2. **Reliability** — Do you do what you say? Ask when stuck?
3. **Team fit** — Communication, code review etiquette, collaboration
4. **Early impact** — At least one shipped improvement by Day 90

---

## Before Day 1 (if you have access)

| Task | Why |
|------|-----|
| Read company engineering blog / tech stack docs | Shows initiative in first 1:1 |
| Set up dev environment per onboarding doc | Day 1 should not be blocked on JDK install |
| Clone main repo; run `README` build steps | Surface blockers early to IT/onboarding buddy |
| Note questions in a doc | Better than interrupting every 10 minutes |

---

## Days 1–30: Learn and Listen

### Week 1 — Orientation

**Focus**: People, process, and "hello world" in the codebase.

| Day | Actions |
|-----|---------|
| 1–2 | Meet manager, buddy, team. Get access (Git, CI, staging, Slack, Jira). Run app locally. |
| 3–5 | Read architecture overview (even if outdated). Trace one user flow end-to-end. |
| 5 | Schedule 1:1s with each teammate (15–30 min): "What do you own? What should I know?" |

**Deliverable by end of Week 2**: Document in your notes:
- How to build, test, deploy
- Main services/modules and what they do
- Who owns what area

### Weeks 2–4 — Small contributions

| Do | Don't |
|----|-------|
| Fix a "good first issue" or doc typo PR | Refactor core module in Week 2 |
| Attend standups; give concise updates | Stay silent for a month |
| Ask "why" after you've tried for 30–60 min | Pretend you understand when lost |
| Read recent PRs in your area | Only read code assigned to you |

**Typical first tickets**: Bug fix, test coverage, logging improvement, small UI tweak, config change.

### 30-day checkpoint (self-review)

- [ ] I can run the app and tests locally without help
- [ ] I understand the team's sprint/ceremony rhythm
- [ ] I merged at least one PR (even small)
- [ ] I know who to ask for: infra, frontend, DB, product questions
- [ ] I have a written map of the codebase (my version is fine)

---

## Days 31–60: Contribute Independently

### Week 5–6 — Own a small feature

Work with your manager to pick a **well-scoped ticket** (1–2 week estimate):

- Clear acceptance criteria
- Touches 1–2 services (not 5)
- Has a reviewer assigned upfront

**Process discipline**:
1. Write a brief design comment in the ticket if non-trivial
2. Open draft PR early for feedback direction
3. Add tests for your change
4. Update docs if you changed behavior

### Week 7–8 — Understand production

| Activity | Purpose |
|----------|---------|
| Shadow on-call (if allowed) | See how incidents are handled |
| Read last 2 post-mortems | Learn failure modes and culture |
| Find your service in monitoring (Grafana/Datadog) | Connect code to metrics |
| Trace one API call through logs | Practice debugging before an incident |

### 60-day checkpoint

- [ ] Shipped at least one user-visible or meaningful internal improvement
- [ ] Participated in code reviews (given and received)
- [ ] Can explain your team's main service to a new hire
- [ ] Know the deployment process and rollback steps (at high level)

---

## Days 61–90: Establish Credibility

### Week 9–10 — Proactive improvement

Pick **one** improvement beyond your assigned tickets:

| Type | Example |
|------|---------|
| Tech debt | Flaky test fix, dependency upgrade |
| Developer experience | Better error message, script for local setup |
| Documentation | Onboarding doc update based on your pain |
| Observability | Add metric or log for confusing failure path |

**Get manager alignment** before spending >2 days — frame as "I noticed X, proposing Y, estimated Z days."

### Week 11–12 — Broader context

- Attend planning/refinement; ask clarifying questions
- Pair with someone on a different layer (frontend if you're backend, etc.)
- Present your work briefly in team demo or retro

### 90-day checkpoint

- [ ] Consistent independent delivery on sprint commitments
- [ ] Code reviews are constructive, not just LGTM
- [ ] At least one proactive improvement merged
- [ ] Manager would re-hire based on first 90 days

---

## Navigating a legacy codebase

Most companies are **brownfield**, not greenfield.

### Strategy

```
1. Find the entry point (controller, main handler, CLI)
2. Follow the happy path for one feature
3. Draw your own diagram (boxes and arrows)
4. Read tests — they document expected behavior
5. Use debugger or logs — faster than reading every file
6. Ask "what breaks if I change this?" before editing
```

### Red flags to avoid early

| Mistake | Why it hurts |
|---------|--------------|
| "This code is terrible, I'll rewrite it" | You don't know the constraints yet |
| Large PR with no tests | Hard to review; high rollback risk |
| Changing shared library without team buy-in | Breaks other services |
| Skipping code review feedback | Trust killer |

---

## Questions to ask your manager (first 1:1)

1. What does success look like at 30, 60, 90 days for me specifically?
2. What are the top 3 priorities for the team this quarter?
3. How do you prefer I communicate blockers?
4. Who are the go-to people for architecture, infra, and product?
5. What should I **not** spend time on in the first month?

---

## India-specific context

| Situation | Tip |
|-----------|-----|
| **Service company bench / internal project** | Clarify billable timeline; still treat onboarding seriously |
| **Startup with no docs** | You become the doc author — write as you learn |
| **GCC / global team** | Overlap hours matter; async updates in Slack/Teams |
| **Hybrid office** | Use in-office days for pairing and relationship building |

---

## Related resources

- [Production Debugging](02_Production_Debugging.md) — when your first bug hits staging
- [Code Review Culture](05_Code_Review_Culture.md) — how to give and receive reviews
- [Git Workflow](../01_TechGuide/33_Git_Version_Control_Workflow.md) — branch and PR conventions

**Next**: [02_Production_Debugging.md](02_Production_Debugging.md)
