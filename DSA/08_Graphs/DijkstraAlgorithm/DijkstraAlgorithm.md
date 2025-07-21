# Dijkstra's Shortest Path Algorithm

## Problem Statement
Find the shortest path from a source vertex to all other vertices in a weighted graph with non-negative edge weights.

**Key Characteristics:**
- **Works only with non-negative weights**
- **Single-source shortest path** algorithm
- **Uses greedy approach** with priority queue
- **Guarantees optimal solution** for non-negative weights

## Example
```
Graph:
    0 ----4---- 1
    |    \      |
    2     \     3
    |      \    |
    3 ----1---- 2

Source: 0
Shortest distances: [0, 4, 5, 2]
Paths: 0→0, 0→1, 0→3→2, 0→3
```

## Approach 1: Basic Dijkstra with Priority Queue

### Key Logic:
```java
public int[] dijkstra(int[][] graph, int source) {
    int n = graph.length;
    int[] dist = new int[n];
    boolean[] visited = new boolean[n];
    
    // Initialize distances
    Arrays.fill(dist, Integer.MAX_VALUE);
    dist[source] = 0;
    
    // Min heap: [distance, vertex]
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    pq.offer(new int[]{0, source});
    
    while (!pq.isEmpty()) {
        int[] current = pq.poll();
        int u = current[1];
        
        if (visited[u]) continue;
        visited[u] = true;
        
        // Explore neighbors
        for (int v = 0; v < n; v++) {
            if (!visited[v] && graph[u][v] != 0) {
                int newDist = dist[u] + graph[u][v];
                
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{newDist, v});
                }
            }
        }
    }
    
    return dist;
}
```

### Time & Space Complexity:
- **Time:** O((V + E) log V) using binary heap
- **Space:** O(V) for distance array and priority queue

## Approach 2: Dijkstra with Adjacency List

### More Efficient Implementation:
```java
class Edge {
    int to, weight;
    
    Edge(int to, int weight) {
        this.to = to;
        this.weight = weight;
    }
}

public class DijkstraGraph {
    private List<List<Edge>> graph;
    private int vertices;
    
    public DijkstraGraph(int vertices) {
        this.vertices = vertices;
        graph = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            graph.add(new ArrayList<>());
        }
    }
    
    public void addEdge(int from, int to, int weight) {
        graph.get(from).add(new Edge(to, weight));
    }
    
    public int[] shortestPath(int source) {
        int[] dist = new int[vertices];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, source});
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentDist = current[0];
            int u = current[1];
            
            // Skip if we've found a better path already
            if (currentDist > dist[u]) continue;
            
            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                int weight = edge.weight;
                int newDist = dist[u] + weight;
                
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    pq.offer(new int[]{newDist, v});
                }
            }
        }
        
        return dist;
    }
}
```

## Approach 3: Dijkstra with Path Reconstruction

### Track Actual Paths:
```java
public class DijkstraWithPath {
    public static class Result {
        int[] distances;
        int[] parent;
        
        Result(int[] distances, int[] parent) {
            this.distances = distances;
            this.parent = parent;
        }
        
        public List<Integer> getPath(int destination) {
            List<Integer> path = new ArrayList<>();
            for (int at = destination; at != -1; at = parent[at]) {
                path.add(at);
            }
            Collections.reverse(path);
            return path;
        }
    }
    
    public static Result dijkstraWithPath(List<List<Edge>> graph, int source) {
        int n = graph.size();
        int[] dist = new int[n];
        int[] parent = new int[n];
        
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;
        
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[]{0, source});
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentDist = current[0];
            int u = current[1];
            
            if (currentDist > dist[u]) continue;
            
            for (Edge edge : graph.get(u)) {
                int v = edge.to;
                int newDist = dist[u] + edge.weight;
                
                if (newDist < dist[v]) {
                    dist[v] = newDist;
                    parent[v] = u;
                    pq.offer(new int[]{newDist, v});
                }
            }
        }
        
        return new Result(dist, parent);
    }
}
```

## Algorithm Visualization:

### Step-by-Step Process:
```
Initial: dist = [0, ∞, ∞, ∞], pq = [(0,0)]

Step 1: Process vertex 0
- Update neighbors: dist = [0, 4, ∞, 2]
- pq = [(2,3), (4,1)]

Step 2: Process vertex 3 (distance 2)
- Update neighbors: dist = [0, 4, 3, 2]
- pq = [(3,2), (4,1)]

Step 3: Process vertex 2 (distance 3)
- No better paths found
- pq = [(4,1)]

Step 4: Process vertex 1 (distance 4)
- No unvisited neighbors
- pq = []

Final: dist = [0, 4, 3, 2]
```

## Advanced Variations:

### 1. Modified Dijkstra for Specific Problems:
```java
// For problems with constraints (e.g., at most K stops)
public int findCheapestPrice(int n, int[][] flights, int src, int dst, int k) {
    // Use modified state: (cost, city, stops)
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    pq.offer(new int[]{0, src, 0});
    
    // Track best cost to reach each city with at most i stops
    int[][] minCost = new int[n][k + 2];
    for (int[] row : minCost) {
        Arrays.fill(row, Integer.MAX_VALUE);
    }
    
    while (!pq.isEmpty()) {
        int[] current = pq.poll();
        int cost = current[0];
        int city = current[1];
        int stops = current[2];
        
        if (city == dst) return cost;
        if (stops > k) continue;
        if (cost > minCost[city][stops]) continue;
        
        minCost[city][stops] = cost;
        
        // Process outgoing flights...
    }
    
    return -1;
}
```

### 2. Bidirectional Dijkstra:
```java
// Search from both source and destination simultaneously
// Can be faster for single-pair shortest path
public int bidirectionalDijkstra(List<List<Edge>> graph, 
                                List<List<Edge>> reverseGraph, 
                                int source, int destination) {
    // Implement two simultaneous searches
    // Stop when they meet in the middle
}
```

### 3. A* Algorithm (Dijkstra with Heuristic):
```java
// Use heuristic function for faster pathfinding
public int aStarSearch(int[][] grid, int[] start, int[] end) {
    // Priority: f(n) = g(n) + h(n)
    // g(n) = actual distance, h(n) = heuristic
    PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> 
        (a[0] + heuristic(a, end)) - (b[0] + heuristic(b, end)));
}
```

## Common Applications:

### 1. Network Routing:
```java
// Find shortest path in computer networks
// IP routing protocols use Dijkstra variants
```

### 2. GPS Navigation:
```java
// Find shortest/fastest route between locations
// Weight edges by distance or time
```

### 3. Social Networks:
```java
// Find shortest connection path between users
// Six degrees of separation problems
```

### 4. Game AI:
```java
// Pathfinding in games
// NPCs finding optimal routes
```

## Limitations and Alternatives:

### ❌ Dijkstra Limitations:
- **Cannot handle negative weights**
- **Single-source only** (use Floyd-Warshall for all-pairs)
- **May be overkill** for unweighted graphs (use BFS)

### ✅ Alternatives:
- **Bellman-Ford:** Handles negative weights
- **Floyd-Warshall:** All-pairs shortest path
- **BFS:** For unweighted graphs
- **A*:** When heuristic is available

## Optimization Techniques:

### 1. Early Termination:
```java
// Stop when destination is reached
if (u == destination) break;
```

### 2. Fibonacci Heap:
```java
// Improves time complexity to O(E + V log V)
// More complex implementation
```

### 3. Preprocessing:
```java
// For repeated queries, precompute landmarks
// Use contraction hierarchies for road networks
```

## LeetCode Similar Problems:
- [743. Network Delay Time](https://leetcode.com/problems/network-delay-time/)
- [787. Cheapest Flights Within K Stops](https://leetcode.com/problems/cheapest-flights-within-k-stops/)
- [1631. Path With Minimum Effort](https://leetcode.com/problems/path-with-minimum-effort/)
- [1102. Path With Maximum Minimum Value](https://leetcode.com/problems/path-with-maximum-minimum-value/)
- [778. Swim in Rising Water](https://leetcode.com/problems/swim-in-rising-water/)

## Interview Tips:
- **Understand when to use** vs other shortest path algorithms
- **Master the priority queue implementation**
- **Know how to reconstruct paths**
- **Practice variations** with constraints
- **Essential for graph problems** at SDE2+ level 