# Construct Binary Tree from Traversals

## Problem Statement

Build binary trees from combinations of preorder, inorder, and postorder traversals.

This covers three main problems:
1. **Construct Binary Tree from Preorder and Inorder Traversal**
2. **Construct Binary Tree from Inorder and Postorder Traversal**  
3. **Construct Binary Tree from Preorder and Postorder Traversal**

## Examples

**Example 1: Preorder + Inorder**
```
Input: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
Output: [3,9,20,null,null,15,7]

Tree structure:
    3
   / \
  9  20
    /  \
   15   7
```

**Example 2: Inorder + Postorder**
```
Input: inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
Output: [3,9,20,null,null,15,7]
```

## Constraints

- 1 ≤ array.length ≤ 3000
- All values are unique
- Arrays have equal lengths

## Problem 1: Preorder + Inorder

### Approach 1: Recursive with HashMap ⭐ (Recommended)

**Key Insight:** 
- First element in preorder is always the root
- Find root in inorder to determine left/right subtree boundaries

**Algorithm:**
1. Create hashmap for O(1) inorder lookups
2. First element in preorder = root
3. Find root position in inorder
4. Split into left/right subtrees recursively

**Implementation:**
```java
public TreeNode buildTreePreIn(int[] preorder, int[] inorder) {
    Map<Integer, Integer> inorderMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inorderMap.put(inorder[i], i);
    }
    return buildHelper(preorder, 0, preorder.length - 1,
                      inorder, 0, inorder.length - 1, inorderMap);
}

private TreeNode buildHelper(int[] preorder, int preStart, int preEnd,
                           int[] inorder, int inStart, int inEnd,
                           Map<Integer, Integer> inorderMap) {
    if (preStart > preEnd || inStart > inEnd) return null;
    
    int rootVal = preorder[preStart];
    TreeNode root = new TreeNode(rootVal);
    
    int rootIndex = inorderMap.get(rootVal);
    int leftSize = rootIndex - inStart;
    
    root.left = buildHelper(preorder, preStart + 1, preStart + leftSize,
                           inorder, inStart, rootIndex - 1, inorderMap);
    root.right = buildHelper(preorder, preStart + leftSize + 1, preEnd,
                            inorder, rootIndex + 1, inEnd, inorderMap);
    return root;
}
```

**Time Complexity:** O(n) - each node processed once  
**Space Complexity:** O(n) - hashmap + recursion stack

### Approach 2: Iterative with Stack

**Algorithm:**
1. Use stack to track path from root
2. Use inorder array to determine when to create right children
3. Build tree in preorder sequence

**Implementation:**
```java
public TreeNode buildTreePreInIterative(int[] preorder, int[] inorder) {
    if (preorder.length == 0) return null;
    
    TreeNode root = new TreeNode(preorder[0]);
    Stack<TreeNode> stack = new Stack<>();
    stack.push(root);
    
    int inorderIndex = 0;
    
    for (int i = 1; i < preorder.length; i++) {
        TreeNode node = stack.peek();
        
        if (node.val != inorder[inorderIndex]) {
            // Create left child
            node.left = new TreeNode(preorder[i]);
            stack.push(node.left);
        } else {
            // Pop until we find correct parent for right child
            while (!stack.isEmpty() && stack.peek().val == inorder[inorderIndex]) {
                node = stack.pop();
                inorderIndex++;
            }
            node.right = new TreeNode(preorder[i]);
            stack.push(node.right);
        }
    }
    return root;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

## Problem 2: Inorder + Postorder

### Approach: Recursive with HashMap

**Key Insight:**
- Last element in postorder is always the root
- Find root in inorder to determine subtree boundaries

**Algorithm:**
1. Last element in postorder = root
2. Find root position in inorder
3. Recursively build left and right subtrees

**Implementation:**
```java
public TreeNode buildTreeInPost(int[] inorder, int[] postorder) {
    Map<Integer, Integer> inorderMap = new HashMap<>();
    for (int i = 0; i < inorder.length; i++) {
        inorderMap.put(inorder[i], i);
    }
    return buildInPostHelper(inorder, 0, inorder.length - 1,
                            postorder, 0, postorder.length - 1, inorderMap);
}

