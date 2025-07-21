# Word Break

## Problem Statement
Given a string `s` and a dictionary of strings `wordDict`, return `true` if `s` can be **segmented into a space-separated sequence** of one or more dictionary words.

**Important Notes:**
- The **same word** in the dictionary may be **reused multiple times** in the segmentation
- You may assume the dictionary does **not contain duplicate words**
- Return `true` if segmentation is possible, `false` otherwise

**Examples:**
```
Input: s = "leetcode", wordDict = ["leet","code"]
Output: true
Explanation: Return true because "leetcode" can be segmented as "leet code".

Input: s = "applepenapple", wordDict = ["apple","pen"]
Output: true
Explanation: Return true because "applepenapple" can be segmented as "apple pen apple".
Note that you are allowed to reuse a dictionary word.

Input: s = "catsandog", wordDict = ["cats","dog","sand","and","cat"]
Output: false
```

## Problem Analysis

### Core Insight
This is a **classic Dynamic Programming problem** where we determine if a string can be broken into valid dictionary words:
- **Optimal Substructure**: If `s[0...i]` can be segmented, we can check if `s[0...j] + s[j...i]` forms a valid segmentation
- **Overlapping Subproblems**: Same prefixes are checked multiple times
- **Decision Making**: At each position, decide where to "break" the string

### Key Characteristics
- **String parsing**: Breaking string into meaningful components
- **Dictionary lookup**: Efficient word validation required
- **Multiple solutions**: Many ways to break might exist, we just need one valid way
- **Greedy won't work**: Need to try all possibilities systematically

## Approaches

### Approach 1: Recursive Brute Force

#### Key Insight
**Try all possible ways** to break the string by recursively checking each prefix.

#### Algorithm
1. **Base case**: If we reach the end of string, return `true`
2. **Try all prefixes**: For each position, check if prefix is in dictionary
3. **Recursive call**: If prefix is valid, recursively check remaining suffix
4. **Backtrack**: Try next possible prefix if current path fails

#### Time Complexity
- **O(2^n)** - Exponential due to overlapping subproblems
- **Branching factor**: Up to n choices at each step

#### Space Complexity
- **O(n)** - Recursion stack depth

```java
public boolean wordBreakRecursive(String s, List<String> wordDict) {
    Set<String> wordSet = new HashSet<>(wordDict);
    return canBreak(s, 0, wordSet);
}

private boolean canBreak(String s, int start, Set<String> wordSet) {
    if (start == s.length()) {
        return true;  // Reached end successfully
    }
    
    // Try all possible substrings from start
    for (int end = start + 1; end <= s.length(); end++) {
        String word = s.substring(start, end);
        if (wordSet.contains(word) && canBreak(s, end, wordSet)) {
            return true;
        }
    }
    
    return false;
}
```

### Approach 2: Memoization (Top-Down DP) ⭐

#### Key Insight
**Cache results** of subproblems to avoid recomputation.

#### Algorithm
1. **Memoization table**: `memo[i]` stores result for substring starting at index `i`
2. **Check cache**: Return cached result if available
3. **Compute and cache**: Calculate result and store in memo table

#### Time Complexity
- **O(n³)** - n subproblems, each taking O(n²) for substring operations

#### Space Complexity
- **O(n)** for memoization array + **O(n)** recursion stack

```java
public boolean wordBreakMemo(String s, List<String> wordDict) {
    Set<String> wordSet = new HashSet<>(wordDict);
    Boolean[] memo = new Boolean[s.length()];
    return canBreakMemo(s, 0, wordSet, memo);
}

private boolean canBreakMemo(String s, int start, Set<String> wordSet, Boolean[] memo) {
    if (start == s.length()) return true;
    
    if (memo[start] != null) return memo[start];  // Cache hit
    
    for (int end = start + 1; end <= s.length(); end++) {
        String word = s.substring(start, end);
        if (wordSet.contains(word) && canBreakMemo(s, end, wordSet, memo)) {
            memo[start] = true;
            return true;
        }
    }
    
    memo[start] = false;
    return false;
}
```

### Approach 3: Bottom-Up DP (Tabulation) ⭐ (Most Popular)

#### Key Insight
**Build solution iteratively** from smaller subproblems to larger ones.

#### Algorithm
1. **DP array**: `dp[i]` = true if `s[0...i-1]` can be segmented
2. **Base case**: `dp[0] = true` (empty string can always be segmented)
3. **Fill table**: For each position, check all possible word endings
4. **Final answer**: `dp[n]` contains the result

