# Organization Design — Conway's Law & Team Topologies

> **You are here**: Principal / Architect — Org Alignment
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§20 Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md), [Multi-Team Architecture Review](../Levels/Multi_Team_Architecture_Review.md) | **Next**: [Multi-Year Vision](Multi_Year_Vision_Build_vs_Buy.md)

> **Mel Conway (1968)**: "Organizations which design systems are constrained to produce designs which are copies of the communication structures of those organizations."

In **India product engineering** (Flipkart verticals, Swiggy pods, Razorpay product lines), Principal interviews probe whether your **architecture matches how teams actually ship** — not an ideal diagram.

---

## Conway's Law — practical form

```
Team A owns UI + API + DB for Checkout
        ↓
Architecture becomes a Checkout monolith or tightly coupled module

Team A (API) + Team B (DB) + Team C (UI) without clear interfaces
        ↓
Integration meetings, shared DB anti-pattern, slow releases
```

**Principal move**: Either **change the org** or **accept the architecture** — don't draw microservices owned by one team.

---

## Team Topologies (Skelton & Pais) — mapped to this repo

| Topology | Purpose | Owns | Example in Java/Spring org |
|----------|---------|------|----------------------------|
| **Stream-aligned** | Deliver user/business value | Feature vertical | Order team: Spring Boot services + Angular checkout |
| **Platform** | Accelerate stream teams | Golden paths, K8s, CI | Internal platform — [§24 IDP](../../01_TechGuide/24_Platform_Engineering_IDP.md) |
| **Enabling** | Upskill, unblock | Short engagements | SRE embed, security champions — [§23 SRE](../../01_TechGuide/23_SRE_Reliability_Engineering.md) |
| **Complicated-subsystem** | Deep specialty | One hard domain | Payments ledger, search ranking — [Payment HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |

### Interaction modes

| Mode | When | Anti-pattern |
|------|------|--------------|
| **Collaboration** | Discovery, new product | Permanent collaboration = unclear ownership |
| **X-as-a-Service** | Stable platform APIs | Platform team as ticket queue |
| **Facilitating** | Coaching, temporary | Enabling team never leaves |

---

## Sizing teams for microservices (rule of thumb)

| Service count | Team model | ARF needed? |
|---------------|------------|-------------|
| 1–5 services | 1–2 stream-aligned teams | Light RFC for new DB |
| 5–20 | Stream + platform squad | [Multi-Team ARF](../Levels/Multi_Team_Architecture_Review.md) for every new service |
| 20+ | Domain-aligned + platform + enabling | Architecture forum + API catalog (Backstage §24) |

**Two-pizza team** ≈ 6–8 engineers → 2–4 services max with clear boundaries.

---

## Case study: E-commerce checkout (interview walkthrough)

### Broken org → broken architecture

```
Team "Full Stack" (12 people): Cart + Payment + Notification in one repo
Problem: Payment change breaks cart; one release train; PCI scope entire app
```

### Target org → target architecture

```
┌─────────────────────┐  ┌─────────────────────┐  ┌─────────────────────┐
│ Stream: Checkout    │  │ Stream: Catalog     │  │ Complicated: Payment│
│ cart-service        │  │ catalog-service     │  │ payment-service     │
│ (Spring Boot)       │  │                     │  │ (ledger, idempotency)│
└──────────┬──────────┘  └─────────────────────┘  └──────────┬──────────┘
           │ async (Kafka §19)                                │
           └──────────────────────────────────────────────────┘
┌─────────────────────┐
│ Platform: Deploy/obs │  golden path Spring Boot + EKS + OTel
└─────────────────────┘
```

**API contracts**: REST/OpenAPI at boundaries; events for `OrderPlaced`, `PaymentCaptured` — not shared PostgreSQL.

---

## Conway reversal — design org from desired architecture

1. Draw **target bounded contexts** (DDD-lite from [§03](../../01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md))
2. Assign **one stream-aligned team per context** (or sub-domain)
3. Extract **platform** when 3+ teams repeat K8s/CI work
4. Run **enabling** sprint for Kafka/outbox adoption before mandating

**India context**: Re-orgs often follow **festival scale** (Big Billion Days, IPL) — platform team forms after second firefight on same infra gap.

---

## Metrics that prove alignment

| Metric | Healthy signal |
|--------|----------------|
| Cross-team PRs per feature | Decreasing as APIs stabilize |
| Shared database tables | Trending to zero between domains |
| Platform self-service rate | >70% new services from template (§24) |
| ARF cycle time | RFC → decision < 2 weeks |
| DORA per team | Stream teams improve without platform firefighting |

---

## Interview quick reference

| Question | Answer |
|----------|--------|
| What is Conway's Law? | System structure mirrors communication structure — align or suffer. |
| When platform team? | 10+ devs repeating same infra; not day one. |
| Stream vs platform? | Stream ships features; platform ships golden paths and APIs. |
| Shared DB between teams? | Avoid — use APIs/events; shared DB = hidden monolith. |
| Re-org for microservices? | Sometimes yes — split by bounded context, not layer (frontend team / backend team). |

---

## Related

- [Multi-Team Architecture Review](../Levels/Multi_Team_Architecture_Review.md)
- [Multi-Year Vision & Build-vs-Buy](Multi_Year_Vision_Build_vs_Buy.md)
- [§06 Microservices](../../01_TechGuide/06_Microservices_Distributed_Systems.md)
