# Section 1: Modern Java Features (Java 17–21) & Virtual Threads

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [00_Web_Fundamentals.md](00_Web_Fundamentals.md) | **Next**: [02_Advanced_SpringBoot_Java_Internals.md](02_Advanced_SpringBoot_Java_Internals.md)

---

## 1.1 Records, Sealed Classes, and Pattern Matching

---

### The "Why" & The Problem

Java has historically been criticized for excessive boilerplate. A simple data class required a constructor, getters, `equals()`, `hashCode()`, and `toString()` — easily 50+ lines for 5 fields. Modern Java (17–21) introduced **records**, **sealed classes**, and **pattern matching** to eliminate this boilerplate while adding compile-time safety guarantees. These features fundamentally change how you model domain data.

A company pays you to know this because:
- **Code quality**: Records and sealed classes make domain models concise, immutable, and less error-prone.
- **Signal of currency**: Using modern Java features signals to the interviewer that you actively keep up with the platform. A Lead who writes Java 8-style code in 2026 appears stagnant.
- **Pattern matching**: Enables more expressive, exhaustive handling of data types — the compiler catches missing cases at compile time instead of runtime.

---

### Interviewer Expectations

- **Records**: Know they are immutable data carriers (final fields, auto-generated constructor, getters, equals, hashCode, toString). Know their limitations (cannot extend classes, cannot have mutable fields). Know when to use them (DTOs, value objects, events) and when NOT to use them (JPA entities, mutable domain objects).
- **Sealed classes**: Know they restrict which classes can extend them. Combined with pattern matching, the compiler enforces exhaustive handling. Use for algebraic data types / discriminated unions.
- **Pattern matching**: `instanceof` with pattern variables (Java 16+), switch expressions with patterns (Java 21+), record patterns (Java 21+), guarded patterns.
- **Keywords**: "Immutable data carrier", "canonical constructor", "compact constructor", "algebraic data type", "exhaustive switch", "record pattern deconstruction", "sealed interface hierarchy".

---

### The Deep Dive & Solution

#### Records (Java 16+, finalized)

A record is a transparent, immutable data carrier. The compiler automatically generates the canonical constructor, accessor methods, `equals()`, `hashCode()`, and `toString()`.

```java
// Old way — 60+ lines of boilerplate
public class OrderDTO {
    private final String orderId;
    private final String customerId;
    private final BigDecimal totalAmount;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    
    public OrderDTO(String orderId, String customerId, BigDecimal totalAmount, 
                    OrderStatus status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    // ... more getters, equals, hashCode, toString ...
}

// New way — 3 lines. Same functionality.
public record OrderDTO(
    String orderId,
    String customerId,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime createdAt
) { }

// Usage:
OrderDTO order = new OrderDTO("ORD-123", "CUST-456", 
    new BigDecimal("99.99"), OrderStatus.PENDING, LocalDateTime.now());
String id = order.orderId();          // Accessor method (no "get" prefix)
System.out.println(order);            // OrderDTO[orderId=ORD-123, customerId=CUST-456, ...]
boolean equal = order1.equals(order2); // Value equality (compares all fields)
```

**Compact constructors** — for validation:
```java
public record OrderDTO(
    String orderId,
    String customerId,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime createdAt
) {
    // Compact constructor — validates without repeating field assignments
    public OrderDTO {
        Objects.requireNonNull(orderId, "orderId must not be null");
        Objects.requireNonNull(customerId, "customerId must not be null");
        if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("totalAmount must be non-negative");
        }
        // Field assignments (this.orderId = orderId) are implicit
    }
    
    // You can add custom methods
    public boolean isHighValue() {
        return totalAmount.compareTo(new BigDecimal("1000")) > 0;
    }
}
```

**When NOT to use records**:
- **JPA entities**: JPA requires a no-arg constructor, mutable fields, and proxying — records are immutable and final. Use records for DTOs, not entities.
- **Spring beans**: Beans with `@Autowired` dependencies cannot be records (they need mutable injection points, or you use constructor injection, but beans are typically singletons, not data carriers).
- **Inheritance**: Records cannot extend classes (they implicitly extend `java.lang.Record`). They can implement interfaces.

