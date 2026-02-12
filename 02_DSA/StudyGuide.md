# DSA Study Guide — From Beginner to Lead Engineer

> **This is your single reference for HOW to study DSA**, covering learning paths, difficulty progression, and study methods for every career level.

---

## Career Level → DSA Expectations

| Level | What They Expect | Target Problems | Time Per Problem |
|-------|-----------------|-----------------|------------------|
| **Associate / Junior** | Solve Easy problems, explain brute force | 80+ Easy | No strict limit |
| **Mid-Level SDE** | Solve Medium problems optimally, name patterns | 100+ Medium | 30-40 min |
| **Senior SDE** | Solve Hard problems, discuss trade-offs, handle follow-ups | 50+ Hard | 35-45 min |
| **Lead / Staff** | Optimal solution, clean code, system design connections, teach | All difficulty levels | 25-35 min |

---

## How to Choose Your Path

### Self-Assessment Questions

Before choosing a path, honestly evaluate yourself:

1. **Programming Experience**
   - Can you write basic programs in Java?
   - Do you understand variables, loops, conditionals, and functions?
   - Have you worked with arrays and basic data structures?

2. **Mathematical Foundation**
   - Are you comfortable with basic math operations?
   - Do you understand logarithms and exponentials?
   - Can you analyze simple mathematical relationships?

3. **Problem-Solving Experience**
   - Have you solved coding problems on LeetCode/HackerRank?
   - Can you break down complex problems into smaller parts?
   - Can you analyze time and space complexity?

---

## Path 1: Associate / Junior Developer (0-1 year experience)

**Duration**: 16-20 weeks | **Time**: 10-15 hours/week | **Goal**: Crack your first SDE job

### Phase 1: Programming Fundamentals (Weeks 1-4)
**Goals**: Build a strong programming foundation in Java

- [ ] **Week 1-2**: Master basic syntax and control structures
  - Practice writing simple programs
  - Understand input/output operations
  - Master loops (for, while) and conditionals (if-else)
  - Learn Java Collections basics (ArrayList, HashMap, HashSet)

- [ ] **Week 3-4**: Functions, OOP basics, and simple problem solving
  - Write reusable functions
  - Understand classes, objects, inheritance basics
  - Solve 20-30 basic programming problems on LeetCode (Easy)

### Phase 2: Core Data Structures (Weeks 5-8)
**Goals**: Understand how data is organized

- [ ] **Week 5**: Arrays and Strings
  - `01_Arrays_Matrix/TwoSum/` — Hash table basics
  - `01_Arrays_Matrix/MoveZeroes/` — Two-pointer basics
  - `02_Strings/ReverseString/` — String manipulation
  - `02_Strings/ValidPalindrome/` — Character filtering

- [ ] **Week 6**: More Arrays and Strings
  - `01_Arrays_Matrix/RotateArray/` — Array manipulation
  - `01_Arrays_Matrix/MaximumSubarray/` — Kadane's Algorithm intro
  - `02_Strings/ValidAnagram/` — Frequency counting
  - `02_Strings/GroupAnagrams/` — HashMap with complex keys

- [ ] **Week 7**: Linked Lists
  - `05_Linked_Lists/ReverseLinkedList/` — Pointer manipulation basics
  - `05_Linked_Lists/MergeTwoSortedLists/` — Dummy node technique
  - `05_Linked_Lists/LinkedListCycle/` — Floyd's cycle detection

- [ ] **Week 8**: Stacks and Queues
  - `06_Stacks_Queues/ValidParentheses/` — Stack basics
  - `06_Stacks_Queues/ImplementQueueUsingStacks/` — LIFO/FIFO conversion
  - `06_Stacks_Queues/MinStack/` — Auxiliary stack pattern

### Phase 3: Basic Algorithms (Weeks 9-12)
**Goals**: Learn fundamental algorithmic thinking

- [ ] **Week 9**: Searching and Sorting
  - `03_Sorting_Searching/BinarySearch/` — Divide search space
  - `03_Sorting_Searching/MergeSort/` — Divide and conquer
  - Understanding O(n log n) vs O(n²)

