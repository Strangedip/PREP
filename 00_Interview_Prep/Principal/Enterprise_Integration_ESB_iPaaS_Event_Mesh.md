# Enterprise Integration — ESB, iPaaS & Event Mesh

> **You are here**: Principal / Architect — Integration Strategy
> **Roadmap**: [Developer Master Roadmap](../../ROADMAP.md) | **Prerequisites**: [§06 Microservices](../../01_TechGuide/06_Microservices_Distributed_Systems.md), [§19 Event-Driven Architecture](../../01_TechGuide/19_Event_Driven_Architecture.md) | **Next**: [Multi-Region DR](Multi_Region_Active_Active_DR.md)

Modern **Java/Spring product orgs** rarely greenfield an ESB — but Principal interviews (enterprise B2B, bank modernization, SAP adjacency) expect you to compare **point-to-point**, **API gateway**, **message bus**, **iPaaS**, and **event mesh** — and map to Kafka/Spring patterns in this repo.

---

## Integration styles — decision matrix

| Style | Best for | Latency | Coupling | This repo's pattern |
|-------|----------|---------|----------|---------------------|
| **REST sync** | Request/response, CRUD | Low | Tight temporal | [§04 API Design](../../01_TechGuide/04_API_Design_REST.md), OpenFeign |
| **gRPC** | Internal service mesh | Very low | Tight | [§06 gRPC](../../01_TechGuide/06_Microservices_Distributed_Systems.md) |
| **Message queue** | Async workflows, buffering | Higher | Loose temporal | Kafka + Spring Kafka |
| **Event streaming** | Event sourcing, audit, fan-out | Higher | Loose | [§19 Kafka](../../01_TechGuide/19_Event_Driven_Architecture.md) |
| **ESB (legacy)** | Orchestrate many SOAP/ERP endpoints | Medium | Hub coupling | Strangler to APIs/events |
| **iPaaS** | SaaS ↔ SaaS, low-code flows | Variable | Vendor-mediated | Workato, MuleSoft, Boomi |
| **Event mesh** | Multi-domain events at scale | Low–medium | Topic contracts | Kafka + Schema Registry + mesh governance |

---

## ESB era vs microservices (know for bank/enterprise interviews)

### Classic ESB (circa 2005–2015)

```
SAP ──┐
CRM ──┼──▶ [ESB: WSO2, Mule ESB, IBM Integration Bus] ──▶ canonical XML, transforms, routing
Legacy─┘
```

| Pros | Cons |
|------|------|
| Central visibility | **Hub becomes bottleneck** |
| Reusable adapters | **Team dependencies on integration team** |
| Protocol mediation | **Heavy governance, slow change** |

### Strangler pattern (Principal recommendation)

```
Phase 1: ESB wraps legacy — new Spring Boot services call ESB adapter
Phase 2: Extract domain APIs behind gateway; events to Kafka for new flows
Phase 3: Retire ESB routes as legacy decommissioned
```

Align with [§07 strangler fig](../../01_TechGuide/07_System_Design.md) and [Payment HLD](../../04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) for financial correctness during migration.

---

## iPaaS — when it fits

**Use iPaaS when**:
- Integrating **Salesforce + Zendesk + Slack** without building connectors
- Business ops owns workflows; engineering owns core product
- Volume < thousands/min; not payment hot path

**Avoid iPaaS when**:
- Sub-100ms checkout orchestration
- PCI in-scope data transformation
- Need code review, unit tests, GitOps — use **Spring Boot + Kafka** instead

| iPaaS | Strength | Watch-out |
|-------|----------|-----------|
| **MuleSoft** | Enterprise API-led connectivity | License cost, Anypoint skills |
| **Boomi** | Mid-market ERP/SaaS | Less developer-native |
| **Workato** | Business user recipes | Governance, secret handling |

---

## Event mesh (Kafka-native orgs)

**Definition**: Governed network of **event streams** where producers/consumers discover and subscribe via **contracts** — not ad-hoc topic sprawl.

### Components (maps to this repo stack)

| Layer | Tool | Purpose |
|-------|------|---------|
| **Transport** | Kafka / MSK | Durable log |
| **Schema** | Confluent Schema Registry / Apicurio | Avro/Protobuf evolution |
| **Contracts** | AsyncAPI docs in Backstage | Discoverability ([§24](../../01_TechGuide/24_Platform_Engineering_IDP.md)) |
| **Governance** | Topic naming, retention, ACLs | `payments.order.placed.v1` |
| **Observability** | Consumer lag, dead-letter queues | [§11 Observability](../../01_TechGuide/11_Observability.md) |

### Spring Boot producer (canonical pattern)

```java
@Service
@RequiredArgsConstructor
public class OrderEventPublisher {
    private final KafkaTemplate<String, OrderPlacedEvent> kafka;

    public void publishOrderPlaced(Order order) {
        var event = new OrderPlacedEvent(order.getId(), order.getUserId(), Instant.now());
        kafka.send("commerce.order.placed.v1", order.getId().toString(), event)
             .whenComplete((result, ex) -> {
                 if (ex != null) log.error("publish failed orderId={}", order.getId(), ex);
             });
    }
}
```

**Outbox pattern** for exactly-once illusion with DB — mandatory for payment adjacency ([§19](../../01_TechGuide/19_Event_Driven_Architecture.md)).

---

## Sync vs async — interview framework

```
Question: "Should Checkout call Inventory sync or via Kafka?"

Sync (REST/gRPC) when:
  - User waits for answer (is item in stock?)
  - Strong consistency needed in same request
  - Simple failure model (timeout + retry + circuit breaker §06)

Async (Kafka) when:
  - Side effects (email, analytics, warehouse pick)
  - Peak buffering (festival sale)
  - Multiple subscribers (notification, fraud, search index)
```

---

## Anti-patterns at org scale

| Anti-pattern | Symptom | Fix |
|--------------|---------|-----|
| **Chatty sync chain** | 7 REST calls per checkout | Saga + local cache + async enrichment |
| **Kafka as database** | Unbounded retention, replay hell | CQRS read models; bounded retention |
| **ESB + microservices** | Two integration models forever | Time-box ESB strangler |
| **No schema registry** | Breaking consumers in prod | CI compatibility checks |
| **iPaaS on hot path** | Opaque failures at 3am | Move to code + observability |

---

## Related

- [Vendor Evaluation Rubrics](Vendor_Evaluation_Rubrics.md) — MuleSoft vs build
- [Organization Design](Organization_Design_Conway_Team_Topologies.md) — integration team topology
- [Distributed Cache HLD](../../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md)
