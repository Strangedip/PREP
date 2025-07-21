import java.util.*;

/**
 * Problem: Lowest Common Ancestor of a Binary Search Tree
 * 
 * Given a binary search tree (BST), find the lowest common ancestor (LCA) of two given nodes in the BST.
 * 
 * According to the definition of LCA on Wikipedia: "The lowest common ancestor is defined 
 * between two nodes p and q as the lowest node in T that has both p and q as descendants 
 * (where we allow a node to be a descendant of itself)."
 * 
 * Example 1:
 * Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
 * Output: 6
 * Explanation: The LCA of nodes 2 and 8 is 6.
 * 
 * Example 2:
 * Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
 * Output: 2
 * Explanation: The LCA of nodes 2 and 4 is 2, since a node can be a descendant of itself.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^5].
 * - -10^9 <= Node.val <= 10^9
 * - All Node.val are unique.
 * - p != q
 * - p and q will exist in the BST.
 */
public class LowestCommonAncestorBST {
    
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
     * APPROACH 1: RECURSIVE SOLUTION (Using BST Properties)
     * Time Complexity: O(h) where h is height of tree
     * Space Complexity: O(h) for recursion stack
     * 
     * Key insight: Use BST property to determine which subtree to explore.
     * - If both p and q are less than root, LCA is in left subtree
     * - If both p and q are greater than root, LCA is in right subtree
     * - Otherwise, root is the LCA
     */
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        // Base case
        if (root == null) {
            return null;
        }
        
