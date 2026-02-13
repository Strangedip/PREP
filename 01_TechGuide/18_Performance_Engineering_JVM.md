# Section 18: Performance Engineering & JVM Tuning

> **Level**: SENIOR+ to LEAD
> **Why This Matters**: Lead engineers are expected to diagnose production performance issues, tune JVM settings, profile applications, and set SLAs. This is what separates a coder from an engineer who owns system performance.

---

## 18.1 JVM Architecture — The Runtime

### The Deep Dive & Solution

```
JVM Memory Layout (Java 17+):

┌──────────────────────────────────────────────────────────────┐
│                         JVM Process                          │
│                                                              │
│  ┌─────────────────────────── HEAP ───────────────────────┐  │
│  │                                                         │  │
│  │  ┌── Young Generation ──┐   ┌── Old Generation ──────┐ │  │
│  │  │ Eden  │  S0  │  S1   │   │  Tenured Space         │ │  │
│  │  │       │      │       │   │  (long-lived objects)   │ │  │
│  │  └───────┴──────┴───────┘   └─────────────────────────┘ │  │
│  │                                                         │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                              │
│  ┌─── Non-Heap ──────────────────────────────────────────┐  │
│  │ Metaspace (class metadata)                             │  │
│  │ Code Cache (JIT compiled code)                         │  │
│  │ Thread Stacks (one per thread, default 1MB each)       │  │
│  │ Direct Memory (NIO buffers)                            │  │
│  └────────────────────────────────────────────────────────┘  │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

#### Essential JVM Flags

```bash
# Memory sizing
-Xms4g                          # Initial heap size (set equal to Xmx to avoid resizing)
-Xmx4g                          # Maximum heap size
-XX:MetaspaceSize=256m           # Initial metaspace size
-XX:MaxMetaspaceSize=512m        # Max metaspace
-Xss512k                        # Thread stack size (reduce from 1MB default for many threads)

# GC Selection (Java 17+)
-XX:+UseG1GC                    # G1 (default, balanced)
-XX:+UseZGC                     # ZGC (ultra-low latency, <1ms pauses)
-XX:+UseShenandoahGC            # Shenandoah (low latency, OpenJDK)

# G1GC Tuning
-XX:MaxGCPauseMillis=200        # Target pause time (G1 adjusts automatically)
-XX:G1HeapRegionSize=16m        # Region size (1MB to 32MB, auto-calculated)
-XX:InitiatingHeapOccupancyPercent=45  # Start concurrent marking at 45% heap usage

# ZGC Tuning (Java 17+)
-XX:+UseZGC
-XX:ZCollectionInterval=0       # 0 = GC only when needed (default)
-XX:SoftMaxHeapSize=3g          # ZGC will try to keep heap below this

# Diagnostics
-XX:+HeapDumpOnOutOfMemoryError       # Auto dump on OOM
-XX:HeapDumpPath=/var/log/heapdumps/  # Where to write dumps
-XX:+PrintGCDetails                    # Detailed GC logging (Java 8)
-Xlog:gc*:file=gc.log:time           # GC logging (Java 9+)
-XX:NativeMemoryTracking=summary      # Track native memory usage

# Performance
-XX:+UseStringDeduplication           # Deduplicate identical strings in heap (G1 only)
-XX:+UseCompressedOops                # Compress object pointers (default for <32GB heap)
-XX:+TieredCompilation                # Enable tiered JIT (default)
```

---

## 18.2 Garbage Collection Deep Dive

### G1GC vs ZGC vs Shenandoah — Decision Matrix

```
| Feature              | G1GC (default)        | ZGC                   | Shenandoah           |
|---------------------|-----------------------|-----------------------|----------------------|
| Pause time target   | ~200ms (configurable) | <1ms (sub-millisecond)| <10ms                |
| Heap size           | 4GB-64GB optimal      | 8MB-16TB              | Any size             |
| Throughput          | High                  | Slightly lower        | Slightly lower       |
| CPU overhead        | Low                   | ~15% more CPU         | ~15% more CPU        |
| Java version        | Java 7+ (default 9+)  | Java 15+ (production) | Java 12+ (OpenJDK)  |
| Concurrent?         | Mostly                | Fully concurrent      | Fully concurrent     |
| Best for            | General purpose       | Latency-sensitive     | Latency-sensitive    |
| Examples            | Batch, web apps       | Trading, gaming, UI   | Same as ZGC          |
```

**When to use each:**
- **G1GC**: Default choice. Works for 90% of applications. Start here.
- **ZGC**: If you need sub-millisecond GC pauses (trading systems, real-time APIs, interactive apps).
- **Shenandoah**: Same use case as ZGC but available in OpenJDK (no Oracle license needed).

### Diagnosing GC Issues

```bash
# Step 1: Enable GC logging
java -Xlog:gc*:file=gc.log:time,level,tags -jar app.jar

