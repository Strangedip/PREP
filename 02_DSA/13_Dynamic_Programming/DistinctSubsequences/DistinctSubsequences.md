# Distinct Subsequences — LeetCode 115

> **You are here**: Staff Engineer — DSA (DP counting)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md#staff-engineer) | **Prerequisites**: [Longest Common Subsequence](../LongestCommonSubsequence/LongestCommonSubsequence.md) | **Next**: [Cherry Pickup](../CherryPickup/CherryPickup.md)
> **Pattern**: [Dynamic Programming](../../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-16-dynamic-programming-patterns) | **Catalog**: [Algorithmic Patterns](../../../03_CodingPatterns/02_AlgorithmicPatterns.md)

## Problem Statement

Given two strings `s` and `t`, return the number of **distinct subsequences** of `s` which equal `t`.

The test cases are generated so that the answer fits on a 32-bit signed integer.

A **subsequence** of a string is formed by deleting some (possibly zero) characters without changing the order of remaining characters. Two subsequences are **distinct** if the sets of indices deleted differ.

**Examples:**
```
Input: s = "rabbbit", t = "rabbit"
Output: 3
Explanation:
  s = "rabbbit"
  t = "rabbit"
  Three ways to form "rabbit":
    - ra_bbit  (delete index 2)
    - rab_bbit (delete index 3)
    - rabb_bit (delete index 4)

Input: s = "babgbag", t = "bag"
Output: 5
Explanation:
  Five distinct ways to pick indices from s forming "bag":
    - ba_gb_ag  → indices (0,1,3,5,6) ... various combinations
    - babg_ag, ba_gbag, babgbag paths through the string

Input: s = "", t = "a"
Output: 0
Explanation: Cannot form non-empty t from empty s.

Input: s = "a", t = ""
Output: 1
Explanation: Empty t is a subsequence of any string (delete all chars).
```

## Problem Analysis

### Core Insight

This is a **counting** variant of [Longest Common Subsequence](../LongestCommonSubsequence/LongestCommonSubsequence.md). Instead of maximizing length, we count the number of ways to match `t` as a subsequence of `s`.

At each position in `s`, we have two choices:
1. **Skip** the current character
2. **Take** it (only if it matches the current character needed in `t`)

### Key Concepts

- **2D DP**: `dp[i][j]` = number of ways to form `t[0..j)` from `s[0..i)`
- **Base case**: `dp[i][0] = 1` for all `i` — empty `t` is always formable (delete everything)
- **Overflow awareness**: Answer can be large; use `long` internally
- **Distinct by index**: `"aab"` → `"ab"` has 2 ways (pick first or second `a`)

### Recurrence

```
dp[i][j] = dp[i-1][j]                           // skip s[i-1]
if s[i-1] == t[j-1]:
    dp[i][j] += dp[i-1][j-1]                    // take s[i-1]
```

## Approaches

### Approach 1: 2D Bottom-Up DP ⭐⭐ (Standard)

#### Key Insight

Fill the table row by row. Each cell depends only on the cell above and the cell diagonally above-left.

#### Algorithm

1. Create `dp[m+1][n+1]` where `m = s.length()`, `n = t.length()`
2. Base: `dp[i][0] = 1` for all `i` (empty target)
3. For `i` from 1 to m, `j` from 1 to n:
   - Start with `dp[i][j] = dp[i-1][j]` (skip)
   - If `s[i-1] == t[j-1]`: add `dp[i-1][j-1]` (take)
4. Return `dp[m][n]`


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="rabbbit", t="rabbit""]
    START --> INIT["Init DP table / memo"]
    INIT --> FILL["Fill states in order"]
    FILL --> TRANS["Apply transition"]
    TRANS --> MORE{"More states?"}
    MORE -->|yes| FILL
    MORE -->|no| DONE["Return dp[target]"]
```

**Walkthrough (same example):**

```
Example: s="rabbbit", t="rabbit" → 3
Approach: 2D Bottom-Up DP

Define subproblem table
Fill base cases
Apply recurrence to reach target state
```

#### Time Complexity

- **O(m × n)** where m = |s|, n = |t|

#### Space Complexity

- **O(m × n)** for the DP table

```java
public int numDistinct(String s, String t) {
    int m = s.length(), n = t.length();
    long[][] dp = new long[m + 1][n + 1];
    for (int i = 0; i <= m; i++) dp[i][0] = 1;

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            dp[i][j] = dp[i - 1][j];
            if (s.charAt(i - 1) == t.charAt(j - 1)) {
                dp[i][j] += dp[i - 1][j - 1];
            }
        }
    }
    return (int) dp[m][n];
}
```

### Approach 2: Space-Optimized 1D DP ⭐

#### Key Insight

Each row only depends on the previous row. Use a single 1D array and iterate `j` in **reverse** to avoid overwriting values still needed.

#### Algorithm

1. `dp[j]` = ways to form `t[0..j)` from processed prefix of `s`
2. Initialize `dp[0] = 1`
3. For each character in `s`, update `dp` from right to left


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="rabbbit", t="rabbit""]
    START --> INIT["Init DP table / memo"]
    INIT --> FILL["Fill states in order"]
    FILL --> TRANS["Apply transition"]
    TRANS --> MORE{"More states?"}
    MORE -->|yes| FILL
    MORE -->|no| DONE["Return dp[target]"]
```

**Walkthrough (same example):**

```
Example: s="rabbbit", t="rabbit" → 3
Approach: Space-Optimized 1D DP

Define subproblem table
Fill base cases
Apply recurrence to reach target state
```

