# Section 16: Spring Ecosystem Deep Dive

> **Level**: MID+ (Spring Data, Profiles) to LEAD (WebFlux, Cloud, Batch, AOP internals)
> **Why This Matters**: Lead engineers own the full Spring Boot stack. Interviewers expect you to go beyond `@RestController` — into JPA internals, reactive programming, cloud-native patterns, and custom AOP.

---

## 16.1 Spring Data JPA — Beyond Basics

### The "Why" & The Problem

Most developers use `JpaRepository.findById()` and stop there. Interviewers expect:
- How do Query Methods work under the hood?
- When to use Specifications vs JPQL vs Native SQL?
- How to avoid the N+1 problem with JPA?
- Auditing, Projections, and custom repository implementations

### Interviewer Expectations

| Level | What They Expect |
|-------|-----------------|
| **Junior** | Basic CRUD with JpaRepository |
| **Mid** | Query methods, @Query, pagination, sorting |
| **Senior** | Specifications, projections, entity graphs, auditing |
| **Lead** | Performance tuning, batch operations, custom repositories, query plan analysis |

### The Deep Dive & Solution

#### Query Method Resolution

```java
// Spring Data JPA generates queries from method names at startup
// It parses the method name into a query AST

public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Simple property match → SELECT * FROM orders WHERE status = ?
    List<Order> findByStatus(OrderStatus status);
    
    // Multiple conditions → AND
    List<Order> findByStatusAndCustomerId(OrderStatus status, Long customerId);
    
    // Comparison operators
    List<Order> findByAmountGreaterThan(BigDecimal amount);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findByCustomerNameContainingIgnoreCase(String namePart);
    
    // Limiting results
    Optional<Order> findFirstByCustomerIdOrderByCreatedAtDesc(Long customerId);
    List<Order> findTop5ByStatusOrderByAmountDesc(OrderStatus status);
    
    // Existence checks (returns boolean — more efficient than count)
    boolean existsByCustomerIdAndStatus(Long customerId, OrderStatus status);
    
    // Delete (returns count of deleted rows)
    long deleteByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime before);
    
    // Count
    long countByStatus(OrderStatus status);
}
```

#### @Query — JPQL and Native SQL

```java
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // JPQL (operates on entities, not tables)
    @Query("SELECT o FROM Order o WHERE o.customer.id = :customerId AND o.status = :status")
    List<Order> findCustomerOrders(@Param("customerId") Long customerId, 
                                   @Param("status") OrderStatus status);
    
    // JPQL with JOIN FETCH (solves N+1 problem)
    @Query("SELECT o FROM Order o JOIN FETCH o.items JOIN FETCH o.customer WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
    
    // Native SQL (when JPQL can't express the query)
    @Query(value = """
        SELECT o.*, c.name as customer_name
        FROM orders o
        JOIN customers c ON o.customer_id = c.id
        WHERE o.amount > :minAmount
        AND o.created_at >= NOW() - INTERVAL ':days days'
        ORDER BY o.amount DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Order> findHighValueRecentOrders(@Param("minAmount") BigDecimal minAmount,
                                          @Param("days") int days,
                                          @Param("limit") int limit);
    
    // Modifying queries (UPDATE/DELETE)
    @Modifying
    @Query("UPDATE Order o SET o.status = :status WHERE o.id IN :ids")
    int bulkUpdateStatus(@Param("ids") List<Long> ids, @Param("status") OrderStatus status);
    
    // Projection query (select specific columns)
    @Query("SELECT new com.example.dto.OrderSummaryDTO(o.id, o.amount, o.status, o.createdAt) " +
           "FROM Order o WHERE o.customer.id = :customerId")
    List<OrderSummaryDTO> findOrderSummaries(@Param("customerId") Long customerId);
}
```

#### Specifications — Dynamic Queries

