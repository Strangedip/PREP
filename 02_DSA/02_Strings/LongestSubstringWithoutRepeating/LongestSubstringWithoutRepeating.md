# Longest Substring Without Repeating Characters (LeetCode 3)

## Problem Statement

Given a string `s`, find the length of the longest substring without repeating characters.

**Example 1:**
```
Input: s = "abcabcbb"
Output: 3
Explanation: The answer is "abc", with the length of 3.
```

**Example 2:**
```
Input: s = "bbbbb"
Output: 1
Explanation: The answer is "b", with the length of 1.
```

**Example 3:**
```
Input: s = "pwwkew"
Output: 3
Explanation: The answer is "wke", with the length of 3.
Notice that "pwke" is a subsequence, not a substring.
```

**Constraints:**
- `0 <= s.length <= 5 * 10^4`
- `s` consists of English letters, digits, symbols and spaces.

---

## Why This Problem Is Critical

LeetCode #3 is consistently one of the top 5 most-asked problems at Google, Amazon, Meta, and Microsoft. It tests the fundamental sliding window pattern that appears in dozens of variations.

---

## Approach 1: Brute Force

**Time:** O(n³), **Space:** O(min(n, m)) where m = charset size

Check every possible substring and verify it has no repeating characters.

```java
public int lengthOfLongestSubstring(String s) {
    int maxLen = 0;
    for (int i = 0; i < s.length(); i++) {
        for (int j = i; j < s.length(); j++) {
            if (allUnique(s, i, j)) {
                maxLen = Math.max(maxLen, j - i + 1);
            }
        }
    }
    return maxLen;
}

private boolean allUnique(String s, int start, int end) {
    Set<Character> set = new HashSet<>();
    for (int i = start; i <= end; i++) {
        if (!set.add(s.charAt(i))) return false;
    }
    return true;
}
```

Not acceptable in an interview. Present only as a baseline.

---

## Approach 2: Sliding Window + HashSet

**Time:** O(n), **Space:** O(min(n, m))

### How It Works

Maintain a window `[left, right]` where all characters are unique. Use a HashSet to track characters in the current window.

1. **Expand**: Move `right` pointer forward, add character to set.
2. **Shrink**: If the character already exists in the set, remove characters from the `left` until the duplicate is gone.
3. **Update**: Track the maximum window size.

### Complete Implementation

```java
import java.util.*;

public class LongestSubstringWithoutRepeating {
    
    public int lengthOfLongestSubstring(String s) {
        Set<Character> seen = new HashSet<>();
        int left = 0;
        int maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            
            // Shrink window until the duplicate is removed
            while (seen.contains(rightChar)) {
                seen.remove(s.charAt(left));
                left++;
            }
            
            // Add the current character and update max
            seen.add(rightChar);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
}
```

### Dry Run Example

```
Input: s = "abcabcbb"

right=0: char='a', set={}, not seen → add → set={'a'}, window="a", maxLen=1
right=1: char='b', set={'a'}, not seen → add → set={'a','b'}, window="ab", maxLen=2
right=2: char='c', set={'a','b'}, not seen → add → set={'a','b','c'}, window="abc", maxLen=3
right=3: char='a', set={'a','b','c'}, SEEN!
  Remove 'a' at left=0 → set={'b','c'}, left=1
  Now 'a' not in set → add → set={'b','c','a'}, window="bca", maxLen=3
right=4: char='b', set={'b','c','a'}, SEEN!
  Remove 'b' at left=1 → set={'c','a'}, left=2
  Now 'b' not in set → add → set={'c','a','b'}, window="cab", maxLen=3
right=5: char='c', set={'c','a','b'}, SEEN!
  Remove 'c' at left=2 → set={'a','b'}, left=3
  Now 'c' not in set → add → set={'a','b','c'}, window="abc", maxLen=3
right=6: char='b', set={'a','b','c'}, SEEN!
  Remove 'a' at left=3 → set={'b','c'}, left=4
  Remove 'b' at left=4 → set={'c'}, left=5
  Now 'b' not in set → add → set={'c','b'}, window="cb", maxLen=3
right=7: char='b', set={'c','b'}, SEEN!
  Remove 'c' at left=5 → set={'b'}, left=6
  Remove 'b' at left=6 → set={}, left=7
  Now 'b' not in set → add → set={'b'}, window="b", maxLen=3

Final answer: 3
```

---

## Approach 3: Sliding Window + HashMap (Optimized Jump)

**Time:** O(n), **Space:** O(min(n, m))

### Key Improvement

Instead of removing characters one by one from the left (which can be slow when the duplicate is far from the left pointer), store the index of each character. When a duplicate is found, jump the `left` pointer directly to one position after the previous occurrence.

### Complete Implementation

```java
public int lengthOfLongestSubstring(String s) {
    Map<Character, Integer> charIndex = new HashMap<>();
    int left = 0;
    int maxLen = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char rightChar = s.charAt(right);
        
        // If character was seen before AND its index is within current window
        if (charIndex.containsKey(rightChar)) {
            // Jump left pointer to one past the previous occurrence
            left = Math.max(left, charIndex.get(rightChar) + 1);
        }
        
        // Store/update the character's latest index
        charIndex.put(rightChar, right);
        
        // Update maximum length
        maxLen = Math.max(maxLen, right - left + 1);
    }
    
    return maxLen;
}
```

### Dry Run Example

