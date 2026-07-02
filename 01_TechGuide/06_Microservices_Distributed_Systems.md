# Section 6: Microservices & Distributed Systems (CRITICAL)

> **You are here**: Senior SDE — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [05_Database_Performance_Tuning.md](05_Database_Performance_Tuning.md) | **Next**: [07_System_Design.md](07_System_Design.md)

---

## 6.1 Resilience Patterns: Circuit Breaker, Bulkhead, Retry, and Rate Limiting

---

### The "Why" & The Problem

In a microservices architecture, your application is no longer a single process. It is a **distributed system** where Service A depends on Service B, which depends on Service C, which depends on an external API. When Service C becomes slow or unavailable, the failure **cascades** upstream:

1. Service C starts responding in 30 seconds instead of 200ms.
2. Service B's thread pool fills up with threads waiting for Service C's response.
3. Service B can no longer serve any requests — even those that don't involve Service C.
4. Service A, which depends on Service B, also starts failing.
5. The entire system collapses because of one slow downstream service. This is called a **cascading failure**.

A company pays you to know this because **a single unhealthy dependency can take down your entire platform**. Netflix famously lost their entire streaming service in 2012 due to cascading failures, which led them to create Hystrix (now deprecated) and the broader resilience engineering discipline. Today, Resilience4j is the standard library for Java/Spring applications.

---

### Interviewer Expectations

- **Circuit Breaker**: Explain the state machine (CLOSED → OPEN → HALF_OPEN). Know the configuration parameters (failure rate threshold, wait duration, permitted calls in half-open). Explain how it prevents cascading failures.
- **Bulkhead**: Explain how it limits concurrent access to a downstream service, isolating failures. Know the difference between semaphore-based and thread-pool-based bulkheads.
- **Retry**: Know when to retry (transient errors, 503s) and when NOT to retry (400s, business logic errors). Explain exponential backoff with jitter.
- **Rate Limiting**: Explain token bucket and sliding window algorithms. Why you need it at the API gateway level AND at the service level.
- **Keywords**: "Cascading failure", "fail fast", "fallback", "state machine", "sliding window", "exponential backoff with jitter", "bulkhead isolation", "graceful degradation", "blast radius".

---

### The Deep Dive & Solution

#### Circuit Breaker (Resilience4j)

The circuit breaker pattern is borrowed from electrical engineering. When a downstream service starts failing, the circuit breaker "trips" and immediately returns a fallback response without making the actual call. This has three benefits:
1. **Protects the caller**: Threads are not wasted waiting for a service that is known to be down.
2. **Protects the callee**: Gives the failing service time to recover without being bombarded with requests.
3. **Fails fast**: The user gets a response (even a degraded one) in milliseconds instead of waiting 30 seconds for a timeout.

**State Machine**:

```
                         failure rate > threshold
┌──────────┐ ─────────────────────────────────────> ┌──────────┐
│  CLOSED   │                                         │   OPEN   │
│ (normal)  │ <───────────────────────────────────── │ (failing) │
└──────────┘     success rate > threshold             └──────────┘
      │              in HALF_OPEN                          │
      │                                                     │
      │                   ┌────────────┐                   │
      │                   │ HALF_OPEN  │                   │
      │                   │ (testing)  │ <─────────────────┘
      │                   └────────────┘   wait duration expires
      │                         │
      └─────────────────────────┘
                 success → CLOSED
                 failure → OPEN
```

- **CLOSED** (default): Requests flow through normally. The circuit breaker monitors the failure rate using a sliding window (count-based or time-based).
- **OPEN**: All requests are immediately rejected with a `CallNotPermittedException`. A fallback is returned instead. The circuit breaker waits for a configured duration (`waitDurationInOpenState`) before transitioning to HALF_OPEN.
- **HALF_OPEN**: A limited number of test requests (`permittedNumberOfCallsInHalfOpenState`) are allowed through. If they succeed above the threshold, the circuit transitions back to CLOSED. If they fail, it goes back to OPEN.

**Implementation with Resilience4j and Spring Boot**:

```java
// application.yml
resilience4j:
  circuitbreaker:
    instances:
      paymentService:
        registerHealthIndicator: true
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10          # Track last 10 calls
        failureRateThreshold: 50       # Open circuit if >= 50% fail
        waitDurationInOpenState: 10s    # Stay open for 10 seconds
        permittedNumberOfCallsInHalfOpenState: 3  # Allow 3 test calls
        minimumNumberOfCalls: 5        # Need at least 5 calls before evaluating
        slowCallDurationThreshold: 2s  # Calls > 2s are counted as slow
        slowCallRateThreshold: 80      # Open if >= 80% calls are slow
        recordExceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
          - org.springframework.web.client.HttpServerErrorException
        ignoreExceptions:
          - com.myapp.BusinessValidationException  # Don't count business errors

// Service with Circuit Breaker
@Service
public class PaymentService {
    
    private final WebClient webClient;
    
    @CircuitBreaker(name = "paymentService", fallbackMethod = "paymentFallback")
    public PaymentResponse processPayment(PaymentRequest request) {
        return webClient.post()
            .uri("/api/payments")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(PaymentResponse.class)
            .block(Duration.ofSeconds(3));
    }
    
    // Fallback method — same signature + Throwable parameter
    private PaymentResponse paymentFallback(PaymentRequest request, Throwable throwable) {
        log.warn("Payment service unavailable, returning degraded response: {}", 
                 throwable.getMessage());
        
        // Options for fallback:
        // 1. Return cached data
        // 2. Return a default/degraded response
        // 3. Queue the request for later processing
        // 4. Call a backup service
        
        return PaymentResponse.builder()
            .status("PENDING")
            .message("Payment is being processed. You will be notified.")
            .build();
    }
}
```

