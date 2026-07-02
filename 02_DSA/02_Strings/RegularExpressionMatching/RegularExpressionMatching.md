# Regular Expression Matching — LeetCode 10

> **You are here**: Staff Engineer — DSA (string DP)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md#staff-engineer) | **Prerequisites**: [Edit Distance](../../13_Dynamic_Programming/EditDistance/EditDistance.md) | **Next**: [Implement strStr](../ImplementStrStr/ImplementStrStr.md)
> **Pattern**: [Dynamic Programming](../../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-16-dynamic-programming-patterns) | **Catalog**: [Algorithmic Patterns](../../../03_CodingPatterns/02_AlgorithmicPatterns.md)

## Problem Statement

Given an input string `s` and a pattern `p`, implement regular expression matching with support for:

- `'.'` — Matches any single character
- `'*'` — Matches **zero or more** of the **preceding** element

The matching should cover the **entire** input string (not partial).

**Examples:**
```
Input: s = "aa", p = "a"
Output: false
Explanation: "a" does not match the entire string "aa".

Input: s = "aa", p = "a*"
Output: true
Explanation: "*" means zero or more 'a's. "aa" matches "a*" (two a's).

Input: s = "ab", p = ".*"
Output: true
Explanation: ".*" means zero or more of any character. Matches "ab".

Input: s = "aab", p = "c*a*b"
Output: true
Explanation: c* → zero c's, a* → two a's, b → one b. Matches "aab".

Input: s = "mississippi", p = "mis*is*p*."
Output: false
Explanation: Cannot match entire string.
```

## Problem Analysis

### Core Insight

This is a **2D string DP** problem similar to [Edit Distance](../../13_Dynamic_Programming/EditDistance/EditDistance.md), but with regex-specific rules. The `*` operator always applies to the **preceding** character in the pattern, never standalone.

### Key Concepts

- **State**: `dp[i][j]` = does `s[0..i)` match `p[0..j)`?
- **Two cases for each cell**: current pattern char is plain/`.` or is `*`
- **Star has two choices**: match zero occurrences OR match one+ (if preceding char matches)
- **Empty pattern matching**: `p = "a*b*c*"` can match empty string `s = ""`

### Pattern Structure

Because `*` always pairs with the preceding element, we process the pattern in pairs:
- `p[j-1] == '*'` → look at `p[j-2]` as the repeated character
- Valid pattern lengths for non-trivial cases always have even structure with stars

## Approaches

### Approach 1: 2D Bottom-Up DP ⭐⭐ (Standard)

#### Key Insight

Build the table incrementally. Handle `*` by considering zero occurrences (`dp[i][j-2]`) or one+ occurrences (`dp[i-1][j]` if char matches).

#### Recurrence

```
If p[j-1] != '*':
    dp[i][j] = dp[i-1][j-1] && (p[j-1]=='.' || s[i-1]==p[j-1])

If p[j-1] == '*':
    // Zero occurrences of preceding char
    dp[i][j] = dp[i][j-2]

    // One or more occurrences (if preceding char matches s[i-1])
    if p[j-2]=='.' || p[j-2]==s[i-1]:
        dp[i][j] |= dp[i-1][j]
```

#### Base Cases

```
dp[0][0] = true  (empty matches empty)
dp[0][j] = true if p[0..j) is all x* pairs (e.g., "a*b*c*")
```


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="aab", p="c*a*b""]
    START --> INIT["Init DP table / memo"]
    INIT --> FILL["Fill states in order"]
    FILL --> TRANS["Apply transition"]
    TRANS --> MORE{"More states?"}
    MORE -->|yes| FILL
    MORE -->|no| DONE["Return dp[target]"]
```

**Walkthrough (same example):**

```
Example: s="aab", p="c*a*b" → true
Approach: 2D Bottom-Up DP

