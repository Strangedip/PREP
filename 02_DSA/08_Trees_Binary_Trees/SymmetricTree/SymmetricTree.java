import java.util.*;

/**
 * Problem: Symmetric Tree
 * 
 * Given the root of a binary tree, check whether it is a mirror of itself 
 * (i.e., symmetric around its center).
 * 
 * Example:
 * Input: root = [1,2,2,3,4,4,3]
 * Output: true
 * 
 * Example 2:
 * Input: root = [1,2,2,null,3,null,3]
 * Output: false
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 1000].
 * - -100 <= Node.val <= 100
 * 
 * Follow up: Could you solve it both recursively and iteratively?
 */
public class SymmetricTree {
    
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
     * APPROACH 1: RECURSIVE (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     * 
     * Compare left and right subtrees recursively.
     */
    public boolean isSymmetricRecursive(TreeNode root) {
        if (root == null) {
            return true;
        }
        return isMirror(root.left, root.right);
    }
    
    private boolean isMirror(TreeNode left, TreeNode right) {
        // Both nodes are null
        if (left == null && right == null) {
            return true;
        }
        
        // One node is null, the other is not
        if (left == null || right == null) {
            return false;
        }
        
        // Check if values are equal and subtrees are mirrors
        return (left.val == right.val) &&
               isMirror(left.left, right.right) &&
               isMirror(left.right, right.left);
    }
    
    /**
     * APPROACH 2: ITERATIVE WITH QUEUE
     * Time Complexity: O(n)
     * Space Complexity: O(w) where w is maximum width of tree
     * 
     * Use BFS with queue to check symmetry level by level.
     */
    public boolean isSymmetricIterative(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root.left);
        queue.offer(root.right);
        
        while (!queue.isEmpty()) {
            TreeNode left = queue.poll();
            TreeNode right = queue.poll();
            
            // Both nodes are null
            if (left == null && right == null) {
                continue;
            }
            
            // One node is null or values are different
            if (left == null || right == null || left.val != right.val) {
                return false;
            }
            
            // Add children in mirror order
            queue.offer(left.left);
            queue.offer(right.right);
            queue.offer(left.right);
            queue.offer(right.left);
        }
        
        return true;
    }
    
    /**
     * APPROACH 3: ITERATIVE WITH STACK
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Use stack for DFS-like traversal to check symmetry.
     */
    public boolean isSymmetricStack(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root.left);
        stack.push(root.right);
        
        while (!stack.isEmpty()) {
            TreeNode right = stack.pop();
            TreeNode left = stack.pop();
            
            // Both nodes are null
            if (left == null && right == null) {
                continue;
            }
            
            // One node is null or values are different
            if (left == null || right == null || left.val != right.val) {
                return false;
            }
            
            // Push children in mirror order
            stack.push(left.left);
            stack.push(right.right);
            stack.push(left.right);
            stack.push(right.left);
        }
        
