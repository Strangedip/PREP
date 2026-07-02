# MLOps & AI in Production — Deploying, Monitoring, and Scaling AI Features

> **Goal**: Understand how to take AI features from prototype to production, including deployment, monitoring, cost control, and scaling strategies.
> **Level**: Senior through Lead — This is what separates demo projects from production systems.

> **You are here**: Senior SDE — AI / ML
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [05_Spring_AI.md](05_Spring_AI.md) | **Next**: [10_AI_System_Design.md](10_AI_System_Design.md)

---

## 1. Why Production AI is Hard

| Challenge | What It Means | Why It's Different from Regular Software |
|-----------|---------------|----------------------------------------|
| **Non-deterministic output** | Same input can produce different output | Can't use traditional equality-based testing |
| **Latency** | LLM calls take 1-30 seconds | Users expect <500ms for most web apps |
| **Cost** | Every API call costs money (tokens) | Cost scales with usage, not just infrastructure |
| **Quality drift** | Model updates can change output quality | Provider updates models without notice |
| **Hallucination** | Confident but wrong answers | Must validate output before showing to users |
| **Privacy** | Data sent to external APIs | PII in prompts = compliance risk |
| **Availability** | Dependent on external API uptime | OpenAI outages affect your application |

---

## 2. Architecture for Production AI

### 2.1 The Production AI Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    PRODUCTION AI SYSTEM                      │
│                                                             │
│  ┌────────┐    ┌──────────────────────────────────┐         │
│  │ Client │───►│         API Gateway              │         │
│  │(Angular│    │  ○ Rate limiting per user/tenant  │         │
│  │  App)  │    │  ○ Auth (JWT/OAuth2)              │         │
│  └────────┘    │  ○ Request routing                │         │
│                └──────────┬───────────────────────┘         │
│                           │                                 │
│            ┌──────────────▼──────────────┐                  │
│            │       AI Service Layer       │                  │
│            │                             │                  │
│            │ ┌─────────────────────────┐ │                  │
│            │ │     Input Pipeline      │ │                  │
│            │ │ ○ PII Redaction         │ │                  │
│            │ │ ○ Prompt Injection Check│ │                  │
│            │ │ ○ Content Moderation    │ │                  │
│            │ │ ○ Token Estimation      │ │                  │
│            │ └───────────┬─────────────┘ │                  │
│            │             │               │                  │
│            │ ┌───────────▼─────────────┐ │                  │
│            │ │    Semantic Cache       │◄├──── Redis        │
│            │ │ (Check before LLM call) │ │                  │
│            │ └───────────┬─────────────┘ │                  │
│            │             │               │                  │
│            │ ┌───────────▼─────────────┐ │     ┌──────────┐│
│            │ │    LLM Router          │◄├────►│ pgvector ││
│            │ │ ○ Model selection      │ │     │(Vector DB)││
│            │ │ ○ Fallback chain       │ │     └──────────┘│
│            │ │ ○ Load balancing       │ │                  │
│            │ └───────────┬─────────────┘ │                  │
│            │             │               │                  │
│            │    ┌────────┼────────┐      │                  │
│            │    │        │        │      │                  │
│            │ ┌──▼──┐ ┌──▼──┐ ┌──▼──┐   │                  │
│            │ │OpenAI│ │Claude│ │Ollama│   │                  │
│            │ │(main)│ │(back)│ │(local│   │                  │
│            │ │      │ │  up) │ │ )    │   │                  │
│            │ └──────┘ └──────┘ └──────┘   │                  │
│            │             │               │                  │
│            │ ┌───────────▼─────────────┐ │                  │
│            │ │   Output Pipeline       │ │                  │
│            │ │ ○ Output Validation     │ │                  │
│            │ │ ○ PII Check            │ │                  │
│            │ │ ○ Toxicity Filter      │ │                  │
│            │ │ ○ Response Caching     │ │                  │
│            │ └─────────────────────────┘ │                  │
│            └──────────────┬──────────────┘                  │
│                           │                                 │
│            ┌──────────────▼──────────────┐                  │
│            │     Observability Layer      │                  │
│            │ ○ Prometheus (metrics)       │                  │
│            │ ○ Grafana (dashboards)       │                  │
│            │ ○ OpenTelemetry (traces)     │                  │
│            │ ○ ELK/Loki (logs)           │                  │
│            └─────────────────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
```

### 2.2 LLM Router with Fallback

```java
@Service
public class LLMRouter {

