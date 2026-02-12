# Lowest Common Ancestor of a Binary Tree

## Problem Statement

Given a binary tree, find the lowest common ancestor (LCA) of two given nodes `p` and `q`. The LCA is defined as the lowest node in the tree that has both `p` and `q` as descendants (where we allow a node to be a descendant of itself).

**LeetCode**: [236. Lowest Common Ancestor of a Binary Tree](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/)

### Examples

```
        3
       / \
      5   1
     / \ / \
    6  2 0  8
      / \
     7   4

Input:  root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 1
Output: 3
Explanation: The LCA of nodes 5 and 1 is 3.

Input:  root = [3,5,1,6,2,0,8,null,null,7,4], p = 5, q = 4
Output: 5
Explanation: The LCA of nodes 5 and 4 is 5, since a node can be a descendant of itself.
```

### Constraints
- Number of nodes in the tree is in `[2, 10^5]`
- `-10^9 <= Node.val <= 10^9`
- All `Node.val` are **unique**
- `p != q`
- Both `p` and `q` exist in the tree

---

## Why This Problem Is Important

1. **Top 25 interview problem** — asked at virtually every FAANG company.
2. Tests recursive thinking and understanding of tree traversal.
3. The LCA concept appears in system design (organizational hierarchies, routing, version control merge-base).
4. The BST variant (LeetCode 235) is different and easier — you must know both.

---

## LCA Binary Tree vs LCA BST

| Feature | LCA Binary Tree (this problem) | LCA BST (LeetCode 235) |
|---------|-------------------------------|------------------------|
| Tree type | General binary tree | Binary Search Tree |
| Approach | DFS — search both subtrees | Use BST property to decide direction |
| Time | O(n) | O(h) where h = height |
| Key insight | LCA is where p and q are in different subtrees | LCA is where p.val <= node.val <= q.val |

---

## Approach 1: Recursive DFS (Optimal)

**Time**: O(n), **Space**: O(h) — recursion stack

### The Key Insight

For any node, exactly one of these is true:
1. **Both p and q are in the left subtree** → LCA is in the left subtree.
2. **Both p and q are in the right subtree** → LCA is in the right subtree.
3. **p is in one subtree and q is in the other** → current node IS the LCA.
4. **Current node is p or q** → current node IS the LCA (the other node must be below).

### How the Algorithm Works

1. Recursively search left and right subtrees.
2. If the left search returns non-null and the right search returns non-null, the current node is the LCA.
3. If only one side returns non-null, propagate that result upward.
4. If the current node is p or q, return it immediately.

### Visual Walkthrough: Find LCA(5, 4)

```
        3
       / \
      5   1
     / \ / \
    6  2 0  8
      / \
     7   4

Call: lca(3, 5, 4)
├── Call: lca(5, 5, 4)     → node 5 == p, return 5 immediately
│   (left returns 5)
├── Call: lca(1, 5, 4)
│   ├── Call: lca(0, 5, 4) → returns null (leaf, not p or q)
│   └── Call: lca(8, 5, 4) → returns null (leaf, not p or q)
│   (both null → returns null)
│   (right returns null)
│
│ left = 5 (non-null), right = null → return left (5)

Answer: 5 ✓ (5 is ancestor of 4, and 5 is a descendant of itself)
```

### Visual Walkthrough: Find LCA(5, 1)

```
        3
       / \
      5   1

Call: lca(3, 5, 1)
├── Call: lca(5, 5, 1) → node 5 == p, return 5
│   (left = 5)
├── Call: lca(1, 5, 1) → node 1 == q, return 1
│   (right = 1)
│
│ left = 5 (non-null), right = 1 (non-null) → BOTH sides found → return current node 3

Answer: 3 ✓ (p and q are in different subtrees of 3)
```

### Java Implementation

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Base case: reached a leaf's child, or found p or q
        if (root == null || root == p || root == q) {
            return root;
        }

        // Recursively search left and right subtrees
        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        // If both sides return non-null, current node is the LCA
        if (left != null && right != null) {
            return root;
        }

        // If only one side found something, propagate it upward
        return left != null ? left : right;
    }
}
```

> **Why this works in 7 lines**: The elegance is that we do not need to explicitly track paths. The recursion naturally "bubbles up" the answer. When p and q are found in different subtrees, the current node is returned. When they are in the same subtree, the deeper found node propagates up.

---

## Approach 2: Iterative with Parent Pointers

**Time**: O(n), **Space**: O(n)

Build a parent map, find the path from p to root, then walk q up to find the first common ancestor.

```java
class Solution {
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Step 1: Build parent map using BFS
        Map<TreeNode, TreeNode> parentMap = new HashMap<>();
        parentMap.put(root, null);

