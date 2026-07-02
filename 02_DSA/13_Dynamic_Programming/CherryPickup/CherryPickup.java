import java.util.*;

/**
 * LeetCode 741: Cherry Pickup
 *
 * Given an n x n grid with cherries (1), empty cells (0), and obstacles (-1),
 * two walkers start at (0,0) and must reach (n-1,n-1) moving only right or down.
 * Maximize total cherries collected (each cell counted at most once unless both
 * walkers arrive at the same cell on the same step).
 *
 * Optimal: 3D DP — O(n³) time, O(n³) space.
 */
public class CherryPickup {

    /**
     * Approach 1: Bottom-Up 3D DP.
     *
     * State: dp[r1][c1][r2] = max cherries when walker 1 is at (r1,c1)
     *         and walker 2 is at (r2, c2) where c2 = r1 + c1 - r2.
     */
    public int cherryPickup(int[][] grid) {
        int n = grid.length;
        int[][][] dp = new int[n][n][n];

        // Initialize unreachable states
        for (int[][] plane : dp) {
            for (int[] row : plane) {
                Arrays.fill(row, Integer.MIN_VALUE);
            }
        }

        // Both walkers start at (0, 0)
        dp[0][0][0] = grid[0][0];

        for (int r1 = 0; r1 < n; r1++) {
            for (int c1 = 0; c1 < n; c1++) {
                if (grid[r1][c1] == -1) {
                    continue;
                }
                for (int r2 = 0; r2 < n; r2++) {
                    int c2 = r1 + c1 - r2;
                    if (c2 < 0 || c2 >= n || grid[r2][c2] == -1) {
                        continue;
                    }

                    // Cherries at current positions (deduplicate shared cell)
                    int cherries = grid[r1][c1];
                    if (r1 != r2 || c1 != c2) {
                        cherries += grid[r2][c2];
                    }

                    // Best of 4 predecessor move combinations
                    int bestPrev = Integer.MIN_VALUE;
                    if (r1 > 0 && r2 > 0) {
                        bestPrev = Math.max(bestPrev, dp[r1 - 1][c1][r2 - 1]);
                    }
                    if (r1 > 0 && c2 > 0) {
                        bestPrev = Math.max(bestPrev, dp[r1 - 1][c1][r2]);
                    }
                    if (c1 > 0 && r2 > 0) {
                        bestPrev = Math.max(bestPrev, dp[r1][c1 - 1][r2 - 1]);
                    }
                    if (c1 > 0 && c2 > 0) {
                        bestPrev = Math.max(bestPrev, dp[r1][c1 - 1][r2]);
                    }

                    if (bestPrev != Integer.MIN_VALUE) {
                        dp[r1][c1][r2] = bestPrev + cherries;
                    }
                }
            }
        }

        int result = dp[n - 1][n - 1][n - 1];
        return Math.max(0, result);
    }

    /**
     * Approach 2: Top-Down Memoization DFS.
     * State (r1, c1, r2, c2) with explicit c2 for clarity.
     */
    public int cherryPickupMemo(int[][] grid) {
        int n = grid.length;
        int[][][][] memo = new int[n][n][n][n];
        for (int[][][] cube : memo) {
            for (int[][] plane : cube) {
                for (int[] row : plane) {
                    Arrays.fill(row, Integer.MIN_VALUE);
                }
            }
        }
        int result = dfs(0, 0, 0, 0, grid, memo);
        return Math.max(0, result);
    }

    private int dfs(int r1, int c1, int r2, int c2, int[][] grid, int[][][][] memo) {
        int n = grid.length;

        // Out of bounds or obstacle
        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n) {
            return Integer.MIN_VALUE;
        }
        if (grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return Integer.MIN_VALUE;
        }
        if (memo[r1][c1][r2][c2] != Integer.MIN_VALUE) {
            return memo[r1][c1][r2][c2];
        }

