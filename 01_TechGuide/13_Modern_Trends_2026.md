# Section 13: Modern Trends (2026 Focus)

> **You are here**: Tech Lead — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [12_Security_OWASP_Cloud.md](12_Security_OWASP_Cloud.md) | **Next**: [14_Leadership_Behavioral_SystemDesign.md](14_Leadership_Behavioral_SystemDesign.md)

---

## 13.1 AI Integration: RAG (Retrieval-Augmented Generation) with Spring Boot & Vector DBs

---

### The "Why" & The Problem

Large Language Models (LLMs) like GPT-4, Claude, and Gemini have transformed software development. But raw LLMs have critical limitations:
1. **Knowledge cutoff**: They only know what was in their training data. They don't know about your company's internal documents, your product catalog, or your customer support tickets.
2. **Hallucination**: When asked about something they don't know, LLMs confidently generate plausible-sounding but incorrect information.
3. **No real-time data**: They can't tell you today's stock price, your current inventory levels, or the status of a customer's order.

**RAG (Retrieval-Augmented Generation)** solves this by combining the reasoning ability of LLMs with the factual accuracy of your own data. Instead of asking the LLM to remember everything, you **retrieve** relevant documents from your knowledge base and **inject** them into the LLM's prompt as context. The LLM then generates a response grounded in your actual data.

A company pays you to know this because:
- **Every enterprise** is building AI-powered features: intelligent search, customer support chatbots, document Q&A, code assistants.
- **RAG is the standard architecture** for enterprise AI applications in 2026. It avoids the cost and complexity of fine-tuning models while providing accurate, up-to-date responses.
- **Lead engineers** are expected to architect these systems — choosing the right vector database, designing the ingestion pipeline, implementing the retrieval strategy, and integrating with LLM APIs.

---

### Interviewer Expectations

- **RAG Architecture**: Explain the full pipeline — document ingestion, chunking, embedding, vector storage, retrieval, augmented prompt construction, LLM generation.
- **Vector Databases**: Know what vector embeddings are, why you need a specialized database for them, and the options (pgvector, Pinecone, Weaviate, Milvus, Qdrant, ChromaDB).
- **Chunking strategies**: Why you can't feed an entire 500-page document to an LLM. How to split documents into meaningful chunks.
- **Spring AI**: Know that Spring AI is the official Spring framework for AI integration (production-ready in 2025+). Demonstrate integration with Open05_AI/Anthropic APIs and vector stores.
- **Keywords**: "Embedding model", "vector similarity search", "cosine similarity", "semantic search", "chunking strategy", "context window", "prompt engineering", "retrieval pipeline", "Spring AI", "pgvector".

---

### The Deep Dive & Solution

#### RAG Architecture — The Complete Pipeline

```
┌────────────────────────────────────────────────────────────────────────┐
│                    RAG PIPELINE (Two Phases)                           │
│                                                                        │
│  PHASE 1: INGESTION (Offline / Batch)                                  │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐   ┌───────────────────┐  │
│  │ Documents  │──>│ Chunking  │──>│ Embedding │──>│  Vector Database  │  │
│  │ (PDF, MD,  │   │ (split    │   │ (convert  │   │  (store vectors   │  │
│  │  HTML, DB) │   │  into     │   │  text to  │   │   + metadata)     │  │
│  └──────────┘   │  segments) │   │  vectors) │   └───────────────────┘  │
│                  └──────────┘   └──────────┘                           │
│                                                                        │
│  PHASE 2: RETRIEVAL + GENERATION (Online / Per Query)                  │
│                                                                        │
│  ┌──────────┐   ┌──────────┐   ┌──────────────────┐  ┌────────────┐  │
│  │User Query │──>│ Embed     │──>│ Vector Similarity │──>│  Top-K     │  │
│  │           │   │ Query     │   │ Search            │  │  Results   │  │
│  └──────────┘   └──────────┘   └──────────────────┘  └─────┬──────┘  │
│                                                              │         │
│  ┌──────────────────────────────────────────────────────────┘         │
│  │                                                                     │
│  ▼                                                                     │
│  ┌────────────────────────────────────────────────────────────────┐    │
│  │  AUGMENTED PROMPT                                              │    │
│  │  ┌─────────────────────────────────────────────────────────┐  │    │
│  │  │ System: You are a helpful assistant. Answer based on    │  │    │
│  │  │ the provided context. If the context doesn't contain    │  │    │
│  │  │ the answer, say "I don't know."                         │  │    │
│  │  │                                                         │  │    │
│  │  │ Context:                                                │  │    │
│  │  │ [Retrieved Document 1: "Our refund policy allows..."]   │  │    │
│  │  │ [Retrieved Document 2: "Refunds are processed in..."]   │  │    │
│  │  │ [Retrieved Document 3: "For orders over $500..."]       │  │    │
│  │  │                                                         │  │    │
│  │  │ User Question: "What is your refund policy for orders   │  │    │
│  │  │ over $500?"                                             │  │    │
│  │  └─────────────────────────────────────────────────────────┘  │    │
│  └───────────────────────────┬────────────────────────────────────┘    │
│                              │                                         │
│                              ▼                                         │
│                        ┌──────────┐                                    │
│                        │   LLM     │                                    │
│                        │ (GPT-4,  │                                    │
│                        │  Claude)  │                                    │
│                        └────┬─────┘                                    │
│                             │                                          │
│                             ▼                                          │
│                    ┌──────────────────┐                                │
│                    │ Grounded Response │                                │
│                    │ (based on YOUR    │                                │
│                    │  actual data)     │                                │
│                    └──────────────────┘                                │
└────────────────────────────────────────────────────────────────────────┘
```

