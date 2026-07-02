# Section 2: Advanced Spring Boot & Java Internals

> **You are here**: SDE1–SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [01_Modern_Java_Features.md](01_Modern_Java_Features.md) | **Next**: [03_Design_Patterns_SOLID_CleanArch.md](03_Design_Patterns_SOLID_CleanArch.md)

---

## 2.1 Java Memory Model: Heap vs. Stack, Garbage Collection, and Debugging Memory Leaks

---

### The "Why" & The Problem

Every Java application runs inside the JVM, and the JVM manages memory on your behalf. This sounds great until your production application starts consuming 8 GB of RAM, response times spike from 50ms to 5 seconds during GC pauses, and your on-call engineer gets a 3 AM PagerDuty alert because the pod got OOMKilled in Kubernetes.

A company pays you to know this because **memory mismanagement is the #1 silent killer of Java applications in production**. An engineer who cannot reason about heap allocation, GC behavior, and memory leaks will ship code that works in dev (with 500 requests/minute) and collapses in production (with 50,000 requests/minute). At Lead level, you are expected to not just fix these issues but to **architect systems that prevent them**, review code for memory anti-patterns, and make informed JVM tuning decisions.

The core production problems this knowledge solves:
- **OutOfMemoryError crashes** that take down entire services at 2 AM.
- **GC pause spikes** (Stop-The-World events) that cause latency violations in SLA-bound services.
- **Memory leaks** from improper caching, unclosed resources, or static collection references that slowly degrade a service over days/weeks.
- **Container memory limits** in Kubernetes where the JVM's memory behavior interacts with cgroup limits in non-obvious ways.

---

### Interviewer Expectations

The interviewer wants to hear you speak fluently about:

- **Heap vs. Stack**: Not just "objects go on the heap, primitives go on the stack" — they want to hear about **Escape Analysis**, how the JIT compiler can allocate objects on the stack if they don't escape the method scope, and how the stack is thread-local (and therefore thread-safe by default).
- **Heap Structure**: Young Generation (Eden + Survivor spaces S0/S1), Old Generation (Tenured), and Metaspace (replaced PermGen in Java 8+). Why this generational design exists (Weak Generational Hypothesis).
- **GC Algorithms**: Not just "G1GC is good" — explain the trade-offs. When do you pick G1GC vs. ZGC vs. Shenandoah? What are your tuning knobs?
- **Heap Dumps & Profiling**: How to capture them (`jmap`, `-XX:+HeapDumpOnOutOfMemoryError`), how to analyze them (Eclipse MAT, VisualVM), and what patterns indicate a leak (Dominator Tree analysis).
- **Keywords to use**: "Weak Generational Hypothesis", "Stop-The-World pause", "concurrent marking", "remembered sets", "TLAB (Thread-Local Allocation Buffer)", "Escape Analysis", "Dominator Tree", "GC roots", "retained heap vs. shallow heap".

---

### The Deep Dive & Solution

#### Heap vs. Stack — The Real Story

**The Stack** is a per-thread memory region. Every time a method is called, a new **stack frame** is pushed onto the thread's stack. This frame contains:
- **Local variables** (primitives like `int`, `boolean`, and **references** to objects — not the objects themselves).
- **Operand stack** (used for intermediate computations by the bytecode interpreter).
- **Return address** (where to go back after the method completes).

When the method returns, the stack frame is popped. This is extremely fast — there is no garbage collection involved. Stack memory is automatically reclaimed via the LIFO discipline.

**Key point**: The stack only holds **references** (pointers) to objects. The actual object data lives on the heap. However, modern JVMs (HotSpot with C2 JIT) perform **Escape Analysis**: if the JIT compiler proves that an object created inside a method never "escapes" that method (i.e., it is not returned, not stored in a field, not passed to another thread), it may allocate the object directly on the stack or even **scalar-replace** it (break the object into its individual fields and keep them in CPU registers).

```java
// Escape Analysis example
public int computeSum() {
    // The Point object may NEVER be allocated on the heap
    // if the JIT compiler determines it doesn't escape this method
    Point p = new Point(3, 4);  // candidate for stack allocation
    return p.x + p.y;
}
```

**Stack size** is configured with `-Xss` (default is typically 512KB–1MB per thread). If you have 500 threads, that is 500MB of stack memory alone. **StackOverflowError** occurs when recursion is too deep and the stack frames exceed this limit.

**The Heap** is the shared memory region where all Java objects live (unless optimized away by Escape Analysis). It is managed by the Garbage Collector. The heap is divided into generations based on the **Weak Generational Hypothesis**: "Most objects die young."

```
┌─────────────────────────────────────────────────────────┐
│                        HEAP                              │
│  ┌──────────────────────────────┐  ┌──────────────────┐ │
│  │      Young Generation         │  │  Old Generation  │ │
│  │  ┌───────┐ ┌────┐ ┌────┐    │  │   (Tenured)      │ │
│  │  │ Eden  │ │ S0 │ │ S1 │    │  │                  │ │
│  │  │       │ │    │ │    │    │  │  Long-lived       │ │
│  │  │ New   │ │From│ │ To │    │  │  objects          │ │
│  │  │objects│ │    │ │    │    │  │                  │ │
│  │  └───────┘ └────┘ └────┘    │  └──────────────────┘ │
│  └──────────────────────────────┘                       │
│                                                          │
│  ┌──────────────────────────────────────────────────────┐│
│  │  Metaspace (off-heap, native memory)                 ││
│  │  Class metadata, method bytecode, constant pools     ││
│  └──────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────┘
```

