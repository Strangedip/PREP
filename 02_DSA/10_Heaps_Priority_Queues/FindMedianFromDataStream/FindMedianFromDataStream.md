# Find Median from Data Stream (LeetCode 295)

## Problem Statement

The median is the middle value in an ordered integer list. If the size of the list is even, there is no middle value, and the median is the mean of the two middle values.

Implement the `MedianFinder` class:
- `MedianFinder()` initializes the MedianFinder object.
- `void addNum(int num)` adds the integer `num` from the data stream to the data structure.
- `double findMedian()` returns the median of all elements so far. Answers within `10^-5` of the actual answer will be accepted.

**Example:**
```
Input:
["MedianFinder", "addNum", "addNum", "findMedian", "addNum", "findMedian"]
[[], [1], [2], [], [3], []]

Output: [null, null, null, 1.5, null, 2.0]

Explanation:
MedianFinder medianFinder = new MedianFinder();
medianFinder.addNum(1);    // arr = [1]
medianFinder.addNum(2);    // arr = [1, 2]
medianFinder.findMedian(); // return 1.5 (i.e., (1 + 2) / 2)
medianFinder.addNum(3);    // arr[1, 2, 3]
medianFinder.findMedian(); // return 2.0
```

**Constraints:**
- `-10^5 <= num <= 10^5`
- There will be at least one element in the data structure before calling `findMedian`.
- At most `5 * 10^4` calls will be made to `addNum` and `findMedian`.

---

## Why This Problem Is Important

This is one of the most frequently asked design + algorithm problems at FAANG companies. It tests:
1. Understanding of heap data structures
2. Invariant maintenance
3. API design
4. Handling edge cases in a streaming context

---

## Approach 1: Sorted List (Naive)

**addNum:** O(n) — find insertion point and shift elements
**findMedian:** O(1) — access middle element(s)

```java
class MedianFinder {
    private List<Integer> sorted = new ArrayList<>();
    
    public void addNum(int num) {
        int pos = Collections.binarySearch(sorted, num);
        if (pos < 0) pos = -(pos + 1); // insertion point
        sorted.add(pos, num);
    }
    
    public double findMedian() {
        int n = sorted.size();
        if (n % 2 == 1) {
            return sorted.get(n / 2);
        }
        return (sorted.get(n / 2 - 1) + sorted.get(n / 2)) / 2.0;
    }
}
```

**Why not optimal**: `ArrayList.add(pos, num)` is O(n) due to element shifting. Over `n` insertions, total time is O(n²).

---

## Approach 2: Two Heaps (Optimal)

**addNum:** O(log n)
**findMedian:** O(1)

### Core Insight

Divide all numbers seen so far into two halves:
- **Left half** (smaller numbers): stored in a **Max Heap** so we can quickly access the largest of the small numbers.
- **Right half** (larger numbers): stored in a **Min Heap** so we can quickly access the smallest of the large numbers.

### Invariants

1. `maxHeap.size() == minHeap.size()` OR `maxHeap.size() == minHeap.size() + 1`
2. Every element in `maxHeap` ≤ every element in `minHeap`

### How to Maintain Invariants

On every `addNum`:
1. Add the number to `maxHeap` first.
2. Move the top of `maxHeap` to `minHeap` (this ensures the largest element in the left half goes to the right half).
3. If `minHeap` has more elements than `maxHeap`, move the top of `minHeap` back to `maxHeap`.

This three-step process guarantees both invariants are always satisfied.

### Complete Implementation

```java
import java.util.*;

class MedianFinder {
    
    // Max heap: stores the smaller half of numbers
    // Top element = largest in the smaller half
    private PriorityQueue<Integer> maxHeap;
    
    // Min heap: stores the larger half of numbers
    // Top element = smallest in the larger half
    private PriorityQueue<Integer> minHeap;
    
    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        // Step 1: Always add to maxHeap first
        maxHeap.offer(num);
        
        // Step 2: Balance — move the largest from left half to right half
        minHeap.offer(maxHeap.poll());
        
        // Step 3: Ensure maxHeap is always >= minHeap in size
        if (maxHeap.size() < minHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            // Odd number of elements — median is the top of maxHeap
            return maxHeap.peek();
        } else {
            // Even number of elements — median is average of both tops
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }
}
```

