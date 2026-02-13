# Section 9: Testing Strategies for Lead Engineers

---

## 9.1 The Testing Pyramid: Unit, Integration, Contract, and E2E Tests

---

### The "Why" & The Problem

A Lead Software Engineer is responsible for the **quality strategy** of their team's codebase. Without a coherent testing strategy:
- Developers write no tests (ship and pray), or they write the wrong kind of tests (slow E2E tests that take 45 minutes to run, providing little value).
- **Refactoring becomes terrifying**: Without tests, every code change is a gamble. Developers stop refactoring, and the codebase rots.
- **Integration failures in production**: Two services work perfectly in isolation but fail when talking to each other. Nobody tested the contract between them.
- **Flaky tests erode trust**: The CI pipeline fails 30% of the time due to flaky tests. Developers start ignoring test failures. Quality plummets.

A company pays you to know this because **the cost of a bug increases 10x at each stage**: a bug caught in a unit test costs 1 minute to fix. A bug caught in production costs hours of developer time, potential data corruption, and customer trust.

---

### Interviewer Expectations

- **Testing Pyramid**: Explain the three layers: many fast unit tests at the base, fewer integration tests in the middle, fewest E2E tests at the top. Know WHY this shape is optimal.
- **Unit testing**: Mocking dependencies with Mockito, testing behavior not implementation, the Arrange-Act-Assert pattern.
- **Integration testing**: `@SpringBootTest`, `@DataJpaTest`, Testcontainers for real databases, WireMock for external service mocking.
- **Contract testing**: Pact or Spring Cloud Contract for verifying API contracts between producer and consumer.
- **Keywords**: "Testing pyramid", "test doubles (mock, stub, spy, fake)", "Testcontainers", "WireMock", "contract testing", "mutation testing", "test coverage vs. test effectiveness", "Arrange-Act-Assert", "Given-When-Then".

---

### The Deep Dive & Solution

#### The Testing Pyramid

```
                    ┌──────────┐
                    │   E2E    │   Few (5-10%)
                    │  Tests   │   Slow, brittle, expensive
                    ├──────────┤   Tests full user journeys
                   ╱            ╲
                  ╱  Integration  ╲  Medium (20-30%)
                 ╱    Tests        ╲  Moderate speed
                ╱                    ╲ Tests component interactions
               ├──────────────────────┤
              ╱                        ╲
             ╱      Unit Tests          ╲  Many (60-70%)
            ╱                            ╲  Very fast (ms)
           ╱   Tests individual functions  ╲
          └────────────────────────────────┘
```

**Why this shape?**
- **Unit tests** run in milliseconds, catch 60-80% of bugs, and pinpoint the exact line of failure. They are cheap to write and maintain. You should have thousands of them.
- **Integration tests** verify that components work together (service + database, service + message broker). They catch configuration errors, serialization issues, and query bugs that unit tests miss. They take seconds to run.
- **E2E tests** verify complete user workflows through the entire stack. They are slow (minutes), brittle (break with UI changes), and expensive to maintain. You should have the fewest of these — only for critical business flows (login, checkout, payment).

#### Test Doubles — The Vocabulary

Every Lead must know these terms precisely:

| Type | Definition | Example |
|------|-----------|---------|
| **Mock** | A test double that records interactions and allows verification | `Mockito.mock(PaymentClient.class)` — verify `charge()` was called exactly once |
| **Stub** | A test double that returns pre-programmed responses | `when(repo.findById("123")).thenReturn(Optional.of(order))` |
| **Spy** | A partial mock that wraps a real object — real methods unless stubbed | `Mockito.spy(realService)` — calls real code, but you can override specific methods |
| **Fake** | A lightweight implementation of a real dependency | An in-memory `Map<String, Order>` instead of a real database |
| **Dummy** | An object passed around but never actually used | A placeholder parameter to satisfy a constructor |

#### Testing Strategy by Layer

| Layer | Speed | What It Tests | Tools | CI Phase |
|-------|-------|--------------|-------|----------|
| **Unit** | <10ms per test | Business logic, transformations, validations | JUnit 5, Mockito, AssertJ | Every commit |
| **Integration** | 1-30s per test | DB queries, HTTP clients, message producers | Testcontainers, WireMock, `@SpringBootTest` | Every PR |
| **Contract** | 1-5s per test | API compatibility between producer and consumer | Spring Cloud Contract, Pact | Every PR |
| **E2E** | 30s-5min per test | Full user workflows across the stack | Playwright, Cypress, Selenium | Nightly / pre-release |
| **Performance** | 1-30min per suite | Throughput, latency, scalability | Gatling, JMeter, k6 | Weekly / pre-release |
| **Security** | 5-30min per suite | Vulnerabilities, misconfigurations, secrets | OWASP ZAP, Snyk, SonarQube | Every PR + nightly |

---

## 9.2 Unit Testing: JUnit 5, Mockito, and BDD-Style Tests

---

### The "Why" & The Problem

Unit tests are the foundation of your testing strategy. They test a single unit of logic (typically one method or one class) in complete isolation from external dependencies (databases, HTTP services, message brokers). A well-unit-tested codebase gives developers confidence to refactor aggressively, ship quickly, and sleep soundly.

A company pays you to know this because:
- **Speed of development**: A developer who writes unit tests ships faster in the long run because they catch bugs before integration, avoid regression cycles, and refactor with confidence.
- **Code design signal**: Code that is hard to unit test is poorly designed (tight coupling, hidden dependencies, god classes). The act of writing unit tests forces better architecture.
- **Interview signal**: Interviewers watch how you test the code you write during a coding round. If you say "I would add tests for edge cases X, Y, Z" after solving a problem, you signal senior-level thinking.

---

### Interviewer Expectations

- **JUnit 5**: `@Test`, `@DisplayName`, `@ParameterizedTest`, `@Nested`, `@BeforeEach`, lifecycle annotations. Know the difference between JUnit 4 and 5.
- **Mockito**: `@Mock`, `@InjectMocks`, `when/thenReturn`, `verify`, `ArgumentCaptor`, `argThat`, `doThrow`, `spy`. Know the difference between `@Mock` and `@MockBean`.
- **Assertions**: AssertJ fluent assertions vs JUnit assertions. `assertThat().isEqualTo()`, `assertThatThrownBy()`, `assertThat().extracting()`.
- **Keywords**: "Arrange-Act-Assert", "Given-When-Then", "test isolation", "test behavior not implementation", "parameterized tests", "test naming conventions".

---

### The Deep Dive & Solution

#### Complete Unit Test Example with JUnit 5 and Mockito

