import java.util.*;

/**
 * Problem: Find Minimum in Rotated Sorted Array
 * 
 * Suppose an array of length n sorted in ascending order is rotated between 1 and n times.
 * For example, the array nums = [0,1,2,4,5,6,7] might become:
 * - [4,5,6,7,0,1,2] if it was rotated 4 times.
 * - [0,1,2,4,5,6,7] if it was rotated 7 times.
 * 
 * Given the sorted rotated array nums of unique elements, return the minimum element of this array.
 * You must write an algorithm that runs in O(log n) time.
 * 
 * Example:
 * Input: nums = [3,4,5,1,2]
 * Output: 1
 * Explanation: The original array was [1,2,3,4,5] rotated 3 times.
 */
public class FindMinimumInRotatedSortedArray {
    
    /**
     * APPROACH 1: LINEAR SEARCH (Brute Force)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Simple approach - just find the minimum element by scanning the array.
     * This doesn't take advantage of the sorted property but works.
     */
    public int findMinLinear(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        
        int min = nums[0];
        for (int i = 1; i < nums.length; i++) {
            min = Math.min(min, nums[i]);
        }
        return min;
    }
    
    /**
     * APPROACH 2: BINARY SEARCH (Optimal)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Key insight: In a rotated sorted array, the minimum element is the point
     * where the rotation happened. We can use binary search to find this point.
     * 
     * The array is divided into two parts:
     * - Left part: All elements are greater than the last element
     * - Right part: All elements are smaller than or equal to the last element
     * The minimum is the first element of the right part.
     */
    public int findMinBinarySearch(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        
        // If array has only one element
        if (nums.length == 1) {
            return nums[0];
        }
        
        int left = 0, right = nums.length - 1;
        
        // If the array is not rotated (already sorted)
        if (nums[left] < nums[right]) {
            return nums[left];
        }
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            // If mid element is greater than rightmost element,
            // then minimum is in the right half
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                // If mid element is less than or equal to rightmost element,
                // then minimum is in the left half (including mid)
                right = mid;
            }
        }
        
        return nums[left];
    }
    
    /**
     * APPROACH 3: BINARY SEARCH (Alternative Implementation)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * This version compares with the leftmost element instead of rightmost.
     */
    public int findMinBinarySearchAlt(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        
        int left = 0, right = nums.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            // Compare with the leftmost element
            if (nums[mid] < nums[left]) {
                // Minimum is in the left half (including mid)
                right = mid;
            } else if (nums[mid] > nums[right]) {
                // Minimum is in the right half
                left = mid + 1;
            } else {
                // Array is not rotated or we're in the sorted portion
                return nums[left];
            }
        }
        
        return nums[left];
    }
    
    /**
     * APPROACH 4: FIND ROTATION POINT (Educational)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * This approach explicitly finds the rotation point (where array was rotated).
     * The element at this point is the minimum.
     */
    public int findMinRotationPoint(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        
        if (nums.length == 1) {
            return nums[0];
        }
        
        int left = 0, right = nums.length - 1;
        
        // Array is not rotated
        if (nums[left] < nums[right]) {
            return nums[left];
        }
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            // Check if mid is the rotation point
            if (mid < nums.length - 1 && nums[mid] > nums[mid + 1]) {
                return nums[mid + 1];
            }
            
            // Check if mid-1 is the rotation point
            if (mid > 0 && nums[mid - 1] > nums[mid]) {
                return nums[mid];
            }
            
            // Decide which half to search
            if (nums[mid] > nums[left]) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return nums[0];  // Fallback
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        FindMinimumInRotatedSortedArray solution = new FindMinimumInRotatedSortedArray();
        
        // Test case 1: Rotated array
        int[] nums1 = {3, 4, 5, 1, 2};
        System.out.println("Test Case 1: [3,4,5,1,2]");
        System.out.println("Linear Search: " + solution.findMinLinear(nums1));
        System.out.println("Binary Search: " + solution.findMinBinarySearch(nums1));
        System.out.println("Binary Search Alt: " + solution.findMinBinarySearchAlt(nums1));
        System.out.println("Rotation Point: " + solution.findMinRotationPoint(nums1));
        System.out.println();
        
        // Test case 2: Rotated array with different rotation
        int[] nums2 = {4, 5, 6, 7, 0, 1, 2};
        System.out.println("Test Case 2: [4,5,6,7,0,1,2]");
        System.out.println("Linear Search: " + solution.findMinLinear(nums2));
        System.out.println("Binary Search: " + solution.findMinBinarySearch(nums2));
        System.out.println("Binary Search Alt: " + solution.findMinBinarySearchAlt(nums2));
        System.out.println("Rotation Point: " + solution.findMinRotationPoint(nums2));
        System.out.println();
        
        // Test case 3: Non-rotated array (edge case)
        int[] nums3 = {1, 2, 3, 4, 5};
        System.out.println("Test Case 3: [1,2,3,4,5] (not rotated)");
        System.out.println("Linear Search: " + solution.findMinLinear(nums3));
        System.out.println("Binary Search: " + solution.findMinBinarySearch(nums3));
        System.out.println("Binary Search Alt: " + solution.findMinBinarySearchAlt(nums3));
        System.out.println("Rotation Point: " + solution.findMinRotationPoint(nums3));
        System.out.println();
        
        // Test case 4: Single element
        int[] nums4 = {1};
        System.out.println("Test Case 4: [1] (single element)");
        System.out.println("Linear Search: " + solution.findMinLinear(nums4));
        System.out.println("Binary Search: " + solution.findMinBinarySearch(nums4));
        System.out.println("Binary Search Alt: " + solution.findMinBinarySearchAlt(nums4));
        System.out.println("Rotation Point: " + solution.findMinRotationPoint(nums4));
        System.out.println();
        
        // Test case 5: Two elements
        int[] nums5 = {2, 1};
        System.out.println("Test Case 5: [2,1] (two elements)");
        System.out.println("Linear Search: " + solution.findMinLinear(nums5));
        System.out.println("Binary Search: " + solution.findMinBinarySearch(nums5));
        System.out.println("Binary Search Alt: " + solution.findMinBinarySearchAlt(nums5));
        System.out.println("Rotation Point: " + solution.findMinRotationPoint(nums5));
    }
} 