### Dry Run

```
Adding: 5, 15, 1, 3

Add 5:
  maxHeap.offer(5) → maxHeap=[5]
  minHeap.offer(maxHeap.poll()) → maxHeap=[], minHeap=[5]
  maxHeap.size(0) < minHeap.size(1) → maxHeap.offer(minHeap.poll())
  → maxHeap=[5], minHeap=[]
  Median: 5.0 (odd, top of maxHeap)

Add 15:
  maxHeap.offer(15) → maxHeap=[15, 5]
  minHeap.offer(maxHeap.poll()) → maxHeap=[5], minHeap=[15]
  maxHeap.size(1) == minHeap.size(1) → no rebalance
  → maxHeap=[5], minHeap=[15]
  Median: (5 + 15) / 2.0 = 10.0

Add 1:
  maxHeap.offer(1) → maxHeap=[5, 1]
  minHeap.offer(maxHeap.poll()) → maxHeap=[1], minHeap=[5, 15]
  maxHeap.size(1) < minHeap.size(2) → maxHeap.offer(minHeap.poll())
  → maxHeap=[5, 1], minHeap=[15]
  Median: 5.0 (odd, top of maxHeap)

Add 3:
  maxHeap.offer(3) → maxHeap=[5, 1, 3]
  minHeap.offer(maxHeap.poll()) → maxHeap=[3, 1], minHeap=[5, 15]
  maxHeap.size(2) == minHeap.size(2) → no rebalance
  → maxHeap=[3, 1], minHeap=[5, 15]
  Median: (3 + 5) / 2.0 = 4.0

Verify: sorted = [1, 3, 5, 15] → median = (3+5)/2 = 4.0 ✓
```

### Visual Representation

```
After adding [1, 3, 5, 15]:

        maxHeap (left half)    |    minHeap (right half)
        ──────────────────     |    ──────────────────
             [3]               |         [5]
            /                  |           \
          [1]                  |           [15]
                               |
        Top: 3                 |    Top: 5
        
Median = (3 + 5) / 2.0 = 4.0
```

---

## Approach 3: Two Heaps with Follow-Up Optimization

### Follow-Up: If all integers are in range [0, 100]

Use **counting sort / bucket approach**: maintain a count array of size 101 and a total count. Finding the median is O(101) = O(1).

```java
class MedianFinder {
    private int[] count = new int[101];
    private int total = 0;
    
    public void addNum(int num) {
        count[num]++;
        total++;
    }
    
    public double findMedian() {
        int target1 = (total + 1) / 2; // position of first middle element
        int target2 = (total + 2) / 2; // position of second middle element
        
        int cumulative = 0;
        int median1 = -1, median2 = -1;
        
        for (int i = 0; i <= 100; i++) {
            cumulative += count[i];
            if (median1 == -1 && cumulative >= target1) {
                median1 = i;
            }
            if (median2 == -1 && cumulative >= target2) {
                median2 = i;
                break;
            }
        }
        
        return (median1 + median2) / 2.0;
    }
}
```

### Follow-Up: If 99% of integers are in range [0, 100]

Use buckets for [0, 100] and two overflow lists (one for negatives, one for values > 100). Process the overflow lists only when the median falls outside [0, 100].

---

## Approach 4: Balanced BST (TreeMap)

**addNum:** O(log n), **findMedian:** O(log n)

Use a `TreeMap` (self-balancing BST) that supports order-statistics operations.

```java
class MedianFinder {
    private TreeMap<Integer, Integer> tree = new TreeMap<>();
    private int total = 0;
    
    public void addNum(int num) {
        tree.merge(num, 1, Integer::sum);
        total++;
    }
    
    public double findMedian() {
        int count = 0;
        int target1 = (total - 1) / 2;
        int target2 = total / 2;
        int val1 = 0, val2 = 0;
        
        for (Map.Entry<Integer, Integer> entry : tree.entrySet()) {
            count += entry.getValue();
            if (count > target1 && val1 == 0) val1 = entry.getKey();
            if (count > target2) {
                val2 = entry.getKey();
                break;
            }
        }
        
        return (val1 + val2) / 2.0;
    }
}
```

