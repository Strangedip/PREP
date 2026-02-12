# Trie (Prefix Tree)

## Problem Statement
A Trie is a tree-like data structure used to store and search strings efficiently. Each node represents a character, and paths from root to leaves represent complete words.

**Key Operations:**
- **Insert(word):** Add word to trie in O(m) where m = word length
- **Search(word):** Check if word exists in O(m)
- **StartsWith(prefix):** Check if any word has given prefix in O(m)
- **Delete(word):** Remove word from trie in O(m)

## Example
```
Words: ["cat", "car", "card", "care", "careful"]
Trie structure:
    root
     |
     c
     |
     a
     |
     r
   / | \
  d  e  t(end)
  |  |
(end) (end)
      |
      f
      |
      u
      |
      l
      |
    (end)
```

## Approach 1: Basic Trie Implementation

### TrieNode Structure:
```java
class TrieNode {
    TrieNode[] children;
    boolean isEndOfWord;
    
    public TrieNode() {
        children = new TrieNode[26]; // For lowercase a-z
        isEndOfWord = false;
    }
}

class Trie {
    private TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode current = root;
        
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        
        current.isEndOfWord = true;
    }
    
    public boolean search(String word) {
        TrieNode node = searchNode(word);
        return node != null && node.isEndOfWord;
    }
    
    public boolean startsWith(String prefix) {
        return searchNode(prefix) != null;
    }
    
    private TrieNode searchNode(String word) {
        TrieNode current = root;
        
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        
        return current;
    }
}
```

### Time & Space Complexity:
- **Insert/Search/StartsWith:** O(m) where m = string length
- **Space:** O(ALPHABET_SIZE * N * M) where N = number of words, M = avg length

## Approach 2: Trie with Deletion

### Enhanced Implementation:
```java
class TrieWithDeletion {
    private TrieNode root;
    
    public boolean delete(String word) {
        return deleteHelper(root, word, 0);
    }
    
    private boolean deleteHelper(TrieNode current, String word, int index) {
        if (index == word.length()) {
            // We've reached end of word
            if (!current.isEndOfWord) {
                return false; // Word doesn't exist
            }
            
            current.isEndOfWord = false;
            
            // Return true if current has no children (can be deleted)
            return !hasChildren(current);
        }
        
        char c = word.charAt(index);
        int charIndex = c - 'a';
        TrieNode node = current.children[charIndex];
        
        if (node == null) {
            return false; // Word doesn't exist
        }
        
        boolean shouldDeleteChild = deleteHelper(node, word, index + 1);
        
        if (shouldDeleteChild) {
            current.children[charIndex] = null;
            
            // Return true if current is not end of another word and has no children
            return !current.isEndOfWord && !hasChildren(current);
        }
        
        return false;
    }
    
    private boolean hasChildren(TrieNode node) {
        for (TrieNode child : node.children) {
            if (child != null) return true;
        }
        return false;
    }
}
```

## Approach 3: HashMap-based Trie (Unicode Support)

### Flexible Character Set:
```java
class TrieHashMap {
    class TrieNode {
        Map<Character, TrieNode> children;
        boolean isEndOfWord;
        
        public TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }
    
    private TrieNode root;
    
    public TrieHashMap() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode current = root;
        
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        
        current.isEndOfWord = true;
    }
    
    // Similar implementation for search, startsWith
}
```

## Advanced Applications:

### 1. Auto-complete/Suggestions:
```java
public List<String> getWordsWithPrefix(String prefix) {
    List<String> result = new ArrayList<>();
    TrieNode prefixNode = searchNode(prefix);
    
    if (prefixNode != null) {
        dfs(prefixNode, prefix, result);
    }
    
    return result;
}

private void dfs(TrieNode node, String currentWord, List<String> result) {
    if (node.isEndOfWord) {
        result.add(currentWord);
    }
    
    for (int i = 0; i < 26; i++) {
        if (node.children[i] != null) {
            char nextChar = (char) ('a' + i);
            dfs(node.children[i], currentWord + nextChar, result);
        }
    }
}
```

