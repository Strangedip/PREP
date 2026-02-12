# Section 10: Observability — Logging, Metrics & Distributed Tracing

---

## 10.1 The Three Pillars of Observability

---

### The "Why" & The Problem

In a monolith, debugging is straightforward: one application, one log file, one stack trace. In a microservices architecture with 50+ services, a single user request might traverse 10 services. When something goes wrong:
- **Which service failed?** The user sees a 500 error, but the root cause is 5 services deep.
- **Why is the application slow?** P99 latency spiked, but which of the 10 services in the call chain is the bottleneck?
- **Is the system healthy right now?** You need real-time visibility into CPU, memory, error rates, throughput, and business metrics.

**Monitoring** tells you *when* something is wrong. **Observability** tells you *why* something is wrong — even for failure modes you've never seen before.

The three pillars of observability are:
1. **Logs**: Discrete events — what happened, when, and where.
2. **Metrics**: Numerical measurements over time — how much, how fast, how often.
3. **Traces**: End-to-end request journeys across services — the full causal chain.

A company pays you to know this because **downtime costs real money**. Amazon loses $66,240 per second of downtime. Observability is how you detect, diagnose, and resolve incidents in minutes instead of hours.

---

### Interviewer Expectations

- **All three pillars**: Don't just talk about logs. Explain how logs, metrics, and traces work together.
- **Correlation**: Show how you correlate logs and traces using a shared `traceId`. A single trace ID connects a log entry to a metric to a span.
- **Proactive vs. reactive**: Don't wait for users to report problems. Set up alerts on SLOs (Service Level Objectives).
- **Keywords**: "Three pillars of observability", "structured logging", "Prometheus", "Grafana", "OpenTelemetry", "distributed tracing", "SLI/SLO/SLA", "P99 latency", "correlation ID", "RED method", "USE method".

---

### The Deep Dive & Solution

#### How the Three Pillars Work Together

```
User Request: POST /api/orders
        │
        ▼
┌─ API Gateway ────────────────────────────────────────────────┐
│  trace-id: abc-123                                            │
│  LOG: "Received POST /api/orders, trace-id=abc-123"          │
│  METRIC: http_requests_total{method="POST", path="/orders"}  │
│  SPAN: api-gateway → order-service (50ms)                    │
└───────────────────────────────────────────────────────────────┘
        │
        ▼
┌─ Order Service ──────────────────────────────────────────────┐
│  trace-id: abc-123 (propagated from API Gateway)             │
│  LOG: "Creating order for customer=C-456, trace-id=abc-123"  │
│  METRIC: orders_created_total{customer_tier="premium"}       │
│  SPAN: order-service → inventory-service (30ms)              │
│  SPAN: order-service → payment-service (200ms) ← SLOW!      │
└───────────────────────────────────────────────────────────────┘
        │
        ▼
┌─ Payment Service ────────────────────────────────────────────┐
│  trace-id: abc-123 (propagated from Order Service)           │
│  LOG: "Payment timeout for order=O-789, trace-id=abc-123"   │
│  METRIC: payment_errors_total{type="timeout"}                │
│  SPAN: payment-service → stripe-api (timeout after 5000ms)  │
└───────────────────────────────────────────────────────────────┘

DEBUGGING WORKFLOW:
1. ALERT fires: "Payment error rate > 5% for 5 minutes"
2. METRICS dashboard: payment_errors_total spiked at 14:23
3. TRACES: Filter by status=ERROR → see that stripe-api calls are timing out
4. LOGS: Filter by trace-id=abc-123 → see exact error message and stack trace
5. ROOT CAUSE: Stripe API degradation → enable circuit breaker fallback
```

---

## 10.2 Structured Logging

---

### The "Why" & The Problem

Traditional logs look like this:
```
2024-01-15 14:23:45.123 INFO OrderService - Creating order for customer C-456
```

This is human-readable but **machine-unfriendly**. You can't easily:
- Filter by customer ID
- Aggregate by order status
- Correlate with distributed traces

Structured logging outputs logs as JSON, making them queryable by any field:
```json
{
  "timestamp": "2024-01-15T14:23:45.123Z",
  "level": "INFO",
  "logger": "OrderService",
  "message": "Creating order",
  "traceId": "abc-123",
  "spanId": "def-456",
  "customerId": "C-456",
  "orderId": "O-789",
  "service": "order-service",
  "environment": "production"
}
```

Now you can query: "Show me all ERROR logs for customer C-456 in the last hour" — instantly.

---

### Interviewer Expectations

- **Structured JSON logging**: Not text-based logging. Every log entry is a JSON object.
- **MDC (Mapped Diagnostic Context)**: Thread-local storage for contextual fields (traceId, userId, requestId) that are automatically included in every log entry.
- **Log aggregation**: ELK Stack (Elasticsearch, Logstash, Kibana) or Grafana Loki for centralized log storage and querying.
- **Keywords**: "Structured logging", "MDC", "correlation ID", "ELK Stack", "Grafana Loki", "log aggregation", "log levels", "log rotation".

