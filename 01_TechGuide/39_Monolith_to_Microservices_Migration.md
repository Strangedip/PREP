# Monolith → Microservices Migration Playbook

> **You are here**: Senior → Principal — Evolutionary architecture
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [§06 Microservices](06_Microservices_Distributed_Systems.md), [§07 System Design](07_System_Design.md), [§19 Event-Driven](19_Event_Driven_Architecture.md) | **Next**: [§24 Platform Engineering](24_Platform_Engineering_IDP.md)

Strangler Fig is mentioned across this repo; this chapter is the **end-to-end playbook** — phases, metrics, rollback, org alignment — for a Java/Spring Boot shop migrating a commerce or fintech monolith. Use it in Senior HLD interviews and Principal vision loops.

---

## 1. The "Why" & The Problem

Monoliths are not failures. They become liabilities when:

| Symptom | Signal |
|---------|--------|
| Deploy coupling | Payment bug blocked by unrelated catalog change |
| Scaling asymmetry | Read-heavy browse forces scale of write-heavy checkout |
| Team contention | 40 engineers, one repo, merge hell |
| Technology lock | Cannot adopt Kafka/outbox without touching everything |
| Compliance blast radius | PCI scope includes the whole WAR |

**Wrong reason to migrate**: "Microservices are modern." **Right reason**: Independent deploy, scale, and team ownership with measurable SLO/cost outcomes.

---

## 2. Interviewer Expectations

| Level | Expectation |
|-------|-------------|
| **Senior** | Name Strangler Fig; dual-write/shadow; rollback; data ownership |
| **Staff** | Sequencing, platform golden path, Conway alignment |
| **Principal** | Multi-year pillars, build-vs-buy during migration, exec narrative |

**Keywords**: Strangler Fig, Anti-Corruption Layer (ACL), Branch by Abstraction, Outbox, Shadow traffic, Expand-Contract, Database-per-service, Saga, Idempotency.

---

## 3. Decision gate — migrate, modularize, or leave alone?

| Path | When | Outcome |
|------|------|---------|
| **Modular monolith** | Team < 15, domain still unclear | Packages + clear module APIs; defer network split |
| **Strangler extraction** | Clear bounded context + pain | Extract 1–2 services first |
| **Big-bang rewrite** | Almost never | Reject unless greenfield + no traffic |

Score with [Build-vs-Buy](../00_Interview_Prep/Principal/Multi_Year_Vision_Build_vs_Buy.md) weights: differentiation, time, TCO, compliance.

---

## 4. Worked narrative — "PayKart Checkout" (India fintech retail)

**Current state (honest)**:
- Single Spring Boot monolith, PostgreSQL, Redis session
- ~2K RPS peak (festival 8K), checkout p99 900ms
- 6 squads commit to one repo; Friday deploy freezes before BBD
- Payment webhooks and inventory in same DB schema

**Target (18 months)**:
- Extracted **Payment Orchestration** and **Inventory Reservation** services
- Kafka + outbox for order lifecycle
- Monolith remains catalog + cart (modularized)
- Checkout p99 ≤ 350ms; payment Sev-1 −40%

**Non-goals (FY1)**: Full active-active multi-region; GraphQL federation; rewriting Angular storefront.

---

## 5. Phased playbook

### Phase 0 — Baseline (2–4 weeks)

| Action | Done when |
|--------|-----------|
| Domain map (bounded contexts) | Context map reviewed with Staff/Principal |
| SLIs: latency, error, saturation | Dashboards + burn alerts ([§23](23_SRE_Reliability_Engineering.md)) |
| Dependency inventory | DB tables, topics, cron, shared libraries listed |
| Risk register | Top 10 failure modes with owners |

### Phase 1 — Seam + ACL (4–8 weeks)

Introduce a **seam** inside the monolith (interface + Branch by Abstraction) before any new deployable.

```
Monolith
  CheckoutFacade ──► PaymentPort (interface)
                         │
                         ├── LegacyPaymentAdapter (existing code)
                         └── NewPaymentClient (HTTP/gRPC) ──► payment-service
```

**Anti-Corruption Layer**: New service speaks clean domain model; adapter translates legacy DTOs/DB rows. Do not leak monolith table shapes as the public API.

### Phase 2 — Extract read path first (often safer)