        // Cherries at current positions
        int cherries = grid[r1][c1];
        if (r1 != r2 || c1 != c2) {
            cherries += grid[r2][c2];
        }

        // Base case: reached destination
        if (r1 == n - 1 && c1 == n - 1) {
            return memo[r1][c1][r2][c2] = cherries;
        }

        // Try all 4 move combinations for the two walkers
        int best = Integer.MIN_VALUE;
        best = Math.max(best, dfs(r1 + 1, c1, r2 + 1, c2, grid, memo)); // down, down
        best = Math.max(best, dfs(r1 + 1, c1, r2, c2 + 1, grid, memo)); // down, right
        best = Math.max(best, dfs(r1, c1 + 1, r2 + 1, c2, grid, memo)); // right, down
        best = Math.max(best, dfs(r1, c1 + 1, r2, c2 + 1, grid, memo)); // right, right

        if (best == Integer.MIN_VALUE) {
            return memo[r1][c1][r2][c2] = Integer.MIN_VALUE;
        }
        return memo[r1][c1][r2][c2] = best + cherries;
    }

    /**
     * Approach 3: Single Walker Baseline (Unique Paths with weights).
     * O(n²) — does not solve the two-walker variant but useful as comparison.
     */
    public int cherryPickupSingleWalker(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == -1 || grid[n - 1][n - 1] == -1) {
            return 0;
        }

        int[][] dp = new int[n][n];
        dp[0][0] = grid[0][0];

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (grid[r][c] == -1) {
                    dp[r][c] = Integer.MIN_VALUE;
                    continue;
                }
                if (r == 0 && c == 0) {
                    continue;
                }
                int fromAbove = (r > 0) ? dp[r - 1][c] : Integer.MIN_VALUE;
                int fromLeft = (c > 0) ? dp[r][c - 1] : Integer.MIN_VALUE;
                int best = Math.max(fromAbove, fromLeft);
                if (best != Integer.MIN_VALUE) {
                    dp[r][c] = best + grid[r][c];
                }
            }
        }

        return Math.max(0, dp[n - 1][n - 1]);
    }

    public static void main(String[] args) {
        CherryPickup solution = new CherryPickup();

        // Test case 1: Classic 3x3 grid
        int[][] grid1 = {
            {0, 1, -1},
            {1, 0, -1},
            {1, 1,  1}
        };
        System.out.println("Test 1: 3x3 grid with obstacle in top-right");
        System.out.println("  3D DP:          " + solution.cherryPickup(grid1));           // 5
        System.out.println("  Memoization:    " + solution.cherryPickupMemo(grid1));      // 5
        System.out.println("  Single Walker:  " + solution.cherryPickupSingleWalker(grid1)); // 4
        System.out.println();

        // Test case 2: No valid path
        int[][] grid2 = {
            {1,  1, -1},
            {1, -1,  1},
            {-1, 1,  1}
        };
        System.out.println("Test 2: Obstacles block all paths");
        System.out.println("  3D DP:          " + solution.cherryPickup(grid2));           // 0
        System.out.println();

        // Test case 3: 2x2 all cherries
        int[][] grid3 = {
            {1, 1},
            {1, 1}
        };
        System.out.println("Test 3: 2x2 all cherries");
        System.out.println("  3D DP:          " + solution.cherryPickup(grid3));           // 4
        System.out.println("  Memoization:    " + solution.cherryPickupMemo(grid3));      // 4
        System.out.println();

        // Test case 4: 1x1 grid
        int[][] grid4 = {{5}};
        System.out.println("Test 4: 1x1 grid");
        System.out.println("  3D DP:          " + solution.cherryPickup(grid4));           // 5
        System.out.println();

        // Test case 5: Path exists but no cherries
        int[][] grid5 = {
            {0, 0},
            {0, 0}
        };
        System.out.println("Test 5: No cherries");
        System.out.println("  3D DP:          " + solution.cherryPickup(grid5));           // 0
    }
}
