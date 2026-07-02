# Vector Databases & Embeddings — Complete Guide

> **Goal**: Understand how vector databases work, how to choose one, and how to use pgvector (the most practical choice for Java developers).
> **Level**: Mid-Level through Lead

> **You are here**: SDE2 — AI / ML
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [03_RAG_Architecture.md](03_RAG_Architecture.md) | **Next**: [05_Spring_AI.md](05_Spring_AI.md)

---

## 1. What is a Vector Database?

A vector database is a specialized database designed to store, index, and query high-dimensional vectors (embeddings). Unlike traditional databases that match exact values or ranges, vector databases find the **most similar** vectors using distance metrics.

### 1.1 Vector DB vs. Traditional DB

| Feature | Traditional DB (PostgreSQL) | Vector DB (Pinecone, pgvector) |
|---------|---------------------------|-------------------------------|
| **Data type** | Rows with columns (int, varchar, date) | Vectors (arrays of floats) + metadata |
| **Query type** | Exact match, range, JOIN, GROUP BY | Nearest neighbor (top K most similar) |
| **Index type** | B-Tree, Hash, GIN, GiST | HNSW, IVFFlat, Annoy, ScaNN |
| **Query example** | `WHERE name = 'John'` | `ORDER BY embedding <=> query_vector LIMIT 5` |
| **Use case** | Structured data, transactions | Semantic search, recommendations, RAG |
| **Consistency** | ACID transactions | Eventually consistent (some), ACID (pgvector) |

### 1.2 How Vector Search Works

```
User query: "How do I configure SSL in Spring Boot?"
     │
     ▼
Embedding Model: text-embedding-3-small
     │
     ▼
Query Vector: [0.12, -0.45, 0.78, ..., -0.91]  (1536 dimensions)
     │
     ▼
Vector Database: Find top 5 most similar vectors
     │
     ▼
Results:
  1. "SSL/TLS Configuration Guide" (similarity: 0.92)
  2. "Spring Boot Security Setup"  (similarity: 0.87)
  3. "HTTPS Endpoint Configuration" (similarity: 0.85)
  4. "Certificate Management"      (similarity: 0.81)
  5. "Server Properties Reference"  (similarity: 0.79)
```

---

## 2. Vector Database Options — Comparison

### 2.1 Full Comparison Table

| Database | Type | Open Source | Managed Cloud | ACID | Filtering | Max Vectors | Spring AI Support | Best For |
|----------|------|-----------|---------------|------|-----------|-------------|-------------------|----------|
| **pgvector** | PostgreSQL extension | Yes | AWS RDS, Supabase, Neon | Yes (PostgreSQL ACID) | Full SQL | ~10M+ | Yes | Teams already using PostgreSQL |
| **Pinecone** | Cloud-native | No | Yes (only option) | No | Metadata | Billions | Yes | Managed, zero-ops, scale |
| **Weaviate** | Dedicated vector DB | Yes | Weaviate Cloud | No | GraphQL + filters | Billions | Yes | Multi-modal, hybrid search |
| **Milvus** | Dedicated vector DB | Yes | Zilliz Cloud | No | Expression-based | Billions | Yes | Massive scale, ML workloads |
| **Qdrant** | Dedicated vector DB | Yes | Qdrant Cloud | No | Rich filtering | Billions | Yes | Payload filtering, precision |
| **Chroma** | Embedded vector DB | Yes | No | No | Metadata | ~1M | Yes | Prototyping, local development |
| **Redis** (Vector) | Redis module | Yes (Source Available) | Redis Cloud | No | RedisJSON filters | ~10M | Yes | Teams already using Redis |
| **Elasticsearch** | Search engine + vector | Yes (Source Available) | Elastic Cloud | No | Full Lucene query | Billions | Yes | Existing Elasticsearch users |
| **Neo4j** (Vector) | Graph DB + vector | Yes | Neo4j Aura | Yes (Graph ACID) | Cypher queries | ~10M | Yes | Knowledge graph + vector |

### 2.2 Decision Guide