| Pattern | Use |
|---------|-----|
| **Read shadow** | New service serves reads; compare to monolith; no user impact |
| **Verify** | Diff rate < threshold for 2 weeks |
| **Cut over** | Feature flag % traffic to new read |

### Phase 3 — Write path with Expand–Contract

1. **Expand**: Dual-write monolith + new service (or outbox → Kafka → consumer)
2. **Migrate**: Backfill historical data; reconcile jobs
3. **Contract**: Monolith stops writing; becomes client only
4. **Delete**: Remove dead code paths after soak

**Payments rule**: Idempotency keys global; never dual-charge. Prefer **orchestrated saga** with compensating void/refund ([Payment HLD](../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md)).

### Phase 4 — Data ownership

| Anti-pattern | Fix |
|--------------|-----|
| Shared DB between services | Own schema; integrate via API/events |
| Distributed joins in app | View service / CQRS read model |
| Cross-DB transactions | Saga + idempotent consumers |

### Phase 5 — Platformize (Staff+)

Once 2+ services exist: golden path templates, CI, observability, [IDP](24_Platform_Engineering_IDP.md). Do not invent a new stack per extraction.

---

## 6. Traffic & rollback toolkit

| Technique | Purpose |
|-----------|---------|
| **Feature flags** | % rollout, instant kill switch |
| **Shadow / dark launch** | Compare without serving |
| **Canary** | 1% → 5% → 25% → 100% with SLO gates |
| **Replay** | Kafka replay for consumers after fix |
| **Runbook** | Who flips flag; who pages; RTO target |

**Rollback must be rehearsed** in a game day before festival traffic.

---

## 7. Metrics that prove the migration works

| Metric | Baseline → Target (example) |
|--------|-----------------------------|
| Checkout p99 | 900ms → 350ms |
| Deploy frequency (payment) | Weekly monolith → daily service |
| Change fail rate | Track separately per service |
| Sev-1 payment incidents / quarter | −40% |
| Lead time for payment fix | Days → hours |
| Cost per 1K checkouts | Flat or down (watch chatty RPC) |

If latency improves but **incident rate rises**, you split the wrong seams.

---

## 8. Org alignment (Conway)

| Wrong | Right |
|-------|-------|
| Extract 12 services, still one team | One stream-aligned team owns Payment end-to-end |
| Platform team writes all services | Platform provides golden path; stream teams extract |
| Shared "DB team" owns all schemas | Service team owns schema + on-call |

See [Organization Design](../00_Interview_Prep/Principal/Organization_Design_Conway_Team_Topologies.md).

---

## 9. Executive one-pager (copy spine)

> **Bet**: Strangle payment + inventory from checkout monolith by FY27 Q2.  
> **Why now**: Festival Sev-1 trend + deploy coupling blocking fraud fixes.  
> **Investment**: 4 engineers × 2 quarters + Kafka platform support.  
> **Return**: p99 −60%, independent payment deploys, PCI scope shrink.  
> **Defer**: Multi-region active-active; full catalog split.  
> **Ask**: Approve Phase 1–3 staffing; freeze unrelated rewrites.

Pair with [Executive Communication](../00_Interview_Prep/Principal/Executive_Communication_Board_Narrative.md).

---

## 10. Interview discussion prompts

1. Why extract payment before catalog?
2. Dual-write vs outbox — when each fails?
3. How do you handle a failed saga mid-festival?
4. When is modular monolith the correct end state?
5. How does this change team topology in 6 months?

---

## 11. Failure modes

| Failure | Why | Fix |
|---------|-----|-----|
| Distributed monolith | Chatty sync RPC, shared DB | Events + owned data; backpressure |
| No flags | Cannot rollback | Flags before cutover |
| Big-bang cut | Irreversible outage | Shadow → canary → contract |
| Ignoring Conway | Services orphaned | Re-org or stop extracting |
| Premature K8s complexity | Ops tax > benefit | Extract on existing runtime first |

---

## Related

- [§06 Microservices](06_Microservices_Distributed_Systems.md)
- [§19 Event-Driven / Kafka](19_Event_Driven_Architecture.md)
- [Enterprise Integration](../00_Interview_Prep/Principal/Enterprise_Integration_ESB_iPaaS_Event_Mesh.md)
- [Multi-Year Vision](../00_Interview_Prep/Principal/Multi_Year_Vision_Build_vs_Buy.md)
- [Payment System HLD](../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md)
