# Merge Intervals

## Problem Statement

Given an array of `intervals` where `intervals[i] = [starti, endi]`, merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.

**Key requirements:**
- Merge overlapping intervals
- Return non-overlapping intervals
- Cover all original intervals

## Example
```
Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
Output: [[1,6],[8,10],[15,18]]

Explanation:
[1,3] and [2,6] overlap → merge to [1,6]
[8,10] and [15,18] don't overlap → keep separate

Visual:
1---3      →    1-----6
  2---6

8--10           8--10

15---18         15---18
```

## Understanding the Problem

### Key Insights:
1. **Overlap condition**: Two intervals `[a,b]` and `[c,d]` overlap if `b ≥ c` (assuming `a ≤ c`)
2. **Sorting helps**: If we sort by start time, we only need to check adjacent intervals
3. **Greedy approach**: We can merge intervals as we encounter them

### Types of Overlaps:
```
Case 1: Complete overlap     Case 2: Partial overlap
[1,5]                       [1,3]
  [2,4]  → [1,5]             [2,5]  → [1,5]

Case 3: Adjacent (touching) Case 4: No overlap
[1,3]                       [1,2]
[4,6]  → [1,6]               [4,5]  → [1,2], [4,5]
```

## Approach 1: Sort Then Merge (Optimal) ⭐

### How it works:
Sort intervals by start time, then iterate through and merge overlapping intervals.

### Algorithm Steps:
1. **Sort** intervals by start time
2. **Initialize** with first interval
3. **Iterate** through remaining intervals:
   - If current overlaps with last merged → extend the last interval
   - If no overlap → add current as new interval

### Example Walkthrough:
```
Input: [[1,3],[2,6],[8,10],[15,18]]

Step 1: Sort by start time → [[1,3],[2,6],[8,10],[15,18]] (already sorted)

Step 2: Start with [1,3] → merged = [[1,3]]

Step 3: Process [2,6]
        3 ≥ 2 (overlap!) → merge to [1,6]
        merged = [[1,6]]

Step 4: Process [8,10]
        6 < 8 (no overlap) → add separately
        merged = [[1,6],[8,10]]

Step 5: Process [15,18]
        10 < 15 (no overlap) → add separately
        merged = [[1,6],[8,10],[15,18]]
```

### Code Logic:
```java
// Sort by start time
Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

List<int[]> merged = new ArrayList<>();
int[] currentInterval = intervals[0];
merged.add(currentInterval);

for (int i = 1; i < intervals.length; i++) {
    int[] nextInterval = intervals[i];
    
    if (currentInterval[1] >= nextInterval[0]) {
        // Overlap: merge by extending end time
        currentInterval[1] = Math.max(currentInterval[1], nextInterval[1]);
    } else {
        // No overlap: start new interval
        currentInterval = nextInterval;
        merged.add(currentInterval);
    }
}
```

### Time & Space Complexity:
- **Time:** O(n log n) - Dominated by sorting
- **Space:** O(log n) for sorting + O(n) for result = O(n)

### When to use:
- **This is the preferred interview solution**
- Most efficient for general case
- Clean and intuitive logic

## Approach 2: Cleaner Implementation

### How it works:
Same sorting approach but with cleaner conditional logic.

### Key Difference:
```java
for (int[] interval : intervals) {
    if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
        merged.add(interval);  // No overlap
    } else {
        // Overlap: extend last interval
        merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
    }
}
```

### When to use:
- When you want cleaner, more readable code
- Slightly different style preference
- Same performance as Approach 1

## Approach 3: Using Stack

### How it works:
Use a stack to keep track of intervals, popping and merging when overlaps are found.

### Algorithm Steps:
1. Sort intervals by start time
2. Push first interval onto stack
3. For each remaining interval:
   - If overlaps with stack top → merge and update top
   - If no overlap → push onto stack

### When to use:
- When interviewer specifically asks for stack-based solution
- To demonstrate understanding of different data structures
- Same time complexity but slightly more space usage

## Approach 4: Coordinate Compression (Advanced)

### How it works:
Use a sweep line algorithm with events to track interval starts and ends.

### Algorithm Steps:
1. Create events: +1 for start, -1 for end+1
2. Sort events by time
3. Sweep through events, tracking active interval count
4. When count goes from 0→1: start new merged interval
5. When count goes from 1→0: end current merged interval

### Example:
```
Intervals: [[1,3],[2,6]]
Events: [1,+1], [2,+1], [4,-1], [7,-1]

Time 1: count=1, start=1
Time 2: count=2 (still in interval)
Time 4: count=1 (still in interval) 
Time 7: count=0, end merged interval [1,6]
```

### When to use:
- Advanced interviews requiring sweep line knowledge
- When dealing with very large numbers of intervals
- Educational purposes to show algorithmic versatility

## Approach 5: Brute Force (Educational)

### How it works:
For each interval, find all other intervals that overlap with it and merge them iteratively.