#### Bulkhead Pattern

The bulkhead pattern limits the number of concurrent calls to a downstream service. Named after ship bulkheads that contain flooding to one compartment, this pattern prevents one slow service from consuming all available threads.

**Without bulkhead**: You have 200 Tomcat threads. Service C becomes slow. All 200 threads get stuck waiting for Service C. No threads left for Service A or B requests → entire service is down.

**With bulkhead**: Maximum 20 concurrent calls to Service C. If all 20 slots are taken, additional requests immediately get a fallback response. The remaining 180 threads can still serve other requests.

**Two types**:
- **Semaphore bulkhead**: Limits concurrency using a semaphore counter. Runs on the caller's thread. Lightweight but offers no timeout on waiting for the semaphore.
- **Thread pool bulkhead**: Runs calls in a dedicated thread pool. Provides true isolation — the downstream service's latency cannot affect the caller's threads. More overhead due to context switching.

```java
// application.yml
resilience4j:
  bulkhead:
    instances:
      inventoryService:
        maxConcurrentCalls: 20          # Max 20 simultaneous calls
        maxWaitDuration: 500ms          # Wait up to 500ms for a slot, then fail

  thread-pool-bulkhead:
    instances:
      reportingService:
        maxThreadPoolSize: 10           # Dedicated thread pool of 10 threads
        coreThreadPoolSize: 5
        queueCapacity: 20              # Buffer 20 tasks in the queue
        keepAliveDuration: 60s

// Service with Bulkhead
@Service
public class InventoryService {
    
    @Bulkhead(name = "inventoryService", fallbackMethod = "inventoryFallback")
    public InventoryResponse checkStock(String productId) {
        return webClient.get()
            .uri("/api/inventory/{id}", productId)
            .retrieve()
            .bodyToMono(InventoryResponse.class)
            .block();
    }
    
    private InventoryResponse inventoryFallback(String productId, Throwable t) {
        // Return cached stock level or "check back later"
        return InventoryResponse.unknown(productId);
    }
}
```

#### Retry Pattern

Retries handle **transient failures**: network blips, temporary 503s, brief DNS resolution failures. These are issues that resolve themselves if you simply try again after a short delay.

**Critical rules for retries**:
1. **Only retry on transient errors**: 503 (Service Unavailable), 429 (Too Many Requests), IOException (network error). NEVER retry on 400 (Bad Request), 401 (Unauthorized), 404 (Not Found) — these will never succeed on retry.
2. **Use exponential backoff**: 100ms → 200ms → 400ms → 800ms. This prevents thundering herd — if 1000 clients all retry at exactly the same moment, you DDoS your own service.
3. **Add jitter**: Randomize the delay (e.g., 100ms ± 50ms). Without jitter, exponential backoff still causes synchronized retries because all clients started at the same time.
4. **Set a maximum retry count**: Infinite retries = infinite waiting. 3-5 retries is typical.
5. **Ensure idempotency**: If you retry a payment request, you MUST guarantee it doesn't charge the user twice (use idempotency keys).

```java
// application.yml
resilience4j:
  retry:
    instances:
      orderService:
        maxAttempts: 3                          # Original + 2 retries
        waitDuration: 500ms                     # Initial wait
        enableExponentialBackoff: true          # 500ms → 1s → 2s
        exponentialBackoffMultiplier: 2
        enableRandomizedWait: true              # Add jitter
        randomizedWaitFactor: 0.5               # ± 50% of calculated wait
        retryExceptions:
          - java.io.IOException
          - java.util.concurrent.TimeoutException
        ignoreExceptions:
          - com.myapp.BusinessValidationException
          - org.springframework.web.client.HttpClientErrorException  # 4xx errors

// Service with Retry
@Service
public class OrderService {
    
    @Retry(name = "orderService", fallbackMethod = "orderFallback")
    @CircuitBreaker(name = "orderService", fallbackMethod = "orderFallback")
    // NOTE: Order matters! Retry is the inner decorator, CircuitBreaker is the outer.
    // Retry wraps the actual call. CircuitBreaker wraps the retried call.
    // If all retries fail, the circuit breaker records ONE failure.
    public Order getOrder(String orderId) {
        return webClient.get()
            .uri("/api/orders/{id}", orderId)
            .retrieve()
            .bodyToMono(Order.class)
            .block();
    }
    
    private Order orderFallback(String orderId, Throwable t) {
        return orderCacheRepository.findById(orderId)
            .orElse(Order.unavailable(orderId));
    }
}
```

**Resilience4j decoration order** (outermost to innermost):
```
Retry ( CircuitBreaker ( RateLimiter ( TimeLimiter ( Bulkhead ( Function ) ) ) ) )
```

#### Rate Limiting

Rate limiting controls the rate at which requests are processed. Two common algorithms:

