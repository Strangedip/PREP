# Minimum Window Substring (LeetCode 76)

> **You are here**: DSA — see [ROADMAP](../../../ROADMAP.md) for level assignment
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Study path**: [StudyGuide](../../StudyGuide.md)
> **Pattern**: [Sliding Window](../../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-2-sliding-window) | **Catalog**: [Algorithmic Patterns](../../../03_CodingPatterns/02_AlgorithmicPatterns.md)

## Problem Statement

Given two strings `s` and `t` of lengths `m` and `n` respectively, return the minimum window substring of `s` such that every character in `t` (including duplicates) is included in the window. If there is no such substring, return the empty string `""`.

The testcases will be generated such that the answer is unique.

**Example 1:**
```
Input: s = "ADOBECODEBANC", t = "ABC"
Output: "BANC"
Explanation: The minimum window substring "BANC" includes 'A', 'B', and 'C' from string t.
```

**Example 2:**
```
Input: s = "a", t = "a"
Output: "a"
Explanation: The entire string s is the minimum window.
```

**Example 3:**
```
Input: s = "a", t = "aa"
Output: ""
Explanation: Both 'a's from t must be included, but s only has one 'a'.
```

**Constraints:**
- `m == s.length`, `n == t.length`
- `1 <= m, n <= 10^5`
- `s` and `t` consist of uppercase and lowercase English letters.

---

## Why This Problem Is Critical

This is one of the hardest and most frequently asked sliding window problems at FAANG companies. It tests:
- Sliding window with complex constraints
- HashMap / frequency counting
- Optimization with `formed` counter
- Careful edge case handling

---

## Approach 1: Brute Force

**Time:** O(m² × n), **Space:** O(m + n)

Check every possible substring of `s` and verify if it contains all characters of `t`. Way too slow for the constraints.

---
#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="ADOBECODEBANC", t="ABC""]
    START --> LOOP["Try all combinations"]
    LOOP --> CHECK{"Valid / optimal?"}
    CHECK -->|no| LOOP
    CHECK -->|yes| OUT["Record best answer"]
    OUT --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: s="ADOBECODEBANC", t="ABC" → "BANC"
Approach: Brute Force

s="ADOBECODEBANC", t="ABC"
window ADOBEC has ABC
shrink to BANC
answer BANC
```


## Approach 2: Sliding Window + HashMap (Optimal)

**Time:** O(m + n), **Space:** O(m + n)

### Core Idea

Maintain a sliding window `[left, right]` over `s`. Expand `right` until the window contains all characters of `t`, then shrink `left` to find the minimum window that still satisfies the condition.

### Key Variables

- `targetCount`: frequency of each character in `t`
- `windowCount`: frequency of each character in the current window
- `required`: number of unique characters in `t` that need to be satisfied
- `formed`: number of unique characters in `t` that are currently satisfied in the window (frequency in window >= frequency in t)

### Complete Implementation


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="ADOBECODEBANC", t="ABC""]
    START --> BUILD["Build HashMap / Set"]
    BUILD --> SCAN["Scan input once"]
    SCAN --> LOOKUP{"Key seen?"}
    LOOKUP -->|yes| FOUND["Return match"]
    LOOKUP -->|no| STORE["Store in map"]
    STORE --> SCAN
```

**Walkthrough (same example):**

