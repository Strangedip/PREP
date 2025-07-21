# Longest Palindromic Substring

## Problem Statement
Given a string, find the longest palindromic substring. A palindrome reads the same forward and backward.

## Example
```
Input: s = "babad"
Output: "bab" (or "aba")

Input: s = "cbbd"
Output: "bb"
```

## Approach 1: Expand Around Centers (Optimal for Interview)

### How it works:
1. **Try every possible center** (character or between characters)
2. **Expand outward** while characters match
3. **Track longest palindrome** found

### Key Logic:
```java
for (int i = 0; i < s.length(); i++) {
    // Odd length palindromes (center at i)
    int len1 = expandAroundCenter(s, i, i);
    
    // Even length palindromes (center between i and i+1)
    int len2 = expandAroundCenter(s, i, i + 1);
    
    int maxLen = Math.max(len1, len2);
    if (maxLen > maxLength) {
        start = i - (maxLen - 1) / 2;
        maxLength = maxLen;
    }
}
```

### Time & Space Complexity:
- **Time:** O(n²) - For each center, expand up to n times
- **Space:** O(1) - Only using a few variables

## Approach 2: Dynamic Programming

### How it works:
1. **dp[i][j] = true** if substring from i to j is palindrome
2. **Fill table** for all substrings
3. **Base cases:** single chars and pairs

### Time & Space Complexity:
- **Time:** O(n²)
- **Space:** O(n²) - DP table

## Approach 3: Manacher's Algorithm (Advanced)

### How it works:
1. **Preprocess string** to handle even/odd lengths uniformly
2. **Use previously computed information** to avoid redundant work
3. **Linear time solution**

### Time & Space Complexity:
- **Time:** O(n) - Optimal
- **Space:** O(n)

## LeetCode Similar Problems:
- [647. Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/)
- [131. Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/)
- [125. Valid Palindrome](https://leetcode.com/problems/valid-palindrome/)
- [409. Longest Palindrome](https://leetcode.com/problems/longest-palindrome/)
- [234. Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list/)

## Interview Tips:
- Start with expand around centers approach
- Handle both odd and even length palindromes
- Consider edge cases: empty string, single character
- For advanced interviews, mention Manacher's algorithm
- Draw examples to visualize the expansion process 