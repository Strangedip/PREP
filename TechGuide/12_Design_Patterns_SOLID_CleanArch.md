# Section 12: Design Patterns, SOLID Principles & Clean Architecture

---

## 12.1 SOLID Principles — The Foundation of Maintainable Code

---

### The "Why" & The Problem

Without SOLID principles, code becomes a tangled mess:
- One class does everything (God class). Changing one feature breaks three others.
- Adding a new feature requires modifying existing, tested code — introducing regressions.
- Unit testing is impossible because classes are tightly coupled to databases, HTTP clients, and other classes.

SOLID principles are **not academic theory** — they're practical guidelines that prevent the most common design mistakes. Every Lead Engineer should be able to identify SOLID violations in code reviews and refactor them.

A company pays you to know this because **maintainability is the #1 cost driver in software**. Over a 10-year lifespan, a system spends 80% of its budget on maintenance. SOLID reduces that cost.

---

### Interviewer Expectations

- **Explain each principle with a real-world example**: Not textbook definitions. Show a violation, explain why it's bad, then show the fix.
- **Trade-offs**: SOLID is not absolute. Over-engineering for SOLID compliance (e.g., extracting an interface for a class that will never have a second implementation) adds unnecessary complexity.
- **Keywords**: "Single Responsibility", "Open-Closed", "Liskov Substitution", "Interface Segregation", "Dependency Inversion", "loose coupling", "high cohesion".

---

### The Deep Dive & Solution

#### S — Single Responsibility Principle (SRP)

**"A class should have only one reason to change."**

```java
// ❌ BAD: OrderService has multiple responsibilities
@Service
public class OrderService {
    public Order createOrder(CreateOrderRequest request) {
        // 1. Business logic: validate order
        if (request.getItems().isEmpty()) throw new InvalidOrderException("No items");

        // 2. Persistence: save to database
        Order order = orderRepository.save(Order.from(request));

        // 3. Notification: send email
        emailService.send(new OrderConfirmationEmail(order));

        // 4. Metrics: track KPI
        meterRegistry.counter("orders.created").increment();

        // 5. Audit: log the event
        auditLogRepository.save(new AuditLog("ORDER_CREATED", order.getId()));

        return order;
    }
}
// If email format changes, you modify OrderService.
// If audit logging changes, you modify OrderService.
// Every change risks breaking order creation.
```

```java
// ✅ GOOD: Each class has one responsibility
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderValidator validator;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        validator.validate(request);
        Order order = orderRepository.save(Order.from(request));

        // Publish event — listeners handle their own concerns
        eventPublisher.publishEvent(new OrderCreatedEvent(order));

        return order;
    }
}

// Each concern is handled by a separate listener
@Component
public class OrderNotificationListener {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        emailService.send(new OrderConfirmationEmail(event.getOrder()));
    }
}

@Component
public class OrderMetricsListener {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        meterRegistry.counter("orders.created").increment();
    }
}

@Component
public class OrderAuditListener {
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        auditLogRepository.save(new AuditLog("ORDER_CREATED", event.getOrder().getId()));
    }
}
```

**Why this is better**: Adding a new notification channel (SMS, push notification) requires adding a new listener — zero changes to `OrderService`.

#### O — Open/Closed Principle (OCP)

**"Software entities should be open for extension, but closed for modification."**

```java
// ❌ BAD: Adding a new discount type requires modifying this method
public class DiscountCalculator {
    public BigDecimal calculateDiscount(Order order) {
        BigDecimal discount = BigDecimal.ZERO;

        if (order.getCustomer().isPremium()) {
            discount = order.getTotal().multiply(new BigDecimal("0.10")); // 10% premium discount
        }
        if (order.isFirstOrder()) {
            discount = discount.add(new BigDecimal("5.00")); // $5 first order bonus
        }
        if (order.getTotal().compareTo(new BigDecimal("100")) > 0) {
            discount = discount.add(order.getTotal().multiply(new BigDecimal("0.05"))); // 5% bulk discount
        }
        // Adding loyalty points discount? Modify this class. Seasonal discount? Modify again.
        return discount;
    }
}
```

```java
// ✅ GOOD: Adding a new discount type means adding a new class, not modifying existing code
public interface DiscountStrategy {
    BigDecimal calculate(Order order);
    boolean isApplicable(Order order);
}

@Component
public class PremiumCustomerDiscount implements DiscountStrategy {
    @Override
    public boolean isApplicable(Order order) {
        return order.getCustomer().isPremium();
    }

    @Override
    public BigDecimal calculate(Order order) {
        return order.getTotal().multiply(new BigDecimal("0.10"));
    }
}

@Component
public class FirstOrderDiscount implements DiscountStrategy {
    @Override
    public boolean isApplicable(Order order) {
        return order.isFirstOrder();
    }

    @Override
    public BigDecimal calculate(Order order) {
        return new BigDecimal("5.00");
    }
}

@Component
public class BulkOrderDiscount implements DiscountStrategy {
    @Override
    public boolean isApplicable(Order order) {
        return order.getTotal().compareTo(new BigDecimal("100")) > 0;
    }

    @Override
    public BigDecimal calculate(Order order) {
        return order.getTotal().multiply(new BigDecimal("0.05"));
    }
}

// The calculator is CLOSED for modification — it never changes
@Service
public class DiscountCalculator {
    private final List<DiscountStrategy> strategies; // Spring injects all implementations

    public DiscountCalculator(List<DiscountStrategy> strategies) {
        this.strategies = strategies;
    }

    public BigDecimal calculateDiscount(Order order) {
        return strategies.stream()
                .filter(strategy -> strategy.isApplicable(order))
                .map(strategy -> strategy.calculate(order))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

// Adding a seasonal discount? Just add a new class:
@Component
public class SeasonalDiscount implements DiscountStrategy { /* ... */ }
// No existing code is modified. No risk of breaking existing discounts.
```

