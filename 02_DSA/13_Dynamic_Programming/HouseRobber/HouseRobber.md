# House Robber Series

## Problem Statement
You are a professional robber planning to rob houses. Each house has a certain amount of money stashed. The constraint is that **adjacent houses have security systems** - if two adjacent houses are robbed on the same night, the police will be alerted.

Return the **maximum amount of money** you can rob without alerting the police.

## Problem Variations

### House Robber I: Linear Street
**Houses arranged in a line**
```
Input: nums = [1,2,3,1]
Output: 4
Explanation: Rob house 0 (money = 1) and house 2 (money = 3).
             Total = 1 + 3 = 4.
```

### House Robber II: Circular Street  
**Houses arranged in a circle** (first and last are adjacent)
```
Input: nums = [2,3,2]
Output: 3
Explanation: Cannot rob houses 0 and 2 (adjacent in circle).
             Rob house 1 for money = 3.
```

### House Robber III: Binary Tree
**Houses arranged as binary tree nodes** (cannot rob parent and child)

## Analysis

### Core DP Pattern
- **Optimal Substructure**: Maximum money at house `i` depends on optimal solutions for previous houses
- **Overlapping Subproblems**: Same subproblems computed multiple times in naive recursion
- **State Choice**: At each house, choose to rob or not rob

## House Robber I: Linear Street

### Approach 1: Bottom-Up DP ⭐ (Recommended)

#### Key Insight
For each house, decide: rob current house + best from 2 houses ago, OR skip current house.

#### Recurrence Relation
```
dp[i] = max(dp[i-1], dp[i-2] + nums[i])
```
- `dp[i-1]`: Skip current house, take best up to previous house
- `dp[i-2] + nums[i]`: Rob current house + best from 2 houses ago

#### Algorithm
1. Handle base cases: 0 houses → 0, 1 house → nums[0]
2. Initialize: `dp[0] = nums[0]`, `dp[1] = max(nums[0], nums[1])`
3. For each house from 2 to n-1: apply recurrence relation

#### Time Complexity
- **O(n)** - single pass through houses

#### Space Complexity
- **O(n)** - DP array, can be optimized to **O(1)**

```java
public int robLinear(int[] nums) {
    if (nums.length == 0) return 0;
    if (nums.length == 1) return nums[0];
    
    int[] dp = new int[nums.length];
    dp[0] = nums[0];
    dp[1] = Math.max(nums[0], nums[1]);
    
    for (int i = 2; i < nums.length; i++) {
        dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]);
    }
    
    return dp[nums.length - 1];
}
```

### Space Optimization: O(1) Solution
Since we only need previous 2 values:

```java
public int robLinearOptimized(int[] nums) {
    if (nums.length == 0) return 0;
    if (nums.length == 1) return nums[0];
    
    int prev2 = nums[0];
    int prev1 = Math.max(nums[0], nums[1]);
    
    for (int i = 2; i < nums.length; i++) {
        int current = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1;
        prev1 = current;
    }
    
    return prev1;
}
```

### Approach 2: Top-Down DP (Memoization)

```java
public int robTopDown(int[] nums) {
    int[] memo = new int[nums.length];
    Arrays.fill(memo, -1);
    return robHelper(nums, nums.length - 1, memo);
}

private int robHelper(int[] nums, int i, int[] memo) {
    if (i < 0) return 0;
    if (i == 0) return nums[0];
    
    if (memo[i] != -1) return memo[i];
    
    memo[i] = Math.max(robHelper(nums, i-1, memo), 
                      robHelper(nums, i-2, memo) + nums[i]);
    return memo[i];
}
```

## House Robber II: Circular Street

### Key Challenge
**First and last houses are adjacent** - cannot rob both.

### Solution Strategy
**Break into two subproblems:**
1. **Case 1**: Rob houses 0 to n-2 (exclude last house)
2. **Case 2**: Rob houses 1 to n-1 (exclude first house)
3. **Return**: Maximum of both cases

#### Why This Works
- If we rob house 0, we cannot rob house n-1
- If we rob house n-1, we cannot rob house 0  
- One of these cases will give the optimal solution

```java
public int robCircular(int[] nums) {
    if (nums.length == 1) return nums[0];
    if (nums.length == 2) return Math.max(nums[0], nums[1]);
    
    // Case 1: Rob houses 0 to n-2
    int maxExcludingLast = robRange(nums, 0, nums.length - 2);
    
    // Case 2: Rob houses 1 to n-1  
    int maxExcludingFirst = robRange(nums, 1, nums.length - 1);
    
    return Math.max(maxExcludingLast, maxExcludingFirst);
}

private int robRange(int[] nums, int start, int end) {
    int prev2 = 0, prev1 = 0;
    
    for (int i = start; i <= end; i++) {
        int current = Math.max(prev1, prev2 + nums[i]);
        prev2 = prev1;
        prev1 = current;
    }
    
    return prev1;
}
```

