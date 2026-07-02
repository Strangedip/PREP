# Alien Dictionary — LeetCode 269

> **You are here**: Staff Engineer — DSA (graph)
> **Roadmap**: [Developer Master Roadmap](../../../ROADMAP.md) | **Prerequisites**: [Topological Sort](../TopologicalSort/TopologicalSort.md), [Course Schedule](../CourseSchedule/CourseSchedule.md) | **Next**: [Critical Connections](../CriticalConnections/CriticalConnections.md)
> **Pattern**: [Topological Sort](../../../03_CodingPatterns/02_AlgorithmicPatterns.md#pattern-15-topological-sort) | **Catalog**: [Algorithmic Patterns](../../../03_CodingPatterns/02_AlgorithmicPatterns.md)

## Problem Statement

Given a sorted list of words in an **alien language**, derive the character order. Words are sorted lexicographically by the rules of this unknown language.

Return a string of unique characters in order. If invalid (cycle), return `""`.

**Example:**
```
Input: words = ["wrt","wrf","er","ett","rftt"]
Output: "wertf"

Input: words = ["z","x"]
Output: "zx"

Input: words = ["z","x","z"]
Output: ""  // cycle
```

---

## Approach: Build graph + topological sort

### Step 1: Compare adjacent words

For each pair `words[i]`, `words[i+1]`:
- Find first differing character at position `j`
- Edge: `words[i][j] → words[i+1][j]` (first comes before second)
- **Invalid**: `words[i]` starts with `words[i+1]` but is longer (e.g. `"abc"` before `"ab"`)

### Step 2: Topological sort (Kahn's BFS)

- In-degree count per character
- Queue nodes with in-degree 0
- Append to result; reduce neighbors' in-degree
- If result length ≠ unique char count → cycle → `""`

### Complexity

- **Time**: O(C) where C = total characters across all words
- **Space**: O(1) alphabet (26) or O(U) unique chars

---

## Comparison with [Course Schedule](../CourseSchedule/CourseSchedule.md)

| | Course Schedule | Alien Dictionary |
|---|-----------------|------------------|
| Nodes | Course IDs | Characters |
| Edges | Prerequisites | Derived from word order |
| Cycle | Cannot finish | Invalid language |

Same Kahn BFS template as [TopologicalSort](../TopologicalSort/TopologicalSort.md).

---

## Java solution sketch


#### Example Flow

**Step flow (mermaid):**

```mermaid
flowchart TD
    START["Input: words=["wrt","wrf","er","ett","rftt"]"]
    START --> ENQ["Enqueue start node"]
    ENQ --> Q{"Queue empty?"}
    Q -->|no| DEQ["Dequeue front"]
    DEQ --> NEI["Visit unvisited neighbors"]
    NEI --> ENQ2["Enqueue neighbors"]
    ENQ2 --> Q
    Q -->|yes| DONE["Return shortest / order"]
```

**Walkthrough (same example):**

```
Example: words=["wrt","wrf","er","ett","rftt"] → "wertf"
Approach: : Build graph + topological sort

Enqueue start node/level
Process neighbors level by level
First reach target = shortest path
```
```java
public String alienOrder(String[] words) {
    Map<Character, Set<Character>> graph = new HashMap<>();
    Map<Character, Integer> indegree = new HashMap<>();
    for (String w : words) {
        for (char c : w.toCharArray()) {
            graph.putIfAbsent(c, new HashSet<>());
            indegree.putIfAbsent(c, 0);
        }
    }
    for (int i = 0; i < words.length - 1; i++) {
        String w1 = words[i], w2 = words[i + 1];
        if (w1.length() > w2.length() && w1.startsWith(w2)) {
            return "";
        }
        for (int j = 0; j < Math.min(w1.length(), w2.length()); j++) {
            char c1 = w1.charAt(j), c2 = w2.charAt(j);
            if (c1 != c2) {
                if (!graph.get(c1).contains(c2)) {
                    graph.get(c1).add(c2);
                    indegree.put(c2, indegree.get(c2) + 1);
                }
                break;
            }
        }
    }
    Queue<Character> q = new LinkedList<>();
    for (char c : indegree.keySet()) {
        if (indegree.get(c) == 0) q.offer(c);
    }
    StringBuilder sb = new StringBuilder();
    while (!q.isEmpty()) {
        char c = q.poll();
        sb.append(c);
        for (char next : graph.get(c)) {
            indegree.put(next, indegree.get(next) - 1);
            if (indegree.get(next) == 0) q.offer(next);
        }
    }
    return sb.length() == indegree.size() ? sb.toString() : "";
}
```

Full code: [AlienDictionary.java](AlienDictionary.java)

---

## Edge Cases

1. **Single word** — return unique chars in any valid order (often any order OK if no edges)
2. **Prefix invalid** — `"abc"` before `"ab"` → `""`
3. **Cycle** — `"z"` before `"x"` and implied `x` before `z`
4. **Disconnected graph** — multiple independent char groups; topo still works

---

## Interview Tips

1. State graph construction before coding topo sort
2. Mention duplicate edge handling (don't double-count indegree)
3. Follow-up: **multiple valid orders** — return any one

## Related

- [Word Ladder](../WordLadder/WordLadder.md) — BFS on words
- [Tier3 Differentiators](../../Tier3_Differentiators.md)
