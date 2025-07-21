/**
 * Problem: Maximum Depth of Binary Tree
 * 
 * Given the root of a binary tree, return its maximum depth.
 * A binary tree's maximum depth is the number of nodes along the longest path 
 * from the root node down to the farthest leaf node.
 */

class TreeNode {
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

public class MaximumDepth {
    
    /**
     * APPROACH 1: RECURSIVE DFS (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height
     */
    public int maxDepth(TreeNode root) {
        if (root == null) return 0;
        
        int leftDepth = maxDepth(root.left);
        int rightDepth = maxDepth(root.right);
        
        return Math.max(leftDepth, rightDepth) + 1;
    }
    
    /**
     * APPROACH 2: ITERATIVE BFS
     * Time Complexity: O(n)
     * Space Complexity: O(w) where w is max width
     */
    public int maxDepthBFS(TreeNode root) {
        if (root == null) return 0;
        
        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int depth = 0;
        
        while (!queue.isEmpty()) {
            int size = queue.size();
            depth++;
            
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                
                if (node.left != null) queue.offer(node.left);
                if (node.right != null) queue.offer(node.right);
            }
        }
        
        return depth;
    }
    
    /**
     * APPROACH 3: ITERATIVE DFS
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     */
    public int maxDepthDFS(TreeNode root) {
        if (root == null) return 0;
        
        java.util.Stack<java.util.AbstractMap.SimpleEntry<TreeNode, Integer>> stack = new java.util.Stack<>();
        stack.push(new java.util.AbstractMap.SimpleEntry<>(root, 1));
        
        int maxDepth = 0;
        
        while (!stack.isEmpty()) {
            java.util.AbstractMap.SimpleEntry<TreeNode, Integer> entry = stack.pop();
            TreeNode node = entry.getKey();
            int currentDepth = entry.getValue();
            
            if (node != null) {
                maxDepth = Math.max(maxDepth, currentDepth);
                stack.push(new java.util.AbstractMap.SimpleEntry<>(node.left, currentDepth + 1));
                stack.push(new java.util.AbstractMap.SimpleEntry<>(node.right, currentDepth + 1));
            }
        }
        
        return maxDepth;
    }
    
    // Test method
    public static void main(String[] args) {
        MaximumDepth solution = new MaximumDepth();
        
        // Create test tree: [3,9,20,null,null,15,7]
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(9);
        root.right = new TreeNode(20);
        root.right.left = new TreeNode(15);
        root.right.right = new TreeNode(7);
        
        System.out.println("Max depth (recursive): " + solution.maxDepth(root));
        System.out.println("Max depth (BFS): " + solution.maxDepthBFS(root));
        System.out.println("Max depth (DFS): " + solution.maxDepthDFS(root));
    }
} 