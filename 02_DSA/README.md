# Data Structures & Algorithms — Lead Engineer Interview Prep

> **Target**: SDE-2 / Lead Software Engineer at FAANG-level companies
> **Strategy**: Pattern recognition over brute-force memorization. Solve by category, then mix.
> **What interviewers expect at this level**: Clean code, optimal complexity, clear communication of trade-offs, and the ability to handle follow-up questions without hesitation.

---

## How This Section is Organized

Every problem has two files:

| File | Purpose |
|------|---------|
| `ProblemName.md` | Complete explanation: problem statement, multiple approaches, complexity analysis, interview tips, edge cases, real-world applications |
| `ProblemName.java` | Clean, production-quality Java implementation with comments |

Problems are grouped by data structure/algorithm category. Within each category, they progress from foundational to advanced.

---

## Repository Structure

```
02_DSA/
├── README.md                        # This file — DSA overview and problem index
├── StudyGuide.md                    # Learning paths by career level + difficulty progression
│
├── 01_Arrays_Matrix/                # 16 problems — Two pointers, prefix sums, binary search
├── 02_Strings/                      # 9 problems — Sliding window, hashing, pattern matching
├── 03_Sorting_Searching/            # 3 problems — Merge sort, quick sort, binary search
├── 04_Sliding_Window_Two_Pointers/  # 5+ problems — Variable window, deque, complex constraints
├── 05_Linked_Lists/                 # 8 problems — Pointer manipulation, fast/slow, reversal
├── 06_Stacks_Queues/                # 6 problems — Monotonic stack, simulation
├── 07_Recursion_Divide_Conquer/     # Master Theorem + classic divide-and-conquer
├── 08_Trees_Binary_Trees/           # 8 problems — DFS, BFS, construction, serialization
├── 09_Binary_Search_Tree/           # 4 problems — BST properties, validation, search
├── 10_Heaps_Priority_Queues/        # 4 problems — Top K, merge K, median finding
├── 11_Graphs/                       # 7 problems — BFS, DFS, Dijkstra, Union Find, Topo Sort
├── 12_Backtracking/                 # 5 problems — Subsets, permutations, constraint satisfaction
├── 13_Dynamic_Programming/          # 10+ problems — Knapsack, LCS, LIS, grid, string, interval
├── 14_Greedy_Algorithms/            # 4 problems — Jump game, gas station, task scheduler
├── 15_Bit_Manipulation/             # 5 problems — Single number, bit counting, power of two
├── 16_Math_Algorithms/              # Number theory, primes, modular arithmetic, combinatorics
├── 17_Advanced_Miscellaneous/       # Segment Tree, Trie (with XOR), design problems
└── 18_Concurrency_Multithreading/   # Print In Order, Producer-Consumer, Read-Write Lock
```

---

## What a Lead Engineer Must Know (Beyond Solving)

At the Lead/SDE-2 level, interviewers evaluate more than just "can you solve it." They evaluate:

| Dimension | What They Look For | How to Demonstrate |
|-----------|-------------------|-------------------|
| **Problem Decomposition** | Can you break the problem into subproblems? | Think out loud, define helper functions |
| **Pattern Recognition** | Do you recognize Two Pointers, Sliding Window, DP, etc.? | Name the pattern: "This is a sliding window problem" |
| **Optimal Complexity** | Do you reach optimal time/space? | State complexity before coding, justify trade-offs |
| **Clean Code** | Is your code readable and maintainable? | Descriptive names, small functions, input validation |
| **Edge Cases** | Do you think about edge cases proactively? | Mention them before the interviewer asks |
| **Communication** | Can you explain your thinking clearly? | Structured approach: understand → plan → code → test |
| **Follow-up Handling** | Can you adapt when constraints change? | "If the input is sorted, we can use binary search" |
| **Testing** | Do you verify your solution? | Trace through examples, boundary cases |

---

## The 45-Minute Interview Structure