```java
// Specifications are reusable, composable query predicates
// Use when you have complex, dynamic filter combinations (like search pages)

public class OrderSpecifications {
    
    public static Specification<Order> hasStatus(OrderStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
    
    public static Specification<Order> amountGreaterThan(BigDecimal amount) {
        return (root, query, cb) -> cb.greaterThan(root.get("amount"), amount);
    }
    
    public static Specification<Order> createdAfter(LocalDateTime date) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), date);
    }
    
    public static Specification<Order> customerNameLike(String name) {
        return (root, query, cb) -> {
            Join<Order, Customer> customer = root.join("customer");
            return cb.like(cb.lower(customer.get("name")), "%" + name.toLowerCase() + "%");
        };
    }
    
    public static Specification<Order> withFetchedItems() {
        return (root, query, cb) -> {
            if (query.getResultType() != Long.class) { // Skip for count queries
                root.fetch("items", JoinType.LEFT);
            }
            return null; // No filtering, just eager fetch
        };
    }
}

// Usage — compose dynamically:
@Service
public class OrderSearchService {
    
    private final OrderRepository repository;
    
    public Page<Order> search(OrderSearchCriteria criteria, Pageable pageable) {
        Specification<Order> spec = Specification.where(null); // Start with no filter
        
        if (criteria.getStatus() != null) {
            spec = spec.and(OrderSpecifications.hasStatus(criteria.getStatus()));
        }
        if (criteria.getMinAmount() != null) {
            spec = spec.and(OrderSpecifications.amountGreaterThan(criteria.getMinAmount()));
        }
        if (criteria.getStartDate() != null) {
            spec = spec.and(OrderSpecifications.createdAfter(criteria.getStartDate()));
        }
        if (criteria.getCustomerName() != null) {
            spec = spec.and(OrderSpecifications.customerNameLike(criteria.getCustomerName()));
        }
        
        return repository.findAll(spec, pageable);
    }
}

// Repository must extend JpaSpecificationExecutor:
public interface OrderRepository extends 
        JpaRepository<Order, Long>, 
        JpaSpecificationExecutor<Order> {
}
```

#### Projections — Fetch Only What You Need

```java
// Interface-based projection (Spring generates proxy)
public interface OrderSummary {
    Long getId();
    BigDecimal getAmount();
    OrderStatus getStatus();
    
    @Value("#{target.customer.name}") // SpEL for computed values
    String getCustomerName();
}

// Usage in repository:
List<OrderSummary> findByStatus(OrderStatus status); // Returns projections, not full entities

// Class-based projection (DTO)
public record OrderSummaryDTO(Long id, BigDecimal amount, OrderStatus status, String customerName) {}

// Dynamic projections (choose at runtime):
<T> List<T> findByStatus(OrderStatus status, Class<T> type);

// Call:
List<OrderSummary> summaries = repo.findByStatus(COMPLETED, OrderSummary.class);
List<Order> fullEntities = repo.findByStatus(COMPLETED, Order.class);
```

#### Auditing — Auto-Track Created/Modified

```java
@Entity
@EntityListeners(AuditingEntityListener.class) // Enable auditing
public abstract class BaseEntity {
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    @CreatedBy
    @Column(updatable = false)
    private String createdBy;
    
    @LastModifiedBy
    private String updatedBy;
}

// Configure auditor:
@Configuration
@EnableJpaAuditing
public class JpaConfig {
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
            .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getName);
    }
}
```

#### Batch Operations for Performance

```java
// Problem: Inserting 10,000 entities one by one → 10,000 SQL INSERTs

// Solution 1: Spring Data JPA saveAll() with batch config
// application.yml:
// spring.jpa.properties.hibernate.jdbc.batch_size: 50
// spring.jpa.properties.hibernate.order_inserts: true
// spring.jpa.properties.hibernate.order_updates: true

@Transactional
public void importOrders(List<OrderDTO> dtos) {
    List<Order> orders = dtos.stream()
        .map(this::toEntity)
        .collect(Collectors.toList());
    
    // Save in batches of 50 (configured via batch_size)
    orderRepository.saveAll(orders);
    
    // For very large datasets, flush and clear periodically:
    for (int i = 0; i < orders.size(); i++) {
        entityManager.persist(orders.get(i));
        if (i % 50 == 0) {
            entityManager.flush();
            entityManager.clear(); // Release memory
        }
    }
}

// Solution 2: JDBC batch for maximum performance (bypasses JPA overhead)
@Repository
public class OrderBatchRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void batchInsert(List<Order> orders) {
        String sql = "INSERT INTO orders (customer_id, amount, status, created_at) VALUES (?, ?, ?, ?)";
        
        jdbcTemplate.batchUpdate(sql, orders, 1000, (ps, order) -> {
            ps.setLong(1, order.getCustomerId());
            ps.setBigDecimal(2, order.getAmount());
            ps.setString(3, order.getStatus().name());
            ps.setTimestamp(4, Timestamp.valueOf(order.getCreatedAt()));
        });
    }
}
```

