# Code Review Culture — Giving and Receiving Feedback

> **You are here**: On the Job — ALL levels
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [CodeQuality](../00_Interview_Prep/Core/CodeQuality.md) | **Next**: [Engineering Manager Track](06_Engineering_Manager_Track.md)

---

## Why code review matters

Code review is the **highest-leverage quality gate** in most teams. It catches bugs, spreads knowledge, and maintains standards — but only if done well. Bad reviews become bottlenecks or ego battles.

---

## Principles of effective reviews

| Principle | Meaning |
|-----------|---------|
| **Review the code, not the person** | "This method is long" not "You always write long methods" |
| **Ask questions** | "What happens if `userId` is null?" vs "This is wrong" |
| **Prioritize** | Block on correctness/security; suggest on style |
| **Respond promptly** | <24 hours for normal PRs; same day for hotfixes |
| **Approve when good enough** | Perfect is enemy of shipped |

---

## What to look for (reviewer checklist)

### Correctness
- [ ] Logic matches requirements / ticket acceptance criteria
- [ ] Edge cases: null, empty, boundary values
- [ ] Error handling — not swallowed exceptions
- [ ] Concurrency / race conditions if shared state

### Security
- [ ] No secrets in code
- [ ] SQL injection, XSS prevented (parameterized queries, escaping)
- [ ] Authorization checked on sensitive endpoints

### Design
- [ ] Single responsibility — not a god class
- [ ] Appropriate abstraction level
- [ ] Consistent with existing patterns in codebase

### Tests
- [ ] Happy path covered
- [ ] At least one failure/edge case
- [ ] Tests are readable and deterministic (no flaky sleeps)

### Observability
- [ ] Meaningful logs (structured, correct level)
- [ ] Metrics for new critical paths if applicable

### Maintainability
- [ ] Clear naming
- [ ] No unnecessary complexity
- [ ] Docs updated if behavior changed

---

## How to write review comments

### Use severity labels (team convention)

| Label | Meaning |
|-------|---------|
| **blocker** | Must fix before merge |
| **suggestion** | Nice to have; author decides |
| **nit** | Style/preference; non-blocking |
| **question** | Need clarification |

### Good vs bad comments

| Bad | Good |
|-----|------|
| "Wrong." | "blocker: This will NPE when `order` is null — see line 42 call path from webhook." |
| "Use a map here." | "suggestion: HashMap would give O(1) lookup here vs current O(n) scan." |
| "I wouldn't do it this way." | "question: Did you consider using the existing `PaymentClient` instead of a new HTTP call?" |

### Praise good work

> "Nice extraction of `validateOrder()` — much easier to test now."

Positive feedback reinforces good patterns.

---

## Author responsibilities

### Before requesting review

- [ ] Self-review the diff (you'll catch 30% of issues)
- [ ] PR description: **what**, **why**, **how to test**
- [ ] Link ticket/Jira
- [ ] CI green
- [ ] Reasonable size (<400 lines ideal; split large changes)

### PR description template

```markdown
## Summary
Add idempotent payment webhook handler to prevent duplicate charges.

## Changes
- New `WebhookController` endpoint
- Dedup table `processed_webhooks`
- Integration test with Testcontainers

## How to test
1. `curl -X POST localhost:8080/webhooks/payment -d @sample.json`
2. Send same payload twice → second returns 200 but no duplicate charge

## Screenshots / logs
[if UI]

## Rollback plan
Revert deploy; dedup table is additive (safe to leave)
```

### Responding to feedback

| Do | Don't |
|----|-------|
| Reply to each thread | Bulk "done" without addressing |
| Explain if you disagree respectfully | Argue emotionally |
| Push fixes as new commits or squash per team norm | Force-push without telling reviewers |
| Re-request review when ready | Assume silence = approval |

**Disagreeing**: "I kept the sync call because the SLA requires immediate confirmation — documented in ADR-0031. Happy to discuss."

---

## Review latency and team health

| Symptom | Fix |
|---------|-----|
| PRs sit 3+ days | Team norm: first review within 24h |
| Rubber-stamp LGTM | Use checklist; rotate reviewers |
| Review fights | Escalate to Tech Lead; refer to ADR/standards |
| Huge PRs | Author splits; reviewer rejects >500 lines |

---

## Senior / Tech Lead reviewer expectations

At Senior+, you are evaluated on **how you raise the bar**:

- Catch design issues, not just typos
- Mentor through questions: "What if traffic 10×?"
- Flag missing tests and observability
- Align with security and compliance for sensitive areas

**Staff signal**: Review drives **standards adoption** — "Let's use the platform retry library per RFC-012."

---

## Interview questions

| Question | Strong answer structure |
|----------|------------------------|
| "How do you handle disagreements in code review?" | Data/ADR, escalate calmly, disagree and commit |
| "What's the best code review you received?" | Specific feedback that taught you something |
| "How do you review junior code?" | Teach, prioritize, praise progress |

---

## Related resources

- [CodeQuality](../00_Interview_Prep/Core/CodeQuality.md)
- [Git Workflow](../01_TechGuide/33_Git_Version_Control_Workflow.md)
- [RFC/ADR Writing](04_RFC_ADR_Writing.md)

**Next**: [06_Engineering_Manager_Track.md](06_Engineering_Manager_Track.md)
