# Maximum Subarray Problem (Kadane's Algorithm)

## Problem Statement

Given an integer array, find the contiguous subarray (containing at least one number) which has the largest sum and return its sum.

**Key Points:**
- Must be a contiguous subarray (elements next to each other)
- At least one element must be included
- Return the sum, not the subarray itself (unless specifically asked)

## Example
```
Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
Output: 6
Explanation: [4,-1,2,1] has the largest sum = 6
```

## Approach 1: Brute Force (Understanding the Problem)

### How it works:
Check every possible contiguous subarray and find the one with maximum sum.

1. Try all starting positions (i)
2. For each start, try all ending positions (j >= i)  
3. Calculate sum of subarray from i to j
4. Keep track of maximum sum seen

### Code Logic:
```java
for (int i = 0; i < n; i++) {
    for (int j = i; j < n; j++) {
        int sum = 0;
        for (int k = i; k <= j; k++) {
            sum += nums[k];
        }
        maxSum = Math.max(maxSum, sum);
    }
}
```

### Complexity:
- **Time:** O(n³) - Three nested loops
- **Space:** O(1)

### When to use:
- Only for tiny arrays (< 10 elements)
- To demonstrate problem understanding

## Approach 2: Optimized Brute Force

### How it works:
Instead of recalculating sums, extend the current subarray.

### Code Logic:
```java
for (int i = 0; i < n; i++) {
    int currentSum = 0;
    for (int j = i; j < n; j++) {
        currentSum += nums[j];  // Extend subarray
        maxSum = Math.max(maxSum, currentSum);
    }
}
```

### Complexity:
- **Time:** O(n²)
- **Space:** O(1)

## Approach 3: Kadane's Algorithm (Optimal!)

### The Big Idea (Simple Explanation):
Imagine you're walking through a path where each step gives you money (positive) or takes money away (negative). At each step, you have a choice:
1. Continue with your current money total
2. Start fresh from this step (if your current total is negative)

**Key Insight:** If your current sum becomes negative, it's better to start fresh from the next element rather than carrying the negative baggage.

### The Algorithm:
```java
int maxSum = nums[0];      // Best sum found so far  
int currentSum = nums[0];  // Best sum ending at current position

for (int i = 1; i < nums.length; i++) {
    // Key decision: extend or start new?
    currentSum = Math.max(nums[i], currentSum + nums[i]);
    maxSum = Math.max(maxSum, currentSum);
}
```

### Step-by-step Walkthrough:
```
nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]

i=0: currentSum = -2, maxSum = -2
i=1: currentSum = max(1, -2+1) = 1, maxSum = 1
i=2: currentSum = max(-3, 1-3) = -2, maxSum = 1  
i=3: currentSum = max(4, -2+4) = 4, maxSum = 4
i=4: currentSum = max(-1, 4-1) = 3, maxSum = 4
i=5: currentSum = max(2, 3+2) = 5, maxSum = 5
i=6: currentSum = max(1, 5+1) = 6, maxSum = 6
i=7: currentSum = max(-5, 6-5) = 1, maxSum = 6
i=8: currentSum = max(4, 1+4) = 5, maxSum = 6

Result: 6
```

### Complexity:
- **Time:** O(n) - Single pass through array
- **Space:** O(1) - Only using two variables

### When to use:
- **This is the standard interview solution**
- Any array size
- When you need optimal performance

## Alternative Thinking Patterns

### Pattern 1: Dynamic Programming Perspective
```
dp[i] = maximum sum of subarray ending at index i
dp[i] = max(nums[i], dp[i-1] + nums[i])
```

### Pattern 2: Two Variables Method
```
maxEndingHere = maximum sum ending at current position
maxSoFar = global maximum sum
```

### Pattern 3: Reset Strategy
```
Keep adding elements to current sum
If current sum becomes negative, reset to 0
```

## Approach 4: Divide and Conquer

### How it works:
1. Divide array into two halves
2. Find max subarray in left half
3. Find max subarray in right half
4. Find max subarray crossing the middle
5. Return maximum of the three

### Complexity:
- **Time:** O(n log n)
- **Space:** O(log n) for recursion stack