---

## 16.2 Spring WebFlux & Project Reactor

### The "Why" & The Problem

Traditional Spring MVC uses thread-per-request model. At 10,000 concurrent connections, you need 10,000 threads (expensive). WebFlux uses non-blocking I/O with a small thread pool (default: CPU cores × 2).

**When to use WebFlux:**
- High-concurrency, I/O-heavy workloads (API gateways, BFFs, streaming)
- Microservices calling many downstream services
- Server-Sent Events (SSE), WebSocket, streaming responses

**When NOT to use WebFlux:**
- CRUD applications with blocking JDBC (defeats the purpose)
- CPU-heavy computation
- Team unfamiliar with reactive programming
- Using blocking libraries (most JDBC, synchronous HTTP clients)

### The Deep Dive & Solution

#### Mono and Flux — The Core Types

```java
// Mono<T> — 0 or 1 element (like Optional but async)
// Flux<T> — 0 to N elements (like Stream but async)

// Creating:
Mono<String> mono = Mono.just("hello");
Mono<String> empty = Mono.empty();
Mono<String> error = Mono.error(new RuntimeException("oops"));
Mono<String> deferred = Mono.fromCallable(() -> expensiveOperation()); // Lazy

Flux<String> flux = Flux.just("A", "B", "C");
Flux<Integer> range = Flux.range(1, 100);
Flux<Long> interval = Flux.interval(Duration.ofSeconds(1)); // Infinite stream

// Transforming:
Mono<User> user = userRepository.findById(id)             // Mono<User>
    .map(u -> enrichUser(u))                                // Mono<User>
    .flatMap(u -> addressService.getAddress(u.getId()))     // Mono<Address>
    .switchIfEmpty(Mono.error(new UserNotFoundException())); // Handle empty

Flux<OrderDTO> orders = orderRepository.findByCustomerId(customerId) // Flux<Order>
    .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
    .map(this::toDTO)
    .take(10);
```

#### Reactive REST Controller

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    @GetMapping("/{id}")
    public Mono<ResponseEntity<OrderDTO>> getOrder(@PathVariable Long id) {
        return orderService.findById(id)
            .map(order -> ResponseEntity.ok(order))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public Flux<OrderDTO> getOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return orderService.findAll(page, size);
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<OrderDTO> createOrder(@Valid @RequestBody Mono<CreateOrderRequest> request) {
        return request.flatMap(orderService::create);
    }
    
    // Server-Sent Events (SSE) — real-time streaming
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<OrderDTO> streamOrders() {
        return orderService.streamNewOrders(); // Infinite stream
    }
}
```

#### WebClient — Non-Blocking HTTP Client

```java
@Service
public class ExternalApiService {
    
    private final WebClient webClient;
    
    public ExternalApiService(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("https://api.external.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(ExchangeFilterFunctions.basicAuthentication("user", "pass"))
            .build();
    }
    
    // Simple GET
    public Mono<ProductDTO> getProduct(String id) {
        return webClient.get()
            .uri("/products/{id}", id)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, 
                response -> Mono.error(new ProductNotFoundException(id)))
            .bodyToMono(ProductDTO.class)
            .timeout(Duration.ofSeconds(5))
            .retryWhen(Retry.backoff(3, Duration.ofMillis(500)));
    }
    
    // Fan-out: Call multiple services in parallel
    public Mono<AggregatedResult> getAggregatedData(String userId) {
        Mono<UserProfile> profile = getProfile(userId);
        Mono<List<Order>> orders = getOrders(userId);
        Mono<WalletBalance> wallet = getWallet(userId);
        
        return Mono.zip(profile, orders, wallet)
            .map(tuple -> new AggregatedResult(
                tuple.getT1(),  // profile
                tuple.getT2(),  // orders
                tuple.getT3()   // wallet
            ));
    }
}
```

---

## 16.3 Spring Cloud — Microservices Infrastructure

### The Deep Dive & Solution

#### Service Discovery — Eureka/Consul

```java
// Server side (Eureka Server):
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApp { }

// Client side (every microservice):
// application.yml:
// eureka:
//   client:
//     serviceUrl:
//       defaultZone: http://eureka-server:8761/eureka
//   instance:
//     preferIpAddress: true
//     leaseRenewalIntervalInSeconds: 10

// Now you can call services by name:
@FeignClient(name = "order-service") // Resolves via Eureka
public interface OrderServiceClient {
    @GetMapping("/api/orders/{id}")
    OrderDTO getOrder(@PathVariable Long id);
}
```

#### Spring Cloud Gateway — API Gateway

```java
// application.yml:
// spring:
//   cloud:
//     gateway:
//       routes:
//         - id: order-service
//           uri: lb://order-service          # Load-balanced via Eureka
//           predicates:
//             - Path=/api/orders/**
//             - Method=GET,POST
//           filters:
//             - StripPrefix=1
//             - AddRequestHeader=X-Request-Source, gateway
//             - CircuitBreaker=name=orderCB,fallbackUri=forward:/fallback
//             - RequestRateLimiter=redis-rate-limiter.replenishRate=10,redis-rate-limiter.burstCapacity=20
//         - id: user-service
//           uri: lb://user-service
//           predicates:
//             - Path=/api/users/**

// Custom global filter:
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        
        return validateToken(token)
            .flatMap(userId -> {
                exchange.getRequest().mutate()
                    .header("X-User-Id", userId)
                    .build();
                return chain.filter(exchange);
            })
            .onErrorResume(e -> {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            });
    }
    
    @Override
    public int getOrder() {
        return -1; // Run before other filters
    }
}
```

#### Spring Cloud Config — Centralized Configuration

```java
// Config Server:
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApp { }

// application.yml (config server):
// spring:
//   cloud:
//     config:
//       server:
//         git:
//           uri: https://github.com/org/config-repo
//           default-label: main
//           search-paths: '{application}'

// Client microservice application.yml:
// spring:
//   config:
//     import: configserver:http://config-server:8888
//   application:
//     name: order-service
//   profiles:
//     active: production

// Dynamic config refresh (without restart):
@RefreshScope // Bean re-created when /actuator/refresh is called
@Component
public class FeatureFlags {
    @Value("${feature.new-checkout-flow:false}")
    private boolean newCheckoutFlow;
    
    public boolean isNewCheckoutFlowEnabled() {
        return newCheckoutFlow;
    }
}
// POST http://service:8080/actuator/refresh → refreshes @RefreshScope beans
// With Spring Cloud Bus + Kafka: one refresh propagates to all instances
```

---

## 16.4 Spring Batch — Processing Large Datasets

### The Deep Dive & Solution

```java
// Spring Batch is for processing millions of records:
// ETL jobs, report generation, data migration, nightly batch processing

@Configuration
public class OrderExportBatchConfig {
    
