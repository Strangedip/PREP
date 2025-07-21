# Find Median from Data Stream

## Problem Statement
Design a data structure that supports the following two operations:
- `addNum(int num)` - Add a number to the data structure
- `findMedian()` - Return the median of all elements so far

## Approach: Two Heaps

### Key Insight
Use two heaps to maintain the smaller and larger halves of the numbers:
- **Max Heap**: Stores the smaller half of numbers
- **Min Heap**: Stores the larger half of numbers

### Algorithm
1. **Initialization**: Create two heaps
   - `maxHeap`: Max heap for smaller half
   - `minHeap`: Min heap for larger half

2. **Adding a number**:
   - Always add to max heap first
   - Move the largest element from max heap to min heap
   - Balance heaps so max heap has at most 1 more element than min heap

3. **Finding median**:
   - If max heap has more elements: return its top
   - Otherwise: return average of both tops

### Time Complexity
- `addNum`: **O(log n)** - Heap operations
- `findMedian`: **O(1)** - Just peek at heap tops

### Space Complexity
- **O(n)** - Store all n numbers in heaps

## Code Walkthrough

```java
public void addNum(int num) {
    // Step 1: Add to max heap first
    maxHeap.offer(num);
    
    // Step 2: Move largest from max heap to min heap
    minHeap.offer(maxHeap.poll());
    
    // Step 3: Balance heaps
    if (maxHeap.size() < minHeap.size()) {
        maxHeap.offer(minHeap.poll());
    }
}
```

### Example Trace
Adding numbers: [1, 2, 3]

1. **Add 1**:
   - maxHeap: [1], minHeap: []
   - Move 1 to minHeap: maxHeap: [], minHeap: [1]
   - Balance: maxHeap: [1], minHeap: []
   - Median: 1.0

2. **Add 2**:
   - maxHeap: [1, 2], minHeap: []
   - Move 2 to minHeap: maxHeap: [1], minHeap: [2]
   - No balance needed
   - Median: (1 + 2) / 2 = 1.5

3. **Add 3**:
   - maxHeap: [1, 3], minHeap: [2]
   - Move 3 to minHeap: maxHeap: [1], minHeap: [2, 3]
   - Balance: maxHeap: [1, 2], minHeap: [3]
   - Median: 2.0

## Key Points
- Always maintain heap size invariant: `|maxHeap.size() - minHeap.size()| ≤ 1`
- Max heap contains smaller half, min heap contains larger half
- The algorithm ensures efficient median calculation in constant time 