**Young Generation**:
- **Eden Space**: Where new objects are allocated (via TLABs — Thread-Local Allocation Buffers — to avoid contention). Each thread gets a small chunk of Eden to allocate into without synchronization.
- **Survivor Spaces (S0, S1)**: Objects that survive a Minor GC are copied between survivor spaces. After surviving a configurable number of Minor GCs (the **tenuring threshold**, `-XX:MaxTenuringThreshold`), they are promoted to Old Generation.
- **Minor GC**: Triggered when Eden is full. It is a **copying collector** — live objects are copied from Eden + one survivor space to the other survivor space. Dead objects are simply abandoned (no explicit deallocation). Minor GCs are fast because most young objects are already dead (Weak Generational Hypothesis).

**Old Generation (Tenured)**:
- Objects that have survived enough Minor GC cycles live here.
- **Major GC / Full GC**: Triggered when Old Gen fills up. These are much more expensive because Old Gen is larger and objects there are more likely to be alive.

**Metaspace** (Java 8+, replaces PermGen):
- Stores class metadata, interned strings (partially), and compiled code.
- Lives in **native memory** (not on the Java heap). It can grow dynamically but can be capped with `-XX:MaxMetaspaceSize`.
- **ClassLoader leaks** (common in application servers with hot-deploy) cause Metaspace to grow unboundedly.

#### Garbage Collection Deep Dive

**The fundamental GC process**:
1. **Mark**: Starting from "GC roots" (stack variables, static fields, JNI references), traverse the object graph and mark all reachable objects as "alive."
2. **Sweep / Compact / Copy**: Reclaim memory occupied by unmarked (dead) objects.

**GC Roots** include:
- Local variables and parameters on thread stacks.
- Active threads themselves.
- Static fields of loaded classes.
- JNI references.

#### G1GC (Garbage-First Garbage Collector) — Default since Java 9

**How it works**:
G1GC divides the heap into **equal-sized regions** (typically 1MB–32MB each). Each region can be Eden, Survivor, Old, or Humongous (for objects > 50% of region size). This is a departure from the traditional contiguous young/old layout.

```
┌────┬────┬────┬────┬────┬────┬────┬────┐
│ E  │ E  │ S  │ O  │ O  │ H  │ E  │ O  │  <- Region types
├────┼────┼────┼────┼────┼────┼────┼────┤     E=Eden, S=Survivor
│ O  │ O  │ E  │Free│ O  │ O  │Free│ S  │     O=Old, H=Humongous
├────┼────┼────┼────┼────┼────┼────┼────┤     Free=Available
│Free│ O  │ O  │ E  │Free│Free│ O  │ E  │
└────┴────┴────┴────┴────┴────┴────┴────┘
```

**Key G1GC phases**:
1. **Young-only Collection**: Collects Eden + Survivor regions (STW pause). Copies live objects to new Survivor regions or promotes to Old regions.
2. **Concurrent Marking Cycle**: Triggered when Old Gen occupancy exceeds `InitiatingHeapOccupancyPercent` (IHOP, default ~45%). This runs **concurrently** with the application:
   - Initial Mark (STW — piggybacks on a Young GC)
   - Concurrent Mark (concurrent with app threads)
   - Remark (STW — finalizes marking, processes SATB buffers)
   - Cleanup (STW — identifies empty regions, sorts regions by garbage ratio)
3. **Mixed Collections**: After concurrent marking, G1 knows which Old regions have the most garbage. It collects Young regions PLUS selected Old regions with the highest garbage ratio ("Garbage First" — hence the name). This avoids a full collection of the entire Old Gen.

**G1GC tuning knobs**:
- `-XX:MaxGCPauseMillis=200` — Target pause time (G1 will try to meet this by adjusting how many regions it collects).
- `-XX:G1HeapRegionSize=16m` — Region size.
- `-XX:InitiatingHeapOccupancyPercent=45` — When to start concurrent marking.
- `-XX:G1ReservePercent=10` — Reserve heap percentage for promotion failures.

**When to use G1GC**: General-purpose. Good for heap sizes of 4GB–64GB. Balances throughput and latency. Default choice for most applications.

#### ZGC (Z Garbage Collector) — Production-ready since Java 15, Generational ZGC since Java 21

**How it works**:
ZGC is a **concurrent, region-based, NUMA-aware** collector designed for **ultra-low latency**. Its defining feature: **GC pauses are sub-millisecond (typically < 1ms) and do NOT scale with heap size**. You can have a 16TB heap with the same pause time as a 256MB heap.

**The trick**: ZGC uses **colored pointers** (also called "pointer tagging"). It uses unused bits in 64-bit pointers to store GC metadata (marked, remapped, finalizable). This allows ZGC to relocate objects concurrently — when a thread reads an object reference, a **load barrier** checks the colored pointer bits and, if the reference is stale (pointing to a relocated object), it transparently fixes the reference. This is called **self-healing**.

**ZGC phases** (all concurrent except for two very brief STW pauses):
1. **Pause Mark Start** (STW, ~microseconds): Scan thread stacks for GC roots.
2. **Concurrent Mark/Remap**: Traverse object graph, mark live objects, remap relocated references.
3. **Pause Mark End** (STW, ~microseconds): Handle edge cases in marking.
4. **Concurrent Prepare for Relocate**: Select regions to compact.
5. **Pause Relocate Start** (STW, ~microseconds): Scan roots for relocation.
6. **Concurrent Relocate**: Move objects to new locations. Application threads that access relocated objects fix their references via load barriers.

**Generational ZGC (Java 21+)**: Adds generational collection to ZGC, improving throughput by collecting young objects more frequently and independently of old objects.

**When to use ZGC**: 
- Latency-critical applications (trading systems, real-time bidding, gaming backends).
- Very large heaps (hundreds of GB to TB).
- When you cannot tolerate > 1ms GC pauses.

**Trade-off**: ZGC has slightly lower throughput than G1GC due to load barrier overhead. It also uses more memory (colored pointers require extra metadata).

