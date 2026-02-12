import java.util.*;

/**
 * Problem: Invert Binary Tree
 * 
 * Given the root of a binary tree, invert the tree, and return its root.
 * 
 * Example 1:
 * Input: root = [4,2,7,1,3,6,9]
 * Output: [4,7,2,9,6,3,1]
 * 
 * Example 2:
 * Input: root = [2,1,3]
 * Output: [2,3,1]
 * 
 * Example 3:
 * Input: root = []
 * Output: []
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 100].
 * - -100 <= Node.val <= 100
 */
public class InvertBinaryTree {
    
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
     * APPROACH 1: RECURSIVE (DFS)
     * Time Complexity: O(n) where n is number of nodes
     * Space Complexity: O(h) where h is height of tree
     * 
     * Simple recursive approach - swap left and right children recursively.
     */
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
    
    /**
     * APPROACH 2: RECURSIVE (Post-order)
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * Invert children first, then swap.
     */
    public TreeNode invertTreePostOrder(TreeNode root) {
        if (root == null) {
            return null;
        }
        
        // First invert the subtrees
        TreeNode left = invertTreePostOrder(root.left);
        TreeNode right = invertTreePostOrder(root.right);
        
        // Then swap them
        root.left = right;
        root.right = left;
        
        return root;
    }
    
    /**
     * APPROACH 3: ITERATIVE BFS (Level Order)
     * Time Complexity: O(n)
     * Space Complexity: O(w) where w is maximum width of tree
     * 
     * Use queue to traverse level by level and swap children.
     */
    public TreeNode invertTreeBFS(TreeNode root) {
        if (root == null) {
            return null;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            // Swap left and right children
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;
            
            // Add children to queue for processing
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
        
        return root;
    }
    
    /**
     * APPROACH 4: ITERATIVE DFS (Using Stack)
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * Use stack to simulate recursive DFS traversal.
     */
    public TreeNode invertTreeDFS(TreeNode root) {
        if (root == null) {
            return null;
        }
        
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            
            // Swap left and right children
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;
            
            // Push children to stack for processing
            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
        }
        
        return root;
    }
    
