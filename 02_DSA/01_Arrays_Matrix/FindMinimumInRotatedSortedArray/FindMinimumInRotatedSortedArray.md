# Find Minimum in Rotated Sorted Array

## Problem Statement

Suppose an array of length n sorted in ascending order is rotated between 1 and n times. For example, the array `nums = [0,1,2,4,5,6,7]` might become:
- `[4,5,6,7,0,1,2]` if it was rotated 4 times
- `[0,1,2,4,5,6,7]` if it was rotated 7 times

Given the sorted rotated array `nums` of **unique elements**, return the **minimum element** of this array.

**Important constraints:**
- All elements are unique
- You must write an algorithm that runs in **O(log n)** time
- The array is guaranteed to be rotated at least once

## Example
```
Input: nums = [3,4,5,1,2]
Output: 1
Explanation: The original array was [1,2,3,4,5] rotated 3 times.
```

## Understanding the Problem

### Key Insights:
1. **Rotation Point**: The minimum element is always at the "rotation point" - where the array was split and rotated
2. **Two Sorted Portions**: A rotated array has two sorted subarrays
3. **Binary Search**: We can eliminate half the search space in each iteration

### Visual Example:
```
Original: [1, 2, 3, 4, 5, 6, 7]
Rotated:  [4, 5, 6, 7, 1, 2, 3]
           ↑        ↑  ↑
           |        |  └── Minimum (rotation point)
           |        └── Last of first sorted portion
           └── Start of first sorted portion
```

## Approach 1: Linear Search (Brute Force)

### How it works:
Simply scan the entire array to find the minimum element.

### Code Logic:
```java
int min = nums[0];
for (int i = 1; i < nums.length; i++) {
    min = Math.min(min, nums[i]);
}
return min;
```

### Time & Space Complexity:
- **Time:** O(n) - We check every element
- **Space:** O(1) - Only use constant extra space

### When to use:
- Very small arrays (< 10 elements)
- When you need a simple, guaranteed solution
- Not acceptable for interview since it doesn't meet O(log n) requirement

## Approach 2: Binary Search (Optimal) ⭐

### How it works (Simple explanation):
Think of the rotated array as two mountains connected together. The valley (minimum) is where one mountain ends and the other begins. We use binary search to quickly find this valley.

### Key Insight:
In a rotated sorted array:
- If `nums[mid] > nums[right]`: The minimum is in the right half
- If `nums[mid] ≤ nums[right]`: The minimum is in the left half (including mid)

### Example Walkthrough:
```
nums = [4, 5, 6, 7, 0, 1, 2]
       left=0, right=6

Step 1: mid=3, nums[3]=7, nums[6]=2
        7 > 2, so minimum is in right half
        left=4, right=6

Step 2: mid=5, nums[5]=1, nums[6]=2  
        1 ≤ 2, so minimum is in left half (including mid)
        left=4, right=5

Step 3: mid=4, nums[4]=0, nums[5]=1
        0 ≤ 1, so minimum is in left half (including mid)
        left=4, right=4

Found: nums[4] = 0
```

### Code Logic:
```java
int left = 0, right = nums.length - 1;

while (left < right) {
    int mid = left + (right - left) / 2;
    
    if (nums[mid] > nums[right]) {
        left = mid + 1;  // Search right half
    } else {
        right = mid;     // Search left half (including mid)
    }
}

return nums[left];
```

### Time & Space Complexity:
- **Time:** O(log n) - We eliminate half the search space each iteration
- **Space:** O(1) - Only use constant extra space

### When to use:
- **This is the preferred interview solution**
- Large arrays where O(log n) is required
- When you need optimal time complexity

## Approach 3: Binary Search (Alternative Implementation)

### How it works:
Similar to Approach 2 but compares with the leftmost element instead of rightmost.

### Key Differences:
- More complex logic due to handling edge cases
- Requires additional checks for non-rotated arrays
- Same time complexity but slightly more code

### When to use:
- When you want to show alternative thinking
- As a follow-up if interviewer asks for different implementation

## Approach 4: Find Rotation Point (Educational)

### How it works:
Explicitly finds the exact point where rotation occurred by looking for the "break" in the sorted order.

### Key Logic:
- Look for `nums[i] > nums[i+1]` (rotation point)
- The element after this break is the minimum

### When to use:
- To demonstrate understanding of the rotation concept
- When interviewer asks about finding the rotation index
- Educational purposes to explain the problem structure

## Interview Tips

### What to say in an interview:
1. **Clarify the problem:** "So we need to find the minimum in O(log n) time..."
2. **Identify the pattern:** "I notice this is a modified binary search problem..."
3. **Explain the insight:** "The key is that one half will always contain the minimum..."
4. **Code the solution:** Start with the optimal binary search approach

### Common Follow-up Questions:
1. **"What if there are duplicates?"** → More complex logic needed (see Find Minimum in Rotated Sorted Array II)
2. **"Can you find the rotation index?"** → Modify to return the index instead of value
3. **"What if the array is not rotated?"** → Handle early termination case
4. **"Can you do it recursively?"** → Convert to recursive binary search

## Edge Cases to Consider

1. **Single element:** `nums = [1]` → Return the only element
2. **Two elements:** `nums = [2, 1]` → Handle carefully with binary search
3. **Not rotated:** `nums = [1, 2, 3, 4, 5]` → Return first element
4. **Fully rotated:** `nums = [1, 2, 3, 4, 5]` → Same as not rotated
5. **Large rotation:** `nums = [5, 1, 2, 3, 4]` → Find the break point

## Common Mistakes to Avoid

### Binary Search Pitfalls:
1. **Infinite loops:** Always ensure `left` and `right` converge
2. **Wrong comparison:** Compare with consistent reference (left vs right)
3. **Off-by-one errors:** Be careful with `mid + 1` vs `mid`
4. **Integer overflow:** Use `left + (right - left) / 2` instead of `(left + right) / 2`

### Logic Errors:
1. **Forgetting edge cases:** Handle single element and non-rotated arrays
2. **Wrong half selection:** Double-check which half contains the minimum
3. **Boundary conditions:** Ensure correct handling when `left == right`

## Optimization Notes

### Performance Considerations:
- Early termination for non-rotated arrays saves time
- Avoid unnecessary comparisons in edge cases
- Use bitwise operations for division by 2 if needed: `mid = left + ((right - left) >> 1)`

### Memory Efficiency:
- All approaches use O(1) space
- No additional data structures needed
- In-place algorithm

## Real-world Applications

1. **Database indexing:** Finding minimum values in rotated index structures
2. **Circular arrays:** Working with circular buffers or queues
3. **Time series data:** Finding minimum values in cyclical data
4. **System scheduling:** Round-robin scheduling with priority finding

## Related Problems

1. **Search in Rotated Sorted Array:** Find a target value instead of minimum
2. **Find Minimum in Rotated Sorted Array II:** Handle duplicate elements
3. **Find Peak Element:** Similar binary search pattern
4. **Search a 2D Matrix:** Extended binary search concepts

## Note

**For mid-level interviews (2+ years experience):**
- Start directly with the optimal binary search solution
- Explain the O(log n) requirement and why linear search won't work
- Demonstrate understanding of rotation point concept
- Handle edge cases proactively
- Be prepared to code it quickly without bugs

**Remember:** This problem tests your ability to apply binary search to a modified sorted array. The key insight is recognizing that despite rotation, we can still eliminate half the search space in each iteration! 