#### Vector Embeddings — What They Are

A **vector embedding** is a numerical representation (array of floating-point numbers) of text that captures its **semantic meaning**. Similar texts have similar embeddings (close together in vector space). Dissimilar texts have different embeddings (far apart).

```
Text: "How do I return a product?"
Embedding: [0.023, -0.412, 0.891, 0.034, ..., -0.156]  (1536 dimensions for OpenAI ada-002)

Text: "What is your refund policy?"
Embedding: [0.019, -0.398, 0.887, 0.041, ..., -0.149]  ← Very similar! Close in vector space.

Text: "What's the weather today?"
Embedding: [0.812, 0.234, -0.567, 0.723, ..., 0.445]  ← Very different. Far in vector space.
```

**Similarity measurement**: **Cosine similarity** is the standard metric. It measures the angle between two vectors (ignoring magnitude). A cosine similarity of 1.0 means identical meaning, 0.0 means no relation, -1.0 means opposite meaning.

```
cosine_similarity(A, B) = (A · B) / (||A|| × ||B||)
```

#### Chunking Strategies

LLMs have a **context window** limit (e.g., 128K tokens for GPT-4, 200K for Claude). You cannot feed an entire 500-page manual into one prompt. You must split documents into chunks that are:
1. **Small enough** to fit multiple chunks in the context window.
2. **Large enough** to contain meaningful, self-contained information.
3. **Semantically coherent** — a chunk should not end mid-sentence or mid-paragraph.

**Common chunking strategies**:

| Strategy | How it works | Best for |
|----------|-------------|----------|
| **Fixed-size** | Split every N characters/tokens with overlap | Simple, works for most text |
| **Sentence-based** | Split at sentence boundaries | Conversational text, Q&A |
| **Paragraph-based** | Split at paragraph boundaries | Structured documents |
| **Semantic** | Use embedding similarity to detect topic boundaries | Research papers, long-form content |
| **Recursive** | Try splitting by paragraph, then by sentence, then by character | Spring AI default — robust for mixed content |

**Overlap**: Chunks should overlap (e.g., the last 100 tokens of chunk N are the first 100 tokens of chunk N+1). This prevents information at chunk boundaries from being lost.

```java
// Spring AI chunking configuration
@Configuration
public class DocumentIngestionConfig {
    
    @Bean
    public TokenTextSplitter textSplitter() {
        return new TokenTextSplitter(
            800,   // defaultChunkSize: target tokens per chunk
            350,   // minChunkSizeChars: minimum chunk size
            200,   // minChunkLengthToEmbed: skip very small chunks
            100,   // maxNumChunks: max chunks per document
            true   // keepSeparator: preserve newlines and formatting
        );
    }
}
```

#### Full RAG Implementation with Spring AI

**Dependencies** (pom.xml):
```xml
<dependencies>
    <!-- Spring AI core -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Vector store — pgvector (PostgreSQL extension) -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
    </dependency>
    
    <!-- Document readers -->
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-pdf-document-reader</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-tika-document-reader</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**Application configuration**:
```yaml
# application.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o
          temperature: 0.3       # Lower = more factual, less creative
          max-tokens: 2000
      embedding:
        options:
          model: text-embedding-3-small  # Embedding model (1536 dimensions)
    vectorstore:
      pgvector:
        index-type: HNSW       # Hierarchical Navigable Small World — fast ANN search
        distance-type: COSINE_DISTANCE
        dimensions: 1536        # Must match embedding model dimensions
        
  datasource:
    url: jdbc:postgresql://localhost:5432/ragdb
    username: raguser
    password: ${DB_PASSWORD}
