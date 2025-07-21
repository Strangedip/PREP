# Longest Common Subsequence (LCS)

## Problem Statement
Given two strings `text1` and `text2`, return the **length of their longest common subsequence**. If there is no common subsequence, return `0`.

A **subsequence** of a string is a new string generated from the original string with some characters (can be none) deleted without changing the **relative order** of the remaining characters.

A **common subsequence** of two strings is a subsequence that is common to both strings.

**Examples:**
```
Input: text1 = "abcde", text2 = "ace"  
Output: 3
Explanation: The longest common subsequence is "ace" and its length is 3.

Input: text1 = "abc", text2 = "abc"
Output: 3  
Explanation: The longest common subsequence is "abc" and its length is 3.

Input: text1 = "abc", text2 = "def"
Output: 0
Explanation: There is no such common subsequence, so the result is 0.
```

## Problem Analysis

### Core Insight
This is a **classic 2D Dynamic Programming problem** where we build solutions for subproblems:
- **Optimal Substructure**: LCS of two strings depends on LCS of their prefixes
- **Overlapping Subproblems**: Same substring pairs are computed multiple times
- **Decision Points**: For each character pair, decide whether to include or skip

### Key Concepts
- **Subsequence vs Substring**: Subsequence doesn't need to be contiguous
- **Order preservation**: Relative order of characters must be maintained
- **Common**: Must appear in both strings in same relative positions

### Mathematical Foundation
- **LCS(i,j)**: Length of LCS between `text1[0...i-1]` and `text2[0...j-1]`
- **Base cases**: LCS with empty string is 0
- **Recurrence relation**:
  ```
  If text1[i-1] == text2[j-1]:
      LCS(i,j) = 1 + LCS(i-1,j-1)
  Else:
      LCS(i,j) = max(LCS(i-1,j), LCS(i,j-1))
  ```

## Approaches

### Approach 1: Recursive Solution (Brute Force)

#### Key Insight
**Explore all possibilities** by making include/exclude decisions for each character.

#### Algorithm
1. **Base case**: If either string is empty, LCS = 0
2. **Characters match**: Include character, solve for remaining parts
3. **Characters don't match**: Try skipping from either string, take maximum

#### Recurrence Relation
```java
LCS(i, j) = {
    0                           if i == 0 or j == 0
    1 + LCS(i-1, j-1)          if text1[i-1] == text2[j-1]  
    max(LCS(i-1,j), LCS(i,j-1)) otherwise
}
```

#### Time Complexity
- **O(2^(m+n))** - Exponential due to overlapping subproblems
- **Branching factor**: 2 at each recursive call
- **Depth**: m + n in worst case

#### Space Complexity
- **O(m + n)** - Recursion stack depth

```java
private int lcsRecursive(String text1, String text2, int i, int j) {
    if (i == text1.length() || j == text2.length()) {
        return 0;  // Base case
    }
    
    if (text1.charAt(i) == text2.charAt(j)) {
        return 1 + lcsRecursive(text1, text2, i + 1, j + 1);
    }
    
    return Math.max(lcsRecursive(text1, text2, i + 1, j),     // Skip text1[i]
                    lcsRecursive(text1, text2, i, j + 1));    // Skip text2[j]
}
```

### Approach 2: Memoization (Top-Down DP) ⭐

#### Key Insight
**Cache results** of subproblems to avoid recomputation.

#### Algorithm
1. **Memoization table**: `memo[i][j]` stores LCS length for `text1[i...]` and `text2[j...]`
2. **Check cache**: Return cached result if available
3. **Compute and cache**: Calculate result and store in memo table

#### Time Complexity
- **O(m × n)** - Each subproblem computed exactly once
- **State space**: m × n possible (i,j) pairs

#### Space Complexity
- **O(m × n)** for memoization table + **O(m + n)** recursion stack