    @Bean
    public Job orderExportJob(JobRepository jobRepository, Step exportStep) {
        return new JobBuilder("orderExportJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .start(exportStep)
            .build();
    }
    
    @Bean
    public Step exportStep(JobRepository jobRepository, 
                           PlatformTransactionManager txManager) {
        return new StepBuilder("exportStep", jobRepository)
            .<Order, OrderExportDTO>chunk(100, txManager)  // Process 100 at a time
            .reader(orderReader())
            .processor(orderProcessor())
            .writer(csvWriter())
            .faultTolerant()
            .skip(DataFormatException.class)
            .skipLimit(50)                      // Skip up to 50 bad records
            .retry(TransientDataAccessException.class)
            .retryLimit(3)                      // Retry transient errors 3 times
            .listener(new StepExecutionListener() {
                @Override
                public ExitStatus afterStep(StepExecution stepExecution) {
                    log.info("Processed {} records, skipped {}", 
                        stepExecution.getWriteCount(), stepExecution.getSkipCount());
                    return stepExecution.getExitStatus();
                }
            })
            .build();
    }
    
    @Bean
    @StepScope
    public JpaPagingItemReader<Order> orderReader() {
        return new JpaPagingItemReaderBuilder<Order>()
            .name("orderReader")
            .entityManagerFactory(entityManagerFactory)
            .queryString("SELECT o FROM Order o WHERE o.status = 'COMPLETED' ORDER BY o.id")
            .pageSize(100)
            .build();
    }
    
    @Bean
    public ItemProcessor<Order, OrderExportDTO> orderProcessor() {
        return order -> {
            if (order.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                return null; // Skip invalid records (returning null filters them out)
            }
            return new OrderExportDTO(
                order.getId(),
                order.getCustomer().getName(),
                order.getAmount(),
                order.getCreatedAt()
            );
        };
    }
    
    @Bean
    public FlatFileItemWriter<OrderExportDTO> csvWriter() {
        return new FlatFileItemWriterBuilder<OrderExportDTO>()
            .name("csvWriter")
            .resource(new FileSystemResource("output/orders-export.csv"))
            .headerCallback(writer -> writer.write("ID,Customer,Amount,Date"))
            .lineAggregator(new DelimitedLineAggregator<>() {{
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                    setNames(new String[]{"id", "customerName", "amount", "createdAt"});
                }});
            }})
            .build();
    }
}
```

---

## 16.5 Spring AOP — Custom Aspects

### The Deep Dive & Solution

```java
// AOP (Aspect-Oriented Programming) separates cross-cutting concerns:
// Logging, security, transaction management, caching, metrics, rate limiting

// How Spring AOP works:
// 1. Spring creates a PROXY around target beans (CGLIB or JDK Dynamic Proxy)
// 2. Method calls go through the proxy → aspect advice runs → actual method executes
// 3. CGLIB proxy: used for class-based proxying (default in Spring Boot)
// 4. JDK Dynamic Proxy: used when target implements an interface

// Why @Transactional fails on self-invocation:
@Service
public class OrderService {
    
    @Transactional
    public void processOrder(Order order) {
        saveOrder(order);
        sendNotification(order); // ❌ This does NOT go through the proxy!
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotification(Order order) {
        // This @Transactional is IGNORED because it's called internally
        // (this.sendNotification() bypasses the CGLIB proxy)
    }
}

// Fix: Inject self-reference or use ApplicationContext
@Service
public class OrderService {
    @Lazy @Autowired private OrderService self; // Self-injection
    
    @Transactional
    public void processOrder(Order order) {
        saveOrder(order);
        self.sendNotification(order); // ✅ Goes through proxy
    }
}

// Custom Aspect — Performance Logging
@Aspect
@Component
@Order(1) // Lower = higher priority
public class PerformanceLoggingAspect {
    
    private static final Logger log = LoggerFactory.getLogger(PerformanceLoggingAspect.class);
    
    // Pointcut: all public methods in service layer
    @Around("execution(public * com.example..service..*(..))")
    public Object logPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long start = System.nanoTime();
        
        try {
            Object result = joinPoint.proceed();
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            
            if (duration > 500) { // Log slow methods
                log.warn("SLOW METHOD: {} took {}ms", methodName, duration);
            } else {
                log.debug("{} completed in {}ms", methodName, duration);
            }
            
            return result;
        } catch (Exception e) {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start);
            log.error("{} failed after {}ms: {}", methodName, duration, e.getMessage());
            throw e;
        }
    }
}

// Custom Annotation + Aspect — Rate Limiting
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    int maxRequests() default 100;
    int windowSeconds() default 60;
}

@Aspect
@Component
public class RateLimitAspect {
    
    private final ConcurrentHashMap<String, RateLimiter> limiters = new ConcurrentHashMap<>();
    
    @Around("@annotation(rateLimit)")
    public Object enforceRateLimit(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = joinPoint.getSignature().toShortString();
        
        RateLimiter limiter = limiters.computeIfAbsent(key, 
            k -> RateLimiter.of(k, RateLimiterConfig.custom()
                .limitForPeriod(rateLimit.maxRequests())
                .limitRefreshPeriod(Duration.ofSeconds(rateLimit.windowSeconds()))
                .build()));
        
        if (!limiter.acquirePermission()) {
            throw new RateLimitExceededException("Rate limit exceeded for " + key);
        }
        
        return joinPoint.proceed();
    }
}