# Step 2: Analyze GC log (use GCViewer or GCEasy.io)
# Look for:
# - Frequent Full GCs → heap too small or memory leak
# - Long GC pauses → switch to ZGC or tune G1
# - Allocation failure → Eden too small
# - Promotion failure → Old gen too small

# Step 3: Monitor with JMX / Prometheus
# Key metrics:
# jvm.gc.pause (histogram) — GC pause durations
# jvm.memory.used — heap usage over time
# jvm.gc.memory.promoted — bytes promoted to old gen per GC
# jvm.gc.memory.allocated — bytes allocated in young gen per GC
```

---

## 18.3 Profiling — Finding the Bottleneck

### Java Flight Recorder (JFR)

```bash
# JFR is ZERO-overhead (production-safe) profiling built into the JVM

# Start recording:
java -XX:StartFlightRecording=duration=60s,filename=recording.jfr -jar app.jar

# Or attach to running process:
jcmd <PID> JFR.start duration=60s filename=recording.jfr

# Continuous recording (circular buffer):
java -XX:StartFlightRecording=disk=true,maxsize=500m,maxage=1h -jar app.jar

# Analyze with JDK Mission Control (jmc) or IntelliJ
```

#### What JFR Captures

```
- CPU profiling (hot methods, call trees)
- Memory allocation (which objects, where allocated)
- GC events (pause times, collection counts)
- Thread analysis (contention, blocked threads, deadlocks)
- I/O operations (file, socket, database)
- Exception creation (including stack traces)
- Class loading
- JIT compilation events
```

### async-profiler — CPU and Allocation Profiling

```bash
# async-profiler generates flame graphs — the gold standard for CPU profiling

# CPU profiling (where is time spent?):
./profiler.sh -d 30 -f cpu.html -o flamegraph <PID>

# Allocation profiling (what objects are created?):
./profiler.sh -d 30 -e alloc -f alloc.html -o flamegraph <PID>

# Lock contention (which locks are hotspots?):
./profiler.sh -d 30 -e lock -f locks.html -o flamegraph <PID>

# Reading a flame graph:
# - X-axis: stack trace population (wider = more samples = more time spent)
# - Y-axis: call stack depth
# - Look for wide bars near the top — these are the hotspots
# - Colors are random (just for visual distinction)
```

### Thread Dump Analysis

```bash
# Take a thread dump:
jstack <PID> > thread_dump.txt
# Or:
kill -3 <PID>  # Writes to stdout (container logs)

# What to look for:
# 1. BLOCKED threads — waiting for a monitor/lock
# 2. WAITING threads — in Object.wait() or LockSupport.park()
# 3. Many threads in same state — thread pool exhaustion
# 4. Deadlock detection — JVM reports these automatically at the end
```

```
# Example: Detecting thread pool exhaustion
"http-nio-8080-exec-200" #312 daemon prio=5 WAITING
   at sun.misc.Unsafe.park(Native Method)
   at java.util.concurrent.locks.LockSupport.park(LockSupport.java:304)
   at java.util.concurrent.FutureTask.awaitDone(FutureTask.java:400)
   at java.util.concurrent.FutureTask.get(FutureTask.java:196)
   at com.example.OrderService.getOrderDetails(OrderService.java:45)
   
# Interpretation: All 200 Tomcat threads are WAITING on FutureTask.get()
# → Downstream service is slow → threads are blocked → no threads available
# Solution: Add timeout to CompletableFuture, use circuit breaker, increase thread pool
```

### Heap Dump Analysis

```bash
# Take a heap dump:
jmap -dump:live,format=b,file=heap.hprof <PID>
# Or:
jcmd <PID> GC.heap_dump heap.hprof

# Auto-dump on OOM (must be set at startup):
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/log/

# Analyze with Eclipse MAT (Memory Analyzer Tool):
# 1. Open heap dump → "Leak Suspects Report"
# 2. Check "Dominator Tree" — objects retaining the most memory
# 3. Check "Histogram" — object count by class
# 4. Look for:
#    - Large collections (HashMap, ArrayList) — data not being cleaned up
#    - byte[] arrays — could be cached HTTP responses, serialized objects
#    - String objects — possible duplicate strings, log message accumulation
#    - ClassLoader leaks — especially in hot-reload environments
```

---

## 18.4 JMH — Microbenchmarking

### The Deep Dive & Solution

```java
// JMH (Java Microbenchmark Harness) is the ONLY reliable way to benchmark Java code
// Why? JIT compilation, dead code elimination, constant folding, loop unrolling
// make naive benchmarks (System.nanoTime()) completely unreliable

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)       // JIT warmup
@Measurement(iterations = 10, time = 1)  // Actual measurement
@Fork(2)                                  // Run in separate JVM processes
@State(Scope.Thread)                      // Each thread gets its own state
public class CollectionBenchmark {
    
