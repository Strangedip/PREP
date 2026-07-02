# Implement strStr() / Find the Index of the First Occurrence in a String (LeetCode 28)

> **You are here**: DSA — see [ROADMAP](../../../ROADMAP.md) for level assignment
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Study path**: [StudyGuide](../../StudyGuide.md)
> **Pattern**: [Dynamic Programming](../../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-16-dynamic-programming-patterns) | **Catalog**: [Algorithmic Patterns](../../../03_CodingPatterns/02_AlgorithmicPatterns.md)

## Problem Statement

Given two strings `haystack` and `needle`, return the index of the first occurrence of `needle` in `haystack`, or `-1` if `needle` is not part of `haystack`.

**Example 1:**
```
Input: haystack = "sadbutsad", needle = "sad"
Output: 0
Explanation: "sad" occurs at index 0 and 6. The first occurrence is at index 0.
```

**Example 2:**
```
Input: haystack = "leetcode", needle = "leeto"
Output: -1
Explanation: "leeto" did not occur in "leetcode".
```

**Constraints:**
- `1 <= haystack.length, needle.length <= 10^4`
- `haystack` and `needle` consist of only lowercase English characters.

---

## Approach 1: Brute Force (Naive String Matching)

**Time:** O(n × m) where n = haystack length, m = needle length
**Space:** O(1)

### How It Works

Try every possible starting position in the haystack. For each starting position, check if the needle matches character by character.

### Complete Implementation


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: haystack="sadbutsad", needle="sad""]
    START --> LOOP["Try all combinations"]
    LOOP --> CHECK{"Valid / optimal?"}
    CHECK -->|no| LOOP
    CHECK -->|yes| OUT["Record best answer"]
    OUT --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: haystack="sadbutsad", needle="sad" → 0
Approach: Brute Force (Naive String Matching)

Enumerate all candidates from example input
Check validity/optimal condition
Keep best answer found
```
```java
public class ImplementStrStr {
    
    public int strStr(String haystack, String needle) {
        int n = haystack.length();
        int m = needle.length();
        
        if (m == 0) return 0;
        if (m > n) return -1;
        
        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && haystack.charAt(i + j) == needle.charAt(j)) {
                j++;
            }
            if (j == m) {
                return i; // Found complete match
            }
        }
        
        return -1;
    }
}
```

### Dry Run Example

```
haystack = "sadbutsad", needle = "sad"

i=0: compare "sad" with "sad"
  j=0: 's' == 's' ✓
  j=1: 'a' == 'a' ✓
  j=2: 'd' == 'd' ✓
  j=3 == m → Match found! Return 0.
```

### When Brute Force Is Actually Fine

For most interview scenarios where `n` and `m` are both under 10^4, the brute force approach runs in under 100 million operations in the worst case, which is acceptable. Many interviewers will accept this solution and then ask you to discuss the optimized approaches verbally.

---

## Approach 2: KMP Algorithm (Knuth-Morris-Pratt)

**Time:** O(n + m), **Space:** O(m)

### Core Idea

The brute force approach wastes work because when a mismatch occurs, it restarts matching from the very next position in the haystack. The KMP algorithm avoids this by precomputing a "failure function" (also called the "partial match table" or "LPS array") that tells us how far back we can skip in the needle when a mismatch occurs.

### What Is the LPS Array?

LPS stands for "Longest Proper Prefix which is also Suffix." For each position `i` in the needle, `lps[i]` is the length of the longest proper prefix of `needle[0..i]` that is also a suffix.

**Example for needle = "ABABAC":**
```
Index:  0  1  2  3  4  5
Char:   A  B  A  B  A  C
LPS:    0  0  1  2  3  0
```

- `lps[0]` = 0 (single char has no proper prefix/suffix)
- `lps[1]` = 0 ("AB" — no match)
- `lps[2]` = 1 ("ABA" — "A" is both prefix and suffix)
- `lps[3]` = 2 ("ABAB" — "AB" is both prefix and suffix)
- `lps[4]` = 3 ("ABABA" — "ABA" is both prefix and suffix)
- `lps[5]` = 0 ("ABABAC" — no match)

### Complete Implementation


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: haystack="sadbutsad", needle="sad""]
    START --> STEP1["KMP Algorithm (Knuth-Morris-Pratt): step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: haystack="sadbutsad", needle="sad" → 0
Approach: KMP Algorithm (Knuth-Morris-Pratt)

Apply KMP Algorithm (Knuth-Morris-Pratt) on the example input step by step
Final answer from example: see above
```
```java
public class KMPSearch {
    
    public int strStr(String haystack, String needle) {
        int n = haystack.length();
        int m = needle.length();
        
        if (m == 0) return 0;
        if (m > n) return -1;
        
        // Step 1: Build the LPS (failure function) array
        int[] lps = buildLPS(needle);
        
        // Step 2: Search using the LPS array
        int i = 0; // index in haystack
        int j = 0; // index in needle
        
        while (i < n) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;
                
                if (j == m) {
                    return i - m; // Found match at position (i - m)
                }
            } else {
                if (j > 0) {
                    // Use LPS to skip ahead in needle (don't move i)
                    j = lps[j - 1];
                } else {
                    // No match at all, advance haystack pointer
                    i++;
                }
            }
        }
        
        return -1;
    }
    
    private int[] buildLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        
        int len = 0; // length of the previous longest prefix suffix
        int i = 1;
        
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len > 0) {
                    // Fall back to the previous LPS value
                    len = lps[len - 1];
                    // Do NOT increment i here
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
}
```

