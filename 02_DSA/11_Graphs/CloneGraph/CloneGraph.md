# Clone Graph

## Problem Statement
Given a reference of a node in a **connected undirected graph**, return a **deep copy (clone)** of the graph.

Each node in the graph contains:
- A value (`int`)  
- A list of its neighbors (`List[Node]`)

**Example:**
```
Input: adjList = [[2,4],[1,3],[2,4],[1,3]]
Output: [[2,4],[1,3],[2,4],[1,3]]

Graph visualization:
    1 ---- 2
    |      |
    |      |  
    4 ---- 3
```

## Key Challenges
1. **Avoid infinite loops** - graphs can have cycles
2. **Ensure deep copy** - new nodes, not references to original
3. **Maintain relationships** - all neighbor connections must be preserved
4. **Handle visited nodes** - don't clone the same node twice

## Approaches

### Approach 1: Depth-First Search (DFS) ⭐ (Recommended)

#### Key Insight
Use a HashMap to track original → clone mapping to avoid cycles and duplicate cloning.

#### Algorithm
1. Base case: if node is null, return null
2. If node already cloned (in HashMap), return existing clone
3. Create new clone node with same value
4. Store original → clone mapping in HashMap
5. Recursively clone all neighbors and add to clone's neighbor list

#### Time Complexity
- **O(V + E)** where V = vertices, E = edges
- Visit each node once, each edge once

#### Space Complexity
- **O(V)** for HashMap + recursion stack

```java
public Node cloneGraphDFS(Node node) {
    if (node == null) return null;
    
    Map<Node, Node> visited = new HashMap<>();
    return dfsClone(node, visited);
}

private Node dfsClone(Node node, Map<Node, Node> visited) {
    // If already cloned, return the clone
    if (visited.containsKey(node)) {
        return visited.get(node);
    }
    
    // Create clone and mark as visited
    Node clone = new Node(node.val);
    visited.put(node, clone);
    
    // Clone all neighbors
    for (Node neighbor : node.neighbors) {
        clone.neighbors.add(dfsClone(neighbor, visited));
    }
    
    return clone;
}
```

### Approach 2: Breadth-First Search (BFS)

#### Key Insight
Use BFS with queue to clone nodes level by level, still using HashMap for tracking.

#### Algorithm
1. Create clone of starting node and add to queue
2. For each node in queue:
   - Process all its neighbors
   - If neighbor not cloned yet, clone it and add to queue
   - Add cloned neighbor to current clone's neighbor list

#### Time Complexity
- **O(V + E)** - same as DFS

#### Space Complexity
- **O(V)** for HashMap + queue

```java
public Node cloneGraphBFS(Node node) {
    if (node == null) return null;
    
    Map<Node, Node> visited = new HashMap<>();
    Queue<Node> queue = new LinkedList<>();
    
    // Clone starting node
    Node clone = new Node(node.val);
    visited.put(node, clone);
    queue.offer(node);
    
    while (!queue.isEmpty()) {
        Node current = queue.poll();
        
        for (Node neighbor : current.neighbors) {
            if (!visited.containsKey(neighbor)) {
                // First time seeing this neighbor
                visited.put(neighbor, new Node(neighbor.val));
                queue.offer(neighbor);
            }
            
            // Add cloned neighbor to current clone
            visited.get(current).neighbors.add(visited.get(neighbor));
        }
    }
    
    return clone;
}
```

### Approach 3: Iterative DFS (Stack)

#### Algorithm
Similar to BFS but uses Stack instead of Queue for LIFO processing.

## Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| DFS (Recursive) | O(V+E) | O(V) | Simple, intuitive | Stack overflow for deep graphs |
| BFS | O(V+E) | O(V) | No stack overflow risk | Slightly more complex |
| Iterative DFS | O(V+E) | O(V) | No recursion, DFS order | Uses explicit stack |

## Example Trace (DFS)

Graph: 1-2-3 (linear)

1. **Clone node 1**:
   - visited = {1 → clone1}
   - clone1.neighbors = []

2. **Process neighbor 2**:
   - 2 not in visited → recursive call
   - visited = {1 → clone1, 2 → clone2}
   - clone2.neighbors = []

3. **Process neighbor 3 from node 2**:
   - 3 not in visited → recursive call  
   - visited = {1 → clone1, 2 → clone2, 3 → clone3}
   - clone3.neighbors = []
   - Add clone3 to clone2.neighbors

4. **Back to node 1**:
   - Add clone2 to clone1.neighbors

**Result**: Cloned graph with same structure

## Common Pitfalls

### 1. Infinite Recursion
```java
// WRONG - no visited tracking
Node clone = new Node(node.val);
for (Node neighbor : node.neighbors) {
    clone.neighbors.add(cloneGraph(neighbor)); // Infinite loop!
}
```

### 2. Shallow Copy
```java
// WRONG - copying references, not creating new nodes
clone.neighbors = node.neighbors; // Shallow copy!
```

### 3. Missing Cycle Handling
```java
// WRONG - doesn't handle when neighbor points back
if (!visited.contains(neighbor)) {
    // Only clone if not visited, but what about adding to neighbors?
}
```

## Key Insights

### HashMap Usage
- **Key**: Original node reference
- **Value**: Cloned node reference  
- **Purpose**: Prevents cycles and duplicate cloning

### Order of Operations
1. **Create clone** before processing neighbors
2. **Store in HashMap** immediately to prevent cycles
3. **Process neighbors** after storing to handle back-references

### Deep vs Shallow Copy
- **Deep**: New nodes with same values, new neighbor lists
- **Shallow**: Same node references (not what we want)

## Interview Tips

1. **Start with DFS** - it's the most intuitive approach
2. **Explain the HashMap strategy** - why we need it for cycles
3. **Handle edge cases** - null input, single node, self-loops
4. **Discuss alternatives** - mention BFS as iterative option
5. **Consider constraints** - for very deep graphs, iterative approaches are safer

## Edge Cases
- **Null input**: Return null
- **Single node**: Clone node with empty neighbors
- **Self-loop**: Node points to itself
- **Disconnected components**: Problem states "connected" graph

## Verification Strategy
To verify clone is correct:
1. **Different objects**: `original != clone`
2. **Same values**: `original.val == clone.val`  
3. **Same structure**: Same neighbor relationships
4. **Deep copy**: All nodes are new objects 