    private final ChatClient openAiClient;
    private final ChatClient anthropicClient;
    private final ChatClient ollamaClient; // Local fallback

    private final CircuitBreaker openAiBreaker;
    private final CircuitBreaker anthropicBreaker;

    public String call(String prompt, RoutingOptions options) {
        // Strategy 1: Route by complexity/cost
        ChatClient primary = switch (options.tier()) {
            case FAST_CHEAP -> openAiClient;      // GPT-4o-mini
            case BALANCED -> openAiClient;          // GPT-4o
            case HIGH_QUALITY -> anthropicClient;   // Claude 3.5 Sonnet
            case LOCAL_ONLY -> ollamaClient;        // LLaMA 3.1 (on-prem)
        };

        // Strategy 2: Circuit breaker with fallback chain
        try {
            return openAiBreaker.executeSupplier(() ->
                primary.prompt().user(prompt).call().content()
            );
        } catch (Exception e) {
            log.warn("Primary LLM failed ({}), trying fallback", e.getMessage());
            try {
                return anthropicBreaker.executeSupplier(() ->
                    anthropicClient.prompt().user(prompt).call().content()
                );
            } catch (Exception e2) {
                log.warn("Secondary LLM failed ({}), using local model", e2.getMessage());
                return ollamaClient.prompt().user(prompt).call().content();
            }
        }
    }

    public record RoutingOptions(
        Tier tier,
        int maxTokens,
        double maxCostUsd,
        Duration timeout
    ) {}

    public enum Tier { FAST_CHEAP, BALANCED, HIGH_QUALITY, LOCAL_ONLY }
}
```

---

## 3. Cost Management

### 3.1 Cost Tracking Dashboard

```java
@Service
public class AICostService {

    private final MeterRegistry registry;
    private final AtomicReference<Double> dailySpend = new AtomicReference<>(0.0);
    private static final double DAILY_BUDGET = 500.0; // $500/day budget

    // Track every LLM call
    public void trackCost(LLMCallMetrics metrics) {
        double cost = calculateCost(metrics);

        // Prometheus metrics
        registry.counter("ai.cost.total_usd",
            "model", metrics.model(),
            "feature", metrics.feature(),
            "tenant", metrics.tenantId()
        ).increment(cost);

        registry.counter("ai.tokens.input",
            "model", metrics.model()
        ).increment(metrics.inputTokens());

        registry.counter("ai.tokens.output",
            "model", metrics.model()
        ).increment(metrics.outputTokens());

        registry.timer("ai.latency",
            "model", metrics.model(),
            "feature", metrics.feature()
        ).record(Duration.ofMillis(metrics.latencyMs()));

        // Budget enforcement
        double newDaily = dailySpend.accumulateAndGet(cost, Double::sum);
        if (newDaily > DAILY_BUDGET) {
            alertService.sendAlert(
                "AI daily budget exceeded: $%.2f / $%.2f".formatted(newDaily, DAILY_BUDGET)
            );
        }

        if (newDaily > DAILY_BUDGET * 0.8) {
            // Start routing to cheaper models
            routingService.enableCostSavingMode();
        }
    }

    private double calculateCost(LLMCallMetrics m) {
        return switch (m.model()) {
            case "gpt-4o" -> (m.inputTokens() * 2.50 + m.outputTokens() * 10.00) / 1_000_000;
            case "gpt-4o-mini" -> (m.inputTokens() * 0.15 + m.outputTokens() * 0.60) / 1_000_000;
            case "claude-3-5-sonnet" -> (m.inputTokens() * 3.00 + m.outputTokens() * 15.00) / 1_000_000;
            case "claude-3-5-haiku" -> (m.inputTokens() * 0.25 + m.outputTokens() * 1.25) / 1_000_000;
            case "local-llama" -> 0.0; // Self-hosted, no per-token cost
            default -> 0.0;
        };
    }

