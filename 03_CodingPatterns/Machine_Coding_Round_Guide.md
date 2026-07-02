# Machine Coding Round Guide — Timed OOD + Spring Boot

> **You are here**: SDE1 — Interview Prep (machine coding)
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [GoF Patterns](01_Patterns.md), [Parking Lot LLD](../04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) | **Next**: [BookMyShow LLD](../04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md)

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

## Trade-off: in-memory vs H2 vs mocked DB

| Choice | Pros | Cons | Interview default |
|--------|------|------|-------------------|
| `HashMap` repository | Fastest to write | No query practice | Time-critical |
| H2 + JPA | Shows real persistence | Setup time | **Recommended** if you know JPA |
| Mockito-only service tests | Fast tests | Skips integration | Pair with 1 `@WebMvcTest` |

---

## Minimum test examples

```java
@Test
void bookLastSpot_throwsWhenFull() {
    assertThrows(NoSpotAvailableException.class, () -> service.book(lastSpotId));
}

@Test
void cancelBooking_releasesSpot() {
    Booking b = service.book(spotId);
    service.cancel(b.getId());
    assertTrue(spotRepository.findById(spotId).get().isAvailable());
}
```

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
