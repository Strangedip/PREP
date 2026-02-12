# RAG (Retrieval-Augmented Generation) Architecture — Complete Deep Dive

> **Goal**: Understand and implement RAG end-to-end using Java, Spring Boot, and modern tooling. RAG is the #1 pattern for building AI-powered applications with real-world knowledge.
> **Level**: Mid-Level through Lead

---

## 1. What is RAG and Why Does It Matter?

### 1.1 The Problem RAG Solves

LLMs have three fundamental limitations:
1. **Knowledge cutoff**: Training data has a fixed date — the model knows nothing after it
2. **No proprietary knowledge**: The model does not know your company's docs, codebase, or data
3. **Hallucination**: When the model does not know something, it may generate plausible but false information

**RAG solves all three** by retrieving relevant information from your own data sources and injecting it into the prompt before the LLM generates a response.

### 1.2 RAG vs. Fine-Tuning vs. Long Context

| Approach | What It Does | When to Use | Cost | Freshness |
|----------|-------------|-------------|------|-----------|
| **RAG** | Retrieve relevant docs, add to prompt | Dynamic knowledge, frequently updated data | Low | Real-time |
| **Fine-tuning** | Retrain model on your data | Teaching new behaviors/styles, not facts | High | Stale (needs re-training) |
| **Long Context** | Dump all docs into the prompt | Small knowledge bases (< 50 pages) | High per-request | Real-time |
| **RAG + Fine-tuning** | Both | Maximum quality for high-value use cases | Very High | Depends |

**Decision Flow:**
```
Is your knowledge base < 50 pages?
├── Yes → Try Long Context first (simplest)
├── No
│   ├── Does it change frequently?
│   │   ├── Yes → RAG (best for dynamic data)
│   │   └── No → Fine-tuning OR RAG (depending on budget)
│   └── Do you need domain-specific behavior/style?
│       ├── Yes → Fine-tuning + RAG
│       └── No → RAG alone
```

### 1.3 RAG Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    RAG SYSTEM                            │
│                                                         │
│  ┌─────────────────────────────────────────────────────┐│
│  │             INGESTION PIPELINE (Offline)            ││
│  │                                                     ││
│  │  Documents → Chunk → Embed → Store in Vector DB     ││
│  │  (PDF, MD,   (Split   (Convert    (pgvector,       ││
│  │   HTML, DB)   text)    to vectors)  Pinecone)       ││
│  └─────────────────────────────────────────────────────┘│
│                                                         │
│  ┌─────────────────────────────────────────────────────┐│
│  │           RETRIEVAL + GENERATION (Online)           ││
│  │                                                     ││
│  │  User Query → Embed → Vector Search → Re-rank →    ││
│  │  Augment Prompt → LLM → Response → (Validate)      ││
│  └─────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

---

## 2. The Ingestion Pipeline — Getting Data Ready

### 2.1 Document Loading

The first step is loading your raw documents from various sources.

```java
@Service
public class DocumentIngestionService {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;

    // Load from various sources
    public void ingestDocuments() {
        // PDF documents
        var pdfReader = new PagePdfDocumentReader(
            new ClassPathResource("docs/technical-guide.pdf"),
            PdfDocumentReaderConfig.builder()
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                    .withNumberOfBottomTextLinesToDelete(3) // Remove footers
                    .withNumberOfTopPagesToSkipBeforeDelete(1) // Skip cover page
                    .build())
                .withPagesPerDocument(1) // One document per page
                .build()
        );
        List<Document> pdfDocs = pdfReader.get();

        // Markdown documents
        var mdReader = new MarkdownDocumentReader(
            new ClassPathResource("docs/api-reference.md"),
            MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true) // Split on ---
                .withIncludeCodeBlock(true)
                .withIncludeBlockquote(true)
                .build()
        );
        List<Document> mdDocs = mdReader.get();

        // JSON/structured data
        var jsonReader = new JsonReader(
            new ClassPathResource("docs/faq.json"),
            "question", "answer", "category" // Fields to extract
        );
        List<Document> jsonDocs = jsonReader.get();

        // HTML web pages
        var htmlReader = new TikaDocumentReader(
            new UrlResource("https://docs.example.com/guide")
        );
        List<Document> htmlDocs = htmlReader.get();

        // Process all documents through the pipeline
        List<Document> allDocs = new ArrayList<>();
        allDocs.addAll(pdfDocs);
        allDocs.addAll(mdDocs);
        allDocs.addAll(jsonDocs);
        allDocs.addAll(htmlDocs);

        processAndStore(allDocs);
    }
}
```

### 2.2 Chunking Strategies — The Most Critical RAG Decision

Chunking is **the most impactful decision in your RAG pipeline**. Poor chunking → poor retrieval → poor answers.

