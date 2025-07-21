# Invert Binary Tree

## Problem Statement

Given the root of a binary tree, invert the tree, and return its root.

**Example 1:**
```
Input: root = [4,2,7,1,3,6,9]
Output: [4,7,2,9,6,3,1]

Original Tree:       Inverted Tree:
      4                    4
     / \                  / \
    2   7                7   2
   / \ / \              / \ / \
  1  3 6  9            9  6 3  1
```

**Example 2:**
```
Input: root = [2,1,3]
Output: [2,3,1]
```

**Example 3:**
```
Input: root = []
Output: []
```

## Constraints

- The number of nodes in the tree is in the range [0, 100]
- -100 <= Node.val <= 100

## Solutions

### Approach 1: Recursive (DFS) - Recommended ⭐

**Algorithm:**
1. Base case: if root is null, return null
2. Swap left and right children
3. Recursively invert left and right subtrees

**Implementation:**
```java
public TreeNode invertTreeRecursive(TreeNode root) {
    if (root == null) {
        return null;
    }
    
    // Swap left and right children
    TreeNode temp = root.left;
    root.left = root.right;
    root.right = temp;
    
    // Recursively invert left and right subtrees
    invertTreeRecursive(root.left);
    invertTreeRecursive(root.right);
    
    return root;
}
```

**Time Complexity:** O(n) - visit each node once  
**Space Complexity:** O(h) - recursion stack where h is height of tree

### Approach 2: Iterative BFS (Level Order)

**Algorithm:**
1. Use a queue to traverse tree level by level
2. For each node, swap its left and right children
3. Add children to queue for further processing

**Implementation:**
```java
public TreeNode invertTreeBFS(TreeNode root) {
    if (root == null) return null;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        
        // Swap left and right children
        TreeNode temp = node.left;
        node.left = node.right;
        node.right = temp;
        
        // Add children to queue
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
    
    return root;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(w) where w is maximum width of tree

### Approach 3: Iterative DFS (Using Stack)

**Algorithm:**
1. Use a stack to simulate recursive DFS
2. For each node, swap children and push them to stack

**Implementation:**
```java
public TreeNode invertTreeDFS(TreeNode root) {
    if (root == null) return null;
    
    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);
    
    while (!stack.isEmpty()) {
        TreeNode node = stack.pop();
        
        // Swap children
        TreeNode temp = node.left;
        node.left = node.right;
        node.right = temp;
        
        // Push children to stack
        if (node.left != null) stack.push(node.left);
        if (node.right != null) stack.push(node.right);
    }
    
    return root;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(h)

### Approach 4: Recursive Post-order

**Algorithm:**
1. Recursively invert left and right subtrees first
2. Then swap the inverted subtrees

**Implementation:**
```java
public TreeNode invertTreePostOrder(TreeNode root) {
    if (root == null) return null;
    
    // First invert the subtrees
    TreeNode left = invertTreePostOrder(root.left);
    TreeNode right = invertTreePostOrder(root.right);
    
    // Then swap them
    root.left = right;
    root.right = left;
    
    return root;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(h)

## Key Insights

1. **Tree Inversion:** Inverting a binary tree means swapping the left and right children of every node in the tree.

2. **Multiple Approaches:** This problem can be solved using various tree traversal methods (DFS, BFS, recursive, iterative).

3. **In-place Operation:** All approaches modify the tree structure in-place and return the original root.

4. **Base Case:** Always handle null root as the base case.

## When to Use Each Approach

- **Recursive DFS:** Most intuitive and clean solution. Preferred for interviews.
- **Iterative BFS:** When you want to avoid recursion or process level by level.
- **Iterative DFS:** When you want to avoid recursion but prefer depth-first processing.
- **Post-order Recursive:** When you prefer to process children before parent.

## Common Mistakes

1. **Forgetting Base Case:** Not handling null root
2. **Double Swapping:** Swapping children twice in recursive calls
3. **Memory Issues:** Not considering space complexity of recursive approaches

## Test Cases

```java
// Test cases to verify solution
TreeNode test1 = [4,2,7,1,3,6,9] → [4,7,2,9,6,3,1]
TreeNode test2 = [2,1,3] → [2,3,1]
TreeNode test3 = [1] → [1]
TreeNode test4 = [] → []
TreeNode test5 = [1,2,null,3,null,4] → [1,null,2,null,3,null,4]
```

## Related Problems

- **Binary Tree Level Order Traversal** - Uses similar traversal techniques
- **Binary Tree Right Side View** - Tree traversal with specific ordering
- **Symmetric Tree** - Tree structure comparison
- **Maximum Depth of Binary Tree** - Basic tree traversal

## Interview Tips

1. **Start Simple:** Begin with recursive solution as it's most intuitive
2. **Discuss Trade-offs:** Mention space complexity differences between approaches
3. **Edge Cases:** Always consider null tree and single node cases
4. **Follow-up Questions:** Be prepared to implement iterative version if asked
5. **Verification:** Walk through example to verify your solution works correctly

## LeetCode Similar Problems:
- [101. Symmetric Tree](https://leetcode.com/problems/symmetric-tree/)
- [100. Same Tree](https://leetcode.com/problems/same-tree/)
- [951. Flip Equivalent Binary Trees](https://leetcode.com/problems/flip-equivalent-binary-trees/)
- [971. Flip Binary Tree To Match Preorder Traversal](https://leetcode.com/problems/flip-binary-tree-to-match-preorder-traversal/)

This problem is excellent for demonstrating understanding of tree traversal algorithms and is frequently asked in technical interviews due to its simplicity and multiple solution approaches. 