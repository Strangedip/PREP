# BookMyShow — Online Movie Ticket Booking System (Low-Level Design)

## Problem Statement

Design an online movie ticket booking system (like BookMyShow, Fandango, or AMC Theatres) that allows users to:

- **Browse movies** currently showing in theatres across cities
- **Select a theatre and showtime** for a given movie
- **Choose seats** from an interactive seat map
- **Book and pay** for tickets with concurrency handling (no double-booking)
- **Cancel bookings** with refund policy enforcement

This is a classic LLD problem that tests your ability to model complex domains with concurrency constraints, state management, and payment integration.

---

## Requirements

### Functional Requirements

1. **City & Theatre Management**: System supports multiple cities, each with multiple theatres (cinemas), each with multiple screens (auditoriums).
2. **Movie & Show Management**: Admins can add movies, schedule shows (a movie playing on a specific screen at a specific time).
3. **Seat Selection**: Users can view available seats for a show and select one or more seats.
4. **Booking with Temporary Lock**: When a user selects seats, those seats are temporarily locked (held) for a configurable duration (e.g., 10 minutes) to prevent race conditions. If payment is not completed within that window, the lock expires and seats become available again.
5. **Payment Processing**: Integrate with a payment gateway. On successful payment, the booking is confirmed. On failure or timeout, the lock is released.
6. **Booking Cancellation**: Users can cancel confirmed bookings. Refund amount depends on cancellation policy (e.g., full refund if cancelled 4+ hours before show, 50% refund if cancelled 2-4 hours before, no refund under 2 hours).
7. **Search**: Users can search movies by name, genre, language, or city.

### Non-Functional Requirements

1. **Concurrency**: Must handle thousands of concurrent seat selection requests for popular shows without double-booking.
2. **Consistency**: Seat state must be strongly consistent — a seat is either AVAILABLE, LOCKED, or BOOKED, never in an ambiguous state.
3. **Scalability**: Support 100+ cities, 1000+ theatres, millions of daily bookings.
4. **Extensibility**: Easy to add new features (e.g., food ordering, loyalty points, reviews).

---

## Design Patterns Used

| Pattern | Where Applied | Why |
|---------|---------------|-----|
| **Strategy** | `PricingStrategy` interface | Different pricing for weekday/weekend/holiday/premium shows |
| **State** | Seat status transitions | AVAILABLE → LOCKED → BOOKED, with strict transitions |
| **Factory** | `BookingFactory` | Creates booking objects with all required associations |
| **Observer** | `NotificationService` | Sends email/SMS on booking confirmation or cancellation |
| **Singleton** | `BookMyShowService` (application entry point) | Single orchestrator for the booking flow |
| **Template Method** | `RefundPolicy` | Fixed cancellation flow with customizable refund calculation |

---

## Class Diagram

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│     City     │     │    Movie     │     │    Genre     │
├──────────────┤     ├──────────────┤     ├──────────────┤
│- id          │     │- id          │     │- name        │
│- name        │     │- title       │     └──────────────┘
│- theatres[]  │     │- description │
└──────────────┘     │- duration    │
                     │- language    │
                     │- genre       │
                     │- releaseDate │
                     └──────────────┘

┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Theatre    │     │    Screen    │     │     Seat     │
├──────────────┤     ├──────────────┤     ├──────────────┤
│- id          │     │- id          │     │- id          │
│- name        │     │- name        │     │- row         │
│- city        │     │- theatre     │     │- number      │
│- address     │     │- seats[]     │     │- seatType    │
│- screens[]   │     │- totalSeats  │     │- screen      │
└──────────────┘     └──────────────┘     └──────────────┘

┌──────────────────┐     ┌──────────────────┐
│      Show        │     │   ShowSeat       │
├──────────────────┤     ├──────────────────┤
│- id              │     │- id              │
│- movie           │     │- show            │
│- screen          │     │- seat            │
│- startTime       │     │- status          │  ← AVAILABLE | LOCKED | BOOKED
│- endTime         │     │- lockedAt        │  ← Timestamp of lock
│- pricingStrategy │     │- lockedByUser    │  ← Who locked it
│- showSeats[]     │     │- price           │
└──────────────────┘     └──────────────────┘

