# Food Delivery System — Low Level Design

> **You are here**: SDE1–SDE2 — System Design (LLD)
> **Depth**: Standard (full LLD with state machine, APIs, and implementation)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [LLD Template](../../00_Templates/LLD_Template/LLD_Template.md) | **Next**: [RideSharing HLD](../../02_HighLevelDesign/RideSharing/RideSharing.md)

---

## Problem Statement

Design the **core order flow** for a food delivery platform (Swiggy / Zomato / DoorDash pattern):

- **Customers** browse restaurants, build cart, place orders
- **Restaurants** accept or reject, then prepare food
- **Delivery agents** are assigned, pick up, and deliver
- **Order lifecycle** is tracked with valid state transitions only
- **Real-time status** reaches the customer app within seconds

At LLD scope: one service orchestrating order state + assignment strategies. At HLD scope: split into Order, Restaurant, Dispatch, Notification services — see [RideSharing HLD](../../02_HighLevelDesign/RideSharing/RideSharing.md) for geo-matching at scale.

---

## Requirements

### Functional Requirements

| # | Requirement | Detail |
|---|-------------|--------|
| 1 | **Browse** | Restaurants by location, cuisine, rating, open/closed |
| 2 | **Menu** | Items with price, availability flag, prep time estimate |
| 3 | **Cart & place order** | Line items, subtotal, delivery fee, payment auth stub |
| 4 | **Restaurant flow** | Accept → Preparing → Ready for pickup |
| 5 | **Dispatch** | Assign nearest available delivery agent |
| 6 | **Delivery** | Picked up → En route → Delivered |
| 7 | **Cancel** | Customer cancel before accept; rules after accept |
| 8 | **Track** | Customer polls or receives push on status change |

### Non-Functional Requirements

| # | Requirement | Target |
|---|-------------|--------|
| 1 | **Valid transitions only** | No skip states (State pattern) |
| 2 | **Real-time updates** | Status visible < 5s after change |
| 3 | **Extensibility** | Pickup-only, scheduled orders later |
| 4 | **Idempotency** | Duplicate place-order → same order ID |

---

## Design Patterns

| Pattern | Where | Why |
|---------|-------|-----|
| **State** | `OrderStatus` + transition map | Enforce valid lifecycle |
| **Strategy** | `AssignmentStrategy` | Nearest agent vs batch vs surge |
| **Observer** | `OrderStatusListener` | Decouple SMS/push/WebSocket |
| **Factory** | `OrderFactory` | Create order + line items consistently |
| **Repository** | Order, Restaurant, Agent stores | Testability |

---

## Order state machine (heart of the design)

```
                    restaurant accept
    PLACED ──────────────────────────▶ ACCEPTED
      │                                     │
      │ reject / timeout                    │ cooking
      ▼                                     ▼
  CANCELLED ◀── cancel (rules) ──── PREPARING
      ▲                                     │
      │                                     │ mark ready
      │                                     ▼
      │                                  READY
      │                                     │
      │                              assign agent
      │                                     ▼
      │                               ASSIGNED
      │                                     │
      │                               picked up
      │                                     ▼
      │                               PICKED_UP
      │                                     │
      │                               delivered
      │                                     ▼
      └──────────────────────────── DELIVERED
```

### Invalid transitions (must reject)

| From | Illegal jump to | Why |
|------|-----------------|-----|
| PLACED | DELIVERED | No restaurant or agent involvement |
| READY | PICKED_UP | Agent must be assigned first |
| DELIVERED | Any | Terminal state |
| CANCELLED | ACCEPTED | Cannot resurrect |

---

## Class diagram

```
┌────────────┐     ┌─────────────┐     ┌──────────────┐
│  Customer  │     │ Restaurant  │     │  MenuItem    │
└─────┬──────┘     └──────┬──────┘     └──────────────┘
      │                   │
      │            ┌──────▼──────┐
      └───────────▶│    Order     │
                   ├─────────────┤
                   │ id, status  │
                   │ items[]     │
                   │ total       │
                   │ restaurantId│
                   │ agentId?    │
                   └──────┬──────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
┌─────────────┐  ┌─────────────┐  ┌───────────────────┐
│  OrderItem  │  │DeliveryAgent│  │ AssignmentStrategy │
├─────────────┤  ├─────────────┤  ├───────────────────┤
│ menuItemId  │  │ id, location│  │ assign(order,agents)│
│ quantity    │  │ status      │  └───────────────────┘
│ unitPrice   │  └─────────────┘
└─────────────┘
```

---

## Core implementation

### State pattern with explicit transition table

