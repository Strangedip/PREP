# Unique Paths

## Problem Statement
There is a robot on an m x n grid. The robot is initially located at the top-left corner (i.e., grid[0][0]). The robot tries to move to the bottom-right corner (i.e., grid[m-1][n-1]). The robot can only move either down or right at any point in time. Given the two integers m and n, return the number of possible unique paths that the robot can take to reach the bottom-right corner.

## Example
```
Input: m = 3, n = 7
Output: 28

Input: m = 3, n = 2
Output: 3
Explanation: From top-left corner, there are 3 ways to reach bottom-right corner:
1. Right -> Down -> Down
2. Down -> Right -> Down  
3. Down -> Down -> Right
```

## Approach 1: Dynamic Programming (2D)

### How it works:
1. **dp[i][j] = number of paths** to reach cell (i,j)
2. **Base case:** dp[0][j] = 1 and dp[i][0] = 1 (only one way along edges)
3. **Recurrence:** dp[i][j] = dp[i-1][j] + dp[i][j-1]

### Key Logic:
```java
public int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    
    // Initialize first row and column
    for (int i = 0; i < m; i++) {
        dp[i][0] = 1;
    }
    for (int j = 0; j < n; j++) {
        dp[0][j] = 1;
    }
    
    // Fill the DP table
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[i][j] = dp[i-1][j] + dp[i][j-1];
        }
    }
    
    return dp[m-1][n-1];
}
```

### Time & Space Complexity:
- **Time:** O(m * n) - Fill entire grid
- **Space:** O(m * n) - 2D DP table

## Approach 2: Space-Optimized DP (1D)

### How it works:
1. **Only need current and previous row**
2. **Use 1D array** and update in place
3. **dp[j] represents** paths to current row, column j

### Key Logic:
```java
public int uniquePaths(int m, int n) {
    int[] dp = new int[n];
    Arrays.fill(dp, 1); // Initialize first row
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[j] = dp[j] + dp[j-1];
            // dp[j] = paths from above + paths from left
        }
    }
    
    return dp[n-1];
}
```

### Time & Space Complexity:
- **Time:** O(m * n) - Same iterations
- **Space:** O(n) - Only one row

## Approach 3: Mathematical Formula (Optimal!)

### How it works:
1. **Total moves needed:** (m-1) down + (n-1) right = m+n-2 moves
2. **Choose positions** for down moves: C(m+n-2, m-1)
3. **Combinatorial formula:** (m+n-2)! / ((m-1)! * (n-1)!)

### Key Logic:
```java
public int uniquePaths(int m, int n) {
    // Calculate C(m+n-2, min(m-1, n-1)) to avoid large numbers
    int totalMoves = m + n - 2;
    int choose = Math.min(m - 1, n - 1);
    
    long result = 1;
    
    // Calculate combination using multiplication and division
    for (int i = 0; i < choose; i++) {
        result = result * (totalMoves - i) / (i + 1);
    }
    
    return (int) result;
}
```

### Time & Space Complexity:
- **Time:** O(min(m, n)) - Loop for combination calculation
- **Space:** O(1) - Only using variables

## Step-by-Step DP Example:

### For m=3, n=4:
```
Initial grid (showing coordinates):
(0,0) (0,1) (0,2) (0,3)
(1,0) (1,1) (1,2) (1,3)
(2,0) (2,1) (2,2) (2,3)

DP table (showing number of paths):
  1    1    1    1
  1    2    3    4
  1    3    6   10

Calculation:
dp[1][1] = dp[0][1] + dp[1][0] = 1 + 1 = 2
dp[1][2] = dp[0][2] + dp[1][1] = 1 + 2 = 3
dp[2][2] = dp[1][2] + dp[2][1] = 3 + 3 = 6
```

## Pattern Recognition:

### This is Pascal's Triangle!
- **Each cell** is sum of cell above and cell to left
- **Same pattern** as binomial coefficients
- **Mathematical insight** leads to O(1) space solution

## Edge Cases:
1. **m=1 or n=1** → Only 1 path (straight line)
2. **m=1, n=1** → 1 path (already at destination)
3. **Large values** → Consider integer overflow

## Follow-up Variations:

### Unique Paths II:
- **Grid has obstacles** → Skip blocked cells
- **Same DP logic** but check for obstacles

### Minimum Path Sum:
- **Grid has costs** → Find path with minimum sum
- **Similar DP** but use min instead of sum

## LeetCode Similar Problems:
- [62. Unique Paths](https://leetcode.com/problems/unique-paths/) (this problem)
- [63. Unique Paths II](https://leetcode.com/problems/unique-paths-ii/)
- [64. Minimum Path Sum](https://leetcode.com/problems/minimum-path-sum/)
- [120. Triangle](https://leetcode.com/problems/triangle/)
- [931. Minimum Falling Path Sum](https://leetcode.com/problems/minimum-falling-path-sum/)

## Interview Tips:
- Start with 2D DP to show understanding
- Optimize to 1D for space efficiency
- Mention mathematical solution for bonus points
- Handle edge cases like 1x1 grid
- This demonstrates classic DP optimization patterns 