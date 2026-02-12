# Section 4: System Design (SDE-2/Lead Level)

---

## 4.1 Scalability Concepts: Vertical vs. Horizontal Scaling, CAP Theorem, Load Balancers

---

### The "Why" & The Problem

System design is the cornerstone of a Lead Software Engineer interview. You will be given a vague problem ("Design a URL shortener", "Design a notification system") and expected to produce a scalable, fault-tolerant architecture in 45 minutes. The interviewer is not looking for a perfect solution — they are evaluating your **thought process**: how you handle ambiguity, how you make trade-offs, and how deeply you understand distributed systems.

A company pays you to know this because Leads are responsible for **architectural decisions that are expensive to reverse**. Choosing the wrong database, the wrong communication protocol, or the wrong scaling strategy can cost months of rework and millions of dollars. These decisions are made during system design.

---

### Interviewer Expectations

- **Scaling**: Know when vertical scaling is sufficient and when you need horizontal scaling. Know the implications of each (state management, data consistency, load balancing).
- **CAP Theorem**: Explain it simply and correctly. Know that it's about choosing between Consistency and Availability during a **network partition**, not in normal operation. Know real-world examples (CP: banking systems, AP: social media feeds).
- **Load Balancers**: Know L4 (TCP/UDP) vs. L7 (HTTP) load balancers. Know algorithms (Round Robin, Least Connections, Consistent Hashing, IP Hash). Know where they sit in the architecture.
- **Keywords**: "Horizontal scaling with stateless services", "CAP theorem partition tolerance", "consistent hashing for cache distribution", "L7 load balancer for path-based routing", "auto-scaling groups", "health checks", "sticky sessions".

---

### The Deep Dive & Solution

#### Vertical vs. Horizontal Scaling

