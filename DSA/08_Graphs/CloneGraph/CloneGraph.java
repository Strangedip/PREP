import java.util.*;

/**
 * LeetCode 133: Clone Graph
 * 
 * Given a reference of a node in a connected undirected graph, return a deep copy (clone) of the graph.
 * Each node in the graph contains a value (int) and a list (List[Node]) of its neighbors.
 * 
 * Time Complexity: O(V + E) where V = vertices, E = edges
 * Space Complexity: O(V) for the hash map and recursion stack
 */
public class CloneGraph {
    
    static class Node {
        public int val;
        public List<Node> neighbors;
        
        public Node() {
            val = 0;
            neighbors = new ArrayList<Node>();
        }
        
        public Node(int _val) {
            val = _val;
            neighbors = new ArrayList<Node>();
        }
        
        public Node(int _val, ArrayList<Node> _neighbors) {
            val = _val;
            neighbors = _neighbors;
        }
    }
    
    /**
     * Approach 1: Depth-First Search (DFS) with HashMap
     * Use a map to track visited nodes and their clones
     */
    public Node cloneGraphDFS(Node node) {
        if (node == null) {
            return null;
        }
        
        Map<Node, Node> visited = new HashMap<>();
        return dfsClone(node, visited);
    }
    
    private Node dfsClone(Node node, Map<Node, Node> visited) {
        // If already cloned, return the clone
        if (visited.containsKey(node)) {
            return visited.get(node);
        }
        
        // Create a clone of the current node
        Node clone = new Node(node.val);
        visited.put(node, clone);
        
        // Clone all neighbors recursively
        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(dfsClone(neighbor, visited));
        }
        
        return clone;
    }
    
    /**
     * Approach 2: Breadth-First Search (BFS) with HashMap
     * Use a queue to traverse nodes level by level
     */
    public Node cloneGraphBFS(Node node) {
        if (node == null) {
            return null;
        }
        
        Map<Node, Node> visited = new HashMap<>();
        Queue<Node> queue = new LinkedList<>();
        
        // Create clone of starting node
        Node clone = new Node(node.val);
        visited.put(node, clone);
        queue.offer(node);
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            
            // Process all neighbors
            for (Node neighbor : current.neighbors) {
                if (!visited.containsKey(neighbor)) {
                    // First time seeing this neighbor - clone it
                    visited.put(neighbor, new Node(neighbor.val));
                    queue.offer(neighbor);
                }
                
                // Add the cloned neighbor to current clone's neighbors
                visited.get(current).neighbors.add(visited.get(neighbor));
            }
        }
        
        return clone;
    }
    
    /**
     * Approach 3: Iterative DFS using Stack
     * Similar to BFS but uses stack instead of queue
     */
    public Node cloneGraphIterativeDFS(Node node) {
        if (node == null) {
            return null;
        }
        
        Map<Node, Node> visited = new HashMap<>();
        Stack<Node> stack = new Stack<>();
        
        // Create clone of starting node
        Node clone = new Node(node.val);
        visited.put(node, clone);
        stack.push(node);
        
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            
            // Process all neighbors
            for (Node neighbor : current.neighbors) {
                if (!visited.containsKey(neighbor)) {
                    // First time seeing this neighbor - clone it
                    visited.put(neighbor, new Node(neighbor.val));
                    stack.push(neighbor);
                }
                
                // Add the cloned neighbor to current clone's neighbors
                visited.get(current).neighbors.add(visited.get(neighbor));
            }
        }
        
        return clone;
    }
    
    // Helper method to create a test graph
    public static Node createTestGraph() {
        /*
         * Create graph: 1 -- 2
         *               |    |
         *               4 -- 3
         */
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);
        
        node1.neighbors.add(node2);
        node1.neighbors.add(node4);
        
        node2.neighbors.add(node1);
        node2.neighbors.add(node3);
        
        node3.neighbors.add(node2);
        node3.neighbors.add(node4);
        
        node4.neighbors.add(node1);
        node4.neighbors.add(node3);
        
        return node1;
    }
    
    // Helper method to print graph (BFS traversal)
    public static void printGraph(Node node, String title) {
        if (node == null) {
            System.out.println(title + ": null");
            return;
        }
        
        System.out.println(title + ":");
        Set<Node> visited = new HashSet<>();
        Queue<Node> queue = new LinkedList<>();
        queue.offer(node);
        visited.add(node);
        
        while (!queue.isEmpty()) {
            Node current = queue.poll();
            List<Integer> neighborVals = new ArrayList<>();
            
            for (Node neighbor : current.neighbors) {
                neighborVals.add(neighbor.val);
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.offer(neighbor);
                }
            }
            
            System.out.println("Node " + current.val + " -> neighbors: " + neighborVals);
        }
        System.out.println();
    }
    
    // Helper method to verify clone is correct
    public static boolean verifyClone(Node original, Node clone) {
        if (original == null && clone == null) return true;
        if (original == null || clone == null) return false;
        
        Map<Node, Node> visited = new HashMap<>();
        return verifyCloneHelper(original, clone, visited);
    }
    
    private static boolean verifyCloneHelper(Node original, Node clone, Map<Node, Node> visited) {
        if (visited.containsKey(original)) {
            return visited.get(original) == clone;
        }
        
        if (original.val != clone.val || original.neighbors.size() != clone.neighbors.size()) {
            return false;
        }
        
        // Ensure clone is not the same object as original
        if (original == clone) return false;
        
        visited.put(original, clone);
        
        for (int i = 0; i < original.neighbors.size(); i++) {
            if (!verifyCloneHelper(original.neighbors.get(i), clone.neighbors.get(i), visited)) {
                return false;
            }
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        CloneGraph solution = new CloneGraph();
        
        // Create test graph
        Node original = createTestGraph();
        printGraph(original, "Original Graph");
        
        // Test DFS approach
        Node cloneDFS = solution.cloneGraphDFS(original);
        printGraph(cloneDFS, "DFS Clone");
        System.out.println("DFS Clone verification: " + verifyClone(original, cloneDFS));
        System.out.println();
        
        // Test BFS approach
        Node cloneBFS = solution.cloneGraphBFS(original);
        printGraph(cloneBFS, "BFS Clone");
        System.out.println("BFS Clone verification: " + verifyClone(original, cloneBFS));
        System.out.println();
        
        // Test Iterative DFS approach
        Node cloneIterDFS = solution.cloneGraphIterativeDFS(original);
        printGraph(cloneIterDFS, "Iterative DFS Clone");
        System.out.println("Iterative DFS Clone verification: " + verifyClone(original, cloneIterDFS));
        
        // Test with null input
        System.out.println("\nNull input test:");
        Node nullClone = solution.cloneGraphDFS(null);
        System.out.println("Clone of null: " + nullClone);
    }
} 