# Section 00C: Computer Science Fundamentals for Interviews

> **Level**: ALL — Bridges to DSA and system design
> **Depth**: Standard (foundational textbook-style explanations with worked examples)
> **Complements**: [02_DSA](../02_DSA/README.md), [03_CodingPatterns/02_AlgorithmicPatterns.md](../03_CodingPatterns/02_AlgorithmicPatterns.md)

> **You are here**: Fresher — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Next**: [00_Java_OOP_Fundamentals.md](00_Java_OOP_Fundamentals.md)

---

## Why this section exists

Every DSA problem and system design discussion assumes you can reason about **time**, **space**, and **data structure trade-offs**. This guide builds that intuition from scratch — not as a cheat sheet, but as concepts you can explain in an interview without memorizing tables.

**How to study**: Read one section, close the file, and explain it aloud in 2 minutes. Then solve one linked DSA problem from that section.

---

## 00C.1 Big-O Complexity — What It Actually Measures

Big-O describes how **runtime or memory grows** as input size `n` increases. It is **not** about milliseconds on your laptop — it is about **scalability**.

### The core idea

Imagine you have a list of 1,000 names:

| Operation | What you do | Growth |
|-----------|-------------|--------|
| Look up one name by index | `names[42]` | Same speed at 1K or 1M names → **O(1)** |
| Find a name by scanning | Check every name | 10× more names ≈ 10× slower → **O(n)** |
| Compare every pair of names | Nested loops | 10× more names ≈ 100× slower → **O(n²)** |

### Common complexities (slowest → fastest for large n)

| Notation | Name | Real-world analogy | Example |
|----------|------|-------------------|---------|
| O(1) | Constant | Opening a specific locker when you know the number | `HashMap.get(key)` average case |
| O(log n) | Logarithmic | Phone book: halve the search space each step | Binary search on sorted array |
| O(n) | Linear | Reading every page of a book once | Single `for` loop over array |
| O(n log n) | Linearithmic | Efficient sorting (merge sort, TimSort) | `Arrays.sort()` on objects |
| O(n²) | Quadratic | Handshake problem: everyone greets everyone | Nested loops over same array |
| O(2^n) | Exponential | Trying every subset of a set | Brute-force subsets |

### Worked example: finding a number in a sorted array

```
Array: [2, 5, 8, 12, 16, 23, 38, 56, 72, 91]   target = 23

Linear search (O(n)): check 2, 5, 8, 12, 16, 23 → 6 comparisons
Binary search (O(log n)):
  mid=16 → 23 > 16, search right half
  mid=56 → 23 < 56, search left half
  mid=23 → found → 3 comparisons
```

For `n = 1,000,000`: linear ≈ 1M steps; binary ≈ 20 steps.

### Rules for simplifying Big-O

1. **Drop constants**: `O(2n)` → `O(n)` — constants don't change growth rate
2. **Drop lower terms**: `O(n² + n)` → `O(n²)` — dominant term wins at scale
3. **Nested loops usually multiply**: outer `n` × inner `n` → `O(n²)`
4. **Sequential blocks add**: loop A `O(n)` then loop B `O(n)` → `O(n)`, not `O(2n)` simplified to `O(n)`

### Common mistakes in interviews

| Mistake | Correct thinking |
|---------|------------------|
| "HashMap is always O(1)" | Average O(1); worst case O(n) with many hash collisions |
| "Two loops = O(n²)" | Only if both iterate up to n. Inner loop shrinking → often O(n) total |
| "Recursion is always slow" | Tail recursion + memoization can be O(n); depth matters for stack |

---

## 00C.2 Space Complexity

Space complexity counts **extra memory** your algorithm uses beyond the input.

| Type | Meaning | Example |
|------|---------|---------|
| **Input space** | Memory to store the input | Array of n integers → O(n) input |
| **Auxiliary space** | Extra memory your algorithm allocates | HashMap of size n → O(n) auxiliary |
| **Output space** | Often excluded from analysis unless problem asks | Returning new array of size n |

