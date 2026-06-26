# Food Delivery System — Low Level Design

## Problem Statement

Design the core order flow for a food delivery platform (like DoorDash/Uber Eats):

- **Customers** browse restaurants, place orders
- **Restaurants** accept/reject and prepare food
- **Delivery agents** assigned to pick up and deliver
- **Order lifecycle** tracked end-to-end
- **Real-time status** updates for customer app

---

## Requirements

### Functional Requirements

1. **Browse** — Restaurants by location, cuisine, rating
2. **Menu** — Items with price, availability
3. **Place order** — Cart, payment authorization (simplified)
4. **Restaurant flow** — Accept, preparing, ready for pickup
5. **Dispatch** — Assign delivery agent to order
6. **Delivery** — Picked up → en route → delivered
7. **Cancel** — Before restaurant accepts (customer); rules after

### Non-Functional Requirements

1. **Consistency** — Order state transitions valid (no skip states)
2. **Real-time** — Status push to customer (< 5s delay)
3. **Extensibility** — New order types (pickup-only, scheduled)

---

## Design Patterns

| Pattern | Use |
|---------|-----|
| **State** | Order status transitions |
| **Strategy** | Delivery assignment algorithm |
| **Observer** | Notify customer/restaurant on status change |
| **Factory** | Order and line items creation |

---

## Order State Machine

```
PLACED ──restaurant accept──▶ ACCEPTED ──cooking──▶ PREPARING ──ready──▶ READY
   │                              │                                        │
   │ reject                       │ cancel                                 │
   ▼                              ▼                                        ▼
CANCELLED                      CANCELLED                          ASSIGNED ──▶ PICKED_UP ──▶ DELIVERED
                                                                    (agent)      (agent)        (agent)
```

Invalid: `PLACED → DELIVERED` without intermediate states.

---

## Class Diagram

```
┌────────────┐   ┌────────────┐   ┌─────────────┐
│  Customer  │   │ Restaurant │   │ MenuItem    │
└─────┬──────┘   └─────┬──────┘   └─────────────┘
      │                │
      │         ┌──────▼──────┐
      │         │   Order     │
      └────────▶├─────────────┤
                │ id, status  │
                │ items[]     │
                │ total       │
                └──────┬──────┘
                       │
         ┌─────────────┼─────────────┐
         ▼             ▼             ▼
┌─────────────┐ ┌─────────────┐ ┌──────────────────┐
│ OrderItem   │ │ DeliveryAgent│ │ AssignmentStrategy│
├─────────────┤ ├─────────────┤ ├──────────────────┤
│ menuItemId  │ │ id, location │ │ assign(order)    │
│ quantity    │ │ status       │ └──────────────────┘
└─────────────┘ └─────────────┘
```

---

## Core Implementation

```java
public class Order {
    private OrderStatus status;
    private final List<OrderItem> items;
    private DeliveryAgent assignedAgent;

    public void transition(OrderStatus newStatus) {
        if (!status.canTransitionTo(newStatus))
            throw new InvalidOrderStateException(status, newStatus);
        this.status = newStatus;
        notifyObservers(new OrderStatusEvent(this, newStatus));
    }
}

public enum OrderStatus {
    PLACED, ACCEPTED, PREPARING, READY, ASSIGNED, PICKED_UP, DELIVERED, CANCELLED;

    boolean canTransitionTo(OrderStatus next) {
        return ALLOWED.getOrDefault(this, Set.of()).contains(next);
    }
}
```

---

## Delivery Assignment (Strategy)

```java
public interface AssignmentStrategy {
    Optional<DeliveryAgent> assign(Order order, List<DeliveryAgent> available);
}

// Nearest available agent with capacity
public class NearestAgentStrategy implements AssignmentStrategy {
    public Optional<DeliveryAgent> assign(Order order, List<DeliveryAgent> agents) {
        return agents.stream()
            .filter(a -> a.isAvailable())
            .min(Comparator.comparing(a -> distance(a.getLocation(), order.getRestaurantLocation())));
    }
}
```

**HLD extension**: Real-time location index (geohash), surge pricing, batching multiple orders.

---

## APIs

```java
Order placeOrder(String customerId, long restaurantId, List<OrderItemRequest> items);
void restaurantAccept(long orderId);
void restaurantReject(long orderId, String reason);
void markReady(long orderId);
void assignAgent(long orderId);  // system or dispatcher
void markPickedUp(long orderId);
void markDelivered(long orderId);
void cancelOrder(long orderId, String actor);
```

---

## Edge Cases

| Case | Handling |
|------|----------|
| Restaurant timeout (no accept) | Auto-cancel after N minutes |
| No agents available | Order stays READY; retry assignment loop |
| Agent cancels mid-delivery | Re-assign; order back to READY or ASSIGNED |
| Item out of stock at accept | Partial order or reject with reason |
| Duplicate place order | Idempotency key on `placeOrder` |
| Payment fails | Order PLACED but not confirmed until payment auth |

---

## Interview Discussion Points

1. **State pattern** — Enforce valid transitions vs string status field
2. **Observer** — Decouple notifications (SMS, push, websocket)
3. **Assignment** — Greedy nearest vs optimize batch deliveries (HLD)
4. **Compare RideSharing HLD** — Similar matching problem at scale
5. **Event sourcing** — Order history as event log for audit and replay

**Related**: [RideSharing HLD](../../02_HighLevelDesign/RideSharing/RideSharing.md), [NotificationSystem HLD](../../02_HighLevelDesign/NotificationSystem/NotificationSystem.md)
