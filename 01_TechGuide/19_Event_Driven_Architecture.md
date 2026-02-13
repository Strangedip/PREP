# Section 19: Event-Driven Architecture & Messaging

> **Level**: MID+ (Kafka basics) to LEAD (Event Sourcing, CQRS, Schema Evolution, exactly-once)
> **Why This Matters**: Event-driven architecture is the backbone of modern microservices. Every Lead engineer must understand Kafka deeply, design event schemas, and know when to apply Event Sourcing vs. traditional CRUD.

---

## 19.1 Event-Driven Architecture Patterns

### The "Why" & The Problem

Synchronous (REST) communication between microservices creates:
- **Tight coupling** — Service A must know Service B's API
- **Temporal coupling** — Service B must be available when A calls it
- **Cascading failures** — If B is down, A fails too
- **Scalability limits** — A must wait for B to respond

Event-driven architecture decouples services through asynchronous messaging.

### The Deep Dive & Solution

#### Three Core Patterns

```
1. EVENT NOTIFICATION
   - Producer publishes event: "OrderCreated { orderId: 123 }"
   - Consumers react independently
   - Producers don't know (or care) who consumes
   - Minimal payload — just the fact that something happened
   
   Example: Order Service → "OrderCreated" → 
            Inventory Service (reserve stock)
            Notification Service (send email)
            Analytics Service (track metrics)

2. EVENT-CARRIED STATE TRANSFER
   - Event contains ALL data consumers need
   - Consumers maintain their own copy of the data
   - No need to call back to the producer
   - Eventual consistency between services
   
   Example: "CustomerUpdated { id: 42, name: 'Sandip', email: 'sandip@...', address: {...} }"
   - Order Service stores customer info locally
   - No need to call Customer Service at order time

3. EVENT SOURCING (see Section 19.4)
   - Store events as the source of truth (not current state)
   - Current state = replay all events from the beginning
   - Full audit trail, time travel, debugging
```

#### Message vs Event vs Command

```
| Concept | Description                          | Delivery          | Example                          |
|---------|--------------------------------------|-------------------|----------------------------------|
| Command | Request to DO something              | Point-to-point    | "CreateOrder { ... }"            |
| Event   | Notification that something HAPPENED | Pub/Sub (1:many)  | "OrderCreated { orderId: 123 }"  |
| Message | Generic unit of communication        | Either            | Both commands and events          |

Naming conventions:
- Commands: imperative verb → "CreateOrder", "SendEmail", "ReserveInventory"
- Events: past tense → "OrderCreated", "EmailSent", "InventoryReserved"
- Queries: question → "GetOrder", "FindCustomer"
```

---

## 19.2 Apache Kafka — Deep Dive

### Architecture

```
┌─────────── Kafka Cluster ──────────────────────────────────┐
│                                                             │
│  Topic: "order-events"                                      │
│  ┌─────────────────────────────────────────────┐            │
│  │ Partition 0: [msg0][msg3][msg6][msg9]...    │  Broker 1  │
│  │ Partition 1: [msg1][msg4][msg7][msg10]...   │  Broker 2  │
│  │ Partition 2: [msg2][msg5][msg8][msg11]...   │  Broker 3  │
│  └─────────────────────────────────────────────┘            │
│                                                             │
│  Consumer Group: "order-processor"                           │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ Consumer 1  │ │ Consumer 2  │ │ Consumer 3  │           │
│  │ (reads P0)  │ │ (reads P1)  │ │ (reads P2)  │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
│                                                             │
│  Consumer Group: "analytics"                                 │
│  ┌─────────────┐                                            │
│  │ Consumer 1  │ (reads ALL partitions — only 1 consumer)   │
│  └─────────────┘                                            │
└─────────────────────────────────────────────────────────────┘

Key concepts:
- Topic: Named feed of messages (like a table in a database)
- Partition: Ordered, immutable sequence of messages (parallelism unit)
- Offset: Position of a message within a partition (monotonically increasing)
- Consumer Group: Set of consumers sharing the work of consuming a topic
  - Each partition is consumed by exactly ONE consumer in the group
  - Max parallelism = number of partitions
- Replication Factor: How many copies of each partition (3 = survive 2 broker failures)
- Leader: One replica handles all reads/writes; others are followers
```

