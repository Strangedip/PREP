# Valid Anagram

## Problem Statement
Given two strings s and t, return true if t is an anagram of s, and false otherwise. An anagram is formed by rearranging letters of another word.

## Example
```
Input: s = "anagram", t = "nagaram"
Output: true

Input: s = "rat", t = "car"
Output: false
```

## Approach 1: Sorting (Simple)

### How it works:
1. **Sort both strings**
2. **Compare sorted strings**
3. **If equal, they are anagrams**

### Key Logic:
```java
char[] sChars = s.toCharArray();
char[] tChars = t.toCharArray();
Arrays.sort(sChars);
Arrays.sort(tChars);
return Arrays.equals(sChars, tChars);
```

### Time & Space Complexity:
- **Time:** O(n log n) - Due to sorting
- **Space:** O(1) or O(n) depending on sorting algorithm

## Approach 2: Character Count (Optimal)

### How it works:
1. **Count frequency** of each character in both strings
2. **Compare character counts**
3. **If counts match, they are anagrams**

### Key Logic:
```java
if (s.length() != t.length()) return false;

int[] count = new int[26]; // For lowercase letters a-z

for (int i = 0; i < s.length(); i++) {
    count[s.charAt(i) - 'a']++;
    count[t.charAt(i) - 'a']--;
}

for (int c : count) {
    if (c != 0) return false;
}
return true;
```

### Time & Space Complexity:
- **Time:** O(n) - Single pass through strings
- **Space:** O(1) - Fixed size array (26 for lowercase letters)

## Approach 3: HashMap (For Unicode)

### When to use:
- **Unicode characters**
- **Mixed case without conversion**
- **General character sets**

### Key Logic:
```java
Map<Character, Integer> count = new HashMap<>();
for (char c : s.toCharArray()) {
    count.put(c, count.getOrDefault(c, 0) + 1);
}
for (char c : t.toCharArray()) {
    count.put(c, count.getOrDefault(c, 0) - 1);
}
return count.values().stream().allMatch(v -> v == 0);
```

## LeetCode Similar Problems:
- [49. Group Anagrams](https://leetcode.com/problems/group-anagrams/)
- [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/)
- [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/)
- [1347. Minimum Number of Steps to Make Two Strings Anagram](https://leetcode.com/problems/minimum-number-of-steps-to-make-two-strings-anagram/)

## Interview Tips:
- Start with sorting approach for clarity
- Optimize to character counting for better time complexity
- Handle edge cases: different lengths, empty strings
- Consider character set constraints (ASCII vs Unicode)
- Ask about case sensitivity requirements 