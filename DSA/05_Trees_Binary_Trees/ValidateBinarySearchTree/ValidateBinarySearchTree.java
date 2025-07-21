import java.util.*;

/**
 * Problem: Validate Binary Search Tree
 * 
 * Given the root of a binary tree, determine if it is a valid binary search tree (BST).
 * 
 * A valid BST is defined as follows:
 * - The left subtree of a node contains only nodes with keys less than the node's key.
 * - The right subtree of a node contains only nodes with keys greater than the node's key.
 * - Both the left and right subtrees must also be binary search trees.
 * 
 * Example:
 * Input: root = [2,1,3]
 * Output: true
 * 
 * Example 2:
 * Input: root = [5,1,4,null,null,3,6]
 * Output: false
 * Explanation: The root node's value is 5 but its right child's value is 4.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -2^31 <= Node.val <= 2^31 - 1
 */
public class ValidateBinarySearchTree {
    
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
     * APPROACH 1: RECURSIVE WITH MIN/MAX BOUNDS (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     * 
     * Use recursion with min/max bounds to validate each node.
     */
    public boolean isValidBSTBounds(TreeNode root) {
        return validate(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private boolean validate(TreeNode node, long minVal, long maxVal) {
        if (node == null) {
            return true;
        }
        
        // Check if current node violates BST property
        if (node.val <= minVal || node.val >= maxVal) {
            return false;
        }
        
        // Recursively validate left and right subtrees with updated bounds
        return validate(node.left, minVal, node.val) && 
               validate(node.right, node.val, maxVal);
    }
    
    /**
     * APPROACH 2: INORDER TRAVERSAL (Most Intuitive)
     * Time Complexity: O(n)
     * Space Complexity: O(n) for list + O(h) for recursion
     * 
     * Perform inorder traversal and check if result is sorted.
     */
    public boolean isValidBSTInorder(TreeNode root) {
        List<Integer> inorderList = new ArrayList<>();
        inorderTraversal(root, inorderList);
        
        // Check if inorder traversal is strictly increasing
        for (int i = 1; i < inorderList.size(); i++) {
            if (inorderList.get(i) <= inorderList.get(i - 1)) {
                return false;
            }
        }
        
        return true;
    }
    
    private void inorderTraversal(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }
        
        inorderTraversal(node.left, result);
        result.add(node.val);
        inorderTraversal(node.right, result);
    }
    
    /**
     * APPROACH 3: INORDER WITH PREVIOUS VALUE
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * Optimize space by keeping track of previous value during inorder.
     */
    private Integer prevValue = null;
    
    public boolean isValidBSTInorderOptimized(TreeNode root) {
        prevValue = null; // Reset for multiple calls
        return inorderCheck(root);
    }
    
    private boolean inorderCheck(TreeNode node) {
        if (node == null) {
            return true;
        }
        
        // Check left subtree
        if (!inorderCheck(node.left)) {
            return false;
        }
        
        // Check current node
        if (prevValue != null && node.val <= prevValue) {
            return false;
        }
        prevValue = node.val;
        
        // Check right subtree
        return inorderCheck(node.right);
    }
    
    /**
     * APPROACH 4: ITERATIVE INORDER TRAVERSAL
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * Use stack for iterative inorder traversal.
     */
    public boolean isValidBSTIterative(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        Integer prevVal = null;
        TreeNode current = root;
        
        while (current != null || !stack.isEmpty()) {
            // Go to leftmost node
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            
            // Process current node
            current = stack.pop();
            
            // Check BST property
            if (prevVal != null && current.val <= prevVal) {
                return false;
            }
            prevVal = current.val;
            
            // Move to right subtree
            current = current.right;
        }
        
        return true;
    }
    
    /**
     * APPROACH 5: USING DEQUE FOR BOUNDS
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * Use deque to store nodes with their bounds.
     */
    public boolean isValidBSTDeque(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Deque<TreeNode> nodeQueue = new ArrayDeque<>();
        Deque<Long> minQueue = new ArrayDeque<>();
        Deque<Long> maxQueue = new ArrayDeque<>();
        
        nodeQueue.offer(root);
        minQueue.offer(Long.MIN_VALUE);
        maxQueue.offer(Long.MAX_VALUE);
        
        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            long minVal = minQueue.poll();
            long maxVal = maxQueue.poll();
            
            if (node.val <= minVal || node.val >= maxVal) {
                return false;
            }
            
            if (node.left != null) {
                nodeQueue.offer(node.left);
                minQueue.offer(minVal);
                maxQueue.offer((long) node.val);
            }
            
            if (node.right != null) {
                nodeQueue.offer(node.right);
                minQueue.offer((long) node.val);
                maxQueue.offer(maxVal);
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 6: POSTORDER TRAVERSAL
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * Use postorder to get min/max values from subtrees.
     */
    public boolean isValidBSTPostorder(TreeNode root) {
        return postorderValidate(root) != null;
    }
    
    private int[] postorderValidate(TreeNode node) {
        if (node == null) {
            return new int[]{Integer.MAX_VALUE, Integer.MIN_VALUE}; // {min, max}
        }
        
        int[] left = postorderValidate(node.left);
        int[] right = postorderValidate(node.right);
        
        // If either subtree is invalid, return null
        if (left == null || right == null) {
            return null;
        }
        
        // Check BST property
        if ((node.left != null && left[1] >= node.val) ||
            (node.right != null && right[0] <= node.val)) {
            return null;
        }
        
        // Return min and max of current subtree
        int min = node.left != null ? left[0] : node.val;
        int max = node.right != null ? right[1] : node.val;
        
        return new int[]{min, max};
    }
    
    /**
     * APPROACH 7: MORRIS INORDER TRAVERSAL
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use Morris traversal for constant space inorder.
     */
    public boolean isValidBSTMorris(TreeNode root) {
        TreeNode current = root;
        Integer prevVal = null;
        
        while (current != null) {
            if (current.left == null) {
                // Process current node
                if (prevVal != null && current.val <= prevVal) {
                    return false;
                }
                prevVal = current.val;
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
                    // Remove thread and process current
                    predecessor.right = null;
                    if (prevVal != null && current.val <= prevVal) {
                        return false;
                    }
                    prevVal = current.val;
                    current = current.right;
                }
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 8: BFS WITH BOUNDS
     * Time Complexity: O(n)
     * Space Complexity: O(w) where w is maximum width of tree
     * 
     * Use BFS level-order traversal with bounds checking.
     */
    public boolean isValidBSTBFS(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<Long> minQueue = new LinkedList<>();
        Queue<Long> maxQueue = new LinkedList<>();
        
        nodeQueue.offer(root);
        minQueue.offer(Long.MIN_VALUE);
        maxQueue.offer(Long.MAX_VALUE);
        
        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            long minVal = minQueue.poll();
            long maxVal = maxQueue.poll();
            
            if (node.val <= minVal || node.val >= maxVal) {
                return false;
            }
            
            if (node.left != null) {
                nodeQueue.offer(node.left);
                minQueue.offer(minVal);
                maxQueue.offer((long) node.val);
            }
            
            if (node.right != null) {
                nodeQueue.offer(node.right);
                minQueue.offer((long) node.val);
                maxQueue.offer(maxVal);
            }
        }
        
        return true;
    }
    
    // Helper methods for testing
    
    /**
     * Create binary tree from array representation
     */
    public static TreeNode createTree(Integer[] values) {
        if (values == null || values.length == 0 || values[0] == null) {
            return null;
        }
        
        TreeNode root = new TreeNode(values[0]);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode node = queue.poll();
            
            if (i < values.length && values[i] != null) {
                node.left = new TreeNode(values[i]);
                queue.offer(node.left);
            }
            i++;
            
            if (i < values.length && values[i] != null) {
                node.right = new TreeNode(values[i]);
                queue.offer(node.right);
            }
            i++;
        }
        
        return root;
    }
    
    /**
     * Print tree in array format for visualization
     */
    public static void printTree(TreeNode root) {
        if (root == null) {
            System.out.println("[]");
            return;
        }
        
        List<String> result = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node != null) {
                result.add(String.valueOf(node.val));
                queue.offer(node.left);
                queue.offer(node.right);
            } else {
                result.add("null");
            }
        }
        
        // Remove trailing nulls
        while (!result.isEmpty() && result.get(result.size() - 1).equals("null")) {
            result.remove(result.size() - 1);
        }
        
        System.out.println(result);
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        ValidateBinarySearchTree solution = new ValidateBinarySearchTree();
        
        // Test case 1: Valid BST
        System.out.println("Test Case 1: Valid BST [2,1,3]");
        TreeNode root1 = createTree(new Integer[]{2, 1, 3});
        System.out.print("Tree: ");
        printTree(root1);
        
        System.out.println("Bounds: " + solution.isValidBSTBounds(root1));
        System.out.println("Inorder: " + solution.isValidBSTInorder(root1));
        System.out.println("Inorder Optimized: " + solution.isValidBSTInorderOptimized(root1));
        System.out.println("Iterative: " + solution.isValidBSTIterative(root1));
        System.out.println("Deque: " + solution.isValidBSTDeque(root1));
        System.out.println("Postorder: " + solution.isValidBSTPostorder(root1));
        System.out.println("Morris: " + solution.isValidBSTMorris(root1));
        System.out.println("BFS: " + solution.isValidBSTBFS(root1));
        System.out.println();
        
        // Test case 2: Invalid BST
        System.out.println("Test Case 2: Invalid BST [5,1,4,null,null,3,6]");
        TreeNode root2 = createTree(new Integer[]{5, 1, 4, null, null, 3, 6});
        System.out.print("Tree: ");
        printTree(root2);
        
        System.out.println("Bounds: " + solution.isValidBSTBounds(root2));
        System.out.println("Inorder: " + solution.isValidBSTInorder(root2));
        System.out.println();
        
        // Test case 3: Edge case - single node
        System.out.println("Test Case 3: Single node [1]");
        TreeNode root3 = createTree(new Integer[]{1});
        System.out.println("Result: " + solution.isValidBSTBounds(root3));
        System.out.println();
        
        // Test case 4: Edge case - duplicate values
        System.out.println("Test Case 4: Duplicate values [5,5,5]");
        TreeNode root4 = createTree(new Integer[]{5, 5, 5});
        System.out.println("Result: " + solution.isValidBSTBounds(root4));
        System.out.println();
        
        // Test case 5: Large values
        System.out.println("Test Case 5: Integer limits");
        TreeNode root5 = createTree(new Integer[]{Integer.MAX_VALUE});
        System.out.println("Max value tree: " + solution.isValidBSTBounds(root5));
        
        TreeNode root6 = createTree(new Integer[]{Integer.MIN_VALUE});
        System.out.println("Min value tree: " + solution.isValidBSTBounds(root6));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(ValidateBinarySearchTree solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large balanced BST
        TreeNode largeTree = createLargeBalancedBST(1000);
        
        long start, end;
        
        // Test Bounds approach (optimal)
        start = System.nanoTime();
        boolean result1 = solution.isValidBSTBounds(largeTree);
        end = System.nanoTime();
        System.out.println("Bounds: " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Iterative approach
        start = System.nanoTime();
        boolean result2 = solution.isValidBSTIterative(largeTree);
        end = System.nanoTime();
        System.out.println("Iterative: " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Inorder approach
        start = System.nanoTime();
        boolean result3 = solution.isValidBSTInorder(largeTree);
        end = System.nanoTime();
        System.out.println("Inorder: " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Morris approach (constant space)
        start = System.nanoTime();
        boolean result4 = solution.isValidBSTMorris(largeTree);
        end = System.nanoTime();
        System.out.println("Morris: " + result4 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test BFS approach
        start = System.nanoTime();
        boolean result5 = solution.isValidBSTBFS(largeTree);
        end = System.nanoTime();
        System.out.println("BFS: " + result5 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        System.out.println("All approaches give same result: " + 
                          (result1 == result2 && result2 == result3 && 
                           result3 == result4 && result4 == result5));
    }
    
    private static TreeNode createLargeBalancedBST(int size) {
        return createBalancedBST(1, size);
    }
    
    private static TreeNode createBalancedBST(int start, int end) {
        if (start > end) {
            return null;
        }
        
        int mid = start + (end - start) / 2;
        TreeNode root = new TreeNode(mid);
        
        root.left = createBalancedBST(start, mid - 1);
        root.right = createBalancedBST(mid + 1, end);
        
        return root;
    }
    
    /**
     * Method to verify correctness with various test cases
     */
    public static boolean verifyCorrectness(ValidateBinarySearchTree solution) {
        Integer[][] testCases = {
            {2, 1, 3},                    // true
            {5, 1, 4, null, null, 3, 6},  // false
            {1},                          // true
            {5, 5, 5},                    // false
            {10, 5, 15, null, null, 6, 20}, // false
            {10, 5, 15, 3, 7, 12, 20},    // true
            {},                           // true (empty)
            {0, -1, 1}                    // true
        };
        
        boolean[] expected = {true, false, true, false, false, true, true, true};
        
        for (int i = 0; i < testCases.length; i++) {
            TreeNode root = testCases[i].length == 0 ? null : createTree(testCases[i]);
            
            boolean result1 = solution.isValidBSTBounds(root);
            boolean result2 = solution.isValidBSTIterative(root);
            boolean result3 = solution.isValidBSTInorder(root);
            
            if (result1 != expected[i] || result2 != expected[i] || result3 != expected[i]) {
                System.out.println("Test case " + i + " failed. Expected: " + expected[i] + 
                                 ", Got: " + result1 + ", " + result2 + ", " + result3);
                return false;
            }
        }
        
        return true;
    }
} 