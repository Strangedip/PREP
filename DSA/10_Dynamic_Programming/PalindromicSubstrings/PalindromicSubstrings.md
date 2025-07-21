# Palindromic Substrings

## Problem Statement
Given a string `s`, return the **number of palindromic substrings** in it.

A string is a **palindrome** when it reads the same backward as forward. A **substring** is a contiguous sequence of characters within the string.

**Examples:**
```
Input: s = "abc"
Output: 3
Explanation: Three palindromic strings: "a", "b", "c".

Input: s = "aaa"
Output: 6
Explanation: Six palindromic strings: "a", "a", "a", "aa", "aa", "aaa".
```

## Problem Analysis

### Core Insight
This is a **palindrome detection problem** where we need to count all contiguous substrings that are palindromes:
- **Single characters**: Always palindromes (base case)
- **Two characters**: Palindrome if both characters are same
- **Longer strings**: Palindrome if first and last characters match AND inner substring is palindrome
- **Contiguous substrings**: Must consider all possible start and end positions

### Key Characteristics
- **Substring vs Subsequence**: Must be contiguous (substring), not just maintaining order
- **Count all**: Need to count every palindromic substring, not find longest
- **Overlapping patterns**: Many palindromes may overlap or be nested
- **Optimization opportunity**: Can reuse computation for similar patterns

### Mathematical Foundation
- **Total substrings**: n(n+1)/2 possible substrings for string of length n
- **Palindrome property**: s[i...j] is palindrome if s[i] == s[j] AND s[i+1...j-1] is palindrome
- **Base cases**: Single chars (always palindrome), adjacent chars (palindrome if equal)

## Approaches

### Approach 1: Brute Force

#### Key Insight
**Check every possible substring** for palindrome property.

#### Algorithm
1. **Generate all substrings**: Use nested loops for all (i,j) pairs
2. **Check palindrome**: For each substring, verify if it's a palindrome
3. **Count valid ones**: Increment counter for each palindromic substring

#### Time Complexity
- **O(n³)** - O(n²) substrings × O(n) palindrome check

#### Space Complexity
- **O(1)** - Only using variables

```java
public int countSubstringsBruteForce(String s) {
    int count = 0;
    int n = s.length();
    
    for (int i = 0; i < n; i++) {
        for (int j = i; j < n; j++) {
            if (isPalindrome(s, i, j)) {
                count++;
            }
        }
    }
    
    return count;
}

private boolean isPalindrome(String s, int left, int right) {
    while (left < right) {
        if (s.charAt(left) != s.charAt(right)) {
            return false;
        }
        left++;
        right--;
    }
    return true;
}
```

### Approach 2: Expand Around Centers ⭐ (Most Intuitive)

#### Key Insight
**Every palindrome has a center**. Expand around each possible center to find all palindromes.

#### Algorithm
1. **Two types of centers**: 
   - Character centers (odd length palindromes)
   - Between-character centers (even length palindromes)
2. **Expand outward**: From each center, expand while characters match
3. **Count as you expand**: Each successful expansion is a palindrome

#### Time Complexity
- **O(n²)** - n centers × O(n) expansion per center

#### Space Complexity
- **O(1)** - Only using variables

```java
public int countSubstringsExpandCenter(String s) {
    int count = 0;
    int n = s.length();
    
    for (int i = 0; i < n; i++) {
        // Odd length palindromes (center at i)
        count += expandAroundCenter(s, i, i);
        
        // Even length palindromes (center between i and i+1)
        count += expandAroundCenter(s, i, i + 1);
    }
    
    return count;
}

private int expandAroundCenter(String s, int left, int right) {
    int count = 0;
    
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        count++;
        left--;
        right++;
    }
    
    return count;
}
```

### Approach 3: Dynamic Programming ⭐

#### Key Insight
**Build palindrome information systematically** using previously computed results.

#### Algorithm
1. **DP table**: `dp[i][j]` = true if `s[i...j]` is palindrome
2. **Base cases**: Single characters and adjacent character pairs
3. **Build up**: Use smaller palindromes to determine larger ones
4. **Count**: Every `true` entry in DP table represents a palindrome

#### Time Complexity
- **O(n²)** - Fill n×n table

#### Space Complexity
- **O(n²)** - DP table

```java
public int countSubstringsDP(String s) {
    int n = s.length();
    boolean[][] dp = new boolean[n][n];
    int count = 0;
    
    // Every single character is a palindrome
    for (int i = 0; i < n; i++) {
        dp[i][i] = true;
        count++;
    }
    
    // Check for palindromes of length 2
    for (int i = 0; i < n - 1; i++) {
        if (s.charAt(i) == s.charAt(i + 1)) {
            dp[i][i + 1] = true;
            count++;
        }
    }
    
    // Check for palindromes of length 3 and more
    for (int len = 3; len <= n; len++) {
        for (int i = 0; i <= n - len; i++) {
            int j = i + len - 1;
            
            if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                dp[i][j] = true;
                count++;
            }
        }
    }
    
    return count;
}
```

