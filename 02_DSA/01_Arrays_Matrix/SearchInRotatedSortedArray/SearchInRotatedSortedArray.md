# Search in Rotated Sorted Array

## Problem Statement

There is an integer array `nums` sorted in ascending order (with **distinct values**). Prior to being passed to your function, `nums` is **possibly rotated** at an unknown pivot index `k` such that the resulting array is `[nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]]`.

For example, `[0,1,2,4,5,6,7]` might be rotated at pivot index 3 and become `[4,5,6,7,0,1,2]`.

Given the array `nums` after the possible rotation and an integer `target`, return the **index** of `target` if it is in `nums`, or **-1** if it is not in `nums`.

**Important constraints:**
- All values are distinct
- You must write an algorithm with **O(log n)** runtime complexity
- Array may or may not be rotated

## Example
```
Input: nums = [4,5,6,7,0,1,2], target = 0
Output: 4

Input: nums = [4,5,6,7,0,1,2], target = 3
Output: -1
```

## Understanding the Problem

### Key Insights:
1. **Partial Sorting**: Although rotated, the array still has two sorted subarrays
2. **Binary Search Applicable**: We can still eliminate half the search space each time
3. **Sorted Half Detection**: At least one half (left or right) is always completely sorted

### Visual Example:
```
Original: [0, 1, 2, 4, 5, 6, 7]
Rotated:  [4, 5, 6, 7, 0, 1, 2]
           ↑---------↑  ↑-----↑
           Sorted     Sorted
           portion    portion
```

## Approach 1: Linear Search (Brute Force)

### How it works:
Simply scan the entire array to find the target element.

### Code Logic:
```java
for (int i = 0; i < nums.length; i++) {
    if (nums[i] == target) {
        return i;
    }
}
return -1;
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
Even though the array is rotated, **at least one half is always completely sorted**. We can:
1. Find the middle element
2. Determine which half is sorted
3. Check if our target lies in the sorted half
4. Search accordingly

### Key Insight:
```
In a rotated array [4,5,6,7,0,1,2]:
- If we're at mid=3 (value=7), left half [4,5,6,7] is sorted
- If we're at mid=5 (value=1), right half [1,2] is sorted
```

### Example Walkthrough:
```
nums = [4,5,6,7,0,1,2], target = 0
       left=0, right=6

Step 1: mid=3, nums[3]=7
        nums[0]=4 ≤ nums[3]=7, so left half [4,5,6,7] is sorted
        target=0 not in [4,7], so search right half
        left=4, right=6

Step 2: mid=5, nums[5]=1  
        nums[4]=0 > nums[5]=1, so right half [1,2] is sorted
        target=0 not in [1,2], so search left half
        left=4, right=4

Step 3: mid=4, nums[4]=0
        Found target! Return 4