```
Do you already use PostgreSQL?
├── Yes → pgvector (simplest, no new infrastructure)
│   └── Is your dataset > 10M vectors?
│       └── Yes → Consider Pinecone or Milvus
├── No
│   ├── Do you want zero-ops managed service?
│   │   └── Yes → Pinecone
│   ├── Do you need hybrid search (vector + keyword)?
│   │   └── Yes → Weaviate or Elasticsearch
│   ├── Do you need massive scale (>100M vectors)?
│   │   └── Yes → Milvus (Zilliz Cloud)
│   ├── Do you need graph relationships + vector?
│   │   └── Yes → Neo4j
│   └── Prototyping / small project?
│       └── Chroma (embedded, no server needed)
```

---

## 3. pgvector — The Java Developer's Best Choice

### 3.1 Why pgvector?

- **No new infrastructure**: Add an extension to your existing PostgreSQL
- **ACID transactions**: Vector operations participate in transactions
- **SQL familiarity**: Use standard SQL with vector operators
- **Metadata filtering**: Combine vector search with WHERE clauses
- **Spring AI integration**: First-class support
- **Production-proven**: Used by Supabase, Neon, and many SaaS companies

### 3.2 Setup

```sql
-- Enable the extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Create a table with a vector column
CREATE TABLE documents (
    id          BIGSERIAL PRIMARY KEY,
    content     TEXT NOT NULL,
    embedding   vector(1536),    -- 1536 dimensions for OpenAI text-embedding-3-small
    metadata    JSONB DEFAULT '{}',
    source      VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create an HNSW index (recommended for most use cases)
CREATE INDEX ON documents USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);
-- m: Max connections per node (higher = better recall, more memory)
-- ef_construction: Build-time search depth (higher = better quality index, slower build)

-- Alternative: IVFFlat index (faster build, slightly less accurate)
CREATE INDEX ON documents USING ivfflat (embedding vector_cosine_ops)
    WITH (lists = 100);
-- lists: Number of clusters. Rule of thumb: sqrt(total_rows) for < 1M rows
-- For > 1M rows: total_rows / 1000
```

### 3.3 Index Types Explained

**HNSW (Hierarchical Navigable Small World):**
- Best overall performance (both build and query)
- Higher memory usage
- No training required
- Supports incremental inserts
- **Recommended for most production use cases**

**IVFFlat (Inverted File with Flat Compression):**
- Lower memory usage
- Requires training (build after data is loaded)
- Faster build time
- Slightly lower recall than HNSW
- Good for very large datasets where memory is constrained

**Comparison:**

| Aspect | HNSW | IVFFlat |
|--------|------|---------|
| Query Speed | Faster | Slightly slower |
| Recall (accuracy) | Higher (99%+) | Good (95%+) |
| Memory Usage | Higher | Lower |
| Build Time | Moderate | Faster |
| Incremental Insert | Supported | Needs re-training |
| Best For | Most use cases | Memory-constrained, bulk load |

### 3.4 Vector Operations in SQL

```sql
-- Cosine Distance (most common for text embeddings)
-- <=> operator: 1 - cosine_similarity (lower = more similar)
SELECT id, content, 1 - (embedding <=> '[0.12, -0.45, ...]'::vector) AS similarity
FROM documents
ORDER BY embedding <=> '[0.12, -0.45, ...]'::vector
LIMIT 5;

-- Euclidean Distance (L2)
-- <-> operator
SELECT id, content, embedding <-> '[0.12, -0.45, ...]'::vector AS distance
FROM documents
ORDER BY embedding <-> '[0.12, -0.45, ...]'::vector
LIMIT 5;

-- Inner Product (dot product)
-- <#> operator (negative inner product for ORDER BY ASC)
SELECT id, content, (embedding <#> '[0.12, -0.45, ...]'::vector) * -1 AS similarity
FROM documents
ORDER BY embedding <#> '[0.12, -0.45, ...]'::vector
LIMIT 5;

-- Combined: Vector search with metadata filtering
SELECT id, content, 1 - (embedding <=> $1::vector) AS similarity
FROM documents
WHERE source = 'api-reference'
  AND (metadata->>'version')::int >= 4
  AND created_at > NOW() - INTERVAL '30 days'
ORDER BY embedding <=> $1::vector
LIMIT 5;

-- Hybrid search: Combine vector similarity with full-text search
SELECT id, content,
    (1 - (embedding <=> $1::vector)) * 0.7 +
    ts_rank(to_tsvector('english', content), plainto_tsquery('english', $2)) * 0.3
    AS combined_score
FROM documents
WHERE to_tsvector('english', content) @@ plainto_tsquery('english', $2)
ORDER BY combined_score DESC
LIMIT 5;
```

