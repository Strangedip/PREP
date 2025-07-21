# Top K Frequent Elements

## Problem Statement

Given an integer array `nums` and an integer `k`, return the **k most frequent elements**. You may return the answer in any order.

**Follow-up:** Your algorithm's time complexity must be better than O(n log n), where n is the array's size.

## Examples

**Example 1:**
```
Input: nums = [1,1,1,2,2,3], k = 2
Output: [1,2]

Frequency count:
- 1 appears 3 times
- 2 appears 2 times  
- 3 appears 1 time

Top 2 most frequent: [1, 2]
```

**Example 2:**
```
Input: nums = [1], k = 1
Output: [1]
```

**Example 3:**
```
Input: nums = [1,2,3,4,5], k = 3
Output: [1,2,3] (or any 3 elements since all have same frequency)
```

## Constraints

- 1 ≤ nums.length ≤ 10⁵
- k is in the range [1, the number of unique elements in the array]
- It is guaranteed that the answer is unique

## Solutions

### Approach 1: Min Heap ⭐ (Most Common in Interviews)

**Algorithm:**
1. Build frequency map of all elements
2. Use min heap of size k to maintain top k frequent elements
3. For each element, add to heap; if heap size > k, remove minimum

**Implementation:**
```java
public int[] topKFrequent(int[] nums, int k) {
    // Step 1: Build frequency map
    Map<Integer, Integer> frequencyMap = new HashMap<>();
    for (int num : nums) {
        frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
    }
    
    // Step 2: Min heap to keep top k frequent elements
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(
        (a, b) -> frequencyMap.get(a) - frequencyMap.get(b)
    );
    
    for (int num : frequencyMap.keySet()) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll(); // Remove least frequent
        }
    }
    
    // Step 3: Extract result
    int[] result = new int[k];
    for (int i = k - 1; i >= 0; i--) {
        result[i] = minHeap.poll();
    }
    
    return result;
}
```

**Time Complexity:** O(n log k) where n = array length  
**Space Complexity:** O(n + k)

**Why Min Heap?**
- Heap size is limited to k (space efficient)
- Always removes least frequent when size > k
- Final heap contains exactly k most frequent elements

### Approach 2: Bucket Sort ⭐ (Optimal Time Complexity)

**Algorithm:**
1. Build frequency map
2. Create buckets where index = frequency
3. Place elements in appropriate frequency buckets
4. Collect top k elements from highest frequency buckets

**Implementation:**
```java
public int[] topKFrequentBucket(int[] nums, int k) {
    Map<Integer, Integer> frequencyMap = new HashMap<>();
    for (int num : nums) {
        frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
    }
    
    // Create buckets: index = frequency
    List<Integer>[] buckets = new List[nums.length + 1];
    for (int i = 0; i < buckets.length; i++) {
        buckets[i] = new ArrayList<>();
    }
    
    // Fill buckets
    for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
        int frequency = entry.getValue();
        int number = entry.getKey();
        buckets[frequency].add(number);
    }
    
    // Collect top k from highest frequency buckets
    List<Integer> result = new ArrayList<>();
    for (int i = buckets.length - 1; i >= 0 && result.size() < k; i--) {
        if (!buckets[i].isEmpty()) {
            result.addAll(buckets[i]);
        }
    }
    
    return result.stream().limit(k).mapToInt(i -> i).toArray();
}
```

**Time Complexity:** O(n) - optimal!  
**Space Complexity:** O(n)

**Key Insight:** Maximum frequency ≤ array length, so we can use frequency as array index.

### Approach 3: Quick Select

**Algorithm:**
1. Build frequency map
2. Use quick select to find kth largest frequency
3. Partition array so that top k frequent elements are at the end

**Implementation:**
```java
public int[] topKFrequentQuickSelect(int[] nums, int k) {
    Map<Integer, Integer> frequencyMap = new HashMap<>();
    for (int num : nums) {
        frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
    }
    
    int[] uniqueNums = frequencyMap.keySet().stream().mapToInt(i -> i).toArray();
    
    int n = uniqueNums.length;
    quickSelect(uniqueNums, 0, n - 1, n - k, frequencyMap);
    
    return Arrays.copyOfRange(uniqueNums, n - k, n);
}

private void quickSelect(int[] nums, int left, int right, int kSmallest, 
                       Map<Integer, Integer> frequencyMap) {
    if (left == right) return;
    
    Random random = new Random();
    int pivotIndex = left + random.nextInt(right - left + 1);
    
    pivotIndex = partition(nums, left, right, pivotIndex, frequencyMap);
    
    if (pivotIndex == kSmallest) {
        return;
    } else if (pivotIndex < kSmallest) {
        quickSelect(nums, pivotIndex + 1, right, kSmallest, frequencyMap);
    } else {
        quickSelect(nums, left, pivotIndex - 1, kSmallest, frequencyMap);
    }
}
```

**Time Complexity:** O(n) average, O(n²) worst case  
**Space Complexity:** O(n)

**Trade-off:** Best average case but can degrade to O(n²)

### Approach 4: Max Heap (Simple but Less Efficient)

**Algorithm:**
1. Build frequency map
2. Add all elements to max heap sorted by frequency
3. Extract top k elements

