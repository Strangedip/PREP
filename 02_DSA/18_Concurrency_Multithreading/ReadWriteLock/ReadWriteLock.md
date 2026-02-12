# Read-Write Lock

## Problem Statement

Design a synchronization mechanism where:
- **Multiple readers** can read concurrently (shared access)
- **Only one writer** can write at a time (exclusive access)
- **No reader can read while a writer is writing**
- **No writer can write while any reader is reading**

This is critical for systems with read-heavy workloads (caches, configuration stores, in-memory databases) where allowing concurrent reads dramatically improves throughput.

---

## Why Read-Write Locks Matter

| Scenario | synchronized / ReentrantLock | ReadWriteLock |
|----------|------------------------------|---------------|
| 10 readers, 0 writers | All readers serialized (1 at a time) | All 10 read concurrently |
| 10 readers, 1 writer | All 11 serialized | 10 read, then 1 writes (or vice versa) |
| Read-heavy workload (95% reads) | Massive contention | Near-linear throughput scaling |

---

## Approach 1: ReentrantReadWriteLock (Standard Library)

```java
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe cache using ReadWriteLock.
 * Multiple threads can read simultaneously, but writes are exclusive.
 */
public class ThreadSafeCache<K, V> {
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * Read operation — acquires read lock.
     * Multiple threads can hold the read lock simultaneously.
     */
    public V get(K key) {
        rwLock.readLock().lock();
        try {
            return cache.get(key);
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Write operation — acquires write lock.
     * Only one thread can hold the write lock, and it blocks all readers.
     */
    public void put(K key, V value) {
        rwLock.writeLock().lock();
        try {
            cache.put(key, value);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Write operation — acquires write lock.
     */
    public V remove(K key) {
        rwLock.writeLock().lock();
        try {
            return cache.remove(key);
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    /**
     * Read operation — acquires read lock.
     */
    public int size() {
        rwLock.readLock().lock();
        try {
            return cache.size();
        } finally {
            rwLock.readLock().unlock();
        }
    }

    /**
     * Lock downgrading: write lock → read lock (allowed).
     * Useful when you need to write, then immediately read the result
     * without allowing other writers in between.
     */
    public V computeIfAbsent(K key, java.util.function.Function<K, V> mappingFunction) {
        // First try with read lock
        rwLock.readLock().lock();
        try {
            V value = cache.get(key);
            if (value != null) {
                return value;
            }
        } finally {
            rwLock.readLock().unlock();
        }

        // Not found — acquire write lock
        rwLock.writeLock().lock();
        try {
            // Double-check (another thread may have written between read unlock and write lock)
            V value = cache.get(key);
            if (value != null) {
                return value;
            }

            // Compute and store
            value = mappingFunction.apply(key);
            cache.put(key, value);

            // Lock downgrading: acquire read lock before releasing write lock
            rwLock.readLock().lock();
            return value;
        } finally {
            rwLock.writeLock().unlock();
            // Read lock is still held — release it
            rwLock.readLock().unlock();
        }
    }
}
```

---

## Approach 2: Custom Read-Write Lock from Scratch

This is what interviewers expect you to implement to demonstrate deep understanding.

```java
/**
 * Custom Read-Write Lock implementation using wait/notify.
 *
 * Rules:
 * - Multiple readers can hold the lock simultaneously
 * - Only one writer can hold the lock
 * - Writers have priority over readers (to prevent writer starvation)
 */
public class CustomReadWriteLock {
    private int readers = 0;        // Number of active readers
    private int writers = 0;        // Number of active writers (0 or 1)
    private int writeRequests = 0;  // Number of threads waiting to write

    /**
     * Acquire read lock.
     * Blocks if a writer is active OR if writers are waiting (to prevent writer starvation).
     */
    public synchronized void readLock() throws InterruptedException {
        while (writers > 0 || writeRequests > 0) {
            wait(); // Wait until no writer is active and no writer is waiting
        }
        readers++;
    }

    /**
     * Release read lock.
     */
    public synchronized void readUnlock() {
        readers--;
        if (readers == 0) {
            notifyAll(); // Wake up waiting writers (and readers)
        }
    }

    /**
     * Acquire write lock.
     * Blocks if any reader or writer is active.
     */
    public synchronized void writeLock() throws InterruptedException {
        writeRequests++;
        try {
            while (readers > 0 || writers > 0) {
                wait(); // Wait until no reader or writer is active
            }
            writers++;
        } finally {
            writeRequests--;
        }
    }

    /**
     * Release write lock.
     */
    public synchronized void writeUnlock() {
        writers--;
        notifyAll(); // Wake up all waiting readers and writers
    }
}
```

