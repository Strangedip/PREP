# RFC and ADR Writing — Technical Decision Records

> **You are here**: On the Job — SDE2 through Staff
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [§20 Technical Leadership](../01_TechGuide/20_Technical_Leadership_Architecture.md) | **Next**: [Code Review Culture](05_Code_Review_Culture.md)

---

## Why write decisions down?

Teams forget **why** choices were made. Six months later, someone asks "Why Kafka?" and the person who decided has left.

| Document | Scope | Audience |
|----------|-------|----------|
| **RFC** (Request for Comments) | Proposed change — new system, major refactor | Team + stakeholders before build |
| **ADR** (Architecture Decision Record) | Record of a decision already made (or accepted) | Future engineers + auditors |

**Interview signal**: Staff+ candidates show ADR/RFC examples proving cross-team influence.

---

## When to write an RFC vs ADR

| Situation | Document |
|-----------|----------|
| "Should we migrate to event-driven architecture?" | RFC first → ADR after approval |
| "We chose PostgreSQL over MongoDB" | ADR |
| "New payment provider integration" | RFC (cross-team impact) |
| "Use MapStruct for DTO mapping" | ADR (team-level) |

**Rule of thumb**: If the decision affects **more than one team** or **costs >2 weeks engineering**, write an RFC.

---

## RFC template

```markdown
# RFC: [Title]

| Field | Value |
|-------|-------|
| Author | @you |
| Status | Draft / In Review / Accepted / Rejected |
| Reviewers | @alice, @bob |
| Created | 2024-03-15 |

## Summary
One paragraph: what and why.

## Problem statement
What pain exists today? Include metrics if possible.
- p99 checkout latency: 2.1s (target: 500ms)
- 3 incidents/month related to synchronous payment calls

## Goals / Non-goals
**Goals:**
- Decouple order service from payment provider latency
- Improve checkout p99 to <600ms

**Non-goals:**
- Multi-region active-active (future RFC)
- Changing payment provider

## Proposed solution
Architecture diagram + component responsibilities.

### API changes
### Data model changes
### Migration plan

## Alternatives considered

| Option | Pros | Cons |
|--------|------|------|
| A: Kafka events | Async, scalable | Ops complexity |
| B: Keep sync REST | Simple | Doesn't fix latency |
| C: SQS only | Managed | Ordering limitations |

## Risks and mitigations
| Risk | Mitigation |
|------|------------|
| Duplicate events | Idempotent consumers + dedup table |

## Rollout plan
1. Week 1: Shadow mode (publish events, don't consume)
2. Week 2: Dual-write
3. Week 3: Cutover with rollback flag

## Open questions
- [ ] Who owns the new Kafka cluster?
- [ ] Retention policy for payment events?

## Appendix
Links to spikes, POC repo, benchmarks.
```

---

## ADR template (Michael Nygard style)

```markdown
# ADR-0042: Use Redis for session cache

## Status
Accepted (2024-03-20)

## Context
Session data stored in PostgreSQL causes 40ms extra latency per request.
Traffic: 12K RPS peak. Sessions: 2M active.

## Decision
Store sessions in Redis with 24h TTL. PostgreSQL remains source for user profile.

## Consequences

**Positive:**
- Session read latency: 40ms → 2ms
- Reduced DB load on users table

**Negative:**
- New failure domain (Redis outage → force re-login)
- Need Redis HA (cluster mode)

**Neutral:**
- Ops team owns Redis patching

## Alternatives rejected
- **Sticky sessions**: Poor failover, uneven load
- **JWT-only**: Hard to revoke sessions on password change
```

Store ADRs in repo: `docs/adr/0042-redis-session-cache.md`

---

## RFC review process

```
1. Author publishes draft → shares in #engineering-rfcs
2. 3–5 business days comment period
3. Sync meeting if contentious
4. Approver(s) sign off (Tech Lead, Staff, EM depending on scope)
5. Status → Accepted → create ADR + Jira epics
```

**Good RFC habits**:
- Lead with problem, not solution
- Quantify impact where possible
- Explicit non-goals prevent scope creep
- Pre-wire with key stakeholders before public review

---

## Common mistakes

| Mistake | Fix |
|---------|-----|
| RFC is a fait accompli | Seek feedback before coding production |
| No alternatives section | Shows you didn't think deeply |
| Missing rollback plan | Reviewers will block |
| ADR never updated when reversed | Add new ADR "Supersedes ADR-0042" |
| 20-page RFC for tiny change | Use ADR or PR description instead |

---

## Example: RFC excerpt (event-driven order flow)

**Problem**: Order service blocks on payment sync call (800ms p95). Black Friday target: 500ms checkout p99.

**Proposal**: Publish `OrderCreated` event; Payment Service consumes and calls provider async; Order status updated via `PaymentCompleted` event.

**Trade-off accepted**: Eventual consistency — user sees "Payment processing" for 2–5 seconds.

---

## Staff interview: "Tell me about a technical decision you drove"

Structure:
1. Problem and stakes
2. Options you evaluated
3. Who you influenced (teams, EM, security)
4. Outcome and metrics
5. What you'd do differently

Point to your RFC/ADR as evidence.

**Next**: [05_Code_Review_Culture.md](05_Code_Review_Culture.md)
