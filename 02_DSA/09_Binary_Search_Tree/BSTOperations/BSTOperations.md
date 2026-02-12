# Binary Search Tree Operations

## Problem Statement

Implement fundamental Binary Search Tree (BST) operations:
1. **Insert** a node into BST
2. **Search** for a value in BST
3. **Delete** a node from BST

## BST Properties

A Binary Search Tree is a binary tree with the following properties:
- **Left subtree** contains only nodes with values **less than** the node's value
- **Right subtree** contains only nodes with values **greater than** the node's value
- Both left and right subtrees are also binary search trees

## Examples

**Insert Operation:**
```
Insert 5 into BST [4,2,7,1,3]:
    4               4
   / \             / \
  2   7    →      2   7
 / \             / \ /
1   3           1  3 5
```

**Search Operation:**
```
Search 3 in BST [4,2,7,1,3]:
Start at 4 → go left to 2 → go right to 3 → FOUND
Result: true
```

**Delete Operation:**
```
Delete 2 from BST [4,2,7,1,3]:
    4               4
   / \             / \
  2   7    →      1   7  (or 3)
 / \               \   
1   3               3  (or 1)
```

## Constraints

- The number of nodes in the tree is in the range [0, 10⁴]
- -10⁸ ≤ Node.val ≤ 10⁸
- All values are unique (no duplicates)

## Solutions

### 1. Insert Operations

#### Approach 1: Recursive Insert ⭐ (Recommended)

**Algorithm:**
1. If tree is empty, create new node as root
2. If value < current node, insert in left subtree
3. If value > current node, insert in right subtree
4. If value equals current node, don't insert (no duplicates)

**Implementation:**
```java
public TreeNode insertBST(TreeNode root, int val) {
    if (root == null) {
        return new TreeNode(val);
    }
    
    if (val < root.val) {
        root.left = insertBST(root.left, val);
    } else if (val > root.val) {
        root.right = insertBST(root.right, val);
    }
    // If val == root.val, don't insert duplicates
    
    return root;
}
```

**Time Complexity:** O(h) where h is height
- Average case: O(log n) for balanced tree
- Worst case: O(n) for skewed tree

**Space Complexity:** O(h) for recursion stack

#### Approach 2: Iterative Insert

**Algorithm:**
1. Find the correct parent node iteratively
2. Insert new node as left or right child

**Implementation:**
```java
public TreeNode insertBSTIterative(TreeNode root, int val) {
    TreeNode newNode = new TreeNode(val);
    if (root == null) return newNode;
    
    TreeNode current = root, parent = null;
    
    while (current != null) {
        parent = current;
        if (val < current.val) {
            current = current.left;
        } else if (val > current.val) {
            current = current.right;
        } else {
            return root; // Value exists, don't insert
        }
    }
    
    if (val < parent.val) {
        parent.left = newNode;
    } else {
        parent.right = newNode;
    }
    
    return root;
}
```

**Time Complexity:** O(h)  
**Space Complexity:** O(1)

### 2. Search Operations

#### Approach 1: Recursive Search

**Algorithm:**
1. If node is null, return false
2. If value equals current node, return true
3. If value < current node, search left subtree
4. If value > current node, search right subtree

**Implementation:**
```java
public boolean searchBST(TreeNode root, int val) {
    if (root == null) return false;
    
    if (val == root.val) return true;
    
    if (val < root.val) {
        return searchBST(root.left, val);
    } else {
        return searchBST(root.right, val);
    }
}
```

**Time Complexity:** O(h)  
**Space Complexity:** O(h)

#### Approach 2: Iterative Search ⭐ (More Efficient)

**Implementation:**
```java
public boolean searchBSTIterative(TreeNode root, int val) {
    TreeNode current = root;
    
    while (current != null) {
        if (val == current.val) return true;
        
        if (val < current.val) {
            current = current.left;
        } else {
            current = current.right;
        }
    }
    
    return false;
}
```

**Time Complexity:** O(h)  
**Space Complexity:** O(1)

### 3. Delete Operations

**Three Cases for Deletion:**

#### Case 1: Node has no children (Leaf node)
- Simply remove the node

#### Case 2: Node has one child
- Replace node with its child

#### Case 3: Node has two children
- Replace with inorder successor (or predecessor)
- Delete the successor

**Implementation:**
```java
public TreeNode deleteNode(TreeNode root, int key) {
    if (root == null) return null;
    
    if (key < root.val) {
        root.left = deleteNode(root.left, key);
    } else if (key > root.val) {
        root.right = deleteNode(root.right, key);
    } else {
        // Found node to delete
        
        // Case 1: No children
        if (root.left == null && root.right == null) {
            return null;
        }
        
        // Case 2: One child
        if (root.left == null) return root.right;
        if (root.right == null) return root.left;
        
        // Case 3: Two children
        TreeNode successor = findMin(root.right);
        root.val = successor.val;
        root.right = deleteNode(root.right, successor.val);
    }
    
    return root;
}

private TreeNode findMin(TreeNode root) {
    while (root.left != null) {
        root = root.left;
    }
    return root;
}
```

**Time Complexity:** O(h)  
**Space Complexity:** O(h)

