# Longest Substring Without Repeating Characters

## Problem Statement
Given a string, find the length of the longest substring without repeating characters.

## Example
```
Input: s = "abcabcbb"
Output: 3
Explanation: The answer is "abc", with length 3.

Input: s = "pwwkew"
Output: 3
Explanation: The answer is "wke", with length 3.
```

## Approach: Sliding Window + HashSet

### How it works:
1. **Use two pointers** (left and right) to maintain a window
2. **Expand right pointer** and add characters to HashSet
3. **When duplicate found**, shrink window from left until no duplicates
4. **Track maximum window size**

### Key Logic:
```java
Set<Character> seen = new HashSet<>();
int left = 0, maxLength = 0;

for (int right = 0; right < s.length(); right++) {
    // Shrink window until no duplicates
    while (seen.contains(s.charAt(right))) {
        seen.remove(s.charAt(left));
        left++;
    }
    
    seen.add(s.charAt(right));
    maxLength = Math.max(maxLength, right - left + 1);
}
```

### Time & Space Complexity:
- **Time:** O(n) - Each character visited at most twice
- **Space:** O(min(m, n)) where m = charset size

## Optimization: HashMap with Jump

### How it works:
1. **Store character positions** in HashMap
2. **When duplicate found**, jump left pointer directly
3. **Avoid removing characters one by one**

### Key Logic:
```java
Map<Character, Integer> charIndex = new HashMap<>();
int left = 0, maxLength = 0;

for (int right = 0; right < s.length(); right++) {
    if (charIndex.containsKey(s.charAt(right))) {
        left = Math.max(left, charIndex.get(s.charAt(right)) + 1);
    }
    
    charIndex.put(s.charAt(right), right);
    maxLength = Math.max(maxLength, right - left + 1);
}
```

## LeetCode Similar Problems:
- [424. Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/)
- [340. Longest Substring with At Most K Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/)
- [159. Longest Substring with At Most Two Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/)
- [992. Subarrays with K Different Integers](https://leetcode.com/problems/subarrays-with-k-different-integers/)

## Interview Tips:
- Start with sliding window concept
- Explain why we shrink from left when duplicate found
- Consider ASCII vs Unicode character sets
- Handle edge cases: empty string, single character 