### Approach 4: Space-Optimized DP

#### Key Insight
**Only need previous length results** to compute current length, so optimize space.

#### Algorithm
1. **Rolling arrays**: Use arrays for previous and current length results
2. **Length by length**: Process palindromes in order of increasing length
3. **Space reuse**: Reuse arrays for different lengths

#### Time Complexity
- **O(n²)** - Same computation as full DP

#### Space Complexity
- **O(n)** - Only store arrays for current computation

### Approach 5: Manacher's Algorithm (Advanced)

#### Key Insight
**Linear time palindrome detection** using preprocessing and clever expansion.

#### Algorithm
1. **Preprocess**: Transform string to handle even/odd lengths uniformly
2. **Radius array**: Track radius of palindrome centered at each position
3. **Reuse information**: Use previously computed palindromes to avoid redundant work
4. **Count palindromes**: Extract count from radius information

#### Time Complexity
- **O(n)** - Linear time due to no redundant character comparisons

#### Space Complexity
- **O(n)** - Preprocessed string and radius array

```java
public int countSubstringsManacher(String s) {
    if (s == null || s.isEmpty()) return 0;
    
    // Preprocess string: "abc" -> "^#a#b#c#$"
    String processed = preprocess(s);
    int n = processed.length();
    int[] radius = new int[n];
    
    int center = 0, right = 0;
    
    for (int i = 1; i < n - 1; i++) {
        int mirror = 2 * center - i;
        
        if (i < right) {
            radius[i] = Math.min(right - i, radius[mirror]);
        }
        
        // Try to expand palindrome centered at i
        while (processed.charAt(i + radius[i] + 1) == processed.charAt(i - radius[i] - 1)) {
            radius[i]++;
        }
        
        // If palindrome centered at i extends past right, update center and right
        if (i + radius[i] > right) {
            center = i;
            right = i + radius[i];
        }
    }
    
    return countPalindromes(radius);
}
```

## Advanced Variations

### 1. Longest Palindromic Substring (LeetCode 5)
Find the **longest palindromic substring** instead of counting all.

```java
public String longestPalindromicSubstring(String s) {
    int start = 0, maxLen = 1;
    
    for (int i = 0; i < s.length(); i++) {
        int len1 = expandFromCenter(s, i, i);     // Odd length
        int len2 = expandFromCenter(s, i, i + 1); // Even length
        
        int len = Math.max(len1, len2);
        if (len > maxLen) {
            maxLen = len;
            start = i - (len - 1) / 2;
        }
    }
    
    return s.substring(start, start + maxLen);
}
```

### 2. Count Distinct Palindromic Substrings
Count **unique palindromic substrings** (avoid counting duplicates).

```java
public int countDistinctPalindromicSubstrings(String s) {
    Set<String> palindromes = new HashSet<>();
    
    for (int i = 0; i < s.length(); i++) {
        expandAndAdd(s, i, i, palindromes);     // Odd length
        expandAndAdd(s, i, i + 1, palindromes); // Even length
    }
    
    return palindromes.size();
}
```

### 3. Palindrome Partitioning (LeetCode 131)
Find minimum cuts to partition string into palindromes.

### 4. Longest Palindromic Subsequence (LeetCode 516)
Find longest palindromic subsequence (not necessarily contiguous).

## Implementation Strategies

### Center-Based Expansion
```java
// Template for expanding around center
private int expandAroundCenter(String s, int left, int right) {
    int count = 0;
    while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
        count++;  // or collect palindrome
        left--;
        right++;
    }
    return count;
}
```

### DP Table Filling
```java
// Template for DP approach
for (int len = 1; len <= n; len++) {          // Length of substring
    for (int i = 0; i <= n - len; i++) {      // Starting position
        int j = i + len - 1;                  // Ending position
        
        if (len == 1) {
            dp[i][j] = true;                  // Single character
        } else if (len == 2) {
            dp[i][j] = (s.charAt(i) == s.charAt(j));  // Two characters
        } else {
            dp[i][j] = (s.charAt(i) == s.charAt(j)) && dp[i+1][j-1];
        }
    }
}
```

## Common Mistakes

1. **Missing even-length palindromes**: Only checking odd-length centers
2. **Off-by-one errors**: Incorrect boundary checking in expansion
3. **Double counting**: Counting same palindrome multiple times
4. **Base case errors**: Wrong handling of single/double character cases
5. **Space optimization bugs**: Incorrect array reuse in optimized versions

## Edge Cases

