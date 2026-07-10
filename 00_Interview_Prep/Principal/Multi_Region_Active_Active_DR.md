# Multi-Region Active-Active & Disaster Recovery

> **You are here**: Principal / Architect — Reliability at Scale
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§23 SRE](../../01_TechGuide/23_SRE_Reliability_Engineering.md), [§29 Advanced Networking](../../01_TechGuide/29_Advanced_Networking_Infrastructure.md), [Payment HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) | **Next**: [Enterprise Integration](Enterprise_Integration_ESB_iPaaS_Event_Mesh.md)

Principal-level system design in **India global SaaS and fintech** (Razorpay multi-region, Freshworks, Zoho US/EU) expects you to compare **active-passive DR**, **active-active read**, and **active-active write** — with honest trade-offs for Spring Boot + PostgreSQL + Kafka stacks in this repo.

---

## DR terminology (use precisely in interviews)

| Term | Meaning | RTO typical | RPO typical |
|------|---------|-------------|-------------|
| **Backup/restore** | Periodic snapshots | Hours–days | Hours |
| **Warm standby** | Scaled-down replica region | 30–60 min | Minutes |
| **Active-passive** | Primary serves traffic; secondary on failover | 5–30 min | Seconds–minutes |
| **Active-active (read)** | Both regions serve reads; one writer | Minutes failover | Near-zero read loss |
| **Active-active (write)** | Writes in multiple regions | Complex | Conflict resolution required |

**RTO** = time to restore service. **RPO** = max acceptable data loss.

---

## Architecture patterns

### Pattern 1: Active-passive (most Java/Spring product orgs start here)

```
                    Route 53 / Global LB (health-based)
                              │
              ┌───────────────┴───────────────┐
              ▼                               ▼
     ┌─────────────────┐            ┌─────────────────┐
     │ Region: ap-south-1 (PRIMARY) │ Region: ap-southeast-1 (STANDBY) │
     │ EKS + Spring Boot services   │ Same Helm charts, min replicas     │
     │ RDS PostgreSQL (writer)      │ RDS read replica / cross-region    │
     │ MSK Kafka                    │ MirrorMaker 2 / cluster link       │
     └─────────────────┘            └─────────────────┘
```

**Failover**: Promote replica DB, shift DNS, scale standby EKS. Practice quarterly ([§23 game days](../../01_TechGuide/23_SRE_Reliability_Engineering.md)).

**Payment caveat**: Never dual-write without ledger reconciliation — [Payment System](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) idempotency keys are region-scoped or global UUID.

---

### Pattern 2: Active-active reads + single writer (common upgrade)

| Component | Strategy |
|-----------|----------|
| **Static assets / CDN** | CloudFront — naturally multi-edge |
| **Read APIs** | Route to nearest region; read from local replica |
| **Writes** | Single primary region OR shard by `user_id % N` |
| **Cache (Redis)** | Region-local; accept cache miss on failover |
| **Search (ES)** | Cross-cluster replication or rebuild from Kafka |

```java
// Routing hint in Spring — primary region for writes
@Transactional
public Order createOrder(CreateOrderRequest req) {
    if (!regionConfig.isWriteRegion()) {
        throw new RedirectToPrimaryException(regionConfig.primaryEndpoint());
    }
    return orderRepository.save(mapper.toEntity(req));
}
```

---

### Pattern 3: Active-active writes (only when business demands)

**Use when**: Global users need <50ms write latency in US and EU; regulatory data residency with local writes.

**Challenges**:
| Challenge | Mitigation |
|-----------|------------|
| **Split brain** | Consensus (etcd), or avoid — prefer single writer per entity |
| **Conflicting updates** | CRDTs (rare in payments), last-write-wins + audit, or **shard by geography** |
| **Kafka ordering** | Per-partition ordering breaks across regions — use **region prefix topics** + saga |
| **PostgreSQL** | CockroachDB/Spanner/Yugabyte for multi-region SQL, or app-level sharding |

**Interview honest answer**: "We'd use **active-active for reads** and **single-writer per payment account** until we adopt a globally consistent store or shard users by home region."

---

## Data replication cheat sheet

| Store | Cross-region option | Consistency |
|-------|---------------------|-------------|
| **PostgreSQL RDS** | Cross-region read replica, Aurora Global | Async replication — RPO > 0 |
| **S3** | Cross-region replication | Eventually consistent |
| **Redis** | Global Datastore (Redis Enterprise) / per-region | Eventual |
| **Kafka** | MirrorMaker 2, Confluent cluster linking | Async |
| **DynamoDB** | Global tables | Eventually / conflict resolution |

