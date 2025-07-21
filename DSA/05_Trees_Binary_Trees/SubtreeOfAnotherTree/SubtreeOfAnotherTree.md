# Subtree of Another Tree

## Problem Statement

Given the roots of two binary trees `root` and `subRoot`, return `true` if there is a subtree of `root` with the same structure and node values of `subRoot` and `false` otherwise.

A subtree of a binary tree `tree` is a tree that consists of a node in `tree` and all of this node's descendants. The tree `tree` could also be considered as a subtree of itself.

## Examples

**Example 1:**
```
Input: root = [3,4,5,1,2], subRoot = [4,1,2]
Output: true

Root tree:       SubRoot:
    3              4
   / \            / \
  4   5          1   2
 / \
1   2

The subtree starting at node 4 matches subRoot exactly.
```

**Example 2:**
```
Input: root = [3,4,5,1,2,null,null,null,null,0], subRoot = [4,1,2]
Output: false

Root tree:       SubRoot:
    3              4
   / \            / \
  4   5          1   2
 / \
1   2
   /
  0

The subtree starting at node 4 has an extra child (0), so it doesn't match.
```

## Constraints

- The number of nodes in the `root` tree is in the range [1, 2000]
- The number of nodes in the `subRoot` tree is in the range [1, 1000]
- -10⁴ ≤ Node.val ≤ 10⁴

## Solutions

### Approach 1: Recursive DFS (Brute Force) ⭐ (Most Common)

**Algorithm:**
1. For each node in the main tree, check if the subtree starting from that node is identical to `subRoot`
2. Use a helper function to compare two trees for equality
3. Recursively check left and right subtrees

**Implementation:**
```java
public boolean isSubtree(TreeNode root, TreeNode subRoot) {
    if (root == null) return false;
    
    // Check if current node forms identical subtree
    if (isSameTree(root, subRoot)) return true;
    
    // Recursively check left and right subtrees
    return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
}

private boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    
    return p.val == q.val && 
           isSameTree(p.left, q.left) && 
           isSameTree(p.right, q.right);
}
```

**Time Complexity:** O(m × n) where m = nodes in root, n = nodes in subRoot  
**Space Complexity:** O(max(h₁, h₂)) where h₁, h₂ are heights of trees

**When to use:** Simple, intuitive solution. Good for interviews due to clarity.

### Approach 2: String Serialization with KMP

**Algorithm:**
1. Serialize both trees to strings with special delimiters
2. Use KMP (Knuth-Morris-Pratt) algorithm to find substring match
3. Ensures linear time complexity

**Implementation:**
```java
public boolean isSubtreeKMP(TreeNode root, TreeNode subRoot) {
    String rootStr = serialize(root);
    String subStr = serialize(subRoot);
    return kmpSearch(rootStr, subStr);
}

private String serialize(TreeNode node) {
    if (node == null) return "#";
    return "^" + node.val + serialize(node.left) + serialize(node.right);
}

// KMP algorithm implementation
private boolean kmpSearch(String text, String pattern) {
    int[] lps = computeLPS(pattern);
    int i = 0, j = 0;
    
    while (i < text.length()) {
        if (text.charAt(i) == pattern.charAt(j)) {
            i++; j++;
        }
        
        if (j == pattern.length()) return true;
        else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
            if (j != 0) j = lps[j - 1];
            else i++;
        }
    }
    return false;
}
```

**Time Complexity:** O(m + n) - optimal  
**Space Complexity:** O(m + n)

**When to use:** When you need optimal time complexity and can handle string manipulation overhead.

### Approach 3: Hash-Based Comparison

**Algorithm:**
1. Generate hash strings for all subtrees in the main tree
2. Generate hash for the target subtree
3. Check if target hash exists in the set of subtree hashes

**Implementation:**
```java
public boolean isSubtreeHash(TreeNode root, TreeNode subRoot) {
    Set<String> rootSubtrees = new HashSet<>();
    String targetHash = getHash(subRoot);
    dfsHash(root, rootSubtrees);
    return rootSubtrees.contains(targetHash);
}

private String getHash(TreeNode node) {
    if (node == null) return "#";
    return node.val + "," + getHash(node.left) + "," + getHash(node.right);
}
```

**Time Complexity:** O(m + n) average case  
**Space Complexity:** O(m + n)

**When to use:** Good compromise between simplicity and efficiency.

### Approach 4: Merkle Hash (Rolling Hash)

**Algorithm:**
1. Use mathematical hashing to generate unique identifiers for subtrees
2. Compare hash values instead of tree structures
3. Handle hash collisions appropriately

**Implementation:**
```java
private Map<TreeNode, Long> merkleHashes = new HashMap<>();
private static final long MOD = 1000000007L;
private static final long BASE = 31L;

public boolean isSubtreeMerkle(TreeNode root, TreeNode subRoot) {
    long targetHash = merkleHash(subRoot);
    return findSubtreeMerkle(root, targetHash);
}

private long merkleHash(TreeNode node) {
    if (node == null) return 0;
    if (merkleHashes.containsKey(node)) return merkleHashes.get(node);
    
    long leftHash = merkleHash(node.left);
    long rightHash = merkleHash(node.right);
    long hash = ((long)node.val + leftHash * BASE + rightHash * BASE * BASE) % MOD;
    
    merkleHashes.put(node, hash);
    return hash;
}
```

