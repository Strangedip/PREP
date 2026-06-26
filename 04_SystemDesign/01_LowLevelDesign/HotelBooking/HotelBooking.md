# Hotel Booking System — Low Level Design

## Problem Statement

Design a hotel room booking system that:

- **Searches** available rooms by date range, city, guests
- **Books** rooms with date-range validation
- **Prevents double booking** of the same room for overlapping dates
- **Supports cancellation** with policy rules
- **Calculates pricing** — base rate, seasonal, room type

---

## Requirements

### Functional Requirements

1. **Search** — Hotels by city, check-in/out, guest count, room type filter
2. **Book** — Reserve room for date range; confirmation ID
3. **Cancel** — Free cancel before deadline; penalty after
4. **Modify** — Change dates if new range available
5. **Room types** — Single, Double, Suite with different capacity and price

### Non-Functional Requirements

1. **Consistency** — No double booking (critical)
2. **Performance** — Search < 500ms for city with 1000 hotels
3. **Extensibility** — New pricing rules without changing core booking logic

---

## Design Patterns

| Pattern | Use |
|---------|-----|
| **Strategy** | Pricing (standard, seasonal, promo) |
| **Repository** | Hotel, Room, Booking persistence abstraction |
| **Factory** | Create booking confirmations |
| **Observer** | Notify on booking confirm/cancel (email hook) |

---

## Class Diagram

```
┌──────────┐     ┌──────────┐     ┌─────────────┐
│  Hotel   │────▶│   Room   │     │  Booking    │
├──────────┤ 1:N ├──────────┤     ├─────────────┤
│ id, city │     │ id, type │     │ id, userId  │
│ name     │     │ capacity │     │ roomId      │
└──────────┘     │ status   │     │ checkIn/out │
                 └──────────┘     │ status      │
                        │         │ totalPrice  │
                        │         └─────────────┘
                 ┌──────▼──────┐
                 │ Availability│
                 │ Service     │
                 ├─────────────┤
                 │ isAvailable │
                 │ lockDates   │
                 └─────────────┘

┌──────────────────┐
│ PricingStrategy  │
├──────────────────┤
│ calculatePrice() │
└────────┬─────────┘
         △
    ┌────┴─────┐
    │ Seasonal │
    │ Standard │
    └──────────┘
```

---

## Double-Booking Prevention

**Approach 1 — Database constraint (production)**:
```sql
-- booking_slots: one row per room per night
INSERT INTO booking_slots (room_id, date, booking_id)
  SELECT room_id, date, :bookingId FROM generate_series(check_in, check_out-1)
ON CONFLICT (room_id, date) DO NOTHING;
-- If rows inserted < expected nights → conflict
```

**Approach 2 — In-memory lock (LLD demo)**:
```java
public synchronized Booking book(Room room, DateRange range, String userId) {
    if (!availabilityService.isAvailable(room, range))
        throw new RoomNotAvailableException();
    availabilityService.reserve(room, range);
    return bookingRepository.save(new Booking(room, range, userId));
}
```

**Approach 3 — Distributed (HLD)**: Redis `SETNX` per `room_id:date` with TTL during checkout — see [Ticketmaster HLD](../../02_HighLevelDesign/Ticketmaster/Ticketmaster.md).

---

## Core APIs

```java
List<Room> search(SearchCriteria criteria);
Booking book(String userId, long roomId, LocalDate checkIn, LocalDate checkOut);
void cancel(String bookingId);
Booking modify(String bookingId, LocalDate newCheckIn, LocalDate newCheckOut);
```

---

## Pricing

```java
long price = pricingStrategy.calculate(room, checkIn, checkOut);
// Base: nights × nightlyRate
// Seasonal: multiplier on peak dates
// Taxes/fees: added at checkout layer
```

---

## Edge Cases

| Case | Handling |
|------|----------|
| Check-out ≤ check-in | Validation error |
| Book last available room | Two users — first wins; second gets conflict |
| Cancel after deadline | Apply penalty % from policy |
| Modify to overlapping busy dates | Release old slots, fail if new not available |
| Guest count > room capacity | Reject at search/book |
| Long stay (365 nights) | Cap max nights; batch slot inserts |

---

## Interview Discussion Points

1. **Overlap detection** — Interval overlap: `start1 < end2 && start2 < end1`
2. **Search optimization** — Index by city + date bitmap; not scan all rooms
3. **Similar to BookMyShow** — Seat vs room night inventory — compare patterns
4. **Idempotent booking** — Client retries with same idempotency key
5. **HLD extension** — Cache search results; CQRS for read-heavy search

**Related**: [BookMyShow](../BookMyShow/BookMyShow.md), [Ticketmaster HLD](../../02_HighLevelDesign/Ticketmaster/Ticketmaster.md)