**Token Bucket**: A bucket holds tokens. Each request consumes a token. Tokens are refilled at a fixed rate. If the bucket is empty, requests are rejected or queued. Allows **bursts** up to the bucket capacity.

**Sliding Window**: Tracks requests in a time window. If the count exceeds the limit, reject. Smoother rate limiting, no burst allowance.

```java
// application.yml
resilience4j:
  ratelimiter:
    instances:
      externalApi:
        limitForPeriod: 50              # 50 requests per period
        limitRefreshPeriod: 1s          # Refresh every second → 50 req/sec
        timeoutDuration: 500ms          # Wait up to 500ms for permission

// Service with Rate Limiter
@Service
public class ExternalApiService {
    
    @RateLimiter(name = "externalApi", fallbackMethod = "rateLimitFallback")
    public ApiResponse callExternalApi(String query) {
        return webClient.get()
            .uri("/external/api?q={query}", query)
            .retrieve()
            .bodyToMono(ApiResponse.class)
            .block();
    }
    
    private ApiResponse rateLimitFallback(String query, Throwable t) {
        log.warn("Rate limit exceeded for external API call");
        throw new TooManyRequestsException("Please try again later");
    }
}
```

---

## 6.2 Data Consistency: SAGA Pattern, Two-Phase Commit, Eventual Consistency

---

### The "Why" & The Problem

In a monolith, you have a single database and a single ACID transaction. When you create an order, you can atomically insert into `orders`, update `inventory`, and charge the `payment` — all in one transaction. If any step fails, everything rolls back. Beautiful.

In microservices, **each service owns its own database** (Database-per-Service pattern). The Order Service has its database, the Inventory Service has its database, and the Payment Service has its database. You **cannot** wrap a single ACID transaction across three different databases owned by three different services. There is no `@Transactional` that spans microservices.

This creates the **distributed transaction problem**: How do you ensure that either ALL services complete their part of the business operation, or NONE of them do? If the Payment Service charges the customer but the Inventory Service fails to reserve the item, you have an **inconsistent state** — the customer paid for something they won't receive.

A company pays you to know this because **data inconsistency in production costs real money**. Customers get charged without receiving products, inventory counts go negative, financial reports don't balance. This is the single most important architectural challenge in microservices.

---

### Interviewer Expectations

- **SAGA Pattern**: Know BOTH choreography and orchestration approaches. Explain compensating transactions. Know when to choose each. This is the #1 most asked microservices interview question for Lead roles.
- **Two-Phase Commit (2PC)**: Know how it works (prepare phase + commit phase). Explain why it's problematic (blocking, coordinator is SPOF, performance). Know when it IS appropriate (within a single database cluster, XA transactions).
- **Eventual Consistency**: Define it precisely. Explain how the system converges to a consistent state. Discuss compensation, idempotency, and conflict resolution.
- **Keywords**: "Compensating transaction", "orchestrator", "choreography", "event sourcing", "idempotency key", "outbox pattern", "distributed transaction", "BASE (Basically Available, Soft state, Eventual consistency)", "dual-write problem".

---

### The Deep Dive & Solution

#### SAGA Pattern — Choreography vs. Orchestration

A SAGA is a sequence of **local transactions**, where each local transaction updates its own service's database and publishes an event or message to trigger the next step. If a step fails, previously completed steps are undone through **compensating transactions**.

**Key insight**: A SAGA provides **eventual consistency** (not ACID). There is a window of time where the system is in an inconsistent state. Your business logic must tolerate this.

##### Choreography-based SAGA

In choreography, **there is no central coordinator**. Each service listens for events, performs its action, and publishes the result as a new event. Services are loosely coupled and communicate only through events.

```
Order Created   →   Inventory Reserved   →   Payment Charged   →   Order Confirmed
      │                    │                       │                      │
      │                    │                       │                      │
      ▼                    ▼                       ▼                      ▼
Order Service        Inventory Service       Payment Service        Order Service
  publishes:           listens for:            listens for:          listens for:
  "OrderCreated"       "OrderCreated"          "InventoryReserved"   "PaymentCharged"
                       publishes:              publishes:            publishes:
                       "InventoryReserved"     "PaymentCharged"      "OrderConfirmed"

FAILURE PATH (Compensating Transactions):
Payment fails → publishes "PaymentFailed"
  → Inventory Service listens → releases reservation → publishes "InventoryReleased"
  → Order Service listens → cancels order → publishes "OrderCancelled"
```

**Implementation with Spring Boot and Kafka**:

```java
// ========== ORDER SERVICE ==========
@Service
public class OrderService {
    
    @Autowired private OrderRepository orderRepository;
    @Autowired private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = Order.builder()
            .customerId(request.getCustomerId())
            .items(request.getItems())
            .status(OrderStatus.PENDING)
            .build();
        
        order = orderRepository.save(order);
        
        // Publish event to start the SAGA
        kafkaTemplate.send("order-events", new OrderCreatedEvent(
            order.getId(), order.getCustomerId(), order.getItems(), order.getTotalAmount()
        ));
        
        return order;
    }
    
    // Listen for completion or failure
    @KafkaListener(topics = "payment-events", groupId = "order-service")
    public void handlePaymentEvent(PaymentEvent event) {
        if (event instanceof PaymentChargedEvent) {
            orderRepository.updateStatus(event.getOrderId(), OrderStatus.CONFIRMED);
            kafkaTemplate.send("order-events", new OrderConfirmedEvent(event.getOrderId()));
        } else if (event instanceof PaymentFailedEvent) {
            orderRepository.updateStatus(event.getOrderId(), OrderStatus.CANCELLED);
            kafkaTemplate.send("order-events", new OrderCancelledEvent(event.getOrderId()));
        }
    }
}

// ========== INVENTORY SERVICE ==========
@Service
public class InventoryService {
    
    @Autowired private InventoryRepository inventoryRepository;
    @Autowired private KafkaTemplate<String, InventoryEvent> kafkaTemplate;
    
    @KafkaListener(topics = "order-events", groupId = "inventory-service")
    public void handleOrderEvent(OrderEvent event) {
        if (event instanceof OrderCreatedEvent orderCreated) {
            try {
                // Local transaction: reserve inventory
                reserveInventory(orderCreated.getItems());
                
                kafkaTemplate.send("inventory-events", new InventoryReservedEvent(
                    orderCreated.getOrderId(), orderCreated.getItems()
                ));
            } catch (InsufficientStockException e) {
                kafkaTemplate.send("inventory-events", new InventoryReservationFailedEvent(
                    orderCreated.getOrderId(), e.getMessage()
                ));
            }
        } else if (event instanceof OrderCancelledEvent orderCancelled) {
            // COMPENSATING TRANSACTION: release the reserved inventory
            releaseInventory(orderCancelled.getOrderId());
        }
    }
    
    @Transactional
    private void reserveInventory(List<OrderItem> items) {
        for (OrderItem item : items) {
            int updated = inventoryRepository.decrementStock(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new InsufficientStockException("Product " + item.getProductId());
            }
        }
    }
    
    @Transactional
    private void releaseInventory(String orderId) {
        // Compensating transaction — undo the reservation
        List<Reservation> reservations = reservationRepository.findByOrderId(orderId);
        for (Reservation r : reservations) {
            inventoryRepository.incrementStock(r.getProductId(), r.getQuantity());
        }
        reservationRepository.deleteByOrderId(orderId);
    }
}

// ========== PAYMENT SERVICE ==========
@Service
public class PaymentService {
    
    @KafkaListener(topics = "inventory-events", groupId = "payment-service")
    public void handleInventoryEvent(InventoryEvent event) {
        if (event instanceof InventoryReservedEvent inventoryReserved) {
            try {
                chargeCustomer(inventoryReserved.getOrderId());
                kafkaTemplate.send("payment-events", new PaymentChargedEvent(
                    inventoryReserved.getOrderId()
                ));
            } catch (PaymentException e) {
                kafkaTemplate.send("payment-events", new PaymentFailedEvent(
                    inventoryReserved.getOrderId(), e.getMessage()
                ));
            }
        }
    }
}
```

**Pros of Choreography**:
- Simple to implement for small SAGAs (2-4 steps).
- No single point of failure (no central coordinator).
- Services are truly decoupled.

**Cons of Choreography**:
- Hard to understand the overall flow — the business logic is scattered across multiple services.
- Difficult to debug when something goes wrong (which service dropped the event?).
- Risk of cyclic dependencies between services.
- Adding a new step requires modifying multiple services.
- No centralized view of the SAGA's current state.

##### Orchestration-based SAGA

In orchestration, a **central orchestrator** (SAGA coordinator) manages the entire workflow. It tells each service what to do and handles failures by invoking compensating transactions in reverse order. The orchestrator holds the state of the SAGA.

```
                    ┌──────────────────────┐
                    │    SAGA Orchestrator   │
                    │   (Order Saga Service) │
                    └──────────┬───────────┘
                               │
           ┌───────────────────┼───────────────────┐
           │                   │                    │
     Step 1: Reserve     Step 2: Charge       Step 3: Confirm
     Inventory           Payment              Order
           │                   │                    │
           ▼                   ▼                    ▼
    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
    │  Inventory   │    │   Payment   │    │    Order    │
    │  Service     │    │   Service   │    │   Service   │
    └─────────────┘    └─────────────┘    └─────────────┘

FAILURE AT STEP 2 (Payment fails):
  Orchestrator → Inventory Service: "Release reservation" (compensate Step 1)
  Orchestrator → Order Service: "Cancel order" (compensate Step 0)
```

**Implementation**:

```java
// SAGA Orchestrator — manages the workflow
@Service
public class CreateOrderSaga {
    
    @Autowired private InventoryClient inventoryClient;
    @Autowired private PaymentClient paymentClient;
    @Autowired private OrderRepository orderRepository;
    @Autowired private SagaStateRepository sagaStateRepository;
    
    public Order execute(CreateOrderRequest request) {
        // Create SAGA state for tracking
        SagaState saga = SagaState.builder()
            .sagaId(UUID.randomUUID().toString())
            .type("CREATE_ORDER")
            .status(SagaStatus.STARTED)
            .data(serialize(request))
            .build();
        sagaStateRepository.save(saga);
        
        Order order = null;
        
        try {
            // Step 1: Create order in PENDING state
            order = createPendingOrder(request);
            saga.setCurrentStep("ORDER_CREATED");
            sagaStateRepository.save(saga);
            
            // Step 2: Reserve inventory
            inventoryClient.reserve(new ReserveRequest(
                order.getId(), request.getItems()
            ));
            saga.setCurrentStep("INVENTORY_RESERVED");
            sagaStateRepository.save(saga);
            
            // Step 3: Charge payment
            paymentClient.charge(new ChargeRequest(
                order.getId(), 
                request.getCustomerId(), 
                order.getTotalAmount(),
                UUID.randomUUID().toString()  // Idempotency key
            ));
            saga.setCurrentStep("PAYMENT_CHARGED");
            sagaStateRepository.save(saga);
            
            // Step 4: Confirm order
            order.setStatus(OrderStatus.CONFIRMED);
            orderRepository.save(order);
            saga.setStatus(SagaStatus.COMPLETED);
            sagaStateRepository.save(saga);
            
            return order;
            
        } catch (Exception e) {
            log.error("SAGA failed at step: {}. Starting compensation.", 
                      saga.getCurrentStep(), e);
            compensate(saga, order);
            throw new OrderCreationFailedException("Order creation failed", e);
        }
    }
    
    private void compensate(SagaState saga, Order order) {
        saga.setStatus(SagaStatus.COMPENSATING);
        sagaStateRepository.save(saga);
        
        // Compensate in REVERSE order
        try {
            switch (saga.getCurrentStep()) {
                case "PAYMENT_CHARGED":
                    paymentClient.refund(order.getId());  // Compensate payment
                    // fall through to next compensation
                case "INVENTORY_RESERVED":
                    inventoryClient.release(order.getId());  // Compensate inventory
                    // fall through
                case "ORDER_CREATED":
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.save(order);
                    break;
            }
            saga.setStatus(SagaStatus.COMPENSATED);
        } catch (Exception compensationError) {
            log.error("COMPENSATION FAILED! Manual intervention required. SagaId: {}", 
                      saga.getSagaId(), compensationError);
            saga.setStatus(SagaStatus.COMPENSATION_FAILED);
            // Alert operations team — this is a critical inconsistency
        }
        sagaStateRepository.save(saga);
    }
}
```

**Pros of Orchestration**:
- Easy to understand — the entire workflow is in one place.
- Easy to add new steps — modify the orchestrator, other services are unaware.
- Centralized SAGA state — easy to monitor and debug.
- No cyclic dependencies.

**Cons of Orchestration**:
- The orchestrator is a potential single point of failure (mitigate with replicas).
- Risk of the orchestrator becoming a "God service" with too much logic.
- Tighter coupling to the orchestrator.

**When to choose which**:
- **Choreography**: Simple SAGAs (2-3 steps), truly decoupled event-driven architecture, teams want full autonomy.
- **Orchestration**: Complex SAGAs (4+ steps), need centralized monitoring, complex compensation logic, the SAGA involves conditional branching.

#### The Outbox Pattern — Solving the Dual-Write Problem

A critical problem in SAGAs: In the Order Service, you need to **save the order to the database** AND **publish an event to Kafka**. These are two different systems. If the database save succeeds but Kafka publish fails (network error), you have an order in the database but no event — the SAGA never starts. If Kafka publish succeeds but the database save fails, you have a phantom event for an order that doesn't exist.

This is the **dual-write problem**: you cannot atomically write to two different systems.

**Solution — Transactional Outbox**:

1. Instead of publishing directly to Kafka, write the event to an `outbox` table in the SAME database as the business data, in the SAME transaction.
2. A separate process (CDC — Change Data Capture, e.g., Debezium) reads the outbox table and publishes events to Kafka.
3. Since the business data and the outbox entry are written in one ACID transaction, they are guaranteed to be consistent.

```sql
-- Outbox table
CREATE TABLE outbox_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(255) NOT NULL,  -- e.g., "Order"
    aggregate_id VARCHAR(255) NOT NULL,    -- e.g., order ID
    event_type VARCHAR(255) NOT NULL,      -- e.g., "OrderCreated"
    payload JSONB NOT NULL,                -- serialized event data
    created_at TIMESTAMP DEFAULT NOW(),
    published BOOLEAN DEFAULT FALSE
);
```

```java
@Service
public class OrderService {
    
    @Autowired private OrderRepository orderRepository;
    @Autowired private OutboxRepository outboxRepository;
    
    @Transactional  // SINGLE transaction for both writes
    public Order createOrder(CreateOrderRequest request) {
        // Write 1: Save order
        Order order = orderRepository.save(Order.from(request));
        
        // Write 2: Save event to outbox (same transaction!)
        outboxRepository.save(OutboxEvent.builder()
            .aggregateType("Order")
            .aggregateId(order.getId())
            .eventType("OrderCreated")
            .payload(objectMapper.writeValueAsString(
                new OrderCreatedEvent(order.getId(), order.getItems(), order.getTotalAmount())
            ))
            .build());
        
        return order;
        // When this transaction commits, BOTH the order and the outbox event are persisted.
        // Debezium (CDC) detects the new row in outbox_events and publishes it to Kafka.
    }
}
```

#### Two-Phase Commit (2PC) — How It Works and Why We Avoid It

2PC is a protocol that ensures all participants in a distributed transaction either ALL commit or ALL abort. It requires a **Transaction Coordinator**.