### Spring Boot + Kafka Producer

```java
@Configuration
public class KafkaProducerConfig {
    
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        
        // Reliability settings:
        config.put(ProducerConfig.ACKS_CONFIG, "all");           // Wait for all replicas
        config.put(ProducerConfig.RETRIES_CONFIG, 3);            // Retry on transient failure
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true); // Exactly-once semantics
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5); // With idempotence
        
        // Performance settings:
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);     // Batch 16KB of messages
        config.put(ProducerConfig.LINGER_MS_CONFIG, 5);          // Wait up to 5ms for batch
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy"); // Compress batches
        
        return new DefaultKafkaProducerFactory<>(config);
    }
    
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

@Service
@RequiredArgsConstructor
public class OrderEventPublisher {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    public void publishOrderCreated(Order order) {
        OrderCreatedEvent event = new OrderCreatedEvent(
            order.getId(),
            order.getCustomerId(),
            order.getAmount(),
            order.getStatus(),
            Instant.now()
        );
        
        // Key = customerId → all orders for same customer go to same partition
        // This guarantees ordering per customer
        CompletableFuture<SendResult<String, Object>> future = 
            kafkaTemplate.send("order-events", order.getCustomerId().toString(), event);
        
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish OrderCreated for order {}", order.getId(), ex);
                // Retry logic, dead letter queue, or alert
            } else {
                log.info("Published OrderCreated for order {} to partition {} offset {}",
                    order.getId(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
            }
        });
    }
}
```

### Spring Boot + Kafka Consumer

```java
@Configuration
public class KafkaConsumerConfig {
    
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "order-processor");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        
        // Reliability settings:
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"); // Start from beginning if no offset
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);     // Manual commit
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500);         // Max records per poll
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 300000);  // 5 min max processing time
        
        // Trusted packages for deserialization:
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.events");
        
        return new DefaultKafkaConsumerFactory<>(config);
    }
    
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, Object>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(3); // 3 consumer threads (match partition count)
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        
        // Error handling:
        factory.setCommonErrorHandler(new DefaultErrorHandler(
            new DeadLetterPublishingRecoverer(kafkaTemplate), // Send to DLT after retries
            new FixedBackOff(1000, 3) // 3 retries with 1s delay
        ));
        
        return factory;
    }
}

@Component
public class OrderEventConsumer {
    
    @KafkaListener(
        topics = "order-events",
        groupId = "order-processor",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderEvent(
            @Payload OrderCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset,
            Acknowledgment ack) {
        
        try {
            log.info("Processing OrderCreated: {} from partition {} offset {}", 
                event.orderId(), partition, offset);
            
            // Process the event (idempotently!)
            processOrderEvent(event);
            
            // Commit offset only after successful processing
            ack.acknowledge();
            
        } catch (Exception e) {
            log.error("Failed to process order event: {}", event.orderId(), e);
            // Don't acknowledge → will be retried or sent to DLT
            throw e;
        }
    }
    
    private void processOrderEvent(OrderCreatedEvent event) {
        // IDEMPOTENT processing — handle duplicates gracefully
        // Check if already processed (using event ID or order ID):
        if (processedEventRepository.existsByEventId(event.eventId())) {
            log.info("Event {} already processed — skipping", event.eventId());
            return;
        }
        
        // Process...
        inventoryService.reserveStock(event.orderId(), event.items());
        
        // Mark as processed
        processedEventRepository.save(new ProcessedEvent(event.eventId()));
    }
}
```

### Kafka Delivery Guarantees

