# Longest Substring with K Distinct Characters

## Problem Statement
Given a string `s` and an integer `k`, return the length of the longest substring of `s` that contains at most `k` distinct characters.

**Example 1:**
```
Input: s = "eceba", k = 2
Output: 3
Explanation: The substring is "ece" with length 3.
```

**Example 2:**
```
Input: s = "aa", k = 1
Output: 2
Explanation: The substring is "aa" with length 2.
```

## Solution Approach

### Algorithm: Sliding Window
The key insight is to use a sliding window approach with two pointers:

1. **Expand**: Move the right pointer to include more characters
2. **Contract**: When we have more than k distinct characters, move the left pointer
3. **Track**: Keep track of character frequencies and the maximum window size

### Step-by-Step Process:
1. Use a HashMap to count character frequencies in the current window
2. Expand the window by moving the right pointer
3. If distinct characters exceed k, shrink from left until we have ≤ k distinct characters
4. Update the maximum length found so far

### Time & Space Complexity:
- **Time Complexity**: O(n) - Each character is visited at most twice
- **Space Complexity**: O(k) - HashMap stores at most k distinct characters

## Key Insights:
- **Sliding Window Pattern**: Classic two-pointer technique
- **Character Frequency Tracking**: Use HashMap or array for frequency counting
- **Window Validation**: Maintain exactly k or fewer distinct characters
- **Optimization**: For ASCII characters, use array instead of HashMap

## Edge Cases:
- Empty string or k = 0
- String length less than k
- All characters are the same
- k is greater than number of distinct characters

## Common Mistakes:
- Forgetting to remove characters with zero frequency from HashMap
- Not handling the case when k = 0
- Incorrect window size calculation

## Related Problems:
- Longest Substring Without Repeating Characters
- Minimum Window Substring
- Sliding Window Maximum 