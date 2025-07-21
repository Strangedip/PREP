import java.util.*;

/**
 * Problem: Subtree of Another Tree
 * 
 * Given the roots of two binary trees root and subRoot, return true if there is a subtree 
 * of root with the same structure and node values of subRoot and false otherwise.
 * 
 * A subtree of a binary tree tree is a tree that consists of a node in tree and all of 
 * this node's descendants. The tree tree could also be considered as a subtree of itself.
 * 
 * Example 1:
 * Input: root = [3,4,5,1,2], subRoot = [4,1,2]
 * Output: true
 * 
 * Example 2:
 * Input: root = [3,4,5,1,2,null,null,null,null,0], subRoot = [4,1,2]
 * Output: false
 * 
 * Constraints:
 * - The number of nodes in the root tree is in the range [1, 2000].
 * - The number of nodes in the subRoot tree is in the range [1, 1000].
 * - -10^4 <= root.val <= 10^4
 * - -10^4 <= subRoot.val <= 10^4
 */
public class SubtreeOfAnotherTree {
    
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
     * APPROACH 1: RECURSIVE DFS - BRUTE FORCE
     * Time Complexity: O(m * n) where m = nodes in root, n = nodes in subRoot
     * Space Complexity: O(max(h1, h2)) where h1, h2 are heights of trees
     * 
     * For each node in root tree, check if subtree starting from that node 
     * is identical to subRoot.
     */
    public boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (root == null) {
            return false;
        }
        
        // Check if current node forms identical subtree with subRoot
        if (isSameTree(root, subRoot)) {
            return true;
        }
        
