# Tier 3 DSA — Differentiators (Senior / Staff Level)

> **You are here**: Staff Engineer — DSA hard patterns
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md#staff-engineer) | **Prerequisites**: [StudyGuide Path 3](StudyGuide.md) | **Next**: [Staff Failure Modes](../00_Interview_Prep/Levels/Staff_Failure_Modes.md)
> **Pattern**: [Algorithmic Patterns](../03_CodingPatterns/02_AlgorithmicPatterns.md) | **Catalog**: [03_CodingPatterns](../03_CodingPatterns/README.md)

> **When to study**: After [StudyGuide Path 3](StudyGuide.md) success metrics and before Staff loops
> **Goal**: Recognize hard patterns quickly; defend optimizations and follow-ups

---

## What Makes Tier 3 Different

| Tier 1-2 | Tier 3 |
|----------|--------|
| Standard patterns (BFS, DP table) | Hybrid patterns + tight constraints |
| Single optimal solution | Multiple approaches with trade-offs |
| "Solve it" | "Solve + defend + extend" |
| 30-45 min medium | 45-60 min hard, sometimes open-ended |

---

## Tier 3 Problem Categories (all in this repo)

### 1. Advanced Graph

| Problem | Pattern | Repo file |
|---------|---------|-----------|
| Alien Dictionary | Topological sort | [AlienDictionary](11_Graphs/AlienDictionary/AlienDictionary.md) |
| Word Ladder II | BFS layers + backtrack | [WordLadderII](11_Graphs/WordLadderII/WordLadderII.md) |
| Network Delay Time | Dijkstra | [NetworkDelayTime](11_Graphs/NetworkDelayTime/NetworkDelayTime.md) |
| Critical Connections | Tarjan bridges | [CriticalConnections](11_Graphs/CriticalConnections/CriticalConnections.md) |
| Min Cost to Connect Points | Kruskal MST + UF | [MinCostToConnectPoints](11_Graphs/MinCostToConnectPoints/MinCostToConnectPoints.md) |
| Dijkstra (general) | Weighted shortest path | [DijkstraAlgorithm](11_Graphs/DijkstraAlgorithm/DijkstraAlgorithm.md) |

**Key insight**: BFS (unweighted) → Dijkstra (non-negative) → Bellman-Ford (negative edges).

---

### 2. Advanced DP

| Problem | Pattern | Repo file |
|---------|---------|-----------|
| Burst Balloons | Interval DP | [BurstBalloons](13_Dynamic_Programming/BurstBalloons/BurstBalloons.md) |
| Regular Expression Matching | 2D DP with `*` | [RegularExpressionMatching](02_Strings/RegularExpressionMatching/RegularExpressionMatching.md) |
| Edit Distance | Classic 2D DP | [EditDistance](13_Dynamic_Programming/EditDistance/EditDistance.md) |
| Distinct Subsequences | DP counting | [DistinctSubsequences](13_Dynamic_Programming/DistinctSubsequences/DistinctSubsequences.md) |
| Cherry Pickup | 3D / two-walker DP | [CherryPickup](13_Dynamic_Programming/CherryPickup/CherryPickup.md) |
| Constrained Subsequence Sum | DP + monotonic deque | [ConstrainedSubsequenceSum](13_Dynamic_Programming/ConstrainedSubsequenceSum/ConstrainedSubsequenceSum.md) |
| Advanced DP catalog | Interval/bitmask/tree | [AdvancedDP](13_Dynamic_Programming/AdvancedDP/AdvancedDP.md) |

---

### 3. Data Structure Design

| Problem | Pattern | Repo file |
|---------|---------|-----------|
| LRU Cache | HashMap + DLL | [LRUCache](05_Linked_Lists/LRUCache/LRUCache.md) |
| LFU Cache | HashMap + freq buckets | [LFUCache](05_Linked_Lists/LFUCache/LFUCache.md) |
| Insert Delete GetRandom O(1) | HashMap + ArrayList | [RandomizedSet](17_Advanced_Miscellaneous/RandomizedSet/RandomizedSet.md) |
| Range Sum Query (Mutable) | Segment tree | [SegmentTree](17_Advanced_Miscellaneous/SegmentTree/SegmentTree.md) |
| Range Sum (sum-only) | Fenwick tree | [FenwickTree](17_Advanced_Miscellaneous/FenwickTree/FenwickTree.md) |
| Design Twitter (system) | Feed + fan-out | [Twitter HLD](../04_SystemDesign/02_HighLevelDesign/Twitter/Twitter.md) |

---

### 4. Binary Search on Answer

