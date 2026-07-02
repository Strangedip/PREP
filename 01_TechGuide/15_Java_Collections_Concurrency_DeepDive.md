# Section 15: Java Collections & Concurrency Deep Dive

> **Level**: MID+ (Collections) to LEAD (Advanced Concurrency)
> **Why This Matters**: HashMap internals, ConcurrentHashMap, Locks, and Synchronizers are asked in **every** Java interview. A Lead engineer must explain internals, not just usage.

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [14_Leadership_Behavioral_SystemDesign.md](14_Leadership_Behavioral_SystemDesign.md) | **Next**: [16_Spring_Ecosystem_DeepDive.md](16_Spring_Ecosystem_DeepDive.md)

---

## 15.1 Collections Framework Internals

### The "Why" & The Problem

Every Java application uses Collections. But interviewers don't want `ArrayList` usage — they want you to explain:
- How does `HashMap` resolve collisions?
- When does a HashMap bucket become a red-black tree?
- Why is `ConcurrentHashMap` thread-safe but `HashMap` isn't?
- What's the difference between `fail-fast` and `fail-safe` iterators?

### Interviewer Expectations

| Level | What They Expect |
|-------|-----------------|
| **Junior** | Know ArrayList, HashMap, HashSet, basic iteration |
| **Mid** | Explain HashMap hashing, load factor, resizing; know TreeMap vs HashMap |
| **Senior** | Explain ConcurrentHashMap internals, NavigableMap, fail-fast vs fail-safe |
| **Lead** | All of the above + performance characteristics, memory layout, when to use which, custom implementations |

### The Deep Dive & Solution

#### HashMap Internals

```java
// What happens when you do: map.put("key", "value")?

// Step 1: Calculate hash
// HashMap does NOT use hashCode() directly. It applies a "spread" function:
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    // XOR upper 16 bits with lower 16 bits to spread hash values
    // This reduces collisions when table size is a power of 2
}

// Step 2: Find bucket index
// index = (n - 1) & hash   (where n = table.length, always a power of 2)
// This is equivalent to hash % n but faster (bitwise AND)

// Step 3: Handle collisions
// Before Java 8: Linked list chaining (O(n) worst case)
// Java 8+: Linked list → Red-Black Tree when bucket has TREEIFY_THRESHOLD (8) entries
//          Red-Black Tree → Linked list when bucket shrinks to UNTREEIFY_THRESHOLD (6)
```

**Critical HashMap constants:**

```java
static final int DEFAULT_INITIAL_CAPACITY = 16;        // Must be power of 2
static final float DEFAULT_LOAD_FACTOR = 0.75f;        // Resize at 75% full
static final int TREEIFY_THRESHOLD = 8;                 // List → Tree
static final int UNTREEIFY_THRESHOLD = 6;               // Tree → List
static final int MIN_TREEIFY_CAPACITY = 64;             // Min table size for treeification
```

**Resizing (rehashing):**

```java
// When size > capacity × loadFactor, HashMap doubles its capacity
// All entries are rehashed and redistributed
// This is O(n) — one of the most expensive operations

// Interview tip: If you know the expected size, pre-allocate:
Map<String, String> map = new HashMap<>(expectedSize / 0.75f + 1);
// This avoids intermediate resizing
```

**HashMap Node structure (Java 8+):**

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;     // Cached hash (never recomputed)
    final K key;        // Immutable key
    V value;            // Mutable value
    Node<K,V> next;     // Next node in chain (linked list)
}

// When bucket treeifies, nodes become:
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;  // Red-black tree parent
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // For unlinking on deletion
    boolean red;            // Color flag
}
```

**Common Interview Question: Why must hashCode() and equals() be consistent?**

```java
// If two objects are equal (equals() returns true), they MUST have the same hashCode()
// Otherwise, HashMap puts them in different buckets, causing "phantom" duplicates

// BAD: Mutable fields in hashCode/equals
public class Employee {
    private String name;
    private int salary; // Mutable!
    
    @Override
    public int hashCode() {
        return Objects.hash(name, salary); // ❌ If salary changes after put(), 
                                           //    the entry becomes unreachable
    }
}

// GOOD: Use only immutable fields
public class Employee {
    private final String employeeId; // Immutable
    
    @Override
    public int hashCode() {
        return Objects.hash(employeeId); // ✅ Stable hash
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee that = (Employee) o;
        return Objects.equals(employeeId, that.employeeId);
    }
}
```

#### LinkedHashMap — Insertion/Access Order

```java
// LinkedHashMap extends HashMap with a doubly-linked list maintaining order

// Insertion order (default):
Map<String, Integer> insertionOrder = new LinkedHashMap<>();
insertionOrder.put("C", 3);
insertionOrder.put("A", 1);
insertionOrder.put("B", 2);
// Iteration: C→A→B (insertion order)

// Access order (LRU cache building block):
Map<String, Integer> accessOrder = new LinkedHashMap<>(16, 0.75f, true); // true = access order
accessOrder.put("C", 3);
accessOrder.put("A", 1);
accessOrder.put("B", 2);
accessOrder.get("C"); // Access moves C to the end
// Iteration: A→B→C (least recently used first)

