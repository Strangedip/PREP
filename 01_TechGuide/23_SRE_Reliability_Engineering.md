# Section 23: SRE & Reliability Engineering (2026)

> **Level**: SR+ (SLI/SLO basics) to LEAD (error budgets, incident command, toil reduction)
> **Why This Matters**: Lead engineers own reliability, not just features. Google-style SRE practices are standard at FAANG and product companies. You must connect observability metrics to business outcomes and incident response.

> **You are here**: Senior SDE — Engineering Practices
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [22_Kotlin_for_Java_Developers.md](22_Kotlin_for_Java_Developers.md) | **Next**: [24_Platform_Engineering_IDP.md](24_Platform_Engineering_IDP.md)

---

## 23.1 SRE vs DevOps vs Platform Engineering

| Role | Focus |
|------|-------|
| **DevOps** | CI/CD, automation, bridge dev and ops |
| **SRE** | Reliability as engineering — SLIs, SLOs, error budgets, incident response |
| **Platform Engineering** | Internal developer platforms, golden paths, self-service infra |

**SRE mantra**: "Hope is not a strategy." Reliability is measured, budgeted, and engineered.

---

## 23.2 SLI → SLO → SLA → Error Budget

```
SLI (Indicator)   →  What you measure (latency, availability, error rate)
SLO (Objective)   →  Target for SLI (99.9% availability, p99 < 200ms)
SLA (Agreement)   →  Contract with customers (often looser than internal SLO)
Error Budget      →  1 - SLO = allowed unreliability (0.1% = ~43 min/month downtime at 99.9%)
```

**Example**:

| Service | SLI | SLO | Error Budget (monthly) |
|---------|-----|-----|------------------------|
| Payment API | Success rate | 99.95% | ~21 minutes downtime |
| Search | p99 latency < 300ms | 99% of requests | 1% slow requests allowed |
| Auth | Availability | 99.9% | ~43 minutes |

**Error budget policy**: When budget is exhausted → freeze feature launches, focus on reliability work.

---

## 23.3 The Four Golden Signals (Google)

| Signal | Question | Metrics |
|--------|----------|---------|
| **Latency** | How long do requests take? | p50, p95, p99 — distinguish success vs error latency |
| **Traffic** | How much demand? | QPS, concurrent users, queue depth |
| **Errors** | What's failing? | HTTP 5xx rate, exception rate, business errors |
| **Saturation** | How full is the system? | CPU, memory, disk I/O, connection pool usage |

**RED Method** (services): Rate, Errors, Duration.
**USE Method** (resources): Utilization, Saturation, Errors.

---

## 23.4 Incident Management

### Severity Levels

| Level | Definition | Response |
|-------|------------|----------|
| **SEV1** | Complete outage or data loss | All hands, exec notification, war room |
| **SEV2** | Major degradation, no workaround | On-call + team, hourly updates |
| **SEV3** | Partial impact, workaround exists | On-call handles, next business day fix |
| **SEV4** | Minor, cosmetic | Backlog |

### Incident Roles

| Role | Responsibility |
|------|----------------|
| **Incident Commander (IC)** | Coordinates response, makes decisions, communicates |
| **Operations Lead** | Executes remediation, rollback, scaling |
| **Communications Lead** | Status page, stakeholder updates |
| **Scribe** | Timeline, actions taken — feeds post-mortem |

### Incident Response Flow

```
Detect (alert) → Triage → Mitigate → Resolve → Post-mortem (within 5 business days)
```

**Mitigate first, root-cause later** — restore service before deep debugging.

---

## 23.5 Post-Mortems — Blameless Culture

**Template**:

1. **Summary** — What happened, duration, impact (users affected, revenue)
2. **Timeline** — Minute-by-minute from detection to resolution
3. **Root Cause** — Technical cause (5 Whys)
4. **Contributing Factors** — Process gaps, missing alerts, tech debt
5. **What Went Well** — Effective responses to repeat
6. **Action Items** — Concrete, assigned, with due dates (not "be more careful")

**Blameless**: Focus on systems and processes, not individuals.

---

## 23.6 Toil Reduction

**Toil** = manual, repetitive, automatable operational work that scales with service growth.

| Toil Example | Automation |
|--------------|------------|
| Manual deploy rollback | Automated rollback on SLO breach |
| Restarting stuck pods | Self-healing + liveness probes |
| On-call paging for known issues | Runbooks → auto-remediation |
| Capacity planning spreadsheets | HPA + predictive scaling |

**Target**: < 50% of SRE time on toil; rest on engineering (automation, tooling, architecture).

