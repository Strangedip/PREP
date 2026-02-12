# 0/1 Knapsack Problem

## Problem Statement

Given `n` items, each with a weight `weights[i]` and a value `values[i]`, and a knapsack with capacity `W`, find the maximum total value you can put in the knapsack. Each item can either be included or excluded (you cannot take a fraction of an item).

### Examples

```
Input:  weights = [1, 3, 4, 5], values = [1, 4, 5, 7], W = 7
Output: 9
Explanation: Take items with weights 3 and 4 (values 4 + 5 = 9).

Input:  weights = [2, 3, 4, 5], values = [3, 4, 5, 6], W = 5
Output: 7
Explanation: Take items with weights 2 and 3 (values 3 + 4 = 7).
```

---

## Why This Problem Is Important

The 0/1 Knapsack is the **most fundamental DP problem** and the parent pattern for a large family of problems:

| Problem | Knapsack Variant |
|---------|-----------------|
| Subset Sum | Knapsack where value = weight, check if target sum exists |
| Partition Equal Subset Sum | Knapsack with target = totalSum / 2 |
| Coin Change | Unbounded Knapsack (items can be reused) |
| Target Sum | Knapsack with +/- choices |
| Last Stone Weight II | Partition into two groups, minimize difference |
| Ones and Zeroes | 2D Knapsack (two constraints) |

If you master 0/1 Knapsack, you can solve this entire family of problems.

---

## Approach 1: Recursion (Brute Force)

**Time**: O(2^n), **Space**: O(n) — recursion stack

For each item, we have two choices: include it or exclude it.

```java
class Solution {
    public int knapsack(int[] weights, int[] values, int W) {
        return solve(weights, values, W, weights.length - 1);
    }

    private int solve(int[] weights, int[] values, int remaining, int i) {
        // Base case: no items left or no capacity
        if (i < 0 || remaining <= 0) return 0;

        // If current item is too heavy, skip it
        if (weights[i] > remaining) {
            return solve(weights, values, remaining, i - 1);
        }

        // Choice 1: Include item i
        int include = values[i] + solve(weights, values, remaining - weights[i], i - 1);

        // Choice 2: Exclude item i
        int exclude = solve(weights, values, remaining, i - 1);

        return Math.max(include, exclude);
    }
}
```

---

## Approach 2: Memoization (Top-Down DP)

**Time**: O(n × W), **Space**: O(n × W)

Cache the results of `(item index, remaining capacity)` pairs.

```java
class Solution {
    private int[][] memo;

    public int knapsack(int[] weights, int[] values, int W) {
        int n = weights.length;
        memo = new int[n][W + 1];
        for (int[] row : memo) Arrays.fill(row, -1);
        return solve(weights, values, W, n - 1);
    }

    private int solve(int[] weights, int[] values, int remaining, int i) {
        if (i < 0 || remaining <= 0) return 0;
        if (memo[i][remaining] != -1) return memo[i][remaining];

        if (weights[i] > remaining) {
            memo[i][remaining] = solve(weights, values, remaining, i - 1);
        } else {
            int include = values[i] + solve(weights, values, remaining - weights[i], i - 1);
            int exclude = solve(weights, values, remaining, i - 1);
            memo[i][remaining] = Math.max(include, exclude);
        }

        return memo[i][remaining];
    }
}
```

---

## Approach 3: Bottom-Up DP (Tabulation) — Standard

**Time**: O(n × W), **Space**: O(n × W)

### DP State Definition

`dp[i][w]` = maximum value achievable using items `0` through `i` with knapsack capacity `w`.

### Transition

```
If weights[i] > w:
    dp[i][w] = dp[i-1][w]                              // Cannot include item i
Else:
    dp[i][w] = max(
        dp[i-1][w],                                     // Exclude item i
        values[i] + dp[i-1][w - weights[i]]            // Include item i
    )
```

### Visual Walkthrough

```
weights = [1, 3, 4, 5], values = [1, 4, 5, 7], W = 7

        w=0  w=1  w=2  w=3  w=4  w=5  w=6  w=7
item 0:  0    1    1    1    1    1    1    1    (weight=1, value=1)
item 1:  0    1    1    4    5    5    5    5    (weight=3, value=4)
item 2:  0    1    1    4    5    6    6    9    (weight=4, value=5)
item 3:  0    1    1    4    5    7    8    9    (weight=5, value=7)

Answer: dp[3][7] = 9

Items selected: items 1 and 2 (weights 3+4=7, values 4+5=9)
```

### Java Implementation

```java
class Solution {
    public int knapsack(int[] weights, int[] values, int W) {
        int n = weights.length;
        int[][] dp = new int[n + 1][W + 1];

        // Base case: dp[0][w] = 0 for all w (no items = no value)

        for (int i = 1; i <= n; i++) {
            for (int w = 0; w <= W; w++) {
                // Default: exclude current item
                dp[i][w] = dp[i - 1][w];

                // Include current item if it fits
                if (weights[i - 1] <= w) {
                    dp[i][w] = Math.max(
                        dp[i][w],
                        values[i - 1] + dp[i - 1][w - weights[i - 1]]
                    );
                }
            }
        }

        return dp[n][W];
    }
}
```

---

## Approach 4: Space-Optimized DP (1D Array)

**Time**: O(n × W), **Space**: O(W)

Since each row only depends on the previous row, and we only look left-and-up, we can use a single 1D array if we iterate weights in **reverse**.

