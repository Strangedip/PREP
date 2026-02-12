import java.util.*;

/**
 * Problem: Topological Sort
 * 
 * Given a directed acyclic graph (DAG), return a topological ordering of its vertices.
 * A topological ordering is a linear ordering of vertices such that for every directed 
 * edge (u, v), vertex u comes before v in the ordering.
 * 
 * Applications:
 * - Course scheduling with prerequisites
 * - Task scheduling with dependencies  
 * - Build systems (make, gradle)
 * - Package dependency resolution
 * 
 * Example:
 * Input: Graph with edges: [(0,1), (0,2), (1,3), (2,3)]
 * Output: [0, 1, 2, 3] or [0, 2, 1, 3] (multiple valid orderings possible)
 */
public class TopologicalSort {
    
    /**
     * APPROACH 1: DFS-BASED TOPOLOGICAL SORT
     * Time Complexity: O(V + E) where V = vertices, E = edges
     * Space Complexity: O(V) for recursion stack and data structures
     * 
     * Uses DFS to find finishing times and reverses the order.
     */
    public List<Integer> topologicalSortDFS(int numVertices, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> graph = buildGraph(numVertices, edges);
        
        boolean[] visited = new boolean[numVertices];
        Stack<Integer> stack = new Stack<>();
        
        // Visit all vertices
        for (int v = 0; v < numVertices; v++) {
            if (!visited[v]) {
                dfs(graph, v, visited, stack);
            }
        }
        
        // Convert stack to list (reverse order of finishing times)
        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        
        return result;
    }
    
    private void dfs(List<List<Integer>> graph, int vertex, boolean[] visited, Stack<Integer> stack) {
        visited[vertex] = true;
        
        // Visit all neighbors
        for (int neighbor : graph.get(vertex)) {
            if (!visited[neighbor]) {
                dfs(graph, neighbor, visited, stack);
            }
        }
        
        // Add to stack after visiting all descendants (post-order)
        stack.push(vertex);
    }
    
    /**
     * APPROACH 2: KAHN'S ALGORITHM (BFS-BASED)
     * Time Complexity: O(V + E)
     * Space Complexity: O(V)
     * 
     * Uses in-degree counting and processes vertices with zero in-degree.
     */
    public List<Integer> topologicalSortKahn(int numVertices, int[][] edges) {
        // Build adjacency list and calculate in-degrees
        List<List<Integer>> graph = buildGraph(numVertices, edges);
        int[] inDegree = new int[numVertices];
        
        for (int[] edge : edges) {
            inDegree[edge[1]]++;  // edge[0] -> edge[1]
        }
        
        // Start with vertices having zero in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int v = 0; v < numVertices; v++) {
            if (inDegree[v] == 0) {
                queue.offer(v);
            }
        }
        
        List<Integer> result = new ArrayList<>();
        