**Vertical Scaling (Scale Up)**: Add more CPU, RAM, and disk to a single machine.
- **Pros**: Simple. No code changes. No distributed systems complexity. Your existing monolith just runs faster.
- **Cons**: Hardware limits (you can't buy a 10,000-core machine). Single point of failure. Diminishing returns (going from 8 to 16 cores doubles cost but rarely doubles throughput due to contention). Downtime during upgrades.
- **When to use**: Databases (scaling out databases is very hard — scale up first). Small-to-medium applications. When time-to-market matters more than scale.

**Horizontal Scaling (Scale Out)**: Add more machines running the same software.
- **Pros**: Theoretically unlimited scale. No single point of failure (if one instance dies, others handle the load). Cost-efficient (many small commodity machines > one giant machine).
- **Cons**: Requires stateless services (or externalized state). Introduces distributed systems problems (consistency, network partitions, coordination). Need load balancers, service discovery, health checks.
- **When to use**: Application servers (stateless APIs). When you need fault tolerance. When you need to scale beyond a single machine's capacity.

**Statelessness is the prerequisite for horizontal scaling**:
```java
// BAD: Stateful — session data stored in server memory
@RestController
public class CartController {
    // If the user's next request goes to a different instance, the cart is lost!
    private Map<String, Cart> sessionCarts = new HashMap<>();
    
    @PostMapping("/cart/add")
    public Cart addToCart(@RequestHeader("Session-Id") String sessionId, @RequestBody Item item) {
        Cart cart = sessionCarts.computeIfAbsent(sessionId, k -> new Cart());
        cart.addItem(item);
        return cart;
    }
}

// GOOD: Stateless — session data externalized to Redis
@RestController
public class CartController {
    @Autowired private RedisTemplate<String, Cart> redisTemplate;
    
    @PostMapping("/cart/add")
    public Cart addToCart(@RequestHeader("Session-Id") String sessionId, @RequestBody Item item) {
        String key = "cart:" + sessionId;
        Cart cart = redisTemplate.opsForValue().get(key);
        if (cart == null) cart = new Cart();
        cart.addItem(item);
        redisTemplate.opsForValue().set(key, cart, Duration.ofHours(24));
        return cart;
    }
}
// Now ANY instance can serve ANY request — perfect for horizontal scaling
```

#### CAP Theorem — Explained Simply

The CAP theorem states that a distributed data store can provide at most **two out of three** guarantees simultaneously:

- **C (Consistency)**: Every read receives the most recent write. All nodes see the same data at the same time.
- **A (Availability)**: Every request receives a response (not an error), regardless of the state of any individual node.
- **P (Partition Tolerance)**: The system continues to operate despite network partitions (messages between nodes being dropped or delayed).

**The critical insight**: In a distributed system, network partitions **will happen** (cables get cut, switches fail, data centers lose connectivity). So P is not optional. You MUST have partition tolerance. The real choice is between **C and A during a partition**:

```
                          ┌─────────────────────┐
                          │    CAP Theorem       │
                          │                      │
                          │    Pick TWO:         │
                          └─────────────────────┘
                                   │
                    ┌──────────────┼──────────────┐
                    │              │               │
                ┌───┴───┐    ┌────┴────┐    ┌─────┴────┐
                │  CA    │    │   CP    │    │    AP    │
                │(theory │    │         │    │          │
                │ only)  │    │Consistent│    │Available │
                │        │    │ when     │    │ when     │
                │Single  │    │partition │    │partition │
                │node    │    │= reject │    │= serve   │
                │RDBMS   │    │ requests │    │ stale    │
                └────────┘    └─────────┘    └──────────┘
```

**CP (Consistent + Partition Tolerant)**: During a partition, the system **rejects requests** (becomes unavailable) rather than risk returning stale data. 
- **Examples**: Banking systems, inventory management (you cannot allow overselling), distributed locks (ZooKeeper, etcd), HBase, MongoDB (with majority read concern).
- **Behavior during partition**: "Sorry, I can't process your request right now because I'm not sure if my data is up-to-date."

**AP (Available + Partition Tolerant)**: During a partition, the system **serves requests** with potentially stale data rather than refusing to respond.
- **Examples**: Social media feeds (showing a post from 30 seconds ago is fine), DNS, Cassandra, DynamoDB (with eventual consistency reads), shopping cart (Amazon's Dynamo paper).
- **Behavior during partition**: "Here's your data, but it might be a few seconds old."

**Real-world example**:
Imagine a distributed database with nodes in US-East and EU-West. A network partition cuts connectivity between them.

- **CP behavior**: A write to US-East will not be acknowledged until EU-West confirms it received the write. Since EU-West is unreachable, the write is rejected. System is consistent but unavailable.
- **AP behavior**: A write to US-East is acknowledged immediately (stored locally). EU-West continues serving reads with its last known data. When the partition heals, the systems reconcile. System is available but temporarily inconsistent.

**PACELC extension** (more nuanced than CAP):
Even when there is **no** partition (normal operation), there is a trade-off between **Latency** and **Consistency**. PACELC says: "If Partition, choose A or C. Else, choose L (Latency) or C (Consistency)."
- **PA/EL**: Favor availability during partition, favor low latency normally. (Cassandra, DynamoDB)
- **PC/EC**: Favor consistency during partition, favor consistency normally. (Traditional RDBMS, VoltDB)
- **PA/EC**: Favor availability during partition, but favor consistency normally. (MongoDB default)

#### Load Balancers — L4 vs. L7

A load balancer distributes incoming traffic across multiple backend servers (instances). It provides:
1. **Distribution**: Spread load evenly to prevent any single instance from being overwhelmed.
2. **Health checking**: Detect unhealthy instances and stop sending traffic to them.
3. **SSL termination**: Handle HTTPS encryption/decryption, offloading this work from application servers.
4. **Session affinity** (if needed): Route requests from the same client to the same backend.

**L4 Load Balancer (Transport Layer — TCP/UDP)**:
Operates at the TCP/UDP level. It sees IP addresses and port numbers but does NOT inspect the HTTP content (no URL, no headers, no cookies). It forwards the entire TCP connection to a backend.

- **How it works**: Client opens a TCP connection to the load balancer's IP. The LB selects a backend (using Round Robin, Least Connections, or IP Hash) and forwards all packets for that connection to the chosen backend.
- **Performance**: Very fast (no HTTP parsing). Low latency. High throughput.
- **Use case**: TCP services (databases, gRPC, raw TCP), initial traffic distribution, when you need maximum performance.
- **Examples**: AWS NLB (Network Load Balancer), HAProxy in TCP mode, nginx stream module.

**L7 Load Balancer (Application Layer — HTTP/HTTPS)**:
Operates at the HTTP level. It can inspect URLs, headers, cookies, and request bodies. This enables **content-based routing**.

- **How it works**: Client sends an HTTP request to the LB. The LB parses the request, applies routing rules (based on URL path, host header, cookies), and forwards the request to the appropriate backend.
- **Features**:
  - **Path-based routing**: `/api/orders/*` → Order Service, `/api/users/*` → User Service.
  - **Host-based routing**: `orders.myapp.com` → Order Service, `users.myapp.com` → User Service.
  - **Header-based routing**: Route `X-API-Version: v2` to the v2 backend.
  - **SSL termination**: Decrypts HTTPS at the LB, forwards HTTP to backends.
  - **Request modification**: Add/remove headers, rewrite URLs.
  - **Rate limiting and WAF**: Apply rate limits and web application firewall rules.
- **Performance**: Slower than L4 (must parse HTTP). Higher memory usage (must buffer HTTP requests).
- **Use case**: HTTP/HTTPS APIs, microservices routing, canary deployments, A/B testing.
- **Examples**: AWS ALB (Application Load Balancer), nginx, Envoy, Traefik, Kong.

```
                    Internet
                       │
                ┌──────┴──────┐
                │  L4 (NLB)    │ ← Raw TCP/UDP forwarding, ultra-fast
                │  or L7 (ALB) │ ← HTTP-aware, content-based routing
                └──────┬──────┘
                       │
        ┌──────────────┼──────────────┐
        │              │               │
   ┌────┴────┐   ┌────┴────┐   ┌─────┴────┐
   │Instance 1│   │Instance 2│   │Instance 3│
   └─────────┘   └─────────┘   └──────────┘
```

**Load Balancing Algorithms**:

| Algorithm | How it works | Best for |
|-----------|-------------|----------|
| **Round Robin** | Send to each server in turn (1, 2, 3, 1, 2, 3...) | Uniform servers, similar request costs |
| **Weighted Round Robin** | Same but servers have weights (server with weight 3 gets 3x traffic) | Servers with different capacities |
| **Least Connections** | Send to the server with the fewest active connections | Varying request durations |
| **IP Hash** | Hash the client IP to determine the server | Simple session affinity without cookies |
| **Consistent Hashing** | Map servers and requests to a hash ring | Cache servers, minimizes cache invalidation on server add/remove |
| **Random** | Pick a random server | Surprisingly effective and very fast |

**Consistent Hashing** — critical for distributed caches:

When you have N cache servers and you use simple modulo routing (`hash(key) % N`), adding or removing a server causes almost all keys to remap (cache invalidation tsunami). Consistent hashing maps both servers and keys to positions on a circular hash ring. Adding a server only affects the keys between it and its predecessor on the ring (~1/N of total keys).

```
         Hash Ring (0 to 2^32)
              ┌────────┐
           ┌──┤ key1   ├──┐
          │   └────────┘   │
     ┌────┴───┐       ┌───┴────┐
     │Server A│       │Server B│
     └────┬───┘       └───┬────┘
          │               │
          │  ┌────────┐   │
          └──┤ key2   ├───┘
             └────────┘
          ┌────────┐
          │Server C│  ← Adding Server C only moves keys 
          └────────┘    between Server B and Server C
```

Each key is assigned to the first server found clockwise on the ring from the key's hash position. When Server C is added, only keys that were between Server B and Server C's positions move to Server C. All other keys remain unchanged.

**Virtual nodes**: To ensure even distribution, each physical server is mapped to multiple virtual nodes on the ring (e.g., Server A → A1, A2, A3, A4 at different ring positions). This prevents the uneven distribution that can occur with only 3-4 physical servers on a very large ring.

---

## 4.2 Design Patterns: API Gateway, BFF, Strangler Fig Pattern

---

### The "Why" & The Problem

As you decompose a monolith into microservices, new architectural challenges emerge:
- **Client complexity**: A mobile app making direct calls to 15 different microservices needs to know the address, API contract, and authentication mechanism of each. If you change a service's URL, you need to update the mobile app (requires an app store release).
- **Protocol mismatch**: Internal services use gRPC for performance, but the mobile app speaks HTTP/REST. Internal services return 50 fields, but the mobile app only needs 5.
- **Cross-cutting concerns**: Authentication, rate limiting, logging, CORS — each service implementing these independently leads to inconsistency and duplication.
- **Migration risk**: You have a 10-year-old monolith with 2 million lines of code. Rewriting it from scratch is a multi-year project with a high failure rate. You need a strategy to migrate incrementally.

---

### Interviewer Expectations

- **API Gateway**: Know what it does (routing, auth, rate limiting, protocol translation), where it sits, and popular implementations (Kong, Spring Cloud Gateway, AWS API Gateway, Envoy).
- **BFF (Backend for Frontend)**: Know that different clients (web, mobile, TV) have different data needs. A single API is often a compromise that serves no one well. BFF is a per-client API layer.
- **Strangler Fig Pattern**: Explain how to incrementally migrate from a monolith to microservices without a "big bang" rewrite. Know the three steps: Transform, Coexist, Eliminate.
- **Keywords**: "API composition", "protocol translation", "request aggregation", "edge service", "reverse proxy", "strangler façade", "anti-corruption layer", "incremental migration", "feature flag", "dark launching".

---

### The Deep Dive & Solution

#### API Gateway

An API Gateway is a **reverse proxy** that sits between external clients and your internal microservices. It is the single entry point for all client requests.

```
┌───────────────────────────────────────────────────────────────┐
│                         Clients                                │
│  ┌──────────┐  ┌──────────┐  ┌──────────────┐               │
│  │  Web SPA  │  │ Mobile   │  │ Third-party  │               │
│  └────┬─────┘  └────┬─────┘  └──────┬───────┘               │
│       │              │                │                        │
└───────┼──────────────┼────────────────┼────────────────────────┘
        │              │                │
        └──────────────┼────────────────┘
                       │
                ┌──────┴──────┐
                │ API Gateway  │
                │              │
                │ • Routing    │
                │ • Auth       │
                │ • Rate Limit │
                │ • SSL Term   │
                │ • Logging    │
                │ • CORS       │
                └──────┬──────┘
                       │
        ┌──────────────┼───────────────┐
        │              │                │
  ┌─────┴─────┐  ┌────┴─────┐  ┌──────┴──────┐
  │ Order Svc  │  │ User Svc │  │ Payment Svc  │
  └───────────┘  └──────────┘  └─────────────┘
```

**API Gateway Responsibilities**:

1. **Request Routing**: Route `/api/orders/*` to Order Service, `/api/users/*` to User Service. This decouples the client from the internal service topology — services can be split, merged, or relocated without client changes.

2. **Authentication & Authorization**: Validate JWT tokens at the gateway. Only authenticated requests reach internal services. Internal services trust the gateway and can focus on business logic.

3. **Rate Limiting**: Protect internal services from abuse. Different rate limits per client (free tier: 100 req/min, paid tier: 10,000 req/min).

4. **Protocol Translation**: Accept REST from clients, translate to gRPC for internal services.

5. **Request Aggregation**: A single client request that needs data from 3 services can be composed at the gateway level, reducing the number of client-to-server round trips (especially important for mobile with high latency).

6. **Load Shedding & Circuit Breaking**: The gateway can reject requests when backend services are overloaded, returning 503 early instead of letting requests queue up.

7. **Observability**: Centralized logging, distributed tracing injection (add `X-Request-Id` header), metrics collection.

**Spring Cloud Gateway implementation**:
```java
// Gateway configuration
@Configuration
public class GatewayConfig {
    
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route to Order Service
            .route("order-service", r -> r
                .path("/api/orders/**")
                .filters(f -> f
                    .stripPrefix(1)                    // Remove /api prefix
                    .addRequestHeader("X-Gateway", "true")
                    .circuitBreaker(c -> c
                        .setName("orderServiceCB")
                        .setFallbackUri("forward:/fallback/orders"))
                    .requestRateLimiter(l -> l
                        .setRateLimiter(redisRateLimiter())
                        .setKeyResolver(userKeyResolver())))
                .uri("lb://order-service"))            // Load-balanced URI
            
            // Route to User Service
            .route("user-service", r -> r
                .path("/api/users/**")
                .filters(f -> f
                    .stripPrefix(1)
                    .retry(config -> config
                        .setRetries(3)
                        .setStatuses(HttpStatus.SERVICE_UNAVAILABLE)))
                .uri("lb://user-service"))
            
            .build();
    }
    
    @Bean
    public KeyResolver userKeyResolver() {
        // Rate limit per user (from JWT claim) or per IP
        return exchange -> {
            String userId = exchange.getRequest().getHeaders().getFirst("X-User-Id");
            return Mono.just(userId != null ? userId : 
                exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        };
    }
}
```

```yaml
# application.yml for Spring Cloud Gateway
spring:
  cloud:
    gateway:
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins: "https://myapp.com"
            allowedMethods: GET, POST, PUT, DELETE
            allowedHeaders: "*"
      default-filters:
        - DedupeResponseHeader=Access-Control-Allow-Origin
        - name: RequestRateLimiter
          args:
            redis-rate-limiter.replenishRate: 100
            redis-rate-limiter.burstCapacity: 200
```

**API Gateway anti-patterns**:
- **Business logic in the gateway**: The gateway should only handle cross-cutting concerns. If you start putting order validation logic in the gateway, it becomes a monolith.
- **Single gateway for everything**: One gateway for web, mobile, and internal services becomes a bottleneck and a deployment risk (change for mobile breaks web).

#### BFF (Backend for Frontend)

The BFF pattern creates a **dedicated backend for each frontend type**. Instead of one API serving web, mobile, and TV, each client gets its own API layer that is optimized for its specific needs.

```
┌──────────────────────────────────────────────────────────────┐
│  ┌──────────┐     ┌──────────┐     ┌──────────────────┐     │
│  │  Web SPA  │     │ Mobile   │     │  Smart TV App    │     │
│  └────┬─────┘     └────┬─────┘     └──────┬───────────┘     │
│       │                │                    │                  │
│  ┌────┴─────┐     ┌────┴─────┐     ┌──────┴───────────┐     │
│  │ Web BFF   │     │Mobile BFF│     │   TV BFF          │     │
│  │           │     │          │     │                    │     │
│  │ GraphQL   │     │ REST     │     │ Simple REST       │     │
│  │ Full data │     │ Minimal  │     │ Large images      │     │
│  │           │     │ payload  │     │ Low resolution    │     │
│  └────┬─────┘     └────┬─────┘     └──────┬───────────┘     │
│       │                │                    │                  │
└───────┼────────────────┼────────────────────┼──────────────────┘
        │                │                    │
        └────────────────┼────────────────────┘
                         │
              ┌──────────┴──────────┐
              │ Shared Microservices │
              │ (Order, User, etc.) │
              └─────────────────────┘
```

**Why different clients need different APIs**:

- **Web SPA**: Runs on a fast network, has a large screen. Can handle complex data structures, multiple API calls, and large payloads. May use GraphQL for flexible querying.
- **Mobile App**: Runs on slow/unreliable cellular networks, limited battery. Needs minimal payloads (no unnecessary fields), fewer round trips (aggregated API calls), and optimized images.
- **Smart TV**: Very limited processing power, constrained navigation (remote control). Needs pre-computed layouts, large image URLs, and extremely simple data structures.

**Without BFF**: The shared API becomes a "lowest common denominator" — it either sends too much data to mobile (wasting bandwidth) or too little data to web (requiring extra calls). Changes for one client risk breaking another.

**With BFF**: Each BFF can:
- **Aggregate** calls to multiple microservices into a single response tailored for that client.
- **Transform** data (rename fields, nest/flatten structures, filter unused fields).
- **Cache** differently (mobile BFF caches aggressively, web BFF caches less).
- **Evolve independently** (mobile BFF team deploys without affecting web).

```java
// Mobile BFF — optimized for minimal payload
@RestController
@RequestMapping("/mobile/api")
public class MobileOrderController {
    
    @Autowired private OrderServiceClient orderClient;
    @Autowired private UserServiceClient userClient;
    
    @GetMapping("/dashboard")
    public MobileDashboardResponse getDashboard(@RequestHeader("X-User-Id") String userId) {
        // Single call that aggregates data from multiple services
        CompletableFuture<List<Order>> ordersFuture = 
            CompletableFuture.supplyAsync(() -> orderClient.getRecentOrders(userId, 5));
        CompletableFuture<UserProfile> profileFuture = 
            CompletableFuture.supplyAsync(() -> userClient.getProfile(userId));
        
        return CompletableFuture.allOf(ordersFuture, profileFuture)
            .thenApply(v -> MobileDashboardResponse.builder()
                .userName(profileFuture.join().getFirstName())  // Only first name for mobile
                .recentOrders(ordersFuture.join().stream()
                    .map(o -> MobileOrderSummary.builder()
                        .id(o.getId())
                        .status(o.getStatus())
                        .total(o.getTotalAmount())
                        // No item details, no addresses — mobile doesn't show these on dashboard
                        .build())
                    .collect(Collectors.toList()))
                .build())
            .join();
    }
}

// Web BFF — richer data for large screens
@RestController
@RequestMapping("/web/api")
public class WebOrderController {
    
    @GetMapping("/dashboard")
    public WebDashboardResponse getDashboard(@RequestHeader("X-User-Id") String userId) {
        // Web gets more data: full order details, recommendations, analytics
        // Multiple parallel calls because web has fast network
        CompletableFuture<List<Order>> ordersFuture = ...;
        CompletableFuture<UserProfile> profileFuture = ...;
        CompletableFuture<List<Recommendation>> recsFuture = ...;
        CompletableFuture<Analytics> analyticsFuture = ...;
        
        return CompletableFuture.allOf(ordersFuture, profileFuture, recsFuture, analyticsFuture)
            .thenApply(v -> WebDashboardResponse.builder()
                .fullName(profileFuture.join().getFullName())
                .email(profileFuture.join().getEmail())
                .recentOrders(ordersFuture.join())  // Full order objects
                .recommendations(recsFuture.join())
                .orderAnalytics(analyticsFuture.join())
                .build())
            .join();
    }
}
```

#### Strangler Fig Pattern — Migrating from Monolith to Microservices

Named after the strangler fig tree that gradually grows around a host tree, eventually replacing it entirely. This pattern enables incremental migration from a monolith to microservices without a risky "big bang" rewrite.

**The three phases**:

```
Phase 1: TRANSFORM             Phase 2: COEXIST              Phase 3: ELIMINATE
┌─────────────────┐           ┌─────────────────┐           ┌─────────────────┐
│     Façade       │           │     Façade       │           │     Façade       │
│  (API Gateway)   │           │  (API Gateway)   │           │  (API Gateway)   │
└───────┬─────────┘           └───────┬─────────┘           └───────┬─────────┘
        │                             │                             │
   ┌────┴────┐                   ┌────┴────┐                       │
   │         │                   │         │                       │
   ▼         ▼                   ▼         ▼                       ▼
┌──────┐ ┌──────┐           ┌──────┐ ┌──────┐              ┌──────────────┐
│Mono- │ │New   │           │Mono- │ │Micro-│              │ Microservices│
│lith  │ │Micro │           │lith  │ │srvcs │              │ (Complete)   │
│(all) │ │(Order│           │(shrn)│ │(grow)│              │              │
│      │ │ only)│           │      │ │      │              │              │
└──────┘ └──────┘           └──────┘ └──────┘              └──────────────┘
```

**Step-by-step migration**:

**Phase 1: Add a Façade (the Strangler Façade)**
- Place an API Gateway in front of the monolith. All client traffic goes through the gateway.
- Initially, the gateway routes 100% of traffic to the monolith. Nothing changes functionally.
- This establishes the routing layer that will be used to redirect traffic incrementally.

**Phase 2: Extract one capability as a microservice**
- Identify a bounded context in the monolith that is relatively independent (e.g., "Order Management").
- Build the new Order Microservice alongside the monolith.
- Update the API Gateway to route `/api/orders/*` to the new microservice and everything else to the monolith.
- The monolith and the microservice coexist. If the microservice has issues, the gateway can route traffic back to the monolith (safety net).

**Phase 3: Repeat and eliminate**
- Extract the next capability (User Management, Payment, Inventory).
- Each extraction reduces the monolith's responsibility.
- Eventually, the monolith has no responsibilities left and can be decommissioned.

**Implementation with Spring Cloud Gateway**:
```java
@Configuration
public class StranglerFigGateway {
    
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
            // New microservice handles orders
            .route("order-service-new", r -> r
                .path("/api/orders/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://order-microservice:8081"))
            
            // New microservice handles users  
            .route("user-service-new", r -> r
                .path("/api/users/**")
                .filters(f -> f.stripPrefix(1))
                .uri("http://user-microservice:8082"))
            
            // EVERYTHING ELSE still goes to the monolith
            .route("monolith-fallback", r -> r
                .path("/api/**")
                .uri("http://monolith:8080"))
            
            .build();
    }
}
```

**Critical considerations during migration**:

1. **Anti-Corruption Layer (ACL)**: The new microservice should NOT directly depend on the monolith's data model. If the monolith has a `customers` table with 100 columns and weird legacy names, the new User Microservice should have its own clean data model. An ACL translates between the old and new models.

2. **Database migration**: The hardest part. Initially, the microservice and monolith may share the database (pragmatic but temporary). Eventually, the microservice needs its own database. Strategies:
   - **Dual-write**: Write to both databases during migration (risk of inconsistency).
   - **CDC (Change Data Capture)**: Use Debezium to stream changes from the monolith's database to the microservice's database.
   - **Read from new, write to both**: Gradually shift reads to the new database, then shift writes.

3. **Feature flags**: Use feature flags to control traffic routing. `order.service.use-microservice=true` in your configuration. If the microservice has issues, flip the flag to route back to the monolith.

```java
// Feature flag based routing
@Component
public class StranglerRouter {
    
    @Value("${feature.orders.use-microservice:false}")
    private boolean useOrderMicroservice;
    
    public OrderResponse getOrder(String orderId) {
        if (useOrderMicroservice) {
            return orderMicroserviceClient.getOrder(orderId);
        } else {
            return monolithClient.getOrder(orderId);  // Fallback to monolith
        }
    }
}
```

4. **Shadow traffic / Dark launching**: Before switching real traffic, send a copy of production requests to the new microservice (shadow traffic). Compare the responses from the monolith and the microservice. If they match, the microservice is ready. If they differ, investigate.

5. **Canary releases**: Route 1% of traffic to the new microservice, monitor error rates and latency. If all looks good, increase to 5%, 10%, 50%, 100%. Automated rollback if error rates exceed a threshold.

```yaml
# Canary routing with weights in Spring Cloud Gateway
spring:
  cloud:
    gateway:
      routes:
        - id: order-canary
          uri: http://order-microservice:8081
          predicates:
            - Path=/api/orders/**
            - Weight=order-group, 10     # 10% of traffic
        - id: order-monolith
          uri: http://monolith:8080
          predicates:
            - Path=/api/orders/**
            - Weight=order-group, 90     # 90% of traffic
```