#### Time Complexity
- **O(n³)** - Two nested loops + substring operation

#### Space Complexity
- **O(n)** - DP array only

```java
public boolean wordBreakDP(String s, List<String> wordDict) {
    Set<String> wordSet = new HashSet<>(wordDict);
    int n = s.length();
    boolean[] dp = new boolean[n + 1];
    dp[0] = true;  // Empty string base case
    
    for (int i = 1; i <= n; i++) {
        for (int j = 0; j < i; j++) {
            // Check if s[0...j-1] can be segmented AND s[j...i-1] is in dictionary
            if (dp[j] && wordSet.contains(s.substring(j, i))) {
                dp[i] = true;
                break;  // Found one valid segmentation
            }
        }
    }
    
    return dp[n];
}
```

### Approach 4: Optimized DP with Length Filtering

#### Key Insight
**Only check substrings with lengths that exist in dictionary** to reduce unnecessary work.

#### Algorithm
1. **Precompute word lengths**: Extract all unique word lengths from dictionary
2. **Length-based checking**: Only check substrings with valid lengths
3. **Reduced iterations**: Skip impossible substring lengths

#### Time Complexity
- **O(n² × k)** where k = number of unique word lengths (usually much smaller than n)

#### Space Complexity
- **O(n + m)** where m = total characters in dictionary

```java
public boolean wordBreakOptimized(String s, List<String> wordDict) {
    Set<String> wordSet = new HashSet<>(wordDict);
    Set<Integer> wordLengths = new HashSet<>();
    
    // Precompute all possible word lengths
    for (String word : wordDict) {
        wordLengths.add(word.length());
    }
    
    int n = s.length();
    boolean[] dp = new boolean[n + 1];
    dp[0] = true;
    
    for (int i = 1; i <= n; i++) {
        for (int len : wordLengths) {
            if (len <= i && dp[i - len] && wordSet.contains(s.substring(i - len, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    
    return dp[n];
}
```

### Approach 5: BFS Solution

#### Key Insight
**Treat as graph problem** where each position is a node and valid words create edges.

#### Algorithm
1. **BFS from start**: Start from position 0
2. **Explore neighbors**: For each position, find all valid word extensions
3. **Avoid cycles**: Use visited set to prevent revisiting positions
4. **Target reached**: Return true if we reach the end of string

#### Time Complexity
- **O(n³)** - Similar to DP but with BFS traversal

#### Space Complexity
- **O(n)** - Queue and visited set

```java
public boolean wordBreakBFS(String s, List<String> wordDict) {
    Set<String> wordSet = new HashSet<>(wordDict);
    Queue<Integer> queue = new LinkedList<>();
    Set<Integer> visited = new HashSet<>();
    
    queue.offer(0);
    visited.add(0);
    
    while (!queue.isEmpty()) {
        int start = queue.poll();
        
        for (int end = start + 1; end <= s.length(); end++) {
            if (visited.contains(end)) continue;
            
            if (wordSet.contains(s.substring(start, end))) {
                if (end == s.length()) return true;  // Reached end
                
                queue.offer(end);
                visited.add(end);
            }
        }
    }
    
    return false;
}
```

## Advanced Variations

### 1. Word Break II - Return All Sentences (LeetCode 140)
Return **all possible sentences** that can be formed.

```java
public List<String> wordBreakII(String s, List<String> wordDict) {
    Set<String> wordSet = new HashSet<>(wordDict);
    Map<Integer, List<String>> memo = new HashMap<>();
    return wordBreakHelper(s, 0, wordSet, memo);
}
```

### 2. Minimum Word Breaks
Find the **minimum number of words** needed to break the string.

```java
public int minWordBreaks(String s, List<String> wordDict) {
    // DP approach: dp[i] = minimum words to break s[0...i-1]
    int[] dp = new int[s.length() + 1];
    Arrays.fill(dp, -1);
    dp[0] = 0;
    // ... implementation
}
```

### 3. Word Break with Concatenated Words
Check if string can be broken using **concatenations of dictionary words**.

## Implementation Optimizations

### 1. Trie Data Structure
Use **Trie for efficient word lookup**:
```java
class Trie {
    TrieNode root = new TrieNode();
    
    public void insert(String word) { /* ... */ }
    public boolean search(String word) { /* ... */ }
}
```

### 2. Early Termination
```java
if (s.length() > maxPossibleLength) return false;
```

### 3. HashSet vs List
Always convert `wordDict` to `HashSet` for O(1) lookup instead of O(n).

