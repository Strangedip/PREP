# Spring AI — The Complete Framework Guide for Java Developers

> **Goal**: Master Spring AI, the official Spring framework for building AI-powered applications with Java and Spring Boot.
> **Level**: Mid-Level through Lead

---

## 1. What is Spring AI?

Spring AI is the official Spring project that brings AI capabilities into the Spring ecosystem. It provides:

- **Unified API** across LLM providers (OpenAI, Anthropic, Google, Ollama, Mistral, and more)
- **ChatClient** — A fluent API for LLM interactions (similar to WebClient for HTTP)
- **Embedding Models** — Generate vector embeddings from text
- **Vector Stores** — Store and search embeddings (pgvector, Pinecone, Milvus, Redis, Chroma, Weaviate, Neo4j, Elasticsearch)
- **Function Calling** — Connect LLMs to your Java methods
- **RAG** — Built-in retrieval-augmented generation support via Advisors
- **Structured Output** — Map LLM responses directly to Java records/classes
- **Streaming** — Token-by-token streaming for real-time UIs
- **Observability** — Built-in Micrometer metrics and tracing
- **Document Processing** — PDF, Markdown, HTML, JSON document readers
- **Multi-modal** — Process images, audio with supported models

### 1.1 Spring AI Architecture

```
┌───────────────────────────────────────────────────────┐
│                 Your Application                      │
│  ┌──────────┐  ┌──────────────┐  ┌────────────────┐  │
│  │ REST API │  │ Service Layer│  │ Scheduled Jobs │  │
│  └────┬─────┘  └──────┬───────┘  └───────┬────────┘  │
│       │               │                  │            │
│  ┌────▼───────────────▼──────────────────▼──────────┐│
│  │              Spring AI Abstractions              ││
│  │ ┌────────────┐ ┌───────────┐ ┌────────────────┐ ││
│  │ │ ChatClient │ │ Embedding │ │ Vector Store   │ ││
│  │ │            │ │ Model     │ │                │ ││
│  │ └─────┬──────┘ └─────┬─────┘ └──────┬─────────┘ ││
│  │       │              │              │            ││
│  │ ┌─────▼──────────────▼──────────────▼──────────┐ ││
│  │ │              Advisors (Middleware)            │ ││
│  │ │  Logging │ RAG │ Memory │ Guardrails │ Cache │ ││
│  │ └─────────────────────────────────────────────┘ ││
│  └──────────────────────┬────────────────────────── ┘│
│                         │                            │
│  ┌──────────────────────▼───────────────────────────┐│
│  │          Provider Implementations                ││
│  │ ┌────────┐ ┌─────────┐ ┌──────┐ ┌─────────────┐││
│  │ │ OpenAI │ │Anthropic│ │Ollama│ │ Google/Gemini│││
│  │ └────────┘ └─────────┘ └──────┘ └─────────────┘││
│  └──────────────────────────────────────────────────┘│
└───────────────────────────────────────────────────────┘
```

### 1.2 Dependencies

```xml
<!-- Spring AI BOM -->
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

<!-- Core: OpenAI (ChatGPT, DALL-E, Whisper, Embeddings) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
</dependency>

<!-- Or: Anthropic (Claude) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-anthropic-spring-boot-starter</artifactId>
</dependency>

<!-- Or: Ollama (Local models — LLaMA, Mistral, etc.) -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
</dependency>

<!-- Vector Store: pgvector -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pgvector-store-spring-boot-starter</artifactId>
</dependency>

<!-- Document Readers -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-pdf-document-reader</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-markdown-document-reader</artifactId>
</dependency>
```

---

## 2. ChatClient — The Core API

### 2.1 Basic Usage

```java
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("You are a helpful assistant for a Java development team.")
            .build();
    }

    // Simple text response
    @PostMapping("/ask")
    public String ask(@RequestBody String question) {
        return chatClient.prompt()
            .user(question)
            .call()
            .content();
    }

    // Full response with metadata (tokens used, model, etc.)
    @PostMapping("/ask/detailed")
    public ChatResponse askDetailed(@RequestBody String question) {
        return chatClient.prompt()
            .user(question)
            .call()
            .chatResponse();
    }

    // Streaming response (Server-Sent Events)
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String question) {
        return chatClient.prompt()
            .user(question)
            .stream()
            .content();
    }
}
```