### Why Reverse Order?

If we iterate left-to-right, `dp[w - weights[i]]` would use the updated value from the current item (allowing the item to be used multiple times — that is the Unbounded Knapsack). By iterating right-to-left, we ensure we use the previous row's values.

```java
class Solution {
    public int knapsack(int[] weights, int[] values, int W) {
        int n = weights.length;
        int[] dp = new int[W + 1];

        for (int i = 0; i < n; i++) {
            // Iterate in REVERSE to prevent using the same item twice
            for (int w = W; w >= weights[i]; w--) {
                dp[w] = Math.max(dp[w], values[i] + dp[w - weights[i]]);
            }
        }

        return dp[W];
    }
}
```

> **Critical**: The reverse iteration is what makes this 0/1 (each item used at most once). Forward iteration gives you Unbounded Knapsack (items can be reused). This is a very common interview question: "Why do you iterate in reverse?"

---

## Reconstructing the Selected Items

To find which items were selected, backtrack through the 2D DP table:

```java
public List<Integer> getSelectedItems(int[][] dp, int[] weights, int[] values, int W) {
    List<Integer> selected = new ArrayList<>();
    int w = W;

    for (int i = dp.length - 1; i >= 1; i--) {
        if (dp[i][w] != dp[i - 1][w]) {
            // Item i was included
            selected.add(i - 1); // Convert to 0-indexed
            w -= weights[i - 1];
        }
    }

    Collections.reverse(selected);
    return selected;
}
```

---

## The Knapsack Family of Problems

### 1. Subset Sum (Knapsack Variant)

**Problem**: Given an array of integers, determine if a subset sums to a target.

```java
public boolean subsetSum(int[] nums, int target) {
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;

    for (int num : nums) {
        for (int t = target; t >= num; t--) {
            dp[t] = dp[t] || dp[t - num];
        }
    }

    return dp[target];
}
```

### 2. Partition Equal Subset Sum (LeetCode 416)

**Problem**: Can the array be partitioned into two subsets with equal sum?

```java
public boolean canPartition(int[] nums) {
    int totalSum = Arrays.stream(nums).sum();
    if (totalSum % 2 != 0) return false;

    int target = totalSum / 2;
    boolean[] dp = new boolean[target + 1];
    dp[0] = true;

    for (int num : nums) {
        for (int t = target; t >= num; t--) {
            dp[t] = dp[t] || dp[t - num];
        }
    }

    return dp[target];
}
```

### 3. Target Sum (LeetCode 494)

**Problem**: Assign + or - to each number to reach a target sum.

This reduces to: find a subset with sum `(totalSum + target) / 2`.

```java
public int findTargetSumWays(int[] nums, int target) {
    int totalSum = Arrays.stream(nums).sum();
    if ((totalSum + target) % 2 != 0 || totalSum + target < 0) return 0;

    int subsetSum = (totalSum + target) / 2;
    int[] dp = new int[subsetSum + 1];
    dp[0] = 1;

    for (int num : nums) {
        for (int t = subsetSum; t >= num; t--) {
            dp[t] += dp[t - num];
        }
    }

    return dp[subsetSum];
}
```

---

## 0/1 Knapsack vs Unbounded Knapsack

| Feature | 0/1 Knapsack | Unbounded Knapsack |
|---------|-------------|-------------------|
| Item usage | Each item used at most once | Each item can be used unlimited times |
| 1D DP iteration | **Reverse** (right to left) | **Forward** (left to right) |
| Example problems | Subset Sum, Partition, Target Sum | Coin Change, Rod Cutting |
| 2D transition | `dp[i-1][w - weights[i]]` (previous row) | `dp[i][w - weights[i]]` (current row) |

---

## Complexity Analysis

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Brute Force | O(2^n) | O(n) | Exponential — TLE |
| Memoization | O(n × W) | O(n × W) | Top-down |
| Tabulation | O(n × W) | O(n × W) | Bottom-up |
| Space-Optimized | O(n × W) | O(W) | Optimal space |

Where n = number of items, W = knapsack capacity.

Note: This is pseudo-polynomial time. If W is exponentially large relative to n, this can be slow.

---

## Edge Cases

| Case | Input | Output | Why |
|------|-------|--------|-----|
| No items | `n = 0` | `0` | Nothing to pick |
| Zero capacity | `W = 0` | `0` | Cannot carry anything |
| All items fit | weights sum <= W | Sum of all values | Take everything |
| Single item fits | 1 item, weight <= W | That item's value | Trivial |
| Single item too heavy | 1 item, weight > W | `0` | Cannot take it |
| All items too heavy | All weights > W | `0` | Nothing fits |

---

## Interview Tips

1. **Always identify the knapsack pattern**: "This is a 0/1 Knapsack problem because each item has a binary choice (include or exclude) and there is a capacity constraint."
2. **Explain the state and transition clearly**: "`dp[i][w]` represents the max value using the first i items with capacity w."
3. **Know the 1D optimization and why reverse iteration matters**: This is a very common follow-up question.
4. **Connect to related problems**: "Subset Sum is a special case where value equals weight and we check for a boolean target."
5. **Mention the time complexity is pseudo-polynomial**: This shows deep understanding. The true complexity depends on the magnitude of W, not just the input size.

---

**Pattern**: 0/1 Knapsack (2D Dynamic Programming)
**Difficulty**: Medium
**Must-Know**: Yes — the foundational DP problem that unlocks an entire family of problems