#### Time Complexity

- **O(m × n)**

#### Space Complexity

- **O(n)** — only one row needed

```java
public int numDistinctOptimized(String s, String t) {
    int m = s.length(), n = t.length();
    long[] dp = new long[n + 1];
    dp[0] = 1;

    for (int i = 1; i <= m; i++) {
        for (int j = n; j >= 1; j--) {
            if (s.charAt(i - 1) == t.charAt(j - 1)) {
                dp[j] += dp[j - 1];
            }
        }
    }
    return (int) dp[n];
}
```

### Approach 3: Top-Down Memoization ⭐

#### Key Insight

Recursive formulation: `solve(i, j)` = ways to form `t[j..]` from `s[i..]`.

#### Recurrence

```
solve(i, j) = solve(i+1, j)                    // skip s[i]
            + (s[i]==t[j] ? solve(i+1, j+1) : 0)  // take if match
Base: j == t.length → 1, i == s.length && j < t.length → 0
```


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="rabbbit", t="rabbit""]
    START --> VISIT["Visit current state"]
    VISIT --> CHOICE{"More choices?"}
    CHOICE -->|yes| RECUR["Recurse / backtrack"]
    RECUR --> UNDO["Undo choice"]
    UNDO --> CHOICE
    CHOICE -->|no| DONE["Return / collect result"]
```

**Walkthrough (same example):**

```
Example: s="rabbbit", t="rabbit" → 3
Approach: Top-Down Memoization

Visit current node/state
Recurse on valid next choices
Backtrack and try alternatives
```

#### Time Complexity

- **O(m × n)**

#### Space Complexity

- **O(m × n)** memo + **O(m)** recursion stack

## Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| 2D DP | O(mn) | O(mn) | Easy to trace and debug | Extra space |
| 1D Optimized | O(mn) | O(n) | Space efficient | Reverse iteration tricky |
| Memoization | O(mn) | O(mn) | Natural recursive thinking | Stack depth for long strings |

## Example Traces

### Example 1: `s = "rabbbit"`, `t = "rabbit"`

```
     ""  r  a  b  b  i  t
""    1  1  1  1  1  1  1
r     1  1  1  1  1  1  1
a     1  1  2  2  2  2  2
b     1  1  2  3  3  3  3
b     1  1  2  3  4  4  4
b     1  1  2  3  4  4  4
i     1  1  2  3  4  4  5
t     1  1  2  3  4  4  5

Answer: dp[7][6] = 3
```

Key moment: at `s[3]='b'`, `dp[4][4]` jumps from 3 to 4 because we can match the second `b` in `t` using either the second or third `b` in `s`.

### Example 2: `s = "aaa"`, `t = "aa"`

```
Each 'a' in s can be chosen or skipped independently for each position in t.
Ways to pick 2 'a's from 3: C(3,2) = 3

     ""  a  a
""    1  1  1
a     1  1  2
a     1  1  3
a     1  1  3

Answer: 3
```

## Edge Cases

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Empty target | `s="abc", t=""` | 1 | Delete all chars of s |
| Empty source | `s="", t="a"` | 0 | Impossible |
| No match possible | `s="abc", t="def"` | 0 | No shared characters |
| Identical strings | `s="abc", t="abc"` | 1 | Exactly one way |
| Repeated chars | `s="aaa", t="aa"` | 3 | Combinatorial counting |
| t longer than s | `s="ab", t="abc"` | 0 | Impossible |
| Large counts | long strings | up to 2³¹-1 | Use `long` internally |

## Key Insights

### vs Longest Common Subsequence

| | LCS | Distinct Subsequences |
|---|-----|----------------------|
| Goal | Maximize length | Count matchings |
| Recurrence | `max(left, top, diag+1)` | `top + diag` (sum, not max) |
| Base case | `dp[i][0] = dp[0][j] = 0` | `dp[i][0] = 1` |

### Why `dp[i][0] = 1`?

The empty string `t=""` is always a valid subsequence — just delete all characters from any prefix of `s`. This is the critical base-case difference from LCS.

### Combinatorial Interpretation

When `s` has repeated characters matching `t`, we're essentially counting ways to choose which occurrence of each character to use — similar to combinations with repetition.

## Interview Tips

1. **Connect to LCS immediately**: "This is like LCS but we count instead of maximize."
2. **Explain distinctness**: Two subsequences differ if the index sets differ, not the resulting string.
3. **State the base case clearly**: `dp[i][0] = 1` is the most common mistake point.
4. **Mention overflow**: Use `long` for intermediate values even though answer fits in `int`.
5. **Offer space optimization**: Shows DP mastery beyond the basic table.

## Common Mistakes

1. **Wrong base case**: Setting `dp[i][0] = 0` instead of `1`.
2. **Using max instead of sum**: Copying LCS recurrence literally.
3. **Forward iteration in 1D optimization**: Overwrites `dp[j-1]` before it's used.
4. **Off-by-one in indices**: `s.charAt(i-1)` when `i` is 1-indexed in DP.
5. **Not handling `t` longer than `s`**: Should return 0, which DP handles naturally.

## Applications

- **DNA sequence matching** — counting how many ways a gene pattern appears
- **Plagiarism detection** — multiple embedded copies of a phrase
- **Version diff analysis** — counting distinct alignments between texts
- **Follow-up**: [Distinct Subsequences II (LeetCode 940)](https://leetcode.com/problems/distinct-subsequences-ii/) — count all distinct subsequences of a single string

**Code**: [DistinctSubsequences.java](DistinctSubsequences.java)
