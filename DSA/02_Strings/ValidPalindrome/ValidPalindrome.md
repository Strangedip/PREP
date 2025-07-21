# Valid Palindrome

## Problem Statement
Check if string is palindrome after removing non-alphanumeric characters and ignoring case.

## Approach: Two Pointers (Optimal!)
**Time:** O(n), **Space:** O(1)

### How it works:
1. Use two pointers from start and end
2. Skip non-alphanumeric characters
3. Compare characters (case-insensitive)
4. Move pointers inward

### Key Pattern:
```java
while (left < right) {
    // Skip invalid chars
    while (left < right && !Character.isLetterOrDigit(s.charAt(left))) left++;
    while (left < right && !Character.isLetterOrDigit(s.charAt(right))) right--;
    
    // Compare
    if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
        return false;
    }
    left++; right--;
}
```

## LeetCode Similar Problems:
- [5. Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/)
- [680. Valid Palindrome II](https://leetcode.com/problems/valid-palindrome-ii/)
- [234. Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list/)
- [9. Palindrome Number](https://leetcode.com/problems/palindrome-number/)

## Note
**For Mid-Level:** Master the two pointers with character filtering technique. 