#### L — Liskov Substitution Principle (LSP)

**"Objects of a superclass should be replaceable with objects of a subclass without breaking correctness."**

```java
// ❌ BAD: Square violates LSP when used as a Rectangle
class Rectangle {
    protected int width;
    protected int height;

    public void setWidth(int width) { this.width = width; }
    public void setHeight(int height) { this.height = height; }
    public int getArea() { return width * height; }
}

class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // Forces height = width. Unexpected behavior!
    }

    @Override
    public void setHeight(int height) {
        this.width = height;
        this.height = height;
    }
}

// This test passes for Rectangle but FAILS for Square
void testArea(Rectangle rect) {
    rect.setWidth(5);
    rect.setHeight(4);
    assert rect.getArea() == 20; // ❌ FAILS for Square! Area is 16 (4×4)
}
```

```java
// ✅ GOOD: Use an interface or make them separate classes
interface Shape {
    int getArea();
}

record Rectangle(int width, int height) implements Shape {
    @Override
    public int getArea() { return width * height; }
}

record Square(int side) implements Shape {
    @Override
    public int getArea() { return side * side; }
}

// Any Shape can be used interchangeably without surprises
```

**Real-world example in Spring**: If your `CachingProductService` extends `ProductService`, it should behave identically for all consumers. It should not silently return stale data if the consumer expects fresh data.

#### I — Interface Segregation Principle (ISP)

**"No client should be forced to depend on methods it does not use."**

```java
// ❌ BAD: Fat interface — forces implementations to handle methods they don't support
public interface UserRepository {
    User findById(Long id);
    List<User> findAll();
    User save(User user);
    void delete(User user);
    List<User> findByRole(String role);
    List<User> searchByName(String name);
    void bulkImport(List<User> users);
    UserStatistics getStatistics();
    void exportToCsv(OutputStream outputStream);
}

// A read-only analytics service is forced to implement save(), delete(), bulkImport()
// even though it never modifies data.
```

```java
// ✅ GOOD: Segregated interfaces — each client depends only on what it needs
public interface UserReader {
    Optional<User> findById(Long id);
    List<User> findAll();
    List<User> findByRole(String role);
}

public interface UserWriter {
    User save(User user);
    void delete(User user);
}

public interface UserSearch {
    List<User> searchByName(String name);
}

public interface UserBulkOperations {
    void bulkImport(List<User> users);
    void exportToCsv(OutputStream outputStream);
}

// The repository implements all of them
@Repository
public class JpaUserRepository implements UserReader, UserWriter, UserSearch, UserBulkOperations {
    // ...
}

// But services depend only on what they need
@Service
public class UserAnalyticsService {
    private final UserReader userReader;  // Only needs read access

    public UserAnalyticsService(UserReader userReader) {
        this.userReader = userReader;
    }
}

@Service
public class UserImportService {
    private final UserBulkOperations bulkOps;  // Only needs bulk operations

    public UserImportService(UserBulkOperations bulkOps) {
        this.bulkOps = bulkOps;
    }
}
```

#### D — Dependency Inversion Principle (DIP)

**"High-level modules should not depend on low-level modules. Both should depend on abstractions."**

```java
// ❌ BAD: OrderService (high-level) directly depends on MySQLOrderRepository (low-level)
@Service
public class OrderService {
    private final MySQLOrderRepository repository; // Concrete class!

    public OrderService() {
        this.repository = new MySQLOrderRepository(); // Hard-coded dependency!
    }
}
// Want to switch to PostgreSQL? Modify OrderService.
// Want to test with an in-memory store? Can't without a real MySQL database.
```

```java
// ✅ GOOD: Both depend on an abstraction (interface)
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long id);
}

@Repository
public class JpaOrderRepository implements OrderRepository {
    // Uses JPA — can work with MySQL, PostgreSQL, H2
}

@Service
public class OrderService {
    private final OrderRepository repository;  // Depends on abstraction!

    public OrderService(OrderRepository repository) {  // Injected by Spring
        this.repository = repository;
    }
}

// In tests:
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock private OrderRepository repository;  // Mock the interface!
    @InjectMocks private OrderService orderService;
    // No database needed for unit tests
}
```

---

## 12.2 Gang of Four (GoF) Design Patterns — The Essential Ones

---

### The "Why" & The Problem

Design patterns are **reusable solutions to common problems**. You don't need to memorize all 23 GoF patterns. Focus on the ones you'll actually use (and that interviewers ask about).

---

### Interviewer Expectations

- **Don't just name patterns**: Explain when and why you'd use each pattern. Show real-world examples from Spring Boot.
- **Anti-patterns**: Know when NOT to use a pattern (e.g., Singleton anti-pattern with mutable global state).
- **Keywords**: "Strategy pattern", "Factory pattern", "Builder pattern", "Observer pattern", "Decorator pattern", "Adapter pattern", "Template Method".

---

### The Deep Dive & Solution

#### Creational Patterns

##### Factory Method

**Problem**: Creating objects with complex initialization logic scattered across the codebase.

