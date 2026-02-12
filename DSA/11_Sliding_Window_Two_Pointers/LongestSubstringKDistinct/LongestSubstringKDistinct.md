# Longest Substring with At Most K Distinct Characters (LeetCode 340)

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

**Example 3:**
```
Input: s = "aabacbebebe", k = 3
Output: 7
Explanation: The substring is "cbebebe" with length 7.
```

**Constraints:**
- `1 <= s.length <= 5 * 10^4`
- `0 <= k <= 50`

---

## Approach 1: Brute Force

**Time:** O(n³), **Space:** O(n)

### How It Works

Check every possible substring and count the number of distinct characters. Track the longest substring with at most `k` distinct characters. This is too slow for an interview but establishes the baseline.

```java
public int lengthOfLongestSubstringKDistinct(String s, int k) {
    int maxLen = 0;
    for (int i = 0; i < s.length(); i++) {
        for (int j = i; j < s.length(); j++) {
            Set<Character> distinct = new HashSet<>();
            for (int m = i; m <= j; m++) {
                distinct.add(s.charAt(m));
            }
            if (distinct.size() <= k) {
                maxLen = Math.max(maxLen, j - i + 1);
            }
        }
    }
    return maxLen;
}
```

---

## Approach 2: Sliding Window with HashMap (Optimal)

**Time:** O(n), **Space:** O(k)

### How It Works

This is a classic sliding window problem. The window represents the current substring, and we use a HashMap to track character frequencies within the window.

**Window Invariant:** The window always contains at most `k` distinct characters.

1. **Expand** the window by moving the `right` pointer and adding characters.
2. **Shrink** the window from the `left` when we have more than `k` distinct characters.
3. **Track** the maximum window size.

### Complete Implementation

```java
import java.util.*;

public class LongestSubstringKDistinct {
    
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (s == null || s.length() == 0 || k == 0) return 0;
        
        Map<Character, Integer> charCount = new HashMap<>();
        int left = 0;
        int maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            // Expand: add character at right pointer
            char rightChar = s.charAt(right);
            charCount.put(rightChar, charCount.getOrDefault(rightChar, 0) + 1);
            
            // Shrink: while we have more than k distinct characters
            while (charCount.size() > k) {
                char leftChar = s.charAt(left);
                charCount.put(leftChar, charCount.get(leftChar) - 1);
                
                // Remove character from map if count becomes 0
                if (charCount.get(leftChar) == 0) {
                    charCount.remove(leftChar);
                }
                left++;
            }
            
            // Update maximum length
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
}
```

### Dry Run Example

```
Input: s = "eceba", k = 2

right=0: char='e', map={'e':1}, size=1 ≤ 2, window="e", maxLen=1
right=1: char='c', map={'e':1,'c':1}, size=2 ≤ 2, window="ec", maxLen=2
right=2: char='e', map={'e':2,'c':1}, size=2 ≤ 2, window="ece", maxLen=3
right=3: char='b', map={'e':2,'c':1,'b':1}, size=3 > 2!
  Shrink: remove 'e' at left=0, map={'e':1,'c':1,'b':1}, size=3 > 2!
  Shrink: remove 'c' at left=1, map={'e':1,'b':1}, size=2 ≤ 2, left=2
  window="eb", maxLen=3 (no update since 2 < 3)
right=4: char='a', map={'e':1,'b':1,'a':1}, size=3 > 2!
  Shrink: remove 'e' at left=2, map={'b':1,'a':1}, size=2 ≤ 2, left=3
  window="ba", maxLen=3 (no update since 2 < 3)

Final answer: 3
```

### Why Each Character Is Visited At Most Twice

- The `right` pointer visits each character exactly once: O(n).
- The `left` pointer also moves forward across the string, visiting each character at most once: O(n).
- Total: O(n + n) = O(n).

This is the key insight that makes sliding window O(n) instead of O(n²).

---

## Approach 3: Sliding Window with Array (Faster for ASCII)

**Time:** O(n), **Space:** O(1) — fixed 128-size array

### How It Works

If the character set is limited (ASCII or lowercase English), use a fixed-size array instead of a HashMap for faster access. Maintain a separate counter for the number of distinct characters.

```java
public int lengthOfLongestSubstringKDistinct(String s, int k) {
    if (s == null || s.length() == 0 || k == 0) return 0;
    
    int[] count = new int[128]; // ASCII characters
    int distinct = 0;
    int left = 0;
    int maxLen = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char rightChar = s.charAt(right);
        if (count[rightChar] == 0) {
            distinct++;
        }
        count[rightChar]++;
        
        while (distinct > k) {
            char leftChar = s.charAt(left);
            count[leftChar]--;
            if (count[leftChar] == 0) {
                distinct--;
            }
            left++;
        }
        
        maxLen = Math.max(maxLen, right - left + 1);
    }
    
    return maxLen;
}
```

### When to Use Array vs HashMap

| Scenario | Use Array | Use HashMap |
|----------|-----------|-------------|
| ASCII characters only | Yes (128 size) | Overkill |
| Lowercase English only | Yes (26 size) | Overkill |
| Unicode characters | No (too large) | Yes |
| Unknown character set | No | Yes |

---

## Approach 4: Sliding Window with Ordered Map (Leftmost Removal)

