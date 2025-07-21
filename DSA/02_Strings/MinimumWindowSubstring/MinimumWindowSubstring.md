# Minimum Window Substring

## Problem Statement
Given strings s and t, return the minimum window substring of s that contains all characters of t. If no such window exists, return empty string.

## Example
```
Input: s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"
Explanation: The minimum window substring "BANC" includes 'A', 'B', and 'C' from string t.
```

## Approach: Sliding Window + HashMap

### How it works:
1. **Count characters** in target string t
2. **Expand right pointer** until window contains all characters of t
3. **Contract left pointer** while maintaining valid window
4. **Track minimum valid window**

### Key Logic:
```java
// Count characters in t
Map<Character, Integer> targetCount = new HashMap<>();
for (char c : t.toCharArray()) {
    targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
}

Map<Character, Integer> windowCount = new HashMap<>();
int left = 0, formed = 0, required = targetCount.size();
int minLen = Integer.MAX_VALUE, minStart = 0;

for (int right = 0; right < s.length(); right++) {
    // Expand window
    char rightChar = s.charAt(right);
    windowCount.put(rightChar, windowCount.getOrDefault(rightChar, 0) + 1);
    
    if (targetCount.containsKey(rightChar) && 
        windowCount.get(rightChar).equals(targetCount.get(rightChar))) {
        formed++;
    }
    
    // Contract window
    while (formed == required && left <= right) {
        if (right - left + 1 < minLen) {
            minLen = right - left + 1;
            minStart = left;
        }
        
        char leftChar = s.charAt(left);
        windowCount.put(leftChar, windowCount.get(leftChar) - 1);
        if (targetCount.containsKey(leftChar) && 
            windowCount.get(leftChar) < targetCount.get(leftChar)) {
            formed--;
        }
        left++;
    }
}
```

### Time & Space Complexity:
- **Time:** O(|s| + |t|) - Each character in s visited at most twice
- **Space:** O(|s| + |t|) - HashMap storage

## Alternative: Character Array Approach

### For ASCII characters only:
```java
int[] targetCount = new int[128];
int[] windowCount = new int[128];
// Use arrays instead of HashMap for better performance
```

## LeetCode Similar Problems:
- [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/)
- [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/)
- [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/)
- [727. Minimum Window Subsequence](https://leetcode.com/problems/minimum-window-subsequence/)

## Interview Tips:
- This is a classic sliding window problem
- Key insight: expand until valid, then contract while valid
- Use `formed` variable to track when window is complete
- Handle edge cases: t longer than s, no valid window exists 