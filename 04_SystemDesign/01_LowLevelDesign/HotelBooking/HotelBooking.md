# Hotel Booking System — Low Level Design

> **You are here**: SDE1–SDE2 — System Design (LLD)
> **Depth**: Standard (full LLD with APIs, flows, and implementation)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [LLD Template](../../00_Templates/LLD_Template/LLD_Template.md) | **Next**: [BookMyShow](../BookMyShow/BookMyShow.md)

---

## Problem Statement

Design a hotel room booking system (like Booking.com / OYO at core) that:

- **Searches** available rooms by date range, city, guests, and room type
- **Books** rooms with strict date-range validation
- **Prevents double booking** of the same room for overlapping dates
- **Supports cancellation** with policy-based penalties
- **Calculates pricing** using pluggable strategies (standard, seasonal, promo)

This is a classic **interval inventory** problem — same family as [BookMyShow](../BookMyShow/BookMyShow.md) (seat + show time) but inventory is **room-nights** instead of discrete seats.

---

## Requirements

### Functional Requirements

| # | Requirement | Detail |
|---|-------------|--------|
| 1 | **Search** | Hotels by city, check-in/out dates, guest count, optional room type filter |
| 2 | **View availability** | Per-hotel room list with nightly rate and total price |
| 3 | **Book** | Reserve room for `[checkIn, checkOut)`; return confirmation ID |
| 4 | **Cancel** | Free cancel before deadline; penalty % after |
| 5 | **Modify** | Change dates if new range is available (atomic release + rebook) |
| 6 | **Room types** | SINGLE (1 guest), DOUBLE (2), SUITE (4) — different capacity and price |

### Non-Functional Requirements

| # | Requirement | Target |
|---|-------------|--------|
| 1 | **Consistency** | Zero double bookings (hard invariant) |
| 2 | **Search latency** | < 500ms for city with 1,000 hotels |
| 3 | **Extensibility** | New pricing rules without changing booking core |
| 4 | **Idempotency** | Duplicate `POST /bookings` with same key → same result |

---

## Design Patterns

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `PricingStrategy` | Standard / seasonal / promo pricing |
| **Repository** | `HotelRepository`, `BookingRepository` | Persistence abstraction (H2 in interview, PostgreSQL in prod) |
| **Factory** | `BookingConfirmationFactory` | Consistent confirmation IDs and emails |
| **Observer** | `BookingEventPublisher` | Decouple confirm/cancel from email/SMS |
| **Facade** | `BookingService` | Single entry for book/cancel/modify orchestration |

---

## Class Diagram

```
┌──────────────┐       1:N      ┌──────────────┐
│    Hotel     │───────────────▶│     Room     │
├──────────────┤                ├──────────────┤
│ id           │                │ id, hotelId  │
│ name, city   │                │ type, capacity│
│ address      │                │ baseRate     │
└──────────────┘                └──────┬───────┘
                                       │
┌──────────────┐                ┌──────▼───────────┐
│   Booking    │◀───────────────│ AvailabilityService│
├──────────────┤                ├──────────────────┤
│ id, userId   │                │ isAvailable()    │
│ roomId       │                │ reserveSlots()   │
│ checkIn/out  │                │ releaseSlots()   │
│ status       │                └──────────────────┘
│ totalPrice   │
└──────────────┘
        │
        ▼
┌──────────────────┐
│ PricingStrategy  │◀── StandardPricing, SeasonalPricing
├──────────────────┤
│ calculatePrice() │
└──────────────────┘
```

---

## Core domain model

```java
public record DateRange(LocalDate checkIn, LocalDate checkOut) {
    public DateRange {
        if (!checkOut.isAfter(checkIn))
            throw new IllegalArgumentException("checkOut must be after checkIn");
    }
    public long nights() { return ChronoUnit.DAYS.between(checkIn, checkOut); }
    public boolean overlaps(DateRange other) {
        return checkIn.isBefore(other.checkOut) && other.checkIn.isBefore(checkOut);
    }
}

public enum RoomType { SINGLE(1), DOUBLE(2), SUITE(4);
    private final int maxGuests;
    RoomType(int maxGuests) { this.maxGuests = maxGuests; }
    public boolean fits(int guests) { return guests <= maxGuests; }
}

public enum BookingStatus { CONFIRMED, CANCELLED, COMPLETED }
```

---

## Double-booking prevention (critical section)

### The overlap rule

Two bookings conflict if date ranges overlap:

```
[start1, end1) overlaps [start2, end2)  ⟺  start1 < end2 AND start2 < end1

Example:
  Booking A: Mar 10 → Mar 15  (nights: 10,11,12,13,14)
  Booking B: Mar 12 → Mar 14  → OVERLAP (shares nights 12, 13)
  Booking C: Mar 15 → Mar 17  → OK (checkout day = next check-in is allowed)
```