```java
// Testing a service with mocked dependencies
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    
    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private PaymentClient paymentClient;
    
    @Mock
    private InventoryClient inventoryClient;
    
    @Mock
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    @InjectMocks
    private OrderService orderService;
    
    @Test
    @DisplayName("Should create order and charge payment when inventory is available")
    void createOrder_whenInventoryAvailable_shouldCreateAndCharge() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-123",
            List.of(new OrderItem("PROD-1", 2, new BigDecimal("49.99"))),
            new ShippingAddress("123 Main St", "NYC", "NY", "10001")
        );
        
        Order savedOrder = Order.builder()
            .id("ORD-456")
            .customerId("CUST-123")
            .status(OrderStatus.CONFIRMED)
            .totalAmount(new BigDecimal("99.98"))
            .build();
        
        when(inventoryClient.reserve(any())).thenReturn(ReservationResponse.success());
        when(orderRepository.save(any())).thenReturn(savedOrder);
        when(paymentClient.charge(any())).thenReturn(PaymentResponse.success("PAY-789"));
        
        // Act
        Order result = orderService.createOrder(request);
        
        // Assert
        assertThat(result.getId()).isEqualTo("ORD-456");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        
        // Verify interactions
        verify(inventoryClient).reserve(argThat(req -> 
            req.getItems().size() == 1 && 
            req.getItems().get(0).getProductId().equals("PROD-1")
        ));
        verify(paymentClient).charge(argThat(req ->
            req.getAmount().compareTo(new BigDecimal("99.98")) == 0
        ));
        verify(kafkaTemplate).send(eq("order-events"), any(OrderCreatedEvent.class));
    }
    
    @Test
    @DisplayName("Should rollback inventory when payment fails")
    void createOrder_whenPaymentFails_shouldRollbackInventory() {
        // Arrange
        CreateOrderRequest request = createValidRequest();
        
        when(inventoryClient.reserve(any())).thenReturn(ReservationResponse.success());
        when(orderRepository.save(any())).thenReturn(createSampleOrder());
        when(paymentClient.charge(any())).thenThrow(
            new PaymentException("Insufficient funds"));
        
        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(OrderCreationFailedException.class)
            .hasMessageContaining("Payment failed");
        
        // Verify compensation — inventory must be released
        verify(inventoryClient).release(any());
        verify(orderRepository).updateStatus(any(), eq(OrderStatus.CANCELLED));
    }
    
    @Test
    @DisplayName("Should throw when inventory is insufficient")
    void createOrder_whenInventoryInsufficient_shouldThrowWithoutChargingPayment() {
        // Arrange
        CreateOrderRequest request = createValidRequest();
        when(inventoryClient.reserve(any())).thenThrow(
            new InsufficientStockException("Product PROD-1 out of stock"));
        
        // Act & Assert
        assertThatThrownBy(() -> orderService.createOrder(request))
            .isInstanceOf(OrderCreationFailedException.class);
        
        // Verify payment was NEVER called
        verify(paymentClient, never()).charge(any());
    }
    
    @ParameterizedTest
    @CsvSource({
        "99.99,  0,     99.99",   // No discount
        "100.00, 10.00, 90.00",   // 10% discount
        "500.00, 50.00, 450.00"   // 10% discount on high-value
    })
    @DisplayName("Should calculate correct total with discounts")
    void calculateTotal_withVariousDiscounts(
            BigDecimal subtotal, BigDecimal discount, BigDecimal expected) {
        BigDecimal result = orderService.calculateTotal(subtotal, discount);
        assertThat(result).isEqualByComparingTo(expected);
    }
}
```

#### Nested Tests for Logical Grouping

```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @InjectMocks
    private UserService userService;
    
    @Mock
    private UserRepository userRepository;
    
    @Nested
    @DisplayName("When creating a user")
    class CreateUser {
        
        @Test
        @DisplayName("should save valid user")
        void shouldSaveValidUser() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
            when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
            
            User user = userService.createUser("test@example.com", "John");
            
            assertThat(user.getEmail()).isEqualTo("test@example.com");
            verify(userRepository).save(any());
        }
        
        @Test
        @DisplayName("should reject duplicate email")
        void shouldRejectDuplicateEmail() {
            when(userRepository.existsByEmail("test@example.com")).thenReturn(true);
            
            assertThatThrownBy(() -> userService.createUser("test@example.com", "John"))
                .isInstanceOf(DuplicateEmailException.class);
            
            verify(userRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("When deleting a user")
    class DeleteUser {
        
        @Test
        @DisplayName("should soft-delete existing user")
        void shouldSoftDeleteExistingUser() {
            User existing = User.builder().id("U-1").active(true).build();
            when(userRepository.findById("U-1")).thenReturn(Optional.of(existing));
            
            userService.deleteUser("U-1");
            
            verify(userRepository).save(argThat(u -> !u.isActive()));
        }
    }
}
```

#### ArgumentCaptor — Capturing Complex Arguments

```java
@Test
void shouldSendCorrectKafkaEvent() {
    // Arrange
    when(orderRepository.save(any())).thenReturn(createSampleOrder());
    when(inventoryClient.reserve(any())).thenReturn(ReservationResponse.success());
    when(paymentClient.charge(any())).thenReturn(PaymentResponse.success("PAY-1"));
    
    // Act
    orderService.createOrder(createValidRequest());
    
    // Capture the event that was sent to Kafka
    ArgumentCaptor<OrderCreatedEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
    verify(kafkaTemplate).send(eq("order-events"), eventCaptor.capture());
    
    // Assert on the captured event
    OrderCreatedEvent event = eventCaptor.getValue();
    assertThat(event.getOrderId()).isNotBlank();
    assertThat(event.getCustomerId()).isEqualTo("CUST-123");
    assertThat(event.getTotalAmount()).isEqualByComparingTo(new BigDecimal("99.98"));
    assertThat(event.getTimestamp()).isCloseTo(Instant.now(), within(5, ChronoUnit.SECONDS));
}
```

#### Custom AssertJ Assertions (Lead-Level Practice)

```java
// Create domain-specific assertions for cleaner tests
public class OrderAssert extends AbstractAssert<OrderAssert, Order> {
    
    protected OrderAssert(Order actual) {
        super(actual, OrderAssert.class);
    }
    
    public static OrderAssert assertThat(Order actual) {
        return new OrderAssert(actual);
    }
    
    public OrderAssert isConfirmed() {
        isNotNull();
        if (actual.getStatus() != OrderStatus.CONFIRMED) {
            failWithMessage("Expected order to be CONFIRMED but was %s", actual.getStatus());
        }
        return this;
    }
    
    public OrderAssert hasTotalAmountGreaterThan(BigDecimal amount) {
        isNotNull();
        if (actual.getTotalAmount().compareTo(amount) <= 0) {
            failWithMessage("Expected total > %s but was %s", amount, actual.getTotalAmount());
        }
        return this;
    }
    
    public OrderAssert belongsToCustomer(String customerId) {
        isNotNull();
        if (!actual.getCustomerId().equals(customerId)) {
            failWithMessage("Expected customer %s but was %s", customerId, actual.getCustomerId());
        }
        return this;
    }
}

// Usage in tests:
@Test
void shouldCreateHighValueOrder() {
    Order order = orderService.createOrder(highValueRequest);
    
    // Reads like English!
    OrderAssert.assertThat(order)
        .isConfirmed()
        .hasTotalAmountGreaterThan(new BigDecimal("1000"))
        .belongsToCustomer("CUST-VIP");
}
```