```java
/**
 * Factory Method — encapsulate object creation logic.
 * Used when the creation logic depends on runtime conditions.
 */
public interface NotificationFactory {
    Notification create(NotificationType type, String recipient, String message);
}

@Component
public class NotificationFactoryImpl implements NotificationFactory {

    private final Map<NotificationType, NotificationSender> senders;

    // Spring injects all NotificationSender implementations
    public NotificationFactoryImpl(List<NotificationSender> senderList) {
        this.senders = senderList.stream()
                .collect(Collectors.toMap(NotificationSender::getType, Function.identity()));
    }

    @Override
    public Notification create(NotificationType type, String recipient, String message) {
        NotificationSender sender = senders.get(type);
        if (sender == null) {
            throw new UnsupportedOperationException("No sender for type: " + type);
        }
        return sender.send(recipient, message);
    }
}

// Each sender handles its own type
@Component
public class EmailNotificationSender implements NotificationSender {
    @Override
    public NotificationType getType() { return NotificationType.EMAIL; }

    @Override
    public Notification send(String recipient, String message) {
        // Send email via SMTP
    }
}

@Component
public class SmsNotificationSender implements NotificationSender {
    @Override
    public NotificationType getType() { return NotificationType.SMS; }

    @Override
    public Notification send(String recipient, String message) {
        // Send SMS via Twilio
    }
}

// Adding push notifications? Just add a new class. Factory doesn't change.
```

##### Builder Pattern

**Problem**: Constructors with many parameters (telescoping constructor anti-pattern).

```java
/**
 * Builder Pattern — clean construction of complex objects.
 * In practice, use Lombok's @Builder for most cases.
 */
@Builder
@Getter
public class SearchCriteria {
    private final String query;
    private final List<String> categories;
    private final BigDecimal minPrice;
    private final BigDecimal maxPrice;
    private final SortField sortBy;
    private final SortDirection sortDirection;
    private final int page;
    private final int pageSize;
    @Builder.Default private final boolean includeOutOfStock = false;
    @Builder.Default private final boolean includeInactive = false;
}

// Usage — readable, self-documenting
SearchCriteria criteria = SearchCriteria.builder()
        .query("wireless headphones")
        .categories(List.of("electronics", "audio"))
        .minPrice(new BigDecimal("50"))
        .maxPrice(new BigDecimal("200"))
        .sortBy(SortField.PRICE)
        .sortDirection(SortDirection.ASC)
        .page(0)
        .pageSize(20)
        .build();
```

#### Structural Patterns

##### Decorator Pattern

**Problem**: Adding behavior to an object dynamically, without modifying the original class.

**Real-world Spring example**: Adding caching, logging, and retry to a service without modifying the service.

```java
/**
 * Decorator Pattern — wrap a service with additional behavior.
 * Each decorator adds one concern and delegates to the next.
 */
public interface ProductService {
    Product getProduct(String id);
}

// Base implementation
@Service
@Primary
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getProduct(String id) {
        return productRepository.findById(id).orElseThrow();
    }
}

// Caching decorator
public class CachingProductService implements ProductService {
    private final ProductService delegate;
    private final Cache cache;

    public CachingProductService(ProductService delegate, Cache cache) {
        this.delegate = delegate;
        this.cache = cache;
    }

    @Override
    public Product getProduct(String id) {
        return cache.get(id, () -> delegate.getProduct(id));
    }
}

// Logging decorator
public class LoggingProductService implements ProductService {
    private final ProductService delegate;

    @Override
    public Product getProduct(String id) {
        log.info("Getting product {}", id);
        long start = System.nanoTime();
        Product product = delegate.getProduct(id);
        log.info("Got product {} in {}ms", id, (System.nanoTime() - start) / 1_000_000);
        return product;
    }
}

// Composition — decorators wrap each other
// LoggingProductService → CachingProductService → ProductServiceImpl
// In Spring, AOP provides this pattern automatically via @Cacheable, @Transactional, etc.
```

**Note**: In Spring Boot, the Decorator pattern is built-in via AOP. `@Transactional`, `@Cacheable`, `@Retryable` are all decorators applied as proxies.

##### Adapter Pattern

**Problem**: You need to use a class with an incompatible interface. Common when integrating third-party libraries.

```java
/**
 * Adapter Pattern — adapt a third-party payment API to your internal interface.
 * If you switch from Stripe to Braintree, only the adapter changes.
 */
public interface PaymentGateway {
    PaymentResult charge(PaymentRequest request);
    PaymentResult refund(String transactionId, BigDecimal amount);
}

// Stripe adapter — adapts Stripe's SDK to your interface
@Component
@ConditionalOnProperty(name = "payment.provider", havingValue = "stripe")
public class StripePaymentAdapter implements PaymentGateway {
    private final StripeClient stripeClient;

    @Override
    public PaymentResult charge(PaymentRequest request) {
        // Translate your PaymentRequest to Stripe's API format
        PaymentIntent intent = PaymentIntent.create(PaymentIntentCreateParams.builder()
                .setAmount(request.getAmount().multiply(new BigDecimal(100)).longValue()) // Stripe uses cents
                .setCurrency(request.getCurrency().toLowerCase())
                .setPaymentMethod(request.getPaymentMethodId())
                .setConfirm(true)
                .build());

        // Translate Stripe's response to your PaymentResult
        return PaymentResult.builder()
                .transactionId(intent.getId())
                .status(mapStatus(intent.getStatus()))
                .amount(request.getAmount())
                .build();
    }

    @Override
    public PaymentResult refund(String transactionId, BigDecimal amount) {
        Refund refund = Refund.create(RefundCreateParams.builder()
                .setPaymentIntent(transactionId)
                .setAmount(amount.multiply(new BigDecimal(100)).longValue())
                .build());

        return PaymentResult.builder()
                .transactionId(refund.getId())
                .status(PaymentStatus.REFUNDED)
                .build();
    }
}

// Braintree adapter — same interface, different implementation
@Component
@ConditionalOnProperty(name = "payment.provider", havingValue = "braintree")
public class BraintreePaymentAdapter implements PaymentGateway {
    // ... Braintree-specific implementation
}

// OrderService doesn't know or care which payment provider is used
@Service
public class OrderService {
    private final PaymentGateway paymentGateway;  // Interface — could be Stripe or Braintree

    public void processPayment(Order order) {
        PaymentResult result = paymentGateway.charge(PaymentRequest.from(order));
        // ...
    }
}
```

