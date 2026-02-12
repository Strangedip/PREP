# Algorithmic Coding Patterns — The Complete Interview Playbook

> **Purpose**: Master the 16 fundamental coding patterns that solve 95% of FAANG DSA interview questions.
> **Approach**: Each pattern includes the mental model, the template, when to recognize it, complete code, and a problem mapping.
> **Target Level**: Lead Software Engineer (SDE-2/SDE-3)

---

## Why Patterns Matter More Than Problems

The difference between solving 500 LeetCode problems randomly and being interview-ready is **pattern recognition**. There are roughly 16 core algorithmic patterns. Once you internalize the template for each, any new problem becomes a matter of recognizing which pattern applies and adapting the template.

**The interview flow should be:**
1. Read the problem → 2. Recognize the pattern → 3. Recall the template → 4. Adapt to constraints → 5. Code it cleanly

---

## Table of Contents

| # | Pattern | When to Use | Difficulty |
|---|---------|------------|-----------|
| 1 | [Two Pointers](#pattern-1-two-pointers) | Sorted array/string, pair/triplet finding | Easy-Medium |
| 2 | [Sliding Window](#pattern-2-sliding-window) | Contiguous subarray/substring optimization | Medium |
| 3 | [Fast & Slow Pointers](#pattern-3-fast--slow-pointers) | Cycle detection, middle finding | Medium |
| 4 | [Merge Intervals](#pattern-4-merge-intervals) | Overlapping intervals, scheduling | Medium |
| 5 | [Cyclic Sort](#pattern-5-cyclic-sort) | Array with values in range [0, n] or [1, n] | Easy-Medium |
| 6 | [In-Place Linked List Reversal](#pattern-6-in-place-linked-list-reversal) | Reverse linked list or sublist | Medium |
| 7 | [BFS (Breadth-First Search)](#pattern-7-bfs-breadth-first-search) | Shortest path, level-order, minimum steps | Medium |
| 8 | [DFS (Depth-First Search)](#pattern-8-dfs-depth-first-search) | Tree paths, graph exploration, exhaustive search | Medium |
| 9 | [Two Heaps](#pattern-9-two-heaps) | Median finding, scheduling with two groups | Hard |
| 10 | [Subsets / Backtracking](#pattern-10-subsets--backtracking) | Permutations, combinations, power set | Medium-Hard |
| 11 | [Modified Binary Search](#pattern-11-modified-binary-search) | Sorted/rotated array, search space reduction | Medium |
| 12 | [Top K Elements](#pattern-12-top-k-elements) | K largest/smallest/frequent elements | Medium |
| 13 | [K-Way Merge](#pattern-13-k-way-merge) | Merge K sorted lists/arrays | Hard |
| 14 | [Monotonic Stack](#pattern-14-monotonic-stack) | Next greater/smaller element, histogram problems | Medium-Hard |
| 15 | [Topological Sort](#pattern-15-topological-sort) | Dependency ordering, course scheduling | Medium |
| 16 | [Dynamic Programming Patterns](#pattern-16-dynamic-programming-patterns) | Optimization, counting, decision-making | Medium-Hard |

---

## Pattern 1: Two Pointers

### Mental Model
Place two pointers at specific positions in a sorted array and move them toward each other (or in the same direction) based on a condition. This replaces nested loops (O(n²)) with a single pass (O(n)).

### When to Recognize This Pattern
- The array or string is **sorted** (or you can sort it).
- You need to find a **pair** or **triplet** that satisfies a condition.
- You need to compare elements from **both ends** or **two positions**.
- Problem mentions "in-place" and "O(1) extra space."

### Template: Opposite Direction (Pair Sum)

```java
public int[] twoSumSorted(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    
    while (left < right) {
        int currentSum = arr[left] + arr[right];
        
        if (currentSum == target) {
            return new int[]{left, right};    // Found the pair
        } else if (currentSum < target) {
            left++;                            // Need a bigger sum
        } else {
            right--;                           // Need a smaller sum
        }
    }
    
    return new int[]{-1, -1};  // No pair found
}
```

**Why it works**: Since the array is sorted, moving `left++` increases the sum and `right--` decreases it. This guarantees we explore all possibilities in O(n).

### Template: Same Direction (Remove Duplicates)

```java
public int removeDuplicates(int[] nums) {
    if (nums.length == 0) return 0;
    
    int slow = 0;  // Points to the position where the next unique element should go
    
    for (int fast = 1; fast < nums.length; fast++) {
        if (nums[fast] != nums[slow]) {
            slow++;
            nums[slow] = nums[fast];
        }
    }
    
    return slow + 1;  // Length of unique portion
}
```

**Why it works**: `slow` tracks the boundary of "processed" elements. `fast` scans ahead to find the next element that belongs.

### Template: Three Sum (Reducing to Two Sum)

```java
public List<List<Integer>> threeSum(int[] nums, int target) {
    Arrays.sort(nums);
    List<List<Integer>> result = new ArrayList<>();
    
    for (int i = 0; i < nums.length - 2; i++) {
        // Skip duplicates for the first element
        if (i > 0 && nums[i] == nums[i - 1]) continue;
        
        int left = i + 1, right = nums.length - 1;
        int remaining = target - nums[i];
        
        while (left < right) {
            int twoSum = nums[left] + nums[right];
            
            if (twoSum == remaining) {
                result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                // Skip duplicates
                while (left < right && nums[left] == nums[left + 1]) left++;
                while (left < right && nums[right] == nums[right - 1]) right--;
                left++;
                right--;
            } else if (twoSum < remaining) {
                left++;
            } else {
                right--;
            }
        }
    }
    
    return result;
}
```

**Key insight**: Fix one element, then use Two Pointers on the remaining subarray. This reduces O(n³) to O(n²).

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Two Sum II (Sorted) | Opposite pointers | Sum equals target |
| Three Sum | Fix one + two pointers | Reduce to 2-sum |
| Container With Most Water | Opposite pointers | Move the shorter side |
| Trapping Rain Water | Opposite pointers | Track left/right max |
| Remove Duplicates | Same direction | Slow/fast partitioning |
| Move Zeroes | Same direction | Partition non-zeros |
| Sort Colors (Dutch National Flag) | Three pointers | Low/mid/high partition |
| Valid Palindrome | Opposite pointers | Compare from ends |
| Squares of Sorted Array | Opposite pointers | Merge from ends |

---

## Pattern 2: Sliding Window

### Mental Model
Maintain a "window" (a contiguous subarray or substring) and slide it across the array. Expand the window by moving the right boundary, and contract it by moving the left boundary when a constraint is violated. The window maintains a running state (sum, count, set of characters) that is updated incrementally.

### When to Recognize This Pattern
- Problem asks for a **contiguous subarray or substring**.
- Problem asks for "longest," "shortest," "maximum," or "minimum" of something contiguous.
- You need to satisfy a constraint (sum ≤ K, at most K distinct characters, etc.).

### Template: Fixed-Size Window

```java
public int maxSumSubarray(int[] nums, int k) {
    int windowSum = 0, maxSum = Integer.MIN_VALUE;
    
    for (int i = 0; i < nums.length; i++) {
        windowSum += nums[i];                   // Add element entering window
        
        if (i >= k) {
            windowSum -= nums[i - k];           // Remove element leaving window
        }
        
        if (i >= k - 1) {
            maxSum = Math.max(maxSum, windowSum); // Window is full, record result
        }
    }
    
    return maxSum;
}
```

### Template: Variable-Size Window (Longest)

```java
// Find the longest substring with at most K distinct characters
public int longestSubstringKDistinct(String s, int k) {
    Map<Character, Integer> charCount = new HashMap<>();
    int left = 0, maxLength = 0;
    
    for (int right = 0; right < s.length(); right++) {
        // EXPAND: Add right character to window
        char rightChar = s.charAt(right);
        charCount.merge(rightChar, 1, Integer::sum);
        
        // CONTRACT: Shrink window while constraint is violated
        while (charCount.size() > k) {
            char leftChar = s.charAt(left);
            charCount.merge(leftChar, -1, Integer::sum);
            if (charCount.get(leftChar) == 0) {
                charCount.remove(leftChar);
            }
            left++;
        }
        
        // RECORD: Update result (window is valid here)
        maxLength = Math.max(maxLength, right - left + 1);
    }
    
    return maxLength;
}
```

### Template: Variable-Size Window (Shortest)

```java
// Find the smallest subarray with sum >= target
public int minSubArrayLen(int target, int[] nums) {
    int left = 0, windowSum = 0;
    int minLength = Integer.MAX_VALUE;
    
    for (int right = 0; right < nums.length; right++) {
        windowSum += nums[right];                // EXPAND
        
        // CONTRACT: Shrink window while constraint is SATISFIED (to find minimum)
        while (windowSum >= target) {
            minLength = Math.min(minLength, right - left + 1); // RECORD
            windowSum -= nums[left];
            left++;
        }
    }
    
    return minLength == Integer.MAX_VALUE ? 0 : minLength;
}
```

### Template: Minimum Window Substring

```java
// Find the smallest window in s that contains all characters of t
public String minWindow(String s, String t) {
    Map<Character, Integer> need = new HashMap<>();
    for (char c : t.toCharArray()) {
        need.merge(c, 1, Integer::sum);
    }
    
    Map<Character, Integer> window = new HashMap<>();
    int left = 0, formed = 0;
    int required = need.size();  // Number of unique chars in t that must be matched
    int[] result = {-1, 0, 0};  // {length, left, right}
    
    for (int right = 0; right < s.length(); right++) {
        char c = s.charAt(right);
        window.merge(c, 1, Integer::sum);
        
        // Check if current character's frequency matches requirement
        if (need.containsKey(c) && window.get(c).intValue() == need.get(c).intValue()) {
            formed++;
        }
        
        // Contract window while all characters are found
        while (formed == required) {
            // Update result if this window is smaller
            if (result[0] == -1 || right - left + 1 < result[0]) {
                result[0] = right - left + 1;
                result[1] = left;
                result[2] = right;
            }
            
            // Remove leftmost character
            char leftChar = s.charAt(left);
            window.merge(leftChar, -1, Integer::sum);
            if (need.containsKey(leftChar) && 
                window.get(leftChar) < need.get(leftChar)) {
                formed--;
            }
            left++;
        }
    }
    
    return result[0] == -1 ? "" : s.substring(result[1], result[2] + 1);
}
```

### The "At Most K" to "Exactly K" Trick

Many problems ask for "exactly K" distinct elements. The trick is:

```
exactlyK(arr, k) = atMostK(arr, k) - atMostK(arr, k - 1)
```

```java
public int subarraysWithExactlyKDistinct(int[] nums, int k) {
    return atMostK(nums, k) - atMostK(nums, k - 1);
}

private int atMostK(int[] nums, int k) {
    Map<Integer, Integer> count = new HashMap<>();
    int left = 0, result = 0;
    
    for (int right = 0; right < nums.length; right++) {
        count.merge(nums[right], 1, Integer::sum);
        
        while (count.size() > k) {
            count.merge(nums[left], -1, Integer::sum);
            if (count.get(nums[left]) == 0) count.remove(nums[left]);
            left++;
        }
        
        result += right - left + 1;  // All subarrays ending at right with at most K distinct
    }
    
    return result;
}
```

### Problem Mapping

| Problem | Window Type | Key Idea |
|---------|-----------|----------|
| Maximum Sum Subarray of Size K | Fixed | Running sum |
| Longest Substring Without Repeating | Variable (longest) | HashSet for uniqueness |
| Longest Substring with K Distinct | Variable (longest) | HashMap for char count |
| Minimum Size Subarray Sum | Variable (shortest) | Shrink when sum ≥ target |
| Minimum Window Substring | Variable (shortest) | Freq matching with `formed` counter |
| Permutation in String | Fixed (size = pattern) | Frequency comparison |
| Max Consecutive Ones III | Variable (longest) | At most K zeros |
| Fruit Into Baskets | Variable (longest) | At most 2 distinct |
| Sliding Window Maximum | Fixed + Deque | Monotonic deque |

---

## Pattern 3: Fast & Slow Pointers

### Mental Model
Two pointers moving at different speeds through a linked list or array. The fast pointer moves 2 steps for every 1 step of the slow pointer. When fast reaches the end, slow is at the middle. If there is a cycle, they will meet.

### When to Recognize This Pattern
- **Cycle detection** in a linked list or array.
- Finding the **middle** of a linked list.
- Finding the **start of a cycle**.
- Detecting if a number is a "happy number."

### Template: Cycle Detection (Floyd's Algorithm)

```java
public boolean hasCycle(ListNode head) {
    ListNode slow = head, fast = head;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;          // 1 step
        fast = fast.next.next;     // 2 steps
        
        if (slow == fast) {
            return true;           // Cycle detected
        }
    }
    
    return false;                  // No cycle (fast reached end)
}
```

### Template: Find Cycle Start

```java
public ListNode detectCycleStart(ListNode head) {
    ListNode slow = head, fast = head;
    
    // Phase 1: Detect cycle
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
        if (slow == fast) break;
    }
    
    if (fast == null || fast.next == null) return null;  // No cycle
    
    // Phase 2: Find start of cycle
    // Move slow to head, keep fast at meeting point
    // Both move 1 step at a time — they meet at cycle start
    slow = head;
    while (slow != fast) {
        slow = slow.next;
        fast = fast.next;
    }
    
    return slow;  // Cycle start
}
```

**Why Phase 2 works**: Mathematical proof — if the distance from head to cycle start is `a`, and the distance from cycle start to meeting point is `b`, and the cycle length is `c`, then: `a = c - b`. So when both pointers move at the same speed, they meet at the cycle start after `a` steps.

### Template: Find Middle of Linked List

```java
public ListNode findMiddle(ListNode head) {
    ListNode slow = head, fast = head;
    
    while (fast != null && fast.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    return slow;  // slow is at the middle
    // For even-length lists: slow points to the second middle node
    // For "first middle" in even lists, use: fast.next != null && fast.next.next != null
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Linked List Cycle | Basic | fast/slow meeting = cycle |
| Linked List Cycle II | Find start | Two-phase: detect + find start |
| Find Middle Node | Basic | When fast reaches end, slow is at middle |
| Happy Number | Cycle in sequence | Treat number sequence as linked list |
| Palindrome Linked List | Middle + reverse | Find middle, reverse second half, compare |
| Reorder List | Middle + reverse + merge | Split at middle, reverse second half, interleave |

---

## Pattern 4: Merge Intervals

### Mental Model
Sort intervals by start time. Then iterate through and merge overlapping intervals by comparing the current interval's start with the previous interval's end.

### When to Recognize This Pattern
- Problem involves **intervals** or **ranges**.
- You need to **merge overlapping** intervals.
- You need to **insert** a new interval.
- You need to find **conflicts** or **free time**.

### Template: Merge Overlapping Intervals

```java
public int[][] merge(int[][] intervals) {
    // Step 1: Sort by start time
    Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
    
    List<int[]> merged = new ArrayList<>();
    merged.add(intervals[0]);
    
    for (int i = 1; i < intervals.length; i++) {
        int[] last = merged.get(merged.size() - 1);
        int[] current = intervals[i];
        
        if (current[0] <= last[1]) {
            // Overlapping: extend the end of the last interval
            last[1] = Math.max(last[1], current[1]);
        } else {
            // Non-overlapping: add as new interval
            merged.add(current);
        }
    }
    
    return merged.toArray(new int[merged.size()][]);
}
```

### Template: Insert Interval

```java
public int[][] insert(int[][] intervals, int[] newInterval) {
    List<int[]> result = new ArrayList<>();
    int i = 0;
    
    // Add all intervals that end before newInterval starts
    while (i < intervals.length && intervals[i][1] < newInterval[0]) {
        result.add(intervals[i]);
        i++;
    }
    
    // Merge all overlapping intervals with newInterval
    while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
        newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
        newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
        i++;
    }
    result.add(newInterval);
    
    // Add remaining intervals
    while (i < intervals.length) {
        result.add(intervals[i]);
        i++;
    }
    
    return result.toArray(new int[result.size()][]);
}
```

### Template: Interval Intersection

```java
public int[][] intervalIntersection(int[][] A, int[][] B) {
    List<int[]> result = new ArrayList<>();
    int i = 0, j = 0;
    
    while (i < A.length && j < B.length) {
        int start = Math.max(A[i][0], B[j][0]);
        int end = Math.min(A[i][1], B[j][1]);
        
        if (start <= end) {
            result.add(new int[]{start, end});
        }
        
        // Move the pointer of the interval that ends first
        if (A[i][1] < B[j][1]) {
            i++;
        } else {
            j++;
        }
    }
    
    return result.toArray(new int[result.size()][]);
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Merge Intervals | Basic merge | Sort + merge overlapping |
| Insert Interval | Insert + merge | Three-phase: before, overlap, after |
| Interval List Intersections | Two pointers | Max start, min end |
| Meeting Rooms | Conflict check | Sort + check overlap |
| Meeting Rooms II | Min rooms needed | Sort starts/ends separately or min-heap |
| Non-overlapping Intervals | Max non-overlapping | Greedy: sort by end, count removals |
| Employee Free Time | Merge + complement | Merge all intervals, find gaps |

---

## Pattern 5: Cyclic Sort

### Mental Model
When the array contains numbers in the range [0, n] or [1, n], place each number at its "correct" index (number `i` goes to index `i` or `i-1`). After sorting, any index where `nums[i] != i` (or `i+1`) indicates a missing or duplicate number.

### When to Recognize This Pattern
- Array contains numbers in range **[0, n]** or **[1, n]**.
- Problem asks for **missing number**, **duplicate**, or **first missing positive**.

### Template: Cyclic Sort

```java
public void cyclicSort(int[] nums) {
    int i = 0;
    
    while (i < nums.length) {
        int correctIndex = nums[i] - 1;  // Where this number should be (for [1, n])
        
        if (nums[i] > 0 && nums[i] <= nums.length && nums[i] != nums[correctIndex]) {
            // Swap to correct position
            int temp = nums[i];
            nums[i] = nums[correctIndex];
            nums[correctIndex] = temp;
        } else {
            i++;  // Already in correct position or out of range
        }
    }
}

// Find missing number in [1, n]
public int findMissing(int[] nums) {
    cyclicSort(nums);
    
    for (int i = 0; i < nums.length; i++) {
        if (nums[i] != i + 1) {
            return i + 1;
        }
    }
    
    return nums.length + 1;
}
```

### Template: Find All Duplicates

```java
public List<Integer> findDuplicates(int[] nums) {
    List<Integer> duplicates = new ArrayList<>();
    int i = 0;
    
    while (i < nums.length) {
        int correctIndex = nums[i] - 1;
        if (nums[i] != nums[correctIndex]) {
            int temp = nums[i];
            nums[i] = nums[correctIndex];
            nums[correctIndex] = temp;
        } else {
            i++;
        }
    }
    
    for (int j = 0; j < nums.length; j++) {
        if (nums[j] != j + 1) {
            duplicates.add(nums[j]);
        }
    }
    
    return duplicates;
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Missing Number | [0, n] range | After sort, check nums[i] != i |
| Find All Numbers Disappeared | [1, n] range | After sort, collect mismatches |
| Find the Duplicate Number | [1, n] range | After sort, find nums[i] != i+1 |
| Find All Duplicates | [1, n] range | Collect elements at wrong positions |
| First Missing Positive | [1, n] range | Ignore negatives and > n |
| Set Mismatch | [1, n] range | Find both duplicate and missing |

---

## Pattern 6: In-Place Linked List Reversal

### Mental Model
Reverse pointers by maintaining three pointers: `prev`, `curr`, and `next`. At each step, save the next node, reverse the current node's pointer to point to prev, then advance both prev and curr.

### When to Recognize This Pattern
- Problem asks to **reverse a linked list** or **sub-section** of it.
- Problem requires **reordering** linked list nodes.

### Template: Full Reversal

```java
public ListNode reverseList(ListNode head) {
    ListNode prev = null, curr = head;
    
    while (curr != null) {
        ListNode next = curr.next;  // Save next
        curr.next = prev;           // Reverse pointer
        prev = curr;                // Advance prev
        curr = next;                // Advance curr
    }
    
    return prev;  // New head
}
```

### Template: Reverse a Sub-list [left, right]

```java
public ListNode reverseBetween(ListNode head, int left, int right) {
    if (left == right) return head;
    
    ListNode dummy = new ListNode(0);
    dummy.next = head;
    ListNode beforeLeft = dummy;
    
    // Move to the node before the sub-list
    for (int i = 1; i < left; i++) {
        beforeLeft = beforeLeft.next;
    }
    
    // Reverse the sub-list
    ListNode prev = null;
    ListNode curr = beforeLeft.next;
    
    for (int i = 0; i <= right - left; i++) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    
    // Connect the reversed sub-list back
    beforeLeft.next.next = curr;  // End of reversed portion → node after right
    beforeLeft.next = prev;       // Node before left → start of reversed portion
    
    return dummy.next;
}
```

### Template: Reverse in K-Groups

```java
public ListNode reverseKGroup(ListNode head, int k) {
    // Check if there are k nodes remaining
    ListNode check = head;
    for (int i = 0; i < k; i++) {
        if (check == null) return head;
        check = check.next;
    }
    
    // Reverse k nodes
    ListNode prev = null, curr = head;
    for (int i = 0; i < k; i++) {
        ListNode next = curr.next;
        curr.next = prev;
        prev = curr;
        curr = next;
    }
    
    // head is now the tail of the reversed group
    // Recursively reverse the remaining list and connect
    head.next = reverseKGroup(curr, k);
    
    return prev;  // prev is the new head of this group
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Reverse Linked List | Full reversal | prev/curr/next |
| Reverse Linked List II | Sub-list reversal | Track beforeLeft, reconnect |
| Reverse Nodes in K-Group | K-group reversal | Check length, recurse |
| Swap Nodes in Pairs | K=2 reversal | Special case of K-group |
| Palindrome Linked List | Reverse + compare | Find middle, reverse second half |

---

## Pattern 7: BFS (Breadth-First Search)

### Mental Model
Explore all neighbors at the current depth before moving to the next depth. Use a **queue** (FIFO). BFS naturally finds the **shortest path** in an unweighted graph.

### When to Recognize This Pattern
- Find the **shortest** path or **minimum** number of steps.
- **Level-order** traversal of a tree.
- Exploring all nodes at distance K.
- Problems on a **grid** (treat cells as graph nodes).

### Template: Tree Level-Order Traversal

```java
public List<List<Integer>> levelOrder(TreeNode root) {
    List<List<Integer>> result = new ArrayList<>();
    if (root == null) return result;
    
    Queue<TreeNode> queue = new LinkedList<>();
    queue.offer(root);
    
    while (!queue.isEmpty()) {
        int levelSize = queue.size();       // Number of nodes at current level
        List<Integer> currentLevel = new ArrayList<>();
        
        for (int i = 0; i < levelSize; i++) {
            TreeNode node = queue.poll();
            currentLevel.add(node.val);
            
            if (node.left != null) queue.offer(node.left);
            if (node.right != null) queue.offer(node.right);
        }
        
        result.add(currentLevel);
    }
    
    return result;
}
```

### Template: Graph BFS (Shortest Path)

```java
public int shortestPath(int[][] grid) {
    int rows = grid.length, cols = grid[0].length;
    int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
    boolean[][] visited = new boolean[rows][cols];
    
    Queue<int[]> queue = new LinkedList<>();
    queue.offer(new int[]{0, 0});
    visited[0][0] = true;
    int steps = 0;
    
    while (!queue.isEmpty()) {
        int size = queue.size();
        
        for (int i = 0; i < size; i++) {
            int[] cell = queue.poll();
            int row = cell[0], col = cell[1];
            
            // Check if we reached the destination
            if (row == rows - 1 && col == cols - 1) {
                return steps;
            }
            
            // Explore all 4 directions
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                
                if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
                    && !visited[newRow][newCol] && grid[newRow][newCol] == 0) {
                    visited[newRow][newCol] = true;
                    queue.offer(new int[]{newRow, newCol});
                }
            }
        }
        
        steps++;
    }
    
    return -1;  // No path found
}
```

### Template: Multi-Source BFS

```java
// 01 Matrix: Find distance from each cell to nearest 0
public int[][] updateMatrix(int[][] mat) {
    int rows = mat.length, cols = mat[0].length;
    int[][] dist = new int[rows][cols];
    Queue<int[]> queue = new LinkedList<>();
    
    // Start BFS from ALL zeros simultaneously
    for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
            if (mat[i][j] == 0) {
                queue.offer(new int[]{i, j});
                dist[i][j] = 0;
            } else {
                dist[i][j] = Integer.MAX_VALUE;
            }
        }
    }
    
    int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
    
    while (!queue.isEmpty()) {
        int[] cell = queue.poll();
        for (int[] dir : directions) {
            int newRow = cell[0] + dir[0];
            int newCol = cell[1] + dir[1];
            
            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
                && dist[newRow][newCol] > dist[cell[0]][cell[1]] + 1) {
                dist[newRow][newCol] = dist[cell[0]][cell[1]] + 1;
                queue.offer(new int[]{newRow, newCol});
            }
        }
    }
    
    return dist;
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Binary Tree Level Order | Tree BFS | Process level by level |
| Minimum Depth of Binary Tree | Tree BFS | First leaf node reached |
| Rotting Oranges | Multi-source BFS | Start from all rotten oranges |
| Word Ladder | Graph BFS | Each word is a node, shortest transformation |
| Shortest Path in Grid | Grid BFS | 4-directional movement |
| 01 Matrix | Multi-source BFS | Start from all zeros |
| Open the Lock | Graph BFS | Each state is a node |
| Number of Islands | BFS/DFS | Connected component counting |

---

## Pattern 8: DFS (Depth-First Search)

### Mental Model
Explore as deep as possible before backtracking. Use a **stack** (or recursion). DFS is ideal for exploring all paths, checking connectivity, and tree problems.

### When to Recognize This Pattern
- **Tree path problems** (root-to-leaf, path sum).
- **Graph traversal** (connected components, cycle detection).
- **Grid problems** (count islands, flood fill).
- Need to explore **all** possibilities (not just shortest).

### Template: Tree DFS (Path Sum)

```java
public boolean hasPathSum(TreeNode root, int targetSum) {
    if (root == null) return false;
    
    // Leaf node: check if remaining sum equals node value
    if (root.left == null && root.right == null) {
        return root.val == targetSum;
    }
    
    // Recurse on children with reduced target
    return hasPathSum(root.left, targetSum - root.val) 
        || hasPathSum(root.right, targetSum - root.val);
}
```

### Template: Tree DFS (Collect All Paths)

```java
public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
    List<List<Integer>> result = new ArrayList<>();
    dfs(root, targetSum, new ArrayList<>(), result);
    return result;
}

private void dfs(TreeNode node, int remaining, List<Integer> path, List<List<Integer>> result) {
    if (node == null) return;
    
    path.add(node.val);
    
    // Leaf node with matching sum
    if (node.left == null && node.right == null && remaining == node.val) {
        result.add(new ArrayList<>(path));  // Copy the path
    } else {
        dfs(node.left, remaining - node.val, path, result);
        dfs(node.right, remaining - node.val, path, result);
    }
    
    path.remove(path.size() - 1);  // BACKTRACK: remove current node
}
```

### Template: Graph DFS (Connected Components)

```java
public int countIslands(char[][] grid) {
    int count = 0;
    
    for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j] == '1') {
                dfs(grid, i, j);
                count++;
            }
        }
    }
    
    return count;
}

private void dfs(char[][] grid, int row, int col) {
    // Boundary and base case checks
    if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length 
        || grid[row][col] != '1') {
        return;
    }
    
    grid[row][col] = '0';  // Mark as visited
    
    // Explore all 4 directions
    dfs(grid, row + 1, col);
    dfs(grid, row - 1, col);
    dfs(grid, row, col + 1);
    dfs(grid, row, col - 1);
}
```

### Template: Graph DFS with Cycle Detection

```java
// Detect cycle in directed graph (Course Schedule)
public boolean canFinish(int numCourses, int[][] prerequisites) {
    List<List<Integer>> graph = new ArrayList<>();
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    for (int[] pre : prerequisites) graph.get(pre[1]).add(pre[0]);
    
    int[] state = new int[numCourses]; // 0=unvisited, 1=in-progress, 2=completed
    
    for (int i = 0; i < numCourses; i++) {
        if (hasCycle(graph, i, state)) return false;
    }
    
    return true;
}

private boolean hasCycle(List<List<Integer>> graph, int node, int[] state) {
    if (state[node] == 1) return true;   // Back edge = cycle
    if (state[node] == 2) return false;  // Already fully processed
    
    state[node] = 1;  // Mark as in-progress
    
    for (int neighbor : graph.get(node)) {
        if (hasCycle(graph, neighbor, state)) return true;
    }
    
    state[node] = 2;  // Mark as completed
    return false;
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Path Sum | Tree DFS | Subtract value, check at leaf |
| Path Sum II | Tree DFS + collect | Backtracking path collection |
| Maximum Depth | Tree DFS | 1 + max(left, right) |
| Diameter of Binary Tree | Tree DFS | Track max(leftDepth + rightDepth) |
| Number of Islands | Grid DFS | Mark visited, count components |
| Flood Fill | Grid DFS | Change color recursively |
| Course Schedule | Directed graph + cycle | Three-state DFS |
| Clone Graph | Graph DFS + HashMap | Map old→new nodes |

---

## Pattern 9: Two Heaps

### Mental Model
Maintain two heaps: a **max-heap** for the smaller half and a **min-heap** for the larger half. The max-heap's top is the largest of the small elements, and the min-heap's top is the smallest of the large elements. The median is derived from the tops of both heaps.

### When to Recognize This Pattern
- Need to find the **median** of a data stream.
- Need to track the **middle** partition of data.
- Scheduling problems with two "sides."

### Template: Find Median from Data Stream

```java
class MedianFinder {
    private PriorityQueue<Integer> maxHeap; // Stores the smaller half (largest at top)
    private PriorityQueue<Integer> minHeap; // Stores the larger half (smallest at top)
    
    public MedianFinder() {
        maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        minHeap = new PriorityQueue<>();
    }
    
    public void addNum(int num) {
        // Always add to maxHeap first
        maxHeap.offer(num);
        
        // Balance: max of smaller half must be ≤ min of larger half
        minHeap.offer(maxHeap.poll());
        
        // Keep sizes balanced: maxHeap can have at most 1 extra element
        if (minHeap.size() > maxHeap.size()) {
            maxHeap.offer(minHeap.poll());
        }
    }
    
    public double findMedian() {
        if (maxHeap.size() > minHeap.size()) {
            return maxHeap.peek();
        }
        return (maxHeap.peek() + minHeap.peek()) / 2.0;
    }
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Find Median from Data Stream | Classic two heaps | Max-heap + min-heap balanced |
| Sliding Window Median | Two heaps + lazy deletion | Add/remove with rebalancing |
| IPO (Maximize Capital) | Two heaps | Min-heap for capital, max-heap for profit |

---

## Pattern 10: Subsets / Backtracking

### Mental Model
Build solutions incrementally by making choices at each step. At each step, you have options: include or exclude an element (subsets), choose from available elements (permutations), or pick from candidates (combinations). If the partial solution violates a constraint, **prune** that branch (backtrack).

### When to Recognize This Pattern
- Generate **all** permutations, combinations, or subsets.
- Problem says "find all" solutions.
- Constraint satisfaction problems (Sudoku, N-Queens).
- Need to explore a **decision tree**.

### Template: Subsets (Power Set)

```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
    result.add(new ArrayList<>(current));  // Add every subset (including empty)
    
    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);                           // CHOOSE
        backtrack(nums, i + 1, current, result);        // EXPLORE
        current.remove(current.size() - 1);             // UNCHOOSE (backtrack)
    }
}
```

### Template: Permutations

```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, new boolean[nums.length], new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;                          // Skip already used
        
        used[i] = true;                                 // CHOOSE
        current.add(nums[i]);
        backtrack(nums, used, current, result);         // EXPLORE
        current.remove(current.size() - 1);             // UNCHOOSE
        used[i] = false;
    }
}
```

### Template: Combinations (Choose K from N)

```java
public List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(n, k, 1, new ArrayList<>(), result);
    return result;
}

private void backtrack(int n, int k, int start, List<Integer> current, List<List<Integer>> result) {
    if (current.size() == k) {
        result.add(new ArrayList<>(current));
        return;
    }
    
    // Pruning: only continue if we can still pick enough elements
    for (int i = start; i <= n - (k - current.size()) + 1; i++) {
        current.add(i);
        backtrack(n, k, i + 1, current, result);
        current.remove(current.size() - 1);
    }
}
```

### Template: Constraint Satisfaction (N-Queens)

```java
public List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    char[][] board = new char[n][n];
    for (char[] row : board) Arrays.fill(row, '.');
    
    backtrack(board, 0, result);
    return result;
}

private void backtrack(char[][] board, int row, List<List<String>> result) {
    if (row == board.length) {
        result.add(construct(board));
        return;
    }
    
    for (int col = 0; col < board.length; col++) {
        if (isValid(board, row, col)) {
            board[row][col] = 'Q';                     // PLACE
            backtrack(board, row + 1, result);          // EXPLORE next row
            board[row][col] = '.';                      // REMOVE (backtrack)
        }
    }
}

private boolean isValid(char[][] board, int row, int col) {
    // Check column
    for (int i = 0; i < row; i++) {
        if (board[i][col] == 'Q') return false;
    }
    // Check upper-left diagonal
    for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
        if (board[i][j] == 'Q') return false;
    }
    // Check upper-right diagonal
    for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) {
        if (board[i][j] == 'Q') return false;
    }
    return true;
}
```

### The Backtracking Framework

Every backtracking problem follows this structure:

```
backtrack(state):
    if state is a solution:
        record solution
        return
    
    for each choice in available_choices:
        if choice is valid:
            make the choice (modify state)
            backtrack(new_state)
            undo the choice (restore state)  ← THIS IS THE BACKTRACK
```

### Problem Mapping

| Problem | Type | Key Idea |
|---------|------|----------|
| Subsets | Subset generation | Include/exclude each element |
| Subsets II (with duplicates) | Subset + dedup | Sort + skip consecutive duplicates |
| Permutations | Permutation | Use `used[]` array |
| Permutations II (with duplicates) | Permutation + dedup | Sort + skip if same as prev unused |
| Combination Sum | Combination | Allow reuse (start from i, not i+1) |
| Combination Sum II | Combination + dedup | No reuse + skip duplicates |
| N-Queens | Constraint satisfaction | Validate placement, prune |
| Sudoku Solver | Constraint satisfaction | Try 1-9, validate, backtrack |
| Generate Parentheses | Constrained generation | Track open/close counts |
| Letter Combinations of Phone | Combination | Map digit→letters, build strings |
| Word Search | Grid backtracking | DFS + mark visited + unmark |

---

## Pattern 11: Modified Binary Search

### Mental Model
Binary search is not just for "find target in sorted array." It is a general technique for **reducing a search space by half** at each step. Anytime you can define a condition that splits the space into "satisfies" and "doesn't satisfy," you can binary search.

### When to Recognize This Pattern
- **Sorted array** or **rotated sorted** array.
- Search space can be **divided in half** based on a condition.
- Problem asks to find "minimum X such that condition" or "maximum X such that condition."
- "Find peak," "search in rotated array," or "Koko eating bananas" style problems.

### Template: Standard Binary Search

```java
public int binarySearch(int[] nums, int target) {
    int left = 0, right = nums.length - 1;
    
    while (left <= right) {
        int mid = left + (right - left) / 2;  // Avoid overflow
        
        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            left = mid + 1;
        } else {
            right = mid - 1;
        }
    }
    
    return -1;
}
```

### Template: Find First/Last Position (Left/Right Bisect)

```java
// Find the first position where nums[i] >= target
public int lowerBound(int[] nums, int target) {
    int left = 0, right = nums.length;
    
    while (left < right) {
        int mid = left + (right - left) / 2;
        
        if (nums[mid] < target) {
            left = mid + 1;
        } else {
            right = mid;      // Don't skip mid — it might be the answer
        }
    }
    
    return left;  // First index where nums[i] >= target
}

// Find the last position where nums[i] <= target
public int upperBound(int[] nums, int target) {
    int left = 0, right = nums.length;
    
    while (left < right) {
        int mid = left + (right - left) / 2;
        
        if (nums[mid] <= target) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }
    
    return left - 1;  // Last index where nums[i] <= target
}
```

### Template: Search in Rotated Sorted Array

```java
public int search(int[] nums, int target) {
    int left = 0, right = nums.length - 1;
    
    while (left <= right) {
        int mid = left + (right - left) / 2;
        
        if (nums[mid] == target) return mid;
        
        // Determine which half is sorted
        if (nums[left] <= nums[mid]) {
            // Left half is sorted
            if (nums[left] <= target && target < nums[mid]) {
                right = mid - 1;  // Target is in the sorted left half
            } else {
                left = mid + 1;   // Target is in the right half
            }
        } else {
            // Right half is sorted
            if (nums[mid] < target && target <= nums[right]) {
                left = mid + 1;   // Target is in the sorted right half
            } else {
                right = mid - 1;  // Target is in the left half
            }
        }
    }
    
    return -1;
}
```

### Template: Binary Search on Answer (Koko Eating Bananas Style)

```java
// Find the minimum eating speed to finish all bananas in H hours
public int minEatingSpeed(int[] piles, int h) {
    int left = 1;
    int right = Arrays.stream(piles).max().getAsInt();
    
    while (left < right) {
        int mid = left + (right - left) / 2;
        
        if (canFinish(piles, mid, h)) {
            right = mid;        // mid works, try smaller
        } else {
            left = mid + 1;     // mid doesn't work, try larger
        }
    }
    
    return left;
}

private boolean canFinish(int[] piles, int speed, int h) {
    int hours = 0;
    for (int pile : piles) {
        hours += (pile + speed - 1) / speed;  // Ceiling division
    }
    return hours <= h;
}
```

**Key insight for "binary search on answer"**: You're not searching an array — you're searching a **range of possible answers** [min, max]. At each mid, you check if that answer is feasible. If yes, try smaller. If no, try larger.

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Binary Search | Standard | Basic search in sorted array |
| First Bad Version | Left bisect | Find first position satisfying condition |
| Search in Rotated Sorted Array | Rotated | Determine which half is sorted |
| Find Minimum in Rotated Sorted Array | Rotated | Binary search for inflection point |
| Find Peak Element | Peak finding | Compare mid with mid+1 |
| Koko Eating Bananas | Answer search | Binary search on speed range |
| Capacity to Ship Packages | Answer search | Binary search on weight capacity |
| Split Array Largest Sum | Answer search | Binary search on max subarray sum |
| Median of Two Sorted Arrays | Partition search | Binary search on partition position |

---

## Pattern 12: Top K Elements

### Mental Model
Use a **heap** (priority queue) to efficiently maintain the K largest or K smallest elements. A min-heap of size K naturally keeps the K largest elements (the smallest of the K largest is at the top).

### When to Recognize This Pattern
- Problem asks for K largest, K smallest, or K most frequent.
- K closest points.
- Top K anything.

### Template: K Largest Elements (Min-Heap)

```java
public int[] topKLargest(int[] nums, int k) {
    PriorityQueue<Integer> minHeap = new PriorityQueue<>();
    
    for (int num : nums) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();     // Remove the smallest — keep only K largest
        }
    }
    
    return minHeap.stream().mapToInt(i -> i).toArray();
}
```

### Template: K Most Frequent Elements

```java
public int[] topKFrequent(int[] nums, int k) {
    // Step 1: Count frequencies
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) {
        freq.merge(num, 1, Integer::sum);
    }
    
    // Step 2: Use min-heap of size K (by frequency)
    PriorityQueue<Integer> minHeap = new PriorityQueue<>(
        (a, b) -> freq.get(a) - freq.get(b)
    );
    
    for (int num : freq.keySet()) {
        minHeap.offer(num);
        if (minHeap.size() > k) {
            minHeap.poll();
        }
    }
    
    return minHeap.stream().mapToInt(i -> i).toArray();
}
```

### Alternative: Bucket Sort (O(n) for K Frequent)

```java
public int[] topKFrequentBucketSort(int[] nums, int k) {
    Map<Integer, Integer> freq = new HashMap<>();
    for (int num : nums) freq.merge(num, 1, Integer::sum);
    
    // Bucket: index = frequency, value = list of numbers with that frequency
    List<Integer>[] buckets = new List[nums.length + 1];
    for (int i = 0; i < buckets.length; i++) buckets[i] = new ArrayList<>();
    
    for (Map.Entry<Integer, Integer> entry : freq.entrySet()) {
        buckets[entry.getValue()].add(entry.getKey());
    }
    
    // Collect from highest frequency bucket to lowest
    List<Integer> result = new ArrayList<>();
    for (int i = buckets.length - 1; i >= 0 && result.size() < k; i--) {
        result.addAll(buckets[i]);
    }
    
    return result.stream().mapToInt(i -> i).toArray();
}
```

### Template: K Closest Points to Origin

```java
public int[][] kClosest(int[][] points, int k) {
    // Max-heap: keep K closest (remove the farthest among our K)
    PriorityQueue<int[]> maxHeap = new PriorityQueue<>(
        (a, b) -> (b[0]*b[0] + b[1]*b[1]) - (a[0]*a[0] + a[1]*a[1])
    );
    
    for (int[] point : points) {
        maxHeap.offer(point);
        if (maxHeap.size() > k) {
            maxHeap.poll();
        }
    }
    
    return maxHeap.toArray(new int[k][]);
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Kth Largest Element | Min-heap of size K | poll when size > K |
| Top K Frequent Elements | Freq map + min-heap | Heap ordered by frequency |
| K Closest Points | Max-heap of size K | Distance comparison |
| Sort Characters by Frequency | Freq map + max-heap | Build string from heap |
| Reorganize String | Max-heap | Always pick most frequent |
| Task Scheduler | Max-heap + cooldown | Greedy scheduling |

---

## Pattern 13: K-Way Merge

### Mental Model
Merge K sorted lists/arrays by using a **min-heap** to always pick the globally smallest element across all K lists. Push the first element of each list into the heap, then repeatedly pop the smallest and push the next element from the same list.

### When to Recognize This Pattern
- Merge **K sorted** lists or arrays.
- Find the **Kth smallest** element in a sorted matrix.
- Merge sorted files or streams.

### Template: Merge K Sorted Lists

```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> minHeap = new PriorityQueue<>(
        (a, b) -> a.val - b.val
    );
    
    // Add the head of each list to the heap
    for (ListNode list : lists) {
        if (list != null) {
            minHeap.offer(list);
        }
    }
    
    ListNode dummy = new ListNode(0);
    ListNode tail = dummy;
    
    while (!minHeap.isEmpty()) {
        ListNode smallest = minHeap.poll();     // Get globally smallest
        tail.next = smallest;
        tail = tail.next;
        
        if (smallest.next != null) {
            minHeap.offer(smallest.next);       // Push next from same list
        }
    }
    
    return dummy.next;
}
```

### Template: Kth Smallest in Sorted Matrix

```java
public int kthSmallest(int[][] matrix, int k) {
    int n = matrix.length;
    // Min-heap: [value, row, col]
    PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);
    
    // Add first element of each row
    for (int i = 0; i < Math.min(n, k); i++) {
        minHeap.offer(new int[]{matrix[i][0], i, 0});
    }
    
    int count = 0;
    while (!minHeap.isEmpty()) {
        int[] smallest = minHeap.poll();
        count++;
        
        if (count == k) return smallest[0];
        
        int row = smallest[1], col = smallest[2];
        if (col + 1 < n) {
            minHeap.offer(new int[]{matrix[row][col + 1], row, col + 1});
        }
    }
    
    return -1;
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Merge K Sorted Lists | Classic K-way merge | Min-heap of list heads |
| Kth Smallest in Sorted Matrix | Matrix K-way merge | Treat each row as a sorted list |
| Smallest Range Covering K Lists | K-way merge + window | Track max while popping min |
| Find K Pairs with Smallest Sums | K-way merge | Pairs as virtual sorted lists |

---

## Pattern 14: Monotonic Stack

### Mental Model
Maintain a stack where elements are always in increasing (or decreasing) order. When a new element violates the monotonic property, pop elements from the stack — each popped element has found its "next greater" (or "next smaller") element.

### When to Recognize This Pattern
- Find the **next greater** or **next smaller** element.
- **Histogram** problems (largest rectangle).
- Problems involving **temperature** or **stock price** comparisons.
- "Looking back" or "looking forward" for boundaries.

### Template: Next Greater Element

```java
public int[] nextGreaterElement(int[] nums) {
    int n = nums.length;
    int[] result = new int[n];
    Arrays.fill(result, -1);
    Deque<Integer> stack = new ArrayDeque<>();  // Stack of INDICES
    
    for (int i = 0; i < n; i++) {
        // Pop all elements that are smaller than current
        while (!stack.isEmpty() && nums[stack.peek()] < nums[i]) {
            int idx = stack.pop();
            result[idx] = nums[i];    // nums[i] is the next greater for nums[idx]
        }
        
        stack.push(i);
    }
    
    return result;
}
```

### Template: Daily Temperatures

```java
public int[] dailyTemperatures(int[] temperatures) {
    int n = temperatures.length;
    int[] result = new int[n];
    Deque<Integer> stack = new ArrayDeque<>();  // Stack of indices
    
    for (int i = 0; i < n; i++) {
        while (!stack.isEmpty() && temperatures[stack.peek()] < temperatures[i]) {
            int prevDay = stack.pop();
            result[prevDay] = i - prevDay;  // Number of days to wait
        }
        stack.push(i);
    }
    
    return result;
}
```

### Template: Largest Rectangle in Histogram

```java
public int largestRectangleArea(int[] heights) {
    int n = heights.length;
    Deque<Integer> stack = new ArrayDeque<>();
    int maxArea = 0;
    
    for (int i = 0; i <= n; i++) {
        int currentHeight = (i == n) ? 0 : heights[i];
        
        while (!stack.isEmpty() && heights[stack.peek()] > currentHeight) {
            int height = heights[stack.pop()];
            int width = stack.isEmpty() ? i : i - stack.peek() - 1;
            maxArea = Math.max(maxArea, height * width);
        }
        
        stack.push(i);
    }
    
    return maxArea;
}
```

### Problem Mapping

| Problem | Stack Type | Key Idea |
|---------|-----------|----------|
| Next Greater Element | Monotonic decreasing | Pop when current > top |
| Daily Temperatures | Monotonic decreasing | Days = index difference |
| Largest Rectangle in Histogram | Monotonic increasing | Pop when height decreases |
| Trapping Rain Water | Monotonic decreasing | Pop and calculate water |
| Stock Span Problem | Monotonic decreasing | Count consecutive ≤ days |
| Remove K Digits | Monotonic increasing | Remove larger digits |
| Asteroid Collision | Custom | Simulate with stack |

---

## Pattern 15: Topological Sort

### Mental Model
Order the nodes of a directed acyclic graph (DAG) such that for every edge u→v, u appears before v. Use **Kahn's Algorithm** (BFS with in-degree tracking) or **DFS with post-order reversal**.

### When to Recognize This Pattern
- **Dependency ordering** (courses, build systems, task scheduling).
- Problem has **prerequisites** or "do X before Y."
- Need to detect if a **valid ordering** exists (no cycles).

### Template: Kahn's Algorithm (BFS)

```java
public int[] topologicalSort(int numCourses, int[][] prerequisites) {
    // Build adjacency list and in-degree array
    List<List<Integer>> graph = new ArrayList<>();
    int[] inDegree = new int[numCourses];
    
    for (int i = 0; i < numCourses; i++) graph.add(new ArrayList<>());
    for (int[] pre : prerequisites) {
        graph.get(pre[1]).add(pre[0]);
        inDegree[pre[0]]++;
    }
    
    // Start with all nodes that have no prerequisites (in-degree = 0)
    Queue<Integer> queue = new LinkedList<>();
    for (int i = 0; i < numCourses; i++) {
        if (inDegree[i] == 0) queue.offer(i);
    }
    
    int[] order = new int[numCourses];
    int index = 0;
    
    while (!queue.isEmpty()) {
        int node = queue.poll();
        order[index++] = node;
        
        for (int neighbor : graph.get(node)) {
            inDegree[neighbor]--;
            if (inDegree[neighbor] == 0) {
                queue.offer(neighbor);
            }
        }
    }
    
    // If we processed all nodes, valid ordering exists
    return index == numCourses ? order : new int[0];
}
```

### Template: All Topological Orderings (Backtracking)

```java
public List<List<Integer>> allTopologicalSorts(int n, int[][] edges) {
    List<List<Integer>> graph = new ArrayList<>();
    int[] inDegree = new int[n];
    for (int i = 0; i < n; i++) graph.add(new ArrayList<>());
    for (int[] e : edges) {
        graph.get(e[0]).add(e[1]);
        inDegree[e[1]]++;
    }
    
    List<List<Integer>> result = new ArrayList<>();
    boolean[] visited = new boolean[n];
    backtrack(graph, inDegree, visited, new ArrayList<>(), n, result);
    return result;
}

private void backtrack(List<List<Integer>> graph, int[] inDegree, boolean[] visited,
                       List<Integer> current, int n, List<List<Integer>> result) {
    if (current.size() == n) {
        result.add(new ArrayList<>(current));
        return;
    }
    
    for (int i = 0; i < n; i++) {
        if (!visited[i] && inDegree[i] == 0) {
            visited[i] = true;
            current.add(i);
            for (int neighbor : graph.get(i)) inDegree[neighbor]--;
            
            backtrack(graph, inDegree, visited, current, n, result);
            
            visited[i] = false;
            current.remove(current.size() - 1);
            for (int neighbor : graph.get(i)) inDegree[neighbor]++;
        }
    }
}
```

### Problem Mapping

| Problem | Variant | Key Idea |
|---------|---------|----------|
| Course Schedule | Cycle detection | If topo sort processes all nodes → no cycle |
| Course Schedule II | Get ordering | Return the topo sort order |
| Alien Dictionary | Build graph from constraints | Compare adjacent words to get edges |
| Minimum Height Trees | Iterative leaf removal | Remove leaves layer by layer (reverse topo) |
| Parallel Courses | Topo sort with levels | BFS levels = parallel batches |
| Build Order | Classic topo sort | Dependencies → edges |

---

## Pattern 16: Dynamic Programming Patterns

### Mental Model
DP solves problems by breaking them into overlapping subproblems and storing results. The key is defining the **state** (what information uniquely identifies a subproblem) and the **transition** (how to compute a state from smaller states).

### When to Recognize This Pattern
- Problem asks for **optimal** value (min, max, count).
- The problem has **overlapping subproblems** and **optimal substructure**.
- Choices at each step affect future choices.
- Phrases: "minimum cost," "number of ways," "is it possible."

### Sub-Pattern A: 0/1 Knapsack

**State**: `dp[i][w]` = max value using first i items with capacity w.
**Transition**: Include item i or exclude it.

```java
public int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];
    
    for (int i = 1; i <= n; i++) {
        for (int w = 0; w <= capacity; w++) {
            dp[i][w] = dp[i - 1][w];  // Exclude item i
            
            if (weights[i - 1] <= w) {
                dp[i][w] = Math.max(dp[i][w],
                    dp[i - 1][w - weights[i - 1]] + values[i - 1]);  // Include item i
            }
        }
    }
    
    return dp[n][capacity];
}
```

**Space-optimized** (1D array):

```java
public int knapsackOptimized(int[] weights, int[] values, int capacity) {
    int[] dp = new int[capacity + 1];
    
    for (int i = 0; i < weights.length; i++) {
        for (int w = capacity; w >= weights[i]; w--) {  // Reverse to avoid using same item twice
            dp[w] = Math.max(dp[w], dp[w - weights[i]] + values[i]);
        }
    }
    
    return dp[capacity];
}
```

### Sub-Pattern B: Unbounded Knapsack

**Difference**: Can use each item unlimited times. Inner loop goes **forward** instead of backward.

```java
// Coin Change: minimum coins to make amount
public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);  // Initialize to impossible value
    dp[0] = 0;
    
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    
    return dp[amount] > amount ? -1 : dp[amount];
}
```

### Sub-Pattern C: Longest Common Subsequence (LCS)

**State**: `dp[i][j]` = length of LCS of s1[0..i-1] and s2[0..j-1].
**Transition**: Match or no-match.

```java
public int longestCommonSubsequence(String s1, String s2) {
    int m = s1.length(), n = s2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1] + 1;          // Characters match
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);  // Skip one
            }
        }
    }
    
    return dp[m][n];
}
```

### Sub-Pattern D: Longest Increasing Subsequence (LIS)

```java
// O(n log n) solution using patience sorting
public int lengthOfLIS(int[] nums) {
    List<Integer> tails = new ArrayList<>();
    
    for (int num : nums) {
        int pos = Collections.binarySearch(tails, num);
        if (pos < 0) pos = -(pos + 1);  // Insertion point
        
        if (pos == tails.size()) {
            tails.add(num);
        } else {
            tails.set(pos, num);
        }
    }
    
    return tails.size();
}
```

### Sub-Pattern E: Grid DP (Unique Paths)

```java
public int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    
    // Base case: first row and first column have only 1 way
    for (int i = 0; i < m; i++) dp[i][0] = 1;
    for (int j = 0; j < n; j++) dp[0][j] = 1;
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[i][j] = dp[i - 1][j] + dp[i][j - 1];  // From top + from left
        }
    }
    
    return dp[m - 1][n - 1];
}
```

### Sub-Pattern F: String DP (Word Break)

```java
public boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;  // Empty string is "breakable"
    
    for (int i = 1; i <= s.length(); i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    
    return dp[s.length()];
}
```

### Sub-Pattern G: Decision DP (House Robber)

```java
public int rob(int[] nums) {
    if (nums.length == 1) return nums[0];
    
    int prev2 = 0, prev1 = 0;
    
    for (int num : nums) {
        int current = Math.max(prev1, prev2 + num);  // Skip or rob
        prev2 = prev1;
        prev1 = current;
    }
    
    return prev1;
}
```

### DP Pattern Recognition Cheat Sheet

| If the problem asks... | DP Sub-Pattern | State Definition |
|----------------------|---------------|-----------------|
| Min/max value with limited items | 0/1 Knapsack | dp[i][capacity] |
| Min/max value with unlimited items | Unbounded Knapsack | dp[capacity] |
| Longest common subsequence/substring | LCS | dp[i][j] = length |
| Longest increasing subsequence | LIS | dp[i] or patience sort |
| Number of paths in grid | Grid DP | dp[i][j] = count |
| Can a string be segmented | String DP | dp[i] = bool |
| Skip or take (adjacent constraint) | Decision DP | dp[i] or prev1/prev2 |
| Palindrome related | Interval DP | dp[i][j] = bool/count |
| Problems on ranges [i, j] | Interval DP | dp[i][j] = optimal value |

---

## Pattern Recognition Decision Tree

Use this flowchart when you encounter a new problem:

```
Is the input sorted or can it be sorted?
├── YES → Is it about pairs/triplets?
│          ├── YES → TWO POINTERS (Pattern 1)
│          └── NO → BINARY SEARCH (Pattern 11)
└── NO → Continue below

