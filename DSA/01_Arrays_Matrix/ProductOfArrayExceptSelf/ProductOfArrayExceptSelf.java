import java.util.*;

/**
 * Problem: Product of Array Except Self
 * 
 * Given an integer array nums, return an array answer such that answer[i] 
 * is equal to the product of all the elements of nums except nums[i].
 * 
 * The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.
 * You must write an algorithm that runs in O(n) time and without using the division operator.
 * 
 * Example:
 * Input: nums = [1,2,3,4]
 * Output: [24,12,8,6]
 */
public class ProductOfArrayExceptSelf {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     */
    public int[] productExceptSelfBruteForce(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        
        for (int i = 0; i < n; i++) {
            int product = 1;
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    product *= nums[j];
                }
            }
            result[i] = product;
        }
        
        return result;
    }
    
    /**
     * APPROACH 2: USING DIVISION (NOT ALLOWED BUT GOOD TO KNOW)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public int[] productExceptSelfDivision(int[] nums) {
        int totalProduct = 1;
        int zeroCount = 0;
        
        for (int num : nums) {
            if (num == 0) {
                zeroCount++;
            } else {
                totalProduct *= num;
            }
        }
        
        int[] result = new int[nums.length];
        
        for (int i = 0; i < nums.length; i++) {
            if (zeroCount > 1) {
                result[i] = 0;
            } else if (zeroCount == 1) {
                result[i] = (nums[i] == 0) ? totalProduct : 0;
            } else {
                result[i] = totalProduct / nums[i];
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: LEFT AND RIGHT PRODUCTS (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int[] productExceptSelfOptimal(int[] nums) {
        int n = nums.length;
        int[] left = new int[n];
        int[] right = new int[n];
        int[] result = new int[n];
        
        // Build left products array
        left[0] = 1;
        for (int i = 1; i < n; i++) {
            left[i] = left[i - 1] * nums[i - 1];
        }
        
        // Build right products array
        right[n - 1] = 1;
        for (int i = n - 2; i >= 0; i--) {
            right[i] = right[i + 1] * nums[i + 1];
        }
        
        // Combine left and right products
        for (int i = 0; i < n; i++) {
            result[i] = left[i] * right[i];
        }
        
        return result;
    }
    
    /**
     * APPROACH 4: SPACE OPTIMIZED (MOST OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1) - not counting output array
     */
    public int[] productExceptSelfSpaceOptimized(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        
        // First pass: store left products in result array
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }
        
        // Second pass: multiply with right products on the fly
        int rightProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] = result[i] * rightProduct;
            rightProduct *= nums[i];
        }
        
        return result;
    }
    
    /**
     * APPROACH 5: PREFIX AND SUFFIX APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public int[] productExceptSelfPrefixSuffix(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        
        // Calculate prefix products
        answer[0] = 1;
        for (int i = 1; i < n; i++) {
            answer[i] = answer[i - 1] * nums[i - 1];
        }
        
        // Calculate suffix products and multiply with prefix
        int suffixProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= suffixProduct;
            suffixProduct *= nums[i];
        }
        
        return answer;
    }
    
    // Test method
    public static void main(String[] args) {
        ProductOfArrayExceptSelf solution = new ProductOfArrayExceptSelf();
        
        // Test case 1
        int[] nums1 = {1, 2, 3, 4};
        System.out.println("Test Case 1: nums = " + Arrays.toString(nums1));
        System.out.println("Brute Force: " + Arrays.toString(solution.productExceptSelfBruteForce(nums1)));
        System.out.println("Optimal: " + Arrays.toString(solution.productExceptSelfOptimal(nums1)));
        System.out.println("Space Optimized: " + Arrays.toString(solution.productExceptSelfSpaceOptimized(nums1)));
        System.out.println();
        
        // Test case 2: With zeros
        int[] nums2 = {-1, 1, 0, -3, 3};
        System.out.println("Test Case 2: nums = " + Arrays.toString(nums2));
        System.out.println("Space Optimized: " + Arrays.toString(solution.productExceptSelfSpaceOptimized(nums2)));
        System.out.println();
        
        // Test case 3: All positive
        int[] nums3 = {2, 3, 4, 5};
        System.out.println("Test Case 3: nums = " + Arrays.toString(nums3));
        System.out.println("Space Optimized: " + Arrays.toString(solution.productExceptSelfSpaceOptimized(nums3)));
        System.out.println();
        
        // Test case 4: With negative numbers
        int[] nums4 = {-1, -2, -3, -4};
        System.out.println("Test Case 4: nums = " + Arrays.toString(nums4));
        System.out.println("Space Optimized: " + Arrays.toString(solution.productExceptSelfSpaceOptimized(nums4)));
    }
} 