    public record LLMCallMetrics(
        String model, String feature, String tenantId,
        int inputTokens, int outputTokens, long latencyMs
    ) {}
}
```

### 3.2 Cost Optimization Strategies

| Strategy | Savings | Implementation |
|----------|---------|---------------|
| **Model tiering** | 50-90% | Use GPT-4o-mini for simple tasks, GPT-4o for complex |
| **Semantic caching** | 30-60% | Cache similar queries (>0.95 similarity) |
| **Prompt compression** | 10-30% | Summarize context before sending to LLM |
| **Max tokens limit** | 10-40% | Limit output token count per request |
| **Batch processing** | 20-50% | Process async requests in batches (OpenAI Batch API: 50% discount) |
| **Local model fallback** | 60-100% | Use Ollama/LLaMA for simple tasks (no API cost) |
| **Request deduplication** | 10-20% | Detect and merge identical concurrent requests |
| **Reduce context window** | 20-40% | Send fewer, more relevant chunks in RAG |

---

## 4. AI Evaluation and Quality Monitoring

### 4.1 Evaluation Metrics

| Metric | What It Measures | How to Calculate |
|--------|-----------------|------------------|
| **Faithfulness** | Is the answer grounded in provided context? | LLM-as-judge: "Does the answer only use info from the context?" |
| **Relevancy** | Does the answer address the question? | Cosine similarity between question and answer embeddings |
| **Correctness** | Is the answer factually correct? | Comparison with ground truth dataset |
| **Toxicity** | Is the output harmful or offensive? | OpenAI moderation API or Perspective API |
| **Latency (P50/P95/P99)** | How fast is the response? | Standard latency metrics |
| **User satisfaction** | Do users like the responses? | Thumbs up/down ratio from UI |
| **Hallucination rate** | How often does the AI make things up? | Periodic human review + automated checking |

### 4.2 LLM-as-Judge Evaluation

```java
@Service
public class LLMEvaluationService {

    private final ChatClient evaluator; // Separate model for evaluation

    // Evaluate faithfulness (is the answer grounded in context?)
    public EvaluationResult evaluateFaithfulness(String question, String context, String answer) {
        return evaluator.prompt()
            .system("""
                You are an evaluation judge. Given a question, context, and answer,
                determine if the answer is faithful to the context.

                Score 1-5:
                1 = Completely unfaithful (makes up information not in context)
                2 = Mostly unfaithful
                3 = Partially faithful
                4 = Mostly faithful
                5 = Completely faithful (every claim is supported by context)

                Also identify any unsupported claims.
                """)
            .user("""
                Question: %s
                Context: %s
                Answer: %s
                """.formatted(question, context, answer))
            .call()
            .entity(EvaluationResult.class);
    }

    // Evaluate answer relevancy
    public EvaluationResult evaluateRelevancy(String question, String answer) {
        return evaluator.prompt()
            .system("""
                Score how well the answer addresses the question (1-5):
                1 = Completely irrelevant
                3 = Partially addresses the question
                5 = Fully and directly addresses the question
                """)
            .user("Question: %s\nAnswer: %s".formatted(question, answer))
            .call()
            .entity(EvaluationResult.class);
    }

    public record EvaluationResult(
        int score,
        String justification,
        List<String> issues
    ) {}
}
```

### 4.3 Automated Quality Regression Testing

```java
@SpringBootTest
@Tag("ai-quality")
class AIQualityRegressionTest {

    @Autowired private RAGService ragService;

    // Golden dataset — manually curated question/answer pairs
    private static final List<GoldenTestCase> GOLDEN_DATASET = List.of(
        new GoldenTestCase(
            "How do I configure SSL in Spring Boot?",
            List.of("server.ssl.key-store", "PKCS12", "JKS"),  // Must mention
            List.of("Python", "Django"),                         // Must NOT mention
            0.7                                                  // Minimum score
        ),
        new GoldenTestCase(
            "What is the default thread pool size in Tomcat?",
            List.of("200", "maxThreads", "server.tomcat"),
            List.of("Jetty", "Undertow"),
            0.8
        )
        // ... 50+ test cases for comprehensive coverage
    );