Deep dives: [§26 PostgreSQL](../../01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md), [§19 Event-Driven](../../01_TechGuide/19_Event_Driven_Architecture.md).

---

## DR runbook essentials

```markdown
## Failover: ap-south-1 → ap-southeast-1

### Preconditions
- [ ] Primary unhealthy > 5 min (SLO breach)
- [ ] Incident commander paged
- [ ] Finance/compliance notified if payment region

### Steps
1. Stop writes to primary (feature flag `dr.mode=true`)
2. Promote RDS replica (or trigger Aurora failover)
3. Scale EKS standby to production replica count
4. Update Route53 / GLB weights 100% → secondary
5. Verify Kafka consumer lag < threshold
6. Smoke test: login, checkout, webhook delivery

### Rollback
- Reverse DNS only when primary validated; avoid split-brain

### Post-incident
- RPO/RTO actuals, data reconciliation report for payments
```

---

## Cost vs availability trade-off

| Tier | Design | Monthly cost multiplier | When |
|------|--------|----------------------|------|
| **Tier 1** | Backup + restore | 1.1× | Internal tools |
| **Tier 2** | Active-passive warm | 1.4–1.8× | B2B SaaS |
| **Tier 3** | Active-active read | 1.8–2.2× | Consumer apps, India festival scale |
| **Tier 4** | Active-active write | 2.5–4×+ | Global fintech, regulated multi-residency |

**India festival scale** (Big Billion Days): Often **scale single region** + CDN first; multi-region active-active is year-2+ unless regulatory mandate.

---

## Tabletop exercise — PayKart region loss (90 min)

Run this as a Principal interview whiteboard or an internal game day.

### Scenario

`ap-south-1` loses connectivity for 40 minutes during a sale. PostgreSQL primary unreachable; Kafka in-region paused; standby `ap-southeast-1` is warm (active-passive).

### Facilitator checklist

| Minute | Prompt | Expected Principal moves |
|--------|--------|--------------------------|
| 0–10 | Declare Sev-1; who is IC? | Name IC, comms lead, scribe; page runbook |
| 10–25 | Failover decision | Stop writes via flag; promote replica; shift GLB; **no** dual primary |
| 25–40 | Payments in flight | Idempotency; PSP reconcile; customer messaging |
| 40–55 | Kafka | Consumers on standby; lag thresholds; no silent drop |
| 55–70 | Declare stable | Smoke tests; RTO/RPO actuals |
| 70–90 | Postmortem spine | Triggers, action items, whether Tier 3 (active-read) is justified |

### Scoring (interview)

| Signal | Strong | Weak |
|--------|--------|------|
| Split-brain avoidance | Explicit write stop before promote | "Just flip DNS" |
| Money correctness | Idempotency + reconcile | "Retry all webhooks" |
| Honesty on RTO | Minutes, not seconds, for DNS/DB | Claims zero downtime without design |
| Cost judgment | Doesn't jump to active-active write | "We should have been multi-master" |

Full migration context: [§39](../../01_TechGuide/39_Monolith_to_Microservices_Migration.md).

---

## Interview discussion points

1. **How do you avoid double payment on failover?** Idempotency keys in durable store replicated or global ID service; reconcile with PSP.
2. **DNS failover vs anycast?** DNS TTL 60s = minutes of bleed; GLB health checks faster.
3. **Kafka during region loss?** Consumers in healthy region; replay from offset after mirror catches up.
4. **GDPR data residency?** EU users' data never written to India region — shard by residency, not just DR copy ([§38](../../01_TechGuide/38_Compliance_and_Regulated_Systems.md)).
5. **Walk the tabletop** above in 10 minutes.

---

## Related

- [§31 Cloud AWS/GCP/Azure](../../01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md)
- [§23 SRE](../../01_TechGuide/23_SRE_Reliability_Engineering.md)
- [Critical Connections DSA](../../02_DSA/11_Graphs/CriticalConnections/CriticalConnections.md) — network bridge analogy
- [Multi-Year Vision](Multi_Year_Vision_Build_vs_Buy.md) — when to defer multi-region
- [On-Call & Incidents](../../06_On_The_Job/03_On_Call_Incident_Response.md)
