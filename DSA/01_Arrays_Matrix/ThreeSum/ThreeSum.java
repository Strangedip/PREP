import java.util.*;

/**
 * Problem: 3Sum
 * 
 * Given an integer array nums, return all the triplets [nums[i], nums[j], nums[k]]
 * such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0.
 * 
 * Notice that the solution set must not contain duplicate triplets.
 * 
 * Example:
 * Input: nums = [-1,0,1,2,-1,-4]
 * Output: [[-1,-1,2],[-1,0,1]]
 */
public class ThreeSum {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n³)
     * Space Complexity: O(1) for algorithm, O(k) for result where k is number of triplets
     * 
     * Check every possible triplet combination and avoid duplicates using a Set.
     */
    public List<List<Integer>> threeSumBruteForce(int[] nums) {
        Set<List<Integer>> resultSet = new HashSet<>();
        int n = nums.length;
        
        // Check every possible triplet
        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 1; j < n - 1; j++) {
                for (int k = j + 1; k < n; k++) {
                    if (nums[i] + nums[j] + nums[k] == 0) {
                        // Create triplet and sort to handle duplicates
                        List<Integer> triplet = Arrays.asList(nums[i], nums[j], nums[k]);
                        Collections.sort(triplet);
                        resultSet.add(triplet);
                    }
                }
            }
        }
        
        return new ArrayList<>(resultSet);
    }
    
    /**
     * APPROACH 2: SORT + TWO POINTERS (OPTIMIZED)
     * Time Complexity: O(n²)
     * Space Complexity: O(1) for algorithm, O(k) for result
     * 
     * Sort the array first, then for each element, use two pointers
     * to find pairs that sum to the negative of that element.
     */
    public List<List<Integer>> threeSumOptimized(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Sort the array first
        
        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates for the first element
            if (i > 0 && nums[i] == nums[i - 1]) {
                continue;
            }
            
            int left = i + 1;
            int right = nums.length - 1;
            int target = -nums[i]; // We want nums[left] + nums[right] = target
            
            while (left < right) {
                int sum = nums[left] + nums[right];
                
                if (sum == target) {
                    // Found a valid triplet
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    
                    // Skip duplicates for left pointer
                    while (left < right && nums[left] == nums[left + 1]) {
                        left++;
                    }
                    // Skip duplicates for right pointer
                    while (left < right && nums[right] == nums[right - 1]) {
                        right--;
                    }
                    
                    left++;
                    right--;
                } else if (sum < target) {
                    left++; // Need a larger sum
                } else {
                    right--; // Need a smaller sum
                }
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: USING HASHMAP (ALTERNATIVE)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * For each pair (i,j), check if -(nums[i] + nums[j]) exists in the array.
     * This approach is less efficient than two pointers due to space usage.
     */
    public List<List<Integer>> threeSumHashMap(int[] nums) {
        Set<List<Integer>> resultSet = new HashSet<>();
        Arrays.sort(nums); // Sort to handle duplicates easily
        
        for (int i = 0; i < nums.length - 2; i++) {
            Set<Integer> seen = new HashSet<>();
            
            for (int j = i + 1; j < nums.length; j++) {
                int complement = -(nums[i] + nums[j]);
                
                if (seen.contains(complement)) {
                    List<Integer> triplet = Arrays.asList(nums[i], complement, nums[j]);
                    Collections.sort(triplet);
                    resultSet.add(triplet);
                }
                
                seen.add(nums[j]);
            }
        }
        
        return new ArrayList<>(resultSet);
    }
    
    /**
     * APPROACH 4: OPTIMIZED WITH EARLY TERMINATION
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * Enhanced version of the two pointers approach with optimizations
     * for better performance on average cases.
     */
    public List<List<Integer>> threeSumOptimizedAdvanced(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);
        
        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates
            if (i > 0 && nums[i] == nums[i - 1]) continue;
            
            // Early termination: if smallest possible sum is > 0, break
            if (nums[i] + nums[i + 1] + nums[i + 2] > 0) break;
            
            // Early termination: if largest possible sum is < 0, continue
            if (nums[i] + nums[nums.length - 2] + nums[nums.length - 1] < 0) continue;
            
            int left = i + 1;
            int right = nums.length - 1;
            
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];
                
                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    
                    // Skip duplicates
                    while (left < right && nums[left] == nums[left + 1]) left++;
                    while (left < right && nums[right] == nums[right - 1]) right--;
                    
                    left++;
                    right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }
        
        return result;
    }
    
    // Test method
    public static void main(String[] args) {
        ThreeSum solution = new ThreeSum();
        
        // Test case 1
        int[] nums1 = {-1, 0, 1, 2, -1, -4};
        System.out.println("Test Case 1: nums = [-1,0,1,2,-1,-4]");
        System.out.println("Brute Force: " + solution.threeSumBruteForce(nums1));
        System.out.println("Optimized: " + solution.threeSumOptimized(nums1));
        System.out.println("HashMap: " + solution.threeSumHashMap(nums1));
        System.out.println("Advanced: " + solution.threeSumOptimizedAdvanced(nums1));
        System.out.println();
        
        // Test case 2: No solution
        int[] nums2 = {0, 1, 1};
        System.out.println("Test Case 2: nums = [0,1,1]");
        System.out.println("Optimized: " + solution.threeSumOptimized(nums2));
        System.out.println();
        
        // Test case 3: All zeros
        int[] nums3 = {0, 0, 0};
        System.out.println("Test Case 3: nums = [0,0,0]");
        System.out.println("Optimized: " + solution.threeSumOptimized(nums3));
        System.out.println();
        
        // Test case 4: Edge case
        int[] nums4 = {-2, 0, 1, 1, 2};
        System.out.println("Test Case 4: nums = [-2,0,1,1,2]");
        System.out.println("Optimized: " + solution.threeSumOptimized(nums4));
    }
} 