### Time Complexity: O(n²)
- Very inefficient but helps understand the problem
- Only for educational purposes or very small inputs

## Interview Tips

### What to say in an interview:
1. **Clarify overlap definition:** "Should I consider [1,3] and [4,6] as overlapping?" (Usually no)
2. **Discuss sorting:** "I'll sort by start time to make checking overlaps easier..."
3. **Explain merge logic:** "When intervals overlap, I take the minimum start and maximum end..."
4. **Consider edge cases:** "What about empty input or single interval?"

### Common Follow-up Questions:
1. **"What if intervals can be negative?"** → Same algorithm works
2. **"How would you handle very large numbers?"** → Discuss overflow protection
3. **"Can you do it without sorting?"** → Discuss coordinate compression or other approaches
4. **"What about merging meeting rooms?"** → Similar problem with different constraints

## Edge Cases to Consider

1. **Empty input:** `intervals = []` → Return `[]`
2. **Single interval:** `intervals = [[1,4]]` → Return `[[1,4]]`
3. **No overlaps:** `intervals = [[1,2],[3,4],[5,6]]` → Return as-is
4. **All overlapping:** `intervals = [[1,4],[2,5],[3,6]]` → Return `[[1,6]]`
5. **Adjacent intervals:** `intervals = [[1,3],[4,6]]` → Are these merged? (Usually no)
6. **Touching intervals:** `intervals = [[1,4],[4,5]]` → Usually merged to `[[1,5]]`
7. **Duplicate intervals:** `intervals = [[1,3],[1,3]]` → Return `[[1,3]]`
8. **Unsorted input:** Algorithm should handle any order

## Common Mistakes to Avoid

### Sorting Issues:
1. **Forgetting to sort:** Not sorting by start time first
2. **Wrong comparator:** Sorting by end time instead of start time
3. **Stability concerns:** Not handling equal start times properly

### Merging Logic:
1. **Overlap condition:** Using `>` instead of `≥` for overlap check
2. **Merge formula:** Not using `Math.max()` for end time
3. **Reference handling:** Modifying original intervals vs. creating new ones

### Edge Cases:
1. **Adjacent intervals:** Mishandling touching intervals
2. **Single interval:** Not handling trivial cases
3. **Empty input:** Missing null/empty checks

## Optimization Notes

### Performance Considerations:
- Sorting dominates time complexity - consider using custom comparators
- For very large datasets, consider external sorting
- Memory usage can be optimized by reusing interval arrays

### Space Optimization:
- Can modify input array in-place if allowed
- Use primitive arrays instead of ArrayList for better memory efficiency
- Consider using iterative merging for memory-constrained environments

## Real-world Applications

1. **Calendar applications:** Merging overlapping meetings/events
2. **Resource scheduling:** Combining overlapping resource allocations
3. **Network monitoring:** Merging overlapping time windows for analysis
4. **Financial systems:** Consolidating overlapping trading periods
5. **Video processing:** Merging overlapping clips or segments

## Related Problems

1. **Insert Interval:** Insert a new interval and merge if necessary
2. **Non-overlapping Intervals:** Find minimum intervals to remove
3. **Meeting Rooms:** Determine if all meetings can be attended
4. **Meeting Rooms II:** Find minimum number of meeting rooms needed

## Complexity Analysis Comparison

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Sort + Merge** | **O(n log n)** | **O(n)** | **Interview favorite** |
| Cleaner Implementation | O(n log n) | O(n) | Code readability |
| Stack-based | O(n log n) | O(n) | Stack preference |
| Coordinate Compression | O(n log n) | O(n) | Advanced algorithms |
| Brute Force | O(n²) | O(n) | Educational only |

## Implementation Tips

### Code Structure:
```java
public int[][] merge(int[][] intervals) {
    // 1. Input validation
    if (intervals == null || intervals.length <= 1) return intervals;
    
    // 2. Sort by start time
    Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
    
    // 3. Initialize result
    List<int[]> merged = new ArrayList<>();
    
    // 4. Process each interval
    for (int[] interval : intervals) {
        // Merge logic here
    }
    
    // 5. Convert to array and return
    return merged.toArray(new int[merged.size()][]);
}
```

### Testing Strategy:
1. **Basic cases:** Simple overlapping and non-overlapping
2. **Edge cases:** Empty, single, all overlapping
3. **Boundary cases:** Adjacent/touching intervals
4. **Performance:** Large inputs with many overlaps

## Note

**For mid-level interviews (2+ years experience):**
- Start with the optimal sort-and-merge approach
- Explain why sorting by start time is the key insight
- Handle edge cases naturally (empty input, single interval)
- Code it cleanly and efficiently
- Be ready to discuss alternative approaches if asked

**Remember:** This problem tests your ability to recognize that sorting simplifies the problem dramatically. The key insight is that once sorted by start time, you only need to check adjacent intervals for overlaps! 