Is it about contiguous subarray/substring?
├── YES → SLIDING WINDOW (Pattern 2)
└── NO → Continue below

Is it a linked list problem?
├── YES → Is it about cycle/middle?
│          ├── YES → FAST & SLOW POINTERS (Pattern 3)
│          └── NO → IN-PLACE REVERSAL (Pattern 6)
└── NO → Continue below

Is it about intervals/ranges?
├── YES → MERGE INTERVALS (Pattern 4)
└── NO → Continue below

Is the array [0,n] or [1,n] with missing/duplicate?
├── YES → CYCLIC SORT (Pattern 5)
└── NO → Continue below

Is it a tree/graph problem?
├── YES → Shortest path or level-order?
│          ├── YES → BFS (Pattern 7)
│          └── NO → DFS (Pattern 8)
└── NO → Continue below

Is it about K largest/smallest/frequent?
├── YES → TOP K ELEMENTS (Pattern 12)
└── NO → Continue below

Is it about merging K sorted things?
├── YES → K-WAY MERGE (Pattern 13)
└── NO → Continue below

Is it about "next greater/smaller"?
├── YES → MONOTONIC STACK (Pattern 14)
└── NO → Continue below

Is it about dependency ordering?
├── YES → TOPOLOGICAL SORT (Pattern 15)
└── NO → Continue below