**Implementation:**
```java
public int[] topKFrequentMaxHeap(int[] nums, int k) {
    Map<Integer, Integer> frequencyMap = new HashMap<>();
    for (int num : nums) {
        frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
    }
    
    // Max heap based on frequency
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
        (a, b) -> frequencyMap.get(b) - frequencyMap.get(a)
    );
    
    maxHeap.addAll(frequencyMap.keySet());
    
    int[] result = new int[k];
    for (int i = 0; i < k; i++) {
        result[i] = maxHeap.poll();
    }
    
    return result;
}
```

**Time Complexity:** O(n log n)  
**Space Complexity:** O(n)

**Note:** Doesn't meet the O(n log n) requirement, but simple to implement.

## Performance Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Min Heap | O(n log k) | O(n + k) | Space efficient, works for large n | Not optimal time |
| Bucket Sort | O(n) | O(n) | **Optimal time** | Uses more space |
| Quick Select | O(n) avg | O(n) | Good average case | Worst case O(n²) |
| Max Heap | O(n log n) | O(n) | Simple to implement | Exceeds time requirement |

## When to Use Each Approach

### Use Min Heap When:
- k is much smaller than n
- Memory is constrained
- Interview setting (most commonly expected)

### Use Bucket Sort When:
- Need guaranteed O(n) time
- Can afford O(n) extra space
- Frequency range is bounded

### Use Quick Select When:
- Want average O(n) performance
- Don't mind worst-case O(n²)
- Need to avoid extra space

## Advanced Variations

### 1. Top K Frequent Words

```java
public List<String> topKFrequentWords(String[] words, int k) {
    Map<String, Integer> frequencyMap = new HashMap<>();
    for (String word : words) {
        frequencyMap.put(word, frequencyMap.getOrDefault(word, 0) + 1);
    }
    
    PriorityQueue<String> minHeap = new PriorityQueue<>((a, b) -> {
        int freqCompare = frequencyMap.get(a) - frequencyMap.get(b);
        if (freqCompare == 0) {
            return b.compareTo(a); // Lexicographical order
        }
        return freqCompare;
    });
    
    for (String word : frequencyMap.keySet()) {
        minHeap.offer(word);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }
    
    List<String> result = new ArrayList<>();
    while (!minHeap.isEmpty()) {
        result.add(0, minHeap.poll());
    }
    
    return result;
}
```

### 2. Top K Least Frequent Elements

```java
public int[] topKLeastFrequent(int[] nums, int k) {
    // Use max heap instead of min heap
    PriorityQueue<Integer> maxHeap = new PriorityQueue<>(
        (a, b) -> frequencyMap.get(b) - frequencyMap.get(a)
    );
    
    // Rest of logic similar but with max heap
}
```

### 3. Streaming Top K Frequent

```java
class StreamingTopK {
    private Map<Integer, Integer> frequencies;
    private PriorityQueue<Integer> minHeap;
    private int k;
    
    public StreamingTopK(int k) {
        this.k = k;
        this.frequencies = new HashMap<>();
        this.minHeap = new PriorityQueue<>(
            (a, b) -> frequencies.get(a) - frequencies.get(b)
        );
    }
    
    public void add(int num) {
        frequencies.put(num, frequencies.getOrDefault(num, 0) + 1);
        
        if (!minHeap.contains(num)) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
    }
    
    public List<Integer> getTopK() {
        return new ArrayList<>(minHeap);
    }
}
```

## Common Mistakes

1. **Using max heap of size n instead of min heap of size k**
   - Results in O(n log n) instead of O(n log k)

2. **Incorrect heap comparator**
   - Forgetting that min heap needs `a - b` for ascending order

3. **Not handling equal frequencies**
   - Problem statement guarantees unique answer, but good to consider

4. **Off-by-one errors in bucket sort**
   - Bucket array should be size `n + 1` not `n`

5. **Inefficient result extraction**
   - Forgetting to reverse order when extracting from min heap

## Edge Cases

```java
// Test cases to consider:
1. k = 1 (most frequent element)
2. k = number of unique elements (all elements)
3. All elements have same frequency
4. Single element array
5. k = n (return all elements)
6. Negative numbers
7. Large frequencies
```

## Related Problems

- **Kth Largest Element in Array** - Similar heap/quick select concepts
- **Find K Pairs with Smallest Sums** - Multiple heaps
- **Top K Frequent Words** - String variation with lexicographical ordering
- **Find Median from Data Stream** - Dynamic heap maintenance
- **Sliding Window Maximum** - Deque vs heap trade-offs

## Interview Tips

1. **Ask clarifications:**
   - Can k be larger than unique elements?
   - What if multiple elements have same frequency?
   - Any memory constraints?

2. **Start with heap approach:**
   - Most interviewers expect min heap solution
   - Then optimize to bucket sort if time permits

3. **Discuss trade-offs:**
   - Time vs space complexity
   - Average vs worst case performance

4. **Code cleanly:**
   - Separate frequency counting from top-k finding
   - Use clear variable names

5. **Test with examples:**
   - Walk through small example
   - Verify heap ordering logic

## Follow-up Questions

1. **"What if k is very large compared to n?"** → Consider max heap approach
2. **"What if we need to handle updates?"** → Discuss streaming solution
3. **"Memory constraints?"** → Compare space usage of different approaches
4. **"What about duplicate handling?"** → Clarify problem requirements
5. **"Can you do better than O(n)?"** → Explain why O(n) is optimal for this problem

This problem is excellent for demonstrating understanding of heaps, hash maps, and algorithm optimization techniques. It's a favorite in technical interviews due to its multiple valid solution approaches and practical applications in data analysis and recommendation systems. 