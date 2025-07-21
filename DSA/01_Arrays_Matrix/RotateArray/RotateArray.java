import java.util.*;

/**
 * Problem: Rotate Array
 * 
 * Given an array, rotate the array to the right by k steps, where k is non-negative.
 * 
 * Example:
 * Input: nums = [1,2,3,4,5,6,7], k = 3
 * Output: [5,6,7,1,2,3,4]
 */
public class RotateArray {
    
    /**
     * APPROACH 1: EXTRA ARRAY
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public void rotateBruteForce(int[] nums, int k) {
        int n = nums.length;
        k = k % n;
        int[] temp = new int[n];
        
        for (int i = 0; i < n; i++) {
            temp[(i + k) % n] = nums[i];
        }
        
        for (int i = 0; i < n; i++) {
            nums[i] = temp[i];
        }
    }
    
    /**
     * APPROACH 2: REVERSE ALGORITHM (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public void rotate(int[] nums, int k) {
        int n = nums.length;
        k = k % n;
        
        // Reverse entire array
        reverse(nums, 0, n - 1);
        // Reverse first k elements
        reverse(nums, 0, k - 1);
        // Reverse remaining elements
        reverse(nums, k, n - 1);
    }
    
    private void reverse(int[] nums, int start, int end) {
        while (start < end) {
            int temp = nums[start];
            nums[start] = nums[end];
            nums[end] = temp;
            start++;
            end--;
        }
    }
    
    /**
     * APPROACH 3: CYCLIC REPLACEMENTS
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public void rotateCyclic(int[] nums, int k) {
        int n = nums.length;
        k = k % n;
        int count = 0;
        
        for (int start = 0; count < n; start++) {
            int current = start;
            int prev = nums[start];
            
            do {
                int next = (current + k) % n;
                int temp = nums[next];
                nums[next] = prev;
                prev = temp;
                current = next;
                count++;
            } while (start != current);
        }
    }
    
    // Test method
    public static void main(String[] args) {
        RotateArray solution = new RotateArray();
        
        int[] nums1 = {1,2,3,4,5,6,7};
        solution.rotate(nums1, 3);
        System.out.println("Test 1: " + Arrays.toString(nums1));
        
        int[] nums2 = {-1,-100,3,99};
        solution.rotate(nums2, 2);
        System.out.println("Test 2: " + Arrays.toString(nums2));
    }
} 