---

### The Deep Dive & Solution

#### Spring Boot Structured Logging with Logback

```xml
<!-- logback-spring.xml -->
<configuration>

    <!-- Console output for local development (human-readable) -->
    <springProfile name="local">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} [%X{traceId}] - %msg%n</pattern>
            </encoder>
        </appender>

        <root level="INFO">
            <appender-ref ref="CONSOLE" />
        </root>
    </springProfile>

    <!-- JSON output for production (machine-readable, sent to log aggregator) -->
    <springProfile name="!local">
        <appender name="JSON_CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder class="net.logstash.logback.encoder.LogstashEncoder">
                <!-- Include MDC fields automatically -->
                <includeMdcKeyName>traceId</includeMdcKeyName>
                <includeMdcKeyName>spanId</includeMdcKeyName>
                <includeMdcKeyName>userId</includeMdcKeyName>
                <includeMdcKeyName>requestId</includeMdcKeyName>

                <!-- Custom fields -->
                <customFields>
                    {"service":"order-service","environment":"${ENVIRONMENT}"} 
                </customFields>

                <!-- Include caller info for ERROR level (class, method, line number) -->
                <throwableConverter class="net.logstash.logback.stacktrace.ShortenedThrowableConverter">
                    <maxDepthPerThrowable>30</maxDepthPerThrowable>
                    <maxLength>2048</maxLength>
                    <rootCauseFirst>true</rootCauseFirst>
                </throwableConverter>
            </encoder>
        </appender>

        <root level="INFO">
            <appender-ref ref="JSON_CONSOLE" />
        </root>

        <!-- Reduce noise from verbose libraries -->
        <logger name="org.hibernate.SQL" level="WARN" />
        <logger name="org.springframework.web" level="WARN" />
        <logger name="io.netty" level="WARN" />
    </springProfile>
</configuration>
```

#### MDC Filter for Automatic Context Propagation

```java
/**
 * Filter that extracts trace context and business context from incoming requests
 * and places them in MDC (Mapped Diagnostic Context). This means EVERY log line
 * in the request processing chain automatically includes these fields.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ObservabilityFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        try {
            // Trace context (from OpenTelemetry or Spring Cloud Sleuth)
            String traceId = Optional.ofNullable(httpRequest.getHeader("X-Trace-Id"))
                    .orElse(UUID.randomUUID().toString());
            MDC.put("traceId", traceId);

            // Request context
            MDC.put("requestId", UUID.randomUUID().toString());
            MDC.put("httpMethod", httpRequest.getMethod());
            MDC.put("httpPath", httpRequest.getRequestURI());

            // Business context (from JWT or session)
            String userId = extractUserId(httpRequest);
            if (userId != null) {
                MDC.put("userId", userId);
            }

            // Propagate trace ID in response headers (for client-side correlation)
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("X-Trace-Id", traceId);

            chain.doFilter(request, response);
        } finally {
            // CRITICAL: Always clear MDC to prevent leaking context to the next request
            // (especially with thread pools where threads are reused)
            MDC.clear();
        }
    }

    private String extractUserId(HttpServletRequest request) {
        // Extract from JWT token or session
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Decode JWT and extract subject claim
            // ... (simplified for brevity)
        }
        return null;
    }
}
```

#### Using Structured Logging in Application Code

```java
@Service
@Slf4j  // Lombok annotation — creates a `log` field
public class OrderService {

    public Order createOrder(CreateOrderRequest request) {
        // Structured logging with key-value pairs
        log.info("Creating order",
            kv("customerId", request.getCustomerId()),
            kv("itemCount", request.getItems().size()),
            kv("totalAmount", request.getTotalAmount()));

        Order order = orderRepository.save(Order.from(request));

        log.info("Order created successfully",
            kv("orderId", order.getId()),
            kv("customerId", order.getCustomerId()),
            kv("status", order.getStatus()));

        return order;
    }

    public void processPayment(Order order) {
        try {
            paymentGateway.charge(order.getPaymentDetails());
            log.info("Payment processed", kv("orderId", order.getId()), kv("amount", order.getTotal()));
        } catch (PaymentException e) {
            // ERROR logs should include the exception (stack trace) AND business context
            log.error("Payment failed",
                kv("orderId", order.getId()),
                kv("customerId", order.getCustomerId()),
                kv("errorType", e.getErrorCode()),
                e);  // Pass the exception — logback will include the stack trace
        }
    }
}
```