```

**Phase 1: Document Ingestion Pipeline**:
```java
@Service
public class DocumentIngestionService {
    
    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter;
    
    public DocumentIngestionService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.textSplitter = new TokenTextSplitter(800, 350, 200, 100, true);
    }
    
    /**
     * Ingest a PDF document into the vector store.
     * Steps: Read → Split into chunks → Generate embeddings → Store in pgvector
     */
    public void ingestPdf(Resource pdfResource, Map<String, Object> metadata) {
        // Step 1: Read the PDF
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(pdfResource);
        List<Document> rawDocuments = pdfReader.get();
        
        // Step 2: Add metadata to each document
        rawDocuments.forEach(doc -> {
            doc.getMetadata().putAll(metadata);
            doc.getMetadata().put("source", pdfResource.getFilename());
            doc.getMetadata().put("ingested_at", Instant.now().toString());
        });
        
        // Step 3: Split into chunks
        List<Document> chunks = textSplitter.apply(rawDocuments);
        
        log.info("Ingesting {} chunks from document: {}", chunks.size(), 
                 pdfResource.getFilename());
        
        // Step 4: Generate embeddings and store in vector database
        // Spring AI does this automatically when you call vectorStore.add()
        // Internally: for each chunk → call OpenAI embedding API → get vector → INSERT into pgvector
        vectorStore.add(chunks);
        
        log.info("Successfully ingested {} chunks", chunks.size());
    }
    
    /**
     * Ingest from a database (e.g., FAQ table, knowledge base articles)
     */
    public void ingestFromDatabase(List<KnowledgeArticle> articles) {
        List<Document> documents = articles.stream()
            .map(article -> new Document(
                article.getContent(),
                Map.of(
                    "article_id", article.getId(),
                    "title", article.getTitle(),
                    "category", article.getCategory(),
                    "last_updated", article.getUpdatedAt().toString()
                )
            ))
            .collect(Collectors.toList());
        
        List<Document> chunks = textSplitter.apply(documents);
        vectorStore.add(chunks);
    }
}
```

**Phase 2: Retrieval and Generation**:
```java
@Service
public class RagChatService {
    
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    
    public RagChatService(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        
        // Configure the ChatClient with a system prompt and RAG advisor
        this.chatClient = chatClientBuilder
            .defaultSystem("""
                You are a helpful customer support assistant for Acme Corp.
                Answer questions based ONLY on the provided context documents.
                If the context does not contain enough information to answer the question,
                say "I don't have enough information to answer that. Please contact support."
                Always cite which document or section your answer is based on.
                Be concise and professional.
                """)
            .defaultAdvisors(
                new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults()
                    .withTopK(5)                      // Retrieve top 5 most relevant chunks
                    .withSimilarityThreshold(0.7))    // Only include chunks with >= 70% similarity
            )
            .build();
    }
    
    /**
     * Answer a user question using RAG
     */
    public String chat(String userQuestion) {
        // Spring AI's QuestionAnswerAdvisor automatically:
        // 1. Embeds the user question
        // 2. Searches the vector store for similar documents
        // 3. Injects the retrieved documents into the prompt
        // 4. Sends the augmented prompt to the LLM
        // 5. Returns the LLM's response
        
        return chatClient.prompt()
            .user(userQuestion)
            .call()
            .content();
    }
    
    /**
     * Chat with metadata filtering (e.g., only search within a specific category)
     */
    public String chatWithFilter(String userQuestion, String category) {
        SearchRequest searchRequest = SearchRequest.defaults()
            .withTopK(5)
            .withSimilarityThreshold(0.7)
            .withFilterExpression(
                new FilterExpressionBuilder()
                    .eq("category", category)
                    .build()
            );
        
        // Manual retrieval + generation for more control
        List<Document> relevantDocs = vectorStore.similaritySearch(searchRequest
            .withQuery(userQuestion));
        
        String context = relevantDocs.stream()
            .map(Document::getContent)
            .collect(Collectors.joining("\n\n---\n\n"));
        
        String augmentedPrompt = String.format("""
            Context documents:
            %s
            
            Based on the above context, please answer the following question:
            %s
            """, context, userQuestion);
        
        return chatClient.prompt()
            .user(augmentedPrompt)
            .call()
            .content();
    }
}
```

**REST API Layer**:
```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {
    
    @Autowired private RagChatService ragChatService;
    @Autowired private DocumentIngestionService ingestionService;
    
    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest request) {
        String answer = ragChatService.chat(request.getQuestion());
        return ResponseEntity.ok(new ChatResponse(answer));
    }
    
    @PostMapping("/ingest")
    public ResponseEntity<String> ingestDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "category", defaultValue = "general") String category) {
        
        ingestionService.ingestPdf(
            file.getResource(),
            Map.of("category", category, "filename", file.getOriginalFilename())
        );
        
        return ResponseEntity.ok("Document ingested successfully");
    }
}
```

**pgvector — PostgreSQL extension for vector storage**:

pgvector turns PostgreSQL into a vector database. Since most companies already run PostgreSQL, this avoids introducing a new database system.

```sql
-- Enable the pgvector extension
CREATE EXTENSION IF NOT EXISTS vector;