```

### Code Logic:
```java
while (left <= right) {
    int mid = left + (right - left) / 2;
    
    if (nums[mid] == target) return mid;
    
    if (nums[left] <= nums[mid]) {
        // Left half is sorted
        if (target >= nums[left] && target < nums[mid]) {
            right = mid - 1;  // Search left
        } else {
            left = mid + 1;   // Search right
        }
    } else {
        // Right half is sorted
        if (target > nums[mid] && target <= nums[right]) {
            left = mid + 1;   // Search right
        } else {
            right = mid - 1;  // Search left
        }
    }
}
```

### Time & Space Complexity:
- **Time:** O(log n) - We eliminate half the search space each iteration
- **Space:** O(1) - Only use constant extra space

### When to use:
- **This is the preferred interview solution**
- Large arrays where O(log n) is required
- When you need optimal time complexity

## Approach 3: Find Pivot Then Binary Search

### How it works:
A two-step approach:
1. First, find the rotation point (pivot) using binary search
2. Then, determine which sorted portion contains the target
3. Finally, perform standard binary search on that portion

### Advantages:
- Separates concerns clearly
- Easy to understand and debug
- Reuses standard binary search

### Disadvantages:
- Requires two passes through the algorithm
- Slightly more complex implementation

### When to use:
- When you want to break down the problem into simpler steps
- For educational purposes to understand the rotation concept
- When interviewer asks about finding the rotation point

## Approach 4: Recursive Binary Search

### How it works:
Same logic as Approach 2 but implemented recursively instead of iteratively.

### Trade-offs:
- **Pros:** More elegant and easier to understand recursion flow
- **Cons:** Uses O(log n) space due to recursion stack

### When to use:
- When interviewer specifically asks for recursive solution
- If you're more comfortable with recursive thinking
- For problems where recursion leads to cleaner code

## Interview Tips

### What to say in an interview:
1. **Understand the constraint:** "I need O(log n), so this suggests binary search..."
2. **Identify the key insight:** "Even though it's rotated, one half is always sorted..."
3. **Explain the logic:** "I'll check which half is sorted and determine where the target could be..."
4. **Handle edge cases:** "Let me consider single elements and non-rotated arrays..."

### Common Follow-up Questions:
1. **"What if there are duplicates?"** → This becomes much more complex (Search in Rotated Sorted Array II)
2. **"Can you find all occurrences?"** → Modify to continue searching after finding first occurrence
3. **"What if you need to find insertion point?"** → Modify to handle target not found case
4. **"Can you do it recursively?"** → Show Approach 4

## Edge Cases to Consider

1. **Empty array:** `nums = []` → Return -1
2. **Single element:** `nums = [1], target = 1` → Return 0 or -1
3. **No rotation:** `nums = [1,2,3,4,5], target = 3` → Works like standard binary search
4. **Target at rotation point:** `nums = [4,5,6,7,0,1,2], target = 0` → Handle carefully
5. **Target at boundaries:** First or last element of array
6. **All elements same (if duplicates allowed):** Would require different approach

## Common Mistakes to Avoid

### Binary Search Pitfalls:
1. **Wrong boundary conditions:** Using `<` vs `<=` in while loop
2. **Infinite loops:** Ensure left and right pointers always progress
3. **Off-by-one errors:** Be careful with `mid + 1` vs `mid - 1`
4. **Integer overflow:** Use `left + (right - left) / 2` instead of `(left + right) / 2`

### Logic Errors:
1. **Wrong half detection:** Incorrectly determining which half is sorted
2. **Target range checking:** Wrong boundary comparisons for target location
3. **Edge case handling:** Not handling single element or non-rotated arrays

### Interview-Specific Mistakes:
1. **Starting with linear search:** Jump directly to binary search approach
2. **Not explaining the insight:** Always explain why one half is sorted
3. **Forgetting edge cases:** Mention and handle them proactively

## Optimization Notes

### Performance Considerations:
- Early termination for non-rotated arrays
- Avoid unnecessary comparisons
- Handle single element arrays quickly

### Code Clarity:
- Use meaningful variable names (`left`, `right`, `mid`)
- Add comments for the sorted half logic
- Separate concerns if using the pivot-finding approach

## Real-world Applications

1. **Database indexing:** Searching in rotated index structures
2. **Circular buffers:** Finding elements in circular data structures
3. **Time-based data:** Searching in cyclical datasets (like hourly data)
4. **Distributed systems:** Searching in consistent hash rings
5. **Graphics programming:** Searching in rotated coordinate systems

## Related Problems

1. **Find Minimum in Rotated Sorted Array:** Find minimum instead of target
2. **Search in Rotated Sorted Array II:** Handle duplicate elements
3. **Find Peak Element:** Similar binary search pattern
4. **Search a 2D Matrix:** Extended search in matrix

## Complexity Analysis Comparison

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| Linear Search | O(n) | O(1) | Small arrays only |
| Binary Search | O(log n) | O(1) | **Interview favorite** |
| Find Pivot | O(log n) | O(1) | Educational purposes |
| Recursive | O(log n) | O(log n) | Recursive preference |

## Note

**For mid-level interviews (2+ years experience):**
- Start directly with the binary search approach
- Clearly explain the "one half is always sorted" insight
- Handle edge cases proactively
- Code it efficiently without bugs
- Be prepared to trace through an example step by step

**Remember:** This problem tests your ability to adapt binary search to a modified array structure. The key insight is recognizing that rotation doesn't break the fundamental property that allows binary search to work - you can still eliminate half the search space in each iteration! 