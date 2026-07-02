# 🧭 Developer Master Roadmap

> **Start here.** This is the canonical entry point for the Developer Master Book repo.
> Companion navigators: [MASTER_INDEX.md](MASTER_INDEX.md) (topic lookup), [README.md](README.md) (repo overview), [00_Interview_Prep/README.md](00_Interview_Prep/README.md) (interview guides), [02_DSA/StudyGuide.md](02_DSA/StudyGuide.md) (DSA schedule).

## How to use this roadmap

1. **Map yourself** using [Where am I?](#where-am-i-map-yourself-on-the-ladder) below — mid-career readers start at their current level, not Fresher.
2. Find your level in **The Ladder** — work that section's checklist only; use prerequisites to fill gaps backward.
3. Work **Core** before Recommended, then Optional. Each topic file is self-contained (no jumping across folders for the same concept).
4. Track progress with [SelfAssessment.md](00_Interview_Prep/Core/SelfAssessment.md) (confidence 1–5 per topic).
5. Pair with [InterviewPlaybook.md](00_Interview_Prep/Core/InterviewPlaybook.md) for scheduling and round protocols.

---

## Where am I? — Map yourself on the ladder

**Audience**: Developers based in **India** preparing for roles you can do **from India** (local office, hybrid, or remote-for-India). Company HQs may be global — see [Companies.md](00_Interview_Prep/Core/Companies.md#hiring-from-india-for-india-based-candidates).

This repo uses **one linear ladder**: Fresher → SDE1 → SDE2 → Senior SDE → Tech Lead → Staff Engineer → Principal. There is no separate "lateral track" — if you have 5 years of experience, you still **pick the level that matches scope you can defend in interviews**, then work that section (and prerequisites you are weak on).

| If you can truthfully say… | Start at | Typical experience |
|----------------------------|----------|-------------------|
| "I need my first dev job" or "I'm in final year" | [Fresher](#fresher) | 0–1 yr |
| "I ship features independently with code review" | [SDE1](#sde1) | 0–2 yr |
| "I own API/DB design and medium DSA in interviews" | [SDE2](#sde2) | 1–4 yr |
| "I design distributed systems and own production NFRs" | [Senior SDE](#senior-sde) | 3–7 yr |
| "I set technical direction for a team + behavioral depth" | [Tech Lead](#tech-lead) | 5–10 yr |
| "I influence multiple teams / platform without people mgr title" | [Staff Engineer](#staff-engineer) | 7–12 yr |
| "I own org-wide architecture, vision, exec narrative" | [Principal](#principal-architect) | 10+ yr |

**How to confirm level**: Run [SelfAssessment.md](00_Interview_Prep/Core/SelfAssessment.md) — compare total score and section averages to each level's "Ready to move on" criteria. When in doubt, prep **one level down** (stronger fundamentals beat title inflation).

---

## The ladder — scope & authority (all levels)

| Level | Scope of ownership | Decision authority | Signals you're ready for next level |
|-------|-------------------|-------------------|-----------------------------------|
| **Fresher** | Guided tasks; learns stack | Implements with review; asks before architectural choices | Path 1 phases 1–2; 80% Easy DSA; Section 0 SelfAssessment ≥ 3/5 |
| **SDE1** | Independent features in one service | Picks implementation inside defined API/schema | Path 1 complete; basic LLD; Sections 0–3 SelfAssessment ≥ 3/5 |
| **SDE2** | End-to-end feature + API/DB design | Owns component design; proposes patterns | Path 2 metrics; SelfAssessment ≥ 95; medium DSA in 30–40 min |
| **Senior SDE** | Production services, NFRs, incidents | Service architecture; trade-offs with staff input | Path 3 metrics; flagship HLD; SelfAssessment ≥ 130 |
| **Tech Lead** | Team roadmap, standards, mentoring | Team technical direction; hiring input | 4+ STAR stories; Section 27 ≥ 4/5; SelfAssessment ≥ 160 |
| **Staff Engineer** | Cross-team platforms, RFCs, standards | Org-wide technical standards; build-vs-buy input | Tier 3 patterns; [Advance Criteria](00_Interview_Prep/Levels/Staff_Principal_Advance_Criteria.md) Staff band |
| **Principal** | Multi-year vision, vendor strategy, DR | Strategic architecture; executive alignment | Advance Criteria Principal band; board narrative ready |

---

## Fresher

**Prerequisite Check:** Comfortable with basic programming (variables, loops, functions). No prior repo content required.

**Ready to move on when:** [SelfAssessment.md](00_Interview_Prep/Core/SelfAssessment.md) Section 0 average ≥ 3/5; [StudyGuide.md](02_DSA/StudyGuide.md) Path 1 success metrics — 80% Easy solved, 80–100 problems, can explain Big-O ([StudyGuide Path 1](02_DSA/StudyGuide.md)).

### Technical Skills
- [ ] `[Core]` [Java OOP Fundamentals](01_TechGuide/00_Java_OOP_Fundamentals.md) — OOP pillars, equals/hashCode, collections intro
- [ ] `[Core]` [Computer Science Fundamentals](01_TechGuide/00_Computer_Science_Fundamentals.md) — Big-O, arrays, recursion, BFS/DFS intuition
- [ ] `[Core]` [Web Fundamentals](01_TechGuide/00_Web_Fundamentals.md) — HTTP methods, status codes, auth basics
- [ ] `[Core]` [SQL Fundamentals](01_TechGuide/35_SQL_Fundamentals.md) — JOINs, WHERE, window functions intro
- [ ] `[Core]` [Git & Version Control](01_TechGuide/33_Git_Version_Control_Workflow.md) — merge vs rebase, PR workflow
- [ ] `[Core]` [Operating Systems & Linux](01_TechGuide/32_Operating_Systems_and_Linux.md) — processes, basic debugging commands
- [ ] `[Recommended]` [TypeScript & Frontend Landscape](01_TechGuide/37_TypeScript_and_Frontend_Landscape.md) — frontend breadth for full-stack awareness
- [ ] `[Core]` [Career Prep — Resume & Portfolio](00_Interview_Prep/Career/Resume_and_Portfolio.md) — first-job resume, GitHub portfolio, application timeline

### DSA (Path 1 — Weeks 5–12)
- [ ] `[Core]` [DSA Study Guide — Path 1](02_DSA/StudyGuide.md) — 16–20 week Fresher curriculum
- [ ] `[Core]` [DSA README](02_DSA/README.md) — problem index and interview rubric
- [ ] `[Core]` [Two Sum](02_DSA/01_Arrays_Matrix/TwoSum/TwoSum.md) — hash map complement search
- [ ] `[Core]` [Move Zeroes](02_DSA/01_Arrays_Matrix/MoveZeroes/MoveZeroes.md) — in-place two-pointer
- [ ] `[Core]` [Rotate Array](02_DSA/01_Arrays_Matrix/RotateArray/RotateArray.md) — reverse trick
- [ ] `[Core]` [Maximum Subarray](02_DSA/01_Arrays_Matrix/MaximumSubarray/MaximumSubarray.md) — Kadane's algorithm
- [ ] `[Core]` [Reverse String](02_DSA/02_Strings/ReverseString/ReverseString.md) — two-pointer basics
- [ ] `[Core]` [Valid Palindrome](02_DSA/02_Strings/ValidPalindrome/ValidPalindrome.md) — character filtering
- [ ] `[Core]` [Valid Anagram](02_DSA/02_Strings/ValidAnagram/ValidAnagram.md) — frequency counting
- [ ] `[Core]` [Group Anagrams](02_DSA/02_Strings/GroupAnagrams/GroupAnagrams.md) — HashMap grouping
- [ ] `[Core]` [Reverse Linked List](02_DSA/05_Linked_Lists/ReverseLinkedList/ReverseLinkedList.md) — pointer rewiring
- [ ] `[Core]` [Merge Two Sorted Lists](02_DSA/05_Linked_Lists/MergeTwoSortedLists/MergeTwoSortedLists.md) — dummy-head merge
- [ ] `[Core]` [Linked List Cycle](02_DSA/05_Linked_Lists/LinkedListCycle/LinkedListCycle.md) — Floyd's cycle detection
- [ ] `[Core]` [Find Middle Node](02_DSA/05_Linked_Lists/FindMiddleNode/FindMiddleNode.md) — fast/slow pointers
- [ ] `[Core]` [Valid Parentheses](02_DSA/06_Stacks_Queues/ValidParentheses/ValidParentheses.md) — stack matching
- [ ] `[Core]` [Implement Queue Using Stacks](02_DSA/06_Stacks_Queues/ImplementQueueUsingStacks/ImplementQueueUsingStacks.md) — LIFO/FIFO conversion
- [ ] `[Core]` [Min Stack](02_DSA/06_Stacks_Queues/MinStack/MinStack.md) — O(1) min tracking
- [ ] `[Core]` [Binary Search](02_DSA/03_Sorting_Searching/BinarySearch/BinarySearch.md) — divide search space
- [ ] `[Core]` [Merge Sort](02_DSA/03_Sorting_Searching/MergeSort/MergeSort.md) — stable O(n log n) sort
- [ ] `[Core]` [Maximum Depth](02_DSA/08_Trees_Binary_Trees/MaximumDepth/MaximumDepth.md) — tree recursion
- [ ] `[Core]` [Invert Binary Tree](02_DSA/08_Trees_Binary_Trees/InvertBinaryTree/InvertBinaryTree.md) — DFS tree transform
- [ ] `[Core]` [Symmetric Tree](02_DSA/08_Trees_Binary_Trees/SymmetricTree/SymmetricTree.md) — mirror comparison
- [ ] `[Core]` [Subsets](02_DSA/12_Backtracking/Subsets/Subsets.md) — backtracking intro
- [ ] `[Core]` [Permutations](02_DSA/12_Backtracking/Permutations/Permutations.md) — permutation generation
- [ ] `[Core]` [Longest Substring Without Repeating](02_DSA/02_Strings/LongestSubstringWithoutRepeating/LongestSubstringWithoutRepeating.md) — sliding window intro
- [ ] `[Core]` [Remove Duplicates From Sorted Array](02_DSA/04_Sliding_Window_Two_Pointers/RemoveDuplicatesFromSortedArray/RemoveDuplicatesFromSortedArray.md) — slow/fast pointer
- [ ] `[Core]` [Climbing Stairs](02_DSA/13_Dynamic_Programming/ClimbingStairs/ClimbingStairs.md) — 1D DP intro
- [ ] `[Core]` [House Robber](02_DSA/13_Dynamic_Programming/HouseRobber/HouseRobber.md) — adjacent-choice DP
- [ ] `[Core]` [BST Operations](02_DSA/09_Binary_Search_Tree/BSTOperations/BSTOperations.md) — insert/search/delete
- [ ] `[Core]` [Lowest Common Ancestor BST](02_DSA/09_Binary_Search_Tree/LowestCommonAncestorBST/LowestCommonAncestorBST.md) — BST ordering walk
- [ ] `[Core]` [Balanced BST Check](02_DSA/09_Binary_Search_Tree/BalancedBSTCheck/BalancedBSTCheck.md) — height-balanced validation
- [ ] `[Core]` [Single Number](02_DSA/15_Bit_Manipulation/SingleNumber/SingleNumber.md) — XOR trick
- [ ] `[Core]` [Counting Bits](02_DSA/15_Bit_Manipulation/CountingBits/CountingBits.md) — DP on bit counts
- [ ] `[Core]` [Power of Two](02_DSA/15_Bit_Manipulation/PowerOfTwo/PowerOfTwo.md) — n & (n-1) test
- [ ] `[Core]` [Number of 1 Bits](02_DSA/15_Bit_Manipulation/NumberOf1Bits/NumberOf1Bits.md) — popcount

### Engineering Practices
- [ ] `[Core]` [Code Quality & Best Practices](00_Interview_Prep/Core/CodeQuality.md) — naming, SOLID preview, review checklist
- [ ] `[Recommended]` [AI Fundamentals](05_AI/01_AI_Fundamentals.md) — developer-level AI literacy (2026 baseline)
- [ ] `[Recommended]` [AI-Powered Dev Tools](05_AI/07_AI_Powered_Dev_Tools.md) — Copilot, Cursor in daily workflow

### Interview Prep
- [ ] `[Core]` [Self-Assessment — Section 0](00_Interview_Prep/Core/SelfAssessment.md) — fundamentals readiness checklist
- [ ] `[Core]` [Interview Playbook](00_Interview_Prep/Core/InterviewPlaybook.md) — round protocols and 12-week schedule
- [ ] `[Core]` [Fresher Screening & OA Guide](00_Interview_Prep/Career/Fresher_Screening_and_OA_Guide.md) — HackerRank/Codility strategy, pipeline stages
- [ ] `[Core]` [Fresher Failure Modes](00_Interview_Prep/Levels/Fresher_Failure_Modes.md) — OA, phone screen, HR eliminators
- [ ] `[Recommended]` [Companies Guide](00_Interview_Prep/Core/Companies.md) — hiring-from-India matrix (global + Indian firms)
- [ ] `[Recommended]` [MASTER INDEX](MASTER_INDEX.md) — topic → file quick lookup

### Company & Role Research
- [ ] `[Recommended]` [Companies Guide — fresher bands](00_Interview_Prep/Core/Companies.md) — mass hiring vs product startup toughness

---

## SDE1

**Prerequisite Check:** Fresher Core items complete; Section 0 SelfAssessment ≥ 3/5.

**Ready to move on when:** Path 1 fully complete; can solve 80% Easy independently; basic Spring REST explanation ([README Fresher outcome](README.md#associate-junior-sde-weeks-1-16)); SelfAssessment Sections 0 + partial Section 3 ≥ 3/5.

### Technical Skills
- [ ] `[Core]` [Advanced Spring Boot & Java Internals](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) — Sections 2.3–2.4: Spring lifecycle, REST, OAuth2/JWT intro
- [ ] `[Core]` [API Design & REST](01_TechGuide/04_API_Design_REST.md) — pagination, versioning, error handling
- [ ] `[Recommended]` [Angular Frontend Engineering](01_TechGuide/08_Angular_Frontend_Engineering.md) — standalone components, routing, forms basics
- [ ] `[Recommended]` [Modern Java Features](01_TechGuide/01_Modern_Java_Features.md) — records, sealed classes preview

### DSA (Path 1 completion + intro Medium)
- [ ] `[Core]` [Best Time to Buy and Sell Stock](02_DSA/01_Arrays_Matrix/BestTimeToBuyAndSellStock/BestTimeToBuyAndSellStock.md) — single-pass optimization
- [ ] `[Core]` [Longest Substring Without Repeating](02_DSA/02_Strings/LongestSubstringWithoutRepeating/LongestSubstringWithoutRepeating.md) — variable window template
- [ ] `[Core]` [Integer to Roman](02_DSA/02_Strings/IntegerToRoman/IntegerToRoman.md) — greedy mapping
- [ ] `[Core]` [Add Two Numbers](02_DSA/05_Linked_Lists/AddTwoNumbers/AddTwoNumbers.md) — digit-list simulation
- [ ] `[Core]` [Evaluate Reverse Polish Notation](02_DSA/06_Stacks_Queues/EvaluateReversePolishNotation/EvaluateReversePolishNotation.md) — stack evaluation
- [ ] `[Core]` [Next Greater Element](02_DSA/06_Stacks_Queues/NextGreaterElement/NextGreaterElement.md) — monotonic stack intro
- [ ] `[Core]` [Daily Temperatures](02_DSA/06_Stacks_Queues/DailyTemperatures/DailyTemperatures.md) — monotonic stack pattern
- [ ] `[Core]` [Quick Sort](02_DSA/03_Sorting_Searching/QuickSort/QuickSort.md) — partition algorithms
- [ ] `[Core]` [Validate BST](02_DSA/08_Trees_Binary_Trees/ValidateBinarySearchTree/ValidateBinarySearchTree.md) — bound propagation
- [ ] `[Core]` [Level Order Traversal](02_DSA/08_Trees_Binary_Trees/BinaryTreeLevelOrderTraversal/BinaryTreeLevelOrderTraversal.md) — BFS on trees
- [ ] `[Core]` [Generate Parentheses](02_DSA/12_Backtracking/GenerateParentheses/GenerateParentheses.md) — constrained generation
- [ ] `[Core]` [Minimum Size Subarray Sum](02_DSA/04_Sliding_Window_Two_Pointers/MinimumSizeSubarraySum/MinimumSizeSubarraySum.md) — variable window on arrays
- [ ] `[Core]` [Coin Change](02_DSA/13_Dynamic_Programming/CoinChange/CoinChange.md) — unbounded knapsack DP
- [ ] `[Core]` [Unique Paths](02_DSA/13_Dynamic_Programming/UniquePaths/UniquePaths.md) — grid path counting
- [ ] `[Core]` [Reverse Bits](02_DSA/15_Bit_Manipulation/ReverseBits/ReverseBits.md) — bit manipulation loop

### System Design
- [ ] `[Core]` [LLD Template](04_SystemDesign/00_Templates/LLD_Template/LLD_Template.md) — structured OOD interview flow
- [ ] `[Core]` [Parking Lot](04_SystemDesign/01_LowLevelDesign/ParkingLot/ParkingLot.md) — Strategy, Factory, Singleton OOD
- [ ] `[Core]` [Snake and Ladder](04_SystemDesign/01_LowLevelDesign/SnakeAndLadder/SnakeAndLadder.md) — game-loop OOD
- [ ] `[Core]` [Vending Machine](04_SystemDesign/01_LowLevelDesign/VendingMachine/VendingMachine.md) — state machine FSM
- [ ] `[Recommended]` [Library Management](04_SystemDesign/01_LowLevelDesign/LibraryManagement/LibraryManagement.md) — domain modeling with patterns
- [ ] `[Core]` [Machine Coding Round Guide](03_CodingPatterns/Machine_Coding_Round_Guide.md) — timed OOD + Spring Boot (90 min structure)

### Engineering Practices
- [ ] `[Core]` [Coding Patterns README](03_CodingPatterns/README.md) — GoF vs algorithmic pattern study order
- [ ] `[Core]` [Algorithmic Patterns](03_CodingPatterns/02_AlgorithmicPatterns.md) — 17 interview templates
- [ ] `[Recommended]` [Design Patterns & SOLID](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md) — SOLID and GoF overview

### Interview Prep
- [ ] `[Core]` [SDE1 Interview Guide](00_Interview_Prep/Levels/SDE1_Interview_Guide.md) — loop structure, Spring/LLD expectations, failure modes
- [ ] `[Core]` [Self-Assessment — Sections 0–3](00_Interview_Prep/Core/SelfAssessment.md) — Java internals and Spring basics
- [ ] `[Recommended]` [Interview Questions](00_Interview_Prep/Core/InterviewQuestions.md) — rapid-fire cram sheet
- [ ] `[Recommended]` [AI Fundamentals](05_AI/01_AI_Fundamentals.md) — baseline literacy before SDE2 AI depth

### Company & Role Research
- [ ] `[Recommended]` [Companies Guide](00_Interview_Prep/Core/Companies.md) — SDE1-level firms hiring in India

### Common Failure Modes
- [ ] `[Core]` [SDE1 Interview Guide — failure modes section](00_Interview_Prep/Levels/SDE1_Interview_Guide.md)

---

## SDE2

**Prerequisite Check:** SDE1 complete; StudyGuide Path 1 success metrics met; SelfAssessment Sections 0–3 average ≥ 3/5.

**Ready to move on when:** StudyGuide Path 2 success metrics — 90% Easy, 70% Medium, 200+ problems, pattern ID in 2–3 min ([StudyGuide Path 2](02_DSA/StudyGuide.md)); SelfAssessment total score ≥ 95 ([SelfAssessment readiness table](00_Interview_Prep/Core/SelfAssessment.md)).

### Technical Skills
- [ ] `[Core]` [Modern Java Features](01_TechGuide/01_Modern_Java_Features.md) — records, virtual threads, sealed classes
- [ ] `[Core]` [Advanced Spring Boot & Java Internals](01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) — JVM, GC, concurrency (§2.1–2.2)
- [ ] `[Core]` [Design Patterns & SOLID](01_TechGuide/03_Design_Patterns_SOLID_CleanArch.md) — GoF, clean/hexagonal architecture
- [ ] `[Core]` [GoF Patterns + Spring](03_CodingPatterns/01_Patterns.md) — 25 patterns with Java examples
- [ ] `[Core]` [Database Performance Tuning](01_TechGuide/05_Database_Performance_Tuning.md) — indexing, N+1, sharding intro
- [ ] `[Core]` [Java Collections & Concurrency Deep Dive](01_TechGuide/15_Java_Collections_Concurrency_DeepDive.md) — HashMap, CHM, locks, CompletableFuture
- [ ] `[Core]` [Testing Strategies](01_TechGuide/09_Testing_Strategies.md) — test pyramid, Testcontainers
- [ ] `[Core]` [Angular Frontend Engineering](01_TechGuide/08_Angular_Frontend_Engineering.md) — signals, RxJS, state management
- [ ] `[Recommended]` [Spring Ecosystem Deep Dive](01_TechGuide/16_Spring_Ecosystem_DeepDive.md) — JPA, WebFlux, Spring Cloud
- [ ] `[Recommended]` [Networking & Protocols](01_TechGuide/17_Networking_Protocols.md) — HTTP/2/3, TLS, gRPC
- [ ] `[Optional]` [Kotlin for Java Developers](01_TechGuide/22_Kotlin_for_Java_Developers.md) — coroutines vs virtual threads
- [ ] `[Optional]` [GraphQL & Alternative APIs](01_TechGuide/21_GraphQL_and_Alternative_APIs.md) — REST vs GraphQL vs gRPC
- [ ] `[Optional]` [Polyglot Interview — Python & Go](01_TechGuide/36_Polyglot_Interview_Python_and_Go.md) — non-Java coding rounds
- [ ] `[Optional]` [PostgreSQL Deep Dive](01_TechGuide/26_PostgreSQL_Relational_DB_Deep_Dive.md) — MVCC, replication (deeper than §05)
- [ ] `[Optional]` [Redis Distributed Caching](01_TechGuide/28_Redis_Distributed_Caching.md) — deeper dive; §05 covers Redis intro
- [ ] `[Optional]` [Cloud Computing AWS/GCP/Azure](01_TechGuide/31_Cloud_Computing_AWS_GCP_Azure.md) — multi-cloud service map

### DSA (Path 2 — Medium mastery)
- [ ] `[Core]` [DSA Study Guide — Path 2](02_DSA/StudyGuide.md) — 12–16 week Mid-level curriculum
- [ ] `[Core]` [Three Sum](02_DSA/01_Arrays_Matrix/ThreeSum/ThreeSum.md) — sort + two-pointer
- [ ] `[Core]` [Container With Most Water](02_DSA/01_Arrays_Matrix/ContainerWithMostWater/ContainerWithMostWater.md) — greedy two-pointer
- [ ] `[Core]` [Product of Array Except Self](02_DSA/01_Arrays_Matrix/ProductOfArrayExceptSelf/ProductOfArrayExceptSelf.md) — prefix/suffix
- [ ] `[Core]` [Merge Intervals](02_DSA/01_Arrays_Matrix/MergeIntervals/MergeIntervals.md) — sort + merge template
- [ ] `[Core]` [Insert Interval](02_DSA/01_Arrays_Matrix/InsertInterval/InsertInterval.md) — interval insertion
- [ ] `[Core]` [Search in Rotated Sorted Array](02_DSA/01_Arrays_Matrix/SearchInRotatedSortedArray/SearchInRotatedSortedArray.md) — modified binary search
- [ ] `[Core]` [Find Minimum in Rotated Sorted Array](02_DSA/01_Arrays_Matrix/FindMinimumInRotatedSortedArray/FindMinimumInRotatedSortedArray.md) — rotation pivot
- [ ] `[Core]` [Set Matrix Zeroes](02_DSA/01_Arrays_Matrix/SetMatrixZeroes/SetMatrixZeroes.md) — in-place marking
- [ ] `[Core]` [Spiral Matrix](02_DSA/01_Arrays_Matrix/SpiralMatrix/SpiralMatrix.md) — boundary simulation
- [ ] `[Core]` [Longest Consecutive Sequence](02_DSA/01_Arrays_Matrix/LongestConsecutiveSequence/LongestConsecutiveSequence.md) — O(n) HashSet trick
- [ ] `[Core]` [Find Duplicate Number](02_DSA/01_Arrays_Matrix/FindDuplicateNumber/FindDuplicateNumber.md) — cyclic sort / Floyd cycle (Pattern 5)
- [ ] `[Core]` [Longest Palindromic Substring](02_DSA/02_Strings/LongestPalindromicSubstring/LongestPalindromicSubstring.md) — expand around center
- [ ] `[Core]` [Minimum Window Substring](02_DSA/02_Strings/MinimumWindowSubstring/MinimumWindowSubstring.md) — complex sliding window
- [ ] `[Core]` [Implement strStr](02_DSA/02_Strings/ImplementStrStr/ImplementStrStr.md) — KMP / Rabin-Karp
- [ ] `[Core]` [Reorder List](02_DSA/05_Linked_Lists/ReorderList/ReorderList.md) — mid + reverse + merge
- [ ] `[Core]` [Remove Nth Node From End](02_DSA/05_Linked_Lists/RemoveNthNodeFromEnd/RemoveNthNodeFromEnd.md) — gap pointer technique
- [ ] `[Core]` [LRU Cache](02_DSA/05_Linked_Lists/LRUCache/LRUCache.md) — HashMap + doubly linked list design
- [ ] `[Core]` [Longest Substring K Distinct](02_DSA/04_Sliding_Window_Two_Pointers/LongestSubstringKDistinct/LongestSubstringKDistinct.md) — at-most-K window
- [ ] `[Core]` [Koko Eating Bananas](02_DSA/03_Sorting_Searching/KokoEatingBananas/KokoEatingBananas.md) — binary search on answer
- [ ] `[Core]` [Construct Tree From Traversals](02_DSA/08_Trees_Binary_Trees/ConstructTreeFromTraversals/ConstructTreeFromTraversals.md) — tree reconstruction
- [ ] `[Core]` [Subtree of Another Tree](02_DSA/08_Trees_Binary_Trees/SubtreeOfAnotherTree/SubtreeOfAnotherTree.md) — tree comparison
- [ ] `[Core]` [Kth Smallest Element BST](02_DSA/09_Binary_Search_Tree/KthSmallestElementBST/KthSmallestElementBST.md) — inorder traversal
- [ ] `[Core]` [Top K Frequent Elements](02_DSA/10_Heaps_Priority_Queues/TopKFrequentElements/TopKFrequentElements.md) — heap + HashMap
- [ ] `[Core]` [Kth Largest Element in Array](02_DSA/10_Heaps_Priority_Queues/KthLargestElementInArray/KthLargestElementInArray.md) — quickselect / heap
- [ ] `[Core]` [Number of Islands](02_DSA/11_Graphs/NumberOfIslands/NumberOfIslands.md) — grid BFS/DFS
- [ ] `[Core]` [Clone Graph](02_DSA/11_Graphs/CloneGraph/CloneGraph.md) — DFS with visited map
- [ ] `[Core]` [Course Schedule](02_DSA/11_Graphs/CourseSchedule/CourseSchedule.md) — topological sort / cycle detection
- [ ] `[Core]` [Topological Sort](02_DSA/11_Graphs/TopologicalSort/TopologicalSort.md) — Kahn's and DFS post-order
- [ ] `[Core]` [Union Find](02_DSA/11_Graphs/UnionFind/UnionFind.md) — DSU with path compression
- [ ] `[Core]` [Longest Common Subsequence](02_DSA/13_Dynamic_Programming/LongestCommonSubsequence/LongestCommonSubsequence.md) — classic 2D DP
- [ ] `[Core]` [Word Break](02_DSA/13_Dynamic_Programming/WordBreak/WordBreak.md) — string segmentation DP
- [ ] `[Core]` [Longest Increasing Subsequence](02_DSA/13_Dynamic_Programming/LongestIncreasingSubsequence/LongestIncreasingSubsequence.md) — O(n log n) LIS
- [ ] `[Core]` [Palindromic Substrings](02_DSA/13_Dynamic_Programming/PalindromicSubstrings/PalindromicSubstrings.md) — palindrome counting DP
- [ ] `[Core]` [Jump Game](02_DSA/13_Dynamic_Programming/JumpGame/JumpGame.md) — reachability DP + greedy farthest reach
- [ ] `[Core]` [Gas Station](02_DSA/14_Greedy_Algorithms/GasStation/GasStation.md) — circular greedy tour
- [ ] `[Core]` [Partition Labels](02_DSA/14_Greedy_Algorithms/PartitionLabels/PartitionLabels.md) — last-occurrence boundaries
- [ ] `[Core]` [N-Queens](02_DSA/12_Backtracking/NQueens/NQueens.md) — constraint satisfaction
- [ ] `[Core]` [Inversion Count](02_DSA/07_Recursion_Divide_Conquer/InversionCount/InversionCount.md) — modified merge sort
- [ ] `[Core]` [Max Subarray Divide & Conquer](02_DSA/07_Recursion_Divide_Conquer/MaximumSubarrayDivideConquer/MaximumSubarrayDivideConquer.md) — D&C combine logic
- [ ] `[Core]` [Master Theorem](02_DSA/07_Recursion_Divide_Conquer/MasterTheorem/MasterTheorem.md) — recurrence analysis
- [ ] `[Core]` [Zero-One Knapsack](02_DSA/13_Dynamic_Programming/ZeroOneKnapsack/ZeroOneKnapsack.md) — take/skip 2D DP
- [ ] `[Recommended]` [Trapping Rain Water](02_DSA/01_Arrays_Matrix/TrappingRainWater/TrappingRainWater.md) — hard array technique
- [ ] `[Recommended]` [Four Sum](02_DSA/01_Arrays_Matrix/FourSum/FourSum.md) — k-sum reduction
- [ ] `[Recommended]` [Sliding Window Maximum](02_DSA/04_Sliding_Window_Two_Pointers/SlidingWindowMaximum/SlidingWindowMaximum.md) — monotonic deque
- [ ] `[Recommended]` [Word Ladder](02_DSA/11_Graphs/WordLadder/WordLadder.md) — BFS shortest path
- [ ] `[Recommended]` [Trie](02_DSA/17_Advanced_Miscellaneous/Trie/Trie.md) — prefix tree
- [ ] `[Recommended]` [Merge K Sorted Lists](02_DSA/10_Heaps_Priority_Queues/MergeKSortedLists/MergeKSortedLists.md) — k-way merge
- [ ] `[Recommended]` [Sudoku Solver](02_DSA/12_Backtracking/SudokuSolver/SudokuSolver.md) — complex backtracking
- [ ] `[Recommended]` [Task Scheduler](02_DSA/14_Greedy_Algorithms/TaskScheduler/TaskScheduler.md) — heap + greedy idle count

### System Design
- [ ] `[Core]` [BookMyShow](04_SystemDesign/01_LowLevelDesign/BookMyShow/BookMyShow.md) — concurrent seat locking LLD
- [ ] `[Core]` [Elevator System](04_SystemDesign/01_LowLevelDesign/ElevatorSystem/ElevatorSystem.md) — scheduling state machine
- [ ] `[Core]` [URL Shortener](04_SystemDesign/02_HighLevelDesign/URLShortener/URLShortener.md) — encoding, cache-aside, sharding
- [ ] `[Core]` [Rate Limiter](04_SystemDesign/02_HighLevelDesign/RateLimiter/RateLimiter.md) — token bucket, sliding window
- [ ] `[Recommended]` [Search Autocomplete](04_SystemDesign/02_HighLevelDesign/SearchAutocomplete/SearchAutocomplete.md) — trie + ranking
- [ ] `[Recommended]` [ATM](04_SystemDesign/01_LowLevelDesign/ATM/ATM.md) — FSM + cash inventory
- [ ] `[Recommended]` [Hotel Booking](04_SystemDesign/01_LowLevelDesign/HotelBooking/HotelBooking.md) — overlap prevention
- [ ] `[Recommended]` [Food Delivery](04_SystemDesign/01_LowLevelDesign/FoodDelivery/FoodDelivery.md) — order lifecycle

### Engineering Practices
- [ ] `[Core]` [Cross References — DSA to System Design](00_Interview_Prep/Core/CrossReferences.md) — mental model bridge
- [ ] `[Recommended]` [LLM & Prompt Engineering](05_AI/02_LLM_and_Prompt_Engineering.md) — production prompt patterns
- [ ] `[Recommended]` [Spring AI](05_AI/05_Spring_AI.md) — ChatClient, function calling in Java

### Interview Prep
- [ ] `[Core]` [Self-Assessment — Sections 1–10](00_Interview_Prep/Core/SelfAssessment.md) — Java, Spring, microservices preview
- [ ] `[Core]` [Companies Guide](00_Interview_Prep/Core/Companies.md) — India-focused company matrix and FAANG deep dives
- [ ] `[Core]` [SDE2 Interview Failure Modes](00_Interview_Prep/Levels/SDE2_Failure_Modes.md) — India mid-level loop eliminators (DSA, Spring, machine coding)

---

## Senior SDE

**Prerequisite Check:** SDE2 complete; Path 2 success metrics met; SelfAssessment ≥ 130 total ([SelfAssessment](00_Interview_Prep/Core/SelfAssessment.md)).

**Ready to move on when:** StudyGuide Path 3 success metrics — 95% Easy, 85% Medium, 60% Hard, connect algorithms to system design ([StudyGuide Path 3](02_DSA/StudyGuide.md)); can whiteboard flagship HLD in 45 min using [HLD Template](04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md).

### Technical Skills
- [ ] `[Core]` [Microservices & Distributed Systems](01_TechGuide/06_Microservices_Distributed_Systems.md) — resilience, SAGA, Kafka, gRPC
- [ ] `[Core]` [System Design Concepts](01_TechGuide/07_System_Design.md) — CAP, scaling, load balancing, API gateway
- [ ] `[Core]` [Observability](01_TechGuide/11_Observability.md) — logs, metrics, traces, SLI/SLO
- [ ] `[Core]` [Security OWASP & Cloud](01_TechGuide/12_Security_OWASP_Cloud.md) — OWASP Top 10, JWT hardening, AWS basics
- [ ] `[Core]` [DevOps CI/CD & Docker](01_TechGuide/10_DevOps_CICD_Docker.md) — containers, CI/CD, GitOps
- [ ] `[Core]` [Performance Engineering & JVM](01_TechGuide/18_Performance_Engineering_JVM.md) — GC tuning, JFR, profiling
- [ ] `[Core]` [Event-Driven Architecture](01_TechGuide/19_Event_Driven_Architecture.md) — Kafka deep dive; §06 covers messaging intro
- [ ] `[Recommended]` [SRE & Reliability Engineering](01_TechGuide/23_SRE_Reliability_Engineering.md) — error budgets, incidents (extends §11)
- [ ] `[Recommended]` [NoSQL Databases Guide](01_TechGuide/27_NoSQL_Databases_Guide.md) — MongoDB, DynamoDB, Cassandra selection
- [ ] `[Recommended]` [Kubernetes Deep Dive](01_TechGuide/30_Kubernetes_Deep_Dive.md) — K8s ops; §13 covers production K8s trends
- [ ] `[Recommended]` [Advanced Networking Infrastructure](01_TechGuide/29_Advanced_Networking_Infrastructure.md) — VPC, L4/L7 LB; §17 covers protocols
- [ ] `[Recommended]` [Search Engines Elasticsearch](01_TechGuide/34_Search_Engines_Elasticsearch.md) — inverted index, BM25
- [ ] `[Recommended]` [Data Engineering Fundamentals](01_TechGuide/25_Data_Engineering_Fundamentals.md) — batch/stream, CDC, lakehouse
- [ ] `[Recommended]` [Compliance & Regulated Systems](01_TechGuide/38_Compliance_and_Regulated_Systems.md) — GDPR, PCI, audit logging
- [ ] `[Optional]` [Modern Trends 2026](01_TechGuide/13_Modern_Trends_2026.md) — RAG/Spring AI + K8s production (Lead-oriented preview)

### DSA (Path 3 — Hard + system-connected)
- [ ] `[Core]` [DSA Study Guide — Path 3](02_DSA/StudyGuide.md) — Senior/Lead 8–12 week curriculum
- [ ] `[Core]` [Dijkstra's Algorithm](02_DSA/11_Graphs/DijkstraAlgorithm/DijkstraAlgorithm.md) — weighted shortest path
- [ ] `[Core]` [Serialize/Deserialize Binary Tree](02_DSA/08_Trees_Binary_Trees/SerializeDeserializeBinaryTree/SerializeDeserializeBinaryTree.md) — tree codec
- [ ] `[Core]` [Lowest Common Ancestor](02_DSA/08_Trees_Binary_Trees/LowestCommonAncestor/LowestCommonAncestor.md) — general binary tree LCA
- [ ] `[Core]` [Find Median from Data Stream](02_DSA/10_Heaps_Priority_Queues/FindMedianFromDataStream/FindMedianFromDataStream.md) — two-heap streaming median
- [ ] `[Core]` [Edit Distance](02_DSA/13_Dynamic_Programming/EditDistance/EditDistance.md) — Levenshtein 2D DP
- [ ] `[Core]` [Advanced DP](02_DSA/13_Dynamic_Programming/AdvancedDP/AdvancedDP.md) — interval, bitmask, tree DP catalog
- [ ] `[Core]` [Advanced Sliding Window](02_DSA/04_Sliding_Window_Two_Pointers/AdvancedSlidingWindow/AdvancedSlidingWindow.md) — atMostK/exactlyK templates
- [ ] `[Core]` [Segment Tree](02_DSA/17_Advanced_Miscellaneous/SegmentTree/SegmentTree.md) — range queries + lazy propagation
- [ ] `[Core]` [Number Theory](02_DSA/16_Math_Algorithms/NumberTheory/NumberTheory.md) — GCD, primes, modular arithmetic
- [ ] `[Core]` [Print In Order](02_DSA/18_Concurrency_Multithreading/PrintInOrder/PrintInOrder.md) — java.util.concurrent primitives
- [ ] `[Core]` [Producer-Consumer](02_DSA/18_Concurrency_Multithreading/ProducerConsumer/ProducerConsumer.md) — bounded buffer patterns
- [ ] `[Core]` [Read-Write Lock](02_DSA/18_Concurrency_Multithreading/ReadWriteLock/ReadWriteLock.md) — readers-writers fairness

### System Design
- [ ] `[Core]` [HLD Template](04_SystemDesign/00_Templates/HLD_Template/HLD_Template.md) — 45–60 min HLD framework
- [ ] `[Core]` [System Design README](04_SystemDesign/README.md) — LLD/HLD catalog and study order
- [ ] `[Core]` [Chat System](04_SystemDesign/02_HighLevelDesign/ChatSystem/ChatSystem.md) — WebSocket, Kafka fan-out
- [ ] `[Core]` [News Feed](04_SystemDesign/02_HighLevelDesign/NewsFeed/NewsFeed.md) — fan-out on write vs read
- [ ] `[Core]` [Notification System](04_SystemDesign/02_HighLevelDesign/NotificationSystem/NotificationSystem.md) — multi-channel fan-out
- [ ] `[Core]` [Distributed Cache](04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) — consistent hashing, replication
- [ ] `[Core]` [Twitter](04_SystemDesign/02_HighLevelDesign/Twitter/Twitter.md) — timeline, Snowflake IDs, search
- [ ] `[Core]` [Ride Sharing](04_SystemDesign/02_HighLevelDesign/RideSharing/RideSharing.md) — geospatial matching, surge
- [ ] `[Core]` [Payment System](04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) — idempotency, ledger, reconciliation
- [ ] `[Recommended]` [File Storage](04_SystemDesign/02_HighLevelDesign/FileStorage/FileStorage.md) — Dropbox-style sync
- [ ] `[Recommended]` [Video Streaming](04_SystemDesign/02_HighLevelDesign/VideoStreaming/VideoStreaming.md) — HLS/DASH pipeline
- [ ] `[Recommended]` [WhatsApp](04_SystemDesign/02_HighLevelDesign/WhatsApp/WhatsApp.md) — E2EE messaging
- [ ] `[Recommended]` [Instagram](04_SystemDesign/02_HighLevelDesign/Instagram/Instagram.md) — media pipeline + feed
- [ ] `[Recommended]` [Ticketmaster](04_SystemDesign/02_HighLevelDesign/Ticketmaster/Ticketmaster.md) — flash-sale seat holds
- [ ] `[Recommended]` [Web Crawler](04_SystemDesign/02_HighLevelDesign/WebCrawler/WebCrawler.md) — distributed crawl
- [ ] `[Recommended]` [Metrics Monitoring](04_SystemDesign/02_HighLevelDesign/MetricsMonitoring/MetricsMonitoring.md) — TSDB at scale
- [ ] `[Recommended]` [YouTube](04_SystemDesign/02_HighLevelDesign/YouTube/YouTube.md) — upload + CDN + recommendations

### Engineering Practices
- [ ] `[Core]` [RAG Architecture](05_AI/03_RAG_Architecture.md) — end-to-end RAG pipeline
- [ ] `[Core]` [Vector Databases & Embeddings](05_AI/04_Vector_Databases_Embeddings.md) — pgvector, HNSW, sizing
- [ ] `[Core]` [MLOps & AI in Production](05_AI/09_MLOps_AI_in_Production.md) — deploy, monitor, cost-control AI
- [ ] `[Recommended]` [AI in Frontend](05_AI/08_AI_in_Frontend.md) — streaming chat UI in Angular
- [ ] `[Recommended]` [AI Ethics & Governance](05_AI/11_AI_Ethics_Safety_Governance.md) — responsible AI controls

### Interview Prep
- [ ] `[Core]` [Self-Assessment — Sections 11–20](00_Interview_Prep/Core/SelfAssessment.md) — system design, DevOps, security depth
- [ ] `[Core]` [Interview Questions](00_Interview_Prep/Core/InterviewQuestions.md) — senior-level rapid review
- [ ] `[Core]` [Senior Loop Failure Modes](00_Interview_Prep/Levels/Senior_Failure_Modes.md) — HLD NFR gaps, coding depth, behavioral at senior bands

---

## Tech Lead

**Prerequisite Check:** Senior SDE Core complete; flagship HLD (Chat, Payment, or News Feed) done; SelfAssessment ≥ 160 ([interview-ready band](00_Interview_Prep/Core/SelfAssessment.md)).

**Ready to move on when:** Can deliver 4+ polished STAR stories; facilitate 45-min system design with trade-off table; SelfAssessment Sections 27 (leadership) and 19 (AI) ≥ 4/5; [Staff/Principal Advance Criteria](00_Interview_Prep/Levels/Staff_Principal_Advance_Criteria.md) Staff band signals for next step.

### Technical Skills
- [ ] `[Core]` [Leadership, Behavioral & System Design Framework](01_TechGuide/14_Leadership_Behavioral_SystemDesign.md) — STAR, 45-min SD framework
- [ ] `[Core]` [Technical Leadership & Architecture](01_TechGuide/20_Technical_Leadership_Architecture.md) — ADRs, estimation, incidents, tech debt
- [ ] `[Core]` [Modern Trends 2026](01_TechGuide/13_Modern_Trends_2026.md) — RAG/Spring AI architecture + K8s production
- [ ] `[Recommended]` [AI Agents & Workflows](05_AI/06_AI_Agents_and_Workflows.md) — ReAct, MCP, multi-agent
- [ ] `[Recommended]` [AI System Design](05_AI/10_AI_System_Design.md) — lead-level AI HLD cases

### System Design
- [ ] `[Core]` [System Design README — Lead study path](04_SystemDesign/README.md) — full LLD+HLD catalog (18 HLD cases, 239–880 lines each)
- [ ] `[Recommended]` Stretch HLD breadth after core flagships (Payment 420 lines, Distributed Cache 409, Chat 325): [YouTube](04_SystemDesign/02_HighLevelDesign/YouTube/YouTube.md) (239), [Metrics Monitoring](04_SystemDesign/02_HighLevelDesign/MetricsMonitoring/MetricsMonitoring.md) (240), [Web Crawler](04_SystemDesign/02_HighLevelDesign/WebCrawler/WebCrawler.md) (243), [Video Streaming](04_SystemDesign/02_HighLevelDesign/VideoStreaming/VideoStreaming.md) (255), [File Storage](04_SystemDesign/02_HighLevelDesign/FileStorage/FileStorage.md) (274), [WhatsApp](04_SystemDesign/02_HighLevelDesign/WhatsApp/WhatsApp.md) (313)

### Soft Skills
- [ ] `[Core]` [Companies Guide — behavioral & LP mapping](00_Interview_Prep/Core/Companies.md) — Amazon LP→STAR, company-specific rounds
- [ ] `[Core]` [Interview Playbook — STAR & behavioral protocol](00_Interview_Prep/Core/InterviewPlaybook.md) — Section 5 STAR method
- [ ] `[Core]` [Tech Lead — Conflict & Performance](00_Interview_Prep/Levels/Tech_Lead_Conflict_and_Performance.md) — STAR conflict stories, perf conversations, LP mapping

### Interview Prep
- [ ] `[Core]` [Self-Assessment — Sections 21–27](00_Interview_Prep/Core/SelfAssessment.md) — DSA hard, leadership, full score ≥ 160
- [ ] `[Core]` [Interview Questions — behavioral section](00_Interview_Prep/Core/InterviewQuestions.md) — leadership rapid-fire
- [ ] `[Core]` [Tech Lead Failure Modes](00_Interview_Prep/Levels/Tech_Lead_Failure_Modes.md) — SD/behavioral/coding traps at lead band
- [ ] `[Core]` [Tier 3 Differentiators](02_DSA/Tier3_Differentiators.md) — hard pattern map for lead loops
- [ ] `[Core]` [Mock Interview Rubric](00_Interview_Prep/Mock/Interview_Rubric.md) — scorer template for coding, SD, behavioral mocks

### Company & Role Research
- [ ] `[Core]` [Companies Guide](00_Interview_Prep/Core/Companies.md) — 250+ company directory, comp/negotiation
- [ ] `[Core]` [Comp & Scope — Staff vs Principal](00_Interview_Prep/Levels/Comp_and_Scope.md) — India bands, scope ladder, negotiation anchors

---

## Staff Engineer

**Prerequisite Check:** Tech Lead Core complete; Tier 3 sprint weeks 1–6 underway; SelfAssessment total ≥ 160 with Sections 22–26 ≥ 4/5.

**Ready to move on when:** Can explain Tier 3 patterns (interval DP, segment tree, concurrency) with production trade-offs; articulate platform/IDP strategy from §24; [Staff/Principal Advance Criteria](00_Interview_Prep/Levels/Staff_Principal_Advance_Criteria.md) signals met.

### Technical Skills
- [ ] `[Core]` [Platform Engineering & IDP](01_TechGuide/24_Platform_Engineering_IDP.md) — golden paths, Backstage, DORA
- [ ] `[Core]` [Technical Leadership & Architecture](01_TechGuide/20_Technical_Leadership_Architecture.md) — cross-team ADRs, estimation at scale
- [ ] `[Recommended]` [SRE & Reliability Engineering](01_TechGuide/23_SRE_Reliability_Engineering.md) — org-wide reliability practices
- [ ] `[Recommended]` [Data Engineering Fundamentals](01_TechGuide/25_Data_Engineering_Fundamentals.md) — pipeline architecture at org scale
- [ ] `[Optional]` [Advanced Networking Infrastructure](01_TechGuide/29_Advanced_Networking_Infrastructure.md) — zero trust, service mesh
- [ ] `[Optional]` [Kubernetes Deep Dive](01_TechGuide/30_Kubernetes_Deep_Dive.md) — platform K8s operations

### DSA (Tier 3 — Staff differentiators)
- [ ] `[Core]` [Tier 3 Differentiators](02_DSA/Tier3_Differentiators.md) — 12-week sprint; full problem table
- [ ] `[Core]` [Network Delay Time](02_DSA/11_Graphs/NetworkDelayTime/NetworkDelayTime.md) — Dijkstra single-source
- [ ] `[Core]` [Min Cost to Connect Points](02_DSA/11_Graphs/MinCostToConnectPoints/MinCostToConnectPoints.md) — Kruskal MST + Union Find
- [ ] `[Core]` [Word Ladder II](02_DSA/11_Graphs/WordLadderII/WordLadderII.md) — BFS layers + all shortest paths
- [ ] `[Core]` [Median of Two Sorted Arrays](02_DSA/03_Sorting_Searching/MedianOfTwoSortedArrays/MedianOfTwoSortedArrays.md) — binary search on partition
- [ ] `[Core]` [Burst Balloons](02_DSA/13_Dynamic_Programming/BurstBalloons/BurstBalloons.md) — interval DP
- [ ] `[Core]` [Regular Expression Matching](02_DSA/02_Strings/RegularExpressionMatching/RegularExpressionMatching.md) — string DP with `*`
- [ ] `[Core]` [Distinct Subsequences](02_DSA/13_Dynamic_Programming/DistinctSubsequences/DistinctSubsequences.md) — DP counting
- [ ] `[Core]` [Cherry Pickup](02_DSA/13_Dynamic_Programming/CherryPickup/CherryPickup.md) — two-walker grid DP
- [ ] `[Core]` [Constrained Subsequence Sum](02_DSA/13_Dynamic_Programming/ConstrainedSubsequenceSum/ConstrainedSubsequenceSum.md) — DP + monotonic deque
- [ ] `[Core]` [Capacity To Ship Packages](02_DSA/03_Sorting_Searching/CapacityToShipPackages/CapacityToShipPackages.md) — binary search on answer
- [ ] `[Core]` [Split Array Largest Sum](02_DSA/03_Sorting_Searching/SplitArrayLargestSum/SplitArrayLargestSum.md) — binary search on answer
- [ ] `[Core]` [Shortest Subarray Sum ≥ K](02_DSA/04_Sliding_Window_Two_Pointers/ShortestSubarraySumAtLeastK/ShortestSubarraySumAtLeastK.md) — deque + prefix sum
- [ ] `[Core]` [RandomizedSet](02_DSA/17_Advanced_Miscellaneous/RandomizedSet/RandomizedSet.md) — insert/delete/getRandom O(1)
- [ ] `[Core]` [Fenwick Tree](02_DSA/17_Advanced_Miscellaneous/FenwickTree/FenwickTree.md) — range sum mutable
- [ ] `[Core]` [Advanced DP](02_DSA/13_Dynamic_Programming/AdvancedDP/AdvancedDP.md) — interval/bitmask/tree DP catalog
- [ ] `[Core]` [Segment Tree](02_DSA/17_Advanced_Miscellaneous/SegmentTree/SegmentTree.md) — mutable range queries
- [ ] `[Core]` [Dijkstra's Algorithm](02_DSA/11_Graphs/DijkstraAlgorithm/DijkstraAlgorithm.md) — weighted shortest path
- [ ] `[Core]` [Producer-Consumer](02_DSA/18_Concurrency_Multithreading/ProducerConsumer/ProducerConsumer.md) — concurrency design
- [ ] `[Core]` [Read-Write Lock](02_DSA/18_Concurrency_Multithreading/ReadWriteLock/ReadWriteLock.md) — readers-writers fairness
- [ ] `[Core]` [LRU Cache](02_DSA/05_Linked_Lists/LRUCache/LRUCache.md) — HashMap + DLL
- [ ] `[Core]` [LFU Cache](02_DSA/05_Linked_Lists/LFUCache/LFUCache.md) — freq buckets
- [ ] `[Core]` [Alien Dictionary](02_DSA/11_Graphs/AlienDictionary/AlienDictionary.md) — topo sort on char graph
- [ ] `[Core]` [Critical Connections](02_DSA/11_Graphs/CriticalConnections/CriticalConnections.md) — Tarjan bridges

### System Design
- [ ] `[Core]` [Distributed Cache](04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md) — consistent hashing at staff depth
- [ ] `[Core]` [Metrics Monitoring](04_SystemDesign/02_HighLevelDesign/MetricsMonitoring/MetricsMonitoring.md) — observability platform design
- [ ] `[Core]` [Payment System](04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) — financial-grade correctness patterns

### Engineering Practices
- [ ] `[Core]` [AI Agents & Workflows](05_AI/06_AI_Agents_and_Workflows.md) — org-scale agent orchestration
- [ ] `[Core]` [MLOps & AI in Production](05_AI/09_MLOps_AI_in_Production.md) — production AI ops at scale
- [ ] `[Core]` [Multi-Team Architecture Review](00_Interview_Prep/Levels/Multi_Team_Architecture_Review.md) — RFC template, ARF checklist, cross-team roadmap

### Interview Prep
- [ ] `[Core]` [Self-Assessment — full 190-item scorecard](00_Interview_Prep/Core/SelfAssessment.md) — target ≥ 160 for staff loops
- [ ] `[Core]` [Staff Loop Expectations](00_Interview_Prep/Levels/Staff_Loop_Expectations.md) — Staff vs Lead scope, loop structure, cross-team signals
- [ ] `[Core]` [Staff Failure Modes](00_Interview_Prep/Levels/Staff_Failure_Modes.md) — platform, influence, architecture eliminators

### Common Failure Modes
- [ ] `[Core]` [Staff Failure Modes](00_Interview_Prep/Levels/Staff_Failure_Modes.md)

---

## Principal / Architect

**Prerequisite Check:** Staff Engineer Core complete. Principal guides cover enterprise architecture frameworks, vision, org design, DR, integration, and executive communication.

**Ready to move on when:** [Staff/Principal Advance Criteria](00_Interview_Prep/Levels/Staff_Principal_Advance_Criteria.md) Principal band met; can run org-wide ARF using [Multi-Team Architecture Review](00_Interview_Prep/Levels/Multi_Team_Architecture_Review.md).

### Technical Skills
- [ ] `[Core]` [Technical Leadership & Architecture](01_TechGuide/20_Technical_Leadership_Architecture.md) — ADRs, tech debt, stakeholder comms (partial Principal coverage)
- [ ] `[Core]` [Platform Engineering & IDP](01_TechGuide/24_Platform_Engineering_IDP.md) — org developer productivity strategy (partial)
- [ ] `[Core]` [Compliance & Regulated Systems](01_TechGuide/38_Compliance_and_Regulated_Systems.md) — enterprise/regulatory architecture
- [ ] `[Core]` [System Design Concepts](01_TechGuide/07_System_Design.md) — CAP, strangler fig at org scale
- [ ] `[Core]` [Enterprise Architecture Frameworks](00_Interview_Prep/Principal/Enterprise_Architecture_Frameworks.md) — TOGAF ADM, Zachman matrix, strategic DDD, context maps
- [ ] `[Core]` [Multi-Year Vision & Build-vs-Buy](00_Interview_Prep/Principal/Multi_Year_Vision_Build_vs_Buy.md) — 3-year pillars, build-vs-buy matrix, executive narrative
- [ ] `[Core]` [Organization Design — Conway & Team Topologies](00_Interview_Prep/Principal/Organization_Design_Conway_Team_Topologies.md) — stream/platform/enabling teams, Conway reversal

### System Design
- [ ] `[Recommended]` [Payment System](04_SystemDesign/02_HighLevelDesign/PaymentSystem/PaymentSystem.md) — regulated financial architecture reference
- [ ] `[Core]` [Multi-Region Active-Active & DR](00_Interview_Prep/Principal/Multi_Region_Active_Active_DR.md) — RTO/RPO, active-passive vs active-active, Kafka/PostgreSQL replication
- [ ] `[Core]` [Enterprise Integration — ESB, iPaaS, Event Mesh](00_Interview_Prep/Principal/Enterprise_Integration_ESB_iPaaS_Event_Mesh.md) — strangler from ESB, Kafka event mesh, Spring patterns

### Soft Skills
- [ ] `[Core]` [Executive Communication & Board Narrative](00_Interview_Prep/Principal/Executive_Communication_Board_Narrative.md) — SCQA, board 3-min template, metric translation
- [ ] `[Core]` [Vendor Evaluation Rubrics](00_Interview_Prep/Principal/Vendor_Evaluation_Rubrics.md) — weighted scorecard, POC criteria, TCO model

### Interview Prep
- [ ] `[Core]` [Principal Interview Loop Guide](00_Interview_Prep/Principal/Interview_Loop_Guide.md) — loop structure using existing repo depth
- [ ] `[Core]` [Principal Failure Modes](00_Interview_Prep/Levels/Principal_Failure_Modes.md) — vision, exec comms, enterprise traps
- [ ] `[Recommended]` [AI System Design](05_AI/10_AI_System_Design.md) — principal-level AI system cases

---

## Gaps Summary

Consolidated content backlog — **all rows below are Done** and link to shipped files.

| Gap | Levels | Status |
|---|---|---|
| Resume, portfolio, job-search playbook | Fresher | **Done** — [Resume_and_Portfolio.md](00_Interview_Prep/Career/Resume_and_Portfolio.md) |
| Machine-coding round guide | SDE1 | **Done** — [Machine_Coding_Round_Guide.md](03_CodingPatterns/Machine_Coding_Round_Guide.md) |
| Mock interview rubric | Tech Lead | **Done** — [Interview_Rubric.md](00_Interview_Prep/Mock/Interview_Rubric.md) |
| Staff loop expectations | Staff | **Done** — [Staff_Loop_Expectations.md](00_Interview_Prep/Levels/Staff_Loop_Expectations.md) |
| SDE2 interview failure modes | SDE2 | **Done** — [SDE2_Failure_Modes.md](00_Interview_Prep/Levels/SDE2_Failure_Modes.md) |
| Senior loop failure modes | Senior SDE | **Done** — [Senior_Failure_Modes.md](00_Interview_Prep/Levels/Senior_Failure_Modes.md) |
| Conflict resolution & performance management | Tech Lead | **Done** — [Tech_Lead_Conflict_and_Performance.md](00_Interview_Prep/Levels/Tech_Lead_Conflict_and_Performance.md) |
| Staff/Principal comp & scope comparison | Tech Lead, Staff | **Done** — [Comp_and_Scope.md](00_Interview_Prep/Levels/Comp_and_Scope.md) |
| Multi-team roadmap & architecture review | Staff | **Done** — [Multi_Team_Architecture_Review.md](00_Interview_Prep/Levels/Multi_Team_Architecture_Review.md) |
| LFU Cache, Alien Dictionary, Critical Connections DSA | Staff | **Done** — [LFUCache](02_DSA/05_Linked_Lists/LFUCache/LFUCache.md), [AlienDictionary](02_DSA/11_Graphs/AlienDictionary/AlienDictionary.md), [CriticalConnections](02_DSA/11_Graphs/CriticalConnections/CriticalConnections.md) |
| Principal interview loop guide | Principal | **Done** — [Interview_Loop_Guide.md](00_Interview_Prep/Principal/Interview_Loop_Guide.md) |
| Staff/Principal SelfAssessment advance criteria | Principal, Staff | **Done** — [Staff_Principal_Advance_Criteria.md](00_Interview_Prep/Levels/Staff_Principal_Advance_Criteria.md) |
| Multi-year vision / build-vs-buy | Principal | **Done** — [Multi_Year_Vision_Build_vs_Buy.md](00_Interview_Prep/Principal/Multi_Year_Vision_Build_vs_Buy.md) |
| Organization design (Conway, team topologies) | Principal | **Done** — [Organization_Design_Conway_Team_Topologies.md](00_Interview_Prep/Principal/Organization_Design_Conway_Team_Topologies.md) |
| Multi-region active-active / DR | Principal | **Done** — [Multi_Region_Active_Active_DR.md](00_Interview_Prep/Principal/Multi_Region_Active_Active_DR.md) |
| Enterprise integration patterns | Principal | **Done** — [Enterprise_Integration_ESB_iPaaS_Event_Mesh.md](00_Interview_Prep/Principal/Enterprise_Integration_ESB_iPaaS_Event_Mesh.md) |
| Executive communication | Principal | **Done** — [Executive_Communication_Board_Narrative.md](00_Interview_Prep/Principal/Executive_Communication_Board_Narrative.md) |
| Vendor evaluation rubrics | Principal | **Done** — [Vendor_Evaluation_Rubrics.md](00_Interview_Prep/Principal/Vendor_Evaluation_Rubrics.md) |
| Enterprise architecture frameworks | Principal | **Done** — [Enterprise_Architecture_Frameworks.md](00_Interview_Prep/Principal/Enterprise_Architecture_Frameworks.md) |
| Fresher OA / screening guide | Fresher | **Done** — [Fresher_Screening_and_OA_Guide.md](00_Interview_Prep/Career/Fresher_Screening_and_OA_Guide.md) |
| SDE1 interview guide | SDE1 | **Done** — [SDE1_Interview_Guide.md](00_Interview_Prep/Levels/SDE1_Interview_Guide.md) |
| Failure modes all levels | All | **Done** — Fresher, SDE1 (in guide), SDE2, Senior, Tech Lead, Staff, Principal |

---

## Maintenance note

Every new file added to this repo must be linked from the relevant level's section above, tagged `[Core]` / `[Recommended]` / `[Optional]`. No orphan files.

When topic duplication exists, the **primary** file is `[Core]`; the secondary is `[Optional] — deeper dive` (e.g. Kafka: §06 Core at Senior, §19 Optional deeper dive). **Do not split one topic across files** — each subject has a single canonical home; cross-links point to that file only.

Every `02_DSA` problem links to its pattern in [02_AlgorithmicPatterns.md](03_CodingPatterns/02_AlgorithmicPatterns.md) via the `**Pattern**` header line. The [Repo Problem Index](03_CodingPatterns/02_AlgorithmicPatterns.md#repo-problem-index-bidirectional-with-02_dsa) lists all problems by pattern.

**Related navigators (not replacements for this file):**
- [README.md](README.md) — repo overview and week-by-week schedules
- [00_Interview_Prep/README.md](00_Interview_Prep/README.md) — interview guides hub (career, mocks, Staff/Principal)
- [MASTER_INDEX.md](MASTER_INDEX.md) — topic → file → round lookup
- [01_TechGuide/00_TableOfContents.md](01_TechGuide/00_TableOfContents.md) — TechGuide keyword index
- [02_DSA/StudyGuide.md](02_DSA/StudyGuide.md) — DSA weekly problem assignments
- [05_AI/README.md](05_AI/README.md) — AI curriculum by level