// Building an LRU Cache with LinkedHashMap:
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;
    
    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true); // access-order
        this.maxSize = maxSize;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > maxSize; // Automatically removes LRU entry
    }
}
```

#### TreeMap — Sorted Map (Red-Black Tree)

```java
// TreeMap maintains keys in sorted order using a Red-Black Tree
// All operations: O(log n) — get, put, remove, containsKey

TreeMap<String, Integer> sorted = new TreeMap<>();
sorted.put("banana", 2);
sorted.put("apple", 1);
sorted.put("cherry", 3);
// Iteration: apple→banana→cherry (natural order)

// NavigableMap operations (very useful in interviews):
sorted.firstKey();                  // "apple"
sorted.lastKey();                   // "cherry"
sorted.lowerKey("banana");          // "apple" (strictly less than)
sorted.floorKey("banana");          // "banana" (less than or equal)
sorted.higherKey("banana");         // "cherry" (strictly greater than)
sorted.ceilingKey("banana");        // "banana" (greater than or equal)
sorted.subMap("apple", "cherry");   // {apple=1, banana=2} (exclusive end)
sorted.headMap("cherry");           // {apple=1, banana=2}
sorted.tailMap("banana");           // {banana=2, cherry=3}

// Custom comparator:
TreeMap<String, Integer> reverse = new TreeMap<>(Comparator.reverseOrder());

// Interview use case: Finding ranges, nearest neighbors, sorted iteration
```

#### Collection Performance Comparison

```
| Operation       | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|----------------|-----------|------------|---------|---------|---------|---------|
| add/put        | O(1)*     | O(1)       | O(1)*   | O(log n)| O(1)*   | O(log n)|
| get(index)     | O(1)      | O(n)       | —       | —       | —       | —       |
| get(key)       | —         | —          | O(1)*   | O(log n)| O(1)*   | O(log n)|
| contains       | O(n)      | O(n)       | O(1)*   | O(log n)| O(1)*   | O(log n)|
| remove         | O(n)      | O(1)**     | O(1)*   | O(log n)| O(1)*   | O(log n)|
| iteration order| insertion | insertion  | none    | sorted  | none    | sorted  |

* Amortized, assumes good hash function and no pathological collisions
** O(1) only if you have a reference to the node; O(n) to find by value
```

#### Fail-Fast vs Fail-Safe Iterators

```java
// FAIL-FAST (java.util collections: ArrayList, HashMap, HashSet)
// Throw ConcurrentModificationException if modified during iteration
List<String> list = new ArrayList<>(List.of("A", "B", "C"));
for (String s : list) {
    if (s.equals("B")) {
        list.remove(s); // ❌ ConcurrentModificationException!
    }
}

// SAFE way to remove during iteration:
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (it.next().equals("B")) {
        it.remove(); // ✅ Safe removal via iterator
    }
}

// Or use removeIf (Java 8+):
list.removeIf(s -> s.equals("B")); // ✅ Clean and safe

// FAIL-SAFE (java.util.concurrent collections: ConcurrentHashMap, CopyOnWriteArrayList)
// Work on a snapshot/copy — never throw ConcurrentModificationException
ConcurrentHashMap<String, Integer> concMap = new ConcurrentHashMap<>();
concMap.put("A", 1);
concMap.put("B", 2);
for (Map.Entry<String, Integer> entry : concMap.entrySet()) {
    concMap.put("C", 3); // ✅ No exception — works on segment view
}
```

#### Immutable Collections (Java 9+)

```java
// Unmodifiable views (Java 2+) — still backed by mutable original
List<String> mutable = new ArrayList<>(List.of("A", "B"));
List<String> view = Collections.unmodifiableList(mutable);
mutable.add("C"); // Changes are visible through the view!

// Truly Immutable (Java 9+) — no backing collection
List<String> immutable = List.of("A", "B", "C");           // Immutable
Set<String> immutableSet = Set.of("A", "B", "C");          // Immutable, no duplicates
Map<String, Integer> immutableMap = Map.of("A", 1, "B", 2); // Immutable

// Java 10+ — copy to immutable
List<String> copy = List.copyOf(mutableList); // Immutable copy

// Java 16+ — toList() on streams
List<String> streamResult = stream.toList(); // Returns unmodifiable list

// Interview tip: Immutable collections have these properties:
// 1. No null elements (NPE on creation)
// 2. Structurally immutable (no add/remove/set)
// 3. If elements are immutable, the whole collection is thread-safe
// 4. Serializable (if elements are)
```

---

## 15.2 Java Stream API Deep Dive

### The "Why" & The Problem

Streams are the modern way to process collections in Java. Interviewers expect you to:
- Write complex pipelines fluently
- Understand lazy evaluation and short-circuiting
- Know when parallel streams help vs hurt
- Write custom Collectors

### The Deep Dive & Solution

#### Stream Pipeline Fundamentals

```java
// A stream pipeline has three parts:
// 1. SOURCE: collection, array, generator, I/O channel
// 2. INTERMEDIATE OPERATIONS: lazy, return a stream (filter, map, flatMap, sorted, distinct, peek)
// 3. TERMINAL OPERATION: triggers execution (collect, forEach, reduce, count, findFirst, anyMatch)