┌──────────────────┐     ┌──────────────────┐
│    Booking       │     │    Payment       │
├──────────────────┤     ├──────────────────┤
│- id              │     │- id              │
│- user            │     │- booking         │
│- show            │     │- amount          │
│- showSeats[]     │     │- method          │
│- totalAmount     │     │- status          │  ← PENDING | SUCCESS | FAILED | REFUNDED
│- status          │     │- transactionId   │
│- bookedAt        │     │- processedAt     │
│- payment         │     └──────────────────┘
└──────────────────┘

┌──────────────────┐
│      User        │
├──────────────────┤
│- id              │
│- name            │
│- email           │
│- phone           │
│- bookings[]      │
└──────────────────┘
```

---

## Complete Java Implementation

### Enums

```java
public enum SeatType {
    REGULAR, PREMIUM, VIP, RECLINER;
}

public enum SeatStatus {
    AVAILABLE, LOCKED, BOOKED;
}

public enum BookingStatus {
    PENDING,      // Seats locked, awaiting payment
    CONFIRMED,    // Payment successful
    CANCELLED,    // User cancelled
    EXPIRED;      // Lock timed out without payment
}

public enum PaymentStatus {
    PENDING, SUCCESS, FAILED, REFUNDED;
}

public enum PaymentMethod {
    CREDIT_CARD, DEBIT_CARD, UPI, NET_BANKING, WALLET;
}
```

### Core Domain Classes

```java
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

// ─── City ───
public class City {
    private final String id;
    private final String name;
    private final List<Theatre> theatres;

    public City(String id, String name) {
        this.id = id;
        this.name = name;
        this.theatres = new ArrayList<>();
    }

    public void addTheatre(Theatre theatre) {
        this.theatres.add(theatre);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public List<Theatre> getTheatres() { return Collections.unmodifiableList(theatres); }
}

// ─── Movie ───
public class Movie {
    private final String id;
    private final String title;
    private final String description;
    private final int durationMinutes;
    private final String language;
    private final Genre genre;
    private final LocalDateTime releaseDate;

    public Movie(String id, String title, String description, int durationMinutes,
                 String language, Genre genre, LocalDateTime releaseDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.durationMinutes = durationMinutes;
        this.language = language;
        this.genre = genre;
        this.releaseDate = releaseDate;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getDurationMinutes() { return durationMinutes; }
    public String getLanguage() { return language; }
    public Genre getGenre() { return genre; }
    public LocalDateTime getReleaseDate() { return releaseDate; }
}

public enum Genre {
    ACTION, COMEDY, DRAMA, HORROR, THRILLER, ROMANCE, SCI_FI, DOCUMENTARY;
}

// ─── Theatre ───
public class Theatre {
    private final String id;
    private final String name;
    private final City city;
    private final String address;
    private final List<Screen> screens;

    public Theatre(String id, String name, City city, String address) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.screens = new ArrayList<>();
    }

    public void addScreen(Screen screen) {
        this.screens.add(screen);
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public City getCity() { return city; }
    public String getAddress() { return address; }
    public List<Screen> getScreens() { return Collections.unmodifiableList(screens); }
}

// ─── Screen (Auditorium) ───
public class Screen {
    private final String id;
    private final String name;
    private final Theatre theatre;
    private final List<Seat> seats;

    public Screen(String id, String name, Theatre theatre) {
        this.id = id;
        this.name = name;
        this.theatre = theatre;
        this.seats = new ArrayList<>();
    }

    public void addSeat(Seat seat) {
        this.seats.add(seat);
    }

    public int getTotalSeats() {
        return seats.size();
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Theatre getTheatre() { return theatre; }
    public List<Seat> getSeats() { return Collections.unmodifiableList(seats); }
}

// ─── Seat (Physical seat in a screen — does NOT change per show) ───
public class Seat {
    private final String id;
    private final String row;       // e.g., "A", "B", "C"
    private final int number;       // e.g., 1, 2, 3
    private final SeatType seatType;
    private final Screen screen;

