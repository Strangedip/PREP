import java.util.*;

/**
 * LeetCode 1425: Constrained Subsequence Sum
 *
 * Return the maximum sum of a non-empty subsequence where consecutive picked
 * indices differ by at most k.
 *
 * Optimal: DP + monotonic deque — O(n) time, O(n) space.
 */
public class ConstrainedSubsequenceSum {

    /**
     * Approach 1: DP + Monotonic Deque (optimal).
     *
     * dp[i] = max sum of a valid subsequence ending at index i.
     * dp[i] = max(nums[i], nums[i] + max{dp[j] : i-k <= j < i})
     *
     * The deque maintains indices with decreasing dp values so the front
     * always holds the index of the maximum dp in the sliding window.
     */
    public int constrainedSubsetSum(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        Deque<Integer> deque = new ArrayDeque<>();
        int answer = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            // Start a new subsequence at i, or extend the best one in window
            dp[i] = nums[i];
            if (!deque.isEmpty()) {
                dp[i] = Math.max(dp[i], dp[deque.peekFirst()] + nums[i]);
            }
            answer = Math.max(answer, dp[i]);

            // Maintain decreasing dp order: remove smaller values from back
            while (!deque.isEmpty() && dp[deque.peekLast()] <= dp[i]) {
                deque.pollLast();
            }
            deque.offerLast(i);

            // Expire indices outside the window [i-k, i-1]
            if (!deque.isEmpty() && deque.peekFirst() <= i - k) {
                deque.pollFirst();
            }
        }

        return answer;
    }

    /**
     * Approach 2: Naive O(n*k) DP.
     * Directly scans the window for each i. Correct but slower for large k.
     */
    public int constrainedSubsetSumNaive(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        int answer = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            dp[i] = nums[i];
            int windowStart = Math.max(0, i - k);
            for (int j = windowStart; j < i; j++) {
                dp[i] = Math.max(dp[i], dp[j] + nums[i]);
            }
            answer = Math.max(answer, dp[i]);
        }

        return answer;
    }

    /**
     * Approach 3: Multiset (TreeMap) for window maximum.
     * O(n log n) — generalizes when window queries are more complex.
     */
    public int constrainedSubsetSumMultiset(int[] nums, int k) {
        int n = nums.length;
        int[] dp = new int[n];
        // TreeMap: dp value -> count of indices with that dp value
        TreeMap<Integer, Integer> windowMax = new TreeMap<>();
        int answer = Integer.MIN_VALUE;

        for (int i = 0; i < n; i++) {
            dp[i] = nums[i];
            if (!windowMax.isEmpty()) {
                int best = windowMax.lastKey();
                dp[i] = Math.max(dp[i], best + nums[i]);
            }
            answer = Math.max(answer, dp[i]);

            // Add dp[i] to window
            windowMax.merge(dp[i], 1, Integer::sum);

            // Remove dp[i-k] from window when it expires
            if (i >= k) {
                int expired = dp[i - k];
                int count = windowMax.get(expired);
                if (count == 1) {
                    windowMax.remove(expired);
                } else {
                    windowMax.put(expired, count - 1);
                }
            }
        }

        return answer;
    }

    public static void main(String[] args) {
        ConstrainedSubsequenceSum solution = new ConstrainedSubsequenceSum();

        // Test case 1: Mixed positive and negative
        int[] nums1 = {10, 2, -10, 5, 20};
        int k1 = 2;
        System.out.println("Test 1: nums=" + Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("  Deque:      " + solution.constrainedSubsetSum(nums1, k1));           // 37
        System.out.println("  Naive:      " + solution.constrainedSubsetSumNaive(nums1, k1));     // 37
        System.out.println("  Multiset:   " + solution.constrainedSubsetSumMultiset(nums1, k1));  // 37
        System.out.println();

        // Test case 2: All negative
        int[] nums2 = {-1, -2, -3};
        int k2 = 1;
        System.out.println("Test 2: nums=" + Arrays.toString(nums2) + ", k=" + k2);
        System.out.println("  Deque:      " + solution.constrainedSubsetSum(nums2, k2));           // -1
        System.out.println();

        // Test case 3: Classic example
        int[] nums3 = {10, -2, -10, -5, 20};
        int k3 = 2;
        System.out.println("Test 3: nums=" + Arrays.toString(nums3) + ", k=" + k3);
        System.out.println("  Deque:      " + solution.constrainedSubsetSum(nums3, k3));           // 23
        System.out.println("  Naive:      " + solution.constrainedSubsetSumNaive(nums3, k3));     // 23
        System.out.println();

        // Test case 4: Single element
        int[] nums4 = {42};
        int k4 = 1;
        System.out.println("Test 4: nums=" + Arrays.toString(nums4) + ", k=" + k4);
        System.out.println("  Deque:      " + solution.constrainedSubsetSum(nums4, k4));           // 42
        System.out.println();

        // Test case 5: k equals array length (no gap restriction)
        int[] nums5 = {1, -1, 2, -2, 3};
        int k5 = 5;
        System.out.println("Test 5: nums=" + Arrays.toString(nums5) + ", k=" + k5);
        System.out.println("  Deque:      " + solution.constrainedSubsetSum(nums5, k5));           // 6
        System.out.println();

        // Test case 6: k = 1 (adjacent only)
        int[] nums6 = {5, 3, -1, 4};
        int k6 = 1;
        System.out.println("Test 6: nums=" + Arrays.toString(nums6) + ", k=" + k6);
        System.out.println("  Deque:      " + solution.constrainedSubsetSum(nums6, k6));           // 11
    }
}
