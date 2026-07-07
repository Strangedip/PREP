# Section 00A: Java OOP Fundamentals

> **Level**: ALL — Associate / Junior SDE (start here before §01 Modern Java)
> **Depth**: Standard (foundational explanations with code examples and interview scenarios)
> **Purpose**: Core object-oriented programming every Java interview assumes you know.

> **You are here**: Fresher — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [00_Computer_Science_Fundamentals.md](00_Computer_Science_Fundamentals.md) | **Next**: [35_SQL_Fundamentals.md](./35_SQL_Fundamentals.md)

---

## Why OOP matters in Java interviews

Java is an object-oriented language. Fresher and SDE1 rounds test whether you can **model real-world entities as classes**, **hide internal state**, and **reuse behavior** through inheritance and interfaces. This section builds that foundation before you touch Spring Boot or collections deep dives.

---

## 00A.1 OOP Four Pillars — Explained with Examples

### 1. Encapsulation — Hide the internals

**Idea**: Users of your class should not directly modify internal fields. You control access through methods.

```java
public class BankAccount {
    private double balance;  // hidden — cannot do account.balance = 1_000_000 from outside

    public BankAccount(double initialBalance) {
        if (initialBalance < 0) throw new IllegalArgumentException("Balance cannot be negative");
        this.balance = initialBalance;
    }

    public void deposit(double amount) {
        if (amount <= 0) throw new IllegalArgumentException("Deposit must be positive");
        this.balance += amount;
    }

    public double getBalance() {
        return balance;  // read-only access
    }
}
```

**Why**: Validation, invariants (balance never negative), freedom to change internal representation later.

**Interview answer**: "Encapsulation means bundling data and methods that operate on that data, and restricting direct field access via `private` + getters/setters or behavior methods."

### 2. Abstraction — Show what, hide how

**Idea**: Expose essential behavior; hide implementation complexity.

```java
public interface PaymentGateway {
    PaymentResult charge(Money amount, CardDetails card);
    void refund(String transactionId);
}

// Caller doesn't care if it's Razorpay, Stripe, or a mock
public class CheckoutService {
    private final PaymentGateway gateway;
    public void checkout(Order order) {
        gateway.charge(order.total(), order.card());
    }
}
```

**Interview answer**: "Abstraction focuses on what an object does, not how. Interfaces and abstract classes are the main tools in Java."

### 3. Inheritance — Reuse and extend ("is-a")

**Idea**: Child class inherits fields and methods from parent.

```java
public abstract class Animal {
    protected String name;
    public abstract void makeSound();
}

public class Dog extends Animal {
    @Override
    public void makeSound() { System.out.println("Woof"); }
    public void fetch() { /* dog-specific */ }
}
```

**Caution**: Deep inheritance hierarchies become fragile. Prefer shallow trees.

### 4. Polymorphism — Same interface, different behavior

**Idea**: A parent reference can point to any child object; the **runtime type** determines which method runs.

```java
List<String> names = new ArrayList<>();  // List reference, ArrayList implementation
names = new LinkedList<>();              // Same reference type, different behavior

Animal pet = new Dog();  // pet.makeSound() → "Woof" (runtime dispatch)
```

**Interview answer**: "Compile-time polymorphism = method overloading (same name, different parameters). Runtime polymorphism = method overriding (child replaces parent method)."

### Pillars summary table

| Pillar | Meaning | Java mechanism |
|--------|---------|----------------|
| Encapsulation | Hide state; control access | `private` fields + methods |
| Abstraction | Hide complexity | `interface`, `abstract class` |
| Inheritance | Reuse parent behavior | `extends` |
| Polymorphism | One interface, many forms | Overriding, interface implementations |

---

## 00A.2 Classes, Objects, and Memory

### Class vs Object

- **Class** = blueprint (defined once in code)
- **Object** = instance (created at runtime with `new`)

```java
public class User {
    private final String id;      // immutable reference — cannot point to another String
    private String name;          // mutable — can change which String name refers to

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void rename(String newName) {
        this.name = newName;
    }
}

User alice = new User("u1", "Alice");  // object on heap; alice is reference on stack
```

### Memory model (simplified)

```
Stack (per thread)          Heap (shared)
┌─────────────┐            ┌──────────────────┐
│ alice ref ──┼───────────→│ User object      │
│             │            │  id = "u1"       │
│ main()      │            │  name = "Alice"  │
└─────────────┘            └──────────────────┘
```

