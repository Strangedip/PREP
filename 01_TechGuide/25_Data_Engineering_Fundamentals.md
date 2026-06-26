# Section 25: Data Engineering Fundamentals (2026)

> **Level**: SR+ (batch vs stream overview) to LEAD (lakehouse architecture, CDC, data platform design)
> **Why This Matters**: Every backend engineer touches data pipelines — event streams, analytics, CDC into warehouses. System design interviews include "design a metrics pipeline" or "sync data across services." You don't need to be a data scientist, but you must speak the language.

---

## 25.1 Data Engineering vs Data Science vs ML Engineering

| Role | Focus |
|------|-------|
| **Data Engineer** | Pipelines, warehouses, ETL/ELT, data quality, streaming |
| **Data Scientist** | Models, statistics, experiments |
| **ML Engineer** | Deploy and scale ML models in production |
| **Backend Engineer** | Application data, APIs, often owns event pipelines |

---

## 25.2 Batch vs Stream Processing

| Mode | Latency | Examples | Tools |
|------|---------|----------|-------|
| **Batch** | Hours to daily | Reports, ML training sets, billing | Spark, Airflow, dbt |
| **Stream** | Seconds to minutes | Fraud detection, live dashboards, CDC | Kafka, Flink, Spark Streaming |
| **Micro-batch** | Minutes | Near-real-time analytics | Spark Structured Streaming |

```
Batch:     [Daily dump] → Spark job → Warehouse table
Stream:    [Events] → Kafka → Flink → Real-time dashboard / alert
```

---

## 25.3 ETL vs ELT

| Pattern | Flow | When |
|---------|------|------|
| **ETL** | Extract → Transform → Load into warehouse | Legacy, heavy transforms before storage |
| **ELT** | Extract → Load raw → Transform in warehouse | Modern — cheap storage, SQL transforms in Snowflake/BigQuery |

**2026 default**: ELT with dbt for transforms in the warehouse.

---

## 25.4 Core Tools Landscape

| Category | Tools |
|----------|-------|
| **Orchestration** | Apache Airflow, Dagster, Prefect |
| **Stream processing** | Apache Flink, Kafka Streams, Spark Structured Streaming |
| **Batch processing** | Apache Spark (PySpark, Scala) |
| **Warehouse** | Snowflake, BigQuery, Redshift, Databricks SQL |
| **Lake / Lakehouse** | Delta Lake, Apache Iceberg, Hudi on S3 |
| **Transform** | dbt (SQL-based transforms) |
| **CDC** | Debezium (Kafka Connect), Fivetran |
| **Ingestion** | Kafka, Kinesis, Pub/Sub |

---

## 25.5 Lambda vs Kappa Architecture

```
Lambda (batch + speed layers):
  Events → Kafka → [Speed: Flink real-time] + [Batch: Spark hourly] → Merge at query time

Kappa (stream-only):
  Events → Kafka → Flink → Materialized views (replay from log for backfill)
```

**Interview**: Kappa is simpler if you can replay the log. Lambda when batch accuracy differs from stream approximations.

---

## 25.6 Change Data Capture (CDC)

Capture database row changes as events — enables sync without polling.

```
PostgreSQL WAL → Debezium → Kafka topic → Consumer (warehouse, cache, search index)
```

**Use cases**:
- Sync read replicas and search indexes
- Event-driven microservices (Outbox + CDC)
- Analytics without impacting production DB

**Connects to**: [19_Event_Driven_Architecture.md](./19_Event_Driven_Architecture.md) Section 19.8

---

## 25.7 Data Warehouse vs Data Lake vs Lakehouse

| | Warehouse | Data Lake | Lakehouse |
|---|-----------|-----------|-----------|
| **Data** | Structured, schema-on-write | Raw files (Parquet, JSON) | Structured + unstructured |
| **Users** | Analysts, BI | Data scientists | Both |
| **ACID** | Yes | No (historically) | Yes (Delta/Iceberg) |
| **Example** | Snowflake | S3 + Parquet | Databricks Delta Lake |

---

## 25.8 Data Quality & Governance

| Concern | Practice |
|---------|----------|
| **Schema evolution** | Avro/Protobuf + Schema Registry |
| **Data contracts** | Producer guarantees schema; consumers validate |
| **Lineage** | Track data origin (OpenLineage, Marquez) |
| **PII** | Column-level encryption, masking in warehouse |
| **Freshness SLAs** | "Dashboard data < 15 min stale" |

---

## 25.9 Backend Engineer Integration Points

| Your System | Data Pipeline Touch |
|-------------|---------------------|
| Spring Boot app | Publish domain events to Kafka |
| Microservice | CDC from DB → analytics without dual writes |
| RAG system | Batch embed documents → vector DB (see 05_AI) |
| Metrics | Prometheus → Thanos → long-term storage |
| Audit log | Append-only event store → compliance queries |

---

## 25.10 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Batch vs stream? | Batch: scheduled large jobs. Stream: continuous processing of events. |
| 2 | ETL vs ELT? | ETL transforms before load; ELT loads raw then transforms in warehouse. |
| 3 | What is CDC? | Capture DB changes as events — Debezium reads transaction log. |
| 4 | Kafka in data eng? | Event bus for ingestion, stream processing, CDC delivery. |
| 5 | Spark use case? | Distributed batch and micro-batch processing on large datasets. |
| 6 | Flink vs Spark Streaming? | Flink: true streaming, lower latency. Spark: micro-batch, easier ops for batch teams. |
| 7 | dbt? | SQL-based transform layer in warehouse — versioned, tested models. |
| 8 | Lakehouse? | Lake storage + warehouse ACID (Delta Lake, Iceberg). |
| 9 | Lambda architecture? | Separate speed (stream) and batch layers, merge at query. |
| 10 | Data contract? | Agreement between producer and consumer on schema and SLAs. |

**Must-say keywords**: ETL/ELT, CDC, Debezium, lakehouse, dbt, batch vs stream, Kafka, schema evolution, data lineage, Kappa.