### 3.5 Spring AI + pgvector Configuration

```xml
<!-- pom.xml dependencies -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>
```

```yaml
# application.yml
spring:
  ai:
    vectorstore:
      pgvector:
        index-type: HNSW
        distance-type: COSINE_DISTANCE
        dimensions: 1536
        schema-validation: true
        initialize-schema: true
        remove-existing-vector-store-table: false
        table-name: vector_store
    openai:
      api-key: ${OPENAI_API_KEY}
      embedding:
        options:
          model: text-embedding-3-small

  datasource:
    url: jdbc:postgresql://localhost:5432/myapp
    username: postgres
    password: ${DB_PASSWORD}
```

```java
@Configuration
public class VectorStoreConfig {

    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return new PgVectorStore(
            jdbcTemplate,
            embeddingModel,
            PgVectorStore.PgVectorStoreConfig.builder()
                .withSchemaName("public")
                .withTableName("vector_store")
                .withVectorDimension(1536)
                .withDistanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .withIndexType(PgVectorStore.PgIndexType.HNSW)
                .withHnswM(16)
                .withHnswEfConstruction(64)
                .build()
        );
    }
}
```

```java
@Service
public class DocumentService {

    private final VectorStore vectorStore;

    // Add documents
    public void addDocuments(List<Document> documents) {
        vectorStore.add(documents);
    }

    // Search with filtering
    public List<Document> search(String query, String source) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(5)
                .withSimilarityThreshold(0.7)
                .withFilterExpression(
                    new FilterExpressionBuilder()
                        .eq("source", source)
                        .build()
                )
        );
    }

    // Delete documents
    public void deleteBySource(String source) {
        vectorStore.delete(
            FilterExpressionBuilder.builder()
                .eq("source", source)
                .build()
        );
    }
}
```

---

## 4. Pinecone — The Cloud-Native Option

### 4.1 When to Choose Pinecone

- You want zero infrastructure management
- You need to scale to billions of vectors
- You want built-in high availability and replication
- Your team does not manage PostgreSQL infrastructure
- Budget is available for a managed service ($70+ per month)

### 4.2 Spring AI + Pinecone Configuration

```yaml
spring:
  ai:
    vectorstore:
      pinecone:
        api-key: ${PINECONE_API_KEY}
        environment: us-east-1-aws
        project-id: your-project-id
        index-name: my-index
        namespace: production
```

```java
// Usage is identical to pgvector — Spring AI abstracts the vector store
@Service
public class PineconeSearchService {

    private final VectorStore vectorStore; // Pinecone implementation injected

    public List<Document> search(String query) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(5)
                .withSimilarityThreshold(0.7)
        );
    }
}
```

---

## 5. Embedding Best Practices

### 5.1 Choosing the Right Embedding Model

| Scenario | Recommended Model | Why |
|----------|-------------------|-----|
| **General English text** | text-embedding-3-small (OpenAI) | Best price/performance |
| **Maximum quality** | text-embedding-3-large (OpenAI) | Highest quality, 3072 dim |
| **Self-hosted (no data leaves)** | BGE-large-en-v1.5 (BAAI) | Free, 1024 dim, high quality |
| **Long documents** | Nomic Embed 1.5 | 8192 token context |
| **Multi-lingual** | Cohere embed-multilingual-v3.0 | 100+ languages |
| **Code** | CodeBERT or StarEncoder | Trained on code |

### 5.2 Embedding Quality Tips