    @Test
    void qualityRegressionTest() {
        List<TestResult> results = new ArrayList<>();

        for (GoldenTestCase testCase : GOLDEN_DATASET) {
            String answer = ragService.ask(testCase.question());

            // Check required terms are present
            long requiredHits = testCase.mustContain().stream()
                .filter(term -> answer.toLowerCase().contains(term.toLowerCase()))
                .count();
            double containsScore = (double) requiredHits / testCase.mustContain().size();

            // Check forbidden terms are absent
            long forbiddenHits = testCase.mustNotContain().stream()
                .filter(term -> answer.toLowerCase().contains(term.toLowerCase()))
                .count();
            boolean noForbidden = forbiddenHits == 0;

            results.add(new TestResult(testCase.question(), containsScore, noForbidden));
        }

        // Aggregate results
        double avgScore = results.stream()
            .mapToDouble(TestResult::containsScore)
            .average()
            .orElse(0);

        long passedTests = results.stream()
            .filter(r -> r.containsScore() >= 0.7 && r.noForbidden())
            .count();

        log.info("AI Quality Score: {:.2f}, Passed: {}/{}", avgScore, passedTests, results.size());

        // Assert minimum quality
        assertThat(avgScore).isGreaterThan(0.7);
        assertThat((double) passedTests / results.size()).isGreaterThan(0.85);
    }

    record GoldenTestCase(String question, List<String> mustContain,
                          List<String> mustNotContain, double minScore) {}
    record TestResult(String question, double containsScore, boolean noForbidden) {}
}
```

---

## 5. A/B Testing AI Features

```java
@Service
public class AIExperimentService {

    private final Map<String, Experiment> activeExperiments;

    public String processWithExperiment(String experimentId, String input) {
        Experiment experiment = activeExperiments.get(experimentId);
        if (experiment == null) return processDefault(input);

        // Assign user to variant
        String variant = experiment.assignVariant(getCurrentUserId());

        // Execute variant
        String result = switch (variant) {
            case "control" -> processWithModel(input, "gpt-4o-mini", experiment.controlPrompt());
            case "treatment_a" -> processWithModel(input, "gpt-4o-mini", experiment.treatmentAPrompt());
            case "treatment_b" -> processWithModel(input, "gpt-4o", experiment.treatmentBPrompt());
            default -> processDefault(input);
        };

        // Track metrics for analysis
        experimentTracker.trackExposure(experimentId, variant, getCurrentUserId());

        return result;
    }

    // Metrics to compare variants:
    // - User satisfaction (thumbs up/down)
    // - Task completion rate
    // - Response latency
    // - Cost per request
    // - Hallucination rate (from evaluation pipeline)
}
```

---

## 6. Scaling AI Features

### 6.1 Scaling Strategies

| Strategy | What It Solves | Implementation |
|----------|---------------|---------------|
| **Async processing** | Long LLM response times | Queue requests with Kafka, return webhook/poll |
| **Request batching** | API rate limits and cost | OpenAI Batch API (50% cost reduction, 24h SLA) |
| **Horizontal scaling** | Throughput | Multiple AI service instances behind LB |
| **Semantic caching** | Repeated similar queries | Vector-based cache with Redis or pgvector |
| **Edge/local models** | Latency and privacy | Ollama on K8s for simple tasks |
| **CDN for static AI** | Pre-computed AI content | Cache AI-generated product descriptions, FAQs |
| **Rate limiting per tenant** | Fair usage, cost control | Token bucket per tenant per feature |

### 6.2 Kubernetes Deployment for AI Services

```yaml
# ai-service-deployment.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ai-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ai-service
  template:
    spec:
      containers:
        - name: ai-service
          image: company/ai-service:latest
          resources:
            requests:
              cpu: "500m"
              memory: "1Gi"
            limits:
              cpu: "2"
              memory: "4Gi"
          env:
            - name: OPENAI_API_KEY
              valueFrom:
                secretKeyRef:
                  name: ai-secrets
                  key: openai-api-key
            - name: SPRING_PROFILES_ACTIVE
              value: "production"
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 10
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 30
          # AI requests can be long-running
          # Set generous timeouts
          lifecycle:
            preStop:
              exec:
                command: ["sh", "-c", "sleep 30"] # Allow streaming responses to complete
      terminationGracePeriodSeconds: 60

---
# HPA for AI service
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: ai-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: ai-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Pods
      pods:
        metric:
          name: ai_active_requests    # Custom metric
        target:
          type: AverageValue
          averageValue: "50"          # Scale when >50 concurrent AI requests per pod