    public Seat(String id, String row, int number, SeatType seatType, Screen screen) {
        this.id = id;
        this.row = row;
        this.number = number;
        this.seatType = seatType;
        this.screen = screen;
    }

    public String getLabel() {
        return row + number; // e.g., "A5"
    }

    public String getId() { return id; }
    public String getRow() { return row; }
    public int getNumber() { return number; }
    public SeatType getSeatType() { return seatType; }
    public Screen getScreen() { return screen; }
}

// ─── User ───
public class User {
    private final String id;
    private final String name;
    private final String email;
    private final String phone;

    public User(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
}
```

### Show and ShowSeat (The Heart of the Problem)

The key design insight is separating the **physical seat** (exists in a screen, never changes) from the **show seat** (a seat's status for a specific show — changes per booking).

```java
// ─── Show ───
public class Show {
    private final String id;
    private final Movie movie;
    private final Screen screen;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final PricingStrategy pricingStrategy;
    private final Map<String, ShowSeat> showSeats; // seatId -> ShowSeat

    public Show(String id, Movie movie, Screen screen,
                LocalDateTime startTime, PricingStrategy pricingStrategy) {
        this.id = id;
        this.movie = movie;
        this.screen = screen;
        this.startTime = startTime;
        this.endTime = startTime.plusMinutes(movie.getDurationMinutes());
        this.pricingStrategy = pricingStrategy;
        this.showSeats = new ConcurrentHashMap<>();

        // Initialize a ShowSeat for every physical seat in the screen
        for (Seat seat : screen.getSeats()) {
            double price = pricingStrategy.calculatePrice(seat.getSeatType());
            ShowSeat showSeat = new ShowSeat(
                UUID.randomUUID().toString(), this, seat, price
            );
            showSeats.put(seat.getId(), showSeat);
        }
    }

    public ShowSeat getShowSeat(String seatId) {
        return showSeats.get(seatId);
    }

    public List<ShowSeat> getAvailableSeats() {
        return showSeats.values().stream()
                .filter(ss -> ss.getStatus() == SeatStatus.AVAILABLE)
                .toList();
    }

    public List<ShowSeat> getAllShowSeats() {
        return new ArrayList<>(showSeats.values());
    }

    // Getters
    public String getId() { return id; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public PricingStrategy getPricingStrategy() { return pricingStrategy; }
}

// ─── ShowSeat (status of a specific seat for a specific show) ───
// THIS IS THE CRITICAL CLASS — must be thread-safe
public class ShowSeat {
    private final String id;
    private final Show show;
    private final Seat seat;
    private final double price;

    private volatile SeatStatus status;
    private volatile LocalDateTime lockedAt;
    private volatile String lockedByUserId;

    private final ReentrantLock lock = new ReentrantLock();

    private static final int LOCK_TIMEOUT_MINUTES = 10;

    public ShowSeat(String id, Show show, Seat seat, double price) {
        this.id = id;
        this.show = show;
        this.seat = seat;
        this.price = price;
        this.status = SeatStatus.AVAILABLE;
    }

