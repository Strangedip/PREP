# AI System Design — Interview Problems with Solutions

> **Goal**: Practice designing AI-powered systems at scale. These are the types of system design questions asked at Lead/Staff level interviews in 2026.
> **Level**: Lead Engineer

> **You are here**: Tech Lead — AI / ML
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [09_MLOps_AI_in_Production.md](09_MLOps_AI_in_Production.md) | **Next**: [11_AI_Ethics_Safety_Governance.md](11_AI_Ethics_Safety_Governance.md)

---

## 1. How AI Changes System Design Interviews

### 1.1 Traditional vs. AI System Design

| Aspect | Traditional | AI-Augmented (2026) |
|--------|------------|---------------------|
| **Data flow** | Request → Service → DB → Response | Request → Guardrails → Cache → LLM/RAG → Validate → Response |
| **Latency** | 50-200ms target | 1-10s acceptable for AI features |
| **Cost model** | Infrastructure (fixed) | Infrastructure + token cost (variable, per-request) |
| **Testing** | Deterministic assertions | Statistical quality metrics, evaluation pipelines |
| **Scaling** | Add more instances | Rate limit management, model routing, caching |
| **Failure modes** | Timeouts, crashes | Hallucination, quality degradation, cost overrun |

### 1.2 AI System Design Framework

Use this framework for every AI system design question:

```
1. REQUIREMENTS (5 min)
   - Functional requirements (what does it do?)
   - Non-functional: latency, scale, cost budget, accuracy target
   - AI-specific: acceptable hallucination rate, freshness of knowledge

2. HIGH-LEVEL ARCHITECTURE (5 min)
   - Core components
   - AI pipeline (ingestion, retrieval, generation)
   - Data flow

3. DEEP DIVE: AI PIPELINE (15 min)
   - Knowledge source and ingestion
   - Chunking and embedding strategy
   - Retrieval and ranking
   - Prompt design
   - Output validation

4. SCALING AND COST (5 min)
   - Caching strategy
   - Model selection and routing
   - Cost estimation and budget
   - Rate limiting

5. EVALUATION AND MONITORING (5 min)
   - Quality metrics
   - Monitoring and alerting
   - A/B testing
   - Feedback loops
```

---

## 2. Problem 1: Design an AI-Powered Customer Support System

### Requirements

**Functional:**
- Customers chat with an AI assistant about their orders, products, and account
- AI resolves 80% of queries without human intervention
- Seamless handoff to human agents when AI cannot resolve
- Multi-turn conversation with context retention
- AI can perform actions: look up orders, process refunds, track shipments

**Non-Functional:**
- 500K daily active users, peak 50K concurrent
- Response latency: first token < 2 seconds, full response < 10 seconds
- Uptime: 99.9%
- Monthly budget: $50,000 for AI costs

**AI-Specific:**
- Hallucination rate < 1% (financial/order data must be accurate)
- Support 10 languages
- Knowledge base: 5,000 product pages, 500 policy documents, 200 FAQ articles

### Architecture

```
┌───────────────────────────────────────────────────────────────────┐
│                                                                   │
│  ┌────────┐     ┌─────────────┐     ┌──────────────────────────┐ │
│  │ Web/   │────►│ API Gateway │────►│    Support AI Service     │ │
│  │ Mobile │◄────│ (WS + SSE)  │◄────│                          │ │
│  │ Client │     └─────────────┘     │ ┌──────────────────────┐ │ │
│  └────────┘                         │ │   Intent Classifier   │ │ │
│                                     │ │   (GPT-4o-mini)       │ │ │
│  ┌────────────────────────────────┐ │ └──────────┬───────────┘ │ │
│  │      Knowledge Base            │ │            │             │ │
│  │ ┌────────┐ ┌─────┐ ┌────────┐│ │ ┌──────────▼───────────┐ │ │
│  │ │Products│ │FAQs │ │Policies││ │ │    RAG Pipeline        │ │ │
│  │ │(5K pg) │ │(200)│ │ (500)  ││ │ │ ○ Retrieval           │ │ │
│  │ └───┬────┘ └──┬──┘ └───┬────┘│ │ │ ○ Context Assembly    │ │ │
│  │     └─────────┼────────┘     │ │ │ ○ Prompt Construction │ │ │
│  │           ┌───▼────┐         │ │ └──────────┬───────────┘ │ │
│  │           │pgvector│         │ │            │             │ │
│  │           │(Vector │         │ │ ┌──────────▼───────────┐ │ │
│  │           │ Store) │         │ │ │  Function Calling     │ │ │
│  │           └────────┘         │ │ │ ○ getOrder()          │ │ │
│  └────────────────────────────────┘ │ │ ○ processRefund()    │ │ │
│                                     │ │ ○ trackShipment()    │ │ │
│  ┌────────────────────────────────┐ │ │ ○ escalateToHuman()  │ │ │
│  │    Backend Services            │ │ └──────────┬───────────┘ │ │
│  │ ┌────────┐ ┌────────┐        │ │            │             │ │
│  │ │ Order  │ │Shipping│ ...    │ │ ┌──────────▼───────────┐ │ │
│  │ │Service │ │Service │        │ │ │  Output Guardrails    │ │ │
│  │ └────────┘ └────────┘        │ │ │ ○ Fact verification   │ │ │
│  └────────────────────────────────┘ │ │ ○ Tone check         │ │ │
│                                     │ │ ○ PII redaction      │ │ │
│  ┌────────────────────────────────┐ │ └──────────────────────┘ │ │
│  │    Human Agent Platform        │ │                          │ │
│  │ ○ Agent dashboard              │ └──────────────────────────┘ │
│  │ ○ AI context handoff           │                              │
│  │ ○ Ticket management            │                              │
│  └────────────────────────────────┘                              │
└───────────────────────────────────────────────────────────────────┘
```

