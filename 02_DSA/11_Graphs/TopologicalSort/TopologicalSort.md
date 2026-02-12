# Topological Sort

## Problem Statement
Given a **directed acyclic graph (DAG)**, return a **topological ordering** of its vertices. A topological ordering is a linear ordering of vertices such that for every directed edge `(u, v)`, vertex `u` comes before `v` in the ordering.

**Key Properties:**
- Only possible for **Directed Acyclic Graphs (DAGs)**
- **Multiple valid orderings** may exist
- **No cycles** allowed in the graph
- **Linear ordering** that respects all edge dependencies

**Examples:**
```
Input: Graph with edges: [(0,1), (0,2), (1,3), (2,3)]
Output: [0, 1, 2, 3] or [0, 2, 1, 3] (both valid)

Visualization:
0 → 1 → 3
↓     ↗
2 -----
```

## Problem Analysis

### Core Insight
This is a **dependency resolution problem** where we need to find an ordering that satisfies all precedence constraints:
- **Prerequisites must come first**: If A depends on B, then B appears before A
- **No circular dependencies**: Graph must be acyclic
- **Multiple solutions possible**: Usually many valid orderings exist

### Real-World Applications
1. **Course Scheduling**: Prerequisites determine course order
2. **Build Systems**: Compile dependencies (make, gradle, maven)
3. **Package Management**: Install packages in dependency order
4. **Task Scheduling**: Execute tasks respecting dependencies
5. **Symbol Table Resolution**: Resolve references in compilers

## Approaches

### Approach 1: DFS-Based Topological Sort ⭐ (Most Intuitive)

#### Key Insight
**Use DFS finishing times**: Vertices that finish later have no dependencies on vertices that finish earlier.

#### Algorithm
1. **Perform DFS** on all unvisited vertices
2. **Record finishing times**: When DFS completes for a vertex, add to result
3. **Reverse the order**: Later finishing times come first in topological order
4. **Use stack**: Natural way to reverse the finishing order

#### Time Complexity
- **O(V + E)** where V = vertices, E = edges
- **Single pass**: Each vertex and edge visited exactly once

#### Space Complexity
- **O(V)** for recursion stack and data structures

```java
public List<Integer> topologicalSortDFS(int numVertices, int[][] edges) {
    List<List<Integer>> graph = buildGraph(numVertices, edges);
    boolean[] visited = new boolean[numVertices];
    Stack<Integer> stack = new Stack<>();
    
    // Visit all vertices
    for (int v = 0; v < numVertices; v++) {
        if (!visited[v]) {
            dfs(graph, v, visited, stack);
        }
    }
    
    // Convert stack to list (reverse finishing order)
    List<Integer> result = new ArrayList<>();
    while (!stack.isEmpty()) {
        result.add(stack.pop());
    }
    return result;
}

private void dfs(List<List<Integer>> graph, int vertex, boolean[] visited, Stack<Integer> stack) {
    visited[vertex] = true;
    
    for (int neighbor : graph.get(vertex)) {
        if (!visited[neighbor]) {
            dfs(graph, neighbor, visited, stack);
        }
    }
    
    stack.push(vertex);  // Add after visiting all descendants
}
```

### Approach 2: Kahn's Algorithm (BFS-Based) ⭐ (Most Popular)

#### Key Insight
**Process vertices with zero in-degree first**: Vertices with no incoming edges can be processed immediately.

#### Algorithm
1. **Calculate in-degrees**: Count incoming edges for each vertex
2. **Queue zero in-degree vertices**: Start with vertices having no dependencies
3. **Process iteratively**: Remove vertex and decrease neighbors' in-degrees
4. **Add newly zero in-degree vertices**: Continue until queue is empty
5. **Cycle detection**: If not all vertices processed, cycle exists

#### Time Complexity
- **O(V + E)** - Each vertex and edge processed once

#### Space Complexity
- **O(V)** for in-degree array and queue

```java
public List<Integer> topologicalSortKahn(int numVertices, int[][] edges) {
    List<List<Integer>> graph = buildGraph(numVertices, edges);
    int[] inDegree = new int[numVertices];
    
    // Calculate in-degrees
    for (int[] edge : edges) {
        inDegree[edge[1]]++;
    }
    
    // Start with zero in-degree vertices
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
    
    // Check for cycle
    if (result.size() != numVertices) {
        throw new IllegalArgumentException("Graph contains a cycle");
    }
    
    return result;
}
```