    /**
     * Attempt to lock this seat for a user.
     * Returns true if lock acquired, false if seat is not available.
     *
     * Thread-safety: Uses ReentrantLock to ensure only one thread
     * can transition the seat from AVAILABLE to LOCKED.
     */
    public boolean tryLock(String userId) {
        lock.lock();
        try {
            // Check if a previous lock has expired
            if (status == SeatStatus.LOCKED && isLockExpired()) {
                // Release expired lock
                status = SeatStatus.AVAILABLE;
                lockedAt = null;
                lockedByUserId = null;
            }

            if (status != SeatStatus.AVAILABLE) {
                return false;
            }

            status = SeatStatus.LOCKED;
            lockedAt = LocalDateTime.now();
            lockedByUserId = userId;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Confirm the booking (transition LOCKED → BOOKED).
     * Only the user who locked it can confirm.
     */
    public boolean confirmBooking(String userId) {
        lock.lock();
        try {
            if (status != SeatStatus.LOCKED) {
                return false;
            }
            if (!userId.equals(lockedByUserId)) {
                return false;
            }
            if (isLockExpired()) {
                // Lock expired, release it
                status = SeatStatus.AVAILABLE;
                lockedAt = null;
                lockedByUserId = null;
                return false;
            }

            status = SeatStatus.BOOKED;
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Release the lock (user cancelled before payment, or lock expired).
     */
    public void releaseLock(String userId) {
        lock.lock();
        try {
            if (status == SeatStatus.LOCKED && userId.equals(lockedByUserId)) {
                status = SeatStatus.AVAILABLE;
                lockedAt = null;
                lockedByUserId = null;
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Cancel a confirmed booking (refund scenario).
     */
    public void cancelBooking() {
        lock.lock();
        try {
            if (status == SeatStatus.BOOKED) {
                status = SeatStatus.AVAILABLE;
                lockedAt = null;
                lockedByUserId = null;
            }
        } finally {
            lock.unlock();
        }
    }

    private boolean isLockExpired() {
        return lockedAt != null &&
               lockedAt.plusMinutes(LOCK_TIMEOUT_MINUTES).isBefore(LocalDateTime.now());
    }

    // Getters
    public String getId() { return id; }
    public Show getShow() { return show; }
    public Seat getSeat() { return seat; }
    public double getPrice() { return price; }
    public SeatStatus getStatus() { return status; }
    public LocalDateTime getLockedAt() { return lockedAt; }
    public String getLockedByUserId() { return lockedByUserId; }
}
```

### Pricing Strategy (Strategy Pattern)

```java
public interface PricingStrategy {
    double calculatePrice(SeatType seatType);
}

public class WeekdayPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(SeatType seatType) {
        return switch (seatType) {
            case REGULAR  -> 200.0;
            case PREMIUM  -> 350.0;
            case VIP      -> 500.0;
            case RECLINER -> 700.0;
        };
    }
}

public class WeekendPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(SeatType seatType) {
        return switch (seatType) {
            case REGULAR  -> 300.0;
            case PREMIUM  -> 500.0;
            case VIP      -> 750.0;
            case RECLINER -> 1000.0;
        };
    }
}

public class HolidayPricingStrategy implements PricingStrategy {
    @Override
    public double calculatePrice(SeatType seatType) {
        return switch (seatType) {
            case REGULAR  -> 400.0;
            case PREMIUM  -> 600.0;
            case VIP      -> 900.0;
            case RECLINER -> 1200.0;
        };
    }
}
```

### Booking and Payment

```java
// ─── Booking ───
public class Booking {
    private final String id;
    private final User user;
    private final Show show;
    private final List<ShowSeat> showSeats;
    private final double totalAmount;
    private BookingStatus status;
    private final LocalDateTime createdAt;
    private Payment payment;

    public Booking(String id, User user, Show show,
                   List<ShowSeat> showSeats, double totalAmount) {
        this.id = id;
        this.user = user;
        this.show = show;
        this.showSeats = Collections.unmodifiableList(showSeats);
        this.totalAmount = totalAmount;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void confirm(Payment payment) {
        this.status = BookingStatus.CONFIRMED;
        this.payment = payment;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public void expire() {
        this.status = BookingStatus.EXPIRED;
    }

    // Getters
    public String getId() { return id; }
    public User getUser() { return user; }
    public Show getShow() { return show; }
    public List<ShowSeat> getShowSeats() { return showSeats; }
    public double getTotalAmount() { return totalAmount; }
    public BookingStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Payment getPayment() { return payment; }
}

// ─── Payment ───
public class Payment {
    private final String id;
    private final Booking booking;
    private final double amount;
    private final PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime processedAt;

    public Payment(String id, Booking booking, double amount, PaymentMethod method) {
        this.id = id;
        this.booking = booking;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
    }

    public void markSuccess(String transactionId) {
        this.status = PaymentStatus.SUCCESS;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }

    public void markFailed() {
        this.status = PaymentStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    // Getters
    public String getId() { return id; }
    public Booking getBooking() { return booking; }
    public double getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getProcessedAt() { return processedAt; }
}
```

### Refund Policy (Template Method Pattern)

```java
public abstract class RefundPolicy {
    /**
     * Template method: fixed flow for processing refunds.
     * Subclasses customize the refund percentage calculation.
     */
    public final double processRefund(Booking booking) {
        long hoursBeforeShow = java.time.Duration.between(
            LocalDateTime.now(), booking.getShow().getStartTime()
        ).toHours();

        double refundPercentage = calculateRefundPercentage(hoursBeforeShow);
        return booking.getTotalAmount() * (refundPercentage / 100.0);
    }

    protected abstract double calculateRefundPercentage(long hoursBeforeShow);
}

public class StandardRefundPolicy extends RefundPolicy {
    @Override
    protected double calculateRefundPercentage(long hoursBeforeShow) {
        if (hoursBeforeShow >= 4) {
            return 100.0; // Full refund
        } else if (hoursBeforeShow >= 2) {
            return 50.0;  // 50% refund
        } else {
            return 0.0;   // No refund
        }
    }
}

public class PremiumRefundPolicy extends RefundPolicy {
    @Override
    protected double calculateRefundPercentage(long hoursBeforeShow) {
        if (hoursBeforeShow >= 2) {
            return 100.0; // Full refund for premium users
        } else if (hoursBeforeShow >= 1) {
            return 75.0;
        } else {
            return 25.0;
        }
    }
}
```

### Payment Gateway (Adapter Pattern)

```java
public interface PaymentGateway {
    PaymentResult processPayment(double amount, PaymentMethod method, String userId);
    PaymentResult processRefund(String transactionId, double amount);
}

public record PaymentResult(boolean success, String transactionId, String message) {}

public class StripePaymentGateway implements PaymentGateway {
    @Override
    public PaymentResult processPayment(double amount, PaymentMethod method, String userId) {
        // In real implementation, this calls Stripe API
        // Simulating success
        String txnId = "stripe_txn_" + UUID.randomUUID().toString().substring(0, 8);
        return new PaymentResult(true, txnId, "Payment processed via Stripe");
    }

    @Override
    public PaymentResult processRefund(String transactionId, double amount) {
        return new PaymentResult(true, transactionId, "Refund of " + amount + " processed");
    }
}

public class RazorpayPaymentGateway implements PaymentGateway {
    @Override
    public PaymentResult processPayment(double amount, PaymentMethod method, String userId) {
        String txnId = "rzp_txn_" + UUID.randomUUID().toString().substring(0, 8);
        return new PaymentResult(true, txnId, "Payment processed via Razorpay");
    }

    @Override
    public PaymentResult processRefund(String transactionId, double amount) {
        return new PaymentResult(true, transactionId, "Refund processed via Razorpay");
    }
}
```

### Notification Service (Observer Pattern)

```java
public interface BookingObserver {
    void onBookingConfirmed(Booking booking);
    void onBookingCancelled(Booking booking, double refundAmount);
}

public class EmailNotificationService implements BookingObserver {
    @Override
    public void onBookingConfirmed(Booking booking) {
        System.out.printf("[EMAIL] Booking confirmed for %s. Movie: %s, Seats: %s, Amount: %.2f%n",
            booking.getUser().getEmail(),
            booking.getShow().getMovie().getTitle(),
            booking.getShowSeats().stream()
                .map(ss -> ss.getSeat().getLabel())
                .toList(),
            booking.getTotalAmount());
    }

    @Override
    public void onBookingCancelled(Booking booking, double refundAmount) {
        System.out.printf("[EMAIL] Booking %s cancelled for %s. Refund: %.2f%n",
            booking.getId(), booking.getUser().getEmail(), refundAmount);
    }
}

public class SmsNotificationService implements BookingObserver {
    @Override
    public void onBookingConfirmed(Booking booking) {
        System.out.printf("[SMS] Booking confirmed. Sent to %s%n",
            booking.getUser().getPhone());
    }

    @Override
    public void onBookingCancelled(Booking booking, double refundAmount) {
        System.out.printf("[SMS] Booking cancelled. Refund: %.2f. Sent to %s%n",
            refundAmount, booking.getUser().getPhone());
    }
}
```

### BookMyShow Service (Orchestrator)

```java
public class BookMyShowService {
    private final Map<String, City> cities = new ConcurrentHashMap<>();
    private final Map<String, Movie> movies = new ConcurrentHashMap<>();
    private final Map<String, Show> shows = new ConcurrentHashMap<>();
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();

    private final PaymentGateway paymentGateway;
    private final RefundPolicy refundPolicy;
    private final List<BookingObserver> observers = new ArrayList<>();

    public BookMyShowService(PaymentGateway paymentGateway, RefundPolicy refundPolicy) {
        this.paymentGateway = paymentGateway;
        this.refundPolicy = refundPolicy;
    }

    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }

    // ─── Admin Operations ───

    public City addCity(String id, String name) {
        City city = new City(id, name);
        cities.put(id, city);
        return city;
    }

    public Theatre addTheatre(String id, String name, String cityId, String address) {
        City city = cities.get(cityId);
        if (city == null) throw new IllegalArgumentException("City not found: " + cityId);
        Theatre theatre = new Theatre(id, name, city, address);
        city.addTheatre(theatre);
        return theatre;
    }

    public Movie addMovie(String id, String title, String description, int durationMinutes,
                          String language, Genre genre, LocalDateTime releaseDate) {
        Movie movie = new Movie(id, title, description, durationMinutes, language, genre, releaseDate);
        movies.put(id, movie);
        return movie;
    }

    public Show addShow(String id, String movieId, Screen screen,
                        LocalDateTime startTime, PricingStrategy pricingStrategy) {
        Movie movie = movies.get(movieId);
        if (movie == null) throw new IllegalArgumentException("Movie not found: " + movieId);
        Show show = new Show(id, movie, screen, startTime, pricingStrategy);
        shows.put(id, show);
        return show;
    }

    // ─── User Operations ───

    public void registerUser(User user) {
        users.put(user.getId(), user);
    }

    /**
     * Search movies playing in a city.
     */
    public List<Movie> searchMovies(String cityId, String query) {
        City city = cities.get(cityId);
        if (city == null) return Collections.emptyList();

        Set<String> movieIds = new HashSet<>();
        for (Theatre theatre : city.getTheatres()) {
            for (Screen screen : theatre.getScreens()) {
                for (Show show : getShowsForScreen(screen)) {
                    Movie m = show.getMovie();
                    if (m.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                        m.getGenre().name().toLowerCase().contains(query.toLowerCase()) ||
                        m.getLanguage().toLowerCase().contains(query.toLowerCase())) {
                        movieIds.add(m.getId());
                    }
                }
            }
        }

        return movieIds.stream().map(movies::get).toList();
    }

    private List<Show> getShowsForScreen(Screen screen) {
        return shows.values().stream()
                .filter(s -> s.getScreen().getId().equals(screen.getId()))
                .toList();
    }

    /**
     * Get available seats for a show.
     */
    public List<ShowSeat> getAvailableSeats(String showId) {
        Show show = shows.get(showId);
        if (show == null) throw new IllegalArgumentException("Show not found: " + showId);
        return show.getAvailableSeats();
    }

    /**
     * CORE BOOKING FLOW:
     * 1. Lock selected seats
     * 2. Process payment
     * 3. Confirm booking (or release locks on failure)
     */
    public Booking bookSeats(String userId, String showId, List<String> seatIds,
                             PaymentMethod paymentMethod) {
        User user = users.get(userId);
        if (user == null) throw new IllegalArgumentException("User not found: " + userId);

        Show show = shows.get(showId);
        if (show == null) throw new IllegalArgumentException("Show not found: " + showId);

        // Step 1: Try to lock all requested seats
        List<ShowSeat> lockedSeats = new ArrayList<>();
        try {
            for (String seatId : seatIds) {
                ShowSeat showSeat = show.getShowSeat(seatId);
                if (showSeat == null) {
                    throw new IllegalArgumentException("Seat not found: " + seatId);
                }

                boolean locked = showSeat.tryLock(userId);
                if (!locked) {
                    // Seat not available — roll back all previously locked seats
                    throw new SeatNotAvailableException(
                        "Seat " + showSeat.getSeat().getLabel() + " is not available"
                    );
                }
                lockedSeats.add(showSeat);
            }

            // Step 2: Calculate total and process payment
            double totalAmount = lockedSeats.stream()
                    .mapToDouble(ShowSeat::getPrice)
                    .sum();

            String bookingId = UUID.randomUUID().toString();
            Booking booking = new Booking(bookingId, user, show, lockedSeats, totalAmount);
            Payment payment = new Payment(
                UUID.randomUUID().toString(), booking, totalAmount, paymentMethod
            );

            PaymentResult result = paymentGateway.processPayment(
                totalAmount, paymentMethod, userId
            );

            if (!result.success()) {
                payment.markFailed();
                throw new PaymentFailedException("Payment failed: " + result.message());
            }

            // Step 3: Confirm booking
            payment.markSuccess(result.transactionId());

            for (ShowSeat showSeat : lockedSeats) {
                boolean confirmed = showSeat.confirmBooking(userId);
                if (!confirmed) {
                    // Lock expired during payment — extremely rare edge case
                    // In production: refund the payment automatically
                    throw new BookingFailedException(
                        "Lock expired during payment for seat " + showSeat.getSeat().getLabel()
                    );
                }
            }

            booking.confirm(payment);
            bookings.put(bookingId, booking);

            // Notify observers
            for (BookingObserver observer : observers) {
                observer.onBookingConfirmed(booking);
            }

            return booking;

        } catch (Exception e) {
            // Roll back: release all locks on any failure
            for (ShowSeat lockedSeat : lockedSeats) {
                lockedSeat.releaseLock(userId);
            }
            throw e;
        }
    }

    /**
     * Cancel a confirmed booking with refund calculation.
     */
    public double cancelBooking(String bookingId, String userId) {
        Booking booking = bookings.get(bookingId);
        if (booking == null) throw new IllegalArgumentException("Booking not found: " + bookingId);
        if (!booking.getUser().getId().equals(userId))
            throw new IllegalArgumentException("User does not own this booking");
        if (booking.getStatus() != BookingStatus.CONFIRMED)
            throw new IllegalStateException("Only confirmed bookings can be cancelled");

        // Calculate refund
        double refundAmount = refundPolicy.processRefund(booking);

        // Process refund via gateway
        if (refundAmount > 0) {
            paymentGateway.processRefund(
                booking.getPayment().getTransactionId(), refundAmount
            );
            booking.getPayment().markRefunded();
        }

        // Release all seats
        for (ShowSeat showSeat : booking.getShowSeats()) {
            showSeat.cancelBooking();
        }

        booking.cancel();

        // Notify observers
        for (BookingObserver observer : observers) {
            observer.onBookingCancelled(booking, refundAmount);
        }

        return refundAmount;
    }
}
```

### Custom Exceptions

```java
public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(String message) {
        super(message);
    }
}

public class PaymentFailedException extends RuntimeException {
    public PaymentFailedException(String message) {
        super(message);
    }
}

public class BookingFailedException extends RuntimeException {
    public BookingFailedException(String message) {
        super(message);
    }
}
```

---

## How Concurrency Is Handled

This is the most critical aspect of this design. Here is the concurrency strategy:

| Concern | Solution |
|---------|----------|
| Two users selecting the same seat simultaneously | `ReentrantLock` inside `ShowSeat.tryLock()` ensures only one thread transitions a seat from AVAILABLE to LOCKED |
| Lock expiry (user abandons checkout) | `ShowSeat.tryLock()` checks `isLockExpired()` before acquiring — stale locks are automatically released |
| Payment takes too long | Lock has a configurable timeout (`LOCK_TIMEOUT_MINUTES`). If payment exceeds this, the seat reverts to AVAILABLE |
| Payment succeeds but lock expired | Extremely rare. In production, trigger automatic refund via a scheduled reconciliation job |
| Rollback on partial failure | If any seat in a multi-seat booking fails to lock, all previously locked seats are released in the `catch` block |

### Why Not `synchronized`?

Using `synchronized` on the entire method would:
1. Block all threads on a single monitor, even if they are booking different seats.
2. Create a bottleneck for popular shows.

Using `ReentrantLock` per `ShowSeat` means locking is **per-seat granular** — two users booking different seats in the same show never block each other.

---

## Testing Strategy

```java
@Test
void testConcurrentSeatBooking() throws Exception {
    // Setup: Create a show with 100 seats
    // Action: Launch 50 threads, each trying to book the same seat
    // Assert: Exactly 1 thread succeeds, 49 get SeatNotAvailableException

    ExecutorService executor = Executors.newFixedThreadPool(50);
    ShowSeat showSeat = /* ... */;
    AtomicInteger successCount = new AtomicInteger(0);
    AtomicInteger failCount = new AtomicInteger(0);

    CountDownLatch latch = new CountDownLatch(50);

    for (int i = 0; i < 50; i++) {
        final String userId = "user-" + i;
        executor.submit(() -> {
            try {
                boolean locked = showSeat.tryLock(userId);
                if (locked) successCount.incrementAndGet();
                else failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });
    }

    latch.await();
    assertEquals(1, successCount.get());
    assertEquals(49, failCount.get());
}

@Test
void testLockExpiry() throws Exception {
    // Setup: Lock a seat with 0-minute timeout (or mock time)
    // Wait for lock to expire
    // Assert: Another user can now lock the same seat
}

@Test
void testBookingRollbackOnPaymentFailure() {
    // Setup: Configure mock payment gateway to fail
    // Action: Attempt to book 3 seats
    // Assert: All 3 seats are released back to AVAILABLE
}

@Test
void testRefundCalculation() {
    // Setup: Create a booking for a show 3 hours from now
    // Action: Cancel with StandardRefundPolicy
    // Assert: 50% refund (between 2-4 hours)
}
```

---

## Interview Discussion Points

### Design Decisions

1. **Why separate `Seat` from `ShowSeat`?**
   - A `Seat` is a physical entity (row A, number 5, VIP type) that never changes. A `ShowSeat` is the booking status of that seat for a specific show. This separation follows the **Single Responsibility Principle** and allows the same physical seat to have different statuses across different shows.

2. **Why `ReentrantLock` per seat instead of database row-level locking?**
   - For an LLD interview, we use in-memory locks to demonstrate concurrency understanding. In production, you would use database-level locking (`SELECT ... FOR UPDATE`) or Redis distributed locks (`SETNX`) for multi-instance deployments.

3. **Why the Strategy pattern for pricing?**
   - Different shows may have different pricing (weekday vs weekend vs holiday vs premiere). The Strategy pattern allows the pricing algorithm to vary independently of the show scheduling logic, following the **Open/Closed Principle**.

4. **Why the Template Method for refund policy?**
   - The refund flow (calculate hours remaining, determine percentage, compute refund amount) is fixed. Only the percentage calculation varies by policy type. Template Method captures this "fixed structure, variable steps" pattern.

### Scalability Considerations

1. **Database Design**: In production, `ShowSeat` status would be a database column with row-level locking (`SELECT ... FOR UPDATE SKIP LOCKED` in PostgreSQL).
2. **Distributed Locking**: For multi-instance deployments, use Redis `SETNX` with TTL for seat locks instead of in-memory `ReentrantLock`.
3. **Event Sourcing**: For high-traffic shows (e.g., opening night of a blockbuster), consider event sourcing where each seat state change is an event, enabling replay and audit.
4. **Caching**: Cache show and seat availability in Redis, invalidated on booking/cancellation events.
5. **Queue-based booking**: For extremely popular shows, put booking requests in a queue and process sequentially to avoid thundering herd on the database.

