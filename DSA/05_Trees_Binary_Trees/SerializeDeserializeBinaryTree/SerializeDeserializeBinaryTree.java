import java.util.*;

/**
 * Problem: Serialize and Deserialize Binary Tree
 * 
 * Serialization is the process of converting a data structure or object into a sequence 
 * of bits so that it can be stored in a file or memory buffer, or transmitted across a 
 * network connection link to be reconstructed later in the same or another computer environment.
 * 
 * Design an algorithm to serialize and deserialize a binary tree. There is no restriction 
 * on how your serialization/deserialization algorithm should work. You just need to ensure 
 * that a binary tree can be serialized to a string and this string can be deserialized to 
 * the original tree structure.
 * 
 * Example:
 * Input: root = [1,2,3,null,null,4,5]
 * Output: [1,2,3,null,null,4,5]
 * 
 * Clarification: The above format is the same as how LeetCode serializes a binary tree. 
 * You do not necessarily need to follow this format, so please be creative and come up 
 * with different approaches yourself.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -1000 <= Node.val <= 1000
 */
public class SerializeDeserializeBinaryTree {
    
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
     * APPROACH 1: PREORDER TRAVERSAL (DFS)
     * Most commonly used approach in interviews.
     */
    public static class CodecPreorder {
        private static final String NULL_MARKER = "#";
        private static final String DELIMITER = ",";
        
        // Encodes a tree to a single string.
        public String serialize(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            serializeHelper(root, sb);
            return sb.toString();
        }
        
        private void serializeHelper(TreeNode node, StringBuilder sb) {
            if (node == null) {
                sb.append(NULL_MARKER).append(DELIMITER);
                return;
            }
            
            sb.append(node.val).append(DELIMITER);
            serializeHelper(node.left, sb);
            serializeHelper(node.right, sb);
        }
        
