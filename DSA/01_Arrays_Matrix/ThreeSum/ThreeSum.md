# Three Sum Problem

## Problem Statement

Given an integer array, find all unique triplets in the array that sum to zero. The triplets must not contain duplicate combinations.

**Key Requirements:**
- Find all triplets [a, b, c] where a + b + c = 0
- No duplicate triplets in the result
- Cannot use the same element twice
- Return the actual values, not indices

## Example
```
Input: nums = [-1,0,1,2,-1,-4]
Output: [[-1,-1,2],[-1,0,1]]
Explanation: 
- nums[0] + nums[1] + nums[2] = (-1) + 0 + 1 = 0
- nums[1] + nums[2] + nums[4] = 0 + 1 + (-1) = 0
- nums[0] + nums[3] + nums[4] = (-1) + 2 + (-1) = 0
```

## Approach 1: Brute Force (Basic Understanding)

### How it works:
Think of trying every possible combination of 3 friends and checking if their ages sum to 0. Very inefficient but easy to understand.

1. Try every possible triplet (i, j, k)
2. Check if nums[i] + nums[j] + nums[k] = 0
3. Use a Set to avoid duplicate triplets

### Code Logic:
```java
for (int i = 0; i < n - 2; i++) {
    for (int j = i + 1; j < n - 1; j++) {
        for (int k = j + 1; k < n; k++) {
            if (nums[i] + nums[j] + nums[k] == 0) {
                // Found a valid triplet
            }
        }
    }
}
```

### Complexity:
- **Time:** O(n³) - Three nested loops
- **Space:** O(k) where k is the number of triplets

### When to use:
- Only for very small arrays (< 20 elements)
- To demonstrate understanding of the problem
- Never use this in interviews for the final solution

## Approach 2: Sort + Two Pointers (Optimal Solution!)

### How it works (Simple explanation):
Imagine you're standing in a line of people sorted by height. You pick one person (let's call them the "anchor"), then you have two other people walk toward each other from opposite ends of the remaining line. If their combined height with the anchor is too much, the taller person steps back. If too little, the shorter person steps forward.

### The Strategy:
1. **Sort the array** first (crucial for avoiding duplicates and using two pointers)
2. **Fix one element** as the "anchor" 
3. **Use two pointers** on the remaining array to find pairs that sum to -anchor
4. **Skip duplicates** carefully to avoid duplicate triplets

### Detailed Walkthrough:
```
nums = [-1,0,1,2,-1,-4]
After sorting: [-4,-1,-1,0,1,2]

i=0: anchor = -4, target = 4
     left=1(-1), right=5(2): sum = 1 < 4 → left++
     left=2(-1), right=5(2): sum = 1 < 4 → left++
     left=3(0), right=5(2): sum = 2 < 4 → left++
     left=4(1), right=5(2): sum = 3 < 4 → left++
     left >= right, continue

i=1: anchor = -1, target = 1
     left=2(-1), right=5(2): sum = 1 = 1 ✓
     Found: [-1,-1,2]
     Skip duplicates, move pointers
     
i=2: anchor = -1 (duplicate), skip

i=3: anchor = 0, target = 0
     left=4(1), right=5(2): sum = 3 > 0 → right--
     left=4(1), right=4(1): left >= right, continue
```

### Code Logic:
```java
Arrays.sort(nums);
for (int i = 0; i < nums.length - 2; i++) {
    if (i > 0 && nums[i] == nums[i-1]) continue; // Skip duplicates
    
    int left = i + 1, right = nums.length - 1;
    while (left < right) {
        int sum = nums[i] + nums[left] + nums[right];
        if (sum == 0) {
            // Found triplet, add to result
            // Skip duplicates for both pointers
        } else if (sum < 0) {
            left++;
        } else {
            right--;
        }
    }
}
```

### Complexity:
- **Time:** O(n²) - One loop + two pointers for each iteration
- **Space:** O(1) for the algorithm (excluding result storage)

### When to use:
- **This is the standard interview solution**
- Large arrays
- When you need optimal time complexity

## Approach 3: HashMap Alternative

### How it works:
For each pair (i,j), check if the complement -(nums[i] + nums[j]) exists in the remaining array.