---

## 9.3 Integration Testing: Spring Boot Test, Testcontainers, and WireMock

---

### The "Why" & The Problem

Unit tests with mocks verify that your code works correctly **assuming the mocks behave like the real dependencies**. But mocks can lie. A mocked `OrderRepository.save()` always succeeds — but the real PostgreSQL might fail because of a unique constraint, a column length violation, or a query syntax difference between H2 and PostgreSQL. Integration tests catch these **seams between components**.

A company pays you to know this because:
- **N+1 query problems** are invisible in unit tests (there is no real database). Integration tests with real PostgreSQL via Testcontainers catch them.
- **Serialization mismatches** between your Java objects and Kafka/RabbitMQ messages only surface when you test against a real broker.
- **Configuration errors** (wrong JDBC URL, missing Flyway migration, incompatible Spring profiles) are only caught when the application context actually starts.

---

### Interviewer Expectations

- **`@SpringBootTest`**: Know the different `webEnvironment` modes (`MOCK`, `RANDOM_PORT`, `DEFINED_PORT`, `NONE`). Know when to use each.
- **`@DataJpaTest`**: Lighter-weight slice test that only loads JPA components. Know it uses an embedded database by default (and why you should override that with Testcontainers).
- **Testcontainers**: Know how to set up containers for PostgreSQL, Redis, Kafka, Elasticsearch. Know `@DynamicPropertySource` for injecting container connection details.
- **WireMock**: Know how to stub external HTTP services, simulate failures (timeouts, 5xx errors), and verify requests.
- **Keywords**: "slice test", "full context test", "Testcontainers", "DynamicPropertySource", "WireMock", "test profiles", "flyway test migration".

---

### The Deep Dive & Solution

#### Testcontainers with Spring Boot

Testcontainers spins up real Docker containers (PostgreSQL, Redis, Kafka) for integration tests. This tests your actual queries, serialization, and configuration — not mocked behavior.

```java
// Integration test with a REAL PostgreSQL database via Testcontainers
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class OrderRepositoryIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private TestEntityManager entityManager;
    
    @Test
    @DisplayName("Should find orders by customer ID with pagination")
    void findByCustomerId_shouldReturnPaginatedOrders() {
        // Arrange — insert test data into REAL PostgreSQL
        for (int i = 0; i < 25; i++) {
            entityManager.persist(Order.builder()
                .customerId("CUST-123")
                .status(i % 2 == 0 ? OrderStatus.PENDING : OrderStatus.SHIPPED)
                .totalAmount(new BigDecimal("99.99"))
                .orderDate(LocalDateTime.now().minusDays(i))
                .build());
        }
        entityManager.flush();
        
        // Act
        Page<Order> page = orderRepository.findByCustomerId(
            "CUST-123", PageRequest.of(0, 10, Sort.by("orderDate").descending()));
        
        // Assert
        assertThat(page.getTotalElements()).isEqualTo(25);
        assertThat(page.getContent()).hasSize(10);
        assertThat(page.getContent().get(0).getOrderDate())
            .isAfter(page.getContent().get(9).getOrderDate());  // Sorted correctly
    }
    
    @Test
    @DisplayName("Should execute native query with EXPLAIN ANALYZE optimization")
    void findHighValueOrders_shouldUseIndex() {
        // Insert enough data to make the optimizer prefer an index
        for (int i = 0; i < 1000; i++) {
            entityManager.persist(Order.builder()
                .customerId("CUST-" + i)
                .status(OrderStatus.CONFIRMED)
                .totalAmount(new BigDecimal(i * 10))
                .orderDate(LocalDateTime.now().minusDays(i))
                .build());
        }
        entityManager.flush();
        
        // Query should use the composite index on (status, total_amount)
        List<Order> highValueOrders = orderRepository
            .findByStatusAndTotalAmountGreaterThan(OrderStatus.CONFIRMED, new BigDecimal("5000"));
        
        assertThat(highValueOrders).allMatch(o -> 
            o.getTotalAmount().compareTo(new BigDecimal("5000")) > 0);
    }
}
```

#### Shared Testcontainer Base Class (DRY Pattern for Teams)

```java
// Base class — all integration tests reuse the same containers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {
    
    static final PostgreSQLContainer<?> POSTGRES;
    static final GenericContainer<?> REDIS;
    static final KafkaContainer KAFKA;
    
    static {
        // Start containers ONCE, share across all test classes
        POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")
            .withReuse(true);  // Reuse across test runs (faster local dev)
        
        REDIS = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .withReuse(true);
        
        KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"))
            .withReuse(true);
        
        POSTGRES.start();
        REDIS.start();
        KAFKA.start();
    }
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));
        registry.add("spring.kafka.bootstrap-servers", KAFKA::getBootstrapServers);
    }
    
    @Autowired
    protected TestRestTemplate restTemplate;
}

// Concrete tests extend the base
class OrderControllerIntegrationTest extends BaseIntegrationTest {
    
    @Test
    @DisplayName("POST /api/orders should create order and return 201")
    void createOrder_shouldReturn201WithOrderResponse() {
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-123",
            List.of(new OrderItem("PROD-1", 2, new BigDecimal("49.99"))),
            new ShippingAddress("123 Main St", "NYC", "NY", "10001")
        );
        
        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
            "/api/orders", request, OrderResponse.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().orderId()).isNotBlank();
        assertThat(response.getBody().status()).isEqualTo(OrderStatus.PENDING);
    }
}
```

#### Kafka Integration Testing

```java
class OrderEventIntegrationTest extends BaseIntegrationTest {
    
    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;
    
    @Autowired
    private OrderEventConsumer orderEventConsumer;
    
    @Test
    @DisplayName("Should produce and consume order event via Kafka")
    void shouldProduceAndConsumeOrderEvent() throws Exception {
        // Arrange
        OrderCreatedEvent event = new OrderCreatedEvent(
            "ORD-123", "CUST-456", new BigDecimal("199.99"), Instant.now());
        
        // Act — produce event
        kafkaTemplate.send("order-events", event.getOrderId(), event).get(10, TimeUnit.SECONDS);
        
        // Assert — wait for consumer to process
        await().atMost(Duration.ofSeconds(30)).untilAsserted(() -> {
            assertThat(orderEventConsumer.getProcessedEvents())
                .extracting(OrderCreatedEvent::getOrderId)
                .contains("ORD-123");
        });
    }
}
```

#### WireMock — Mocking External Services