-- Spring AI auto-creates this table, but here's what it looks like:
CREATE TABLE vector_store (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    content TEXT,                              -- The text chunk
    metadata JSONB,                            -- Document metadata (title, source, category)
    embedding vector(1536)                     -- The vector embedding (1536 dimensions)
);

-- HNSW index for fast approximate nearest neighbor (ANN) search
CREATE INDEX ON vector_store USING hnsw (embedding vector_cosine_ops)
WITH (m = 16, ef_construction = 200);
-- m: max connections per node (higher = more accurate but slower build)
-- ef_construction: dynamic candidate list size during index build

-- Example similarity search query (this is what Spring AI generates):
SELECT id, content, metadata, 
       1 - (embedding <=> '[0.023, -0.412, ...]'::vector) AS similarity
FROM vector_store
WHERE metadata->>'category' = 'refund-policy'
ORDER BY embedding <=> '[0.023, -0.412, ...]'::vector  -- <=> is cosine distance operator
LIMIT 5;
```

**Vector database comparison**:

| Database | Type | Best for | Key feature |
|----------|------|----------|-------------|
| **pgvector** | PostgreSQL extension | Teams already on PostgreSQL, moderate scale | No new infrastructure, ACID transactions |
| **Pinecone** | Managed SaaS | Serverless, zero ops | Fully managed, auto-scaling |
| **Weaviate** | Open-source | Built-in ML models, multi-modal | Can embed text/images at query time |
| **Milvus** | Open-source | Billion-scale vectors, high performance | GPU acceleration, distributed |
| **Qdrant** | Open-source | Rust-based, high performance | Rich filtering, payload indexing |
| **ChromaDB** | Open-source | Local development, prototyping | Simplest API, runs embedded |

#### Advanced RAG Techniques

**1. Hybrid Search (Vector + Keyword)**: Combine semantic search (vectors) with traditional keyword search (BM25/full-text search). This catches cases where semantic search misses exact terms.

```java
// Hybrid search: combine vector similarity with full-text search
public List<Document> hybridSearch(String query, int topK) {
    // Semantic search
    List<Document> semanticResults = vectorStore.similaritySearch(
        SearchRequest.query(query).withTopK(topK));
    
    // Keyword search (PostgreSQL full-text search)
    List<Document> keywordResults = jdbcTemplate.query(
        "SELECT * FROM vector_store WHERE to_tsvector('english', content) @@ plainto_tsquery(?)",
        new Object[]{query},
        documentRowMapper);
    
    // Reciprocal Rank Fusion to merge results
    return reciprocalRankFusion(semanticResults, keywordResults, topK);
}