- [ ] **Week 10**: Trees (Basics)
  - `08_Trees_Binary_Trees/MaximumDepth/` — Recursion on trees
  - `08_Trees_Binary_Trees/InvertBinaryTree/` — Tree transformation
  - `08_Trees_Binary_Trees/SymmetricTree/` — Mirror validation

- [ ] **Week 11**: Recursion and Backtracking (Intro)
  - `12_Backtracking/Subsets/` — Generate all subsets
  - `12_Backtracking/Permutations/` — Generate permutations
  - Understanding recursive call stack

- [ ] **Week 12**: Basic Sliding Window and Two Pointers
  - `02_Strings/LongestSubstringWithoutRepeating/` — Sliding window
  - `04_Sliding_Window_Two_Pointers/RemoveDuplicatesFromSortedArray/` — Two pointers

### Phase 4: Problem Patterns (Weeks 13-16)
**Goals**: Recognize common patterns and solve independently

- [ ] **Week 13**: Dynamic Programming (Intro)
  - `13_Dynamic_Programming/ClimbingStairs/` — Fibonacci-like DP
  - `13_Dynamic_Programming/HouseRobber/` — Include/exclude DP
  - Understanding memoization vs tabulation

- [ ] **Week 14**: Binary Search Tree
  - `09_Binary_Search_Tree/BSTOperations/` — Insert, Search, Delete
  - `09_Binary_Search_Tree/LowestCommonAncestorBST/` — BST property usage
  - `09_Binary_Search_Tree/BalancedBSTCheck/` — Height checking

- [ ] **Week 15**: Bit Manipulation and Math
  - `15_Bit_Manipulation/SingleNumber/` — XOR trick
  - `15_Bit_Manipulation/NumberOf1Bits/` — Bit counting
  - `15_Bit_Manipulation/PowerOfTwo/` — n & (n-1) trick

- [ ] **Week 16**: Review and Mixed Practice
  - Solve mixed problems from different categories
  - Focus on explaining your approach out loud
  - Practice writing code without looking at solutions

### Success Metrics
- [ ] Can solve 80% of Easy problems independently
- [ ] Understands time and space complexity basics (Big O)
- [ ] Can explain solution approach clearly
- [ ] Has solved at least 80-100 problems across different topics

---

## Path 2: Mid-Level SDE (1-3 years experience)

**Duration**: 12-16 weeks | **Time**: 12-18 hours/week | **Goal**: Land SDE-1/SDE-2 at a top company

### Phase 1: Strengthen Foundations (Weeks 1-4)
**Goals**: Fill knowledge gaps and build confidence with Medium problems

- [ ] **Week 1**: Advanced Array Techniques
  - `01_Arrays_Matrix/ThreeSum/` — Two-pointer with sorting
  - `01_Arrays_Matrix/ContainerWithMostWater/` — Greedy two-pointer
  - `01_Arrays_Matrix/ProductOfArrayExceptSelf/` — Prefix/suffix technique
  - `01_Arrays_Matrix/BestTimeToBuyAndSellStock/` — Single pass optimization

- [ ] **Week 2**: String Algorithms
  - `02_Strings/LongestPalindromicSubstring/` — Expand around center
  - `02_Strings/MinimumWindowSubstring/` — Complex sliding window
  - `02_Strings/ImplementStrStr/` — Pattern matching intro
  - `02_Strings/IntegerToRoman/` — Mapping problems

- [ ] **Week 3**: Linked List Mastery
  - `05_Linked_Lists/ReorderList/` — Multi-step transformation
  - `05_Linked_Lists/AddTwoNumbers/` — Digit arithmetic
  - `05_Linked_Lists/RemoveNthNodeFromEnd/` — Two-pointer with gap
  - `05_Linked_Lists/LRUCache/` — DoublyLinkedList + HashMap design

- [ ] **Week 4**: Stack Applications
  - `06_Stacks_Queues/DailyTemperatures/` — Monotonic stack
  - `06_Stacks_Queues/EvaluateReversePolishNotation/` — Stack evaluation
  - `06_Stacks_Queues/NextGreaterElement/` — Monotonic stack pattern

### Phase 2: Trees and Graphs (Weeks 5-8)
**Goals**: Master hierarchical and connected data structures