Note: This approach has O(log n) for `findMedian` (not O(1)), so the two-heap approach is preferred.

---

## Approach Comparison

| Approach | addNum | findMedian | Space | Best For |
|----------|--------|-----------|-------|----------|
| Sorted List | O(n) | O(1) | O(n) | Small datasets |
| Two Heaps | O(log n) | O(1) | O(n) | General case (preferred) |
| Counting Sort | O(1) | O(100)≈O(1) | O(101) | Bounded integers [0,100] |
| Balanced BST | O(log n) | O(log n) | O(n) | Need other operations too |

---

## Common Mistakes

1. **Using min heap for small half**: The small half needs a MAX heap (to access the largest of the small numbers).
2. **Wrong rebalancing**: The invariant is `maxHeap.size() >= minHeap.size()`, not the other way around. If you swap which heap is larger, the median formula changes.
3. **Integer division in median**: `(a + b) / 2` uses integer division! Must use `(a + b) / 2.0` or cast to double.
4. **Not handling the first element**: The algorithm works even for the first element, but make sure `peek()` is not called on an empty heap.
5. **Forgetting about integer overflow**: For very large numbers, `maxHeap.peek() + minHeap.peek()` could overflow. Use `maxHeap.peek() / 2.0 + minHeap.peek() / 2.0` to avoid this.

---

## Edge Cases

| Case | Sequence | Expected Median |
|------|----------|-----------------|
| Single element | [5] | 5.0 |
| Two elements | [5, 10] | 7.5 |
| All same | [3, 3, 3] | 3.0 |
| Negative numbers | [-5, -3, -1] | -3.0 |
| Mixed signs | [-2, 0, 2] | 0.0 |
| Large stream | 50,000 elements | Should handle efficiently |

---

## Thread Safety Consideration (Lead-Level)

In a multi-threaded streaming context, the two-heap approach needs synchronization:

```java
class ThreadSafeMedianFinder {
    private final PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
    private final PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    private final ReentrantLock lock = new ReentrantLock();
    
    public void addNum(int num) {
        lock.lock();
        try {
            maxHeap.offer(num);
            minHeap.offer(maxHeap.poll());
            if (maxHeap.size() < minHeap.size()) {
                maxHeap.offer(minHeap.poll());
            }
        } finally {
            lock.unlock();
        }
    }
    
    public double findMedian() {
        lock.lock();
        try {
            if (maxHeap.size() > minHeap.size()) {
                return maxHeap.peek();
            }
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        } finally {
            lock.unlock();
        }
    }
}
```

---

## LeetCode Similar Problems

- [480. Sliding Window Median](https://leetcode.com/problems/sliding-window-median/) — Two heaps with lazy deletion
- [703. Kth Largest Element in a Stream](https://leetcode.com/problems/kth-largest-element-in-a-stream/) — Min heap of size K
- [502. IPO](https://leetcode.com/problems/ipo/) — Two heaps for greedy selection
- [253. Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/) — Min heap for scheduling
- [378. Kth Smallest Element in a Sorted Matrix](https://leetcode.com/problems/kth-smallest-element-in-a-sorted-matrix/) — Heap-based selection

---

## Interview Tips

1. **Immediately state the two-heap approach**: "I'll use a max heap for the smaller half and a min heap for the larger half. This gives O(log n) insertion and O(1) median."
2. **Draw the two heaps visually**: Show the invariant with boxes and arrows.
3. **Explain the three-step add process**: "Add to max, move top to min, rebalance if needed. This maintains both invariants."
4. **Discuss the follow-ups proactively**: Mention the bounded-range optimization before the interviewer asks.
5. **For Lead-level**: Discuss thread safety, distributed median finding (merge heaps from multiple nodes), and the connection to the order statistics problem (finding the kth element in a stream).
6. **Know the Sliding Window Median variant**: LeetCode 480 is a harder version where you need to find the median within a sliding window. It uses the same two-heap approach but adds lazy deletion.