```
| Guarantee        | acks Setting | Idempotence | Transactional | Duplicates? | Data Loss? |
|-----------------|-------------|-------------|---------------|-------------|------------|
| At-most-once    | 0 or 1      | No          | No            | No          | Possible   |
| At-least-once   | all         | No          | No            | Possible    | No         |
| Exactly-once    | all         | Yes         | Yes           | No          | No         |

How exactly-once works:
1. Idempotent Producer: Kafka assigns each producer a ProducerID and sequence number
   - If the same message is sent twice, Kafka deduplicates based on {PID, sequence}
2. Transactional Producer: Atomically write to multiple partitions
   - Either ALL messages in a transaction are committed, or NONE
3. Consumer: Read only committed messages (isolation.level = read_committed)
```

---

## 19.3 Schema Evolution — Avro & Schema Registry

### The Deep Dive & Solution

```
Problem: Events are contracts between services. If you change an event schema,
         you can break all consumers.

Solution: Schema Registry + backward/forward compatibility rules

Schema Compatibility Types:
1. BACKWARD (default) — new schema can read old data
   - Can add fields with defaults
   - Can remove fields
   
2. FORWARD — old schema can read new data
   - Can remove fields
   - Can add fields with defaults
   
3. FULL — both backward and forward compatible
   - Only add/remove OPTIONAL fields with defaults

4. NONE — no compatibility check (dangerous)
```

```java
// Using Avro with Schema Registry:

// 1. Define schema (order_created.avsc):
{
  "type": "record",
  "name": "OrderCreatedEvent",
  "namespace": "com.example.events",
  "fields": [
    {"name": "orderId", "type": "string"},
    {"name": "customerId", "type": "string"},
    {"name": "amount", "type": "double"},
    {"name": "currency", "type": "string", "default": "USD"},  // New field with default
    {"name": "items", "type": {"type": "array", "items": "string"}},
    {"name": "timestamp", "type": {"type": "long", "logicalType": "timestamp-millis"}}
  ]
}

// Producer config for Avro:
config.put("schema.registry.url", "http://schema-registry:8081");
config.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
config.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
config.put("auto.register.schemas", true);

// Consumer config for Avro:
config.put("schema.registry.url", "http://schema-registry:8081");
config.put("key.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
config.put("value.deserializer", "io.confluent.kafka.serializers.KafkaAvroDeserializer");
config.put("specific.avro.reader", true); // Use generated Java classes
```

---

## 19.4 Event Sourcing & CQRS

### Event Sourcing

```
Traditional CRUD:
  Database stores CURRENT state: { orderId: 123, status: "SHIPPED", amount: 99.99 }
  History is LOST — you can't see what changed or when

Event Sourcing:
  Database stores ALL events (append-only):
  [OrderCreated { orderId: 123, amount: 99.99 }]
  [PaymentReceived { orderId: 123, paymentId: "pay-1" }]
  [OrderConfirmed { orderId: 123 }]
  [ItemShipped { orderId: 123, trackingId: "track-1" }]
  
  Current state = replay all events → { status: "SHIPPED", amount: 99.99, ... }

Benefits:
- Complete audit trail (who did what, when)
- Time travel (reconstruct state at any point in time)
- Debugging (replay events to reproduce bugs)
- New projections (build new views from existing events)

Drawbacks:
- Complexity (eventual consistency, event schema evolution)
- Query difficulty (can't SELECT WHERE on current state directly)
- Storage growth (events accumulate forever)
- Framework/tooling needed (Axon, EventStoreDB)
```

### CQRS (Command Query Responsibility Segregation)

