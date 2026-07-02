# Multi-Team Architecture Review & Technical Roadmap

> **You are here**: Staff Engineer — Engineering Practices
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§20 Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md), [§24 Platform Engineering](../../01_TechGuide/24_Platform_Engineering_IDP.md) | **Next**: [Staff Loop Expectations](Staff_Loop_Expectations.md)

Staff engineers in **Java/Spring product orgs** (Flipkart platform, Razorpay infra, Amazon horizontal teams) own **cross-squad architecture**: RFCs, roadmaps, and review forums — not sprint JIRA for one team. This extends [§20 ADRs](../../01_TechGuide/20_Technical_Leadership_Architecture.md) to org scale.

---

## Architecture review forum (ARF)

### When to require ARF

| Change type | ARF required? | Example |
|-------------|---------------|---------|
| New microservice | Yes | Order notifications split from monolith |
| New datastore | Yes | Mongo for catalog — [§27 NoSQL](../../01_TechGuide/27_NoSQL_Databases_Guide.md) |
| Kafka topic + schema | Yes | [§19 Event-Driven](../../01_TechGuide/19_Event_Driven_Architecture.md) |
| Breaking API | Yes | Mobile clients — [§04 versioning](../../01_TechGuide/04_API_Design_REST.md) |
| Internal refactor | No (team ADR) | Extract pricing Strategy class |
| Dependency upgrade | No* | Spring Boot 3.3→3.4 unless security CVE |

### RFC template (1–2 pages)

```markdown
# RFC: [Title] — Author, Date, Status (Draft/Review/Accepted)

## Problem
[Metric or incident driving change — link post-mortem]

## Goals / Non-goals
- Goals: ...
- Non-goals: ...

## Proposals considered
| Option | Pros | Cons | Cost (eng-weeks) |
|--------|------|------|------------------|
| A: Kafka + outbox | ... | ... | 8 |
| B: REST + sync | ... | ... | 3 |

## Recommendation
[Chosen option + why — tie to SLO]

## Rollout plan
- Phase 1: shadow traffic
- Phase 2: 10% canary
- Rollback: feature flag / revert deploy

## Open questions
[Security, compliance — [§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md) if payments]
```

---

## Multi-team technical roadmap (12–18 months)

### Layers (what Staff owns)

```
┌─────────────────────────────────────────────────────────┐
│  Themes (Staff/Principal): Reliability, Cost, Velocity   │
├─────────────────────────────────────────────────────────┤
│  Initiatives: IDP, event mesh, Java 21 rollout           │
├─────────────────────────────────────────────────────────┤
│  Team epics: Squad A payment, Squad B catalog          │
└─────────────────────────────────────────────────────────┘
```

### Example theme: "Order path reliability" (Spring microservices)

| Quarter | Initiative | Teams | Success metric |
|---------|------------|-------|----------------|
| Q1 | Idempotency keys on all write APIs | Payments, Orders | 0 duplicate charges in chaos test |
| Q2 | Outbox + Kafka for order events | Orders, Notify | p99 publish lag < 5s |
| Q3 | SLO dashboards + error budgets | All | 99.95% order API availability |
| Q4 | Load test 3× peak (sale event) | SRE + Orders | No manual scale incident |

Link: [Payment System HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md), [§06 SAGA](../../01_TechGuide/06_Microservices_Distributed_Systems.md).

### Example theme: "Developer velocity" (platform)

| Quarter | Initiative | Success metric |
|---------|------------|----------------|
| Q1 | Golden path Spring Boot template — [§24](../../01_TechGuide/24_Platform_Engineering_IDP.md) | New service in < 1 day |
| Q2 | Testcontainers in CI by default — [§09](../../01_TechGuide/09_Testing_Strategies.md) | 80% services adopted |
| Q3 | Internal Backstage catalog | 100% services registered |

---

## Review checklist (Staff reviewer)

### Functional & NFR

- [ ] Capacity estimate stated (QPS, storage) — [HLD Template](../../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md)
- [ ] Failure modes: DB down, dependency timeout, retry storm
- [ ] Idempotency for writes
- [ ] Observability: logs, metrics, traces — [§11](../../01_TechGuide/11_Observability.md)
- [ ] Security: authZ, PII, OWASP — [§12](../../01_TechGuide/12_Security_OWASP_Cloud.md)

### Java/Spring specific

- [ ] `@Transactional` boundaries correct (no self-invocation trap)
- [ ] Thread pool sizing for async — [§02](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md)
- [ ] Connection pool limits vs K8s replicas — [§30 K8s](../../01_TechGuide/30_Kubernetes_Deep_Dive.md)
- [ ] Feature flags for rollout — [§10 DevOps](../../01_TechGuide/10_DevOps_CICD_Docker.md)

### Org fit

- [ ] Duplicates existing platform capability?
- [ ] Conway alignment — team owns service boundary
- [ ] Migration path from current state (strangler fig — [§07](../../01_TechGuide/07_System_Design.md))

---

## Tech debt prioritization matrix

| Debt | User impact | Incident risk | Eng tax/week | Priority |
|------|-------------|---------------|--------------|----------|
| No idempotency on refunds | High | Sev-1 potential | 2h on-call | P0 |
| Java 11 EOL | Low visible | Security | 4h upgrades | P1 |
| Missing integration tests | Medium | Regressions | 6h debugging | P1 |
| Sonar code smells | Low | Low | 1h | P3 |

Staff presents **top 5 P0/P1** to leadership quarterly — tie to [§20 tech debt](../../01_TechGuide/20_Technical_Leadership_Architecture.md).

---

## Incident-driven architecture (blameless)

After Sev-1/2:

1. **Timeline** — deploy, metric shift, mitigation
2. **Root cause** — 5 whys to systemic fix
3. **Architecture action** — ADR if pattern repeats (e.g. missing circuit breaker — [§06](../../01_TechGuide/06_Microservices_Distributed_Systems.md))
4. **Track in roadmap** — not "fix later" without quarter

---

## Interview stories (Staff loop)

Prepare one example of each:

- **RFC you drove** that changed 3+ teams' design
- **Roadmap trade-off** you cut (said no to shiny Kafka use case)
- **Review where you blocked launch** until load test passed
- **Platform win** — DORA metric improved ([§24](../../01_TechGuide/24_Platform_Engineering_IDP.md))

Map to [Staff Loop Expectations](Staff_Loop_Expectations.md) and [Mock Rubric](../Mock/Interview_Rubric.md).

---

## Anti-patterns (Staff credibility killers)

| Anti-pattern | Why it fails |
|--------------|--------------|
| Architecture by PowerPoint only | No spike code in [Spring Boot](../../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) |
| Mandate without migration help | Teams ignore golden path |
| Review every PR org-wide | Bottleneck — review RFCs only |
| Ignoring cost | K8s replica sprawl — [§31 Cloud](../../01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md) |
