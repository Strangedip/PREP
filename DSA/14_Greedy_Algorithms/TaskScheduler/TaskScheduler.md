# Task Scheduler

## Problem Statement

You are given an array of CPU tasks `tasks`, each represented by a character from A to Z, and a cooling interval `n`. Each cycle (or interval), the CPU can complete either one task or be idle. However, identical tasks must be separated by at least `n` intervals.

Return the **minimum number of intervals** the CPU will take to finish all the given tasks.

**LeetCode**: [621. Task Scheduler](https://leetcode.com/problems/task-scheduler/)

### Examples

```
Input:  tasks = ["A","A","A","B","B","B"], n = 2
Output: 8
Explanation: A → B → idle → A → B → idle → A → B
             1    2    3     4    5    6     7    8

Input:  tasks = ["A","A","A","B","B","B"], n = 0
Output: 6
Explanation: No cooldown needed, just do all 6 tasks back-to-back.

Input:  tasks = ["A","A","A","A","A","A","B","C","D","E","F","G"], n = 2
Output: 16
Explanation: A → B → C → A → D → E → A → F → G → A → idle → idle → A → idle → idle → A
```

### Constraints
- `1 <= tasks.length <= 10^4`
- `tasks[i]` is an uppercase English letter
- `0 <= n <= 100`

---

## Why This Problem Is Important

1. Tests greedy thinking and mathematical reasoning.
2. Appears frequently at Facebook/Meta, Amazon, and Google interviews.
3. Has both a mathematical formula approach and a priority queue simulation approach — interviewers want to see which you choose and why.
4. Real-world relevance: OS scheduling, rate limiting, job queuing.

---

## Approach 1: Greedy with Math Formula (Optimal)

**Time**: O(n), **Space**: O(1) — only 26 letters

### The Key Insight

The most frequent task determines the schedule structure. If task A appears `maxFreq` times and the cooldown is `n`, then the schedule has this shape:

```
A _ _ A _ _ A _ _ A
  ↑       ↑       ↑
  n slots  n slots  (last A has no cooldown after it)
```

The structure creates `(maxFreq - 1)` "frames", each of size `(n + 1)`, plus a final partial frame containing all tasks with the maximum frequency.

### The Formula

```
totalSlots = (maxFreq - 1) × (n + 1) + countOfMaxFreqTasks
answer = max(totalSlots, tasks.length)
```

- `maxFreq`: The highest frequency of any task.
- `countOfMaxFreqTasks`: How many tasks share that maximum frequency.
- We take `max` with `tasks.length` because if there are enough diverse tasks to fill all idle slots, we never need to idle (answer = total number of tasks).

### Visual Walkthrough

```
tasks = ["A","A","A","B","B","B"], n = 2

Frequencies: A=3, B=3
maxFreq = 3
countOfMaxFreqTasks = 2 (both A and B have freq 3)

Frame structure (maxFreq - 1 = 2 frames, each of size n+1 = 3):
Frame 1: A  B  idle
Frame 2: A  B  idle
Final:   A  B

totalSlots = (3 - 1) × (2 + 1) + 2 = 2 × 3 + 2 = 8
tasks.length = 6

answer = max(8, 6) = 8

Schedule: A → B → idle → A → B → idle → A → B ✓
```

Another example where tasks fill all idle slots:

```
tasks = ["A","A","A","B","B","B","C","C","C","D","D","E"], n = 2

Frequencies: A=3, B=3, C=3, D=2, E=1
maxFreq = 3
countOfMaxFreqTasks = 3 (A, B, C all have freq 3)

totalSlots = (3 - 1) × (2 + 1) + 3 = 6 + 3 = 9
tasks.length = 12

answer = max(9, 12) = 12

Schedule: A → B → C → A → B → C → A → B → C → D → D → E ✓
(No idle time needed — enough tasks to fill all gaps)
```

### Java Implementation

```java
class Solution {
    public int leastInterval(char[] tasks, int n) {
        // Step 1: Count frequency of each task
        int[] freq = new int[26];
        for (char task : tasks) {
            freq[task - 'A']++;
        }

        // Step 2: Find the maximum frequency
        int maxFreq = 0;
        for (int f : freq) {
            maxFreq = Math.max(maxFreq, f);
        }

        // Step 3: Count how many tasks have the maximum frequency
        int countOfMaxFreqTasks = 0;
        for (int f : freq) {
            if (f == maxFreq) {
                countOfMaxFreqTasks++;
            }
        }

        // Step 4: Apply the formula
        int totalSlots = (maxFreq - 1) * (n + 1) + countOfMaxFreqTasks;

        // The answer is at least the number of tasks (no idle needed if enough variety)
        return Math.max(totalSlots, tasks.length);
    }
}
```

---

## Approach 2: Priority Queue (Max-Heap) Simulation

**Time**: O(totalTime × 26) ≈ O(totalTime), **Space**: O(1)

This approach simulates the actual scheduling process. It is more intuitive and easier to extend if the problem has additional constraints.

### Algorithm
1. Count task frequencies.
2. Push all frequencies into a max-heap.
3. In each round, try to schedule up to `n + 1` tasks (one "frame").
4. After scheduling, push remaining tasks (with decremented frequency) back into the heap.
5. If the heap is not empty but we could not fill the frame, the remaining slots are idle.

```java
class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char task : tasks) {
            freq[task - 'A']++;
        }

        // Max-heap of frequencies
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int f : freq) {
            if (f > 0) maxHeap.offer(f);
        }

        int totalTime = 0;

        while (!maxHeap.isEmpty()) {
            List<Integer> temp = new ArrayList<>();
            int frameSize = n + 1; // Each frame can hold n+1 tasks

            // Schedule up to n+1 tasks in this frame
            for (int i = 0; i < frameSize && !maxHeap.isEmpty(); i++) {
                int taskFreq = maxHeap.poll();
                if (taskFreq > 1) {
                    temp.add(taskFreq - 1); // Task still has remaining instances
                }
                totalTime++;
            }

            // Put remaining tasks back into the heap
            for (int remainingFreq : temp) {
                maxHeap.offer(remainingFreq);
            }

            // If heap is not empty, we need idle time to fill the frame
            if (!maxHeap.isEmpty()) {
                totalTime += (frameSize - (temp.size() + (frameSize - temp.size() - (maxHeap.size() > 0 ? 0 : 0))));
                // Simpler: we already counted the tasks we scheduled.
                // If we scheduled fewer than frameSize tasks, the rest are idle.
                // But we already incremented totalTime for each task.
                // We need to add idle slots:
            }

            // Actually, let's simplify the idle calculation
            if (!maxHeap.isEmpty()) {
                // We scheduled (frameSize - remaining idle slots) tasks
                // Idle slots = frameSize - number of tasks scheduled this round
                int tasksScheduled = (frameSize - maxHeap.size() >= 0) ? frameSize : frameSize; // simplify below
            }
        }

        return totalTime;
    }
}
```

Let me provide a cleaner version of the simulation:

```java
class Solution {
    public int leastInterval(char[] tasks, int n) {
        int[] freq = new int[26];
        for (char task : tasks) {
            freq[task - 'A']++;
        }

        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int f : freq) {
            if (f > 0) maxHeap.offer(f);
        }

        int totalTime = 0;

        while (!maxHeap.isEmpty()) {
            List<Integer> remaining = new ArrayList<>();
            int slots = n + 1; // Frame size
            int tasksThisRound = 0;

            // Try to fill n+1 slots with different tasks
            for (int i = 0; i < slots; i++) {
                if (!maxHeap.isEmpty()) {
                    int currentFreq = maxHeap.poll();
                    if (currentFreq > 1) {
                        remaining.add(currentFreq - 1);
                    }
                    tasksThisRound++;
                }
            }

            // Add remaining tasks back to heap
            for (int r : remaining) {
                maxHeap.offer(r);
            }

            // If heap is empty, we only used the actual tasks scheduled
            // If heap is not empty, we used the full frame (tasks + idle)
            totalTime += maxHeap.isEmpty() ? tasksThisRound : slots;
        }

        return totalTime;
    }
}
```

> **Interview Note**: The formula approach is O(n) and cleaner. The simulation approach is easier to understand and extend. Present the formula approach first, then mention the simulation as an alternative.

---

## Complexity Analysis

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Math Formula | O(n) | O(1) | n = tasks.length; only 26 frequency slots |
| Priority Queue | O(totalTime × log 26) ≈ O(totalTime) | O(1) | Simulation; totalTime can be > tasks.length |

---

## Edge Cases

| Case | tasks | n | Output | Why |
|------|-------|---|--------|-----|
| n = 0 | `["A","A","B","B"]` | 0 | 4 | No cooldown, just do all tasks |
| All same task | `["A","A","A"]` | 2 | 7 | A _ _ A _ _ A |
| All different | `["A","B","C","D"]` | 3 | 4 | No idle needed |
| Single task | `["A"]` | 5 | 1 | Just one task |
| Many tasks, small n | `["A","A","B","B","C","C"]` | 1 | 6 | A B A B C C, no idle |

---

## Interview Tips

1. **Identify the greedy choice**: "The most frequent task determines the minimum schedule length because it creates the most mandatory idle slots."
2. **Derive the formula step by step**: Draw the frame structure on the whiteboard. Show the `(maxFreq - 1) × (n + 1) + countOfMaxFreq` derivation visually.
3. **Explain the max with tasks.length**: "If we have enough diverse tasks, we never idle — the answer is just the total number of tasks."
4. **Know both approaches**: Formula is faster and cleaner; simulation is more flexible for variants.

### Common Follow-Up Questions
- "What if tasks have priorities?" → Use a priority queue with a custom comparator that considers both frequency and priority.
- "What if the cooldown is per-task-type (different n for different tasks)?" → Simulation approach with per-task cooldown tracking.
- "Return the actual schedule, not just the length." → Use the simulation approach and record which task is executed at each step.

---

## Related Problems

| Problem | Connection | LeetCode |
|---------|-----------|----------|
| Rearrange String K Distance Apart | Same greedy approach, arrange chars with k distance | [358](https://leetcode.com/problems/rearrange-string-k-distance-apart/) |
| Reorganize String | Special case with k=2 (no adjacent duplicates) | [767](https://leetcode.com/problems/reorganize-string/) |
| Maximum Frequency Stack | Related frequency-based greedy/stack | [895](https://leetcode.com/problems/maximum-frequency-stack/) |

---

**Pattern**: Greedy + Math / Priority Queue
**Difficulty**: Medium
**Must-Know**: Yes — frequently asked at Meta, Amazon, Google