```
Traditional:
  Same model for reads AND writes → forces compromises on both

CQRS:
  WRITE side (Commands) → Optimized for writes (normalized, event store)
  READ side (Queries)   → Optimized for reads (denormalized, materialized views)

┌──────────────┐     Commands      ┌───────────────┐
│   Client     │ ────────────────> │  Write Model  │
│              │                   │  (Domain/ES)  │
│              │                   └───────┬───────┘
│              │                           │ Events
│              │                   ┌───────▼───────┐
│              │     Queries       │  Read Model   │
│              │ <──────────────── │  (Projections)│
└──────────────┘                   └───────────────┘

// Event Sourcing + CQRS with Axon Framework:
@Aggregate
public class OrderAggregate {
    
    @AggregateIdentifier
    private String orderId;
    private OrderStatus status;
    private BigDecimal amount;
    
    @CommandHandler
    public OrderAggregate(CreateOrderCommand cmd) {
        // Validate business rules
        if (cmd.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        // Apply event (don't mutate state in command handler!)
        AggregateLifecycle.apply(new OrderCreatedEvent(cmd.getOrderId(), cmd.getAmount()));
    }
    
    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        // Mutate state from event (this runs during replay too)
        this.orderId = event.getOrderId();
        this.status = OrderStatus.PENDING;
        this.amount = event.getAmount();
    }
    
    @CommandHandler
    public void handle(ConfirmOrderCommand cmd) {
        if (this.status != OrderStatus.PENDING) {
            throw new IllegalStateException("Order is not pending");
        }
        AggregateLifecycle.apply(new OrderConfirmedEvent(this.orderId));
    }
    
    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        this.status = OrderStatus.CONFIRMED;
    }
}

// Read side projection:
@Component
public class OrderProjection {
    
    @EventHandler
    public void on(OrderCreatedEvent event) {
        OrderView view = new OrderView(event.getOrderId(), event.getAmount(), "PENDING");
        orderViewRepository.save(view); // Denormalized read model
    }
    
    @EventHandler
    public void on(OrderConfirmedEvent event) {
        orderViewRepository.updateStatus(event.getOrderId(), "CONFIRMED");
    }
}
```

---

## 19.5 Dead Letter Queues & Error Handling

### The Deep Dive & Solution

```
When event processing fails, you need a strategy:

1. RETRY with backoff:
   - First failure: retry after 1s
   - Second failure: retry after 5s
   - Third failure: retry after 30s
   - After max retries: send to DLT (Dead Letter Topic)

2. DEAD LETTER TOPIC (DLT):
   - Failed messages go to topic: "order-events.DLT"
   - Contains original message + error details + retry count
   - Operations team can inspect, fix, and replay

3. POISON PILL HANDLING:
   - Message that can NEVER be processed (bad format, schema mismatch)
   - Skip it immediately (don't waste retries)
   - Log details for investigation
```

```java
// Spring Kafka error handling:
@Bean
public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
    // Dead letter publisher
    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
        (record, ex) -> new TopicPartition(record.topic() + ".DLT", record.partition()));
    
    // Backoff strategy: 3 retries with exponential backoff
    ExponentialBackOff backOff = new ExponentialBackOff(1000, 2.0); // 1s, 2s, 4s
    backOff.setMaxElapsedTime(30000); // Give up after 30s
    
    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, backOff);
    
    // Don't retry these (poison pills):
    handler.addNotRetryableExceptions(
        DeserializationException.class,  // Bad message format
        SchemaException.class,           // Schema mismatch
        ValidationException.class        // Business validation failure
    );
    
    return handler;
}

// DLT Consumer (for manual inspection and replay):
@KafkaListener(topics = "order-events.DLT", groupId = "dlt-processor")
public void handleDlt(
        ConsumerRecord<String, Object> record,
        @Header(KafkaHeaders.DLT_EXCEPTION_MESSAGE) String errorMessage,
        @Header(KafkaHeaders.DLT_ORIGINAL_TOPIC) String originalTopic) {
    
    log.error("DLT received: topic={}, key={}, error={}", 
        originalTopic, record.key(), errorMessage);
    
    // Store in DB for manual investigation
    dltRepository.save(new DltRecord(
        record.key(),
        originalTopic,
        record.value().toString(),
        errorMessage,
        Instant.now()
    ));
    
    // Alert operations team
    alertService.sendAlert("Dead letter received for " + originalTopic);
}
```