## House Robber III: Binary Tree

### Key Challenge
**Tree structure** - cannot rob directly connected parent-child nodes.

### Approach 1: Return Two States ⭐ (Optimal)

#### Key Insight
For each node, track two values:
- Maximum money **without robbing** current node
- Maximum money **with robbing** current node

#### Algorithm
```java
public int robTree(TreeNode root) {
    int[] result = robTreeHelper(root);
    return Math.max(result[0], result[1]);
}

private int[] robTreeHelper(TreeNode root) {
    if (root == null) return new int[]{0, 0};
    
    int[] left = robTreeHelper(root.left);
    int[] right = robTreeHelper(root.right);
    
    // If rob current: cannot rob children
    int withRoot = root.val + left[0] + right[0];
    
    // If don't rob current: choose optimally from children
    int withoutRoot = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);
    
    return new int[]{withoutRoot, withRoot};
}
```

### Approach 2: Memoization
```java
public int robTreeMemo(TreeNode root) {
    Map<TreeNode, Integer> memo = new HashMap<>();
    return robTreeHelper(root, memo);
}

private int robTreeHelper(TreeNode root, Map<TreeNode, Integer> memo) {
    if (root == null) return 0;
    if (memo.containsKey(root)) return memo.get(root);
    
    // Option 1: Rob current + grandchildren
    int robCurrent = root.val;
    if (root.left != null) {
        robCurrent += robTreeHelper(root.left.left, memo) + 
                     robTreeHelper(root.left.right, memo);
    }
    if (root.right != null) {
        robCurrent += robTreeHelper(root.right.left, memo) + 
                     robTreeHelper(root.right.right, memo);
    }
    
    // Option 2: Skip current, rob children
    int skipCurrent = robTreeHelper(root.left, memo) + 
                     robTreeHelper(root.right, memo);
    
    int result = Math.max(robCurrent, skipCurrent);
    memo.put(root, result);
    return result;
}
```

## Example Traces

### Linear Example: [2, 7, 9, 3, 1]

**DP Array Building:**
```
dp[0] = 2
dp[1] = max(2, 7) = 7  
dp[2] = max(7, 2+9) = max(7, 11) = 11
dp[3] = max(11, 7+3) = max(11, 10) = 11
dp[4] = max(11, 11+1) = max(11, 12) = 12
```
**Result:** 12 (rob houses at indices 0, 2, 4)

### Circular Example: [2, 3, 2]

**Case 1** (exclude last): [2, 3] → max = 3
**Case 2** (exclude first): [3, 2] → max = 3  
**Result:** max(3, 3) = 3

### Tree Example:
```
      3
     / \
    2   3
     \   \
      3   1
```

**Analysis:**
- Rob root 3: 3 + (cannot rob children) + grandchildren = 3 + 3 + 1 = 7
- Skip root: max from children = max(2+3, 3+1) = max(5, 4) = 5
- **Result:** max(7, 5) = 7

## Comparison

| Variation | Time | Space | Key Challenge |
|-----------|------|-------|---------------|
| Linear | O(n) | O(1) | Adjacent constraint |
| Circular | O(n) | O(1) | First-last adjacency |
| Tree | O(n) | O(h) | Parent-child constraint |

## Key Insights

### State Transition Patterns
1. **Linear**: Choice between rob/skip with 2-step gap
2. **Circular**: Split into two linear subproblems  
3. **Tree**: Track rob/not-rob states for each node

### Space Optimization
- **Linear**: Only need last 2 values → O(1) space
- **Circular**: Reuse linear solution → O(1) space
- **Tree**: Height-dependent, inherently O(h) for recursion

### Why Greedy Fails
Greedy (always rob highest value) fails because:
- Local optimal ≠ global optimal
- Must consider long-term consequences of adjacent constraints

## Interview Tips

1. **Start with linear version** - establishes core DP pattern
2. **Explain the recurrence clearly** - why max(skip, rob + prev2)
3. **Demonstrate space optimization** - show O(n) → O(1) progression  
4. **Handle circular version** - explain why splitting works
5. **Tree version challenges** - discuss state tracking approach

## Common Mistakes

1. **Wrong recurrence**: Using `dp[i-1] + nums[i]` (ignoring adjacency)
2. **Base case errors**: Not handling 0, 1, 2 house cases properly
3. **Circular logic**: Trying to solve directly instead of splitting
4. **Tree traversal**: Forgetting to handle null nodes or wrong state tracking

The House Robber series excellently demonstrates how DP patterns evolve across different data structures while maintaining core optimization principles! 