# Coding Patterns — Lead Engineer Interview Prep

> **Two types of patterns are covered**: Software Design Patterns (GoF) and Algorithmic Coding Patterns (Interview Problem-Solving).

---

## What's in This Directory

| File | Content | When to Study |
|------|---------|---------------|
| [AlgorithmicPatterns.md](AlgorithmicPatterns.md) | 16 algorithmic patterns with templates, complexity, and when-to-use | Before DSA practice — learn patterns, then apply |
| [patterns.md](patterns.md) | 25 GoF Design Patterns + Spring Boot integration | Before System Design and Machine Coding rounds |

---

## Algorithmic Patterns (16 Patterns)

These patterns cover 90%+ of coding interview problems. Master the pattern, then problems become pattern-matching exercises.

| # | Pattern | Key Problems | When to Use |
|---|---------|-------------|-------------|
| 1 | Sliding Window | Min Window Substring, Longest Substring | Contiguous subarray/substring optimization |
| 2 | Two Pointers | Two Sum (sorted), 3Sum, Container With Water | Sorted arrays, pair finding, converging search |
| 3 | Fast & Slow Pointers | Cycle Detection, Middle of List | Linked list cycle, finding middle |
| 4 | Merge Intervals | Merge Intervals, Insert Interval | Overlapping intervals |
| 5 | Cyclic Sort | Find Missing/Duplicate Number | Array with numbers in range [0, n] |
| 6 | In-Place Linked List Reversal | Reverse List, Reverse K-Group | Reversing linked list sections |
| 7 | BFS | Level Order, Shortest Path | Level-by-level, unweighted shortest path |
| 8 | DFS | Path problems, tree traversals | All paths, tree problems, exhaustive search |
| 9 | Two Heaps | Median from Stream | Running median, stream problems |
| 10 | Subsets / Backtracking | Subsets, Permutations, N-Queens | Combinatorial problems |
| 11 | Modified Binary Search | Search Rotated Array, Search Range | Sorted/rotated search spaces |
| 12 | Top K Elements | Top K Frequent, K Closest | Selection from a large dataset |
| 13 | K-Way Merge | Merge K Lists, Smallest Range | Multiple sorted sources |
| 14 | Dynamic Programming | Knapsack, LCS, LIS | Overlapping subproblems + optimal substructure |
| 15 | Monotonic Stack | Daily Temperatures, Next Greater | Next greater/smaller element |
| 16 | Union Find | Connected Components, MST | Dynamic connectivity, grouping |

→ **Detailed templates and code in [AlgorithmicPatterns.md](AlgorithmicPatterns.md)**

---

## Software Design Patterns (25 GoF Patterns)

Used in Machine Coding rounds, System Design discussions, and code quality evaluation.

### Creational (5)
| Pattern | In Spring Boot | Interview Signal |
|---------|---------------|-----------------|
| Singleton | `@Component` (default scope) | Show you understand bean scopes |
| Factory Method | `BeanFactory`, `FactoryBean` | Decoupling object creation |
| Abstract Factory | `ApplicationContext` variants | Families of related objects |
| Builder | `UriComponentsBuilder`, Lombok `@Builder` | Complex object construction |
| Prototype | `@Scope("prototype")` | When each request needs a fresh bean |

### Structural (7)
| Pattern | In Spring Boot | Interview Signal |
|---------|---------------|-----------------|
| Adapter | Spring `HandlerAdapter` | Integrating incompatible interfaces |
| Decorator | `BufferedReader` wrapping `FileReader` | Adding behavior dynamically |
| Proxy | `@Transactional` (CGLIB proxy) | AOP, security, caching |
| Facade | Spring `RestTemplate`, `WebClient` | Simplifying complex subsystems |
| Bridge | JDBC `DriverManager` | Decoupling abstraction from implementation |
| Composite | Spring `CompositeHealthContributor` | Tree structures |
| Flyweight | String pool, Integer cache | Memory optimization |

### Behavioral (11+)
| Pattern | In Spring Boot | Interview Signal |
|---------|---------------|-----------------|
| Strategy | `@Qualifier` + interface implementations | Swappable algorithms |
| Observer | `ApplicationEventPublisher`, `@EventListener` | Event-driven architecture |
| Template Method | `JdbcTemplate`, `RestTemplate` | Define skeleton, let subclasses fill steps |
| Command | Spring Batch `Tasklet` | Encapsulating requests as objects |
| State | State machines (`spring-statemachine`) | Object behavior changes with state |
| Chain of Responsibility | `Filter` chain, `HandlerInterceptor` | Sequential processing with optional handling |
| Iterator | Java `Iterator`, Spring `ItemReader` | Sequential access without exposing internals |
| Mediator | Spring `ApplicationContext` (bean wiring) | Reducing coupling between components |
| Memento | `@SessionAttributes` | Saving/restoring state |
| Visitor | `BeanDefinitionVisitor` | Operations on heterogeneous structures |
| Interpreter | SpEL (Spring Expression Language) | Parsing expressions |

→ **Full code examples in [patterns.md](patterns.md)**

---

## How Patterns Connect to Interviews

| Interview Round | Patterns Used | Example |
|----------------|--------------|---------|
| **DSA Coding** | Algorithmic Patterns | "This is a sliding window problem" → apply template |
| **Machine Coding** | GoF (Strategy, Factory, Observer, State) | Build Parking Lot with Strategy pattern for pricing |
| **System Design** | GoF (Proxy, Facade, Observer) + Architectural | Circuit Breaker uses State pattern, API Gateway uses Facade |
| **Code Review** | SOLID + GoF awareness | "This violates OCP — use Strategy pattern instead" |
| **Behavioral** | Pattern vocabulary | "I refactored the payment system using the Strategy pattern to support multiple providers" |

---

## Study Order

1. **Week 1**: Algorithmic Patterns 1-8 (Sliding Window through DFS) — these cover 60% of problems.
2. **Week 2**: Algorithmic Patterns 9-16 (Heaps through Union Find) — cover the remaining 30%.
3. **Week 3**: GoF Creational + Behavioral patterns — needed for Machine Coding and design discussions.
4. **Week 4**: GoF Structural patterns — understand Proxy, Adapter, Decorator (heavily used in Spring Boot).

