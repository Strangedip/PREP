# Insert Interval

## Problem Statement
Given a set of non-overlapping intervals sorted by their start times, insert a new interval and merge any overlapping intervals.

## Example
```
Input: intervals = [[1,3],[6,9]], newInterval = [2,5]
Output: [[1,5],[6,9]]
Explanation: [2,5] overlaps with [1,3], so they merge to [1,5]
```

## Approach: Linear Scan and Merge

### How it works:
1. **Add non-overlapping intervals before new interval**
2. **Merge all overlapping intervals with new interval**
3. **Add remaining non-overlapping intervals after**

### Key Logic:
```java
// Phase 1: Add intervals that end before newInterval starts
while (i < intervals.length && intervals[i][1] < newInterval[0]) {
    result.add(intervals[i]);
    i++;
}

// Phase 2: Merge overlapping intervals
while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
    newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
    newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
    i++;
}
result.add(newInterval);

// Phase 3: Add remaining intervals
while (i < intervals.length) {
    result.add(intervals[i]);
    i++;
}
```

### Time & Space Complexity:
- **Time:** O(n) - Single pass through intervals
- **Space:** O(n) - For result array

## Edge Cases:
1. **Empty intervals array**
2. **New interval doesn't overlap with any existing**
3. **New interval overlaps with all existing intervals**
4. **Insert at beginning or end**

## LeetCode Similar Problems:
- [56. Merge Intervals](https://leetcode.com/problems/merge-intervals/)
- [495. Teemo Attacking](https://leetcode.com/problems/teemo-attacking/)
- [252. Meeting Rooms](https://leetcode.com/problems/meeting-rooms/)
- [253. Meeting Rooms II](https://leetcode.com/problems/meeting-rooms-ii/)
- [435. Non-overlapping Intervals](https://leetcode.com/problems/non-overlapping-intervals/)

## Interview Tips:
- Draw timeline to visualize overlaps
- Handle three phases systematically
- Consider edge cases with empty arrays
- This pattern appears in many interval problems 