### Key Design Decisions

**1. Intent Classification (First):**
```
User message → GPT-4o-mini classifier → Intent
Intents:
- ORDER_STATUS: Route to order lookup tool
- REFUND_REQUEST: Route to refund workflow (with human approval for > $100)
- PRODUCT_QUESTION: Route to RAG over product docs
- GENERAL_SUPPORT: Route to RAG over FAQ/policies
- COMPLAINT: Route to human agent (always)
- OFF_TOPIC: Politely redirect
```

**2. Conversation Memory:**
- Store last 20 messages per session in Redis (TTL: 2 hours)
- When session ends, summarize conversation and store in PostgreSQL
- Customer history (past tickets, orders) loaded as context

**3. Escalation Triggers (AI → Human):**
- AI confidence score < 0.5
- Customer says "talk to a human" or "agent"
- 3+ failed attempts to resolve
- Sentiment analysis detects high frustration
- Financial action > $100

**4. Cost Estimation:**
```
500K users/day × 4 messages avg = 2M messages/day
Average tokens per message: 500 input + 200 output
Model: GPT-4o-mini ($0.15 / $0.60 per 1M tokens)

Daily cost:
- Input: 2M × 500 × $0.15 / 1M = $150/day
- Output: 2M × 200 × $0.60 / 1M = $240/day
- Total: ~$390/day ≈ $11,700/month

With 35% cache hit rate: ~$7,600/month ✓ (within $50K budget)
```

---

## 3. Problem 2: Design a RAG-Powered Enterprise Knowledge Base

### Requirements

**Functional:**
- Internal knowledge base for 10,000 employees
- Search across: Confluence (50K pages), Slack (500K messages), Jira (200K tickets), GitHub (1M files)
- AI-powered Q&A with source citations
- Auto-updated when source documents change
- Access control: users can only see docs they have permission for

**Non-Functional:**
- Query latency: < 5 seconds
- Ingestion latency: < 15 minutes from source update
- Accuracy: > 90% faithfulness score
- Scale: 50K queries/day

### Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                 ENTERPRISE KNOWLEDGE BASE                    │
│                                                             │
│  ┌────────────────────── INGESTION ──────────────────────┐  │
│  │                                                       │  │
│  │  ┌───────────┐  ┌───────────┐  ┌─────────┐  ┌─────┐ │  │
│  │  │Confluence │  │   Slack   │  │  Jira   │  │GitHub│ │  │
│  │  │  Webhooks │  │  Events   │  │ Webhooks│  │Events│ │  │
│  │  └─────┬─────┘  └─────┬─────┘  └────┬────┘  └──┬──┘ │  │
│  │        └───────────────┼─────────────┼──────────┘    │  │
│  │                   ┌────▼────┐                        │  │
│  │                   │  Kafka  │                        │  │
│  │                   │(Events) │                        │  │
│  │                   └────┬────┘                        │  │
│  │                   ┌────▼─────────────────┐           │  │
│  │                   │  Ingestion Service    │           │  │
│  │                   │ ○ Parse documents     │           │  │
│  │                   │ ○ Chunk (by type)     │           │  │
│  │                   │ ○ Extract metadata    │           │  │
│  │                   │ ○ ACL tags            │           │  │
│  │                   │ ○ Embed (batch)       │           │  │
│  │                   └────┬─────────────────┘           │  │
│  │                   ┌────▼─────────────────┐           │  │
│  │                   │  pgvector + metadata  │           │  │
│  │                   │  (Access-controlled)  │           │  │
│  │                   └──────────────────────┘           │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌────────────────── QUERY ──────────────────────────────┐  │
│  │                                                       │  │
│  │  User Query → Auth (JWT) → Get User Groups            │  │
│  │       ↓                                               │  │
│  │  Query Rewrite (AI) → Multi-Query Expansion           │  │
│  │       ↓                                               │  │
│  │  Hybrid Search (Vector + BM25)                        │  │
│  │  + ACL Filter (WHERE groups && user_groups)           │  │
│  │       ↓                                               │  │
│  │  Re-rank (Cross-encoder)                              │  │
│  │       ↓                                               │  │
│  │  Generate Answer (Claude Sonnet for quality)          │  │
│  │  + Source Citations                                   │  │
│  │       ↓                                               │  │
│  │  Stream to User (SSE)                                 │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Key Design Decisions

