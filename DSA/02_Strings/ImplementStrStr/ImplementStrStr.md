# Implement strStr() / Find Needle in Haystack

## Problem Statement
Find the first occurrence of needle string in haystack string. Return the index of first match, or -1 if needle is not part of haystack.

## Example
```
Input: haystack = "sadbutsad", needle = "sad"
Output: 0
Explanation: "sad" occurs at index 0 and 6. First occurrence is at index 0.
```

## Approach 1: Brute Force (Simple)

### How it works:
1. **Try every possible starting position** in haystack
2. **Check if needle matches** starting from that position
3. **Return first match index**

### Time & Space Complexity:
- **Time:** O(n * m) where n = haystack length, m = needle length
- **Space:** O(1)

## Approach 2: Built-in Method
```java
return haystack.indexOf(needle);
```

## Approach 3: KMP Algorithm (Advanced)

### How it works:
1. **Preprocess needle** to build failure function
2. **Use failure function** to skip characters efficiently
3. **Linear time matching**

### Time & Space Complexity:
- **Time:** O(n + m) - Optimal
- **Space:** O(m) for failure function

## Approach 4: Rolling Hash (Rabin-Karp)

### How it works:
1. **Calculate hash of needle**
2. **Rolling hash of haystack substrings**
3. **Compare hashes, verify with string comparison**

### Time & Space Complexity:
- **Time:** O(n + m) average, O(n * m) worst case
- **Space:** O(1)

## LeetCode Similar Problems:
- [796. Rotate String](https://leetcode.com/problems/rotate-string/)
- [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/)
- [1392. Longest Happy Prefix](https://leetcode.com/problems/longest-happy-prefix/)
- [686. Repeated String Match](https://leetcode.com/problems/repeated-string-match/)

## Interview Tips:
- Start with brute force to show understanding
- Mention built-in methods exist but implement manually
- For advanced: discuss KMP or rolling hash
- Handle edge cases: empty needle, needle longer than haystack 