| Phase | Time | What to Do |
|-------|------|-----------|
| **Understand** | 3-5 min | Repeat the problem, ask clarifying questions, confirm constraints |
| **Plan** | 5-7 min | Identify the pattern, describe the approach, state complexity |
| **Code** | 20-25 min | Write clean code, explain as you go |
| **Test** | 5-7 min | Trace through examples, check edge cases |
| **Follow-up** | 5 min | Handle variations ("What if the input is too large for memory?") |

---

## Category Deep Dive

### 01. Arrays & Matrix (16 Problems)

The most frequently tested category. Every FAANG interview has at least one array problem.

| Problem | Pattern | Difficulty | Must-Know |
|---------|---------|-----------|-----------|
| [Two Sum](01_Arrays_Matrix/TwoSum/TwoSum.md) | Hash Map | Easy | Yes |
| [Three Sum](01_Arrays_Matrix/ThreeSum/ThreeSum.md) | Two Pointers + Sort | Medium | Yes |
| [Four Sum](01_Arrays_Matrix/FourSum/FourSum.md) | K-Sum reduction | Medium | Nice-to-have |
| [Best Time to Buy and Sell Stock](01_Arrays_Matrix/BestTimeToBuyAndSellStock/BestTimeToBuyAndSellStock.md) | Kadane's variant | Easy | Yes |
| [Maximum Subarray](01_Arrays_Matrix/MaximumSubarray/MaximumSubarray.md) | Kadane's Algorithm | Medium | Yes |
| [Product of Array Except Self](01_Arrays_Matrix/ProductOfArrayExceptSelf/ProductOfArrayExceptSelf.md) | Prefix/Suffix pass | Medium | Yes |
| [Container With Most Water](01_Arrays_Matrix/ContainerWithMostWater/ContainerWithMostWater.md) | Two Pointers | Medium | Yes |
| [Trapping Rain Water](01_Arrays_Matrix/TrappingRainWater/TrappingRainWater.md) | Two Pointers / Stack | Hard | Yes |
| [Merge Intervals](01_Arrays_Matrix/MergeIntervals/MergeIntervals.md) | Sort + Merge | Medium | Yes |
| [Insert Interval](01_Arrays_Matrix/InsertInterval/InsertInterval.md) | Merge Intervals | Medium | Yes |
| [Rotate Array](01_Arrays_Matrix/RotateArray/RotateArray.md) | Reverse | Medium | Yes |
| [Move Zeroes](01_Arrays_Matrix/MoveZeroes/MoveZeroes.md) | Two Pointers (Same Dir) | Easy | Warm-up |
| [Search in Rotated Sorted Array](01_Arrays_Matrix/SearchInRotatedSortedArray/SearchInRotatedSortedArray.md) | Modified Binary Search | Medium | Yes |
| [Find Minimum in Rotated Sorted Array](01_Arrays_Matrix/FindMinimumInRotatedSortedArray/FindMinimumInRotatedSortedArray.md) | Binary Search | Medium | Yes |
| [Set Matrix Zeroes](01_Arrays_Matrix/SetMatrixZeroes/SetMatrixZeroes.md) | In-place marking | Medium | Nice-to-have |
| [Spiral Matrix](01_Arrays_Matrix/SpiralMatrix/SpiralMatrix.md) | Simulation | Medium | Nice-to-have |

**Key patterns in this category**: Two Pointers, Sliding Window, Prefix Sum, Binary Search, Kadane's Algorithm, Sort + Merge.

### 02. Strings (9 Problems)

String problems test sliding window, hashing, and pattern matching.