#### Behavioral Patterns

##### Strategy Pattern

**Problem**: Multiple algorithms that can be swapped at runtime. (Covered above in OCP with `DiscountStrategy`.)

**In Spring**: The Strategy pattern is everywhere. Any interface with multiple `@Component` implementations is a strategy. Spring injects the appropriate one based on qualifiers or conditions.

##### Observer Pattern (Event-Driven)

**Problem**: When object A changes, objects B, C, D need to react — without A knowing about B, C, D.

```java
/**
 * Observer Pattern in Spring — using ApplicationEventPublisher.
 * The event source doesn't know who listens. New listeners can be added without modifying the source.
 */

// Event
public record OrderCreatedEvent(Order order, Instant occurredAt) {
    public OrderCreatedEvent(Order order) {
        this(order, Instant.now());
    }
}

// Publisher (doesn't know about listeners)
@Service
public class OrderService {
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = orderRepository.save(Order.from(request));
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        return order;
    }
}

// Listeners (don't know about each other)
@Component
public class InventoryListener {
    @EventListener
    public void reserveInventory(OrderCreatedEvent event) {
        inventoryService.reserve(event.order().getItems());
    }
}

@Component
public class NotificationListener {
    @Async  // Runs in a separate thread — doesn't slow down order creation
    @EventListener
    public void sendConfirmation(OrderCreatedEvent event) {
        emailService.send(new OrderConfirmationEmail(event.order()));
    }
}

@Component
public class AnalyticsListener {
    @Async
    @EventListener
    public void trackOrder(OrderCreatedEvent event) {
        analyticsService.track("order_created", Map.of(
                "orderId", event.order().getId(),
                "amount", event.order().getTotal()));
    }
}
```

##### Template Method Pattern

**Problem**: An algorithm has a fixed structure but some steps vary. Define the skeleton in a base class, let subclasses override specific steps.

```java
/**
 * Template Method — define the data import workflow.
 * Subclasses customize specific steps.
 */
public abstract class DataImportTemplate<T> {

    // The template method — defines the fixed workflow
    public final ImportResult importData(InputStream input) {
        List<T> rawRecords = parse(input);           // Step 1: Parse (varies by format)
        List<T> validRecords = validate(rawRecords);  // Step 2: Validate
        List<T> transformed = transform(validRecords); // Step 3: Transform (varies by domain)
        int saved = save(transformed);                 // Step 4: Save
        sendNotification(saved);                       // Step 5: Notify

        return new ImportResult(rawRecords.size(), validRecords.size(), saved);
    }

    // Abstract steps — subclasses MUST implement
    protected abstract List<T> parse(InputStream input);
    protected abstract List<T> transform(List<T> records);

    // Concrete steps — subclasses CAN override (hooks)
    protected List<T> validate(List<T> records) {
        return records.stream().filter(this::isValid).toList();
    }

    protected abstract boolean isValid(T record);
    protected abstract int save(List<T> records);

    protected void sendNotification(int count) {
        log.info("Imported {} records", count);
    }
}

// CSV Product importer
@Component
public class CsvProductImporter extends DataImportTemplate<Product> {
    @Override
    protected List<Product> parse(InputStream input) {
        // Parse CSV to Product objects
    }

    @Override
    protected List<Product> transform(List<Product> records) {
        // Normalize product names, calculate prices
    }

    @Override
    protected boolean isValid(Product product) {
        return product.getName() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    protected int save(List<Product> records) {
        return productRepository.saveAll(records).size();
    }
}
```

---

## 12.3 Microservices Design Patterns

---

### The "Why" & The Problem

Microservices introduce distributed system challenges. These patterns solve specific distributed system problems.

---

### The Deep Dive & Solution

#### CQRS — Command Query Responsibility Segregation

**Problem**: Read and write workloads have different performance characteristics. A write-optimized schema (normalized) is slow for complex reads. A read-optimized schema (denormalized) is complex for writes.

**Solution**: Separate the write model (commands) from the read model (queries). Each can be optimized independently.