List<Order> orders = getOrders();

// Complex pipeline example:
Map<String, Double> avgOrderByCity = orders.stream()
    .filter(o -> o.getStatus() == OrderStatus.COMPLETED)     // Intermediate (lazy)
    .filter(o -> o.getAmount().compareTo(BigDecimal.ZERO) > 0)
    .collect(Collectors.groupingBy(                            // Terminal (triggers execution)
        Order::getCity,
        Collectors.averagingDouble(o -> o.getAmount().doubleValue())
    ));
```

#### The Four Collector Categories You Must Know

```java
// 1. REDUCING COLLECTORS
long count = orders.stream().count();
Optional<Order> max = orders.stream().max(Comparator.comparing(Order::getAmount));
double sum = orders.stream().mapToDouble(o -> o.getAmount().doubleValue()).sum();
DoubleSummaryStatistics stats = orders.stream()
    .mapToDouble(o -> o.getAmount().doubleValue()).summaryStatistics();
// stats.getAverage(), stats.getMax(), stats.getMin(), stats.getCount(), stats.getSum()

// 2. GROUPING COLLECTORS
Map<OrderStatus, List<Order>> byStatus = orders.stream()
    .collect(Collectors.groupingBy(Order::getStatus));

// Multi-level grouping:
Map<String, Map<OrderStatus, List<Order>>> byCityAndStatus = orders.stream()
    .collect(Collectors.groupingBy(Order::getCity,
             Collectors.groupingBy(Order::getStatus)));

// Grouping with downstream collector:
Map<String, Long> countByCity = orders.stream()
    .collect(Collectors.groupingBy(Order::getCity, Collectors.counting()));

Map<String, Optional<Order>> maxOrderByCity = orders.stream()
    .collect(Collectors.groupingBy(Order::getCity,
             Collectors.maxBy(Comparator.comparing(Order::getAmount))));

// 3. PARTITIONING (special case of grouping with boolean key)
Map<Boolean, List<Order>> partitioned = orders.stream()
    .collect(Collectors.partitioningBy(o -> o.getAmount().compareTo(new BigDecimal("100")) > 0));
List<Order> highValue = partitioned.get(true);
List<Order> lowValue = partitioned.get(false);

// 4. STRING JOINING
String cityList = orders.stream()
    .map(Order::getCity)
    .distinct()
    .collect(Collectors.joining(", ", "Cities: [", "]"));
// Output: "Cities: [Mumbai, Delhi, Bangalore]"
```

#### flatMap — Flattening Nested Structures

```java
// Problem: You have a list of orders, each with a list of items
// You want all items across all orders

List<OrderItem> allItems = orders.stream()
    .flatMap(order -> order.getItems().stream())  // Stream<Order> → Stream<OrderItem>
    .collect(Collectors.toList());

// Count items per category across all orders:
Map<String, Long> itemsByCategory = orders.stream()
    .flatMap(o -> o.getItems().stream())
    .collect(Collectors.groupingBy(OrderItem::getCategory, Collectors.counting()));

// Real-world: Flatten nested Optional
Optional<String> customerCity = getOrder(orderId)
    .flatMap(Order::getCustomer)     // Optional<Customer>
    .flatMap(Customer::getAddress)   // Optional<Address>
    .map(Address::getCity);          // Optional<String>
```

#### Custom Collector

```java
// Custom collector to collect into an ImmutableList (Guava-style)
public class ImmutableListCollector<T> implements Collector<T, List<T>, List<T>> {
    @Override
    public Supplier<List<T>> supplier() {
        return ArrayList::new;
    }
    
    @Override
    public BiConsumer<List<T>, T> accumulator() {
        return List::add;
    }
    
    @Override
    public BinaryOperator<List<T>> combiner() {
        return (left, right) -> {
            left.addAll(right);
            return left;
        };
    }
    
    @Override
    public Function<List<T>, List<T>> finisher() {
        return Collections::unmodifiableList;
    }
    
    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(); // No IDENTITY_FINISH, not UNORDERED, not CONCURRENT
    }
}

// Usage:
List<String> immutableNames = names.stream()
    .filter(n -> n.length() > 3)
    .collect(new ImmutableListCollector<>());
```

#### Parallel Streams — When to Use and When to Avoid

```java
// Parallel streams use the common ForkJoinPool
// Default parallelism = Runtime.getRuntime().availableProcessors() - 1

// ✅ WHEN PARALLEL STREAMS HELP:
// 1. Large datasets (>10,000 elements)
// 2. CPU-bound operations (computation, not I/O)
// 3. Stateless, non-interfering operations
// 4. Splittable data sources (ArrayList, arrays — NOT LinkedList)

long sum = LongStream.rangeClosed(1, 10_000_000)
    .parallel()
    .sum(); // ✅ CPU-bound, large dataset, easily splittable

// ❌ WHEN PARALLEL STREAMS HURT:
// 1. Small datasets (overhead > benefit)
// 2. I/O-bound operations (network calls, DB queries)
// 3. Operations with shared mutable state
// 4. LinkedList or other poorly-splittable sources
// 5. Operations that require ordering (findFirst in parallel = expensive)