**Records as DTOs in Spring Boot**:
```java
// Request DTO — immutable, self-validating
public record CreateOrderRequest(
    @NotBlank String customerId,
    @NotEmpty List<OrderItemRequest> items,
    @NotNull ShippingAddress shippingAddress
) { }

public record OrderItemRequest(
    @NotBlank String productId,
    @Positive int quantity
) { }

public record ShippingAddress(
    @NotBlank String street,
    @NotBlank String city,
    @NotBlank String state,
    @Pattern(regexp = "\\d{5}(-\\d{4})?") String zipCode
) { }

// Response DTO
public record OrderResponse(
    String orderId,
    String customerId,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime createdAt,
    List<OrderItemResponse> items
) {
    // Static factory method for mapping from entity
    public static OrderResponse from(Order entity) {
        return new OrderResponse(
            entity.getId(),
            entity.getCustomerId(),
            entity.getTotalAmount(),
            entity.getStatus(),
            entity.getCreatedAt(),
            entity.getItems().stream()
                .map(OrderItemResponse::from)
                .toList()
        );
    }
}

// Controller using records
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(OrderResponse.from(order));
    }
}
```

#### Sealed Classes and Interfaces (Java 17+)

Sealed classes restrict which classes can extend them. Combined with pattern matching, this creates **algebraic data types** — the compiler knows all possible subtypes and can enforce exhaustive handling.

```java
// A payment method can ONLY be one of these three types
// No other class can extend PaymentMethod
public sealed interface PaymentMethod 
    permits CreditCard, BankTransfer, DigitalWallet { }

public record CreditCard(
    String cardNumber, 
    String expiryDate, 
    String cvv
) implements PaymentMethod { }

public record BankTransfer(
    String accountNumber, 
    String routingNumber
) implements PaymentMethod { }

public record DigitalWallet(
    String walletId, 
    WalletType type  // APPLE_PAY, GOOGLE_PAY, PAYPAL
) implements PaymentMethod { }

// Exhaustive pattern matching — the compiler ensures ALL cases are handled
public BigDecimal calculateProcessingFee(PaymentMethod method) {
    return switch (method) {
        case CreditCard cc -> cc.cardNumber().startsWith("4") 
            ? new BigDecimal("0.029")    // Visa: 2.9%
            : new BigDecimal("0.035");   // Others: 3.5%
        case BankTransfer bt -> new BigDecimal("0.005");  // 0.5%
        case DigitalWallet dw -> switch (dw.type()) {
            case APPLE_PAY -> new BigDecimal("0.015");
            case GOOGLE_PAY -> new BigDecimal("0.015");
            case PAYPAL -> new BigDecimal("0.034");
        };
        // No 'default' needed — compiler knows all cases are covered!
        // If you add a new PaymentMethod implementation, this switch
        // will fail to compile until you handle the new case.
    };
}
```

**Real-world use case — Event-driven architecture**:
```java
// All events in the order domain are sealed
public sealed interface OrderEvent 
    permits OrderCreated, OrderConfirmed, OrderShipped, OrderCancelled, OrderRefunded { }

public record OrderCreated(String orderId, String customerId, BigDecimal amount, 
                           Instant timestamp) implements OrderEvent { }
public record OrderConfirmed(String orderId, Instant timestamp) implements OrderEvent { }
public record OrderShipped(String orderId, String trackingNumber, 
                           Instant timestamp) implements OrderEvent { }
public record OrderCancelled(String orderId, String reason, 
                             Instant timestamp) implements OrderEvent { }
public record OrderRefunded(String orderId, BigDecimal refundAmount, 
                            Instant timestamp) implements OrderEvent { }

// Event handler — exhaustive, type-safe
@Service
public class OrderEventHandler {
    
    public void handle(OrderEvent event) {
        switch (event) {
            case OrderCreated e -> {
                log.info("Order {} created for customer {}", e.orderId(), e.customerId());
                inventoryService.reserve(e.orderId());
                notificationService.sendOrderConfirmationEmail(e.customerId(), e.orderId());
            }
            case OrderConfirmed e -> {
                log.info("Order {} confirmed", e.orderId());
                paymentService.capturePayment(e.orderId());
            }
            case OrderShipped e -> {
                log.info("Order {} shipped, tracking: {}", e.orderId(), e.trackingNumber());
                notificationService.sendShippingNotification(e.orderId(), e.trackingNumber());
            }
            case OrderCancelled e -> {
                log.info("Order {} cancelled: {}", e.orderId(), e.reason());
                inventoryService.release(e.orderId());
                paymentService.refund(e.orderId());
            }
            case OrderRefunded e -> {
                log.info("Order {} refunded: ${}", e.orderId(), e.refundAmount());
                accountingService.recordRefund(e.orderId(), e.refundAmount());
            }
            // Compiler error if you miss a case!
        }
    }
}
```