```
┌────────────────────────────────────────────────────────────┐
│  CQRS Architecture                                          │
│                                                              │
│  Command Side (Write)              Query Side (Read)        │
│  ┌─────────────────┐              ┌─────────────────┐      │
│  │ POST /orders    │              │ GET /orders/{id} │      │
│  │ PUT /orders/{id}│              │ GET /orders?...  │      │
│  └────────┬────────┘              └────────┬────────┘      │
│           │                                 │                │
│  ┌────────▼────────┐              ┌────────▼────────┐      │
│  │ Command Handler │              │ Query Handler   │      │
│  │ (validates,     │              │ (simple read,   │      │
│  │  enforces       │              │  no business    │      │
│  │  business rules)│              │  logic)         │      │
│  └────────┬────────┘              └────────┬────────┘      │
│           │                                 │                │
│  ┌────────▼────────┐              ┌────────▼────────┐      │
│  │ Write Database  │  ──events──▶ │ Read Database   │      │
│  │ (normalized,    │              │ (denormalized,  │      │
│  │  PostgreSQL)    │              │  ElasticSearch  │      │
│  │                 │              │  or materialized│      │
│  │                 │              │  view)          │      │
│  └─────────────────┘              └─────────────────┘      │
└────────────────────────────────────────────────────────────┘
```

```java
/**
 * CQRS Implementation — Command side
 */
@Service
public class OrderCommandService {
    private final OrderRepository writeRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long createOrder(CreateOrderCommand command) {
        // Enforce business rules on the write side
        Order order = Order.create(command);
        order = writeRepository.save(order);

        // Publish event to update the read model
        eventPublisher.publishEvent(new OrderCreatedEvent(order));
        return order.getId();
    }

    @Transactional
    public void cancelOrder(CancelOrderCommand command) {
        Order order = writeRepository.findById(command.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order", command.getOrderId()));
        order.cancel(command.getReason());
        writeRepository.save(order);

        eventPublisher.publishEvent(new OrderCancelledEvent(order));
    }
}

/**
 * CQRS Implementation — Query side (optimized for reads)
 */
@Service
public class OrderQueryService {
    private final OrderReadRepository readRepository; // Backed by ElasticSearch or denormalized view

    public OrderSummaryDto getOrderSummary(Long orderId) {
        // Direct read from optimized read store — no joins, no complex queries
        return readRepository.findSummaryById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", orderId));
    }

    public Page<OrderSummaryDto> searchOrders(OrderSearchCriteria criteria) {
        // Full-text search, faceted filtering — optimized read model
        return readRepository.search(criteria);
    }
}

/**
 * Event listener that updates the read model when the write model changes.
 * This is the "projection" — it materializes the read-optimized view.
 */
@Component
public class OrderProjectionUpdater {

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        Order order = event.getOrder();

        // Build denormalized read model
        OrderSummaryDto summary = OrderSummaryDto.builder()
                .id(order.getId())
                .customerName(order.getCustomer().getFullName())  // Pre-joined!
                .itemCount(order.getItems().size())
                .totalAmount(order.getTotal())
                .status(order.getStatus().name())
                .build();

        readRepository.save(summary);
    }
}
```

**When to use CQRS**:
- Read and write workloads are dramatically different (e.g., 100:1 read-to-write ratio).
- Read queries are complex (full-text search, multi-table joins) and slow down the write path.
- You need different scaling strategies for reads vs. writes.

**When NOT to use CQRS**:
- Simple CRUD applications where reads and writes are similar.
- The added complexity of eventual consistency between read and write models is not justified.

#### Event Sourcing

**Problem**: Traditional databases store the *current state*. You lose the history. How did this order go from CREATED → PAID → SHIPPED → DELIVERED? When was the price changed?

**Solution**: Instead of storing the current state, store every **event** that happened. The current state is derived by replaying events.

```java
/**
 * Event Sourcing — the event store is the single source of truth.
 * Current state is computed by replaying all events.
 */

// Domain Events (immutable records of what happened)
public sealed interface OrderEvent permits
        OrderCreated, OrderItemAdded, OrderItemRemoved,
        PaymentProcessed, OrderShipped, OrderDelivered, OrderCancelled {
    String orderId();
    Instant occurredAt();
}

public record OrderCreated(String orderId, String customerId, Instant occurredAt) implements OrderEvent {}
public record OrderItemAdded(String orderId, String itemId, int quantity, BigDecimal price, Instant occurredAt) implements OrderEvent {}
public record PaymentProcessed(String orderId, String transactionId, BigDecimal amount, Instant occurredAt) implements OrderEvent {}
public record OrderShipped(String orderId, String trackingNumber, Instant occurredAt) implements OrderEvent {}

// The Aggregate — rebuilt from events
public class OrderAggregate {
    private String orderId;
    private String customerId;
    private OrderStatus status;
    private List<OrderItem> items = new ArrayList<>();
    private BigDecimal totalPaid = BigDecimal.ZERO;

    // Rebuild state from event history
    public static OrderAggregate fromEvents(List<OrderEvent> events) {
        OrderAggregate order = new OrderAggregate();
        events.forEach(order::apply);
        return order;
    }

    private void apply(OrderEvent event) {
        switch (event) {
            case OrderCreated e -> {
                this.orderId = e.orderId();
                this.customerId = e.customerId();
                this.status = OrderStatus.CREATED;
            }
            case OrderItemAdded e -> {
                this.items.add(new OrderItem(e.itemId(), e.quantity(), e.price()));
            }
            case PaymentProcessed e -> {
                this.totalPaid = this.totalPaid.add(e.amount());
                this.status = OrderStatus.PAID;
            }
            case OrderShipped e -> {
                this.status = OrderStatus.SHIPPED;
            }
            default -> { /* handle other events */ }
        }
    }

    // Command method — validates and produces new events
    public List<OrderEvent> addItem(String itemId, int quantity, BigDecimal price) {
        if (status != OrderStatus.CREATED) {
            throw new IllegalStateException("Cannot add items to a " + status + " order");
        }
        var event = new OrderItemAdded(orderId, itemId, quantity, price, Instant.now());
        apply(event);  // Apply to in-memory state
        return List.of(event);  // Return event to be persisted
    }
}

// Event Store
@Repository
public class EventStore {
    @Autowired private JdbcTemplate jdbc;

    public void append(String aggregateId, List<OrderEvent> events) {
        for (OrderEvent event : events) {
            jdbc.update(
                "INSERT INTO event_store (aggregate_id, event_type, event_data, occurred_at, version) VALUES (?, ?, ?::jsonb, ?, ?)",
                aggregateId, event.getClass().getSimpleName(),
                objectMapper.writeValueAsString(event), event.occurredAt(),
                getNextVersion(aggregateId));
        }
    }

    public List<OrderEvent> loadEvents(String aggregateId) {
        return jdbc.query(
            "SELECT event_type, event_data FROM event_store WHERE aggregate_id = ? ORDER BY version",
            (rs, rowNum) -> deserialize(rs.getString("event_type"), rs.getString("event_data")),
            aggregateId);
    }
}
```

