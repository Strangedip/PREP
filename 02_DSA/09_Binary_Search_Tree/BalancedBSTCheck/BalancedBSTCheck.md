# Balanced Binary Search Tree Check

## Problem Statement

Given a binary tree, determine if it is **height-balanced**.

A height-balanced binary tree is defined as a binary tree in which the left and right subtrees of **every node** differ in height by no more than 1.

## Examples

**Example 1: Balanced Tree**
```
Input: root = [3,9,20,null,null,15,7]
Output: true

Tree Structure:
    3
   / \
  9  20
    /  \
   15   7

Heights: Left subtree = 1, Right subtree = 2
Difference = |1-2| = 1 ≤ 1 ✓ Balanced
```

**Example 2: Unbalanced Tree**
```
Input: root = [1,2,2,3,3,null,null,4,4]
Output: false

Tree Structure:
       1
      / \
     2   2
    / \
   3   3
  / \
 4   4

Heights: Left subtree = 3, Right subtree = 1
Difference = |3-1| = 2 > 1 ✗ Not Balanced
```

**Example 3: Empty Tree**
```
Input: root = []
Output: true
```

## Constraints

- The number of nodes in the tree is in the range [0, 5000]
- -10⁴ ≤ Node.val ≤ 10⁴

## Key Concepts

### Height-Balanced Definition
A tree is height-balanced if **for every node**:
- The absolute difference between heights of left and right subtrees ≤ 1
- Both left and right subtrees are also height-balanced

### Tree Height
- **Empty tree:** height = 0
- **Single node:** height = 1
- **General case:** height = 1 + max(left_height, right_height)

## Solutions

### Approach 1: Top-Down Recursive (Naive)

**Algorithm:**
1. For each node, calculate heights of left and right subtrees
2. Check if height difference ≤ 1
3. Recursively check if both subtrees are balanced

**Implementation:**
```java
public boolean isBalanced(TreeNode root) {
    if (root == null) return true;
    
    int leftHeight = height(root.left);
    int rightHeight = height(root.right);
    
    return Math.abs(leftHeight - rightHeight) <= 1 &&
           isBalanced(root.left) &&
           isBalanced(root.right);
}

private int height(TreeNode node) {
    if (node == null) return 0;
    return 1 + Math.max(height(node.left), height(node.right));
}
```

**Time Complexity:** O(n²) - height calculation repeated for each node  
**Space Complexity:** O(n) - recursion stack

**Problems:** Inefficient due to repeated height calculations

### Approach 2: Bottom-Up Recursive (Optimized) ⭐ (Recommended)

**Algorithm:**
1. Calculate height and check balance in single traversal
2. Return -1 if subtree is unbalanced, height otherwise
3. Early termination when imbalance is detected

**Implementation:**
```java
public boolean isBalancedOptimized(TreeNode root) {
    return checkBalanceAndHeight(root) != -1;
}

private int checkBalanceAndHeight(TreeNode node) {
    if (node == null) return 0;
    
    // Check left subtree
    int leftHeight = checkBalanceAndHeight(node.left);
    if (leftHeight == -1) return -1; // Left subtree unbalanced
    
    // Check right subtree
    int rightHeight = checkBalanceAndHeight(node.right);
    if (rightHeight == -1) return -1; // Right subtree unbalanced
    
    // Check current node balance
    if (Math.abs(leftHeight - rightHeight) > 1) {
        return -1; // Current subtree unbalanced
    }
    
    // Return height of current subtree
    return 1 + Math.max(leftHeight, rightHeight);
}
```

**Time Complexity:** O(n) - each node visited once  
**Space Complexity:** O(n) - recursion stack

**Advantages:** Single traversal, early termination, optimal time complexity

### Approach 3: Iterative with Stack

**Algorithm:**
1. Use post-order traversal with stack
2. Calculate heights and check balance iteratively
3. Store heights in hash map

**Implementation:**
```java
public boolean isBalancedIterative(TreeNode root) {
    if (root == null) return true;
    
    Stack<TreeNode> stack = new Stack<>();
    Map<TreeNode, Integer> heights = new HashMap<>();
    
    TreeNode current = root;
    TreeNode lastVisited = null;
    
    while (!stack.isEmpty() || current != null) {
        if (current != null) {
            stack.push(current);
            current = current.left;
        } else {
            TreeNode peekNode = stack.peek();
            
            if (peekNode.right != null && lastVisited != peekNode.right) {
                current = peekNode.right;
            } else {
                // Process current node
                int leftHeight = heights.getOrDefault(peekNode.left, 0);
                int rightHeight = heights.getOrDefault(peekNode.right, 0);
                
                if (Math.abs(leftHeight - rightHeight) > 1) {
                    return false;
                }
                
                heights.put(peekNode, 1 + Math.max(leftHeight, rightHeight));
                lastVisited = stack.pop();
            }
        }
    }
    
    return true;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n) - stack + hash map

**Use case:** When avoiding recursion is required

### Approach 4: Level-Order Traversal (BFS)

**Algorithm:**
1. Calculate heights for all nodes using BFS
2. Check balance condition for each node

**Implementation:**
```java
public boolean isBalancedBFS(TreeNode root) {
    if (root == null) return true;
    
    Map<TreeNode, Integer> heights = new HashMap<>();
    
    // Calculate heights
    calculateHeightsBFS(root, heights);
    
    // Check balance for all nodes
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        TreeNode node = queue.poll();
        
        int leftHeight = heights.getOrDefault(node.left, 0);
        int rightHeight = heights.getOrDefault(node.right, 0);
        
        if (Math.abs(leftHeight - rightHeight) > 1) {
            return false;
        }
        
        if (node.left != null) queue.offer(node.left);
        if (node.right != null) queue.offer(node.right);
    }
    
    return true;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