**Why Chunking Matters:**
- Embedding models have token limits (512-8192 tokens)
- Smaller chunks = more precise retrieval but less context
- Larger chunks = more context but noisier retrieval
- Chunk boundaries must respect semantic boundaries (do not split mid-sentence)

**Chunking Strategies:**

#### Strategy 1: Fixed-Size Chunking (Simplest)

```java
// Simple but crude — may split mid-sentence, mid-paragraph
var splitter = new TokenTextSplitter(
    800,    // defaultChunkSize (tokens)
    350,    // minChunkSizeChars
    200,    // minChunkLengthToEmbed
    1000,   // maxNumChunks
    true    // keepSeparator
);
List<Document> chunks = splitter.apply(documents);
```

**Pros**: Simple, predictable chunk sizes
**Cons**: Splits mid-sentence, loses context

#### Strategy 2: Recursive Character Splitting (Recommended Default)

Splits by progressively smaller delimiters: paragraphs → sentences → words.

```java
// Spring AI's TextSplitter with overlap
var splitter = new TokenTextSplitter(
    500,   // chunk size in tokens
    100    // overlap in tokens (critical for context continuity!)
);

// Custom recursive splitter
public class RecursiveTextSplitter {
    private final List<String> separators = List.of(
        "\n\n",     // Try splitting on double newline (paragraph) first
        "\n",       // Then single newline
        ". ",       // Then sentence boundary
        ", ",       // Then clause boundary
        " ",        // Then word boundary (last resort)
        ""          // Character by character (emergency only)
    );

    private final int chunkSize;
    private final int chunkOverlap;

    public List<String> split(String text) {
        return recursiveSplit(text, separators);
    }

    private List<String> recursiveSplit(String text, List<String> seps) {
        List<String> chunks = new ArrayList<>();
        String separator = seps.getFirst();

        String[] splits = text.split(Pattern.quote(separator));
        StringBuilder current = new StringBuilder();

        for (String split : splits) {
            if (current.length() + split.length() > chunkSize) {
                if (current.length() > 0) {
                    chunks.add(current.toString().trim());
                    // Add overlap by keeping the last portion
                    String overlap = current.substring(
                        Math.max(0, current.length() - chunkOverlap)
                    );
                    current = new StringBuilder(overlap);
                }
            }
            current.append(split).append(separator);
        }

        if (current.length() > 0) {
            chunks.add(current.toString().trim());
        }

        return chunks;
    }
}
```

**Pros**: Respects semantic boundaries, overlap preserves context
**Cons**: Variable chunk sizes

#### Strategy 3: Semantic Chunking (Advanced — Best Quality)

Uses embeddings to detect semantic boundaries. Splits when the meaning shifts significantly.

```java
public class SemanticChunker {

    private final EmbeddingModel embeddingModel;
    private final double similarityThreshold;

    public SemanticChunker(EmbeddingModel embeddingModel, double threshold) {
        this.embeddingModel = embeddingModel;
        this.similarityThreshold = threshold; // e.g., 0.8
    }

    public List<String> chunk(String text) {
        // Step 1: Split into sentences
        String[] sentences = text.split("(?<=[.!?])\\s+");

        // Step 2: Get embeddings for each sentence
        List<float[]> embeddings = Arrays.stream(sentences)
            .map(s -> embeddingModel.embed(s))
            .toList();

        // Step 3: Group sentences by semantic similarity
        List<String> chunks = new ArrayList<>();
        StringBuilder currentChunk = new StringBuilder(sentences[0]);

        for (int i = 1; i < sentences.length; i++) {
            double similarity = cosineSimilarity(embeddings.get(i - 1), embeddings.get(i));

            if (similarity < similarityThreshold) {
                // Semantic shift detected — start new chunk
                chunks.add(currentChunk.toString().trim());
                currentChunk = new StringBuilder();
            }

            currentChunk.append(" ").append(sentences[i]);
        }

        if (currentChunk.length() > 0) {
            chunks.add(currentChunk.toString().trim());
        }

        return chunks;
    }

    private double cosineSimilarity(float[] a, float[] b) {
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
```

**Pros**: Best quality, respects meaning boundaries
**Cons**: Expensive (requires embedding each sentence), slower

#### Strategy 4: Document-Structure-Aware Chunking

Uses the document's structure (headings, sections) as natural boundaries.