#### Pattern Matching (Java 16–21)

**`instanceof` with pattern variables** (Java 16+):
```java
// Old way
if (obj instanceof String) {
    String s = (String) obj;  // Explicit cast
    System.out.println(s.length());
}

// New way — pattern variable 's' is auto-typed
if (obj instanceof String s) {
    System.out.println(s.length());  // No explicit cast needed
}

// Works in conditions too
if (obj instanceof String s && s.length() > 5) {
    System.out.println("Long string: " + s);
}
```

**Switch expressions with pattern matching** (Java 21+):
```java
// Pattern matching + guarded patterns (when clause)
public String formatValue(Object obj) {
    return switch (obj) {
        case Integer i when i < 0    -> "Negative: " + i;
        case Integer i               -> "Positive: " + i;
        case String s when s.isBlank() -> "(empty string)";
        case String s                -> "String: " + s;
        case List<?> list when list.isEmpty() -> "(empty list)";
        case List<?> list            -> "List of " + list.size() + " items";
        case null                    -> "(null)";
        default                      -> "Unknown: " + obj.getClass().getSimpleName();
    };
}
```

**Record patterns (deconstruction)** (Java 21+):
```java
// Decompose records directly in pattern matching
public record Point(int x, int y) { }
public record Line(Point start, Point end) { }

public double calculateLength(Object shape) {
    return switch (shape) {
        // Deconstruct the record directly in the pattern!
        case Point(int x, int y) -> Math.sqrt(x * x + y * y);
        
        // Nested deconstruction — decompose Line AND its Point fields
        case Line(Point(int x1, int y1), Point(int x2, int y2)) -> 
            Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        
        default -> 0.0;
    };
}
```

---

## 1.2 Virtual Threads (Project Loom) — Java 21+

---

### The "Why" & The Problem

Traditional Java threads are **platform threads** — each one maps 1:1 to an operating system thread. OS threads are expensive: each one consumes ~1MB of stack memory and requires an OS context switch (~1-10μs) when switching between threads. This means:
- A server with 8GB of RAM can support ~8,000 platform threads (8GB / 1MB).
- A Tomcat server with 200 threads can handle ~200 concurrent blocking requests.
- If each request makes 3 blocking HTTP calls (each taking 100ms), your throughput is limited by the number of threads, not CPU utilization.

**The core problem**: In I/O-heavy applications (which most web services are), threads spend 95% of their time **blocked** (waiting for database responses, HTTP calls, file I/O). While blocked, the thread is completely idle but still consumes 1MB of memory and an OS scheduling slot. This is the **thread-per-request scalability wall**.

**Virtual threads** break this wall. They are **lightweight threads** managed by the JVM (not the OS). A virtual thread that blocks on I/O **releases its underlying OS thread** to run other virtual threads. You can have **millions** of virtual threads on a single JVM.

A company pays you to know this because:
- **10x throughput** for I/O-heavy services without changing your code (just swap the thread pool).
- **Simpler than reactive programming**: Virtual threads let you write familiar blocking code (no Mono/Flux, no CompletableFuture chains) while getting reactive-like scalability.
- **The future of Java**: Virtual threads are the most significant Java platform feature in a decade. Spring Boot 3.2+ has first-class support.