- [ ] **Week 5**: Tree Traversal and Construction
  - `08_Trees_Binary_Trees/BinaryTreeLevelOrderTraversal/` — BFS on trees
  - `08_Trees_Binary_Trees/ValidateBinarySearchTree/` — Range validation
  - `08_Trees_Binary_Trees/ConstructTreeFromTraversals/` — Reconstruction
  - `08_Trees_Binary_Trees/SerializeDeserializeBinaryTree/` — Encoding/Decoding

- [ ] **Week 6**: BST Operations + Heaps
  - `09_Binary_Search_Tree/KthSmallestElementBST/` — Inorder traversal
  - `10_Heaps_Priority_Queues/TopKFrequentElements/` — Heap + HashMap
  - `10_Heaps_Priority_Queues/KthLargestElementInArray/` — QuickSelect
  - `10_Heaps_Priority_Queues/FindMedianFromDataStream/` — Two-heap pattern

- [ ] **Week 7**: Graph Basics
  - `11_Graphs/NumberOfIslands/` — Grid as graph, DFS/BFS
  - `11_Graphs/CloneGraph/` — DFS with visited map
  - `11_Graphs/CourseSchedule/` — Topological sort, cycle detection

- [ ] **Week 8**: Advanced Graphs
  - `11_Graphs/WordLadder/` — BFS for shortest path
  - `11_Graphs/TopologicalSort/` — Kahn's algorithm
  - `11_Graphs/UnionFind/` — Disjoint set with path compression

### Phase 3: Advanced Techniques (Weeks 9-12)
**Goals**: Master optimization and advanced patterns

- [ ] **Week 9**: Dynamic Programming
  - `13_Dynamic_Programming/CoinChange/` — Unbounded knapsack
  - `13_Dynamic_Programming/LongestCommonSubsequence/` — 2D DP
  - `13_Dynamic_Programming/UniquePaths/` — Grid DP
  - `13_Dynamic_Programming/WordBreak/` — String DP

- [ ] **Week 10**: More DP + Backtracking
  - `13_Dynamic_Programming/LongestIncreasingSubsequence/` — DP + Binary Search
  - `13_Dynamic_Programming/PalindromicSubstrings/` — Palindrome DP
  - `12_Backtracking/GenerateParentheses/` — Constrained generation
  - `12_Backtracking/NQueens/` — Complex constraint satisfaction

- [ ] **Week 11**: Greedy + Sliding Window
  - `14_Greedy_Algorithms/JumpGame/` — Greedy choice
  - `14_Greedy_Algorithms/GasStation/` — Circular greedy
  - `14_Greedy_Algorithms/PartitionLabels/` — Interval greedy
  - `04_Sliding_Window_Two_Pointers/LongestSubstringKDistinct/` — Variable window

- [ ] **Week 12**: Sorting + Advanced Search
  - `03_Sorting_Searching/QuickSort/` — Partition algorithm
  - `01_Arrays_Matrix/MergeIntervals/` — Interval sorting
  - `01_Arrays_Matrix/SearchInRotatedSortedArray/` — Modified binary search
  - `01_Arrays_Matrix/FindMinimumInRotatedSortedArray/` — Rotated array binary search

### Phase 4: Hard Problems + Integration (Weeks 13-16)
**Goals**: Tackle Hard problems and connect concepts

- [ ] **Week 13**: Hard Array/String Problems
  - `01_Arrays_Matrix/TrappingRainWater/` — Multiple approaches
  - `01_Arrays_Matrix/FourSum/` — Generalized multi-pointer
  - `04_Sliding_Window_Two_Pointers/SlidingWindowMaximum/` — Monotonic deque

- [ ] **Week 14**: Advanced Data Structures
  - `17_Advanced_Miscellaneous/Trie/` — Prefix tree
  - `17_Advanced_Miscellaneous/SegmentTree/` — Range queries
  - `15_Bit_Manipulation/CountingBits/` — DP on bits
  - `15_Bit_Manipulation/ReverseBits/` — Bit manipulation

- [ ] **Week 15**: Merge K + Mixed Hard
  - `10_Heaps_Priority_Queues/MergeKSortedLists/` — K-way merge
  - `12_Backtracking/SudokuSolver/` — Complex backtracking
  - `08_Trees_Binary_Trees/SubtreeOfAnotherTree/` — Tree comparison