---

## 23.7 Reliability Patterns (Production)

| Pattern | Purpose |
|---------|---------|
| **Circuit Breaker** | Stop calling failing dependencies |
| **Bulkhead** | Isolate failures (separate thread pools) |
| **Graceful degradation** | Serve cached/stale data when dependency down |
| **Feature flags** | Kill switch for bad deploys without rollback |
| **Canary + auto-rollback** | Deploy to 1% → monitor SLO → promote or revert |
| **Chaos engineering** | Proactively inject failures (Chaos Monkey, Litmus) |
| **Multi-region failover** | Active-active or active-passive DR |

---

## 23.8 On-Call Best Practices

| Practice | Detail |
|----------|--------|
| **Actionable alerts** | Every alert needs a runbook link; no "CPU > 80%" without context |
| **Alert fatigue** | Tune thresholds; aggregate related alerts |
| **Runbooks** | Step-by-step mitigation for common failures |
| **Escalation policy** | 15 min no ack → escalate to secondary → manager |
| **On-call compensation** | Rotation limits (max 1 week), follow-up time for incidents |

---

## 23.9 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | SLI vs SLO vs SLA? | SLI = metric, SLO = internal target, SLA = customer contract. |
| 2 | Error budget? | Allowed unreliability; when exhausted, prioritize stability over features. |
| 3 | Four golden signals? | Latency, Traffic, Errors, Saturation. |
| 4 | SEV1 response? | War room, IC leads, mitigate first, communicate, post-mortem. |
| 5 | Blameless post-mortem? | Focus on systems; timeline + root cause + action items, no finger-pointing. |
| 6 | Toil? | Manual ops work that should be automated; SREs should minimize it. |
| 7 | RED vs USE? | RED for services (rate/errors/duration), USE for resources (util/saturation/errors). |
| 8 | When to freeze releases? | Error budget exhausted or approaching SLO breach during incident. |
| 9 | Chaos engineering? | Inject controlled failures to validate resilience before real outages. |
| 10 | SRE vs DevOps? | SRE applies software engineering to operations problems with measurable SLOs. |

**Must-say keywords**: SLI, SLO, error budget, golden signals, incident commander, blameless post-mortem, toil, mitigation, runbook, canary rollback.

---

## §23.10 Production & Interview Depth — SLOs for Indian Festival Scale

Flipkart Big Billion Days and Paytm UPI peaks are the Indian equivalent of Prime Day — traffic jumps 10–50× for 72 hours. SRE interviews at product companies ask how you'd **protect payment SLOs** while product insists on feature flags for sale banners. The answer ties SLIs to revenue, not just HTTP 200.

### SLI Design for Payments (Razorpay / PhonePe Context)

| SLI | Measurement | SLO (typical internal) | Why it matters |
|-----|-------------|------------------------|----------------|
| Payment success rate | `successful_captures / initiated` (exclude user abort) | 99.95% | Direct GMV impact |
| p99 auth latency | Gateway → PSP round-trip | < 800ms at p99 | UPI timeout = user retries = double load |
| Webhook delivery | Delivered within 30s / enqueued | 99.9% | Merchant reconciliation |
| Queue lag | Kafka consumer offset lag | < 5s p99 | Settlement pipeline backlog |

Error budget burn during a sale: if payment SLO drops below 99.9% in the first 6 hours, **freeze non-critical deploys** and scale checkout pods — see [11_Observability.md](./11_Observability.md) for RED metrics wiring.

### Spring Boot Production Pattern: SLO-Aware Circuit Breaking

```java
@RestController
public class PaymentController {

    private final CircuitBreaker pspBreaker = CircuitBreaker.of("razorpay-psp",
        CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(100)
            .build());

    @PostMapping("/pay")
    public PaymentResponse pay(@RequestBody PaymentRequest req) {
        return pspBreaker.executeSupplier(() -> pspClient.charge(req));
        // fallback: queue for async retry + graceful "try again" UX
    }
}
```

Use Resilience4j (pairs with [06_Microservices_Distributed_Systems.md](./06_Microservices_Distributed_Systems.md)). Incident command during SEV1: IC owns comms to business ("₹X GMV at risk"), ops lead scales HPA and toggles feature flags, scribe captures timeline for blameless post-mortem within 5 days. **Mitigate first** — route to backup PSP, enable cached product catalog from [28_Redis_Distributed_Caching.md](./28_Redis_Distributed_Caching.md) — root-cause JDBC pool exhaustion can wait until traffic subsides.