    /**
     * APPROACH 5: USING DEQUE (Double-ended queue)
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Similar to BFS but using Deque for potentially better performance.
     */
    public TreeNode invertTreeDeque(TreeNode root) {
        if (root == null) {
            return null;
        }
        
        Deque<TreeNode> deque = new ArrayDeque<>();
        deque.offer(root);
        
        while (!deque.isEmpty()) {
            TreeNode node = deque.poll();
            
            // Swap left and right children
            TreeNode temp = node.left;
            node.left = node.right;
            node.right = temp;
            
            // Add children to deque for processing
            if (node.left != null) {
                deque.offer(node.left);
            }
            if (node.right != null) {
                deque.offer(node.right);
            }
        }
        
        return root;
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
     * Convert tree to array representation
     */
    public static List<Integer> treeToArray(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
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
     * Print tree in a visual format
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
            int levelSize = queue.size();
            List<String> level = new ArrayList<>();
            boolean hasNext = false;
            
            for (int i = 0; i < levelSize; i++) {
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
            if (!hasNext) {
                break;
            }
        }
        
        for (List<String> level : levels) {
            System.out.println(level);
        }
    }
    
    /**
     * Create a copy of the tree to preserve original for testing
     */
    public static TreeNode copyTree(TreeNode root) {
        if (root == null) {
            return null;
        }
        
        TreeNode newRoot = new TreeNode(root.val);
        newRoot.left = copyTree(root.left);
        newRoot.right = copyTree(root.right);
        
        return newRoot;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        InvertBinaryTree solution = new InvertBinaryTree();
        
        // Test case 1: Standard example
        System.out.println("Test Case 1: [4,2,7,1,3,6,9]");
        TreeNode root1 = createTree(new Integer[]{4, 2, 7, 1, 3, 6, 9});
        System.out.print("Original tree: ");
        System.out.println(treeToArray(root1));
        printTree(root1);
        
        TreeNode inverted1 = solution.invertTreeRecursive(copyTree(root1));
        System.out.print("Inverted (Recursive): ");
        System.out.println(treeToArray(inverted1));
        printTree(inverted1);
        System.out.println();
        
        // Test case 2: Simple tree
        System.out.println("Test Case 2: [2,1,3]");
        TreeNode root2 = createTree(new Integer[]{2, 1, 3});
        System.out.print("Original: ");
        System.out.println(treeToArray(root2));
        
        TreeNode inverted2 = solution.invertTreeBFS(copyTree(root2));
        System.out.print("Inverted (BFS): ");
        System.out.println(treeToArray(inverted2));
        System.out.println();
        
        // Test case 3: Single node
        System.out.println("Test Case 3: [1]");
        TreeNode root3 = createTree(new Integer[]{1});
        System.out.print("Original: ");
        System.out.println(treeToArray(root3));
        
        TreeNode inverted3 = solution.invertTreeDFS(copyTree(root3));
        System.out.print("Inverted (DFS): ");
        System.out.println(treeToArray(inverted3));
        System.out.println();
        
        // Test case 4: Empty tree
        System.out.println("Test Case 4: []");
        TreeNode root4 = null;
        TreeNode inverted4 = solution.invertTreeRecursive(root4);
        System.out.println("Inverted: " + treeToArray(inverted4));
        System.out.println();
        
        // Test case 5: Skewed tree
        System.out.println("Test Case 5: [1,2,null,3,null,4]");
        TreeNode root5 = createTree(new Integer[]{1, 2, null, 3, null, 4});
        System.out.print("Original: ");
        System.out.println(treeToArray(root5));
        
        TreeNode inverted5 = solution.invertTreePostOrder(copyTree(root5));
        System.out.print("Inverted (Post-order): ");
        System.out.println(treeToArray(inverted5));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
        
        // Correctness verification
        System.out.println("All approaches produce same result: " + verifyCorrectness(solution));
    }
    
    private static void performanceTest(InvertBinaryTree solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large complete binary tree
        TreeNode largeTree = createLargeTree(1000);
        
        long start, end;
        
        // Test Recursive approach
        start = System.nanoTime();
        TreeNode result1 = solution.invertTreeRecursive(copyTree(largeTree));
        end = System.nanoTime();
        System.out.println("Recursive: " + (end - start) / 1000000.0 + " ms");
        
        // Test BFS approach
        start = System.nanoTime();
        TreeNode result2 = solution.invertTreeBFS(copyTree(largeTree));
        end = System.nanoTime();
        System.out.println("BFS: " + (end - start) / 1000000.0 + " ms");
        
        // Test DFS approach
        start = System.nanoTime();
        TreeNode result3 = solution.invertTreeDFS(copyTree(largeTree));
        end = System.nanoTime();
        System.out.println("DFS: " + (end - start) / 1000000.0 + " ms");
        
        // Test Deque approach
        start = System.nanoTime();
        TreeNode result4 = solution.invertTreeDeque(copyTree(largeTree));
        end = System.nanoTime();
        System.out.println("Deque: " + (end - start) / 1000000.0 + " ms");
        
        // Test Post-order approach
        start = System.nanoTime();
        TreeNode result5 = solution.invertTreePostOrder(copyTree(largeTree));
        end = System.nanoTime();
        System.out.println("Post-order: " + (end - start) / 1000000.0 + " ms");
    }
    
    private static TreeNode createLargeTree(int numNodes) {
        if (numNodes <= 0) {
            return null;
        }
        
        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        int nodeValue = 2;
        
        while (!queue.isEmpty() && nodeValue <= numNodes) {
            TreeNode node = queue.poll();
            
            if (nodeValue <= numNodes) {
                node.left = new TreeNode(nodeValue++);
                queue.offer(node.left);
            }
            
            if (nodeValue <= numNodes) {
                node.right = new TreeNode(nodeValue++);
                queue.offer(node.right);
            }
        }
        
        return root;
    }
    
    /**
     * Verify that all approaches produce the same result
     */
    public static boolean verifyCorrectness(InvertBinaryTree solution) {
        TreeNode testTree = createTree(new Integer[]{4, 2, 7, 1, 3, 6, 9});
        
        List<Integer> result1 = treeToArray(solution.invertTreeRecursive(copyTree(testTree)));
        List<Integer> result2 = treeToArray(solution.invertTreeBFS(copyTree(testTree)));
        List<Integer> result3 = treeToArray(solution.invertTreeDFS(copyTree(testTree)));
        List<Integer> result4 = treeToArray(solution.invertTreeDeque(copyTree(testTree)));
        List<Integer> result5 = treeToArray(solution.invertTreePostOrder(copyTree(testTree)));
        
        return result1.equals(result2) && result2.equals(result3) && 
               result3.equals(result4) && result4.equals(result5);
    }
} 