```
Example: s="ADOBECODEBANC", t="ABC" → "BANC"
Approach: Sliding Window + HashMap (Optimal)

s="ADOBECODEBANC", t="ABC"
window ADOBEC has ABC
shrink to BANC
answer BANC
```
```java
import java.util.*;

public class MinimumWindowSubstring {
    
    public String minWindow(String s, String t) {
        if (s == null || t == null || s.length() == 0 || t.length() == 0 || s.length() < t.length()) {
            return "";
        }
        
        // Step 1: Count character frequencies in t
        Map<Character, Integer> targetCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
        }
        
        // Number of unique characters in t that need to be present in the window
        int required = targetCount.size();
        
        // Step 2: Sliding window
        Map<Character, Integer> windowCount = new HashMap<>();
        int formed = 0; // Unique characters in window matching target frequency
        int left = 0;
        
        // Track the best window found
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        
        for (int right = 0; right < s.length(); right++) {
            // EXPAND: Add character at right to window
            char rightChar = s.charAt(right);
            windowCount.put(rightChar, windowCount.getOrDefault(rightChar, 0) + 1);
            
            // Check if this character's frequency in window matches target
            if (targetCount.containsKey(rightChar) && 
                windowCount.get(rightChar).intValue() == targetCount.get(rightChar).intValue()) {
                formed++;
            }
            
            // SHRINK: While the window is valid, try to minimize it
            while (formed == required) {
                // Update the minimum window
                int windowSize = right - left + 1;
                if (windowSize < minLen) {
                    minLen = windowSize;
                    minStart = left;
                }
                
                // Remove character at left from window
                char leftChar = s.charAt(left);
                windowCount.put(leftChar, windowCount.get(leftChar) - 1);
                
                // Check if removing this character breaks the constraint
                if (targetCount.containsKey(leftChar) && 
                    windowCount.get(leftChar).intValue() < targetCount.get(leftChar).intValue()) {
                    formed--;
                }
                
                left++;
            }
        }
        
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }
}
```

### Dry Run Example

```
s = "ADOBECODEBANC", t = "ABC"
targetCount = {'A':1, 'B':1, 'C':1}, required = 3

right=0: 'A', windowCount={'A':1}, formed=1 (A satisfied)
right=1: 'D', windowCount={'A':1,'D':1}, formed=1
right=2: 'O', windowCount={'A':1,'D':1,'O':1}, formed=1
right=3: 'B', windowCount={'A':1,'D':1,'O':1,'B':1}, formed=2 (B satisfied)
right=4: 'E', windowCount={...,'E':1}, formed=2
right=5: 'C', windowCount={...,'C':1}, formed=3 ✓ (ALL SATISFIED!)

  SHRINK:
  Window = "ADOBEC" [0,5], len=6 → minLen=6, minStart=0
  Remove 'A' at left=0: windowCount['A']=0, formed=2 (A no longer satisfied)
  left=1

right=6: 'O', formed=2 (still need A)
right=7: 'D', formed=2
right=8: 'E', formed=2
right=9: 'B', windowCount['B']=2, formed=2 (B already satisfied)
right=10: 'A', windowCount['A']=1, formed=3 ✓

  SHRINK:
  Window = "DOBECODEBA" [1,10], len=10 > 6 → no update
  Remove 'D' at left=1: formed=3 (D not in target)
  left=2
  Window = "OBECODEBA" [2,10], len=9 > 6 → no update
  Remove 'O' at left=2: formed=3
  left=3
  Window = "BECODEBA" [3,10], len=8 > 6 → no update
  Remove 'B' at left=3: windowCount['B']=1, still >= 1 → formed=3
  left=4
  Window = "ECODEBA" [4,10], len=7 > 6 → no update
  Remove 'E' at left=4: formed=3
  left=5
  Window = "CODEBA" [5,10], len=6 = 6 → minLen=6, minStart=5
  Remove 'C' at left=5: windowCount['C']=0, formed=2
  left=6

right=11: 'N', formed=2
right=12: 'C', windowCount['C']=1, formed=3 ✓

  SHRINK:
  Window = "ODEBANC" [6,12], len=7 > 6 → no update
  Remove 'O' at left=6: formed=3
  left=7
  Window = "DEBANC" [7,12], len=6 = 6 → minLen=6, minStart=7
  Remove 'D' at left=7: formed=3
  left=8
  Window = "EBANC" [8,12], len=5 < 6 → minLen=5, minStart=8
  Remove 'E' at left=8: formed=3
  left=9
  Window = "BANC" [9,12], len=4 < 5 → minLen=4, minStart=9
  Remove 'B' at left=9: windowCount['B']=0, formed=2
  left=10

End of string.
Result: s.substring(9, 13) = "BANC"
```

