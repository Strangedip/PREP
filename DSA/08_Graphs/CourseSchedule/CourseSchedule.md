# Course Schedule

## Problem Statement
There are a total of `numCourses` courses you have to take, labeled from `0` to `numCourses - 1`.

You are given an array `prerequisites` where `prerequisites[i] = [ai, bi]` indicates that you **must take course `bi` first** if you want to take course `ai`.

Return `true` if you can finish all courses. Otherwise, return `false`.

**Examples:**
```
Input: numCourses = 2, prerequisites = [[1,0]]
Output: true
Explanation: Take course 0, then course 1.

Input: numCourses = 2, prerequisites = [[1,0],[0,1]]  
Output: false
Explanation: Circular dependency - impossible!
```

## Problem Analysis

### Core Insight
This is a **cycle detection** problem in a **directed graph**:
- **Nodes**: Courses (0 to numCourses-1)
- **Edges**: Prerequisites (bi → ai means bi must come before ai)
- **Goal**: Determine if graph has cycles

### Key Concepts
- **Cycle exists** → Cannot finish all courses
- **No cycle** → Can arrange courses in valid order (topological sort)

## Approaches

### Approach 1: DFS Cycle Detection ⭐ (Most Intuitive)

#### Key Insight
Use DFS with three states to detect back edges (cycles):
- **0**: Unvisited
- **1**: Visiting (in current DFS path)  
- **2**: Visited (completed)

#### Algorithm
1. Build adjacency list from prerequisites
2. For each unvisited course, run DFS
3. In DFS: mark as visiting(1) → explore neighbors → mark as visited(2)
4. If we encounter a node in visiting(1) state → cycle detected!

#### Time Complexity
- **O(V + E)** where V = courses, E = prerequisites

#### Space Complexity  
- **O(V + E)** for adjacency list + O(V) for recursion stack

```java
public boolean canFinishDFS(int numCourses, int[][] prerequisites) {
    // Build graph
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) {
        graph.add(new ArrayList<>());
    }
    
    for (int[] prereq : prerequisites) {
        graph.get(prereq[1]).add(prereq[0]); // prereq[1] → prereq[0]
    }
    
    int[] state = new int[numCourses]; // 0: unvisited, 1: visiting, 2: visited
    
    for (int course = 0; course < numCourses; course++) {
        if (state[course] == 0) {
            if (hasCycleDFS(graph, course, state)) {
                return false;
            }
        }
    }
    
    return true;
}

private boolean hasCycleDFS(List<List<Integer>> graph, int course, int[] state) {
    if (state[course] == 1) return true;  // Back edge - cycle!
    if (state[course] == 2) return false; // Already processed
    
    state[course] = 1; // Mark as visiting
    
    for (int nextCourse : graph.get(course)) {
        if (hasCycleDFS(graph, nextCourse, state)) {
            return true;
        }
    }
    
    state[course] = 2; // Mark as completed
    return false;
}
```

### Approach 2: Topological Sort (Kahn's Algorithm) ⭐⭐

#### Key Insight
Use BFS with in-degree counting. If we can process all courses → no cycle exists.

#### Algorithm
1. Calculate in-degree for each course
2. Start with courses having in-degree 0 (no prerequisites)
3. Process courses in BFS manner:
   - Remove course and decrease in-degree of dependent courses
   - Add courses with in-degree 0 to queue
4. If processed courses = total courses → no cycle

#### Time Complexity
- **O(V + E)** - same as DFS

#### Space Complexity
- **O(V + E)** for adjacency list + O(V) for queue