### Keywords you must know

| Keyword | Meaning |
|---------|---------|
| `this` | Reference to current object instance |
| `static` | Belongs to class, not instance — one copy shared |
| `final` (field) | Reference cannot be reassigned after construction |
| `final` (class) | Cannot be subclassed (`String` is final) |
| `final` (method) | Cannot be overridden |

```java
public class Counter {
    private static int totalInstances = 0;  // shared across all Counter objects
    public Counter() { totalInstances++; }
    public static int getTotalInstances() { return totalInstances; }
}
```

---

## 00A.3 Inheritance vs Composition

### Inheritance — "is-a" relationship

```java
class Dog extends Animal { }  // Dog IS AN Animal
```

**Use when**: True subtype polymorphism is needed and Liskov Substitution holds (a Dog can be used wherever Animal is expected).

### Composition — "has-a" relationship (usually preferred)

```java
class Car {
    private final Engine engine;  // Car HAS AN Engine
    private final GPS gps;

    public Car(Engine engine, GPS gps) {
        this.engine = engine;
        this.gps = gps;
    }
}
```

**Why prefer composition**:
- Avoid fragile base classes (parent change breaks children)
- Swap implementations at runtime (plug different Engine)
- No diamond problem (Java allows single inheritance only anyway)

**Interview rule**: "Favor composition over inheritance unless you need true subtype polymorphism."

### Liskov Substitution Principle (LSP)

Subtypes must be substitutable for their base type without breaking behavior.

**Violation example**: `Square extends Rectangle` where setting width also changes height — breaks code expecting independent width/height.

---

## 00A.4 Interfaces vs Abstract Classes

| | Interface | Abstract Class |
|---|-----------|----------------|
| Methods | Abstract + `default` + `static` (Java 8+) | Abstract + concrete |
| Fields | `public static final` constants only | Any field type |
| Inheritance | Class `implements` many interfaces | Class `extends` one class |
| Constructor | No | Yes |
| Use when | Capability contract across unrelated classes | Shared base with partial implementation |

```java
// Interface — capability
public interface Comparable<T> {
    int compareTo(T other);
}

// Abstract class — shared partial implementation
public abstract class AbstractRepository<T> {
    protected abstract T findById(String id);
    public List<T> findAll() { /* common JDBC logic */ }
}
```

**Java 8+ default methods**: Add new methods to interfaces without breaking existing implementations.

```java
public interface Logger {
    void log(String message);
    default void logError(String message) { log("ERROR: " + message); }
}
```

---

## 00A.5 equals(), hashCode(), and toString()

### Why they matter

`HashMap` and `HashSet` use `hashCode()` to find the bucket and `equals()` to compare objects in that bucket. **Breaking the contract causes subtle bugs** — objects "disappear" from sets or duplicate keys appear.

### The contract

1. If `a.equals(b)` is true, then `a.hashCode() == b.hashCode()` must be true
2. If `a.hashCode() == b.hashCode()`, `a.equals(b)` may still be false (collision)
3. `equals()` must be reflexive, symmetric, transitive, consistent

### Correct implementation

```java
public class User {
    private final String id;
    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                    // same reference
        if (o == null || getClass() != o.getClass()) return false;  // null + type check
        User user = (User) o;
        return id.equals(user.id);                     // compare business key
    }

    @Override
    public int hashCode() {
        return id.hashCode();                            // same fields as equals
    }

    @Override
    public String toString() {
        return "User{id='" + id + "', name='" + name + "'}";
    }
}
```

### Records (Java 16+) — auto-generated for data carriers

```java
public record Point(int x, int y) { }
// Auto: constructor, equals, hashCode, toString, accessors x() and y()
```

**When to use records**: DTOs, value objects, immutable data holders — not for entities with complex lifecycle.

---

## 00A.6 Exception Handling

### Checked vs Unchecked

| Type | Extends | Compiler forces handling? | Examples |
|------|---------|------------------------|----------|
| **Checked** | `Exception` (not RuntimeException) | Yes — catch or declare | `IOException`, `SQLException` |
| **Unchecked** | `RuntimeException` | No | `NullPointerException`, `IllegalArgumentException` |

### Best practices