        Deque<TreeNode> stack = new ArrayDeque<>();
        stack.push(root);

        // Traverse until we find both p and q
        while (!parentMap.containsKey(p) || !parentMap.containsKey(q)) {
            TreeNode node = stack.pop();
            if (node.left != null) {
                parentMap.put(node.left, node);
                stack.push(node.left);
            }
            if (node.right != null) {
                parentMap.put(node.right, node);
                stack.push(node.right);
            }
        }

        // Step 2: Build set of all ancestors of p
        Set<TreeNode> ancestors = new HashSet<>();
        TreeNode current = p;
        while (current != null) {
            ancestors.add(current);
            current = parentMap.get(current);
        }

        // Step 3: Walk q up until we find a common ancestor
        current = q;
        while (!ancestors.contains(current)) {
            current = parentMap.get(current);
        }

        return current;
    }
}
```

### When to Use This Approach
- When you need to find LCA for multiple queries (build parent map once, reuse).
- When the tree is given as an adjacency list rather than a TreeNode structure.
- When recursion depth is a concern (very deep trees).

---

## Approach 3: Euler Tour + Sparse Table (For Multiple Queries)

**Time**: O(n) preprocessing, O(1) per query. **Space**: O(n log n)

This advanced approach is used when you need to answer many LCA queries efficiently. It converts the problem to a Range Minimum Query (RMQ).

> **Interview Note**: You do not need to implement this in an interview. Mention it if asked: "For multiple LCA queries, I would use Euler Tour + Sparse Table for O(1) per query after O(n log n) preprocessing."

---

## Complexity Analysis

| Approach | Time | Space | Best For |
|----------|------|-------|---------|
| Recursive DFS | O(n) | O(h) | Single query — preferred in interviews |
| Iterative with Parent Map | O(n) | O(n) | When recursion depth is a concern |
| Euler Tour + Sparse Table | O(n log n) prep, O(1) query | O(n log n) | Multiple LCA queries |

Where n = number of nodes, h = height of the tree (h = O(log n) for balanced, O(n) for skewed).

---

## Edge Cases

| Case | p | q | LCA | Why |
|------|---|---|-----|-----|
| p is ancestor of q | 5 | 4 | 5 | A node is a descendant of itself |
| q is ancestor of p | 4 | 5 | 5 | Same as above, just swapped |
| p and q are siblings | 6 | 2 | 5 | Direct parent is the LCA |
| p and q are the root's children | 5 | 1 | 3 | Root is the LCA |
| Tree has only 2 nodes | root | child | root | Root is always the LCA for root and any descendant |
| p = root | 3 | 7 | 3 | Root is ancestor of all nodes |

---

## Interview Tips

1. **Clarify**: "Is this a BST or a general binary tree?" — this determines the approach (BST allows O(h) using value comparisons).
2. **Explain the three cases**: "If I find p on the left and q on the right, the current node is the LCA. If both are on the same side, the LCA is deeper."
3. **Trace through an example**: Walk through 2-3 recursive calls to show the interviewer you understand the recursion.
4. **State the time complexity**: "O(n) because we visit each node at most once."
5. **Common follow-up**: "What if nodes might not exist in the tree?" — Then you need to verify existence first, or modify the algorithm to track whether both nodes were found.

---

## Related Problems

| Problem | Key Difference | LeetCode |
|---------|---------------|----------|
| LCA of BST | Use BST property for O(h) time | [235](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/) |
| LCA with Parent Pointers | Each node has a parent pointer (like finding intersection of two linked lists) | [1650](https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iii/) |
| LCA of Deepest Leaves | Find LCA of all deepest leaf nodes | [1123](https://leetcode.com/problems/lowest-common-ancestor-of-deepest-leaves/) |
| Distance Between Nodes | Find LCA first, then compute distances | Custom |

---

## Real-World Applications

| Application | How LCA Is Used |
|-------------|----------------|
| **Version Control (Git)** | Finding the merge-base (common ancestor) of two branches |
| **Organizational Hierarchies** | Finding the common manager of two employees |
| **File Systems** | Finding the common parent directory of two files |
| **Networking** | Finding the common router in a network topology |
| **Phylogenetics** | Finding the common ancestor in evolutionary trees |
| **DOM Manipulation** | Finding the closest common parent of two HTML elements |

---

**Pattern**: DFS (Post-Order Traversal)
**Difficulty**: Medium
**Must-Know**: Yes — Top 25 problem, fundamental tree concept