private TreeNode buildInPostHelper(int[] inorder, int inStart, int inEnd,
                                  int[] postorder, int postStart, int postEnd,
                                  Map<Integer, Integer> inorderMap) {
    if (inStart > inEnd || postStart > postEnd) return null;
    
    int rootVal = postorder[postEnd];  // Last element is root
    TreeNode root = new TreeNode(rootVal);
    
    int rootIndex = inorderMap.get(rootVal);
    int leftSize = rootIndex - inStart;
    
    root.left = buildInPostHelper(inorder, inStart, rootIndex - 1,
                                 postorder, postStart, postStart + leftSize - 1,
                                 inorderMap);
    root.right = buildInPostHelper(inorder, rootIndex + 1, inEnd,
                                  postorder, postStart + leftSize, postEnd - 1,
                                  inorderMap);
    return root;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

## Problem 3: Preorder + Postorder

### Important Note
⚠️ **Construction from preorder + postorder is NOT unique!**
- Multiple valid trees can have same preorder and postorder
- Solution assumes left-skewed preference when ambiguous

### Approach: Recursive with HashMap

**Key Insight:**
- First element in preorder = root
- Second element in preorder = left child (if exists)
- Use postorder to find subtree boundaries

**Implementation:**
```java
public TreeNode buildTreePrePost(int[] preorder, int[] postorder) {
    Map<Integer, Integer> postorderMap = new HashMap<>();
    for (int i = 0; i < postorder.length; i++) {
        postorderMap.put(postorder[i], i);
    }
    return buildPrePostHelper(preorder, 0, preorder.length - 1,
                             postorder, 0, postorder.length - 1, postorderMap);
}

private TreeNode buildPrePostHelper(int[] preorder, int preStart, int preEnd,
                                   int[] postorder, int postStart, int postEnd,
                                   Map<Integer, Integer> postorderMap) {
    if (preStart > preEnd) return null;
    
    TreeNode root = new TreeNode(preorder[preStart]);
    if (preStart == preEnd) return root;
    
    // Left child is second element in preorder
    int leftRootVal = preorder[preStart + 1];
    int leftRootPostIndex = postorderMap.get(leftRootVal);
    int leftSize = leftRootPostIndex - postStart + 1;
    
    root.left = buildPrePostHelper(preorder, preStart + 1, preStart + leftSize,
                                  postorder, postStart, leftRootPostIndex,
                                  postorderMap);
    root.right = buildPrePostHelper(preorder, preStart + leftSize + 1, preEnd,
                                   postorder, leftRootPostIndex + 1, postEnd - 1,
                                   postorderMap);
    return root;
}
```

**Time Complexity:** O(n)  
**Space Complexity:** O(n)

## Key Insights

### Traversal Properties
1. **Preorder:** Root → Left → Right
2. **Inorder:** Left → Root → Right  
3. **Postorder:** Left → Right → Root

### Construction Rules
- **Preorder + Inorder:** Unique construction possible
- **Inorder + Postorder:** Unique construction possible
- **Preorder + Postorder:** Multiple valid trees possible
- **Inorder + Any other:** Always unique (inorder provides left/right boundary)

### Why Inorder is Special
- Inorder traversal splits tree into left and right subtrees
- Elements before root = left subtree
- Elements after root = right subtree

## Common Patterns

### Root Identification
```java
// From preorder + inorder
int root = preorder[preStart];

// From inorder + postorder  
int root = postorder[postEnd];

// From preorder + postorder
int root = preorder[preStart];
```

### Subtree Size Calculation
```java
int rootIndex = inorderMap.get(rootVal);
int leftSize = rootIndex - inStart;
```

### Boundary Updates
```java
// Left subtree boundaries
leftPreStart = preStart + 1;
leftPreEnd = preStart + leftSize;
leftInStart = inStart;
leftInEnd = rootIndex - 1;

// Right subtree boundaries  
rightPreStart = preStart + leftSize + 1;
rightPreEnd = preEnd;
rightInStart = rootIndex + 1;
rightInEnd = inEnd;
```

## Performance Comparison

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| HashMap Optimized | O(n) | O(n) | Best performance |
| Linear Search | O(n²) | O(n) | Simple but slower |
| Iterative | O(n) | O(n) | Good for avoiding recursion |

## Common Mistakes

1. **Index Calculation Errors:** Off-by-one errors in boundary calculations
2. **HashMap Misuse:** Not handling duplicate values properly
3. **Base Case:** Forgetting null checks
4. **Subtree Size:** Incorrect calculation of left/right subtree sizes

## Test Cases

```java
// Standard case
preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]

// Single node
preorder = [1], inorder = [1]

// Left skewed
preorder = [1,2,3], inorder = [3,2,1]

// Right skewed  
preorder = [1,2,3], inorder = [1,2,3]

// Complete binary tree
preorder = [1,2,4,5,3,6,7], inorder = [4,2,5,1,6,3,7]
```

## Related Problems

- **Binary Tree Traversals** - Understanding traversal orders
- **Serialize and Deserialize Binary Tree** - Tree reconstruction
- **Recover Binary Search Tree** - Tree structure analysis
- **Binary Tree Maximum Path Sum** - Tree traversal applications

## Interview Tips

1. **Start with Examples:** Draw out small trees to understand the pattern
2. **Identify Root:** Know how to find root in each traversal type
3. **HashMap Optimization:** Always mention O(1) lookup optimization
4. **Handle Edge Cases:** Empty arrays, single nodes
5. **Verify Solution:** Walk through construction with given example
6. **Discuss Alternatives:** Mention iterative approach as follow-up

## LeetCode Similar Problems:
- [105. Construct Binary Tree from Preorder and Inorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-inorder-traversal/)
- [106. Construct Binary Tree from Inorder and Postorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-inorder-and-postorder-traversal/)
- [889. Construct Binary Tree from Preorder and Postorder Traversal](https://leetcode.com/problems/construct-binary-tree-from-preorder-and-postorder-traversal/)
- [297. Serialize and Deserialize Binary Tree](https://leetcode.com/problems/serialize-and-deserialize-binary-tree/)
- [108. Convert Sorted Array to Binary Search Tree](https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/)

This problem family is fundamental for understanding tree construction and is frequently asked in technical interviews due to its requirement of understanding tree traversal properties and recursive thinking. 