```java
// Mocking an external payment gateway in integration tests
@SpringBootTest
@WireMockTest(httpPort = 8089)  // Start WireMock on port 8089
class PaymentIntegrationTest {
    
    @Autowired
    private PaymentService paymentService;
    
    @Test
    @DisplayName("Should handle payment gateway timeout gracefully")
    void chargePayment_whenGatewayTimesOut_shouldRetryAndFail() {
        // Arrange — simulate a slow payment gateway
        stubFor(post(urlEqualTo("/api/v1/charges"))
            .willReturn(aResponse()
                .withFixedDelay(5000)  // 5 second delay — will cause timeout
                .withStatus(200)));
        
        // Act & Assert
        assertThatThrownBy(() -> paymentService.charge("ORD-123", new BigDecimal("99.99")))
            .isInstanceOf(PaymentTimeoutException.class);
        
        // Verify retries happened
        verify(3, postRequestedFor(urlEqualTo("/api/v1/charges")));
    }
    
    @Test
    @DisplayName("Should parse payment gateway error response correctly")
    void chargePayment_whenGatewayReturns422_shouldThrowBusinessException() {
        // Arrange — simulate a business error from the payment gateway
        stubFor(post(urlEqualTo("/api/v1/charges"))
            .willReturn(aResponse()
                .withStatus(422)
                .withHeader("Content-Type", "application/json")
                .withBody("""
                    {
                      "error": "insufficient_funds",
                      "message": "The card has insufficient funds"
                    }
                    """)));
        
        // Act & Assert
        assertThatThrownBy(() -> paymentService.charge("ORD-123", new BigDecimal("99.99")))
            .isInstanceOf(InsufficientFundsException.class)
            .hasMessageContaining("insufficient funds");
    }
    
    @Test
    @DisplayName("Should simulate circuit breaker opening on repeated failures")
    void chargePayment_whenGatewayRepeatedlyFails_shouldOpenCircuitBreaker() {
        // Arrange — return 500 for all requests
        stubFor(post(urlEqualTo("/api/v1/charges"))
            .willReturn(aResponse().withStatus(500)));
        
        // Act — make enough calls to trip the circuit breaker
        for (int i = 0; i < 10; i++) {
            try {
                paymentService.charge("ORD-" + i, new BigDecimal("10.00"));
            } catch (Exception ignored) { }
        }
        
        // Assert — after the circuit breaker opens, calls should fail fast
        // without actually reaching the payment gateway
        assertThatThrownBy(() -> paymentService.charge("ORD-11", new BigDecimal("10.00")))
            .isInstanceOf(CircuitBreakerOpenException.class);
        
        // Verify the gateway was NOT called for the last attempt
        verify(lessThan(11), postRequestedFor(urlEqualTo("/api/v1/charges")));
    }
}
```

---

## 9.4 Contract Testing: Spring Cloud Contract and Consumer-Driven Contracts

---

### The "Why" & The Problem

In a microservices architecture, Service A calls Service B's API. Both teams develop independently. When Service B changes its API response format (adds a field, renames a field, changes a status code), Service A breaks — but nobody knows until production. **Contract testing** prevents this by defining an explicit API contract that both sides must satisfy.

A company pays you to know this because:
- **Integration tests between microservices are fragile**: They require both services running, correct data seeding, and network connectivity. They break for infrastructure reasons, not code reasons.
- **Contract tests run independently**: The producer verifies the contract in its CI pipeline. The consumer verifies against a stub generated from the contract. Neither needs the other running.
- **Breaking changes are caught at build time**, not production time.

---

### Interviewer Expectations

- **Consumer-Driven Contracts**: The consumer defines what it needs from the producer. The producer must satisfy these expectations.
- **Spring Cloud Contract**: Know the producer-side contract definition (Groovy DSL or YAML), the auto-generated test, and the consumer-side stub runner.
- **Pact**: Know it as an alternative (JSON-based contracts, Pact Broker for sharing contracts between teams).
- **Keywords**: "consumer-driven contracts", "contract verification", "stub runner", "Pact Broker", "backwards compatibility", "breaking vs. non-breaking changes".

---

### The Deep Dive & Solution

```java
// PRODUCER SIDE — Define the contract
// src/test/resources/contracts/order/shouldReturnOrderById.groovy
Contract.make {
    description "Should return order by ID"
    request {
        method GET()
        url "/api/orders/ORD-123"
        headers {
            contentType applicationJson()
        }
    }
    response {
        status 200
        headers {
            contentType applicationJson()
        }
        body([
            orderId: "ORD-123",
            customerId: "CUST-456",
            status: "CONFIRMED",
            totalAmount: 99.99,
            items: [[
                productId: "PROD-1",
                quantity: 2,
                price: 49.99
            ]]
        ])
    }
}

// Producer test base class — Spring Cloud Contract auto-generates tests
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class OrderContractTestBase {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private OrderService orderService;
    
    @BeforeEach
    void setup() {
        when(orderService.getOrder("ORD-123")).thenReturn(
            new OrderResponse("ORD-123", "CUST-456", OrderStatus.CONFIRMED,
                new BigDecimal("99.99"), List.of(
                    new OrderItemResponse("PROD-1", 2, new BigDecimal("49.99"))
                ))
        );
        
        RestAssuredMockMvc.mockMvc(mockMvc);
    }
}

// CONSUMER SIDE — Verify against the producer's contract
@SpringBootTest
@AutoConfigureStubRunner(
    ids = "com.myapp:order-service:+:stubs:8080",
    stubsMode = StubRunnerProperties.StubsMode.LOCAL
)
class OrderClientContractTest {
    
    @Autowired
    private OrderClient orderClient;  // The client that calls the order service
    
    @Test
    void shouldGetOrderById() {
        // The stub server automatically responds according to the contract
        OrderResponse order = orderClient.getOrder("ORD-123");
        
        assertThat(order.orderId()).isEqualTo("ORD-123");
        assertThat(order.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(order.totalAmount()).isEqualByComparingTo(new BigDecimal("99.99"));
    }
}
```

#### YAML-Based Contracts (Alternative Syntax)

```yaml
# src/test/resources/contracts/order/shouldCreateOrder.yml
description: "Should create order"
request:
  method: POST
  url: /api/orders
  headers:
    Content-Type: application/json
  body:
    customerId: "CUST-123"
    items:
      - productId: "PROD-1"
        quantity: 2
response:
  status: 201
  headers:
    Content-Type: application/json
  body:
    orderId: $(regex('[A-Z]{3}-[0-9]{3}'))
    status: "PENDING"
  matchers:
    body:
      - path: $.orderId
        type: by_regex
        value: "[A-Z]{3}-[0-9]+"
```

#### Pact (Consumer-Driven Contract Alternative)