| Problem | Pattern | Difficulty | Must-Know |
|---------|---------|-----------|-----------|
| [Longest Substring Without Repeating](02_Strings/LongestSubstringWithoutRepeating/LongestSubstringWithoutRepeating.md) | Sliding Window | Medium | Yes |
| [Minimum Window Substring](02_Strings/MinimumWindowSubstring/MinimumWindowSubstring.md) | Sliding Window | Hard | Yes |
| [Group Anagrams](02_Strings/GroupAnagrams/GroupAnagrams.md) | Hashing | Medium | Yes |
| [Longest Palindromic Substring](02_Strings/LongestPalindromicSubstring/LongestPalindromicSubstring.md) | Expand Around Center | Medium | Yes |
| [Valid Anagram](02_Strings/ValidAnagram/ValidAnagram.md) | Hashing/Sorting | Easy | Warm-up |
| [Valid Palindrome](02_Strings/ValidPalindrome/ValidPalindrome.md) | Two Pointers | Easy | Warm-up |
| [Reverse String](02_Strings/ReverseString/ReverseString.md) | Two Pointers | Easy | Warm-up |
| [Implement strStr](02_Strings/ImplementStrStr/ImplementStrStr.md) | KMP / Two Pointers | Medium | Nice-to-have |
| [Integer to Roman](02_Strings/IntegerToRoman/IntegerToRoman.md) | Greedy | Medium | Nice-to-have |

### 03. Sorting & Searching

Fundamental algorithms. Understanding these is a prerequisite for many interview problems.

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Merge Sort](03_Sorting_Searching/MergeSort/MergeSort.md) | Divide & Conquer | Medium |
| [Quick Sort](03_Sorting_Searching/QuickSort/QuickSort.md) | Partition | Medium |
| [Binary Search](03_Sorting_Searching/BinarySearch/BinarySearch.md) | Divide & Conquer | Easy |

### 04. Sliding Window & Two Pointers (5+ Problems)

Key technique for subarray/substring problems. Master the variable-window template.

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Longest Substring K Distinct](04_Sliding_Window_Two_Pointers/LongestSubstringKDistinct/LongestSubstringKDistinct.md) | Sliding Window | Medium |
| [Sliding Window Maximum](04_Sliding_Window_Two_Pointers/SlidingWindowMaximum/SlidingWindowMaximum.md) | Monotonic Deque | Hard |
| [Minimum Size Subarray Sum](04_Sliding_Window_Two_Pointers/MinimumSizeSubarraySum/MinimumSizeSubarraySum.md) | Sliding Window | Medium |

### 05. Linked Lists (8 Problems)

Tests pointer manipulation, fast/slow pointers, and in-place reversal.

| Problem | Pattern | Difficulty | Must-Know |
|---------|---------|-----------|-----------|
| [Reverse Linked List](05_Linked_Lists/ReverseLinkedList/ReverseLinkedList.md) | In-Place Reversal | Easy | Yes |
| [Linked List Cycle](05_Linked_Lists/LinkedListCycle/LinkedListCycle.md) | Fast & Slow Pointers | Easy | Yes |
| [Merge Two Sorted Lists](05_Linked_Lists/MergeTwoSortedLists/MergeTwoSortedLists.md) | Two Pointers Merge | Easy | Yes |
| [LRU Cache](05_Linked_Lists/LRUCache/LRUCache.md) | HashMap + DLL | Hard | Yes (design) |
| [Reorder List](05_Linked_Lists/ReorderList/ReorderList.md) | Middle + Reverse + Merge | Medium | Yes |
| [Remove Nth Node From End](05_Linked_Lists/RemoveNthNodeFromEnd/RemoveNthNodeFromEnd.md) | Two Pointers with Gap | Medium | Yes |
| [Add Two Numbers](05_Linked_Lists/AddTwoNumbers/AddTwoNumbers.md) | Simulation | Medium | Yes |
| [Find Middle Node](05_Linked_Lists/FindMiddleNode/FindMiddleNode.md) | Fast & Slow | Easy | Yes |

### 06. Stacks & Queues (6 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Valid Parentheses](06_Stacks_Queues/ValidParentheses/ValidParentheses.md) | Stack | Easy |
| [Min Stack](06_Stacks_Queues/MinStack/MinStack.md) | Two Stacks | Medium |
| [Daily Temperatures](06_Stacks_Queues/DailyTemperatures/DailyTemperatures.md) | Monotonic Stack | Medium |
| [Evaluate Reverse Polish Notation](06_Stacks_Queues/EvaluateReversePolishNotation/EvaluateReversePolishNotation.md) | Stack | Medium |
| [Next Greater Element](06_Stacks_Queues/NextGreaterElement/NextGreaterElement.md) | Monotonic Stack | Medium |
| [Implement Queue Using Stacks](06_Stacks_Queues/ImplementQueueUsingStacks/ImplementQueueUsingStacks.md) | Two Stacks | Easy |