### Approach 3: DFS with Cycle Detection

#### Key Insight
**Detect cycles during DFS** using three-state coloring: unvisited, visiting, visited.

#### Algorithm
1. **Three states**: 0 = unvisited, 1 = visiting (in current path), 2 = visited
2. **Back edge detection**: If we encounter a vertex in "visiting" state, cycle found
3. **Post-order addition**: Add to result only after visiting all descendants

#### Cycle Detection Logic
```java
private boolean hasCycleDFS(List<List<Integer>> graph, int vertex, int[] state, Stack<Integer> stack) {
    state[vertex] = 1;  // Mark as visiting
    
    for (int neighbor : graph.get(vertex)) {
        if (state[neighbor] == 1) {
            return true;  // Back edge - cycle found
        }
        if (state[neighbor] == 0 && hasCycleDFS(graph, neighbor, state, stack)) {
            return true;
        }
    }
    
    state[vertex] = 2;  // Mark as visited
    stack.push(vertex);
    return false;
}
```

### Approach 4: Lexicographically Smallest Ordering

#### Key Insight
**Use priority queue** instead of regular queue to ensure smallest vertex is always chosen first.

#### Algorithm
1. **Replace queue with min-heap**: Always process smallest available vertex
2. **Same logic as Kahn's**: But with ordered processing
3. **Guarantees unique result**: Among all valid orderings, returns lexicographically smallest

#### Time Complexity
- **O(V log V + E)** due to priority queue operations

```java
public List<Integer> lexicographicallySmallestTopologicalSort(int numVertices, int[][] edges) {
    // ... calculate in-degrees ...
    
    PriorityQueue<Integer> pq = new PriorityQueue<>();  // Min-heap
    for (int v = 0; v < numVertices; v++) {
        if (inDegree[v] == 0) {
            pq.offer(v);
        }
    }
    
    // Same processing logic but with ordered selection
}
```

## Implementation Strategies

### Graph Representation
```java
// Adjacency list (most common)
List<List<Integer>> graph = new ArrayList<>();
for (int i = 0; i < numVertices; i++) {
    graph.add(new ArrayList<>());
}
for (int[] edge : edges) {
    graph.get(edge[0]).add(edge[1]);  // edge[0] → edge[1]
}
```

### In-Degree Calculation
```java
int[] inDegree = new int[numVertices];
for (int[] edge : edges) {
    inDegree[edge[1]]++;  // Increment in-degree of destination
}
```

### Cycle Detection Patterns
```java
// Method 1: Count processed vertices
if (result.size() != numVertices) {
    // Cycle exists
}

// Method 2: Three-state DFS
int[] state = new int[numVertices];  // 0: unvisited, 1: visiting, 2: visited
```

## Common Applications

### 1. Course Schedule (LeetCode 207/210)
```java
public boolean canFinish(int numCourses, int[][] prerequisites) {
    try {
        List<Integer> order = topologicalSortKahn(numCourses, prerequisites);
        return order.size() == numCourses;
    } catch (IllegalArgumentException e) {
        return false;  // Cycle detected
    }
}
```

### 2. Alien Dictionary (LeetCode 269)
```java
public String alienOrder(String[] words) {
    // Build graph from word comparisons
    // Apply topological sort on characters
    // Return character ordering as string
}
```

### 3. Build Dependencies
```java
public List<String> buildOrder(String[] projects, String[][] dependencies) {
    // Model projects as vertices, dependencies as edges
    // Return topological ordering of projects
}
```

## Variations & Extensions

### 1. All Topological Sorts
Find all possible valid orderings using backtracking.

### 2. Minimum/Maximum Topological Order
Use priority queues with custom comparators.

### 3. Parallel Processing
Determine which tasks can be executed in parallel.

### 4. Critical Path
Find longest path in DAG (scheduling applications).

## Common Mistakes