1. **Empty string**: Should return 0
2. **Single character**: Should return 1
3. **All same characters**: "aaa" → many overlapping palindromes
4. **No repeated characters**: "abc" → only single character palindromes
5. **Very long palindromes**: Performance considerations

## Interview Tips

### Problem Recognition
- **"Palindromic substrings"** → Think expand around centers or DP
- **"Count all"** → Different from finding longest
- **"Contiguous"** → Substring, not subsequence

### Approach Strategy
1. **Start with expand around centers**: Most intuitive O(n²) solution
2. **Explain the center concept**: Odd vs even length palindromes
3. **Discuss DP alternative**: Show understanding of different paradigms
4. **Mention Manacher's**: For advanced optimization (if time permits)
5. **Handle edge cases**: Empty string, single character

### Code Structure
```java
public int countSubstrings(String s) {
    int count = 0;
    
    for (int i = 0; i < s.length(); i++) {
        // Count odd length palindromes centered at i
        count += expandAroundCenter(s, i, i);
        
        // Count even length palindromes centered between i and i+1
        count += expandAroundCenter(s, i, i + 1);
    }
    
    return count;
}
```

## LeetCode Problems

### Core Problems:
- **[LeetCode 647 - Palindromic Substrings](https://leetcode.com/problems/palindromic-substrings/)** ⭐ (This problem)
- **[LeetCode 5 - Longest Palindromic Substring](https://leetcode.com/problems/longest-palindromic-substring/)** ⭐ (Find longest instead of count)
- **[LeetCode 516 - Longest Palindromic Subsequence](https://leetcode.com/problems/longest-palindromic-subsequence/)** (Subsequence, not substring)

### Related Palindrome Problems:
- **[LeetCode 131 - Palindrome Partitioning](https://leetcode.com/problems/palindrome-partitioning/)** (All ways to partition into palindromes)
- **[LeetCode 132 - Palindrome Partitioning II](https://leetcode.com/problems/palindrome-partitioning-ii/)** (Minimum cuts for palindrome partition)
- **[LeetCode 214 - Shortest Palindrome](https://leetcode.com/problems/shortest-palindrome/)** (Add minimum chars to make palindrome)
- **[LeetCode 336 - Palindrome Pairs](https://leetcode.com/problems/palindrome-pairs/)** (Find pairs that form palindromes)

### Advanced String Problems:
- **[LeetCode 1312 - Minimum Insertion Steps to Make a String Palindrome](https://leetcode.com/problems/minimum-insertion-steps-to-make-a-string-palindrome/)** (Transform to palindrome)
- **[LeetCode 1216 - Valid Palindrome III](https://leetcode.com/problems/valid-palindrome-iii/)** (Premium - palindrome with k deletions)

### Difficulty Progression:
1. **Start with**: LeetCode 647 (Palindromic Substrings) - Learn the pattern
2. **Next try**: LeetCode 5 (Longest Palindromic Substring) - Apply to different goal
3. **Advanced**: LeetCode 131 (Palindrome Partitioning) - Backtracking + palindromes
4. **Expert**: LeetCode 214 (Shortest Palindrome) - Advanced string manipulation

## Complexity Analysis Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Brute Force** | O(n³) | O(1) | **Understanding problem** |
| **Expand Centers** | O(n²) | O(1) | **Interview favorite** |
| **Dynamic Programming** | O(n²) | O(n²) | **Learning DP patterns** |
| **Space-Optimized DP** | O(n²) | O(n) | **Memory constraints** |
| **Manacher's Algorithm** | O(n) | O(n) | **Optimal performance** |

## Real-World Applications

1. **DNA Analysis**: Finding palindromic sequences in genetic data
2. **Text Processing**: Identifying repetitive patterns in documents
3. **Cryptography**: Palindrome-based encoding schemes
4. **Data Compression**: Recognizing symmetric patterns for compression
5. **Bioinformatics**: Searching for structural motifs in sequences
6. **Natural Language Processing**: Finding rhyme patterns and word plays

## Mathematical Insights

### Palindrome Count Formula
For string with all same characters of length n:
```
Count = n + (n-1) + (n-2) + ... + 1 = n(n+1)/2
```

### Center-Based Analysis
- **n possible odd-length centers**: At each character
- **n-1 possible even-length centers**: Between adjacent characters
- **Total centers**: 2n-1

### DP Recurrence
```
dp[i][j] = {
    true                           if i == j (single character)
    s[i] == s[j]                  if j == i+1 (two characters)
    s[i] == s[j] && dp[i+1][j-1]  if j > i+1 (general case)
}
```

**Remember**: Palindromic Substrings demonstrates the power of **center-based expansion** and shows how **simple geometric insights** can lead to elegant algorithmic solutions! 