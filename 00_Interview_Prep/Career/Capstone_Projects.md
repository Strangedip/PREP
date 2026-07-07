# Capstone Portfolio Projects — Specs & Rubrics

> **You are here**: Fresher → SDE2 — Career Prep
> **Depth**: Standard (full project specs with milestones and evaluation rubrics)
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [Resume & Portfolio](Resume_and_Portfolio.md) | **Next**: [First 90 Days](../../06_On_The_Job/01_First_90_Days.md)

---

## Why capstone projects matter

Interviewers trust **working code** more than certificate lists. A well-executed capstone proves you can:

- Model a real domain with OOP and clean APIs
- Write tests and document your work
- Make trade-offs visible in a README
- Explain decisions in a 5-minute walkthrough

Pick **one project matching your target level** — depth beats quantity.

---

## How to use this guide

| Step | Action |
|------|--------|
| 1 | Choose project by target role (table below) |
| 2 | Complete milestones in order — don't skip tests |
| 3 | Self-score with rubric before adding to resume |
| 4 | Record a 3-min demo video (optional but strong differentiator) |
| 5 | Prepare STAR stories from challenges you hit |

| Target level | Recommended project |
|--------------|---------------------|
| **Fresher / internship** | Project 1 — Task API |
| **SDE1–SDE2** | Project 2 — Order Service |
| **SDE2–Senior** | Project 3 — Observability Platform |

---

# Project 1: Task Management API (Fresher / SDE1)

## Overview

Build a **team task tracker** backend (like a minimal Jira/Trello API) with users, projects, tasks, and status workflow. Demonstrates OOP, REST, SQL, auth, and tests — the core fresher interview stack.

## Tech stack

| Layer | Choice |
|-------|--------|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL (or H2 for local dev) |
| Auth | JWT (stateless) |
| Tests | JUnit 5 + MockMvc |
| Docs | OpenAPI (springdoc) |

## Functional requirements

| # | Feature | Detail |
|---|---------|--------|
| 1 | User registration/login | Email + password; bcrypt hash |
| 2 | Projects | CRUD; owner can invite members |
| 3 | Tasks | Title, description, assignee, priority, status |
| 4 | Status workflow | TODO → IN_PROGRESS → DONE (no skip) |
| 5 | Filter tasks | By project, assignee, status |
| 6 | Pagination | `GET /tasks?page=0&size=20` |

## Non-functional requirements

- API returns consistent JSON error format
- All endpoints except `/auth/*` require JWT
- README with setup in < 10 commands

## Milestones (4 weeks)

| Week | Deliverable | Done when |
|------|-------------|-----------|
| 1 | Project scaffold, User entity, register/login, JWT | Postman login returns token |
| 2 | Project CRUD, membership, authorization | Non-member cannot access project |
| 3 | Task CRUD, status workflow, filters | Invalid status transition returns 400 |
| 4 | Tests (≥10), README, OpenAPI, Dockerfile | `mvn test` green; new clone runs via README |

## Architecture sketch

```
Client → TaskController → TaskService → TaskRepository
                       → ProjectService (auth check)
         AuthController → UserService → UserRepository
```

## Evaluation rubric (self-score / peer review)

| Criterion | 1 (Weak) | 3 (Good) | 5 (Excellent) |
|-----------|----------|----------|---------------|
| **Code structure** | Everything in one class | Controller/Service/Repo split | Clear packages; DTOs separate from entities |
| **API design** | Random URLs, wrong verbs | RESTful nouns, correct status codes | Pagination, validation, consistent errors |
| **Auth** | Hardcoded user | JWT works | Role check: only members edit project tasks |
| **Tests** | None | Service unit tests | MockMvc integration tests for happy + error paths |
| **README** | "Run main" | Setup steps | Architecture diagram, API examples, trade-offs |
| **Git hygiene** | One giant commit | Logical commits | Meaningful messages; no secrets committed |

**Resume-ready**: Total ≥ 18/30 with no criterion below 2.

## Interview talking points

- "Why JWT over sessions?" → Stateless, scales horizontally
- "How did you prevent invalid task transitions?" → Enum + allowed transitions map (State pattern lite)
- "What would you add next?" → Comments, file attachments, WebSocket notifications

---

# Project 2: E-Commerce Order Service (SDE1 / SDE2)

## Overview

Build an **order microservice** for a fictional ecommerce platform: catalog read, cart checkout, inventory reservation, and order status tracking. Demonstrates transactions, idempotency, and event-driven design — common SDE2 interview topics.

## Tech stack

| Layer | Choice |
|-------|--------|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| Database | PostgreSQL |
| Messaging | Kafka (or in-memory EventBus for MVP) |
| Cache | Redis (inventory holds) |
| Tests | Testcontainers (Postgres + Kafka) |

## Functional requirements

| # | Feature | Detail |
|---|---------|--------|
| 1 | Product catalog | Read-only products with SKU, price, stock |
| 2 | Place order | Reserve inventory, create order, publish `OrderCreated` event |
| 3 | Idempotency | `Idempotency-Key` header prevents duplicate orders |
| 4 | Cancel order | Release inventory if status allows |
| 5 | Order status | CREATED → CONFIRMED → SHIPPED → DELIVERED |
| 6 | Inventory | Decrement on confirm; reject if insufficient stock |

## Non-functional requirements