### 07. Recursion & Divide and Conquer

Foundation for Trees, Backtracking, and DP. Master the Master Theorem.

| Topic | Key Concept |
|-------|-------------|
| Master Theorem | Recurrence analysis for divide-and-conquer algorithms |
| [Max Subarray (D&C)](07_Recursion_Divide_Conquer/MaximumSubarrayDivideConquer/MaximumSubarrayDivideConquer.md) | Classic D&C | 
| [Inversion Count](07_Recursion_Divide_Conquer/InversionCount/InversionCount.md) | Modified Merge Sort |

### 08. Trees & Binary Trees (8 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Maximum Depth](08_Trees_Binary_Trees/MaximumDepth/MaximumDepth.md) | DFS | Easy |
| [Invert Binary Tree](08_Trees_Binary_Trees/InvertBinaryTree/InvertBinaryTree.md) | DFS/BFS | Easy |
| [Symmetric Tree](08_Trees_Binary_Trees/SymmetricTree/SymmetricTree.md) | DFS (two pointers) | Easy |
| [Validate BST](08_Trees_Binary_Trees/ValidateBinarySearchTree/ValidateBinarySearchTree.md) | DFS with bounds | Medium |
| [Level Order Traversal](08_Trees_Binary_Trees/BinaryTreeLevelOrderTraversal/BinaryTreeLevelOrderTraversal.md) | BFS | Medium |
| [Subtree of Another Tree](08_Trees_Binary_Trees/SubtreeOfAnotherTree/SubtreeOfAnotherTree.md) | DFS + comparison | Medium |
| [Construct Tree From Traversals](08_Trees_Binary_Trees/ConstructTreeFromTraversals/ConstructTreeFromTraversals.md) | Divide and Conquer | Medium |
| [Serialize/Deserialize Binary Tree](08_Trees_Binary_Trees/SerializeDeserializeBinaryTree/SerializeDeserializeBinaryTree.md) | BFS/DFS | Hard |

### 09. Binary Search Tree (4 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [BST Operations](09_Binary_Search_Tree/BSTOperations/BSTOperations.md) | BST properties | Medium |
| [Lowest Common Ancestor BST](09_Binary_Search_Tree/LowestCommonAncestorBST/LowestCommonAncestorBST.md) | BST properties | Medium |
| [Kth Smallest Element BST](09_Binary_Search_Tree/KthSmallestElementBST/KthSmallestElementBST.md) | Inorder traversal | Medium |
| [Balanced BST Check](09_Binary_Search_Tree/BalancedBSTCheck/BalancedBSTCheck.md) | DFS with height | Easy |

### 10. Heaps & Priority Queues (4 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Top K Frequent Elements](10_Heaps_Priority_Queues/TopKFrequentElements/TopKFrequentElements.md) | Min-Heap / Bucket Sort | Medium |
| [Kth Largest Element](10_Heaps_Priority_Queues/KthLargestElementInArray/KthLargestElementInArray.md) | Quickselect / Min-Heap | Medium |
| [Merge K Sorted Lists](10_Heaps_Priority_Queues/MergeKSortedLists/MergeKSortedLists.md) | K-Way Merge | Hard |
| [Find Median from Data Stream](10_Heaps_Priority_Queues/FindMedianFromDataStream/FindMedianFromDataStream.md) | Two Heaps | Hard |