**Output in production** (JSON, sent to ELK/Loki):
```json
{
  "@timestamp": "2024-01-15T14:23:45.123Z",
  "level": "INFO",
  "logger_name": "com.example.OrderService",
  "message": "Order created successfully",
  "traceId": "abc-123",
  "spanId": "def-456",
  "userId": "U-789",
  "requestId": "req-012",
  "service": "order-service",
  "environment": "production",
  "orderId": "O-345",
  "customerId": "C-456",
  "status": "CREATED"
}
```

#### Log Aggregation Architecture

```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Order Service │  │ Payment Svc  │  │ Inventory Svc│
│ (JSON logs   │  │ (JSON logs   │  │ (JSON logs   │
│  to stdout)  │  │  to stdout)  │  │  to stdout)  │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                  │                  │
       ▼                  ▼                  ▼
┌─────────────────────────────────────────────────┐
│  Log Collector (Fluentd / Fluent Bit / Promtail)│
│  - Runs as DaemonSet on each Kubernetes node    │
│  - Collects stdout/stderr from all containers   │
│  - Adds Kubernetes metadata (pod, namespace)    │
│  - Buffers and batches for efficiency           │
└─────────────────────┬───────────────────────────┘
                      │
          ┌───────────┴───────────┐
          ▼                       ▼
┌──────────────────┐   ┌──────────────────┐
│  Option A:       │   │  Option B:       │
│  ELK Stack       │   │  Grafana Loki    │
│                  │   │                  │
│  Elasticsearch   │   │  Loki            │
│  (full-text      │   │  (label-indexed, │
│   index, very    │   │   S3 storage,    │
│   powerful but   │   │   10x cheaper    │
│   expensive)     │   │   than ELK)      │
│       │          │   │       │          │
│  Kibana          │   │  Grafana         │
│  (visualization) │   │  (visualization) │
└──────────────────┘   └──────────────────┘
```

**ELK vs. Grafana Loki**:

| Aspect | ELK Stack | Grafana Loki |
|--------|-----------|-------------|
| **Indexing** | Full-text index (every field is indexed) | Only labels are indexed (message is not) |
| **Storage cost** | High (indexes are large) | Low (10x cheaper, uses S3/GCS) |
| **Query speed** | Fast for complex queries | Fast for label queries, slower for text search |
| **Best for** | Complex log analytics, security (SIEM) | Cost-effective log aggregation, Kubernetes-native |
| **Memory usage** | High (Elasticsearch is memory-hungry) | Low |

---

## 10.3 Metrics with Prometheus & Grafana

---

### The "Why" & The Problem

Logs tell you what happened to individual requests. Metrics tell you what's happening to the system as a whole:
- How many requests per second is the service handling?
- What's the P99 latency?
- How much CPU and memory is each service using?
- How many orders were created in the last hour?

Without metrics, you're blind. You don't know if the system is healthy until a user complains.

---

### Interviewer Expectations

- **Prometheus data model**: Metric types (Counter, Gauge, Histogram, Summary). Labels for dimensions.
- **RED method**: Rate, Errors, Duration — the three golden signals for every service.
- **USE method**: Utilization, Saturation, Errors — for infrastructure (CPU, memory, disk).
- **Spring Boot Actuator + Micrometer**: How Spring Boot exposes metrics to Prometheus.
- **Keywords**: "RED method", "USE method", "Prometheus", "Grafana", "Micrometer", "Counter", "Histogram", "P99 latency", "SLI", "SLO", "SLA", "alerting".

---

### The Deep Dive & Solution

#### Prometheus Metric Types

| Type | Description | Example | When to Use |
|------|------------|---------|-------------|
| **Counter** | Monotonically increasing value. Only goes up (or resets to 0). | `http_requests_total` | Request count, error count, bytes processed |
| **Gauge** | Value that can go up and down. | `jvm_memory_used_bytes` | Current memory, queue size, temperature |
| **Histogram** | Measures the distribution of values in predefined buckets. | `http_request_duration_seconds` | Latency, request size — use for percentiles (P50, P95, P99) |
| **Summary** | Similar to histogram but calculates quantiles on the client side. | `rpc_duration_seconds` | When you need exact quantiles but can't aggregate across instances |

#### Spring Boot + Micrometer + Prometheus Integration

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, info, prometheus, metrics
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true   # Enable histogram buckets for HTTP latency
      slo:
        http.server.requests: 50ms, 100ms, 250ms, 500ms, 1s, 5s
    tags:
      application: order-service
      environment: ${SPRING_PROFILES_ACTIVE:local}
  prometheus:
    metrics:
      export:
        enabled: true
```

```java
/**
 * Custom business metrics — not just HTTP metrics, but domain-specific KPIs.
 * This is what separates a Lead from a mid-level engineer.
 */
@Service
public class OrderService {

    private final Counter ordersCreatedCounter;
    private final Counter ordersFailedCounter;
    private final Timer orderProcessingTimer;
    private final DistributionSummary orderAmountSummary;
    private final AtomicInteger activeOrdersGauge;

