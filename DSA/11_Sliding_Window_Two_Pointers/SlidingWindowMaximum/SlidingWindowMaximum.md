# Sliding Window Maximum

## Problem Statement

Given an array of integers `nums` and a sliding window of size `k`, find the maximum element in each window position as the window slides from left to right.

**Constraints:**
- 1 ≤ nums.length ≤ 10^5
- -10^4 ≤ nums[i] ≤ 10^4
- 1 ≤ k ≤ nums.length

## Example
```
Input: nums = [1,3,-1,-3,5,3,6,7], k = 3
Output: [3,3,5,5,6,7]

Window position                Max
[1  3  -1] -3  5  3  6  7      3
 1 [3  -1  -3] 5  3  6  7      3
 1  3 [-1  -3  5] 3  6  7      5
 1  3  -1 [-3  5  3] 6  7      5
 1  3  -1  -3 [5  3  6] 7      6
 1  3  -1  -3  5 [3  6  7]     7
```

## Approach 1: Brute Force

For each window position, scan all k elements to find the maximum.

```java
public int[] maxSlidingWindowBrute(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    
    for (int i = 0; i <= n - k; i++) {
        int max = nums[i];
        for (int j = i + 1; j < i + k; j++) {
            max = Math.max(max, nums[j]);
        }
        result[i] = max;
    }
    
    return result;
}
```

**Time**: O(n × k) — For each of the (n - k + 1) windows, scan k elements.
**Space**: O(1) extra (besides output).

## Approach 2: Monotonic Deque (Optimal)

### Key Insight
Maintain a deque (double-ended queue) that stores **indices** of elements in decreasing order of their values. The front of the deque always has the index of the maximum element in the current window.

### Why It Works
- When a new element enters the window, we remove all smaller elements from the back of the deque because they can never be the maximum for any future window (the new element is larger and will stay in the window longer).
- When the window slides past an element, we remove it from the front of the deque if it is there.
- This maintains a decreasing monotonic deque where the front is always the maximum.

### Algorithm
1. For each element `nums[i]`:
   - **Remove expired**: If the front of the deque is outside the window (`deque.peekFirst() < i - k + 1`), remove it.
   - **Maintain decreasing order**: Remove all elements from the back of the deque that are smaller than `nums[i]`.
   - **Add current**: Push `i` to the back of the deque.
   - **Record result**: Once we've processed at least `k` elements, `nums[deque.peekFirst()]` is the window maximum.

```java
public int[] maxSlidingWindow(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    Deque<Integer> deque = new ArrayDeque<>();  // Stores indices
    
    for (int i = 0; i < n; i++) {
        // Remove indices that are out of the current window
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // Remove all indices whose corresponding values are less than nums[i]
        // They can never be the maximum for any future window
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        // Add current index
        deque.offerLast(i);
        
        // Record the maximum for this window
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

### Walkthrough
```
nums = [1, 3, -1, -3, 5, 3, 6, 7], k = 3

i=0: deque=[0]           → nums[0]=1 added
i=1: deque=[1]           → 1 < 3, remove 0; add 1
i=2: deque=[1, 2]        → -1 < 3, keep 1; add 2 → result[0]=nums[1]=3
i=3: deque=[1, 2, 3]     → -3 < -1, keep all; add 3 → result[1]=nums[1]=3
i=4: deque=[4]           → 5 > all, clear; add 4 → result[2]=nums[4]=5
                            (also 1 is expired, but already removed)
i=5: deque=[4, 5]        → 3 < 5, keep 4; add 5 → result[3]=nums[4]=5
i=6: deque=[6]           → 6 > all; add 6 → result[4]=nums[6]=6
i=7: deque=[7]           → 7 > 6; add 7 → result[5]=nums[7]=7
```

**Time**: O(n) — Each element is pushed and popped at most once from the deque.
**Space**: O(k) — Deque stores at most k indices.

## Approach 3: Max Heap (Priority Queue)

Use a max-heap to always get the maximum. Lazy deletion: only remove elements from the heap when they appear at the top and are outside the window.

```java
public int[] maxSlidingWindowHeap(int[] nums, int k) {
    int n = nums.length;
    int[] result = new int[n - k + 1];
    
    // Max-heap: [value, index]
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> b[0] != a[0] ? b[0] - a[0] : b[1] - a[1]
    );
    
    for (int i = 0; i < n; i++) {
        maxHeap.offer(new int[]{nums[i], i});
        
        // Remove elements outside the window (lazy deletion)
        while (maxHeap.peek()[1] < i - k + 1) {
            maxHeap.poll();
        }
        
        if (i >= k - 1) {
            result[i - k + 1] = maxHeap.peek()[0];
        }
    }
    
    return result;
}
```

**Time**: O(n log n) worst case — Each push/pop is O(log n), and lazy deletion may leave extra elements.
**Space**: O(n) — Heap can hold up to n elements.

## Comparison of Approaches

| Approach | Time | Space | When to Use |
|----------|------|-------|-------------|
| Brute Force | O(n × k) | O(1) | Only for small k |
| Monotonic Deque | O(n) | O(k) | **Optimal — always use this** |
| Max Heap | O(n log n) | O(n) | When you already have a heap infrastructure |

## Edge Cases

1. **k = 1**: Every element is its own window maximum → return the array itself.
2. **k = n**: Single window → return the max of the entire array.
3. **All elements equal**: Deque always has one element.
4. **Strictly decreasing array**: Deque is always full (k elements).
5. **Strictly increasing array**: Deque always has just the latest element.

## Real-World Applications

1. **Stock trading**: Find the maximum stock price in the last K trading days.
2. **Network monitoring**: Track peak bandwidth usage in a sliding time window.
3. **Temperature analysis**: Find the hottest day in the last K days.
4. **Rate limiting**: Track maximum requests per sliding time window.

## Interview Tips

1. **Always mention the deque approach** — This is the expected optimal solution.
2. **Explain why we store indices, not values** — We need to know when elements expire from the window.
3. **The key insight is**: "If `nums[i] >= nums[j]` and `i > j`, then `j` can never be the answer for any future window that contains `i`."
4. **Follow-up**: "How would you handle a sliding window minimum?" → Same approach, but maintain an increasing deque instead of decreasing.

## Related Problems
- [Sliding Window Minimum](same approach, increasing deque)
- [Longest Subarray with Limit](deque for max and min)
- [Max Value of Equation](deque optimization)
- [Constrained Subsequence Sum](DP + deque optimization)

