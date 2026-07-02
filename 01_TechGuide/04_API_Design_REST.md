# Section 4: API Design & REST Best Practices

> **You are here**: SDE1–SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [03_Design_Patterns_SOLID_CleanArch.md](03_Design_Patterns_SOLID_CleanArch.md) | **Next**: [05_Database_Performance_Tuning.md](05_Database_Performance_Tuning.md)

---

## 4.1 RESTful API Design Principles

---

### The "Why" & The Problem

APIs are the contracts between your services and their consumers (frontend apps, mobile apps, third-party integrations, other microservices). A badly designed API:
- **Causes constant breaking changes**: Every release breaks the frontend because the API changes unpredictably.
- **Creates confusion**: Developers don't know what endpoints do from their names. `POST /doAction` tells you nothing.
- **Is hard to evolve**: Adding new features requires rewriting existing endpoints, which breaks existing consumers.

A well-designed API is **intuitive** (developers understand it without reading docs), **consistent** (follows predictable patterns), and **evolvable** (new features don't break existing consumers).

A company pays you to know this because **APIs are products**. Internal APIs serve your team, external APIs serve your customers. Bad APIs slow down every team that depends on them.

---

### Interviewer Expectations

- **Resource-oriented design**: Think in terms of resources (nouns), not actions (verbs). `GET /orders/123` not `GET /getOrderById?id=123`.
- **HTTP semantics**: Use HTTP methods (GET, POST, PUT, PATCH, DELETE) and status codes (200, 201, 204, 400, 404, 409, 422, 500) correctly.
- **Idempotency**: Understand which operations are idempotent and why this matters for reliability.
- **Error handling**: Use RFC 7807 Problem Details format. Don't just return `{"error": "something went wrong"}`.
- **Keywords**: "Resource-oriented", "HATEOAS", "idempotent", "RFC 7807", "API versioning", "cursor-based pagination", "content negotiation", "OpenAPI specification".

---

### The Deep Dive & Solution

#### Resource-Oriented Design

**The core principle**: Your API exposes **resources** (nouns), not **actions** (verbs). HTTP methods provide the verbs.

| Bad (RPC-style) | Good (REST) | HTTP Method |
|-----------------|-------------|-------------|
| `GET /getOrders` | `GET /orders` | GET (list) |
| `GET /getOrderById?id=123` | `GET /orders/123` | GET (single) |
| `POST /createOrder` | `POST /orders` | POST (create) |
| `POST /updateOrder` | `PUT /orders/123` | PUT (replace) |
| `POST /cancelOrder?id=123` | `DELETE /orders/123` | DELETE |
| `POST /partialUpdateOrder` | `PATCH /orders/123` | PATCH (partial update) |

**Nested resources** for relationships:
```
GET    /orders/123/items           # Items in order 123
POST   /orders/123/items           # Add item to order 123
GET    /orders/123/items/456       # Specific item in order 123
DELETE /orders/123/items/456       # Remove item from order 123

GET    /customers/789/orders       # Orders for customer 789
```

**Rule of thumb**: Nest resources at most 2 levels deep. Beyond that, provide a top-level resource with a filter:
```
# Instead of: /customers/789/orders/123/items/456/reviews
# Use:
GET /reviews?orderId=123&itemId=456
```

#### HTTP Methods & Idempotency

| Method | Purpose | Idempotent? | Safe? | Request Body? | Response Body? |
|--------|---------|-------------|-------|---------------|----------------|
| **GET** | Retrieve resource(s) | ✅ Yes | ✅ Yes | No | Yes |
| **POST** | Create a new resource | ❌ No | ❌ No | Yes | Yes (created resource) |
| **PUT** | Replace entire resource | ✅ Yes | ❌ No | Yes | Yes (updated resource) |
| **PATCH** | Partial update | ❌ No* | ❌ No | Yes (partial) | Yes (updated resource) |
| **DELETE** | Remove resource | ✅ Yes | ❌ No | No | No (or confirmation) |

*PATCH can be idempotent if implemented correctly (e.g., JSON Merge Patch), but is not guaranteed.

**Why idempotency matters**: In distributed systems, network failures cause retries. If a `POST /orders` request times out, the client retries. Without idempotency, you get duplicate orders. With an **idempotency key**, the server recognizes the retry and returns the original response.

```java
/**
 * Idempotent order creation using an idempotency key.
 * The client generates a unique key (UUID) and sends it in the header.
 * If the server has already processed this key, it returns the cached response.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private IdempotencyKeyService idempotencyService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {

        // Check if this key was already processed
        Optional<OrderResponse> cached = idempotencyService.get(idempotencyKey);
        if (cached.isPresent()) {
            return ResponseEntity.ok(cached.get());  // Return cached response — no duplicate order
        }

        // Process the order
        OrderResponse response = orderService.createOrder(request);

        // Cache the response for this idempotency key (TTL: 24 hours)
        idempotencyService.store(idempotencyKey, response, Duration.ofHours(24));

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

#### HTTP Status Codes — Correct Usage

```java
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    // GET /api/v1/orders/123
    // 200 OK — resource found
    // 404 Not Found — resource doesn't exist
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return orderService.findById(id)
                .map(ResponseEntity::ok)                                     // 200
                .orElseThrow(() -> new ResourceNotFoundException("Order", id)); // 404
    }

    // POST /api/v1/orders
    // 201 Created — resource created (include Location header)
    // 400 Bad Request — invalid input
    // 409 Conflict — duplicate resource
    // 422 Unprocessable Entity — valid syntax, invalid semantics
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {
        OrderResponse order = orderService.createOrder(request);
        URI location = URI.create("/api/v1/orders/" + order.getId());
        return ResponseEntity.created(location).body(order);                 // 201
    }

    // PUT /api/v1/orders/123
    // 200 OK — resource updated
    // 404 Not Found — resource doesn't exist
    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderRequest request) {
        OrderResponse order = orderService.updateOrder(id, request);
        return ResponseEntity.ok(order);                                     // 200
    }

    // PATCH /api/v1/orders/123
    // 200 OK — resource partially updated
    @PatchMapping("/{id}")
    public ResponseEntity<OrderResponse> patchOrder(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        OrderResponse order = orderService.patchOrder(id, updates);
        return ResponseEntity.ok(order);                                     // 200
    }

    // DELETE /api/v1/orders/123
    // 204 No Content — resource deleted (no response body)
    // 404 Not Found — resource doesn't exist
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();                           // 204
    }
}
```

**Status Code Cheat Sheet**:

| Code | Meaning | When to Use |
|------|---------|-------------|
| **200** | OK | GET (found), PUT/PATCH (updated) |
| **201** | Created | POST (new resource created). Include `Location` header. |
| **204** | No Content | DELETE (successful, no body to return) |
| **400** | Bad Request | Malformed request (invalid JSON, missing required fields) |
| **401** | Unauthorized | No authentication or invalid credentials |
| **403** | Forbidden | Authenticated but not authorized for this resource |
| **404** | Not Found | Resource doesn't exist |
| **405** | Method Not Allowed | Trying DELETE on a resource that doesn't support it |
| **409** | Conflict | Duplicate resource, optimistic locking conflict |
| **422** | Unprocessable Entity | Valid JSON but business validation failed (e.g., negative quantity) |
| **429** | Too Many Requests | Rate limited. Include `Retry-After` header. |
| **500** | Internal Server Error | Unexpected server error. Never expose stack traces to the client. |
| **502** | Bad Gateway | Upstream service failed |
| **503** | Service Unavailable | Service is down or overloaded. Include `Retry-After`. |
| **504** | Gateway Timeout | Upstream service timed out |

---

## 4.2 Error Handling with RFC 7807 (Problem Details)

---

### The "Why" & The Problem

Most APIs return errors inconsistently:
```json
{"error": "something went wrong"}
{"message": "Not found", "code": 404}
{"errors": [{"field": "email", "msg": "invalid format"}]}
```

Every API uses a different format. The client has to handle each one differently. RFC 7807 defines a **standard error format** that all APIs should follow.

---

### Interviewer Expectations

- **RFC 7807 format**: Know the standard fields: `type`, `title`, `status`, `detail`, `instance`.
- **Consistent error handling**: Global exception handler that maps all exceptions to RFC 7807 format.
- **Keywords**: "Problem Details", "RFC 7807", "global exception handler", "error response contract".

---

### The Deep Dive & Solution

#### RFC 7807 Error Response Format

```json
{
  "type": "https://api.example.com/problems/insufficient-funds",
  "title": "Insufficient Funds",
  "status": 422,
  "detail": "Account balance is $30.00, but the transaction requires $50.00.",
  "instance": "/api/v1/payments/txn-789",
  "traceId": "abc-123-def-456",
  "timestamp": "2024-01-15T14:23:45.123Z",
  "errors": [
    {
      "field": "amount",
      "message": "Transaction amount exceeds available balance",
      "rejectedValue": 50.00
    }
  ]
}
```

#### Global Exception Handler

```java
/**
 * Global exception handler that converts all exceptions to RFC 7807 Problem Details format.
 * This ensures EVERY error response from the API follows the same structure.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String PROBLEM_BASE_URL = "https://api.example.com/problems/";

    // ──────────────────────────────────────────────────────────
    // Business Exceptions → 4xx
    // ──────────────────────────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setType(URI.create(PROBLEM_BASE_URL + "resource-not-found"));
        problem.setTitle("Resource Not Found");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("traceId", MDC.get("traceId"));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(DuplicateResourceException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage());
        problem.setType(URI.create(PROBLEM_BASE_URL + "duplicate-resource"));
        problem.setTitle("Duplicate Resource");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("traceId", MDC.get("traceId"));

        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ProblemDetail> handleBusinessRule(BusinessRuleViolationException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ex.getMessage());
        problem.setType(URI.create(PROBLEM_BASE_URL + ex.getErrorCode()));
        problem.setTitle(ex.getErrorTitle());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("traceId", MDC.get("traceId"));

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(problem);
    }

    // ──────────────────────────────────────────────────────────
    // Validation Errors → 400 Bad Request
    // ──────────────────────────────────────────────────────────

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Request validation failed");
        problem.setType(URI.create(PROBLEM_BASE_URL + "validation-error"));
        problem.setTitle("Validation Error");

        // Collect all field errors
        List<Map<String, Object>> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        "field", (Object) error.getField(),
                        "message", (Object) error.getDefaultMessage(),
                        "rejectedValue", (Object) String.valueOf(error.getRejectedValue())))
                .collect(Collectors.toList());

        problem.setProperty("errors", errors);
        problem.setProperty("traceId", MDC.get("traceId"));

        return ResponseEntity.badRequest().body(problem);
    }

    // ──────────────────────────────────────────────────────────
    // Rate Limiting → 429 Too Many Requests
    // ──────────────────────────────────────────────────────────

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ProblemDetail> handleRateLimit(RateLimitExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.TOO_MANY_REQUESTS,
                "Rate limit exceeded. Try again in " + ex.getRetryAfterSeconds() + " seconds.");
        problem.setType(URI.create(PROBLEM_BASE_URL + "rate-limit-exceeded"));
        problem.setTitle("Rate Limit Exceeded");

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("Retry-After", String.valueOf(ex.getRetryAfterSeconds()))
                .body(problem);
    }

    // ──────────────────────────────────────────────────────────
    // Catch-all → 500 Internal Server Error
    // ──────────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex, HttpServletRequest request) {
        // Log the full stack trace server-side
        log.error("Unexpected error processing request {} {}",
                request.getMethod(), request.getRequestURI(), ex);

        // Never expose internal details to the client
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support with the trace ID.");
        problem.setType(URI.create(PROBLEM_BASE_URL + "internal-error"));
        problem.setTitle("Internal Server Error");
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("traceId", MDC.get("traceId"));
        problem.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
```

---

## 4.3 Pagination, Filtering & Sorting

---

### The "Why" & The Problem

Without pagination, `GET /orders` returns all 10 million orders. The database runs out of memory, the network saturates, and the client crashes.

---

### Interviewer Expectations

- **Cursor-based vs. offset-based pagination**: Know the trade-offs. Cursor-based is more efficient for large datasets.
- **Filtering**: Allow clients to filter by multiple fields.
- **Sorting**: Allow clients to specify sort fields and directions.
- **Keywords**: "Cursor-based pagination", "offset pagination", "keyset pagination", "page token", "HATEOAS links".

---

### The Deep Dive & Solution

#### Offset-Based Pagination (Simple but Slow for Large Datasets)

```
GET /api/v1/orders?page=0&size=20&sort=createdAt,desc

Response:
{
  "content": [ ... 20 orders ... ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 15432,
    "totalPages": 772
  },
  "_links": {
    "self":  { "href": "/api/v1/orders?page=0&size=20" },
    "next":  { "href": "/api/v1/orders?page=1&size=20" },
    "last":  { "href": "/api/v1/orders?page=771&size=20" }
  }
}
```

**Problem**: `OFFSET 100000 LIMIT 20` — the database must scan and discard 100,000 rows before returning 20. Gets slower as you paginate deeper.

#### Cursor-Based Pagination (Efficient for Large Datasets)

```
GET /api/v1/orders?limit=20&after=eyJpZCI6MTIzfQ==

Response:
{
  "data": [ ... 20 orders ... ],
  "cursors": {
    "after": "eyJpZCI6MTQ1fQ==",  // Base64-encoded cursor pointing to the last item
    "before": "eyJpZCI6MTI2fQ==",
    "hasNext": true,
    "hasPrevious": true
  },
  "_links": {
    "next": { "href": "/api/v1/orders?limit=20&after=eyJpZCI6MTQ1fQ==" }
  }
}
```

**SQL behind the scenes**:
```sql
-- Offset-based (slow for large offsets):
SELECT * FROM orders ORDER BY id DESC LIMIT 20 OFFSET 100000;
-- Must scan 100,000+ rows

-- Cursor-based (always fast, regardless of "page number"):
SELECT * FROM orders WHERE id < 123 ORDER BY id DESC LIMIT 20;
-- Uses index — always scans exactly 20 rows
```

```java
/**
 * Cursor-based pagination implementation.
 * The cursor is an opaque token (Base64-encoded JSON) that encodes the last seen value.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @GetMapping
    public ResponseEntity<CursorPageResponse<OrderResponse>> listOrders(
            @RequestParam(defaultValue = "20") @Max(100) int limit,
            @RequestParam(required = false) String after,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {

        // Decode cursor
        OrderCursor cursor = after != null ? OrderCursor.decode(after) : null;

        // Fetch limit + 1 to check if there's a next page
        List<Order> orders = orderRepository.findWithCursor(
                cursor, status, sort, limit + 1);

        boolean hasNext = orders.size() > limit;
        if (hasNext) {
            orders = orders.subList(0, limit);  // Remove the extra item
        }

        List<OrderResponse> content = orders.stream()
                .map(OrderResponse::from)
                .toList();

        // Encode new cursor from the last item
        String nextCursor = hasNext ? OrderCursor.encode(orders.get(orders.size() - 1)) : null;

        return ResponseEntity.ok(CursorPageResponse.<OrderResponse>builder()
                .data(content)
                .nextCursor(nextCursor)
                .hasNext(hasNext)
                .build());
    }
}
```

#### Offset vs. Cursor Pagination Comparison

| Aspect | Offset-Based | Cursor-Based |
|--------|-------------|-------------|
| **Performance** | Degrades with deep pages (OFFSET 100000) | Constant performance (always fast) |
| **Jump to page** | ✅ Can go to page 50 directly | ❌ Can only go forward/backward |
| **Total count** | ✅ Provides total elements/pages | ❌ Usually no total count (expensive to compute) |
| **Consistency** | ❌ New items shift pages (duplicates/skips) | ✅ Stable — no duplicates even with concurrent inserts |
| **Best for** | Small datasets, admin dashboards | Large datasets, infinite scroll, APIs |

#### Filtering & Sorting Best Practices

```
# Filtering — use query parameters with clear field names
GET /api/v1/orders?status=COMPLETED&customerId=C-456&minAmount=100&maxAmount=500

# Sorting — field name + direction (comma-separated)
GET /api/v1/orders?sort=createdAt,desc&sort=total,asc

# Searching — use a dedicated parameter
GET /api/v1/products?search=wireless+headphones&category=electronics

# Date range filtering
GET /api/v1/orders?createdAfter=2024-01-01T00:00:00Z&createdBefore=2024-01-31T23:59:59Z

# Field selection (sparse fieldsets) — reduce payload size
GET /api/v1/orders?fields=id,status,total,createdAt
```

---

## 4.4 API Versioning Strategies

---

### The "Why" & The Problem

Your API has consumers. You need to make breaking changes (rename a field, remove an endpoint, change the response structure). If you change the API without versioning, you break every consumer simultaneously.

---

### Interviewer Expectations

- **Three strategies**: URL path, header, query parameter. Know the trade-offs.
- **Backward compatibility**: Know what changes are breaking vs. non-breaking.
- **Keywords**: "API versioning", "backward compatibility", "deprecation policy", "sunset header".

---

### The Deep Dive & Solution

#### Versioning Strategies Comparison

| Strategy | Example | Pros | Cons |
|----------|---------|------|------|
| **URL Path** | `/api/v1/orders` | Clear, easy to understand, easy to route | URL changes, harder to share across versions |
| **Header** | `Accept: application/vnd.example.v1+json` | Clean URLs, content negotiation | Hidden version, harder to test in browser |
| **Query Param** | `/api/orders?version=1` | Easy to add, visible | Pollutes URL, caching issues |

**Recommendation**: **URL path versioning** (`/api/v1/`) is the most common and most practical. It's immediately visible, easy to route in API gateways and load balancers, and works with all clients including browsers.

```java
// URL Path Versioning — recommended approach
@RestController
@RequestMapping("/api/v1/orders")
public class OrderControllerV1 {

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseV1> getOrder(@PathVariable Long id) {
        // Returns V1 format
        return ResponseEntity.ok(orderService.getOrderV1(id));
    }
}

@RestController
@RequestMapping("/api/v2/orders")
public class OrderControllerV2 {

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseV2> getOrder(@PathVariable Long id) {
        // Returns V2 format (new fields, different structure)
        return ResponseEntity.ok(orderService.getOrderV2(id));
    }
}
```

#### Breaking vs. Non-Breaking Changes

| Change Type | Breaking? | Example |
|-------------|-----------|---------|
| Add a new field to response | ❌ No | Adding `estimatedDelivery` to OrderResponse |
| Add a new optional query parameter | ❌ No | Adding `?status=COMPLETED` filter |
| Add a new endpoint | ❌ No | Adding `GET /api/v1/orders/{id}/tracking` |
| Remove a field from response | ✅ Yes | Removing `shippingAddress` from OrderResponse |
| Rename a field | ✅ Yes | Renaming `totalPrice` to `total` |
| Change a field's type | ✅ Yes | Changing `price` from string to number |
| Make an optional field required | ✅ Yes | Making `phoneNumber` required in CreateOrderRequest |
| Change the response status code | ✅ Yes | Changing 200 to 201 for a POST |
| Remove an endpoint | ✅ Yes | Removing `DELETE /api/v1/orders/{id}` |

#### Deprecation Strategy

```java
/**
 * When deprecating an API version:
 * 1. Add @Deprecated annotation
 * 2. Add Sunset and Deprecation headers to responses
 * 3. Log usage to track which consumers still use the old version
 * 4. Set a sunset date (at least 6 months notice)
 */