        // If both p and q are smaller than root, LCA lies in left subtree
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        
        // If both p and q are greater than root, LCA lies in right subtree
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }
        
        // If one is smaller and other is greater (or equal), root is LCA
        return root;
    }
    
    /**
     * APPROACH 2: ITERATIVE SOLUTION
     * Time Complexity: O(h)
     * Space Complexity: O(1)
     * 
     * More space-efficient as it doesn't use recursion stack.
     */
    public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode current = root;
        
        while (current != null) {
            // If both p and q are smaller than current, go left
            if (p.val < current.val && q.val < current.val) {
                current = current.left;
            }
            // If both p and q are greater than current, go right
            else if (p.val > current.val && q.val > current.val) {
                current = current.right;
            }
            // Current node is the LCA
            else {
                return current;
            }
        }
        
        return null;
    }
    
    /**
     * APPROACH 3: USING VALUES ONLY (When nodes might not exist)
     * Time Complexity: O(h)
     * Space Complexity: O(h)
     * 
     * Useful when you only have values instead of node references.
     */
    public TreeNode lowestCommonAncestorByValue(TreeNode root, int pVal, int qVal) {
        if (root == null) {
            return null;
        }
        
        // Ensure pVal <= qVal for easier logic
        if (pVal > qVal) {
            int temp = pVal;
            pVal = qVal;
            qVal = temp;
        }
        
        // If root value is between p and q (inclusive), root is LCA
        if (root.val >= pVal && root.val <= qVal) {
            return root;
        }
        
        // If both values are smaller than root, go left
        if (qVal < root.val) {
            return lowestCommonAncestorByValue(root.left, pVal, qVal);
        }
        
        // If both values are greater than root, go right
        return lowestCommonAncestorByValue(root.right, pVal, qVal);
    }
    
    /**
     * APPROACH 4: PATH-BASED SOLUTION
     * Time Complexity: O(h)
     * Space Complexity: O(h)
     * 
     * Find paths from root to both nodes, then find last common node.
     */
    public TreeNode lowestCommonAncestorWithPath(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathToP = findPath(root, p.val);
        List<TreeNode> pathToQ = findPath(root, q.val);
        
        TreeNode lca = null;
        int minLength = Math.min(pathToP.size(), pathToQ.size());
        
        for (int i = 0; i < minLength; i++) {
            if (pathToP.get(i).val == pathToQ.get(i).val) {
                lca = pathToP.get(i);
            } else {
                break;
            }
        }
        
        return lca;
    }
    
    /**
     * Helper method to find path from root to target value
     */
    private List<TreeNode> findPath(TreeNode root, int target) {
        List<TreeNode> path = new ArrayList<>();
        TreeNode current = root;
        
        while (current != null) {
            path.add(current);
            
            if (target == current.val) {
                break;
            } else if (target < current.val) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        
        return path;
    }
    
    /**
     * APPROACH 5: RECURSIVE WITH VALIDATION
     * Time Complexity: O(h)
     * Space Complexity: O(h)
     * 
     * Includes validation that both nodes exist in the tree.
     */
    public TreeNode lowestCommonAncestorWithValidation(TreeNode root, TreeNode p, TreeNode q) {
        // First, verify both nodes exist in the tree
        if (!contains(root, p.val) || !contains(root, q.val)) {
            return null;
        }
        
        return lowestCommonAncestor(root, p, q);
    }
    
    /**
     * Helper method to check if a value exists in BST
     */
    private boolean contains(TreeNode root, int val) {
        TreeNode current = root;
        
        while (current != null) {
            if (val == current.val) {
                return true;
            } else if (val < current.val) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        
        return false;
    }
    
    /**
     * APPROACH 6: GENERIC LCA (Works for any binary tree, not just BST)
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * This approach doesn't use BST properties - included for comparison.
     */
    public TreeNode lowestCommonAncestorGeneric(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q) {
            return root;
        }
        
        TreeNode left = lowestCommonAncestorGeneric(root.left, p, q);
        TreeNode right = lowestCommonAncestorGeneric(root.right, p, q);
        
        if (left != null && right != null) {
            return root;
        }
        
        return left != null ? left : right;
    }
    
    // ==================== ADVANCED OPERATIONS ====================
    
    /**
     * Find LCA of multiple nodes
     */
    public TreeNode lowestCommonAncestorMultiple(TreeNode root, List<TreeNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return null;
        }
        
        TreeNode lca = nodes.get(0);
        
        for (int i = 1; i < nodes.size(); i++) {
            lca = lowestCommonAncestor(root, lca, nodes.get(i));
        }
        
        return lca;
    }
    
    /**
     * Find distance between two nodes using LCA
     */
    public int distanceBetweenNodes(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode lca = lowestCommonAncestor(root, p, q);
        
        int distanceToP = findDistance(lca, p.val);
        int distanceToQ = findDistance(lca, q.val);
        
        return distanceToP + distanceToQ;
    }
    
    /**
     * Helper method to find distance from root to target
     */
    private int findDistance(TreeNode root, int target) {
        if (root == null) {
            return -1;
        }
        
        if (root.val == target) {
            return 0;
        }
        
        int distance = -1;
        if (target < root.val) {
            distance = findDistance(root.left, target);
        } else {
            distance = findDistance(root.right, target);
        }
        
        return distance == -1 ? -1 : distance + 1;
    }
    
    /**
     * Find all ancestors of a node
     */
    public List<TreeNode> findAncestors(TreeNode root, int target) {
        List<TreeNode> ancestors = new ArrayList<>();
        findAncestorsHelper(root, target, ancestors);
        return ancestors;
    }
    
    private boolean findAncestorsHelper(TreeNode node, int target, List<TreeNode> ancestors) {
        if (node == null) {
            return false;
        }
        
        if (node.val == target) {
            return true;
        }
        
        ancestors.add(node);
        
        boolean found = false;
        if (target < node.val) {
            found = findAncestorsHelper(node.left, target, ancestors);
        } else {
            found = findAncestorsHelper(node.right, target, ancestors);
        }
        
        if (!found) {
            ancestors.remove(ancestors.size() - 1);
        }
        
        return found;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Create BST from sorted array
     */
    public static TreeNode createBST(int[] sortedArray) {
        return createBSTHelper(sortedArray, 0, sortedArray.length - 1);
    }
    
    private static TreeNode createBSTHelper(int[] array, int left, int right) {
        if (left > right) {
            return null;
        }
        
        int mid = left + (right - left) / 2;
        TreeNode node = new TreeNode(array[mid]);
        
        node.left = createBSTHelper(array, left, mid - 1);
        node.right = createBSTHelper(array, mid + 1, right);
        
        return node;
    }
    
    /**
     * Find node by value
     */
    public static TreeNode findNode(TreeNode root, int val) {
        while (root != null) {
            if (root.val == val) {
                return root;
            } else if (val < root.val) {
                root = root.left;
            } else {
                root = root.right;
            }
        }
        return null;
    }
    
    /**
     * Print tree structure
     */
    public static void printBST(TreeNode root) {
        if (root == null) {
            System.out.println("Empty BST");
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
    
    /**
     * Get inorder traversal
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
    
    // ==================== TEST METHODS ====================
    
    public static void main(String[] args) {
        LowestCommonAncestorBST solution = new LowestCommonAncestorBST();
        
        System.out.println("=== Lowest Common Ancestor in BST Demo ===\n");
        
        // Test Case 1: Standard BST
        System.out.println("1. Standard BST: [6,2,8,0,4,7,9,null,null,3,5]");
        TreeNode root = createBST(new int[]{0, 2, 3, 4, 5, 6, 7, 8, 9});
        printBST(root);
        
        // Find nodes
        TreeNode node2 = findNode(root, 2);
        TreeNode node8 = findNode(root, 8);
        TreeNode node3 = findNode(root, 3);
        TreeNode node5 = findNode(root, 5);
        
        // Test different approaches
        TreeNode lca1 = solution.lowestCommonAncestor(root, node2, node8);
        TreeNode lca2 = solution.lowestCommonAncestorIterative(root, node2, node8);
        TreeNode lca3 = solution.lowestCommonAncestorWithPath(root, node2, node8);
        
        System.out.println("LCA of 2 and 8:");
        System.out.println("  Recursive: " + (lca1 != null ? lca1.val : "null"));
        System.out.println("  Iterative: " + (lca2 != null ? lca2.val : "null"));
        System.out.println("  Path-based: " + (lca3 != null ? lca3.val : "null"));
        
        // Test when one node is ancestor of another
        TreeNode lca4 = solution.lowestCommonAncestor(root, node2, node3);
        System.out.println("LCA of 2 and 3: " + (lca4 != null ? lca4.val : "null"));
        
        // Test siblings
        TreeNode lca5 = solution.lowestCommonAncestor(root, node3, node5);
        System.out.println("LCA of 3 and 5: " + (lca5 != null ? lca5.val : "null"));
        System.out.println();
        
        // Test Case 2: By value
        System.out.println("2. Testing LCA by value:");
        TreeNode lcaByVal = solution.lowestCommonAncestorByValue(root, 3, 7);
        System.out.println("LCA of values 3 and 7: " + (lcaByVal != null ? lcaByVal.val : "null"));
        System.out.println();
        
        // Test Case 3: Distance between nodes
        System.out.println("3. Distance between nodes:");
        int distance = solution.distanceBetweenNodes(root, node3, node5);
        System.out.println("Distance between 3 and 5: " + distance);
        
        distance = solution.distanceBetweenNodes(root, node2, node8);
        System.out.println("Distance between 2 and 8: " + distance);
        System.out.println();
        
        // Test Case 4: Multiple nodes LCA
        System.out.println("4. LCA of multiple nodes:");
        List<TreeNode> multipleNodes = Arrays.asList(node3, node5, findNode(root, 7));
        TreeNode multiLCA = solution.lowestCommonAncestorMultiple(root, multipleNodes);
        System.out.println("LCA of [3, 5, 7]: " + (multiLCA != null ? multiLCA.val : "null"));
        System.out.println();
        
        // Test Case 5: Find ancestors
        System.out.println("5. Ancestors of node 3:");
        List<TreeNode> ancestors = solution.findAncestors(root, 3);
        System.out.print("Ancestors: ");
        for (TreeNode ancestor : ancestors) {
            System.out.print(ancestor.val + " ");
        }
        System.out.println("\n");
        
        // Test Case 6: Edge cases
        System.out.println("6. Edge Cases:");
        
        // Root is LCA
        TreeNode rootLCA = solution.lowestCommonAncestor(root, findNode(root, 2), findNode(root, 8));
        System.out.println("Root as LCA (2, 8): " + (rootLCA != null ? rootLCA.val : "null"));
        
        // Same node
        TreeNode sameLCA = solution.lowestCommonAncestor(root, node3, node3);
        System.out.println("Same node LCA (3, 3): " + (sameLCA != null ? sameLCA.val : "null"));
        
        // Single path tree
        TreeNode pathRoot = new TreeNode(1);
        pathRoot.right = new TreeNode(2);
        pathRoot.right.right = new TreeNode(3);
        
        TreeNode pathLCA = solution.lowestCommonAncestor(pathRoot, pathRoot, pathRoot.right.right);
        System.out.println("Path tree LCA (1, 3): " + (pathLCA != null ? pathLCA.val : "null"));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
        
        // Correctness verification
        System.out.println("All approaches produce consistent results: " + verifyCorrectness(solution));
    }
    
    private static void performanceTest(LowestCommonAncestorBST solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large balanced BST
        int size = 10000;
        int[] sortedArray = new int[size];
        for (int i = 0; i < size; i++) {
            sortedArray[i] = i;
        }
        
        TreeNode largeRoot = createBST(sortedArray);
        TreeNode p = findNode(largeRoot, 1000);
        TreeNode q = findNode(largeRoot, 8000);
        
        long start, end;
        
        // Test recursive approach
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            solution.lowestCommonAncestor(largeRoot, p, q);
        }
        end = System.nanoTime();
        System.out.println("Recursive (1000 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test iterative approach
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            solution.lowestCommonAncestorIterative(largeRoot, p, q);
        }
        end = System.nanoTime();
        System.out.println("Iterative (1000 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test path-based approach
        start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            solution.lowestCommonAncestorWithPath(largeRoot, p, q);
        }
        end = System.nanoTime();
        System.out.println("Path-based (1000 ops): " + (end - start) / 1000000.0 + " ms");
        System.out.println();
    }
    
    private static boolean verifyCorrectness(LowestCommonAncestorBST solution) {
        // Create test BST
        TreeNode root = createBST(new int[]{1, 2, 3, 4, 5, 6, 7});
        TreeNode node2 = findNode(root, 2);
        TreeNode node6 = findNode(root, 6);
        
        // Test all approaches give same result
        TreeNode lca1 = solution.lowestCommonAncestor(root, node2, node6);
        TreeNode lca2 = solution.lowestCommonAncestorIterative(root, node2, node6);
        TreeNode lca3 = solution.lowestCommonAncestorWithPath(root, node2, node6);
        TreeNode lca4 = solution.lowestCommonAncestorByValue(root, 2, 6);
        
        return lca1.val == lca2.val && lca2.val == lca3.val && lca3.val == lca4.val;
    }
} 