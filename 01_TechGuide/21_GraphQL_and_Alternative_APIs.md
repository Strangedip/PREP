# Section 21: GraphQL & Alternative API Styles (2026)

> **Level**: MID+ (when to choose GraphQL) to SR+ (federation, N+1, caching at scale)
> **Why This Matters**: REST remains the default, but GraphQL, gRPC, and event-driven APIs are standard in modern stacks. Senior interviews expect you to compare styles and justify choices — not just default to REST.

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [20_Technical_Leadership_Architecture.md](20_Technical_Leadership_Architecture.md) | **Next**: [22_Kotlin_for_Java_Developers.md](22_Kotlin_for_Java_Developers.md)

---

## 21.1 API Style Comparison — Decision Matrix

| Style | Best For | Strengths | Weaknesses |
|-------|----------|-----------|------------|
| **REST** | Public APIs, CRUD resources, caching via HTTP | Simple, cacheable, universal tooling | Over/under-fetching, many endpoints |
| **GraphQL** | Mobile/frontend-heavy apps, aggregating backends | Single endpoint, client-driven shape | N+1 queries, caching complexity, learning curve |
| **gRPC** | Internal microservices, low latency, streaming | Binary Protobuf, HTTP/2, bi-directional streams | Browser support limited (needs grpc-web) |
| **WebSocket** | Real-time push (chat, live scores) | Full-duplex, low latency | Connection management, scaling |
| **SSE** | Server-to-client streaming (AI tokens, logs) | Simple over HTTP, auto-reconnect | One-direction only |

**Interview answer template**: "I'd use REST for public CRUD with CDN caching. GraphQL when the client needs flexible queries across multiple domains. gRPC for internal service-to-service. WebSocket/SSE for real-time."

---

## 21.2 GraphQL Fundamentals

### Core Concepts

```
Schema (types)     →  defines what can be queried
Query              →  read data (like GET, but flexible)
Mutation           →  write data (like POST/PUT/DELETE)
Subscription       →  real-time updates over WebSocket
Resolver           →  function that fetches data for each field
```

**Example schema:**

```graphql
type User {
  id: ID!
  name: String!
  email: String!
  orders: [Order!]!    # nested — resolved separately
}

type Query {
  user(id: ID!): User
  users(limit: Int = 10): [User!]!
}

type Mutation {
  createUser(input: CreateUserInput!): User!
}
```

### GraphQL vs REST — Over-fetching Example

```
REST: GET /users/42        → returns ALL user fields + fixed nested shape
GraphQL: query { user(id: 42) { name } }  → returns ONLY name
```

---

## 21.3 Spring GraphQL (Spring Boot 3.x)

```java
@Controller
public class UserGraphQLController {

    @QueryMapping
    public User user(@Argument Long id) {
        return userService.findById(id);
    }

    @SchemaMapping(typeName = "User", field = "orders")
    public List<Order> orders(User user) {
        return orderService.findByUserId(user.getId());
    }

    @MutationMapping
    public User createUser(@Argument CreateUserInput input) {
        return userService.create(input);
    }
}
```

**Dependencies**: `spring-boot-starter-graphql` + GraphiQL for dev.

---

## 21.4 The N+1 Problem — Critical Interview Topic

When resolving nested fields, each parent row triggers a separate DB query.

```
Query: users { orders { items { product } } }

Without batching:
  1 query for users
  N queries for each user's orders
  N×M queries for items...

With DataLoader (batch + cache within request):
  1 query users
  1 batched query all orders for user IDs
  1 batched query all items for order IDs
```

**Spring GraphQL DataLoader:**

```java
@Bean
public DataLoader<Long, List<Order>> ordersDataLoader(OrderService orderService) {
    return DataLoader.newMappedDataLoader(userIds ->
        orderService.findOrdersByUserIds(userIds));
}
```

**Keywords**: DataLoader, batch loading, request-scoped cache, `@BatchMapping`.

---

## 21.5 GraphQL at Scale — Caching & Security

### Caching Challenges

HTTP GET caching does not work for arbitrary GraphQL queries (unique query shapes).

| Strategy | How |
|----------|-----|
| **Persisted queries** | Client sends query ID; server maps to known query — enables CDN caching |
| **APQ (Automatic Persisted Queries)** | Hash query body → store on server |
| **Field-level caching** | Cache resolver results in Redis (short TTL) |
| **Entity caching** | Cache `User:42` regardless of query shape |

### Security

| Risk | Mitigation |
|------|------------|
| **Query depth attack** | Max depth limit (e.g., 10 levels) |
| **Query complexity** | Cost analysis — reject expensive queries |
| **Introspection in prod** | Disable GraphQL introspection in production |
| **Authorization** | Field-level auth in resolvers, not just endpoint-level |
| **Rate limiting** | Per-client query cost budgets |

---

## 21.6 GraphQL Federation (Apollo / Spring)

For microservices, each service owns part of the schema:

```
User Service     →  type User @key(fields: "id")
Order Service    →  extend type User @key(fields: "id") { orders: [Order] }
Gateway          →  stitches subgraphs into unified schema
```