```java
@Service
public class EmbeddingBestPractices {

    private final EmbeddingModel embeddingModel;

    // TIP 1: Prefix queries vs. documents differently (for some models)
    // Models like E5 and Nomic use different prefixes for queries vs. documents
    public float[] embedQuery(String query) {
        return embeddingModel.embed("search_query: " + query);
    }

    public float[] embedDocument(String document) {
        return embeddingModel.embed("search_document: " + document);
    }

    // TIP 2: Batch embeddings for efficiency
    public List<float[]> batchEmbed(List<String> texts) {
        // Send in batches of 100 (API limits)
        List<float[]> allEmbeddings = new ArrayList<>();
        for (int i = 0; i < texts.size(); i += 100) {
            List<String> batch = texts.subList(i, Math.min(i + 100, texts.size()));
            EmbeddingResponse response = embeddingModel.embedForResponse(batch);
            allEmbeddings.addAll(response.getResults().stream()
                .map(Embedding::getOutput)
                .toList());
        }
        return allEmbeddings;
    }

    // TIP 3: Normalize embeddings for cosine similarity
    public float[] normalize(float[] vector) {
        double norm = 0;
        for (float v : vector) norm += v * v;
        norm = Math.sqrt(norm);
        float[] normalized = new float[vector.length];
        for (int i = 0; i < vector.length; i++) {
            normalized[i] = (float) (vector[i] / norm);
        }
        return normalized;
    }

    // TIP 4: Dimensionality reduction (for OpenAI text-embedding-3 models)
    // These models support Matryoshka representations — you can truncate dimensions
    // 1536 dims → 512 dims with ~2% quality loss, 3x less storage
    public float[] reduceDimensions(float[] fullVector, int targetDims) {
        float[] reduced = Arrays.copyOf(fullVector, targetDims);
        // Re-normalize after truncation
        return normalize(reduced);
    }
}
```

### 5.3 Embedding Storage Calculations

| Vectors | Dimensions | Size per Vector | Total Storage | RAM for Index |
|---------|-----------|----------------|---------------|---------------|
| 100K | 1536 | 6 KB | ~600 MB | ~1 GB |
| 1M | 1536 | 6 KB | ~6 GB | ~10 GB |
| 10M | 1536 | 6 KB | ~60 GB | ~100 GB |
| 100K | 768 | 3 KB | ~300 MB | ~500 MB |
| 1M | 768 | 3 KB | ~3 GB | ~5 GB |

**Formula**: Size = num_vectors × dimensions × 4 bytes (float32)

---

## 6. ANN (Approximate Nearest Neighbor) Algorithms

### 6.1 Why "Approximate"?

Exact nearest neighbor search is O(n × d) where n = number of vectors and d = dimensions. For 1M vectors with 1536 dimensions, that is 1.5 billion comparisons per query — too slow. ANN algorithms trade a small accuracy loss for massive speed gains.

### 6.2 HNSW (Hierarchical Navigable Small World) — The Standard

```
Layer 2:  [A] ──────────────────── [F]
           │                          │
Layer 1:  [A] ──── [C] ──── [D] ──── [F]
           │        │        │        │
Layer 0:  [A] ─ [B] ─ [C] ─ [D] ─ [E] ─ [F] ─ [G] ─ [H]
                                  ↑
                          Query lands here,
                        navigates through layers
```

**How it works:**
1. Build a multi-layer graph where each layer has fewer nodes
2. Start search at the top layer (fewest nodes, coarsest)
3. At each layer, greedily navigate to the nearest node
4. Drop down to the next layer and continue
5. At the bottom layer, explore neighbors for final results

**Parameters:**
- `M`: Maximum connections per node (16-64, higher = better recall, more memory)
- `ef_construction`: Search depth during build (64-512, higher = better index quality)
- `ef_search`: Search depth at query time (higher = better recall, slower)

### 6.3 IVF (Inverted File Index)

1. Cluster all vectors into N clusters (using k-means)
2. At query time, find the nearest cluster center(s)
3. Only search vectors within those clusters

**Parameters:**
- `nlist`: Number of clusters (sqrt(n) for < 1M vectors)
- `nprobe`: Number of clusters to search at query time (higher = better recall, slower)

### 6.4 Performance Benchmarks (Approximate)

