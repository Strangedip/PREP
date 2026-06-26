# Tier 3 DSA — Differentiators (Senior / Staff Level)

> **When to study**: After completing [StudyGuide.md](StudyGuide.md) Path 2 (medium mastery) and before Staff-level loops
> **Goal**: Recognize hard patterns quickly; discuss optimizations and follow-ups like a senior engineer

---

## What Makes Tier 3 Different

| Tier 1-2 | Tier 3 |
|----------|--------|
| Standard patterns (BFS, DP table) | Hybrid patterns + tight constraints |
| Single optimal solution | Multiple approaches with trade-offs |
| "Solve it" | "Solve + defend + extend" |
| 30-45 min medium | 45-60 min hard, sometimes open-ended |

---

## Tier 3 Problem Categories

### 1. Advanced Graph

| Problem | Pattern | Repo Reference |
|---------|---------|----------------|
| Alien Dictionary | Topological sort on implicit graph | [11_Graphs](11_Graphs/) |
| Word Ladder II | BFS + backtracking all shortest paths | [02_Strings](02_Strings/) |
| Network Delay Time | Dijkstra / Bellman-Ford | [11_Graphs](11_Graphs/) |
| Critical Connections | Bridges in graph (Tarjan) | Tier 3 — practice on LeetCode |
| Min Cost to Connect Points | MST (Prim/Kruskal) | Tier 3 |

**Key insight**: Know when to switch from BFS → Dijkstra → Bellman-Ford (negative weights).

---

### 2. Advanced DP

| Problem | Pattern | Repo Reference |
|---------|---------|----------------|
| Burst Balloons | Interval DP | [13_Dynamic_Programming](13_Dynamic_Programming/) |
| Regular Expression Matching | 2D DP with `*` and `.` | [02_Strings](02_Strings/) |
| Edit Distance | Classic 2D DP | [13_Dynamic_Programming](13_Dynamic_Programming/) |
| Distinct Subsequences | DP counting | Tier 3 |
| Cherry Pickup | DP on grid with state | Tier 3 |

**Key insight**: Define state precisely — `(i, j, k)` dimensions when greedy fails.

---

### 3. Data Structure Design

| Problem | Pattern | Repo Reference |
|---------|---------|----------------|
| LRU Cache | HashMap + doubly linked list | [17_Advanced_Miscellaneous](17_Advanced_Miscellaneous/) |
| LFU Cache | HashMap + freq buckets | Tier 3 |
| Design Twitter | HashMap + heap for feed | [04_SystemDesign Twitter HLD](../04_SystemDesign/02_HighLevelDesign/Twitter/Twitter.md) |
| Range Sum Query (Mutable) | Segment tree / Fenwick | [17_Advanced_Miscellaneous](17_Advanced_Miscellaneous/) |
| Insert Delete GetRandom O(1) | HashMap + ArrayList | Tier 3 |

---

### 4. Binary Search on Answer

| Problem | Pattern | Repo Reference |
|---------|---------|----------------|
| Koko Eating Bananas | BS on speed | [03_Sorting_Searching](03_Sorting_Searching/) |
| Capacity to Ship Packages | BS on capacity | Tier 3 |
| Split Array Largest Sum | BS on max sum | Tier 3 |
| Median of Two Sorted Arrays | BS on partition | Tier 3 |

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

| Problem | Pattern |
|---------|---------|
| Sliding Window Maximum | Monotonic deque |
| Shortest Subarray with Sum ≥ K | Deque + prefix sum |
| Constrained Subsequence Sum | DP + monotonic deque |

---

### 6. Concurrency (Staff Differentiator)

| Problem | Pattern | Repo Reference |
|---------|---------|----------------|
| Print In Order | Semaphore / CountDownLatch | [18_Concurrency_Multithreading](18_Concurrency_Multithreading/) |
| Producer-Consumer | BlockingQueue | [18_Concurrency_Multithreading](18_Concurrency_Multithreading/) |
| Design RW Lock | Read-write lock | [18_Concurrency_Multithreading](18_Concurrency_Multithreading/) |
| Thread Pool (design) | ExecutorService design | [§02 Java Internals](../01_TechGuide/02_Advanced_SpringBoot_Java_Internals.md) |

---

## 12-Week Tier 3 Sprint

| Week | Focus | Problems |
|------|-------|----------|
| 1-2 | Graph advanced | 6 hard graph |
| 3-4 | DP hard | 6 interval / multi-dim DP |
| 5 | Binary search on answer | 5 problems |
| 6 | Segment tree / Fenwick | 4 problems |
| 7 | Design data structures | LRU, LFU, Twitter |
| 8 | String hard | Regex, palindrome partitioning |
| 9 | Concurrency | 4 from §18 + explain JVM model |
| 10 | Mixed mock hard | 2 timed hard per session |
| 11 | Company-specific | Google/Meta hard list |
| 12 | Review weak patterns | From SelfAssessment §21 |

---

## Interview Follow-Up Questions (Prepare Answers)

1. **"What if input doesn't fit in memory?"** — Streaming, external sort, map-side reduce
2. **"What if queries are online?"** — Persistent data structure, segment tree
3. **"Distributed version?"** — Shard by key, consistent hashing — link to [04_SystemDesign](../04_SystemDesign/README.md)
4. **"Prove correctness"** — Loop invariant, DP recurrence justification
5. **"Worst case when HashMap degrades?"** — O(n) — use TreeMap or custom hash

---

## Readiness Checklist

- [ ] Solve 15+ LeetCode Hard without hints in 45 min
- [ ] Explain time/space for every solution
- [ ] Code segment tree or Dijkstra from scratch
- [ ] Handle 2 hard follow-ups per problem (stream, distributed)
- [ ] Concurrency: explain happens-before + fix race in code

**Patterns primer**: [03_CodingPatterns/02_AlgorithmicPatterns.md](../03_CodingPatterns/02_AlgorithmicPatterns.md)