**When to use**: Large orgs with 20+ services and multiple frontend teams. **Not** for small monoliths.

---

## 21.7 REST vs GraphQL — Interview Trade-offs

| Dimension | REST | GraphQL |
|-----------|------|---------|
| Caching | HTTP cache, CDN-friendly | Complex — needs persisted queries |
| Versioning | URL/header versioning | Evolve schema, deprecate fields |
| Error handling | HTTP status codes | Partial success (`errors` + `data`) |
| File upload | Standard multipart | Needs separate endpoint or GraphQL multipart spec |
| Learning curve | Low | Medium-high |
| Mobile bandwidth | Over-fetching risk | Client picks fields |
| Backend complexity | Simple controllers | Resolvers, N+1, schema governance |

---

## 21.8 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | REST vs GraphQL? | REST: resource URLs + HTTP caching. GraphQL: single endpoint, client-defined shape. |
| 2 | GraphQL N+1? | Nested resolvers fire per parent row. Fix with DataLoader batching. |
| 3 | When NOT GraphQL? | Simple CRUD, heavy CDN caching needs, public API with stable contracts. |
| 4 | GraphQL caching? | Persisted queries, entity caching, short TTL field caches — not standard HTTP cache. |
| 5 | gRPC vs GraphQL? | gRPC: internal services, binary, streaming. GraphQL: client-facing flexible queries. |
| 6 | GraphQL subscriptions? | WebSocket transport for real-time field updates. |
| 7 | Schema stitching vs federation? | Federation: distributed ownership with `@key`. Stitching: gateway merges schemas (legacy). |
| 8 | GraphQL security? | Depth limits, complexity scoring, disable introspection, field-level auth. |
| 9 | Spring GraphQL? | `@QueryMapping`, `@SchemaMapping`, DataLoader integration, GraphiQL. |
| 10 | Partial errors? | GraphQL returns `data` + `errors` — some fields succeed, others fail. |

**Must-say keywords**: DataLoader, N+1, persisted queries, federation, resolver, over-fetching, schema evolution, query complexity.

---

## §21.10 Production & Interview Depth — BFF, Federation & Festival Traffic

Indian product orgs rarely adopt GraphQL as the **only** API style. Flipkart and Swiggy-style stacks typically run **REST/gRPC internally** and expose GraphQL (or a thin BFF) only where mobile bandwidth and screen-specific aggregation justify the operational cost. During Big Billion Days or IPL flash sales, a poorly bounded GraphQL gateway becomes a fan-out amplifier — one client query can trigger 15 downstream calls.

### Architecture Pattern: BFF + GraphQL Gateway

```
Mobile App → GraphQL BFF (Spring GraphQL) → gRPC/REST microservices
                │                              ├── Catalog (REST)
                │                              ├── Inventory (gRPC)
                └── DataLoader batching          └── Pricing (REST)
```

The BFF owns the schema for **one client surface** (consumer app vs seller dashboard). Domain services stay REST/gRPC — see [04_API_Design_REST.md](./04_API_Design_REST.md) and [06_Microservices_Distributed_Systems.md](./06_Microservices_Distributed_Systems.md). Razorpay-style dashboards often skip GraphQL entirely and use REST with field filtering via `?fields=` — simpler to cache at CDN.

### Trade-off: GraphQL Gateway vs REST BFF at Scale

| Dimension | GraphQL BFF (Spring GraphQL) | REST BFF (Spring MVC) |
|-----------|------------------------------|------------------------|
| Mobile payload | Client picks fields — wins on 4G | Over-fetch unless DTOs per screen |
| Caching at edge | Needs APQ/persisted queries | HTTP cache + CDN trivial |
| N+1 risk | High without DataLoader | One endpoint = one query plan |
| Team ownership | Schema governance across squads | Per-endpoint ownership — clearer |
| Festival load | Query complexity limits mandatory | Rate limit per endpoint — familiar ops |

### Production Guardrails (Spring Boot 3.x)

```java
@Configuration
public class GraphQLSecurityConfig {

    @Bean
    public Instrumentation maxQueryDepthInstrumentation() {
        return new MaxQueryDepthInstrumentation(8);  // block depth-10 cart→item→seller chains
    }

    @Bean
    public DataLoader<Long, Inventory> inventoryLoader(InventoryClient client) {
        return DataLoader.newMappedDataLoader(skus ->
            client.batchGetInventory(skus));  // 1 gRPC call, not N
    }
}
```

Pair with [28_Redis_Distributed_Caching.md](./28_Redis_Distributed_Caching.md) for entity-level resolver caching (`product:42` TTL 30s) during catalog spikes. Interview framing: *"GraphQL where the client team needs shape flexibility; gRPC between services for p99 latency; REST for public partner APIs with CDN."* For subscriptions (live order tracking), prefer WebSocket BFF over GraphQL subscriptions unless the team already operates a federation mesh — see [19_Event_Driven_Architecture.md](./19_Event_Driven_Architecture.md).