private List<Document> reciprocalRankFusion(
        List<Document> list1, List<Document> list2, int topK) {
    Map<String, Double> scores = new HashMap<>();
    int k = 60; // Constant to prevent high ranks from dominating
    
    for (int i = 0; i < list1.size(); i++) {
        scores.merge(list1.get(i).getId(), 1.0 / (k + i + 1), Double::sum);
    }
    for (int i = 0; i < list2.size(); i++) {
        scores.merge(list2.get(i).getId(), 1.0 / (k + i + 1), Double::sum);
    }
    
    return scores.entrySet().stream()
        .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
        .limit(topK)
        .map(e -> findDocById(e.getKey()))
        .collect(Collectors.toList());
}
```

**2. Re-ranking**: After initial retrieval, use a cross-encoder model to re-rank results for higher precision. The initial vector search is fast but approximate. The re-ranker is slower but more accurate.

**3. Query Expansion**: Rephrase the user's question in multiple ways and search for each variant. This catches relevant documents that match different phrasings.

**4. Metadata Filtering**: Filter by metadata before vector search. If the user asks about "refund policy for premium users," filter by `tier=premium` first, then do vector search within those documents.

---

## 13.2 Deployment: Kubernetes Basics for Leads

---

### The "Why" & The Problem

As a Lead Software Engineer, you will not typically be a Kubernetes operator (that's the platform/DevOps team's job). But you WILL be responsible for:
- **Writing Kubernetes manifests** (Deployments, Services, ConfigMaps) for your team's services.
- **Designing for Kubernetes**: Health checks, graceful shutdown, 12-factor app compliance, resource limits.
- **Troubleshooting**: When your service is not starting, when pods are crashing, when traffic is not routing correctly.
- **Architectural decisions**: Rolling updates vs. blue-green, scaling policies, inter-service communication within the cluster.

A company pays you to know this because Kubernetes is the **de facto standard** for container orchestration in 2026. Every major company runs their microservices on Kubernetes (whether self-managed, EKS, GKE, or AKS). A Lead who cannot reason about Kubernetes is a Lead who cannot reason about production.

---

### Interviewer Expectations

- **Core concepts**: Pods, Deployments, Services, Ingress, ConfigMaps, Secrets, Namespaces. Know what each does and why.
- **Pod lifecycle**: Readiness probes, liveness probes, startup probes. Why you need all three.
- **Deployments**: Rolling updates, rollback, scaling. `maxSurge` and `maxUnavailable`.
- **Services**: ClusterIP, NodePort, LoadBalancer. How service discovery works (DNS-based).
- **Ingress**: How external traffic reaches your services. Ingress controllers (nginx, Traefik, Istio).
- **Keywords**: "Desired state", "reconciliation loop", "rolling update", "readiness probe", "liveness probe", "pod disruption budget", "resource requests and limits", "horizontal pod autoscaler (HPA)", "service mesh", "sidecar pattern".

---

### The Deep Dive & Solution

#### Core Kubernetes Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                     KUBERNETES CLUSTER                            │
│                                                                    │
│  ┌────────────────────┐                                          │
│  │   Control Plane      │                                          │
│  │  ┌──────────────┐   │         ┌─────────────────────────────┐ │
│  │  │ API Server    │   │         │       Worker Node 1          │ │
│  │  ├──────────────┤   │         │  ┌───────────────────────┐  │ │
│  │  │ etcd (state)  │   │         │  │  Pod: order-svc-abc   │  │ │
│  │  ├──────────────┤   │  watch  │  │  ┌─────────────────┐  │  │ │
│  │  │ Scheduler     │───┤────────│  │  │ Container:       │  │  │ │
│  │  ├──────────────┤   │         │  │  │ order-service    │  │  │ │
│  │  │ Controller    │   │         │  │  │ :latest          │  │  │ │
│  │  │ Manager       │   │         │  │  └─────────────────┘  │  │ │
│  │  └──────────────┘   │         │  └───────────────────────┘  │ │
│  └────────────────────┘         │  ┌───────────────────────┐  │ │
│                                   │  │  Pod: user-svc-xyz    │  │ │
│                                   │  └───────────────────────┘  │ │
│                                   │  ┌──────────┐               │ │
│                                   │  │ kubelet   │ ← Agent on   │ │
│                                   │  │ kube-proxy│    each node  │ │
│                                   │  └──────────┘               │ │
│                                   └─────────────────────────────┘ │
│                                                                    │
│                                   ┌─────────────────────────────┐ │
│                                   │       Worker Node 2          │ │
│                                   │  ┌───────────────────────┐  │ │
│                                   │  │  Pod: order-svc-def   │  │ │
│                                   │  └───────────────────────┘  │ │
│                                   │  ┌───────────────────────┐  │ │
│                                   │  │  Pod: payment-svc-ghi │  │ │
│                                   │  └───────────────────────┘  │ │
│                                   └─────────────────────────────┘ │
└──────────────────────────────────────────────────────────────────┘
```

**How Kubernetes works — the declarative model**:
You don't tell Kubernetes "start 3 instances of my service." You tell it "I want 3 instances of my service to be running" (desired state). Kubernetes continuously compares the current state with the desired state and takes corrective action (reconciliation loop). If a pod crashes, Kubernetes automatically starts a new one. If a node dies, pods are rescheduled to other nodes.

