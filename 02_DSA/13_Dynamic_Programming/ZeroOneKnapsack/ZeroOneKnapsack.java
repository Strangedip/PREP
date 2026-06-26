/**
 * 0/1 Knapsack — maximize value with weight capacity W.
 * Each item can be used at most once.
 */
public class ZeroOneKnapsack {

  /** DP table — O(n*W) time and space */
  public int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];

    for (int i = 1; i <= n; i++) {
      for (int w = 0; w <= capacity; w++) {
        dp[i][w] = dp[i - 1][w];
        if (weights[i - 1] <= w) {
          dp[i][w] = Math.max(dp[i][w],
              values[i - 1] + dp[i - 1][w - weights[i - 1]]);
        }
      }
    }
    return dp[n][capacity];
  }

  /** Space-optimized — O(W) space */
  public int knapsackOptimized(int[] weights, int[] values, int capacity) {
    int[] dp = new int[capacity + 1];
    for (int i = 0; i < weights.length; i++) {
      for (int w = capacity; w >= weights[i]; w--) {
        dp[w] = Math.max(dp[w], values[i] + dp[w - weights[i]]);
      }
    }
    return dp[capacity];
  }

  public static void main(String[] args) {
    ZeroOneKnapsack sol = new ZeroOneKnapsack();
    int[] w = {1, 3, 4, 5};
    int[] v = {1, 4, 5, 7};
  System.out.println(sol.knapsack(w, v, 7)); // 9 (items 1 and 3)
  }
}
