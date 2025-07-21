import java.util.*;

/**
 * Problem: Move Zeroes
 * 
 * Given an integer array nums, move all 0s to the end of it while maintaining 
 * the relative order of the non-zero elements.
 * 
 * Note: You must do this in-place without making a copy of the array.
 * 
 * Example:
 * Input: nums = [0,1,0,3,12]
 * Output: [1,3,12,0,0]
 */
public class MoveZeroes {
    
    /**
     * APPROACH 1: BRUTE FORCE (NOT IN-PLACE)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Create new array, copy non-zero elements first, then zeros.
     */
    public void moveZeroesBruteForce(int[] nums) {
        List<Integer> nonZero = new ArrayList<>();
        int zeroCount = 0;
        
        // Collect non-zero elements and count zeros
        for (int num : nums) {
            if (num != 0) {
                nonZero.add(num);
            } else {
                zeroCount++;
            }
        }
        
        // Copy back to original array
        for (int i = 0; i < nonZero.size(); i++) {
            nums[i] = nonZero.get(i);
        }
        
        // Fill remaining positions with zeros
        for (int i = nonZero.size(); i < nums.length; i++) {
            nums[i] = 0;
        }
    }
    
    /**
     * APPROACH 2: TWO POINTERS (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use two pointers: one to track position for next non-zero element,
     * another to iterate through array.
     */
    public void moveZeroesOptimal(int[] nums) {
        int insertPos = 0; // Position to insert next non-zero element
        
        // Move all non-zero elements to the front
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[insertPos] = nums[i];
                insertPos++;
            }
        }
        
        // Fill remaining positions with zeros
        while (insertPos < nums.length) {
            nums[insertPos] = 0;
            insertPos++;
        }
    }
    
    /**
     * APPROACH 3: SWAP APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Swap non-zero elements with elements at the correct position.
     */
    public void moveZeroesSwap(int[] nums) {
        int insertPos = 0;
        
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                // Swap current element with element at insertPos
                int temp = nums[i];
                nums[i] = nums[insertPos];
                nums[insertPos] = temp;
                insertPos++;
            }
        }
    }
    
    /**
     * APPROACH 4: OPTIMIZED SWAP (AVOID UNNECESSARY SWAPS)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Only swap when necessary to avoid swapping element with itself.
     */
    public void moveZeroesOptimizedSwap(int[] nums) {
        int insertPos = 0;
        
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                if (i != insertPos) {
                    // Only swap if positions are different
                    int temp = nums[i];
                    nums[i] = nums[insertPos];
                    nums[insertPos] = temp;
                }
                insertPos++;
            }
        }
    }
    
    /**
     * APPROACH 5: BUBBLE SORT STYLE (INEFFICIENT)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * Keep bubbling zeros to the right. Not recommended but shows alternative thinking.
     */
    public void moveZeroesBubble(int[] nums) {
        int n = nums.length;
        
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (nums[j] == 0 && nums[j + 1] != 0) {
                    // Swap zero with non-zero
                    int temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                }
            }
        }
    }
    
    /**
     * APPROACH 6: COUNTING APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Count non-zeros while shifting, then fill zeros.
     */
    public void moveZeroesCounting(int[] nums) {
        int writeIndex = 0;
        int nonZeroCount = 0;
        
        // Count and move non-zeros
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                nums[writeIndex++] = nums[i];
                nonZeroCount++;
            }
        }
        
        // Fill remaining with zeros
        for (int i = nonZeroCount; i < nums.length; i++) {
            nums[i] = 0;
        }
    }
    
    // Helper method to print array
    private void printArray(int[] nums) {
        System.out.println(Arrays.toString(nums));
    }
    
    // Test method
    public static void main(String[] args) {
        MoveZeroes solution = new MoveZeroes();
        
        // Test case 1
        int[] nums1 = {0, 1, 0, 3, 12};
        System.out.println("Test Case 1: Original = " + Arrays.toString(nums1));
        solution.moveZeroesOptimal(nums1.clone());
        System.out.println("After moving zeros: " + Arrays.toString(nums1));
        
        // Test the optimal solution
        int[] test1 = {0, 1, 0, 3, 12};
        solution.moveZeroesOptimal(test1);
        System.out.println("Optimal: " + Arrays.toString(test1));
        System.out.println();
        
        // Test case 2: All zeros
        int[] nums2 = {0, 0, 0};
        System.out.println("Test Case 2: All zeros = " + Arrays.toString(nums2));
        solution.moveZeroesOptimal(nums2);
        System.out.println("After moving: " + Arrays.toString(nums2));
        System.out.println();
        
        // Test case 3: No zeros
        int[] nums3 = {1, 2, 3};
        System.out.println("Test Case 3: No zeros = " + Arrays.toString(nums3));
        solution.moveZeroesOptimal(nums3);
        System.out.println("After moving: " + Arrays.toString(nums3));
        System.out.println();
        
        // Test case 4: Single element
        int[] nums4 = {0};
        System.out.println("Test Case 4: Single zero = " + Arrays.toString(nums4));
        solution.moveZeroesOptimal(nums4);
        System.out.println("After moving: " + Arrays.toString(nums4));
        System.out.println();
        
        // Test case 5: Zeros at beginning
        int[] nums5 = {0, 0, 1, 2, 3};
        System.out.println("Test Case 5: Zeros at start = " + Arrays.toString(nums5));
        solution.moveZeroesOptimal(nums5);
        System.out.println("After moving: " + Arrays.toString(nums5));
        System.out.println();
        
        // Compare different approaches
        int[] test = {0, 1, 0, 3, 12};
        
        int[] testSwap = test.clone();
        solution.moveZeroesSwap(testSwap);
        System.out.println("Swap approach: " + Arrays.toString(testSwap));
        
        int[] testOptSwap = test.clone();
        solution.moveZeroesOptimizedSwap(testOptSwap);
        System.out.println("Optimized swap: " + Arrays.toString(testOptSwap));
    }
} 