```java
private int lcsMemo(String text1, String text2, int i, int j, Integer[][] memo) {
    if (i == text1.length() || j == text2.length()) return 0;
    
    if (memo[i][j] != null) return memo[i][j];  // Cache hit
    
    int result;
    if (text1.charAt(i) == text2.charAt(j)) {
        result = 1 + lcsMemo(text1, text2, i + 1, j + 1, memo);
    } else {
        result = Math.max(lcsMemo(text1, text2, i + 1, j, memo),
                         lcsMemo(text1, text2, i, j + 1, memo));
    }
    
    memo[i][j] = result;  // Cache result
    return result;
}
```

### Approach 3: Tabulation (Bottom-Up DP) ⭐ (Most Common)

#### Key Insight
**Build solution iteratively** from smaller subproblems to larger ones.

#### Algorithm
1. **DP table**: `dp[i][j]` = LCS length of `text1[0...i-1]` and `text2[0...j-1]`
2. **Initialize base cases**: `dp[0][j] = dp[i][0] = 0`
3. **Fill table**: Use recurrence relation to fill each cell
4. **Return result**: `dp[m][n]` contains the final answer

#### Time Complexity
- **O(m × n)** - Two nested loops

#### Space Complexity
- **O(m × n)** - 2D DP table

```java
public int longestCommonSubsequenceDP(String text1, String text2) {
    int m = text1.length(), n = text2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[i][j] = 1 + dp[i - 1][j - 1];  // Match found
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);  // Take max
            }
        }
    }
    
    return dp[m][n];
}
```

#### DP Table Visualization
```
        ""  a  c  e
    ""   0  0  0  0
    a    0  1  1  1
    b    0  1  1  1  
    c    0  1  2  2
    d    0  1  2  2
    e    0  1  2  3
```

### Approach 4: Space-Optimized DP ⭐ (Efficient)

#### Key Insight
**Only previous row needed** for computation, so use rolling arrays.

#### Algorithm
1. **Two arrays**: `prev` and `curr` to represent previous and current rows
2. **Compute row by row**: Fill current row using previous row
3. **Swap arrays**: Prepare for next iteration

#### Time Complexity
- **O(m × n)** - Same as 2D DP

#### Space Complexity
- **O(min(m, n))** - Use shorter string for columns

```java
public int longestCommonSubsequenceOptimized(String text1, String text2) {
    if (text1.length() < text2.length()) {
        return longestCommonSubsequenceOptimized(text2, text1);  // Optimize for shorter string
    }
    
    int m = text1.length(), n = text2.length();
    int[] prev = new int[n + 1];
    int[] curr = new int[n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                curr[j] = 1 + prev[j - 1];
            } else {
                curr[j] = Math.max(prev[j], curr[j - 1]);
            }
        }
        
        int[] temp = prev;  // Swap arrays
        prev = curr;
        curr = temp;
    }
    
    return prev[n];
}
```

### Approach 5: Single Array Optimization (Advanced)

#### Key Insight
**Use one array with careful update order** to achieve O(n) space.

#### Algorithm
1. **Single array**: `dp[j]` represents current computation
2. **Track diagonal**: Store `dp[i-1][j-1]` value in temporary variable
3. **Update in correct order**: Ensure dependencies are satisfied

#### Time Complexity
- **O(m × n)** - Same computation

#### Space Complexity
- **O(n)** - Single array

```java
public int longestCommonSubsequenceSingleRow(String text1, String text2) {
    int m = text1.length(), n = text2.length();
    int[] dp = new int[n + 1];
    
    for (int i = 1; i <= m; i++) {
        int prev = 0;  // Represents dp[i-1][j-1]
        for (int j = 1; j <= n; j++) {
            int temp = dp[j];  // Store dp[i-1][j] before updating
            
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[j] = 1 + prev;
            } else {
                dp[j] = Math.max(dp[j], dp[j - 1]);
            }
            
            prev = temp;  // Update for next iteration
        }
    }
    
    return dp[n];
}
```

## String Reconstruction

### Getting the Actual LCS String

#### Key Insight
**Backtrack through DP table** to reconstruct the actual subsequence.