---

### Interviewer Expectations

- **What virtual threads are**: Lightweight threads managed by the JVM, not the OS. They are mounted on platform threads (carrier threads) by the JVM scheduler. When a virtual thread blocks on I/O, it is unmounted from the carrier thread, which is then free to run another virtual thread.
- **How they solve the scalability problem**: With platform threads, 10,000 concurrent requests = 10,000 OS threads (impossible). With virtual threads, 10,000 concurrent requests = 10,000 virtual threads that time-share a small pool of carrier threads (e.g., 8 carrier threads for 8 CPU cores).
- **Spring Boot integration**: Know how to enable virtual threads in Spring Boot 3.2+ (one property: `spring.threads.virtual.enabled=true`).
- **Pinning**: Know that `synchronized` blocks and JNI calls can "pin" a virtual thread to its carrier thread, preventing the carrier from being reused. Use `ReentrantLock` instead of `synchronized`.
- **Keywords**: "Virtual thread", "carrier thread", "mounting/unmounting", "continuation", "structured concurrency", "pinning", "thread-per-request with virtual threads", "Project Loom".

---

### The Deep Dive & Solution

#### How Virtual Threads Work

```
Platform Threads (Traditional):
┌─────────────────────────────────────────────────────┐
│  OS Thread 1: [Request A ████████BLOCKED████████████]│ ← Thread wasted while blocked
│  OS Thread 2: [Request B ████████BLOCKED████████████]│
│  OS Thread 3: [Request C ████████BLOCKED████████████]│
│  ... (limited to ~200 threads)                       │
└─────────────────────────────────────────────────────┘

Virtual Threads (Java 21+):
┌─────────────────────────────────────────────────────┐
│  Carrier Thread 1: [VT-A █RUN█][VT-D █RUN█][VT-G █]│ ← Carrier multiplexes many VTs
│  Carrier Thread 2: [VT-B █RUN█][VT-E █RUN█][VT-H █]│
│  Carrier Thread 3: [VT-C █RUN█][VT-F █RUN█][VT-I █]│
│  (8 carrier threads for 8 CPU cores)                 │
│                                                       │
│  Virtual Threads: VT-A, VT-B, VT-C ... VT-10000     │
│  (10,000 virtual threads, each handling one request)  │
│                                                       │
│  When VT-A blocks on I/O:                            │
│  1. VT-A is unmounted from Carrier Thread 1          │
│  2. VT-D is mounted on Carrier Thread 1              │
│  3. VT-A resumes when I/O completes (mounted on any  │
│     available carrier thread)                         │
└─────────────────────────────────────────────────────┘
```

#### Using Virtual Threads in Code

```java
// Creating virtual threads directly (Java 21+)
Thread vThread = Thread.ofVirtual().name("my-virtual-thread").start(() -> {
    // This runs on a virtual thread
    String result = httpClient.send(request, BodyHandlers.ofString()).body();
    processResult(result);
});

// Virtual thread executor — creates a new virtual thread for each task
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Submit 10,000 tasks — each gets its own virtual thread
    List<Future<String>> futures = new ArrayList<>();
    for (int i = 0; i < 10_000; i++) {
        final int userId = i;
        futures.add(executor.submit(() -> {
            // Blocking HTTP call — but the virtual thread yields its carrier thread
            return fetchUserData(userId);
        }));
    }
    
    // Collect results
    for (Future<String> future : futures) {
        System.out.println(future.get());
    }
}
// 10,000 concurrent HTTP calls with only 8 carrier threads!
```

#### Spring Boot 3.2+ with Virtual Threads

Enabling virtual threads in Spring Boot is a single configuration line:

```yaml
# application.yml — That's it. Tomcat will use virtual threads for request handling.
spring:
  threads:
    virtual:
      enabled: true
```