- No overselling (inventory consistency under concurrent checkout)
- Order placement p99 < 500ms on local load test (100 concurrent)
- Events consumed by stub NotificationService (log or email)

## Milestones (6 weeks)

| Week | Deliverable |
|------|-------------|
| 1 | Product + Inventory entities, catalog API |
| 2 | Order placement with DB transaction |
| 3 | Redis hold with TTL (15 min checkout window) |
| 4 | Kafka events: OrderCreated, OrderCancelled |
| 5 | Idempotency table + concurrent load test |
| 6 | Testcontainers integration tests, README architecture |

## Key design decisions to document

| Decision | Options | Your choice + why |
|----------|---------|-------------------|
| Inventory lock | Pessimistic DB lock vs Redis hold | Document trade-off |
| Idempotency | DB unique key vs Redis | Document retry behavior |
| Events | Sync vs async notification | Why async after order save |

## Evaluation rubric

| Criterion | 1 | 3 | 5 |
|-----------|---|---|---|
| **Concurrency** | Race oversells stock | synchronized block works | Redis SETNX or DB optimistic lock + test |
| **Transactions** | Partial order on failure | @Transactional rollback | Saga or outbox pattern mentioned |
| **Events** | None | Log on publish | Kafka + consumer with idempotent handler |
| **Idempotency** | Duplicate orders possible | Key stored in DB | Same key returns same order response |
| **Testing** | Manual only | Unit tests | Testcontainers e2e place-order flow |
| **Observability** | println | SLF4J structured logs | Correlation ID in logs |

**Resume-ready**: Total ≥ 20/30; must score ≥ 3 on Concurrency.

## Interview talking points

- Walk through concurrent checkout scenario
- Explain what happens when payment service is down (order CREATED but not CONFIRMED)
- HLD extension: split into Catalog, Order, Inventory, Payment services

---

# Project 3: Observability-Ready Microservice Platform (SDE2 / Senior)

## Overview

Build **two Spring Boot services** (User + Order) with a shared **observability stack**: structured logging, metrics, distributed tracing, health checks, and a sample Grafana dashboard. Demonstrates production-minded engineering expected at Senior loops.

## Tech stack

| Layer | Choice |
|-------|--------|
| Services | 2× Spring Boot 3.x (Java 17) |
| API Gateway | Spring Cloud Gateway or simple reverse proxy |
| Tracing | Micrometer Tracing + Zipkin (or Jaeger) |
| Metrics | Micrometer + Prometheus |
| Logs | JSON to stdout (ELK-ready) |
| Dashboard | Grafana + Prometheus |
| Deploy | Docker Compose |

## Functional requirements

| # | Feature | Detail |
|---|---------|--------|
| 1 | User Service | CRUD users, `GET /users/{id}` |
| 2 | Order Service | Create order; calls User Service to validate user |
| 3 | Gateway | Route `/users/**` and `/orders/**` |
| 4 | Health | `/actuator/health` on each service |
| 5 | Trace | Single trace spans gateway → order → user |
| 6 | Metrics | HTTP request count, latency histogram, error rate |

## Non-functional requirements

- Every log line includes `traceId` and `spanId`
- Prometheus scrapes metrics every 15s
- README documents how to reproduce a traced request in Zipkin UI

## Milestones (6 weeks)

| Week | Deliverable |
|------|-------------|
| 1 | Two services + gateway; order calls user via RestClient |
| 2 | Actuator health + info endpoints |
| 3 | Micrometer metrics exposed; Prometheus scrapes |
| 4 | Distributed tracing end-to-end |
| 5 | Grafana dashboard: RPS, p95 latency, error % |
| 6 | Load test (k6 or hey); document SLO targets |

## Evaluation rubric

| Criterion | 1 | 3 | 5 |
|-----------|---|---|---|
| **Tracing** | No trace IDs | Zipkin shows spans | Full chain gateway→order→user with timing |
| **Metrics** | None | /actuator/prometheus works | Custom business metric (orders_created_total) |
| **Logging** | Plain text | SLF4J | JSON structured; correlation ID |
| **Resilience** | Crash if user svc down | Timeout configured | Circuit breaker or graceful 503 + retry doc |
| **Dashboard** | None | Prometheus UI only | Grafana with latency + error panels |
| **Documentation** | Minimal | Docker compose runs | SLO table, runbook for "high error rate" |

**Resume-ready**: Total ≥ 22/30; must demo trace in interview screen share.

## Interview talking points

- "How would you debug p99 latency spike?" → Trace slow span, check DB metrics
- "What's your SLO?" → Example: 99.5% availability, p99 < 300ms
- Connect to [Production Debugging](../../06_On_The_Job/02_Production_Debugging.md)

---

## General portfolio checklist (all projects)

- [ ] GitHub repo public with MIT license
- [ ] README: problem, architecture diagram, setup, API sample, tests command
- [ ] No secrets in git history (`git log -p` check)
- [ ] CI badge (GitHub Actions: build + test)
- [ ] Can explain any line in your core service class
- [ ] Linked from resume with one metric bullet

---

## Related resources

- [Resume & Portfolio](Resume_and_Portfolio.md)
- [Code Quality](../Core/CodeQuality.md)
- [Machine Coding Guide](../../03_CodingPatterns/Machine_Coding_Round_Guide.md)
- [First 90 Days](../../06_On_The_Job/01_First_90_Days.md)
