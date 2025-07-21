# Reverse String

## Problem Statement
Reverse string in-place with O(1) extra memory.

## Approach: Two Pointers (Optimal!)
**Time:** O(n), **Space:** O(1)

### How it works:
1. Left pointer at start, right pointer at end
2. Swap characters at both pointers
3. Move pointers toward center
4. Stop when pointers meet

## LeetCode Similar Problems:
- [344. Reverse String](https://leetcode.com/problems/reverse-string/) (this problem)
- [541. Reverse String II](https://leetcode.com/problems/reverse-string-ii/)
- [557. Reverse Words in a String III](https://leetcode.com/problems/reverse-words-in-a-string-iii/)
- [186. Reverse Words in a String II](https://leetcode.com/problems/reverse-words-in-a-string-ii/)
- [345. Reverse Vowels of a String](https://leetcode.com/problems/reverse-vowels-of-a-string/)

## Note
**For Mid-Level:** This is a fundamental two pointers problem. Must do in-place. 