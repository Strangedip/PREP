# Group Anagrams

## Problem Statement
Given an array of strings, group anagrams together. Anagrams are words formed by rearranging letters of another word.

## Example
```
Input: strs = ["eat","tea","tan","ate","nat","bat"]
Output: [["bat"],["nat","tan"],["ate","eat","tea"]]
```

## Approach 1: Sort + HashMap (Most Common)

### How it works:
1. **Sort each string** to create a canonical form
2. **Use sorted string as HashMap key**
3. **Group original strings by their sorted key**

### Key Logic:
```java
Map<String, List<String>> map = new HashMap<>();
for (String str : strs) {
    char[] chars = str.toCharArray();
    Arrays.sort(chars);
    String key = new String(chars);
    
    map.computeIfAbsent(key, k -> new ArrayList<>()).add(str);
}
```

### Time & Space Complexity:
- **Time:** O(n * k log k) where n = number of strings, k = max string length
- **Space:** O(n * k) for storing groups

## Approach 2: Character Count Array (Optimal)

### How it works:
1. **Count frequency of each character** (a-z)
2. **Use count array as key** (convert to string)
3. **Group strings with same character frequencies**

### Time & Space Complexity:
- **Time:** O(n * k) - Linear in string length
- **Space:** O(n * k) for storing groups

## LeetCode Similar Problems:
- [242. Valid Anagram](https://leetcode.com/problems/valid-anagram/)
- [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/)
- [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/)
- [1347. Minimum Number of Steps to Make Two Strings Anagram](https://leetcode.com/problems/minimum-number-of-steps-to-make-two-strings-anagram/)

## Interview Tips:
- Start with sorting approach (easier to code)
- Mention character count optimization
- Consider follow-up: Unicode characters vs only lowercase
- Handle empty strings and single characters 