#### Algorithm
1. **Build DP table**: Compute lengths first
2. **Start from bottom-right**: Begin at `dp[m][n]`
3. **Backtrack**: Follow the path that led to optimal solution
4. **Reconstruct**: Build LCS string in reverse order

```java
public String getLongestCommonSubsequence(String text1, String text2) {
    int m = text1.length(), n = text2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    // Build DP table (same as before)
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                dp[i][j] = 1 + dp[i - 1][j - 1];
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }
    
    // Backtrack to build LCS string
    StringBuilder lcs = new StringBuilder();
    int i = m, j = n;
    
    while (i > 0 && j > 0) {
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
            lcs.append(text1.charAt(i - 1));  // Part of LCS
            i--; j--;
        } else if (dp[i - 1][j] > dp[i][j - 1]) {
            i--;  // Came from above
        } else {
            j--;  // Came from left
        }
    }
    
    return lcs.reverse().toString();  // Built in reverse
}
```

## Common Variations & Applications

### 1. **Shortest Common Supersequence**
Find shortest string that contains both input strings as subsequences.
```java
public int shortestCommonSupersequence(String text1, String text2) {
    int lcsLength = longestCommonSubsequenceDP(text1, text2);
    return text1.length() + text2.length() - lcsLength;
}
```

### 2. **Edit Distance (Insert/Delete Only)**
Minimum operations to transform one string to another using only insertions and deletions.
```java
public int editDistanceUsingLCS(String text1, String text2) {
    int lcsLength = longestCommonSubsequenceDP(text1, text2);
    int deletions = text1.length() - lcsLength;
    int insertions = text2.length() - lcsLength;
    return deletions + insertions;
}
```

### 3. **Longest Palindromic Subsequence**
LCS of string with its reverse.
```java
public int longestPalindromicSubsequence(String s) {
    return longestCommonSubsequenceDP(s, new StringBuilder(s).reverse().toString());
}
```

### 4. **Multiple String LCS**
Extend to find LCS of multiple strings.

## Implementation Strategies

### Direction of Iteration

#### 1. **Index-based** (0 to length)
```java
// Using 0-based indexing with bounds checking
if (i == text1.length() || j == text2.length()) return 0;
```

#### 2. **Length-based** (1 to length+1)  
```java
// Using 1-based indexing with padding
int[][] dp = new int[m + 1][n + 1];  // Extra row/column for base case
```

### String Comparison Patterns
```java
// Pattern 1: Direct character comparison
if (text1.charAt(i - 1) == text2.charAt(j - 1))

// Pattern 2: Convert to char array for better performance
char[] chars1 = text1.toCharArray();
char[] chars2 = text2.toCharArray();
if (chars1[i - 1] == chars2[j - 1])
```

## Optimization Techniques

### 1. **Early Termination**
```java
// If one string is much longer, maximum LCS is length of shorter string
if (Math.abs(text1.length() - text2.length()) >= maxPossibleLCS) {
    return Math.min(text1.length(), text2.length());
}
```

### 2. **String Length Optimization**
```java
// Always iterate over longer string to minimize space
if (text1.length() < text2.length()) {
    return longestCommonSubsequence(text2, text1);
}
```

### 3. **Memory Access Patterns**
```java
// Process by rows for better cache locality
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        // Sequential memory access
    }
}
```

## Common Mistakes

1. **Index confusion**: Mixing 0-based and 1-based indexing
2. **Base case errors**: Wrong initialization of DP table boundaries  
3. **Character comparison**: Using wrong indices in string comparison
4. **Space optimization bugs**: Incorrect array swapping or diagonal tracking
5. **String reconstruction**: Wrong backtracking logic

## Edge Cases

1. **Empty strings**: One or both strings are empty
2. **No common characters**: LCS length = 0
3. **Identical strings**: LCS = either string
4. **Single character strings**: Simple case for verification
5. **Very long strings**: Performance and memory considerations

## Interview Tips

### Problem Recognition
- **"Longest"** + **"Common"** + **"Subsequence"** → Think LCS
- **Two sequences** + **Optimization** → 2D DP pattern
- **"Edit distance"** variations → Often related to LCS