**Phase 1 — Prepare (Voting)**:
1. The coordinator sends a `PREPARE` message to all participants.
2. Each participant executes the transaction up to the point of committing, writes redo/undo logs, and acquires all necessary locks.
3. Each participant responds with `VOTE_COMMIT` (ready to commit) or `VOTE_ABORT` (something went wrong).

**Phase 2 — Commit (Decision)**:
- If ALL participants voted `COMMIT`: The coordinator sends `GLOBAL_COMMIT` to all. Each participant commits and releases locks.
- If ANY participant voted `ABORT`: The coordinator sends `GLOBAL_ABORT` to all. Each participant rolls back and releases locks.

```
Coordinator          Participant A         Participant B
    │                     │                      │
    │──── PREPARE ───────>│                      │
    │──── PREPARE ────────┼─────────────────────>│
    │                     │                      │
    │<── VOTE_COMMIT ─────│                      │
    │<── VOTE_COMMIT ─────┼──────────────────────│
    │                     │                      │
    │── GLOBAL_COMMIT ───>│                      │
    │── GLOBAL_COMMIT ────┼─────────────────────>│
    │                     │                      │
    │<── ACK ─────────────│                      │
    │<── ACK ─────────────┼──────────────────────│
```

**Why we usually avoid 2PC in microservices**:

1. **Blocking**: During the prepare phase, participants hold database locks and wait for the coordinator's decision. If the coordinator crashes, participants are stuck holding locks indefinitely (the "blocking problem"). This destroys throughput.
2. **Single point of failure**: If the coordinator goes down between the PREPARE and COMMIT phases, participants are in an uncertain state — they cannot commit or abort independently.
3. **Performance**: The protocol requires multiple network round-trips and holding locks across the entire prepare-commit cycle. Latency is high, throughput is low.
4. **Coupling**: All participants must support the XA (eXtended Architecture) protocol. Not all databases/message brokers support it, and those that do often have performance penalties.
5. **Not suitable for inter-service communication**: 2PC works within a single application connecting to multiple databases (e.g., JTA/XA transactions) but is impractical across network boundaries in microservices.

**When 2PC IS appropriate**:
- Within a single service that needs to write to two databases atomically (e.g., a relational DB and a message broker that supports XA).
- Database clusters that natively use 2PC internally (e.g., distributed databases like CockroachDB, Google Spanner use variants of 2PC within their cluster).

#### Eventual Consistency — What It Means In Practice

**Definition**: A system is eventually consistent if, in the absence of new updates, all replicas/services will eventually converge to the same state. At any given moment, different parts of the system may see different versions of the data, but they will eventually agree.

**In practice with the SAGA pattern**:
- When an order is created, the Order Service sets status = PENDING. At this point, the Inventory Service hasn't reserved the stock yet. The system is **temporarily inconsistent**: the order exists but inventory hasn't been updated.
- After the Inventory Service processes the event and reserves stock, that part becomes consistent. But the Payment Service hasn't charged yet.
- After all steps complete, the system is **fully consistent**.
- If a step fails and compensating transactions run, the system converges to a consistent "cancelled" state.

**The window of inconsistency** is typically milliseconds to seconds, but can be longer if services are slow or there's a backlog in the message queue.

**Designing for eventual consistency**:
1. **Idempotency**: Every service operation must be idempotent — processing the same event twice must produce the same result. Use idempotency keys.
   ```java
   @Transactional
   public void processPayment(PaymentEvent event) {
       // Check if already processed
       if (processedEventRepository.existsByEventId(event.getEventId())) {
           log.info("Event {} already processed, skipping", event.getEventId());
           return;
       }
       // Process payment
       // ...
       // Record that this event was processed
       processedEventRepository.save(new ProcessedEvent(event.getEventId()));
   }
   ```
2. **Read-your-writes consistency**: After creating an order, the user should see their order. Use the Order Service's own database (source of truth) for reads immediately after writes, not a read replica that may lag.
3. **Conflict resolution**: When two services update the same logical entity based on stale data, use techniques like last-writer-wins (simple but lossy), merge functions, or CRDTs (Conflict-free Replicated Data Types).

---

## 6.3 Communication: REST vs. gRPC, and Message Queues (Kafka/RabbitMQ)

---

### The "Why" & The Problem

Microservices must communicate. The choice of communication protocol profoundly affects latency, throughput, developer experience, and system complexity. There are two fundamental communication styles:

1. **Synchronous** (request-response): The caller sends a request and **waits** for a response (REST, gRPC). Used when the caller needs the result immediately.
2. **Asynchronous** (event-driven): The caller sends a message to a broker and **does not wait** (Kafka, RabbitMQ). Used when the caller doesn't need an immediate response, or when you need to decouple producers from consumers.

A company pays you to know this because choosing the wrong communication pattern leads to:
- **REST everywhere**: High latency from sequential synchronous calls, cascading failures, tight coupling.
- **Kafka everywhere**: Over-complexity, harder debugging, eventual consistency where you actually needed strong consistency.
- **No gRPC when needed**: 10x more bandwidth consumed than necessary in high-throughput internal communication.

---

### Interviewer Expectations