// Usage:
@RestController
public class ApiController {
    
    @RateLimit(maxRequests = 10, windowSeconds = 60)
    @GetMapping("/api/expensive")
    public Response expensiveOperation() { ... }
}
```

---

## 16.6 Spring Profiles & Externalized Configuration

### The Deep Dive & Solution

```yaml
# application.yml (common config for all profiles)
spring:
  application:
    name: order-service
  jpa:
    open-in-view: false  # Always disable in production
    
server:
  port: 8080

---
# application-dev.yml
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:h2:mem:devdb
    driver-class-name: org.h2.Driver
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true

logging:
  level:
    com.example: DEBUG
    org.hibernate.SQL: DEBUG

---
# application-production.yml
spring:
  config:
    activate:
      on-profile: production
  datasource:
    url: jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
  jpa:
    show-sql: false
    hibernate:
      ddl-auto: validate  # Never auto-create in production
    properties:
      hibernate:
        jdbc.batch_size: 50
        order_inserts: true

logging:
  level:
    root: WARN
    com.example: INFO
```

```java
// Conditional beans based on profile:
@Configuration
public class CacheConfig {
    
    @Bean
    @Profile("dev")
    public CacheManager devCacheManager() {
        return new ConcurrentMapCacheManager("orders", "users"); // In-memory for dev
    }
    
    @Bean
    @Profile("production")
    public CacheManager productionCacheManager(RedisConnectionFactory factory) {
        return RedisCacheManager.builder(factory)
            .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)))
            .build();
    }
}

// Configuration properties (type-safe):
@ConfigurationProperties(prefix = "app.order")
@Validated
public record OrderProperties(
    @NotNull Duration processingTimeout,
    @Min(1) @Max(100) int batchSize,
    @NotBlank String notificationTopic,
    RetryProperties retry
) {
    public record RetryProperties(
        @Min(0) int maxAttempts,
        Duration backoff
    ) {}
}

// application.yml:
// app:
//   order:
//     processing-timeout: 30s
//     batch-size: 50
//     notification-topic: order-events
//     retry:
//       max-attempts: 3
//       backoff: 2s
```

---

## 16.7 Spring Actuator — Production Monitoring

### The Deep Dive & Solution

```yaml
# application-production.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus,loggers
      base-path: /internal/actuator  # Non-standard path (security by obscurity)
  endpoint:
    health:
      show-details: when-authorized  # Only show details to authenticated users
      show-components: when-authorized
    loggers:
      enabled: true  # Dynamic log level changes
  health:
    db:
      enabled: true
    redis:
      enabled: true
    diskspace:
      enabled: true
  metrics:
    export:
      prometheus:
        enabled: true
    tags:
      application: ${spring.application.name}
      environment: ${spring.profiles.active}
```

```java
// Custom Health Indicator:
@Component
public class ExternalApiHealthIndicator implements HealthIndicator {
    
    private final WebClient webClient;
    
    @Override
    public Health health() {
        try {
            String status = webClient.get()
                .uri("/health")
                .retrieve()
                .bodyToMono(String.class)
                .block(Duration.ofSeconds(5));
            
            return Health.up()
                .withDetail("externalApi", "reachable")
                .withDetail("response", status)
                .build();
        } catch (Exception e) {
            return Health.down()
                .withDetail("externalApi", "unreachable")
                .withDetail("error", e.getMessage())
                .build();
        }
    }
}

// Custom Business Metrics:
@Service
public class OrderService {
    
    private final Counter orderCounter;
    private final Timer orderProcessingTimer;
    private final DistributionSummary orderAmountSummary;
    
    public OrderService(MeterRegistry meterRegistry) {
        this.orderCounter = Counter.builder("orders.created.total")
            .description("Total orders created")
            .tag("service", "order-service")
            .register(meterRegistry);
            
        this.orderProcessingTimer = Timer.builder("orders.processing.duration")
            .description("Order processing time")
            .publishPercentiles(0.5, 0.95, 0.99)
            .register(meterRegistry);
            
        this.orderAmountSummary = DistributionSummary.builder("orders.amount")
            .description("Order amounts")
            .baseUnit("USD")
            .register(meterRegistry);
    }
    