```java
// Consumer side — define expectations
@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "order-service", port = "8080")
class OrderClientPactTest {
    
    @Pact(consumer = "checkout-service")
    public V4Pact getOrderPact(PactDslWithProvider builder) {
        return builder
            .given("order ORD-123 exists")
            .uponReceiving("a request for order ORD-123")
            .path("/api/orders/ORD-123")
            .method("GET")
            .willRespondWith()
            .status(200)
            .headers(Map.of("Content-Type", "application/json"))
            .body(newJsonBody(body -> {
                body.stringValue("orderId", "ORD-123");
                body.stringValue("status", "CONFIRMED");
                body.decimalType("totalAmount", 99.99);
            }).build())
            .toPact(V4Pact.class);
    }
    
    @Test
    @PactTestFor(pactMethod = "getOrderPact")
    void shouldGetOrder(MockServer mockServer) {
        OrderClient client = new OrderClient(mockServer.getUrl());
        OrderResponse order = client.getOrder("ORD-123");
        
        assertThat(order.orderId()).isEqualTo("ORD-123");
        assertThat(order.status()).isEqualTo("CONFIRMED");
    }
}
```

---

## 9.5 Performance Testing: Load Testing, Profiling, and SLA Validation

---

### The "Why" & The Problem

Your application works perfectly with 10 concurrent users. What happens at 10,000? Performance testing answers this question before your users do. Without performance testing:
- **Silent scalability walls**: Your database connection pool (default 10) becomes the bottleneck at 200 concurrent users. Your Tomcat thread pool (default 200) saturates at 1,000 concurrent requests. You discover this in production on Black Friday.
- **SLA violations**: You promised the customer <200ms P95 latency. But nobody measured it under load. Under real traffic, P95 is 3 seconds.
- **Memory leaks surface under load**: A leak that grows at 1KB per request is invisible with 100 requests/minute but fatal at 100,000 requests/minute.

A company pays you to know this because a Lead Engineer is responsible for **capacity planning** and **SLA guarantees**. You need to answer: "How many pods do we need to handle Black Friday traffic?" with data, not guesses.

---

### Interviewer Expectations

- **Load testing tools**: Know Gatling (Scala DSL, excellent reporting), JMeter (GUI-based, widely used), k6 (JavaScript-based, developer-friendly).
- **Load profiles**: Ramp-up, constant load, spike test, soak test. Know what each reveals.
- **Metrics to measure**: Throughput (req/sec), Latency (P50, P95, P99), Error rate, Resource utilization (CPU, memory, connection pools).
- **SLA/SLO/SLI**: Service Level Agreement (the promise), Service Level Objective (the target), Service Level Indicator (the measurement).
- **Keywords**: "P99 latency", "throughput saturation", "connection pool exhaustion", "load profile", "soak test", "stress test", "Gatling", "k6", "Apdex score".

---

### The Deep Dive & Solution

#### Load Test Profiles

```
Ramp-Up Test:                    Spike Test:
Users ▲                          Users ▲
      │        ╱────────         │    ╱╲
      │      ╱                   │   ╱  ╲
      │    ╱                     │  ╱    ╲────────
      │  ╱                       │ ╱
      │╱                         │╱
      └──────────── Time →       └──────────── Time →
Reveals: max capacity            Reveals: auto-scaling behavior

Soak Test:                       Stress Test:
Users ▲                          Users ▲
      │  ┌──────────────┐        │             ╱
      │  │              │        │           ╱
      │  │  constant    │        │         ╱
      │  │  load        │        │       ╱
      │  │              │        │     ╱ (until failure)
      └──┴──────────────┴─→      └───╱──────── Time →
Reveals: memory leaks, GC       Reveals: breaking point
```

| Profile | Duration | Purpose | What It Reveals |
|---------|----------|---------|-----------------|
| **Ramp-Up** | 10-30 min | Gradually increase load | Maximum sustainable throughput |
| **Spike** | 5-10 min | Sudden burst of traffic | Auto-scaling response time, error handling under burst |
| **Soak** | 2-8 hours | Constant moderate load | Memory leaks, connection pool leaks, GC degradation |
| **Stress** | 15-60 min | Increase load until failure | Breaking point, graceful degradation behavior |

#### Gatling Load Test (Scala DSL)

```scala
// Gatling simulation for an Order API
class OrderApiSimulation extends Simulation {
  
  val httpProtocol = http
    .baseUrl("http://localhost:8080")
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
  
  // Define user scenarios
  val createOrderScenario = scenario("Create Order Flow")
    .exec(
      http("Create Order")
        .post("/api/orders")
        .body(StringBody("""
          {
            "customerId": "CUST-${randomInt}",
            "items": [{"productId": "PROD-1", "quantity": 1}]
          }
        """)).asJson
        .check(status.is(201))
        .check(jsonPath("$.orderId").saveAs("orderId"))
    )
    .pause(1, 3)  // Think time: 1-3 seconds
    .exec(
      http("Get Order")
        .get("/api/orders/${orderId}")
        .check(status.is(200))
        .check(jsonPath("$.status").is("PENDING"))
    )
  
  val searchScenario = scenario("Search Orders")
    .exec(
      http("Search Orders")
        .get("/api/orders?customerId=CUST-123&page=0&size=20")
        .check(status.is(200))
    )
  
  // Load profile: ramp up to 500 users over 5 minutes, hold for 10 minutes
  setUp(
    createOrderScenario.inject(
      rampUsers(500).during(5.minutes),
      constantUsersPerSec(50).during(10.minutes)
    ),
    searchScenario.inject(
      rampUsers(200).during(5.minutes),
      constantUsersPerSec(20).during(10.minutes)
    )
  ).protocols(httpProtocol)
   .assertions(
     global.responseTime.percentile3.lt(500),     // P95 < 500ms
     global.responseTime.percentile4.lt(1000),    // P99 < 1000ms
     global.successfulRequests.percent.gt(99.5),   // > 99.5% success rate
     forAll.responseTime.mean.lt(200)              // Mean < 200ms
   )
}
```

#### k6 Load Test (JavaScript-based)

```javascript
// k6 load test — developer-friendly, runs from CLI
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// Custom metrics
const errorRate = new Rate('errors');
const orderCreationTime = new Trend('order_creation_time');

export const options = {
  stages: [
    { duration: '2m', target: 100 },   // Ramp up to 100 users
    { duration: '5m', target: 100 },   // Hold at 100
    { duration: '2m', target: 500 },   // Ramp up to 500
    { duration: '5m', target: 500 },   // Hold at 500
    { duration: '2m', target: 0 },     // Ramp down
  ],
  thresholds: {
    http_req_duration: ['p(95)<500', 'p(99)<1000'],  // SLA: P95 < 500ms
    errors: ['rate<0.01'],                             // Error rate < 1%
    order_creation_time: ['p(95)<300'],                // Custom: order creation P95 < 300ms
  },
};

export default function () {
  // Create order
  const createPayload = JSON.stringify({
    customerId: `CUST-${Math.floor(Math.random() * 10000)}`,
    items: [{ productId: 'PROD-1', quantity: 1 }],
  });
  
  const createRes = http.post('http://localhost:8080/api/orders', createPayload, {
    headers: { 'Content-Type': 'application/json' },
  });
  
  orderCreationTime.add(createRes.timings.duration);
  
  check(createRes, {
    'create order status is 201': (r) => r.status === 201,
    'create order has orderId': (r) => JSON.parse(r.body).orderId !== undefined,
  });
  
  errorRate.add(createRes.status !== 201);
  
  sleep(Math.random() * 3 + 1);  // Think time: 1-4 seconds
  
  // Get order
  if (createRes.status === 201) {
    const orderId = JSON.parse(createRes.body).orderId;
    const getRes = http.get(`http://localhost:8080/api/orders/${orderId}`);
    
    check(getRes, {
      'get order status is 200': (r) => r.status === 200,
    });
  }
}
```

#### SLA/SLO/SLI Framework

```
SLI (Service Level Indicator) — What you MEASURE
    ↓
