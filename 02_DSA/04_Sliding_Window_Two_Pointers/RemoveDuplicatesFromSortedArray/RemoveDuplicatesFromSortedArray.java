public class RemoveDuplicatesFromSortedArray {
    
    /**
     * Remove duplicates from sorted array in-place
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public int removeDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int slow = 0; // Points to the last unique element
        
        for (int fast = 1; fast < nums.length; fast++) {
            // If current element is different from the last unique element
            if (nums[fast] != nums[slow]) {
                slow++;
                nums[slow] = nums[fast];
            }
        }
        
        return slow + 1; // Length of array with unique elements
    }
    
    /**
     * Remove duplicates from sorted array II - Allow at most 2 duplicates
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public int removeDuplicatesII(int[] nums) {
        if (nums == null || nums.length <= 2) {
            return nums == null ? 0 : nums.length;
        }
        
        int slow = 2; // Start from index 2 since first two elements are always valid
        
        for (int fast = 2; fast < nums.length; fast++) {
            // Allow at most 2 duplicates by comparing with element at slow-2
            if (nums[fast] != nums[slow - 2]) {
                nums[slow] = nums[fast];
                slow++;
            }
        }
        
        return slow;
    }
    
    /**
     * Generic version: Remove duplicates allowing at most k duplicates
     */
    public int removeDuplicatesAtMostK(int[] nums, int k) {
        if (nums == null || nums.length <= k) {
            return nums == null ? 0 : nums.length;
        }
        
        int slow = k;
        
        for (int fast = k; fast < nums.length; fast++) {
            if (nums[fast] != nums[slow - k]) {
                nums[slow] = nums[fast];
                slow++;
            }
        }
        
        return slow;
    }
    
    /**
     * Remove all duplicates (keep only unique elements)
     */
    public int removeAllDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return 0;
        }
        
        int slow = 0;
        int i = 0;
        
        while (i < nums.length) {
            int count = 1;
            
            // Count consecutive duplicates
            while (i + 1 < nums.length && nums[i] == nums[i + 1]) {
                count++;
                i++;
            }
            
            // If element appears exactly once, keep it
            if (count == 1) {
                nums[slow] = nums[i];
                slow++;
            }
            
            i++;
        }
        
        return slow;
    }
    
    // Helper method to print array
    private void printArray(int[] nums, int length) {
        for (int i = 0; i < length; i++) {
            System.out.print(nums[i] + " ");
        }
        System.out.println();
    }
    
    // Test cases
    public static void main(String[] args) {
        RemoveDuplicatesFromSortedArray solution = new RemoveDuplicatesFromSortedArray();
        
        // Test case 1: Basic remove duplicates
        int[] nums1 = {1, 1, 2};
        int length1 = solution.removeDuplicates(nums1.clone());
        System.out.println("Test 1 - Length: " + length1); // Expected: 2
        solution.printArray(nums1, length1); // Expected: [1, 2]
        
        // Test case 2: Remove duplicates with more elements
        int[] nums2 = {0, 0, 1, 1, 1, 2, 2, 3, 3, 4};
        int length2 = solution.removeDuplicates(nums2.clone());
        System.out.println("Test 2 - Length: " + length2); // Expected: 5
        solution.printArray(nums2, length2); // Expected: [0, 1, 2, 3, 4]
        
        // Test case 3: Remove duplicates II (allow 2 duplicates)
        int[] nums3 = {1, 1, 1, 2, 2, 3};
        int length3 = solution.removeDuplicatesII(nums3.clone());
        System.out.println("Test 3 - Length: " + length3); // Expected: 5
        solution.printArray(nums3, length3); // Expected: [1, 1, 2, 2, 3]
        
        // Test case 4: Remove all duplicates
        int[] nums4 = {1, 1, 2, 3, 3};
        int length4 = solution.removeAllDuplicates(nums4.clone());
        System.out.println("Test 4 - Length: " + length4); // Expected: 1
        solution.printArray(nums4, length4); // Expected: [2]
    }
} 