    public OrderService(MeterRegistry meterRegistry) {
        // Counter: total orders created, tagged by customer tier
        this.ordersCreatedCounter = Counter.builder("orders.created.total")
                .description("Total number of orders created")
                .tag("service", "order-service")
                .register(meterRegistry);

        this.ordersFailedCounter = Counter.builder("orders.failed.total")
                .description("Total number of failed order attempts")
                .tag("service", "order-service")
                .register(meterRegistry);

        // Timer: order processing duration (automatically creates histogram buckets)
        this.orderProcessingTimer = Timer.builder("orders.processing.duration")
                .description("Time taken to process an order end-to-end")
                .publishPercentileHistogram()
                .sla(Duration.ofMillis(100), Duration.ofMillis(500), Duration.ofSeconds(1))
                .register(meterRegistry);

        // Distribution Summary: order amounts (to track average order value)
        this.orderAmountSummary = DistributionSummary.builder("orders.amount")
                .description("Distribution of order amounts")
                .baseUnit("usd")
                .publishPercentileHistogram()
                .register(meterRegistry);

        // Gauge: currently active orders being processed
        this.activeOrdersGauge = meterRegistry.gauge(
                "orders.active",
                Tags.of("service", "order-service"),
                new AtomicInteger(0));
    }

    public Order createOrder(CreateOrderRequest request) {
        activeOrdersGauge.incrementAndGet();

        return orderProcessingTimer.record(() -> {
            try {
                Order order = orderRepository.save(Order.from(request));

                ordersCreatedCounter.increment();
                orderAmountSummary.record(order.getTotal().doubleValue());

                return order;
            } catch (Exception e) {
                ordersFailedCounter.increment();
                throw e;
            } finally {
                activeOrdersGauge.decrementAndGet();
            }
        });
    }
}
```

#### RED Method — The Three Golden Signals

For every service, monitor these three metrics:

```
┌────────────────────────────────────────────────────────┐
│  RED Method — Monitor These for Every Microservice     │
│                                                         │
│  R = Rate:    requests per second                       │
│               rate(http_server_requests_seconds_count   │
│                    {uri="/api/orders"}[5m])             │
│                                                         │
│  E = Errors:  error rate (percentage of 5xx responses)  │
│               rate(http_server_requests_seconds_count   │
│                    {status=~"5.."}[5m]) /                │
│               rate(http_server_requests_seconds_count   │
│                    [5m]) * 100                           │
│                                                         │
│  D = Duration: latency percentiles (P50, P95, P99)      │
│               histogram_quantile(0.99,                  │
│                 rate(http_server_requests_seconds        │
│                      _bucket[5m]))                      │
└────────────────────────────────────────────────────────┘
```

#### Prometheus Configuration

```yaml
# prometheus.yml
global:
  scrape_interval: 15s
  evaluation_interval: 15s

# Alert rules
rule_files:
  - "alert_rules.yml"

# Alertmanager configuration
alerting:
  alertmanagers:
    - static_configs:
        - targets: ['alertmanager:9093']

# Scrape configurations — where to collect metrics from
scrape_configs:
  # Kubernetes service discovery — automatically finds all pods with prometheus annotations
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      # Only scrape pods with annotation: prometheus.io/scrape: "true"
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      # Use the annotation for the metrics path (default: /actuator/prometheus)
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_path]
        action: replace
        target_label: __metrics_path__
        regex: (.+)
      # Use the annotation for the port
      - source_labels: [__address__, __meta_kubernetes_pod_annotation_prometheus_io_port]
        action: replace
        regex: ([^:]+)(?::\d+)?;(\d+)
        replacement: $1:$2
        target_label: __address__
      # Add pod metadata as labels
      - source_labels: [__meta_kubernetes_namespace]
        action: replace
        target_label: namespace
      - source_labels: [__meta_kubernetes_pod_name]
        action: replace
        target_label: pod
```

```yaml
# alert_rules.yml — Alerting rules for SLO violations
groups:
  - name: order-service-slos
    rules:
      # Alert: High error rate (SLO: <1% errors)
      - alert: HighErrorRate
        expr: |
          (
            rate(http_server_requests_seconds_count{status=~"5..", application="order-service"}[5m])
            /
            rate(http_server_requests_seconds_count{application="order-service"}[5m])
          ) > 0.01
        for: 5m   # Must persist for 5 minutes to fire
        labels:
          severity: critical
          team: order-team
        annotations:
          summary: "High error rate on order-service"
          description: "Error rate is {{ $value | humanizePercentage }} (> 1% SLO) for 5 minutes"
          runbook_url: "https://wiki.example.com/runbooks/order-service-high-error-rate"

      # Alert: High latency (SLO: P99 < 500ms)
      - alert: HighP99Latency
        expr: |
          histogram_quantile(0.99,
            rate(http_server_requests_seconds_bucket{application="order-service"}[5m])
          ) > 0.5
        for: 5m
        labels:
          severity: warning
          team: order-team
        annotations:
          summary: "High P99 latency on order-service"
          description: "P99 latency is {{ $value | humanizeDuration }} (> 500ms SLO)"

      # Alert: Pod restarts (possible OOM or crash loop)
      - alert: HighPodRestarts
        expr: |
          increase(kube_pod_container_status_restarts_total{namespace="production", container="order-service"}[1h]) > 3
        for: 0m
        labels:
          severity: critical
          team: platform
        annotations:
          summary: "Pod {{ $labels.pod }} restarted {{ $value }} times in the last hour"
