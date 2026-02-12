import java.util.*;

/**
 * LeetCode 78: Subsets
 * 
 * Given an integer array nums of unique elements, return all possible subsets (the power set).
 * The solution set must not contain duplicate subsets. Return the solution in any order.
 * 
 * Example:
 * Input: nums = [1,2,3]
 * Output: [[],[1],[2],[1,2],[3],[1,3],[2,3],[1,2,3]]
 * 
 * Time Complexity: O(n * 2^n) where n is the length of nums
 * Space Complexity: O(n * 2^n) for the result
 */
public class Subsets {
    
    /**
     * Approach 1: Backtracking ⭐ (Most Fundamental)
     * For each element, we have 2 choices: include it or exclude it
     */
    public List<List<Integer>> subsets(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(nums, 0, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrack(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        // Add current subset to result (every recursive call represents a valid subset)
        result.add(new ArrayList<>(current));
        
        // Try adding each remaining element
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);              // Make choice
            backtrack(nums, i + 1, current, result);  // Recurse
            current.remove(current.size() - 1); // Backtrack (undo choice)
        }
    }
    
    /**
     * Approach 2: Backtracking with Include/Exclude Decision
     * Explicitly model the binary choice for each element
     */
    public List<List<Integer>> subsetsIncludeExclude(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackIncludeExclude(nums, 0, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrackIncludeExclude(int[] nums, int index, List<Integer> current, List<List<Integer>> result) {
        // Base case: processed all elements
        if (index == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        // Choice 1: Exclude current element
        backtrackIncludeExclude(nums, index + 1, current, result);
        
        // Choice 2: Include current element
        current.add(nums[index]);
        backtrackIncludeExclude(nums, index + 1, current, result);
        current.remove(current.size() - 1); // Backtrack
    }
    
    /**
     * Approach 3: Bit Manipulation ⭐ (Most Efficient)
     * Use bits to represent which elements are included in each subset
     */
    public List<List<Integer>> subsetsBitManipulation(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        
        // There are 2^n possible subsets
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Integer> subset = new ArrayList<>();
            
            // Check each bit position
            for (int i = 0; i < n; i++) {
                // If bit i is set, include nums[i] in subset
                if ((mask & (1 << i)) != 0) {
                    subset.add(nums[i]);
                }
            }
            
            result.add(subset);
        }
        
        return result;
    }
    
    /**
     * Approach 4: Iterative Building
     * Start with empty set, for each new element, double the number of subsets
     */
    public List<List<Integer>> subsetsIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); // Start with empty subset
        
        for (int num : nums) {
            int size = result.size();
            
            // For each existing subset, create a new subset by adding current element
            for (int i = 0; i < size; i++) {
                List<Integer> newSubset = new ArrayList<>(result.get(i));
                newSubset.add(num);
                result.add(newSubset);
            }
        }
        
        return result;
    }
    
    /**
     * Approach 5: Lexicographic Generation (Cascading)
     * Generate subsets in lexicographic order
     */
    public List<List<Integer>> subsetsLexicographic(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); // Empty set
        
        for (int num : nums) {
            List<List<Integer>> newSubsets = new ArrayList<>();
            
            for (List<Integer> subset : result) {
                List<Integer> newSubset = new ArrayList<>(subset);
                newSubset.add(num);
                newSubsets.add(newSubset);
            }
            
            result.addAll(newSubsets);
        }
        
