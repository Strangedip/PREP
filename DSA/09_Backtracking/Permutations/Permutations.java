import java.util.*;

/**
 * LeetCode 46: Permutations & LeetCode 47: Permutations II
 * 
 * Given an array nums of distinct integers, return all the possible permutations.
 * You can return the answer in any order.
 * 
 * Example:
 * Input: nums = [1,2,3]
 * Output: [[1,2,3],[1,3,2],[2,1,3],[2,3,1],[3,1,2],[3,2,1]]
 * 
 * Time Complexity: O(n! * n) where n is the length of nums
 * Space Complexity: O(n! * n) for storing all permutations
 */
public class Permutations {
    
    /**
     * Approach 1: Backtracking with Used Array ⭐ (Most Common)
     * Use a boolean array to track which elements are already used
     */
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrack(nums, used, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrack(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
        // Base case: if current permutation is complete
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        // Try each unused element
        for (int i = 0; i < nums.length; i++) {
            if (!used[i]) {
                used[i] = true;                    // Mark as used
                current.add(nums[i]);             // Make choice
                backtrack(nums, used, current, result);  // Recurse
                current.remove(current.size() - 1); // Backtrack
                used[i] = false;                   // Mark as unused
            }
        }
    }
    
    /**
     * Approach 2: Backtracking with Swapping ⭐ (More Efficient)
     * Swap elements to generate permutations without extra space for tracking used elements
     */
    public List<List<Integer>> permuteSwapping(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        backtrackSwap(nums, 0, result);
        return result;
    }
    
    private void backtrackSwap(int[] nums, int start, List<List<Integer>> result) {
        // Base case: if we've placed all elements
        if (start == nums.length) {
            // Convert array to list and add to result
            List<Integer> permutation = new ArrayList<>();
            for (int num : nums) {
                permutation.add(num);
            }
            result.add(permutation);
            return;
        }
        
        // Try placing each remaining element at current position
        for (int i = start; i < nums.length; i++) {
            swap(nums, start, i);              // Make choice (swap)
            backtrackSwap(nums, start + 1, result);  // Recurse
            swap(nums, start, i);              // Backtrack (swap back)
        }
    }
    
    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
    
    /**
     * Approach 3: Iterative Building
     * Build permutations iteratively by inserting each new element at all possible positions
     */
    public List<List<Integer>> permuteIterative(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        result.add(new ArrayList<>()); // Start with empty permutation
        
        for (int num : nums) {
            List<List<Integer>> newPermutations = new ArrayList<>();
            
            for (List<Integer> permutation : result) {
                // Insert current number at each possible position
                for (int i = 0; i <= permutation.size(); i++) {
                    List<Integer> newPermutation = new ArrayList<>(permutation);
                    newPermutation.add(i, num);
                    newPermutations.add(newPermutation);
                }
            }
            
            result = newPermutations;
        }
        
        return result;
    }
    
    /**
     * Approach 4: Using Collections.nextPermutation equivalent
     * Generate permutations in lexicographic order
     */
    public List<List<Integer>> permuteLexicographic(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Start with smallest permutation
        
        do {
            List<Integer> permutation = new ArrayList<>();
            for (int num : nums) {
                permutation.add(num);
            }
            result.add(permutation);
        } while (nextPermutation(nums));
        
        return result;
    }
    
    /**
     * Generate next lexicographic permutation
     * Returns true if next permutation exists, false otherwise
     */
    private boolean nextPermutation(int[] nums) {
        int i = nums.length - 2;
        
        // Find the largest index i such that nums[i] < nums[i + 1]
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }
        
        if (i == -1) {
            return false; // No next permutation exists
        }
        
        // Find the largest index j such that nums[i] < nums[j]
        int j = nums.length - 1;
        while (nums[j] <= nums[i]) {
            j--;
        }
        
        // Swap nums[i] and nums[j]
        swap(nums, i, j);
        
        // Reverse the suffix starting at nums[i + 1]
        reverse(nums, i + 1, nums.length - 1);
        
        return true;
    }
    
    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            swap(nums, start, end);
            start++;
            end--;
        }
    }
    
    /**
     * Permutations II: Handle Duplicates (LeetCode 47)
     * When array contains duplicates, avoid duplicate permutations
     */
    public List<List<Integer>> permuteUnique(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums); // Sort to group duplicates
        boolean[] used = new boolean[nums.length];
        backtrackUnique(nums, used, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrackUnique(int[] nums, boolean[] used, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == nums.length) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = 0; i < nums.length; i++) {
            if (used[i]) continue;
            
            // Skip duplicates: only use duplicate if previous same element is used
            if (i > 0 && nums[i] == nums[i - 1] && !used[i - 1]) {
                continue;
            }
            
            used[i] = true;
            current.add(nums[i]);
            backtrackUnique(nums, used, current, result);
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
    
    /**
     * Extension: Permutations of String
     */
    public List<String> permuteString(String s) {
        List<String> result = new ArrayList<>();
        char[] chars = s.toCharArray();
        Arrays.sort(chars); // For consistent ordering
        boolean[] used = new boolean[chars.length];
        backtrackString(chars, used, new StringBuilder(), result);
        return result;
    }
    
    private void backtrackString(char[] chars, boolean[] used, StringBuilder current, List<String> result) {
        if (current.length() == chars.length) {
            result.add(current.toString());
            return;
        }
        
        for (int i = 0; i < chars.length; i++) {
            if (used[i]) continue;
            
            // Skip duplicates for string permutations
            if (i > 0 && chars[i] == chars[i - 1] && !used[i - 1]) {
                continue;
            }
            
            used[i] = true;
            current.append(chars[i]);
            backtrackString(chars, used, current, result);
            current.deleteCharAt(current.length() - 1);
            used[i] = false;
        }
    }
    
    /**
     * Extension: k-Permutations (Permutations of length k)
     */
    public List<List<Integer>> permuteK(int[] nums, int k) {
        List<List<Integer>> result = new ArrayList<>();
        boolean[] used = new boolean[nums.length];
        backtrackK(nums, used, k, new ArrayList<>(), result);
        return result;
    }
    
    private void backtrackK(int[] nums, boolean[] used, int k, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        
        for (int i = 0; i < nums.length; i++) {
            if (!used[i]) {
                used[i] = true;
                current.add(nums[i]);
                backtrackK(nums, used, k, current, result);
                current.remove(current.size() - 1);
                used[i] = false;
            }
        }
    }
    
    /**
     * Helper method to count total permutations
     */
    public long countPermutations(int n) {
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
    
    /**
     * Helper method to count permutations with duplicates
     */
    public long countPermutationsWithDuplicates(int[] nums) {
        Map<Integer, Integer> frequency = new HashMap<>();
        for (int num : nums) {
            frequency.put(num, frequency.getOrDefault(num, 0) + 1);
        }
        
        long numerator = countPermutations(nums.length);
        long denominator = 1;
        
        for (int freq : frequency.values()) {
            denominator *= countPermutations(freq);
        }
        
        return numerator / denominator;
    }
    
    public static void main(String[] args) {
        Permutations solution = new Permutations();
        
        // Test case 1: Basic permutations
        int[] nums1 = {1, 2, 3};
        System.out.println("Input: " + Arrays.toString(nums1));
        System.out.println("Backtracking with used array: " + solution.permute(nums1));
        System.out.println("Backtracking with swapping: " + solution.permuteSwapping(nums1.clone()));
        System.out.println("Iterative: " + solution.permuteIterative(nums1));
        System.out.println("Lexicographic: " + solution.permuteLexicographic(nums1.clone()));
        System.out.println("Total permutations: " + solution.countPermutations(nums1.length));
        System.out.println();
        
        // Test case 2: Permutations with duplicates
        int[] nums2 = {1, 1, 2};
        System.out.println("Input with duplicates: " + Arrays.toString(nums2));
        System.out.println("Unique permutations: " + solution.permuteUnique(nums2));
        System.out.println("Expected count: " + solution.countPermutationsWithDuplicates(nums2));
        System.out.println();
        
        // Test case 3: Single element
        int[] nums3 = {1};
        System.out.println("Input: " + Arrays.toString(nums3));
        System.out.println("Result: " + solution.permute(nums3));
        System.out.println();
        
        // Test case 4: Empty array
        int[] nums4 = {};
        System.out.println("Input: " + Arrays.toString(nums4));
        System.out.println("Result: " + solution.permute(nums4));
        System.out.println();
        
        // Test case 5: k-Permutations
        int[] nums5 = {1, 2, 3, 4};
        int k = 2;
        System.out.println("Input: " + Arrays.toString(nums5) + ", k = " + k);
        System.out.println("k-Permutations: " + solution.permuteK(nums5, k));
        System.out.println();
        
        // Test case 6: String permutations
        String s = "abc";
        System.out.println("String permutations of '" + s + "': " + solution.permuteString(s));
        
        // Performance comparison
        System.out.println("\nPerformance comparison:");
        int[] nums6 = {1, 2, 3, 4, 5};
        
        long start = System.nanoTime();
        List<List<Integer>> result1 = solution.permute(nums6);
        long time1 = System.nanoTime() - start;
        
        start = System.nanoTime();
        List<List<Integer>> result2 = solution.permuteSwapping(nums6.clone());
        long time2 = System.nanoTime() - start;
        
        System.out.println("Used array approach: " + time1 / 1000000.0 + " ms");
        System.out.println("Swapping approach: " + time2 / 1000000.0 + " ms");
        System.out.println("Results equal: " + (result1.size() == result2.size()));
    }
} 