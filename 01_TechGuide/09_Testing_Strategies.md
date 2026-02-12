# Section 8: Testing Strategies for Lead Engineers

---

## 8.1 The Testing Pyramid: Unit, Integration, Contract, and E2E Tests

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

#### Unit Testing with Mockito and JUnit 5

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

#### Integration Testing with Testcontainers

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
        // This test verifies that the query uses an index, not a sequential scan
        // Useful for catching performance regressions
        
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

// Integration test for the full API layer
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class OrderControllerIntegrationTest {
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16");
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("POST /api/orders should create order and return 201")
    void createOrder_shouldReturn201WithOrderResponse() {
        // Arrange
        CreateOrderRequest request = new CreateOrderRequest(
            "CUST-123",
            List.of(new OrderItem("PROD-1", 2, new BigDecimal("49.99"))),
            new ShippingAddress("123 Main St", "NYC", "NY", "10001")
        );
        
        // Act
        ResponseEntity<OrderResponse> response = restTemplate.postForEntity(
            "/api/orders", request, OrderResponse.class);
        
        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().orderId()).isNotBlank();
        assertThat(response.getBody().status()).isEqualTo(OrderStatus.PENDING);
        assertThat(response.getBody().totalAmount()).isEqualByComparingTo(new BigDecimal("99.98"));
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
}
```

#### Contract Testing with Spring Cloud Contract

Contract testing ensures that the API producer (backend) and consumer (frontend or another service) agree on the API contract. If either side breaks the contract, the tests fail immediately — before deployment.

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

#### Testing Best Practices for a Lead

| Practice | Why |
|----------|-----|
| **Test behavior, not implementation** | If you test that a method calls `repo.save()`, any refactoring breaks the test even if the behavior is correct. Instead, test that the order was persisted (check the output/side effect). |
| **Use `@DataJpaTest` for repository tests** | Faster than `@SpringBootTest` — only loads JPA components, not the entire context. |
| **One assertion concept per test** | A test that asserts 10 things is hard to debug when it fails. Which assertion broke? |
| **Name tests with the `should` convention** | `shouldCreateOrder_whenInventoryAvailable` is immediately understandable. |
| **Use Testcontainers instead of H2** | H2 has different SQL behavior than PostgreSQL. Tests pass on H2, fail in production. Use the same database engine in tests as in production. |
| **Run unit tests on every commit, integration tests on every PR** | Unit tests in < 10 seconds, integration tests in < 5 minutes. |


