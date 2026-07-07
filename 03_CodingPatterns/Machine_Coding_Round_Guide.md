# Machine Coding Round Guide — Timed OOD + Spring Boot

> **You are here**: SDE1 — Interview Prep (machine coding)
> **Depth**: Standard (process + full 90-minute worked example)
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [GoF Patterns](01_Patterns.md), [Parking Lot LLD](../04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) | **Next**: [BookMyShow LLD](../04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md)

---

## What interviewers test

Common at **Flipkart, Swiggy, Razorpay, PhonePe**, and many India product companies (90–120 minutes):

| Dimension | Weight | Evidence |
|-----------|--------|----------|
| Working code | High | Compiles, core flow runs |
| OOD / patterns | High | Clear classes, justified patterns |
| API design | Medium | REST endpoints, sensible DTOs |
| Tests | Medium | At least 2–3 unit tests on core logic |
| Production touches | Low–Med | Exception handling, validation, logging |

Not the same as **LeetCode** (see [Algorithmic Patterns](02_AlgorithmicPatterns.md)) or **whiteboard HLD** (see [HLD Template](../04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md)).

---

## 90-minute time budget

| Minutes | Activity |
|---------|----------|
| 0–10 | Clarify requirements; list entities and APIs on paper |
| 10–25 | Class diagram + pattern choice; agree scope cuts with interviewer |
| 25–70 | Implement happy path first (create → main operation → query) |
| 70–85 | Edge cases + 2–3 tests |
| 85–90 | Demo + mention extensions |

**Rule**: Happy path before perfection. A working parking assign beats a perfect diagram with no code.

---

## Scope negotiation script

When requirements are large, say:

> "In 90 minutes I'll implement: entity model, core booking/assign API, in-memory or H2 repository, and unit tests. I'll stub payment/notifications and describe them verbally."

Interviewers expect cuts — proposing them shows senior judgment.

---

## Spring Boot starter structure

```
src/main/java/com/example/demo/
  controller/     # REST only — thin
  service/        # business logic
  domain/         # entities, enums
  repository/     # JPA or in-memory
  dto/            # request/response
  exception/      # @ControllerAdvice
src/test/java/    # @WebMvcTest or service tests
```

| Layer | Do | Don't |
|-------|-----|-------|
| Controller | Validate input, map DTOs | Business rules |
| Service | Transactions, patterns | HTTP concerns |
| Repository | Persistence | Complex joins in controller |

---

## Pattern → problem mapping

| Problem type | Pattern | Repo example |
|--------------|---------|--------------|
| Vending / ATM / turn-based game | State | [VendingMachine](../04_SystemDesign/01_LowLevelDesign/VendingMachine/VendingMachine.md) |
| Parking / pricing tiers | Strategy | [ParkingLot](../04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) |
| Seat / room booking | Lock + inventory | [BookMyShow](../04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md) |
| Singleton resource | Singleton (careful in Spring) | ParkingLot system |

---

## Full worked example: Movie Ticket Booking (90 min)

This walkthrough mirrors a common machine coding prompt: **book a seat, view bookings, cancel with rules**.

### Minutes 0–10: Clarify requirements

**Ask the interviewer:**

| Question | Answer (assume) |
|----------|-----------------|
| Single theatre or multiple? | Single theatre, multiple shows |
| Seat layout? | Fixed rows × seats per row |
| Concurrent booking? | Two users cannot book same seat |
| Persistence? | In-memory OK; H2 if time |
| Payment? | Stub — mark CONFIRMED on book |

**Write scope on paper:**

```
IN SCOPE:  Show listing, seat map, book seat, cancel booking, list my bookings
OUT OF SCOPE: Payment gateway, notifications, admin panel
```

### Minutes 10–25: Design

**Entities:**

```
Show(id, movieName, startTime)
Seat(id, row, number, status: AVAILABLE|HELD|BOOKED)
Booking(id, showId, seatId, userId, status: CONFIRMED|CANCELLED)
```

**APIs:**

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/shows` | List shows |
| GET | `/shows/{id}/seats` | Seat map with status |
| POST | `/bookings` | Book a seat |
| DELETE | `/bookings/{id}` | Cancel booking |
| GET | `/users/{userId}/bookings` | User's bookings |

**Pattern choice**: **Strategy** not needed; use **synchronized service method** or DB unique constraint for concurrency.

**Class diagram (simplified):**

```
BookingController → BookingService → BookingRepository
                                   → SeatRepository
                                   → ShowRepository
```

### Minutes 25–55: Implement happy path

**Domain — Seat.java**

```java
public class Seat {
    private Long id;
    private String row;
    private int number;
    private SeatStatus status; // AVAILABLE, BOOKED

