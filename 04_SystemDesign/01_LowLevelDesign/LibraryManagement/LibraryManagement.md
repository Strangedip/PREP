# Library Management System — Low-Level Design

> **You are here**: SDE1–SDE2 — System Design (LLD)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [LLD_Template.md](../../00_Templates/LLD_Template/LLD_Template.md) | **Next**: [README.md](../../README.md)

## Problem Statement

Design a Library Management System that supports:

- **Book Management**: Add, remove, search books by title, author, ISBN, or subject
- **Member Management**: Register members, track borrowing history
- **Borrowing System**: Issue books, return books, handle due dates and fines
- **Reservation**: Reserve a book that is currently checked out
- **Rack Management**: Track physical location of books in the library
- **Fine Calculation**: Automatic fine calculation for overdue books

---

## Design Patterns Used

| Pattern | Where | Why |
|---------|-------|-----|
| **Strategy** | `FineCalculationStrategy` | Different fine policies (flat rate, progressive, no fine for first-timers) |
| **Observer** | `NotificationService` | Notify members when reserved book is available |
| **Factory** | `BookItemFactory` | Create book items with auto-generated barcodes |
| **Singleton** | `Library` | Single library system instance |
| **State** | `BookItem` status | AVAILABLE → LOANED → RESERVED, with rules per state |

---

## Complete Java Implementation

### Enums

```java
public enum BookStatus {
    AVAILABLE,    // On the shelf, ready to borrow
    LOANED,       // Checked out by a member
    RESERVED,     // Available but reserved for a specific member
    LOST,         // Reported lost
    UNDER_REPAIR; // Damaged, being repaired
}

public enum MemberStatus {
    ACTIVE, SUSPENDED, CLOSED;
}

public enum ReservationStatus {
    WAITING,     // Book still checked out by someone else
    READY,       // Book returned, waiting for reserved member to pick up
    COMPLETED,   // Member picked up the book
    CANCELLED;   // Reservation cancelled
}
```

### Core Domain Classes