```java
public enum OrderStatus {
    PLACED, ACCEPTED, PREPARING, READY, ASSIGNED, PICKED_UP, DELIVERED, CANCELLED;

    private static final Map<OrderStatus, Set<OrderStatus>> ALLOWED = Map.of(
        PLACED,    Set.of(ACCEPTED, CANCELLED),
        ACCEPTED,  Set.of(PREPARING, CANCELLED),
        PREPARING, Set.of(READY, CANCELLED),
        READY,     Set.of(ASSIGNED, CANCELLED),
        ASSIGNED,  Set.of(PICKED_UP, CANCELLED),
        PICKED_UP, Set.of(DELIVERED),
        DELIVERED, Set.of(),
        CANCELLED, Set.of()
    );

    public boolean canTransitionTo(OrderStatus next) {
        return ALLOWED.getOrDefault(this, Set.of()).contains(next);
    }
}

public class Order {
    private final String id;
    private OrderStatus status;
    private final List<OrderItem> items;
    private final long restaurantId;
    private Long assignedAgentId;

    public void transition(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus))
            throw new InvalidOrderStateException(status, newStatus);
        this.status = newStatus;
    }
}
```

### Order service orchestration

```java
@Service
public class OrderService {
    private final OrderRepository orders;
    private final AssignmentStrategy assignmentStrategy;
    private final AgentRepository agents;
    private final List<OrderStatusListener> listeners;

    @Transactional
    public Order placeOrder(PlaceOrderRequest req) {
        validateMenuItemsAvailable(req);
        Order order = OrderFactory.create(req);
        orders.save(order);
        notifyListeners(order, OrderStatus.PLACED);
        scheduleRestaurantTimeout(order.id(), Duration.ofMinutes(5));
        return order;
    }

    public void restaurantAccept(String orderId) {
        Order order = getOrThrow(orderId);
        order.transition(OrderStatus.ACCEPTED);
        orders.save(order);
        notifyListeners(order, OrderStatus.ACCEPTED);
    }

    public void markReady(String orderId) {
        Order order = getOrThrow(orderId);
        order.transition(OrderStatus.READY);
        orders.save(order);
        assignAgent(order);
    }

    private void assignAgent(Order order) {
        List<DeliveryAgent> available = agents.findAvailableNear(
            order.restaurantLocation());
        DeliveryAgent agent = assignmentStrategy.assign(order, available)
            .orElseThrow(() -> new NoAgentAvailableException(order.id()));
        agent.assignOrder(order.id());
        order.assignAgent(agent.id());
        order.transition(OrderStatus.ASSIGNED);
        orders.save(order);
        notifyListeners(order, OrderStatus.ASSIGNED);
    }
}
```

---

## Delivery assignment strategies

| Strategy | Algorithm | Pros | Cons | When |
|----------|-----------|------|------|------|
| **Nearest** | Min distance to restaurant | Simple, fast | Ignores traffic, batching | LLD default |
| **Min wait time** | Minimize customer wait estimate | Better UX | O(n) per order | Medium scale |
| **Batch** | One agent, multiple READY orders | Higher utilization | Delayed delivery | Peak dinner |
| **Surge zone** | Price incentive in low-supply areas | Balances supply/demand | Policy complexity | HLD / [RideSharing](../../02_HighLevelDesign/RideSharing/RideSharing.md) |

```java
public class NearestAgentStrategy implements AssignmentStrategy {
    @Override
    public Optional<DeliveryAgent> assign(Order order, List<DeliveryAgent> agents) {
        GeoPoint restaurant = order.restaurantLocation();
        return agents.stream()
            .filter(DeliveryAgent::isAvailable)
            .min(Comparator.comparing(a -> distance(a.location(), restaurant)));
    }
}
```

---

## REST APIs

| Method | Endpoint | Actor | Purpose |
|--------|----------|-------|---------|
| GET | `/v1/restaurants` | Customer | Browse by lat/lng, cuisine |
| GET | `/v1/restaurants/{id}/menu` | Customer | Menu with availability |
| POST | `/v1/orders` | Customer | Place order (`Idempotency-Key`) |
| GET | `/v1/orders/{id}` | Customer | Status + timeline |
| POST | `/v1/orders/{id}/accept` | Restaurant | Accept order |
| POST | `/v1/orders/{id}/reject` | Restaurant | Reject with reason |
| POST | `/v1/orders/{id}/preparing` | Restaurant | Start cooking |
| POST | `/v1/orders/{id}/ready` | Restaurant | Ready for pickup |
| POST | `/v1/orders/{id}/picked-up` | Agent | Confirm pickup |
| POST | `/v1/orders/{id}/delivered` | Agent | Complete delivery |
| DELETE | `/v1/orders/{id}` | Customer/System | Cancel per rules |

### Sample place order

```json
POST /v1/orders
{
  "customerId": "cust-101",
  "restaurantId": 42,
  "items": [
    { "menuItemId": 7, "quantity": 2 },
    { "menuItemId": 12, "quantity": 1 }
  ],
  "deliveryAddress": { "lat": 19.076, "lng": 72.877 }
}
```