```

#### SLI / SLO / SLA — Service Level Framework

| Concept | Definition | Example |
|---------|-----------|---------|
| **SLI** (Service Level Indicator) | A metric that measures service quality | P99 latency, error rate, availability |
| **SLO** (Service Level Objective) | A target value for an SLI | P99 latency < 500ms, error rate < 0.1%, availability > 99.9% |
| **SLA** (Service Level Agreement) | A contract with consequences for missing SLOs | "99.9% uptime or we credit 10% of the monthly fee" |
| **Error Budget** | The amount of unreliability you're allowed | 99.9% availability = 43.2 minutes of downtime per month |

**Lead Engineer insight**: Define SLOs *before* building alerting. Your alerts should fire when you're burning through your error budget too fast, not just when a single request fails.

---

## 10.4 Distributed Tracing with OpenTelemetry

---

### The "Why" & The Problem

A user clicks "Place Order." The request goes through: API Gateway → Order Service → Inventory Service → Payment Service → Notification Service. The P99 latency is 3 seconds. Which service is slow?

Without distributed tracing, you'd have to manually correlate logs across 5 services using timestamps. With distributed tracing, you see a **waterfall chart** showing exactly how long each service took and where the bottleneck is.

---

### Interviewer Expectations

- **OpenTelemetry**: The industry standard for distributed tracing (merged from OpenTracing + OpenCensus). Know the data model: **traces**, **spans**, **context propagation**.
- **Context propagation**: How trace IDs are passed between services (HTTP headers: `traceparent`, Kafka headers, gRPC metadata).
- **Sampling strategies**: You can't trace 100% of requests in production (too expensive). Know head-based vs. tail-based sampling.
- **Keywords**: "OpenTelemetry", "distributed trace", "span", "context propagation", "trace ID", "parent span", "sampling", "Jaeger", "Zipkin", "Tempo".

---

### The Deep Dive & Solution

#### Trace Structure

```
Trace ID: abc-123  (unique identifier for the entire request journey)
│
├── Span: API Gateway (root span)
│   ├── Duration: 3200ms
│   ├── Tags: http.method=POST, http.url=/api/orders, http.status=200
│   │
│   ├── Span: Order Service
│   │   ├── Duration: 3100ms
│   │   ├── Tags: service=order-service
│   │   │
│   │   ├── Span: Inventory Check
│   │   │   ├── Duration: 150ms
│   │   │   └── Tags: service=inventory-service, items=3
│   │   │
│   │   ├── Span: Payment Processing  ← BOTTLENECK
│   │   │   ├── Duration: 2800ms      ← THIS IS WHY IT'S SLOW
│   │   │   ├── Tags: service=payment-service, payment.provider=stripe
│   │   │   │
│   │   │   └── Span: Stripe API Call
│   │   │       ├── Duration: 2700ms  ← External API is slow
│   │   │       └── Tags: http.url=api.stripe.com/charges
│   │   │
│   │   └── Span: Send Notification
│   │       ├── Duration: 50ms
│   │       └── Tags: service=notification-service, channel=email
│   │
│   └── End
```

#### Spring Boot + OpenTelemetry Auto-Instrumentation

The simplest way to add tracing — zero code changes. OpenTelemetry's Java agent instruments Spring, JDBC, Kafka, Redis, gRPC, and HTTP clients automatically.

```dockerfile
# Add OpenTelemetry Java agent to your Docker image
FROM eclipse-temurin:21-jre-alpine

# Download the OpenTelemetry Java agent
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.1.0/opentelemetry-javaagent.jar /opt/opentelemetry-javaagent.jar

COPY target/order-service.jar /app/order-service.jar

ENV JAVA_OPTS="-javaagent:/opt/opentelemetry-javaagent.jar \
               -Dotel.service.name=order-service \
               -Dotel.exporter.otlp.endpoint=http://otel-collector:4317 \
               -Dotel.traces.sampler=parentbased_traceidratio \
               -Dotel.traces.sampler.arg=0.1"
#               ↑ Sample 10% of traces (production-realistic)

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app/order-service.jar"]
```

```yaml
# Kubernetes deployment with OpenTelemetry environment variables
apiVersion: apps/v1
kind: Deployment
metadata:
  name: order-service
