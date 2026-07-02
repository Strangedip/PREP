import java.util.*;

/**
 * LeetCode 312: Burst Balloons
 *
 * You are given n balloons, indexed from 0 to n - 1. Each balloon has a number
 * represented by array nums. Bursting balloon i yields nums[i-1] * nums[i] * nums[i+1]
 * coins (out-of-bounds neighbors count as 1).
 *
 * Return the maximum coins you can collect by bursting the balloons wisely.
 *
 * Optimal: Interval DP — O(n³) time, O(n²) space.
 */
public class BurstBalloons {

    /**
     * Approach 1: Bottom-Up Interval DP (recommended for interviews).
     *
     * dp[l][r] = max coins from bursting all balloons in the inclusive range [l, r]
     * of the padded array, assuming boundaries l-1 and r+1 survive until the end.
     *
     * For each candidate k as the LAST balloon burst in [l, r]:
     *   coins = a[l-1] * a[k] * a[r+1] + dp[l][k-1] + dp[k+1][r]
     */
    public int maxCoins(int[] nums) {
        int n = nums.length;
        // Pad with virtual balloons of value 1 at both ends
        int[] padded = new int[n + 2];
        padded[0] = padded[n + 1] = 1;
        System.arraycopy(nums, 0, padded, 1, n);

        // dp[l][r] for 1-indexed padded array positions
        int[][] dp = new int[n + 2][n + 2];

        // Iterate by interval length: solve small ranges before large ones
        for (int len = 1; len <= n; len++) {
            for (int left = 1; left <= n - len + 1; left++) {
                int right = left + len - 1;
                for (int last = left; last <= right; last++) {
                    int burstCoins = padded[left - 1] * padded[last] * padded[right + 1];
                    int leftSub = dp[left][last - 1];   // 0 when last == left
                    int rightSub = dp[last + 1][right]; // 0 when last == right
                    dp[left][right] = Math.max(dp[left][right], burstCoins + leftSub + rightSub);
                }
            }
        }
        return dp[1][n];
    }

    /**
     * Approach 2: Top-Down Memoization.
     * Same recurrence as bottom-up, but computed recursively on demand.
     */
    public int maxCoinsMemo(int[] nums) {
        int n = nums.length;
        int[] padded = new int[n + 2];
        padded[0] = padded[n + 1] = 1;
        System.arraycopy(nums, 0, padded, 1, n);

        int[][] memo = new int[n + 2][n + 2];
        return dfs(1, n, padded, memo);
    }

    /**
     * Returns max coins bursting all balloons in [left, right] (inclusive).
     * Memo stores 0 for uncomputed states; valid results are always >= 0 for this problem.
     */
    private int dfs(int left, int right, int[] padded, int[][] memo) {
        if (left > right) {
            return 0;
        }
        if (memo[left][right] != 0) {
            return memo[left][right];
        }

        int best = 0;
        for (int last = left; last <= right; last++) {
            int coins = padded[left - 1] * padded[last] * padded[right + 1]
                      + dfs(left, last - 1, padded, memo)
                      + dfs(last + 1, right, padded, memo);
            best = Math.max(best, coins);
        }
        memo[left][right] = best;
        return best;
    }

    /**
     * Approach 3: Brute Force — try every burst order recursively.
     * Exponential time; included for educational comparison only.
     */
    public int maxCoinsBruteForce(int[] nums) {
        List<Integer> balloons = new ArrayList<>();
        for (int num : nums) {
            balloons.add(num);
        }
        return bruteForceHelper(balloons);
    }

    private int bruteForceHelper(List<Integer> balloons) {
        if (balloons.isEmpty()) {
            return 0;
        }
        int best = 0;
        for (int i = 0; i < balloons.size(); i++) {
            int left = (i == 0) ? 1 : balloons.get(i - 1);
            int mid = balloons.get(i);
            int right = (i == balloons.size() - 1) ? 1 : balloons.get(i + 1);
            int coins = left * mid * right;

            List<Integer> remaining = new ArrayList<>(balloons);
            remaining.remove(i);
            best = Math.max(best, coins + bruteForceHelper(remaining));
        }
        return best;
    }

    public static void main(String[] args) {
        BurstBalloons solution = new BurstBalloons();

        // Test case 1: Classic example
        int[] nums1 = {3, 1, 5, 8};
        System.out.println("Test 1: nums = " + Arrays.toString(nums1));
        System.out.println("  Interval DP:    " + solution.maxCoins(nums1));       // 167
        System.out.println("  Memoization:    " + solution.maxCoinsMemo(nums1));   // 167
        System.out.println("  Brute Force:    " + solution.maxCoinsBruteForce(nums1)); // 167
        System.out.println();

        // Test case 2: Two balloons
        int[] nums2 = {1, 5};
        System.out.println("Test 2: nums = " + Arrays.toString(nums2));
        System.out.println("  Interval DP:    " + solution.maxCoins(nums2));       // 10
        System.out.println("  Memoization:    " + solution.maxCoinsMemo(nums2));   // 10
        System.out.println();

        // Test case 3: Single balloon
        int[] nums3 = {9};
        System.out.println("Test 3: nums = " + Arrays.toString(nums3));
        System.out.println("  Interval DP:    " + solution.maxCoins(nums3));       // 9
        System.out.println();

        // Test case 4: All ones
        int[] nums4 = {1, 1, 1, 1};
        System.out.println("Test 4: nums = " + Arrays.toString(nums4));
        System.out.println("  Interval DP:    " + solution.maxCoins(nums4));       // 4
        System.out.println();

        // Test case 5: Increasing sequence
        int[] nums5 = {1, 2, 3, 4};
        System.out.println("Test 5: nums = " + Arrays.toString(nums5));
        System.out.println("  Interval DP:    " + solution.maxCoins(nums5));       // 40
        System.out.println("  Memoization:    " + solution.maxCoinsMemo(nums5));   // 40
    }
}
