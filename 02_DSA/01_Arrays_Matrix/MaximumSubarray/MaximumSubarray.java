import java.util.*;

/**
 * Problem: Maximum Subarray (Kadane's Algorithm)
 * 
 * Given an integer array nums, find the contiguous subarray (containing at least one number)
 * which has the largest sum and return its sum.
 * 
 * Example:
 * Input: nums = [-2,1,-3,4,-1,2,1,-5,4]
 * Output: 6
 * Explanation: [4,-1,2,1] has the largest sum = 6.
 */
public class MaximumSubarray {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n³)
     * Space Complexity: O(1)
     * 
     * Check every possible subarray and find the maximum sum.
     */
    public int maxSubArrayBruteForce(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        
        // Check every possible subarray
        for (int i = 0; i < nums.length; i++) {
            for (int j = i; j < nums.length; j++) {
                int currentSum = 0;
                
                // Calculate sum of subarray from i to j
                for (int k = i; k <= j; k++) {
                    currentSum += nums[k];
                }
                
                maxSum = Math.max(maxSum, currentSum);
            }
        }
        
        return maxSum;
    }
    
    /**
     * APPROACH 2: OPTIMIZED BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * Avoid recalculating sums by extending the current subarray.
     */
    public int maxSubArrayOptimizedBruteForce(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        
        for (int i = 0; i < nums.length; i++) {
            int currentSum = 0;
            
            // Extend subarray from index i
            for (int j = i; j < nums.length; j++) {
                currentSum += nums[j];
                maxSum = Math.max(maxSum, currentSum);
            }
        }
        
        return maxSum;
    }
    
    /**
     * APPROACH 3: KADANE'S ALGORITHM (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * The key insight: at each position, decide whether to extend
     * the existing subarray or start a new one.
     */
    public int maxSubArrayKadane(int[] nums) {
        int maxSum = nums[0];      // Maximum sum found so far
        int currentSum = nums[0];  // Maximum sum ending at current position
        
        for (int i = 1; i < nums.length; i++) {
            // Key decision: extend current subarray or start new one?
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            
            // Update global maximum
            maxSum = Math.max(maxSum, currentSum);
        }
        
        return maxSum;
    }
    
    /**
     * APPROACH 4: KADANE'S WITH SUBARRAY INDICES
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Modified Kadane's algorithm that also tracks the indices
     * of the maximum subarray.
     */
    public int[] maxSubArrayWithIndices(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];
        int start = 0, end = 0, tempStart = 0;
        
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > currentSum + nums[i]) {
                // Start new subarray
                currentSum = nums[i];
                tempStart = i;
            } else {
                // Extend current subarray
                currentSum = currentSum + nums[i];
            }
            
            if (currentSum > maxSum) {
                maxSum = currentSum;
                start = tempStart;
                end = i;
            }
        }
        
        return new int[]{maxSum, start, end};
    }
    
    /**
     * APPROACH 5: DIVIDE AND CONQUER
     * Time Complexity: O(n log n)
     * Space Complexity: O(log n) due to recursion stack
     * 
     * Divide the array into two halves and find:
     * 1. Max subarray in left half
     * 2. Max subarray in right half  
     * 3. Max subarray crossing the middle
     */
    public int maxSubArrayDivideConquer(int[] nums) {
        return maxSubArrayHelper(nums, 0, nums.length - 1);
    }
    
    private int maxSubArrayHelper(int[] nums, int left, int right) {
        // Base case
        if (left == right) {
            return nums[left];
        }
        
        int mid = left + (right - left) / 2;
        
        // Find max subarray in left and right halves
        int leftMax = maxSubArrayHelper(nums, left, mid);
        int rightMax = maxSubArrayHelper(nums, mid + 1, right);
        
        // Find max subarray crossing the middle
        int crossMax = maxCrossingSum(nums, left, mid, right);
        
        // Return maximum of the three
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }
    
    private int maxCrossingSum(int[] nums, int left, int mid, int right) {
        // Find max sum for left side ending at mid
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }
        
        // Find max sum for right side starting from mid+1
        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }
        
        return leftSum + rightSum;
    }
    
    /**
     * APPROACH 6: PREFIX SUM APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Another way to think about the problem using prefix sums.
     */
    public int maxSubArrayPrefixSum(int[] nums) {
        int maxSum = Integer.MIN_VALUE;
        int prefixSum = 0;
        int minPrefix = 0;
        
        for (int num : nums) {
            prefixSum += num;
            
            // Current subarray sum = prefixSum - minPrefix
            maxSum = Math.max(maxSum, prefixSum - minPrefix);
            
            // Update minimum prefix sum seen so far
            minPrefix = Math.min(minPrefix, prefixSum);
        }
        
        return maxSum;
    }
    
    /**
     * APPROACH 7: HANDLING EDGE CASE - ALL NEGATIVE NUMBERS
     * When all numbers are negative, return the least negative number.
     */
    public int maxSubArrayHandleNegative(int[] nums) {
        // Check if all numbers are negative
        boolean allNegative = true;
        int maxElement = Integer.MIN_VALUE;
        
        for (int num : nums) {
            if (num >= 0) {
                allNegative = false;
                break;
            }
            maxElement = Math.max(maxElement, num);
        }
        
        // If all negative, return the maximum element
        if (allNegative) {
            return maxElement;
        }
        
        // Otherwise, use Kadane's algorithm
        return maxSubArrayKadane(nums);
    }
    
    // Test method
    public static void main(String[] args) {
        MaximumSubarray solution = new MaximumSubarray();
        
        // Test case 1: Mixed positive and negative
        int[] nums1 = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
        System.out.println("Test Case 1: nums = [-2,1,-3,4,-1,2,1,-5,4]");
        System.out.println("Brute Force: " + solution.maxSubArrayBruteForce(nums1));
        System.out.println("Optimized Brute Force: " + solution.maxSubArrayOptimizedBruteForce(nums1));
        System.out.println("Kadane's Algorithm: " + solution.maxSubArrayKadane(nums1));
        System.out.println("Divide & Conquer: " + solution.maxSubArrayDivideConquer(nums1));
        System.out.println("Prefix Sum: " + solution.maxSubArrayPrefixSum(nums1));
        
        int[] result = solution.maxSubArrayWithIndices(nums1);
        System.out.println("Max sum: " + result[0] + ", Subarray: [" + result[1] + ", " + result[2] + "]");
        System.out.println();
        
        // Test case 2: All positive
        int[] nums2 = {1, 2, 3, 4, 5};
        System.out.println("Test Case 2: nums = [1,2,3,4,5]");
        System.out.println("Kadane's Algorithm: " + solution.maxSubArrayKadane(nums2));
        System.out.println();
        
        // Test case 3: All negative
        int[] nums3 = {-5, -2, -8, -1};
        System.out.println("Test Case 3: nums = [-5,-2,-8,-1]");
        System.out.println("Kadane's Algorithm: " + solution.maxSubArrayKadane(nums3));
        System.out.println("Handle Negative: " + solution.maxSubArrayHandleNegative(nums3));
        System.out.println();
        
        // Test case 4: Single element
        int[] nums4 = {-1};
        System.out.println("Test Case 4: nums = [-1]");
        System.out.println("Kadane's Algorithm: " + solution.maxSubArrayKadane(nums4));
        System.out.println();
        
        // Test case 5: Two elements
        int[] nums5 = {-1, 2};
        System.out.println("Test Case 5: nums = [-1,2]");
        System.out.println("Kadane's Algorithm: " + solution.maxSubArrayKadane(nums5));
    }
} 