    public boolean isAvailable() { return status == SeatStatus.AVAILABLE; }
    public void book() { this.status = SeatStatus.BOOKED; }
    public void release() { this.status = SeatStatus.AVAILABLE; }
}
```

**Service — core logic with concurrency guard**

```java
@Service
public class BookingService {
    private final SeatRepository seatRepo;
    private final BookingRepository bookingRepo;

    public synchronized BookingResponse book(BookingRequest req) {
        Seat seat = seatRepo.findByShowIdAndSeatId(req.showId(), req.seatId())
            .orElseThrow(() -> new NotFoundException("Seat not found"));

        if (!seat.isAvailable()) {
            throw new ConflictException("Seat already booked");
        }

        seat.book();
        seatRepo.save(seat);

        Booking booking = new Booking(req.showId(), seat.getId(), req.userId());
        bookingRepo.save(booking);

        return BookingResponse.from(booking);
    }
}
```

**Controller — thin**

```java
@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingResponse book(@Valid @RequestBody BookingRequest req) {
        return bookingService.book(req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id, @RequestParam String userId) {
        bookingService.cancel(id, userId);
    }
}
```

**Global exception handler**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(ConflictException ex) {
        return Map.of("error", ex.getMessage());
    }
}
```

### Minutes 55–70: Cancel flow + seed data

```java
public void cancel(Long bookingId, String userId) {
    Booking booking = bookingRepo.findById(bookingId)
        .orElseThrow(() -> new NotFoundException("Booking not found"));
    if (!booking.getUserId().equals(userId)) {
        throw new ForbiddenException("Not your booking");
    }
    Seat seat = seatRepo.findById(booking.getSeatId()).orElseThrow();
    seat.release();
    seatRepo.save(seat);
    booking.cancel();
    bookingRepo.save(booking);
}
```

**DataLoader** (optional): seed 1 show, 20 seats — saves demo time.

### Minutes 70–85: Tests

```java
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock SeatRepository seatRepo;
    @Mock BookingRepository bookingRepo;
    @InjectMocks BookingService service;

    @Test
    void book_success_whenSeatAvailable() {
        Seat seat = new Seat(1L, "A", 1, SeatStatus.AVAILABLE);
        when(seatRepo.findByShowIdAndSeatId(1L, 1L)).thenReturn(Optional.of(seat));

        BookingResponse resp = service.book(new BookingRequest(1L, 1L, "user1"));

        assertEquals("CONFIRMED", resp.status());
        verify(seatRepo).save(argThat(s -> s.getStatus() == SeatStatus.BOOKED));
    }

    @Test
    void book_throwsConflict_whenSeatAlreadyBooked() {
        Seat seat = new Seat(1L, "A", 1, SeatStatus.BOOKED);
        when(seatRepo.findByShowIdAndSeatId(1L, 1L)).thenReturn(Optional.of(seat));

        assertThrows(ConflictException.class,
            () -> service.book(new BookingRequest(1L, 1L, "user1")));
    }
}
```

### Minutes 85–90: Demo + extensions (verbal)

> "I'd add: optimistic locking with `@Version` on Seat, Redis hold with TTL for seat selection, Kafka event `BookingConfirmed`, and integration test with `@SpringBootTest`."

---

## Trade-off: in-memory vs H2 vs mocked DB

| Choice | Pros | Cons | Interview default |
|--------|------|------|-------------------|
| `HashMap` repository | Fastest to write | No query practice | Time-critical |
| H2 + JPA | Shows real persistence | Setup time | **Recommended** if you know JPA |
| Mockito-only service tests | Fast tests | Skips integration | Pair with 1 `@WebMvcTest` |

---

## Common failure modes

| Failure | Prevention |
|---------|------------|
| No code in last 30 min | Implement create + one read by minute 40 |
| God class `ApplicationService` | Split by aggregate (OrderService, InventoryService) |
| No validation | `@NotNull`, `@Positive` on DTOs |
| Ignoring concurrency | Mention `synchronized` or DB unique constraint for inventory |
| No extension discussion | Prepare 1-minute "I'd add Kafka for events, Redis for holds" |

---

## Practice problems in this repo (in order)

1. [ParkingLot](../04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) — 90 min baseline
2. [VendingMachine](../04_SystemDesign/01_LowLevelDesign/VendingMachine/VendingMachine.md) — State pattern
3. [BookMyShow](../04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md) — concurrency
4. [HotelBooking](../04_SystemDesign/01_LowLevelDesign/HotelBooking/HotelBooking.md) — date-range overlap

Time yourself with IDE only (no Copilot) once per problem before the real round.