**Event Sourcing pros**: Complete audit trail, time-travel debugging, event replay for new read models.
**Event Sourcing cons**: Complex, eventual consistency, event schema evolution is tricky.

---

## 12.4 Clean Architecture

---

### The "Why" & The Problem

Without architectural boundaries:
- Business logic is mixed with framework code. Switching from Spring to Quarkus requires rewriting everything.
- Business logic is mixed with database code. Switching from PostgreSQL to MongoDB requires touching business rules.
- Testing business logic requires booting the entire Spring context.

Clean Architecture enforces a **dependency rule**: inner layers (business logic) never depend on outer layers (frameworks, databases, UI).

---

### Interviewer Expectations

- **Layers**: Know the layers (Entities, Use Cases, Interface Adapters, Frameworks) and the dependency direction.
- **Dependency Rule**: The core business logic has zero dependencies on frameworks or databases.
- **Keywords**: "Clean Architecture", "Hexagonal Architecture", "Ports and Adapters", "Dependency Rule", "domain model", "use case", "inversion of control".

---

### The Deep Dive & Solution

#### Clean Architecture Layers

```
┌──────────────────────────────────────────────────────────┐
│  FRAMEWORKS & DRIVERS (outermost)                         │
│  Spring Boot, JPA, REST controllers, Kafka, Redis         │
│  ┌──────────────────────────────────────────────────────┐ │
│  │  INTERFACE ADAPTERS                                   │ │
│  │  Controllers, Presenters, Gateways, Repository impls  │ │
│  │  ┌──────────────────────────────────────────────────┐ │ │
│  │  │  APPLICATION / USE CASES                          │ │ │
│  │  │  CreateOrderUseCase, CancelOrderUseCase           │ │ │
│  │  │  ┌──────────────────────────────────────────────┐ │ │ │
│  │  │  │  DOMAIN / ENTITIES (innermost)               │ │ │ │
│  │  │  │  Order, Product, Customer                     │ │ │ │
│  │  │  │  Business rules, no framework dependencies    │ │ │ │
│  │  │  └──────────────────────────────────────────────┘ │ │ │
│  │  └──────────────────────────────────────────────────┘ │ │
│  └──────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘

Dependency Rule: Dependencies point INWARD only.
Inner layers know NOTHING about outer layers.
```

#### Package Structure

```
com.example.order/
├── domain/                          # INNERMOST — pure business logic, no Spring
│   ├── model/
│   │   ├── Order.java               # Entity with business methods
│   │   ├── OrderItem.java
│   │   ├── OrderStatus.java
│   │   └── Money.java               # Value object
│   ├── exception/
│   │   ├── InsufficientInventoryException.java
│   │   └── InvalidOrderException.java
│   └── port/                        # Interfaces (ports) — defined by domain, implemented by adapters
│       ├── in/                      # Inbound ports (use case interfaces)
│       │   ├── CreateOrderUseCase.java
│       │   ├── CancelOrderUseCase.java
│       │   └── GetOrderQuery.java
│       └── out/                     # Outbound ports (repository/gateway interfaces)
│           ├── OrderRepository.java     # Interface — NOT Spring's JpaRepository
│           ├── PaymentGateway.java
│           ├── InventoryGateway.java
│           └── EventPublisher.java
│
├── application/                     # USE CASES — orchestrate domain objects
│   ├── CreateOrderService.java      # Implements CreateOrderUseCase
│   ├── CancelOrderService.java
│   └── dto/
│       ├── CreateOrderCommand.java
│       └── OrderResult.java
│
├── adapter/                         # INTERFACE ADAPTERS — translate between domain and frameworks
│   ├── in/                          # Inbound adapters (drive the application)
│   │   ├── web/
│   │   │   ├── OrderController.java     # REST controller → calls use case
│   │   │   ├── OrderRequest.java        # HTTP request DTO
│   │   │   └── OrderResponseDto.java    # HTTP response DTO
│   │   └── messaging/
│   │       └── OrderEventKafkaListener.java
│   └── out/                         # Outbound adapters (driven by the application)
│       ├── persistence/
│       │   ├── JpaOrderRepository.java  # Implements domain's OrderRepository using JPA
│       │   ├── OrderJpaEntity.java      # JPA entity (separate from domain model!)
│       │   └── OrderJpaMapper.java      # Maps between domain Order and JPA entity
│       ├── payment/
│       │   └── StripePaymentAdapter.java # Implements PaymentGateway
│       └── messaging/
│           └── KafkaEventPublisher.java  # Implements EventPublisher
│
└── config/                          # FRAMEWORKS — Spring configuration
    ├── SecurityConfig.java
    ├── KafkaConfig.java
    └── PersistenceConfig.java
```

