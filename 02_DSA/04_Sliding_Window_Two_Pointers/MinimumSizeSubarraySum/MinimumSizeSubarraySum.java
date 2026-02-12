/**
 * 209. Minimum Size Subarray Sum
 * 
 * Find the minimal length of a contiguous subarray whose sum >= target.
 * 
 * Approach: Sliding Window — expand right, contract left while sum >= target.
 * Time: O(n) — each element added and removed at most once.
 * Space: O(1)
 */
public class MinimumSizeSubarraySum {

    public int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int windowSum = 0;
        int minLength = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            windowSum += nums[right];

            while (windowSum >= target) {
                minLength = Math.min(minLength, right - left + 1);
                windowSum -= nums[left];
                left++;
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    public static void main(String[] args) {
        MinimumSizeSubarraySum solution = new MinimumSizeSubarraySum();

        System.out.println(solution.minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3})); // 2
        System.out.println(solution.minSubArrayLen(4, new int[]{1, 4, 4}));           // 1
        System.out.println(solution.minSubArrayLen(11, new int[]{1, 1, 1, 1, 1}));    // 0
    }
}