```java
public class MarkdownStructureChunker {

    public List<Document> chunkByHeading(String markdown) {
        List<Document> chunks = new ArrayList<>();
        String[] lines = markdown.split("\n");

        StringBuilder currentSection = new StringBuilder();
        String currentHeading = "";
        int headingLevel = 0;

        for (String line : lines) {
            if (line.matches("^#{1,3}\\s+.*")) {
                // New heading found — save previous section
                if (currentSection.length() > 0) {
                    chunks.add(new Document(
                        currentSection.toString().trim(),
                        Map.of(
                            "heading", currentHeading,
                            "headingLevel", String.valueOf(headingLevel),
                            "source", "api-reference.md"
                        )
                    ));
                }
                currentHeading = line.replaceAll("^#+\\s+", "");
                headingLevel = line.indexOf(' ');
                currentSection = new StringBuilder(line).append("\n");
            } else {
                currentSection.append(line).append("\n");
            }
        }

        // Don't forget the last section
        if (currentSection.length() > 0) {
            chunks.add(new Document(
                currentSection.toString().trim(),
                Map.of("heading", currentHeading, "headingLevel", String.valueOf(headingLevel))
            ));
        }

        return chunks;
    }
}
```

#### Strategy 5: Code-Aware Chunking (For Codebase RAG)

When building RAG over source code, you must chunk by code structure, not by text.

```java
public class JavaCodeChunker {

    public List<Document> chunkJavaFile(String sourceCode, String filePath) {
        List<Document> chunks = new ArrayList<>();

        // Parse with a Java parser (e.g., JavaParser library)
        CompilationUnit cu = StaticJavaParser.parse(sourceCode);

        // Chunk 1: Package and imports
        String header = cu.getPackageDeclaration()
            .map(pd -> pd.toString())
            .orElse("") + "\n" +
            cu.getImports().stream()
                .map(Object::toString)
                .collect(Collectors.joining("\n"));

        // Chunk per class
        for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            // Chunk per method within class
            for (MethodDeclaration method : cls.getMethods()) {
                String methodCode = method.toString();
                chunks.add(new Document(
                    methodCode,
                    Map.of(
                        "file", filePath,
                        "class", cls.getNameAsString(),
                        "method", method.getNameAsString(),
                        "returnType", method.getTypeAsString(),
                        "parameters", method.getParameters().toString(),
                        "type", "method"
                    )
                ));
            }

            // Chunk for class-level info (fields, inner classes)
            String classInfo = "Class: " + cls.getNameAsString() + "\n" +
                "Fields: " + cls.getFields().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
            chunks.add(new Document(classInfo, Map.of(
                "file", filePath,
                "class", cls.getNameAsString(),
                "type", "class_definition"
            )));
        }

        return chunks;
    }
}
```

### 2.3 Chunking Decision Guide

| Document Type | Recommended Strategy | Chunk Size | Overlap |
|--------------|---------------------|------------|---------|
| **Technical documentation** | Heading-based + recursive | 500-1000 tokens | 100 tokens |
| **FAQ/Q&A pairs** | Per question-answer pair | Variable (1 pair per chunk) | None |
| **Legal/policy documents** | Paragraph-based with structure | 300-500 tokens | 50 tokens |
| **Source code** | Code-structure-aware | Per function/method | Include class context |
| **Chat logs** | Per conversation turn or session | Per exchange | Include previous turn |
| **General text** | Recursive character splitting | 500 tokens | 100 tokens |
| **Tables/structured data** | Per row or per table section | Variable | Include headers |

### 2.4 Metadata Enrichment

Adding metadata to chunks dramatically improves retrieval quality.

```java
public class MetadataEnricher {

    private final ChatClient chatClient; // For AI-powered enrichment

    public Document enrichChunk(Document chunk) {
        Map<String, Object> metadata = new HashMap<>(chunk.getMetadata());

        // 1. Source tracking
        metadata.put("source", "technical-guide.pdf");
        metadata.put("page", "15");
        metadata.put("section", "Authentication");
        metadata.put("ingestionDate", Instant.now().toString());

        // 2. AI-generated summary (for long chunks)
        if (chunk.getContent().length() > 1000) {
            String summary = chatClient.prompt()
                .system("Summarize this text in one sentence.")
                .user(chunk.getContent())
                .call()
                .content();
            metadata.put("summary", summary);
        }

        // 3. AI-generated keywords/tags
        String keywords = chatClient.prompt()
            .system("Extract 3-5 keywords from this text. Return as comma-separated list.")
            .user(chunk.getContent())
            .call()
            .content();
        metadata.put("keywords", keywords);

        // 4. AI-generated hypothetical questions (HyDE technique)
        String questions = chatClient.prompt()
            .system("Generate 3 questions that this text answers. One per line.")
            .user(chunk.getContent())
            .call()
            .content();
        metadata.put("hypotheticalQuestions", questions);

        return new Document(chunk.getContent(), metadata);
    }
}
```

### 2.5 Embedding and Storing