### Why `formed` Is Better Than Checking All Frequencies

Without `formed`, you would need to check all characters in `targetCount` every time the window changes, making each check O(|t|). With `formed`, each check is O(1), keeping the overall algorithm at O(|s| + |t|).

---

## Approach 3: Optimized with Character Array (Faster)

**Time:** O(m + n), **Space:** O(1) — fixed 128-size arrays


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="ADOBECODEBANC", t="ABC""]
    START --> STEP1["Optimized with Character Array (Faster): step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: s="ADOBECODEBANC", t="ABC" → "BANC"
Approach: Optimized with Character Array (Faster)

s="ADOBECODEBANC", t="ABC"
window ADOBEC has ABC
shrink to BANC
answer BANC
```
```java
public String minWindow(String s, String t) {
    if (s.length() < t.length()) return "";
    
    int[] targetCount = new int[128];
    int[] windowCount = new int[128];
    
    int required = 0;
    for (char c : t.toCharArray()) {
        if (targetCount[c] == 0) required++;
        targetCount[c]++;
    }
    
    int formed = 0, left = 0;
    int minLen = Integer.MAX_VALUE, minStart = 0;
    
    for (int right = 0; right < s.length(); right++) {
        char rc = s.charAt(right);
        windowCount[rc]++;
        
        if (targetCount[rc] > 0 && windowCount[rc] == targetCount[rc]) {
            formed++;
        }
        
        while (formed == required) {
            if (right - left + 1 < minLen) {
                minLen = right - left + 1;
                minStart = left;
            }
            
            char lc = s.charAt(left);
            windowCount[lc]--;
            if (targetCount[lc] > 0 && windowCount[lc] < targetCount[lc]) {
                formed--;
            }
            left++;
        }
    }
    
    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
```

### Why Array Is Faster Than HashMap

- Array access `arr[c]` is O(1) with no hashing overhead.
- HashMap operations (`get`, `put`, `getOrDefault`) involve hashing, boxing/unboxing, and potential collision handling.
- For ASCII characters, the array approach is 3-5x faster in practice.

---

## Approach 4: Filtered Characters (Optimization for Large s, Small t)

**Time:** O(|s| + |t|), but faster in practice when `|s| >> |t|`


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="ADOBECODEBANC", t="ABC""]
    START --> STEP1["Filtered Characters (Optimization for Large s, Small t): step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: s="ADOBECODEBANC", t="ABC" → "BANC"
Approach: Filtered Characters (Optimization for Large s, Small t)

s="ADOBECODEBANC", t="ABC"
window ADOBEC has ABC
shrink to BANC
answer BANC
```
```java
public String minWindow(String s, String t) {
    Map<Character, Integer> targetCount = new HashMap<>();
    for (char c : t.toCharArray()) {
        targetCount.merge(c, 1, Integer::sum);
    }
    
    // Create filtered list: only characters in s that are also in t
    List<int[]> filtered = new ArrayList<>(); // [index, char]
    for (int i = 0; i < s.length(); i++) {
        char c = s.charAt(i);
        if (targetCount.containsKey(c)) {
            filtered.add(new int[]{i, c});
        }
    }
    
    // Apply sliding window on filtered list
    Map<Character, Integer> windowCount = new HashMap<>();
    int required = targetCount.size(), formed = 0;
    int left = 0;
    int minLen = Integer.MAX_VALUE, minStart = 0;
    
    for (int right = 0; right < filtered.size(); right++) {
        char rc = (char) filtered.get(right)[1];
        windowCount.merge(rc, 1, Integer::sum);
        
        if (windowCount.get(rc).intValue() == targetCount.get(rc).intValue()) {
            formed++;
        }
        
        while (formed == required) {
            int start = filtered.get(left)[0];
            int end = filtered.get(right)[0];
            if (end - start + 1 < minLen) {
                minLen = end - start + 1;
                minStart = start;
            }
            
            char lc = (char) filtered.get(left)[1];
            windowCount.merge(lc, -1, Integer::sum);
            if (windowCount.get(lc) < targetCount.get(lc)) {
                formed--;
            }
            left++;
        }
    }
    
    return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
}
```

This is useful when `s` has millions of characters but only a few match characters in `t`.

---

## Approach Comparison

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| Brute Force | O(m² × n) | O(m+n) | Understanding only |
| HashMap Window | O(m + n) | O(m+n) | General case, readable |
| Array Window | O(m + n) | O(1) | Performance, ASCII only |
| Filtered Window | O(m + n) | O(m+n) | Large s, small t |

---

## Common Mistakes

1. **Using `==` instead of `.intValue()` for Integer comparison**: In Java, `HashMap<Character, Integer>` stores `Integer` objects. Comparing with `==` checks reference equality, not value equality. Always use `.intValue()` or `.equals()`.
2. **Not handling duplicate characters in t**: `t = "AAB"` requires the window to have at least 2 A's and 1 B.
3. **Forgetting the `formed` counter**: Without it, you would need to iterate through all target characters each time.
4. **Not shrinking enough**: The `while` loop for shrinking must continue as long as the window is valid, not just once.
5. **Off-by-one in substring extraction**: `s.substring(minStart, minStart + minLen)` — the second parameter is exclusive.

---

## Edge Cases

| Case | s | t | Expected |
|------|---|---|----------|
| No valid window | "abc" | "xyz" | "" |
| t longer than s | "a" | "aa" | "" |
| Exact match | "abc" | "abc" | "abc" |
| Single char match | "a" | "a" | "a" |
| t has duplicates | "aab" | "aab" | "aab" |
| Case sensitive | "Ab" | "ab" | "" (if case matters) |

---

## LeetCode Similar Problems

- [3. Longest Substring Without Repeating Characters](https://leetcode.com/problems/longest-substring-without-repeating-characters/)
- [30. Substring with Concatenation of All Words](https://leetcode.com/problems/substring-with-concatenation-of-all-words/)
- [209. Minimum Size Subarray Sum](https://leetcode.com/problems/minimum-size-subarray-sum/)
- [239. Sliding Window Maximum](https://leetcode.com/problems/sliding-window-maximum/)
- [438. Find All Anagrams in a String](https://leetcode.com/problems/find-all-anagrams-in-a-string/)
- [567. Permutation in String](https://leetcode.com/problems/permutation-in-string/)
- [727. Minimum Window Subsequence](https://leetcode.com/problems/minimum-window-subsequence/)

---

## Interview Tips

1. **State the approach clearly**: "I'll use a sliding window. Expand right until the window is valid, then shrink left to minimize."
2. **Explain the `formed` optimization**: "I track how many unique characters are satisfied, so I can check validity in O(1) instead of O(|t|)."
3. **Handle the Integer comparison carefully**: Mention the `.intValue()` pitfall in Java.
4. **Test with duplicates**: Make sure your solution handles `t = "AAB"` correctly.
5. **Mention the filtered optimization**: "If s is much larger than t, I can pre-filter s to only include characters that appear in t, reducing the window operations."
6. **For Lead-level**: Discuss how this pattern applies to real-world problems — log analysis (finding minimum time window containing all event types), network packet analysis, or text search.
#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="ADOBECODEBANC", t="ABC""]
    START --> STEP1["Approach 5: step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: s="ADOBECODEBANC", t="ABC" → "BANC"
Approach: Approach 5

s="ADOBECODEBANC", t="ABC"
window ADOBEC has ABC
shrink to BANC
answer BANC
```