For 1M vectors, 1536 dimensions:

| Algorithm | Query Time | Recall@10 | Memory | Build Time |
|-----------|-----------|-----------|--------|-----------|
| Exact (brute force) | ~100ms | 100% | Minimal | None |
| HNSW (M=16) | ~1ms | 99%+ | 2x data | ~10 min |
| IVFFlat (nlist=1000) | ~5ms | 95%+ | 1.1x data | ~5 min |
| IVF+PQ (compressed) | ~2ms | 90%+ | 0.1x data | ~15 min |

---

## 7. Advanced Vector Database Patterns

### 7.1 Multi-Tenancy

```sql
-- Option 1: Filter by tenant (simpler)
CREATE TABLE documents (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536),
    metadata JSONB
);

CREATE INDEX idx_tenant_vector ON documents USING hnsw (embedding vector_cosine_ops);
CREATE INDEX idx_tenant_id ON documents (tenant_id);

-- Query with tenant filter
SELECT * FROM documents
WHERE tenant_id = 'customer-123'
ORDER BY embedding <=> $1::vector
LIMIT 5;

-- Option 2: Partitioning by tenant (better isolation)
CREATE TABLE documents (
    id BIGSERIAL,
    tenant_id VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    embedding vector(1536)
) PARTITION BY LIST (tenant_id);

CREATE TABLE documents_customer_123 PARTITION OF documents
    FOR VALUES IN ('customer-123');

-- Each partition gets its own HNSW index
CREATE INDEX ON documents_customer_123 USING hnsw (embedding vector_cosine_ops);
```

### 7.2 Incremental Updates

```java
@Service
public class IncrementalIngestionService {

    private final VectorStore vectorStore;
    private final JdbcTemplate jdbcTemplate;

    // Track what has been ingested
    public void ingestNewDocuments() {
        // Find documents modified since last ingestion
        List<Document> newDocs = jdbcTemplate.query(
            """
            SELECT * FROM source_documents
            WHERE updated_at > (
                SELECT COALESCE(MAX(ingested_at), '1970-01-01')
                FROM ingestion_log
            )
            """,
            documentRowMapper
        );

        if (newDocs.isEmpty()) {
            log.info("No new documents to ingest");
            return;
        }

        // Process and store
        List<Document> chunks = chunkAndEnrich(newDocs);
        vectorStore.add(chunks);

        // Log the ingestion
        jdbcTemplate.update(
            "INSERT INTO ingestion_log (ingested_at, document_count) VALUES (NOW(), ?)",
            chunks.size()
        );

        log.info("Ingested {} new chunks from {} documents", chunks.size(), newDocs.size());
    }

    // Handle document updates (delete old vectors, add new)
    public void updateDocument(String documentId, String newContent) {
        // Delete old vectors for this document
        vectorStore.delete(
            FilterExpressionBuilder.builder()
                .eq("documentId", documentId)
                .build()
        );

        // Re-chunk and add
        List<Document> newChunks = chunk(newContent, documentId);
        vectorStore.add(newChunks);
    }
}
```

---

## 8. Interview Questions — Vector Databases

| Question | Key Points |
|----------|-----------|
| "What is a vector database?" | Stores embeddings, enables similarity search, uses ANN algorithms |
| "How does cosine similarity work?" | Measures angle between vectors, range [-1,1], 1 = identical meaning |
| "pgvector vs. Pinecone?" | pgvector: no new infra, ACID, SQL. Pinecone: managed, scales to billions |
| "What is HNSW?" | Multi-layer graph, navigable small world, best recall/speed trade-off |
| "How do you handle multi-tenancy?" | Metadata filtering or table partitioning per tenant |
| "How do you update documents in a vector DB?" | Delete old vectors → re-chunk → re-embed → insert new vectors |
| "What embedding dimensions should you use?" | 1536 (default), 512-768 for cost savings, 3072 for max quality |
| "How does hybrid search work?" | Combine vector similarity scores with BM25 keyword scores using RRF |

---

**Next**: [05_Spring_AI.md](05_Spring_AI.md) — The Java framework for building AI-powered applications.