// ❌ DANGEROUS: Shared mutable state
List<Integer> results = new ArrayList<>(); // NOT thread-safe!
IntStream.range(0, 1000).parallel()
    .forEach(results::add); // Race condition! Missing elements, ArrayIndexOutOfBounds

// ✅ SAFE: Use collect() instead
List<Integer> results = IntStream.range(0, 1000).parallel()
    .boxed()
    .collect(Collectors.toList()); // Thread-safe collection

// Custom ForkJoinPool for parallel stream (avoid starving the common pool):
ForkJoinPool customPool = new ForkJoinPool(4);
List<String> result = customPool.submit(() ->
    largeList.parallelStream()
        .filter(s -> expensiveCheck(s))
        .collect(Collectors.toList())
).get();
customPool.shutdown();
```

#### Java 16+ Stream Enhancements

```java
// toList() — unmodifiable list (Java 16+)
List<String> names = users.stream()
    .map(User::getName)
    .toList(); // Simpler than collect(Collectors.toList())

// mapMulti (Java 16+) — alternative to flatMap for simple cases
Stream<Integer> flatMapped = orders.stream()
    .<Integer>mapMulti((order, consumer) -> {
        for (OrderItem item : order.getItems()) {
            consumer.accept(item.getQuantity());
        }
    });

// Gatherers (Java 22+ preview) — custom intermediate operations
// Example: sliding window
orders.stream()
    .gather(Gatherers.windowSliding(3)) // Groups of 3 consecutive elements
    .forEach(window -> System.out.println("Window: " + window));
```

---

## 15.3 Functional Programming in Java

### The Deep Dive & Solution

#### Core Functional Interfaces

```java
// Java provides 43 functional interfaces in java.util.function
// The FOUR you must know:

// 1. Function<T, R> — takes T, returns R
Function<String, Integer> strLength = String::length;
Function<String, String> toUpper = String::toUpperCase;

// Composition:
Function<String, Integer> upperThenLength = toUpper.andThen(strLength);
int len = upperThenLength.apply("hello"); // 5

// 2. Predicate<T> — takes T, returns boolean
Predicate<String> isLong = s -> s.length() > 5;
Predicate<String> startsWithA = s -> s.startsWith("A");

// Composition:
Predicate<String> longAndStartsWithA = isLong.and(startsWithA);
Predicate<String> shortOrStartsWithA = isLong.negate().or(startsWithA);

// 3. Consumer<T> — takes T, returns void
Consumer<String> printer = System.out::println;
Consumer<String> logger = s -> log.info("Processing: {}", s);

// Composition:
Consumer<String> printAndLog = printer.andThen(logger);

// 4. Supplier<T> — takes nothing, returns T
Supplier<LocalDateTime> now = LocalDateTime::now;
Supplier<UUID> idGenerator = UUID::randomUUID;
Supplier<List<String>> listFactory = ArrayList::new;
```

#### Optional — The Right Way

```java
// Optional is NOT a replacement for null checks everywhere
// It's for return types that may have no meaningful value

// ✅ GOOD: Return type for "might not exist" scenarios
public Optional<User> findById(String id) {
    return Optional.ofNullable(userRepository.findById(id));
}

// ✅ GOOD: Chaining transformations
String cityName = findById("123")
    .flatMap(User::getAddress)       // Optional<Address>
    .map(Address::getCity)           // Optional<String>
    .orElse("Unknown");              // String

// ✅ GOOD: orElseGet for expensive defaults (lazy evaluation)
User user = findById("123")
    .orElseGet(() -> createDefaultUser()); // Only called if empty

// ✅ GOOD: orElseThrow (Java 10+ no-arg version)
User user = findById("123")
    .orElseThrow(); // Throws NoSuchElementException

// ❌ BAD: Optional as method parameter
public void processUser(Optional<User> user) { } // Don't do this

// ❌ BAD: Optional for class fields
public class Order {
    private Optional<Discount> discount; // Don't do this — use null
}

// ❌ BAD: isPresent() + get() — defeats the purpose
if (optional.isPresent()) {
    return optional.get(); // Just use orElse, map, etc.
}

// ✅ Java 9+ additions:
optional.ifPresentOrElse(
    user -> processUser(user),    // If present
    () -> handleMissingUser()     // If empty
);

optional.or(() -> findByEmail(email)); // Chain with another Optional
optional.stream(); // Convert to Stream (0 or 1 elements)
```

---

## 15.4 Concurrency — Locks & Synchronizers

### The "Why" & The Problem

`synchronized` is the simplest concurrency tool, but it has limitations:
- No fairness guarantee
- No interruptible lock acquisition
- No try-lock with timeout
- No read/write separation

For production systems, you need advanced locks and synchronizers.

### The Deep Dive & Solution

#### synchronized vs ReentrantLock

```java
// synchronized — Intrinsic lock, simple but limited
public class Counter {
    private int count = 0;
    
    public synchronized void increment() { // Lock on 'this'
        count++;
    }
    
    public synchronized int getCount() { // Same lock — blocks even reads
        return count;
    }
}

// ReentrantLock — Explicit lock, more features
public class BetterCounter {
    private int count = 0;
    private final ReentrantLock lock = new ReentrantLock(true); // fair = true
    