### Examples

```java
// O(1) auxiliary — only a few variables
int findMax(int[] nums) {
    int max = nums[0];
    for (int n : nums) if (n > max) max = n;
    return max;
}

// O(n) auxiliary — HashMap stores up to n entries
int[] twoSum(int[] nums, int target) {
    Map<Integer, Integer> seen = new HashMap<>();
    // ...
}

// O(n) auxiliary — recursion stack depth = tree height
int factorial(int n) {
    if (n <= 1) return 1;
    return n * factorial(n - 1);  // n stack frames
}
```

**Interview tip**: Always state both time and space. "O(n) time, O(n) space for the HashMap" beats just "O(n)".

---

## 00C.3 Core Data Structures — When to Use What

### Array vs Linked List

```
Array:     [10][20][30][40][50]   ← contiguous memory, index access in O(1)
Linked:    [10]→[20]→[30]→[40]→[50]   ← nodes scattered, traverse to reach index O(n)
```

| Operation | Array | Linked List |
|-----------|-------|-------------|
| Access by index | O(1) | O(n) |
| Insert at end | O(1) amortized | O(1) if tail pointer |
| Insert at middle | O(n) shift | O(1) if node known |
| Delete | O(n) shift | O(1) if node known |
| Cache performance | Excellent (locality) | Poor (pointer chasing) |

**When to choose**: Arrays for random access and iteration. Linked lists when you insert/delete frequently at known positions (rare in practice — `ArrayList` wins most Java interviews).

### Stack and Queue

```
Stack (LIFO):  push → [3][2][1] ← pop     Use: DFS, undo, bracket matching
Queue (FIFO):  enqueue → [1][2][3] → dequeue   Use: BFS, task scheduling
```

### Hash Table (HashMap)

- **Average**: O(1) insert, lookup, delete
- **How**: `hash(key) % bucket_count` → bucket → handle collisions (chaining or open addressing)
- **Worst case**: O(n) if all keys collide (rare with good hash function)
- **Interview use**: Frequency counting, complement search (Two Sum), deduplication

### Binary Tree and BST

```
        8
       / \
      3   10
     / \    \
    1   6    14

BST property: left < parent < right
Balanced BST: O(log n) search/insert/delete
Skewed tree: degrades to O(n) — like a linked list
```

### Heap (Priority Queue)

- **Min-heap**: smallest element at root — O(1) peek, O(log n) insert/extract
- **Use cases**: Top K elements, merge K sorted lists, Dijkstra's algorithm
- **Not sorted**: only guarantees parent ≤ children (min-heap)

### Master comparison table

| Structure | Access | Search | Insert | Delete | Best for |
|-----------|--------|--------|--------|--------|----------|
| Array | O(1) index | O(n) | O(1) end | O(n) | Random access, sorting |
| Linked List | O(n) | O(n) | O(1) head | O(1) known node | Rare — know for interviews |
| Stack | O(1) top | — | O(1) push | O(1) pop | DFS, parsing |
| Queue | O(1) | — | O(1) enqueue | O(1) dequeue | BFS, scheduling |
| Hash Table | — | O(1)* | O(1)* | O(1)* | Fast lookup |
| BST (balanced) | — | O(log n) | O(log n) | O(log n) | Ordered operations |
| Heap | — | O(n) min | O(log n) | O(log n) | Top K, priority |

---

## 00C.4 Recursion — The Pattern Every Tree Problem Uses

Recursion = solve a problem by solving **smaller versions** of the same problem.

### The two mandatory parts

```java
int factorial(int n) {
    if (n <= 1) return 1;           // 1. BASE CASE — stop recursion
    return n * factorial(n - 1);    // 2. RECURSIVE CASE — smaller input
}
```

**Trace for factorial(4):**
```
factorial(4) = 4 * factorial(3)
             = 4 * 3 * factorial(2)
             = 4 * 3 * 2 * factorial(1)
             = 4 * 3 * 2 * 1
             = 24
```

