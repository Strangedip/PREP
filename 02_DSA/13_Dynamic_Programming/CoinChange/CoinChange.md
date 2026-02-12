# Coin Change

## Problem Statement
You are given an integer array `coins` representing coins of different denominations and an integer `amount` representing a total amount of money.

Return the **fewest number of coins** that you need to make up that amount. If that amount cannot be made up by any combination of coins, return `-1`.

You may assume that you have an **infinite number** of each kind of coin.

**Examples:**
```
Input: coins = [1,3,4], amount = 6
Output: 2
Explanation: 6 = 3 + 3

Input: coins = [2], amount = 3  
Output: -1
Explanation: Cannot make 3 with only coin 2
```

## Problem Analysis

### Core Insight
This is a classic **optimization DP problem** where we want to minimize the number of coins used.

### DP Pattern Recognition
- **Optimal Substructure**: If we know the minimum coins for amount `i-coin`, we can find minimum for amount `i`
- **Overlapping Subproblems**: Same amounts are computed multiple times
- **Recurrence Relation**: `dp[amount] = min(dp[amount - coin] + 1)` for all valid coins

## Approaches

### Approach 1: Bottom-Up DP (Tabulation) ⭐ (Recommended)

#### Key Insight
Build solution iteratively from smaller amounts to target amount.

#### Algorithm
1. Create `dp` array where `dp[i]` = minimum coins for amount `i`
2. Initialize `dp[0] = 0` (base case)
3. For each amount from 1 to target:
   - Try each coin that's ≤ current amount
   - Update minimum: `dp[i] = min(dp[i], dp[i-coin] + 1)`

#### Time Complexity
- **O(amount × coins.length)** - nested loops

#### Space Complexity
- **O(amount)** - DP array

```java
public int coinChangeBottomUp(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1); // Fill with impossible value
    
    dp[0] = 0; // Base case
    
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

### Approach 2: Top-Down DP (Memoization)

#### Key Insight
Use recursion with memoization to compute minimum coins recursively.

#### Algorithm
1. Base cases: amount < 0 → invalid, amount = 0 → 0 coins
2. If already computed, return memoized result
3. Try each coin, recursively compute minimum for remaining amount
4. Return minimum across all coin choices

#### Time Complexity
- **O(amount × coins.length)** - each amount computed once

#### Space Complexity
- **O(amount)** - memoization array + recursion stack

```java
public int coinChangeTopDown(int[] coins, int amount) {
    int[] memo = new int[amount + 1];
    Arrays.fill(memo, -2); // -2 = not computed
    
    int result = helper(coins, amount, memo);
    return result == Integer.MAX_VALUE ? -1 : result;
}

private int helper(int[] coins, int amount, int[] memo) {
    if (amount < 0) return Integer.MAX_VALUE;
    if (amount == 0) return 0;
    
    if (memo[amount] != -2) {
        return memo[amount] == -1 ? Integer.MAX_VALUE : memo[amount];
    }
    
    int minCoins = Integer.MAX_VALUE;
    for (int coin : coins) {
        int subResult = helper(coins, amount - coin, memo);
        if (subResult != Integer.MAX_VALUE) {
            minCoins = Math.min(minCoins, subResult + 1);
        }
    }
    
    memo[amount] = (minCoins == Integer.MAX_VALUE) ? -1 : minCoins;
    return minCoins;
}
```

### Approach 3: BFS (Creative Alternative)

#### Key Insight
Treat as shortest path problem: each coin use = one step, find shortest path to target amount.

#### Algorithm
1. Start from amount 0, use BFS level by level
2. Each level represents using one more coin
3. For each current amount, try adding each coin
4. First time we reach target amount = minimum coins

#### Time Complexity
- **O(amount × coins.length)** - similar to DP

#### Space Complexity
- **O(amount)** - queue and visited set

## Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Bottom-Up DP | O(S×n) | O(S) | Iterative, space-efficient | Must solve all subproblems |
| Top-Down DP | O(S×n) | O(S) | Intuitive recursion | Recursion overhead |
| BFS | O(S×n) | O(S) | Creative shortest-path view | More complex implementation |

*S = amount, n = number of coins*

## Example Trace (Bottom-Up DP)

**Input**: coins = [1,3,4], amount = 6

**DP Array Building**:
```
dp[0] = 0  (base case)