    public void increment() {
        lock.lock();
        try {
            count++;
        } finally {
            lock.unlock(); // ALWAYS in finally block
        }
    }
    
    // Try-lock with timeout — no deadlock risk
    public boolean tryIncrement(long timeout, TimeUnit unit) throws InterruptedException {
        if (lock.tryLock(timeout, unit)) {
            try {
                count++;
                return true;
            } finally {
                lock.unlock();
            }
        }
        return false; // Could not acquire lock in time
    }
    
    // Interruptible lock — can be interrupted while waiting
    public void incrementInterruptibly() throws InterruptedException {
        lock.lockInterruptibly();
        try {
            count++;
        } finally {
            lock.unlock();
        }
    }
}
```

#### ReadWriteLock — Concurrent Reads, Exclusive Writes

```java
// Problem: A cache read by many threads but written rarely
// synchronized blocks ALL access — even concurrent reads
// ReadWriteLock allows multiple concurrent readers OR one exclusive writer

public class ThreadSafeCache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();
    
    public V get(K key) {
        readLock.lock(); // Multiple threads can hold read lock simultaneously
        try {
            return cache.get(key);
        } finally {
            readLock.unlock();
        }
    }
    
    public void put(K key, V value) {
        writeLock.lock(); // Exclusive — blocks all readers and writers
        try {
            cache.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }
    
    public V computeIfAbsent(K key, Function<K, V> loader) {
        // First try with read lock (optimistic)
        readLock.lock();
        try {
            V value = cache.get(key);
            if (value != null) return value;
        } finally {
            readLock.unlock();
        }
        
        // Cache miss — need write lock
        writeLock.lock();
        try {
            // Double-check (another thread might have loaded it)
            V value = cache.get(key);
            if (value != null) return value;
            
            value = loader.apply(key);
            cache.put(key, value);
            return value;
        } finally {
            writeLock.unlock();
        }
    }
}
```

#### StampedLock (Java 8+) — Optimistic Reading

```java
// StampedLock adds optimistic reading — no lock acquisition for reads
// If data hasn't changed, the optimistic read succeeds without blocking
// Useful for read-heavy workloads with rare writes

public class Point {
    private double x, y;
    private final StampedLock lock = new StampedLock();
    
    public void move(double deltaX, double deltaY) {
        long stamp = lock.writeLock(); // Exclusive write
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            lock.unlockWrite(stamp);
        }
    }
    
    public double distanceFromOrigin() {
        long stamp = lock.tryOptimisticRead(); // No lock — just get a stamp
        double currentX = x, currentY = y;     // Read the values
        
        if (!lock.validate(stamp)) {           // Check if a write happened
            // Optimistic read failed — fall back to pessimistic read lock
            stamp = lock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                lock.unlockRead(stamp);
            }
        }
        
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }
}

// When to use StampedLock vs ReadWriteLock:
// StampedLock: Read-heavy, short read operations, can tolerate retry
// ReadWriteLock: Read-heavy but reads are longer, simpler to reason about
// StampedLock is NOT reentrant — cannot acquire a write lock inside a read lock
```

#### Synchronizers — CountDownLatch, CyclicBarrier, Semaphore, Phaser

```java
// === CountDownLatch ===
// One-shot barrier: wait for N events to complete
// Use case: Wait for multiple services to initialize before starting

public class ApplicationStartup {
    private final CountDownLatch latch;
    
    public ApplicationStartup(int serviceCount) {
        this.latch = new CountDownLatch(serviceCount);
    }
    
    // Each service calls this when ready
    public void serviceReady(String serviceName) {
        System.out.println(serviceName + " is ready");
        latch.countDown(); // Decrement count
    }
    
    // Main thread waits here
    public void awaitAllServices(long timeout, TimeUnit unit) throws InterruptedException {
        if (!latch.await(timeout, unit)) {
            throw new TimeoutException("Not all services started in time");
        }
        System.out.println("All services ready — starting application");
    }
}

// Usage:
ApplicationStartup startup = new ApplicationStartup(3);
executor.submit(() -> { initDB(); startup.serviceReady("Database"); });
executor.submit(() -> { initCache(); startup.serviceReady("Cache"); });
executor.submit(() -> { initMQ(); startup.serviceReady("MessageQueue"); });
startup.awaitAllServices(30, TimeUnit.SECONDS);
```

```java
// === CyclicBarrier ===
// Reusable barrier: N threads wait for each other, then all proceed
// Use case: Parallel computation phases (all threads must finish phase N before starting N+1)

public class ParallelMatrixComputation {
    private final CyclicBarrier barrier;
    
    public ParallelMatrixComputation(int threadCount) {
        this.barrier = new CyclicBarrier(threadCount, () -> {
            // This runs ONCE after all threads reach the barrier
            System.out.println("Phase complete — merging results");
        });
    }
    