```

---

## 7. Monitoring Dashboard (Grafana)

### 7.1 Key Panels for AI Dashboard

```
┌─────────────────────────────────────────────────────────────┐
│                  AI SERVICE DASHBOARD                        │
├─────────────────┬─────────────────┬─────────────────────────┤
│ Daily Cost ($)  │ Active Requests │ Error Rate              │
│ $234.56 / $500  │ 47 concurrent   │ 0.3% (target: <1%)     │
├─────────────────┴─────────────────┴─────────────────────────┤
│                                                             │
│  Cost by Model (pie chart)    │  Latency P50/P95/P99 (graph)│
│  ┌──────────┐                 │  P50: 1.2s                  │
│  │ GPT-4o-m │ 72%            │  P95: 3.8s                  │
│  │ GPT-4o   │ 18%            │  P99: 8.2s                  │
│  │ Claude   │ 10%            │                              │
│  └──────────┘                 │                              │
├───────────────────────────────┼──────────────────────────────┤
│ Tokens Used (time series)     │ Cache Hit Rate              │
│ Input: 12.3M today           │ Exact: 15%                  │
│ Output: 3.1M today           │ Semantic: 32%               │
│ Total: 15.4M today           │ Miss: 53%                   │
├───────────────────────────────┼──────────────────────────────┤
│ Quality Metrics               │ Alerts                      │
│ User Satisfaction: 4.2/5     │ ⚠️ P99 latency > 10s (1h ago)│
│ Faithfulness: 0.87           │ ✅ Cost on track             │
│ Hallucination Rate: 2.1%    │ ✅ Cache hit rate stable     │
│ Relevancy Score: 0.91       │                              │
└───────────────────────────────┴──────────────────────────────┘
```

### 7.2 Alerting Rules

```yaml
# Prometheus alerting rules for AI service
groups:
  - name: ai-service-alerts
    rules:
      # Cost alert
      - alert: AIDailyBudgetExceeded
        expr: sum(increase(ai_cost_total_usd[24h])) > 500
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "AI daily budget exceeded (${{ $value }})"

      # Latency alert
      - alert: AIHighLatency
        expr: histogram_quantile(0.95, rate(ai_latency_seconds_bucket[5m])) > 10
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "AI P95 latency is {{ $value }}s (threshold: 10s)"

      # Error rate alert
      - alert: AIHighErrorRate
        expr: rate(ai_requests_total{status="error"}[5m]) / rate(ai_requests_total[5m]) > 0.05
        for: 3m
        labels:
          severity: critical
        annotations:
          summary: "AI error rate is {{ $value | humanizePercentage }}"

      # Quality alert
      - alert: AIQualityDegraded
        expr: avg(ai_faithfulness_score) < 0.7
        for: 30m
        labels:
          severity: warning
        annotations:
          summary: "AI faithfulness score dropped to {{ $value }}"

      # Cache effectiveness
      - alert: AICacheHitRateLow
        expr: rate(ai_cache_hits_total[1h]) / rate(ai_cache_requests_total[1h]) < 0.2
        for: 1h
        labels:
          severity: info
        annotations:
          summary: "AI cache hit rate is only {{ $value | humanizePercentage }}"
```

---

## 8. Interview Questions — MLOps / AI in Production

| Question | Key Points |
|----------|-----------|
| "How do you deploy AI features to production?" | API gateway, input/output guardrails, fallback chain, monitoring, gradual rollout |
| "How do you manage LLM costs?" | Model tiering, caching (exact + semantic), max tokens, batch API, budget alerts |
| "How do you handle LLM provider outages?" | Circuit breaker, fallback chain (primary → secondary → local model) |
| "How do you monitor AI quality?" | Faithfulness, relevancy, user satisfaction, hallucination rate, regression tests |
| "How do you A/B test AI features?" | Variant assignment, track metrics per variant, statistical significance |
| "How do you scale AI features?" | Async processing, caching, horizontal scaling, rate limiting per tenant |
| "How do you handle PII in AI systems?" | Redact before sending, use local models for sensitive data, audit trails |

---

**Next**: [10_AI_System_Design.md](10_AI_System_Design.md) — System design interview problems with AI components.