### 11. Graphs (7 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Number of Islands](11_Graphs/NumberOfIslands/NumberOfIslands.md) | BFS/DFS on Grid | Medium |
| [Clone Graph](11_Graphs/CloneGraph/CloneGraph.md) | DFS + HashMap | Medium |
| [Course Schedule](11_Graphs/CourseSchedule/CourseSchedule.md) | Topological Sort / Cycle Detection | Medium |
| [Topological Sort](11_Graphs/TopologicalSort/TopologicalSort.md) | Kahn's Algorithm / DFS | Medium |
| [Word Ladder](11_Graphs/WordLadder/WordLadder.md) | BFS Shortest Path | Hard |
| [Dijkstra's Algorithm](11_Graphs/DijkstraAlgorithm/DijkstraAlgorithm.md) | Priority Queue + Greedy | Hard |
| [Union Find](11_Graphs/UnionFind/UnionFind.md) | Path Compression + Rank | Medium |

### 12. Backtracking (5 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Subsets](12_Backtracking/Subsets/Subsets.md) | Include/Exclude | Medium |
| [Permutations](12_Backtracking/Permutations/Permutations.md) | Used array | Medium |
| [Generate Parentheses](12_Backtracking/GenerateParentheses/GenerateParentheses.md) | Constraint tracking | Medium |
| [N-Queens](12_Backtracking/NQueens/NQueens.md) | Constraint satisfaction | Hard |
| [Sudoku Solver](12_Backtracking/SudokuSolver/SudokuSolver.md) | Constraint satisfaction | Hard |

### 13. Dynamic Programming (10+ Problems)

| Problem | Sub-Pattern | Difficulty |
|---------|-----------|-----------|
| [Climbing Stairs](13_Dynamic_Programming/ClimbingStairs/ClimbingStairs.md) | Decision DP | Easy |
| [House Robber](13_Dynamic_Programming/HouseRobber/HouseRobber.md) | Decision DP | Medium |
| [Coin Change](13_Dynamic_Programming/CoinChange/CoinChange.md) | Unbounded Knapsack | Medium |
| [Unique Paths](13_Dynamic_Programming/UniquePaths/UniquePaths.md) | Grid DP | Medium |
| [Longest Common Subsequence](13_Dynamic_Programming/LongestCommonSubsequence/LongestCommonSubsequence.md) | LCS | Medium |
| [Longest Increasing Subsequence](13_Dynamic_Programming/LongestIncreasingSubsequence/LongestIncreasingSubsequence.md) | LIS | Medium |
| [Word Break](13_Dynamic_Programming/WordBreak/WordBreak.md) | String DP | Medium |
| [Palindromic Substrings](13_Dynamic_Programming/PalindromicSubstrings/PalindromicSubstrings.md) | Interval DP | Medium |
| [Jump Game](13_Dynamic_Programming/JumpGame/JumpGame.md) | Greedy/DP | Medium |
| [Advanced DP Patterns](13_Dynamic_Programming/AdvancedDP/AdvancedDP.md) | Interval, Bitmask, Tree, Digit, Probability | Hard |

### 14. Greedy Algorithms (4 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Jump Game](14_Greedy_Algorithms/JumpGame/JumpGame.md) | Greedy reachability | Medium |
| [Gas Station](14_Greedy_Algorithms/GasStation/GasStation.md) | Circular greedy | Medium |
| [Partition Labels](14_Greedy_Algorithms/PartitionLabels/PartitionLabels.md) | Two Pointers + Greedy | Medium |
| [Task Scheduler](14_Greedy_Algorithms/TaskScheduler/TaskScheduler.md) | Max Heap + Greedy | Medium |

### 15. Bit Manipulation (5 Problems)

| Problem | Pattern | Difficulty |
|---------|---------|-----------|
| [Single Number](15_Bit_Manipulation/SingleNumber/SingleNumber.md) | XOR | Easy |
| [Counting Bits](15_Bit_Manipulation/CountingBits/CountingBits.md) | DP + Bit | Easy |
| [Reverse Bits](15_Bit_Manipulation/ReverseBits/ReverseBits.md) | Bit Shift | Easy |
| [Power of Two](15_Bit_Manipulation/PowerOfTwo/PowerOfTwo.md) | Bit Tricks | Easy |
| [Number of 1 Bits](15_Bit_Manipulation/NumberOf1Bits/NumberOf1Bits.md) | Brian Kernighan | Easy |

### 16. Math Algorithms

| Topic | Key Concept |
|-------|-------------|
| GCD/LCM | Euclidean algorithm, extended GCD |
| Primes | Sieve of Eratosthenes, prime factorization |
| Modular Arithmetic | Modular exponentiation, Fermat's little theorem |
| Combinatorics | nCr with modular inverse, Pascal's triangle |

### 17. Advanced Data Structures

| Topic | Key Concept |
|-------|-------------|
| Segment Tree | Range queries (sum, min, max), lazy propagation |
| Trie | Prefix search, XOR maximum, autocomplete |

### 18. Concurrency & Multithreading

| Problem | Key Concept | Difficulty |
|---------|-------------|-----------|
| [Print In Order](18_Concurrency_Multithreading/PrintInOrder/PrintInOrder.md) | CountDownLatch, Semaphore | Easy |
| [Producer-Consumer](18_Concurrency_Multithreading/ProducerConsumer/ProducerConsumer.md) | BlockingQueue, wait/notify | Medium |
| [Read-Write Lock](18_Concurrency_Multithreading/ReadWriteLock/ReadWriteLock.md) | ReentrantReadWriteLock | Medium |

---

## Complexity Cheat Sheet

### Time Complexity Ranking (Best → Worst)

| Complexity | Name | Example |
|-----------|------|---------|
| O(1) | Constant | HashMap lookup, array access |
| O(log n) | Logarithmic | Binary search, balanced BST |
| O(n) | Linear | Single pass, two pointers |
| O(n log n) | Linearithmic | Merge sort, heap sort |
| O(n²) | Quadratic | Nested loops, brute force pairs |
| O(2^n) | Exponential | Subsets, recursive backtracking |
| O(n!) | Factorial | Permutations |

### Space Complexity Common Cases

| Structure | Space |
|-----------|-------|
| Hash Map with n entries | O(n) |
| Recursion depth d | O(d) stack frames |
| 2D DP table n×m | O(n×m) → optimize to O(n) with rolling array |
| Adjacency list with V vertices, E edges | O(V + E) |
| Heap of K elements | O(K) |

---

## Interview Preparation Priority

### Tier 1: Must-Solve (Asked in 80%+ of interviews)

These are the problems you must solve in your sleep:

1. Two Sum
2. Best Time to Buy and Sell Stock
3. Maximum Subarray
4. Merge Intervals
5. Longest Substring Without Repeating Characters
6. Reverse Linked List
7. Linked List Cycle
8. Valid Parentheses
9. Binary Tree Level Order Traversal
10. Number of Islands
11. Course Schedule
12. Coin Change
13. LRU Cache
14. Top K Frequent Elements

### Tier 2: Frequently Asked (50-80% of interviews)

15. Three Sum
16. Container With Most Water
17. Product of Array Except Self
18. Group Anagrams
19. Merge Two Sorted Lists
20. Validate BST
21. Serialize/Deserialize Binary Tree
22. Word Ladder
23. Subsets
24. Permutations
25. Climbing Stairs
26. Longest Increasing Subsequence
27. Daily Temperatures

### Tier 3: Differentiators (Asked at Senior+ levels)

28. Trapping Rain Water
29. Minimum Window Substring
30. LRU Cache (with follow-ups)
31. Merge K Sorted Lists
32. Find Median from Data Stream
33. N-Queens
34. Word Break
35. Dijkstra's Algorithm
36. Segment Tree
37. Trie (with XOR variant)

---

## Cross-References

| Need to study... | Go to... |
|-----------------|---------|
| 16 Algorithmic Pattern Templates | [02_AlgorithmicPatterns.md](../03_CodingPatterns/02_AlgorithmicPatterns.md) |
| GoF Design Patterns | [01_Patterns.md](../03_CodingPatterns/01_Patterns.md) |
| System Design (uses DSA concepts) | [CrossReferences.md](../CrossReferences.md) |
| Study Paths + Difficulty Progression | [StudyGuide.md](StudyGuide.md) |