    private static final int SIZE = 1_000_000;
    
    private List<Integer> arrayList;
    private LinkedList<Integer> linkedList;
    private Set<Integer> hashSet;
    private Set<Integer> treeSet;
    
    @Setup(Level.Trial)
    public void setup() {
        arrayList = new ArrayList<>(SIZE);
        linkedList = new LinkedList<>();
        hashSet = new HashSet<>(SIZE);
        treeSet = new TreeSet<>();
        
        for (int i = 0; i < SIZE; i++) {
            arrayList.add(i);
            linkedList.add(i);
            hashSet.add(i);
            treeSet.add(i);
        }
    }
    
    @Benchmark
    public boolean arrayListContains() {
        return arrayList.contains(SIZE / 2); // O(n) — linear scan
    }
    
    @Benchmark
    public boolean hashSetContains() {
        return hashSet.contains(SIZE / 2); // O(1) — hash lookup
    }
    
    @Benchmark
    public boolean treeSetContains() {
        return treeSet.contains(SIZE / 2); // O(log n) — tree search
    }
    
    @Benchmark
    public int streamSum() {
        return arrayList.stream().mapToInt(Integer::intValue).sum();
    }
    
    @Benchmark
    public int parallelStreamSum() {
        return arrayList.parallelStream().mapToInt(Integer::intValue).sum();
    }
    
    @Benchmark
    public int forLoopSum() {
        int sum = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            sum += arrayList.get(i);
        }
        return sum;
    }
    
    // Prevent dead code elimination:
    @Benchmark
    public void stringConcat(Blackhole bh) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("test");
        }
        bh.consume(sb.toString()); // Blackhole prevents JIT from eliminating the code
    }
}

// Run:
// mvn clean install
// java -jar target/benchmarks.jar
```

---

## 18.5 Common Performance Anti-Patterns

### The Deep Dive & Solution

```java
// 1. N+1 QUERY PROBLEM (the #1 JPA performance killer)
// BAD: 
List<Order> orders = orderRepository.findAll(); // 1 query
for (Order order : orders) {
    order.getCustomer().getName(); // N queries (one per order — lazy loading)
}
// GOOD:
@Query("SELECT o FROM Order o JOIN FETCH o.customer")
List<Order> findAllWithCustomer(); // 1 query with JOIN

// 2. CHATTY MICROSERVICE CALLS
// BAD:
for (String productId : cart.getProductIds()) {
    Product p = productService.getProduct(productId); // N HTTP calls
    prices.add(p.getPrice());
}
// GOOD:
List<Product> products = productService.getProducts(cart.getProductIds()); // 1 batch call

// 3. INEFFICIENT SERIALIZATION
// BAD:
objectMapper.writeValueAsString(hugeObject); // Serialize entire entity graph
// GOOD: Use DTOs with only required fields
OrderSummaryDTO dto = new OrderSummaryDTO(order.getId(), order.getAmount());

// 4. MISSING CONNECTION POOL LIMITS
// BAD: Unbounded pool → server runs out of connections under load
// GOOD: HikariCP with bounded pool (see Section 17.8)

// 5. SYNCHRONOUS I/O IN EVENT LOOPS
// BAD (in WebFlux/reactive):
Mono<String> result = Mono.fromCallable(() -> {
    return jdbcTemplate.queryForObject("SELECT ...", String.class); // Blocking!
}).subscribeOn(Schedulers.boundedElastic()); // Must offload to blocking scheduler

// 6. EXCESSIVE LOGGING IN HOT PATHS
// BAD:
for (Order order : orders) { // 100K orders
    log.debug("Processing order: {}", objectMapper.writeValueAsString(order)); // Serializes EVERY time
}
// GOOD:
if (log.isDebugEnabled()) { // Check before expensive serialization
    log.debug("Processing batch of {} orders", orders.size());
}

// 7. STRING CONCATENATION IN LOOPS
// BAD:
String result = "";
for (String s : list) {
    result += s; // Creates new String object every iteration: O(n²)
}
// GOOD:
StringBuilder sb = new StringBuilder();
for (String s : list) {
    sb.append(s); // Amortized O(1) per append: O(n) total
}
```

---

## 18.6 Performance Testing Methodology

### The Deep Dive & Solution

```
Performance Testing Types:

1. LOAD TEST: Simulate expected production load
   - Goal: Verify system meets SLAs under normal conditions
   - Duration: 30-60 minutes
   - Users: Expected concurrent users

