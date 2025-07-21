# Binary Tree Level Order Traversal

## Problem Statement
Given the root of a binary tree, return the level order traversal of its nodes' values (i.e., from left to right, level by level).

## Example
```
Input: root = [3,9,20,null,null,15,7]
Output: [[3],[9,20],[15,7]]

Tree structure:
    3
   / \
  9  20
    /  \
   15   7
```

## Approach 1: BFS with Queue

### How it works:
1. **Use queue** to process nodes level by level
2. **Track level size** to group nodes by level
3. **Process all nodes** at current level before moving to next

### Key Logic:
```java
List<List<Integer>> result = new ArrayList<>();
if (root == null) return result;

Queue<TreeNode> queue = new LinkedList<>();
queue.offer(root);

while (!queue.isEmpty()) {
    int levelSize = queue.size();
    List<Integer> currentLevel = new ArrayList<>();
    
    // Process all nodes at current level
    for (int i = 0; i < levelSize; i++) {
        TreeNode node = queue.poll();
        currentLevel.add(node.val);
        
        // Add children for next level
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
    
    result.add(currentLevel);
}

return result;
```

### Time & Space Complexity:
- **Time:** O(n) - Visit each node once
- **Space:** O(w) where w is maximum width of tree

## Approach 2: DFS with Level Tracking

### How it works:
1. **Recursively traverse** with level parameter
2. **Initialize new level list** when first visiting a level
3. **Add node value** to appropriate level list

### Key Logic:
```java
List<List<Integer>> result = new ArrayList<>();

public List<List<Integer>> levelOrder(TreeNode root) {
    if (root == null) return result;
    dfs(root, 0);
    return result;
}

private void dfs(TreeNode node, int level) {
    if (node == null) return;
    
    // Create new level if needed
    if (level == result.size()) {
        result.add(new ArrayList<>());
    }
    
    // Add current node to its level
    result.get(level).add(node.val);
    
    // Recurse on children
    dfs(node.left, level + 1);
    dfs(node.right, level + 1);
}
```

### Time & Space Complexity:
- **Time:** O(n) - Visit each node once
- **Space:** O(h) for recursion stack, where h is tree height

## BFS vs DFS Comparison:

### BFS (Iterative):
- **Pros:** Natural level-by-level processing, iterative
- **Cons:** Uses more memory for wide trees

### DFS (Recursive):
- **Pros:** Less memory for balanced trees, recursive elegance
- **Cons:** May use more stack space for skewed trees

## Variations:

### Right to Left Traversal:
```java
// Add right child before left child
if (node.right != null) queue.offer(node.right);
if (node.left != null) queue.offer(node.left);
```

### Zigzag Level Order:
- **Alternate direction** for each level
- **Use deque** or reverse alternate levels

### Bottom-Up Level Order:
- **Same logic** but reverse final result
- **Or build result** from bottom up

## Edge Cases:
1. **Empty tree** → Return empty list
2. **Single node** → Return [[root.val]]
3. **Skewed tree** → Each level has one node
4. **Complete binary tree** → Levels are full

## LeetCode Similar Problems:
- [102. Binary Tree Level Order Traversal](https://leetcode.com/problems/binary-tree-level-order-traversal/) (this problem)
- [107. Binary Tree Level Order Traversal II](https://leetcode.com/problems/binary-tree-level-order-traversal-ii/)
- [103. Binary Tree Zigzag Level Order Traversal](https://leetcode.com/problems/binary-tree-zigzag-level-order-traversal/)
- [199. Binary Tree Right Side View](https://leetcode.com/problems/binary-tree-right-side-view/)
- [637. Average of Levels in Binary Tree](https://leetcode.com/problems/average-of-levels-in-binary-tree/)

## Interview Tips:
- BFS with queue is the standard approach
- Mention DFS alternative to show versatility
- Handle empty tree edge case
- Understand space complexity differences
- This pattern is foundation for many tree problems 