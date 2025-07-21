import java.util.*;

/**
 * Problem: 4Sum
 * 
 * Given an array nums of n integers, return an array of all the unique quadruplets 
 * [nums[a], nums[b], nums[c], nums[d]] such that:
 * - 0 <= a, b, c, d < n
 * - a, b, c, d are all distinct
 * - nums[a] + nums[b] + nums[c] + nums[d] == target
 * 
 * Example:
 * Input: nums = [1,0,-1,0,-2,2], target = 0
 * Output: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]
 */
public class FourSum {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n^4)
     * Space Complexity: O(1)
     */
    public List<List<Integer>> fourSumBruteForce(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Set<List<Integer>> seen = new HashSet<>();
        int n = nums.length;
        
        for (int i = 0; i < n - 3; i++) {
            for (int j = i + 1; j < n - 2; j++) {
                for (int k = j + 1; k < n - 1; k++) {
                    for (int l = k + 1; l < n; l++) {
                        if ((long)nums[i] + nums[j] + nums[k] + nums[l] == target) {
                            List<Integer> quad = Arrays.asList(nums[i], nums[j], nums[k], nums[l]);
                            Collections.sort(quad);
                            seen.add(quad);
                        }
                    }
                }
            }
        }
        
        result.addAll(seen);
        return result;
    }
    
    /**
     * APPROACH 2: OPTIMIZED (SORT + TWO POINTERS)
     * Time Complexity: O(n^3)
     * Space Complexity: O(1)
     */
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 4) return result;
        
        Arrays.sort(nums);
        int n = nums.length;
        
        for (int i = 0; i < n - 3; i++) {
            // Skip duplicates for first element
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            for (int j = i + 1; j < n - 2; j++) {
                // Skip duplicates for second element
                if (j > i + 1 && nums[j] == nums[j - 1]) continue;
                
                int left = j + 1;
                int right = n - 1;
                long twoSum = (long)target - nums[i] - nums[j];
                
                while (left < right) {
                    long currentSum = (long)nums[left] + nums[right];
                    
                    if (currentSum == twoSum) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));
                        
                        // Skip duplicates
                        while (left < right && nums[left] == nums[left + 1]) left++;
                        while (left < right && nums[right] == nums[right - 1]) right--;
                        
                        left++;
                        right--;
                    } else if (currentSum < twoSum) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: HASHMAP APPROACH
     * Time Complexity: O(n^3)
     * Space Complexity: O(n^2)
     */
    public List<List<Integer>> fourSumHashMap(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 4) return result;
        
        Arrays.sort(nums);
        Set<List<Integer>> seen = new HashSet<>();
        
        for (int i = 0; i < nums.length - 3; i++) {
            for (int j = i + 1; j < nums.length - 2; j++) {
                Set<Integer> complement = new HashSet<>();
                
                for (int k = j + 1; k < nums.length; k++) {
                    long sum = (long)target - nums[i] - nums[j] - nums[k];
                    
                    if (complement.contains((int)sum)) {
                        List<Integer> quad = Arrays.asList(nums[i], nums[j], (int)sum, nums[k]);
                        Collections.sort(quad);
                        seen.add(quad);
                    }
                    complement.add(nums[k]);
                }
            }
        }
        
        result.addAll(seen);
        return result;
    }
    
    // Test method
    public static void main(String[] args) {
        FourSum solution = new FourSum();
        
        int[] nums1 = {1, 0, -1, 0, -2, 2};
        int target1 = 0;
        System.out.println("Test 1: " + solution.fourSum(nums1, target1));
        
        int[] nums2 = {2, 2, 2, 2, 2};
        int target2 = 8;
        System.out.println("Test 2: " + solution.fourSum(nums2, target2));
    }
} 