### Complexity:
- **Time:** O(n²)
- **Space:** O(n) for the HashMap

### When to use:
- When interviewer asks for alternative approaches
- Less preferred due to higher space complexity

## Advanced Optimizations

### Early Termination Tricks:
1. **Smallest sum check:** If nums[i] + nums[i+1] + nums[i+2] > 0, break
2. **Largest sum check:** If nums[i] + nums[n-2] + nums[n-1] < 0, continue

These optimizations can significantly improve average-case performance.

## Handling Duplicates (Critical!)

The trickiest part of this problem is avoiding duplicate triplets:

### Method 1: Using Set (Brute Force)
```java
Set<List<Integer>> resultSet = new HashSet<>();
// Add triplets to set (automatically handles duplicates)
```

### Method 2: Skip Duplicates During Generation (Optimal)
```java
// Skip duplicate anchors
if (i > 0 && nums[i] == nums[i-1]) continue;

// Skip duplicate left pointers
while (left < right && nums[left] == nums[left+1]) left++;

// Skip duplicate right pointers  
while (left < right && nums[right] == nums[right-1]) right--;
```

## Interview Strategy

### What to say step by step:
1. **"Let me understand: we need all unique triplets that sum to zero"**
2. **"The brute force would be O(n³) checking every triplet"**
3. **"I can optimize this to O(n²) using sort + two pointers"**
4. **"The key insight is: fix one element, then it becomes a Two Sum problem"**
5. **"Sorting helps with both duplicate handling and two pointers technique"**

### Implementation Order:
1. Sort the array
2. Implement the main loop with anchor
3. Implement two pointers logic
4. Handle duplicates carefully
5. Test with edge cases

## Edge Cases to Master

1. **All zeros:** `[0,0,0]` → Should return `[[0,0,0]]`
2. **No solution:** `[1,2,3]` → Should return `[]`
3. **Duplicate elements:** `[-1,0,1,2,-1,-4]` → Handle carefully
4. **Small arrays:** `[1,2]` → Not enough elements
5. **All same negative:** `[-1,-1,-1]` → No solution
6. **Two elements sum to zero:** `[-1,0,1]` → Should return `[[-1,0,1]]`

## Common Mistakes to Avoid

1. **Forgetting to sort the array** → Two pointers won't work correctly
2. **Not handling duplicates** → Will have duplicate triplets in result
3. **Wrong loop boundaries** → Array index out of bounds
4. **Incorrect duplicate skipping** → Missing valid triplets or including duplicates
5. **Not considering edge cases** → Solution fails on corner cases

## Follow-up Questions & Answers

**Q: What if we want the closest sum to target (not necessarily 0)?**
A: Modify the condition to track the closest sum seen so far.

**Q: What if we want 4Sum instead of 3Sum?**
A: Add another outer loop, making it O(n³).

**Q: Can we do better than O(n²)?**
A: No, this is optimal for the general case.

**Q: What if the array is very large and doesn't fit in memory?**
A: Use external sorting and process in chunks.

## Real-world Applications

1. **Financial Analysis:** Finding transactions that balance accounts
2. **Chemistry:** Finding molecular combinations with neutral charge
3. **Resource Allocation:** Balancing positive and negative contributions
4. **Game Development:** Balancing player teams or abilities

## Note

**For your level (2+ years experience):**
- Master the sort + two pointers approach
- Be able to code it quickly without bugs
- Understand the duplicate handling thoroughly
- Know the time/space complexity analysis
- Practice explaining the intuition clearly

**Key Interview Points:**
- Always mention sorting is O(n log n) but overall is still O(n²)
- Explain why two pointers works only on sorted arrays
- Demonstrate understanding of duplicate handling strategies
- Show you can optimize with early termination

## LeetCode Similar Problems:
- [1. Two Sum](https://leetcode.com/problems/two-sum/)
- [18. 4Sum](https://leetcode.com/problems/4sum/)
- [454. 4Sum II](https://leetcode.com/problems/4sum-ii/)
- [259. 3Sum Smaller](https://leetcode.com/problems/3sum-smaller/)
- [16. 3Sum Closest](https://leetcode.com/problems/3sum-closest/)

**Remember:** This problem is a stepping stone to 4Sum, and the two pointers technique is used in many other array problems! 