**Time Complexity:** O(m + n)  
**Space Complexity:** O(m + n)

**When to use:** When you need very fast comparisons and can handle potential hash collisions.

### Approach 5: Iterative DFS

**Algorithm:**
1. Use stack to traverse the main tree iteratively
2. For each node, check if it forms a matching subtree
3. Use iterative tree comparison as well

**Implementation:**
```java
public boolean isSubtreeIterative(TreeNode root, TreeNode subRoot) {
    if (root == null) return false;
    
    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);
    
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        
        if (isSameTreeIterative(node, subRoot)) return true;
        
        if (node.right != null) stack.push(node.right);
        if (node.left != null) stack.push(node.left);
    }
    return false;
}
```

**Time Complexity:** O(m × n)  
**Space Complexity:** O(h₁ + h₂)

**When to use:** When you want to avoid recursion due to stack limitations.

### Approach 6: BFS Level Order

**Algorithm:**
1. Use queue to traverse the main tree level by level
2. For each node encountered, check if it matches the subtree
3. Good for finding subtrees that are closer to the root

**Implementation:**
```java
public boolean isSubtreeBFS(TreeNode root, TreeNode subRoot) {
    if (root == null) return false;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        
        if (isSameTree(node, subRoot)) return true;
        
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
    return false;
}
```

**Time Complexity:** O(m × n)  
**Space Complexity:** O(w) where w is maximum width

**When to use:** When subtrees are likely to be found near the root.

## Key Insights

### Tree Equality Check
The core operation is determining if two trees are identical:
```java
private boolean isSameTree(TreeNode p, TreeNode q) {
    if (p == null && q == null) return true;
    if (p == null || q == null) return false;
    return p.val == q.val && 
           isSameTree(p.left, q.left) && 
           isSameTree(p.right, q.right);
}
```

### Serialization Strategy
When using string-based approaches, proper serialization is crucial:
- Use delimiters to distinguish between nodes
- Handle null nodes consistently
- Ensure unique representation for each tree structure

### Hash Collision Handling
For hash-based approaches:
- Use large prime modulus to reduce collisions
- Consider double hashing for critical applications
- Always verify hash matches with actual tree comparison

## Performance Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Recursive DFS | O(m×n) | O(h) | Simple, intuitive | Not optimal for large trees |
| KMP Serialization | O(m+n) | O(m+n) | Optimal time | String overhead |
| Hash-based | O(m+n) | O(m+n) | Fast, good balance | Potential collisions |
| Merkle Hash | O(m+n) | O(m+n) | Very fast | Hash collision risk |
| Iterative DFS | O(m×n) | O(h) | No recursion limit | More complex code |
| BFS | O(m×n) | O(w) | Good for shallow subtrees | Not optimal |

## Common Mistakes

1. **Incorrect Base Cases:** Not handling null nodes properly in tree comparison
2. **Serialization Issues:** Missing delimiters causing incorrect string matches
3. **Hash Collisions:** Not validating hash matches with actual comparison
4. **Edge Cases:** Not handling empty trees or single-node trees
5. **Index Errors:** Off-by-one errors in string manipulation approaches

## Test Cases

```java
// Basic positive case
root = [3,4,5,1,2], subRoot = [4,1,2] → true

// Basic negative case  
root = [3,4,5,1,2,null,null,null,null,0], subRoot = [4,1,2] → false

// Single node match
root = [1], subRoot = [1] → true

// Single node mismatch
root = [1], subRoot = [2] → false

// Identical trees
root = [1,2,3], subRoot = [1,2,3] → true

// Subtree at root
root = [1,2,3], subRoot = [1,2,3] → true

// Deep subtree
root = [1,2,3,4,5,6,7], subRoot = [4] → true

// Partial match (structure differs)
root = [1,2,3], subRoot = [2] → false (if subRoot has children)
```

## Related Problems

- **Same Tree** - Core logic for tree comparison
- **Symmetric Tree** - Tree structure analysis
- **Binary Tree Maximum Path Sum** - Tree traversal patterns
- **Serialize and Deserialize Binary Tree** - Tree serialization techniques

## Interview Tips

1. **Start Simple:** Begin with recursive DFS approach - it's most intuitive
2. **Optimize if Asked:** Mention KMP or hashing for optimal solutions
3. **Handle Edge Cases:** Consider null trees, single nodes, identical trees
4. **Discuss Trade-offs:** Time vs space complexity for different approaches
5. **Code Modularity:** Separate tree comparison logic into helper function
6. **Test Thoroughly:** Walk through examples to verify correctness

## Follow-up Questions

1. **"What if trees are very large?"** → Discuss KMP or hashing approaches
2. **"What if we need to find all occurrences?"** → Modify to continue searching
3. **"What about memory constraints?"** → Discuss iterative vs recursive trade-offs
4. **"How to handle hash collisions?"** → Double hashing or verification step

This problem is excellent for demonstrating understanding of tree traversal, recursion, and optimization techniques. The multiple solution approaches make it a favorite in technical interviews for discussing algorithm trade-offs. 