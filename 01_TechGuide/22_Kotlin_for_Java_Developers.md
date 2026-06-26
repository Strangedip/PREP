# Section 22: Kotlin for Java Developers (2026)

> **Level**: MID+ (interop basics) to SR+ (coroutines vs virtual threads, Spring Boot Kotlin)
> **Why This Matters**: Kotlin is the default for Android and increasingly common in backend (Spring Boot, Ktor). Java-heavy teams adopt Kotlin for new services. Interviewers ask when you'd choose Kotlin and how it interoperates with Java.

---

## 22.1 Why Kotlin in 2026?

| Driver | Detail |
|--------|--------|
| **Android** | Kotlin is the primary Android language (Google mandate since 2019) |
| **Spring Boot** | Official Kotlin support, `spring-boot-starter` works identically |
| **Null safety** | Compile-time null checks reduce NPE bugs in large codebases |
| **Coroutines** | Structured async without callback hell (compare to CompletableFuture / virtual threads) |
| **Gradual adoption** | 100% Java interop — mix Kotlin and Java in same project |

---

## 22.2 Kotlin vs Java — Key Differences

| Feature | Java | Kotlin |
|---------|------|--------|
| Null safety | `Optional`, annotations | `String` vs `String?` at compile time |
| Data classes | Records (Java 16+) | `data class` since 1.0 |
| Immutability | `final` + Records | `val` vs `var`, data classes |
| Extension functions | Utility classes | `fun String.isEmail(): Boolean` |
| Default parameters | Not supported | `fun greet(name: String = "World")` |
| Smart casts | Manual casting | `if (x is String) x.length` |
| Concurrency | Threads, CompletableFuture, virtual threads | Coroutines + structured concurrency |
| SAM conversions | Functional interfaces | Lambdas auto-convert |

---

## 22.3 Null Safety — Interview Favorite

```kotlin
var name: String = "Alice"      // non-null — compiler enforces
var nickname: String? = null    // nullable

// Safe call
val len = nickname?.length      // Int? — null if nickname is null

// Elvis operator
val display = nickname ?: "Anonymous"

// Not-null assertion (avoid in production)
val forced = nickname!!         // throws if null
```

**Java equivalent pain**: `Optional<String>`, `@NonNull` annotations, runtime NPE.

---

## 22.4 Coroutines vs Java Virtual Threads

| Aspect | Kotlin Coroutines | Java Virtual Threads (21+) |
|--------|-------------------|----------------------------|
| Model | Lightweight tasks on thread pool | JVM-managed virtual threads on carrier threads |
| Blocking code | `suspend` functions — compiler transforms | Blocking JDBC works natively |
| Structured concurrency | `coroutineScope`, `supervisorScope` | `StructuredTaskScope` (Java 21+) |
| Ecosystem | Mature in Android, Ktor, Spring | Growing in Spring Boot 3.2+ |
| Learning curve | New keywords (`suspend`, `launch`, `async`) | Mostly familiar thread code |

**Interview answer**: "For Kotlin-first teams, coroutines are natural. For Java Spring shops, virtual threads are lower migration cost. Both solve blocking I/O scalability. Don't mix blocking calls inside coroutine dispatchers without care."

```kotlin
// Coroutine — non-blocking style
suspend fun fetchUser(id: Long): User = withContext(Dispatchers.IO) {
    userRepository.findById(id)  // blocking JDBC OK on IO dispatcher
}

fun main() = runBlocking {
    val user = async { fetchUser(1) }
    val orders = async { fetchOrders(1) }
    println(user.await() to orders.await())
}
```

---

## 22.5 Spring Boot with Kotlin

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): UserResponse =
        userService.findById(id)
            ?: throw UserNotFoundException(id)
}

@Service
class UserService(private val repo: UserRepository) {
    fun findById(id: Long): UserResponse? =
        repo.findById(id)?.toResponse()
}

data class UserResponse(val id: Long, val name: String, val email: String)
```

**Tips**:
- Use `kotlin-spring` plugin (opens classes for `@Transactional` proxies)
- Use `jackson-module-kotlin` for JSON
- Prefer `data class` for DTOs, `val` for immutability

---

## 22.6 Java Interop — Rules to Know

| Kotlin | Java sees |
|--------|-----------|
| `object Singleton` | `Singleton.INSTANCE` |
| `companion object` | `MyClass.Companion` |
| Extension function | Static method on `MyClassKt` |
| `fun List<Int>.sum()` | `MyFileKt.sum(list)` |
| `@JvmStatic` | True static method |
| `@JvmOverloads` | Generates overloads for default params |
| `String?` | `@Nullable String` if annotated |

**Pitfall**: Kotlin `null` in Java code without checks → NPE at runtime.

---

## 22.7 When to Choose Kotlin vs Java

| Choose Kotlin | Choose Java |
|---------------|-------------|
| Android app | Legacy Java monolith with no Kotlin toolchain |
| New microservice in Kotlin-fluent team | Team has zero Kotlin experience and tight deadline |
| Heavy DSL / config APIs | Maximum library compatibility concerns (rare now) |
| Null-safety critical domain | Government/regulated environments mandating Java |

---

## 22.8 Interview Quick Reference

| # | Question | One-Line Answer |
|---|----------|-----------------|
| 1 | Kotlin null safety? | `String` vs `String?`, safe call `?.`, Elvis `?:`, compile-time NPE prevention. |
| 2 | Coroutines vs threads? | Coroutines are lightweight, suspendable tasks; cheaper than platform threads. |
| 3 | Coroutines vs virtual threads? | Both scale I/O; coroutines are Kotlin-native with `suspend`; VTs are JVM feature for Java. |
| 4 | `data class` vs Java Record? | Both: immutable carriers with equals/hashCode/toString. Kotlin adds `copy()`. |
| 5 | Kotlin Spring Boot gotchas? | `kotlin-spring` plugin, Jackson Kotlin module, don't use `open` everywhere manually. |
| 6 | `val` vs `var`? | `val` immutable reference (like final), `var` mutable. |
| 7 | Extension functions? | Add methods to existing types without inheritance; compiled to static helpers. |
| 8 | `sealed class` in Kotlin? | Restricted hierarchies like Java sealed classes — exhaustive `when`. |
| 9 | SAM conversion? | Kotlin lambda → Java functional interface automatically. |
| 10 | Why Kotlin for backend? | Null safety, coroutines, less boilerplate, full Java interop. |

**Must-say keywords**: null safety, coroutines, `suspend`, data class, interop, `@JvmStatic`, virtual threads comparison, Spring Kotlin plugin.