- [ ] **Week 16**: Timed Mock Practice
  - Solve 2-3 random Medium/Hard problems in 45 minutes each
  - Practice explaining trade-offs and alternative approaches
  - Simulate real interview conditions

### Success Metrics
- [ ] Can solve 90% of Easy and 70% of Medium problems
- [ ] Can identify patterns within 2-3 minutes of reading a problem
- [ ] Can analyze time and space complexity accurately
- [ ] Has solved at least 200 problems across all topics

---

## Path 3: Senior / Lead Engineer (2+ years experience)

**Duration**: 8-12 weeks | **Time**: 15-20 hours/week | **Goal**: Crack SDE-2/Lead at FAANG

### Phase 1: Advanced Data Structures (Weeks 1-3)
**Goals**: Master sophisticated data structures

- [ ] **Week 1**: Union Find and Advanced Trees
  - `11_Graphs/UnionFind/` — Path compression + rank
  - `17_Advanced_Miscellaneous/SegmentTree/` — Range queries with lazy propagation
  - `11_Graphs/DijkstraAlgorithm/` — Weighted shortest path

- [ ] **Week 2**: Trie Applications + Advanced Graphs
  - `17_Advanced_Miscellaneous/Trie/` — Prefix tree with XOR applications
  - `07_Recursion_Divide_Conquer/InversionCount/` — Merge sort application
  - `07_Recursion_Divide_Conquer/MasterTheorem/` — Complexity analysis

- [ ] **Week 3**: Mathematical Algorithms
  - `16_Math_Algorithms/NumberTheory/` — GCD, primes, modular arithmetic
  - `04_Sliding_Window_Two_Pointers/AdvancedSlidingWindow/` — Complex variable windows
  - `04_Sliding_Window_Two_Pointers/MinimumSizeSubarraySum/` — Subarray window

### Phase 2: Advanced DP + Complex Problems (Weeks 4-6)
**Goals**: Master advanced algorithmic techniques

- [ ] **Week 4**: Advanced Dynamic Programming
  - `13_Dynamic_Programming/AdvancedDP/` — Interval DP, Bitmask DP, Tree DP
  - Digit DP and Probability DP patterns
  - State space optimization techniques

- [ ] **Week 5**: Hard Graph + Hard DP Combination
  - Solve 3-4 Hard graph problems per day (LeetCode Hard)
  - Focus on problems that combine multiple patterns
  - Practice explaining approach BEFORE coding

- [ ] **Week 6**: System-Design-Connected Algorithms
  - Consistent Hashing implementation
  - LRU/LFU Cache design from scratch
  - Rate Limiter (Token Bucket) implementation
  - How algorithms connect to distributed systems (see `CrossReferences.md`)

### Phase 3: Interview Simulation (Weeks 7-8)
**Goals**: Interview-ready polish

- [ ] **Week 7**: Timed Mock Interviews
  - 2 problems per session, 45 minutes each
  - Random category selection
  - Practice speaking while coding
  - Handle follow-up variations ("What if the data doesn't fit in memory?")

- [ ] **Week 8**: Weak Area Deep Dive + Final Review
  - Identify weakest categories and drill them
  - Review all algorithmic patterns (see `03_CodingPatterns/02_AlgorithmicPatterns.md`)
  - Revisit top 20 most-asked problems by company
  - Final timed assessment: 4 problems in 3 hours

### Success Metrics
- [ ] Can solve 95% of Easy, 85% of Medium, and 60% of Hard problems
- [ ] Can design optimal solutions and explain them clearly in under 35 minutes
- [ ] Can connect algorithmic choices to system design trade-offs
- [ ] Can handle any follow-up question with alternative approaches

---

## Category-by-Category Difficulty Progression

Use this section when you want to master a specific category from beginner to advanced.

### Arrays & Matrix — Progression