```
// JVM flags comparison
// G1GC (default, balanced)
-XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Xms4g -Xmx4g

// ZGC (ultra-low latency)
-XX:+UseZGC -XX:+ZGenerational -Xms8g -Xmx8g

// Shenandoah (alternative low-latency, available in OpenJDK)
-XX:+UseShenandoahGC -Xms4g -Xmx4g
```

#### Debugging Memory Leaks with Heap Dumps

**How memory leaks happen in Java**: A memory leak occurs when objects are no longer needed by the application logic but are still reachable from GC roots, preventing the garbage collector from reclaiming them.

**Common causes**:
1. **Static collections that grow forever**: `static Map<String, Object> cache = new HashMap<>();` — objects added but never removed.
2. **Listeners/Callbacks not deregistered**: Registering an event listener on a long-lived object but never removing it.
3. **Unclosed resources**: `InputStream`, `Connection`, `Session` objects not closed in `finally` blocks or try-with-resources.
4. **ThreadLocal variables**: In thread pools (like Tomcat's), threads are reused. If `ThreadLocal` is not cleaned up, the referenced object lives as long as the thread.
5. **ClassLoader leaks**: In application servers, redeploying an app without properly unloading the old classloader causes all classes and their static fields to stay in memory.
6. **Inner classes holding outer class references**: Non-static inner classes hold an implicit reference to the outer class instance.

**Step-by-step leak debugging**:

**Step 1: Enable automatic heap dump on OOM**:
```bash
java -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/var/logs/heapdumps/ \
     -jar myservice.jar
```

**Step 2: Capture a heap dump from a running process**:
```bash
# Find the PID
jps -v

# Capture heap dump
jmap -dump:live,format=b,file=heap_dump.hprof <PID>

# Or use jcmd (preferred in modern JDK)
jcmd <PID> GC.heap_dump /tmp/heap_dump.hprof
```

**Step 3: Analyze with Eclipse MAT (Memory Analyzer Tool)**:
- Open the `.hprof` file in MAT.
- **Leak Suspects Report**: MAT automatically identifies the biggest memory consumers.
- **Dominator Tree**: Shows which objects "dominate" (retain) the most memory. The dominator of an object X is the last object on the path from GC root to X that, if removed, would make X unreachable. This reveals the "real" memory owners.
- **Shallow Heap vs. Retained Heap**:
  - **Shallow Heap**: Memory consumed by the object itself (its fields).
  - **Retained Heap**: Memory that would be freed if this object were garbage collected (the object + all objects it exclusively dominates).
- **Path to GC Roots**: For a suspected leaking object, right-click → "Path to GC Roots" → "exclude weak/soft references." This shows you exactly WHY the object is being kept alive.

**Step 4: Monitor in production** (without heap dumps):
```bash
# GC logging (Java 11+)
-Xlog:gc*:file=/var/log/gc.log:time,uptime,level,tags:filecount=10,filesize=50m

# Key metrics to monitor:
# - Heap usage after Full GC (if it keeps growing → leak)
# - GC pause duration and frequency
# - Old Gen occupancy trend
```

**Production debugging pattern**:
```java
// Common leak pattern: ThreadLocal in a web server
public class UserContext {
    // BAD: ThreadLocal never cleaned up in Tomcat's thread pool
    private static final ThreadLocal<UserSession> context = new ThreadLocal<>();
    
    public static void set(UserSession session) {
        context.set(session);
    }
    
    // MUST be called in a finally block or servlet filter
    public static void clear() {
        context.remove();  // Critical! Without this, Tomcat's pooled threads
                           // retain UserSession objects forever
    }
}

// Fix: Use a servlet filter to guarantee cleanup
@Component
public class UserContextFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) 
            throws IOException, ServletException {
        try {
            UserContext.set(extractSession(req));
            chain.doFilter(req, res);
        } finally {
            UserContext.clear();  // Always cleanup
        }
    }
}
```

---

## 2.2 Concurrency: CompletableFuture, Custom Thread Pools, and Race Conditions

---

### The "Why" & The Problem

Modern backend services spend most of their time **waiting**: waiting for a database query to return, waiting for an HTTP call to another microservice, waiting for a message broker acknowledgment. If your service handles each request on a single thread that blocks on every I/O call, you are wasting CPU resources. A service with 200 Tomcat threads making 3 sequential HTTP calls of 100ms each can only handle ~666 requests/second, even though the CPU is sitting idle 95% of the time.

**Concurrency** lets you overlap these waiting periods. Instead of making 3 sequential HTTP calls (300ms total), you make them in parallel (100ms total). Instead of blocking a thread while waiting for a database response, you release the thread to do other work.

A company pays you to know this because:
- **Throughput**: Properly concurrent code can handle 5-10x more requests with the same hardware.
- **Latency**: Parallel fan-out reduces end-to-end response time.
- **Cost**: Fewer servers = less AWS bill.
- **Correctness**: Improperly concurrent code causes race conditions, data corruption, deadlocks, and intermittent bugs that are nearly impossible to reproduce in testing.

---

### Interviewer Expectations

- **CompletableFuture**: Not just basic usage — demonstrate `thenCombine`, `thenCompose`, `allOf`, `anyOf`, error handling with `exceptionally` and `handle`, and custom executors.
- **Thread Pools**: Explain `ThreadPoolExecutor` parameters (core pool size, max pool size, queue capacity, rejection policies). Why the default `ForkJoinPool.commonPool()` is dangerous in production.
- **Race Conditions**: Define them precisely, explain visibility vs. atomicity, and demonstrate solutions (synchronized, volatile, AtomicReference, ConcurrentHashMap, and lock-free algorithms).
- **Keywords**: "Non-blocking I/O", "fan-out/fan-in", "backpressure", "ForkJoinPool", "work-stealing", "compare-and-swap (CAS)", "happens-before relationship", "memory visibility", "lock striping".

---

### The Deep Dive & Solution

#### CompletableFuture — The Right Way

`CompletableFuture` (Java 8+) is the backbone of asynchronous programming in modern Java. It represents a computation that will complete in the future and allows you to chain dependent computations without blocking.

**Basic Creation**:
```java
// Run a computation asynchronously
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    // This runs on ForkJoinPool.commonPool() by default
    return callExternalService();
});

// Non-blocking transformation
CompletableFuture<Integer> lengthFuture = future.thenApply(String::length);

// Terminal operation (blocking — use sparingly)
Integer result = lengthFuture.get(5, TimeUnit.SECONDS);
```

**Fan-out/Fan-in Pattern** (the most important pattern in microservices):
```java
// Problem: An API endpoint needs data from 3 different services.
// Sequential: 100ms + 150ms + 80ms = 330ms
// Parallel:   max(100ms, 150ms, 80ms) = 150ms

public OrderSummary getOrderSummary(String orderId, ExecutorService executor) {
    CompletableFuture<Order> orderFuture = CompletableFuture.supplyAsync(
        () -> orderService.getOrder(orderId), executor
    );
    
    CompletableFuture<Customer> customerFuture = CompletableFuture.supplyAsync(
        () -> customerService.getCustomer(orderId), executor
    );
    
    CompletableFuture<List<Item>> itemsFuture = CompletableFuture.supplyAsync(
        () -> inventoryService.getItems(orderId), executor
    );
    
    // Wait for all three to complete, then combine
    return CompletableFuture.allOf(orderFuture, customerFuture, itemsFuture)
        .thenApply(ignored -> new OrderSummary(
            orderFuture.join(),       // safe to call join() here — already complete
            customerFuture.join(),
            itemsFuture.join()
        ))
        .get(3, TimeUnit.SECONDS);
}
```

**Chaining Dependent Computations**:
```java
// thenApply: synchronous transformation (T -> U)
// thenCompose: async transformation (T -> CompletableFuture<U>) — the flatMap equivalent
// thenCombine: combine results of two independent futures

CompletableFuture<String> userFuture = getUserAsync(userId);

// thenCompose — dependent async call (user is needed to get orders)
CompletableFuture<List<Order>> ordersFuture = userFuture
    .thenCompose(user -> getOrdersAsync(user.getRegion()));

// thenCombine — independent results merged
CompletableFuture<Dashboard> dashboardFuture = userFuture
    .thenCombine(getMetricsAsync(), (user, metrics) -> 
        new Dashboard(user, metrics)
    );
```

**Error Handling**:
```java
CompletableFuture<String> result = callServiceAsync()
    .thenApply(response -> processResponse(response))
    .exceptionally(throwable -> {
        // Handle any exception in the chain
        log.error("Service call failed", throwable);
        return "default_fallback_value";
    });

// Or use handle() for both success and failure
CompletableFuture<Result> result = callServiceAsync()
    .handle((response, throwable) -> {
        if (throwable != null) {
            return Result.failure(throwable.getMessage());
        }
        return Result.success(response);
    });
```

**Timeout handling (Java 9+)**:
```java
CompletableFuture<String> result = callServiceAsync()
    .orTimeout(2, TimeUnit.SECONDS)           // throws TimeoutException
    .completeOnTimeout("fallback", 2, TimeUnit.SECONDS);  // returns default
```

#### Custom Thread Pools — Why and How

**The danger of `ForkJoinPool.commonPool()`**: By default, `CompletableFuture.supplyAsync()` uses `ForkJoinPool.commonPool()`, which has `Runtime.getRuntime().availableProcessors() - 1` threads. This pool is **shared across the entire JVM** — parallel streams, CompletableFuture, and any library that uses it. If you submit 50 blocking HTTP calls to the common pool, you will **starve** all other concurrent operations in your application.

**Rule of thumb**: Always provide a custom `ExecutorService` for I/O-bound work.

```java
@Configuration
public class ThreadPoolConfig {
    
    // For I/O-bound operations (HTTP calls, DB queries)
    // Many threads because they spend most time waiting
    @Bean("ioExecutor")
    public ExecutorService ioExecutor() {
        return new ThreadPoolExecutor(
            20,                          // corePoolSize: min threads kept alive
            100,                         // maxPoolSize: max threads under load
            60L, TimeUnit.SECONDS,       // keepAliveTime: idle threads above core die after this
            new LinkedBlockingQueue<>(500), // work queue: buffer when all threads busy
            new ThreadFactoryBuilder()
                .setNameFormat("io-worker-%d")  // Named threads for debugging
                .setDaemon(true)
                .build(),
            new ThreadPoolExecutor.CallerRunsPolicy()  // Rejection policy: 
            // When queue is full AND max threads are busy, the CALLING thread runs the task.
            // This provides natural backpressure — the submitter slows down.
        );
    }
    
    // For CPU-bound operations (computation, serialization)
    // Few threads — no point having more threads than cores for CPU work
    @Bean("cpuExecutor")
    public ExecutorService cpuExecutor() {
        int cores = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(cores, new ThreadFactoryBuilder()
            .setNameFormat("cpu-worker-%d")
            .build());
    }
}
```

**`ThreadPoolExecutor` parameters explained**:

| Parameter | What it does | Example |
|-----------|-------------|---------|
| `corePoolSize` | Threads that are always kept alive, even if idle | 20 for I/O work |
| `maximumPoolSize` | Max threads that can ever exist | 100 for bursty I/O |
| `keepAliveTime` | How long idle threads (above core) wait before dying | 60 seconds |
| `workQueue` | Queue to buffer tasks when all core threads are busy | `LinkedBlockingQueue(500)` |
| `threadFactory` | Custom thread naming (critical for debugging thread dumps) | `"io-worker-%d"` |
| `rejectionPolicy` | What to do when queue is full and max threads are busy | `CallerRunsPolicy` |

**Rejection Policies**:
- `AbortPolicy` (default): Throws `RejectedExecutionException`. Harsh but explicit.
- `CallerRunsPolicy`: The submitting thread runs the task itself. Provides natural backpressure.
- `DiscardPolicy`: Silently drops the task. Dangerous — use only for fire-and-forget tasks.
- `DiscardOldestPolicy`: Drops the oldest queued task and retries. Useful for time-sensitive work.

**How `ThreadPoolExecutor` grows**:
1. New task arrives → If `currentThreads < corePoolSize`, create a new thread.
2. If `currentThreads >= corePoolSize`, put the task in the queue.
3. If the queue is full AND `currentThreads < maxPoolSize`, create a new thread.
4. If the queue is full AND `currentThreads >= maxPoolSize`, apply the rejection policy.

**Common mistake**: Using `new LinkedBlockingQueue<>()` (unbounded queue). With an unbounded queue, `maxPoolSize` is never reached because tasks always queue up. The pool stays at `corePoolSize` forever, and you slowly accumulate millions of tasks in the queue until OOM.

#### Race Conditions — Visibility and Atomicity

A **race condition** occurs when the correctness of a program depends on the relative timing of thread operations, and the program does not enforce the required ordering.

**Two distinct problems**:
1. **Visibility**: Thread A writes a value, Thread B reads the same value but sees a stale (cached) version. This happens because each CPU core has its own L1/L2 cache, and without proper synchronization, one core's writes may not be visible to another core's reads.
2. **Atomicity**: A "single" operation is actually multiple steps that can be interleaved. `counter++` is read-increment-write: three separate operations.

**Visibility example — the `volatile` keyword**:
```java
// BUG: Thread B may loop forever because it never sees the updated value
public class BrokenShutdown {
    private boolean running = true;  // No volatile — visibility not guaranteed
    
    // Thread A
    public void shutdown() {
        running = false;  // Write may stay in Thread A's CPU cache
    }
    
    // Thread B
    public void run() {
        while (running) {  // May read a cached 'true' forever
            doWork();
        }
    }
}

// FIX: volatile guarantees visibility across threads
public class FixedShutdown {
    private volatile boolean running = true;
    // Now, every write to 'running' is immediately visible to all threads
    // via the 'happens-before' relationship established by volatile
}
```

**Atomicity example — `AtomicInteger`**:
```java
// BUG: counter++ is not atomic (read + increment + write)
public class BrokenCounter {
    private int counter = 0;
    
    public void increment() {
        counter++;  // Two threads can both read 5, increment to 6, write 6 → lost update
    }
}

// FIX 1: synchronized (simple but blocks other threads)
public class SyncCounter {
    private int counter = 0;
    
    public synchronized void increment() {
        counter++;
    }
}

// FIX 2: AtomicInteger (lock-free, uses CAS — Compare-And-Swap)
public class AtomicCounter {
    private final AtomicInteger counter = new AtomicInteger(0);
    
    public void increment() {
        counter.incrementAndGet();
        // Internally: 
        // 1. Read current value (5)
        // 2. Try CAS(expected=5, new=6)
        // 3. If another thread changed it to 6, CAS fails → retry with new value (6)
        // 4. CAS(expected=6, new=7) → succeeds
    }
}
```

**ConcurrentHashMap — Thread-safe map without global locking**:
```java
// BUG: check-then-act race condition
Map<String, Integer> map = new ConcurrentHashMap<>();

// Thread A and Thread B both check and find key absent, both put → lost update
if (!map.containsKey("key")) {   // check
    map.put("key", 1);           // act — another thread may have inserted between check and act
}

// FIX: Atomic compound operations
map.putIfAbsent("key", 1);
map.computeIfAbsent("key", k -> expensiveComputation(k));
map.merge("key", 1, Integer::sum);  // Atomic increment in a map
```

**The Happens-Before Relationship** (Java Memory Model guarantee):
The Java Memory Model (JMM) defines which writes are guaranteed to be visible to which reads. Key happens-before rules:
- **Monitor lock**: Unlock of a monitor happens-before every subsequent lock of that same monitor.
- **Volatile**: Write to a volatile variable happens-before every subsequent read of that volatile.
- **Thread start**: `thread.start()` happens-before any action in the started thread.
- **Thread join**: All actions in a thread happen-before `thread.join()` returns.
- **CompletableFuture composition**: Completion of one stage happens-before the start of the next stage in a chain.

---

## 2.3 Spring Magic: @SpringBootApplication, AutoConfiguration, and Bean Lifecycle

---

### The "Why" & The Problem

Spring Boot is ubiquitous in Java backend development. Every developer uses `@SpringBootApplication` and `@Autowired`, but most treat Spring as a black box. When something goes wrong — a bean isn't injected, autoconfiguration enables an unwanted feature, or a `@Transactional` annotation silently doesn't work — developers who don't understand Spring's internals are stuck.

A company pays you to know this because:
- **Debugging**: When autowiring fails, you need to understand component scanning, bean lifecycle, and condition evaluation to diagnose the issue.
- **Architecture decisions**: Knowing how Spring's DI container works lets you design clean, testable, modular architectures with proper separation of concerns.
- **Performance**: Understanding bean scopes, lazy initialization, and proxy mechanisms lets you optimize startup time and runtime performance.
- **Custom starters**: At Lead level, you will create shared Spring Boot starters for your organization (e.g., a company-wide security starter), which requires deep understanding of autoconfiguration.

---

### Interviewer Expectations

- **@SpringBootApplication**: Know that it is a meta-annotation combining `@Configuration`, `@EnableAutoConfiguration`, and `@ComponentScan`. Explain what each does.
- **AutoConfiguration**: Explain the `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` file (Spring Boot 3.x) or `spring.factories` (Spring Boot 2.x). Explain `@ConditionalOnClass`, `@ConditionalOnMissingBean`, `@ConditionalOnProperty`.
- **Bean Lifecycle**: Know the full lifecycle from instantiation to destruction, including `BeanPostProcessor`, `@PostConstruct`, `InitializingBean`, `DisposableBean`, and `@PreDestroy`.
- **Keywords**: "Inversion of Control (IoC)", "Dependency Injection", "BeanFactory vs ApplicationContext", "BeanPostProcessor", "BeanDefinition", "conditional beans", "component scanning", "proxy-based AOP", "CGLIB proxy vs JDK dynamic proxy".

---

### The Deep Dive & Solution

#### @SpringBootApplication — Unpacked

```java
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}
```

`@SpringBootApplication` is a **meta-annotation** equivalent to:

```java
@SpringBootConfiguration   // @Configuration — this class is a source of bean definitions
@EnableAutoConfiguration   // Triggers Spring Boot's autoconfiguration mechanism
@ComponentScan             // Scans the current package and all sub-packages for @Component, 
                           // @Service, @Repository, @Controller, etc.
public class MyApp { }
```

**What happens when `SpringApplication.run()` is called**:

1. **Create `SpringApplication` instance**: Detects the application type (SERVLET, REACTIVE, or NONE) by checking the classpath for `DispatcherServlet` or `ReactiveWebApplicationContext`.
2. **Load `ApplicationContextInitializer` instances**: From `META-INF/spring.factories`.
3. **Create the `ApplicationContext`**: For web apps, this is `AnnotationConfigServletWebServerApplicationContext`.
4. **Load Bean Definitions**: 
   - Component scanning finds all `@Component`-annotated classes.
   - `@Configuration` classes are parsed for `@Bean` methods.
   - AutoConfiguration classes are loaded and evaluated.
5. **Refresh the context**: Instantiate all singleton beans, resolve dependencies, call lifecycle callbacks.
6. **Start the embedded server**: Tomcat/Jetty/Undertow starts on the configured port.
7. **Fire `ApplicationReadyEvent`**: Application is fully started and ready to serve traffic.

#### AutoConfiguration — How Spring Boot "Just Works"

When you add `spring-boot-starter-data-jpa` to your pom.xml, Spring Boot magically configures a `DataSource`, `EntityManagerFactory`, and `TransactionManager`. No XML, no explicit `@Bean` methods. How?

**The mechanism**:

1. `@EnableAutoConfiguration` imports `AutoConfigurationImportSelector`.
2. This selector reads `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` from all JARs on the classpath. This file lists all autoconfiguration classes (e.g., `DataSourceAutoConfiguration`, `JpaRepositoriesAutoConfiguration`).
3. Each autoconfiguration class uses **conditional annotations** to decide whether to activate:

```java
@AutoConfiguration
@ConditionalOnClass(DataSource.class)  // Only if DataSource is on the classpath
@ConditionalOnMissingBean(DataSource.class)  // Only if user hasn't defined their own DataSource
@EnableConfigurationProperties(DataSourceProperties.class)
public class DataSourceAutoConfiguration {
    
    @Bean
    @ConditionalOnProperty(prefix = "spring.datasource", name = "url")
    public DataSource dataSource(DataSourceProperties properties) {
        return DataSourceBuilder.create()
            .url(properties.getUrl())
            .username(properties.getUsername())
            .password(properties.getPassword())
            .build();
    }
}
```

**Key conditional annotations**:
| Annotation | Activates when... |
|-----------|-------------------|
| `@ConditionalOnClass` | Specified class is on the classpath |
| `@ConditionalOnMissingClass` | Specified class is NOT on the classpath |
| `@ConditionalOnBean` | Specified bean already exists in the context |
| `@ConditionalOnMissingBean` | Specified bean does NOT exist — lets users override defaults |
| `@ConditionalOnProperty` | Specified property is set (and optionally has a specific value) |
| `@ConditionalOnWebApplication` | Application is a web application |
| `@ConditionalOnExpression` | SpEL expression evaluates to true |

**Debugging autoconfiguration**: Add `--debug` to your application launch or set `debug=true` in `application.properties`. Spring Boot will print a **CONDITIONS EVALUATION REPORT** showing:
- **Positive matches**: Autoconfiguration classes that were activated and why.
- **Negative matches**: Classes that were skipped and why (e.g., "Class not found: com.zaxxer.hikari.HikariDataSource").

#### Bean Lifecycle — The Complete Picture

When the Spring container creates a bean, it goes through a well-defined lifecycle. Understanding this is critical for resource management, initialization logic, and custom frameworks.

```
Bean Lifecycle (Full Sequence):
─────────────────────────────
1.  Bean Definition loaded (from @Component scan, @Bean method, or XML)
2.  Bean Instantiated (constructor called)
3.  Dependencies Injected (@Autowired fields/setters populated)
4.  BeanNameAware.setBeanName()          ← Aware interfaces (if implemented)
5.  BeanFactoryAware.setBeanFactory()
6.  ApplicationContextAware.setApplicationContext()
7.  BeanPostProcessor.postProcessBeforeInitialization()  ← ALL BeanPostProcessors
8.  @PostConstruct method called
9.  InitializingBean.afterPropertiesSet()
10. Custom init-method (specified in @Bean(initMethod="init"))
11. BeanPostProcessor.postProcessAfterInitialization()   ← AOP proxies created HERE
    ───── Bean is now READY and available for use ─────
12. ... application runs ...
    ───── Application shutdown begins ─────
13. @PreDestroy method called
14. DisposableBean.destroy()
15. Custom destroy-method (specified in @Bean(destroyMethod="cleanup"))
```

**Practical example**:
```java
@Component
public class CacheWarmer implements InitializingBean, DisposableBean {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CacheManager cacheManager;
    
    // Phase 8: Called after all dependencies are injected
    @PostConstruct
    public void init() {
        log.info("@PostConstruct: Dependencies are injected, starting warm-up");
    }
    
    // Phase 9: Alternative to @PostConstruct (Spring-specific interface)
    @Override
    public void afterPropertiesSet() {
        log.info("afterPropertiesSet: Warming cache with top 1000 products");
        List<Product> products = productRepository.findTop1000ByOrderBySalesDesc();
        products.forEach(p -> cacheManager.getCache("products").put(p.getId(), p));
    }
    
    // Phase 13: Called on shutdown
    @PreDestroy
    public void shutdown() {
        log.info("@PreDestroy: Flushing cache metrics before shutdown");
        cacheManager.getCache("products").clear();
    }
    
    // Phase 14: Alternative to @PreDestroy
    @Override
    public void destroy() {
        log.info("destroy: Final cleanup");
    }
}
```

**BeanPostProcessor — Spring's extension point**:
`BeanPostProcessor` is how Spring implements features like `@Autowired`, `@Transactional`, `@Async`, and AOP proxying. Every bean passes through all registered `BeanPostProcessor` instances.

```java
@Component
public class TimingBeanPostProcessor implements BeanPostProcessor {
    
    // Called BEFORE @PostConstruct
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        // Can modify or replace the bean
        return bean;
    }
    
    // Called AFTER @PostConstruct — AOP proxies are typically created here
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        // Example: wrap every @Service bean in a timing proxy
        if (bean.getClass().isAnnotationPresent(Service.class)) {
            return createTimingProxy(bean);
        }
        return bean;
    }
}
```

**Why @Transactional doesn't work on private methods or self-invocation**:
`@Transactional` works through AOP proxying. Spring creates a **proxy object** that wraps your bean. When external code calls `myBean.save()`, it actually calls `proxy.save()`, which starts a transaction, delegates to the real `save()`, and commits/rolls back. But when `save()` internally calls `this.helper()`, `this` refers to the real bean, not the proxy. So `@Transactional` on `helper()` is bypassed.

```java
@Service
public class OrderService {
    
    @Transactional
    public void processOrder(Order order) {
        // ... 
        this.updateInventory(order);  // BUG: calls real method, not proxy
                                       // @Transactional on updateInventory is IGNORED
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateInventory(Order order) {
        // This won't run in a new transaction when called from processOrder()!
    }
}

// FIX 1: Inject self
@Service
public class OrderService {
    @Autowired
    private OrderService self;  // The proxy, not 'this'
    
    @Transactional
    public void processOrder(Order order) {
        self.updateInventory(order);  // Goes through the proxy → works!
    }
}

// FIX 2: Move to separate class (preferred for clean architecture)
```

**Bean Scopes**:
| Scope | Description | Use Case |
|-------|------------|----------|
| `singleton` (default) | One instance per ApplicationContext | Stateless services |
| `prototype` | New instance every time it's requested | Stateful builders |
| `request` | One instance per HTTP request | Request-scoped data |
| `session` | One instance per HTTP session | User session state |
| `application` | One instance per ServletContext | Shared across servlets |

---

## 2.4 Security: OAuth2 Flows, JWT Implementation, and Stateless vs. Stateful Security

---

### The "Why" & The Problem

Every web application needs authentication (who are you?) and authorization (what are you allowed to do?). In the monolith era, this was simple: a server-side session stored the user's identity in memory, and a cookie (`JSESSIONID`) linked the browser to the session. But in the microservices era, this breaks down:
- **Scalability**: Session state must be shared across all instances of a service (sticky sessions or distributed session stores like Redis).
- **Cross-service auth**: If Service A receives a request, how does Service B trust that the user is authenticated when Service A calls it?
- **Third-party integration**: How do you let users "Sign in with Google" without handling their Google password?

OAuth2 and JWT solve these problems. A company pays you to know this because security vulnerabilities are the most expensive bugs — they cause data breaches, regulatory fines, and loss of customer trust.

---

### Interviewer Expectations

- **OAuth2**: Know the four grant types (Authorization Code, Client Credentials, Implicit, Resource Owner Password) and when to use each. Know that Implicit and Resource Owner Password are **deprecated** in OAuth 2.1. Know PKCE (Proof Key for Code Exchange) for public clients.
- **JWT**: Know the structure (Header.Payload.Signature), signing algorithms (HS256 vs RS256), token validation, and security pitfalls (token theft, no revocation). Know the difference between access tokens and refresh tokens.
- **Stateless vs. Stateful**: Articulate the trade-offs clearly. Stateless = better scalability, no shared state. Stateful = easier revocation, simpler session management.
- **Keywords**: "Authorization Server", "Resource Server", "PKCE", "opaque token vs. self-contained token (JWT)", "token introspection", "refresh token rotation", "claims", "RBAC (Role-Based Access Control)".

---

### The Deep Dive & Solution

#### OAuth2 Flows — When to Use Which

OAuth2 is an **authorization framework** (not authentication — that's OpenID Connect on top of OAuth2). It defines how a **client** (your frontend/app) can obtain an **access token** from an **authorization server** to access a **resource server** (your API).

**Key roles**:
- **Resource Owner**: The user who owns the data.
- **Client**: The application requesting access (SPA, mobile app, backend service).
- **Authorization Server**: Issues tokens (e.g., Keycloak, Auth0, Okta, Spring Authorization Server).
- **Resource Server**: The API that holds protected resources and validates tokens.

**Flow 1: Authorization Code (with PKCE) — For SPAs, Mobile Apps, Server-side Apps**

This is the **recommended flow for almost all scenarios** in OAuth 2.1.

```
┌───────────┐                              ┌──────────────────┐
│  Browser   │──(1) Auth Request──────────>│  Authorization    │
│  (SPA)     │    + code_challenge          │  Server           │
│            │<─(2) Redirect to Login──────│  (Keycloak)       │
│            │──(3) User Logs In──────────>│                    │
│            │<─(4) Auth Code──────────────│                    │
│            │──(5) Exchange Code + ──────>│                    │
│            │      code_verifier           │                    │
│            │<─(6) Access Token + ────────│                    │
│            │      Refresh Token           │                    │
│            │                              └──────────────────┘
│            │──(7) API Call + ───────────>┌──────────────────┐
│            │      Access Token            │  Resource Server  │
│            │<─(8) Protected Resource─────│  (Your API)       │
└───────────┘                              └──────────────────┘
```

**PKCE (Proof Key for Code Exchange)**: Protects against authorization code interception. The client generates a random `code_verifier`, computes `code_challenge = SHA256(code_verifier)`, sends the challenge in step 1, and the verifier in step 5. The authorization server verifies that `SHA256(code_verifier) == code_challenge`. This ensures that even if an attacker intercepts the authorization code, they can't exchange it without the verifier.

**Flow 2: Client Credentials — For Service-to-Service Communication**

No user involved. Service A authenticates as itself to get a token to call Service B.

```java
// Spring Security configuration for a service acting as an OAuth2 client
@Configuration
public class OAuth2ClientConfig {
    
    @Bean
    public WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
            new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("internal-service");
        
        return WebClient.builder()
            .apply(oauth2Client.oauth2Configuration())
            .build();
    }
}

// application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          internal-service:
            client-id: order-service
            client-secret: ${ORDER_SERVICE_SECRET}
            authorization-grant-type: client_credentials
            scope: read,write
        provider:
          internal-service:
            token-uri: https://auth.company.com/oauth2/token
```

#### JWT — Structure and Implementation

A JWT (JSON Web Token) is a **self-contained** token: it carries all the information needed to validate it and identify the user, without querying a database or session store.

**JWT Structure**: `xxxxx.yyyyy.zzzzz` (Header.Payload.Signature)

```json
// Header (Base64URL encoded)
{
  "alg": "RS256",    // Signing algorithm
  "typ": "JWT",
  "kid": "key-id-1"  // Key ID — helps the verifier find the correct public key
}

// Payload (Base64URL encoded) — contains "claims"
{
  "sub": "user-12345",           // Subject (user ID)
  "iss": "https://auth.company.com",  // Issuer
  "aud": "order-service",        // Audience (intended recipient)
  "exp": 1735689600,             // Expiration (Unix timestamp)
  "iat": 1735686000,             // Issued At
  "roles": ["ADMIN", "USER"],    // Custom claims
  "email": "user@company.com"
}

// Signature
RSASHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  privateKey
)
```

**HS256 vs RS256**:
- **HS256 (HMAC-SHA256)**: Symmetric. Same secret key signs and verifies. Simple but the secret must be shared with every service that validates tokens — security risk.
- **RS256 (RSA-SHA256)**: Asymmetric. Private key signs (only the auth server has it), public key verifies (shared with all resource servers via JWKS endpoint). **Preferred in microservices** because resource servers never see the private key.

**Spring Boot Resource Server configuration**:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // No server-side sessions
            )
            .csrf(csrf -> csrf.disable());  // CSRF not needed for stateless JWT auth
        
        return http.build();
    }
    
    // Convert JWT claims to Spring Security authorities
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = 
            new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
    }
}