```java
@Service
public class IngestionPipeline {

    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final TokenTextSplitter splitter;

    public void ingestAndStore(List<Document> documents) {
        // Step 1: Chunk the documents
        List<Document> chunks = splitter.apply(documents);
        log.info("Created {} chunks from {} documents", chunks.size(), documents.size());

        // Step 2: Batch embed and store
        // Spring AI handles embedding + storage in one call
        vectorStore.add(chunks);
        // Under the hood:
        // 1. Each chunk.getContent() is sent to embeddingModel.embed()
        // 2. The resulting vector + content + metadata is stored in the vector DB
        log.info("Stored {} chunks in vector store", chunks.size());
    }
}
```

---

## 3. The Retrieval Pipeline — Finding Relevant Context

### 3.1 Basic Similarity Search

```java
@Service
public class RetrievalService {

    private final VectorStore vectorStore;

    // Basic retrieval
    public List<Document> retrieve(String query) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query)
                .withTopK(5)                    // Return top 5 most similar
                .withSimilarityThreshold(0.7)   // Minimum similarity score
        );
    }
}
```

### 3.2 Filtered Retrieval (Metadata Filtering)

```java
// Only search within specific documents/categories
public List<Document> filteredRetrieve(String query, String source, String section) {
    return vectorStore.similaritySearch(
        SearchRequest.query(query)
            .withTopK(5)
            .withSimilarityThreshold(0.7)
            .withFilterExpression(
                new FilterExpressionBuilder()
                    .eq("source", source)
                    .and(new FilterExpressionBuilder().eq("section", section))
                    .build()
            )
    );
}
```

### 3.3 Hybrid Search (Vector + Keyword)

Pure vector search can miss exact keyword matches. Hybrid search combines vector similarity with traditional keyword search (BM25).

```java
@Service
public class HybridSearchService {

    private final VectorStore vectorStore;         // Semantic search
    private final ElasticsearchClient esClient;     // Keyword search

    public List<Document> hybridSearch(String query, int topK) {
        // Step 1: Vector similarity search
        List<Document> semanticResults = vectorStore.similaritySearch(
            SearchRequest.query(query).withTopK(topK * 2)
        );

        // Step 2: Keyword search (BM25)
        List<Document> keywordResults = keywordSearch(query, topK * 2);

        // Step 3: Reciprocal Rank Fusion (RRF) to combine results
        return reciprocalRankFusion(semanticResults, keywordResults, topK);
    }

    private List<Document> reciprocalRankFusion(
            List<Document> list1, List<Document> list2, int topK) {
        // RRF score = Σ 1/(k + rank_i) for each result list
        // k is typically 60
        final int k = 60;
        Map<String, Double> scores = new HashMap<>();

        for (int i = 0; i < list1.size(); i++) {
            String id = list1.get(i).getId();
            scores.merge(id, 1.0 / (k + i + 1), Double::sum);
        }

        for (int i = 0; i < list2.size(); i++) {
            String id = list2.get(i).getId();
            scores.merge(id, 1.0 / (k + i + 1), Double::sum);
        }

        // Sort by combined score and return top K
        Map<String, Document> allDocs = new HashMap<>();
        list1.forEach(d -> allDocs.putIfAbsent(d.getId(), d));
        list2.forEach(d -> allDocs.putIfAbsent(d.getId(), d));

        return scores.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .limit(topK)
            .map(entry -> allDocs.get(entry.getKey()))
            .toList();
    }
}
```

### 3.4 Query Transformation Techniques

Sometimes the user's query is not a good search query. Transform it before retrieval.

```java
@Service
public class QueryTransformer {

    private final ChatClient chatClient;

    // Technique 1: Query Rewriting
    public String rewriteQuery(String originalQuery) {
        return chatClient.prompt()
            .system("""
                Rewrite the following question to be more specific and search-friendly.
                Remove filler words. Add relevant technical terms.
                Return ONLY the rewritten query, nothing else.
                """)
            .user(originalQuery)
            .call()
            .content();
    }

    // Technique 2: HyDE (Hypothetical Document Embedding)
    // Generate a hypothetical answer, then use THAT as the search query
    public String generateHypotheticalAnswer(String query) {
        return chatClient.prompt()
            .system("""
                Generate a short, factual paragraph that would answer this question.
                Write as if you are writing a documentation page.
                Do not add disclaimers or qualifications.
                """)
            .user(query)
            .call()
            .content();
    }

    // Technique 3: Multi-Query Expansion
    // Generate multiple versions of the query to improve recall
    public List<String> expandQuery(String query) {
        String expansion = chatClient.prompt()
            .system("""
                Generate 3 different versions of the following question.
                Each version should approach the topic from a different angle.
                Return one question per line, numbered 1-3.
                """)
            .user(query)
            .call()
            .content();

        return Arrays.stream(expansion.split("\n"))
            .map(line -> line.replaceAll("^\\d+\\.\\s*", ""))
            .filter(line -> !line.isBlank())
            .toList();
    }

    // Technique 4: Step-Back Prompting
    // Ask a broader question first, then use that context
    public String stepBackQuery(String specificQuery) {
        return chatClient.prompt()
            .system("""
                Given a specific question, generate a broader, more general question
                that would help provide context for answering the specific question.
                Return ONLY the broader question.
                """)
            .user(specificQuery)
            .call()
            .content();
    }
}
```