spec:
  template:
    spec:
      containers:
        - name: order-service
          image: myregistry/order-service:v2.3.1
          env:
            - name: OTEL_SERVICE_NAME
              value: "order-service"
            - name: OTEL_EXPORTER_OTLP_ENDPOINT
              value: "http://otel-collector.observability:4317"
            - name: OTEL_TRACES_SAMPLER
              value: "parentbased_traceidratio"
            - name: OTEL_TRACES_SAMPLER_ARG
              value: "0.1"  # 10% sampling
            - name: OTEL_RESOURCE_ATTRIBUTES
              value: "deployment.environment=production,k8s.namespace.name=$(POD_NAMESPACE),k8s.pod.name=$(POD_NAME)"
            - name: POD_NAMESPACE
              valueFrom:
                fieldRef:
                  fieldPath: metadata.namespace
            - name: POD_NAME
              valueFrom:
                fieldRef:
                  fieldPath: metadata.name
```

#### Custom Spans for Business Logic

```java
/**
 * While auto-instrumentation covers HTTP/DB/Kafka, you may want custom spans
 * for important business operations to get fine-grained visibility.
 */
@Service
public class OrderService {

    private final Tracer tracer;  // Injected by OpenTelemetry

    public OrderService(Tracer tracer) {
        this.tracer = tracer;
    }

    public Order createOrder(CreateOrderRequest request) {
        // Create a custom span for the entire order creation workflow
        Span orderSpan = tracer.spanBuilder("order.create")
                .setAttribute("order.customer_id", request.getCustomerId())
                .setAttribute("order.item_count", request.getItems().size())
                .startSpan();

        try (Scope scope = orderSpan.makeCurrent()) {
            // Step 1: Validate
            Span validationSpan = tracer.spanBuilder("order.validate").startSpan();
            try (Scope s = validationSpan.makeCurrent()) {
                validateOrder(request);
            } finally {
                validationSpan.end();
            }

            // Step 2: Reserve inventory
            Span inventorySpan = tracer.spanBuilder("order.reserve_inventory")
                    .setAttribute("inventory.items", request.getItems().size())
                    .startSpan();
            try (Scope s = inventorySpan.makeCurrent()) {
                inventoryService.reserveItems(request.getItems());
            } finally {
                inventorySpan.end();
            }

            // Step 3: Process payment
            Span paymentSpan = tracer.spanBuilder("order.process_payment")
                    .setAttribute("payment.amount", request.getTotalAmount().doubleValue())
                    .startSpan();
            try (Scope s = paymentSpan.makeCurrent()) {
                paymentService.charge(request.getPaymentDetails());
            } finally {
                paymentSpan.end();
            }

            Order order = orderRepository.save(Order.from(request));
            orderSpan.setAttribute("order.id", order.getId());
            return order;

        } catch (Exception e) {
            orderSpan.recordException(e);
            orderSpan.setStatus(StatusCode.ERROR, e.getMessage());
            throw e;
        } finally {
            orderSpan.end();
        }
    }
}
```

#### Simpler: Using `@WithSpan` Annotation

```java
@Service
public class OrderService {

    @WithSpan("order.create")  // Automatically creates a span
    public Order createOrder(
            @SpanAttribute("order.customer_id") String customerId,
            @SpanAttribute("order.item_count") int itemCount,
            CreateOrderRequest request) {

        validateOrder(request);
        inventoryService.reserveItems(request.getItems());
        paymentService.charge(request.getPaymentDetails());

        return orderRepository.save(Order.from(request));
    }

    @WithSpan("order.validate")
    private void validateOrder(CreateOrderRequest request) {
        // Validation logic
    }
}
```

#### OpenTelemetry Collector Architecture

```
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│ Order Service │  │ Payment Svc  │  │ Inventory Svc│
│  (OTLP gRPC) │  │  (OTLP gRPC) │  │  (OTLP gRPC) │
└──────┬───────┘  └──────┬───────┘  └──────┬───────┘
       │                  │                  │
       └──────────────────┼──────────────────┘
                          │
                          ▼
              ┌──────────────────────┐
              │  OpenTelemetry       │
              │  Collector           │
              │                      │
              │  Receivers:          │
              │  - OTLP (gRPC/HTTP)  │
              │                      │
              │  Processors:         │
              │  - Batch (buffer &   │
              │    send in batches)  │
              │  - Tail sampling     │
              │    (keep 100% of     │
              │    errors, 10% of    │
              │    success)          │
              │  - Attributes        │
              │    (add k8s metadata)│
              │                      │
              │  Exporters:          │
              │  - Jaeger / Tempo    │
              │  - Prometheus        │
              │  - Loki (logs)       │
              └──────────┬───────────┘
                         │
          ┌──────────────┼──────────────┐
          ▼              ▼              ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│  Jaeger /    │ │  Prometheus  │ │  Grafana     │
