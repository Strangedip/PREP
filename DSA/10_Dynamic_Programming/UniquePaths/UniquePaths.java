import java.util.*;

/**
 * Unique Paths Problem
 * 
 * There is a robot on an m x n grid. The robot is initially located at the top-left 
 * corner (i.e., grid[0][0]). The robot tries to move to the bottom-right corner 
 * (i.e., grid[m - 1][n - 1]). The robot can only move either down or right at any point in time.
 * 
 * Given the two integers m and n, return the number of possible unique paths that 
 * the robot can take to reach the bottom-right corner.
 * 
 * Example:
 * Input: m = 3, n = 7
 * Output: 28
 */
public class UniquePaths {
    
    /**
     * APPROACH 1: 2D DYNAMIC PROGRAMMING
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     * 
     * Classic 2D DP approach using grid to store number of paths.
     */
    public int uniquePaths2D(int m, int n) {
        if (m <= 0 || n <= 0) {
            return 0;
        }
        
        int[][] dp = new int[m][n];
        
        // Initialize first row and first column
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1; // Only one way to reach any cell in first column
        }
        for (int j = 0; j < n; j++) {
            dp[0][j] = 1; // Only one way to reach any cell in first row
        }
        
        // Fill the DP table
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i-1][j] + dp[i][j-1];
            }
        }
        
        return dp[m-1][n-1];
    }
    
    /**
     * APPROACH 2: SPACE OPTIMIZED DP (1D ARRAY)
     * Time Complexity: O(m * n)
     * Space Complexity: O(n)
     * 
     * Uses only 1D array since we only need previous row to compute current row.
     */
    public int uniquePathsOptimized(int m, int n) {
        if (m <= 0 || n <= 0) {
            return 0;
        }
        
        int[] dp = new int[n];
        Arrays.fill(dp, 1); // Initialize first row
        
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] = dp[j] + dp[j-1]; // dp[j] from above + dp[j-1] from left
            }
        }
        
        return dp[n-1];
    }
    
    /**
     * APPROACH 3: MATHEMATICAL SOLUTION (COMBINATORICS)
     * Time Complexity: O(min(m, n))
     * Space Complexity: O(1)
     * 
     * Uses combinatorial formula: C(m+n-2, m-1) = (m+n-2)! / ((m-1)! * (n-1)!)
     */
    public int uniquePathsMath(int m, int n) {
        if (m <= 0 || n <= 0) {
            return 0;
        }
        
        // Total moves needed: (m-1) down + (n-1) right = m+n-2
        // Choose positions for (m-1) down moves: C(m+n-2, m-1)
        
        int totalMoves = m + n - 2;
        int downMoves = m - 1;
        
        // Optimize by choosing smaller of the two
        if (downMoves > totalMoves - downMoves) {
            downMoves = totalMoves - downMoves;
        }
        
        long result = 1;
        for (int i = 0; i < downMoves; i++) {
            result = result * (totalMoves - i) / (i + 1);
        }
        
        return (int) result;
    }
    
    /**
     * APPROACH 4: RECURSIVE WITH MEMOIZATION
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n) for memoization + O(m + n) for recursion stack
     * 
     * Top-down approach with memoization.
     */
    public int uniquePathsMemo(int m, int n) {
        if (m <= 0 || n <= 0) {
            return 0;
        }
        
        Integer[][] memo = new Integer[m][n];
        return uniquePathsHelper(m - 1, n - 1, memo);
    }
    
    private int uniquePathsHelper(int i, int j, Integer[][] memo) {
        // Base cases
        if (i == 0 || j == 0) {
            return 1;
        }
        
        if (memo[i][j] != null) {
            return memo[i][j];
        }
        
        memo[i][j] = uniquePathsHelper(i - 1, j, memo) + uniquePathsHelper(i, j - 1, memo);
        return memo[i][j];
    }
    
    /**
     * APPROACH 5: UNIQUE PATHS WITH OBSTACLES
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     * 
     * Extension: Grid with obstacles (1 represents obstacle, 0 represents empty space).
     */
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        if (obstacleGrid == null || obstacleGrid.length == 0 || 
            obstacleGrid[0].length == 0 || obstacleGrid[0][0] == 1) {
            return 0;
        }
        
        int m = obstacleGrid.length;
        int n = obstacleGrid[0].length;
        int[][] dp = new int[m][n];
        
        // Initialize starting point
        dp[0][0] = 1;
        
        // Fill first column
        for (int i = 1; i < m; i++) {
            dp[i][0] = (obstacleGrid[i][0] == 1) ? 0 : dp[i-1][0];
        }
        
        // Fill first row
        for (int j = 1; j < n; j++) {
            dp[0][j] = (obstacleGrid[0][j] == 1) ? 0 : dp[0][j-1];
        }
        
        // Fill the rest of the grid
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    dp[i][j] = 0; // Obstacle
                } else {
                    dp[i][j] = dp[i-1][j] + dp[i][j-1];
                }
            }
        }
        
        return dp[m-1][n-1];
    }
    
    /**
     * APPROACH 6: UNIQUE PATHS III (VISITING ALL EMPTY SQUARES)
     * Time Complexity: O(4^(m*n))
     * Space Complexity: O(m*n)
     * 
     * Extension: Must visit every empty square exactly once.
     */
    public int uniquePathsIII(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        
        int m = grid.length, n = grid[0].length;
        int startX = -1, startY = -1, emptyCount = 0;
        
        // Find start position and count empty squares
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    startX = i;
                    startY = j;
                    emptyCount++;
                } else if (grid[i][j] == 0) {
                    emptyCount++;
                }
            }
        }
        
        return dfsUniquePathsIII(grid, startX, startY, emptyCount);
    }
    
    private int dfsUniquePathsIII(int[][] grid, int x, int y, int remaining) {
        // Out of bounds or obstacle
        if (x < 0 || x >= grid.length || y < 0 || y >= grid[0].length || grid[x][y] == -1) {
            return 0;
        }
        
        // Reached end
        if (grid[x][y] == 2) {
            return remaining == 1 ? 1 : 0; // Must have visited all empty squares
        }
        
        // Mark as visited
        int originalValue = grid[x][y];
        grid[x][y] = -1;
        
        int paths = 0;
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        
        for (int[] dir : directions) {
            paths += dfsUniquePathsIII(grid, x + dir[0], y + dir[1], remaining - 1);
        }
        
        // Backtrack
        grid[x][y] = originalValue;
        
        return paths;
    }
    
    /**
     * APPROACH 7: MINIMUM PATH SUM VARIANT
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     * 
     * Related problem: Find minimum sum path from top-left to bottom-right.
     */
    public int minPathSum(int[][] grid) {
        if (grid == null || grid.length == 0) {
            return 0;
        }
        
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        
        dp[0][0] = grid[0][0];
        
        // Fill first row
        for (int j = 1; j < n; j++) {
            dp[0][j] = dp[0][j-1] + grid[0][j];
        }
        
        // Fill first column
        for (int i = 1; i < m; i++) {
            dp[i][0] = dp[i-1][0] + grid[i][0];
        }
        
        // Fill the rest
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(dp[i-1][j], dp[i][j-1]) + grid[i][j];
            }
        }
        
        return dp[m-1][n-1];
    }
    
    /**
     * APPROACH 8: UNIQUE PATHS WITH VARIABLE MOVES
     * Time Complexity: O(m * n * k) where k is max moves per step
     * Space Complexity: O(m * n)
     * 
     * Extension: Robot can move 1-k steps right or down in one move.
     */
    public int uniquePathsVariableMoves(int m, int n, int maxMoves) {
        int[][] dp = new int[m][n];
        dp[0][0] = 1;
        
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0) continue;
                
                // Check all possible previous positions
                for (int move = 1; move <= maxMoves; move++) {
                    // From above
                    if (i - move >= 0) {
                        dp[i][j] += dp[i - move][j];
                    }
                    // From left
                    if (j - move >= 0) {
                        dp[i][j] += dp[i][j - move];
                    }
                }
            }
        }
        
        return dp[m-1][n-1];
    }
    
    /**
     * Utility method to print grid
     */
    private void printGrid(int[][] grid, String label) {
        System.out.println(label + ":");
        for (int[] row : grid) {
            for (int cell : row) {
                System.out.printf("%3d ", cell);
            }
            System.out.println();
        }
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        UniquePaths solution = new UniquePaths();
        
        // Test Case 1: Standard unique paths
        System.out.println("=== Test Case 1: Standard Unique Paths ===");
        int m1 = 3, n1 = 7;
        System.out.println("Grid size: " + m1 + "x" + n1);
        System.out.println("2D DP: " + solution.uniquePaths2D(m1, n1));
        System.out.println("Optimized DP: " + solution.uniquePathsOptimized(m1, n1));
        System.out.println("Mathematical: " + solution.uniquePathsMath(m1, n1));
        System.out.println("Memoization: " + solution.uniquePathsMemo(m1, n1));
        System.out.println();
        
        // Test Case 2: Small grid
        System.out.println("=== Test Case 2: Small Grid ===");
        int m2 = 3, n2 = 3;
        System.out.println("Grid size: " + m2 + "x" + n2);
        System.out.println("Unique paths: " + solution.uniquePathsOptimized(m2, n2));
        System.out.println();
        
        // Test Case 3: Unique paths with obstacles
        System.out.println("=== Test Case 3: With Obstacles ===");
        int[][] obstacleGrid = {
            {0, 0, 0},
            {0, 1, 0},
            {0, 0, 0}
        };
        solution.printGrid(obstacleGrid, "Obstacle Grid (1 = obstacle)");
        System.out.println("Paths with obstacles: " + 
                          solution.uniquePathsWithObstacles(obstacleGrid));
        System.out.println();
        
        // Test Case 4: Minimum path sum
        System.out.println("=== Test Case 4: Minimum Path Sum ===");
        int[][] pathSumGrid = {
            {1, 3, 1},
            {1, 5, 1},
            {4, 2, 1}
        };
        solution.printGrid(pathSumGrid, "Path Sum Grid");
        System.out.println("Minimum path sum: " + solution.minPathSum(pathSumGrid));
        System.out.println();
        
        // Test Case 5: Unique Paths III
        System.out.println("=== Test Case 5: Unique Paths III ===");
        int[][] grid3 = {
            {1, 0, 0, 0},
            {0, 0, 0, 0},
            {0, 0, 2, -1}
        };
        solution.printGrid(grid3, "Grid III (1=start, 2=end, -1=obstacle)");
        System.out.println("Paths visiting all squares: " + 
                          solution.uniquePathsIII(grid3));
        System.out.println();
        
        // Test Case 6: Variable moves
        System.out.println("=== Test Case 6: Variable Moves ===");
        int m6 = 3, n6 = 3, maxMoves = 2;
        System.out.println("Grid: " + m6 + "x" + n6 + ", Max moves per step: " + maxMoves);
        System.out.println("Unique paths: " + 
                          solution.uniquePathsVariableMoves(m6, n6, maxMoves));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(UniquePaths solution) {
        System.out.println("=== Performance Test ===");
        
        int[][] testCases = {{10, 10}, {15, 15}, {20, 20}};
        
        for (int[] testCase : testCases) {
            int m = testCase[0], n = testCase[1];
            System.out.println("Grid size: " + m + "x" + n);
            
            long startTime, endTime;
            
            // 2D DP
            startTime = System.nanoTime();
            int result1 = solution.uniquePaths2D(m, n);
            endTime = System.nanoTime();
            System.out.println("2D DP: " + result1 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Optimized DP
            startTime = System.nanoTime();
            int result2 = solution.uniquePathsOptimized(m, n);
            endTime = System.nanoTime();
            System.out.println("Optimized DP: " + result2 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Mathematical
            startTime = System.nanoTime();
            int result3 = solution.uniquePathsMath(m, n);
            endTime = System.nanoTime();
            System.out.println("Mathematical: " + result3 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Memoization
            startTime = System.nanoTime();
            int result4 = solution.uniquePathsMemo(m, n);
            endTime = System.nanoTime();
            System.out.println("Memoization: " + result4 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            System.out.println("All methods match: " + 
                             (result1 == result2 && result2 == result3 && result3 == result4));
            System.out.println();
        }
    }
} 