### 3.5 Re-Ranking (Cross-Encoder)

Initial retrieval gets candidate documents. Re-ranking reorders them by relevance using a more accurate (but slower) model.

```java
@Service
public class ReRankingService {

    private final ChatClient chatClient;

    // AI-based re-ranking (when you don't have a dedicated re-ranker)
    public List<Document> rerank(String query, List<Document> candidates) {
        String prompt = """
            Given the query: "%s"

            Rank the following documents from MOST to LEAST relevant.
            Return ONLY the document numbers in order, comma-separated.
            Example: 3,1,5,2,4

            Documents:
            %s
            """.formatted(
                query,
                IntStream.range(0, candidates.size())
                    .mapToObj(i -> "Document " + (i + 1) + ": " + candidates.get(i).getContent().substring(0, 200))
                    .collect(Collectors.joining("\n\n"))
            );

        String ranking = chatClient.prompt()
            .user(prompt)
            .call()
            .content()
            .trim();

        // Parse ranking and reorder
        List<Integer> order = Arrays.stream(ranking.split(","))
            .map(String::trim)
            .map(Integer::parseInt)
            .map(i -> i - 1) // Convert to 0-based index
            .toList();

        return order.stream()
            .filter(i -> i >= 0 && i < candidates.size())
            .map(candidates::get)
            .toList();
    }

    // Using Cohere Re-Rank API (production-grade)
    public List<Document> cohereRerank(String query, List<Document> candidates) {
        // Call Cohere's /v1/rerank endpoint
        // Returns relevance scores for each document
        // Much faster and more accurate than LLM-based re-ranking
        // Cost: ~$1 per 1000 search queries
        // Implementation depends on Cohere Java SDK
        throw new UnsupportedOperationException("Implement with Cohere SDK");
    }
}
```

---

## 4. The Generation Pipeline — Producing the Answer

### 4.1 Basic RAG Chain with Spring AI

```java
@Service
public class RAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RAGService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chatClient = builder
            .defaultSystem("""
                You are a helpful assistant that answers questions based on the provided context.
                Rules:
                1. ONLY use information from the provided context to answer
                2. If the context doesn't contain the answer, say "I don't have information about that"
                3. Cite which source your answer comes from
                4. Be concise and accurate
                """)
            .defaultAdvisors(
                new QuestionAnswerAdvisor(vectorStore, SearchRequest.defaults().withTopK(5))
            )
            .build();
    }

    public String askQuestion(String question) {
        return chatClient.prompt()
            .user(question)
            .call()
            .content();
    }
}
```

### 4.2 Advanced RAG Chain (Full Control)

```java
@Service
public class AdvancedRAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final QueryTransformer queryTransformer;
    private final ReRankingService reRankingService;

    public RAGResponse askQuestion(String userQuestion) {
        long startTime = System.currentTimeMillis();

        // Step 1: Query transformation
        String rewrittenQuery = queryTransformer.rewriteQuery(userQuestion);
        log.info("Rewritten query: {}", rewrittenQuery);

        // Step 2: Multi-query retrieval
        List<String> expandedQueries = queryTransformer.expandQuery(rewrittenQuery);
        expandedQueries.add(rewrittenQuery); // Include original

        Set<Document> allResults = new LinkedHashSet<>();
        for (String query : expandedQueries) {
            List<Document> results = vectorStore.similaritySearch(
                SearchRequest.query(query)
                    .withTopK(5)
                    .withSimilarityThreshold(0.65)
            );
            allResults.addAll(results);
        }

        List<Document> candidates = new ArrayList<>(allResults);
        log.info("Retrieved {} unique documents from {} queries",
            candidates.size(), expandedQueries.size());

        // Step 3: Re-ranking
        List<Document> rerankedDocs = reRankingService.rerank(rewrittenQuery, candidates);
        List<Document> topDocs = rerankedDocs.stream().limit(5).toList();

        // Step 4: Context assembly
        String context = topDocs.stream()
            .map(doc -> """
                ---
                Source: %s, Section: %s
                %s
                ---
                """.formatted(
                    doc.getMetadata().getOrDefault("source", "Unknown"),
                    doc.getMetadata().getOrDefault("section", "Unknown"),
                    doc.getContent()
                ))
            .collect(Collectors.joining("\n"));

        // Step 5: Generation with citation
        String answer = chatClient.prompt()
            .system("""
                Answer the user's question using ONLY the provided context.
                Rules:
                1. Cite sources for every claim: "According to [Source, Section]..."
                2. If context is insufficient, clearly state what's missing
                3. Distinguish between facts from context and your reasoning
                4. Format with headers and bullet points for readability
                """)
            .user("""
                CONTEXT:
                %s

                QUESTION: %s
                """.formatted(context, userQuestion))
            .call()
            .content();

        long duration = System.currentTimeMillis() - startTime;

        // Step 6: Build response with metadata
        return new RAGResponse(
            answer,
            topDocs.stream()
                .map(d -> new SourceReference(
                    d.getMetadata().getOrDefault("source", "Unknown").toString(),
                    d.getMetadata().getOrDefault("section", "Unknown").toString(),
                    d.getMetadata().getOrDefault("page", "").toString()
                ))
                .toList(),
            duration,
            rewrittenQuery
        );
    }

    public record RAGResponse(
        String answer,
        List<SourceReference> sources,
        long latencyMs,
        String processedQuery
    ) {}

    public record SourceReference(
        String source,
        String section,
        String page
    ) {}
}
```

