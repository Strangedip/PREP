# Kth Smallest Element in BST

## Problem Statement

Given the root of a binary search tree, and an integer k, return the **kth smallest value** (1-indexed) of all the values of the nodes in the tree.

## Examples

**Example 1:**
```
Input: root = [3,1,4,null,2], k = 1
Output: 1

BST Structure:
    3
   / \
  1   4
   \
    2

Inorder: [1, 2, 3, 4]
1st smallest = 1
```

**Example 2:**
```
Input: root = [5,3,6,2,4,null,null,1], k = 3
Output: 3

BST Structure:
      5
     / \
    3   6
   / \
  2   4
 /
1

Inorder: [1, 2, 3, 4, 5, 6]
3rd smallest = 3
```

## Constraints

- The number of nodes in the tree is n
- 1 ≤ k ≤ n ≤ 10⁴
- 0 ≤ Node.val ≤ 10⁴

## Key Insight

**BST Property:** Inorder traversal of a BST gives nodes in **sorted ascending order**

Therefore, the kth smallest element is the kth element in the inorder traversal.

## Solutions

### Approach 1: Complete Inorder Traversal ⭐ (Simple)

**Algorithm:**
1. Perform complete inorder traversal
2. Store all elements in a list
3. Return the kth element (index k-1)

**Implementation:**
```java
public int kthSmallest(TreeNode root, int k) {
    List<Integer> inorder = new ArrayList<>();
    inorderTraversal(root, inorder);
    return inorder.get(k - 1);
}

private void inorderTraversal(TreeNode node, List<Integer> result) {
    if (node != null) {
        inorderTraversal(node.left, result);
        result.add(node.val);
        inorderTraversal(node.right, result);
    }
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n) for storing all elements

**Pros:** Simple, handles multiple queries efficiently  
**Cons:** Uses extra space, processes all nodes even if k is small

### Approach 2: Optimized Inorder with Early Termination ⭐ (Recommended)

**Algorithm:**
1. Perform inorder traversal
2. Count nodes as we visit them
3. Stop when we reach the kth node

**Implementation:**
```java
private int count = 0;
private int result = 0;

public int kthSmallestOptimized(TreeNode root, int k) {
    count = 0;
    inorderOptimized(root, k);
    return result;
}