        // Decodes your encoded data to tree.
        public TreeNode deserialize(String data) {
            Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(DELIMITER)));
            return deserializeHelper(queue);
        }
        
        private TreeNode deserializeHelper(Queue<String> queue) {
            String val = queue.poll();
            
            if (NULL_MARKER.equals(val)) {
                return null;
            }
            
            TreeNode node = new TreeNode(Integer.parseInt(val));
            node.left = deserializeHelper(queue);
            node.right = deserializeHelper(queue);
            
            return node;
        }
    }
    
    /**
     * APPROACH 2: LEVEL ORDER TRAVERSAL (BFS)
     * Similar to LeetCode's default serialization format.
     */
    public static class CodecLevelOrder {
        private static final String NULL_MARKER = "null";
        private static final String DELIMITER = ",";
        
        public String serialize(TreeNode root) {
            if (root == null) {
                return "";
            }
            
            StringBuilder sb = new StringBuilder();
            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);
            
            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();
                
                if (node == null) {
                    sb.append(NULL_MARKER).append(DELIMITER);
                } else {
                    sb.append(node.val).append(DELIMITER);
                    queue.offer(node.left);
                    queue.offer(node.right);
                }
            }
            
            return sb.toString();
        }
        
        public TreeNode deserialize(String data) {
            if (data.isEmpty()) {
                return null;
            }
            
            String[] values = data.split(DELIMITER);
            TreeNode root = new TreeNode(Integer.parseInt(values[0]));
            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);
            
            int i = 1;
            while (!queue.isEmpty() && i < values.length) {
                TreeNode node = queue.poll();
                
                // Process left child
                if (!NULL_MARKER.equals(values[i])) {
                    node.left = new TreeNode(Integer.parseInt(values[i]));
                    queue.offer(node.left);
                }
                i++;
                
                // Process right child
                if (i < values.length && !NULL_MARKER.equals(values[i])) {
                    node.right = new TreeNode(Integer.parseInt(values[i]));
                    queue.offer(node.right);
                }
                i++;
            }
            
            return root;
        }
    }
    
    /**
     * APPROACH 3: POSTORDER TRAVERSAL
     * Serialize in postorder, deserialize by building from right to left.
     */
    public static class CodecPostorder {
        private static final String NULL_MARKER = "#";
        private static final String DELIMITER = ",";
        
        public String serialize(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            serializeHelper(root, sb);
            return sb.toString();
        }
        
        private void serializeHelper(TreeNode node, StringBuilder sb) {
            if (node == null) {
                sb.append(NULL_MARKER).append(DELIMITER);
                return;
            }
            
            serializeHelper(node.left, sb);
            serializeHelper(node.right, sb);
            sb.append(node.val).append(DELIMITER);
        }
        
        public TreeNode deserialize(String data) {
            List<String> list = new ArrayList<>(Arrays.asList(data.split(DELIMITER)));
            return deserializeHelper(list);
        }
        
        private TreeNode deserializeHelper(List<String> list) {
            if (list.isEmpty()) {
                return null;
            }
            
            String val = list.remove(list.size() - 1);
            
            if (NULL_MARKER.equals(val)) {
                return null;
            }
            
            TreeNode node = new TreeNode(Integer.parseInt(val));
            node.right = deserializeHelper(list);  // Note: right first for postorder
            node.left = deserializeHelper(list);
            
            return node;
        }
    }
    
    /**
     * APPROACH 4: COMPACT PREORDER (No delimiters)
     * Use length encoding to avoid delimiters.
     */
    public static class CodecCompact {
        public String serialize(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            serializeHelper(root, sb);
            return sb.toString();
        }
        
        private void serializeHelper(TreeNode node, StringBuilder sb) {
            if (node == null) {
                sb.append("X");
                return;
            }
            
            String val = String.valueOf(node.val);
            sb.append(val.length()).append(val);
            serializeHelper(node.left, sb);
            serializeHelper(node.right, sb);
        }
        
        public TreeNode deserialize(String data) {
            int[] index = {0};
            return deserializeHelper(data, index);
        }
        
        private TreeNode deserializeHelper(String data, int[] index) {
            if (index[0] >= data.length()) {
                return null;
            }
            
            char c = data.charAt(index[0]++);
            
            if (c == 'X') {
                return null;
            }
            
            int len = c - '0';
            int val = Integer.parseInt(data.substring(index[0], index[0] + len));
            index[0] += len;
            
            TreeNode node = new TreeNode(val);
            node.left = deserializeHelper(data, index);
            node.right = deserializeHelper(data, index);
            
            return node;
        }
    }
    
    /**
     * APPROACH 5: BRACKET REPRESENTATION
     * Represent tree using nested brackets.
     */
    public static class CodecBracket {
        public String serialize(TreeNode root) {
            if (root == null) {
                return "";
            }
            
            StringBuilder sb = new StringBuilder();
            serializeHelper(root, sb);
            return sb.toString();
        }
        
        private void serializeHelper(TreeNode node, StringBuilder sb) {
            if (node == null) {
                return;
            }
            
            sb.append(node.val);
            
            if (node.left != null || node.right != null) {
                sb.append("(");
                serializeHelper(node.left, sb);
                sb.append(")(");
                serializeHelper(node.right, sb);
                sb.append(")");
            }
        }
        
        public TreeNode deserialize(String data) {
            if (data.isEmpty()) {
                return null;
            }
            
            int[] index = {0};
            return deserializeHelper(data, index);
        }
        
        private TreeNode deserializeHelper(String data, int[] index) {
            if (index[0] >= data.length()) {
                return null;
            }
            
            // Parse the number
            int start = index[0];
            if (data.charAt(index[0]) == '-') {
                index[0]++;
            }
            
            while (index[0] < data.length() && Character.isDigit(data.charAt(index[0]))) {
                index[0]++;
            }
            
            int val = Integer.parseInt(data.substring(start, index[0]));
            TreeNode node = new TreeNode(val);
            
            if (index[0] < data.length() && data.charAt(index[0]) == '(') {
                index[0]++; // skip '('
                node.left = deserializeHelper(data, index);
                index[0]++; // skip ')('
                node.right = deserializeHelper(data, index);
                index[0]++; // skip ')'
            }
            
            return node;
        }
    }
    
    /**
     * APPROACH 6: ITERATIVE PREORDER
     * Non-recursive implementation using stack.
     */
    public static class CodecIterative {
        private static final String NULL_MARKER = "#";
        private static final String DELIMITER = ",";
        
        public String serialize(TreeNode root) {
            if (root == null) {
                return NULL_MARKER;
            }
            
            StringBuilder sb = new StringBuilder();
            Stack<TreeNode> stack = new Stack<>();
            stack.push(root);
            
            while (!stack.isEmpty()) {
                TreeNode node = stack.pop();
                
                if (node == null) {
                    sb.append(NULL_MARKER).append(DELIMITER);
                } else {
                    sb.append(node.val).append(DELIMITER);
                    stack.push(node.right);  // Push right first
                    stack.push(node.left);
                }
            }
            
            return sb.toString();
        }
        
        public TreeNode deserialize(String data) {
            if (NULL_MARKER.equals(data)) {
                return null;
            }
            
            String[] values = data.split(DELIMITER);
            TreeNode root = new TreeNode(Integer.parseInt(values[0]));
            Stack<TreeNode> stack = new Stack<>();
            stack.push(root);
            
            int i = 1;
            while (!stack.isEmpty() && i < values.length) {
                TreeNode node = stack.pop();
                
                // Process left child
                if (!NULL_MARKER.equals(values[i])) {
                    node.left = new TreeNode(Integer.parseInt(values[i]));
                    stack.push(node.left);
                }
                i++;
                
                // Process right child
                if (i < values.length && !NULL_MARKER.equals(values[i])) {
                    node.right = new TreeNode(Integer.parseInt(values[i]));
                    stack.push(node.right);
                }
                i++;
            }
            
            return root;
        }
    }
    
    // Helper methods for testing
    
    /**
     * Create tree from array representation
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
     * Convert tree to array for comparison
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
     * Check if two trees are identical
     */
    public static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) return true;
        if (p == null || q == null) return false;
        return p.val == q.val && isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
    }
    
    /**
     * Print tree structure
     */
    public static void printTree(TreeNode root, String name) {
        System.out.println(name + ": " + treeToArray(root));
    }
    
    // Test method
    public static void main(String[] args) {
        // Test trees
        TreeNode tree1 = createTree(new Integer[]{1, 2, 3, null, null, 4, 5});
        TreeNode tree2 = createTree(new Integer[]{1});
        TreeNode tree3 = null;
        TreeNode tree4 = createTree(new Integer[]{1, 2, 3, 4, 5, 6, 7});
        
        TreeNode[] testTrees = {tree1, tree2, tree3, tree4};
        String[] treeNames = {"Complex tree", "Single node", "Empty tree", "Complete tree"};
        
        // Test all codecs
        Object[] codecs = {
            new CodecPreorder(),
            new CodecLevelOrder(),
            new CodecPostorder(),
            new CodecCompact(),
            new CodecBracket(),
            new CodecIterative()
        };
        
        String[] codecNames = {
            "Preorder", "Level Order", "Postorder", 
            "Compact", "Bracket", "Iterative"
        };
        
        for (int i = 0; i < testTrees.length; i++) {
            System.out.println("=== Testing " + treeNames[i] + " ===");
            printTree(testTrees[i], "Original");
            
            for (int j = 0; j < codecs.length; j++) {
                try {
                    String serialized = "";
                    TreeNode deserialized = null;
                    
                    if (codecs[j] instanceof CodecPreorder) {
                        CodecPreorder codec = (CodecPreorder) codecs[j];
                        serialized = codec.serialize(testTrees[i]);
                        deserialized = codec.deserialize(serialized);
                    } else if (codecs[j] instanceof CodecLevelOrder) {
                        CodecLevelOrder codec = (CodecLevelOrder) codecs[j];
                        serialized = codec.serialize(testTrees[i]);
                        deserialized = codec.deserialize(serialized);
                    } else if (codecs[j] instanceof CodecPostorder) {
                        CodecPostorder codec = (CodecPostorder) codecs[j];
                        serialized = codec.serialize(testTrees[i]);
                        deserialized = codec.deserialize(serialized);
                    } else if (codecs[j] instanceof CodecCompact) {
                        CodecCompact codec = (CodecCompact) codecs[j];
                        serialized = codec.serialize(testTrees[i]);
                        deserialized = codec.deserialize(serialized);
                    } else if (codecs[j] instanceof CodecBracket) {
                        CodecBracket codec = (CodecBracket) codecs[j];
                        serialized = codec.serialize(testTrees[i]);
                        deserialized = codec.deserialize(serialized);
                    } else if (codecs[j] instanceof CodecIterative) {
                        CodecIterative codec = (CodecIterative) codecs[j];
                        serialized = codec.serialize(testTrees[i]);
                        deserialized = codec.deserialize(serialized);
                    }
                    
                    boolean isCorrect = isSameTree(testTrees[i], deserialized);
                    System.out.println(codecNames[j] + ": \"" + serialized + "\" -> " + 
                                     (isCorrect ? "✓" : "✗"));
                    
                } catch (Exception e) {
                    System.out.println(codecNames[j] + ": Error - " + e.getMessage());
                }
            }
            System.out.println();
        }
        
        // Performance test
        performanceTest();
        
        // Edge cases test
        edgeCasesTest();
    }
    
    private static void performanceTest() {
        System.out.println("=== Performance Test ===");
        
        // Create large tree
        TreeNode largeTree = createLargeTree(1000);
        
        CodecPreorder codecPreorder = new CodecPreorder();
        CodecLevelOrder codecLevelOrder = new CodecLevelOrder();
        
        long start, end;
        
        // Test Preorder codec
        start = System.nanoTime();
        String serialized1 = codecPreorder.serialize(largeTree);
        TreeNode deserialized1 = codecPreorder.deserialize(serialized1);
        end = System.nanoTime();
        System.out.println("Preorder: " + (end - start) / 1000000.0 + " ms, Length: " + serialized1.length());
        
        // Test Level Order codec
        start = System.nanoTime();
        String serialized2 = codecLevelOrder.serialize(largeTree);
        TreeNode deserialized2 = codecLevelOrder.deserialize(serialized2);
        end = System.nanoTime();
        System.out.println("Level Order: " + (end - start) / 1000000.0 + " ms, Length: " + serialized2.length());
        
        System.out.println("Results match: " + isSameTree(deserialized1, deserialized2));
        System.out.println();
    }
    
    private static TreeNode createLargeTree(int numNodes) {
        if (numNodes <= 0) return null;
        
        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        int nodeValue = 2;
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
    
    private static void edgeCasesTest() {
        System.out.println("=== Edge Cases Test ===");
        
        CodecPreorder codec = new CodecPreorder();
        
        // Test negative numbers
        TreeNode negativeTree = new TreeNode(-1);
        negativeTree.left = new TreeNode(-2);
        negativeTree.right = new TreeNode(-3);
        
        String serialized = codec.serialize(negativeTree);
        TreeNode deserialized = codec.deserialize(serialized);
        System.out.println("Negative numbers: " + isSameTree(negativeTree, deserialized));
        
        // Test large numbers
        TreeNode largeNumTree = new TreeNode(1000);
        largeNumTree.left = new TreeNode(-1000);
        
        serialized = codec.serialize(largeNumTree);
        deserialized = codec.deserialize(serialized);
        System.out.println("Large numbers: " + isSameTree(largeNumTree, deserialized));
        
        // Test skewed tree
        TreeNode skewedTree = new TreeNode(1);
        TreeNode current = skewedTree;
        for (int i = 2; i <= 10; i++) {
            current.right = new TreeNode(i);
            current = current.right;
        }
        
        serialized = codec.serialize(skewedTree);
        deserialized = codec.deserialize(serialized);
        System.out.println("Skewed tree: " + isSameTree(skewedTree, deserialized));
        
        System.out.println();
    }
} 