### When to use:
- Academic interest
- When interviewer specifically asks for divide and conquer
- Not optimal for this problem

## Edge Cases & Variations

### Case 1: All Negative Numbers
```
Input: [-5, -2, -8, -1]
Standard Kadane's: -1 (least negative)
Some variations: 0 (empty subarray allowed)
```

### Case 2: All Positive Numbers
```
Input: [1, 2, 3, 4, 5]
Output: 15 (entire array)
```

### Case 3: Mixed with Zeros
```
Input: [-1, 0, -2, 3]
Output: 3
```

### Case 4: Single Element
```
Input: [-1] or [5]
Output: -1 or 5 (the element itself)
```

## Common Interview Variations

### Variation 1: Return the Subarray (not just sum)
```java
// Track start and end indices during Kadane's
int start = 0, end = 0, tempStart = 0;
// Update indices when we find new maximum
```

### Variation 2: Maximum Product Subarray
Different algorithm needed due to negative numbers.

### Variation 3: Circular Array
Need to consider wrap-around cases.

### Variation 4: At Most K Elements
Add constraint on subarray length.

## Interview Strategy

### Step-by-step Approach:
1. **Clarify requirements:** "Should I return sum or the actual subarray?"
2. **Start with brute force:** "The naive approach would be O(n³)..."
3. **Optimize:** "I can optimize this to O(n²) by avoiding recalculation..."
4. **Introduce Kadane's:** "The optimal solution uses Kadane's algorithm in O(n)..."
5. **Explain the intuition:** "The key insight is at each position, we decide whether to extend or restart..."
6. **Handle edge cases:** "What if all numbers are negative?"

### What Interviewers Look For:
- Understanding of the core insight
- Ability to code Kadane's algorithm correctly
- Handling of edge cases
- Clear explanation of the approach

## Common Coding Mistakes

1. **Wrong initialization:** `maxSum = 0` instead of `nums[0]`
2. **Missing edge case:** Not handling single element arrays
3. **Logic error:** `currentSum = max(nums[i], currentSum) + nums[i]` (wrong!)
4. **Index confusion:** When tracking subarray indices

## Real-world Applications

1. **Stock Trading:** Maximum profit from buying/selling stocks
2. **Resource Management:** Finding periods of maximum resource utilization
3. **Signal Processing:** Finding strongest signal periods
4. **Performance Analysis:** Finding best performing time periods
5. **Financial Analysis:** Maximum cumulative profit periods

## Advanced Optimizations

### Space-Optimized DP:
Instead of storing entire DP array, use just two variables.

### Early Termination:
If we find a very large positive sum, we might terminate early in some scenarios.

### Parallel Processing:
Divide and conquer approach can be parallelized.

## Note

**For Mid-Level Interviews (2+ years):**
- **Master Kadane's Algorithm:** Be able to code it perfectly in 2-3 minutes
- **Explain the intuition clearly:** Focus on the "extend vs restart" decision
- **Know the complexity:** O(n) time, O(1) space
- **Handle variations:** Be ready for follow-up questions about modifications
- **Edge cases:** Always consider all-negative arrays

**Interview Red Flags to Avoid:**
- Not knowing Kadane's algorithm by name
- Unable to explain why it works
- Coding it incorrectly
- Not handling negative numbers properly
- Suggesting brute force as the final solution

## LeetCode Similar Problems:
- [152. Maximum Product Subarray](https://leetcode.com/problems/maximum-product-subarray/)
- [209. Minimum Size Subarray Sum](https://leetcode.com/problems/minimum-size-subarray-sum/)
- [325. Maximum Size Subarray Sum Equals k](https://leetcode.com/problems/maximum-size-subarray-sum-equals-k/)
- [560. Subarray Sum Equals K](https://leetcode.com/problems/subarray-sum-equals-k/)
- [713. Subarray Product Less Than K](https://leetcode.com/problems/subarray-product-less-than-k/)

**Golden Rule:** This problem is a classic example of how a simple greedy choice (extend vs restart) can lead to an optimal solution. The pattern appears in many other problems!

**Remember:** Kadane's algorithm is not just about this problem—it's a fundamental technique used in many DP and array problems. Master it thoroughly! 