        return result;
    }
    
    /**
     * Subsets II: Handle Duplicates (LeetCode 90)
     * When array contains duplicates, avoid duplicate subsets
     */
    public List<List<Integer>> subsetsWithDup(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Sort to group duplicates together
        backtrackWithDup(nums, 0, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrackWithDup(int[] nums, int start, List<Integer> current, List<List<Integer>> result) {
        result.add(new ArrayList<>(current));
        
        for (int i = start; i < nums.length; i++) {
            // Skip duplicates: if current element equals previous and we're not at start
            if (i > start && nums[i] == nums[i - 1]) {
                continue;
            }
            
            current.add(nums[i]);
            backtrackWithDup(nums, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    /**
     * Extension: Generate Subsets of Specific Size K
     */
    public List<List<Integer>> subsetsOfSizeK(int[] nums, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSizeK(nums, 0, k, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrackSizeK(int[] nums, int start, int k, List<Integer> current, List<List<Integer>> result) {
        // If we have k elements, add to result
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        // Pruning: if we can't possibly reach k elements, stop
        if (current.size() + (nums.length - start) < k) {
            return;
        }
        
        for (int i = start; i < nums.length; i++) {
            current.add(nums[i]);
            backtrackSizeK(nums, i + 1, k, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    /**
     * Extension: Generate k-Subsets (Combinations) - Different from subsetsOfSizeK
     * This is essentially LeetCode 77: Combinations
     */
    public List<List<Integer>> combinations(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackCombinations(1, n, k, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrackCombinations(int start, int n, int k, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = start; i <= n; i++) {
            current.add(i);
            backtrackCombinations(i + 1, n, k, current, result);
            current.remove(current.size() - 1);
        }
    }
    
    /**
     * Helper method to visualize bit representation
     */
    public void printBitRepresentation(int[] nums) {
        int n = nums.length;
        System.out.println("Bit representation for subsets of " + Arrays.toString(nums) + ":");
        
        for (int mask = 0; mask < (1 << n); mask++) {
            System.out.print("Mask " + mask + " (" + Integer.toBinaryString(mask) + "): ");
            List<Integer> subset = new ArrayList<>();
            
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    subset.add(nums[i]);
                }
            }
            
            System.out.println(subset);
        }
    }
    
    public static void main(String[] args) {
        Subsets solution = new Subsets();
        
        // Test case 1: Basic subsets
        int[] nums1 = {1, 2, 3};
        System.out.println("Input: " + Arrays.toString(nums1));
        System.out.println("Backtracking: " + solution.subsets(nums1));
        System.out.println("Include/Exclude: " + solution.subsetsIncludeExclude(nums1));
        System.out.println("Bit Manipulation: " + solution.subsetsBitManipulation(nums1));
        System.out.println("Iterative: " + solution.subsetsIterative(nums1));
        System.out.println("Lexicographic: " + solution.subsetsLexicographic(nums1));
        System.out.println();
        
        // Test case 2: Empty array
        int[] nums2 = {};
        System.out.println("Input: " + Arrays.toString(nums2));
        System.out.println("Result: " + solution.subsets(nums2));
        System.out.println();
        
        // Test case 3: Single element
        int[] nums3 = {0};
        System.out.println("Input: " + Arrays.toString(nums3));
        System.out.println("Result: " + solution.subsets(nums3));
        System.out.println();
        
        // Test case 4: Subsets with duplicates
        int[] nums4 = {1, 2, 2};
        System.out.println("Input with duplicates: " + Arrays.toString(nums4));
        System.out.println("Subsets with duplicates: " + solution.subsetsWithDup(nums4));
        System.out.println();
        
        // Test case 5: Subsets of specific size
        int[] nums5 = {1, 2, 3, 4};
        int k = 2;
        System.out.println("Input: " + Arrays.toString(nums5) + ", k = " + k);
        System.out.println("Subsets of size " + k + ": " + solution.subsetsOfSizeK(nums5, k));
        System.out.println();
        
        // Test case 6: Combinations
        int n = 4, k2 = 2;
        System.out.println("Combinations C(" + n + ", " + k2 + "): " + solution.combinations(n, k2));
        System.out.println();
        
        // Bit representation visualization
        int[] nums6 = {1, 2};
        solution.printBitRepresentation(nums6);
        
        // Performance comparison
        System.out.println("\nPerformance comparison for larger input:");
        int[] nums7 = {1, 2, 3, 4, 5};
        
        long start = System.nanoTime();
        List<List<Integer>> backtrackResult = solution.subsets(nums7);
        long backtrackTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        List<List<Integer>> bitResult = solution.subsetsBitManipulation(nums7);
        long bitTime = System.nanoTime() - start;
        
        System.out.println("Backtracking time: " + backtrackTime / 1000000.0 + " ms");
        System.out.println("Bit manipulation time: " + bitTime / 1000000.0 + " ms");
        System.out.println("Results equal: " + (backtrackResult.size() == bitResult.size()));
    }
} 