### Dry Run of KMP

```
haystack = "ABABDABABAC", needle = "ABABAC"
LPS = [0, 0, 1, 2, 3, 0]

i=0, j=0: A==A → i=1, j=1
i=1, j=1: B==B → i=2, j=2
i=2, j=2: A==A → i=3, j=3
i=3, j=3: B==B → i=4, j=4
i=4, j=4: D≠A → j=lps[3]=2 (skip to position 2 in needle)
i=4, j=2: D≠A → j=lps[1]=0
i=4, j=0: D≠A → i=5 (advance haystack)
i=5, j=0: A==A → i=6, j=1
i=6, j=1: B==B → i=7, j=2
i=7, j=2: A==A → i=8, j=3
i=8, j=3: B==B → i=9, j=4
i=9, j=4: A==A → i=10, j=5
i=10, j=5: C==C → i=11, j=6
j==m → Match found at position 11-6 = 5!
```

### Why KMP Is O(n + m)

- Building the LPS array takes O(m).
- The search phase takes O(n) because the `i` pointer never moves backward, and the `j` pointer can only be reset via the LPS array a bounded number of times.
- Total: O(n + m).

---

## Approach 3: Rabin-Karp (Rolling Hash)

**Time:** O(n + m) average, O(n × m) worst case
**Space:** O(1)

### Core Idea

Compute a hash of the needle. Then slide a window of size `m` across the haystack, maintaining a rolling hash. When the hashes match, verify with a character-by-character comparison (to handle hash collisions).

### Complete Implementation


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: haystack="sadbutsad", needle="sad""]
    START --> BUILD["Build HashMap / Set"]
    BUILD --> SCAN["Scan input once"]
    SCAN --> LOOKUP{"Key seen?"}
    LOOKUP -->|yes| FOUND["Return match"]
    LOOKUP -->|no| STORE["Store in map"]
    STORE --> SCAN
```

**Walkthrough (same example):**

```
Example: haystack="sadbutsad", needle="sad" → 0
Approach: Rabin-Karp (Rolling Hash)

Scan input left-to-right
Store seen keys/values in hash map
O(1) lookup finds complement or group
```
```java
public class RabinKarp {
    
    public int strStr(String haystack, String needle) {
        int n = haystack.length();
        int m = needle.length();
        
        if (m == 0) return 0;
        if (m > n) return -1;
        
        long BASE = 26;
        long MOD = 1_000_000_007; // Large prime to reduce collisions
        
        // Compute hash of needle and first window of haystack
        long needleHash = 0;
        long windowHash = 0;
        long power = 1; // BASE^(m-1) mod MOD
        
        for (int i = 0; i < m; i++) {
            needleHash = (needleHash * BASE + (needle.charAt(i) - 'a')) % MOD;
            windowHash = (windowHash * BASE + (haystack.charAt(i) - 'a')) % MOD;
            if (i > 0) {
                power = (power * BASE) % MOD;
            }
        }
        
        // Slide the window
        for (int i = 0; i <= n - m; i++) {
            // Check if hashes match
            if (needleHash == windowHash) {
                // Verify character by character (handles collisions)
                if (haystack.substring(i, i + m).equals(needle)) {
                    return i;
                }
            }
            
            // Update rolling hash for next window
            if (i < n - m) {
                windowHash = (windowHash - (haystack.charAt(i) - 'a') * power % MOD + MOD) % MOD;
                windowHash = (windowHash * BASE + (haystack.charAt(i + m) - 'a')) % MOD;
            }
        }
        
        return -1;
    }
}
```

### How Rolling Hash Works

For a window "abc" with base 26:
```
hash("abc") = a*26² + b*26¹ + c*26⁰

