import java.util.*;

/**
 * Problem: Kth Smallest Element in a BST
 * 
 * Given the root of a binary search tree, and an integer k, return the kth smallest 
 * value (1-indexed) of all the values of the nodes in the tree.
 * 
 * Example 1:
 * Input: root = [3,1,4,null,2], k = 1
 * Output: 1
 * 
 * Example 2:
 * Input: root = [5,3,6,2,4,null,null,1], k = 3
 * Output: 3
 * 
 * Constraints:
 * - The number of nodes in the tree is n.
 * - 1 <= k <= n <= 10^4
 * - 0 <= Node.val <= 10^4
 * 
 * Follow-up: If the BST is modified (insert/delete operations) often and you need to 
 * find the kth smallest frequently, how would you optimize the kthSmallest method?
 */
public class KthSmallestElementBST {
    
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
     * APPROACH 1: INORDER TRAVERSAL - RECURSIVE
     * Time Complexity: O(n) worst case, O(k) average case
     * Space Complexity: O(n) for storing all elements
     * 
     * Perform inorder traversal and return kth element.
     */
    public int kthSmallest(TreeNode root, int k) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return inorder.get(k - 1);
    }
    
    private void inorderTraversal(TreeNode node, List<Integer> result) {
        if (node != null) {
            inorderTraversal(node.left, result);
            result.add(node.val);
            inorderTraversal(node.right, result);
        }
    }
    
    /**
     * APPROACH 2: OPTIMIZED INORDER - EARLY TERMINATION
     * Time Complexity: O(H + k) where H is height
     * Space Complexity: O(H) for recursion stack
     * 
     * Stop traversal once we find the kth element.
     */
    private int count = 0;
    private int result = 0;
    
    public int kthSmallestOptimized(TreeNode root, int k) {
        count = 0;
        result = 0;
        inorderOptimized(root, k);
        return result;
    }
    
    private void inorderOptimized(TreeNode node, int k) {
        if (node == null) return;
        
        inorderOptimized(node.left, k);
        
        count++;
        if (count == k) {
            result = node.val;
            return;
        }
        
        inorderOptimized(node.right, k);
    }
    
    /**
     * APPROACH 3: ITERATIVE INORDER WITH STACK
     * Time Complexity: O(H + k)
     * Space Complexity: O(H) for stack
     * 
     * Use stack to simulate recursion and early termination.
     */
    public int kthSmallestIterative(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        int count = 0;
        
        while (current != null || !stack.isEmpty()) {
            // Go to leftmost node
            while (current != null) {
                stack.push(current);
                current = current.left;
            }
            
            // Process node
            current = stack.pop();
            count++;
            
            if (count == k) {
                return current.val;
            }
            
            // Move to right subtree
            current = current.right;
        }
        
        return -1; // Should never reach here with valid input
    }
    
    /**
     * APPROACH 4: MORRIS TRAVERSAL (CONSTANT SPACE)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use Morris traversal for inorder without recursion or stack.
     */
    public int kthSmallestMorris(TreeNode root, int k) {
        TreeNode current = root;
        int count = 0;
        
        while (current != null) {
            if (current.left == null) {
                // Process current node
                count++;
                if (count == k) {
                    return current.val;
                }
                current = current.right;
            } else {
                // Find inorder predecessor
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }
                
                if (predecessor.right == null) {
                    // Make current the right child of predecessor
                    predecessor.right = current;
                    current = current.left;
                } else {
                    // Restore tree and process current node
                    predecessor.right = null;
                    count++;
                    if (count == k) {
                        return current.val;
                    }
                    current = current.right;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * APPROACH 5: BINARY SEARCH APPROACH
     * Time Complexity: O(n log n) worst case
     * Space Complexity: O(H)
     * 
     * Use BST properties to perform binary search.
     */
    public int kthSmallestBinarySearch(TreeNode root, int k) {
        int leftCount = countNodes(root.left);
        
        if (k <= leftCount) {
            // Kth smallest is in left subtree
            return kthSmallestBinarySearch(root.left, k);
        } else if (k == leftCount + 1) {
            // Current node is the kth smallest
            return root.val;
        } else {
            // Kth smallest is in right subtree
            return kthSmallestBinarySearch(root.right, k - leftCount - 1);
        }
    }
    
    private int countNodes(TreeNode node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }
    
    /**
     * APPROACH 6: AUGMENTED BST (For frequent queries)
     * Time Complexity: O(H) for query, O(H) for insert/delete
     * Space Complexity: O(1) additional per node
     * 
     * Store count of nodes in left subtree for each node.
     */
    public static class AugmentedTreeNode {
        int val;
        int leftCount; // Number of nodes in left subtree
        AugmentedTreeNode left;
        AugmentedTreeNode right;
        
        AugmentedTreeNode(int val) {
            this.val = val;
            this.leftCount = 0;
        }
    }
    
    public int kthSmallestAugmented(AugmentedTreeNode root, int k) {
        if (root == null) return -1;
        
        if (k <= root.leftCount) {
            // Kth smallest is in left subtree
            return kthSmallestAugmented(root.left, k);
        } else if (k == root.leftCount + 1) {
            // Current node is kth smallest
            return root.val;
        } else {
            // Kth smallest is in right subtree
            return kthSmallestAugmented(root.right, k - root.leftCount - 1);
        }
    }
    
    // Insert operation for augmented BST
    public AugmentedTreeNode insertAugmented(AugmentedTreeNode root, int val) {
        if (root == null) {
            return new AugmentedTreeNode(val);
        }
        
        if (val < root.val) {
            root.left = insertAugmented(root.left, val);
            root.leftCount++;
        } else if (val > root.val) {
            root.right = insertAugmented(root.right, val);
        }
        
        return root;
    }
    
    /**
     * APPROACH 7: TWO-PASS SOLUTION
     * Time Complexity: O(n)
     * Space Complexity: O(H)
     * 
     * First pass to count nodes, second pass to find kth element.
     */
    public int kthSmallestTwoPass(TreeNode root, int k) {
        int totalNodes = countNodes(root);
        
        // Validate k
        if (k < 1 || k > totalNodes) {
            return -1;
        }
        
        return findKthElement(root, k);
    }
    
    private int findKthElement(TreeNode node, int k) {
        if (node == null) return -1;
        
        int leftCount = countNodes(node.left);
        
        if (k <= leftCount) {
            return findKthElement(node.left, k);
        } else if (k == leftCount + 1) {
            return node.val;
        } else {
            return findKthElement(node.right, k - leftCount - 1);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Find kth largest element (reverse order)
     */
    public int kthLargest(TreeNode root, int k) {
        int totalNodes = countNodes(root);
        return kthSmallest(root, totalNodes - k + 1);
    }
    
    /**
     * Find median of BST
     */
    public double findMedian(TreeNode root) {
        int n = countNodes(root);
        
        if (n % 2 == 1) {
            // Odd number of nodes
            return kthSmallest(root, n / 2 + 1);
        } else {
            // Even number of nodes
            int mid1 = kthSmallest(root, n / 2);
            int mid2 = kthSmallest(root, n / 2 + 1);
            return (mid1 + mid2) / 2.0;
        }
    }
    
    /**
     * Find all elements in range [k1, k2]
     */
    public List<Integer> findRange(TreeNode root, int k1, int k2) {
        List<Integer> result = new ArrayList<>();
        
        if (k1 > k2) return result;
        
        findRangeHelper(root, k1, k2, result);
        return result;
    }
    
    private int currentIndex = 0;
    private void findRangeHelper(TreeNode node, int k1, int k2, List<Integer> result) {
        if (node == null) return;
        
        findRangeHelper(node.left, k1, k2, result);
        
        currentIndex++;
        if (currentIndex >= k1 && currentIndex <= k2) {
            result.add(node.val);
        }
        
        if (currentIndex < k2) {
            findRangeHelper(node.right, k1, k2, result);
        }
    }
    
    /**
     * Create BST from sorted array
     */
    public static TreeNode createBST(int[] sortedArray) {
        return createBSTHelper(sortedArray, 0, sortedArray.length - 1);
    }
    
    private static TreeNode createBSTHelper(int[] array, int left, int right) {
        if (left > right) return null;
        
        int mid = left + (right - left) / 2;
        TreeNode node = new TreeNode(array[mid]);
        
        node.left = createBSTHelper(array, left, mid - 1);
        node.right = createBSTHelper(array, mid + 1, right);
        
        return node;
    }
    
    /**
     * Convert BST to sorted array
     */
    public static List<Integer> bstToSortedArray(TreeNode root) {
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
     * Print BST structure
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
    
    // ==================== TEST METHODS ====================
    
    public static void main(String[] args) {
        KthSmallestElementBST solution = new KthSmallestElementBST();
        
        System.out.println("=== Kth Smallest Element in BST Demo ===\n");
        
        // Test Case 1: Standard BST
        System.out.println("1. BST: [3,1,4,null,2]");
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(4);
        root1.left.right = new TreeNode(2);
        
        printBST(root1);
        System.out.println("Sorted elements: " + bstToSortedArray(root1));
        
        // Test different approaches
        for (int k = 1; k <= 4; k++) {
            System.out.println("k=" + k + ":");
            System.out.println("  Recursive: " + solution.kthSmallest(root1, k));
            System.out.println("  Optimized: " + solution.kthSmallestOptimized(root1, k));
            System.out.println("  Iterative: " + solution.kthSmallestIterative(root1, k));
            System.out.println("  Morris: " + solution.kthSmallestMorris(root1, k));
            System.out.println("  Binary Search: " + solution.kthSmallestBinarySearch(root1, k));
        }
        System.out.println();
        
        // Test Case 2: Larger BST
        System.out.println("2. Larger BST: [5,3,6,2,4,null,null,1]");
        TreeNode root2 = createBST(new int[]{1, 2, 3, 4, 5, 6});
        printBST(root2);
        System.out.println("Sorted elements: " + bstToSortedArray(root2));
        
        // Test median
        double median = solution.findMedian(root2);
        System.out.println("Median: " + median);
        
        // Test range queries
        solution.currentIndex = 0;
        List<Integer> range = solution.findRange(root2, 2, 4);
        System.out.println("Elements from 2nd to 4th smallest: " + range);
        System.out.println();
        
        // Test Case 3: Augmented BST
        System.out.println("3. Augmented BST Demo:");
        AugmentedTreeNode augRoot = null;
        int[] values = {4, 2, 6, 1, 3, 5, 7};
        
        for (int val : values) {
            augRoot = solution.insertAugmented(augRoot, val);
        }
        
        System.out.println("Testing augmented BST with values: " + Arrays.toString(values));
        for (int k = 1; k <= values.length; k++) {
            int result = solution.kthSmallestAugmented(augRoot, k);
            System.out.println("k=" + k + ": " + result);
        }
        System.out.println();
        
        // Test Case 4: Edge Cases
        System.out.println("4. Edge Cases:");
        
        // Single node
        TreeNode singleNode = new TreeNode(42);
        System.out.println("Single node BST, k=1: " + solution.kthSmallest(singleNode, 1));
        
        // Linear tree (worst case for recursion)
        TreeNode linearRoot = new TreeNode(1);
        TreeNode current = linearRoot;
        for (int i = 2; i <= 5; i++) {
            current.right = new TreeNode(i);
            current = current.right;
        }
        System.out.println("Linear BST, k=3: " + solution.kthSmallestIterative(linearRoot, 3));
        
        // Perfect binary tree
        TreeNode perfectRoot = createBST(new int[]{1, 2, 3, 4, 5, 6, 7});
        System.out.println("Perfect BST, k=4: " + solution.kthSmallest(perfectRoot, 4));
        System.out.println();
        
        // Performance comparison
        performanceComparison(solution);
        
        // Correctness verification
        System.out.println("All approaches produce consistent results: " + verifyCorrectness(solution));
    }
    
    private static void performanceComparison(KthSmallestElementBST solution) {
        System.out.println("=== Performance Comparison ===");
        
        // Create large balanced BST
        int size = 10000;
        int[] sortedArray = new int[size];
        for (int i = 0; i < size; i++) {
            sortedArray[i] = i + 1;
        }
        
        TreeNode largeRoot = createBST(sortedArray);
        int k = size / 2; // Middle element
        
        long start, end;
        
        // Test recursive approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            solution.kthSmallest(largeRoot, k);
        }
        end = System.nanoTime();
        System.out.println("Recursive (100 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test optimized approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            solution.kthSmallestOptimized(largeRoot, k);
        }
        end = System.nanoTime();
        System.out.println("Optimized (100 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test iterative approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            solution.kthSmallestIterative(largeRoot, k);
        }
        end = System.nanoTime();
        System.out.println("Iterative (100 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test Morris approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            solution.kthSmallestMorris(largeRoot, k);
        }
        end = System.nanoTime();
        System.out.println("Morris (100 ops): " + (end - start) / 1000000.0 + " ms");
        
        // Test binary search approach
        start = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            solution.kthSmallestBinarySearch(largeRoot, k);
        }
        end = System.nanoTime();
        System.out.println("Binary Search (100 ops): " + (end - start) / 1000000.0 + " ms");
        System.out.println();
    }
    
    private static boolean verifyCorrectness(KthSmallestElementBST solution) {
        TreeNode testRoot = createBST(new int[]{1, 2, 3, 4, 5, 6, 7});
        
        for (int k = 1; k <= 7; k++) {
            int result1 = solution.kthSmallest(testRoot, k);
            int result2 = solution.kthSmallestOptimized(testRoot, k);
            int result3 = solution.kthSmallestIterative(testRoot, k);
            int result4 = solution.kthSmallestMorris(testRoot, k);
            int result5 = solution.kthSmallestBinarySearch(testRoot, k);
            
            if (!(result1 == result2 && result2 == result3 && 
                  result3 == result4 && result4 == result5 && result5 == k)) {
                System.out.println("Inconsistency found for k=" + k);
                return false;
            }
        }
        
        return true;
    }
} 