import java.util.*;

/**
 * Longest Increasing Subsequence (LIS)
 * 
 * Given an integer array nums, return the length of the longest strictly increasing subsequence.
 * A subsequence is a sequence that can be derived from the array by deleting some or no elements 
 * without changing the order of the remaining elements.
 * 
 * Example:
 * Input: nums = [10,9,2,5,3,7,101,18]
 * Output: 4
 * Explanation: The longest increasing subsequence is [2,3,7,18], therefore the length is 4.
 */
public class LongestIncreasingSubsequence {
    
    /**
     * APPROACH 1: DYNAMIC PROGRAMMING
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Classic DP approach where dp[i] represents length of LIS ending at index i.
     */
    public int lengthOfLISDP(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1); // Each element forms a subsequence of length 1
        
        int maxLength = 1;
        
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLength = Math.max(maxLength, dp[i]);
        }
        
        return maxLength;
    }
    
    /**
     * APPROACH 2: BINARY SEARCH + GREEDY (OPTIMAL)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Maintains an array where tails[i] is the smallest ending element of all 
     * increasing subsequences of length i+1.
     */
    public int lengthOfLISOptimal(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        List<Integer> tails = new ArrayList<>();
        
        for (int num : nums) {
            // Binary search for the position to insert/replace
            int pos = binarySearch(tails, num);
            
            if (pos == tails.size()) {
                // num is larger than all elements in tails, extend the sequence
                tails.add(num);
            } else {
                // Replace the element at pos with num
                tails.set(pos, num);
            }
        }
        
        return tails.size();
    }
    
    private int binarySearch(List<Integer> tails, int target) {
        int left = 0, right = tails.size();
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            if (tails.get(mid) < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return left;
    }
    
    /**
     * APPROACH 3: RECONSTRUCT THE ACTUAL LIS
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Not only finds the length but also reconstructs the actual LIS.
     */
    public List<Integer> findLIS(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new ArrayList<>();
        }
        
        int n = nums.length;
        int[] dp = new int[n];
        int[] parent = new int[n];
        
        Arrays.fill(dp, 1);
        Arrays.fill(parent, -1);
        
        int maxLength = 1;
        int maxIndex = 0;
        
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    parent[i] = j;
                }
            }
            
            if (dp[i] > maxLength) {
                maxLength = dp[i];
                maxIndex = i;
            }
        }
        
        // Reconstruct the LIS
        List<Integer> lis = new ArrayList<>();
        int current = maxIndex;
        
        while (current != -1) {
            lis.add(nums[current]);
            current = parent[current];
        }
        
        Collections.reverse(lis);
        return lis;
    }
    
    /**
     * APPROACH 4: OPTIMIZED LIS RECONSTRUCTION
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Reconstructs LIS using the optimal O(n log n) approach.
     */
    public List<Integer> findLISOptimal(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new ArrayList<>();
        }
        
        int n = nums.length;
        List<Integer> tails = new ArrayList<>();
        int[] dp = new int[n]; // dp[i] = length of LIS ending at nums[i]
        int[] parent = new int[n]; // To reconstruct the sequence
        
        Arrays.fill(parent, -1);
        
        for (int i = 0; i < n; i++) {
            int pos = binarySearch(tails, nums[i]);
            
            if (pos == tails.size()) {
                tails.add(nums[i]);
            } else {
                tails.set(pos, nums[i]);
            }
            
            dp[i] = pos + 1;
            
            // Find parent for reconstruction
            if (pos > 0) {
                // Find the rightmost element in the previous length
                for (int j = i - 1; j >= 0; j--) {
                    if (dp[j] == pos && nums[j] < nums[i]) {
                        parent[i] = j;
                        break;
                    }
                }
            }
        }
        
        // Find the ending index of LIS
        int maxLength = tails.size();
        int endIndex = -1;
        
        for (int i = n - 1; i >= 0; i--) {
            if (dp[i] == maxLength) {
                endIndex = i;
                break;
            }
        }
        
        // Reconstruct LIS
        List<Integer> lis = new ArrayList<>();
        int current = endIndex;
        
        while (current != -1) {
            lis.add(nums[current]);
            current = parent[current];
        }
        
        Collections.reverse(lis);
        return lis;
    }
    
    /**
     * APPROACH 5: COUNT OF LIS
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Finds the number of longest increasing subsequences.
     */
    public int findNumberOfLIS(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int n = nums.length;
        int[] lengths = new int[n]; // lengths[i] = length of LIS ending at i
        int[] counts = new int[n];  // counts[i] = number of LIS ending at i
        
        Arrays.fill(lengths, 1);
        Arrays.fill(counts, 1);
        
        int maxLength = 1;
        
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] > nums[j]) {
                    if (lengths[j] + 1 > lengths[i]) {
                        lengths[i] = lengths[j] + 1;
                        counts[i] = counts[j];
                    } else if (lengths[j] + 1 == lengths[i]) {
                        counts[i] += counts[j];
                    }
                }
            }
            maxLength = Math.max(maxLength, lengths[i]);
        }
        
        // Sum up counts for all LIS
        int result = 0;
        for (int i = 0; i < n; i++) {
            if (lengths[i] == maxLength) {
                result += counts[i];
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 6: LIS WITH DUPLICATES ALLOWED
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Longest non-decreasing subsequence (allows duplicates).
     */
    public int lengthOfLISWithDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        
        int maxLength = 1;
        
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] >= nums[j]) { // Allow equal elements
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLength = Math.max(maxLength, dp[i]);
        }
        
        return maxLength;
    }
    
    /**
     * APPROACH 7: LONGEST DECREASING SUBSEQUENCE
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Variant: Find the longest decreasing subsequence.
     */
    public int lengthOfLDS(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int n = nums.length;
        int[] dp = new int[n];
        Arrays.fill(dp, 1);
        
        int maxLength = 1;
        
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[i] < nums[j]) { // Decreasing condition
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
            maxLength = Math.max(maxLength, dp[i]);
        }
        
        return maxLength;
    }
    
    /**
     * Utility method to print array
     */
    private void printArray(int[] arr, String label) {
        System.out.print(label + ": ");
        for (int num : arr) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    
    /**
     * Utility method to print list
     */
    private void printList(List<Integer> list, String label) {
        System.out.print(label + ": ");
        for (int num : list) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        LongestIncreasingSubsequence lis = new LongestIncreasingSubsequence();
        
        // Test Case 1: Standard example
        System.out.println("=== Test Case 1: Standard Example ===");
        int[] nums1 = {10, 9, 2, 5, 3, 7, 101, 18};
        lis.printArray(nums1, "Array");
        
        System.out.println("DP Length: " + lis.lengthOfLISDP(nums1));
        System.out.println("Optimal Length: " + lis.lengthOfLISOptimal(nums1));
        lis.printList(lis.findLIS(nums1), "Actual LIS");
        System.out.println("Number of LIS: " + lis.findNumberOfLIS(nums1));
        System.out.println();
        
        // Test Case 2: All increasing
        System.out.println("=== Test Case 2: All Increasing ===");
        int[] nums2 = {1, 2, 3, 4, 5};
        lis.printArray(nums2, "Array");
        System.out.println("Length: " + lis.lengthOfLISOptimal(nums2));
        lis.printList(lis.findLIS(nums2), "Actual LIS");
        System.out.println();
        
        // Test Case 3: All decreasing
        System.out.println("=== Test Case 3: All Decreasing ===");
        int[] nums3 = {5, 4, 3, 2, 1};
        lis.printArray(nums3, "Array");
        System.out.println("LIS Length: " + lis.lengthOfLISOptimal(nums3));
        System.out.println("LDS Length: " + lis.lengthOfLDS(nums3));
        System.out.println();
        
        // Test Case 4: With duplicates
        System.out.println("=== Test Case 4: With Duplicates ===");
        int[] nums4 = {1, 3, 6, 7, 9, 4, 10, 5, 6};
        lis.printArray(nums4, "Array");
        System.out.println("Strict LIS: " + lis.lengthOfLISOptimal(nums4));
        System.out.println("LIS with duplicates: " + lis.lengthOfLISWithDuplicates(nums4));
        System.out.println();
        
        // Test Case 5: Complex example
        System.out.println("=== Test Case 5: Complex Example ===");
        int[] nums5 = {1, 2, 3, 1, 2, 3, 1, 2, 3};
        lis.printArray(nums5, "Array");
        System.out.println("Length: " + lis.lengthOfLISOptimal(nums5));
        lis.printList(lis.findLIS(nums5), "One possible LIS");
        System.out.println("Number of LIS: " + lis.findNumberOfLIS(nums5));
        System.out.println();
        
        // Performance comparison
        performanceTest(lis);
    }
    
    private static void performanceTest(LongestIncreasingSubsequence lis) {
        System.out.println("=== Performance Test ===");
        
        int[] sizes = {100, 1000, 5000};
        
        for (int size : sizes) {
            // Generate random array
            int[] nums = new int[size];
            Random rand = new Random(42); // Fixed seed for reproducibility
            for (int i = 0; i < size; i++) {
                nums[i] = rand.nextInt(size);
            }
            
            System.out.println("Array size: " + size);
            
            // Test DP approach
            long startTime = System.nanoTime();
            int result1 = lis.lengthOfLISDP(nums);
            long endTime = System.nanoTime();
            System.out.println("DP O(n²): " + result1 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            // Test optimal approach
            startTime = System.nanoTime();
            int result2 = lis.lengthOfLISOptimal(nums);
            endTime = System.nanoTime();
            System.out.println("Optimal O(n log n): " + result2 + " (" + 
                             (endTime - startTime) / 1_000_000.0 + " ms)");
            
            System.out.println("Results match: " + (result1 == result2));
            System.out.println();
        }
    }
} 