```java
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

// ─── Book (Catalog entry — title, author, ISBN) ───
public class Book {
    private final String isbn;
    private final String title;
    private final String author;
    private final String subject;
    private final int publicationYear;
    private final List<BookItem> copies; // Physical copies of this book

    public Book(String isbn, String title, String author,
                String subject, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.subject = subject;
        this.publicationYear = publicationYear;
        this.copies = new ArrayList<>();
    }

    public void addCopy(BookItem copy) { copies.add(copy); }

    public long getAvailableCopies() {
        return copies.stream().filter(c -> c.getStatus() == BookStatus.AVAILABLE).count();
    }

    // Getters
    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getSubject() { return subject; }
    public int getPublicationYear() { return publicationYear; }
    public List<BookItem> getCopies() { return Collections.unmodifiableList(copies); }
}

// ─── BookItem (Physical copy of a book with a barcode) ───
public class BookItem {
    private final String barcode;
    private final Book book;
    private final String rackLocation; // e.g., "Shelf-A3-Row2"
    private BookStatus status;
    private LocalDate dueDate;
    private Member borrowedBy;

    public BookItem(String barcode, Book book, String rackLocation) {
        this.barcode = barcode;
        this.book = book;
        this.rackLocation = rackLocation;
        this.status = BookStatus.AVAILABLE;
    }

    public void checkOut(Member member, int loanDays) {
        if (status != BookStatus.AVAILABLE && status != BookStatus.RESERVED) {
            throw new IllegalStateException("Book is not available for checkout. Status: " + status);
        }
        this.borrowedBy = member;
        this.dueDate = LocalDate.now().plusDays(loanDays);
        this.status = BookStatus.LOANED;
    }

    public void returnBook() {
        this.borrowedBy = null;
        this.dueDate = null;
        this.status = BookStatus.AVAILABLE;
    }

    public void markReserved() {
        this.status = BookStatus.RESERVED;
    }

    public void markLost() {
        this.status = BookStatus.LOST;
    }

    public boolean isOverdue() {
        return status == BookStatus.LOANED && dueDate != null && LocalDate.now().isAfter(dueDate);
    }

    public long getOverdueDays() {
        if (!isOverdue()) return 0;
        return ChronoUnit.DAYS.between(dueDate, LocalDate.now());
    }

    // Getters
    public String getBarcode() { return barcode; }
    public Book getBook() { return book; }
    public String getRackLocation() { return rackLocation; }
    public BookStatus getStatus() { return status; }
    public LocalDate getDueDate() { return dueDate; }
    public Member getBorrowedBy() { return borrowedBy; }
}

// ─── Member ───
public class Member {
    private final String id;
    private final String name;
    private final String email;
    private MemberStatus status;
    private final List<BookLoan> loanHistory;
    private final List<BookLoan> activeLoans;
    private double totalFines;
    private double paidFines;

    private static final int MAX_BOOKS_ALLOWED = 5;

    public Member(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = MemberStatus.ACTIVE;
        this.loanHistory = new ArrayList<>();
        this.activeLoans = new ArrayList<>();
        this.totalFines = 0;
        this.paidFines = 0;
    }

    public boolean canBorrow() {
        return status == MemberStatus.ACTIVE &&
               activeLoans.size() < MAX_BOOKS_ALLOWED &&
               getOutstandingFines() <= 0; // No unpaid fines
    }

    public void addLoan(BookLoan loan) {
        activeLoans.add(loan);
        loanHistory.add(loan);
    }

    public void completeLoan(BookLoan loan) {
        activeLoans.remove(loan);
    }

    public void addFine(double amount) {
        totalFines += amount;
    }

    public void payFine(double amount) {
        paidFines += Math.min(amount, getOutstandingFines());
    }

    public double getOutstandingFines() {
        return totalFines - paidFines;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public MemberStatus getStatus() { return status; }
    public void setStatus(MemberStatus status) { this.status = status; }
    public List<BookLoan> getActiveLoans() { return Collections.unmodifiableList(activeLoans); }
    public List<BookLoan> getLoanHistory() { return Collections.unmodifiableList(loanHistory); }
}

// ─── BookLoan ───
public class BookLoan {
    private final String id;
    private final BookItem bookItem;
    private final Member member;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;

    public BookLoan(String id, BookItem bookItem, Member member, int loanDays) {
        this.id = id;
        this.bookItem = bookItem;
        this.member = member;
        this.issueDate = LocalDate.now();
        this.dueDate = issueDate.plusDays(loanDays);
    }

    public void complete(double fineAmount) {
        this.returnDate = LocalDate.now();
        this.fineAmount = fineAmount;
    }

    // Getters
    public String getId() { return id; }
    public BookItem getBookItem() { return bookItem; }
    public Member getMember() { return member; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFineAmount() { return fineAmount; }
}

// ─── BookReservation ───
public class BookReservation {
    private final String id;
    private final Book book;
    private final Member member;
    private final LocalDate reservationDate;
    private ReservationStatus status;

    public BookReservation(String id, Book book, Member member) {
        this.id = id;
        this.book = book;
        this.member = member;
        this.reservationDate = LocalDate.now();
        this.status = ReservationStatus.WAITING;
    }

    public void markReady() { this.status = ReservationStatus.READY; }
    public void complete() { this.status = ReservationStatus.COMPLETED; }
    public void cancel() { this.status = ReservationStatus.CANCELLED; }

    // Getters
    public String getId() { return id; }
    public Book getBook() { return book; }
    public Member getMember() { return member; }
    public LocalDate getReservationDate() { return reservationDate; }
    public ReservationStatus getStatus() { return status; }
}
```

### Fine Calculation Strategy

```java
public interface FineCalculationStrategy {
    double calculateFine(long overdueDays);
}

public class FlatRateFineStrategy implements FineCalculationStrategy {
    private final double ratePerDay;

    public FlatRateFineStrategy(double ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    @Override
    public double calculateFine(long overdueDays) {
        return overdueDays * ratePerDay;
    }
}

public class ProgressiveFineStrategy implements FineCalculationStrategy {
    // First 7 days: $1/day, 8-14 days: $2/day, 15+ days: $5/day
    @Override
    public double calculateFine(long overdueDays) {
        double fine = 0;
        if (overdueDays <= 7) {
            fine = overdueDays * 1.0;
        } else if (overdueDays <= 14) {
            fine = 7 * 1.0 + (overdueDays - 7) * 2.0;
        } else {
            fine = 7 * 1.0 + 7 * 2.0 + (overdueDays - 14) * 5.0;
        }
        return fine;
    }
}
```

### Notification Observer

