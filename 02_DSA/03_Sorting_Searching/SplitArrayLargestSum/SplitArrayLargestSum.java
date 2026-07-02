/**
 * LeetCode 410: Split Array Largest Sum
 *
 * Split nums into k non-empty contiguous subarrays. Minimize the largest subarray sum.
 *
 * Time Complexity:
 *   - splitArray:       O(n log S) where S = sum(nums)
 *   - splitArrayLinear: O(n · S)
 * Space Complexity: O(1)
 */
public class SplitArrayLargestSum {

    /**
     * Approach 1: Binary Search on Answer (Optimal)
     *
     * Binary search the minimum feasible maximum subarray sum. For each candidate
     * limit, greedily count how many subarrays are needed without exceeding it.
     */
    public int splitArray(int[] nums, int k) {
        int lo = 0;
        int hi = 0;
        for (int x : nums) {
            lo = Math.max(lo, x);
            hi += x;
        }

        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (canSplit(nums, k, mid)) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    /**
     * Approach 2: Linear Search on maxSum (Baseline)
     *
     * Increase the allowed subarray sum until greedy splitting fits within k parts.
     */
    public int splitArrayLinear(int[] nums, int k) {
        int limit = 0;
        for (int x : nums) {
            limit = Math.max(limit, x);
        }
        while (!canSplit(nums, k, limit)) {
            limit++;
        }
        return limit;
    }

    /**
     * Greedy feasibility: form contiguous subarrays without exceeding maxSum.
     * Returns true if at most k subarrays suffice.
     */
    private boolean canSplit(int[] nums, int k, int maxSum) {
        int parts = 1;
        int currentSum = 0;

        for (int x : nums) {
            if (currentSum + x > maxSum) {
                parts++;
                currentSum = 0;
            }
            currentSum += x;
        }
        return parts <= k;
    }

    public static void main(String[] args) {
        SplitArrayLargestSum solution = new SplitArrayLargestSum();

        // Test case 1: classic example
        int[] nums1 = {7, 2, 5, 10, 8};
        int k1 = 2;
        System.out.println("Test 1: nums=" + java.util.Arrays.toString(nums1) + ", k=" + k1);
        System.out.println("  Binary search: " + solution.splitArray(nums1, k1));       // 18
        System.out.println("  Linear search: " + solution.splitArrayLinear(nums1, k1)); // 18
        System.out.println();

        // Test case 2: more splits
        int[] nums2 = {1, 2, 3, 4, 5};
        int k2 = 2;
        System.out.println("Test 2: nums=" + java.util.Arrays.toString(nums2) + ", k=" + k2);
        System.out.println("  Binary search: " + solution.splitArray(nums2, k2));       // 9
        System.out.println("  Linear search: " + solution.splitArrayLinear(nums2, k2)); // 9
        System.out.println();

        // Test case 3: k equals array length (each element its own subarray)
        int[] nums3 = {10, 20, 30};
        int k3 = 3;
        System.out.println("Test 3: nums=" + java.util.Arrays.toString(nums3) + ", k=" + k3);
        System.out.println("  Binary search: " + solution.splitArray(nums3, k3));       // 30
        System.out.println("  Linear search: " + solution.splitArrayLinear(nums3, k3)); // 30
        System.out.println();

        // Test case 4: k = 1 (single subarray)
        int[] nums4 = {2, 3, 1, 4};
        int k4 = 1;
        System.out.println("Test 4: nums=" + java.util.Arrays.toString(nums4) + ", k=" + k4);
        System.out.println("  Binary search: " + solution.splitArray(nums4, k4));       // 10
        System.out.println("  Linear search: " + solution.splitArrayLinear(nums4, k4)); // 10
    }
}