#### Clean Architecture Code Example

```java
// ──────────────────────────────────────────────────────────
// DOMAIN LAYER — pure Java, no framework dependencies
// ──────────────────────────────────────────────────────────

// Domain Entity — contains business rules
public class Order {
    private final String id;
    private final String customerId;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Money total;

    public static Order create(String customerId, List<OrderItem> items) {
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item");
        }
        Order order = new Order(UUID.randomUUID().toString(), customerId, items);
        order.status = OrderStatus.CREATED;
        order.total = items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::add);
        return order;
    }

    public void cancel(String reason) {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.DELIVERED) {
            throw new InvalidOrderException("Cannot cancel a " + status + " order");
        }
        this.status = OrderStatus.CANCELLED;
    }
    // No @Entity, no @Id, no Spring annotations — pure domain model
}

// Inbound Port — defines what the application can do
public interface CreateOrderUseCase {
    OrderResult execute(CreateOrderCommand command);
}

// Outbound Port — defines what the application needs
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
}

public interface PaymentGateway {
    PaymentResult charge(String customerId, Money amount);
}

// ──────────────────────────────────────────────────────────
// APPLICATION LAYER — orchestrates domain objects
// ──────────────────────────────────────────────────────────

@Service  // Spring annotation is OK here — this layer knows about Spring
@RequiredArgsConstructor
public class CreateOrderService implements CreateOrderUseCase {
    private final OrderRepository orderRepository;      // Port — not the JPA implementation
    private final PaymentGateway paymentGateway;        // Port — not the Stripe implementation
    private final InventoryGateway inventoryGateway;
    private final EventPublisher eventPublisher;

    @Override
    @Transactional
    public OrderResult execute(CreateOrderCommand command) {
        // 1. Create domain object (business rules enforced inside Order.create)
        Order order = Order.create(command.customerId(), command.items());

        // 2. Check inventory (via port — doesn't know about the HTTP client behind it)
        inventoryGateway.reserve(order.getItems());

        // 3. Process payment (via port — doesn't know about Stripe)
        PaymentResult payment = paymentGateway.charge(order.getCustomerId(), order.getTotal());

        // 4. Persist (via port — doesn't know about JPA or PostgreSQL)
        Order saved = orderRepository.save(order);

        // 5. Publish event (via port — doesn't know about Kafka)
        eventPublisher.publish(new OrderCreatedEvent(saved));

        return OrderResult.from(saved, payment);
    }
}

// ──────────────────────────────────────────────────────────
// ADAPTER LAYER — connects domain to the real world
// ──────────────────────────────────────────────────────────

// Inbound adapter: REST controller
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final CreateOrderUseCase createOrderUseCase;  // Depends on the use case interface

    @PostMapping
    public ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequest request) {
        CreateOrderCommand command = OrderMapper.toCommand(request);
        OrderResult result = createOrderUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrderMapper.toResponse(result));
    }
}

// Outbound adapter: JPA repository
@Repository
@RequiredArgsConstructor
public class JpaOrderRepository implements OrderRepository {
    private final SpringDataOrderRepository springRepo;  // Spring's JpaRepository
    private final OrderJpaMapper mapper;

    @Override
    public Order save(Order domainOrder) {
        OrderJpaEntity entity = mapper.toEntity(domainOrder);
        OrderJpaEntity saved = springRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Order> findById(String id) {
        return springRepo.findById(id).map(mapper::toDomain);
    }
}
```

**Key insight**: The domain layer has **zero imports from Spring, JPA, or any framework**. It can be tested with plain JUnit — no Spring context needed. If you switch from PostgreSQL to MongoDB, only the adapter layer changes. The domain and application layers are untouched.

---

## 12.5 Domain-Driven Design (DDD) — Key Concepts

---

### The "Why" & The Problem

In large systems, the biggest challenge is **understanding the business domain**. Technical complexity is manageable; business complexity is not. DDD provides patterns for organizing code around the business domain.

---

### Interviewer Expectations

- **Bounded Contexts**: Each microservice represents a bounded context with its own ubiquitous language.
- **Aggregates**: Consistency boundary around a group of entities. Only the aggregate root is directly accessible.
- **Value Objects**: Immutable objects identified by their values, not their identity (e.g., Money, Address).
- **Keywords**: "Bounded Context", "Aggregate", "Aggregate Root", "Value Object", "Entity", "Domain Event", "Ubiquitous Language", "Anti-Corruption Layer".

---

### The Deep Dive & Solution

#### Bounded Contexts

```
┌─────────────────────────────────────────────────────────────┐
│  E-Commerce System — Bounded Contexts                        │
│                                                               │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │
│  │  Order Context   │  │ Inventory Context│  │ Customer    │ │
│  │                  │  │                  │  │ Context     │ │
│  │  "Order"         │  │  "Product"       │  │  "Customer" │ │
│  │  "OrderItem"     │  │  "StockLevel"    │  │  "Address"  │ │
│  │  "OrderStatus"   │  │  "Warehouse"     │  │  "Account"  │ │
│  │                  │  │  "Reservation"   │  │  "Loyalty"  │ │
│  │  OrderService    │  │  InventoryService│  │  AccountSvc │ │
│  └────────┬─────────┘  └────────┬─────────┘  └──────┬──────┘ │
│           │   Domain Events      │                    │       │
│           └──────────────────────┴────────────────────┘       │
└─────────────────────────────────────────────────────────────┘

Key: "Customer" in the Order Context means: { customerId, name }
     "Customer" in the Customer Context means: { id, name, email, addresses, loyaltyPoints, ... }
     Same word, different meanings — that's why bounded contexts exist.
```