### 2.2 Structured Output (Type-Safe Responses)

```java
// Define your response type as a Java record
public record TechnicalAssessment(
    String technology,
    String assessment,         // "Recommended", "Consider Alternatives", "Avoid"
    List<String> pros,
    List<String> cons,
    List<String> alternatives,
    int maturityScore,         // 1-10
    String recommendation
) {}

@Service
public class TechAssessmentService {

    private final ChatClient chatClient;

    public TechnicalAssessment assess(String technology) {
        return chatClient.prompt()
            .system("""
                You are a senior software architect. Assess the given technology
                for enterprise Java applications. Be objective and thorough.
                """)
            .user("Assess this technology for our enterprise Java application: " + technology)
            .call()
            .entity(TechnicalAssessment.class);
        // Spring AI automatically:
        // 1. Generates a JSON schema from TechnicalAssessment record
        // 2. Includes it in the prompt
        // 3. Parses the LLM's JSON response into the record
    }

    // List of entities
    public List<TechnicalAssessment> assessMultiple(List<String> technologies) {
        return chatClient.prompt()
            .user("Assess these technologies: " + String.join(", ", technologies))
            .call()
            .entity(new ParameterizedTypeReference<List<TechnicalAssessment>>() {});
    }
}
```

### 2.3 Multi-Modal (Images)

```java
@Service
public class ImageAnalysisService {

    private final ChatClient chatClient;

    // Analyze an image
    public String analyzeImage(byte[] imageBytes) {
        return chatClient.prompt()
            .user(u -> u.text("Describe this image in detail. "
                            + "Identify any text, objects, and their relationships.")
                        .media(MimeTypeUtils.IMAGE_PNG, new ByteArrayResource(imageBytes)))
            .call()
            .content();
    }

    // Analyze a diagram/architecture image for code review
    public ArchitectureReview reviewArchitectureDiagram(Resource diagramImage) {
        return chatClient.prompt()
            .system("""
                You are a software architect reviewing system design diagrams.
                Identify components, data flows, potential bottlenecks, and missing elements.
                """)
            .user(u -> u.text("Review this architecture diagram.")
                        .media(MimeTypeUtils.IMAGE_PNG, diagramImage))
            .call()
            .entity(ArchitectureReview.class);
    }
}
```

---

## 3. Function Calling — Connecting AI to Your Code

Function calling allows the LLM to request execution of your Java methods. The LLM does not call the function directly — it tells Spring AI which function to call with which arguments, Spring AI executes it, and sends the result back to the LLM.

### 3.1 How Function Calling Works

```
User: "What is the current stock price of AAPL?"
    │
    ▼
LLM: "I need to call getStockPrice(symbol: 'AAPL')"
    │
    ▼ (Spring AI intercepts this)
Spring AI: Calls your Java method getStockPrice("AAPL")
    │
    ▼
Your method returns: { "symbol": "AAPL", "price": 178.52, "change": "+1.23%" }
    │
    ▼ (Spring AI sends result back to LLM)
LLM: "The current stock price of Apple (AAPL) is $178.52, up 1.23% today."
    │
    ▼
User sees the final answer
```

### 3.2 Implementing Functions