SLO (Service Level Objective) — What you TARGET  
    ↓
SLA (Service Level Agreement) — What you PROMISE to customers
```

| Metric | SLI (Measured) | SLO (Internal Target) | SLA (Customer Promise) |
|--------|----------------|----------------------|----------------------|
| **Availability** | Successful requests / Total requests | 99.95% per month | 99.9% per month |
| **Latency** | P95 response time | P95 < 300ms | P95 < 500ms |
| **Error Rate** | 5xx errors / Total requests | < 0.05% | < 0.1% |
| **Throughput** | Requests per second | 10,000 rps | 5,000 rps |

**Error Budget**: If your SLO is 99.95% availability per month, you have a 0.05% error budget. On a 30-day month (43,200 minutes), that is 21.6 minutes of allowed downtime. When the error budget is exhausted, you freeze feature deployments and focus on reliability.

---

## 9.6 Security Testing: OWASP ZAP, SonarQube, and Dependency Scanning

---

### The "Why" & The Problem

Security vulnerabilities are the most expensive bugs. A SQL injection in production leads to data breaches, regulatory fines (GDPR: up to 4% of annual revenue), and loss of customer trust. Security testing catches these vulnerabilities **before** deployment.

A company pays you to know this because:
- **Shift-left security**: Finding a security bug in development costs $500 to fix. Finding it after a breach costs $5,000,000.
- **Compliance requirements**: SOC 2, PCI DSS, HIPAA, and ISO 27001 all require evidence of security testing.
- **Lead responsibility**: A Lead Engineer establishes the security testing practices for the team. If the team ships vulnerable code, you are accountable.

---

### Interviewer Expectations

- **SAST (Static Application Security Testing)**: Tools that scan source code for vulnerabilities (SonarQube, Checkmarx, Semgrep).
- **DAST (Dynamic Application Security Testing)**: Tools that attack running applications (OWASP ZAP, Burp Suite).
- **SCA (Software Composition Analysis)**: Tools that scan dependencies for known vulnerabilities (Snyk, OWASP Dependency-Check, Dependabot).
- **Keywords**: "SAST", "DAST", "SCA", "OWASP ZAP", "shift-left security", "dependency scanning", "CVE", "CVSS score", "security gate in CI pipeline".

---

### The Deep Dive & Solution

#### Security Testing in the CI/CD Pipeline

```
┌─────────────────────────────────────────────────────────────────┐
│                    CI/CD Security Pipeline                       │
│                                                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────────┐│
│  │  SAST    │  │  SCA     │  │  Secrets │  │  Container Scan  ││
│  │ SonarQube│→│  Snyk    │→│  Scanner │→│  Trivy / Grype   ││
│  │          │  │          │  │ gitleaks │  │                  ││
│  └──────────┘  └──────────┘  └──────────┘  └──────────────────┘│
│       ↓              ↓             ↓              ↓             │
│  ┌──────────────────────────────────────────────────────────────┐│
│  │  Quality Gate: FAIL if Critical/High vulnerabilities found   ││
│  └──────────────────────────────────────────────────────────────┘│
│       ↓ (pass)                                                   │
│  ┌──────────┐                                                    │
│  │  DAST    │  (Nightly or pre-release — runs against deployed  │
│  │ OWASP ZAP│   staging environment)                            │
│  └──────────┘                                                    │
└─────────────────────────────────────────────────────────────────┘
```

#### SAST with SonarQube in CI

```yaml
# GitHub Actions — SonarQube scan on every PR
name: Security Scan
on: [pull_request]

jobs:
  sonarqube:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0  # Full history for accurate analysis
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Build and analyze
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          ./mvnw verify sonar:sonar \
            -Dsonar.projectKey=my-project \
            -Dsonar.host.url=https://sonar.company.com \
            -Dsonar.qualitygate.wait=true  # Fails if quality gate not passed
      
  dependency-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Run Snyk to check for vulnerabilities
        uses: snyk/actions/maven@master
        env:
          SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
        with:
          args: --severity-threshold=high  # Fail on high/critical only
      
  secret-scan:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0
      
      - name: Scan for secrets
        uses: gitleaks/gitleaks-action@v2
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```

#### DAST with OWASP ZAP

```yaml
# OWASP ZAP scan against staging environment (nightly)
name: DAST Scan
on:
  schedule:
    - cron: '0 2 * * *'  # Every night at 2 AM

jobs:
  zap-scan:
    runs-on: ubuntu-latest
    steps:
      - name: OWASP ZAP Full Scan
        uses: zaproxy/action-full-scan@v0.9.0
        with:
          target: 'https://staging.myapp.com'
          rules_file_name: '.zap/rules.tsv'
          cmd_options: '-a -j'
      
      - name: Upload ZAP Report
        uses: actions/upload-artifact@v4
        with:
          name: zap-report
          path: report_html.html
```

#### OWASP Dependency-Check in Maven

```xml
<!-- pom.xml — automatically fail build if critical CVEs found -->
<plugin>
    <groupId>org.owasp</groupId>
    <artifactId>dependency-check-maven</artifactId>
    <version>9.0.9</version>
    <configuration>
        <failBuildOnCVSS>7</failBuildOnCVSS>  <!-- Fail on High (7+) -->
        <suppressionFiles>
            <suppressionFile>owasp-suppressions.xml</suppressionFile>
        </suppressionFiles>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

#### Security Unit Tests (Application Layer)

```java
// Test that your security configuration actually works
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityConfigIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("Unauthenticated request to protected endpoint should return 401")
    void protectedEndpoint_withoutToken_shouldReturn401() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            "/api/orders", String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    @DisplayName("Request with invalid JWT should return 401")
    void protectedEndpoint_withInvalidToken_shouldReturn401() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("invalid.jwt.token");
        
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/orders", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    @DisplayName("Non-admin user should not access admin endpoints")
    void adminEndpoint_withNonAdminToken_shouldReturn403() {
        String userToken = generateTestJwt("user@example.com", List.of("ROLE_USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/admin/users", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
    
    @Test
    @DisplayName("SQL injection attempt should be rejected")
    void searchEndpoint_withSqlInjection_shouldReturnBadRequest() {
        String userToken = generateTestJwt("user@example.com", List.of("ROLE_USER"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        
        ResponseEntity<String> response = restTemplate.exchange(
            "/api/orders?customerId=' OR '1'='1", HttpMethod.GET, 
            new HttpEntity<>(headers), String.class);
        
        // Should either return 400 (bad request) or empty results — NOT all orders
        assertThat(response.getStatusCode()).isIn(HttpStatus.BAD_REQUEST, HttpStatus.OK);
        if (response.getStatusCode() == HttpStatus.OK) {
            // If 200, verify it returned empty results, not all data
            assertThat(response.getBody()).doesNotContain("\"totalElements\":"); // Shouldn't return paginated full results
        }
    }
}
```