#### Pods — The Smallest Deployable Unit

A Pod is one or more containers that share:
- **Network namespace**: All containers in a pod share the same IP address and port space. They communicate via `localhost`.
- **Storage volumes**: Containers in a pod can share mounted volumes.
- **Lifecycle**: All containers in a pod start and stop together.

**In practice**: Most pods contain a single application container. Multi-container pods are used for the **sidecar pattern** (e.g., a logging sidecar, an Envoy proxy sidecar for service mesh).

**You should never create bare pods directly**. Always use a Deployment (or StatefulSet, DaemonSet) which manages pods for you with replication, self-healing, and rolling updates.

#### Deployments — Managing Pod Replicas

A Deployment defines:
- How many replicas (pods) to run.
- The pod template (container image, environment variables, resource limits).
- The update strategy (rolling update parameters).

```yaml
# deployment.yaml — Complete production-ready example
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
  namespace: production
  labels:
    app: order-service
    version: v2.3.1
spec:
  replicas: 3                        # Run 3 instances
  
  selector:
    matchLabels:
      app: order-service             # This Deployment manages pods with this label
  
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1                    # During update, allow 1 extra pod (4 total)
      maxUnavailable: 0              # During update, never have fewer than 3 healthy pods
                                      # This ensures zero-downtime deployments
  
  template:                          # Pod template
    metadata:
      labels:
        app: order-service
        version: v2.3.1
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    
    spec:
      terminationGracePeriodSeconds: 60  # Give the app 60s to finish requests on shutdown
      
      containers:
        - name: order-service
          image: myregistry.com/order-service:v2.3.1  # Never use :latest in production
          
          ports:
            - containerPort: 8080
              name: http
            - containerPort: 8081
              name: management     # Separate port for health checks
          
          # Resource management — CRITICAL for production
          resources:
            requests:               # Minimum guaranteed resources (used for scheduling)
              cpu: "500m"           # 0.5 CPU core
              memory: "512Mi"       # 512 MiB RAM
            limits:                 # Maximum allowed resources (pod killed if exceeded)
              cpu: "2000m"          # 2 CPU cores
              memory: "1Gi"         # 1 GiB RAM (OOMKilled if exceeded)
          
          # Health Probes — CRITICAL for reliability
          
          # Startup Probe: Wait for the app to finish starting
          # Spring Boot apps can take 30-60s to start (classpath scanning, DB migration)
          startupProbe:
            httpGet:
              path: /actuator/health/liveness
              port: management
            initialDelaySeconds: 10
            periodSeconds: 5
            failureThreshold: 30    # 30 * 5s = 150s max startup time
          
          # Liveness Probe: Is the app still alive? If not, RESTART the pod.
          # Detects deadlocks, infinite loops, and unrecoverable states
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: management
            periodSeconds: 10
            failureThreshold: 3     # 3 failures → pod restarted
          
          # Readiness Probe: Can the app serve traffic? If not, REMOVE from Service.
          # Detects temporary overload, DB connection issues, cache warming in progress
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: management
            periodSeconds: 5
            failureThreshold: 3     # 3 failures → pod removed from load balancer
          
          # Environment variables
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "production"
            - name: JAVA_OPTS
              value: "-XX:+UseZGC -Xms512m -Xmx768m -XX:+ZGenerational"
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: order-service-secrets
                  key: db-password
          
          # Configuration from ConfigMap
          envFrom:
            - configMapRef:
                name: order-service-config
          
          # Volume mounts
          volumeMounts:
            - name: config-volume
              mountPath: /app/config
              readOnly: true
      
      volumes:
        - name: config-volume
          configMap:
            name: order-service-config
```

**The three types of probes**:

| Probe | What it checks | On failure | Example |
|-------|---------------|------------|---------|
| **Startup** | Has the app finished starting? | Keep waiting (don't run liveness) | Spring Boot takes 45s to start — don't kill it after 10s |
| **Liveness** | Is the app alive and not deadlocked? | **Restart** the pod | App is stuck in an infinite loop — restart it |
| **Readiness** | Can the app serve traffic RIGHT NOW? | **Remove** from Service (stop sending traffic) | Database connection is down temporarily — don't send requests |

**Critical difference**: Liveness failure **restarts** the pod (nuclear option). Readiness failure **removes** the pod from the load balancer (gentle). A readiness failure does NOT restart the pod — it might recover on its own (e.g., database comes back).

**Spring Boot Actuator health groups** (for Kubernetes probes):
```yaml
# application.yml
management:
  server:
    port: 8081                       # Separate management port
  endpoints:
    web:
      exposure:
        include: health, prometheus, info
  health:
    livenessstate:
      enabled: true                  # /actuator/health/liveness
    readinessstate:
      enabled: true                  # /actuator/health/readiness
  endpoint:
    health:
      probes:
        enabled: true
      group:
        readiness:
          include: db, redis, kafka  # Readiness depends on downstream systems
        liveness:
          include: livenessState     # Liveness is just "is the JVM alive"
```

#### Services — Internal Networking and Discovery

A Kubernetes Service provides a **stable network endpoint** (IP address + DNS name) for a set of pods. Since pods are ephemeral (they come and go), you cannot rely on pod IP addresses. A Service gives you a stable address that automatically routes to healthy pods.

```yaml
# service.yaml
apiVersion: v1
kind: Service
metadata:
  name: order-service              # DNS name: order-service.production.svc.cluster.local
  namespace: production
spec:
  selector:
    app: order-service             # Routes traffic to pods with this label
  ports:
    - name: http
      port: 80                     # The port exposed by the Service
      targetPort: 8080             # The port on the pod
  type: ClusterIP                  # Only accessible within the cluster (default)
```

**Service types**:

| Type | Accessibility | Use case |
|------|-------------|----------|
| **ClusterIP** (default) | Only within the cluster | Internal microservice-to-microservice communication |
| **NodePort** | External access via `<NodeIP>:<NodePort>` | Development, testing (not for production) |
| **LoadBalancer** | External access via cloud provider's LB (e.g., AWS ELB) | Exposing a single service externally |

**DNS-based service discovery**: Kubernetes automatically creates DNS records for each Service. Within the cluster, services can call each other by name:

```java
// In Spring Boot application.yml
# Instead of hardcoding IP addresses:
order-service:
  url: http://order-service.production.svc.cluster.local  # Full DNS name
  # or simply: http://order-service (if in the same namespace)
```

#### Ingress — External Traffic Routing

Ingress is how external traffic (from the internet) reaches your services. An Ingress resource defines routing rules, and an **Ingress Controller** (nginx, Traefik, Istio Gateway) implements them.

```yaml
# ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: api-ingress
  namespace: production
  annotations:
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/rate-limit: "100"     # 100 req/sec per IP
    nginx.ingress.kubernetes.io/proxy-body-size: "10m"
    cert-manager.io/cluster-issuer: "letsencrypt-prod" # Auto-TLS with cert-manager
spec:
  ingressClassName: nginx
  tls:
    - hosts:
        - api.myapp.com
      secretName: api-tls-cert    # TLS certificate
  rules:
    - host: api.myapp.com
      http:
        paths:
          # Path-based routing to different services
          - path: /api/orders
            pathType: Prefix
            backend:
              service:
                name: order-service
                port:
                  number: 80
          - path: /api/users
            pathType: Prefix
            backend:
              service:
                name: user-service
                port:
                  number: 80
          - path: /api/payments
            pathType: Prefix
            backend:
              service:
                name: payment-service
                port:
                  number: 80
```

```
Internet Traffic Flow:
                                                    
   Client → DNS (api.myapp.com) → Cloud LB → Ingress Controller (nginx)
                                                    │
                                   ┌────────────────┼────────────────┐
                                   │                │                 │
                              /api/orders       /api/users       /api/payments
                                   │                │                 │
                                   ▼                ▼                 ▼
                             order-service    user-service    payment-service
                            (3 pods, LB'd)   (2 pods, LB'd)  (2 pods, LB'd)
```

#### Rolling Updates and Rollback

**Rolling Update** (the default strategy): Kubernetes gradually replaces old pods with new ones. At no point are all pods simultaneously unavailable.

```
Rolling Update Process (3 replicas, maxSurge=1, maxUnavailable=0):

Step 0: [v1] [v1] [v1]           ← 3 old pods running
Step 1: [v1] [v1] [v1] [v2]     ← 1 new pod created (maxSurge=1 → 4 total allowed)
Step 2: [v1] [v1] [v2] [v2]     ← v2 pod passes readiness → old v1 terminated
Step 3: [v1] [v2] [v2] [v2]     ← Another v2 created, another v1 terminated
Step 4: [v2] [v2] [v2]          ← All pods updated, done!
```

**How to trigger a rolling update**:
```bash
# Update the container image
kubectl set image deployment/order-service order-service=myregistry.com/order-service:v2.3.2

# Or apply an updated manifest
kubectl apply -f deployment.yaml

# Monitor the rollout
kubectl rollout status deployment/order-service

# Rollback to the previous version if something goes wrong
kubectl rollout undo deployment/order-service

# Rollback to a specific revision
kubectl rollout history deployment/order-service
kubectl rollout undo deployment/order-service --to-revision=3
```

#### Horizontal Pod Autoscaler (HPA)

HPA automatically adjusts the number of pod replicas based on CPU utilization, memory utilization, or custom metrics.

```yaml
# hpa.yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: order-service-hpa
  namespace: production
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: order-service
  minReplicas: 3                    # Never go below 3 pods
  maxReplicas: 20                   # Never exceed 20 pods
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70    # Scale up when avg CPU > 70%
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80    # Scale up when avg memory > 80%
    - type: Pods
      pods:
        metric:
          name: http_requests_per_second  # Custom metric from Prometheus
        target:
          type: AverageValue
          averageValue: "1000"      # Scale up when avg > 1000 RPS per pod
  behavior:
    scaleUp:
      stabilizationWindowSeconds: 60   # Wait 60s before scaling up again
      policies:
        - type: Pods
          value: 3                      # Add at most 3 pods at a time
          periodSeconds: 60
    scaleDown:
      stabilizationWindowSeconds: 300  # Wait 5 min before scaling down (avoid flapping)
      policies:
        - type: Pods
          value: 1                      # Remove at most 1 pod at a time
          periodSeconds: 120
```

#### ConfigMaps and Secrets

```yaml
# configmap.yaml — non-sensitive configuration
apiVersion: v1
kind: ConfigMap
metadata:
  name: order-service-config
  namespace: production
data:
  SPRING_DATASOURCE_URL: "jdbc:postgresql://postgres:5432/orders"
  SPRING_REDIS_HOST: "redis-cluster"
  SPRING_KAFKA_BOOTSTRAP_SERVERS: "kafka:9092"
  LOG_LEVEL: "INFO"

---
# secret.yaml — sensitive data (base64 encoded)
apiVersion: v1
kind: Secret
metadata:
  name: order-service-secrets
  namespace: production
type: Opaque
data:
  db-password: cGFzc3dvcmQxMjM=     # base64 of "password123"
  api-key: c2VjcmV0LWtleS14eXo=      # base64 of "secret-key-xyz"
# NOTE: In production, use an external secrets manager (Vault, AWS Secrets Manager)
# not Kubernetes Secrets directly (they are only base64 encoded, not encrypted at rest by default)
```

#### Graceful Shutdown — The Full Picture

When Kubernetes terminates a pod (during scaling down, rolling update, or node draining), the pod needs time to finish processing in-flight requests. Without graceful shutdown, requests are dropped mid-processing.

**Kubernetes pod termination sequence**:
1. Pod is marked as "Terminating". It is **immediately removed from Service endpoints** (no new traffic).
2. PreStop hook runs (if configured).
3. `SIGTERM` signal is sent to the container's main process.
4. Kubernetes waits up to `terminationGracePeriodSeconds` (default 30s) for the process to exit.
5. If the process hasn't exited, `SIGKILL` is sent (forced kill).

**Spring Boot graceful shutdown**:
```yaml
# application.yml
server:
  shutdown: graceful                    # Wait for in-flight requests to complete
  
spring:
  lifecycle:
    timeout-per-shutdown-phase: 30s     # Max wait time for graceful shutdown
```

```java
// Custom shutdown behavior
@Component
public class GracefulShutdownHandler {
    
    @PreDestroy
    public void onShutdown() {
        log.info("Received shutdown signal. Completing in-flight requests...");
        // Stop accepting new Kafka messages
        // Flush any buffered data
        // Close database connection pool gracefully
    }
}
```

**Kubernetes PreStop hook** — add a small delay to account for the time between pod removal from endpoints and actual `SIGTERM`:
```yaml
lifecycle:
  preStop:
    exec:
      command: ["sh", "-c", "sleep 5"]
# Why: There's a brief race condition. Kubernetes removes the pod from endpoints
# and sends SIGTERM simultaneously. If the removal hasn't propagated to all
# kube-proxies/ingress controllers yet, some requests may still arrive.
# A 5-second sleep ensures the endpoints update has propagated before we start shutting down.
```