**What this does**:
1. Tomcat creates a virtual thread for each incoming HTTP request (instead of using a fixed-size thread pool of 200 platform threads).
2. When your `@Service` method makes a blocking JDBC call or `RestTemplate` call, the virtual thread yields its carrier thread to other virtual threads.
3. You can now handle 10,000+ concurrent requests with the same straightforward blocking code you've always written.

```java
// This familiar blocking code now scales to thousands of concurrent requests
@Service
public class OrderService {
    
    @Autowired private OrderRepository orderRepository;    // Blocking JDBC
    @Autowired private RestTemplate restTemplate;          // Blocking HTTP
    @Autowired private RedisTemplate<String, String> redis; // Blocking Redis
    
    public OrderSummary getOrderSummary(String orderId) {
        // Each of these blocking calls yields the carrier thread
        Order order = orderRepository.findById(orderId).orElseThrow();
        Customer customer = restTemplate.getForObject(
            "/api/customers/" + order.getCustomerId(), Customer.class);
        String cachedRating = redis.opsForValue().get("rating:" + orderId);
        
        return new OrderSummary(order, customer, cachedRating);
        // Total blocking time: ~150ms. But the carrier thread was free during those blocks.
    }
}
```

**Before virtual threads**: 200 Tomcat threads × 150ms blocking = 1,333 req/sec max.
**With virtual threads**: 10,000 virtual threads × 150ms blocking = 66,666 req/sec potential (limited by database, not threads).

#### Pinning — The Gotcha

When a virtual thread executes inside a `synchronized` block or calls JNI code, it **pins** to its carrier thread. A pinned virtual thread cannot unmount — it blocks the carrier thread just like a platform thread. This defeats the purpose.

```java
// BAD: synchronized pins the virtual thread
public synchronized String getFromCache(String key) {
    return cache.get(key);  // If this blocks, the carrier thread is wasted
}

// GOOD: ReentrantLock does NOT pin
private final ReentrantLock lock = new ReentrantLock();

public String getFromCache(String key) {
    lock.lock();
    try {
        return cache.get(key);  // Virtual thread can unmount during blocking inside lock
    } finally {
        lock.unlock();
    }
}
```

**Detecting pinning in production**:
```bash
# JVM flag to detect pinned virtual threads
-Djdk.tracePinnedThreads=short
# Or for full stack trace:
-Djdk.tracePinnedThreads=full
```

#### Structured Concurrency (Preview in Java 21)

Structured concurrency treats a group of concurrent tasks as a single unit of work. If one task fails, all sibling tasks are cancelled. This prevents leaked threads and makes concurrent code easier to reason about.

```java
// Structured concurrency — all tasks are scoped to the try block
public OrderSummary getOrderSummary(String orderId) throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        
        // Fork three concurrent tasks (each on its own virtual thread)
        Subtask<Order> orderTask = scope.fork(() -> 
            orderRepository.findById(orderId).orElseThrow());
        Subtask<Customer> customerTask = scope.fork(() -> 
            customerService.getCustomer(orderId));
        Subtask<List<Item>> itemsTask = scope.fork(() -> 
            inventoryService.getItems(orderId));
        
        // Wait for all tasks to complete (or fail)
        scope.join();           // Blocks until all tasks finish
        scope.throwIfFailed();  // Throws if any task failed
        
        // All tasks completed successfully — get results
        return new OrderSummary(
            orderTask.get(), 
            customerTask.get(), 
            itemsTask.get()
        );
    }
    // If customerService.getCustomer() throws an exception:
    // 1. scope.throwIfFailed() propagates the exception
    // 2. orderTask and itemsTask are automatically cancelled
    // 3. No leaked threads
}
```

---

## 1.3 Text Blocks, Switch Expressions, and Other Quality-of-Life Features

---

### The "Why" & The Problem

These features reduce boilerplate and improve readability. While individually small, they collectively make Java code significantly more pleasant to write and read. An interviewer expects a Lead to use modern syntax fluently.

---

### The Deep Dive & Solution

#### Text Blocks (Java 15+)