**Note**: Hotel convention uses `[checkIn, checkOut)` — check-out morning does not block that night for next guest.

### Approach comparison

| Approach | Consistency | Throughput | Complexity | Best for |
|----------|-------------|------------|------------|----------|
| **DB slot rows + UNIQUE** | Strong | Medium | Medium | **Production default** |
| **In-memory `synchronized`** | Single JVM | Low | Low | Interview demo |
| **Redis SETNX per night** | Strong + TTL | High | High | Flash sales / HLD |
| **Optimistic locking (`version`)** | Strong | Medium | Low | Low contention |

### Production: slot-per-night table

```sql
CREATE TABLE booking_slots (
    room_id     BIGINT NOT NULL,
    slot_date   DATE NOT NULL,
    booking_id  BIGINT NOT NULL,
    PRIMARY KEY (room_id, slot_date)
);

-- On book: insert one row per night in range
INSERT INTO booking_slots (room_id, slot_date, booking_id)
SELECT :roomId, d::date, :bookingId
FROM generate_series(:checkIn, :checkOut - INTERVAL '1 day', '1 day') AS d
ON CONFLICT (room_id, slot_date) DO NOTHING;

-- If inserted_rows < expected_nights → room not available
```

### Interview demo: in-memory service

```java
@Service
public class BookingService {
    private final AvailabilityService availability;
    private final BookingRepository bookingRepo;
    private final PricingStrategy pricing;

    public synchronized BookingResponse book(BookRequest req) {
        Room room = roomRepo.findById(req.roomId())
            .orElseThrow(() -> new NotFoundException("Room not found"));

        if (!room.type().fits(req.guestCount()))
            throw new ValidationException("Guest count exceeds room capacity");

        DateRange range = new DateRange(req.checkIn(), req.checkOut());
        if (!availability.isAvailable(room.id(), range))
            throw new RoomNotAvailableException(room.id(), range);

        long price = pricing.calculate(room, range);
        availability.reserve(room.id(), range);
        Booking booking = bookingRepo.save(
            new Booking(req.userId(), room.id(), range, price, BookingStatus.CONFIRMED));
        return BookingResponse.from(booking);
    }
}
```

---

## Search flow — how availability works

### Naive approach (interview start)

For each room in city, check if any existing booking overlaps requested dates — O(rooms × bookings).

### Optimized approach (production)

1. Index hotels by `city`
2. For date range, query `booking_slots` with anti-join:

```sql
SELECT r.* FROM rooms r
JOIN hotels h ON r.hotel_id = h.id
WHERE h.city = :city
  AND r.capacity >= :guests
  AND r.id NOT IN (
    SELECT room_id FROM booking_slots
    WHERE slot_date >= :checkIn AND slot_date < :checkOut
  );
```

3. Cache search results keyed by `(city, checkIn, checkOut, guests)` with short TTL (1–5 min)

### Search sequence

```
Client → GET /search?city=Mumbai&checkIn=2024-03-10&checkOut=2024-03-13&guests=2
       → SearchService validates dates
       → RoomRepository.findAvailable(city, range, guests)
       → PricingStrategy.calculate for each room
       → Return sorted by price or rating
```

---

## REST APIs

| Method | Endpoint | Request | Response |
|--------|----------|---------|----------|
| GET | `/v1/hotels/search` | `city`, `checkIn`, `checkOut`, `guests`, `roomType?` | `List<HotelSearchResult>` |
| GET | `/v1/rooms/{roomId}` | — | `RoomDetail` with calendar availability |
| POST | `/v1/bookings` | `{userId, roomId, checkIn, checkOut, guests}` + `Idempotency-Key` header | `201 BookingResponse` |
| GET | `/v1/bookings/{id}` | — | `BookingResponse` |
| DELETE | `/v1/bookings/{id}` | `{userId}` | `204` or penalty breakdown |
| PATCH | `/v1/bookings/{id}` | `{newCheckIn, newCheckOut}` | `BookingResponse` |

### Sample request/response

```http
POST /v1/bookings
Idempotency-Key: book-user42-room7-20240310
Content-Type: application/json

{
  "userId": "user-42",
  "roomId": 7,
  "checkIn": "2024-03-10",
  "checkOut": "2024-03-13",
  "guests": 2
}
```

```json
{
  "bookingId": "BK-2024-001234",
  "status": "CONFIRMED",
  "roomId": 7,
  "hotelName": "Sea View Mumbai",
  "checkIn": "2024-03-10",
  "checkOut": "2024-03-13",
  "nights": 3,
  "totalPrice": 15000,
  "currency": "INR",
  "cancellationPolicy": "Free cancel until 2024-03-08 18:00 IST"
}
```

---

## Pricing strategies