dp[1]: Try coins [1,3,4]
  - coin 1: dp[1] = min(∞, dp[0]+1) = min(∞, 1) = 1

dp[2]: Try coins [1,3,4]  
  - coin 1: dp[2] = min(∞, dp[1]+1) = min(∞, 2) = 2

dp[3]: Try coins [1,3,4]
  - coin 1: dp[3] = min(∞, dp[2]+1) = min(∞, 3) = 3
  - coin 3: dp[3] = min(3, dp[0]+1) = min(3, 1) = 1

dp[4]: Try coins [1,3,4]
  - coin 1: dp[4] = min(∞, dp[3]+1) = min(∞, 2) = 2
  - coin 3: dp[4] = min(2, dp[1]+1) = min(2, 2) = 2  
  - coin 4: dp[4] = min(2, dp[0]+1) = min(2, 1) = 1

dp[5]: Try coins [1,3,4]
  - coin 1: dp[5] = min(∞, dp[4]+1) = min(∞, 2) = 2
  - coin 3: dp[5] = min(2, dp[2]+1) = min(2, 3) = 2
  - coin 4: dp[5] = min(2, dp[1]+1) = min(2, 2) = 2

dp[6]: Try coins [1,3,4]
  - coin 1: dp[6] = min(∞, dp[5]+1) = min(∞, 3) = 3
  - coin 3: dp[6] = min(3, dp[3]+1) = min(3, 2) = 2
  - coin 4: dp[6] = min(2, dp[2]+1) = min(2, 3) = 2
```

**Result**: dp[6] = 2 (using coins 3+3)

## Extensions

### 1. Coin Change II - Count Ways
Count number of ways to make the amount:

```java
public int coinChangeWays(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    dp[0] = 1; // One way to make 0
    
    for (int coin : coins) {
        for (int i = coin; i <= amount; i++) {
            dp[i] += dp[i - coin];
        }
    }
    
    return dp[amount];
}
```

### 2. Print Actual Coins Used
Track parent pointers to reconstruct solution:

```java
public List<Integer> getCoinCombination(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    int[] parent = new int[amount + 1];
    // ... fill dp and parent arrays ...
    
    // Reconstruct path
    List<Integer> result = new ArrayList<>();
    int current = amount;
    while (current > 0) {
        int coin = parent[current];
        result.add(coin);
        current -= coin;
    }
    return result;
}
```

## Key Insights

### Why DP Works
- **Optimal Substructure**: Optimal solution contains optimal solutions to subproblems
- **Overlapping Subproblems**: Same amounts computed multiple times in naive recursion

### State Definition
- **State**: `dp[i]` = minimum coins needed for amount `i`
- **Transition**: `dp[i] = min(dp[i-coin] + 1)` for all valid coins
- **Base Case**: `dp[0] = 0`

### Edge Cases
- Amount = 0: Return 0
- No solution possible: Return -1
- Single coin type: Check if amount is divisible

## Interview Tips

1. **Start with recurrence relation**: Clearly state the DP formula
2. **Explain state meaning**: What does `dp[i]` represent?
3. **Handle base cases**: `dp[0] = 0`, impossible values
4. **Discuss optimizations**: Space optimization, early termination
5. **Consider variations**: Coin Change II, limited coins, printing solution

## Common Mistakes

1. **Wrong initialization**: Using 0 instead of impossible value (∞)
2. **Off-by-one errors**: Array bounds, loop conditions
3. **Not handling impossible cases**: Forgetting to return -1
4. **Coin order dependency**: In counting ways variation

## Applications
- **Currency exchange**: Minimum bills/coins for amount
- **Resource allocation**: Minimum units to achieve target
- **Knapsack variants**: Unbounded knapsack with optimization
- **Change-making algorithms**: Real-world cashier systems

The Coin Change problem is fundamental because it introduces core DP concepts while being practical and intuitive! 