## Advanced Operations

### Find Minimum/Maximum

```java
public TreeNode findMin(TreeNode root) {
    if (root == null) return null;
    while (root.left != null) {
        root = root.left;
    }
    return root;
}

public TreeNode findMax(TreeNode root) {
    if (root == null) return null;
    while (root.right != null) {
        root = root.right;
    }
    return root;
}
```

### Find Inorder Successor

```java
public TreeNode findSuccessor(TreeNode root, int val) {
    TreeNode successor = null;
    
    while (root != null) {
        if (val < root.val) {
            successor = root;
            root = root.left;
        } else {
            root = root.right;
        }
    }
    
    return successor;
}
```

### Validate BST

```java
public boolean isValidBST(TreeNode root) {
    return isValidBSTHelper(root, Long.MIN_VALUE, Long.MAX_VALUE);
}

private boolean isValidBSTHelper(TreeNode node, long min, long max) {
    if (node == null) return true;
    
    if (node.val <= min || node.val >= max) return false;
    
    return isValidBSTHelper(node.left, min, node.val) &&
           isValidBSTHelper(node.right, node.val, max);
}
```

## Performance Analysis

### Time Complexity Summary

| Operation | Average Case | Worst Case | Best Case |
|-----------|--------------|------------|-----------|
| Insert | O(log n) | O(n) | O(1) |
| Search | O(log n) | O(n) | O(1) |
| Delete | O(log n) | O(n) | O(1) |
| Find Min/Max | O(log n) | O(n) | O(1) |

### Space Complexity
- **Recursive:** O(h) for call stack
- **Iterative:** O(1) auxiliary space

### When Each Case Occurs

**Best Case (O(1)):** Root operation  
**Average Case (O(log n)):** Balanced BST  
**Worst Case (O(n)):** Completely skewed tree (essentially a linked list)

## BST vs Other Data Structures

| Operation | BST | Array | Linked List | Hash Table |
|-----------|-----|-------|-------------|------------|
| Search | O(log n) | O(log n)* | O(n) | O(1)** |
| Insert | O(log n) | O(n) | O(1) | O(1)** |
| Delete | O(log n) | O(n) | O(n) | O(1)** |
| Sorted Order | ✓ | ✓* | ✗ | ✗ |

*Sorted array  
**Average case

## Common Mistakes

1. **Not handling null nodes:** Always check for null before accessing node properties
2. **Incorrect BST property:** Ensure left < node < right at every level
3. **Memory leaks:** In languages with manual memory management, free deleted nodes
4. **Duplicate handling:** Decide policy for duplicate values
5. **Wrong successor/predecessor:** In deletion, ensure correct replacement node

## Implementation Tips

### Recursive vs Iterative Choice

**Use Recursive When:**
- Code readability is important
- Tree height is guaranteed to be small
- Natural divide-and-conquer approach

**Use Iterative When:**
- Memory is constrained
- Stack overflow is a concern
- Performance is critical

### Error Handling

```java
public TreeNode insertBST(TreeNode root, int val) {
    // Validate input
    if (val < Integer.MIN_VALUE || val > Integer.MAX_VALUE) {
        throw new IllegalArgumentException("Value out of range");
    }
    
    // Rest of implementation...
}
```

## Test Cases

```java
// Basic operations
TreeNode root = null;
root = insertBST(root, 4);     // [4]
root = insertBST(root, 2);     // [4,2]
root = insertBST(root, 7);     // [4,2,7]

// Search operations
searchBST(root, 2);    // true
searchBST(root, 5);    // false

// Delete operations
root = deleteNode(root, 2);    // Remove leaf
root = deleteNode(root, 4);    // Remove root with two children

// Edge cases
insertBST(null, 1);            // Insert into empty tree
deleteNode(singleNode, val);   // Delete only node
searchBST(null, 1);           // Search empty tree
```

## Related Problems

- **Validate Binary Search Tree** - Verification of BST property
- **Lowest Common Ancestor in BST** - Finding LCA using BST properties
- **Kth Smallest Element in BST** - Inorder traversal applications
- **Convert Sorted Array to BST** - BST construction
- **BST Iterator** - Inorder traversal with iterator pattern

## Interview Tips

1. **Start with clarifications:** Ask about duplicates, null handling, balancing requirements
2. **Choose approach wisely:** Iterative for better space complexity, recursive for clarity
3. **Handle all cases:** Empty tree, single node, balanced vs skewed trees
4. **Discuss trade-offs:** Time vs space complexity, recursive vs iterative
5. **Test thoroughly:** Include edge cases and verify BST property after operations
6. **Consider follow-ups:** Self-balancing trees (AVL, Red-Black), thread-safe operations

## Follow-up Questions

1. **"How would you balance the BST?"** → Discuss AVL trees, Red-Black trees
2. **"What if we need range queries?"** → Segment trees, interval trees
3. **"How to make it thread-safe?"** → Locking strategies, lock-free algorithms
4. **"What about duplicate values?"** → Design decisions for handling duplicates

BST operations are fundamental for understanding tree-based data structures and are frequently tested in technical interviews due to their practical applications and algorithmic complexity. 