```java
public interface LibraryObserver {
    void onBookAvailable(Member member, Book book);
    void onBookOverdue(Member member, BookItem bookItem);
    void onFineCharged(Member member, double amount);
}

public class EmailNotificationService implements LibraryObserver {
    @Override
    public void onBookAvailable(Member member, Book book) {
        System.out.printf("[EMAIL → %s] Your reserved book '%s' is now available for pickup.%n",
            member.getEmail(), book.getTitle());
    }

    @Override
    public void onBookOverdue(Member member, BookItem bookItem) {
        System.out.printf("[EMAIL → %s] Book '%s' is overdue! Please return it.%n",
            member.getEmail(), bookItem.getBook().getTitle());
    }

    @Override
    public void onFineCharged(Member member, double amount) {
        System.out.printf("[EMAIL → %s] A fine of $%.2f has been charged.%n",
            member.getEmail(), amount);
    }
}
```

### Library Service (Orchestrator)

```java
public class LibraryService {
    private final Map<String, Book> booksByIsbn = new HashMap<>();
    private final Map<String, BookItem> bookItemsByBarcode = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final Queue<BookReservation> reservationQueue = new LinkedList<>();
    private final FineCalculationStrategy fineStrategy;
    private final List<LibraryObserver> observers = new ArrayList<>();

    private static final int DEFAULT_LOAN_DAYS = 14;

    public LibraryService(FineCalculationStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }

    public void addObserver(LibraryObserver observer) {
        observers.add(observer);
    }

    // ─── Catalog Management ───

    public Book addBook(String isbn, String title, String author,
                        String subject, int publicationYear) {
        Book book = booksByIsbn.computeIfAbsent(isbn,
            k -> new Book(isbn, title, author, subject, publicationYear));
        return book;
    }

    public BookItem addBookCopy(String isbn, String rackLocation) {
        Book book = booksByIsbn.get(isbn);
        if (book == null) throw new IllegalArgumentException("Book not found: " + isbn);

        String barcode = "BC-" + isbn + "-" + (book.getCopies().size() + 1);
        BookItem item = new BookItem(barcode, book, rackLocation);
        book.addCopy(item);
        bookItemsByBarcode.put(barcode, item);
        return item;
    }

    // ─── Member Management ───

    public Member registerMember(String id, String name, String email) {
        Member member = new Member(id, name, email);
        members.put(id, member);
        return member;
    }

    // ─── Search ───

    public List<Book> searchByTitle(String query) {
        return booksByIsbn.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<Book> searchByAuthor(String query) {
        return booksByIsbn.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(query.toLowerCase()))
                .toList();
    }

    public List<Book> searchBySubject(String subject) {
        return booksByIsbn.values().stream()
                .filter(b -> b.getSubject().equalsIgnoreCase(subject))
                .toList();
    }

    // ─── Core Operations ───

    public BookLoan issueBook(String memberId, String barcode) {
        Member member = members.get(memberId);
        if (member == null) throw new IllegalArgumentException("Member not found: " + memberId);
        if (!member.canBorrow()) throw new IllegalStateException(
            "Member cannot borrow. Active loans: " + member.getActiveLoans().size() +
            ", Outstanding fines: $" + member.getOutstandingFines()
        );

        BookItem bookItem = bookItemsByBarcode.get(barcode);
        if (bookItem == null) throw new IllegalArgumentException("Book item not found: " + barcode);
        if (bookItem.getStatus() != BookStatus.AVAILABLE &&
            bookItem.getStatus() != BookStatus.RESERVED) {
            throw new IllegalStateException("Book is not available. Status: " + bookItem.getStatus());
        }

        // If reserved, check it's reserved for this member
        if (bookItem.getStatus() == BookStatus.RESERVED) {
            boolean isReservedForMember = reservationQueue.stream()
                .anyMatch(r -> r.getBook().getIsbn().equals(bookItem.getBook().getIsbn()) &&
                          r.getMember().getId().equals(memberId) &&
                          r.getStatus() == ReservationStatus.READY);
            if (!isReservedForMember) {
                throw new IllegalStateException("This book is reserved for another member");
            }
            // Complete the reservation
            reservationQueue.stream()
                .filter(r -> r.getBook().getIsbn().equals(bookItem.getBook().getIsbn()) &&
                        r.getMember().getId().equals(memberId) &&
                        r.getStatus() == ReservationStatus.READY)
                .findFirst()
                .ifPresent(BookReservation::complete);
        }

        bookItem.checkOut(member, DEFAULT_LOAN_DAYS);
        BookLoan loan = new BookLoan(
            UUID.randomUUID().toString(), bookItem, member, DEFAULT_LOAN_DAYS
        );
        member.addLoan(loan);

        System.out.printf("Book '%s' issued to %s. Due: %s%n",
            bookItem.getBook().getTitle(), member.getName(), bookItem.getDueDate());
        return loan;
    }

    public double returnBook(String barcode) {
        BookItem bookItem = bookItemsByBarcode.get(barcode);
        if (bookItem == null) throw new IllegalArgumentException("Book item not found: " + barcode);
        if (bookItem.getStatus() != BookStatus.LOANED) {
            throw new IllegalStateException("Book is not currently loaned");
        }

        Member member = bookItem.getBorrowedBy();
        double fine = 0;

        // Calculate fine if overdue
        if (bookItem.isOverdue()) {
            fine = fineStrategy.calculateFine(bookItem.getOverdueDays());
            member.addFine(fine);
            for (LibraryObserver observer : observers) {
                observer.onFineCharged(member, fine);
            }
        }

        // Complete the loan record
        BookLoan loan = member.getActiveLoans().stream()
                .filter(l -> l.getBookItem().getBarcode().equals(barcode))
                .findFirst()
                .orElseThrow();
        loan.complete(fine);
        member.completeLoan(loan);

        bookItem.returnBook();

        // Check if there are pending reservations for this book
        processReservations(bookItem.getBook());

        System.out.printf("Book '%s' returned by %s. Fine: $%.2f%n",
            bookItem.getBook().getTitle(), member.getName(), fine);
        return fine;
    }

    public BookReservation reserveBook(String memberId, String isbn) {
        Member member = members.get(memberId);
        if (member == null) throw new IllegalArgumentException("Member not found");

        Book book = booksByIsbn.get(isbn);
        if (book == null) throw new IllegalArgumentException("Book not found");

        // Check if any copy is available (no need to reserve)
        if (book.getAvailableCopies() > 0) {
            throw new IllegalStateException("Book has available copies. No reservation needed.");
        }

        BookReservation reservation = new BookReservation(
            UUID.randomUUID().toString(), book, member
        );
        reservationQueue.add(reservation);

        System.out.printf("Reservation created for '%s' by %s%n", book.getTitle(), member.getName());
        return reservation;
    }

    private void processReservations(Book book) {
        // Find the earliest waiting reservation for this book
        Optional<BookReservation> pendingReservation = reservationQueue.stream()
                .filter(r -> r.getBook().getIsbn().equals(book.getIsbn()) &&
                        r.getStatus() == ReservationStatus.WAITING)
                .findFirst();

        if (pendingReservation.isPresent()) {
            BookReservation reservation = pendingReservation.get();
            reservation.markReady();

            // Mark one available copy as reserved
            book.getCopies().stream()
                    .filter(c -> c.getStatus() == BookStatus.AVAILABLE)
                    .findFirst()
                    .ifPresent(BookItem::markReserved);

            // Notify the member
            for (LibraryObserver observer : observers) {
                observer.onBookAvailable(reservation.getMember(), book);
            }
        }
    }

    public void payFine(String memberId, double amount) {
        Member member = members.get(memberId);
        if (member == null) throw new IllegalArgumentException("Member not found");
        member.payFine(amount);
        System.out.printf("%s paid $%.2f. Remaining: $%.2f%n",
            member.getName(), amount, member.getOutstandingFines());
    }
}
```

---

## Interview Discussion Points

1. **Why separate `Book` from `BookItem`?**
   - A `Book` is a catalog entry (title, author, ISBN). A `BookItem` is a physical copy with a barcode and a location. A library can have 5 copies of the same book. This follows the **Type Object** pattern and mirrors how real library systems work.

2. **How does the reservation queue work?**
   - When a book is returned, `processReservations()` checks if anyone is waiting. If so, the first person in the queue is notified, and one copy is marked RESERVED. This is a FIFO queue — first-come, first-served.

3. **How would you handle concurrent checkouts in a multi-threaded system?**
   - Use database-level optimistic locking (`@Version` in JPA) or pessimistic locking (`SELECT ... FOR UPDATE`). For the `BookItem.checkOut()` method, you'd use a database row lock to prevent two users from checking out the same physical copy.

4. **Extensibility examples?**
   - **Subscription tiers**: Different `MAX_BOOKS_ALLOWED` per member tier (Basic=3, Premium=10).
   - **E-books**: Add a `DigitalBookItem` subclass with no physical location, unlimited copies, and auto-return after loan period.
   - **Audit trail**: Use the Observer pattern to log every operation to an audit table.