- **REST vs. gRPC**: Compare them on serialization format, performance, streaming, contract enforcement, and browser support. Don't just say "gRPC is faster" — explain WHY (binary Protocol Buffers, HTTP/2 multiplexing, header compression).
- **Kafka deep dive**: Consumer groups, partitions, offsets, at-least-once vs. exactly-once semantics, idempotent producers, compacted topics.
- **RabbitMQ**: When to choose it over Kafka. Exchange types (direct, topic, fanout, headers). Acknowledgments and dead-letter queues.
- **Keywords**: "Protocol Buffers (protobuf)", "HTTP/2 multiplexing", "binary serialization", "consumer group rebalancing", "partition key", "offset commit", "at-least-once delivery", "dead-letter queue (DLQ)", "backpressure", "fan-out".

---

### The Deep Dive & Solution

#### REST vs. gRPC — Performance Trade-offs

| Aspect | REST (HTTP/1.1 + JSON) | gRPC (HTTP/2 + Protobuf) |
|--------|----------------------|-------------------------|
| **Serialization** | JSON (text, human-readable, ~2x-10x larger) | Protocol Buffers (binary, compact, ~2x-10x smaller) |
| **Transport** | HTTP/1.1 (one request per connection, or HTTP/2 for some implementations) | HTTP/2 (multiplexed streams on one connection, header compression) |
| **Contract** | OpenAPI/Swagger (loosely enforced) | `.proto` files (strongly typed, code generated) |
| **Streaming** | Limited (SSE, WebSocket workarounds) | Native bidirectional streaming |
| **Browser support** | Universal | Requires gRPC-Web proxy (not native) |
| **Latency** | Higher (JSON parse overhead, text transfer) | Lower (~2-5x faster for small payloads) |
| **Tooling** | Curl, Postman, browser — easy to debug | Requires grpcurl, Evans — harder to debug |
| **Use case** | Public APIs, frontend-to-backend | Internal service-to-service, high-throughput |

**Why gRPC is faster**:
1. **Protocol Buffers**: Binary serialization. A JSON payload of `{"userId": 12345, "name": "John"}` might be 40 bytes as text. The same data in protobuf might be 12 bytes. Less data = less network I/O = faster.
2. **HTTP/2 Multiplexing**: Multiple gRPC calls share a single TCP connection. HTTP/1.1 needs a new connection (or connection reuse with head-of-line blocking) per request.
3. **HTTP/2 Header Compression (HPACK)**: Repeated headers (like `Content-Type`, `Authorization`) are compressed across requests on the same connection.
4. **Code generation**: The `.proto` file generates client/server stubs. No hand-written HTTP clients. Type safety at compile time.

**gRPC with Spring Boot**:

```protobuf
// order_service.proto
syntax = "proto3";
package com.myapp.order;

service OrderService {
    rpc GetOrder (GetOrderRequest) returns (OrderResponse);
    rpc StreamOrders (StreamOrdersRequest) returns (stream OrderResponse);  // Server streaming
}

message GetOrderRequest {
    string order_id = 1;
}

message OrderResponse {
    string order_id = 1;
    string customer_id = 2;
    double total_amount = 3;
    OrderStatus status = 4;
    repeated OrderItem items = 5;
}

enum OrderStatus {
    PENDING = 0;
    CONFIRMED = 1;
    SHIPPED = 2;
    CANCELLED = 3;
}

message OrderItem {
    string product_id = 1;
    int32 quantity = 2;
    double price = 3;
}
```

```java
// gRPC Server implementation
@GrpcService
public class OrderGrpcService extends OrderServiceGrpc.OrderServiceImplBase {
    
    @Autowired private OrderRepository orderRepository;
    
    @Override
    public void getOrder(GetOrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new StatusRuntimeException(
                Status.NOT_FOUND.withDescription("Order not found: " + request.getOrderId())
            ));
        
        OrderResponse response = OrderResponse.newBuilder()
            .setOrderId(order.getId())
            .setCustomerId(order.getCustomerId())
            .setTotalAmount(order.getTotalAmount())
            .setStatus(mapStatus(order.getStatus()))
            .addAllItems(mapItems(order.getItems()))
            .build();
        
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
    
    @Override
    public void streamOrders(StreamOrdersRequest request, 
                              StreamObserver<OrderResponse> responseObserver) {
        // Server streaming — push orders as they're created
        orderRepository.findByCustomerIdStream(request.getCustomerId())
            .forEach(order -> {
                responseObserver.onNext(toProto(order));
            });
        responseObserver.onCompleted();
    }
}
```

**When to use REST**: Public APIs, browser-facing endpoints, simple CRUD operations, when human-readability of payloads matters for debugging.

**When to use gRPC**: Internal service-to-service communication, high-throughput/low-latency requirements, streaming use cases, polyglot environments where strong contracts matter.

#### Apache Kafka — Deep Dive

Kafka is a **distributed event streaming platform**. It is not a traditional message queue — it is a **distributed commit log**.

**Core concepts**:

```
┌─────────────────────────────────────────────────────────────┐
│                        KAFKA CLUSTER                         │
│                                                               │
│  Topic: "order-events"                                       │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │  Partition 0:  [msg0] [msg3] [msg6] [msg9]  ...        │ │
│  │  Partition 1:  [msg1] [msg4] [msg7] [msg10] ...        │ │
│  │  Partition 2:  [msg2] [msg5] [msg8] [msg11] ...        │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                               │
│  Consumer Group "order-processing"                            │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │
│  │ Consumer A    │ │ Consumer B    │ │ Consumer C    │        │
│  │ (Partition 0) │ │ (Partition 1) │ │ (Partition 2) │        │
│  └──────────────┘ └──────────────┘ └──────────────┘        │
│                                                               │
│  Consumer Group "analytics"                                   │
│  ┌──────────────┐ ┌──────────────┐                          │
│  │ Consumer X    │ │ Consumer Y    │                          │
│  │ (Part 0 + 1) │ │ (Partition 2) │                          │
│  └──────────────┘ └──────────────┘                          │
└─────────────────────────────────────────────────────────────┘
```

