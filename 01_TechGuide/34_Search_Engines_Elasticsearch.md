# Section 34: Search Engines & Elasticsearch

> **Level**: SR+ (indexing, queries) to LEAD (cluster design, relevance tuning)
> **Use Cases**: Full-text search, log analytics (ELK), autocomplete, e-commerce catalog search

> **You are here**: Senior SDE — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [33_Git_Version_Control_Workflow.md](33_Git_Version_Control_Workflow.md) | **Next**: [35_SQL_Fundamentals.md](35_SQL_Fundamentals.md)

---

## 34.1 How Search Engines Work

```
Documents → Analyze (tokenize, stem, lowercase) → Inverted Index → Query → Ranked Results
```

### Inverted Index
```
Term "java"     → [doc1, doc5, doc42]
Term "spring"   → [doc1, doc3, doc5]
Query "java spring" → intersect posting lists → score and rank
```

**vs B-Tree (SQL)**: B-Tree for exact match; inverted index for **full-text** and **fuzzy** search.

---

## 34.2 Elasticsearch Architecture

| Concept | Description |
|---------|-------------|
| **Cluster** | Collection of nodes |
| **Node** | Single ES instance |
| **Index** | Like a database table |
| **Shard** | Horizontal partition of index |
| **Replica** | Copy of shard for HA and read scaling |
| **Document** | JSON record in index |

```
Index "products" (5 shards, 1 replica)
  → 5 primary shards + 5 replica shards = 10 shard copies across nodes
```

**Shard sizing**: 10-50 GB per shard target; too many small shards hurts performance.

---

## 34.3 Analysis Pipeline

```
"Spring Boot 3.2!" 
  → Character filters (remove HTML)
  → Tokenizer (split on whitespace/punctuation)
  → Token filters (lowercase, stem, stop words)
  → ["spring", "boot", "3.2"]
```

### Common Analyzers
| Analyzer | Behavior |
|----------|----------|
| **standard** | Lowercase + split on punctuation |
| **english** | + stemming ("running" → "run") + stop words |
| **keyword** | Entire field as single token — exact match |
| **ngram** | Autocomplete — "spr" matches "spring" |

```json
{
  "settings": {
    "analysis": {
      "analyzer": {
        "autocomplete": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "edge_ngram_filter"]
        }
      }
    }
  }
}
```

---

## 34.4 Query Types

### Match Query (full-text)
```json
{ "query": { "match": { "title": "spring boot tutorial" } } }
```

### Term Query (exact — not analyzed)
```json
{ "query": { "term": { "status": "ACTIVE" } } }
```

### Bool Query (combine)
```json
{
  "query": {
    "bool": {
      "must": [{ "match": { "title": "laptop" } }],
      "filter": [{ "term": { "category": "electronics" } }],
      "must_not": [{ "term": { "status": "deleted" } }]
    }
  }
}
```

**Filter vs Query**: Filters don't affect score — cacheable, faster.

---

## 34.5 Relevance Scoring (BM25)

Default algorithm — considers:
- Term frequency in document
- Inverse document frequency (rare terms score higher)
- Field length normalization

**Boost fields**: Title matches score higher than description.
```json
{ "multi_match": {
    "query": "wireless keyboard",
    "fields": ["title^3", "description", "tags^2"]
}}
```

---

## 34.6 Aggregations (Analytics)

```json
{
  "aggs": {
    "by_category": {
      "terms": { "field": "category" },
      "aggs": {
        "avg_price": { "avg": { "field": "price" } }
      }
    }
  }
}
```

Used for faceted search — "show products by category with counts."

---

## 34.7 ELK Stack (Logging)

| Component | Role |
|-----------|------|
| **Elasticsearch** | Store and search logs |
| **Logstash** | Collect, transform, ship logs |
| **Kibana** | Visualize dashboards |
| **Beats** | Lightweight shippers (Filebeat) |

**Alternative 2026**: OpenSearch (AWS fork), Grafana Loki (lighter, label-based).

---

## 34.8 Elasticsearch vs PostgreSQL Full-Text

| | PostgreSQL `tsvector` | Elasticsearch |
|---|----------------------|---------------|
| Scale | Millions of docs | Billions of docs |
| Relevance tuning | Basic | Advanced (BM25, custom scoring) |
| Ops complexity | Low (same DB) | High (separate cluster) |
| Faceted search | Manual | Native aggregations |
| **Choose PG** | Small catalog, simple search | |
| **Choose ES** | Large scale, complex relevance, analytics | |

---