| Level | Problem | Key Learning | Pattern |
|-------|---------|-------------|---------|
| Easy | TwoSum | Hash table for O(1) lookup | Key-value mapping |
| Easy | MoveZeroes | Two-pointer in-place modification | Partition |
| Easy | RotateArray | Reverse technique, modular arithmetic | Cyclic rotation |
| Medium | MaximumSubarray | Kadane's Algorithm | Local vs global optimum |
| Medium | ProductOfArrayExceptSelf | Prefix/Suffix products | Two-pass technique |
| Medium | ThreeSum | Sort + Two-pointer | Reduce N-pointer to 2-pointer |
| Medium | BestTimeToBuyAndSellStock | Single pass tracking | Min-so-far pattern |
| Medium | MergeIntervals | Sort + linear sweep | Interval merging |
| Medium | SetMatrixZeroes | In-place markers | First row/col as flags |
| Medium | SpiralMatrix | Boundary tracking | Layer-by-layer traversal |
| Medium | SearchInRotatedSortedArray | Modified binary search | Identify sorted half |
| Hard | ContainerWithMostWater | Greedy two-pointer | Maximization under constraint |
| Hard | InsertInterval | Binary search insert + merge | Interval manipulation |
| Hard | FourSum | Generalized multi-pointer | k-Sum generalization |
| Hard | TrappingRainWater | DP / Two-pointer / Stack | Multiple approaches |

### Strings — Progression

| Level | Problem | Key Learning | Pattern |
|-------|---------|-------------|---------|
| Easy | ReverseString | Two-pointer swap | In-place modification |
| Easy | ValidPalindrome | Character filtering | Two-pointer validation |
| Easy | ValidAnagram | Frequency counting | HashMap counting |
| Medium | GroupAnagrams | Hash table with sorted key | Grouping by characteristic |
| Medium | LongestSubstringWithoutRepeating | Sliding window | Window expansion/contraction |
| Medium | ImplementStrStr | Pattern matching | KMP / Rabin-Karp intro |
| Medium | IntegerToRoman | Mapping/greedy | Value-to-symbol mapping |
| Hard | LongestPalindromicSubstring | Expand around center | Center-based expansion |
| Hard | MinimumWindowSubstring | Complex sliding window | Multi-constraint window |

### Linked Lists — Progression

| Level | Problem | Key Learning | Pattern |
|-------|---------|-------------|---------|
| Easy | ReverseLinkedList | prev/curr/next pointers | Iterative vs recursive |
| Easy | MergeTwoSortedLists | Dummy node technique | Two-pointer merge |
| Medium | LinkedListCycle | Floyd's tortoise and hare | Fast/slow pointers |
| Medium | AddTwoNumbers | Digit-by-digit arithmetic | Carry handling |
| Medium | RemoveNthNodeFromEnd | Two-pointer with gap | Lookahead technique |
| Medium | FindMiddleNode | Fast/slow pointers | Half-speed traversal |
| Hard | ReorderList | Find middle + reverse + merge | Multi-step transformation |
| Hard | LRUCache | DoublyLinkedList + HashMap | O(1) get/put design |

### Trees — Progression

| Level | Problem | Key Learning | Pattern |
|-------|---------|-------------|---------|
| Easy | MaximumDepth | DFS with return value | Recursive tree traversal |
| Easy | InvertBinaryTree | Recursive transformation | Bottom-up/top-down |
| Medium | SymmetricTree | Mirror validation | Simultaneous traversal |
| Medium | ValidateBinarySearchTree | Range validation | Constraint passing |
| Medium | BinaryTreeLevelOrderTraversal | BFS with queue | Level-by-level processing |
| Hard | ConstructTreeFromTraversals | Array partitioning | Divide and conquer |
| Hard | SerializeDeserializeBinaryTree | Encoding/Decoding | String + tree structure |

### Graphs — Progression

| Level | Problem | Key Learning | Pattern |
|-------|---------|-------------|---------|
| Medium | NumberOfIslands | Grid as graph, DFS/BFS | Connected components |
| Medium | CloneGraph | DFS with state tracking | Graph traversal + creation |
| Medium | CourseSchedule | Topological sort, cycle detection | Dependency resolution |
| Hard | WordLadder | BFS for shortest path | Unweighted shortest path |
| Hard | TopologicalSort | Kahn's algorithm | Indegree tracking |
| Hard | UnionFind | Path compression, rank | Dynamic connectivity |
| Hard | DijkstraAlgorithm | Priority queue, relaxation | Weighted shortest path |

### Dynamic Programming — Progression