    public Order createOrder(CreateOrderRequest request) {
        return orderProcessingTimer.record(() -> {
            Order order = processOrder(request);
            orderCounter.increment();
            orderAmountSummary.record(order.getAmount().doubleValue());
            return order;
        });
    }
}
```

---

## 16.8 Spring Events — Decoupled Communication

### The Deep Dive & Solution

```java
// Spring Events provide in-process pub/sub — decouple components within a service

// Define event:
public record OrderCreatedEvent(
    Long orderId,
    Long customerId,
    BigDecimal amount,
    LocalDateTime timestamp
) {}

// Publish event:
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = orderRepository.save(toEntity(request));
        
        // Publish event — listeners run SYNCHRONOUSLY by default (same thread, same TX)
        eventPublisher.publishEvent(new OrderCreatedEvent(
            order.getId(), order.getCustomerId(), order.getAmount(), LocalDateTime.now()
        ));
        
        return order;
    }
}

// Listen to events:
@Component
public class NotificationListener {
    
    // Synchronous — runs in same thread and transaction
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        sendConfirmationEmail(event.customerId(), event.orderId());
    }
}

@Component
public class InventoryListener {
    
    // Async — runs in separate thread (requires @EnableAsync)
    @Async
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        reserveInventory(event.orderId());
    }
}

@Component
public class AnalyticsListener {
    
    // Transactional — runs AFTER the transaction commits successfully
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onOrderCreated(OrderCreatedEvent event) {
        // Safe to send Kafka message here — only runs if order was actually saved
        kafkaTemplate.send("order-events", event);
    }
}
```

---

## 16.9 Interview Quick Reference — Spring Ecosystem

### Top Questions and One-Line Answers

| Question | Answer |
|----------|--------|
| Spring Data JPA N+1 problem? | Use `JOIN FETCH` in JPQL, `@EntityGraph`, or `@BatchSize`. |
| Specifications vs JPQL? | Specifications for dynamic, composable queries; JPQL for static queries. |
| Spring WebFlux vs MVC? | WebFlux: non-blocking I/O, Netty, small thread pool. MVC: thread-per-request, Tomcat. |
| Mono vs Flux? | Mono: 0-1 element. Flux: 0-N elements. Both are lazy and subscribe-on-demand. |
| Spring Cloud Gateway vs Zuul? | Gateway: reactive (WebFlux), Zuul 1: blocking. Gateway is the modern choice. |
| @Transactional self-invocation? | Self-calls bypass CGLIB proxy. Fix: inject self, use `ObjectProvider`, or refactor. |
| CGLIB vs JDK Proxy? | CGLIB: subclass-based (works on classes). JDK: interface-based. Spring Boot defaults to CGLIB. |
| @EventListener vs @TransactionalEventListener? | `@EventListener`: synchronous in same TX. `@TransactionalEventListener`: runs after TX commit. |
| Spring Batch chunk processing? | Read N items → process N items → write N items in one transaction. |
| What does @RefreshScope do? | Recreates the bean when `/actuator/refresh` is called (for dynamic config). |
| How does Spring Boot auto-configuration work? | Reads `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`, applies `@Conditional*` annotations. |
| What is the bean lifecycle? | Constructor → `@PostConstruct` → `InitializingBean.afterPropertiesSet()` → init-method → ... → `@PreDestroy` → `DisposableBean.destroy()` → destroy-method |

### Keywords to Use in Interviews

```
Spring Data: Specification, Projection, EntityGraph, Auditing, Pageable, Slice vs Page,
             Query Method Resolution, @Modifying, Batch Insert, JPA Criteria API

WebFlux: Reactor, Mono, Flux, Backpressure, WebClient, RouterFunction, HandlerFunction,
         Non-blocking I/O, Event Loop, Netty

Spring Cloud: Service Discovery, Config Server, API Gateway, Circuit Breaker,
              Load Balancing, Distributed Tracing, Spring Cloud Bus

Spring AOP: CGLIB Proxy, JDK Dynamic Proxy, Pointcut, Advice, JoinPoint,
            @Around, @Before, @After, @AfterReturning, @AfterThrowing
```

