import java.util.*;

/**
 * Problem: Binary Tree Level Order Traversal
 * 
 * Given the root of a binary tree, return the level order traversal of its nodes' values. 
 * (i.e., from left to right, level by level).
 * 
 * Example:
 * Input: root = [3,9,20,null,null,15,7]
 * Output: [[3],[9,20],[15,7]]
 * 
 * Example 2:
 * Input: root = [1]
 * Output: [[1]]
 * 
 * Example 3:
 * Input: root = []
 * Output: []
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 2000].
 * - -1000 <= Node.val <= 1000
 */
public class BinaryTreeLevelOrderTraversal {
    
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
     * APPROACH 1: BFS WITH QUEUE (Standard)
     * Time Complexity: O(n)
     * Space Complexity: O(w) where w is maximum width of tree
     * 
     * Use queue to traverse level by level.
     */
    public List<List<Integer>> levelOrderBFS(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            
            result.add(currentLevel);
        }
        
        return result;
    }
    
    /**
     * APPROACH 2: DFS WITH LEVEL TRACKING
     * Time Complexity: O(n)
     * Space Complexity: O(h) where h is height of tree
     * 
     * Use recursive DFS with level parameter.
     */
    public List<List<Integer>> levelOrderDFS(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        dfsHelper(root, 0, result);
        return result;
    }
    
    private void dfsHelper(TreeNode node, int level, List<List<Integer>> result) {
        if (node == null) {
            return;
        }
        
        // Create new level if needed
        if (level >= result.size()) {
            result.add(new ArrayList<>());
        }
        
        // Add current node to its level
        result.get(level).add(node.val);
        
        // Recursively process children
        dfsHelper(node.left, level + 1, result);
        dfsHelper(node.right, level + 1, result);
    }
    
    /**
     * APPROACH 3: BFS WITH TWO QUEUES
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Use two queues to separate current and next levels.
     */
    public List<List<Integer>> levelOrderTwoQueues(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> currentLevel = new LinkedList<>();
        Queue<TreeNode> nextLevel = new LinkedList<>();
        
        currentLevel.offer(root);
        
        while (!currentLevel.isEmpty()) {
            List<Integer> levelValues = new ArrayList<>();
            
            while (!currentLevel.isEmpty()) {
                TreeNode node = currentLevel.poll();
                levelValues.add(node.val);
                
                if (node.left != null) {
                    nextLevel.offer(node.left);
                }
                if (node.right != null) {
                    nextLevel.offer(node.right);
                }
            }
            
            result.add(levelValues);
            
            // Swap queues
            Queue<TreeNode> temp = currentLevel;
            currentLevel = nextLevel;
            nextLevel = temp;
        }
        
        return result;
    }
    
    /**
     * APPROACH 4: BFS WITH DELIMITER
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Use null as delimiter between levels.
     */
    public List<List<Integer>> levelOrderDelimiter(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        queue.offer(null); // Level delimiter
        
        List<Integer> currentLevel = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            
            if (node == null) {
                // End of current level
                result.add(new ArrayList<>(currentLevel));
                currentLevel.clear();
                
                // Add delimiter for next level if queue is not empty
                if (!queue.isEmpty()) {
                    queue.offer(null);
                }
            } else {
                currentLevel.add(node.val);
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 5: USING ARRAYLIST OF QUEUES
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Maintain separate queue for each level.
     */
    public List<List<Integer>> levelOrderMultipleQueues(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        List<Queue<TreeNode>> levels = new ArrayList<>();
        Queue<TreeNode> firstLevel = new LinkedList<>();
        firstLevel.offer(root);
        levels.add(firstLevel);
        
        int currentLevelIndex = 0;
        
        while (currentLevelIndex < levels.size()) {
            Queue<TreeNode> currentLevel = levels.get(currentLevelIndex);
            List<Integer> levelValues = new ArrayList<>();
            Queue<TreeNode> nextLevel = new LinkedList<>();
            
            while (!currentLevel.isEmpty()) {
                TreeNode node = currentLevel.poll();
                levelValues.add(node.val);
                
                if (node.left != null) {
                    nextLevel.offer(node.left);
                }
                if (node.right != null) {
                    nextLevel.offer(node.right);
                }
            }
            
            result.add(levelValues);
            
            if (!nextLevel.isEmpty()) {
                levels.add(nextLevel);
            }
            
            currentLevelIndex++;
        }
        
        return result;
    }
    
    /**
     * APPROACH 6: ITERATIVE DFS WITH STACK
     * Time Complexity: O(n)
     * Space Complexity: O(h)
     * 
     * Use stack to simulate DFS traversal.
     */
    public List<List<Integer>> levelOrderStackDFS(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<Integer> levelStack = new Stack<>();
        
        nodeStack.push(root);
        levelStack.push(0);
        
        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            int level = levelStack.pop();
            
            // Create new level if needed
            while (level >= result.size()) {
                result.add(new ArrayList<>());
            }
            
            result.get(level).add(node.val);
            
            // Push children (right first for left-to-right order)
            if (node.right != null) {
                nodeStack.push(node.right);
                levelStack.push(level + 1);
            }
            if (node.left != null) {
                nodeStack.push(node.left);
                levelStack.push(level + 1);
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 7: USING DEQUE
     * Time Complexity: O(n)
     * Space Complexity: O(w)
     * 
     * Use Deque for potentially better performance.
     */
    public List<List<Integer>> levelOrderDeque(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Deque<TreeNode> deque = new ArrayDeque<>();
        deque.offer(root);
        
        while (!deque.isEmpty()) {
            int levelSize = deque.size();
            List<Integer> currentLevel = new ArrayList<>();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = deque.poll();
                currentLevel.add(node.val);
                
                if (node.left != null) {
                    deque.offer(node.left);
                }
                if (node.right != null) {
                    deque.offer(node.right);
                }
            }
            
            result.add(currentLevel);
        }
        
        return result;
    }
    
    /**
     * BONUS: REVERSE LEVEL ORDER TRAVERSAL
     * Returns levels from bottom to top.
     */
    public List<List<Integer>> levelOrderBottom(TreeNode root) {
        List<List<Integer>> result = levelOrderBFS(root);
        Collections.reverse(result);
        return result;
    }
    
    /**
     * BONUS: RIGHT SIDE VIEW
     * Returns the rightmost node at each level.
     */
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                
                // Add rightmost node of each level
                if (i == levelSize - 1) {
                    result.add(node.val);
                }
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }
        
        return result;
    }
    
    /**
     * BONUS: ZIGZAG LEVEL ORDER TRAVERSAL
     * Alternate direction for each level.
     */
    public List<List<Integer>> zigzagLevelOrder(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }
        
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        boolean leftToRight = true;
        
        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            List<Integer> currentLevel = new ArrayList<>();
            
            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();
                currentLevel.add(node.val);
                
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
            
            if (!leftToRight) {
                Collections.reverse(currentLevel);
            }
            
            result.add(currentLevel);
            leftToRight = !leftToRight;
        }
        
        return result;
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
        BinaryTreeLevelOrderTraversal solution = new BinaryTreeLevelOrderTraversal();
        
        // Test case 1: Standard example
        System.out.println("Test Case 1: [3,9,20,null,null,15,7]");
        TreeNode root1 = createTree(new Integer[]{3, 9, 20, null, null, 15, 7});
        System.out.print("Tree: ");
        printTree(root1);
        
        System.out.println("BFS: " + solution.levelOrderBFS(root1));
        System.out.println("DFS: " + solution.levelOrderDFS(root1));
        System.out.println("Two Queues: " + solution.levelOrderTwoQueues(root1));
        System.out.println("Delimiter: " + solution.levelOrderDelimiter(root1));
        System.out.println("Stack DFS: " + solution.levelOrderStackDFS(root1));
        System.out.println("Deque: " + solution.levelOrderDeque(root1));
        System.out.println();
        
        // Test case 2: Single node
        System.out.println("Test Case 2: [1]");
        TreeNode root2 = createTree(new Integer[]{1});
        System.out.println("Result: " + solution.levelOrderBFS(root2));
        System.out.println();
        
        // Test case 3: Empty tree
        System.out.println("Test Case 3: []");
        TreeNode root3 = null;
        System.out.println("Result: " + solution.levelOrderBFS(root3));
        System.out.println();
        
        // Test case 4: Complete binary tree
        System.out.println("Test Case 4: [1,2,3,4,5,6,7]");
        TreeNode root4 = createTree(new Integer[]{1, 2, 3, 4, 5, 6, 7});
        System.out.println("Result: " + solution.levelOrderBFS(root4));
        System.out.println();
        
        // Test case 5: Skewed tree
        System.out.println("Test Case 5: [1,2,null,3,null,4]");
        TreeNode root5 = createTree(new Integer[]{1, 2, null, 3, null, 4});
        System.out.println("Result: " + solution.levelOrderBFS(root5));
        System.out.println();
        
        // Bonus test cases
        System.out.println("Bonus: Reverse Level Order");
        System.out.println("Result: " + solution.levelOrderBottom(root1));
        
        System.out.println("Bonus: Right Side View");
        System.out.println("Result: " + solution.rightSideView(root1));
        
        System.out.println("Bonus: Zigzag Level Order");
        System.out.println("Result: " + solution.zigzagLevelOrder(root1));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(BinaryTreeLevelOrderTraversal solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large complete binary tree
        TreeNode largeTree = createLargeTree(1000); // 1000 nodes
        
        long start, end;
        
        // Test BFS approach (standard)
        start = System.nanoTime();
        List<List<Integer>> result1 = solution.levelOrderBFS(largeTree);
        end = System.nanoTime();
        System.out.println("BFS: " + result1.size() + " levels (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test DFS approach
        start = System.nanoTime();
        List<List<Integer>> result2 = solution.levelOrderDFS(largeTree);
        end = System.nanoTime();
        System.out.println("DFS: " + result2.size() + " levels (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Deque approach
        start = System.nanoTime();
        List<List<Integer>> result3 = solution.levelOrderDeque(largeTree);
        end = System.nanoTime();
        System.out.println("Deque: " + result3.size() + " levels (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Two Queues approach
        start = System.nanoTime();
        List<List<Integer>> result4 = solution.levelOrderTwoQueues(largeTree);
        end = System.nanoTime();
        System.out.println("Two Queues: " + result4.size() + " levels (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Stack DFS approach
        start = System.nanoTime();
        List<List<Integer>> result5 = solution.levelOrderStackDFS(largeTree);
        end = System.nanoTime();
        System.out.println("Stack DFS: " + result5.size() + " levels (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Verify all approaches give same number of levels
        System.out.println("All approaches give same result: " + 
                          (result1.size() == result2.size() && result2.size() == result3.size() && 
                           result3.size() == result4.size() && result4.size() == result5.size()));
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
     * Method to verify correctness with various test cases
     */
    public static boolean verifyCorrectness(BinaryTreeLevelOrderTraversal solution) {
        Integer[][] testCases = {
            {3, 9, 20, null, null, 15, 7},  // [[3],[9,20],[15,7]]
            {1},                            // [[1]]
            {},                             // []
            {1, 2, 3, 4, 5, 6, 7},         // [[1],[2,3],[4,5,6,7]]
            {1, 2, null, 3, null, 4}       // [[1],[2],[3],[4]]
        };
        
        List<List<List<Integer>>> expected = Arrays.asList(
            Arrays.asList(Arrays.asList(3), Arrays.asList(9, 20), Arrays.asList(15, 7)),
            Arrays.asList(Arrays.asList(1)),
            new ArrayList<>(),
            Arrays.asList(Arrays.asList(1), Arrays.asList(2, 3), Arrays.asList(4, 5, 6, 7)),
            Arrays.asList(Arrays.asList(1), Arrays.asList(2), Arrays.asList(3), Arrays.asList(4))
        );
        
        for (int i = 0; i < testCases.length; i++) {
            TreeNode root = testCases[i].length == 0 ? null : createTree(testCases[i]);
            
            List<List<Integer>> result1 = solution.levelOrderBFS(root);
            List<List<Integer>> result2 = solution.levelOrderDFS(root);
            List<List<Integer>> result3 = solution.levelOrderDeque(root);
            
            if (!result1.equals(expected.get(i)) || !result2.equals(expected.get(i)) || 
                !result3.equals(expected.get(i))) {
                System.out.println("Test case " + i + " failed.");
                System.out.println("Expected: " + expected.get(i));
                System.out.println("Got BFS: " + result1);
                System.out.println("Got DFS: " + result2);
                return false;
            }
        }
        
        return true;
    }
} 