**1. Access Control at the Vector Level:**
```sql
CREATE TABLE knowledge_vectors (
    id BIGSERIAL PRIMARY KEY,
    content TEXT,
    embedding vector(1536),
    source_type VARCHAR(20),   -- confluence, slack, jira, github
    source_id VARCHAR(255),     -- Original document ID
    source_url TEXT,
    acl_groups TEXT[],          -- Groups that can access this document
    updated_at TIMESTAMP
);

-- Query with ACL filtering
SELECT content, source_url, 1 - (embedding <=> $1::vector) AS similarity
FROM knowledge_vectors
WHERE acl_groups && $2::text[]    -- $2 = user's groups
ORDER BY embedding <=> $1::vector
LIMIT 10;
```

**2. Chunking Strategy by Source Type:**
| Source | Chunking Strategy | Chunk Size | Metadata |
|--------|-------------------|-----------|----------|
| Confluence | Heading-based sections | 500-1000 tokens | page_title, space, author, updated |
| Slack | Per message thread | Full thread | channel, participants, timestamp |
| Jira | Per ticket (title + description + comments) | Variable | project, status, assignee, labels |
| GitHub | Per file, per function (code-aware) | Per function/class | repo, path, language, branch |

**3. Incremental Ingestion:**
- Each source has a webhook/event listener
- Events go to Kafka for reliable processing
- Ingestion service processes events: parse → chunk → embed → upsert
- Old vectors for the same source_id are deleted before new ones are inserted
- Latency target: < 15 minutes from source change to searchable

---

## 4. Problem 3: Design an AI Code Review System

### Requirements

**Functional:**
- Automatically review every PR for: bugs, security issues, performance, best practices
- Provide inline comments on specific code lines
- Suggest fixes with code snippets
- Learn from codebase patterns and team conventions
- Integrate with GitHub/GitLab

**Non-Functional:**
- Process PRs within 2 minutes of creation
- Handle 500 PRs/day
- False positive rate < 10%

### Architecture

```
GitHub PR Webhook
      │
      ▼
┌────────────────────────────┐
│    PR Processing Queue     │
│         (Kafka)            │
└──────────┬─────────────────┘
           │
      ┌────▼────┐
      │  Diff   │
      │ Parser  │───► Extract changed files, hunks, context
      └────┬────┘
           │
      ┌────▼────────────────────┐
      │  Context Enrichment     │
      │ ○ Full file content     │
      │ ○ Related files (imports│
      │   , tests, interfaces)  │
      │ ○ PR description        │
      │ ○ Coding standards (RAG)│
      │ ○ Past review patterns  │
      └────┬────────────────────┘
           │
      ┌────▼────────────────────┐
      │  Multi-Pass Review      │
      │                         │
      │  Pass 1: Bug Detection  │──► GPT-4o (high quality)
      │  Pass 2: Security Scan  │──► Specialized security prompt
      │  Pass 3: Performance    │──► Performance-focused prompt
      │  Pass 4: Code Quality   │──► Style/conventions prompt
      │                         │
      └────┬────────────────────┘
           │
      ┌────▼────────────────────┐
      │  Issue Deduplication    │
      │  & Confidence Scoring   │
      └────┬────────────────────┘
           │
      ┌────▼────────────────────┐
      │  GitHub PR Comments API │
      │  (Inline review comments│
      │   with suggestions)     │
      └────────────────────────┘
```

### Key Design Decision: Multi-Pass Review

Instead of one monolithic prompt, use specialized passes:

```java
public class CodeReviewPipeline {

    public ReviewResult review(PullRequest pr) {
        DiffContext context = diffParser.parse(pr);

        // Run passes in parallel
        CompletableFuture<List<Issue>> bugs = CompletableFuture.supplyAsync(
            () -> bugDetector.analyze(context));
        CompletableFuture<List<Issue>> security = CompletableFuture.supplyAsync(
            () -> securityScanner.analyze(context));
        CompletableFuture<List<Issue>> performance = CompletableFuture.supplyAsync(
            () -> performanceAnalyzer.analyze(context));
        CompletableFuture<List<Issue>> quality = CompletableFuture.supplyAsync(
            () -> qualityChecker.analyze(context));

        // Combine and deduplicate
        List<Issue> allIssues = CompletableFuture.allOf(bugs, security, performance, quality)
            .thenApply(v -> {
                List<Issue> combined = new ArrayList<>();
                combined.addAll(bugs.join());
                combined.addAll(security.join());
                combined.addAll(performance.join());
                combined.addAll(quality.join());
                return deduplicator.deduplicate(combined);
            })
            .join();

        // Filter by confidence threshold
        List<Issue> highConfidence = allIssues.stream()
            .filter(issue -> issue.confidence() > 0.7)
            .toList();

        return new ReviewResult(highConfidence, calculateOverallScore(highConfidence));
    }
}
```