#### Aggregate Design

```java
/**
 * Order is an Aggregate Root. It controls access to OrderItems.
 * Rules:
 * 1. Only the Aggregate Root (Order) is referenced from outside.
 * 2. All changes go through the root (order.addItem, not item.setQuantity).
 * 3. One transaction = one aggregate.
 */
public class Order {  // Aggregate Root
    private final OrderId id;
    private final CustomerId customerId;
    private final List<OrderItem> items;  // Entities inside the aggregate
    private OrderStatus status;
    private Money total;

    // Business method — enforces invariants
    public void addItem(ProductId productId, int quantity, Money price) {
        if (status != OrderStatus.DRAFT) {
            throw new InvalidOrderException("Can only add items to DRAFT orders");
        }
        if (quantity <= 0) {
            throw new InvalidOrderException("Quantity must be positive");
        }
        if (items.size() >= 50) {
            throw new InvalidOrderException("Maximum 50 items per order");
        }

        // Check if item already exists — merge quantities
        items.stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        existing -> existing.increaseQuantity(quantity),
                        () -> items.add(new OrderItem(productId, quantity, price))
                );

        recalculateTotal();
    }

    public void submit() {
        if (status != OrderStatus.DRAFT) {
            throw new InvalidOrderException("Can only submit DRAFT orders");
        }
        if (items.isEmpty()) {
            throw new InvalidOrderException("Cannot submit an empty order");
        }
        this.status = OrderStatus.SUBMITTED;
        // Register domain event
        registerEvent(new OrderSubmittedEvent(this.id, this.customerId, this.total));
    }

    private void recalculateTotal() {
        this.total = items.stream()
                .map(OrderItem::subtotal)
                .reduce(Money.ZERO, Money::add);
    }
}

// Value Object — immutable, identified by value, not identity
public record Money(BigDecimal amount, Currency currency) {
    public static final Money ZERO = new Money(BigDecimal.ZERO, Currency.getInstance("USD"));

    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.scale() > 2) {
            amount = amount.setScale(2, RoundingMode.HALF_UP);
        }
    }

    public Money add(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot add different currencies");
        }
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money multiply(int quantity) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(quantity)), this.currency);
    }
}

// Typed ID — prevents mixing up OrderId and CustomerId
public record OrderId(String value) {
    public OrderId {
        Objects.requireNonNull(value, "OrderId cannot be null");
    }
}

public record CustomerId(String value) {
    public CustomerId {
        Objects.requireNonNull(value, "CustomerId cannot be null");
    }
}
```

---

## 12.6 Interview Quick Reference: Design Patterns & Architecture

---

### Common Interview Questions & Lead-Level Answers

**Q: "Explain SOLID principles with real-world examples."**

A: "**SRP**: An `OrderService` should handle order business logic, not email sending or audit logging. I use Spring's `ApplicationEventPublisher` to delegate cross-cutting concerns to separate listeners. **OCP**: A `DiscountCalculator` uses the Strategy pattern — new discount types are added as new classes, not by modifying existing code. **LSP**: Subtypes must honor the supertype's contract. If `CachingRepository` extends `Repository`, it must not silently return stale data when the caller expects fresh data. **ISP**: I split fat interfaces into focused ones — `UserReader`, `UserWriter`, `UserSearch` — so services depend only on what they use. **DIP**: Services depend on interfaces (`OrderRepository`), not concrete implementations (`JpaOrderRepository`). This enables unit testing with mocks and swapping implementations without changing business logic."

**Q: "How would you structure a microservice using Clean Architecture?"**

A: "I organize code into four layers with strict dependency direction: **Domain** (innermost) contains entities, value objects, and business rules with zero framework dependencies. **Application** contains use cases that orchestrate domain objects. **Adapter** contains inbound adapters (REST controllers, Kafka listeners) and outbound adapters (JPA repositories, HTTP clients for external services). **Config** (outermost) contains Spring configuration. The domain defines **ports** (interfaces) for what it needs — `OrderRepository`, `PaymentGateway`. The adapter layer provides **implementations** — `JpaOrderRepository`, `StripePaymentAdapter`. This means the domain can be tested with plain JUnit (no Spring context), and switching from PostgreSQL to MongoDB only changes the adapter layer."

**Q: "When would you use CQRS and Event Sourcing?"**

A: "I use **CQRS** when read and write workloads are dramatically different — for example, a system with 100:1 read-to-write ratio where reads require complex full-text search across denormalized data. The write side uses a normalized PostgreSQL schema optimized for consistency, while the read side uses ElasticSearch optimized for search queries. Events synchronize the two. I add **Event Sourcing** when I need a complete audit trail — financial systems, compliance-heavy domains, or when business stakeholders need to answer 'how did this order get to this state?' Event Sourcing is powerful but complex: it requires handling event schema evolution, snapshots for performance, and eventual consistency. For most CRUD services, simple CQRS without Event Sourcing (or even plain REST with a database) is sufficient. I always apply the **simplest architecture that meets the requirements** and evolve as complexity grows."
