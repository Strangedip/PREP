# Multi-Year Technical Vision & Build-vs-Buy Framework

> **You are here**: Principal / Architect — Technical Strategy
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§20 Technical Leadership](../../01_TechGuide/20_Technical_Leadership_Architecture.md), [Multi-Team Architecture Review](../Levels/Multi_Team_Architecture_Review.md) | **Next**: [Vendor Evaluation Rubrics](Vendor_Evaluation_Rubrics.md)

Principal engineers in **India product orgs** (Flipkart platform, Razorpay infra, PhonePe core, Amazon horizontal teams) are judged on **3–5 year bets** — not sprint output. This guide ties vision work to this repo's Spring/Kafka/K8s stack and interview loops.

---

## What "multi-year vision" means here

| Horizon | Owner | Output | Repo anchor |
|---------|-------|--------|-------------|
| **0–6 months** | Tech Lead / Staff | RFCs, ADRs, quarterly OKRs | [§20 ADRs](../../01_TechGuide/20_Technical_Leadership_Architecture.md) |
| **6–18 months** | Staff / Principal | Platform roadmap, strangler migrations | [§24 IDP](../../01_TechGuide/24_Platform_Engineering_IDP.md) |
| **18–36 months** | Principal | Domain architecture, data platform, multi-region | [Payment HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |
| **36+ months** | Principal + VP Eng | Build-vs-buy, vendor strategy, org capability | This doc |

**Interview signal**: You can articulate **one big bet**, what you deferred, and how you measured progress — without a 40-slide deck.

---

## Vision document template (2–4 pages)

```markdown
# Technical Vision: [Domain] — FY26–FY28

## Current state (honest)
- Monolith/module boundaries, p99 latency, incident themes, team topology
- Link: post-mortems, DORA metrics from §24

## Forces (why change now)
- Regulatory (PCI/GDPR — §38), traffic 3×, cost per txn, hiring constraints

## Target state (18–24 months)
- Capability map: Catalog | Cart | Payment | Fulfillment
- NFRs: 99.95% availability, <200ms checkout p99, audit-ready

## Strategic pillars (max 3)
1. Event-native order pipeline (Kafka + outbox — §19)
2. Golden-path microservices on EKS (§24, §30)
3. Observability + SLO culture (§23)

## Explicit non-goals
- No multi-region active-active in FY26 (cost; see Multi_Region doc)
- No GraphQL federation until 15+ services (§21)

## Milestones & metrics
| Quarter | Milestone | Metric |
|---------|-----------|--------|
| Q1 | Outbox on payment service | 0 duplicate charges in shadow |
| Q2 | 30% traffic on new checkout | p99 ≤ baseline + 10% |

## Risks & mitigations
| Risk | Mitigation |
|------|------------|
| Team Conway mismatch | Re-org slice with platform squad (Org Design doc) |
| Vendor lock-in | Abstraction layer + exit criteria in vendor rubric |
```

---

## Build vs buy — decision framework

Use when the bet touches **payments, search, ML, identity, observability, or integration buses**.

### Scoring matrix (weight for your context)

| Criterion | Weight | Build score 1–5 | Buy score 1–5 | Notes |
|-----------|--------|-----------------|---------------|-------|
| **Differentiation** | 25% | High if core revenue path | Low if commodity | Checkout ledger → build; email → buy |
| **Time to market** | 20% | Slow | Fast | Razorpay/Stripe for PCI scope reduction (§38) |
| **Total cost (3 yr)** | 20% | Eng + ops + opportunity cost | License + integration | Include 2 FTE maintenance for build |
| **Compliance / data residency** | 15% | You own audit story | Vendor SOC2/PCI attestation | India data localization — verify region |
| **Integration fit** | 10% | Native Spring/Kafka | APIs, iPaaS, batch sync | See Enterprise Integration doc |
| **Exit / portability** | 10% | You own code | Contract, data export, API standard | Kafka > proprietary queue |

**Rule of thumb for Java/Spring shops**:
- **Build**: Order state machine, pricing engine, idempotent payment orchestration, internal platform (§24)
- **Buy**: SMS/email, fraud scoring (initially), APM (Datadog/New Relic), identity (Auth0/Cognito), feature flags
- **Hybrid**: RAG — buy embeddings API, build retrieval + guardrails ([§03 RAG](../../05_AI/03_RAG_Architecture.md))

---

## Executive narrative (60 seconds)

> "Our FY27 vision is **event-native commerce** on Kubernetes. We will **buy** payment tokenization and observability to reduce PCI and ops burden; we will **build** checkout orchestration and inventory reservation because that's our differentiation and latency SLO. We defer multi-region active-active until single-region SLO is green for two quarters. Success = 40% fewer checkout incidents and deploy frequency 2× via golden paths."

Pair with [Executive Communication](Executive_Communication_Board_Narrative.md) for board/VP format.

---

## Common failure modes (Principal interviews)

| Failure | Why it fails | Fix |
|---------|--------------|-----|
| Technology-first vision | "We'll move everything to K8s" with no business metric | Start from revenue/SLO/incident data |
| No non-goals | Scope creep across 12 initiatives | Max 3 pillars; publish defer list |
| Build everything | 18-month science project | Buy commodity; build moat |
| Buy everything | No platform skill, vendor tax | Own orchestration + data model |
| Ignoring Conway | Org can't staff the architecture | Align teams to pillars first |

---

## Related

- [Vendor Evaluation Rubrics](Vendor_Evaluation_Rubrics.md)
- [Organization Design — Conway & Team Topologies](Organization_Design_Conway_Team_Topologies.md)
- [Staff/Principal Advance Criteria](../Levels/Staff_Principal_Advance_Criteria.md)
- [Principal Interview Loop Guide](Interview_Loop_Guide.md)