private void inorderOptimized(TreeNode node, int k) {
    if (node == null) return;
    
    inorderOptimized(node.left, k);
    
    count++;
    if (count == k) {
        result = node.val;
        return;
    }
    
    inorderOptimized(node.right, k);
}
```

**Time Complexity:** O(H + k) where H is height  
**Space Complexity:** O(H) for recursion stack

**Pros:** Early termination, optimal for small k  
**Cons:** Still uses recursion stack

### Approach 3: Iterative Inorder with Stack ⭐ (Best for Large Trees)

**Algorithm:**
1. Use stack to simulate inorder traversal
2. Process nodes one by one until kth element

**Implementation:**
```java
public int kthSmallestIterative(TreeNode root, int k) {
    Stack<TreeNode> stack = new Stack<>();
    TreeNode current = root;
    int count = 0;
    
    while (current != null || !stack.isEmpty()) {
        // Go to leftmost node
        while (current != null) {
            stack.push(current);
            current = current.left;
        }
        
        // Process node
        current = stack.pop();
        count++;
        
        if (count == k) {
            return current.val;
        }
        
        // Move to right subtree
        current = current.right;
    }
    
    return -1;
}
```

**Time Complexity:** O(H + k)  
**Space Complexity:** O(H) for stack

**Pros:** No recursion, early termination, memory efficient  
**Cons:** Slightly more complex implementation

### Approach 4: Morris Traversal (Constant Space)

**Algorithm:**
1. Use Morris traversal for inorder without extra space
2. Temporarily modify tree structure using threading

**Implementation:**
```java
public int kthSmallestMorris(TreeNode root, int k) {
    TreeNode current = root;
    int count = 0;
    
    while (current != null) {
        if (current.left == null) {
            // Process current node
            count++;
            if (count == k) {
                return current.val;
            }
            current = current.right;
        } else {
            // Find inorder predecessor
            TreeNode predecessor = current.left;
            while (predecessor.right != null && predecessor.right != current) {
                predecessor = predecessor.right;
            }
            
            if (predecessor.right == null) {
                // Create thread
                predecessor.right = current;
                current = current.left;
            } else {
                // Remove thread and process node
                predecessor.right = null;
                count++;
                if (count == k) {
                    return current.val;
                }
                current = current.right;
            }
        }
    }
    
    return -1;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(1) - constant space!

**Pros:** Constant space complexity  
**Cons:** Temporarily modifies tree, more complex

### Approach 5: Binary Search with Node Counting

**Algorithm:**
1. Count nodes in left subtree
2. Use BST properties to decide which subtree to search
3. Recursively search appropriate subtree

**Implementation:**
```java
public int kthSmallestBinarySearch(TreeNode root, int k) {
    int leftCount = countNodes(root.left);
    
    if (k <= leftCount) {
        // kth smallest is in left subtree
        return kthSmallestBinarySearch(root.left, k);
    } else if (k == leftCount + 1) {
        // current node is kth smallest
        return root.val;
    } else {
        // kth smallest is in right subtree
        return kthSmallestBinarySearch(root.right, k - leftCount - 1);
    }
}

private int countNodes(TreeNode node) {
    if (node == null) return 0;
    return 1 + countNodes(node.left) + countNodes(node.right);
}
```

**Time Complexity:** O(n) worst case due to counting  
**Space Complexity:** O(H)

**Pros:** Uses BST properties directly  
**Cons:** Not efficient due to repeated counting

### Approach 6: Augmented BST (For Frequent Queries) ⭐

**Algorithm:**
1. Store count of left subtree nodes in each node
2. Use stored counts for O(H) queries

**Implementation:**
```java
class AugmentedTreeNode {
    int val;
    int leftCount; // Number of nodes in left subtree
    AugmentedTreeNode left, right;
}

public int kthSmallestAugmented(AugmentedTreeNode root, int k) {
    if (k <= root.leftCount) {
        return kthSmallestAugmented(root.left, k);
    } else if (k == root.leftCount + 1) {
        return root.val;
    } else {
        return kthSmallestAugmented(root.right, k - root.leftCount - 1);
    }
}
```

**Time Complexity:** O(H) for query, O(H) for insert/delete  
**Space Complexity:** O(1) additional per node

**Pros:** Optimal for frequent queries, handles updates efficiently  
**Cons:** Requires modifying tree structure

## Performance Comparison

| Approach | Time | Space | Early Stop | Const Space | Best Use Case |
|----------|------|-------|------------|-------------|---------------|
| Complete Inorder | O(n) | O(n) | ❌ | ❌ | Multiple queries |
| Optimized Inorder | O(H+k) | O(H) | ✅ | ❌ | Small k values |
| Iterative Stack | O(H+k) | O(H) | ✅ | ❌ | General purpose |
| Morris Traversal | O(n) | O(1) | ✅ | ✅ | Space-constrained |
| Binary Search | O(n) | O(H) | ❌ | ❌ | Not recommended |
| Augmented BST | O(H) | O(1) | ✅ | ✅ | Frequent queries |

## Advanced Applications

### 1. Find Median in BST

```java
public double findMedian(TreeNode root) {
    int n = countNodes(root);
    
    if (n % 2 == 1) {
        return kthSmallest(root, n / 2 + 1);
    } else {
        int mid1 = kthSmallest(root, n / 2);
        int mid2 = kthSmallest(root, n / 2 + 1);
        return (mid1 + mid2) / 2.0;
    }
}
```

### 2. Find Kth Largest Element

```java
public int kthLargest(TreeNode root, int k) {
    int totalNodes = countNodes(root);
    return kthSmallest(root, totalNodes - k + 1);
}
```

### 3. Range Queries (Find elements from kth to mth smallest)

```java
public List<Integer> findRange(TreeNode root, int k1, int k2) {
    List<Integer> result = new ArrayList<>();
    findRangeHelper(root, k1, k2, result);
    return result;
}
```

## Follow-up: Optimizing for Frequent Operations

**Problem:** If the BST is modified frequently and you need to find kth smallest often, how would you optimize?

**Solution:** Use **Augmented BST**

### Benefits of Augmented BST:

1. **Query Time:** O(H) instead of O(H + k)
2. **Insert/Delete:** Still O(H) with count maintenance
3. **Space Overhead:** Minimal (one integer per node)

### Implementation Strategy:

```java
// During insertion
public AugmentedTreeNode insert(AugmentedTreeNode root, int val) {
    if (root == null) {
        return new AugmentedTreeNode(val);
    }
    
    if (val < root.val) {
        root.left = insert(root.left, val);
        root.leftCount++; // Increment left count
    } else if (val > root.val) {
        root.right = insert(root.right, val);
    }
    
    return root;
}
```

## When to Use Each Approach

### Use Iterative Stack When:
- General-purpose solution needed
- Tree height is reasonable
- Memory usage is moderate concern

### Use Morris Traversal When:
- Memory is extremely constrained
- Constant space is required
- Tree modifications are acceptable

### Use Augmented BST When:
- Frequent kth smallest queries
- Tree is modified often
- Can modify tree structure

### Use Complete Inorder When:
- Multiple queries on same tree
- k values vary significantly
- Simplicity is preferred

## Common Mistakes

1. **Off-by-one errors:** Remember k is 1-indexed
2. **Not handling edge cases:** Empty tree, k > n
3. **Inefficient counting:** Recounting nodes repeatedly
4. **Stack overflow:** Deep recursion on skewed trees
5. **Not leveraging BST properties:** Using generic tree algorithms

## Edge Cases to Test

```java
// Test cases
1. Single node tree, k = 1
2. k = 1 (minimum element)
3. k = n (maximum element)
4. Balanced vs skewed trees
5. k > number of nodes
6. Duplicate values (if allowed)
```

## Related Problems

- **Validate Binary Search Tree** - BST property validation
- **Binary Tree Inorder Traversal** - Core traversal technique
- **Find Median from Data Stream** - Related median finding
- **Kth Largest Element in Array** - Similar k-selection problem
- **Binary Search Tree Iterator** - Iterative traversal patterns

## Interview Tips

1. **Start simple:** Begin with complete inorder traversal
2. **Optimize gradually:** Move to early termination, then iterative
3. **Discuss trade-offs:** Time vs space, simplicity vs efficiency
4. **Handle follow-ups:** Be ready to discuss augmented BST
5. **Test edge cases:** Verify with small examples
6. **Consider constraints:** Ask about frequency of operations

## Follow-up Questions

1. **"What if we need kth largest?"** → Transform: kth largest = (n-k+1)th smallest
2. **"Multiple queries on same tree?"** → Consider complete inorder or augmented BST
3. **"What if tree is modified frequently?"** → Augmented BST with counts
4. **"Constant space requirement?"** → Morris traversal
5. **"What about duplicates?"** → Clarify definition and adjust algorithm

This problem is excellent for demonstrating understanding of BST properties, tree traversal techniques, and algorithm optimization strategies. It's a popular choice in technical interviews due to its multiple solution approaches and practical applications. 