## Common Mistakes

1. **Forgetting base case**: Not initializing `dp[0] = true`
2. **Substring bounds**: Off-by-one errors in substring operations
3. **Dictionary lookup**: Using List instead of HashSet for word checking
4. **Memoization bugs**: Not properly caching results
5. **Break condition**: Not breaking inner loop when solution found

## Edge Cases

1. **Empty string**: Should return true (can be segmented trivially)
2. **Single character**: Dictionary with single chars
3. **No valid segmentation**: String that cannot be broken
4. **Entire string in dictionary**: Trivial case
5. **Very long words**: Performance considerations

## Interview Tips

### Problem Recognition
- **"Can be segmented"** → Think DP
- **"Dictionary of words"** → HashSet for fast lookup
- **"Space-separated sequence"** → String parsing problem

### Approach Strategy
1. **Start with recursive solution**: Show understanding of problem structure
2. **Add memoization**: Demonstrate DP optimization awareness
3. **Move to tabulation**: Show iterative DP mastery
4. **Discuss optimizations**: Length filtering, early termination
5. **Handle variations**: Word Break II, minimum breaks

### Code Structure
```java
public boolean wordBreak(String s, List<String> wordDict) {
    // 1. Convert to HashSet for O(1) lookup
    Set<String> wordSet = new HashSet<>(wordDict);
    
    // 2. Initialize DP array
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;
    
    // 3. Fill DP table
    for (int i = 1; i <= s.length(); i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && wordSet.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    
    // 4. Return result
    return dp[s.length()];
}
```

## LeetCode Problems

### Core Problems:
- **[LeetCode 139 - Word Break](https://leetcode.com/problems/word-break/)** ⭐ (This problem)
- **[LeetCode 140 - Word Break II](https://leetcode.com/problems/word-break-ii/)** ⭐ (Return all possible sentences)

### Related String DP Problems:
- **[LeetCode 472 - Concatenated Words](https://leetcode.com/problems/concatenated-words/)** (Words formed by concatenating other words)
- **[LeetCode 818 - Race Car](https://leetcode.com/problems/race-car/)** (DP with string-like state transitions)
- **[LeetCode 842 - Split Array into Fibonacci Sequence](https://leetcode.com/problems/split-array-into-fibonacci-sequence/)** (Similar string segmentation)
- **[LeetCode 1048 - Longest String Chain](https://leetcode.com/problems/longest-string-chain/)** (DP on strings)

### Advanced Variations:
- **[LeetCode 1278 - Palindrome Partitioning III](https://leetcode.com/problems/palindrome-partitioning-iii/)** (String partitioning with constraints)
- **[LeetCode 1416 - Restore The Array](https://leetcode.com/problems/restore-the-array/)** (Number-based word break)

### Difficulty Progression:
1. **Start with**: LeetCode 139 (Word Break) - Learn the basic pattern
2. **Next try**: LeetCode 140 (Word Break II) - Handle multiple solutions
3. **Advanced**: LeetCode 472 (Concatenated Words) - Complex word relationships
4. **Expert**: LeetCode 1278 (Palindrome Partitioning III) - Multi-constraint optimization

## Complexity Analysis Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Recursive** | O(2^n) | O(n) | **Understanding problem structure** |
| **Memoization** | O(n³) | O(n) | **Top-down thinking** |
| **Tabulation** | O(n³) | O(n) | **Interview standard** |
| **Optimized DP** | O(n²k) | O(n) | **Performance optimization** |
| **BFS** | O(n³) | O(n) | **Alternative perspective** |

## Real-World Applications

1. **Natural Language Processing**: Sentence segmentation, tokenization
2. **Compiler Design**: Lexical analysis, keyword recognition
3. **Search Engines**: Query parsing and understanding
4. **Text Processing**: Document analysis, content extraction
5. **Machine Translation**: Breaking text into translatable units
6. **Autocomplete Systems**: Suggesting word completions

## Mathematical Insights

### DP Recurrence Relation
```
dp[i] = OR over all j < i of (dp[j] AND s[j...i-1] ∈ wordDict)
```

### Optimization Bounds
- **Best case**: O(n × avg_word_length) when most prefixes match
- **Worst case**: O(n³) when checking all possible breakpoints
- **Space optimization**: Can be reduced to O(max_word_length) in some cases

**Remember**: Word Break is a **fundamental string DP problem** that demonstrates how to **systematically explore all possible partitions** while using **memoization to avoid redundant computations**! 