```java
// Step 1: Define the function as a bean
@Configuration
public class AIFunctionConfig {

    @Bean
    @Description("Get current weather for a city. Returns temperature, humidity, and conditions.")
    public Function<WeatherRequest, WeatherResponse> getWeather(WeatherService weatherService) {
        return request -> weatherService.getCurrentWeather(request.city(), request.country());
    }

    @Bean
    @Description("Search the product catalog. Returns matching products with prices.")
    public Function<ProductSearchRequest, List<ProductDTO>> searchProducts(
            ProductService productService) {
        return request -> productService.search(request.query(), request.category(), request.maxPrice());
    }

    @Bean
    @Description("Create a support ticket for the customer. Returns the ticket ID.")
    public Function<CreateTicketRequest, TicketResponse> createSupportTicket(
            TicketService ticketService) {
        return request -> ticketService.createTicket(
            request.customerId(), request.subject(), request.description(), request.priority()
        );
    }

    // Request/Response records
    public record WeatherRequest(String city, String country) {}
    public record WeatherResponse(double temperature, int humidity, String conditions) {}
    public record ProductSearchRequest(String query, String category, Double maxPrice) {}
    public record CreateTicketRequest(
        String customerId, String subject, String description, String priority
    ) {}
    public record TicketResponse(String ticketId, String status) {}
}

// Step 2: Use the functions in ChatClient
@Service
public class AIAssistantService {

    private final ChatClient chatClient;

    public AIAssistantService(ChatClient.Builder builder) {
        this.chatClient = builder
            .defaultSystem("""
                You are a customer service assistant for an e-commerce platform.
                You can look up weather, search products, and create support tickets.
                Always confirm with the user before creating tickets.
                """)
            .defaultFunctions("getWeather", "searchProducts", "createSupportTicket")
            .build();
    }

    public String chat(String userMessage) {
        return chatClient.prompt()
            .user(userMessage)
            .call()
            .content();
    }
}
```

### 3.3 Dynamic Function Registration

```java
@Service
public class DynamicFunctionService {

    private final ChatClient.Builder chatClientBuilder;

    // Register functions dynamically per request
    public String chatWithTools(String userMessage, Set<String> allowedTools) {
        var builder = chatClientBuilder.build().prompt()
            .user(userMessage);

        // Only expose tools the user is authorized to use
        if (allowedTools.contains("database_query")) {
            builder.function("queryDatabase", "Execute a read-only SQL query",
                (QueryRequest req) -> jdbcTemplate.queryForList(req.sql()));
        }

        if (allowedTools.contains("send_email")) {
            builder.function("sendEmail", "Send an email to a customer",
                (EmailRequest req) -> emailService.send(req.to(), req.subject(), req.body()));
        }

        return builder.call().content();
    }
}
```

---

## 4. Advisors — Middleware for AI Requests

Advisors intercept and transform requests/responses in the ChatClient pipeline.

### 4.1 Built-in Advisors

```java
@Service
public class AdvisorDemoService {

    private final ChatClient chatClient;

    public AdvisorDemoService(ChatClient.Builder builder, VectorStore vectorStore) {
        this.chatClient = builder
            .defaultSystem("You are a helpful assistant.")

            // RAG Advisor — automatically retrieves and adds context
            .defaultAdvisors(
                new QuestionAnswerAdvisor(vectorStore,
                    SearchRequest.defaults()
                        .withTopK(5)
                        .withSimilarityThreshold(0.7)),

                // Message Chat Memory Advisor — maintains conversation history
                new MessageChatMemoryAdvisor(
                    new InMemoryChatMemory(),   // or RedisChatMemory, JdbcChatMemory
                    "default-user",
                    10  // Keep last 10 messages
                ),

                // Logging Advisor — logs prompts and responses
                new SimpleLoggerAdvisor()
            )
            .build();
    }
}
```

### 4.2 Custom Advisors