│  Tempo       │ │  (metrics)   │ │  Loki (logs) │
│  (traces)    │ │              │ │              │
└──────────────┘ └──────────────┘ └──────────────┘
          │              │              │
          └──────────────┼──────────────┘
                         ▼
              ┌──────────────────────┐
              │  Grafana Dashboard   │
              │  (unified view:      │
              │   traces + metrics   │
              │   + logs correlated) │
              └──────────────────────┘
```

#### Sampling Strategies

| Strategy | Description | When to Use |
|----------|------------|-------------|
| **Always On** | Trace 100% of requests | Development, staging, low-traffic services |
| **Probabilistic (Head-based)** | Randomly sample X% of requests at the start | Production — simple, predictable cost |
| **Rate-limiting** | Sample N traces per second | When you want a fixed budget |
| **Tail-based** | Decide whether to keep a trace AFTER it completes (keep all errors, sample successes) | When you need 100% of error traces but can't afford 100% of all traces |
| **Parent-based** | If the parent span was sampled, sample the child too (consistent within a trace) | Multi-service systems — ensures you get complete traces, not partial ones |

**Recommendation**: Use **parent-based + tail-based sampling** in production. The OTel Collector makes the decision after the trace completes: keep all error traces, keep 10% of successful traces.

---

## 10.5 Grafana Dashboards: Putting It All Together

---

### The "Why" & The Problem

Metrics, logs, and traces are useless if nobody looks at them. Grafana provides a unified dashboard that visualizes all three pillars, allowing you to jump from a metric anomaly to the relevant traces and logs in seconds.

---

### The Deep Dive & Solution

#### The Four Essential Dashboards for a Lead Engineer

**Dashboard 1: Service Health Overview (RED Metrics)**
```
┌─────────────────────────────────────────────────────────────┐
│  SERVICE HEALTH — order-service                              │
│                                                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐         │
│  │ Request Rate │  │ Error Rate  │  │ P99 Latency │         │
│  │  1,234 rps   │  │   0.05%     │  │   127ms     │         │
│  │  ▓▓▓▓▓▓▓▓▓▓ │  │  ✅ < 1%    │  │  ✅ < 500ms │         │
│  └─────────────┘  └─────────────┘  └─────────────┘         │
│                                                               │
│  [Request Rate over time — line chart]                        │
│  [Error Rate over time — line chart with SLO threshold line]  │
│  [Latency distribution — heatmap showing P50/P95/P99]        │
│  [Active connections — gauge]                                 │
└─────────────────────────────────────────────────────────────┘
```

**Dashboard 2: Infrastructure (USE Metrics)**
```
┌─────────────────────────────────────────────────────────────┐
│  INFRASTRUCTURE — order-service pods                         │
│                                                               │
│  [CPU usage per pod — stacked area chart]                    │
│  [Memory usage per pod — stacked area chart with limit line] │
│  [JVM Heap usage — used vs. max]                             │
│  [GC pause time — histogram]                                 │
│  [Thread pool utilization — active vs. max threads]          │
│  [Connection pool — active vs. max connections]              │
│  [Disk I/O — read/write bytes per second]                    │
└─────────────────────────────────────────────────────────────┘
```

**Dashboard 3: Business KPIs**
```
┌─────────────────────────────────────────────────────────────┐
│  BUSINESS METRICS — Order Platform                           │
│                                                               │
│  [Orders created per hour — bar chart]                       │
│  [Revenue per hour — line chart]                             │
│  [Average order value — gauge]                               │
│  [Payment success rate — percentage gauge]                   │
│  [Cart abandonment rate — line chart]                        │
│  [Top products ordered — table]                              │
└─────────────────────────────────────────────────────────────┘
```

**Dashboard 4: On-Call Incident Response**
```
┌─────────────────────────────────────────────────────────────┐
│  INCIDENT RESPONSE — Current Alerts                          │
│                                                               │
│  [Active alerts — table with severity, description, duration]│
│  [Error rate by endpoint — breakdown of which API is failing]│
│  [Recent traces with errors — clickable → Jaeger detail]     │
│  [Recent error logs — clickable → Loki full log context]     │
│  [Deployment markers — when was the last deployment?]        │
│  [Dependency health — status of Postgres, Redis, Kafka]      │
└─────────────────────────────────────────────────────────────┘
```

---

## 10.6 Alerting Strategy

---

### The "Why" & The Problem

Too many alerts = alert fatigue. The on-call engineer ignores pages because 90% are false positives. Too few alerts = incidents go undetected for hours.

---

### Interviewer Expectations

- **Alert on symptoms, not causes**: Alert on "error rate > 1%" (symptom), not "CPU > 80%" (cause). High CPU might be fine if the service is handling traffic correctly.
- **Severity levels**: Critical (PagerDuty wake-up call), Warning (Slack notification), Info (dashboard only).
- **Runbooks**: Every alert should link to a runbook that explains what the alert means and how to investigate.
- **Keywords**: "Alert fatigue", "symptom-based alerting", "error budget", "burn rate", "runbook", "escalation policy".

---

### The Deep Dive & Solution

#### Alerting Best Practices

| Principle | Do | Don't |
|-----------|-----|-------|
| **Alert on symptoms** | "Error rate > 1% for 5 min" | "CPU > 80%" (might be normal under load) |
| **Use `for` duration** | "Error rate > 1% for 5 min" | "Error rate > 1%" (single blip fires alert) |
| **Include context** | Alert includes service, namespace, runbook URL | "Something is wrong" |
| **Severity matters** | Critical = pager, Warning = Slack | Everything is Critical |
| **Error budget burn rate** | "Burning error budget 10x faster than normal" | "One 500 error occurred" |

#### Alertmanager Configuration

```yaml
# alertmanager.yml
global:
  resolve_timeout: 5m