```java
// Old way — string concatenation hell
String json = "{\n" +
    "  \"orderId\": \"" + orderId + "\",\n" +
    "  \"status\": \"PENDING\",\n" +
    "  \"items\": [\n" +
    "    { \"productId\": \"P1\", \"qty\": 2 }\n" +
    "  ]\n" +
    "}";

// New way — text blocks
String json = """
    {
      "orderId": "%s",
      "status": "PENDING",
      "items": [
        { "productId": "P1", "qty": 2 }
      ]
    }
    """.formatted(orderId);

// SQL queries become much more readable
String sql = """
    SELECT o.order_id, o.total_amount, c.name
    FROM orders o
    JOIN customers c ON o.customer_id = c.id
    WHERE o.status = 'PENDING'
      AND o.order_date > :startDate
    ORDER BY o.order_date DESC
    LIMIT 20
    """;
```

#### Switch Expressions (Java 14+)

```java
// Old switch (statement) — verbose, error-prone (fall-through bug)
String label;
switch (status) {
    case PENDING:
        label = "⏳ Pending";
        break;  // Forget this 'break' and you have a bug
    case SHIPPED:
        label = "🚚 Shipped";
        break;
    case DELIVERED:
        label = "✅ Delivered";
        break;
    default:
        label = "Unknown";
}

// New switch (expression) — concise, no fall-through, returns a value
String label = switch (status) {
    case PENDING   -> "⏳ Pending";
    case SHIPPED   -> "🚚 Shipped";
    case DELIVERED -> "✅ Delivered";
    // If status is an enum with more values, compiler forces you to handle them or add default
};
```

#### Helpful NullPointerExceptions (Java 14+)

```java
// Before Java 14:
// NullPointerException at OrderService.java:42
// Which variable is null? order? customer? address? city? 🤷

// Java 14+:
// NullPointerException: Cannot invoke "Address.getCity()" 
// because the return value of "Customer.getAddress()" is null
// Now you know EXACTLY what's null!
```

#### Sequenced Collections (Java 21)

```java
// Before: Getting the first/last element of a collection was inconsistent
list.get(0);                    // List: first
list.get(list.size() - 1);     // List: last
sortedSet.first();              // SortedSet: first
sortedSet.last();               // SortedSet: last
// LinkedHashMap had no way to get the last entry!

// Java 21: SequencedCollection interface — consistent API
list.getFirst();                // First element
list.getLast();                 // Last element
list.reversed();                // Reversed view

sortedSet.getFirst();           // Works the same way
linkedHashMap.firstEntry();     // First entry
linkedHashMap.lastEntry();      // Last entry
linkedHashMap.reversed();       // Reversed view
```

#### Unnamed Variables (Java 22, Preview in 21)

```java
// When you don't need a variable, use _ (underscore)
try {
    processOrder(order);
} catch (OrderNotFoundException _) {
    // We don't need the exception variable — just log a message
    log.warn("Order not found, returning empty response");
    return Optional.empty();
}

// In enhanced for loops
for (var _ : IntStream.range(0, 5).toArray()) {
    retryOperation();  // Just need to loop 5 times, don't need the index
}

// In lambdas
map.forEach((_, value) -> processValue(value));  // Don't need the key
```

#### Stream API Enhancements (Java 16–21)

```java
// Stream.toList() — Java 16+ (returns unmodifiable list)
List<String> names = orders.stream()
    .map(Order::getCustomerName)
    .toList();  // Instead of .collect(Collectors.toList())

// Stream.mapMulti() — Java 16+ (alternative to flatMap for 1-to-many)
List<OrderItem> allItems = orders.stream()
    .<OrderItem>mapMulti((order, consumer) -> {
        for (OrderItem item : order.getItems()) {
            if (item.getQuantity() > 0) {
                consumer.accept(item);
            }
        }
    })
    .toList();

// Gatherers (Java 22, Preview) — custom intermediate operations
// Group stream elements into windows of size 3
List<List<Order>> batches = orders.stream()
    .gather(Gatherers.windowFixed(3))
    .toList();
```


