# Section 00C: Computer Science Fundamentals for Interviews

> **Level**: ALL — Bridges to DSA and system design
> **Complements**: [02_DSA](../02_DSA/README.md), [03_CodingPatterns/02_AlgorithmicPatterns.md](../03_CodingPatterns/02_AlgorithmicPatterns.md)

---

## 00C.1 Big-O Complexity

| Notation | Name | Example |
|----------|------|---------|
| O(1) | Constant | HashMap get |
| O(log n) | Logarithmic | Binary search |
| O(n) | Linear | Single loop |
| O(n log n) | Linearithmic | Merge sort |
| O(n²) | Quadratic | Nested loops |
| O(2^n) | Exponential | Subsets brute force |

**Rules**:
- Drop constants: O(2n) → O(n)
- Drop lower terms: O(n² + n) → O(n²)
- Nested loops usually multiply

---

## 00C.2 Space Complexity

- **Input space** vs **auxiliary space** (extra memory algorithm uses)
- Recursion: O(depth) stack frames
- Hash map: O(n) for n stored elements

---

## 00C.3 Core Data Structures

| Structure | Access | Search | Insert | Delete | Use |
|-----------|--------|--------|--------|--------|-----|
| Array | O(1) index | O(n) | O(1) end | O(n) | Cache-friendly |
| Linked List | O(n) | O(n) | O(1) head | O(1) if node known | Insert/delete middle |
| Stack | O(1) top | — | O(1) push | O(1) pop | DFS, parsing |
| Queue | O(1) | — | O(1) enqueue | O(1) dequeue | BFS, scheduling |
| Hash Table | — | O(1)* | O(1)* | O(1)* | Fast lookup |
| Binary Tree | — | O(n) | O(n) | O(n) | Hierarchy |
| BST (balanced) | — | O(log n) | O(log n) | O(log n) | Sorted ops |
| Heap | — | O(n) min | O(log n) | O(log n) | Top K, priority |

---

## 00C.4 Recursion Pattern

```java
int factorial(int n) {
    if (n <= 1) return 1;           // base case
    return n * factorial(n - 1);    // recursive case
}
```

**Every recursion needs**: base case + progress toward base case.

**When to use**: Tree/graph traversal, divide-and-conquer, backtracking.

---

## 00C.5 Sorting Algorithms (Interview Summary)

| Algorithm | Time | Space | Stable? |
|-----------|------|-------|---------|
| Bubble/Insertion | O(n²) | O(1) | Yes |
| Merge Sort | O(n log n) | O(n) | Yes |
| Quick Sort | O(n log n) avg | O(log n) | No |
| Heap Sort | O(n log n) | O(1) | No |

**Default in Java**: `Arrays.sort` — dual-pivot quicksort for primitives, TimSort for objects.

---

## 00C.6 Graph Basics

- **Directed vs undirected**
- **BFS** — shortest path unweighted, level order — **queue**
- **DFS** — paths, cycles, connectivity — **stack/recursion**
- **Weighted shortest path** — Dijkstra (non-negative weights)

---

## 00C.7 Dynamic Programming Intuition

1. **Overlapping subproblems** — same subproblem solved many times
2. **Optimal substructure** — optimal answer built from optimal sub-answers

Approaches: top-down (memoization) or bottom-up (table).

Classic: Fibonacci, knapsack, LCS, coin change → see [02_DSA/13_Dynamic_Programming](../02_DSA/13_Dynamic_Programming/).

---

## 00C.8 Interview Quick Reference

| Question | Answer |
|----------|--------|
| Array vs LinkedList? | Array: O(1) access. LinkedList: O(1) insert at known node; O(n) access. |
| HashMap average lookup? | O(1); worst O(n) if many collisions. |
| BFS vs DFS? | BFS: shortest path unweighted, queue. DFS: stack, deeper exploration. |
| When DP? | Overlapping subproblems + optimal substructure. |
| Stable sort? | Equal elements keep original order (merge sort yes, quick sort no). |
| Master Theorem? | Analyze divide-and-conquer recurrences — see [02_DSA MasterTheorem](../02_DSA/07_Recursion_Divide_Conquer/MasterTheorem/). |

**Next step**: [03_CodingPatterns/02_AlgorithmicPatterns.md](../03_CodingPatterns/02_AlgorithmicPatterns.md) then [02_DSA/StudyGuide.md](../02_DSA/StudyGuide.md).