```
Input: s = "abcabcbb"

right=0: char='a', not in map, map={'a':0}, window=[0,0], maxLen=1
right=1: char='b', not in map, map={'a':0,'b':1}, window=[0,1], maxLen=2
right=2: char='c', not in map, map={'a':0,'b':1,'c':2}, window=[0,2], maxLen=3
right=3: char='a', in map at index 0!
  left = max(0, 0+1) = 1
  map={'a':3,'b':1,'c':2}, window=[1,3], maxLen=3
right=4: char='b', in map at index 1!
  left = max(1, 1+1) = 2
  map={'a':3,'b':4,'c':2}, window=[2,4], maxLen=3
right=5: char='c', in map at index 2!
  left = max(2, 2+1) = 3
  map={'a':3,'b':4,'c':5}, window=[3,5], maxLen=3
right=6: char='b', in map at index 4!
  left = max(3, 4+1) = 5
  map={'a':3,'b':6,'c':5}, window=[5,6], maxLen=3
right=7: char='b', in map at index 6!
  left = max(5, 6+1) = 7
  map={'a':3,'b':7,'c':5}, window=[7,7], maxLen=3

Final answer: 3
```

### Why `Math.max(left, ...)`?

The `Math.max` is critical. Without it, when we encounter a duplicate, we might jump the `left` pointer backward if the character was seen before but outside the current window. For example:

```
s = "abba"

right=0: 'a' at 0, left=0, window="a"
right=1: 'b' at 1, left=0, window="ab"
right=2: 'b' at 1 (previous), left = max(0, 1+1) = 2, window="b"
right=3: 'a' at 0 (previous), WITHOUT max: left = 0+1 = 1 (WRONG! Goes backward!)
         WITH max: left = max(2, 0+1) = 2 (Correct! Stays at current position)
```

---

## Approach 4: Sliding Window + Integer Array (Fastest)

**Time:** O(n), **Space:** O(128) = O(1)

### How It Works

If the character set is ASCII (128 characters), use an integer array instead of a HashMap for O(1) lookups.

```java
public int lengthOfLongestSubstring(String s) {
    int[] lastIndex = new int[128];
    Arrays.fill(lastIndex, -1); // -1 means character not seen yet
    
    int left = 0;
    int maxLen = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        
        // If character was seen and is within current window
        if (lastIndex[c] >= left) {
            left = lastIndex[c] + 1;
        }
        
        lastIndex[c] = right;
        maxLen = Math.max(maxLen, right - left + 1);
    }
    
    return maxLen;
}
```

This is the fastest approach in practice because array access is faster than HashMap operations.

---

## Approach Comparison

| Approach | Time | Space | Operations per char | Best For |
|----------|------|-------|-------------------|----------|
| Brute Force | O(n³) | O(n) | Multiple passes | Baseline only |
| HashSet | O(n) | O(min(n,m)) | 1-2 set ops | Simple and correct |
| HashMap (jump) | O(n) | O(min(n,m)) | 1 map op | Cleaner code |
| Int Array | O(n) | O(128) | 1 array access | Fastest in practice |

---

## Sliding Window Pattern Template

This problem is the foundation for a family of sliding window problems:

```java
// Pattern: "Longest/Shortest substring with [constraint]"
int left = 0, result = 0;
State windowState = new State(); // HashSet, HashMap, int[], etc.

for (int right = 0; right < s.length(); right++) {
    // 1. EXPAND: Update window state with s[right]
    windowState.add(s.charAt(right));
    
    // 2. SHRINK: While window is invalid
    while (windowIsInvalid(windowState)) {
        windowState.remove(s.charAt(left));
        left++;
    }
    
    // 3. UPDATE: Record best result
    result = Math.max(result, right - left + 1); // for longest
    // result = Math.min(result, right - left + 1); // for shortest
}
```

---

## Edge Cases

| Case | Input | Expected | Explanation |
|------|-------|----------|-------------|
| Empty string | "" | 0 | No characters |
| Single char | "a" | 1 | Entire string |
| All same | "bbbbb" | 1 | Only one unique at a time |
| All unique | "abcdef" | 6 | Entire string |
| Spaces | "a b c" | 3 | Space is a character ("a b") |
| Special chars | "!@#!@" | 3 | "!@#" |

---

## Common Mistakes

1. **Not using `Math.max` for left pointer jump**: This causes the left pointer to go backward, producing wrong results (see the `"abba"` example above).
2. **Using `substring` to check duplicates**: This makes the solution O(n²). Always use a set/map.
3. **Confusing substring with subsequence**: The problem asks for contiguous substring, not subsequence.
4. **Off-by-one in window size**: Window size is `right - left + 1`, not `right - left`.
5. **Not handling empty string**: An empty string should return 0.

---

## LeetCode Similar Problems

- [159. Longest Substring with At Most Two Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/)
- [340. Longest Substring with At Most K Distinct Characters](https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/)
- [424. Longest Repeating Character Replacement](https://leetcode.com/problems/longest-repeating-character-replacement/)
- [76. Minimum Window Substring](https://leetcode.com/problems/minimum-window-substring/)
- [904. Fruit Into Baskets](https://leetcode.com/problems/fruit-into-baskets/)
- [992. Subarrays with K Different Integers](https://leetcode.com/problems/subarrays-with-k-different-integers/)
- [1695. Maximum Erasure Value](https://leetcode.com/problems/maximum-erasure-value/)

---

## Interview Tips

1. **State the pattern immediately**: "This is a sliding window problem. I'll maintain a window of unique characters and track the maximum size."
2. **Start with HashSet approach**: It is the most intuitive. Then optimize to HashMap or array if time permits.
3. **Explain why O(n)**: "Each character is added to the window once (right pointer) and removed at most once (left pointer), giving O(2n) = O(n)."
4. **Discuss the `Math.max` trick**: If you use the HashMap jump approach, proactively explain why `Math.max` is needed.
5. **Mention the character set**: "I'm using ASCII (128), but if we need Unicode support, I'd use a HashMap instead of an array."
6. **Be ready for follow-ups**: "If asked for the actual substring (not just length), I'd track the start index alongside the max length."