```java
public boolean canFinishTopological(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>();
    int[] inDegree = new int[numCourses];
    
    // Build graph and calculate in-degrees
    for (int i = 0; i < numCourses; i++) {
        graph.add(new ArrayList<>());
    }
    
    for (int[] prereq : prerequisites) {
        graph.get(prereq[1]).add(prereq[0]);
        inDegree[prereq[0]]++;
    }
    
    // Start with courses having no prerequisites
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < numCourses; i++) {
        if (inDegree[i] == 0) {
            queue.offer(i);
        }
    }
    
    int processedCourses = 0;
    
    while (!queue.isEmpty()) {
        int course = queue.poll();
        processedCourses++;
        
        // Update in-degrees of dependent courses
        for (int nextCourse : graph.get(course)) {
            inDegree[nextCourse]--;
            if (inDegree[nextCourse] == 0) {
                queue.offer(nextCourse);
            }
        }
    }
    
    return processedCourses == numCourses;
}
```

## Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| DFS Cycle Detection | O(V+E) | O(V+E) | Intuitive, detects cycles directly | Recursion stack may overflow |
| Topological Sort (BFS) | O(V+E) | O(V+E) | Iterative, natural ordering | Less intuitive for cycle detection |

## Example Traces

### Example 1: Valid Course Schedule
```
numCourses = 3, prerequisites = [[1,0], [2,1]]
Graph: 0 → 1 → 2
```

**DFS Trace:**
1. Start DFS from course 0: state[0] = 1
2. Visit course 1: state[1] = 1  
3. Visit course 2: state[2] = 1
4. Course 2 has no neighbors: state[2] = 2
5. Back to course 1: state[1] = 2
6. Back to course 0: state[0] = 2
7. No cycles found → return true

**Topological Sort Trace:**
1. In-degrees: [0, 1, 1] 
2. Queue starts with: [0]
3. Process 0 → update in-degree[1] = 0 → queue: [1]
4. Process 1 → update in-degree[2] = 0 → queue: [2]  
5. Process 2 → queue: []
6. Processed 3 courses = total → return true

### Example 2: Cycle Detected
```
numCourses = 2, prerequisites = [[1,0], [0,1]]
Graph: 0 ⇄ 1 (cycle)
```

**DFS Trace:**
1. Start DFS from course 0: state[0] = 1
2. Visit course 1: state[1] = 1
3. From course 1, try to visit course 0
4. state[0] == 1 (visiting) → cycle detected! → return false

**Topological Sort Trace:**
1. In-degrees: [1, 1]
2. No courses with in-degree 0 → queue: []
3. Process 0 courses ≠ 2 total → return false

## Course Schedule II Extension

To return the actual course ordering:

```java
public int[] findOrder(int numCourses, int[][] prerequisites) {
    // Same topological sort setup...
    
    int[] result = new int[numCourses];
    int index = 0;
    
    while (!queue.isEmpty()) {
        int course = queue.poll();
        result[index++] = course; // Store the order
        
        // Update dependencies...
    }
    
    return index == numCourses ? result : new int[0];
}
```

## Key Insights

### Why These Approaches Work
- **DFS**: Back edges in DFS indicate cycles in directed graphs
- **Topological Sort**: Only possible in DAGs (Directed Acyclic Graphs)

### State Management in DFS
- **White (0)**: Unvisited
- **Gray (1)**: Visiting (in current path) - key for cycle detection
- **Black (2)**: Visited (completely processed)

### In-Degree Intuition
- In-degree = number of prerequisites
- Course with in-degree 0 = no prerequisites = can take immediately
- Removing course = completing prerequisite for others

## Interview Tips

1. **Clarify the problem**: Course dependencies = directed graph
2. **Choose your approach**: DFS is more intuitive for cycle detection
3. **Explain the cycle connection**: Why cycles make it impossible
4. **Handle edge cases**: No prerequisites, self-loops, disconnected components
5. **Discuss extensions**: Course Schedule II (ordering), parallel courses

## Common Mistakes

1. **Wrong graph direction**: `[a,b]` means b→a, not a→b
2. **Missing cycle detection**: Not handling the "visiting" state properly
3. **Incorrect in-degree calculation**: Counting edges in wrong direction
4. **Off-by-one errors**: Course numbering starts from 0

## Applications
- **Course scheduling** in universities
- **Task scheduling** with dependencies  
- **Build systems** (compile order)
- **Package managers** (dependency resolution) 