```java
// Custom advisor: Add guardrails to every request
public class GuardrailAdvisor implements RequestResponseAdvisor {

    private final ContentModerationService moderationService;

    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
        // Input guardrail: Check for prompt injection
        String userMessage = request.userText();
        if (moderationService.isPotentialInjection(userMessage)) {
            throw new PromptInjectionException("Potential prompt injection detected");
        }

        // Input guardrail: Redact PII
        String sanitized = moderationService.redactPII(userMessage);

        return AdvisedRequest.from(request)
            .withUserText(sanitized)
            .build();
    }

    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> context) {
        // Output guardrail: Check response content
        String content = response.getResult().getOutput().getContent();
        if (moderationService.containsSensitiveInfo(content)) {
            // Replace with safe version
            String safe = moderationService.sanitize(content);
            return ChatResponse.builder()
                .withGenerations(List.of(new Generation(new AssistantMessage(safe))))
                .build();
        }
        return response;
    }

    @Override
    public String getName() {
        return "GuardrailAdvisor";
    }

    @Override
    public int getOrder() {
        return 0; // Run first
    }
}

// Custom advisor: Semantic caching
public class SemanticCacheAdvisor implements RequestResponseAdvisor {

    private final VectorStore cacheStore;
    private static final double CACHE_THRESHOLD = 0.95;

    @Override
    public AdvisedRequest adviseRequest(AdvisedRequest request, Map<String, Object> context) {
        String query = request.userText();

        // Check cache
        List<Document> cached = cacheStore.similaritySearch(
            SearchRequest.query(query).withTopK(1).withSimilarityThreshold(CACHE_THRESHOLD)
        );

        if (!cached.isEmpty()) {
            // Cache hit — store the cached answer to bypass LLM
            context.put("cachedAnswer", cached.getFirst().getMetadata().get("answer"));
        }

        return request;
    }

    @Override
    public ChatResponse adviseResponse(ChatResponse response, Map<String, Object> context) {
        if (context.containsKey("cachedAnswer")) {
            // Return cached response instead
            String cachedAnswer = (String) context.get("cachedAnswer");
            return ChatResponse.builder()
                .withGenerations(List.of(new Generation(new AssistantMessage(cachedAnswer))))
                .build();
        }

        // Cache the new response
        String question = (String) context.get("userText");
        String answer = response.getResult().getOutput().getContent();
        cacheStore.add(List.of(new Document(question, Map.of("answer", answer))));

        return response;
    }
}
```

---

## 5. Streaming — Real-Time AI Responses

### 5.1 Server-Sent Events (SSE) for Angular

```java
@RestController
@RequestMapping("/api/ai")
public class StreamingController {

    private final ChatClient chatClient;

    // SSE endpoint for streaming
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestParam String message) {
        return chatClient.prompt()
            .user(message)
            .stream()
            .content()
            .map(token -> ServerSentEvent.<String>builder()
                .data(token)
                .build())
            .concatWith(Flux.just(
                ServerSentEvent.<String>builder()
                    .event("complete")
                    .data("[DONE]")
                    .build()
            ));
    }

    // Structured streaming (parse as objects come in)
    @GetMapping(value = "/analysis/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamAnalysis(@RequestParam String code) {
        return chatClient.prompt()
            .system("Analyze this code and describe each issue you find, one at a time.")
            .user(code)
            .stream()
            .content()
            .map(chunk -> ServerSentEvent.<String>builder()
                .data(chunk)
                .build());
    }
}
```

---

## 6. Observability & Monitoring

### 6.1 Built-in Metrics

```yaml
# application.yml
spring:
  ai:
    chat:
      observations:
        include-input: true    # Log input prompts (disable in production for PII)
        include-output: true   # Log output responses
management:
  metrics:
    tags:
      application: my-ai-app
  tracing:
    sampling:
      probability: 1.0  # Sample all requests
```

```java
@Configuration
public class AIObservabilityConfig {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCustomizer() {
        return registry -> {
            // Spring AI automatically registers these metrics:
            // spring.ai.chat.client.call (timer) — total chat call duration
            // spring.ai.chat.client.tokens.input (counter) — input tokens used
            // spring.ai.chat.client.tokens.output (counter) — output tokens used
            // spring.ai.chat.client.tokens.total (counter) — total tokens used
            // spring.ai.embedding.tokens (counter) — embedding tokens used
            // spring.ai.vectorstore.add (timer) — vector store add duration
            // spring.ai.vectorstore.query (timer) — vector store query duration
        };
    }
}
```

### 6.2 Custom Cost Tracking

```java
@Component
public class AICostDashboard {

    private final MeterRegistry registry;

    public void recordUsage(String model, String feature, int inputTokens, int outputTokens) {
        double cost = calculateCost(model, inputTokens, outputTokens);

        registry.counter("ai.cost.usd",
            "model", model,
            "feature", feature
        ).increment(cost);

        registry.counter("ai.tokens.total",
            "model", model,
            "type", "input"
        ).increment(inputTokens);

        registry.counter("ai.tokens.total",
            "model", model,
            "type", "output"
        ).increment(outputTokens);

        registry.timer("ai.request.duration", "model", model)
            .record(Duration.ofMillis(latencyMs));
    }
}
```

