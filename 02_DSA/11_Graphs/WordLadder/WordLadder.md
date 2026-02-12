# Word Ladder

## Problem Statement
A transformation sequence from word `beginWord` to word `endWord` using a dictionary `wordList` is a sequence of words `beginWord → s1 → s2 → ... → sk` such that:

- Every adjacent pair of words differs by exactly **one letter**
- Every `si` for `1 <= i <= k` is in `wordList` (beginWord doesn't need to be in wordList)
- `sk == endWord`

Given two words (`beginWord` and `endWord`) and a dictionary `wordList`, return the **number of words** in the shortest transformation sequence from `beginWord` to `endWord`, or `0` if no such sequence exists.

**Examples:**
```
Input: beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log","cog"]
Output: 5
Explanation: "hit" → "hot" → "dot" → "dog" → "cog" (5 words)

Input: beginWord = "hit", endWord = "cog", wordList = ["hot","dot","dog","lot","log"]
Output: 0
Explanation: endWord "cog" is not in wordList, so no transformation possible.
```

## Problem Analysis

### Core Insight
This is a **shortest path problem** in an **unweighted graph** where:
- **Nodes**: Words that differ by exactly one character
- **Edges**: Valid transformations between words
- **Goal**: Find shortest path from `beginWord` to `endWord`

### Key Characteristics
- **Unweighted graph** → BFS finds shortest path
- **Large search space** → Need optimization (bidirectional BFS)
- **String transformation** → Pattern-based preprocessing can help

## Approaches

### Approach 1: Standard BFS ⭐ (Most Intuitive)

#### Key Insight
**Breadth-First Search guarantees shortest path** in unweighted graphs.

#### Algorithm
1. **Initialize**: Queue with `beginWord`, visited set, level counter
2. **Level-by-level BFS**: Process all words at current distance
3. **Generate neighbors**: Try changing each character to 'a'-'z'
4. **Check validity**: New word must be in wordList and not visited
5. **Found target**: Return level when we reach `endWord`

#### Time Complexity
- **O(M² × N)** where M = word length, N = number of words
- **M²**: For each word, try M positions × 26 characters
- **N**: Process each word once

#### Space Complexity
- **O(M × N)** for queue and visited set

```java
public int ladderLength(String beginWord, String endWord, List<String> wordList) {
    if (!wordList.contains(endWord)) return 0;
    
    Set<String> wordSet = new HashSet<>(wordList);
    Queue<String> queue = new LinkedList<>();
    Set<String> visited = new HashSet<>();
    
    queue.offer(beginWord);
    visited.add(beginWord);
    int level = 1;
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        
        for (int i = 0; i < size; i++) {
            String current = queue.poll();
            if (current.equals(endWord)) return level;
            
            // Try all single-character changes
            char[] chars = current.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                char original = chars[j];
                for (char c = 'a'; c <= 'z'; c++) {
                    if (c == original) continue;
                    chars[j] = c;
                    String newWord = new String(chars);
                    
                    if (wordSet.contains(newWord) && !visited.contains(newWord)) {
                        queue.offer(newWord);
                        visited.add(newWord);
                    }
                }
                chars[j] = original;
            }
        }
        level++;
    }
    return 0;
}
```

### Approach 2: Bidirectional BFS ⭐ (Optimized)

#### Key Insight
**Search from both ends simultaneously** to reduce search space from O(b^d) to O(b^(d/2)).

#### Algorithm
1. **Two sets**: Start from `beginWord` and `endWord` simultaneously
2. **Always expand smaller set**: Optimization to keep search balanced
3. **Meet in middle**: When sets intersect, we found shortest path
4. **Remove visited**: Avoid cycles by removing words from wordSet

#### Performance Improvement
- **Standard BFS**: O(b^d) where b = branching factor, d = depth
- **Bidirectional**: O(b^(d/2)) = significant improvement for large graphs

#### Time Complexity
- **O(M² × N)** same as BFS but **much faster in practice**

#### Space Complexity
- **O(M × N)** for two sets and wordSet

```java
public int ladderLengthBidirectional(String beginWord, String endWord, List<String> wordList) {
    if (!wordList.contains(endWord)) return 0;
    
    Set<String> wordSet = new HashSet<>(wordList);
    Set<String> beginSet = new HashSet<>();
    Set<String> endSet = new HashSet<>();
    
    beginSet.add(beginWord);
    endSet.add(endWord);
    int level = 1;
    
    while (!beginSet.isEmpty() && !endSet.isEmpty()) {
        // Always expand the smaller set
        if (beginSet.size() > endSet.size()) {
            Set<String> temp = beginSet;
            beginSet = endSet;
            endSet = temp;
        }
        
        Set<String> nextSet = new HashSet<>();
        for (String word : beginSet) {
            // Generate all neighbors
            // If neighbor in endSet → found path
            // If neighbor in wordSet → add to nextSet
        }
        beginSet = nextSet;
        level++;
    }
    return 0;
}
```

### Approach 3: Pattern-Based Preprocessing

#### Key Insight
**Preprocess words into patterns** like "h*t", "*it", "hi*" to group similar words.

#### Algorithm
1. **Build pattern map**: For "hit" → create patterns "h*t", "*it", "hi*"
2. **Group by patterns**: Words sharing patterns are neighbors
3. **Standard BFS**: Use pattern map instead of generating neighbors on-the-fly

#### Advantages
- **Cleaner neighbor generation**: No nested loops for character changes
- **Better for multiple queries**: Preprocessing amortizes over multiple calls

#### Time Complexity
- **O(M² × N)** for preprocessing + O(M² × N) for BFS

#### Space Complexity
- **O(M² × N)** for pattern map

## Implementation Details

### Neighbor Generation Strategies

#### 1. Character Replacement (Most Common)
```java
char[] chars = word.toCharArray();
for (int i = 0; i < chars.length; i++) {
    char original = chars[i];
    for (char c = 'a'; c <= 'z'; c++) {
        if (c != original) {
            chars[i] = c;
            String neighbor = new String(chars);
            // Process neighbor
        }
    }
    chars[i] = original; // Restore
}
```

#### 2. Pattern-Based
```java
for (int i = 0; i < word.length(); i++) {
    String pattern = word.substring(0, i) + "*" + word.substring(i + 1);
    List<String> neighbors = patternMap.get(pattern);
    // Process neighbors
}
```

### Optimization Techniques

#### 1. **Use HashSet for wordList**: O(1) lookup vs O(N) for List
#### 2. **Remove visited words**: Prevents cycles and reduces search space
#### 3. **Early termination**: Return immediately when target found
#### 4. **Bidirectional search**: Exponential speedup for long paths

## Common Mistakes

1. **Not checking if endWord exists**: Must return 0 if endWord not in wordList
2. **Including beginWord in count**: Level starts at 1, not 0
3. **Not handling single character**: Edge case when beginWord.length() == 1
4. **Infinite loops**: Must mark words as visited or remove from wordSet
5. **String creation overhead**: Use StringBuilder or char arrays efficiently

## Edge Cases

1. **Empty wordList**: Return 0
2. **beginWord == endWord**: Return 1
3. **Single character words**: "a" → "b"
4. **No transformation possible**: Return 0
5. **Large word length**: Performance considerations

## Extensions & Variations

### 1. **Word Ladder II** (LeetCode 126)
Return **all shortest transformation sequences**, not just the length.

### 2. **Word Ladder with Different Rules**
- Allow insertions/deletions, not just substitutions
- Multiple character changes allowed
- Weighted transformations

### 3. **Word Break vs Word Ladder**
- Word Break: Can we form target using dictionary words?
- Word Ladder: Can we transform one word to another?

## Interview Tips

1. **Start with standard BFS**: Always explain the basic approach first
2. **Mention bidirectional optimization**: Shows advanced understanding
3. **Discuss time complexity clearly**: M², N, and why
4. **Handle edge cases**: Empty lists, same words, impossible cases
5. **Code cleanly**: Use meaningful variable names, clear structure

## LeetCode Problems

### Related Problems:
- **[LeetCode 127 - Word Ladder](https://leetcode.com/problems/word-ladder/)** ⭐ (This problem)
- **[LeetCode 126 - Word Ladder II](https://leetcode.com/problems/word-ladder-ii/)** (Return all shortest paths)
- **[LeetCode 433 - Minimum Genetic Mutation](https://leetcode.com/problems/minimum-genetic-mutation/)** (Similar BFS pattern)
- **[LeetCode 1197 - Minimum Knight Moves](https://leetcode.com/problems/minimum-knight-moves/)** (Bidirectional BFS)
- **[LeetCode 752 - Open the Lock](https://leetcode.com/problems/open-the-lock/)** (State space search)
- **[LeetCode 773 - Sliding Puzzle](https://leetcode.com/problems/sliding-puzzle/)** (BFS on configurations)

### Difficulty Progression:
1. **Start with**: LeetCode 127 (Word Ladder) - Basic BFS
2. **Next try**: LeetCode 433 (Minimum Genetic Mutation) - Similar pattern
3. **Advanced**: LeetCode 126 (Word Ladder II) - Return all paths
4. **Master level**: LeetCode 752 (Open the Lock) - Complex state space

## Complexity Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Standard BFS** | O(M²N) | O(MN) | **Learning, most interviews** |
| **Bidirectional BFS** | O(M²N) | O(MN) | **Optimization, advanced interviews** |
| **Pattern-based** | O(M²N) | O(M²N) | **Multiple queries, clean code** |

## Real-World Applications

1. **Spell checkers**: Finding word suggestions with minimal edits
2. **DNA analysis**: Finding genetic mutations and similarities  
3. **Social networks**: Degrees of separation between people
4. **Game AI**: State space search in puzzles and games
5. **Recommendation systems**: Finding similar items through transformations

**Remember**: This problem is a classic example of **modeling real-world problems as graph search**. The key insight is recognizing that word transformations form an unweighted graph where BFS finds the optimal solution! 