1. **Forgetting cycle detection**: Not checking if all vertices are processed
2. **Wrong edge direction**: Confusing source and destination in edge representation
3. **In-degree calculation errors**: Incorrect counting of incoming edges
4. **Stack vs Queue confusion**: Using wrong data structure for DFS approach
5. **Multiple disconnected components**: Not processing all vertices in DFS

## Edge Cases

1. **Empty graph**: No vertices or edges
2. **Single vertex**: Trivial topological order
3. **Disconnected components**: Multiple separate DAGs
4. **Self-loops**: Invalid for topological sort
5. **Parallel edges**: Multiple edges between same vertices

## Interview Tips

### Problem Recognition
- **"Prerequisites"** or **"dependencies"** → Think topological sort
- **"Course schedule"** → Classic topological sort application
- **"Build order"** → Dependency resolution
- **Cycle detection** in directed graphs

### Approach Strategy
1. **Start with Kahn's algorithm**: Most intuitive for interviews
2. **Explain cycle detection**: Show understanding of DAG requirement
3. **Handle edge cases**: Empty graph, disconnected components
4. **Discuss time complexity**: O(V + E) for both main approaches
5. **Code cleanly**: Clear variable names and logic flow

### Code Template
```java
public List<Integer> topologicalSort(int numVertices, int[][] edges) {
    // 1. Build adjacency list
    // 2. Calculate in-degrees
    // 3. Initialize queue with zero in-degree vertices
    // 4. Process vertices iteratively
    // 5. Check for cycles
    // 6. Return result
}
```

## LeetCode Problems

### Core Problems:
- **[LeetCode 207 - Course Schedule](https://leetcode.com/problems/course-schedule/)** ⭐ (Can finish all courses - cycle detection)
- **[LeetCode 210 - Course Schedule II](https://leetcode.com/problems/course-schedule-ii/)** ⭐ (Return course order - topological sort)
- **[LeetCode 269 - Alien Dictionary](https://leetcode.com/problems/alien-dictionary/)** ⭐ (Premium - derive character ordering)

### Related Graph Problems:
- **[LeetCode 802 - Find Eventual Safe States](https://leetcode.com/problems/find-eventual-safe-states/)** (Cycle detection in directed graph)
- **[LeetCode 310 - Minimum Height Trees](https://leetcode.com/problems/minimum-height-trees/)** (Topological sort variation)
- **[LeetCode 444 - Sequence Reconstruction](https://leetcode.com/problems/sequence-reconstruction/)** (Premium - unique topological order)
- **[LeetCode 1136 - Parallel Courses](https://leetcode.com/problems/parallel-courses/)** (Premium - course scheduling with time)

### Advanced Applications:
- **[LeetCode 1203 - Sort Items by Groups Respecting Dependencies](https://leetcode.com/problems/sort-items-by-groups-respecting-dependencies/)** (Multi-level topological sort)
- **[LeetCode 1857 - Largest Color Value in Directed Graph](https://leetcode.com/problems/largest-color-value-in-a-directed-graph/)** (DP + topological sort)

### Difficulty Progression:
1. **Start with**: LeetCode 207 (Course Schedule) - Basic cycle detection
2. **Next try**: LeetCode 210 (Course Schedule II) - Actual topological ordering
3. **Advanced**: LeetCode 269 (Alien Dictionary) - Building graph from constraints
4. **Expert**: LeetCode 1203 (Sort Items by Groups) - Complex dependency systems

## Complexity Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **DFS-based** | O(V+E) | O(V) | **Intuitive understanding** |
| **Kahn's Algorithm** | O(V+E) | O(V) | **Interview favorite** |
| **Cycle Detection** | O(V+E) | O(V) | **Robust error handling** |
| **Lexicographic** | O(V log V + E) | O(V) | **Unique ordering required** |

## Real-World Impact

1. **Build Systems**: Maven, Gradle dependency resolution
2. **Package Managers**: npm, pip, apt package installation order
3. **Compiler Design**: Symbol resolution, module compilation
4. **Database**: Foreign key constraint checking
5. **Operating Systems**: Process scheduling with dependencies
6. **Project Management**: Task scheduling in project plans

**Remember**: Topological Sort is the **foundation of dependency resolution** and demonstrates the power of **graph algorithms** in solving real-world scheduling and ordering problems! 