## 34.9 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Inverted index? | Term → list of documents containing term — fast full-text lookup. |
| 2 | Shard vs replica? | Shard: data partition. Replica: copy for HA and read scaling. |
| 3 | Analyzer vs tokenizer? | Analyzer = char filters + tokenizer + token filters full pipeline. |
| 4 | Match vs term query? | Match: analyzed full-text. Term: exact value, not analyzed. |
| 5 | BM25? | Default relevance scoring — TF-IDF variant with length normalization. |
| 6 | Filter vs query context? | Filter: yes/no, cacheable. Query: affects relevance score. |
| 7 | Too many shards problem? | Each shard = Lucene index overhead — cluster state explosion. |
| 8 | Near real-time? | Document visible ~1s after index (refresh interval). |
| 9 | ELK vs Loki? | ELK: full-text log search. Loki: label-based, cheaper, Grafana native. |
| 10 | ES vs Solr? | Both Lucene-based. ES: distributed native, REST API. Solr: mature, XML config. |

**Must-say keywords**: inverted index, shard, replica, analyzer, BM25, aggregation, near real-time, bool query, ngram, ELK.

---

## §34.10 Production & Interview Depth — Catalog Search for Indian E-Commerce

Myntra, Amazon India, and B2B marketplaces run **product search** as a tier-0 path: Hindi/English mixed queries, aggressive faceting (size, delivery SLA), and **near real-time** inventory sync from Spring Boot catalog services. Interviewers want **ES + PostgreSQL division of labor**, not "we'd use Elasticsearch for everything."

### PostgreSQL vs Elasticsearch — Production Split

| Concern | PostgreSQL ([26_PostgreSQL_Relational_DB_Deep_Dive.md](./26_PostgreSQL_Relational_DB_Deep_Dive.md)) | Elasticsearch / OpenSearch |
|---------|------------------------------------------------------------------------------------------------------|----------------------------|
| **Source of truth** | SKU, price, stock, seller ID | Search-optimized denormalized doc |
| **Transactional updates** | ACID order reservation | Async index via Kafka/outbox |
| **Faceted browse** | Possible but painful at scale | Native aggregations |
| **Hindi transliteration** | Limited | Custom analyzer + phonetic plugins |
| **Ops cost on AWS India** | RDS already paid for | OpenSearch domain $$$ — justify with QPS |

Pattern from [19_Event_Driven_Architecture.md](./19_Event_Driven_Architecture.md): **transactional outbox** → indexer consumer → bulk API.

### Spring Boot 3 + OpenSearch Client (Indexing Pattern)

```java
@Service
@RequiredArgsConstructor
public class ProductSearchIndexer {
    private final OpenSearchClient client;

    public void indexProduct(ProductDocument doc) throws IOException {
        client.index(i -> i
            .index("products-v2")
            .id(doc.skuId())
            .document(doc)
            .refresh(Refresh.False));  // rely on refresh_interval, not per-doc refresh
    }

    public SearchResponse<ProductDocument> search(String q, String pincode) {
        return client.search(s -> s.index("products-v2")
            .query(qb -> qb.bool(b -> b
                .must(m -> m.multiMatch(mm -> mm
                    .query(q).fields("title^3", "brand^2", "description")))
                .filter(f -> f.term(t -> t.field("serviceable_pincodes").value(pincode)))
                .filter(f -> f.term(t -> t.field("in_stock").value(true))))),
            ProductDocument.class);
    }
}
```

### Relevance Tuning Trade-offs

| Technique | Upside | Downside |
|-----------|--------|----------|
| **Field boosts** (`title^3`) | Simple win for brand queries | Over-boosts spammy seller titles |
| **Function score** (popularity, margin) | Business KPI alignment | "Search feels biased" — legal/product review |
| **Synonyms** ("mobile" = "phone") | Bharat query coverage | Stale synonym files without owner |
| **Edge n-gram autocomplete** | Fast typeahead | Index size bloat — shard planning critical |

### Interview Story

*"Catalog lived in Postgres; we denormalized to OpenSearch with 5 primary shards, `refresh_interval: 5s`, and **search-as-you-type** only on title. Stock updates used debounced bulk reindex; PDP reads still hit Postgres for price integrity."*

**Cross-links**: SQL reporting on sales → [35_SQL_Fundamentals.md](./35_SQL_Fundamentals.md); cache layer for hot SKUs → [28_Redis_Distributed_Caching.md](./28_Redis_Distributed_Caching.md).

**Must-say keywords**: outbox pattern, bulk API, filter context, refresh interval, analyzer for Indian languages, search vs analytics cluster split.
