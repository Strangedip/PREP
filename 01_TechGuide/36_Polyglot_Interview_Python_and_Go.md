# Section 36: Polyglot Interview Prep — Python & Go

> **Level**: MID+ — When the job description says "polyglot" or the round uses Python/Go
> **Primary stack**: Java — use this section to **pass** Python/Go screens, not to switch stacks

> **You are here**: SDE2 — Technical Skills
> **Roadmap**: [Developer Master Roadmap](../ROADMAP.md) | **Prerequisites**: [35_SQL_Fundamentals.md](35_SQL_Fundamentals.md) | **Next**: [37_TypeScript_and_Frontend_Landscape.md](37_TypeScript_and_Frontend_Landscape.md)

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

---

## §36.10 Production & Interview Depth — Polyglot Teams in Indian Orgs

At Google Hyderabad, VMware/Broadcom, and cloud-native startups, **round 1 may be Python** even when the JD says Java. Infra/platform squads hire **Go for operators and sidecars** while product APIs stay Spring Boot 3.x. Goal: pass the screen, then steer home to your JVM depth ([02_Advanced_SpringBoot_Java_Internals.md](./02_Advanced_SpringBoot_Java_Internals.md)).

### When Interviewers Pick Python vs Go

| Signal in JD / Round | Likely Language | Your Strategy |
|---------------------|-----------------|---------------|
| "Data platform, ML adjacency" | Python | `collections`, `heapq`, Big-O narration |
| "Kubernetes controller, CLI tool" | Go | goroutines, `err != nil`, slices |
| "Full-stack product backend" | Java or choice | Ask; default Python for speed of writing |
| "Distributed systems" | Any | Draw diagram first — [07_System_Design.md](./07_System_Design.md) |

### Python DSA — Patterns That Score in 45 Minutes

```python
from collections import deque

def shortest_path_grid(grid: list[list[int]]) -> int:
    """BFS — PhonePe-style matrix shortest path."""
    q, seen = deque([(0, 0, 0)]), {(0, 0)}
    rows, cols = len(grid), len(grid[0])
    while q:
        r, c, d = q.popleft()
        if (r, c) == (rows - 1, cols - 1): return d
        for dr, dc in ((0,1),(1,0),(0,-1),(-1,0)):
            nr, nc = r + dr, c + dc
            if 0 <= nr < rows and 0 <= nc < cols and grid[nr][nc] == 0 and (nr, nc) not in seen:
                seen.add((nr, nc)); q.append((nr, nc, d + 1))
    return -1
```

### Go — Concurrency Question Template

```go
func fetchAll(ctx context.Context, urls []string) ([]Result, error) {
    results := make([]Result, len(urls))
    g, ctx := errgroup.WithContext(ctx)
    for i, url := range urls {
        i, url := i, url
        g.Go(func() error {
            r, err := fetch(ctx, url)
            if err != nil { return err }
            results[i] = r
            return nil
        })
    }
    return results, g.Wait()
}
```

Compare aloud to Java: `CompletableFuture.allOf` or virtual-thread executor — [15_Java_Collections_Concurrency_DeepDive.md](./15_Java_Collections_Concurrency_DeepDive.md).

### Java vs Polyglot — Production Trade-off Table

| Factor | Stay Java | Add Python/Go |
|--------|-----------|---------------|
| **Team skill density** | 80+ Spring devs in Bangalore | Small platform cell |
| **Interop** | Single artifact, shared libraries | gRPC/REST contracts — [04_API_Design_REST.md](./04_API_Design_REST.md) |
| **Hiring** | Mass market India supply | Niche premium for Go infra |
| **Interview honesty** | "I'd prototype in Python, ship in Java" | Shows maturity, not stack flip-flop |

**Must-say keywords**: GIL vs goroutine, `errgroup`, `defaultdict`/deque, "same algorithm different syntax", production boundary in Java.