---

## 5. Problem 4: Design a Content Recommendation Engine with AI

### Requirements

- E-commerce platform with 10M users, 1M products
- AI-powered recommendations: "You might also like", "Because you viewed X"
- Natural language product discovery: "Find me a warm jacket for skiing under $200"
- Real-time personalization based on browsing behavior

### Key Architecture Components

**1. Dual Retrieval (Traditional + AI):**
```
User browses product
       │
       ├──► Collaborative Filtering (traditional)
       │    "Users who bought X also bought Y"
       │    Precomputed, Redis-cached, < 10ms
       │
       ├──► Content-Based (embedding similarity)
       │    Product embedding ←→ cosine similarity
       │    pgvector, < 50ms
       │
       └──► AI Natural Language Search
            User types: "warm skiing jacket under $200"
            Embed query → vector search + metadata filter (price < 200)
            + LLM re-rank by relevance
            < 3 seconds
```

**2. Product Embedding Strategy:**
```java
// Combine multiple signals into one embedding
public float[] createProductEmbedding(Product product) {
    String richText = """
        Product: %s
        Category: %s
        Description: %s
        Features: %s
        Price Range: %s
        Suitable for: %s
        """.formatted(
            product.name(),
            product.category(),
            product.description(),
            String.join(", ", product.features()),
            priceRange(product.price()),
            String.join(", ", product.useCases())
        );

    return embeddingModel.embed(richText);
}
```

---

## 6. Common AI System Design Questions

| Problem | Key AI Components | Main Challenges |
|---------|-------------------|-----------------|
| **AI Customer Support** | RAG, Function Calling, Conversation Memory | Hallucination prevention, escalation logic, cost |
| **Enterprise Knowledge Base** | RAG, ACL, Hybrid Search, Incremental Ingestion | Access control, multi-source, freshness |
| **AI Code Review** | Multi-pass analysis, Codebase RAG | False positives, context size, latency |
| **Content Recommendation** | Embeddings, Vector Search, Personalization | Scale (10M users), real-time, cold start |
| **AI-Powered Search** | Hybrid Search, RAG, Query Understanding | Relevance, latency, multi-language |
| **Document Processing Pipeline** | OCR, Extraction, Classification, Structured Output | Accuracy, document variety, scale |
| **AI Chatbot for Healthcare** | RAG, Strict Guardrails, Audit Trail | Safety, compliance (HIPAA), liability |
| **Fraud Detection with AI** | Real-time scoring, Anomaly Detection, Explainability | Latency (<100ms), false positive rate |
| **AI-Powered Content Moderation** | Classification, Multi-modal (text + image) | Scale, nuance, cultural sensitivity |
| **Internal AI Developer Tools** | Code RAG, Agent with Tools, CI/CD integration | Codebase size, context window limits |

---

## 7. Back-of-the-Envelope Estimation for AI Systems

### 7.1 Token Cost Quick Reference

```
1 token ≈ 4 characters ≈ 0.75 words
1 page of text ≈ 500 tokens
1 typical RAG request ≈ 3,000 input tokens + 500 output tokens

Cost per request (GPT-4o-mini): ~$0.0008
Cost per request (GPT-4o): ~$0.0125
Cost per request (Claude Sonnet): ~$0.0165

1M requests/month:
  GPT-4o-mini: ~$800/month
  GPT-4o: ~$12,500/month
  Claude Sonnet: ~$16,500/month
```

### 7.2 Embedding Storage Quick Reference

```
1M documents (avg 500 tokens each):
  Chunks (with 500-token chunks): ~2M vectors
  Storage: 2M × 1536 dims × 4 bytes = ~12 GB
  HNSW index: ~24 GB RAM
  Embedding cost (one-time): 2M × 500 tokens × $0.02/1M = $20

Ingestion throughput:
  OpenAI embeddings API: ~3000 tokens/second (single request)
  Batch mode: ~100K tokens/second
  1M documents: ~6 hours (batch), ~3 days (single)
```

---

**Next**: [11_AI_Ethics_Safety_Governance.md](11_AI_Ethics_Safety_Governance.md) — Responsible AI, bias, compliance, and governance.

