import java.util.*;

/**
 * Problem: Balanced Binary Search Tree Check
 * 
 * Given a binary tree, determine if it is height-balanced.
 * 
 * A height-balanced binary tree is defined as:
 * A binary tree in which the left and right subtrees of every node differ in height by no more than 1.
 * 
 * Example 1:
 * Input: root = [3,9,20,null,null,15,7]
 * Output: true
 * 
 * Example 2:
 * Input: root = [1,2,2,3,3,null,null,4,4]
 * Output: false
 * 
 * Example 3:
 * Input: root = []
 * Output: true
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 5000].
 * - -10^4 <= Node.val <= 10^4
 */
public class BalancedBSTCheck {
    
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
     * APPROACH 1: TOP-DOWN RECURSIVE (NAIVE)
     * Time Complexity: O(n^2) worst case
     * Space Complexity: O(n) for recursion stack
     * 
     * For each node, calculate height of left and right subtrees.
     * Check if difference is <= 1 and both subtrees are balanced.
     */
    public boolean isBalanced(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        // Calculate heights of left and right subtrees
        int leftHeight = height(root.left);
        int rightHeight = height(root.right);
        
        // Check if current node is balanced and both subtrees are balanced
        return Math.abs(leftHeight - rightHeight) <= 1 &&
               isBalanced(root.left) &&
               isBalanced(root.right);
    }
    
    /**
     * Helper method to calculate height of tree
     */
    private int height(TreeNode node) {
        if (node == null) {
            return 0;
        }
        
        return 1 + Math.max(height(node.left), height(node.right));
    }
    
    /**
     * APPROACH 2: BOTTOM-UP RECURSIVE (OPTIMIZED)
     * Time Complexity: O(n)
     * Space Complexity: O(n) for recursion stack
     * 
     * Calculate height and check balance in single pass.
     * Return -1 if tree is unbalanced, height otherwise.
     */
    public boolean isBalancedOptimized(TreeNode root) {
        return checkBalanceAndHeight(root) != -1;
    }
    
    private int checkBalanceAndHeight(TreeNode node) {
        if (node == null) {
            return 0;
        }
        
        // Get height of left subtree
        int leftHeight = checkBalanceAndHeight(node.left);
        if (leftHeight == -1) {
            return -1; // Left subtree is unbalanced
        }
        
        // Get height of right subtree
        int rightHeight = checkBalanceAndHeight(node.right);
        if (rightHeight == -1) {
            return -1; // Right subtree is unbalanced
        }
        
        // Check if current node is balanced
        if (Math.abs(leftHeight - rightHeight) > 1) {
            return -1; // Current subtree is unbalanced
        }
        
        // Return height of current subtree
        return 1 + Math.max(leftHeight, rightHeight);
    }
    
