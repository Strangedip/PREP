import java.util.*;

/**
 * Problem: Two Sum
 * 
 * Given an array of integers nums and an integer target, 
 * return indices of the two numbers such that they add up to target.
 * 
 * You may assume that each input would have exactly one solution, 
 * and you may not use the same element twice.
 * 
 * Example:
 * Input: nums = [2,7,11,15], target = 9
 * Output: [0,1]
 * Explanation: Because nums[0] + nums[1] == 9, we return [0, 1].
 */
public class TwoSum {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * This is the most straightforward approach where we check every pair
     * of numbers to see if they sum to the target.
     */
    public int[] twoSumBruteForce(int[] nums, int target) {
        // Check every possible pair
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                // If we find a pair that sums to target, return their indices
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        // No solution found (should not happen according to problem statement)
        return new int[]{};
    }
    
    /**
     * APPROACH 2: OPTIMIZED USING HASHMAP
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * We use a HashMap to store numbers we've seen and their indices.
     * For each number, we check if its complement (target - current number) 
     * exists in our map.
     */
    public int[] twoSumOptimized(int[] nums, int target) {
        // HashMap to store number -> index mapping
        Map<Integer, Integer> map = new HashMap<>();
        
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            
            // If complement exists in map, we found our answer
            if (map.containsKey(complement)) {
                return new int[]{map.get(complement), i};
            }
            
            // Store current number and its index
            map.put(nums[i], i);
        }
        
        // No solution found (should not happen according to problem statement)
        return new int[]{};
    }
    
    /**
     * APPROACH 3: TWO POINTERS (Only works if we can modify the array or return values instead of indices)
     * Time Complexity: O(n log n) due to sorting
     * Space Complexity: O(1) if we don't count the space used by sorting
     * 
     * Note: This approach works only if we need to return the actual values,
     * not indices, because sorting changes the original indices.
     */
    public int[] twoSumTwoPointers(int[] nums, int target) {
        // Create array of [value, original_index] pairs
        int[][] numsWithIndex = new int[nums.length][2];
        for (int i = 0; i < nums.length; i++) {
            numsWithIndex[i][0] = nums[i];
            numsWithIndex[i][1] = i;
        }
        
        // Sort by values
        Arrays.sort(numsWithIndex, (a, b) -> a[0] - b[0]);
        
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int sum = numsWithIndex[left][0] + numsWithIndex[right][0];
            
            if (sum == target) {
                // Return original indices
                return new int[]{numsWithIndex[left][1], numsWithIndex[right][1]};
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        
        return new int[]{};
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        TwoSum solution = new TwoSum();
        
        // Test case 1
        int[] nums1 = {2, 7, 11, 15};
        int target1 = 9;
        
        System.out.println("Test Case 1: nums = [2,7,11,15], target = 9");
        System.out.println("Brute Force: " + Arrays.toString(solution.twoSumBruteForce(nums1, target1)));
        System.out.println("Optimized: " + Arrays.toString(solution.twoSumOptimized(nums1, target1)));
        System.out.println("Two Pointers: " + Arrays.toString(solution.twoSumTwoPointers(nums1, target1)));
        System.out.println();
        
        // Test case 2
        int[] nums2 = {3, 2, 4};
        int target2 = 6;
        
        System.out.println("Test Case 2: nums = [3,2,4], target = 6");
        System.out.println("Brute Force: " + Arrays.toString(solution.twoSumBruteForce(nums2, target2)));
        System.out.println("Optimized: " + Arrays.toString(solution.twoSumOptimized(nums2, target2)));
        System.out.println("Two Pointers: " + Arrays.toString(solution.twoSumTwoPointers(nums2, target2)));
        System.out.println();
        
        // Test case 3
        int[] nums3 = {3, 3};
        int target3 = 6;
        
        System.out.println("Test Case 3: nums = [3,3], target = 6");
        System.out.println("Brute Force: " + Arrays.toString(solution.twoSumBruteForce(nums3, target3)));
        System.out.println("Optimized: " + Arrays.toString(solution.twoSumOptimized(nums3, target3)));
        System.out.println("Two Pointers: " + Arrays.toString(solution.twoSumTwoPointers(nums3, target3)));
    }
} 