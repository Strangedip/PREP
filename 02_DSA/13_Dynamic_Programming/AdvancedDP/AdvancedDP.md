# Advanced Dynamic Programming Patterns

## Problem Statement
Advanced DP patterns that go beyond basic 1D/2D DP and are essential for solving complex optimization problems at SDE2+ level.

**Key Patterns Covered:**
- **Interval DP:** Problems on ranges/intervals  
- **Bitmask DP:** Using bit manipulation with DP
- **Tree DP:** Dynamic programming on trees
- **Digit DP:** Problems on digit constraints
- **Probability DP:** Expected value problems

## Pattern 1: Interval DP

### Problem: Matrix Chain Multiplication

**Core Insight:** For interval [i, j], optimal solution depends on optimal solutions of sub-intervals.

```java
public int matrixChainMultiplication(int[] dims) {
    int n = dims.length - 1; // Number of matrices
    int[][] dp = new int[n][n];
    
    // l is chain length
    for (int l = 2; l <= n; l++) {
        for (int i = 0; i <= n - l; i++) {
            int j = i + l - 1;
            dp[i][j] = Integer.MAX_VALUE;
            
            // Try all possible splits
            for (int k = i; k < j; k++) {
                int cost = dp[i][k] + dp[k + 1][j] + 
                          dims[i] * dims[k + 1] * dims[j + 1];
                dp[i][j] = Math.min(dp[i][j], cost);
            }
        }
    }
    
    return dp[0][n - 1];
}
```

### Pattern Template:
```java
// For interval [i, j]
for (int len = 2; len <= n; len++) {
    for (int i = 0; i <= n - len; i++) {
        int j = i + len - 1;
        for (int k = i; k < j; k++) {
            dp[i][j] = optimize(dp[i][k], dp[k+1][j], cost(i, k, j));
        }
    }
}
```

### More Interval DP Problems:
```java
// Palindrome Partitioning
public int minCut(String s) {
    int n = s.length();
    boolean[][] isPalindrome = new boolean[n][n];
    int[] cuts = new int[n];
    
    // Precompute palindromes
    for (int i = 0; i < n; i++) {
        cuts[i] = i; // Max cuts needed
        for (int j = 0; j <= i; j++) {
            if (s.charAt(i) == s.charAt(j) && 
                (i - j <= 1 || isPalindrome[j + 1][i - 1])) {
                isPalindrome[j][i] = true;
                cuts[i] = (j == 0) ? 0 : Math.min(cuts[i], cuts[j - 1] + 1);
            }
        }
    }
    
    return cuts[n - 1];
}
```

## Pattern 2: Bitmask DP

### Problem: Traveling Salesman Problem (TSP)

**Core Insight:** Use bitmask to represent visited cities, DP state is (current_city, visited_mask).

```java
public int tsp(int[][] dist) {
    int n = dist.length;
    int MASK_ALL = (1 << n) - 1;
    
    // dp[mask][i] = min cost to visit cities in mask, ending at city i
    int[][] dp = new int[1 << n][n];
    
    // Initialize
    for (int i = 0; i < (1 << n); i++) {
        Arrays.fill(dp[i], Integer.MAX_VALUE / 2);
    }
    dp[1][0] = 0; // Start at city 0
    
    for (int mask = 1; mask < (1 << n); mask++) {
        for (int u = 0; u < n; u++) {
            if ((mask & (1 << u)) == 0) continue; // u not in mask
            
            for (int v = 0; v < n; v++) {
                if (u == v || (mask & (1 << v)) == 0) continue;
                
                int prevMask = mask ^ (1 << u); // Remove u from mask
                dp[mask][u] = Math.min(dp[mask][u], 
                                     dp[prevMask][v] + dist[v][u]);
            }
        }
    }
    
    // Find minimum cost to return to start
    int result = Integer.MAX_VALUE;
    for (int i = 1; i < n; i++) {
        result = Math.min(result, dp[MASK_ALL][i] + dist[i][0]);
    }
    
    return result;
}
```

### Bitmask DP Template:
```java
// For subset problems
for (int mask = 0; mask < (1 << n); mask++) {
    for (int i = 0; i < n; i++) {
        if (mask & (1 << i)) {
            // Process state where element i is included
            int prevMask = mask ^ (1 << i);
            dp[mask] = optimize(dp[mask], dp[prevMask] + cost[i]);
        }
    }
}
```

### Assignment Problem (Hungarian Algorithm DP):
```java
public int assignmentProblem(int[][] cost) {
    int n = cost.length;
    int[] dp = new int[1 << n];
    Arrays.fill(dp, Integer.MAX_VALUE);
    dp[0] = 0;
    
    for (int mask = 0; mask < (1 << n); mask++) {
        if (dp[mask] == Integer.MAX_VALUE) continue;
        
        int person = Integer.bitCount(mask); // Next person to assign
        if (person >= n) continue;
        
        for (int job = 0; job < n; job++) {
            if ((mask & (1 << job)) == 0) { // Job not assigned
                int newMask = mask | (1 << job);
                dp[newMask] = Math.min(dp[newMask], dp[mask] + cost[person][job]);
            }
        }
    }
    
    return dp[(1 << n) - 1];
}
```

## Pattern 3: Tree DP

### Problem: Maximum Path Sum in Binary Tree

**Core Insight:** For each node, compute max path passing through it vs max path in its subtree.