@RestController
@RequestMapping("/api/v1/orders")
@Deprecated(since = "2024-06-01", forRemoval = true)
public class OrderControllerV1 {

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseV1> getOrder(@PathVariable Long id) {
        log.warn("Deprecated API v1 called by consumer. Migrate to v2.",
                kv("path", "/api/v1/orders/" + id),
                kv("sunsetDate", "2025-01-01"));

        OrderResponseV1 response = orderService.getOrderV1(id);

        return ResponseEntity.ok()
                .header("Sunset", "Sat, 01 Jan 2025 00:00:00 GMT")
                .header("Deprecation", "true")
                .header("Link", "</api/v2/orders/" + id + ">; rel=\"successor-version\"")
                .body(response);
    }
}
```

---

## 4.5 HATEOAS — Hypermedia as the Engine of Application State

---

### The "Why" & The Problem

Without HATEOAS, the client must hardcode all API URLs. If you rename an endpoint, every client breaks. HATEOAS makes the API **self-describing** — responses include links to related resources and available actions.

---

### Interviewer Expectations

- **Understand the concept**: Responses contain links that guide the client through available transitions.
- **Practical use**: Know when HATEOAS is worth the complexity (public APIs, long-lived APIs) vs. when it's overkill (internal microservice-to-microservice calls).
- **Keywords**: "HATEOAS", "hypermedia", "discoverability", "self-describing API", "HAL format".

---

### The Deep Dive & Solution

```java
/**
 * HATEOAS response — the client doesn't need to hardcode URLs.
 * The response tells the client what it can do next.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @GetMapping("/{id}")
    public EntityModel<OrderResponse> getOrder(@PathVariable Long id) {
        OrderResponse order = orderService.findById(id);

        EntityModel<OrderResponse> model = EntityModel.of(order);

        // Self link
        model.add(linkTo(methodOn(OrderController.class).getOrder(id)).withSelfRel());

        // Related resources
        model.add(linkTo(methodOn(OrderController.class).getOrderItems(id)).withRel("items"));
        model.add(linkTo(methodOn(CustomerController.class).getCustomer(order.getCustomerId())).withRel("customer"));

        // Available actions based on current state
        if (order.getStatus() == OrderStatus.PENDING) {
            model.add(linkTo(methodOn(OrderController.class).confirmOrder(id)).withRel("confirm"));
            model.add(linkTo(methodOn(OrderController.class).cancelOrder(id)).withRel("cancel"));
        }
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            model.add(linkTo(methodOn(OrderController.class).shipOrder(id)).withRel("ship"));
        }

        return model;
    }
}
```

**Response**:
```json
{
  "id": "O-123",
  "status": "PENDING",
  "total": 99.99,
  "customerId": "C-456",
  "_links": {
    "self": { "href": "/api/v1/orders/O-123" },
    "items": { "href": "/api/v1/orders/O-123/items" },
    "customer": { "href": "/api/v1/customers/C-456" },
    "confirm": { "href": "/api/v1/orders/O-123/confirm" },
    "cancel": { "href": "/api/v1/orders/O-123/cancel" }
  }
}
```

---

## 4.6 OpenAPI Specification (Swagger)

---

### The "Why" & The Problem

Without API documentation, consumers have to guess how your API works. They message you on Slack asking "what's the request format for creating an order?" This doesn't scale.

OpenAPI (formerly Swagger) generates API documentation from your code annotations, keeps it in sync with the actual implementation, and provides an interactive testing interface.

---

### The Deep Dive & Solution

```java
// Spring Boot + springdoc-openapi integration
// application.yml
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
    operations-sorter: method
    tags-sorter: alpha
  info:
    title: Order Service API
    description: API for managing orders in the e-commerce platform
    version: 1.0.0
    contact:
      name: Order Team
      email: order-team@example.com
```

```java
@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {

    @Operation(
        summary = "Create a new order",
        description = "Creates a new order for the authenticated customer. Requires a valid idempotency key.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Duplicate order (idempotency key already used)",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422", description = "Business rule violation",
                content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
        })
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @Parameter(description = "Unique idempotency key (UUID)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        // ...
    }
}

@Schema(description = "Request to create a new order")
public record CreateOrderRequest(
        @Schema(description = "Customer ID", example = "C-456", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank String customerId,

        @Schema(description = "Order items (at least 1)", minLength = 1)
        @NotEmpty @Valid List<OrderItemRequest> items,

        @Schema(description = "Shipping address")
        @NotNull @Valid AddressRequest shippingAddress,

        @Schema(description = "Optional notes for the order", example = "Please deliver before 5pm")
        String notes
) {}
```

---

## 4.7 API Security Best Practices

---

### The "Why" & The Problem

APIs are the primary attack surface. The OWASP API Security Top 10 lists the most common API vulnerabilities: broken authentication, broken object-level authorization, excessive data exposure, lack of rate limiting.

---

### The Deep Dive & Solution

#### Security Checklist for APIs

| # | Practice | Implementation |
|---|---------|---------------|
| 1 | **Authentication** | OAuth2 + JWT. Validate token signature, expiration, issuer, audience. |
| 2 | **Authorization** | Object-level: user can only access their own orders. `@PreAuthorize("@authService.isOwner(#id)")` |
| 3 | **Input validation** | Validate all inputs. Use `@Valid`, `@NotBlank`, `@Size`, `@Pattern`. Never trust client input. |
| 4 | **Rate limiting** | Per-user and per-IP rate limits. Return `429` with `Retry-After` header. |
| 5 | **HTTPS only** | TLS everywhere. HSTS header. No HTTP. |
| 6 | **No sensitive data in URLs** | Tokens, passwords, SSN — always in headers or body, never in URL (logged by proxies). |
| 7 | **Output filtering** | Don't expose internal fields (database IDs, internal timestamps). Use DTOs. |
| 8 | **CORS** | Restrict allowed origins. Don't use `*` in production. |
| 9 | **Security headers** | `X-Content-Type-Options: nosniff`, `X-Frame-Options: DENY`, `Content-Security-Policy`. |
| 10 | **Audit logging** | Log all authentication attempts, authorization failures, and data mutations. |

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disabled for stateless API (using JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health/**").permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            )
            .headers(headers -> headers
                .contentTypeOptions(Customizer.withDefaults())
                .frameOptions(frame -> frame.deny())
                .xssProtection(Customizer.withDefaults())
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://app.example.com", "https://admin.example.com"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Idempotency-Key"));
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
```

---

## 4.8 Interview Quick Reference: API Design

---

### Common Interview Questions & Lead-Level Answers

**Q: "How would you design a REST API for an e-commerce platform?"**

A: "I'd follow **resource-oriented design** with nouns in URLs: `/api/v1/products`, `/api/v1/orders`, `/api/v1/customers`. Each resource supports standard HTTP methods: GET for retrieval, POST for creation (with `Idempotency-Key` header to prevent duplicates), PUT for full updates, PATCH for partial updates, DELETE for removal. I'd use **cursor-based pagination** for list endpoints to maintain performance at scale, with filtering via query parameters (`?status=COMPLETED&minAmount=100`). Errors follow **RFC 7807 Problem Details** format for consistent error handling across all endpoints. All responses include **HATEOAS links** to guide consumers to related resources and available actions. The API is versioned via **URL path** (`/api/v1/`), documented with **OpenAPI/Swagger**, and secured with **OAuth2 + JWT** with object-level authorization to ensure users can only access their own resources."

**Q: "When would you use cursor-based vs. offset-based pagination?"**

A: "**Offset-based** is simpler and allows jumping to arbitrary pages — ideal for admin dashboards and small datasets (< 100K rows). However, `OFFSET 100000` requires the database to scan and discard 100K rows, and concurrent inserts can cause duplicate/skipped items. **Cursor-based** uses a `WHERE id < :lastId` clause, which always hits an index and returns in constant time regardless of 'page depth'. It's ideal for large datasets, infinite scroll UIs, and real-time data where rows are being inserted. The trade-off is you lose the ability to jump to page 50 — you can only move forward or backward. For most production APIs serving mobile or web clients, I default to cursor-based."