---

## 19.6 Saga Pattern — Distributed Transactions

### The Deep Dive & Solution

```
Problem: In microservices, you can't use traditional ACID transactions across services.
         How do you ensure data consistency across Order, Payment, and Inventory services?

Solution: Saga — a sequence of local transactions with compensating actions on failure.

Two Saga types:

1. CHOREOGRAPHY (event-driven, no central coordinator):
   Order Service → "OrderCreated" →
   Payment Service → "PaymentCompleted" →
   Inventory Service → "InventoryReserved" →
   Order Service → "OrderConfirmed"
   
   If Payment fails:
   Payment Service → "PaymentFailed" →
   Order Service → "OrderCancelled" (compensating action)

2. ORCHESTRATION (central coordinator):
   Saga Orchestrator:
     Step 1: Call Order Service → Create Order
     Step 2: Call Payment Service → Process Payment
     Step 3: Call Inventory Service → Reserve Stock
     On failure at Step 2:
       Compensate Step 1: Cancel Order
```

```java
// Saga Orchestrator Example:
@Service
public class CreateOrderSagaOrchestrator {
    
    public Mono<OrderResult> execute(CreateOrderCommand command) {
        return Mono.just(new SagaState(command))
            // Step 1: Create Order
            .flatMap(state -> orderService.createOrder(command)
                .map(order -> state.withOrder(order)))
            
            // Step 2: Reserve Inventory
            .flatMap(state -> inventoryService.reserve(state.getOrder().getItems())
                .map(reservation -> state.withReservation(reservation))
                .onErrorResume(e -> {
                    // Compensate: Cancel order
                    return orderService.cancelOrder(state.getOrder().getId())
                        .then(Mono.error(new SagaFailedException("Inventory reservation failed", e)));
                }))
            
            // Step 3: Process Payment
            .flatMap(state -> paymentService.charge(state.getOrder())
                .map(payment -> state.withPayment(payment))
                .onErrorResume(e -> {
                    // Compensate: Release inventory, cancel order
                    return inventoryService.release(state.getReservation().getId())
                        .then(orderService.cancelOrder(state.getOrder().getId()))
                        .then(Mono.error(new SagaFailedException("Payment failed", e)));
                }))
            
            // Step 4: Confirm Order
            .flatMap(state -> orderService.confirmOrder(state.getOrder().getId())
                .map(order -> OrderResult.success(order)));
    }
}
```

---

## 19.7 RabbitMQ vs Kafka — Decision Guide

```
| Feature              | Kafka                          | RabbitMQ                        |
|---------------------|--------------------------------|---------------------------------|
| Model               | Distributed log (append-only)  | Message broker (queue-based)    |
| Message retention   | Configurable (days/forever)    | Until consumed (then deleted)   |
| Consumer model      | Pull (consumers poll)          | Push (broker pushes to consumer)|
| Ordering            | Per-partition ordering          | Per-queue ordering (FIFO)       |
| Replay              | Yes (consumers can rewind)     | No (message deleted after ACK)  |
| Throughput          | Millions/sec (1 MB messages)   | Tens of thousands/sec           |
| Latency             | ~5ms (batching)                | ~1ms (immediate delivery)       |
| Protocol            | Custom binary                  | AMQP 0-9-1 (standard)          |
| Routing             | Topic-based                    | Exchange-based (direct, topic,  |
|                     |                                |  fanout, headers)               |
| Consumer groups     | Built-in                       | Competing consumers (manual)    |
| Exactly-once        | Yes (transactions)             | No (at-least-once)              |
| Use case            | Event streaming, event sourcing| Task queues, RPC, pub/sub       |
|                     | Log aggregation, CDC           | Job processing, notifications   |

When to use Kafka:
- Event streaming / event sourcing
- High throughput (>100K msg/sec)
- Need message replay
- Multiple consumer groups reading same data
- Data pipeline / CDC (Change Data Capture)

When to use RabbitMQ:
- Task queues / job processing
- Complex routing (headers, patterns)
- Low-latency requirement (<1ms)
- Request-reply pattern (RPC)
- Simple pub/sub with small volume
```