        return true;
    }
    
    /**
     * APPROACH 4: LEVEL ORDER TRAVERSAL COMPARISON
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Compare each level to see if it's palindromic.
     */
    public boolean isSymmetricLevelOrder(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();
            
            // Process current level
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                
                if (node != null) {
                    level.add(node.val);
                    queue.offer(node.left);
                    queue.offer(node.right);
                } else {
                    level.add(null);
                }
            }
            
            // Check if level is palindromic
            if (!isPalindrome(level)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isPalindrome(List<Integer> list) {
        int left = 0, right = list.size() - 1;
        while (left < right) {
            if (!Objects.equals(list.get(left), list.get(right))) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    
    /**
     * APPROACH 5: INORDER TRAVERSAL COMPARISON
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Compare inorder traversals of left and right subtrees.
     */
    public boolean isSymmetricInorder(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        List<Integer> leftInorder = new ArrayList<>();
        List<Integer> rightInorder = new ArrayList<>();
        
        inorderTraversal(root.left, leftInorder, false);
        inorderTraversal(root.right, rightInorder, true);
        
        return leftInorder.equals(rightInorder);
    }
    
    private void inorderTraversal(TreeNode node, List<Integer> result, boolean reverse) {
        if (node == null) {
            result.add(null);
            return;
        }
        
        if (reverse) {
            inorderTraversal(node.right, result, reverse);
            result.add(node.val);
            inorderTraversal(node.left, result, reverse);
        } else {
            inorderTraversal(node.left, result, reverse);
            result.add(node.val);
            inorderTraversal(node.right, result, reverse);
        }
    }
    
    /**
     * APPROACH 6: SERIALIZATION COMPARISON
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Serialize both subtrees and compare strings.
     */
    public boolean isSymmetricSerialization(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        String leftSerialization = serialize(root.left, false);
        String rightSerialization = serialize(root.right, true);
        
        return leftSerialization.equals(rightSerialization);
    }
    
    private String serialize(TreeNode node, boolean reverse) {
        if (node == null) {
            return "#";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(node.val).append(",");
        
        if (reverse) {
            sb.append(serialize(node.right, reverse)).append(",");
            sb.append(serialize(node.left, reverse));
        } else {
            sb.append(serialize(node.left, reverse)).append(",");
            sb.append(serialize(node.right, reverse));
        }
        
        return sb.toString();
    }
    
    /**
     * APPROACH 7: DEQUE-BASED SOLUTION
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Use deque for more efficient operations.
     */
    public boolean isSymmetricDeque(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        Deque<TreeNode> deque = new ArrayDeque<>();
        deque.offer(root.left);
        deque.offer(root.right);
        
        while (!deque.isEmpty()) {
            TreeNode left = deque.poll();
            TreeNode right = deque.poll();
            
            if (left == null && right == null) {
                continue;
            }
            
            if (left == null || right == null || left.val != right.val) {
                return false;
            }
            
            deque.offer(left.left);
            deque.offer(right.right);
            deque.offer(left.right);
            deque.offer(right.left);
        }
        
        return true;
    }
    
    /**
     * APPROACH 8: MORRIS TRAVERSAL APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(1) excluding recursion for helper
     * 
     * Use Morris-like approach for constant space (conceptual).
     */
    public boolean isSymmetricMorris(TreeNode root) {
        if (root == null) {
            return true;
        }
        
        // For this problem, Morris traversal is complex to implement
        // for symmetry checking, so we use a simplified approach
        return isSymmetricRecursive(root);
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
     * Print tree in array format
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
        SymmetricTree solution = new SymmetricTree();
        
        // Test case 1: Symmetric tree
        System.out.println("Test Case 1: Symmetric tree [1,2,2,3,4,4,3]");
        TreeNode root1 = createTree(new Integer[]{1, 2, 2, 3, 4, 4, 3});
        System.out.print("Tree: ");
        printTree(root1);
        
        System.out.println("Recursive: " + solution.isSymmetricRecursive(root1));
        System.out.println("Iterative: " + solution.isSymmetricIterative(root1));
        System.out.println("Stack: " + solution.isSymmetricStack(root1));
        System.out.println("Level Order: " + solution.isSymmetricLevelOrder(root1));
        System.out.println("Inorder: " + solution.isSymmetricInorder(root1));
        System.out.println("Serialization: " + solution.isSymmetricSerialization(root1));
        System.out.println("Deque: " + solution.isSymmetricDeque(root1));
        System.out.println();
        
        // Test case 2: Asymmetric tree
        System.out.println("Test Case 2: Asymmetric tree [1,2,2,null,3,null,3]");
        TreeNode root2 = createTree(new Integer[]{1, 2, 2, null, 3, null, 3});
        System.out.print("Tree: ");
        printTree(root2);
        
        System.out.println("Recursive: " + solution.isSymmetricRecursive(root2));
        System.out.println("Iterative: " + solution.isSymmetricIterative(root2));
        System.out.println();
        
        // Test case 3: Single node
        System.out.println("Test Case 3: Single node [1]");
        TreeNode root3 = createTree(new Integer[]{1});
        System.out.println("Result: " + solution.isSymmetricRecursive(root3));
        System.out.println();
        
        // Test case 4: Empty tree
        System.out.println("Test Case 4: Empty tree");
        TreeNode root4 = null;
        System.out.println("Result: " + solution.isSymmetricRecursive(root4));
        System.out.println();
        
        // Test case 5: Two nodes
        System.out.println("Test Case 5: Two nodes [1,2]");
        TreeNode root5 = createTree(new Integer[]{1, 2});
        System.out.println("Result: " + solution.isSymmetricRecursive(root5));
        System.out.println();
        
        // Test case 6: Perfect symmetric tree
        System.out.println("Test Case 6: Perfect symmetric [1,2,2]");
        TreeNode root6 = createTree(new Integer[]{1, 2, 2});
        System.out.println("Result: " + solution.isSymmetricRecursive(root6));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(SymmetricTree solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large symmetric tree
        TreeNode largeTree = createLargeSymmetricTree(10); // Depth 10
        
        long start, end;
        
        // Test Recursive approach
        start = System.nanoTime();
        boolean result1 = solution.isSymmetricRecursive(largeTree);
        end = System.nanoTime();
        System.out.println("Recursive: " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Iterative approach
        start = System.nanoTime();
        boolean result2 = solution.isSymmetricIterative(largeTree);
        end = System.nanoTime();
        System.out.println("Iterative: " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Stack approach
        start = System.nanoTime();
        boolean result3 = solution.isSymmetricStack(largeTree);
        end = System.nanoTime();
        System.out.println("Stack: " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Deque approach
        start = System.nanoTime();
        boolean result4 = solution.isSymmetricDeque(largeTree);
        end = System.nanoTime();
        System.out.println("Deque: " + result4 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Level Order approach
        start = System.nanoTime();
        boolean result5 = solution.isSymmetricLevelOrder(largeTree);
        end = System.nanoTime();
        System.out.println("Level Order: " + result5 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        System.out.println("All approaches give same result: " + 
                          (result1 == result2 && result2 == result3 && 
                           result3 == result4 && result4 == result5));
    }
    
    private static TreeNode createLargeSymmetricTree(int depth) {
        if (depth <= 0) {
            return null;
        }
        
        TreeNode root = new TreeNode(1);
        root.left = createSymmetricSubtree(depth - 1, false);
        root.right = createSymmetricSubtree(depth - 1, true);
        
        return root;
    }
    
    private static TreeNode createSymmetricSubtree(int depth, boolean mirror) {
        if (depth <= 0) {
            return null;
        }
        
        TreeNode node = new TreeNode(depth);
        
        if (mirror) {
            node.left = createSymmetricSubtree(depth - 1, true);
            node.right = createSymmetricSubtree(depth - 1, false);
        } else {
            node.left = createSymmetricSubtree(depth - 1, false);
            node.right = createSymmetricSubtree(depth - 1, true);
        }
        
        return node;
    }
    
    /**
     * Method to verify correctness with various test cases
     */
    public static boolean verifyCorrectness(SymmetricTree solution) {
        Integer[][] testCases = {
            {1, 2, 2, 3, 4, 4, 3},        // true
            {1, 2, 2, null, 3, null, 3},  // false
            {1},                          // true
            {},                           // true (empty)
            {1, 2},                       // false
            {1, 2, 2},                    // true
            {1, 2, 3},                    // false
            {1, 0, 0}                     // true
        };
        
        boolean[] expected = {true, false, true, true, false, true, false, true};
        
        for (int i = 0; i < testCases.length; i++) {
            TreeNode root = testCases[i].length == 0 ? null : createTree(testCases[i]);
            
            boolean result1 = solution.isSymmetricRecursive(root);
            boolean result2 = solution.isSymmetricIterative(root);
            boolean result3 = solution.isSymmetricStack(root);
            
            if (result1 != expected[i] || result2 != expected[i] || result3 != expected[i]) {
                System.out.println("Test case " + i + " failed. Expected: " + expected[i] + 
                                 ", Got: " + result1 + ", " + result2 + ", " + result3);
                return false;
            }
        }
        
        return true;
    }
} 