---

## 7. Testing Spring AI Applications

```java
@SpringBootTest
class RAGServiceTest {

    @Autowired
    private RAGService ragService;

    @MockBean
    private ChatClient.Builder chatClientBuilder;

    @MockBean
    private VectorStore vectorStore;

    @Test
    void shouldReturnAnswerFromContext() {
        // Mock vector store to return known documents
        when(vectorStore.similaritySearch(any())).thenReturn(List.of(
            new Document("Spring Boot supports virtual threads with spring.threads.virtual.enabled=true",
                Map.of("source", "spring-docs"))
        ));

        // Mock the chat client
        ChatClient mockClient = mock(ChatClient.class);
        when(chatClientBuilder.build()).thenReturn(mockClient);
        // ... setup prompt mock chain ...

        String answer = ragService.ask("How do I enable virtual threads?");
        assertThat(answer).containsIgnoringCase("virtual threads");
    }

    @Test
    void shouldHandleEmptyRetrieval() {
        when(vectorStore.similaritySearch(any())).thenReturn(List.of());

        String answer = ragService.ask("What is the quantum flux capacitor API?");
        assertThat(answer).containsIgnoringCase("don't have information");
    }
}

// Integration test with Testcontainers
@SpringBootTest
@Testcontainers
class PgVectorIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
        .withDatabaseName("testdb");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private VectorStore vectorStore;

    @Test
    void shouldStoreAndRetrieveDocuments() {
        // Add documents
        vectorStore.add(List.of(
            new Document("Spring Boot is a Java framework", Map.of("source", "docs")),
            new Document("Angular is a TypeScript framework", Map.of("source", "docs"))
        ));

        // Search
        List<Document> results = vectorStore.similaritySearch(
            SearchRequest.query("Java web framework").withTopK(1)
        );

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getContent()).contains("Spring Boot");
    }
}
```

---

## 8. Production Configuration

```yaml
# application-production.yml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}  # From Kubernetes secret
      chat:
        options:
          model: gpt-4o-mini       # Cost-effective for most requests
          temperature: 0.3         # Low for consistency
          max-tokens: 2000         # Limit output cost
      embedding:
        options:
          model: text-embedding-3-small
    retry:
      max-attempts: 3
      backoff:
        initial-interval: 1000
        multiplier: 2
        max-interval: 10000
    vectorstore:
      pgvector:
        dimensions: 1536
        distance-type: COSINE_DISTANCE
        index-type: HNSW
        remove-existing-vector-store-table: false  # NEVER true in production
    chat:
      observations:
        include-input: false    # Don't log prompts in production (PII risk)
        include-output: false
```

---

## 9. Spring AI vs. LangChain4j — Comparison

| Feature | Spring AI | LangChain4j |
|---------|-----------|-------------|
| **Ecosystem** | Official Spring project | Community project |
| **Spring Boot integration** | Native, auto-configuration | Manual configuration |
| **API style** | Fluent Builder (like WebClient) | Chain-based (like LangChain Python) |
| **Model support** | OpenAI, Anthropic, Ollama, Google, Mistral, more | Similar breadth |
| **Vector stores** | 10+ integrations | 10+ integrations |
| **Function calling** | Spring Bean-based | Interface-based |
| **RAG** | Advisor-based pipeline | Chain-based pipeline |
| **Memory** | ChatMemory abstraction | ChatMemory abstraction |
| **Observability** | Micrometer native | Manual |
| **Maturity** | 1.0 GA (stable) | Pre-1.0 |
| **Recommendation** | ✅ Use for Spring Boot apps | Consider for non-Spring Java apps |

---

**Next**: [06_AI_Agents_and_Workflows.md](06_AI_Agents_and_Workflows.md) — Build AI systems that can reason, plan, and take actions autonomously.