```java
try (InputStream in = new FileInputStream(path)) {  // try-with-resources: auto-close
    return parse(in);
} catch (FileNotFoundException e) {
    throw new ServiceException("File not found: " + path, e);  // wrap with context
}
```

| Do | Don't |
|----|-------|
| Catch specific exceptions | Catch `Exception` and swallow |
| Add context when rethrowing | `e.printStackTrace()` in production |
| Use try-with-resources for Closeable | Forget to close streams |
| Use custom exceptions for domain errors | Throw generic `Exception` everywhere |

### Exception hierarchy (simplified)

```
Throwable
├── Error (OutOfMemoryError — don't catch)
└── Exception
    ├── RuntimeException (unchecked)
    │   ├── NullPointerException
    │   ├── IllegalArgumentException
    │   └── IllegalStateException
    └── IOException, SQLException (checked)
```

---

## 00A.7 Collections Overview

Before the deep dive in [§15](15_Java_Collections_Concurrency_DeepDive.md), know the interfaces:

| Interface | Implementations | Ordered? | Duplicates? | Use case |
|-----------|-----------------|----------|-------------|----------|
| `List` | `ArrayList`, `LinkedList` | Yes | Yes | Indexed collection |
| `Set` | `HashSet`, `TreeSet`, `LinkedHashSet` | Varies | No | Unique elements |
| `Map` | `HashMap`, `TreeMap`, `LinkedHashMap` | Keys vary | Keys unique | Key-value lookup |
| `Queue` | `LinkedList`, `PriorityQueue` | Yes | Yes | FIFO / priority |
| `Deque` | `ArrayDeque` | Yes | Yes | Stack/queue operations both ends |

### ArrayList vs LinkedList — the honest answer

| Operation | ArrayList | LinkedList |
|-----------|-----------|------------|
| `get(i)` | O(1) | O(n) |
| `add(end)` | O(1) amortized | O(1) |
| `add(middle)` | O(n) | O(n) to find + O(1) insert |

**In practice**: Use `ArrayList` unless you have a specific reason for `LinkedList` (rare). `LinkedList` as `Queue` is fine; as general-purpose list, `ArrayList` wins.

### Map iteration

```java
Map<String, Integer> scores = new HashMap<>();
for (Map.Entry<String, Integer> entry : scores.entrySet()) {
    System.out.println(entry.getKey() + " → " + entry.getValue());
}
```

---

## 00A.8 Common Interview Questions (full answers)

| Question | Answer |
|----------|--------|
| **What is encapsulation?** | Bundling data and methods; restricting direct field access via `private` and exposing controlled methods. Enables validation and internal change without breaking callers. |
| **Composition vs inheritance?** | Prefer composition (has-a) for flexibility. Use inheritance (is-a) when true subtype polymorphism is required and LSP holds. |
| **Why override hashCode with equals?** | HashMap/HashSet use hashCode for bucket lookup and equals for equality. Inconsistent contract → lost objects or duplicates. |
| **Checked vs unchecked?** | Checked must be caught or declared; compiler enforces. Unchecked extends RuntimeException; indicates programming errors. |
| **`final` on reference?** | Reference cannot be reassigned; object contents may still mutate unless object is immutable. |
| **Abstract class vs interface?** | Abstract: shared code + single inheritance. Interface: multiple capability contracts; use default methods for evolution. |
| **What is polymorphism?** | Same interface/reference type, different runtime behavior via overriding. Enables plug-in architectures (PaymentGateway implementations). |
| **String immutability?** | Strings cannot change after creation; operations return new String. Safe for caching, hashing, and thread sharing. |
| **`==` vs `equals()`?** | `==` compares references (same object in memory). `equals()` compares content (when overridden). |

---

## Practice checklist

- [ ] Write a class with encapsulation, `equals`/`hashCode` for a business key
- [ ] Explain one project class diagram: which used inheritance vs composition?
- [ ] Trace a `NullPointerException` — what was null and why?
- [ ] Use `ArrayList`, `HashMap`, and `HashSet` in a small program

**Next**: [35_SQL_Fundamentals.md](./35_SQL_Fundamentals.md) → [02_DSA StudyGuide Path 1](../02_DSA/StudyGuide.md) → [01_Modern_Java_Features.md](./01_Modern_Java_Features.md).