### When recursion fails

| Problem | Symptom | Fix |
|---------|---------|-----|
| Missing base case | StackOverflowError | Add termination condition |
| Not progressing toward base | Infinite recursion | Ensure input shrinks each call |
| Redundant work | Timeout on Fibonacci | Memoization (top-down DP) |

### Fibonacci — naive vs memoized

```java
// O(2^n) — recalculates fib(2), fib(3) many times
int fibNaive(int n) {
    if (n <= 1) return n;
    return fibNaive(n-1) + fibNaive(n-2);
}

// O(n) — each subproblem solved once
int fibMemo(int n, Map<Integer, Integer> memo) {
    if (n <= 1) return n;
    if (memo.containsKey(n)) return memo.get(n);
    int result = fibMemo(n-1, memo) + fibMemo(n-2, memo);
    memo.put(n, result);
    return result;
}
```

**When to use recursion**: Tree/graph traversal, divide-and-conquer (merge sort), backtracking (N-Queens), linked list problems.

---

## 00C.5 Sorting Algorithms — What Interviewers Expect

You rarely implement sort from scratch in product interviews, but you must **compare algorithms** and know what Java uses.

| Algorithm | Time (avg) | Time (worst) | Space | Stable? | Notes |
|-----------|------------|--------------|-------|---------|-------|
| Bubble Sort | O(n²) | O(n²) | O(1) | Yes | Teaching only |
| Insertion Sort | O(n²) | O(n²) | O(1) | Yes | Good for nearly sorted small arrays |
| Merge Sort | O(n log n) | O(n log n) | O(n) | Yes | Divide-and-conquer; used in TimSort |
| Quick Sort | O(n log n) | O(n²) | O(log n) stack | No | Fast in practice; dual-pivot in Java primitives |
| Heap Sort | O(n log n) | O(n log n) | O(1) | No | Guaranteed O(n log n) worst case |
| Counting Sort | O(n + k) | O(n + k) | O(k) | Yes | When range k is small |

**Stable sort**: Equal elements keep their original relative order. Matters when sorting by multiple keys (e.g., sort by name, then by age).

**Java defaults**:
- `Arrays.sort(int[])` → dual-pivot quicksort (primitives)
- `Arrays.sort(Object[])` → TimSort (merge + insertion hybrid, stable)

**Interview question**: "When is O(n²) acceptable?" → Small n (< 20), nearly sorted data, or as a subroutine (insertion sort in TimSort for small runs).

---

## 00C.6 Graph Basics — Nodes, Edges, and Traversal

A **graph** = vertices (nodes) + edges (connections).

| Type | Meaning | Example |
|------|---------|---------|
| Directed | Edges have direction | Twitter follows: A → B |
| Undirected | Edges go both ways | Facebook friendship |
| Weighted | Edges have cost/distance | Road map with km |
| Cyclic | Path returns to start | A → B → C → A |
| Acyclic | No cycles | Tree is acyclic graph |

### BFS — Breadth-First Search

**Use a queue.** Explore all neighbors at distance 1, then distance 2, etc.

```
Graph:  A — B — D
        |   |
        C — E

BFS from A: A → B, C → D, E
```

**Use when**: Shortest path in **unweighted** graph, level-order tree traversal, finding minimum steps.

### DFS — Depth-First Search

**Use a stack or recursion.** Go deep along one path before backtracking.

```
DFS from A: A → B → D → (back) → E → (back) → C
```

**Use when**: Detecting cycles, connected components, topological sort, path existence.

### Dijkstra's Algorithm

Shortest path in **weighted** graph with **non-negative** weights. Uses a min-heap (priority queue).

**Not for**: Negative edge weights → use Bellman-Ford instead.

### Representation

| Method | Space | Best for |
|--------|-------|----------|
| Adjacency list | O(V + E) | Sparse graphs (most real-world) |
| Adjacency matrix | O(V²) | Dense graphs, O(1) edge lookup |

