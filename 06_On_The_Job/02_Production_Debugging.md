# Production Debugging — From Alert to Root Cause

> **You are here**: On the Job — SDE2+
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [§23 SRE](../01_TechGuide/23_SRE_Reliability_Engineering.md) | **Next**: [On-Call & Incidents](03_On_Call_Incident_Response.md)

---

## Mindset: debug systematically, not randomly

Production bugs differ from local bugs: **you cannot always reproduce**, **data is sensitive**, and **time pressure is real**. Use a repeatable process instead of guessing.

```
1. Triage severity (P0–P3)
2. Stabilize (mitigate before root cause if users are impacted)
3. Gather evidence (logs, metrics, traces, recent deploys)
4. Form hypothesis → test → narrow
5. Fix → verify → document
```

---

## Step 1: Triage — How bad is it?

| Severity | User impact | Response |
|----------|-------------|----------|
| **P0** | Service down, data loss, payment broken | All hands, incident channel, rollback first |
| **P1** | Major feature broken, significant subset affected | Fix within hours |
| **P2** | Degraded performance, workaround exists | Fix within days |
| **P3** | Minor bug, cosmetic | Backlog |

**First question**: Is this a **deploy regression**? Check if error rate spiked after a deployment. If yes, **rollback** is often fastest mitigation.

---

## Step 2: Gather evidence

### Logs — your primary weapon

```bash
# Kubernetes — tail logs for a pod
kubectl logs -f deployment/order-service -n production --tail=200

# Filter by request ID (correlation)
kubectl logs deployment/order-service | grep "req-abc-123"

# Previous crashed container
kubectl logs pod/order-service-xyz --previous
```

**What to look for**:
- Stack traces (`Exception`, `Error`)
- `ERROR` and `WARN` lines before the failure
- Request ID to correlate across services

### Structured logging best practice

```java
log.info("Order created orderId={} userId={} amount={}", orderId, userId, amount);
// NOT: log.info("Order created " + orderId);  // hard to search
```

Always log: **who** (userId), **what** (orderId), **when** (timestamp automatic), **correlation ID** (requestId).

### Metrics — when did it break?

Open your dashboard (Grafana, Datadog, CloudWatch):

| Metric | Tells you |
|--------|-----------|
| Error rate (5xx) | Server failures — spike = deploy or dependency |
| Latency p95/p99 | Slowdown — DB, external API, GC |
| Throughput (RPS) | Traffic spike or drop |
| CPU / memory | Resource exhaustion |
| DB connections | Pool exhaustion |

**Correlate**: Overlay deploy markers on error rate graph.

### Distributed tracing

When a request crosses 5 microservices, logs alone are insufficient.

```
Browser → API Gateway → Order Service → Payment Service → DB
         [trace-id: abc123 spans entire chain]
```

Tools: Jaeger, Zipkin, AWS X-Ray, Datadog APM.

**How to use**: Find a failed request's `trace-id` in logs → open trace → see which span failed and how long each step took.

---

## Step 3: Common failure patterns

### Database issues

| Symptom | Likely cause | Investigation |
|---------|--------------|---------------|
| Sudden 503s | Connection pool exhausted | Check active connections vs pool max |
| Slow endpoints | Missing index, full table scan | `EXPLAIN` on slow query |
| Deadlocks | Concurrent updates same rows | DB deadlock logs |
| Migration failure | Schema change broke app | Check flyway/liquibase history |

```sql
-- PostgreSQL: find slow queries
SELECT query, mean_exec_time, calls
FROM pg_stat_statements
ORDER BY mean_exec_time DESC
LIMIT 10;
```

### JVM / Java-specific

| Symptom | Tool | Action |
|---------|------|--------|
| High CPU | `jstack` thread dump | Find hot threads; infinite loop? regex? |
| Memory leak | Heap dump + MAT/VisualVM | Dominator tree — what's retaining objects? |
| GC pauses | GC logs, JFR | Tune heap or fix allocation hotspot |
| Thread deadlock | `jstack` | Look for "deadlock" section |

```bash
# Thread dump (safe in production — no pause)
jstack <pid> > thread-dump.txt

# Or via kubectl
kubectl exec -it pod/order-service-xyz -- jstack 1
```

**Reading thread dumps**: Search for `BLOCKED` threads and what lock they wait on. Search for your business logic class names in `RUNNABLE` threads burning CPU.

### External dependency failures

| Symptom | Check |
|---------|-------|
| Timeout errors | Dependency latency dashboard; circuit breaker state |
| 429 from partner API | Rate limits; backoff and retry config |
| DNS failures | Recent infra change, cert expiry |

**Pattern**: Your service is healthy; downstream is not. Fix: circuit breaker, fallback, or escalate to dependency owner.

### Caching issues

| Symptom | Cause |
|---------|-------|
| Stale data | Cache TTL too long; missing invalidation |
| Thundering herd | Cache expired; all requests hit DB |
| Inconsistent reads | Cache and DB out of sync |

---

## Step 4: Reproduce and isolate

### Reproduce in staging

1. Copy anonymized production data pattern (not raw PII)
2. Replay request with same headers/body
3. Use feature flags to enable debug logging temporarily

### Binary search the problem space

| Dimension | Halve it |
|-----------|----------|
| Time | When did it start? Last deploy? Config change? |
| Scope | All users or one tenant? All regions or one? |
| Layer | Disable cache — still fails? Mock payment — still fails? |

### The "5 Whys" for root cause

```
Why did checkout fail? → Payment service returned 500
Why? → DB connection timeout
Why? → Connection pool maxed out
Why? → Slow queries held connections
Why? → Missing index on orders.created_at
Root cause: Missing index. Fix: Add index + increase pool temporarily.
```

---

## Step 5: Fix, verify, document

### Before merging fix

- [ ] Test in staging with production-like load if possible
- [ ] Add regression test if bug was logic error
- [ ] Add metric or alert if blind spot caused late detection
- [ ] Plan rollback if fix deploy fails

### Post-incident

Write a **blameless post-mortem** (see [On-Call Guide](03_On_Call_Incident_Response.md)):
- Timeline
- Root cause (not who to blame)
- Action items with owners

---

## Debugging toolkit cheat sheet

| Tool | Use for |
|------|---------|
| `kubectl logs` | Container stdout/stderr |
| `kubectl describe pod` | OOMKilled, crash loop, events |
| `jstack` | Thread states, deadlocks |
| `jmap -dump` | Heap dump for memory leaks |
| `curl -v` | Reproduce HTTP calls with headers |
| `redis-cli MONITOR` | Debug cache (careful in prod — noisy) |
| `EXPLAIN ANALYZE` | Query plan |
| APM trace view | Cross-service latency |

---

## Interview STAR story template

> "In production, our checkout API p99 latency jumped from 200ms to 8s. I correlated the spike with a deploy, checked traces, and found Payment Service waiting on a new DB query without an index. I recommended rollback for immediate relief, added the index in a hotfix, and we added a CI check for missing indexes on new queries. p99 returned to 180ms within 2 hours."

---

## Related resources

- [§18 JVM Performance](../01_TechGuide/18_Performance_Engineering_JVM.md)
- [§23 SRE](../01_TechGuide/23_SRE_Reliability_Engineering.md)
- [On-Call & Incidents](03_On_Call_Incident_Response.md)

**Next**: [03_On_Call_Incident_Response.md](03_On_Call_Incident_Response.md)