---

## 5. Advanced RAG Patterns

### 5.1 Parent-Child Chunking

Store small chunks for precise retrieval, but return the parent (larger) chunk for better context.

```java
@Service
public class ParentChildRAG {

    private final VectorStore childStore;   // Small chunks (for retrieval)
    private final Map<String, String> parentStore; // Large chunks (for context)

    public void ingest(String document) {
        // Create large parent chunks
        List<String> parentChunks = splitByParagraphs(document, 2000); // 2000 tokens

        for (int i = 0; i < parentChunks.size(); i++) {
            String parentId = "parent-" + i;
            parentStore.put(parentId, parentChunks.get(i));

            // Create smaller child chunks within each parent
            List<String> childChunks = splitBySentences(parentChunks.get(i), 200); // 200 tokens
            for (String child : childChunks) {
                Document childDoc = new Document(child, Map.of("parentId", parentId));
                childStore.add(List.of(childDoc));
            }
        }
    }

    public String retrieve(String query) {
        // Retrieve using small chunks (precise matching)
        List<Document> childResults = childStore.similaritySearch(
            SearchRequest.query(query).withTopK(3)
        );

        // Return the parent chunks (full context)
        Set<String> parentIds = childResults.stream()
            .map(d -> d.getMetadata().get("parentId").toString())
            .collect(Collectors.toSet());

        return parentIds.stream()
            .map(parentStore::get)
            .collect(Collectors.joining("\n\n---\n\n"));
    }
}
```

### 5.2 Contextual Compression

Compress retrieved documents to only the relevant parts before sending to the LLM.

```java
@Service
public class ContextualCompressionRAG {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;

    public String ask(String question) {
        // Step 1: Retrieve raw documents
        List<Document> docs = vectorStore.similaritySearch(
            SearchRequest.query(question).withTopK(10)
        );

        // Step 2: Compress each document to only relevant parts
        List<String> compressedDocs = docs.stream()
            .map(doc -> compressDocument(doc.getContent(), question))
            .filter(compressed -> !compressed.isBlank())
            .toList();

        // Step 3: Generate answer from compressed context
        String context = String.join("\n---\n", compressedDocs);
        return chatClient.prompt()
            .user("Context: " + context + "\n\nQuestion: " + question)
            .call()
            .content();
    }

    private String compressDocument(String document, String question) {
        return chatClient.prompt()
            .system("""
                Extract ONLY the parts of the following document that are relevant
                to the question. If no parts are relevant, return an empty string.
                Do not add any information — only extract.
                """)
            .user("Question: " + question + "\n\nDocument:\n" + document)
            .options(OpenAiChatOptions.builder().model("gpt-4o-mini").build())
            .call()
            .content();
    }
}
```

### 5.3 Multi-Index RAG

Use different vector stores for different types of content.

```java
@Service
public class MultiIndexRAG {

    private final VectorStore documentStore;     // Technical documentation
    private final VectorStore codeStore;          // Source code
    private final VectorStore faqStore;           // FAQ/Support tickets
    private final VectorStore apiStore;           // API reference

    public List<Document> intelligentRetrieve(String query) {
        // Step 1: Classify the query intent
        String intent = classifyIntent(query);

        // Step 2: Route to appropriate store(s)
        return switch (intent) {
            case "code" -> codeStore.similaritySearch(
                SearchRequest.query(query).withTopK(5));
            case "api" -> apiStore.similaritySearch(
                SearchRequest.query(query).withTopK(5));
            case "troubleshooting" -> {
                List<Document> results = new ArrayList<>();
                results.addAll(faqStore.similaritySearch(
                    SearchRequest.query(query).withTopK(3)));
                results.addAll(documentStore.similaritySearch(
                    SearchRequest.query(query).withTopK(2)));
                yield results;
            }
            default -> {
                // Search all stores
                List<Document> results = new ArrayList<>();
                results.addAll(documentStore.similaritySearch(
                    SearchRequest.query(query).withTopK(2)));
                results.addAll(codeStore.similaritySearch(
                    SearchRequest.query(query).withTopK(2)));
                results.addAll(faqStore.similaritySearch(
                    SearchRequest.query(query).withTopK(1)));
                yield results;
            }
        };
    }
}
```