        while (!queue.isEmpty()) {
            int current = queue.poll();
            result.add(current);
            
            // Reduce in-degree of neighbors
            for (int neighbor : graph.get(current)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        // Check for cycle (if not all vertices processed)
        if (result.size() != numVertices) {
            throw new IllegalArgumentException("Graph contains a cycle - topological sort not possible");
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: DFS WITH CYCLE DETECTION
     * Time Complexity: O(V + E)
     * Space Complexity: O(V)
     * 
     * Detects cycles during DFS and throws exception if found.
     */
    public List<Integer> topologicalSortWithCycleDetection(int numVertices, int[][] edges) {
        List<List<Integer>> graph = buildGraph(numVertices, edges);
        
        int[] state = new int[numVertices];  // 0: unvisited, 1: visiting, 2: visited
        Stack<Integer> stack = new Stack<>();
        
        for (int v = 0; v < numVertices; v++) {
            if (state[v] == 0) {
                if (hasCycleDFS(graph, v, state, stack)) {
                    throw new IllegalArgumentException("Graph contains a cycle");
                }
            }
        }
        
        List<Integer> result = new ArrayList<>();
        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }
        
        return result;
    }
    
    private boolean hasCycleDFS(List<List<Integer>> graph, int vertex, int[] state, Stack<Integer> stack) {
        state[vertex] = 1;  // Mark as visiting
        
        for (int neighbor : graph.get(vertex)) {
            if (state[neighbor] == 1) {
                return true;  // Back edge found - cycle detected
            }
            if (state[neighbor] == 0 && hasCycleDFS(graph, neighbor, state, stack)) {
                return true;
            }
        }
        
        state[vertex] = 2;  // Mark as visited
        stack.push(vertex);
        return false;
    }
    
    /**
     * APPROACH 4: LEXICOGRAPHICALLY SMALLEST TOPOLOGICAL ORDER
     * Time Complexity: O(V * log V + E)
     * Space Complexity: O(V)
     * 
     * Uses priority queue to ensure lexicographically smallest ordering.
     */
    public List<Integer> lexicographicallySmallestTopologicalSort(int numVertices, int[][] edges) {
        List<List<Integer>> graph = buildGraph(numVertices, edges);
        int[] inDegree = new int[numVertices];
        
        for (int[] edge : edges) {
            inDegree[edge[1]]++;
        }
        
        // Use min-heap to always pick smallest vertex with zero in-degree
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        for (int v = 0; v < numVertices; v++) {
            if (inDegree[v] == 0) {
                pq.offer(v);
            }
        }
        
        List<Integer> result = new ArrayList<>();
        
        while (!pq.isEmpty()) {
            int current = pq.poll();
            result.add(current);
            
            for (int neighbor : graph.get(current)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    pq.offer(neighbor);
                }
            }
        }
        
        if (result.size() != numVertices) {
            throw new IllegalArgumentException("Graph contains a cycle");
        }
        
        return result;
    }
    
    /**
     * VARIATION: ALL TOPOLOGICAL SORTS
     * Time Complexity: O(V! * (V + E)) - Exponential due to all permutations
     * Space Complexity: O(V)
     * 
     * Returns all possible topological orderings.
     */
    public List<List<Integer>> allTopologicalSorts(int numVertices, int[][] edges) {
        List<List<Integer>> graph = buildGraph(numVertices, edges);
        int[] inDegree = new int[numVertices];
        
        for (int[] edge : edges) {
            inDegree[edge[1]]++;
        }
        
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        
        allTopologicalSortsHelper(graph, inDegree, current, result);
        return result;
    }
    
    private void allTopologicalSortsHelper(List<List<Integer>> graph, int[] inDegree, 
                                         List<Integer> current, List<List<Integer>> result) {
        // Find all vertices with zero in-degree
        List<Integer> candidates = new ArrayList<>();
        for (int v = 0; v < inDegree.length; v++) {
            if (inDegree[v] == 0) {
                candidates.add(v);
            }
        }
        
        if (candidates.isEmpty()) {
            if (current.size() == inDegree.length) {
                result.add(new ArrayList<>(current));
            }
            return;
        }
        
        // Try each candidate
        for (int candidate : candidates) {
            // Make choice
            current.add(candidate);
            inDegree[candidate] = -1;  // Mark as used
            
            // Update in-degrees of neighbors
            List<Integer> affected = new ArrayList<>();
            for (int neighbor : graph.get(candidate)) {
                if (inDegree[neighbor] > 0) {
                    inDegree[neighbor]--;
                    affected.add(neighbor);
                }
            }
            
            // Recurse
            allTopologicalSortsHelper(graph, inDegree, current, result);
            
            // Backtrack
            current.remove(current.size() - 1);
            inDegree[candidate] = 0;  // Restore
            for (int neighbor : affected) {
                inDegree[neighbor]++;
            }
        }
    }
    
    /**
     * APPLICATION: COURSE SCHEDULE
     * Time Complexity: O(V + E)
     * Space Complexity: O(V)
     * 
     * Determines if all courses can be completed given prerequisites.
     */
    public boolean canFinishCourses(int numCourses, int[][] prerequisites) {
        try {
            List<Integer> order = topologicalSortKahn(numCourses, prerequisites);
            return order.size() == numCourses;
        } catch (IllegalArgumentException e) {
            return false;  // Cycle detected
        }
    }
    
    /**
     * APPLICATION: COURSE SCHEDULE II
     * Time Complexity: O(V + E)
     * Space Complexity: O(V)
     * 
     * Returns the order in which courses should be taken.
     */
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        try {
            List<Integer> order = topologicalSortKahn(numCourses, prerequisites);
            return order.stream().mapToInt(i -> i).toArray();
        } catch (IllegalArgumentException e) {
            return new int[0];  // No valid order exists
        }
    }
    
    /**
     * APPLICATION: ALIEN DICTIONARY
     * Time Complexity: O(N + K) where N = total chars, K = unique chars
     * Space Complexity: O(K)
     * 
     * Derives character ordering from sorted alien words.
     */
    public String alienOrder(String[] words) {
        // Build graph from word comparisons
        Set<Character> chars = new HashSet<>();
        Map<Character, Set<Character>> graph = new HashMap<>();
        Map<Character, Integer> inDegree = new HashMap<>();
        
        // Initialize with all characters
        for (String word : words) {
            for (char c : word.toCharArray()) {
                chars.add(c);
                graph.putIfAbsent(c, new HashSet<>());
                inDegree.putIfAbsent(c, 0);
            }
        }
        
        // Build edges from adjacent words
        for (int i = 0; i < words.length - 1; i++) {
            String word1 = words[i];
            String word2 = words[i + 1];
            
            // Find first differing character
            int minLen = Math.min(word1.length(), word2.length());
            boolean foundDiff = false;
            
            for (int j = 0; j < minLen; j++) {
                char c1 = word1.charAt(j);
                char c2 = word2.charAt(j);
                
                if (c1 != c2) {
                    if (!graph.get(c1).contains(c2)) {
                        graph.get(c1).add(c2);
                        inDegree.put(c2, inDegree.get(c2) + 1);
                    }
                    foundDiff = true;
                    break;
                }
            }
            
            // Invalid case: word1 is prefix of word2 but appears after
            if (!foundDiff && word1.length() > word2.length()) {
                return "";
            }
        }
        
        // Topological sort using Kahn's algorithm
        Queue<Character> queue = new LinkedList<>();
        for (char c : chars) {
            if (inDegree.get(c) == 0) {
                queue.offer(c);
            }
        }
        
        StringBuilder result = new StringBuilder();
        while (!queue.isEmpty()) {
            char current = queue.poll();
            result.append(current);
            
            for (char neighbor : graph.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.offer(neighbor);
                }
            }
        }
        