### Approach Strategy
1. **Start with recursive**: Show understanding of problem structure
2. **Add memoization**: Demonstrate DP optimization
3. **Move to tabulation**: Show iterative DP mastery
4. **Optimize space**: Discuss practical considerations
5. **Handle variations**: String reconstruction, multiple strings

### Code Structure
```java
public int longestCommonSubsequence(String text1, String text2) {
    // 1. Handle edge cases
    if (text1.isEmpty() || text2.isEmpty()) return 0;
    
    // 2. Initialize DP table
    int m = text1.length(), n = text2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    // 3. Fill DP table using recurrence relation
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            // Apply recurrence relation
        }
    }
    
    // 4. Return result
    return dp[m][n];
}
```

## LeetCode Problems

### Core Problems:
- **[LeetCode 1143 - Longest Common Subsequence](https://leetcode.com/problems/longest-common-subsequence/)** ⭐ (This problem)
- **[LeetCode 516 - Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/)** (LCS with reverse)
- **[LeetCode 1092 - Shortest Common Supersequence](https://leetcode.com/problems/shortest-common-supersequence/)** (Direct application)

### Related DP String Problems:
- **[LeetCode 72 - Edit Distance](https://leetcode.com/problems/edit-distance/)** (Similar DP pattern)
- **[LeetCode 583 - Delete Operation for Two Strings](https://leetcode.com/problems/delete-operation-for-two-strings/)** (LCS-based solution)
- **[LeetCode 712 - Minimum ASCII Delete Sum](https://leetcode.com/problems/minimum-ascii-delete-sum-for-two-strings/)** (Weighted LCS)
- **[LeetCode 1312 - Minimum Insertion Steps to Make String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/)** (LCS variant)

### Advanced Variations:
- **[LeetCode 1035 - Uncrossed Lines](https://leetcode.com/problems/uncrossed-lines/)** (Disguised LCS)
- **[LeetCode 1458 - Max Dot Product of Two Subsequences](https://leetcode.com/problems/max-dot-product-of-two-subsequences/)** (Weighted LCS variant)

### Difficulty Progression:
1. **Start with**: LeetCode 1143 (LCS) - Learn the pattern
2. **Next try**: LeetCode 516 (Longest Palindromic Subsequence) - Apply to single string
3. **Advanced**: LeetCode 72 (Edit Distance) - More complex DP
4. **Expert**: LeetCode 1458 (Max Dot Product) - Weighted variations

## Complexity Analysis Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Recursive** | O(2^(m+n)) | O(m+n) | **Understanding problem** |
| **Memoization** | O(mn) | O(mn) | **Top-down thinking** |
| **Tabulation** | O(mn) | O(mn) | **Interview standard** |
| **Space Optimized** | O(mn) | O(min(m,n)) | **Memory constraints** |
| **Single Array** | O(mn) | O(n) | **Extreme optimization** |

## Real-World Applications

1. **Version control**: Finding common changes between file versions
2. **Bioinformatics**: DNA/protein sequence alignment
3. **File comparison**: Tools like `diff` use LCS-based algorithms
4. **Data synchronization**: Identifying common elements for sync
5. **Text analysis**: Finding similar patterns in documents
6. **Code plagiarism detection**: Identifying common code structures

## Mathematical Insights

### LCS Properties
- **Optimal Substructure**: LCS exhibits optimal substructure property
- **Overlapping Subproblems**: Many subproblems are repeated
- **Monotonicity**: LCS length is non-decreasing with string length
- **Symmetry**: LCS(A,B) = LCS(B,A)

### Bounds and Limits
- **Maximum LCS**: min(|text1|, |text2|)
- **Minimum LCS**: 0 (when no common characters)
- **Time lower bound**: Ω(mn) for general strings
- **Space lower bound**: Ω(min(m,n)) for practical algorithms

**Remember**: LCS is the **foundation of many string DP problems** and demonstrates the power of **2D dynamic programming** for solving optimization problems involving two sequences! 