        // Recursively check left and right subtrees
        return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }
    
    /**
     * Helper method to check if two trees are identical
     */
    private boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }
        
        if (p == null || q == null) {
            return false;
        }
        
        return p.val == q.val && 
               isSameTree(p.left, q.left) && 
               isSameTree(p.right, q.right);
    }
    
    /**
     * APPROACH 2: STRING SERIALIZATION WITH KMP
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     * 
     * Serialize both trees to strings and use KMP algorithm to find substring.
     */
    public boolean isSubtreeKMP(TreeNode root, TreeNode subRoot) {
        String rootStr = serialize(root);
        String subStr = serialize(subRoot);
        
        return kmpSearch(rootStr, subStr);
    }
    
    /**
     * Serialize tree to string with special markers
     */
    private String serialize(TreeNode node) {
        if (node == null) {
            return "#";
        }
        
        return "^" + node.val + serialize(node.left) + serialize(node.right);
    }
    
    /**
     * KMP string matching algorithm
     */
    private boolean kmpSearch(String text, String pattern) {
        int[] lps = computeLPS(pattern);
        int i = 0, j = 0;
        
        while (i < text.length()) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            
            if (j == pattern.length()) {
                return true;
            } else if (i < text.length() && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Compute Longest Proper Prefix which is also Suffix array
     */
    private int[] computeLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;
        
        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    /**
     * APPROACH 3: HASH-BASED APPROACH
     * Time Complexity: O(m + n) average case
     * Space Complexity: O(m + n)
     * 
     * Use rolling hash to quickly compare subtrees.
     */
    public boolean isSubtreeHash(TreeNode root, TreeNode subRoot) {
        Set<String> rootSubtrees = new HashSet<>();
        String targetHash = getHash(subRoot);
        
        return dfsHash(root, rootSubtrees) && rootSubtrees.contains(targetHash);
    }
    
    private boolean dfsHash(TreeNode node, Set<String> subtrees) {
        if (node == null) {
            return false;
        }
        
        String hash = getHash(node);
        subtrees.add(hash);
        
        return dfsHash(node.left, subtrees) || dfsHash(node.right, subtrees);
    }
    
    private String getHash(TreeNode node) {
        if (node == null) {
            return "#";
        }
        
        return node.val + "," + getHash(node.left) + "," + getHash(node.right);
    }
    
    /**
     * APPROACH 4: MERKLE HASH APPROACH
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     * 
     * Use Merkle hashing for efficient subtree comparison.
     */
    private Map<TreeNode, Long> merkleHashes = new HashMap<>();
    private static final long MOD = 1000000007L;
    private static final long BASE = 31L;
    
    public boolean isSubtreeMerkle(TreeNode root, TreeNode subRoot) {
        merkleHashes.clear();
        long targetHash = merkleHash(subRoot);
        return findSubtreeMerkle(root, targetHash);
    }
    
    private boolean findSubtreeMerkle(TreeNode node, long targetHash) {
        if (node == null) {
            return false;
        }
        
        if (merkleHash(node) == targetHash) {
            return true;
        }
        
        return findSubtreeMerkle(node.left, targetHash) || 
               findSubtreeMerkle(node.right, targetHash);
    }
    
    private long merkleHash(TreeNode node) {
        if (node == null) {
            return 0;
        }
        
        if (merkleHashes.containsKey(node)) {
            return merkleHashes.get(node);
        }
        
        long leftHash = merkleHash(node.left);
        long rightHash = merkleHash(node.right);
        
        long hash = ((long)node.val + leftHash * BASE + rightHash * BASE * BASE) % MOD;
        merkleHashes.put(node, hash);
        
        return hash;
    }
    
    /**
     * APPROACH 5: ITERATIVE DFS
     * Time Complexity: O(m * n)
     * Space Complexity: O(h1 + h2)
     * 
     * Use stack for iterative traversal.
     */
    public boolean isSubtreeIterative(TreeNode root, TreeNode subRoot) {
        if (root == null) return false;
        
        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);
        
        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            
            if (isSameTreeIterative(node, subRoot)) {
                return true;
            }
            
            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }
        
        return false;
    }
    
    private boolean isSameTreeIterative(TreeNode p, TreeNode q) {
        Stack<TreeNode> stack1 = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();
        
        stack1.push(p);
        stack2.push(q);
        
        while (!stack1.isEmpty() && !stack2.isEmpty()) {
            TreeNode node1 = stack1.pop();
            TreeNode node2 = stack2.pop();
            
            if (node1 == null && node2 == null) {
                continue;
            }
            
            if (node1 == null || node2 == null || node1.val != node2.val) {
                return false;
            }
            
            stack1.push(node1.right);
            stack1.push(node1.left);
            stack2.push(node2.right);
            stack2.push(node2.left);
        }
        
        return stack1.isEmpty() && stack2.isEmpty();
    }
    
    /**
     * APPROACH 6: BFS LEVEL ORDER
     * Time Complexity: O(m * n)
     * Space Complexity: O(w) where w is maximum width
     * 
     * Use level order traversal to find potential subtree roots.
     */
    public boolean isSubtreeBFS(TreeNode root, TreeNode subRoot) {
        if (root == null) return false;
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            if (isSameTree(node, subRoot)) {
                return true;
            }
            
            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }
        
        return false;
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
     * Convert tree to array representation for printing
     */
    public static List<Integer> treeToArray(TreeNode root) {
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
     * Print tree structure visually
     */
    public static void printTree(TreeNode root, String name) {
        System.out.println(name + ": " + treeToArray(root));
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
        System.out.println();
    }
    
    // Test method
    public static void main(String[] args) {
        SubtreeOfAnotherTree solution = new SubtreeOfAnotherTree();
        
        System.out.println("=== Test Case 1: True case ===");
        TreeNode root1 = createTree(new Integer[]{3, 4, 5, 1, 2});
        TreeNode subRoot1 = createTree(new Integer[]{4, 1, 2});
        
        printTree(root1, "Root tree");
        printTree(subRoot1, "Subtree");
        
        System.out.println("Results:");
        System.out.println("Recursive: " + solution.isSubtree(root1, subRoot1));
        System.out.println("KMP: " + solution.isSubtreeKMP(root1, subRoot1));
        System.out.println("Hash: " + solution.isSubtreeHash(root1, subRoot1));
        System.out.println("Merkle: " + solution.isSubtreeMerkle(root1, subRoot1));
        System.out.println("Iterative: " + solution.isSubtreeIterative(root1, subRoot1));
        System.out.println("BFS: " + solution.isSubtreeBFS(root1, subRoot1));
        System.out.println();
        
        System.out.println("=== Test Case 2: False case ===");
        TreeNode root2 = createTree(new Integer[]{3, 4, 5, 1, 2, null, null, null, null, 0});
        TreeNode subRoot2 = createTree(new Integer[]{4, 1, 2});
        
        printTree(root2, "Root tree");
        printTree(subRoot2, "Subtree");
        
        System.out.println("Results:");
        System.out.println("Recursive: " + solution.isSubtree(root2, subRoot2));
        System.out.println("KMP: " + solution.isSubtreeKMP(root2, subRoot2));
        System.out.println("Hash: " + solution.isSubtreeHash(root2, subRoot2));
        System.out.println("Merkle: " + solution.isSubtreeMerkle(root2, subRoot2));
        System.out.println("Iterative: " + solution.isSubtreeIterative(root2, subRoot2));
        System.out.println("BFS: " + solution.isSubtreeBFS(root2, subRoot2));
        System.out.println();
        
        System.out.println("=== Test Case 3: Single node ===");
        TreeNode root3 = createTree(new Integer[]{1});
        TreeNode subRoot3 = createTree(new Integer[]{1});
        
        System.out.println("Single node match: " + solution.isSubtree(root3, subRoot3));
        System.out.println();
        
        System.out.println("=== Test Case 4: Identical trees ===");
        TreeNode root4 = createTree(new Integer[]{1, 2, 3});
        TreeNode subRoot4 = createTree(new Integer[]{1, 2, 3});
        
        System.out.println("Identical trees: " + solution.isSubtree(root4, subRoot4));
        System.out.println();
        
        System.out.println("=== Test Case 5: Edge case - null subtree ===");
        TreeNode root5 = createTree(new Integer[]{1, 2, 3});
        TreeNode subRoot5 = null;
        
        System.out.println("Null subtree: " + solution.isSubtree(root5, subRoot5));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
        
        // Correctness verification
        System.out.println("All approaches produce same result: " + verifyCorrectness(solution));
    }
    
    private static void performanceTest(SubtreeOfAnotherTree solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large test trees
        TreeNode largeRoot = createLargeTree(500, 1);
        TreeNode largeSubRoot = createLargeTree(50, 100);
        
        long start, end;
        
        // Test Recursive approach
        start = System.nanoTime();
        boolean result1 = solution.isSubtree(largeRoot, largeSubRoot);
        end = System.nanoTime();
        System.out.println("Recursive: " + result1 + " (" + (end - start) / 1000000.0 + " ms)");
        
        // Test KMP approach
        start = System.nanoTime();
        boolean result2 = solution.isSubtreeKMP(largeRoot, largeSubRoot);
        end = System.nanoTime();
        System.out.println("KMP: " + result2 + " (" + (end - start) / 1000000.0 + " ms)");
        
        // Test Hash approach
        start = System.nanoTime();
        boolean result3 = solution.isSubtreeHash(largeRoot, largeSubRoot);
        end = System.nanoTime();
        System.out.println("Hash: " + result3 + " (" + (end - start) / 1000000.0 + " ms)");
        
        // Test Merkle approach
        start = System.nanoTime();
        boolean result4 = solution.isSubtreeMerkle(largeRoot, largeSubRoot);
        end = System.nanoTime();
        System.out.println("Merkle: " + result4 + " (" + (end - start) / 1000000.0 + " ms)");
        
        System.out.println("All performance results match: " + 
                          (result1 == result2 && result2 == result3 && result3 == result4));
    }
    
    private static TreeNode createLargeTree(int numNodes, int startValue) {
        if (numNodes <= 0) return null;
        
        TreeNode root = new TreeNode(startValue);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        int nodeValue = startValue + 1;
        int created = 1;
        
        while (!queue.isEmpty() && created < numNodes) {
            TreeNode node = queue.poll();
            
            if (created < numNodes) {
                node.left = new TreeNode(nodeValue++);
                queue.offer(node.left);
                created++;
            }
            
            if (created < numNodes) {
                node.right = new TreeNode(nodeValue++);
                queue.offer(node.right);
                created++;
            }
        }
        
        return root;
    }
    
    /**
     * Verify that all approaches produce the same result
     */
    public static boolean verifyCorrectness(SubtreeOfAnotherTree solution) {
        TreeNode[] testRoots = {
            createTree(new Integer[]{3, 4, 5, 1, 2}),
            createTree(new Integer[]{3, 4, 5, 1, 2, null, null, null, null, 0}),
            createTree(new Integer[]{1}),
            createTree(new Integer[]{1, 2, 3})
        };
        
        TreeNode[] testSubs = {
            createTree(new Integer[]{4, 1, 2}),
            createTree(new Integer[]{4, 1, 2}),
            createTree(new Integer[]{1}),
            createTree(new Integer[]{1, 2, 3})
        };
        
        for (int i = 0; i < testRoots.length; i++) {
            boolean result1 = solution.isSubtree(testRoots[i], testSubs[i]);
            boolean result2 = solution.isSubtreeKMP(testRoots[i], testSubs[i]);
            boolean result3 = solution.isSubtreeHash(testRoots[i], testSubs[i]);
            boolean result4 = solution.isSubtreeMerkle(testRoots[i], testSubs[i]);
            boolean result5 = solution.isSubtreeIterative(testRoots[i], testSubs[i]);
            boolean result6 = solution.isSubtreeBFS(testRoots[i], testSubs[i]);
            
            if (!(result1 == result2 && result2 == result3 && result3 == result4 && 
                  result4 == result5 && result5 == result6)) {
                System.out.println("Test case " + i + " failed!");
                return false;
            }
        }
        
        return true;
    }
} 