Define subproblem table
Fill base cases
Apply recurrence to reach target state
```

#### Time Complexity

- **O(m × n)** where m = |s|, n = |p|

#### Space Complexity

- **O(m × n)** for the DP table

```java
public boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m + 1][n + 1];
    dp[0][0] = true;

    // Empty string matches patterns like a*b*c*
    for (int j = 2; j <= n; j++) {
        if (p.charAt(j - 1) == '*') dp[0][j] = dp[0][j - 2];
    }

    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            char sc = s.charAt(i - 1), pc = p.charAt(j - 1);
            if (pc != '*') {
                dp[i][j] = dp[i-1][j-1] && (pc == '.' || sc == pc);
            } else {
                char prev = p.charAt(j - 2);
                dp[i][j] = dp[i][j - 2]; // zero occurrences
                if (prev == '.' || prev == sc) {
                    dp[i][j] |= dp[i - 1][j]; // one or more
                }
            }
        }
    }
    return dp[m][n];
}
```

### Approach 2: Top-Down Memoization ⭐

#### Key Insight

Recursive with memo: `match(i, j)` = does `s[i..]` match `p[j..]`?

#### Algorithm

1. Base: if `j == p.length()`, return `i == s.length()`
2. Check if next char is `*` (lookahead)
3. If star: try zero matches (skip `x*`) or one+ matches (advance `i` if char matches)
4. If not star: require char match and recurse on both `i+1, j+1`


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="aab", p="c*a*b""]
    START --> VISIT["Visit current state"]
    VISIT --> CHOICE{"More choices?"}
    CHOICE -->|yes| RECUR["Recurse / backtrack"]
    RECUR --> UNDO["Undo choice"]
    UNDO --> CHOICE
    CHOICE -->|no| DONE["Return / collect result"]
```

**Walkthrough (same example):**

```
Example: s="aab", p="c*a*b" → true
Approach: Top-Down Memoization

Visit current node/state
Recurse on valid next choices
Backtrack and try alternatives
```

#### Time Complexity

- **O(m × n)**

#### Space Complexity

- **O(m × n)** memo + **O(m+n)** recursion stack

```java
private Boolean dfs(int i, int j, String s, String p, Boolean[][] memo) {
    if (j == p.length()) return i == s.length();
    if (memo[i][j] != null) return memo[i][j];

    boolean result;
    boolean firstMatch = i < s.length() &&
        (p.charAt(j) == '.' || p.charAt(j) == s.charAt(i));

    if (j + 1 < p.length() && p.charAt(j + 1) == '*') {
        result = dfs(i, j + 2, s, p, memo)           // zero
              || (firstMatch && dfs(i + 1, j, s, p, memo)); // one+
    } else {
        result = firstMatch && dfs(i + 1, j + 1, s, p, memo);
    }
    return memo[i][j] = result;
}
```

### Approach 3: Space-Optimized 1D DP ⭐

#### Key Insight

Only the previous row is needed. Iterate `j` in reverse to preserve dependencies.


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: s="aab", p="c*a*b""]
    START --> INIT["Init DP table / memo"]
    INIT --> FILL["Fill states in order"]
    FILL --> TRANS["Apply transition"]
    TRANS --> MORE{"More states?"}
    MORE -->|yes| FILL
    MORE -->|no| DONE["Return dp[target]"]
```

**Walkthrough (same example):**

```
Example: s="aab", p="c*a*b" → true
Approach: Space-Optimized 1D DP

Define subproblem table
Fill base cases
Apply recurrence to reach target state
```

#### Time Complexity

- **O(m × n)**

#### Space Complexity

- **O(n)**

Useful when space is constrained; same logic as 2D but with one row.

## Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| 2D DP | O(mn) | O(mn) | Clear table for debugging | Extra space |
| Memoization | O(mn) | O(mn) | Natural recursive structure | Stack overflow risk |
| 1D Optimized | O(mn) | O(n) | Space efficient | Harder to implement correctly |

## Example Traces

### Example 1: `s = "aab"`, `p = "c*a*b"`

```
Pattern breakdown: c* (zero c's) + a* (two a's) + b (one b)

     ""  c  c*  a  a*  b
""    T   F   T   F   T   F
a     F   F   T   T   T   F
a     F   F   T   F   T   F
b     F   F   F   F   F   T