| Level | Problem | Key Learning | Pattern |
|-------|---------|-------------|---------|
| Easy | ClimbingStairs | Memoization vs tabulation | Fibonacci-like |
| Easy | HouseRobber | Include/exclude decision | 1D DP |
| Medium | CoinChange | Unbounded knapsack | Min ways to target |
| Medium | UniquePaths | Grid-based DP | 2D counting |
| Medium | JumpGame | Greedy/DP hybrid | Reachability |
| Medium | LongestCommonSubsequence | 2D string DP | Sequence alignment |
| Hard | LongestIncreasingSubsequence | DP + Binary Search | Patience sorting |
| Hard | PalindromicSubstrings | Expand around center | Palindrome counting |
| Hard | WordBreak | String DP with dictionary | Substring validation |
| Expert | AdvancedDP (Interval, Bitmask, Tree, Digit, Probability) | Multi-dimensional DP | State compression |

---

## Complexity Cheat Sheet

| Data Structure | Access | Search | Insert | Delete |
|---------------|--------|--------|--------|--------|
| Array | O(1) | O(n) | O(n) | O(n) |
| HashMap | O(1)* | O(1)* | O(1)* | O(1)* |
| LinkedList | O(n) | O(n) | O(1) | O(1) |
| Stack/Queue | O(n) | O(n) | O(1) | O(1) |
| BST (balanced) | O(log n) | O(log n) | O(log n) | O(log n) |
| Heap | O(1) top | O(n) | O(log n) | O(log n) |
| Trie | - | O(m) | O(m) | O(m) |
| Segment Tree | - | O(log n) | O(log n) | O(log n) |
| Union Find | - | O(α(n)) | O(α(n)) | - |

\* Amortized, assuming good hash function

| Algorithm | Best | Average | Worst | Space |
|-----------|------|---------|-------|-------|
| Binary Search | O(1) | O(log n) | O(log n) | O(1) |
| Merge Sort | O(n log n) | O(n log n) | O(n log n) | O(n) |
| Quick Sort | O(n log n) | O(n log n) | O(n²) | O(log n) |
| BFS/DFS | O(V+E) | O(V+E) | O(V+E) | O(V) |
| Dijkstra | O((V+E)log V) | O((V+E)log V) | O((V+E)log V) | O(V) |
| Kadane's | O(n) | O(n) | O(n) | O(1) |

---

## Study Methods That Work

### Spaced Repetition
Review solved problems after: **1 day → 3 days → 1 week → 2 weeks → 1 month**

### Active Recall
Solve problems WITHOUT looking at previous solutions. If stuck for 20 minutes, peek at the approach (not code), then try again.

### The Feynman Technique
After solving a problem, explain it as if teaching a junior developer. If you cannot explain it simply, you do not understand it well enough.

### Pattern-First Approach
Before jumping into problems, study the 16 algorithmic patterns in `03_CodingPatterns/02_AlgorithmicPatterns.md`. Then when you see a new problem, your first thought should be: "Which pattern does this match?"

### Daily Practice Routine

| Time | Activity | Duration |
|------|----------|----------|
| Morning | Solve 1 Medium problem (timed, 35 min) | 40 min |
| Lunch | Review yesterday's problem without code | 10 min |
| Evening | Study one new pattern or data structure concept | 30 min |
| Weekend | Solve 1 Hard problem (untimed) + mock interview | 90 min |

---

## Progress Tracking Template

```
Week ___ Progress Report
========================
Date Range: ___ to ___

Problems Solved This Week:
- [ ] Problem 1 (Category, Difficulty): [Time taken, Pattern used]
- [ ] Problem 2 (Category, Difficulty): [Time taken, Pattern used]
- [ ] Problem 3 (Category, Difficulty): [Time taken, Pattern used]

Patterns Practiced:
- [ ] Pattern 1: [Comfort level 1-5]
- [ ] Pattern 2: [Comfort level 1-5]

Weakest Areas:
- Area 1: [Action plan for next week]
- Area 2: [Action plan for next week]

Key Learnings:
- Insight 1
- Insight 2

Self-Assessment:
- Pattern Recognition Speed: ___/5
- Code Quality: ___/5
- Communication While Coding: ___/5
- Edge Case Handling: ___/5
```

---

**Start with the path that matches your current level. Consistency beats intensity — 1 hour daily is better than 7 hours on Sunday.**

