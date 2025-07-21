import java.util.*;

/**
 * Problem: Binary Search Tree Operations
 * 
 * Implement fundamental BST operations:
 * 1. Insert a node into BST
 * 2. Search for a value in BST  
 * 3. Delete a node from BST
 * 
 * BST Properties:
 * - Left subtree contains only nodes with values less than the node's value
 * - Right subtree contains only nodes with values greater than the node's value
 * - Both left and right subtrees are also binary search trees
 * 
 * Examples:
 * Insert: Insert 5 into [4,2,7,1,3] → [4,2,7,1,3,5]
 * Search: Search 3 in [4,2,7,1,3] → true
 * Delete: Delete 2 from [4,2,7,1,3] → [4,1,7,null,3] or [4,3,7,1]
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -10^8 <= Node.val <= 10^8
 * - It's guaranteed that val is unique
 */
public class BSTOperations {
    
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
    
    // ==================== INSERT OPERATIONS ====================
    
    /**
     * APPROACH 1: RECURSIVE INSERT
     * Time Complexity: O(h) where h is height - O(log n) average, O(n) worst
     * Space Complexity: O(h) for recursion stack
     */
    public TreeNode insertBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }
        
        if (val < root.val) {
            root.left = insertBST(root.left, val);
        } else if (val > root.val) {
            root.right = insertBST(root.right, val);
        }
        // If val == root.val, we don't insert (no duplicates)
        
        return root;
    }
    
    /**
     * APPROACH 2: ITERATIVE INSERT
     * Time Complexity: O(h)
     * Space Complexity: O(1)
     */
    public TreeNode insertBSTIterative(TreeNode root, int val) {
        TreeNode newNode = new TreeNode(val);
        
        if (root == null) {
            return newNode;
        }
        
        TreeNode current = root;
        TreeNode parent = null;
        
        while (current != null) {
            parent = current;
            if (val < current.val) {
                current = current.left;
            } else if (val > current.val) {
                current = current.right;
            } else {
                // Value already exists, don't insert
                return root;
            }
        }
        
        // Insert as child of parent
        if (val < parent.val) {
            parent.left = newNode;
        } else {
            parent.right = newNode;
        }
        
        return root;
    }
    
    // ==================== SEARCH OPERATIONS ====================
    
    /**
     * APPROACH 1: RECURSIVE SEARCH
     * Time Complexity: O(h)
     * Space Complexity: O(h)
     */
    public boolean searchBST(TreeNode root, int val) {
        if (root == null) {
            return false;
        }
        
        if (val == root.val) {
            return true;
        }
        
        if (val < root.val) {
            return searchBST(root.left, val);
        } else {
            return searchBST(root.right, val);
        }
    }
    
    /**
     * APPROACH 2: ITERATIVE SEARCH
     * Time Complexity: O(h)
     * Space Complexity: O(1)
     */
    public boolean searchBSTIterative(TreeNode root, int val) {
        TreeNode current = root;
        
        while (current != null) {
            if (val == current.val) {
                return true;
            }
            
            if (val < current.val) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
        
        return false;
    }
    
    /**
     * SEARCH AND RETURN NODE (useful for other operations)
     */
    public TreeNode searchNode(TreeNode root, int val) {
        if (root == null || root.val == val) {
            return root;
        }
        
        if (val < root.val) {
            return searchNode(root.left, val);
        } else {
            return searchNode(root.right, val);
        }
    }
    
    // ==================== DELETE OPERATIONS ====================
    
    /**
     * APPROACH 1: RECURSIVE DELETE
     * Time Complexity: O(h)
     * Space Complexity: O(h)
     * 
     * Three cases for deletion:
     * 1. Node has no children (leaf) - simply remove
     * 2. Node has one child - replace node with its child
     * 3. Node has two children - replace with inorder successor/predecessor
     */
    public TreeNode deleteNode(TreeNode root, int key) {
        if (root == null) {
            return null;
        }
        
        if (key < root.val) {
            root.left = deleteNode(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNode(root.right, key);
        } else {
            // Found the node to delete
            
            // Case 1: No children (leaf node)
            if (root.left == null && root.right == null) {
                return null;
            }
            
            // Case 2: One child
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            
            // Case 3: Two children
            // Find inorder successor (smallest node in right subtree)
            TreeNode successor = findMin(root.right);
            
            // Replace current node's value with successor's value
            root.val = successor.val;
            
            // Delete the successor
            root.right = deleteNode(root.right, successor.val);
        }
        
        return root;
    }
    
    /**
     * ALTERNATIVE DELETE: Using inorder predecessor
     */
    public TreeNode deleteNodeWithPredecessor(TreeNode root, int key) {
        if (root == null) {
            return null;
        }
        
        if (key < root.val) {
            root.left = deleteNodeWithPredecessor(root.left, key);
        } else if (key > root.val) {
            root.right = deleteNodeWithPredecessor(root.right, key);
        } else {
            if (root.left == null && root.right == null) {
                return null;
            }
            
            if (root.left == null) {
                return root.right;
            }
            if (root.right == null) {
                return root.left;
            }
            
            // Use inorder predecessor (largest in left subtree)
            TreeNode predecessor = findMax(root.left);
            root.val = predecessor.val;
            root.left = deleteNodeWithPredecessor(root.left, predecessor.val);
        }
        
        return root;
    }
    
    /**
     * APPROACH 2: ITERATIVE DELETE
     * Time Complexity: O(h)
     * Space Complexity: O(1)
     */
    public TreeNode deleteNodeIterative(TreeNode root, int key) {
        if (root == null) return null;
        
        // Special case: deleting root
        if (root.val == key) {
            return deleteNodeHelper(root);
        }
        
        TreeNode current = root;
        
        while (current != null) {
            if (key < current.val) {
                if (current.left != null && current.left.val == key) {
                    current.left = deleteNodeHelper(current.left);
                    break;
                }
                current = current.left;
            } else {
                if (current.right != null && current.right.val == key) {
                    current.right = deleteNodeHelper(current.right);
                    break;
                }
                current = current.right;
            }
        }
        
        return root;
    }
    
    private TreeNode deleteNodeHelper(TreeNode node) {
        if (node.left == null) return node.right;
        if (node.right == null) return node.left;
        
        // Two children - find successor
        TreeNode successor = node.right;
        while (successor.left != null) {
            successor = successor.left;
        }
        
        successor.left = node.left;
        return node.right;
    }
    
    // ==================== HELPER METHODS ====================
    
    /**
     * Find minimum value node in BST
     */
    public TreeNode findMin(TreeNode root) {
        if (root == null) return null;
        
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }
    
    /**
     * Find maximum value node in BST
     */
    public TreeNode findMax(TreeNode root) {
        if (root == null) return null;
        
        while (root.right != null) {
            root = root.right;
        }
        return root;
    }
    
    /**
     * Find inorder successor
     */
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
    
    /**
     * Find inorder predecessor
     */
    public TreeNode findPredecessor(TreeNode root, int val) {
        TreeNode predecessor = null;
        
        while (root != null) {
            if (val > root.val) {
                predecessor = root;
                root = root.right;
            } else {
                root = root.left;
            }
        }
        
        return predecessor;
    }
    
    /**
     * Validate if tree is a valid BST
     */
    public boolean isValidBST(TreeNode root) {
        return isValidBSTHelper(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }
    
    private boolean isValidBSTHelper(TreeNode node, long min, long max) {
        if (node == null) return true;
        
        if (node.val <= min || node.val >= max) {
            return false;
        }
        
        return isValidBSTHelper(node.left, min, node.val) &&
               isValidBSTHelper(node.right, node.val, max);
    }
    
    /**
     * Get inorder traversal (should be sorted for valid BST)
     */
    public List<Integer> inorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        inorderHelper(root, result);
        return result;
    }
    
    private void inorderHelper(TreeNode node, List<Integer> result) {
        if (node != null) {
            inorderHelper(node.left, result);
            result.add(node.val);
            inorderHelper(node.right, result);
        }
    }
    
    /**
     * Create BST from sorted array
     */
    public TreeNode sortedArrayToBST(int[] nums) {
        return sortedArrayToBSTHelper(nums, 0, nums.length - 1);
    }
    
    private TreeNode sortedArrayToBSTHelper(int[] nums, int left, int right) {
        if (left > right) return null;
        
        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);
        
        root.left = sortedArrayToBSTHelper(nums, left, mid - 1);
        root.right = sortedArrayToBSTHelper(nums, mid + 1, right);
        
        return root;
    }
    
    // ==================== UTILITY METHODS ====================
    
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
     * Convert BST to array representation
     */
    public static List<Integer> bstToArray(TreeNode root) {
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
    
    // ==================== TEST METHODS ====================
    
    public static void main(String[] args) {
        BSTOperations bst = new BSTOperations();
        
        System.out.println("=== BST Operations Demo ===\n");
        
        // Test 1: Build BST from scratch
        System.out.println("1. Building BST by inserting: 4, 2, 7, 1, 3, 6, 9");
        TreeNode root = null;
        int[] insertValues = {4, 2, 7, 1, 3, 6, 9};
        
        for (int val : insertValues) {
            root = bst.insertBST(root, val);
            System.out.println("Inserted " + val + ": " + bst.inorderTraversal(root));
        }
        
        System.out.println("Final BST structure:");
        printBST(root);
        System.out.println("Is valid BST: " + bst.isValidBST(root));
        System.out.println();
        
        // Test 2: Search operations
        System.out.println("2. Search Operations:");
        int[] searchValues = {3, 5, 7, 10};
        for (int val : searchValues) {
            boolean found = bst.searchBST(root, val);
            System.out.println("Search " + val + ": " + found);
        }
        System.out.println();
        
        // Test 3: Find min/max
        System.out.println("3. Min/Max Operations:");
        TreeNode min = bst.findMin(root);
        TreeNode max = bst.findMax(root);
        System.out.println("Minimum value: " + (min != null ? min.val : "null"));
        System.out.println("Maximum value: " + (max != null ? max.val : "null"));
        System.out.println();
        
        // Test 4: Successor/Predecessor
        System.out.println("4. Successor/Predecessor:");
        TreeNode succ3 = bst.findSuccessor(root, 3);
        TreeNode pred7 = bst.findPredecessor(root, 7);
        System.out.println("Successor of 3: " + (succ3 != null ? succ3.val : "null"));
        System.out.println("Predecessor of 7: " + (pred7 != null ? pred7.val : "null"));
        System.out.println();
        
        // Test 5: Delete operations
        System.out.println("5. Delete Operations:");
        System.out.println("Before deletion: " + bst.inorderTraversal(root));
        
        // Delete leaf node
        System.out.println("Deleting leaf node 1:");
        root = bst.deleteNode(root, 1);
        System.out.println("After deletion: " + bst.inorderTraversal(root));
        printBST(root);
        
        // Delete node with one child
        System.out.println("Deleting node with one child 6:");
        root = bst.deleteNode(root, 6);
        System.out.println("After deletion: " + bst.inorderTraversal(root));
        printBST(root);
        
        // Delete node with two children
        System.out.println("Deleting node with two children 4:");
        root = bst.deleteNode(root, 4);
        System.out.println("After deletion: " + bst.inorderTraversal(root));
        printBST(root);
        System.out.println();
        
        // Test 6: Edge cases
        System.out.println("6. Edge Cases:");
        
        // Empty tree operations
        TreeNode emptyRoot = null;
        emptyRoot = bst.insertBST(emptyRoot, 5);
        System.out.println("Insert into empty tree: " + bst.inorderTraversal(emptyRoot));
        
        boolean searchEmpty = bst.searchBST(null, 5);
        System.out.println("Search in empty tree: " + searchEmpty);
        
        TreeNode deletedEmpty = bst.deleteNode(null, 5);
        System.out.println("Delete from empty tree: " + (deletedEmpty == null ? "null" : "not null"));
        
        // Single node tree
        TreeNode singleNode = new TreeNode(42);
        singleNode = bst.deleteNode(singleNode, 42);
        System.out.println("Delete only node: " + (singleNode == null ? "null" : "not null"));
        System.out.println();
        
        // Performance test
        performanceTest(bst);
        
        // Correctness verification
        System.out.println("All operations maintain BST property: " + verifyCorrectness(bst));
    }
    
    private static void performanceTest(BSTOperations bst) {
        System.out.println("=== Performance Test ===");
        
        // Build large BST
        TreeNode root = null;
        int size = 1000;
        
        long start = System.nanoTime();
        for (int i = 1; i <= size; i++) {
            root = bst.insertBST(root, i);
        }
        long end = System.nanoTime();
        System.out.println("Insert " + size + " nodes: " + (end - start) / 1000000.0 + " ms");
        
        // Search test
        start = System.nanoTime();
        for (int i = 1; i <= size; i++) {
            bst.searchBST(root, i);
        }
        end = System.nanoTime();
        System.out.println("Search " + size + " nodes: " + (end - start) / 1000000.0 + " ms");
        
        // Delete test
        start = System.nanoTime();
        for (int i = 1; i <= size / 2; i++) {
            root = bst.deleteNode(root, i);
        }
        end = System.nanoTime();
        System.out.println("Delete " + (size / 2) + " nodes: " + (end - start) / 1000000.0 + " ms");
        
        System.out.println("Final tree size: " + bst.inorderTraversal(root).size());
        System.out.println();
    }
    
    private static boolean verifyCorrectness(BSTOperations bst) {
        // Test various BST operations and verify BST property
        TreeNode root = bst.sortedArrayToBST(new int[]{1, 2, 3, 4, 5, 6, 7});
        
        // Insert and verify
        root = bst.insertBST(root, 0);
        root = bst.insertBST(root, 8);
        if (!bst.isValidBST(root)) return false;
        
        // Search and verify
        if (!bst.searchBST(root, 4)) return false;
        if (bst.searchBST(root, 10)) return false;
        
        // Delete and verify
        root = bst.deleteNode(root, 4);
        if (!bst.isValidBST(root)) return false;
        
        return true;
    }
} 