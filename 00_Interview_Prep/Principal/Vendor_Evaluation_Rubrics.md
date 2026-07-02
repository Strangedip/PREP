# Vendor Evaluation & Build-vs-Buy Rubrics

> **You are here**: Principal / Architect — Procurement & Strategy
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [Multi-Year Vision & Build-vs-Buy](Multi_Year_Vision_Build_vs_Buy.md), [§12 Security](../../01_TechGuide/12_Security_OWASP_Cloud.md) | **Next**: [Executive Communication](Executive_Communication_Board_Narrative.md)

When **Java/Spring orgs** choose Datadog vs Grafana, Confluent vs MSK, MuleSoft vs Kafka, or Razorpay vs custom ledger — Principal engineers own the **technical scorecard**. This rubric complements [Build-vs-Buy framework](Multi_Year_Vision_Build_vs_Buy.md) with a repeatable evaluation process for India enterprise procurement cycles.

---

## When to run a formal vendor eval

| Trigger | Example |
|---------|---------|
| New capability gap | Need APM, feature flags, iPaaS |
| Contract renewal | Datadog bill 3× YoY |
| Compliance requirement | SOC2 + India data residency |
| Scale failure | Managed Kafka limits hit |
| M&A integration | Two observability stacks |

**Skip formal eval** for <$20K/yr SaaS with easy exit — use team spike (1 week).

---

## Evaluation process (4–6 weeks)

```
Week 1: Requirements + must-haves (security, region, integration)
Week 2: Longlist 3–5 vendors + build option
Week 3: POC with production-like workload (not hello-world)
Week 4: Scorecard + TCO model
Week 5: Reference calls (other India eng teams)
Week 6: Recommendation doc → ARF if platform-wide ([Multi-Team ARF](../Levels/Multi_Team_Architecture_Review.md))
```

---

## Scorecard template (weighted)

| Category | Weight | Criteria |
|----------|--------|----------|
| **Functional fit** | 25% | Meets requirements; roadmap alignment |
| **Integration** | 20% | Spring Boot, Kafka, K8s, SSO (OIDC), Terraform |
| **Security & compliance** | 20% | SOC2, ISO27001, PCI, data residency (Mumbai/Hyderabad region) |
| **Operability** | 15% | SLAs, support TAM, on-call integration, runbooks |
| **TCO (3 year)** | 15% | License + egress + eng integration + maintenance |
| **Exit risk** | 5% | Data export, API standards, contract terms |

### Scoring scale

| Score | Meaning |
|-------|---------|
| 5 | Exceeds; production-proven at our scale |
| 4 | Meets with minor gaps |
| 3 | Meets with workarounds |
| 2 | Significant gap |
| 1 | Disqualifying |

**Disqualifiers (auto-fail)**:
- No India/APAC region when PII requires it ([§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md))
- No SSO/SAML for enterprise
- Prohibits penetration test scope
- Vendor locks data without export API

---

## TCO model (3-year) — Java shop example

```
TCO = License + Implementation + Integration eng + Ongoing ops + Opportunity cost

Example: Managed Kafka (Confluent Cloud) vs MSK self-managed

Confluent:
  License:     $X / year (by throughput + retention)
  Impl:        4 eng-weeks (connectors, Schema Registry)
  Ops:         0.2 FTE platform
  Opportunity: Faster schema governance

MSK + self Schema Registry:
  License:     AWS MSK hours + S3
  Impl:        12 eng-weeks (operate registry, monitoring)
  Ops:         0.8 FTE platform
  Opportunity: Team learns Kafka ops — good if core competency

Document assumptions in recommendation — CFO will ask.
```

---

## Category-specific must-asks

### Observability (Datadog / New Relic / Grafana Cloud)

| Question | Why |
|----------|-----|
| Per-host vs per-span pricing at 500 services? | Bill shock |
| OpenTelemetry native? | Avoid agent lock-in ([§11](../../01_TechGuide/11_Observability.md)) |
| PII scrubbing in logs? | Compliance |
| India data storage? | Residency |

### API / Integration (MuleSoft / Kong / Apigee)

| Question | Why |
|----------|-----|
| Rate limit + auth at gateway? | [Rate Limiter HLD](../../04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) |
| Spring Boot service discovery integration? | K8s native vs manual |
| Event vs REST mediation? | [Enterprise Integration](Enterprise_Integration_ESB_iPaaS_Event_Mesh.md) |

### Payments (Razorpay / Stripe / Adyen)

| Question | Why |
|----------|-----|
| SAQ level for our integration? | PCI scope ([§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md)) |
| Idempotency + webhook retry semantics? | [Payment HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) |
| Settlement reconciliation API? | India GST / ledger |

### Vector DB / AI (Pinecone / pgvector / Weaviate)

| Question | Why |
|----------|-----|
| Latency p99 at our QPS? | RAG path ([§04 Vector DB](../../05_AI/04_Vector_Databases_Embeddings.md)) |
| Hybrid with PostgreSQL? | [§26 pgvector](../../01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md) |
| Embedding model portability? | Vendor model lock-in |

---

## POC criteria (make it fair)

```markdown
## POC: [Vendor] for [use case]

### Workload
- 10K RPS read / 1K RPS write (or realistic peak)
- Same JVM/Spring Boot app as prod
- Failure injection: kill broker, network partition

### Success metrics
| Metric | Target |
|--------|--------|
| p99 latency | ≤ current + 10% |
| Setup time | ≤ 2 eng-weeks |
| Alert → root cause | ≤ 15 min in drill |
| Data export | Full export in < 24h test |

### Participants
- Platform + 1 stream-aligned team (Conway alignment)
```

---

## Recommendation doc outline

1. **Decision**: Select vendor X / build / hybrid
2. **Context**: 2 sentences business driver
3. **Options considered**: Table with scores
4. **POC results**: Metrics vs targets
5. **Risks & mitigations**: Lock-in, cost cap, exit plan
6. **Contract asks**: Multi-year discount, data residency clause, SLA credits
7. **Rollout**: Pilot team → platform mandate timeline

Present to execs via [Executive Communication](Executive_Communication_Board_Narrative.md) one-pager.

---

## Interview quick reference

| Question | Answer |
|----------|--------|
| How do you evaluate vendors? | Weighted scorecard + POC on real workload + TCO + exit plan. |
| Build vs buy observability? | Buy if team < 5 platform; build on OSS if OTel + Grafana skill exists. |
| Avoid vendor lock-in? | Open APIs, OTel, data export contract, abstraction at integration boundary. |
| Who owns decision? | Principal recommends; ARF for platform; procurement for contract. |

---

## Related

- [Multi-Year Vision & Build-vs-Buy](Multi_Year_Vision_Build_vs_Buy.md)
- [Enterprise Integration](Enterprise_Integration_ESB_iPaaS_Event_Mesh.md)
- [Companies Guide — negotiation](../Core/Companies.md)