**Time:** O(n log k), **Space:** O(k)

### How It Works

Instead of shrinking the window one character at a time, use a `LinkedHashMap` (or `TreeMap`) to track the rightmost position of each character. When we need to shrink, we can jump directly to the leftmost character's last position + 1.

```java
public int lengthOfLongestSubstringKDistinct(String s, int k) {
    if (s == null || s.length() == 0 || k == 0) return 0;
    
    // Maps character to its rightmost index in the current window
    LinkedHashMap<Character, Integer> lastSeen = new LinkedHashMap<>();
    int left = 0;
    int maxLen = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        
        // If character already exists, remove and re-insert to maintain order
        if (lastSeen.containsKey(c)) {
            lastSeen.remove(c);
        }
        lastSeen.put(c, right);
        
        // If we have more than k distinct, remove the leftmost (oldest) character
        if (lastSeen.size() > k) {
            // The first entry in LinkedHashMap is the oldest (leftmost last occurrence)
            Map.Entry<Character, Integer> oldest = lastSeen.entrySet().iterator().next();
            left = oldest.getValue() + 1;
            lastSeen.remove(oldest.getKey());
        }
        
        maxLen = Math.max(maxLen, right - left + 1);
    }
    
    return maxLen;
}
```

This approach is more complex but avoids the inner `while` loop entirely, making it useful when k is very small and the strings between distinct characters are very long.

---

## Approach Comparison

| Approach | Time | Space | Code Complexity | Best For |
|----------|------|-------|----------------|----------|
| Brute Force | O(n³) | O(n) | Simple | Understanding only |
| Sliding Window + HashMap | O(n) | O(k) | Medium | General case |
| Sliding Window + Array | O(n) | O(1) | Simple | ASCII characters |
| Sliding Window + OrderedMap | O(n) | O(k) | Complex | Very small k |

---

## Pattern: Sliding Window Template

This problem follows the classic **Variable-Size Sliding Window** pattern:

```java
// Template for "longest substring with at most K [constraint]"
int left = 0, maxLen = 0;
Map<Key, Value> window = new HashMap<>();

for (int right = 0; right < s.length(); right++) {
    // 1. EXPAND: Add s[right] to window state
    updateWindow(s.charAt(right));
    
    // 2. SHRINK: While window violates constraint
    while (windowViolatesConstraint()) {
        removeFromWindow(s.charAt(left));
        left++;
    }
    
    // 3. UPDATE: Record best answer
    maxLen = Math.max(maxLen, right - left + 1);
}
```

---

## Variations of This Problem

| Variation | Problem | Key Difference |
|-----------|---------|----------------|
| Exactly K distinct | [992. Subarrays with K Different Integers](https://leetcode.com/problems/subarrays-with-k-different-integers/) | exactlyK = atMostK - atMost(K-1) |
| At most 2 distinct | [159. Longest Substring with At Most Two Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/) | Special case where k=2 |
| No repeating characters | [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/) | k = number of unique chars in substring |
| Minimum window substring | [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/) | Minimum window containing all chars |

---

## Edge Cases

| Case | Input | Expected | Explanation |
|------|-------|----------|-------------|
| k = 0 | "abc", 0 | 0 | No characters allowed |
| k ≥ distinct chars | "abc", 5 | 3 | Entire string is valid |
| Empty string | "", 2 | 0 | No substring possible |
| All same characters | "aaaa", 1 | 4 | Entire string is valid |
| k = 1 | "abcba", 1 | 1 | Single character at a time |
| Single character string | "a", 1 | 1 | Trivial case |

---

## Common Mistakes

1. **Forgetting to remove characters with zero count from the HashMap**: If you do `map.put(c, map.get(c) - 1)` but do not remove the entry when count is 0, the `map.size()` will be wrong, and the window constraint check will fail.
2. **Off-by-one in window size**: The window size is `right - left + 1`, not `right - left`.
3. **Not handling k = 0**: Must return 0 immediately, otherwise the algorithm enters an infinite shrink loop.
4. **Not handling null or empty string**: Always add null/empty checks upfront.

---

## LeetCode Similar Problems

- [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/)
- [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/)
- [159. Longest Substring with At Most Two Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/)
- [209. Minimum Size Subarray Sum](https://leetcode.com/problems/minimum-size-subarray-sum/)
- [424. Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/)
- [904. Fruit Into Baskets](https://leetcode.com/problems/fruit-into-baskets/)
- [992. Subarrays with K Different Integers](https://leetcode.com/problems/subarrays-with-k-different-integers/)

---

## Interview Tips

1. **Identify the pattern immediately**: "This is a variable-size sliding window problem where the constraint is at most k distinct characters."
2. **State the invariant**: "My window will always maintain at most k distinct characters. When it exceeds k, I shrink from the left."
3. **Explain why it is O(n)**: "Each character is added once (right pointer) and removed at most once (left pointer), so total work is O(2n) = O(n)."
4. **Discuss the HashMap vs Array trade-off**: Show awareness of when to use each.
5. **Know the exactly-K trick**: "If asked for exactly K distinct, I compute atMostK(s, k) - atMostK(s, k-1)."
6. **Connect to other problems**: "This is the same sliding window template used for Minimum Window Substring, Longest Substring Without Repeating Characters, and Fruit Into Baskets."