### 5.4 Agentic RAG

The AI decides whether and how to retrieve, rather than always retrieving.

```java
@Service
public class AgenticRAGService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public String ask(String question) {
        // Step 1: Let the AI decide if retrieval is needed
        String plan = chatClient.prompt()
            .system("""
                You are a question-routing agent. Given a question, decide:
                1. "RETRIEVE" — if the question requires looking up specific information
                2. "DIRECT" — if you can answer from general knowledge
                3. "CLARIFY" — if the question is ambiguous and needs clarification
                4. "MULTI_RETRIEVE" — if multiple different searches are needed

                For RETRIEVE/MULTI_RETRIEVE, also specify the search queries.

                Respond in JSON:
                {"action": "RETRIEVE", "queries": ["search query 1", "search query 2"]}
                """)
            .user(question)
            .options(OpenAiChatOptions.builder()
                .responseFormat(new ResponseFormat(ResponseFormat.Type.JSON_OBJECT))
                .build())
            .call()
            .content();

        var actionPlan = objectMapper.readValue(plan, ActionPlan.class);

        return switch (actionPlan.action()) {
            case "DIRECT" -> chatClient.prompt()
                .user(question)
                .call()
                .content();

            case "RETRIEVE", "MULTI_RETRIEVE" -> {
                List<Document> allDocs = new ArrayList<>();
                for (String query : actionPlan.queries()) {
                    allDocs.addAll(vectorStore.similaritySearch(
                        SearchRequest.query(query).withTopK(3)
                    ));
                }
                yield generateWithContext(question, allDocs);
            }

            case "CLARIFY" -> "Could you please clarify: " + actionPlan.clarificationQuestion();
            default -> throw new IllegalStateException("Unknown action: " + actionPlan.action());
        };
    }
}
```

---

## 6. RAG Evaluation — How to Measure Quality

### 6.1 The RAGAS Framework

RAGAS (Retrieval Augmented Generation Assessment) provides automated metrics.

| Metric | What It Measures | Formula |
|--------|-----------------|---------|
| **Context Precision** | Are the retrieved docs relevant? | Relevant docs / Total retrieved docs |
| **Context Recall** | Did we find all relevant docs? | Retrieved relevant / Total relevant docs |
| **Faithfulness** | Is the answer grounded in the context? | Supported claims / Total claims in answer |
| **Answer Relevancy** | Does the answer address the question? | Semantic similarity between question and answer |
| **Answer Correctness** | Is the answer factually correct? | Comparison with ground truth |

### 6.2 Automated RAG Testing

```java
@SpringBootTest
public class RAGQualityTest {

    @Autowired
    private RAGService ragService;

    // Test dataset: question + expected answer + expected sources
    private static final List<RAGTestCase> TEST_CASES = List.of(
        new RAGTestCase(
            "How do I configure the database connection pool?",
            "Set spring.datasource.hikari.maximum-pool-size",
            List.of("configuration-guide.md")
        ),
        new RAGTestCase(
            "What is the rate limit for the API?",
            "1000 requests per minute per API key",
            List.of("api-reference.md")
        )
    );

    @Test
    void testRetrievalQuality() {
        double totalPrecision = 0;
        double totalRecall = 0;
        double totalFaithfulness = 0;

        for (RAGTestCase testCase : TEST_CASES) {
            RAGResponse response = ragService.askQuestion(testCase.question());

            // Context Precision: Are retrieved sources correct?
            long relevantSources = response.sources().stream()
                .filter(s -> testCase.expectedSources().contains(s.source()))
                .count();
            double precision = (double) relevantSources / response.sources().size();
            totalPrecision += precision;

            // Context Recall: Did we find all expected sources?
            double recall = (double) relevantSources / testCase.expectedSources().size();
            totalRecall += recall;

            // Faithfulness: Does the answer contain expected information?
            boolean containsExpected = response.answer()
                .toLowerCase()
                .contains(testCase.expectedKeyPhrase().toLowerCase());
            totalFaithfulness += containsExpected ? 1.0 : 0.0;
        }

        double avgPrecision = totalPrecision / TEST_CASES.size();
        double avgRecall = totalRecall / TEST_CASES.size();
        double avgFaithfulness = totalFaithfulness / TEST_CASES.size();

        log.info("Precision: {}, Recall: {}, Faithfulness: {}",
            avgPrecision, avgRecall, avgFaithfulness);

        assertThat(avgPrecision).isGreaterThan(0.7);
        assertThat(avgRecall).isGreaterThan(0.7);
        assertThat(avgFaithfulness).isGreaterThan(0.8);
    }

    record RAGTestCase(String question, String expectedKeyPhrase, List<String> expectedSources) {}
}
```

