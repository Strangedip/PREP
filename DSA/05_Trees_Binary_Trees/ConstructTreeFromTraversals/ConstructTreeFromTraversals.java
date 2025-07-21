import java.util.*;

/**
 * Problem: Construct Binary Tree from Traversals
 * 
 * Build binary trees from combinations of preorder, inorder, and postorder traversals.
 * 
 * Problems covered:
 * 1. Construct Binary Tree from Preorder and Inorder Traversal
 * 2. Construct Binary Tree from Inorder and Postorder Traversal
 * 3. Construct Binary Tree from Preorder and Postorder Traversal
 * 
 * Example 1: From Preorder and Inorder
 * Input: preorder = [3,9,20,15,7], inorder = [9,3,15,20,7]
 * Output: [3,9,20,null,null,15,7]
 * 
 * Example 2: From Inorder and Postorder
 * Input: inorder = [9,3,15,20,7], postorder = [9,15,7,20,3]
 * Output: [3,9,20,null,null,15,7]
 * 
 * Constraints:
 * - 1 <= preorder.length <= 3000
 * - inorder.length == preorder.length
 * - preorder and inorder consist of unique values.
 */
public class ConstructTreeFromTraversals {
    
    /**
     * Definition for a binary tree node.
     */
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        TreeNode() {}
        TreeNode(int val) { this.val = val; }
        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }
    
    /**
     * PROBLEM 1: CONSTRUCT FROM PREORDER AND INORDER
     * 
     * APPROACH 1: RECURSIVE WITH HASHMAP (Optimized)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use hashmap to quickly find root position in inorder.
     */
    public TreeNode buildTreePreIn(int[] preorder, int[] inorder) {
        if (preorder == null || inorder == null || preorder.length == 0) {
            return null;
        }
        
        // Create index mapping for inorder array
        Map<Integer, Integer> inorderMap = new HashMap<>();
        for (int i = 0; i < inorder.length; i++) {
            inorderMap.put(inorder[i], i);
        }
        
        return buildPreInHelper(preorder, 0, preorder.length - 1,
                               inorder, 0, inorder.length - 1, inorderMap);
    }
    
    private TreeNode buildPreInHelper(int[] preorder, int preStart, int preEnd,
                                     int[] inorder, int inStart, int inEnd,
                                     Map<Integer, Integer> inorderMap) {
        if (preStart > preEnd || inStart > inEnd) {
            return null;
        }
        
        // Root is first element in preorder
        int rootVal = preorder[preStart];
        TreeNode root = new TreeNode(rootVal);
        
        // Find root position in inorder
        int rootIndex = inorderMap.get(rootVal);
        int leftSize = rootIndex - inStart;
        
        // Build left and right subtrees
        root.left = buildPreInHelper(preorder, preStart + 1, preStart + leftSize,
                                    inorder, inStart, rootIndex - 1, inorderMap);
        root.right = buildPreInHelper(preorder, preStart + leftSize + 1, preEnd,
                                     inorder, rootIndex + 1, inEnd, inorderMap);
        
        return root;
    }
    
    /**
     * APPROACH 2: RECURSIVE WITHOUT HASHMAP
     * Time Complexity: O(n²) - worst case when tree is skewed
     * Space Complexity: O(n)
     * 
     * Linear search to find root in inorder array.
     */
    public TreeNode buildTreePreInSimple(int[] preorder, int[] inorder) {
        return buildPreInSimpleHelper(preorder, inorder, 0, 0, inorder.length - 1);
    }
    
    private TreeNode buildPreInSimpleHelper(int[] preorder, int[] inorder, 
                                           int preIndex, int inStart, int inEnd) {
        if (preIndex >= preorder.length || inStart > inEnd) {
            return null;
        }
        
        TreeNode root = new TreeNode(preorder[preIndex]);
        
        // Find root in inorder
        int inIndex = -1;
        for (int i = inStart; i <= inEnd; i++) {
            if (inorder[i] == preorder[preIndex]) {
                inIndex = i;
                break;
            }
        }
        
        // Build subtrees
        root.left = buildPreInSimpleHelper(preorder, inorder, preIndex + 1, 
                                          inStart, inIndex - 1);
        root.right = buildPreInSimpleHelper(preorder, inorder, 
                                           preIndex + (inIndex - inStart) + 1, 
                                           inIndex + 1, inEnd);
        
        return root;
    }
    
    /**
     * PROBLEM 2: CONSTRUCT FROM INORDER AND POSTORDER
     * 
     * APPROACH 1: RECURSIVE WITH HASHMAP
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public TreeNode buildTreeInPost(int[] inorder, int[] postorder) {
        if (inorder == null || postorder == null || inorder.length == 0) {
            return null;
        }
        
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
        if (inStart > inEnd || postStart > postEnd) {
            return null;
        }
        
        // Root is last element in postorder
        int rootVal = postorder[postEnd];
        TreeNode root = new TreeNode(rootVal);
        
        int rootIndex = inorderMap.get(rootVal);
        int leftSize = rootIndex - inStart;
        
        // Build left and right subtrees
        root.left = buildInPostHelper(inorder, inStart, rootIndex - 1,
                                     postorder, postStart, postStart + leftSize - 1,
                                     inorderMap);
        root.right = buildInPostHelper(inorder, rootIndex + 1, inEnd,
                                      postorder, postStart + leftSize, postEnd - 1,
                                      inorderMap);
        
        return root;
    }
    
    /**
     * PROBLEM 3: CONSTRUCT FROM PREORDER AND POSTORDER
     * 
     * Note: This construction is not unique unless we have additional constraints.
     * We assume that when ambiguous, we prefer left-skewed trees.
     * 
     * APPROACH 1: RECURSIVE WITH HASHMAP
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public TreeNode buildTreePrePost(int[] preorder, int[] postorder) {
        if (preorder == null || postorder == null || preorder.length == 0) {
            return null;
        }
        
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
        if (preStart > preEnd || postStart > postEnd) {
            return null;
        }
        
        TreeNode root = new TreeNode(preorder[preStart]);
        
        if (preStart == preEnd) {
            return root;
        }
        
        // Left child is the second element in preorder (if exists)
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
    
    /**
     * APPROACH 2: ITERATIVE CONSTRUCTION (PreOrder + InOrder)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Using stack-based approach.
     */
    public TreeNode buildTreePreInIterative(int[] preorder, int[] inorder) {
        if (preorder == null || preorder.length == 0) {
            return null;
        }
        
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
                // Pop nodes until we find the correct parent for right child
                while (!stack.isEmpty() && stack.peek().val == inorder[inorderIndex]) {
                    node = stack.pop();
                    inorderIndex++;
                }
                // Create right child
                node.right = new TreeNode(preorder[i]);
                stack.push(node.right);
            }
        }
        
        return root;
    }
    
    // Helper methods for testing and verification
    
    /**
     * Generate inorder traversal
     */
    public static List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }
    
    private static void inorderHelper(TreeNode node, List<Integer> result) {
        if (node != null) {
            inorderHelper(node.left, result);
            result.add(node.val);
            inorderHelper(node.right, result);
        }
    }
    
    /**
     * Generate preorder traversal
     */
    public static List<Integer> preorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        preorderHelper(root, result);
        return result;
    }
    
    private static void preorderHelper(TreeNode node, List<Integer> result) {
        if (node != null) {
            result.add(node.val);
            preorderHelper(node.left, result);
            preorderHelper(node.right, result);
        }
    }
    
    /**
     * Generate postorder traversal
     */
    public static List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }
    
    private static void postorderHelper(TreeNode node, List<Integer> result) {
        if (node != null) {
            postorderHelper(node.left, result);
            postorderHelper(node.right, result);
            result.add(node.val);
        }
    }
    
    /**
     * Convert tree to level order array
     */
    public static List<Integer> levelOrder(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node != null) {
                result.add(node.val);
                queue.offer(node.left);
                queue.offer(node.right);
            } else {
                result.add(null);
            }
        }
        
        // Remove trailing nulls
        while (!result.isEmpty() && result.get(result.size() - 1) == null) {
            result.remove(result.size() - 1);
        }
        
        return result;
    }
    
    /**
     * Pretty print tree structure
     */
    public static void printTree(TreeNode root) {
        if (root == null) {
            System.out.println("Empty tree");
            return;
        }
        
        List<List<String>> levels = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<String> level = new ArrayList<>();
            boolean hasNext = false;
            
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                if (node != null) {
                    level.add(String.valueOf(node.val));
                    queue.offer(node.left);
                    queue.offer(node.right);
                    if (node.left != null || node.right != null) {
                        hasNext = true;
                    }
                } else {
                    level.add("null");
                    queue.offer(null);
                    queue.offer(null);
                }
            }
            
            levels.add(level);
            if (!hasNext) break;
        }
        
        for (List<String> level : levels) {
            System.out.println(level);
        }
    }
    
    // Test method
    public static void main(String[] args) {
        ConstructTreeFromTraversals solution = new ConstructTreeFromTraversals();
        
        System.out.println("=== Test Case 1: Preorder + Inorder ===");
        int[] preorder1 = {3, 9, 20, 15, 7};
        int[] inorder1 = {9, 3, 15, 20, 7};
        
        System.out.println("Input:");
        System.out.println("Preorder: " + Arrays.toString(preorder1));
        System.out.println("Inorder:  " + Arrays.toString(inorder1));
        
        TreeNode tree1 = solution.buildTreePreIn(preorder1, inorder1);
        System.out.println("Constructed tree (level order): " + levelOrder(tree1));
        System.out.println("Tree structure:");
        printTree(tree1);
        
        // Verify by generating traversals
        System.out.println("Verification:");
        System.out.println("Generated Preorder: " + preorderTraversal(tree1));
        System.out.println("Generated Inorder:  " + inorderTraversal(tree1));
        System.out.println();
        
        System.out.println("=== Test Case 2: Inorder + Postorder ===");
        int[] inorder2 = {9, 3, 15, 20, 7};
        int[] postorder2 = {9, 15, 7, 20, 3};
        
        System.out.println("Input:");
        System.out.println("Inorder:   " + Arrays.toString(inorder2));
        System.out.println("Postorder: " + Arrays.toString(postorder2));
        
        TreeNode tree2 = solution.buildTreeInPost(inorder2, postorder2);
        System.out.println("Constructed tree (level order): " + levelOrder(tree2));
        System.out.println("Tree structure:");
        printTree(tree2);
        
        System.out.println("Verification:");
        System.out.println("Generated Inorder:   " + inorderTraversal(tree2));
        System.out.println("Generated Postorder: " + postorderTraversal(tree2));
        System.out.println();
        
        System.out.println("=== Test Case 3: Preorder + Postorder ===");
        int[] preorder3 = {1, 2, 4, 5, 3, 6, 7};
        int[] postorder3 = {4, 5, 2, 6, 7, 3, 1};
        
        System.out.println("Input:");
        System.out.println("Preorder:  " + Arrays.toString(preorder3));
        System.out.println("Postorder: " + Arrays.toString(postorder3));
        
        TreeNode tree3 = solution.buildTreePrePost(preorder3, postorder3);
        System.out.println("Constructed tree (level order): " + levelOrder(tree3));
        System.out.println("Tree structure:");
        printTree(tree3);
        
        System.out.println("Verification:");
        System.out.println("Generated Preorder:  " + preorderTraversal(tree3));
        System.out.println("Generated Postorder: " + postorderTraversal(tree3));
        System.out.println();
        
        System.out.println("=== Test Case 4: Single Node ===");
        int[] singlePre = {42};
        int[] singleIn = {42};
        TreeNode singleTree = solution.buildTreePreIn(singlePre, singleIn);
        System.out.println("Single node tree: " + levelOrder(singleTree));
        System.out.println();
        
        System.out.println("=== Test Case 5: Iterative vs Recursive ===");
        TreeNode treeRecursive = solution.buildTreePreIn(preorder1, inorder1);
        TreeNode treeIterative = solution.buildTreePreInIterative(preorder1, inorder1);
        
        System.out.println("Recursive result:  " + levelOrder(treeRecursive));
        System.out.println("Iterative result:  " + levelOrder(treeIterative));
        System.out.println("Results match: " + 
                          levelOrder(treeRecursive).equals(levelOrder(treeIterative)));
        
        // Performance comparison
        performanceTest(solution);
    }
    
    private static void performanceTest(ConstructTreeFromTraversals solution) {
        System.out.println("\n=== Performance Test ===");
        
        // Generate large test case
        int size = 1000;
        int[] preorder = new int[size];
        int[] inorder = new int[size];
        
        // Create a balanced tree scenario
        for (int i = 0; i < size; i++) {
            preorder[i] = i + 1;
            inorder[i] = i + 1;
        }
        
        long start, end;
        
        // Test optimized approach
        start = System.nanoTime();
        TreeNode tree1 = solution.buildTreePreIn(preorder, inorder);
        end = System.nanoTime();
        System.out.println("Optimized (with HashMap): " + (end - start) / 1000000.0 + " ms");
        
        // Test simple approach
        start = System.nanoTime();
        TreeNode tree2 = solution.buildTreePreInSimple(preorder, inorder);
        end = System.nanoTime();
        System.out.println("Simple (linear search): " + (end - start) / 1000000.0 + " ms");
        
        // Test iterative approach
        start = System.nanoTime();
        TreeNode tree3 = solution.buildTreePreInIterative(preorder, inorder);
        end = System.nanoTime();
        System.out.println("Iterative: " + (end - start) / 1000000.0 + " ms");
        
        System.out.println("All approaches produce same result: " + 
                          (levelOrder(tree1).equals(levelOrder(tree2)) && 
                           levelOrder(tree2).equals(levelOrder(tree3))));
    }
} 