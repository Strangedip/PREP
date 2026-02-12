/**
 * Maximum Subarray — Divide and Conquer approach.
 * 
 * T(n) = 2T(n/2) + O(n) → O(n log n) by Master Theorem Case 2.
 * Space: O(log n) recursion stack.
 */
public class MaximumSubarrayDivideConquer {

    public int maxSubArray(int[] nums) {
        return divideAndConquer(nums, 0, nums.length - 1);
    }

    private int divideAndConquer(int[] nums, int left, int right) {
        if (left == right) {
            return nums[left];
        }

        int mid = left + (right - left) / 2;

        int leftMax = divideAndConquer(nums, left, mid);
        int rightMax = divideAndConquer(nums, mid + 1, right);
        int crossMax = maxCrossingSum(nums, left, mid, right);

        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }

    private int maxCrossingSum(int[] nums, int left, int mid, int right) {
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }

        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }

        return leftSum + rightSum;
    }

    public static void main(String[] args) {
        MaximumSubarrayDivideConquer solution = new MaximumSubarrayDivideConquer();

        int[] nums1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Max subarray sum: " + solution.maxSubArray(nums1)); // 6

        int[] nums2 = {1};
        System.out.println("Max subarray sum: " + solution.maxSubArray(nums2)); // 1

        int[] nums3 = {5, 4, -1, 7, 8};
        System.out.println("Max subarray sum: " + solution.maxSubArray(nums3)); // 23
    }
}