---

## 7. Production RAG Architecture

### 7.1 Complete Production Setup

```
                                    ┌──────────────────┐
                                    │   Angular UI      │
                                    │  (Chat Interface) │
                                    └────────┬─────────┘
                                             │ SSE/WebSocket
                                    ┌────────▼─────────┐
                                    │   API Gateway     │
                                    │  (Rate Limiting,  │
                                    │   Auth, Routing)  │
                                    └────────┬─────────┘
                                             │
                           ┌─────────────────┼─────────────────┐
                           │                 │                 │
                  ┌────────▼──────┐ ┌───────▼───────┐ ┌──────▼───────┐
                  │  RAG Service  │ │  Ingestion    │ │  Admin       │
                  │  (Query →     │ │  Service      │ │  Service     │
                  │   Answer)     │ │  (Doc → Vector│ │  (Config,    │
                  │               │ │   Pipeline)   │ │   Monitor)   │
                  └──┬────┬──┬───┘ └──────┬────────┘ └──────────────┘
                     │    │  │            │
              ┌──────┘    │  └──────┐     │
              │           │         │     │
    ┌─────────▼──┐  ┌────▼────┐ ┌──▼─────▼──┐
    │  LLM API   │  │ Redis   │ │ PostgreSQL │
    │  (Open05_AI/  │  │ (Cache, │ │ + pgvector │
    │  Claude)   │  │  Sessions│ │ (Vector DB)│
    └────────────┘  └─────────┘ └────────────┘
```

### 7.2 Caching Strategy for RAG

```java
@Service
public class CachedRAGService {

    private final RAGService ragService;
    private final VectorStore cacheStore;     // Separate vector store for cache
    private final RedisTemplate<String, String> redis;

    public String ask(String question) {
        // Level 1: Exact cache (Redis)
        String cacheKey = "rag:" + DigestUtils.sha256Hex(question.toLowerCase().trim());
        String cached = redis.opsForValue().get(cacheKey);
        if (cached != null) {
            log.info("Exact cache hit");
            return cached;
        }

        // Level 2: Semantic cache (similar questions)
        List<Document> similar = cacheStore.similaritySearch(
            SearchRequest.query(question)
                .withTopK(1)
                .withSimilarityThreshold(0.95) // Very high threshold for cache hits
        );
        if (!similar.isEmpty()) {
            log.info("Semantic cache hit (similarity: {})", similar.getFirst().getMetadata().get("score"));
            String answer = similar.getFirst().getMetadata().get("answer").toString();
            redis.opsForValue().set(cacheKey, answer, Duration.ofHours(24));
            return answer;
        }

        // Level 3: Cache miss — full RAG pipeline
        log.info("Cache miss — running full RAG pipeline");
        String answer = ragService.askQuestion(question).answer();

        // Store in both caches
        redis.opsForValue().set(cacheKey, answer, Duration.ofHours(24));
        cacheStore.add(List.of(new Document(question, Map.of("answer", answer))));

        return answer;
    }
}
```

---

## 8. RAG Anti-Patterns and How to Fix Them

| Anti-Pattern | Symptom | Fix |
|-------------|---------|-----|
| **Chunks too large** | Irrelevant information in context, confused answers | Reduce chunk size to 300-500 tokens |
| **Chunks too small** | Missing context, incomplete answers | Increase chunk size or use parent-child chunking |
| **No overlap** | Context lost at chunk boundaries | Add 10-20% overlap between chunks |
| **Wrong embedding model** | Poor retrieval quality | Use task-specific embeddings (e.g., Cohere for search) |
| **No metadata filtering** | Irrelevant results from wrong documents | Add source/category metadata and filter |
| **Always retrieving** | Slow, expensive for simple questions | Use agentic RAG — let AI decide when to retrieve |
| **No re-ranking** | Top results are not the most relevant | Add a re-ranking step after initial retrieval |
| **Stuffing context** | Token waste, "lost in the middle" problem | Compress context, limit to 3-5 most relevant chunks |
| **No evaluation** | Can't tell if RAG is improving | Implement RAGAS metrics and regression tests |
| **Ignoring hybrid search** | Missing exact keyword matches | Combine vector search with BM25 keyword search |

---

**Next**: [04_Vector_Databases_Embeddings.md](04_Vector_Databases_Embeddings.md) — Deep dive into vector databases, the storage layer that makes RAG possible.

