# Longest Increasing Subsequence (LIS)

## Problem Statement
Given an integer array `nums`, return the length of the **longest strictly increasing subsequence**.

A **subsequence** is a sequence that can be derived from the array by deleting some or no elements without changing the order of the remaining elements.

**Example:**
```
Input: nums = [10,9,2,5,3,7,101,18]
Output: 4
Explanation: The longest increasing subsequence is [2,3,7,18], length = 4.
```

## Solution Approaches

### 1. Dynamic Programming Approach
**Time Complexity:** O(n²)  
**Space Complexity:** O(n)

```java
public int lengthOfLISDP(int[] nums) {
    int[] dp = new int[nums.length];
    Arrays.fill(dp, 1);
    
    for (int i = 1; i < nums.length; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[i] > nums[j]) {
                dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }
    }
    
    return Arrays.stream(dp).max().orElse(0);
}
```

**Key Insight:** `dp[i]` represents the length of LIS ending at index `i`.

### 2. Binary Search + Greedy (Optimal)
**Time Complexity:** O(n log n)  
**Space Complexity:** O(n)

```java
public int lengthOfLISOptimal(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    
    for (int num : nums) {
        int pos = Collections.binarySearch(tails, num);
        if (pos < 0) pos = -(pos + 1);
        
        if (pos == tails.size()) {
            tails.add(num);
        } else {
            tails.set(pos, num);
        }
    }
    
    return tails.size();
}
```

**Key Insight:** `tails[i]` stores the smallest ending element of all increasing subsequences of length `i+1`.

## Algorithm Explanation

### DP Approach:
1. **State Definition:** `dp[i]` = length of LIS ending at index `i`
2. **Transition:** For each `i`, check all previous elements `j < i`
   - If `nums[i] > nums[j]`, then `dp[i] = max(dp[i], dp[j] + 1)`
3. **Result:** Maximum value in `dp` array

### Optimal Approach:
1. **Maintain `tails` array:** `tails[i]` = smallest tail element for LIS of length `i+1`
2. **For each element:**
   - Binary search for position in `tails`
   - If position is at end, extend the sequence
   - Otherwise, replace element to keep smallest possible tail
3. **Result:** Length of `tails` array

## Why the Optimal Approach Works

The key insight is **maintaining the smallest possible tail for each length**:

- If we have multiple increasing subsequences of the same length, we only care about the one with the smallest ending element
- This gives us the best chance to extend the subsequence further
- Binary search helps us find the correct position efficiently

### Example Walkthrough:
Array: `[10, 9, 2, 5, 3, 7, 101, 18]`

| Step | Element | Action | tails array |
|------|---------|--------|-------------|
| 1 | 10 | Add | [10] |
| 2 | 9 | Replace 10 | [9] |
| 3 | 2 | Replace 9 | [2] |
| 4 | 5 | Add | [2, 5] |
| 5 | 3 | Replace 5 | [2, 3] |
| 6 | 7 | Add | [2, 3, 7] |
| 7 | 101 | Add | [2, 3, 7, 101] |
| 8 | 18 | Replace 101 | [2, 3, 7, 18] |

Final length: 4

## Variations and Extensions

### 1. Reconstruct Actual LIS
Keep parent pointers to backtrack and build the actual subsequence.

### 2. Count Number of LIS
Track count of ways to form LIS of each length.

### 3. Longest Non-Decreasing Subsequence
Allow equal elements: change `>` to `>=` in comparison.

### 4. Longest Decreasing Subsequence
Change condition to find decreasing subsequence.

### 5. LIS with Specific Constraints
- Maximum difference between consecutive elements
- LIS ending at specific position

## Common Mistakes

1. **Confusing subsequence with subarray**
   - Subsequence: Elements don't need to be contiguous
   - Subarray: Elements must be contiguous

2. **Off-by-one errors in binary search**
   - Ensure correct bounds and position calculation

3. **Not handling edge cases**
   - Empty array, single element array

4. **Incorrect reconstruction logic**
   - Parent tracking must be consistent with DP state

## Applications

1. **Box Stacking Problem**
2. **Russian Doll Envelopes**
3. **Activity Selection with Dependencies**
4. **Version Control Systems**
5. **Scheduling Problems**

## Time Complexity Comparison

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Brute Force | O(2ⁿ) | O(n) | Generate all subsequences |
| DP | O(n²) | O(n) | Classic dynamic programming |
| Binary Search | O(n log n) | O(n) | Optimal solution |

## Related Problems

- **Longest Common Subsequence (LCS)**
- **Longest Bitonic Subsequence**
- **Maximum Length of Pair Chain**
- **Russian Doll Envelopes**
- **Minimum Number of Taps to Open**

## Key Insights

1. **Greedy Choice:** Always keep smallest possible tail for each length
2. **Binary Search:** Efficiently find insertion/replacement position
3. **State Optimization:** Don't need to store all possible subsequences
4. **Reconstruction:** Requires additional bookkeeping but doesn't change complexity

## Practice Tips

1. Start with DP approach to understand the problem
2. Learn the binary search optimization
3. Practice reconstruction variants
4. Solve related problems to reinforce patterns
5. Understand why greedy choice works in this context 