### Status timeline response

```json
{
  "orderId": "ORD-8842",
  "status": "ASSIGNED",
  "timeline": [
    { "status": "PLACED",    "at": "2024-03-15T18:02:00Z" },
    { "status": "ACCEPTED",  "at": "2024-03-15T18:03:12Z" },
    { "status": "PREPARING", "at": "2024-03-15T18:03:30Z" },
    { "status": "READY",     "at": "2024-03-15T18:18:00Z" },
    { "status": "ASSIGNED",  "at": "2024-03-15T18:18:45Z", "agentId": "DA-55" }
  ],
  "estimatedDelivery": "2024-03-15T18:45:00Z"
}
```

---

## Real-time updates (Observer pattern)

```java
public interface OrderStatusListener {
    void onStatusChange(Order order, OrderStatus newStatus);
}

@Component
public class WebSocketNotifier implements OrderStatusListener {
    public void onStatusChange(Order order, OrderStatus newStatus) {
        messagingTemplate.convertAndSend(
            "/topic/orders/" + order.customerId(),
            OrderStatusEvent.of(order.id(), newStatus));
    }
}

@Component
public class PushNotificationService implements OrderStatusListener {
    public void onStatusChange(Order order, OrderStatus newStatus) {
        if (newStatus == OrderStatus.ASSIGNED)
            pushService.send(order.customerId(), "Your order is on the way!");
    }
}
```

**HLD extension**: Kafka topic `order-events` → Notification Service, Analytics, Restaurant dashboard.

---

## Restaurant timeout (scheduled job)

```java
@Scheduled(fixedRate = 60_000)
public void cancelUnacceptedOrders() {
    List<Order> stale = orders.findByStatusAndCreatedBefore(
        OrderStatus.PLACED, Instant.now().minus(5, ChronoUnit.MINUTES));
    stale.forEach(order -> {
        order.transition(OrderStatus.CANCELLED);
        orders.save(order);
        notifyListeners(order, OrderStatus.CANCELLED);
        paymentService.refund(order.paymentId());
    });
}
```

---

## Edge cases

| Case | Handling |
|------|----------|
| Restaurant timeout (no accept in 5 min) | Auto-cancel + refund |
| No agents available | Stay READY; retry assignment every 30s |
| Agent cancels mid-delivery | Re-assign; order back to READY |
| Item out of stock at accept | Partial order or reject with reason |
| Duplicate place order | `Idempotency-Key` → return existing order |
| Payment fails | Order stays `PAYMENT_PENDING`; never reaches PLACED |
| Customer cancel after ACCEPTED | Policy: may charge cancellation fee |
| Concurrent status updates | Optimistic lock on `order.version` |

---

## Unit tests

```java
@Test
void placed_canTransitionToAccepted() {
    Order order = new Order(OrderStatus.PLACED);
    order.transition(OrderStatus.ACCEPTED);
    assertEquals(OrderStatus.ACCEPTED, order.status());
}

@Test
void placed_cannotSkipToDelivered() {
    Order order = new Order(OrderStatus.PLACED);
    assertThrows(InvalidOrderStateException.class,
        () -> order.transition(OrderStatus.DELIVERED));
}

@Test
void assignAgent_picksNearest() {
    Order order = orderAt(mumbaiRestaurant());
    DeliveryAgent near = agentAt(500);  // meters away
    DeliveryAgent far = agentAt(5000);
    Optional<DeliveryAgent> chosen = strategy.assign(order, List.of(far, near));
    assertEquals(near.id(), chosen.get().id());
}
```

---

## HLD extensions (verbal in interview)

| Component | Responsibility |
|-----------|----------------|
| **Order Service** | State machine, idempotency |
| **Dispatch Service** | Geo index (geohash), real-time agent locations |
| **Payment Service** | Auth on place, capture on deliver |
| **Notification Service** | Push, SMS, WebSocket fan-out |
| **Restaurant Service** | Menu, hours, prep time ML |

**Event sourcing**: Store `OrderEvent` log for audit, replay, and analytics.

---

## Interview walkthrough (45 min)

1. **Clarify** (5 min): actors, cancel rules, real-time requirement
2. **State machine** (15 min): draw diagram — interviewers love this
3. **Classes + State pattern** (10 min)
4. **Assignment strategy** (5 min)
5. **APIs + observer** (5 min)
6. **Edge cases** (5 min): timeout, no agent, idempotency

**Related**: [RideSharing HLD](../../02_HighLevelDesign/RideSharing/RideSharing.md), [NotificationSystem HLD](../../02_HighLevelDesign/NotificationSystem/NotificationSystem.md), [Machine Coding Guide](../../../03_CodingPatterns/Machine_Coding_Round_Guide.md)