---

## 00C.7 Dynamic Programming — The Two Conditions

DP optimizes recursion by **storing subproblem answers** instead of recomputing.

### Condition 1: Overlapping subproblems

Same subproblem solved multiple times (Fibonacci, coin change).

### Condition 2: Optimal substructure

Optimal solution to the problem contains optimal solutions to subproblems.

```
Coin change: amount 11, coins [1, 5, 6]
  Optimal: 5 + 6 = 11 (2 coins)
  Subproblem: amount 6 optimally solved with one coin [6]
```

### Top-down vs bottom-up

| Approach | Style | Pros |
|----------|-------|------|
| Top-down (memoization) | Recursion + cache | Natural to write; only computes needed subproblems |
| Bottom-up (tabulation) | Iterative table | No stack overflow; often better cache locality |

### Classic DP families (see [02_DSA/13_Dynamic_Programming](../02_DSA/13_Dynamic_Programming/))

| Pattern | Examples |
|---------|----------|
| 1D DP | Climbing stairs, house robber, coin change |
| 2D DP | Unique paths, LCS, edit distance |
| Knapsack | 0/1 knapsack, subset sum |
| Interval DP | Burst balloons, matrix chain multiplication |

**How to recognize DP in interviews**: "Count ways", "minimum/maximum", "can you reach", "longest subsequence" + overlapping subproblems.

---

## 00C.8 Master Theorem (Divide and Conquer)

For recurrences of the form `T(n) = aT(n/b) + O(n^d)`:

Used to analyze merge sort, binary search on answer, and many divide-and-conquer algorithms.

**Example**: Merge sort → `T(n) = 2T(n/2) + O(n)` → **O(n log n)**

Deep dive: [MasterTheorem](../02_DSA/07_Recursion_Divide_Conquer/MasterTheorem/MasterTheorem.md)

---

## 00C.9 Interview Quick Reference (with full answers)

| Question | Full answer |
|----------|-------------|
| **Array vs LinkedList?** | Array: O(1) random access, cache-friendly, fixed or amortized resize cost. LinkedList: O(1) insert/delete at known node, O(n) access. In practice, ArrayList dominates unless you need frequent middle insertions. |
| **HashMap average lookup?** | O(1) average via hashing; O(n) worst case with collisions. Java 8+ converts long buckets to balanced trees. |
| **BFS vs DFS?** | BFS: queue, level-by-level, shortest path unweighted. DFS: stack/recursion, deep exploration, cycle detection, topological sort. |
| **When to use DP?** | Overlapping subproblems + optimal substructure. Try recursion first, then add memo or table. |
| **Stable sort?** | Equal elements keep original order. Merge sort yes; quick sort no. Matters for multi-key sorting. |
| **O(n log n) lower bound for comparison sort?** | Yes — cannot sort n elements with only comparisons faster than O(n log n) in the general case. |

---

## Practice problems by section

| Section | Start here |
|---------|------------|
| Big-O / arrays | [Two Sum](../02_DSA/01_Arrays_Matrix/TwoSum/TwoSum.md) |
| Binary search | [Binary Search](../02_DSA/03_Sorting_Searching/BinarySearch/BinarySearch.md) |
| Recursion / trees | [Invert Binary Tree](../02_DSA/08_Trees_Binary_Trees/InvertBinaryTree/InvertBinaryTree.md) |
| BFS / DFS | [Number of Islands](../02_DSA/11_Graphs/NumberOfIslands/NumberOfIslands.md) |
| DP intro | [Climbing Stairs](../02_DSA/13_Dynamic_Programming/ClimbingStairs/ClimbingStairs.md) |

**Next step**: [00_Java_OOP_Fundamentals.md](00_Java_OOP_Fundamentals.md) → [03_CodingPatterns/02_AlgorithmicPatterns.md](../03_CodingPatterns/02_AlgorithmicPatterns.md) → [02_DSA/StudyGuide.md](../02_DSA/StudyGuide.md).