```java
public interface PricingStrategy {
    long calculate(Room room, DateRange range);
}

public class StandardPricing implements PricingStrategy {
    public long calculate(Room room, DateRange range) {
        return range.nights() * room.baseRate();
    }
}

public class SeasonalPricing implements PricingStrategy {
    private final Set<LocalDate> peakDates;
    private final double peakMultiplier;

    public long calculate(Room room, DateRange range) {
        long total = 0;
        for (LocalDate d = range.checkIn(); d.isBefore(range.checkOut()); d = d.plusDays(1)) {
            long nightly = room.baseRate();
            if (peakDates.contains(d)) nightly = (long) (nightly * peakMultiplier);
            total += nightly;
        }
        return total;
    }
}
```

**Interview tip**: Use Strategy pattern so `BookingService` does not change when marketing adds "weekend discount."

---

## Cancellation and modification

### Cancellation policy

```java
public CancellationResult cancel(Booking booking, Instant now) {
    if (booking.status() == BookingStatus.CANCELLED)
        throw new ConflictException("Already cancelled");

    long penalty = 0;
    if (now.isAfter(booking.freeCancelDeadline())) {
        penalty = (long) (booking.totalPrice() * booking.penaltyPercent());
    }
    availability.release(booking.roomId(), booking.dateRange());
    booking.cancel();
    bookingRepo.save(booking);
    return new CancellationResult(penalty, booking.totalPrice() - penalty);
}
```

### Modification (atomic)

```
1. Begin transaction
2. Check new range available (excluding current booking's slots)
3. Release old slots
4. Reserve new slots
5. Recalculate price difference
6. Update booking record
7. Commit — or rollback all on failure
```

---

## Edge cases

| Case | Handling |
|------|----------|
| `checkOut <= checkIn` | `400 Bad Request` — validation on `DateRange` |
| Two users book last room | First transaction wins; second gets `409 Conflict` |
| Cancel after deadline | Apply penalty %; still release slots |
| Modify to busy dates | Transaction fails; old booking unchanged |
| `guests > room.capacity` | Reject at search and book |
| Long stay (365 nights) | Cap max nights; batch slot inserts |
| Timezone | Store dates in hotel local timezone; document in API |
| Idempotent retry | Same `Idempotency-Key` returns existing booking |

---

## Unit tests (minimum for interview)

```java
@Test
void book_succeeds_whenRoomAvailable() {
    when(availability.isAvailable(7L, range)).thenReturn(true);
    BookingResponse resp = service.book(validRequest());
    assertEquals("CONFIRMED", resp.status());
}

@Test
void book_fails_whenDatesOverlap() {
    when(availability.isAvailable(7L, range)).thenReturn(false);
    assertThrows(RoomNotAvailableException.class, () -> service.book(validRequest()));
}

@Test
void dateRange_overlap_detectsSharedNights() {
    DateRange a = new DateRange(LocalDate.of(2024, 3, 10), LocalDate.of(2024, 3, 15));
    DateRange b = new DateRange(LocalDate.of(2024, 3, 12), LocalDate.of(2024, 3, 14));
    assertTrue(a.overlaps(b));
}

@Test
void cancel_appliesPenalty_afterDeadline() {
    Booking booking = confirmedBookingWithDeadline(pastDeadline());
    CancellationResult result = service.cancel(booking, Instant.now());
    assertTrue(result.penalty() > 0);
}
```

---

## Comparison: Hotel Booking vs BookMyShow

| Dimension | Hotel Booking | BookMyShow |
|-----------|---------------|------------|
| Inventory unit | Room-night (interval) | Seat (discrete) |
| Overlap rule | Date range intersection | Same seat + same show |
| Hold pattern | Optional checkout timer | 5–10 min seat lock |
| Pricing | Per night × strategy | Per seat tier |
| Concurrency | Slot UNIQUE constraint | Row-level lock on seat |

---

## HLD extensions (mention in interview)

| Topic | Approach |
|-------|----------|
| Search scale | Elasticsearch geo + availability bitmap |
| Payment | Async confirm after payment auth (SAGA) |
| Multi-property chains | Hotel group inventory pool |
| CQRS | Write to booking DB; read from search index |

---

## Interview walkthrough (45 min)

1. **Clarify** (5 min): guests, cancellation, modify scope
2. **Entities** (10 min): Hotel, Room, Booking, DateRange
3. **Double-booking** (10 min): overlap rule + slot table — this is the differentiator
4. **APIs** (5 min): search + book + cancel
5. **Pricing** (5 min): Strategy pattern
6. **Edge cases + tests** (10 min): concurrent book, idempotency

**Related**: [BookMyShow](../BookMyShow/BookMyShow.md), [Ticketmaster HLD](../../02_HighLevelDesign/Ticketmaster/Ticketmaster.md), [Machine Coding Guide](../../../03_CodingPatterns/Machine_Coding_Round_Guide.md)
