# Section 00A: Java OOP Fundamentals

> **Level**: ALL — Associate / Junior SDE (start here before §01 Modern Java)
> **Purpose**: Core object-oriented programming every Java interview assumes you know.

---

## 00A.1 OOP Four Pillars

| Pillar | Meaning | Java Example |
|--------|---------|--------------|
| **Encapsulation** | Hide internal state; expose via methods | `private` fields + getters/setters |
| **Abstraction** | Hide complexity; show essential behavior | `interface PaymentGateway` |
| **Inheritance** | Reuse and extend behavior | `class SavingsAccount extends Account` |
| **Polymorphism** | Same interface, different behavior | `List` → `ArrayList` or `LinkedList` |

---

## 00A.2 Classes, Objects, and Memory

```java
public class User {
    private final String id;      // immutable reference
    private String name;          // mutable

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void rename(String newName) {
        this.name = newName;
    }
}
```

- **Object** = instance on the heap; **reference** on stack or in another object
- **`this`** = current instance reference
- **`static`** = one copy per class, not per instance

---

## 00A.3 Inheritance vs Composition

```java
// Inheritance — "is-a"
class Dog extends Animal { void bark() { } }

// Composition — "has-a" (prefer for flexibility)
class Car {
    private final Engine engine;  // Car HAS an Engine
}
```

**Interview rule**: Prefer **composition over inheritance** unless true subtype polymorphism is needed (Liskov).

---

## 00A.4 Interfaces vs Abstract Classes

| | Interface | Abstract Class |
|---|-----------|----------------|
| Methods | Abstract + default (Java 8+) | Abstract + concrete |
| Fields | `public static final` only | Any field |
| Multiple? | Class implements many | Class extends one |
| Use when | Capability contract (`Comparable`, `Serializable`) | Shared base with partial implementation |

---

## 00A.5 equals(), hashCode(), and toString()

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return id.equals(user.id);
}

@Override
public int hashCode() {
    return id.hashCode();
}
```

**Contract**: Equal objects must have equal hash codes. Required for `HashMap`/`HashSet` correctness.

**Records (Java 16+)**: Auto-generate equals/hashCode/toString for data carriers.

---

## 00A.6 Exception Handling

| Type | Examples | Handling |
|------|----------|----------|
| **Checked** | `IOException`, `SQLException` | Must catch or declare |
| **Unchecked** | `NullPointerException`, `IllegalArgumentException` | Optional catch |

```java
try {
    process(file);
} catch (IOException e) {
    log.error("Failed to read", e);
    throw new ServiceException("Upload failed", e);
} finally {
    closeQuietly(file);
}
```

**Best practice**: Catch specific exceptions; don't swallow; use `finally` or try-with-resources for cleanup.

```java
try (InputStream in = new FileInputStream(path)) {
    // auto-closed
}
```

---

## 00A.7 Collections Overview (before deep dive in §15)

| Interface | Implementations | Use |
|-----------|-----------------|-----|
| `List` | `ArrayList`, `LinkedList` | Ordered, allows duplicates |
| `Set` | `HashSet`, `TreeSet` | Unique elements |
| `Map` | `HashMap`, `TreeMap` | Key-value lookup |
| `Queue` | `LinkedList`, `PriorityQueue` | FIFO / priority |

**ArrayList vs LinkedList**: ArrayList for random access; LinkedList rarely wins in practice.

---

## 00A.8 Interview Quick Reference

| Question | Answer |
|----------|--------|
| Encapsulation? | Hide fields; control access via methods. |
| Composition vs inheritance? | Prefer composition; inheritance for true subtyping. |
| Why override hashCode with equals? | HashMap/HashSet break without consistent contract. |
| Checked vs unchecked? | Checked must be handled; unchecked extends RuntimeException. |
| `final` on reference? | Reference can't change; object contents may still change. |
| Abstract class vs interface? | Abstract: shared code + single parent. Interface: multiple contracts. |
| What is polymorphism? | Parent reference → child behavior at runtime. |

**Next**: [35_SQL_Fundamentals.md](./35_SQL_Fundamentals.md) → [02_DSA](../02_DSA/StudyGuide.md) Path 1 → then [01_Modern_Java_Features.md](./01_Modern_Java_Features.md).