        return result.length() == chars.size() ? result.toString() : "";
    }
    
    /**
     * HELPER: Build adjacency list from edge array
     */
    private List<List<Integer>> buildGraph(int numVertices, int[][] edges) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numVertices; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);  // edge[0] -> edge[1]
        }
        
        return graph;
    }
    
    /**
     * HELPER: Check if graph is DAG (Directed Acyclic Graph)
     */
    public boolean isDAG(int numVertices, int[][] edges) {
        try {
            topologicalSortWithCycleDetection(numVertices, edges);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * HELPER: Print graph for visualization
     */
    public void printGraph(int numVertices, int[][] edges) {
        List<List<Integer>> graph = buildGraph(numVertices, edges);
        
        System.out.println("Graph Adjacency List:");
        for (int v = 0; v < numVertices; v++) {
            System.out.print(v + " -> ");
            System.out.println(graph.get(v));
        }
        System.out.println();
    }
    
    /**
     * Test the implementations
     */
    public static void main(String[] args) {
        TopologicalSort solution = new TopologicalSort();
        
        // Test case 1: Simple DAG
        System.out.println("=== Test Case 1: Simple DAG ===");
        int[][] edges1 = {{0, 1}, {0, 2}, {1, 3}, {2, 3}};
        int numVertices1 = 4;
        
        solution.printGraph(numVertices1, edges1);
        
        System.out.println("DFS-based: " + solution.topologicalSortDFS(numVertices1, edges1));
        System.out.println("Kahn's algorithm: " + solution.topologicalSortKahn(numVertices1, edges1));
        System.out.println("With cycle detection: " + solution.topologicalSortWithCycleDetection(numVertices1, edges1));
        System.out.println("Lexicographically smallest: " + solution.lexicographicallySmallestTopologicalSort(numVertices1, edges1));
        
        System.out.println("All topological sorts:");
        List<List<Integer>> allSorts = solution.allTopologicalSorts(numVertices1, edges1);
        for (List<Integer> sort : allSorts) {
            System.out.println("  " + sort);
        }
        
        // Test case 2: Course scheduling
        System.out.println("\n=== Test Case 2: Course Scheduling ===");
        int numCourses = 4;
        int[][] prerequisites = {{1, 0}, {2, 0}, {3, 1}, {3, 2}};
        
        System.out.println("Can finish all courses: " + solution.canFinishCourses(numCourses, prerequisites));
        System.out.println("Course order: " + Arrays.toString(solution.findOrder(numCourses, prerequisites)));
        
        // Test case 3: Cycle detection
        System.out.println("\n=== Test Case 3: Graph with Cycle ===");
        int[][] cycleEdges = {{0, 1}, {1, 2}, {2, 0}};
        int numVertices3 = 3;
        
        solution.printGraph(numVertices3, cycleEdges);
        System.out.println("Is DAG: " + solution.isDAG(numVertices3, cycleEdges));
        
        try {
            solution.topologicalSortKahn(numVertices3, cycleEdges);
        } catch (IllegalArgumentException e) {
            System.out.println("Cycle detected: " + e.getMessage());
        }
        
        // Test case 4: Alien Dictionary
        System.out.println("\n=== Test Case 4: Alien Dictionary ===");
        String[] words = {"wrt", "wrf", "er", "ett", "rftt"};
        System.out.println("Words: " + Arrays.toString(words));
        System.out.println("Alien order: \"" + solution.alienOrder(words) + "\"");
        
        // Performance test
        System.out.println("\n=== Performance Test ===");
        int n = 1000;
        int[][] largeGraph = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++) {
            largeGraph[i] = new int[]{i, i + 1};  // Linear chain
        }
        
        long startTime = System.currentTimeMillis();
        List<Integer> result1 = solution.topologicalSortDFS(n, largeGraph);
        long endTime = System.currentTimeMillis();
        System.out.println("DFS (" + n + " vertices): " + (endTime - startTime) + " ms");
        
        startTime = System.currentTimeMillis();
        List<Integer> result2 = solution.topologicalSortKahn(n, largeGraph);
        endTime = System.currentTimeMillis();
        System.out.println("Kahn (" + n + " vertices): " + (endTime - startTime) + " ms");
        
        System.out.println("Results match: " + result1.equals(result2));
    }
} 