### Using the Custom Lock:

```java
public class SharedResource {
    private final CustomReadWriteLock lock = new CustomReadWriteLock();
    private int data = 0;

    public int read() throws InterruptedException {
        lock.readLock();
        try {
            return data;
        } finally {
            lock.readUnlock();
        }
    }

    public void write(int value) throws InterruptedException {
        lock.writeLock();
        try {
            data = value;
        } finally {
            lock.writeUnlock();
        }
    }
}
```

---

## Approach 3: StampedLock (Java 8+, Optimistic Reads)

`StampedLock` provides a third mode: **optimistic read**, which does not block writers. This is ideal for scenarios where reads are extremely frequent and writes are rare.

```java
import java.util.concurrent.locks.StampedLock;

public class OptimisticCache {
    private final StampedLock stampedLock = new StampedLock();
    private double x, y; // Example: 2D point

    /**
     * Optimistic read — does NOT block writers.
     * If a write occurred during the read, retry with a pessimistic read lock.
     */
    public double distanceFromOrigin() {
        // Step 1: Try optimistic read (no locking)
        long stamp = stampedLock.tryOptimisticRead();
        double currentX = x;
        double currentY = y;

        // Step 2: Validate that no write occurred during our read
        if (!stampedLock.validate(stamp)) {
            // A write occurred — fall back to pessimistic read lock
            stamp = stampedLock.readLock();
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp);
            }
        }

        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    /**
     * Write — acquires exclusive write lock.
     */
    public void move(double deltaX, double deltaY) {
        long stamp = stampedLock.writeLock();
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            stampedLock.unlockWrite(stamp);
        }
    }
}
```

### StampedLock vs ReentrantReadWriteLock:

| Feature | ReentrantReadWriteLock | StampedLock |
|---------|----------------------|-------------|
| Reentrancy | Yes | No |
| Optimistic reads | No | Yes |
| Lock downgrading | Yes | Yes (via conversion) |
| Condition variables | Yes | No |
| Best for | General use | Ultra-high read throughput |

---

## Writer Starvation vs Reader Starvation

### Writer Starvation (No Priority)
If readers keep arriving, a waiting writer might never get the lock. Our custom implementation prevents this by checking `writeRequests > 0` in `readLock()`.

### Reader Starvation (Writer Priority)
If writers keep arriving, readers might be starved. The `ReentrantReadWriteLock` with fair=true (`new ReentrantReadWriteLock(true)`) uses FIFO ordering to prevent both types of starvation.

### Fairness Policy:
```java
// Fair lock — threads acquire in FIFO order
ReadWriteLock fairLock = new ReentrantReadWriteLock(true);

// Non-fair lock (default) — higher throughput, possible starvation
ReadWriteLock unfairLock = new ReentrantReadWriteLock(false);
```

---

## Real-World Applications

1. **In-memory cache** (ConcurrentHashMap uses a form of read-write locking internally with lock striping)
2. **Configuration store** (read frequently, updated rarely)
3. **Database connection pool** metadata
4. **File system** (many readers, few writers)
5. **CopyOnWriteArrayList** (conceptually: readers never block, writes create a copy)

---

## Interview Tips

- **Explain the problem first**: "In read-heavy systems, using a single mutex serializes reads unnecessarily. A read-write lock allows concurrent reads, dramatically improving throughput."
- **Implement the custom version** from scratch with wait/notify to show deep understanding.
- **Discuss writer starvation**: Show you know the pitfall and how to prevent it (track `writeRequests`).
- **Mention StampedLock** for bonus points — it shows you know modern Java concurrency.
- **Always use try-finally** for lock release to prevent deadlocks.

## Related Concepts:
- **ConcurrentHashMap** uses segmented locking (similar to read-write concepts)
- **CopyOnWriteArrayList** is an alternative for small lists with frequent reads
- **Database row-level locking** (shared locks for reads, exclusive locks for writes)

## Note
**For Lead-Level:** A lead should be able to implement a read-write lock from scratch, explain writer starvation, discuss fairness policies, and know when to use `StampedLock` vs `ReentrantReadWriteLock` vs `ConcurrentHashMap`. They should also connect this to real-world systems like caches and configuration stores.