---

## 19.8 Change Data Capture (CDC)

### The Deep Dive & Solution

```
CDC captures database changes as events — without modifying application code.

Database → CDC Connector → Kafka → Consumers

Tools: Debezium (most popular), Maxwell, AWS DMS

How Debezium works:
1. Reads database transaction log (WAL in PostgreSQL, binlog in MySQL)
2. Converts each INSERT/UPDATE/DELETE into a Kafka event
3. Publishes to topic named: "dbserver1.schema.tablename"
```

```json
// Debezium event for a row update:
{
  "before": {
    "id": 123,
    "status": "PENDING",
    "amount": 99.99
  },
  "after": {
    "id": 123,
    "status": "CONFIRMED",
    "amount": 99.99
  },
  "source": {
    "version": "2.5.0",
    "connector": "postgresql",
    "name": "order-db",
    "ts_ms": 1707840000000,
    "db": "orders",
    "schema": "public",
    "table": "orders"
  },
  "op": "u",  // u=update, c=create, d=delete, r=read (snapshot)
  "ts_ms": 1707840000123
}
```

```
CDC Use Cases:
1. Microservice data sync (replicate data without shared database)
2. Cache invalidation (database changes → invalidate Redis cache)
3. Search indexing (database changes → update Elasticsearch)
4. Analytics pipeline (database changes → data warehouse)
5. Audit logging (capture all changes without application code changes)
6. Legacy migration (capture changes from monolith → feed new microservices)
```

---

## 19.9 Interview Quick Reference — Event-Driven Architecture

### Top Questions and One-Line Answers

| Question | Answer |
|----------|--------|
| Kafka vs RabbitMQ? | Kafka: high-throughput log streaming, replay. RabbitMQ: low-latency task queues, complex routing. |
| How does Kafka guarantee ordering? | Per-partition ordering. Use message key to route related messages to same partition. |
| What is a consumer group? | Set of consumers sharing partition assignments. Max parallelism = partition count. |
| Exactly-once in Kafka? | Idempotent producer (acks=all) + transactional producer + read_committed consumers. |
| What is Event Sourcing? | Store events (not current state) as the source of truth. Replay events to rebuild state. |
| What is CQRS? | Separate write model (commands) from read model (queries). Often combined with event sourcing. |
| What is a Saga? | Distributed transaction using local transactions + compensating actions on failure. |
| Choreography vs Orchestration? | Choreography: event-driven, no coordinator. Orchestration: central coordinator manages steps. |
| What is CDC? | Change Data Capture — capture database changes as events (via transaction log). |
| How to handle poison pills? | Mark as non-retryable → send directly to DLT → don't block other messages. |
| What is a Dead Letter Queue? | Topic for messages that failed processing after all retries. Inspected by ops team. |
| Schema evolution? | Use Avro + Schema Registry with backward/forward compatibility rules. |

### Keywords to Use in Interviews

```
Kafka: Partition, Offset, Consumer Group, Rebalancing, ISR (In-Sync Replicas),
       Idempotent Producer, Transactional API, Compacted Topic, Log Retention

Event Sourcing: Aggregate, Event Store, Projection, Snapshot, Replay,
                Upcasting, Event Versioning, Temporal Query

CQRS: Command Model, Query Model, Projection, Eventual Consistency,
      Materialized View, Read Replica

Patterns: Saga, Outbox Pattern, Dead Letter Queue, Poison Pill,
          Backpressure, Circuit Breaker, Bulkhead

CDC: Debezium, Transaction Log, WAL, Binlog, Outbox Pattern,
     Capture + Publish, Zero-Code Integration
```

