# Section 36: Polyglot Interview Prep — Python & Go

> **Level**: MID+ — When the job description says "polyglot" or the round uses Python/Go
> **Primary stack**: Java — use this section to **pass** Python/Go screens, not to switch stacks

---

## 36.1 Why Polyglot Rounds Exist

Companies test whether you can **think in algorithms** beyond one language. Java developers often face:
- **Python** — data science teams, startups, Google/Meta phone screens
- **Go** — cloud-native, infra, Kubernetes ecosystem roles

**Strategy**: Know syntax + idioms for DSA and concurrency basics. Deep framework knowledge stays in Java.

---

## 36.2 Python — DSA Essentials

```python
# Collections
from collections import defaultdict, deque, Counter
from heapq import heappush, heappop

# Two sum pattern
def two_sum(nums, target):
    seen = {}
    for i, n in enumerate(nums):
        if target - n in seen:
            return [seen[target - n], i]
        seen[n] = i

# BFS
def bfs(graph, start):
    queue = deque([start])
    visited = {start}
    while queue:
        node = queue.popleft()
        for neighbor in graph[node]:
            if neighbor not in visited:
                visited.add(neighbor)
                queue.append(neighbor)

# DFS
def dfs(graph, node, visited=None):
    if visited is None:
        visited = set()
    visited.add(node)
    for neighbor in graph[node]:
        if neighbor not in visited:
            dfs(graph, neighbor, visited)

# Sorting: nums.sort() or sorted(nums, key=lambda x: x[1])
# List comprehension: [x*2 for x in nums if x > 0]
```

| Java | Python |
|------|--------|
| `HashMap` | `dict` / `defaultdict` |
| `ArrayList` | `list` |
| `Queue` | `deque` |
| `null` | `None` |
| `for (int i : arr)` | `for i in arr` |
| `arr.length` | `len(arr)` |

**Interview tip**: Use `//` for integer division in Python 3. Prefer `collections` over manual loops.

---

## 36.3 Python — Concurrency (Brief)

```python
import asyncio

async def fetch_all(urls):
    tasks = [fetch(url) for url in urls]
    return await asyncio.gather(*tasks)

# Threading for I/O-bound (not CPU-bound — GIL limits CPU parallelism)
from concurrent.futures import ThreadPoolExecutor
with ThreadPoolExecutor(max_workers=10) as ex:
    results = list(ex.map(fetch, urls))
```

**GIL**: One thread executes Python bytecode at a time. Use **multiprocessing** for CPU-bound work, **asyncio** for I/O.

---

## 36.4 Go — DSA Essentials

```go
package main

import (
    "fmt"
    "sort"
)

// Map
seen := make(map[int]bool)

// Slice (dynamic array)
nums := []int{1, 2, 3}
nums = append(nums, 4)

// Two sum
func twoSum(nums []int, target int) []int {
    seen := make(map[int]int)
    for i, n := range nums {
        if j, ok := seen[target-n]; ok {
            return []int{j, i}
        }
        seen[n] = i
    }
    return nil
}

// Sort
sort.Ints(nums)
sort.Slice(pairs, func(i, j int) bool {
    return pairs[i][1] < pairs[j][1]
})
```

| Java | Go |
|------|-----|
| `class` | `struct` + methods |
| `interface` | `interface` (implicit satisfaction) |
| `extends` | composition |
| `null` | `nil` |
| `try/catch` | `if err != nil` |
| `new ArrayList<>()` | `make([]int, 0)` or `[]int{}` |

**Error handling**: Go returns `(result, error)` — always check `err != nil`.

---

## 36.5 Go — Concurrency (Goroutines & Channels)

```go
// Goroutine + channel
ch := make(chan int, 10) // buffered
go func() { ch <- 42 }()
val := <-ch

// WaitGroup
var wg sync.WaitGroup
for _, url := range urls {
    wg.Add(1)
    go func(u string) {
        defer wg.Done()
        fetch(u)
    }(url)
}
wg.Wait()

// Context for cancellation/timeouts
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()
```

**Interview angle**: Goroutines are cheap (vs Java threads). Channels for communication; `sync.Mutex` when sharing memory.

---

## 36.6 When to Say "I'd Use Java Here"

It's acceptable to say: *"In production I'd implement this in Java with CompletableFuture, but in Go I'd use goroutines and channels."* Shows polyglot awareness without pretending expertise.

---

## 36.7 Interview Quick Reference

| Question | Answer |
|----------|--------|
| Python list vs tuple? | List mutable; tuple immutable (hashable, faster). |
| Python GIL? | Limits CPU parallelism in threads; use multiprocessing or asyncio. |
| Go vs Java concurrency? | Go: goroutines + channels. Java: threads + ExecutorService. |
| Go interface? | Implicit — no `implements` keyword. |
| Python `defaultdict`? | Auto-creates missing keys — great for graphs, frequency maps. |
| Go slice vs array? | Array fixed size; slice is dynamic view with len/cap. |

**Practice**: Re-solve 5 medium DSA problems from [02_DSA](../02_DSA/README.md) in Python or Go on LeetCode.
