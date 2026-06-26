# Section 34: Search Engines & Elasticsearch

> **Level**: SR+ (indexing, queries) to LEAD (cluster design, relevance tuning)
> **Use Cases**: Full-text search, log analytics (ELK), autocomplete, e-commerce catalog search

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
