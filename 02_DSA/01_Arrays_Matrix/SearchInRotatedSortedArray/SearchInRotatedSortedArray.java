import java.util.*;

/**
 * Problem: Search in Rotated Sorted Array
 * 
 * There is an integer array nums sorted in ascending order (with distinct values).
 * Prior to being passed to your function, nums is possibly rotated at an unknown pivot 
 * index k (1 <= k < nums.length) such that the resulting array is 
 * [nums[k], nums[k+1], ..., nums[n-1], nums[0], nums[1], ..., nums[k-1]] (0-indexed).
 * 
 * For example, [0,1,2,4,5,6,7] might be rotated at pivot index 3 and become [4,5,6,7,0,1,2].
 * 
 * Given the array nums after the possible rotation and an integer target, 
 * return the index of target if it is in nums, or -1 if it is not in nums.
 * 
 * You must write an algorithm with O(log n) runtime complexity.
 * 
 * Example:
 * Input: nums = [4,5,6,7,0,1,2], target = 0
 * Output: 4
 */
public class SearchInRotatedSortedArray {
    
    /**
     * APPROACH 1: LINEAR SEARCH (Brute Force)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Simple approach - just scan the array to find the target.
     * This doesn't take advantage of the sorted property but works.
     */
    public int searchLinear(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * APPROACH 2: BINARY SEARCH (Optimal)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Key insight: Although the array is rotated, at least one half of the array
     * is always sorted. We can determine which half is sorted and then decide
     * whether our target lies in that sorted half or the other half.
     */
    public int searchBinarySearch(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        
        int left = 0, right = nums.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            // Found target
            if (nums[mid] == target) {
                return mid;
            }
            
            // Determine which side is sorted
            if (nums[left] <= nums[mid]) {
                // Left half is sorted
                if (target >= nums[left] && target < nums[mid]) {
                    // Target is in the sorted left half
                    right = mid - 1;
                } else {
                    // Target is in the right half
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (target > nums[mid] && target <= nums[right]) {
                    // Target is in the sorted right half
                    left = mid + 1;
                } else {
                    // Target is in the left half
                    right = mid - 1;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * APPROACH 3: FIND PIVOT THEN BINARY SEARCH
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * First find the rotation point (pivot), then determine which half to search,
     * and finally perform binary search on that half.
     */
    public int searchFindPivot(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        
        if (nums.length == 1) {
            return nums[0] == target ? 0 : -1;
        }
        
        // Find the pivot (rotation point)
        int pivot = findPivot(nums);
        
        // If pivot is 0, array is not rotated
        if (pivot == 0) {
            return binarySearch(nums, 0, nums.length - 1, target);
        }
        
        // Determine which half to search
        if (nums[0] <= target) {
            // Search in left half [0, pivot-1]
            return binarySearch(nums, 0, pivot - 1, target);
        } else {
            // Search in right half [pivot, n-1]
            return binarySearch(nums, pivot, nums.length - 1, target);
        }
    }
    
    /**
     * Helper method to find the pivot (rotation point)
     */
    private int findPivot(int[] nums) {
        int left = 0, right = nums.length - 1;
        
        // Array is not rotated
        if (nums[left] < nums[right]) {
            return 0;
        }
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return left;
    }
    
    /**
     * Helper method for standard binary search
     */
    private int binarySearch(int[] nums, int left, int right, int target) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1;
    }
    
    /**
     * APPROACH 4: RECURSIVE BINARY SEARCH
     * Time Complexity: O(log n)
     * Space Complexity: O(log n) due to recursion stack
     * 
     * Recursive implementation of the binary search approach.
     */
    public int searchRecursive(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return -1;
        }
        return searchRecursiveHelper(nums, 0, nums.length - 1, target);
    }
    
    private int searchRecursiveHelper(int[] nums, int left, int right, int target) {
        if (left > right) {
            return -1;
        }
        
        int mid = left + (right - left) / 2;
        
        if (nums[mid] == target) {
            return mid;
        }
        
        // Check which half is sorted
        if (nums[left] <= nums[mid]) {
            // Left half is sorted
            if (target >= nums[left] && target < nums[mid]) {
                return searchRecursiveHelper(nums, left, mid - 1, target);
            } else {
                return searchRecursiveHelper(nums, mid + 1, right, target);
            }
        } else {
            // Right half is sorted
            if (target > nums[mid] && target <= nums[right]) {
                return searchRecursiveHelper(nums, mid + 1, right, target);
            } else {
                return searchRecursiveHelper(nums, left, mid - 1, target);
            }
        }
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        SearchInRotatedSortedArray solution = new SearchInRotatedSortedArray();
        
        // Test case 1: Target found
        int[] nums1 = {4, 5, 6, 7, 0, 1, 2};
        int target1 = 0;
        System.out.println("Test Case 1: [4,5,6,7,0,1,2], target = 0");
        System.out.println("Linear Search: " + solution.searchLinear(nums1, target1));
        System.out.println("Binary Search: " + solution.searchBinarySearch(nums1, target1));
        System.out.println("Find Pivot: " + solution.searchFindPivot(nums1, target1));
        System.out.println("Recursive: " + solution.searchRecursive(nums1, target1));
        System.out.println();
        
        // Test case 2: Target not found
        int[] nums2 = {4, 5, 6, 7, 0, 1, 2};
        int target2 = 3;
        System.out.println("Test Case 2: [4,5,6,7,0,1,2], target = 3");
        System.out.println("Linear Search: " + solution.searchLinear(nums2, target2));
        System.out.println("Binary Search: " + solution.searchBinarySearch(nums2, target2));
        System.out.println("Find Pivot: " + solution.searchFindPivot(nums2, target2));
        System.out.println("Recursive: " + solution.searchRecursive(nums2, target2));
        System.out.println();
        
        // Test case 3: Single element found
        int[] nums3 = {1};
        int target3 = 1;
        System.out.println("Test Case 3: [1], target = 1");
        System.out.println("Linear Search: " + solution.searchLinear(nums3, target3));
        System.out.println("Binary Search: " + solution.searchBinarySearch(nums3, target3));
        System.out.println("Find Pivot: " + solution.searchFindPivot(nums3, target3));
        System.out.println("Recursive: " + solution.searchRecursive(nums3, target3));
        System.out.println();
        
        // Test case 4: No rotation
        int[] nums4 = {1, 2, 3, 4, 5, 6, 7};
        int target4 = 5;
        System.out.println("Test Case 4: [1,2,3,4,5,6,7] (not rotated), target = 5");
        System.out.println("Linear Search: " + solution.searchLinear(nums4, target4));
        System.out.println("Binary Search: " + solution.searchBinarySearch(nums4, target4));
        System.out.println("Find Pivot: " + solution.searchFindPivot(nums4, target4));
        System.out.println("Recursive: " + solution.searchRecursive(nums4, target4));
        System.out.println();
        
        // Test case 5: Target at boundaries
        int[] nums5 = {4, 5, 6, 7, 0, 1, 2};
        int target5 = 4;
        System.out.println("Test Case 5: [4,5,6,7,0,1,2], target = 4 (first element)");
        System.out.println("Linear Search: " + solution.searchLinear(nums5, target5));
        System.out.println("Binary Search: " + solution.searchBinarySearch(nums5, target5));
        System.out.println("Find Pivot: " + solution.searchFindPivot(nums5, target5));
        System.out.println("Recursive: " + solution.searchRecursive(nums5, target5));
    }
} 