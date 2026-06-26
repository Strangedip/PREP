import java.util.Arrays;

/**
 * Advanced DP pattern templates — interval, bitmask, tree, digit DP examples.
 */
public class AdvancedDP {

    /** Interval DP — Burst Balloons style: max coins from bursting all balloons */
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] arr = new int[n + 2];
        arr[0] = 1;
        arr[n + 1] = 1;
        for (int i = 0; i < n; i++) arr[i + 1] = nums[i];

        int[][] dp = new int[n + 2][n + 2];
        for (int len = 1; len <= n; len++) {
            for (int left = 1; left + len - 1 <= n; left++) {
                int right = left + len - 1;
                for (int k = left; k <= right; k++) {
                    dp[left][right] = Math.max(dp[left][right],
                            arr[left - 1] * arr[k] * arr[right + 1]
                                    + dp[left][k - 1] + dp[k + 1][right]);
                }
            }
        }
        return dp[1][n];
    }

    /** Bitmask DP — Traveling Salesman on small n (n <= 15) */
    public int tsp(int[][] dist) {
        int n = dist.length;
        int[][] dp = new int[1 << n][n];
        for (int[] row : dp) Arrays.fill(row, Integer.MAX_VALUE / 2);
        dp[1][0] = 0;

        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) == 0) continue;
                for (int v = 0; v < n; v++) {
                    if ((mask & (1 << v)) != 0) continue;
                    int nextMask = mask | (1 << v);
                    dp[nextMask][v] = Math.min(dp[nextMask][v], dp[mask][u] + dist[u][v]);
                }
            }
        }
        int full = (1 << n) - 1;
        int ans = Integer.MAX_VALUE;
        for (int u = 0; u < n; u++) {
            ans = Math.min(ans, dp[full][u] + dist[u][0]);
        }
        return ans;
    }

    public static void main(String[] args) {
        AdvancedDP sol = new AdvancedDP();
        System.out.println(sol.maxCoins(new int[]{3, 1, 5, 8})); // 167
        int[][] dist = {{0, 10, 15, 20}, {10, 0, 35, 25}, {15, 35, 0, 30}, {20, 25, 30, 0}};
        System.out.println(sol.tsp(dist)); // 80
    }
}