**Use case:** When level-by-level processing is preferred

## Advanced Tree Analysis

### Complete Tree Property Analysis

```java
// Check multiple tree properties
public class TreeStats {
    int height;
    int nodes;
    int leaves;
    boolean isBalanced;
    boolean isValidBST;
    boolean isComplete;
    boolean isPerfect;
}

public TreeStats getTreeStatistics(TreeNode root) {
    TreeStats stats = new TreeStats();
    
    stats.height = height(root);
    stats.nodes = countNodes(root);
    stats.leaves = countLeaves(root);
    stats.isBalanced = isBalancedOptimized(root);
    stats.isValidBST = isValidBST(root);
    stats.isComplete = isCompleteTree(root);
    stats.isPerfect = isPerfectTree(root);
    
    return stats;
}
```

### Tree Type Classifications

| Tree Type | Definition | Balance Property |
|-----------|------------|------------------|
| **Balanced** | Height difference ≤ 1 for all nodes | ✓ Automatically balanced |
| **Complete** | All levels filled except possibly last | Usually balanced |
| **Perfect** | All internal nodes have 2 children | Always balanced |
| **Full** | Every node has 0 or 2 children | May or may not be balanced |

### Balance Visualization

```java
public void visualizeBalance(TreeNode root) {
    // Show balance factor for each node
    // Balance factor = height(left) - height(right)
    // Values: -1, 0, +1 indicate balanced
    // Values: ≤-2 or ≥+2 indicate unbalanced
}
```

## Performance Comparison

| Approach | Time | Space | Early Termination | Best Use Case |
|----------|------|-------|-------------------|---------------|
| Top-Down Recursive | O(n²) | O(n) | ❌ | Simple implementation |
| Bottom-Up Recursive | O(n) | O(n) | ✅ | **Recommended** |
| Iterative Stack | O(n) | O(n) | ✅ | Avoid recursion |
| BFS Approach | O(n) | O(n) | ❌ | Level-wise processing |

## Real-World Applications

### 1. AVL Tree Validation
```java
// AVL trees maintain balance automatically
// This check validates AVL property
public boolean isValidAVL(TreeNode root) {
    return isBalancedOptimized(root) && isValidBST(root);
}
```

### 2. Database Index Optimization
```java
// Database B-trees need balance for optimal performance
// Periodic balance checking ensures query efficiency
```

### 3. Decision Tree Analysis
```java
// Machine learning decision trees benefit from balance
// Prevents overfitting and improves generalization
```

## Common Mistakes

1. **Incorrect Height Calculation:** Forgetting to add 1 for current node
2. **Wrong Base Case:** Not handling null nodes properly
3. **Off-by-One Errors:** Using < instead of <= for balance condition
4. **Inefficient Algorithm:** Using O(n²) approach instead of O(n)
5. **Missing Early Termination:** Not stopping when imbalance is found

## Edge Cases

```java
// Test cases to consider:
1. Empty tree (null) → true
2. Single node → true
3. Two nodes (left child only) → true
4. Two nodes (right child only) → true
5. Perfect binary tree → true
6. Completely unbalanced (linear) → false
7. Almost balanced (difference = 2) → false
```

## Optimization Techniques

### 1. Early Termination
```java
// Stop as soon as imbalance is detected
if (leftHeight == -1 || rightHeight == -1) {
    return -1; // Propagate imbalance upward
}
```

### 2. Memoization (if multiple queries)
```java
// Cache heights to avoid recalculation
Map<TreeNode, Integer> heightCache = new HashMap<>();
```

### 3. Iterative Approach for Deep Trees
```java
// Avoid stack overflow for very deep trees
// Use explicit stack instead of recursion
```

## Related Problems

- **Maximum Depth of Binary Tree** - Height calculation
- **Diameter of Binary Tree** - Similar tree analysis
- **Validate Binary Search Tree** - Tree property validation
- **Symmetric Tree** - Tree structure comparison
- **Binary Tree Level Order Traversal** - Tree traversal patterns

## Interview Tips

1. **Start with brute force:** Explain O(n²) approach first
2. **Optimize to O(n):** Show bottom-up improvement
3. **Discuss trade-offs:** Recursive vs iterative approaches
4. **Handle edge cases:** Empty tree, single node, linear tree
5. **Consider follow-ups:** Other tree properties, optimization for multiple queries
6. **Draw examples:** Visual representation helps explain the concept

## Follow-up Questions

1. **"What if you need to check balance frequently?"** → Memoization or maintain balance info
2. **"How to make an unbalanced tree balanced?"** → Tree rotations, rebuild from sorted array
3. **"What's the difference from AVL trees?"** → AVL maintains balance automatically
4. **"How to detect the most unbalanced node?"** → Track maximum imbalance during traversal
5. **"What about other tree properties?"** → Extend to check complete, perfect, full trees

## Example Walkthrough

```java
Tree: [1,2,2,3,3,null,null,4,4]

       1           height = 4
      / \
     2   2         height = 3, 1
    / \
   3   3           height = 2, 1
  / \
 4   4             height = 1, 1

Analysis:
- Node 1: |3-1| = 2 > 1 → UNBALANCED ❌
- Result: false

Tree: [3,9,20,null,null,15,7]

    3              height = 3
   / \
  9  20            height = 1, 2
    /  \
   15   7          height = 1, 1

Analysis:
- Node 3: |1-2| = 1 ≤ 1 → BALANCED ✓
- Node 20: |1-1| = 0 ≤ 1 → BALANCED ✓
- Result: true
```

This problem is excellent for demonstrating understanding of tree properties, recursion optimization, and algorithm efficiency. It's frequently asked in technical interviews as it combines multiple important concepts in tree algorithms. 