---

## 9.7 E2E Testing: Playwright, Cypress, and Page Object Model

---

### The "Why" & The Problem

End-to-end (E2E) tests verify that the entire application stack works together from the user's perspective. They simulate real user interactions: clicking buttons, filling forms, navigating pages, and verifying the displayed results. While they are the most expensive tier of the testing pyramid, they are essential for **critical business flows** (login, checkout, payment, onboarding).

A company pays you to know this because:
- **User-facing regressions**: A bug in the checkout flow directly impacts revenue. E2E tests for the checkout flow are worth their maintenance cost.
- **Cross-system integration**: E2E tests catch issues that no other test tier can: broken CSS, incorrect routing, frontend-backend serialization mismatches, CORS errors.
- **Confidence for releases**: A passing E2E suite for critical flows gives the team confidence to release on a Friday (yes, some teams do this).

---

### Interviewer Expectations

- **Tool landscape**: Know Playwright (Microsoft, modern, fast, multi-browser), Cypress (JavaScript, developer-friendly, single-browser), Selenium (legacy, cross-browser but slow).
- **Page Object Model**: Know why it exists (separation of test logic from UI interaction details) and how to implement it.
- **Flakiness mitigation**: Auto-wait strategies, retry mechanisms, test data isolation.
- **Keywords**: "Page Object Model", "auto-wait", "visual regression testing", "Playwright", "Cypress", "test data isolation", "headless browser", "parallel execution".

---

### The Deep Dive & Solution

#### Playwright (Recommended for 2026)

Playwright is the modern standard for E2E testing. It supports Chromium, Firefox, and WebKit out of the box, auto-waits for elements, and runs tests in parallel.

```typescript
// tests/order-creation.spec.ts — Playwright E2E test
import { test, expect } from '@playwright/test';

test.describe('Order Creation Flow', () => {
    
    test.beforeEach(async ({ page }) => {
        // Login before each test
        await page.goto('/login');
        await page.fill('[data-testid="email"]', 'testuser@example.com');
        await page.fill('[data-testid="password"]', 'TestPass123!');
        await page.click('[data-testid="login-button"]');
        await expect(page).toHaveURL('/dashboard');
    });
    
    test('should create a new order successfully', async ({ page }) => {
        // Navigate to create order page
        await page.click('[data-testid="create-order-btn"]');
        await expect(page).toHaveURL('/orders/new');
        
        // Fill order form
        await page.fill('[data-testid="customer-search"]', 'Acme Corp');
        await page.click('[data-testid="customer-option-CUST-123"]');
        
        // Add items
        await page.click('[data-testid="add-item-btn"]');
        await page.fill('[data-testid="product-search-0"]', 'Widget Pro');
        await page.click('[data-testid="product-option-PROD-1"]');
        await page.fill('[data-testid="quantity-0"]', '5');
        
        // Verify calculated total
        await expect(page.locator('[data-testid="order-total"]')).toHaveText('$249.95');
        
        // Submit order
        await page.click('[data-testid="submit-order-btn"]');
        
        // Verify success
        await expect(page.locator('[data-testid="success-message"]'))
            .toHaveText('Order created successfully');
        await expect(page).toHaveURL(/\/orders\/ORD-/);
        
        // Verify order appears in order list
        await page.click('[data-testid="nav-orders"]');
        await expect(page.locator('[data-testid="order-list"]'))
            .toContainText('Acme Corp');
    });
    
    test('should show validation errors for empty form', async ({ page }) => {
        await page.goto('/orders/new');
        await page.click('[data-testid="submit-order-btn"]');
        
        await expect(page.locator('[data-testid="error-customer"]'))
            .toHaveText('Customer is required');
        await expect(page.locator('[data-testid="error-items"]'))
            .toHaveText('At least one item is required');
    });
});
```

#### Page Object Model (POM)

The Page Object Model encapsulates page-specific interactions in reusable classes, separating **what the test does** from **how it interacts with the UI**. When the UI changes (a button moves, a selector changes), you update one Page Object instead of dozens of tests.

```typescript
// page-objects/LoginPage.ts
import { Page, Locator, expect } from '@playwright/test';

export class LoginPage {
    private readonly page: Page;
    private readonly emailInput: Locator;
    private readonly passwordInput: Locator;
    private readonly loginButton: Locator;
    private readonly errorMessage: Locator;
    
    constructor(page: Page) {
        this.page = page;
        this.emailInput = page.locator('[data-testid="email"]');
        this.passwordInput = page.locator('[data-testid="password"]');
        this.loginButton = page.locator('[data-testid="login-button"]');
        this.errorMessage = page.locator('[data-testid="error-message"]');
    }
    
    async goto(): Promise<void> {
        await this.page.goto('/login');
    }
    
    async login(email: string, password: string): Promise<void> {
        await this.emailInput.fill(email);
        await this.passwordInput.fill(password);
        await this.loginButton.click();
    }
    
    async expectError(message: string): Promise<void> {
        await expect(this.errorMessage).toHaveText(message);
    }
    
    async expectRedirectToDashboard(): Promise<void> {
        await expect(this.page).toHaveURL('/dashboard');
    }
}

// page-objects/OrderPage.ts
export class OrderPage {
    private readonly page: Page;
    
    constructor(page: Page) {
        this.page = page;
    }
    
    async createOrder(customer: string, items: Array<{product: string, qty: number}>): Promise<void> {
        await this.page.click('[data-testid="create-order-btn"]');
        await this.page.fill('[data-testid="customer-search"]', customer);
        await this.page.click(`[data-testid="customer-option-${customer}"]`);
        
        for (let i = 0; i < items.length; i++) {
            if (i > 0) await this.page.click('[data-testid="add-item-btn"]');
            await this.page.fill(`[data-testid="product-search-${i}"]`, items[i].product);
            await this.page.click(`[data-testid="product-option-${items[i].product}"]`);
            await this.page.fill(`[data-testid="quantity-${i}"]`, items[i].qty.toString());
        }
        
        await this.page.click('[data-testid="submit-order-btn"]');
    }
    
    async expectSuccess(): Promise<void> {
        await expect(this.page.locator('[data-testid="success-message"]'))
            .toBeVisible();
    }
    
    async getTotal(): Promise<string> {
        return await this.page.locator('[data-testid="order-total"]').textContent() ?? '';
    }
}

// Tests using Page Objects — clean and readable
test('should create order with Page Objects', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const orderPage = new OrderPage(page);
    
    await loginPage.goto();
    await loginPage.login('testuser@example.com', 'TestPass123!');
    await loginPage.expectRedirectToDashboard();
    
    await orderPage.createOrder('Acme Corp', [
        { product: 'Widget Pro', qty: 5 },
        { product: 'Gadget Max', qty: 2 },
    ]);
    await orderPage.expectSuccess();
});
```