    /**
     * APPROACH 3: ITERATIVE WITH STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n) for stack
     * 
     * Use post-order traversal to check balance iteratively.
     */
    public boolean isBalancedIterative(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Stack<TreeNode> stack = new Stack<>();
        Map<TreeNode, Integer> heights = new HashMap<>();
        
        // Post-order traversal
        TreeNode current = root;
        TreeNode lastVisited = null;
        
        while (!stack.isEmpty() || current != null) {
            if (current != null) {
                stack.push(current);
                current = current.left;
            } else {
                TreeNode peekNode = stack.peek();
                
                // If right child exists and hasn't been processed yet
                if (peekNode.right != null && lastVisited != peekNode.right) {
                    current = peekNode.right;
                } else {
                    // Process current node
                    int leftHeight = heights.getOrDefault(peekNode.left, 0);
                    int rightHeight = heights.getOrDefault(peekNode.right, 0);
                    
                    // Check if current node is balanced
                    if (Math.abs(leftHeight - rightHeight) > 1) {
                        return false;
                    }
                    
                    // Store height of current node
                    heights.put(peekNode, 1 + Math.max(leftHeight, rightHeight));
                    
                    lastVisited = stack.pop();
                }
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 4: LEVEL-ORDER TRAVERSAL
     * Time Complexity: O(n)
     * Space Complexity: O(n) for queue and height map
     * 
     * Use BFS and calculate heights bottom-up.
     */
    public boolean isBalancedBFS(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Map<TreeNode, Integer> heights = new HashMap<>();
        Queue<TreeNode> queue = new LinkedList<>();
        
        // First pass: calculate heights for all nodes
        calculateHeightsBFS(root, heights);
        
        // Second pass: check balance for all nodes
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            int leftHeight = heights.getOrDefault(node.left, 0);
            int rightHeight = heights.getOrDefault(node.right, 0);
            
            if (Math.abs(leftHeight - rightHeight) > 1) {
                return false;
            }
            
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
        
        return true;
    }
    
    private void calculateHeightsBFS(TreeNode root, Map<TreeNode, Integer> heights) {
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            int leftHeight = heights.getOrDefault(node.left, 0);
            int rightHeight = heights.getOrDefault(node.right, 0);
            
            heights.put(node, 1 + Math.max(leftHeight, rightHeight));
            
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
    }
    
    /**
     * APPROACH 5: DIAMETER-BASED CHECK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Check balance while calculating diameter.
     */
    private boolean isTreeBalanced = true;
    
    public boolean isBalancedDiameter(TreeNode root) {
        isTreeBalanced = true;
        diameterHelper(root);
        return isTreeBalanced;
    }
    
    private int diameterHelper(TreeNode node) {
        if (node == null || !isTreeBalanced) {
            return 0;
        }
        
        int leftHeight = diameterHelper(node.left);
        int rightHeight = diameterHelper(node.right);
        
        // Check if current node is balanced
        if (Math.abs(leftHeight - rightHeight) > 1) {
            isTreeBalanced = false;
        }
        
        return 1 + Math.max(leftHeight, rightHeight);
    }
    
    // ==================== ADDITIONAL TREE CHECKS ====================
    
    /**
     * Check if tree is a valid BST
     */
    public boolean isValidBST(TreeNode root) {
        return isValidBSTHelper(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private boolean isValidBSTHelper(TreeNode node, long min, long max) {
        if (node == null) {
            return true;
        }
        
        if (node.val <= min || node.val >= max) {
            return false;
        }
        
        return isValidBSTHelper(node.left, min, node.val) &&
               isValidBSTHelper(node.right, node.val, max);
    }
    
    /**
     * Check if tree is complete
     */
    public boolean isCompleteTree(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean nullFound = false;
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            if (node == null) {
                nullFound = true;
            } else {
                if (nullFound) {
                    return false; // Non-null node after null node
                }
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }
        
        return true;
    }
    
    /**
     * Check if tree is perfect (all leaves at same level)
     */
    public boolean isPerfectTree(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        int depth = findDepth(root);
        return isPerfectHelper(root, depth, 0);
    }
    
    private int findDepth(TreeNode node) {
        int depth = 0;
        while (node != null) {
            depth++;
            node = node.left;
        }
        return depth;
    }
    
    private boolean isPerfectHelper(TreeNode node, int depth, int level) {
        if (node == null) {
            return true;
        }
        
        // If leaf node
        if (node.left == null && node.right == null) {
            return level == depth - 1;
        }
        
        // If internal node
        if (node.left == null || node.right == null) {
            return false;
        }
        
        return isPerfectHelper(node.left, depth, level + 1) &&
               isPerfectHelper(node.right, depth, level + 1);
    }
    
    /**
     * Get tree statistics
     */
    public static class TreeStats {
        int height;
        int nodes;
        int leaves;
        boolean isBalanced;
        boolean isValidBST;
        boolean isComplete;
        boolean isPerfect;
        
        @Override
        public String toString() {
            return String.format(
                "Height: %d, Nodes: %d, Leaves: %d, Balanced: %s, Valid BST: %s, Complete: %s, Perfect: %s",
                height, nodes, leaves, isBalanced, isValidBST, isComplete, isPerfect
            );
        }
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
    
    private int countNodes(TreeNode node) {
        if (node == null) {
            return 0;
        }
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    
    private int countLeaves(TreeNode node) {
        if (node == null) {
            return 0;
        }
        if (node.left == null && node.right == null) {
            return 1;
        }
        return countLeaves(node.left) + countLeaves(node.right);
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Create balanced BST from sorted array
     */
    public static TreeNode createBalancedBST(int[] sortedArray) {
        return createBalancedBSTHelper(sortedArray, 0, sortedArray.length - 1);
    }
    
    private static TreeNode createBalancedBSTHelper(int[] array, int left, int right) {
        if (left > right) {
            return null;
        }
        
        int mid = left + (right - left) / 2;
        TreeNode node = new TreeNode(array[mid]);
        
        node.left = createBalancedBSTHelper(array, left, mid - 1);
        node.right = createBalancedBSTHelper(array, mid + 1, right);
        
        return node;
    }
    
    /**
     * Create unbalanced tree for testing
     */
    public static TreeNode createUnbalancedTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.left.left = new TreeNode(3);
        root.left.left.left = new TreeNode(4);
        return root;
    }
    
    /**
     * Print tree structure with heights
     */
    public static void printTreeWithHeights(TreeNode root, BalancedBSTCheck checker) {
        if (root == null) {
            System.out.println("Empty tree");
            return;
        }
        
        System.out.println("Tree structure with heights:");
        printTreeHelper(root, "", true, checker);
    }
    
    private static void printTreeHelper(TreeNode node, String prefix, boolean isLast, BalancedBSTCheck checker) {
        if (node != null) {
            System.out.println(prefix + (isLast ? "└── " : "├── ") + node.val + " (h=" + checker.height(node) + ")");
            
            if (node.left != null || node.right != null) {
                if (node.left != null) {
                    printTreeHelper(node.left, prefix + (isLast ? "    " : "│   "), node.right == null, checker);
                }
                if (node.right != null) {
                    printTreeHelper(node.right, prefix + (isLast ? "    " : "│   "), true, checker);
                }
            }
        }
    }
    
    /**
     * Visualize tree balance
     */
    public static void visualizeBalance(TreeNode root, BalancedBSTCheck checker) {
        if (root == null) {
            System.out.println("Empty tree");
            return;
        }
        
        System.out.println("Balance visualization:");
        visualizeBalanceHelper(root, "", true, checker);
    }
    
    private static void visualizeBalanceHelper(TreeNode node, String prefix, boolean isLast, BalancedBSTCheck checker) {
        if (node != null) {
            int leftHeight = checker.height(node.left);
            int rightHeight = checker.height(node.right);
            int balance = leftHeight - rightHeight;
            String balanceStr = balance == 0 ? "=" : (balance > 0 ? "L+" + balance : "R" + Math.abs(balance));
            
            System.out.println(prefix + (isLast ? "└── " : "├── ") + node.val + " [" + balanceStr + "]");
            
            if (node.left != null || node.right != null) {
                if (node.left != null) {
                    visualizeBalanceHelper(node.left, prefix + (isLast ? "    " : "│   "), node.right == null, checker);
                }
                if (node.right != null) {
                    visualizeBalanceHelper(node.right, prefix + (isLast ? "    " : "│   "), true, checker);
                }
            }
        }
    }
    
    // ==================== TEST METHODS ====================
    
    public static void main(String[] args) {
        BalancedBSTCheck checker = new BalancedBSTCheck();
        
        System.out.println("=== Balanced BST Check Demo ===\n");
        
        // Test Case 1: Balanced tree
        System.out.println("1. Balanced Tree: [3,9,20,null,null,15,7]");
        TreeNode balanced = new TreeNode(3);
        balanced.left = new TreeNode(9);
        balanced.right = new TreeNode(20);
        balanced.right.left = new TreeNode(15);
        balanced.right.right = new TreeNode(7);
        
        printTreeWithHeights(balanced, checker);
        visualizeBalance(balanced, checker);
        
        System.out.println("Balance check results:");
        System.out.println("  Naive: " + checker.isBalanced(balanced));
        System.out.println("  Optimized: " + checker.isBalancedOptimized(balanced));
        System.out.println("  Iterative: " + checker.isBalancedIterative(balanced));
        System.out.println("  BFS: " + checker.isBalancedBFS(balanced));
        System.out.println("  Diameter: " + checker.isBalancedDiameter(balanced));
        
        TreeStats stats1 = checker.getTreeStatistics(balanced);
        System.out.println("Statistics: " + stats1);
        System.out.println();
        
        // Test Case 2: Unbalanced tree
        System.out.println("2. Unbalanced Tree: [1,2,2,3,3,null,null,4,4]");
        TreeNode unbalanced = new TreeNode(1);
        unbalanced.left = new TreeNode(2);
        unbalanced.right = new TreeNode(2);
        unbalanced.left.left = new TreeNode(3);
        unbalanced.left.right = new TreeNode(3);
        unbalanced.left.left.left = new TreeNode(4);
        unbalanced.left.left.right = new TreeNode(4);
        
        printTreeWithHeights(unbalanced, checker);
        visualizeBalance(unbalanced, checker);
        
        System.out.println("Balance check results:");
        System.out.println("  Naive: " + checker.isBalanced(unbalanced));
        System.out.println("  Optimized: " + checker.isBalancedOptimized(unbalanced));
        System.out.println("  Iterative: " + checker.isBalancedIterative(unbalanced));
        System.out.println("  BFS: " + checker.isBalancedBFS(unbalanced));
        System.out.println("  Diameter: " + checker.isBalancedDiameter(unbalanced));
        
        TreeStats stats2 = checker.getTreeStatistics(unbalanced);
        System.out.println("Statistics: " + stats2);
        System.out.println();
        
        // Test Case 3: Perfect balanced BST
        System.out.println("3. Perfect Balanced BST from sorted array [1,2,3,4,5,6,7]");
        TreeNode perfectBST = createBalancedBST(new int[]{1, 2, 3, 4, 5, 6, 7});
        
        printTreeWithHeights(perfectBST, checker);
        visualizeBalance(perfectBST, checker);
        
        TreeStats stats3 = checker.getTreeStatistics(perfectBST);
        System.out.println("Statistics: " + stats3);
        System.out.println();
        
        // Test Case 4: Completely unbalanced (linear)
        System.out.println("4. Completely Unbalanced (Linear) Tree");
        TreeNode linear = createUnbalancedTree();
        
        printTreeWithHeights(linear, checker);
        visualizeBalance(linear, checker);
        
        TreeStats stats4 = checker.getTreeStatistics(linear);
        System.out.println("Statistics: " + stats4);
        System.out.println();
        
        // Test Case 5: Edge cases
        System.out.println("5. Edge Cases:");
        
        // Empty tree
        System.out.println("Empty tree: " + checker.isBalancedOptimized(null));
        
        // Single node
        TreeNode single = new TreeNode(42);
        System.out.println("Single node: " + checker.isBalancedOptimized(single));
        
        // Two nodes
        TreeNode twoNodes = new TreeNode(1);
        twoNodes.left = new TreeNode(2);
        System.out.println("Two nodes (left): " + checker.isBalancedOptimized(twoNodes));
        
        twoNodes = new TreeNode(1);
        twoNodes.right = new TreeNode(2);
        System.out.println("Two nodes (right): " + checker.isBalancedOptimized(twoNodes));
        System.out.println();
        
        // Performance comparison
        performanceComparison(checker);
        
        // Correctness verification
        System.out.println("All approaches produce consistent results: " + verifyCorrectness(checker));
    }
    
    private static void performanceComparison(BalancedBSTCheck checker) {
        System.out.println("=== Performance Comparison ===");
        
        // Create large balanced tree
        int size = 10000;
        int[] sortedArray = new int[size];
        for (int i = 0; i < size; i++) {
            sortedArray[i] = i + 1;
        }
        
        TreeNode largeBalanced = createBalancedBST(sortedArray);
        
        long start, end;
        
        // Test optimized approach (baseline)
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            checker.isBalancedOptimized(largeBalanced);
        }
        end = System.nanoTime();
        System.out.println("Optimized (100 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test iterative approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            checker.isBalancedIterative(largeBalanced);
        }
        end = System.nanoTime();
        System.out.println("Iterative (100 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test BFS approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            checker.isBalancedBFS(largeBalanced);
        }
        end = System.nanoTime();
        System.out.println("BFS (100 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test diameter approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            checker.isBalancedDiameter(largeBalanced);
        }
        end = System.nanoTime();
        System.out.println("Diameter (100 ops): " + (end - start) / 1000000.0 + " ms");
        System.out.println();
    }
    
    private static boolean verifyCorrectness(BalancedBSTCheck checker) {
        // Test with multiple tree types
        TreeNode[] testTrees = {
            createBalancedBST(new int[]{1, 2, 3, 4, 5, 6, 7}),
            createUnbalancedTree(),
            null,
            new TreeNode(42)
        };
        
        for (TreeNode tree : testTrees) {
            boolean result1 = checker.isBalanced(tree);
            boolean result2 = checker.isBalancedOptimized(tree);
            boolean result3 = checker.isBalancedIterative(tree);
            boolean result4 = checker.isBalancedBFS(tree);
            boolean result5 = checker.isBalancedDiameter(tree);
            
            if (!(result1 == result2 && result2 == result3 && 
                  result3 == result4 && result4 == result5)) {
                return false;
            }
        }
        
        return true;
    }
} 