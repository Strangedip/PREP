# Metrics & Monitoring System — High-Level Design

## Problem Statement

Design a metrics and monitoring platform (like Datadog / Prometheus ecosystem at scale) that:

- **Collects** metrics from thousands of services (counters, gauges, histograms)
- **Stores** time-series data with efficient aggregation
- **Queries** dashboards and alerts (e.g., p99 latency > 500ms)
- **Alerts** on thresholds and anomaly detection
- **Scales** to millions of metrics per second

---

## Requirements

### Functional Requirements

1. **Metric ingestion** — Push (agents) or pull (scrape) from apps
2. **Metric types** — Counter, Gauge, Histogram, Summary
3. **Labels/tags** — `service`, `host`, `region`, `endpoint`
4. **Query API** — Time range, aggregation (sum, avg, p99), grouping
5. **Dashboards** — Grafana-style visualization
6. **Alerting** — Rules on metrics → PagerDuty/Slack

### Non-Functional Requirements

1. **Write scale** — 10M+ data points/sec
2. **Read latency** — Dashboard queries < 2s for 24h range
3. **Retention** — Raw 15 days, downsampled 1 year
4. **Availability** — Monitoring must survive partial outages

---

## Capacity Estimation

```
Services: 10,000
Metrics per service: 500 (avg)
Scrape interval: 15 sec
Data points/sec: 10K × 500 / 15 ≈ 333K/sec (raw)
Each point: ~16 bytes (timestamp + value + labels compressed)
Write bandwidth: ~5 MB/sec raw; with replication 3×
Storage/day: 333K × 86400 × 16 ≈ 460 GB/day raw → downsample reduces long-term
```

---

## High-Level Architecture

```
┌─────────────┐  push/pull  ┌──────────────┐  ┌─────────────────┐
│ App / Agent │────────────▶│ Ingestion    │─▶│ Stream Buffer   │
│ (OTel SDK)  │             │ Gateway      │  │ (Kafka)         │
└─────────────┘             └──────────────┘  └────────┬────────┘
                                                         │
                    ┌────────────────────────────────────┼────────────────────┐
                    ▼                    ▼               ▼                    ▼
            ┌──────────────┐    ┌──────────────┐ ┌──────────────┐   ┌──────────────┐
            │ TSDB Writer  │    │ TSDB Writer  │ │ Alert        │   │ Cardinality  │
            │ (shard 1)    │    │ (shard N)    │ │ Evaluator    │   │ Limiter      │
            └──────┬───────┘    └──────┬───────┘ └──────┬───────┘   └──────────────┘
                   │                   │                │
                   └─────────┬─────────┘                │
                             ▼                          ▼
                    ┌─────────────────┐         ┌───────────────┐
                    │ Time-Series DB  │         │ Notification  │
                    │ (Cassandra /    │         │ (PagerDuty)   │
                    │  M3 / Victoria) │         └───────────────┘
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │ Query Service   │◀── Grafana / API
                    │ + Cache         │
                    └─────────────────┘
```

---

## Metric Model (OpenTelemetry / Prometheus style)

```
http_requests_total{service="api", method="GET", status="200"} 1523 @ 1719398400
http_request_duration_seconds_bucket{le="0.1"} 1200
```

| Type | Use | Example |
|------|-----|---------|
| **Counter** | Monotonic increase | Total requests |
| **Gauge** | Point-in-time value | Queue depth, CPU % |
| **Histogram** | Distribution buckets | Latency p50/p99 |

---

## Storage Design

**Time-series shard key**: `hash(metric_name + sorted_labels) % num_shards`

**Cassandra-style wide row**:
```
Partition: metric_id + day
Columns: timestamp → value
```

**Downsampling**:
- Raw: 15s resolution, 15 days
- 5-min aggregates: 90 days
- 1-hour aggregates: 1 year

**Cardinality control**: Reject or drop high-cardinality labels (e.g., `user_id` as tag) — explosion kills TSDB.

---

## Query Path

1. Parse PromQL / SQL-like query
2. Identify shards from metric name + label matchers
3. Parallel fetch from TSDB shards
4. Merge + aggregate in query service
5. Cache hot queries (Redis) — dashboard refreshes

```promql
histogram_quantile(0.99, sum(rate(http_duration_bucket[5m])) by (le, service))
```

---

## Alerting

```
Rule: p99_latency{service="payment"} > 500ms for 5m
Evaluator: Stream processor or periodic scanner on recent windows
State: OK → PENDING (1 breach) → FIRING → notify
Dedup: Group alerts by service; throttle repeat notifications
```

---

## APIs

| API | Purpose |
|-----|---------|
| `POST /v1/metrics` | Ingest batch (OTel protobuf or JSON) |
| `GET /v1/query` | Instant query at timestamp |
| `GET /v1/query_range` | Range query for graphs |
| `POST /v1/alerts/rules` | CRUD alert rules |

---

## Interview Discussion Points

1. **Push vs pull** — Pull: Prometheus scrape. Push: agents for short-lived jobs (Lambda)
2. **Cardinality** — Why `user_id` as label is dangerous; use logs for high-cardinality debug
3. **Histogram vs Summary** — Histogram: aggregatable p99 across instances. Summary: pre-computed quantiles, harder to aggregate
4. **Logs vs metrics vs traces** — Three pillars; this system is metrics layer — see [§11 Observability](../../01_TechGuide/11_Observability.md)
5. **Fail-open on ingest** — Drop samples under overload vs crash the monitored app

**Related**: [§11 Observability](../../01_TechGuide/11_Observability.md), [§23 SRE](../../01_TechGuide/23_SRE_Reliability_Engineering.md)
