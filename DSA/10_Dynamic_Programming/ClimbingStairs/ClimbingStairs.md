# Climbing Stairs

## Problem Statement

You're climbing a staircase with `n` steps. Each time you can climb either 1 or 2 steps. How many distinct ways can you reach the top?

**Key Points:**
- You can take 1 or 2 steps at a time
- Find the number of distinct ways to reach step n
- This is a classic Dynamic Programming problem

## Example
```
Input: n = 3
Output: 3
Explanation: Three ways to climb:
1. 1 step + 1 step + 1 step
2. 1 step + 2 steps
3. 2 steps + 1 step
```

## The Pattern Recognition

This is actually the **Fibonacci sequence in disguise**!
- To reach step n, you can come from step (n-1) with 1 step, or from step (n-2) with 2 steps
- So: `ways(n) = ways(n-1) + ways(n-2)`
- This is exactly the Fibonacci recurrence relation!

## Approach 1: Recursive (Brute Force)

### The Big Idea:
Think recursively: to reach step n, you either came from step (n-1) or step (n-2).

### Code Logic:
```java
public int climbStairs(int n) {
    if (n <= 1) return 1;
    return climbStairs(n-1) + climbStairs(n-2);
}
```

### Complexity:
- **Time:** O(2^n) - Exponential due to repeated calculations
- **Space:** O(n) - Recursion stack depth

### Problems:
- **Extremely slow** for large n
- Recalculates same subproblems multiple times

## Approach 2: Memoization (Top-Down DP)

### The Optimization:
Store results of subproblems to avoid recalculation.

### Code Logic:
```java
public int climbStairs(int n) {
    Map<Integer, Integer> memo = new HashMap<>();
    return helper(n, memo);
}

private int helper(int n, Map<Integer, Integer> memo) {
    if (n <= 1) return 1;
    
    if (memo.containsKey(n)) {
        return memo.get(n);  // Use cached result
    }
    
    int result = helper(n-1, memo) + helper(n-2, memo);
    memo.put(n, result);  // Cache the result
    return result;
}
```

### Complexity:
- **Time:** O(n) - Each subproblem calculated only once
- **Space:** O(n) - HashMap + recursion stack

## Approach 3: Tabulation (Bottom-Up DP)

### The Idea:
Build up the solution from the base cases.

### Code Logic:
```java
public int climbStairs(int n) {
    if (n <= 1) return 1;
    
    int[] dp = new int[n + 1];
    dp[0] = 1;  // Base case
    dp[1] = 1;  // Base case
    
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i-1] + dp[i-2];
    }
    
    return dp[n];
}
```

### Complexity:
- **Time:** O(n) - Single loop
- **Space:** O(n) - DP array

## Approach 4: Space Optimized (Optimal!)

### The Key Insight:
We only need the last two values to compute the next one.

### Code Logic:
```java
public int climbStairs(int n) {
    if (n <= 1) return 1;
    
    int prev2 = 1;  // dp[i-2]
    int prev1 = 1;  // dp[i-1]
    
    for (int i = 2; i <= n; i++) {
        int current = prev1 + prev2;
        prev2 = prev1;
        prev1 = current;
    }
    
    return prev1;
}
```

### Complexity:
- **Time:** O(n) - Single loop
- **Space:** O(1) - Only three variables

### When to use:
- **This is the standard interview solution**
- Perfect balance of simplicity and efficiency

## Advanced Approaches

### Mathematical Formula (Fibonacci Closed Form):
Using Binet's formula for Fibonacci numbers:
```java
double phi = (1 + sqrt(5)) / 2;
return (int) round(pow(phi, n+1) / sqrt(5));
```
- **Time:** O(1) - Constant time calculation
- **Space:** O(1)
- **Note:** May have precision issues for large n

### Matrix Exponentiation:
Using matrix multiplication to compute Fibonacci in O(log n):
- **Time:** O(log n)
- **Space:** O(1)
- **Use case:** When n is extremely large

## Step-by-step Example

```
n = 4

Base cases:
dp[0] = 1 (1 way to stay at ground)
dp[1] = 1 (1 way to reach step 1)

Build up:
dp[2] = dp[1] + dp[0] = 1 + 1 = 2
dp[3] = dp[2] + dp[1] = 2 + 1 = 3  
dp[4] = dp[3] + dp[2] = 3 + 2 = 5

Answer: 5 ways
```

## Interview Strategy

### Step-by-step Approach:
1. **Recognize the pattern:** "This looks like I need to build up from smaller cases"
2. **Identify recurrence:** "To reach step n, I can come from n-1 or n-2"
3. **Start with recursive solution:** Show understanding of the problem
4. **Optimize with DP:** "I can memoize to avoid recalculation"
5. **Space optimize:** "I only need the last two values"

### What Interviewers Look For:
- Recognition of the DP pattern
- Understanding of state transition
- Ability to optimize space complexity
- Knowledge of different DP approaches

## Edge Cases

1. **n = 0:** `1` (staying at ground level)
2. **n = 1:** `1` (one step)  
3. **n = 2:** `2` (1+1 or 2)
4. **Large n:** Ensure no integer overflow

## Common Mistakes

1. **Wrong base cases:** Not handling n=0 or n=1 correctly
2. **Off-by-one errors:** Incorrect loop boundaries
3. **Not optimizing space:** Using O(n) space when O(1) is possible
4. **Integer overflow:** For very large n values

## Real-world Applications

1. **Path counting:** Number of ways to reach a destination
2. **Resource allocation:** Ways to distribute discrete resources
3. **Sequence generation:** Fibonacci-like sequences in nature
4. **Algorithm design:** Understanding optimal substructure

## Variations & Follow-ups

**Q: What if you can take 1, 2, or 3 steps?**
A: `dp[i] = dp[i-1] + dp[i-2] + dp[i-3]`

**Q: What if each step has a cost and you want minimum cost?**
A: Change recurrence to: `dp[i] = cost[i] + min(dp[i-1], dp[i-2])`

**Q: What if you want to return the actual path, not just count?**
A: Store the paths along with counts in your DP state.

## Pattern Recognition

This problem teaches:
- **Optimal substructure:** Solution depends on optimal solutions to subproblems
- **Overlapping subproblems:** Same subproblems appear multiple times
- **State transition:** Clear relationship between states
- **Space optimization:** From O(n) to O(1) space

## Note

**For Mid-Level Interviews (2+ years):**
- **Master the space-optimized solution:** This shows DP understanding
- **Recognize it's Fibonacci:** Shows pattern recognition skills
- **Know multiple approaches:** Recursive → Memoization → Tabulation → Optimized
- **Handle edge cases:** n=0, n=1, large n
- **Explain the recurrence relation clearly**

**Interview Red Flags:**
- Not recognizing this as a DP problem
- Unable to optimize from recursive to iterative
- Not optimizing space complexity
- Missing base cases

**Remember:** This is often the first DP problem people learn. It perfectly demonstrates the core DP concepts and is a stepping stone to more complex DP problems!

**Pattern Connection:** Once you master this, you can solve House Robber, Coin Change, and many other classic DP problems using similar thinking! 