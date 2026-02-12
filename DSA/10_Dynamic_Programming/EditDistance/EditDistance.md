# Edit Distance (Levenshtein Distance)

## Problem Statement

Given two strings `word1` and `word2`, return the minimum number of operations required to convert `word1` to `word2`. You have the following three operations permitted on a word:
- **Insert** a character
- **Delete** a character
- **Replace** a character

**LeetCode**: [72. Edit Distance](https://leetcode.com/problems/edit-distance/)

### Examples

```
Input:  word1 = "horse", word2 = "ros"
Output: 3
Explanation: 
  horse → rorse (replace 'h' with 'r')
  rorse → rose  (delete 'r')
  rose  → ros   (delete 'e')

Input:  word1 = "intention", word2 = "execution"
Output: 5
Explanation:
  intention → inention  (delete 't')
  inention  → enention  (replace 'i' with 'e')
  enention  → exention  (replace 'n' with 'x')
  exention  → exection  (replace 'n' with 'c')
  exection  → execution (insert 'u')
```

### Constraints
- `0 <= word1.length, word2.length <= 500`
- `word1` and `word2` consist of lowercase English letters

---

## Why This Problem Is Important

Edit Distance is one of the **most important DP problems** in computer science:
1. It is the canonical 2D DP problem that tests your ability to define states, transitions, and base cases.
2. It has real-world applications: spell checkers, DNA sequence alignment, diff algorithms, fuzzy string matching, and NLP.
3. It appears frequently in FAANG interviews, especially at Google and Amazon.
4. It demonstrates the three fundamental DP operations: insert, delete, replace — which map to moving in three directions in the DP table.

---

## Approach 1: Recursion with Memoization (Top-Down DP)

**Time**: O(m × n), **Space**: O(m × n)

### Recursive Intuition

Compare characters from the end of both strings:
- If `word1[i] == word2[j]`, no operation needed — move both pointers: `dp(i-1, j-1)`.
- If they differ, try all three operations and take the minimum:
  - **Replace**: Change `word1[i]` to `word2[j]`, then solve `dp(i-1, j-1)`.
  - **Delete**: Delete `word1[i]`, then solve `dp(i-1, j)`.
  - **Insert**: Insert `word2[j]` into word1, then solve `dp(i, j-1)`.

```java
class Solution {
    private int[][] memo;

    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();
        memo = new int[m][n];
        for (int[] row : memo) Arrays.fill(row, -1);
        return dp(word1, word2, m - 1, n - 1);
    }

    private int dp(String w1, String w2, int i, int j) {
        // Base cases
        if (i < 0) return j + 1; // Insert remaining chars of word2
        if (j < 0) return i + 1; // Delete remaining chars of word1

        if (memo[i][j] != -1) return memo[i][j];

        if (w1.charAt(i) == w2.charAt(j)) {
            memo[i][j] = dp(w1, w2, i - 1, j - 1); // Characters match
        } else {
            int replace = dp(w1, w2, i - 1, j - 1); // Replace w1[i] with w2[j]
            int delete  = dp(w1, w2, i - 1, j);     // Delete w1[i]
            int insert  = dp(w1, w2, i, j - 1);     // Insert w2[j] into w1
            memo[i][j] = 1 + Math.min(replace, Math.min(delete, insert));
        }

        return memo[i][j];
    }
}
```

---

## Approach 2: Bottom-Up DP (Tabulation) — Optimal

**Time**: O(m × n), **Space**: O(m × n)

### DP State Definition

`dp[i][j]` = minimum edit distance between `word1[0..i-1]` and `word2[0..j-1]`.

### Base Cases

- `dp[0][j] = j` — Converting empty string to `word2[0..j-1]` requires j insertions.
- `dp[i][0] = i` — Converting `word1[0..i-1]` to empty string requires i deletions.

### Transition

```
If word1[i-1] == word2[j-1]:
    dp[i][j] = dp[i-1][j-1]        // No operation needed
Else:
    dp[i][j] = 1 + min(
        dp[i-1][j-1],              // Replace
        dp[i-1][j],                // Delete from word1
        dp[i][j-1]                 // Insert into word1
    )
```

### Visual Walkthrough: "horse" → "ros"

```
        ""   r    o    s
    ""   0   1    2    3
    h    1   1    2    3
    o    2   2    1    2
    r    3   2    2    2
    s    4   3    3    2
    e    5   4    4    3

Reading dp[5][3] = 3 → Answer is 3 operations
```

How to read the table:
- `dp[1][1] = 1`: "h" → "r" needs 1 replace
- `dp[2][2] = 1`: "ho" → "ro" needs 1 replace (h→r)
- `dp[3][2] = 2`: "hor" → "ro" needs 2 operations
- `dp[5][3] = 3`: "horse" → "ros" needs 3 operations ✓

### Java Implementation

```java
class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();

        // dp[i][j] = edit distance between word1[0..i-1] and word2[0..j-1]
        int[][] dp = new int[m + 1][n + 1];

        // Base case: converting to/from empty string
        for (int i = 0; i <= m; i++) dp[i][0] = i; // Delete all chars
        for (int j = 0; j <= n; j++) dp[0][j] = j; // Insert all chars

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // Characters match, no cost
                } else {
                    dp[i][j] = 1 + Math.min(
                        dp[i - 1][j - 1], // Replace
                        Math.min(
                            dp[i - 1][j],  // Delete
                            dp[i][j - 1]   // Insert
                        )
                    );
                }
            }
        }

        return dp[m][n];
    }
}
```

---

## Approach 3: Space-Optimized DP

**Time**: O(m × n), **Space**: O(min(m, n))

Since each row only depends on the current and previous row, we can use two 1D arrays.

```java
class Solution {
    public int minDistance(String word1, String word2) {
        int m = word1.length(), n = word2.length();

        // Ensure we use the shorter string for columns (space optimization)
        if (m < n) return minDistance(word2, word1);

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        // Base case: empty word1
        for (int j = 0; j <= n; j++) prev[j] = j;

        for (int i = 1; i <= m; i++) {
            curr[0] = i; // Base case: empty word2

            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = 1 + Math.min(prev[j - 1], Math.min(prev[j], curr[j - 1]));
                }
            }

            // Swap prev and curr
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }
}
```

---

## Understanding the Three Operations in the DP Table

This is the most important conceptual understanding for the interview:

```
dp[i-1][j-1]  →  dp[i-1][j]
     ↓               ↓
dp[i][j-1]    →  dp[i][j]
```

| Direction | Operation | Meaning |
|-----------|-----------|---------|
| Diagonal (i-1, j-1) → (i, j) | **Replace** | Replace word1[i-1] with word2[j-1], then solve the smaller problem |
| Up (i-1, j) → (i, j) | **Delete** | Delete word1[i-1], then convert remaining word1[0..i-2] to word2[0..j-1] |
| Left (i, j-1) → (i, j) | **Insert** | Insert word2[j-1] into word1, then convert word1[0..i-1] to word2[0..j-2] |

---

## Complexity Analysis

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Recursion (no memo) | O(3^max(m,n)) | O(max(m,n)) | Exponential — TLE |
| Top-Down DP | O(m × n) | O(m × n) | Memoization |
| Bottom-Up DP | O(m × n) | O(m × n) | Tabulation — preferred |
| Space-Optimized | O(m × n) | O(min(m, n)) | Best space |

Where m = length of word1, n = length of word2.

---

## Edge Cases

| Case | word1 | word2 | Output | Why |
|------|-------|-------|--------|-----|
| Both empty | `""` | `""` | `0` | No operations needed |
| One empty | `""` | `"abc"` | `3` | Insert 3 characters |
| One empty | `"abc"` | `""` | `3` | Delete 3 characters |
| Same strings | `"abc"` | `"abc"` | `0` | Already equal |
| Single char diff | `"a"` | `"b"` | `1` | One replacement |
| Completely different | `"abc"` | `"xyz"` | `3` | Replace all three |
| Anagrams | `"abc"` | `"bca"` | `2` | Anagrams are not free — still need operations |

---

## Interview Tips

1. **Start with the recursive intuition**: "I compare characters from the end. If they match, move both pointers. If not, I try all three operations and take the minimum."
2. **Define the DP state precisely**: "`dp[i][j]` is the minimum edit distance between the first i characters of word1 and the first j characters of word2."
3. **Draw the DP table for a small example**: This shows strong communication and helps catch errors.
4. **Explain the three directions**: Replace = diagonal, Delete = up, Insert = left.
5. **Mention space optimization**: Shows maturity. "I can reduce space to O(n) since each row only depends on the previous row."

### Common Follow-Up Questions
- "Can you reconstruct the actual sequence of operations?" — Backtrack through the DP table from `dp[m][n]` to `dp[0][0]`.
- "What if some operations have different costs?" — Modify the transition: `cost_replace * (chars differ) + dp[i-1][j-1]`, and similar for insert/delete.
- "What about the One Edit Distance problem?" — [LeetCode 161](https://leetcode.com/problems/one-edit-distance/) — check if exactly one operation converts word1 to word2.

---

## Real-World Applications

| Application | How Edit Distance Is Used |
|-------------|--------------------------|
| **Spell Checker** | Find words in dictionary with smallest edit distance to the misspelled word |
| **DNA Sequence Alignment** | Align two DNA sequences by minimizing mutations (substitution, insertion, deletion) |
| **Diff Algorithms** | Git diff, file comparison tools compute edit operations between file versions |
| **Fuzzy String Matching** | Search engines and databases use edit distance for approximate string matching |
| **Natural Language Processing** | Auto-correct, text similarity, machine translation evaluation (BLEU score) |
| **Plagiarism Detection** | Measure similarity between documents using string distance metrics |

---

**Pattern**: 2D Dynamic Programming (String DP)
**Difficulty**: Medium-Hard
**Must-Know**: Yes — canonical DP problem, tests fundamental DP skills

