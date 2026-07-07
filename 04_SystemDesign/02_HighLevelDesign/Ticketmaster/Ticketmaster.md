# Ticket Booking System (Ticketmaster) — High-Level Design

> **You are here**: Senior SDE — System Design (HLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [HLD_Template.md](../../00_Templates/HLD_Template/HLD_Template.md)

## Problem Statement

Design an event ticket booking platform like **Ticketmaster**:

- **Browse events** (concerts, sports) with seat maps
- **Search and filter** by location, date, artist
- **Real-time seat availability** — no double booking
- **Temporary seat hold** during checkout (e.g., 10 minutes)
- **Concurrent booking** — thousands of users targeting same seats
- **Payment integration** with idempotent checkout
- **Scale**: Major stadium release — 100K seats, 1M users attempting in first hour

> **Related LLD**: [BookMyShow](../../01_LowLevelDesign/BookMyShow/BookMyShow.md) covers seat locking patterns at OOD level. This HLD focuses on **distributed inventory**, **contention**, and **payment at scale**.

---

## Requirements

### Functional Requirements

1. **Event catalog**: List events, venues, show times, pricing tiers.
2. **Seat map**: Interactive map showing available/held/booked seats.
3. **Hold seats**: User selects seats → temporary lock for checkout window.
4. **Book seats**: Payment success → confirm booking; failure/timeout → release hold.
5. **Booking history**: User views past and upcoming tickets.
6. **Waitlist** (optional): When sold out, queue for returns.

### Non-Functional Requirements

1. **Consistency**: **No double booking** — strong consistency for seat state.
2. **Latency**: Seat map load < 1s; hold operation < 500ms.
3. **Availability**: 99.99% during on-sale events.
4. **Fairness**: Queue or rate limit during flash sales.

---

## Capacity Estimation

```
Major on-sale: 1M users in first hour for 50K seats
Peak QPS: ~50K seat status reads/sec, ~5K hold attempts/sec
Hold storage: 50K concurrent holds × 500 bytes ≈ 25 MB (Redis)
Bookings DB: 50K rows per event — small; contention is the challenge

Read-heavy: 90% seat map views, 10% holds/bookings
```

---

## High-Level Architecture

```
┌──────────┐     ┌─────────────┐     ┌─────────────────────────────────┐
│  Client  │────►│ API Gateway │────►│  Booking Service                 │
│          │     │ + WAF/Queue │     │  (hold, confirm, release)        │
└──────────┘     └─────────────┘     └───────────┬─────────────────────┘
                                                   │
         ┌─────────────────────────────────────────┼──────────────────────────┐
         │                                         │                          │
┌────────▼────────┐              ┌────────────────▼────────┐    ┌───────────▼──────────┐
│ Seat Map Service │              │  Inventory Store         │    │  Payment Service      │
│ (read-optimized) │              │  Redis + PostgreSQL      │    │  (idempotent)         │
│ CDN cache tiers  │              │  seat state per event    │    │  Stripe / internal    │
└──────────────────┘              └──────────────────────────┘    └──────────────────────┘
```

---

## Seat State Machine

```
AVAILABLE → HELD (user_id, expires_at) → BOOKED (order_id)
     ↑              │
     └──────────────┘ timeout or payment failure → release to AVAILABLE
```

| State | Meaning |
|-------|---------|
| **AVAILABLE** | Can be selected |
| **HELD** | Locked for user until `expires_at` (e.g., now + 10 min) |
| **BOOKED** | Payment confirmed, permanent |

**Invariant**: A seat can be HELD or BOOKED by **at most one** user at any time.

---

## Concurrency Control — Critical Interview Topic

### Option 1: Redis Atomic Operations (Recommended for holds)

```lua
-- Lua script: atomic hold if available
if redis.call('HGET', seat_key, seat_id) == 'AVAILABLE' then
    redis.call('HSET', seat_key, seat_id, 'HELD:' .. user_id .. ':' .. expire_time)
    return 1
else
    return 0
end
```

### Option 2: PostgreSQL Row Lock

```sql
BEGIN;
SELECT * FROM seats WHERE event_id = ? AND seat_id = ? AND status = 'AVAILABLE'
FOR UPDATE SKIP LOCKED;
UPDATE seats SET status = 'HELD', held_by = ?, hold_expires = ? WHERE seat_id = ?;
COMMIT;
```

`SKIP LOCKED` — next transaction skips locked rows instead of waiting (good for queue workers).

### Option 3: Optimistic Locking

```sql
UPDATE seats SET status = 'HELD', version = version + 1
WHERE seat_id = ? AND status = 'AVAILABLE' AND version = ?
-- rows affected = 0 → someone else took it
```

**Interview answer**: "For flash sales I use Redis for atomic holds with TTL; PostgreSQL as source of truth for booked state. Redis TTL auto-releases expired holds."

---

## Hold Expiration

```
Redis key: hold:event:{eventId}:seat:{seatId}  TTL = 600 seconds
Background sweeper (or Redis TTL callback):
  On expiry → mark seat AVAILABLE in Redis + notify seat map cache
Payment timeout job runs every minute for DB consistency backup
```

---

## Booking Flow (Happy Path)

```
1. User views seat map (read from cache — mostly AVAILABLE/BOOKED, not HELD details for others)
2. User selects seats → POST /hold { seat_ids[] }
3. Booking Service atomic hold in Redis → return hold_id + expires_at
4. User enters payment → POST /checkout { hold_id, payment_token }
5. Payment Service processes (idempotency key = hold_id)
6. On success: Booking Service transitions HELD → BOOKED in DB + Redis
7. Generate ticket PDF/QR; send confirmation email
8. On failure: release hold immediately
```

---

## Flash Sale / Virtual Waiting Room

```
Before on-sale:
  Users enter waiting room queue (SQS or custom queue service)
  Token issued when admitted — rate limit holds per token

Alternative: CloudFront + WAF rate limiting per IP
Queue-it style: assign random queue position, admit N users/minute
```

Prevents thundering herd crashing booking API.

---

## Database Schema

```sql
events (id, name, venue_id, start_time, status)
venues (id, name, city, seat_map_json)
seats (id, event_id, section, row, number, price_tier, status,
       held_by, hold_expires, booked_by, order_id, version)
orders (id, user_id, event_id, total_amount, status, idempotency_key)
order_seats (order_id, seat_id, price)
users (id, email)
```

**Sharding**: By `event_id` — all seats for one event on same shard for transactional holds.

---

## Seat Map Read Optimization

```
Tier pricing colors precomputed
CDN cache seat map static layout (sections don't change)
Real-time availability: Redis bitmap or hash per event
  seat_id → status (only AVAILABLE count per section for map legend)
Client polls or WebSocket for availability updates every 5-10s during sale
```

Don't load full 50K seat state to every client — aggregate by section for overview.

---

## API Design

```
GET    /v1/events?city=&date=
GET    /v1/events/{id}/seatmap          — layout + availability summary
GET    /v1/events/{id}/seats?section=A  — detailed section availability
POST   /v1/events/{id}/hold           — { seat_ids: [...] } → hold_id
POST   /v1/checkout                     — { hold_id, payment_token, idempotency_key }
DELETE /v1/holds/{hold_id}              — user abandons checkout
GET    /v1/users/me/bookings
```

---

## Payment Integration

- **Idempotency key** = `hold_id` — duplicate checkout requests don't double-charge
- **Saga pattern**: Payment success → confirm seats; payment fail → release hold
- **Outbox pattern**: Publish `BookingConfirmed` event for email/notification service

---

## Spring Boot Reference Sketch

Focused Java 17 / Spring Boot 3.x sketch of atomic seat hold + idempotent checkout — not production-complete.

```java
@RestController
@RequestMapping("/v1/events")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/{eventId}/hold")
    public HoldResponse holdSeats(@PathVariable long eventId,
                                  @RequestBody HoldRequest request) {
        return bookingService.holdSeats(eventId, request.userId(), request.seatIds());
    }

    @PostMapping("/checkout")
    public BookingConfirmation checkout(@RequestBody CheckoutRequest request) {
        return bookingService.confirmBooking(request.holdId(), request.idempotencyKey(), request.paymentToken());
    }
}

public interface SeatInventoryRepository {
    /** Atomic CAS: AVAILABLE → HELD. Returns false if any seat unavailable. */
    boolean tryHold(long eventId, List<Long> seatIds, long userId, Instant expiresAt);
    boolean confirmHeldSeats(String holdId, long userId, String orderId);
    void releaseHold(String holdId);
}

@Service
public class BookingService {
    private static final Duration HOLD_TTL = Duration.ofMinutes(10);
    private final SeatInventoryRepository seatInventory;
    private final HoldRepository holdRepository;
    private final PaymentClient paymentClient;

    public HoldResponse holdSeats(long eventId, long userId, List<Long> seatIds) {
        Instant expiresAt = Instant.now().plus(HOLD_TTL);
        boolean acquired = seatInventory.tryHold(eventId, seatIds, userId, expiresAt);
        if (!acquired) throw new SeatUnavailableException(seatIds);

        String holdId = holdRepository.save(eventId, userId, seatIds, expiresAt);
        return new HoldResponse(holdId, expiresAt);
    }

    public BookingConfirmation confirmBooking(String holdId, String idempotencyKey, String paymentToken) {
        Hold hold = holdRepository.findById(holdId).orElseThrow();
        if (holdRepository.orderExists(idempotencyKey)) {
            return holdRepository.findOrderByIdempotencyKey(idempotencyKey); // idempotent retry
        }

        PaymentResult payment = paymentClient.charge(paymentToken, hold.totalAmount(), idempotencyKey);
        if (!payment.success()) {
            seatInventory.releaseHold(holdId);
            throw new PaymentFailedException(payment.reason());
        }

        String orderId = UUID.randomUUID().toString();
        seatInventory.confirmHeldSeats(holdId, hold.userId(), orderId);
        return holdRepository.persistOrder(orderId, hold, idempotencyKey);
    }
}

public record HoldRequest(long userId, List<Long> seatIds) {}
public record CheckoutRequest(String holdId, String idempotencyKey, String paymentToken) {}
public record HoldResponse(String holdId, Instant expiresAt) {}
```

> **Idempotency / TTL**: Redis Lua script or `FOR UPDATE SKIP LOCKED` prevents double booking. `idempotencyKey` (= hold_id) deduplicates checkout retries; holds auto-expire via Redis TTL.

---

## Interview Discussion Points

1. **How prevent double booking?** Atomic compare-and-set on seat state; single writer per seat.
2. **Redis vs DB for holds?** Redis for speed + TTL; DB for durable BOOKED state.
3. **What if Redis fails?** Fall back to DB row locks; degraded but correct.
4. **Celebrity on-sale traffic?** Waiting room, pre-scale workers, cache seat maps, shard by event.
5. **Hold timeout UX?** Show countdown timer; extend hold once if payment nearly complete (careful with abuse).

---

## Trade-offs

| Choice | Pros | Cons |
|--------|------|------|
| Redis holds | Fast atomic ops, TTL expiry | Extra consistency sync with DB |
| DB-only locking | Strong consistency simple model | Harder at 5K holds/sec |
| Section-level cache | Fast map loads | Less granular real-time UI |
| Virtual waiting room | Protects backend | User frustration, complex UX |

---

## Deep dive: Redis Lua atomic hold (full script)

```lua
-- KEYS[1] = seat hash key for event
-- ARGV[1] = seat_id, ARGV[2] = user_id, ARGV[3] = expire_timestamp

local status = redis.call('HGET', KEYS[1], ARGV[1])
if status == false or status == 'AVAILABLE' then
    redis.call('HSET', KEYS[1], ARGV[1],
        'HELD:' .. ARGV[2] .. ':' .. ARGV[3])
    redis.call('EXPIRE', KEYS[1], 7200)
    return 1
end
return 0
```

**Why Lua**: `HGET` + `HSET` must be atomic — no race between two users grabbing same seat.

See [§28 Redis](../../../01_TechGuide/28_Redis_Distributed_Caching.md) for SET NX patterns and TTL strategy.

---

## Deep dive: End-to-end flash sale timeline

```
T-30min:  CDN pre-warm seat map layout (static)
T-0:      Sale opens → waiting room admits 5K users/min
T+0:      50K QPS seat map reads (Redis bitmap per section)
T+1min:   5K hold attempts/sec → Redis Lua CAS
T+5min:   80% seats held or booked
T+10min:  TTL releases abandoned holds → seats return to pool
T+60min:  Event 95% sold out
```

---

## Deep dive: Payment saga

```
1. hold_id created (HELD state)
2. Payment authorized (idempotency_key = hold_id)
3a. Success → confirm seats BOOKED → publish BookingConfirmed
3b. Fail → release hold → AVAILABLE
3c. Timeout → Redis TTL + sweeper job
```

**Outbox pattern**: Write `orders` row + `outbox_events` in same DB transaction → relay to Kafka for email.

---

## Failure modes

| Failure | Impact | Mitigation |
|---------|--------|------------|
| Redis down | Cannot hold seats | Fallback to DB `FOR UPDATE SKIP LOCKED` |
| Double checkout click | Double charge risk | Idempotency key on payment |
| Hold TTL too short | User loses seats mid-payment | One-time 2-min extension |
| Section map stale | User sees available seat that's gone | Optimistic UI + confirm on hold response |
| Waiting room bypass | Bots scrape API | WAF, token admission, CAPTCHA |

---

## Interview walkthrough (45 min)

1. **Seat state machine** — AVAILABLE → HELD → BOOKED
2. **Concurrency** — Redis Lua vs DB locks (compare both)
3. **Hold TTL** — auto-release abandoned carts
4. **Flash sale** — waiting room, rate limits
5. **Payment** — idempotency, saga
6. **Link to LLD** — [BookMyShow](../../01_LowLevelDesign/BookMyShow/BookMyShow.md) for OOD detail