2. STRESS TEST: Push beyond expected load until failure
   - Goal: Find breaking point and failure behavior
   - Ramp up until errors/timeouts increase
   - Observe: graceful degradation or catastrophic failure?

3. SOAK TEST (Endurance): Run at normal load for extended time
   - Goal: Find memory leaks, connection pool exhaustion, resource drift
   - Duration: 8-24 hours
   - Monitor: heap usage trend, GC frequency, connection count

4. SPIKE TEST: Sudden burst of traffic
   - Goal: Verify auto-scaling, circuit breakers, rate limiters
   - Pattern: Normal → 10x spike → Normal
   - Observe: recovery time, error rate during spike
```

#### SLA / SLO / SLI Framework

```
SLI (Service Level Indicator): What you MEASURE
  - Request latency (p50, p95, p99)
  - Error rate (5xx / total requests)
  - Availability (uptime / total time)
  - Throughput (requests/second)

SLO (Service Level Objective): What you TARGET
  - p99 latency < 200ms
  - Error rate < 0.1%
  - Availability > 99.9% (8.76 hours downtime/year)
  - Throughput > 10,000 RPS

SLA (Service Level Agreement): What you PROMISE (contractual)
  - If SLOs are violated, there are consequences (refunds, credits)
  - SLAs should be less aggressive than SLOs

Error Budget:
  - If SLO = 99.9% availability → Error budget = 0.1% = 43.8 min/month
  - While budget remains: deploy fast, take risks
  - When budget is exhausted: freeze deployments, focus on reliability
```

---

## 18.7 Performance Budgets

### The Deep Dive & Solution

```
Backend Performance Budget (per API endpoint):

| Metric          | Budget    | Alert At  | Critical At |
|----------------|-----------|-----------|-------------|
| p50 latency    | < 50ms    | > 100ms   | > 200ms     |
| p99 latency    | < 200ms   | > 500ms   | > 1s        |
| Error rate     | < 0.01%   | > 0.1%    | > 1%        |
| CPU usage      | < 60%     | > 75%     | > 90%       |
| Memory usage   | < 70%     | > 80%     | > 90%       |
| GC pause (p99) | < 50ms    | > 100ms   | > 500ms     |
| DB query time  | < 20ms    | > 50ms    | > 200ms     |
| Connection pool | < 70%    | > 80%     | > 95%       |

Frontend Performance Budget:
| Metric          | Budget    |
|----------------|-----------|
| First Contentful Paint | < 1.5s  |
| Largest Contentful Paint | < 2.5s |
| Time to Interactive | < 3s    |
| Total Bundle Size | < 250KB (gzipped) |
| JavaScript Execution | < 300ms |
```

---

## 18.8 Interview Quick Reference — Performance

### Top Questions and One-Line Answers

| Question | Answer |
|----------|--------|
| How would you diagnose a slow API? | Distributed trace → identify slow span → profile (JFR/async-profiler) → check DB queries → check downstream calls. |
| G1GC vs ZGC? | G1: general purpose, ~200ms pauses. ZGC: <1ms pauses, slightly lower throughput. |
| What is JFR? | Java Flight Recorder — production-safe profiler built into JVM, captures CPU, memory, GC, I/O, threads. |
| How to find a memory leak? | Heap dump → Eclipse MAT → Dominator Tree → find objects retaining most memory → trace to GC root. |
| What JVM flags do you use? | -Xms/-Xmx (equal), GC selection, HeapDumpOnOutOfMemoryError, GC logging. |
| Explain thread pool sizing? | CPU-bound: cores. I/O-bound: cores × (1 + wait/compute). Over-sizing = more context switching. |
| What is a flame graph? | Visualization of CPU time. Wide bars = hotspots. X-axis = sample count, Y-axis = call stack. |
| SLI vs SLO vs SLA? | SLI: what you measure. SLO: what you target. SLA: what you contractually promise. |
| What is error budget? | If SLO=99.9%, error budget = 0.1% downtime. Spent? Freeze deployments. Remaining? Ship fast. |

### Keywords to Use in Interviews

```
JVM: Escape Analysis, TLAB, Safepoint, JIT (C1/C2), OSR, Intrinsic,
     Compressed Oops, Card Table, Remembered Set, Write Barrier

GC: Concurrent Marking, Colored Pointers (ZGC), Load Barrier (ZGC),
    Concurrent Evacuation (Shenandoah), Region-based (G1), Humongous Objects

Profiling: Flame Graph, JFR, async-profiler, JMH, Dominator Tree,
           Retained Heap, Shallow Heap, GC Root, Thread Dump

Performance: p50/p95/p99, SLI, SLO, SLA, Error Budget, Throughput,
             Saturation, Utilization, RED Method, USE Method
```