Does it ask for median of stream?
├── YES → TWO HEAPS (Pattern 9)
└── NO → Continue below

Does it say "find all" or "generate all"?
├── YES → BACKTRACKING (Pattern 10)
└── NO → Continue below

Does it ask for optimal/count/possible?
├── YES → DYNAMIC PROGRAMMING (Pattern 16)
└── NO → Re-read the problem and consider combinations of patterns
```

---

## Practice Schedule: 4-Week Pattern Mastery Plan

### Week 1: Foundation Patterns (3-4 problems/day)
| Day | Pattern | Problems to Solve |
|-----|---------|------------------|
| Mon | Two Pointers | Two Sum II, Three Sum, Container With Most Water |
| Tue | Two Pointers | Remove Duplicates, Move Zeroes, Sort Colors |
| Wed | Sliding Window | Max Sum Subarray K, Longest Substring Without Repeating |
| Thu | Sliding Window | Min Window Substring, Permutation in String |
| Fri | Fast & Slow | Linked List Cycle, Happy Number, Find Middle |
| Sat | Merge Intervals | Merge Intervals, Insert Interval, Meeting Rooms |
| Sun | Review | Redo any problem you struggled with |

### Week 2: Core Patterns (3-4 problems/day)
| Day | Pattern | Problems to Solve |
|-----|---------|------------------|
| Mon | BFS | Level Order Traversal, Rotting Oranges, Word Ladder |
| Tue | DFS | Path Sum, Number of Islands, Clone Graph |
| Wed | Binary Search | Search Rotated, Find Peak, Koko Eating Bananas |
| Thu | Backtracking | Subsets, Permutations, Combination Sum |
| Fri | Backtracking | N-Queens, Word Search, Generate Parentheses |
| Sat | Cyclic Sort | Missing Number, Find Duplicate, First Missing Positive |
| Sun | Review | Redo any problem you struggled with |

### Week 3: Advanced Patterns (2-3 problems/day)
| Day | Pattern | Problems to Solve |
|-----|---------|------------------|
| Mon | Top K | Kth Largest, Top K Frequent, K Closest Points |
| Tue | K-Way Merge | Merge K Lists, Kth Smallest in Matrix |
| Wed | Monotonic Stack | Next Greater, Daily Temperatures, Largest Rectangle |
| Thu | Two Heaps | Find Median, Sliding Window Median |
| Fri | Topological Sort | Course Schedule I/II, Alien Dictionary |
| Sat | Linked List Reversal | Reverse List, Reverse Between, K-Group |
| Sun | Review | Redo any problem you struggled with |

### Week 4: Dynamic Programming (2-3 problems/day)
| Day | Pattern | Problems to Solve |
|-----|---------|------------------|
| Mon | DP: Knapsack | 0/1 Knapsack, Partition Equal Subset Sum |
| Tue | DP: Unbounded | Coin Change, Unbounded Knapsack |
| Wed | DP: LCS/LIS | Longest Common Subsequence, LIS |
| Thu | DP: Grid | Unique Paths, Minimum Path Sum |
| Fri | DP: String | Word Break, Palindrome Partitioning |
| Sat | DP: Decision | House Robber, Jump Game |
| Sun | Mixed Review | 3 random problems from different patterns |

---

## Quick Reference Card

| Pattern | Data Structure | Time | Key Template Line |
|---------|---------------|------|-------------------|
| Two Pointers | Array | O(n) | `while (left < right)` |
| Sliding Window | HashMap/Array | O(n) | `while (constraint violated) left++` |
| Fast & Slow | Linked List | O(n) | `slow = slow.next; fast = fast.next.next` |
| Merge Intervals | Sort + List | O(n log n) | `if (current[0] <= last[1])` merge |
| Cyclic Sort | Array | O(n) | `while (nums[i] != nums[correctIdx])` swap |
| Linked List Reversal | Linked List | O(n) | `curr.next = prev; prev = curr` |
| BFS | Queue | O(V+E) | `queue.offer(); while (!queue.isEmpty())` |
| DFS | Stack/Recursion | O(V+E) | `visited[node] = true; for (neighbor)` |
| Two Heaps | 2 PriorityQueues | O(log n) | `maxHeap + minHeap balanced` |
| Backtracking | Recursion | O(2^n) | `choose → explore → unchoose` |
| Binary Search | Array | O(log n) | `mid = left + (right - left) / 2` |
| Top K | Min/Max Heap | O(n log k) | `if (heap.size() > k) heap.poll()` |
| K-Way Merge | Min Heap | O(n log k) | `heap.poll() then push next from same list` |
| Monotonic Stack | Stack | O(n) | `while (!stack.isEmpty() && top < current) pop` |
| Topological Sort | Queue + inDegree | O(V+E) | `if (inDegree[i] == 0) queue.offer(i)` |
| Dynamic Programming | Array/Table | varies | `dp[i] = optimize(dp[i-1], dp[i-2], ...)` |