#### Cypress Alternative (Angular-Friendly)

```typescript
// cypress/e2e/order.cy.ts
describe('Order Management', () => {
    
    beforeEach(() => {
        // Seed test data via API (faster than UI setup)
        cy.request('POST', '/api/test/reset-database');
        cy.request('POST', '/api/test/seed', {
            customers: [{ id: 'CUST-123', name: 'Acme Corp' }],
            products: [{ id: 'PROD-1', name: 'Widget Pro', price: 49.99 }]
        });
        
        // Login via API (skip the login UI — faster)
        cy.request('POST', '/api/auth/login', {
            email: 'testuser@example.com',
            password: 'TestPass123!'
        }).then(response => {
            window.localStorage.setItem('auth_token', response.body.accessToken);
        });
    });
    
    it('should create an order', () => {
        cy.visit('/orders/new');
        
        cy.get('[data-testid="customer-search"]').type('Acme');
        cy.get('[data-testid="customer-option-CUST-123"]').click();
        
        cy.get('[data-testid="product-search-0"]').type('Widget');
        cy.get('[data-testid="product-option-PROD-1"]').click();
        cy.get('[data-testid="quantity-0"]').clear().type('3');
        
        cy.get('[data-testid="order-total"]').should('contain', '$149.97');
        
        cy.get('[data-testid="submit-order-btn"]').click();
        
        cy.get('[data-testid="success-message"]').should('be.visible');
        cy.url().should('match', /\/orders\/ORD-/);
    });
});
```

#### E2E Testing Best Practices for Leads

| Practice | Why |
|----------|-----|
| **Use `data-testid` attributes** | Decouples tests from CSS classes and DOM structure. Tests survive UI redesigns. |
| **Login via API, not UI** | Skip the login flow in every test. Use `cy.request()` or Playwright's `request` API to get a token, then set it in localStorage. This saves 5-10 seconds per test. |
| **Seed test data via API** | Call a `/api/test/seed` endpoint to set up test data. Never rely on existing data or UI-based setup. |
| **Run E2E tests in parallel** | Playwright supports parallel test execution out of the box. Cypress requires the Dashboard service for parallelization. |
| **Keep the E2E suite small** | Only test critical business flows: login, registration, checkout, payment, core CRUD. Everything else should be covered by unit and integration tests. |
| **Visual regression testing** | Use Playwright's `toHaveScreenshot()` or Percy to catch unintended visual changes. |
| **Test data isolation** | Each test should create its own data and not depend on data from other tests. This prevents flakiness from test ordering. |
| **Auto-retry on flakiness** | Configure test retries (`retries: 2` in Playwright config) for network-dependent tests. But also fix the root cause of flakiness. |

#### Playwright Configuration for Angular

```typescript
// playwright.config.ts
import { defineConfig, devices } from '@playwright/test';

export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  workers: process.env.CI ? 4 : undefined,
  reporter: [
    ['html', { outputFolder: 'playwright-report' }],
    ['junit', { outputFile: 'test-results/e2e-results.xml' }],
  ],
  use: {
    baseURL: 'http://localhost:4200',
    trace: 'on-first-retry',       // Capture trace for debugging failed tests
    screenshot: 'only-on-failure',  // Screenshot on failure
    video: 'on-first-retry',       // Record video for debugging
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] },
    },
    {
      name: 'firefox',
      use: { ...devices['Desktop Firefox'] },
    },
    {
      name: 'mobile-chrome',
      use: { ...devices['Pixel 5'] },
    },
  ],
  webServer: {
    command: 'ng serve',
    url: 'http://localhost:4200',
    reuseExistingServer: !process.env.CI,
  },
});
```

---

## 9.8 Testing Best Practices for a Lead — Quick Reference

---

| Practice | Why |
|----------|-----|
| **Test behavior, not implementation** | If you test that a method calls `repo.save()`, any refactoring breaks the test even if the behavior is correct. Instead, test that the order was persisted (check the output/side effect). |
| **Use `@DataJpaTest` for repository tests** | Faster than `@SpringBootTest` — only loads JPA components, not the entire context. |
| **One assertion concept per test** | A test that asserts 10 things is hard to debug when it fails. Which assertion broke? |
| **Name tests with the `should` convention** | `shouldCreateOrder_whenInventoryAvailable` is immediately understandable. |
| **Use Testcontainers instead of H2** | H2 has different SQL behavior than PostgreSQL. Tests pass on H2, fail in production. Use the same database engine in tests as in production. |
| **Run unit tests on every commit, integration tests on every PR** | Unit tests in < 10 seconds, integration tests in < 5 minutes. |
| **Mutation testing with PIT** | Code coverage tells you which lines were executed, not which lines were actually tested. Mutation testing modifies your code (mutants) and checks if tests catch the changes. If a mutant survives, your tests are weak. |
| **Test the sad path more than the happy path** | Production code fails far more often than it succeeds. Test error handling, timeouts, invalid input, edge cases, boundary conditions, concurrent access, and null values. |
| **Arrange-Act-Assert (AAA)** | Every test has three sections: set up the data (Arrange), call the code under test (Act), verify the result (Assert). This structure makes tests immediately readable. |
| **Shared test fixtures** | Use `@BeforeEach` for common setup. Use `@TestConfiguration` to define test-specific beans. Use a shared Testcontainers base class to avoid container startup per test class. |

### Interview Quick Reference: Testing

| Topic | Must-Say Keywords |
|-------|-------------------|
| **Testing Pyramid** | Many unit, fewer integration, fewest E2E; cost increases up the pyramid |
| **Unit Testing** | JUnit 5, Mockito, @ExtendWith, @Mock, @InjectMocks, Arrange-Act-Assert |
| **Integration Testing** | Testcontainers, @SpringBootTest, @DynamicPropertySource, real database |
| **Contract Testing** | Spring Cloud Contract, Pact, consumer-driven, stub runner |
| **Performance Testing** | Gatling, k6, P95/P99 latency, throughput, soak test, stress test |
| **Security Testing** | SAST (SonarQube), DAST (ZAP), SCA (Snyk), quality gate, CVE, CVSS |
| **E2E Testing** | Playwright, Page Object Model, data-testid, auto-wait, parallel execution |
| **Mutation Testing** | PIT, mutant survival rate, test effectiveness vs. test coverage |
| **Test Doubles** | Mock (verify interactions), Stub (return values), Spy (partial mock), Fake (lightweight impl) |
