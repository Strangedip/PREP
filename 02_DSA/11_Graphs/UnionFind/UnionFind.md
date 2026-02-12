# Union Find (Disjoint Set Union)

## Problem Statement
Union Find is a data structure that efficiently handles dynamic connectivity queries. It supports two main operations:
- **Union(x, y):** Connect elements x and y
- **Find(x):** Find which set element x belongs to

Common applications: detecting cycles in graphs, Kruskal's MST, network connectivity, and social network analysis.

## Example
```
Initial: {0}, {1}, {2}, {3}, {4}
union(0, 1): {0,1}, {2}, {3}, {4}
union(2, 3): {0,1}, {2,3}, {4}
union(1, 3): {0,1,2,3}, {4}
find(0) == find(3): true (same component)
find(0) == find(4): false (different components)
```

## Approach 1: Basic Union Find

### Key Logic:
```java
class UnionFind {
    private int[] parent;
    private int components;
    
    public UnionFind(int n) {
        parent = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i; // Each node is its own parent initially
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            return find(parent[x]); // Recursive find
        }
        return x;
    }
    
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) {
            return false; // Already connected
        }
        
        parent[rootY] = rootX; // Connect components
        components--;
        return true;
    }
    
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
}
```

### Time & Space Complexity:
- **Time:** O(α(n)) amortized for both operations, where α is inverse Ackermann
- **Space:** O(n) for parent array

## Approach 2: Union Find with Path Compression

### Key Optimization:
```java
public int find(int x) {
    if (parent[x] != x) {
        parent[x] = find(parent[x]); // Path compression
    }
    return parent[x];
}
```

### How Path Compression Works:
- **Flattens the tree** during find operations
- **Makes future finds faster** by connecting nodes directly to root
- **Reduces tree height** significantly

## Approach 3: Union by Rank + Path Compression (Optimal!)

### Complete Implementation:
```java
class UnionFind {
    private int[] parent;
    private int[] rank;
    private int components;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            rank[i] = 0;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }
    
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) {
            return false;
        }
        
        // Union by rank: attach smaller tree to larger tree
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        
        components--;
        return true;
    }
    
    public int getComponents() {
        return components;
    }
}
```

## Why Union by Rank?
- **Keeps trees balanced** by always attaching shorter tree to taller tree
- **Prevents degenerate cases** where tree becomes a linked list
- **Maintains logarithmic height** in worst case

## Common Applications:

### 1. Cycle Detection in Undirected Graph:
```java
public boolean hasCycle(int[][] edges, int n) {
    UnionFind uf = new UnionFind(n);
    
    for (int[] edge : edges) {
        if (!uf.union(edge[0], edge[1])) {
            return true; // Cycle found
        }
    }
    return false;
}
```

### 2. Number of Connected Components:
```java
public int countComponents(int n, int[][] edges) {
    UnionFind uf = new UnionFind(n);
    
    for (int[] edge : edges) {
        uf.union(edge[0], edge[1]);
    }
    
    return uf.getComponents();
}
```

### 3. Kruskal's MST Algorithm:
```java
public int minCostConnectPoints(int[][] points) {
    // Sort edges by weight
    // Use Union Find to detect cycles
    // Add edge if it doesn't create cycle
}
```

## Advanced Variations:

### Union Find with Size:
```java
private int[] size; // Track component sizes

public int union(int x, int y) {
    int rootX = find(x);
    int rootY = find(y);
    
    if (rootX == rootY) return size[rootX];
    
    // Union by size instead of rank
    if (size[rootX] < size[rootY]) {
        parent[rootX] = rootY;
        size[rootY] += size[rootX];
        return size[rootY];
    } else {
        parent[rootY] = rootX;
        size[rootX] += size[rootY];
        return size[rootX];
    }
}
```

### Weighted Union Find:
```java
// For problems requiring distance/weight information
private int[] weight; // Weight from node to parent

public int find(int x) {
    if (parent[x] != x) {
        int root = find(parent[x]);
        weight[x] += weight[parent[x]]; // Path compression with weight
        parent[x] = root;
    }
    return parent[x];
}
```

## Performance Analysis:

### Without Optimizations: O(n) per operation
### With Path Compression Only: O(log n) amortized
### With Union by Rank Only: O(log n) worst case
### With Both Optimizations: O(α(n)) amortized

Where α(n) is the inverse Ackermann function, which is practically constant for all reasonable values of n.

## LeetCode Similar Problems:
- [200. Number of Islands](https://leetcode.com/problems/number-of-islands/)
- [547. Number of Provinces](https://leetcode.com/problems/number-of-provinces/)
- [684. Redundant Connection](https://leetcode.com/problems/redundant-connection/)
- [721. Accounts Merge](https://leetcode.com/problems/accounts-merge/)
- [1202. Smallest String With Swaps](https://leetcode.com/problems/smallest-string-with-swaps/)

## Interview Tips:
- **Always implement both optimizations** for SDE2 level
- **Explain the intuition** behind path compression and union by rank
- **Know common applications** like cycle detection and MST
- **Practice variations** like weighted Union Find
- **This is a must-know data structure** for graph problems at senior level 