dp[3][6] = true
```

Trace `dp[2][5]` (s="aa", p="c*a*"):
- `*` at j=5: zero → dp[2][3]; one+ → dp[1][5] if 'a' matches 'a'
- Eventually chains back to c* matching zero c's

### Example 2: `s = "aa"`, `p = "a*"`

```
     ""  a  a*
""    T   F  T
a     F   T  T
a     F   F  T

dp[0][2] = true (a* matches zero a's → empty)
dp[1][2] = true (a* matches one a)
dp[2][2] = true (a* matches two a's)
```

### Example 3: `s = "ab"`, `p = ".*"`

```
     ""  .  .*
""    T   F  T
a     F   T  T
b     F   F  T

.* matches any sequence: "ab" matches
```

## Edge Cases

| Case | Input | Expected | Notes |
|------|-------|----------|-------|
| Empty string, empty pattern | `s="", p=""` | true | Base case |
| Empty string, star pattern | `s="", p="a*b*"` | true | Stars match zero |
| Pattern longer than string | `s="a", p="ab"` | false | Must match entire s |
| Only dots | `s="abc", p="..."` | true | Each dot matches one char |
| Star with dot | `s="anything", p=".*"` | true | Classic wildcard |
| Consecutive stars | `s="aaa", p="a*a*"` | true | Redundant but valid |
| No match | `s="aaa", p="ab"` | false | Pattern too short |

## Key Insights

### Why `dp[i][j-2]` for Zero Occurrences?

`x*` in the pattern occupies two positions: `x` and `*`. To skip the entire `x*` unit, we jump back two positions in the pattern index.

### Why `dp[i-1][j]` for One+ Occurrences?

When matching one or more, we consume one character from `s` (move `i` back by 1) but keep the same pattern position `j` because `*` can still match more.

### The Dot-Star Combo `.*`

`.*` is the regex "wildcard" — matches any string (including empty). In DP: `dp[i][j] = dp[i][j-2] | dp[i-1][j]` always succeeds for one+ when `p[j-2]=='.'`.

### Relation to Wildcard Matching (LeetCode 44)

| | Regex (LC 10) | Wildcard (LC 44) |
|---|---------------|------------------|
| `*` meaning | Zero+ of **preceding** char | Zero+ of **any** char |
| `.` meaning | Any single char | N/A |
| `?` | N/A | Any single char |
| Difficulty | Harder (paired star) | Medium |

## Interview Tips

1. **Clarify star semantics**: `*` always modifies the **preceding** element, never standalone.
2. **Draw the DP table** for `s="aab", p="c*a*b"` — classic example that tests star logic.
3. **Handle empty string base case**: Initialize row 0 for patterns like `a*b*`.
4. **Start with recursion**, then memoize, then bottom-up — shows progressive refinement.
5. **Mention follow-ups**: Wildcard Matching (LC 44), Regex with `+` operator.

## Common Mistakes

1. **Treating `*` as standalone wildcard** (that's LC 44, not LC 10).
2. **Wrong zero-match jump**: Using `j-1` instead of `j-2` for skipping `x*`.
3. **Forgetting `dp[0][j]` initialization** for patterns matching empty string.
4. **Not checking `i > 0`** before accessing `s[i-1]` in one+ match case.
5. **Using `||` vs `&&` incorrectly** between dot and char comparison.

## Applications

- **Text editors and IDEs** — find/replace with regex
- **Log parsing** — pattern matching in observability pipelines
- **Input validation** — email, phone number patterns
- **Foundation for** [Wildcard Matching (LeetCode 44)](https://leetcode.com/problems/wildcard-matching/) and full regex engines

## Related

- [Edit Distance](../../13_Dynamic_Programming/EditDistance/EditDistance.md)
- [Implement strStr](../ImplementStrStr/ImplementStrStr.md)
- [Tier3 Differentiators](../../Tier3_Differentiators.md)

**Code**: [RegularExpressionMatching.java](RegularExpressionMatching.java)
