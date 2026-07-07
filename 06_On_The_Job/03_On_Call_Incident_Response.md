# On-Call and Incident Response

> **You are here**: On the Job — Senior SDE / Tech Lead
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [Production Debugging](02_Production_Debugging.md), [§23 SRE](../01_TechGuide/23_SRE_Reliability_Engineering.md)

---

## What on-call means

You are the **first responder** when production breaks outside business hours. Your job is to **mitigate user impact**, **coordinate communication**, and **hand off** to a durable fix — not to single-handedly solve every problem at 3 AM.

---

## Before your first on-call shift

| Preparation | Action |
|-------------|--------|
| Access | PagerDuty/Opsgenie, kubectl, cloud console, DB read access, runbooks |
| Runbooks | Read runbooks for your top 5 alert types |
| Dashboards | Bookmark service health, error rate, latency, DB |
| Escalation | Know who to page for infra, DB, security |
| Shadow | Do 1–2 shadow shifts with current on-call |

---

## Incident response lifecycle

```
Detect → Triage → Mitigate → Resolve → Post-mortem → Follow-up
```

### 1. Detect

Alerts fire from: metrics thresholds, synthetic checks, user reports, support tickets.

**Don't ignore flapping alerts** — investigate or tune thresholds.

### 2. Triage (first 5 minutes)

| Question | Action |
|----------|--------|
| Is this real? | Check dashboard, not just one alert |
| Who is impacted? | All users? One region? One tenant? |
| Severity? | P0 = incident channel + comms lead |
| Recent change? | Deploy, config, feature flag, infra |

**Declare incident** when P0/P1: create Slack/Teams channel `#inc-2024-0315-checkout-outage`.

### 3. Mitigate (user impact first)

| Mitigation | When |
|------------|------|
| **Rollback** deploy | Error spike after release |
| **Scale up** | Traffic or CPU saturation |
| **Disable feature flag** | New feature causing failures |
| **Failover** | Region or AZ failure |
| **Block bad traffic** | DDoS, bad client version |
| **Circuit breaker** | Downstream dependency failing |

**Rule**: Mitigate first, root-cause later — if users cannot pay, rollback beats debugging.

### 4. Resolve

Permanent fix: code patch, config correction, capacity increase, dependency fix.

Verify:
- Error rate back to baseline
- Latency normal
- Synthetic checks pass
- No new errors in logs

### 5. Post-mortem (within 48–72 hours)

**Blameless** — focus on systems and process, not individuals.

---

## Incident roles (medium+ incidents)

| Role | Responsibility |
|------|----------------|
| **Incident Commander (IC)** | Coordinates; does not debug alone. Assigns tasks. |
| **Communications Lead** | Status page, stakeholder updates every 30–60 min |
| **Subject Matter Experts** | Debug assigned areas (DB, payment, frontend) |
| **Scribe** | Timeline in incident doc |

For small teams, one person may wear multiple hats. For P0, split roles early.

---

## Communication templates

### Internal update (every 30–60 min during P0)

```
INCIDENT: Checkout failures — P0
STATUS: Investigating / Mitigated / Resolved
IMPACT: ~30% of checkout attempts failing since 14:32 IST
ACTION: Rolled back deploy v2.4.1 at 14:55; error rate dropping
NEXT: Monitoring 30 min; root cause analysis in progress
IC: @alice
```

### Status page (external)

```
We are investigating elevated error rates affecting checkout.
Some users may be unable to complete purchases.
We will update every 30 minutes.
```

**Never**: Share root cause before confirmed. **Never**: Blame a vendor publicly without legal review.

---

## Post-mortem template

```markdown
# Post-mortem: Checkout outage — 2024-03-15

## Summary
30% checkout failure for 23 minutes due to payment DB connection pool exhaustion.

## Impact
- Duration: 14:32–14:55 IST (23 min)
- Users affected: ~4,200 failed checkouts
- Revenue impact: [estimated by PM/finance]

## Timeline (all times IST)
- 14:32 — Error rate alert fires
- 14:35 — On-call acknowledges
- 14:40 — Identified payment-service DB timeouts
- 14:50 — Root cause: missing index on new query in v2.4.1
- 14:55 — Rollback to v2.4.0 complete
- 15:00 — Error rate normalized

## Root cause
Deploy v2.4.1 added a report query without index on orders.status.
Under load, queries held connections → pool exhausted → checkout failed.

## What went well
- Rollback runbook worked; 5 min rollback time
- IC assigned quickly

## What went wrong
- No load test on new query path
- Connection pool alert threshold too high

## Action items
| Action | Owner | Due |
|--------|-------|-----|
| Add index on orders.status | @bob | 2024-03-16 |
| CI check: EXPLAIN on new queries | @carol | 2024-03-22 |
| Lower pool utilization alert to 70% | @alice | 2024-03-18 |
```

---

## SLOs and error budgets (brief)

| Term | Meaning |
|------|---------|
| **SLI** | Metric (availability, latency) |
| **SLO** | Target (99.9% availability/month) |
| **Error budget** | Allowed downtime (99.9% = ~43 min/month) |

When error budget is burned: freeze features, focus on reliability.

Deep dive: [§23 SRE](../01_TechGuide/23_SRE_Reliability_Engineering.md)

---

## On-call wellbeing

| Practice | Why |
|----------|-----|
| Hand off cleanly at shift end | Document open threads |
| Page only for actionable alerts | Alert fatigue → missed real incidents |
| Post-incident rest (some companies) | 3 AM fix deserves recovery time |
| Say "I need help" early | Escalation is strength, not weakness |

---

## Interview talking points

- "I was on-call for order service. During a P1, I rolled back within 8 minutes, then led post-mortem with 3 preventive action items."
- "I reduced alert noise by 40% by fixing flaky thresholds — on-call satisfaction improved."

**Next**: [04_RFC_ADR_Writing.md](04_RFC_ADR_Writing.md)
