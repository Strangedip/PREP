# Coding Patterns — Lead Engineer Interview Prep

> **You are here**: SDE1–SDE2 — Patterns
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [Design Patterns](../01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md) | **Next**: [Algorithmic Patterns](02_AlgorithmicPatterns.md)

> **Two types of patterns are covered**: Software Design Patterns (GoF) and Algorithmic Coding Patterns (Interview Problem-Solving).

---

## What's in This Directory

| File | Content | When to Study |
|------|---------|---------------|
| [Machine_Coding_Round_Guide.md](Machine_Coding_Round_Guide.md) | Timed OOD + Spring Boot (90 min) | Before Flipkart/Swiggy-style machine coding rounds |
| [02_AlgorithmicPatterns.md](02_AlgorithmicPatterns.md) | 17 algorithmic patterns with templates, complexity, and when-to-use | Before DSA practice — learn patterns, then apply |
| [01_Patterns.md](01_Patterns.md) | 25 GoF Design Patterns + Spring Boot integration | Before System Design and Machine Coding rounds |

---

## Algorithmic Patterns (16 in [02_AlgorithmicPatterns.md](02_AlgorithmicPatterns.md))

These patterns cover 90%+ of coding interview problems. Numbering matches the main guide.

| # | Pattern | Repo problems | When to use |
|---|---------|---------------|-------------|
| 1 | Two Pointers | [Two Sum](../02_DSA/01_Arrays_Matrix/TwoSum/TwoSum.md), [Three Sum](../02_DSA/01_Arrays_Matrix/ThreeSum/ThreeSum.md) | Sorted arrays, pair/triplet search |
| 2 | Sliding Window | [Longest Substring](../02_DSA/02_Strings/LongestSubstringWithoutRepeating/LongestSubstringWithoutRepeating.md) | Contiguous subarray/substring |
| 3 | Fast & Slow Pointers | [Linked List Cycle](../02_DSA/05_Linked_Lists/LinkedListCycle/LinkedListCycle.md) | Cycle, middle of list |
| 4 | Merge Intervals | [Merge Intervals](../02_DSA/01_Arrays_Matrix/MergeIntervals/MergeIntervals.md) | Overlapping ranges |
| 5 | Cyclic Sort | [Find Duplicate Number](../02_DSA/01_Arrays_Matrix/FindDuplicateNumber/FindDuplicateNumber.md) | Values in range [1, n] |
| 6 | In-Place LL Reversal | [Reverse Linked List](../02_DSA/05_Linked_Lists/ReverseLinkedList/ReverseLinkedList.md) | Pointer rewiring |
| 7 | BFS | [Number of Islands](../02_DSA/11_Graphs/NumberOfIslands/NumberOfIslands.md) | Shortest path, levels |
| 8 | DFS | [Clone Graph](../02_DSA/11_Graphs/CloneGraph/CloneGraph.md) | Paths, trees, exhaustive |
| 9 | Two Heaps | [Find Median from Stream](../02_DSA/10_Heaps_Priority_Queues/FindMedianFromDataStream/FindMedianFromDataStream.md) | Running median |
| 10 | Backtracking | [Subsets](../02_DSA/12_Backtracking/Subsets/Subsets.md) | Combinatorial generation |
| 11 | Modified Binary Search | [Search Rotated Array](../02_DSA/01_Arrays_Matrix/SearchInRotatedSortedArray/SearchInRotatedSortedArray.md) | Rotated / answer space |
| 12 | Top K Elements | [Top K Frequent](../02_DSA/10_Heaps_Priority_Queues/TopKFrequentElements/TopKFrequentElements.md) | Heap selection |
| 13 | K-Way Merge | [Merge K Lists](../02_DSA/10_Heaps_Priority_Queues/MergeKSortedLists/MergeKSortedLists.md) | Multiple sorted inputs |
| 14 | Monotonic Stack | [Daily Temperatures](../02_DSA/06_Stacks_Queues/DailyTemperatures/DailyTemperatures.md) | Next greater/smaller |
| 15 | Topological Sort | [Course Schedule](../02_DSA/11_Graphs/CourseSchedule/CourseSchedule.md) | Dependencies, ordering |
| 16 | Dynamic Programming | [Coin Change](../02_DSA/13_Dynamic_Programming/CoinChange/CoinChange.md) | Optimal substructure |

**Related (graph connectivity)**: [Union Find](../02_DSA/11_Graphs/UnionFind/UnionFind.md) — not a separate numbered pattern; used with [Number of Islands](../02_DSA/11_Graphs/NumberOfIslands/NumberOfIslands.md), [Critical Connections](../02_DSA/11_Graphs/CriticalConnections/CriticalConnections.md).

→ **Templates and decision tree**: [02_AlgorithmicPatterns.md](02_AlgorithmicPatterns.md)

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

→ **Full code examples in [01_Patterns.md](01_Patterns.md)**

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