| Problem | Pattern | Repo file |
|---------|---------|-----------|
| Koko Eating Bananas | BS on speed | [KokoEatingBananas](03_Sorting_Searching/KokoEatingBananas/KokoEatingBananas.md) |
| Capacity to Ship Packages | BS on capacity | [CapacityToShipPackages](03_Sorting_Searching/CapacityToShipPackages/CapacityToShipPackages.md) |
| Split Array Largest Sum | BS on max sum | [SplitArrayLargestSum](03_Sorting_Searching/SplitArrayLargestSum/SplitArrayLargestSum.md) |
| Median of Two Sorted Arrays | BS on partition | [MedianOfTwoSortedArrays](03_Sorting_Searching/MedianOfTwoSortedArrays/MedianOfTwoSortedArrays.md) |

**Template**:
```java
int lo = minPossible, hi = maxPossible;
while (lo < hi) {
    int mid = lo + (hi - lo) / 2;
    if (canAchieve(mid)) hi = mid;
    else lo = mid + 1;
}
return lo;
```

---

### 5. Sliding Window / Monotonic Structure

| Problem | Pattern | Repo file |
|---------|---------|-----------|
| Sliding Window Maximum | Monotonic deque | [SlidingWindowMaximum](04_Sliding_Window_Two_Pointers/SlidingWindowMaximum/SlidingWindowMaximum.md) |
| Shortest Subarray with Sum ≥ K | Deque + prefix sum | [ShortestSubarraySumAtLeastK](04_Sliding_Window_Two_Pointers/ShortestSubarraySumAtLeastK/ShortestSubarraySumAtLeastK.md) |
| Constrained Subsequence Sum | DP + monotonic deque | [ConstrainedSubsequenceSum](13_Dynamic_Programming/ConstrainedSubsequenceSum/ConstrainedSubsequenceSum.md) |

---

### 6. Concurrency (Staff Differentiator)

| Problem | Pattern | Repo file |
|---------|---------|-----------|
| Print In Order | `CountDownLatch` / semaphores | [PrintInOrder](18_Concurrency_Multithreading/PrintInOrder/PrintInOrder.md) |
| Producer-Consumer | `BlockingQueue` | [ProducerConsumer](18_Concurrency_Multithreading/ProducerConsumer/ProducerConsumer.md) |
| Read-Write Lock | Readers-writers | [ReadWriteLock](18_Concurrency_Multithreading/ReadWriteLock/ReadWriteLock.md) |
| Thread pool (design) | `ExecutorService` | [§02 Java Internals](../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) |

---

## 12-Week Tier 3 Sprint

| Week | Focus | Problems in repo |
|------|-------|------------------|
| 1-2 | Graph advanced | NetworkDelayTime, MinCostToConnectPoints, CriticalConnections, WordLadderII, AlienDictionary |
| 3-4 | DP hard | BurstBalloons, Regex, DistinctSubsequences, CherryPickup, EditDistance |
| 5 | Binary search on answer | CapacityToShip, SplitArray, MedianTwoArrays, Koko |
| 6 | Segment / Fenwick | SegmentTree, FenwickTree |
| 7 | Design structures | LRU, LFU, RandomizedSet |
| 8 | String hard | RegularExpressionMatching, MinimumWindowSubstring |
| 9 | Concurrency | PrintInOrder, ProducerConsumer, ReadWriteLock |
| 10 | Mixed mock hard | 2 timed hard per session |
| 11 | Company-specific | Google/Meta hard list + [Companies.md](../00_Interview_Prep/Core/Companies.md) |
| 12 | Review weak patterns | [SelfAssessment](../00_Interview_Prep/Core/SelfAssessment.md) §21 |

---

## Interview Follow-Up Questions (Prepare Answers)

1. **"What if input doesn't fit in memory?"** — Streaming, external sort, map-side reduce
2. **"What if queries are online?"** — Persistent segment tree / Fenwick
3. **"Distributed version?"** — Shard by key — [Distributed Cache HLD](../04_SystemDesign/02_HighLevelDesign/DistributedCache/DistributedCache.md)
4. **"Prove correctness"** — Loop invariant, DP recurrence
5. **"Worst case when HashMap degrades?"** — O(n) buckets — TreeMap fallback

---

## Readiness Checklist

- [ ] All Tier 3 tables above — at least one problem solved per row
- [ ] Explain time/space for every solution
- [ ] Code Dijkstra or segment tree from scratch
- [ ] 2 hard follow-ups per problem (stream, distributed)
- [ ] Concurrency: happens-before + fix race in code

**Patterns primer**: [02_AlgorithmicPatterns.md](../03_CodingPatterns/02_AlgorithmicPatterns.md)