**Topics**: Logical channels for messages. Like a database table.
**Partitions**: A topic is split into partitions for parallelism. Each partition is an ordered, immutable sequence of records (a commit log). The **partition key** determines which partition a message goes to (hash of key mod number of partitions).
**Offsets**: Each message in a partition has a sequential offset (0, 1, 2, ...). Consumers track their position using offsets. They can re-read messages by resetting their offset (this is unique to Kafka — traditional queues delete messages after consumption).
**Consumer Groups**: A group of consumers that cooperate to consume a topic. Each partition is assigned to exactly one consumer within a group. This enables parallel processing. If you have 3 partitions and 3 consumers in a group, each consumer handles one partition. If a consumer dies, its partitions are reassigned to the remaining consumers (**rebalancing**).

**Multiple consumer groups** can independently consume the same topic. Group "order-processing" and group "analytics" both read all messages — they maintain separate offsets.

**Idempotency in Kafka**:

The default delivery guarantee is **at-least-once**: if a consumer processes a message but crashes before committing its offset, it will re-process the message after restart. Your consumer MUST be idempotent.

```java
@Service
public class OrderEventConsumer {
    
    @Autowired private ProcessedEventRepository processedEvents;
    @Autowired private OrderRepository orderRepository;
    
    @KafkaListener(
        topics = "order-events",
        groupId = "order-processing",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(ConsumerRecord<String, OrderEvent> record) {
        String eventId = record.headers().lastHeader("event-id").value().toString();
        
        // Idempotency check — prevent duplicate processing
        if (processedEvents.existsById(eventId)) {
            log.info("Event {} already processed at offset {}, skipping", 
                     eventId, record.offset());
            return;
        }
        
        // Process the event
        OrderEvent event = record.value();
        processOrderEvent(event);
        
        // Mark as processed
        processedEvents.save(new ProcessedEvent(eventId, Instant.now()));
    }
}
```

**Kafka Producer with idempotency and exactly-once semantics**:
```yaml
# application.yml
spring:
  kafka:
    producer:
      bootstrap-servers: kafka:9092
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        enable.idempotence: true            # Prevents duplicate messages from producer retries
        acks: all                           # All replicas must acknowledge
        max.in.flight.requests.per.connection: 5  # Allows 5 in-flight requests with idempotency
    consumer:
      bootstrap-servers: kafka:9092
      group-id: order-processing
      auto-offset-reset: earliest          # Start from beginning if no committed offset
      enable-auto-commit: false            # Manual offset commit for reliability
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer
      properties:
        isolation.level: read_committed    # Only read committed transactional messages
```

#### Kafka vs. RabbitMQ — When to Use Which

| Aspect | Kafka | RabbitMQ |
|--------|-------|----------|
| **Model** | Distributed log (messages retained) | Message queue (messages deleted after consumption) |
| **Ordering** | Guaranteed within a partition | Guaranteed within a queue |
| **Replay** | Yes — consumers can re-read by resetting offsets | No — once consumed and ack'd, gone |
| **Throughput** | Millions of messages/sec (append-only log) | Tens of thousands/sec (broker does more work) |
| **Routing** | Simple topic/partition (key-based) | Rich routing (direct, topic, fanout, headers exchanges) |
| **Use case** | Event streaming, event sourcing, high-throughput data pipelines | Task queues, RPC, complex routing, low-latency |
| **Consumer model** | Pull (consumers poll for messages) | Push (broker pushes to consumers) |
| **Retention** | Time-based or size-based (can keep forever) | Until consumed and acknowledged |

**Choose Kafka when**: Event sourcing, audit logs, high-throughput data pipelines, you need message replay, multiple independent consumers need the same events.

**Choose RabbitMQ when**: Task queue/work queue pattern, complex routing logic, low-latency point-to-point messaging, RPC-style communication, simpler operational requirements.

**RabbitMQ Exchange Types**:
- **Direct**: Routes messages to queues whose binding key exactly matches the routing key. Like a point-to-point channel.
- **Topic**: Routes based on pattern matching (`order.created.us` matches `order.created.*` and `order.#`). Flexible pub-sub.
- **Fanout**: Broadcasts to ALL bound queues, ignoring the routing key. Pure pub-sub.
- **Headers**: Routes based on message header attributes instead of routing key. Most flexible but least common.

**Dead Letter Queue (DLQ)**:
When a message cannot be processed after maximum retries, it is sent to a DLQ for manual investigation or automated retry later.

```java
// Kafka DLQ with Spring
@Configuration
public class KafkaConfig {
    
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        // After 3 retries with exponential backoff, send to DLQ
        DeadLetterPublishingRecoverer recoverer = 
            new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
        
        return new DefaultErrorHandler(recoverer, 
            new FixedBackOff(1000L, 3L));  // 1 sec delay, 3 attempts
    }
}
```