To slide window from "abc" to "bcd":
1. Remove 'a': hash = hash - a*26²
2. Shift left: hash = hash * 26
3. Add 'd': hash = hash + d
```

### When Rabin-Karp Shines

- **Multiple pattern search**: When searching for multiple patterns simultaneously, Rabin-Karp with a hash set is very efficient.
- **2D pattern matching**: Rolling hashes extend naturally to 2D grids.

---

## Approach 4: Built-in Methods


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: haystack="sadbutsad", needle="sad""]
    START --> STEP1["Built-in Methods: step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: haystack="sadbutsad", needle="sad" → 0
Approach: Built-in Methods

Apply Built-in Methods on the example input step by step
Final answer from example: see above
```
```java
// Java
return haystack.indexOf(needle);

// Using contains
return haystack.contains(needle) ? haystack.indexOf(needle) : -1;
```

Mention this exists but always implement manually in an interview.

---

## Approach Comparison

| Approach | Preprocessing | Search Time | Space | Worst Case | Best For |
|----------|--------------|-------------|-------|-----------|----------|
| Brute Force | O(1) | O(n × m) | O(1) | O(n × m) | Short strings, interviews |
| KMP | O(m) | O(n) | O(m) | O(n + m) | Guaranteed linear time |
| Rabin-Karp | O(m) | O(n) avg | O(1) | O(n × m) | Multiple patterns |
| Built-in | — | O(n × m) | O(1) | O(n × m) | Production code |
| Boyer-Moore | O(m + σ) | O(n/m) avg | O(σ) | O(n × m) | Long patterns, large alphabets |

(σ = alphabet size)

---

## Advanced: Boyer-Moore (Brief Overview)

Boyer-Moore is often faster in practice than KMP because it can skip large portions of the haystack. It uses two heuristics:

1. **Bad Character Rule**: When a mismatch occurs, shift the pattern so the mismatched character in the haystack aligns with its last occurrence in the needle.
2. **Good Suffix Rule**: When a mismatch occurs after matching some suffix, shift the pattern based on the matching suffix's position within the pattern.

Boyer-Moore achieves O(n/m) average time (sublinear!) for long patterns with large alphabets, making it the algorithm of choice for text editors and grep.

---

## Common Mistakes

1. **Off-by-one in loop bound**: The outer loop should run from `0` to `n - m` (inclusive), not `n - 1` or `n`.
2. **Not handling empty needle**: An empty needle should return 0 (every string contains the empty string at position 0).
3. **Incorrect LPS construction**: The most common KMP bug is incrementing `i` when `len > 0` and there is a mismatch. You should NOT increment `i` in that case — only fall back `len`.
4. **Integer overflow in Rabin-Karp**: Without modular arithmetic, the hash value overflows quickly for long strings.
5. **Missing collision verification in Rabin-Karp**: Hash match does not guarantee string match. Always verify with character comparison.

---

## Edge Cases

| Case | Haystack | Needle | Expected |
|------|----------|--------|----------|
| Needle is empty | "abc" | "" | 0 |
| Needle longer than haystack | "ab" | "abc" | -1 |
| Exact match | "abc" | "abc" | 0 |
| Needle at the end | "abcdef" | "def" | 3 |
| No match | "aaaa" | "bb" | -1 |
| Single character match | "a" | "a" | 0 |
| Repeated pattern | "aaaaaa" | "aaa" | 0 |

---

## LeetCode Similar Problems

- [214. Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/) — KMP application
- [459. Repeated Substring Pattern](https://leetcode.com/problems/repeated-substring-pattern/) — KMP/LPS application
- [686. Repeated String Match](https://leetcode.com/problems/repeated-string-match/) — String matching variant
- [796. Rotate String](https://leetcode.com/problems/rotate-string/) — Concatenation + search
- [1392. Longest Happy Prefix](https://leetcode.com/problems/longest-happy-prefix/) — Direct LPS application

---

## Interview Tips

1. **Start with brute force**: Code it quickly and correctly. Most interviewers accept O(n × m) for this problem.
2. **Discuss KMP verbally**: You may not have time to code KMP in an interview, but explaining the concept (LPS array, no backtracking in the text pointer) shows depth.
3. **Know the complexity of Java's `indexOf`**: Java's `String.indexOf()` uses a brute force O(n × m) approach internally (not KMP), so there is no "cheating" by using it.
4. **Mention Rabin-Karp for follow-ups**: If the interviewer asks about searching for multiple patterns, pivot to Rabin-Karp with a hash set.
5. **For Lead-level**: Be prepared to discuss Boyer-Moore, the choice of string matching algorithm in production systems (grep, text editors, search engines), and the trade-offs between preprocessing time and search time.
#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: haystack="sadbutsad", needle="sad""]
    START --> STEP1["Approach 5: step 1"]
    STEP1 --> STEP2["Process data"]
    STEP2 --> STEP3["Update state"]
    STEP3 --> DONE["Return result"]
```

**Walkthrough (same example):**

```
Example: haystack="sadbutsad", needle="sad" → 0
Approach: Approach 5

Apply Approach 5 on the example input step by step
Final answer from example: see above
```