```java
class TreeDP {
    private int maxSum = Integer.MIN_VALUE;
    
    public int maxPathSum(TreeNode root) {
        maxPathHelper(root);
        return maxSum;
    }
    
    private int maxPathHelper(TreeNode node) {
        if (node == null) return 0;
        
        // Max path sum ending at left/right child
        int leftMax = Math.max(0, maxPathHelper(node.left));
        int rightMax = Math.max(0, maxPathHelper(node.right));
        
        // Max path passing through current node
        int currentMax = node.val + leftMax + rightMax;
        maxSum = Math.max(maxSum, currentMax);
        
        // Return max path ending at current node (can extend upward)
        return node.val + Math.max(leftMax, rightMax);
    }
}
```

### Tree DP Template:
```java
// For tree problems with choices at each node
public int treeDP(TreeNode node) {
    if (node == null) return baseCase;
    
    int leftDP = treeDP(node.left);
    int rightDP = treeDP(node.right);
    
    // Update global answer if needed
    globalAnswer = optimize(globalAnswer, combine(node.val, leftDP, rightDP));
    
    // Return optimal value for parent
    return optimalForParent(node.val, leftDP, rightDP);
}
```

### House Robber III (Tree Version):
```java
class Solution {
    public int rob(TreeNode root) {
        int[] result = robHelper(root);
        return Math.max(result[0], result[1]);
    }
    
    // Returns [maxIfNotRobbed, maxIfRobbed]
    private int[] robHelper(TreeNode node) {
        if (node == null) return new int[]{0, 0};
        
        int[] left = robHelper(node.left);
        int[] right = robHelper(node.right);
        
        int notRobbed = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
        int robbed = node.val + left[0] + right[0];
        
        return new int[]{notRobbed, robbed};
    }
}
```

## Pattern 4: Digit DP

### Problem: Count Numbers with Sum of Digits = K

**Core Insight:** Build number digit by digit with constraints.

```java
public int countNumbersWithSum(int n, int k) {
    int[][][] memo = new int[n + 1][k + 1][2];
    for (int[][] arr : memo) {
        for (int[] row : arr) {
            Arrays.fill(row, -1);
        }
    }
    return digitDP(n, k, 1, memo); // Start with tight constraint
}

private int digitDP(int pos, int sum, int tight, int[][][] memo) {
    if (pos == 0) {
        return sum == 0 ? 1 : 0;
    }
    
    if (memo[pos][sum][tight] != -1) {
        return memo[pos][sum][tight];
    }
    
    int limit = tight == 1 ? getDigitAt(pos) : 9;
    int result = 0;
    
    for (int digit = 0; digit <= limit && digit <= sum; digit++) {
        int newTight = (tight == 1 && digit == limit) ? 1 : 0;
        result += digitDP(pos - 1, sum - digit, newTight, memo);
    }
    
    return memo[pos][sum][tight] = result;
}
```

## Pattern 5: Probability DP

### Problem: Expected Steps to Reach Target

```java
public double expectedSteps(int target) {
    // dp[i] = expected steps to reach target from position i
    double[] dp = new double[target + 1];
    
    for (int i = target - 1; i >= 0; i--) {
        if (i + 1 >= target) {
            dp[i] = 1; // One step to reach target
        } else {
            // E[X] = 1 + 0.5 * E[from i+1] + 0.5 * E[from i-1]
            dp[i] = 1 + 0.5 * dp[i + 1] + 0.5 * (i > 0 ? dp[i - 1] : dp[i]);
        }
    }
    
    return dp[0];
}
```

## Advanced Techniques:

### 1. Space Optimization:
```java
// For interval DP, sometimes we can optimize space
// by processing diagonals instead of full 2D array
```

### 2. Divide and Conquer DP Optimization:
```java
// For DP with specific cost function properties
// Can reduce O(n³) to O(n² log n) using divide and conquer
```

### 3. Convex Hull Optimization:
```java
// For DP with linear cost functions
// Can reduce O(n²) to O(n) using convex hull trick
```

## When to Use Each Pattern:

### ✅ Interval DP:
- Matrix chain multiplication type problems
- Palindrome partitioning
- Optimal binary search trees
- Burst balloons

### ✅ Bitmask DP:
- Subset problems with constraints
- Assignment problems  
- Traveling salesman variants
- State space ≤ 2²⁰

### ✅ Tree DP:
- Optimization problems on trees
- Path problems in trees
- Subtree aggregation problems

### ✅ Digit DP:
- Count numbers with digit constraints
- Numbers with specific properties
- Range counting problems

## LeetCode Similar Problems:
- [312. Burst Balloons](https://leetcode.com/problems/burst-balloons/) (Interval DP)
- [464. Can I Win](https://leetcode.com/problems/can-i-win/) (Bitmask DP)
- [337. House Robber III](https://leetcode.com/problems/house-robber-iii/) (Tree DP)
- [233. Number of Digit One](https://leetcode.com/problems/number-of-digit-one/) (Digit DP)
- [808. Soup Servings](https://leetcode.com/problems/soup-servings/) (Probability DP)

## Interview Tips:
- **Identify the pattern** from problem constraints
- **Start with brute force** then optimize with memoization
- **Define state carefully** - this is crucial for advanced DP
- **Practice state space reduction** techniques
- **These patterns separate SDE2 from SDE1** candidates 