// application.yml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://auth.company.com  
          # Spring auto-discovers JWKS endpoint: https://auth.company.com/.well-known/jwks.json
```

#### Stateless vs. Stateful Security — Trade-offs

| Aspect | Stateless (JWT) | Stateful (Sessions) |
|--------|-----------------|---------------------|
| **Storage** | Token on client (browser/mobile) | Session in server memory/Redis |
| **Scalability** | Excellent — any server can validate | Requires session replication or sticky sessions |
| **Revocation** | Hard — token is valid until expiry | Easy — delete the session from the store |
| **Token Size** | Larger (contains all claims) | Small (just a session ID) |
| **Database Hit** | None for validation (self-contained) | One hit per request (session lookup) |
| **Security Risk** | Token theft = full access until expiry | Session fixation, CSRF |
| **Logout** | Requires a token blacklist or short-lived tokens + refresh | Simply destroy the session |

**Best practice for production**:
- Use **short-lived access tokens** (5–15 minutes) + **refresh tokens** (hours–days).
- Store refresh tokens server-side (database/Redis) for revocation capability.
- Implement **refresh token rotation**: every time a refresh token is used, issue a new one and invalidate the old one. If a stolen refresh token is reused, the rotation detects the anomaly and revokes all tokens for that user.
- For immediate revocation needs (e.g., user clicks "logout from all devices"), maintain a **token blacklist** in Redis with TTL equal to the access token's remaining lifetime.

```java
// Refresh token rotation implementation
@Service
public class TokenService {
    
    @Autowired
    private RefreshTokenRepository refreshTokenRepo;
    
    public TokenPair refreshAccessToken(String refreshToken) {
        RefreshTokenEntity stored = refreshTokenRepo.findByToken(refreshToken)
            .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));
        
        if (stored.isRevoked()) {
            // Token reuse detected! Revoke ALL tokens for this user (security breach)
            refreshTokenRepo.revokeAllForUser(stored.getUserId());
            throw new SecurityBreachException("Refresh token reuse detected");
        }
        
        if (stored.getExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("Refresh token expired");
        }
        
        // Rotate: revoke old token, issue new pair
        stored.setRevoked(true);
        refreshTokenRepo.save(stored);
        
        String newAccessToken = generateAccessToken(stored.getUserId());
        String newRefreshToken = generateRefreshToken(stored.getUserId());
        
        return new TokenPair(newAccessToken, newRefreshToken);
    }
}
```