### 2. Word Search in 2D Grid:
```java
public List<String> findWords(char[][] board, String[] words) {
    // Build trie from words
    Trie trie = new Trie();
    for (String word : words) {
        trie.insert(word);
    }
    
    List<String> result = new ArrayList<>();
    int m = board.length, n = board[0].length;
    
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            dfs(board, i, j, trie.root, "", result);
        }
    }
    
    return result;
}

private void dfs(char[][] board, int i, int j, TrieNode node, 
                 String word, List<String> result) {
    if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) {
        return;
    }
    
    char c = board[i][j];
    if (c == '#' || node.children[c - 'a'] == null) {
        return;
    }
    
    node = node.children[c - 'a'];
    word += c;
    
    if (node.isEndOfWord) {
        result.add(word);
        node.isEndOfWord = false; // Avoid duplicates
    }
    
    board[i][j] = '#'; // Mark as visited
    
    // Explore all 4 directions
    dfs(board, i + 1, j, node, word, result);
    dfs(board, i - 1, j, node, word, result);
    dfs(board, i, j + 1, node, word, result);
    dfs(board, i, j - 1, node, word, result);
    
    board[i][j] = c; // Backtrack
}
```

### 3. XOR Trie (Maximum XOR):
```java
class XORTrie {
    class TrieNode {
        TrieNode[] children = new TrieNode[2]; // 0 and 1
    }
    
    private TrieNode root = new TrieNode();
    
    public void insert(int num) {
        TrieNode current = root;
        
        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1;
            if (current.children[bit] == null) {
                current.children[bit] = new TrieNode();
            }
            current = current.children[bit];
        }
    }
    
    public int findMaxXOR(int num) {
        TrieNode current = root;
        int maxXOR = 0;
        
        for (int i = 31; i >= 0; i--) {
            int bit = (num >> i) & 1;
            int desiredBit = 1 - bit; // We want opposite bit for max XOR
            
            if (current.children[desiredBit] != null) {
                maxXOR |= (1 << i);
                current = current.children[desiredBit];
            } else {
                current = current.children[bit];
            }
        }
        
        return maxXOR;
    }
}
```

### 4. Count Words with Given Prefix:
```java
class TrieWithCount {
    class TrieNode {
        TrieNode[] children = new TrieNode[26];
        int prefixCount = 0; // Count of words passing through this node
        boolean isEndOfWord = false;
    }
    
    private TrieNode root = new TrieNode();
    
    public void insert(String word) {
        TrieNode current = root;
        
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
            current.prefixCount++;
        }
        
        current.isEndOfWord = true;
    }
    
    public int countWordsWithPrefix(String prefix) {
        TrieNode current = root;
        
        for (char c : prefix.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                return 0;
            }
            current = current.children[index];
        }
        
        return current.prefixCount;
    }
}
```

## Common Use Cases:

### ✅ Perfect for:
- **Auto-complete systems**
- **Spell checkers**
- **IP routing tables**
- **Phone book applications**
- **Word games** (Scrabble, Boggle)
- **Dictionary implementations**

### ❌ Not suitable for:
- **Numeric data** (use other trees)
- **Very long strings** (memory intensive)
- **Frequent random access** (use hash tables)

## Memory Optimization Techniques:

### 1. Compressed Trie (Radix Tree):
```java
// Store common prefixes as single nodes
// Reduces space when many nodes have single child
```

### 2. Ternary Search Trie:
```java
// Each node has 3 children: less, equal, greater
// More memory efficient for sparse character sets
```

### 3. Array vs HashMap Trade-off:
- **Array:** Faster access, more memory
- **HashMap:** Less memory, slightly slower

## LeetCode Similar Problems:
- [208. Implement Trie (Prefix Tree)](https://leetcode.com/problems/implement-trie-prefix-tree/)
- [212. Word Search II](https://leetcode.com/problems/word-search-ii/)
- [421. Maximum XOR of Two Numbers in an Array](https://leetcode.com/problems/maximum-xor-of-two-numbers-in-an-array/)
- [676. Implement Magic Dictionary](https://leetcode.com/problems/implement-magic-dictionary/)
- [1804. Implement Trie II (Prefix Tree)](https://leetcode.com/problems/implement-trie-ii-prefix-tree/)

## Interview Tips:
- **Start with basic implementation** using arrays
- **Know how to handle deletion** properly
- **Understand XOR trie** for bit manipulation problems
- **Practice word search** applications
- **Essential for string processing** problems at SDE2+ level 