route:
  receiver: 'slack-default'
  group_by: ['alertname', 'service']
  group_wait: 30s       # Wait 30s to batch related alerts
  group_interval: 5m    # Wait 5m before sending another batch
  repeat_interval: 4h   # Repeat unresolved alerts every 4 hours

  routes:
    # Critical alerts → PagerDuty (wake up on-call)
    - match:
        severity: critical
      receiver: 'pagerduty-critical'
      continue: true  # Also send to Slack

    # Warning alerts → Slack only
    - match:
        severity: warning
      receiver: 'slack-warnings'

receivers:
  - name: 'pagerduty-critical'
    pagerduty_configs:
      - routing_key: '<pagerduty-integration-key>'
        severity: critical
        description: '{{ .CommonAnnotations.summary }}'
        details:
          description: '{{ .CommonAnnotations.description }}'
          runbook_url: '{{ .CommonAnnotations.runbook_url }}'

  - name: 'slack-warnings'
    slack_configs:
      - api_url: '<slack-webhook-url>'
        channel: '#alerts-order-team'
        title: '⚠️ {{ .CommonAnnotations.summary }}'
        text: '{{ .CommonAnnotations.description }}\nRunbook: {{ .CommonAnnotations.runbook_url }}'

  - name: 'slack-default'
    slack_configs:
      - api_url: '<slack-webhook-url>'
        channel: '#alerts-general'
```

---

## 10.7 Interview Quick Reference: Observability

---

### Common Interview Questions & Lead-Level Answers

**Q: "How would you set up observability for a microservices architecture?"**

A: "I'd implement all **three pillars of observability**: structured logging with JSON output collected by Fluent Bit into **Grafana Loki**, metrics exposed via **Micrometer** and scraped by **Prometheus** with **Grafana** dashboards, and distributed tracing using **OpenTelemetry** auto-instrumentation exported to **Jaeger** or **Grafana Tempo**. The key is **correlation** — every log entry includes the `traceId` from OpenTelemetry, so I can jump from a metric anomaly to the relevant traces to the exact log lines. I'd define **SLOs** for each service (e.g., P99 < 500ms, error rate < 0.1%) and set up **error budget burn rate** alerts in Prometheus Alertmanager. Critical alerts page via PagerDuty with runbook links, warnings go to Slack. For sampling, I use **tail-based sampling** in the OTel Collector — keep 100% of error traces, 10% of successful ones — to balance cost with debugging capability."

**Q: "Your P99 latency just spiked. Walk me through your debugging process."**

A: "First, I check the **Grafana dashboard** to see which service's P99 spiked and when it started. I look for **correlation with deployments** (did we just deploy?) or **external events** (traffic spike? dependency outage?). Then I go to **distributed traces** — I filter traces by duration > P99 threshold and look at the waterfall view. This shows me exactly which span (which service, which operation) is the bottleneck. If the bottleneck is a database call, I check the **database metrics** (connection pool utilization, query duration, lock contention). If it's an external API call, I check the **circuit breaker metrics** (is the circuit open?). I then check the **logs** for that trace ID to see any error messages or warnings. Common root causes I look for: connection pool exhaustion, GC pauses (check JVM metrics), N+1 queries (check Hibernate query count metric), or upstream service degradation."

**Q: "What's the difference between monitoring and observability?"**

A: "**Monitoring** answers predefined questions: 'Is the CPU above 80%?' 'Is the service up?' It's based on known failure modes. **Observability** lets you answer *unknown* questions: 'Why is this specific user's request slow?' 'What changed between yesterday and today?' Observability achieves this by emitting rich, structured telemetry (logs, metrics, traces) that you can query ad-hoc. The three pillars — structured logs with contextual fields, dimensional metrics with labels, and distributed traces with span-level detail — together let you debug any issue, even ones you've never seen before. A system with good monitoring tells you *when* something is wrong. A system with good observability tells you *why*."