    public void computePartition(int[][] matrix, int startRow, int endRow) {
        try {
            // Phase 1: Local computation
            computeLocally(matrix, startRow, endRow);
            barrier.await(); // Wait for all threads to finish phase 1
            
            // Phase 2: Exchange results with neighbors
            exchangeResults(matrix, startRow, endRow);
            barrier.await(); // Wait for all threads to finish phase 2
            
            // Phase 3: Final computation
            finalizeResults(matrix, startRow, endRow);
            barrier.await(); // All done
        } catch (InterruptedException | BrokenBarrierException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

```java
// === Semaphore ===
// Controls access to N permits (rate limiting, connection pooling)
// Use case: Limit concurrent database connections

public class ConnectionPool {
    private final Semaphore semaphore;
    private final BlockingQueue<Connection> pool;
    
    public ConnectionPool(int maxConnections) {
        this.semaphore = new Semaphore(maxConnections, true); // fair
        this.pool = new LinkedBlockingQueue<>(maxConnections);
        for (int i = 0; i < maxConnections; i++) {
            pool.offer(createConnection());
        }
    }
    
    public Connection acquire(long timeout, TimeUnit unit) throws InterruptedException {
        if (!semaphore.tryAcquire(timeout, unit)) {
            throw new TimeoutException("Cannot acquire connection — pool exhausted");
        }
        return pool.poll(); // Guaranteed to succeed (semaphore guards it)
    }
    
    public void release(Connection conn) {
        pool.offer(conn);
        semaphore.release();
    }
}
```

```java
// === Phaser ===
// Dynamic barrier: threads can register/deregister at any time
// Use case: Dynamic workloads where thread count changes between phases

Phaser phaser = new Phaser(1); // Register self as coordinator

for (int i = 0; i < workerCount; i++) {
    phaser.register(); // Dynamic registration
    executor.submit(() -> {
        try {
            // Phase 0
            doPhaseWork(0);
            phaser.arriveAndAwaitAdvance(); // Barrier
            
            // Phase 1
            doPhaseWork(1);
            phaser.arriveAndAwaitAdvance(); // Barrier
            
            // Done — deregister
            phaser.arriveAndDeregister();
        } catch (Exception e) {
            phaser.arriveAndDeregister(); // Always deregister on failure
        }
    });
}

phaser.arriveAndDeregister(); // Coordinator deregisters
```

#### Synchronizer Comparison Table

```
| Feature              | CountDownLatch | CyclicBarrier | Semaphore | Phaser     |
|---------------------|----------------|---------------|-----------|------------|
| Reusable?           | No (one-shot)  | Yes           | Yes       | Yes        |
| Dynamic parties?    | No             | No            | N/A       | Yes        |
| Reset?              | No             | Yes (reset()) | N/A       | Auto       |
| Action on complete? | No             | Yes (Runnable)| No        | Yes        |
| Use case            | Wait for N     | N wait for    | Limit     | Dynamic    |
|                     | events         | each other    | access    | phases     |
| Thread waits?       | await()        | await()       | acquire() | arrive*()  |
| Signal complete?    | countDown()    | await()       | release() | arrive*()  |
```

---

## 15.5 ConcurrentHashMap Internals

### The "Why" & The Problem

This is a **top 5 most-asked Java question** at FAANG. You must understand how ConcurrentHashMap achieves thread safety without synchronizing the entire map.

### The Deep Dive & Solution

#### Java 7 vs Java 8 ConcurrentHashMap

```java
// Java 7: Segment-based locking
// - Map divided into 16 segments (by default)
// - Each segment is independently lockable
// - Reads don't need locks (volatile reads)
// - Writes lock only the affected segment
// - Concurrency level = number of segments (default 16)
// - Problem: Fixed number of segments, segment array wasted memory

// Java 8+: Node-level locking with CAS + synchronized
// - No more Segments — uses an array of Node (like HashMap)
// - Each bucket is independently lockable (synchronized on first node)
// - Uses CAS (Compare-And-Swap) for lock-free operations where possible
// - Reads are lock-free (volatile node.val)
// - Empty bucket: CAS to insert first node
// - Non-empty bucket: synchronized on the first node of that bucket
// - Also treeifies at threshold 8 (like HashMap)
```

#### Key ConcurrentHashMap Operations

```java
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Atomic compound operations (not possible with HashMap):
map.putIfAbsent("key", 1);                        // Put only if key doesn't exist
map.computeIfAbsent("key", k -> expensiveCalc()); // Compute only if absent
map.computeIfPresent("key", (k, v) -> v + 1);     // Update only if present
map.merge("key", 1, Integer::sum);                 // Merge: put if absent, apply function if present

// Thread-safe counter pattern:
ConcurrentHashMap<String, LongAdder> counters = new ConcurrentHashMap<>();
counters.computeIfAbsent("page_views", k -> new LongAdder()).increment();

// Bulk operations (Java 8+) — parallel with threshold:
// parallelismThreshold: if map size > threshold, operations run in parallel
long sum = map.reduceValuesToLong(
    1,              // parallelismThreshold (1 = always parallel)
    Integer::longValue,
    0L,
    Long::sum
);

map.forEach(1, (key, value) -> {
    System.out.println(key + " = " + value); // Parallel forEach
});

String maxKey = map.reduceEntries(1,
    (e1, e2) -> e1.getValue() > e2.getValue() ? e1 : e2
).getKey();
```

#### ConcurrentHashMap vs Other Thread-Safe Maps

```
| Feature                  | Hashtable       | synchronizedMap  | ConcurrentHashMap |
|-------------------------|-----------------|------------------|-------------------|
| Locking                 | Entire table    | Entire map       | Per-bucket (Java 8)|
| Null keys/values        | No              | Yes              | No                |
| Iterator                | Fail-fast       | Fail-fast        | Weakly consistent |
| Concurrent reads        | Blocked         | Blocked          | Lock-free         |
| Compound operations     | Not atomic      | Not atomic       | Atomic (compute*) |
| Performance (high conc.)| Poor            | Poor             | Excellent         |
| Java version            | 1.0             | 1.2              | 1.5 (rewritten 1.8)|
```

---

## 15.6 Atomic Variables & CAS (Compare-And-Swap)

### The Deep Dive & Solution

```java
// Atomic classes use CAS (hardware-level atomic instruction)
// No locks needed — "lock-free" programming

// Basic Atomics:
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();    // Atomic i++
counter.getAndIncrement();    // Atomic i++ (returns old value)
counter.compareAndSet(5, 10); // If current == 5, set to 10 (returns true/false)
counter.updateAndGet(x -> x * 2); // Atomic transformation

// AtomicReference — for atomic reference swaps
AtomicReference<Node> head = new AtomicReference<>(null);
// Lock-free stack push:
public void push(E item) {
    Node newHead = new Node(item);
    Node oldHead;
    do {
        oldHead = head.get();
        newHead.next = oldHead;
    } while (!head.compareAndSet(oldHead, newHead)); // Retry if another thread changed head
}

// LongAdder (Java 8+) — faster than AtomicLong under high contention
// Maintains multiple cells, threads update different cells, sum on demand
LongAdder adder = new LongAdder();
adder.increment();     // Fast — updates a random cell
adder.add(10);
long total = adder.sum(); // Aggregates all cells

// When to use what:
// AtomicInteger/Long: Low-to-moderate contention, need exact value frequently
// LongAdder/LongAccumulator: High contention, infrequent reads (counters, metrics)
// AtomicReference: Lock-free data structures (stacks, queues)
```

---

## 15.7 CompletableFuture Advanced Patterns

### The Deep Dive & Solution

```java
// === Fan-Out / Fan-In Pattern ===
// Call multiple services in parallel, combine results

public OrderDetails getOrderDetails(String orderId) {
    CompletableFuture<Order> orderFuture = CompletableFuture
        .supplyAsync(() -> orderService.getOrder(orderId));
    
    CompletableFuture<Customer> customerFuture = CompletableFuture
        .supplyAsync(() -> customerService.getCustomer(orderId));
    
    CompletableFuture<List<Payment>> paymentsFuture = CompletableFuture
        .supplyAsync(() -> paymentService.getPayments(orderId));
    
    CompletableFuture<ShippingInfo> shippingFuture = CompletableFuture
        .supplyAsync(() -> shippingService.getShipping(orderId));
    
    // Wait for all to complete and combine:
    return CompletableFuture.allOf(orderFuture, customerFuture, paymentsFuture, shippingFuture)
        .thenApply(ignored -> new OrderDetails(
            orderFuture.join(),
            customerFuture.join(),
            paymentsFuture.join(),
            shippingFuture.join()
        ))
        .orTimeout(5, TimeUnit.SECONDS)        // Timeout (Java 9+)
        .exceptionally(ex -> {
            log.error("Failed to get order details", ex);
            return OrderDetails.fallback(orderId);
        })
        .join();
}

// === First-Wins Pattern ===
// Call multiple providers, use whichever responds first

public Price getBestPrice(String productId) {
    CompletableFuture<Price> provider1 = CompletableFuture
        .supplyAsync(() -> priceProvider1.getPrice(productId));
    CompletableFuture<Price> provider2 = CompletableFuture
        .supplyAsync(() -> priceProvider2.getPrice(productId));
    CompletableFuture<Price> provider3 = CompletableFuture
        .supplyAsync(() -> priceProvider3.getPrice(productId));
    
    return CompletableFuture.anyOf(provider1, provider2, provider3)
        .thenApply(result -> (Price) result)
        .orTimeout(3, TimeUnit.SECONDS)
        .join();
}

// === Pipeline Pattern ===
// Sequential transformations with async I/O at each stage

public String processDocument(String docId) {
    return CompletableFuture
        .supplyAsync(() -> downloadDocument(docId))           // I/O
        .thenApplyAsync(doc -> parseDocument(doc))            // CPU
        .thenApplyAsync(parsed -> enrichWithMetadata(parsed)) // I/O
        .thenApplyAsync(enriched -> validateDocument(enriched))// CPU
        .thenApplyAsync(valid -> storeDocument(valid))        // I/O
        .handle((result, ex) -> {
            if (ex != null) {
                log.error("Document processing failed for {}", docId, ex);
                return "FAILED";
            }
            return result;
        })
        .join();
}

// === Retry Pattern with CompletableFuture ===
public <T> CompletableFuture<T> retryAsync(
        Supplier<CompletableFuture<T>> operation, 
        int maxRetries, 
        Duration delay) {
    
    return operation.get().handle((result, ex) -> {
        if (ex == null) return CompletableFuture.completedFuture(result);
        if (maxRetries <= 0) return CompletableFuture.<T>failedFuture(ex);
        
        return CompletableFuture
            .delayedExecutor(delay.toMillis(), TimeUnit.MILLISECONDS)
            .execute(() -> {}) // Delay
            .thenCompose(ignored -> retryAsync(operation, maxRetries - 1, delay.multipliedBy(2)));
    }).thenCompose(Function.identity());
}
```

---

## 15.8 Thread Pool Configuration — The Interview Answer

### The Deep Dive & Solution

```java
// ThreadPoolExecutor has 7 parameters — you MUST know all of them:

ThreadPoolExecutor executor = new ThreadPoolExecutor(
    4,                              // corePoolSize: threads always alive
    16,                             // maxPoolSize: maximum threads
    60L, TimeUnit.SECONDS,          // keepAliveTime: idle thread timeout (for threads > core)
    new ArrayBlockingQueue<>(100),  // workQueue: bounded queue
    new ThreadFactory() {           // threadFactory: naming for debugging
        private final AtomicInteger count = new AtomicInteger(0);
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "order-processor-" + count.incrementAndGet());
            t.setDaemon(false);
            return t;
        }
    },
    new ThreadPoolExecutor.CallerRunsPolicy() // rejectionHandler
);

// HOW THE POOL WORKS (interview explanation):
// 1. New task arrives
// 2. If activeThreads < corePoolSize → create new thread
// 3. If activeThreads >= corePoolSize → put in queue
// 4. If queue is full AND activeThreads < maxPoolSize → create new thread
// 5. If queue is full AND activeThreads >= maxPoolSize → reject (RejectionHandler)

// REJECTION POLICIES:
// AbortPolicy (default): throws RejectedExecutionException
// CallerRunsPolicy: caller's thread executes the task (back-pressure)
// DiscardPolicy: silently drops the task
// DiscardOldestPolicy: drops the oldest queued task, then retries

// SIZING GUIDELINES:
// CPU-bound tasks: corePoolSize = Runtime.getRuntime().availableProcessors()
// I/O-bound tasks: corePoolSize = processors × (1 + waitTime/computeTime)
//   e.g., 8 cores, 90% wait time → 8 × (1 + 9) = 80 threads

// SPRING BOOT CONFIGURATION:
@Bean
public TaskExecutor orderProcessingExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(8);
    executor.setMaxPoolSize(32);
    executor.setQueueCapacity(200);
    executor.setThreadNamePrefix("order-");
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    executor.setWaitForTasksToCompleteOnShutdown(true);
    executor.setAwaitTerminationSeconds(30);
    executor.initialize();
    return executor;
}
```

---

## 15.9 Interview Quick Reference — Collections & Concurrency

### Top Questions and One-Line Answers

| Question | Answer |
|----------|--------|
| HashMap vs Hashtable? | HashMap: allows null, not synchronized, uses chaining+treeification. Hashtable: no null, synchronized (slow), legacy. |
| HashMap vs ConcurrentHashMap? | HashMap: not thread-safe. CHM: per-bucket locking, no null, atomic compound ops. |
| ArrayList vs LinkedList? | ArrayList: O(1) random access, O(n) insert. LinkedList: O(n) access, O(1) insert at known position. ArrayList wins 99% of the time (cache locality). |
| HashSet implementation? | HashSet is backed by a HashMap where values are a dummy constant Object. |
| TreeMap vs HashMap? | TreeMap: sorted keys (Red-Black Tree, O(log n)). HashMap: unsorted (O(1)). |
| How HashMap resizes? | When size > capacity×loadFactor, doubles capacity, rehashes all entries (O(n)). |
| Why 0.75 load factor? | Balance between space (lower = more space) and time (higher = more collisions). |
| fail-fast vs fail-safe? | fail-fast: ConcurrentModificationException on structural change during iteration. fail-safe: works on copy/snapshot. |
| volatile vs AtomicInteger? | volatile: visibility only. AtomicInteger: visibility + atomicity (CAS-based). |
| synchronized vs ReentrantLock? | ReentrantLock: fairness, tryLock, interruptible, multiple Conditions. |
| CountDownLatch vs CyclicBarrier? | CDL: one-shot, wait for N events. CB: reusable, N threads wait for each other. |
| When to use parallel streams? | Large datasets (>10K), CPU-bound, splittable source, no shared mutable state. |
| CompletableFuture vs Future? | CF: chaining, combining, exception handling, async callbacks. Future: just get(). |
| LongAdder vs AtomicLong? | LongAdder: faster under high contention (multiple cells). AtomicLong: single cell. |

### Keywords to Use in Interviews

```
Collections: Treeification, Load Factor 0.75, Power-of-2 capacity, Fail-fast/fail-safe,
             Weakly consistent, Backing array, Red-Black Tree, NavigableMap

Concurrency: CAS (Compare-And-Swap), Happens-Before, Memory Visibility, Lock Striping,
             Optimistic Locking, Spin Lock, Work-Stealing, ForkJoinPool,
             Lock-free, Wait-free, Reentrant, Fairness, Starvation

Streams: Lazy Evaluation, Short-circuiting, Spliterator, Encounter Order,
         Reduction, Mutable Reduction, Collector Characteristics
```

