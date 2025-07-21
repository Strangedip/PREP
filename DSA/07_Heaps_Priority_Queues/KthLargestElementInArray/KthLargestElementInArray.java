import java.util.*;

/**
 * LeetCode 215: Kth Largest Element in an Array
 * 
 * Given an integer array nums and an integer k, return the kth largest element in the array.
 * Note that it is the kth largest element in the sorted order, not the kth distinct element.
 * 
 * You must solve it in O(n) time complexity.
 */
public class KthLargestElementInArray {
    
    /**
     * Approach 1: Min Heap of size k
     * Keep only k largest elements in a min heap
     * 
     * Time Complexity: O(n log k)
     * Space Complexity: O(k)
     */
    public int findKthLargestMinHeap(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        
        for (int num : nums) {
            minHeap.offer(num);
            // Keep only k largest elements
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        
        return minHeap.peek(); // Root of min heap is kth largest
    }
    
    /**
     * Approach 2: Max Heap
     * Add all elements to max heap and extract k times
     * 
     * Time Complexity: O(n + k log n)
     * Space Complexity: O(n)
     */
    public int findKthLargestMaxHeap(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        
        // Add all elements to max heap
        for (int num : nums) {
            maxHeap.offer(num);
        }
        
        // Extract k-1 largest elements
        for (int i = 0; i < k - 1; i++) {
            maxHeap.poll();
        }
        
        return maxHeap.peek();
    }
    
    /**
     * Approach 3: QuickSelect (Optimal O(n) average case)
     * Based on quicksort partitioning
     * 
     * Time Complexity: O(n) average, O(n²) worst case
     * Space Complexity: O(1)
     */
    public int findKthLargestQuickSelect(int[] nums, int k) {
        return quickSelect(nums, 0, nums.length - 1, nums.length - k);
    }
    
    private int quickSelect(int[] nums, int left, int right, int kSmallest) {
        if (left == right) {
            return nums[left];
        }
        
        // Random pivot for better average case performance
        Random random = new Random();
        int pivotIndex = left + random.nextInt(right - left + 1);
        
        pivotIndex = partition(nums, left, right, pivotIndex);
        
        if (kSmallest == pivotIndex) {
            return nums[kSmallest];
        } else if (kSmallest < pivotIndex) {
            return quickSelect(nums, left, pivotIndex - 1, kSmallest);
        } else {
            return quickSelect(nums, pivotIndex + 1, right, kSmallest);
        }
    }
    
    private int partition(int[] nums, int left, int right, int pivotIndex) {
        int pivotValue = nums[pivotIndex];
        
        // Move pivot to end
        swap(nums, pivotIndex, right);
        
        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (nums[i] < pivotValue) {
                swap(nums, i, storeIndex);
                storeIndex++;
            }
        }
        
        // Move pivot to its final place
        swap(nums, storeIndex, right);
        return storeIndex;
    }
    
    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
    
    /**
     * Approach 4: Simple Sorting
     * Sort array and return kth from end
     * 
     * Time Complexity: O(n log n)
     * Space Complexity: O(1) if in-place sort
     */
    public int findKthLargestSorting(int[] nums, int k) {
        Arrays.sort(nums);
        return nums[nums.length - k];
    }
    
    public static void main(String[] args) {
        KthLargestElementInArray solution = new KthLargestElementInArray();
        
        // Test case 1: [3,2,1,5,6,4], k = 2
        int[] nums1 = {3, 2, 1, 5, 6, 4};
        int k1 = 2;
        System.out.println("Array: " + Arrays.toString(nums1) + ", k = " + k1);
        System.out.println("Min Heap approach: " + solution.findKthLargestMinHeap(nums1.clone(), k1)); // 5
        System.out.println("Max Heap approach: " + solution.findKthLargestMaxHeap(nums1.clone(), k1)); // 5
        System.out.println("QuickSelect approach: " + solution.findKthLargestQuickSelect(nums1.clone(), k1)); // 5
        System.out.println("Sorting approach: " + solution.findKthLargestSorting(nums1.clone(), k1)); // 5
        System.out.println();
        
        // Test case 2: [3,2,3,1,2,4,5,5,6], k = 4
        int[] nums2 = {3, 2, 3, 1, 2, 4, 5, 5, 6};
        int k2 = 4;
        System.out.println("Array: " + Arrays.toString(nums2) + ", k = " + k2);
        System.out.println("Min Heap approach: " + solution.findKthLargestMinHeap(nums2.clone(), k2)); // 4
        System.out.println("Max Heap approach: " + solution.findKthLargestMaxHeap(nums2.clone(), k2)); // 4
        System.out.println("QuickSelect approach: " + solution.findKthLargestQuickSelect(nums2.clone(), k2)); // 4
        System.out.println("Sorting approach: " + solution.findKthLargestSorting(nums2.clone(), k2)); // 4
        System.out.println();
        
        // Test case 3: Single element
        int[] nums3 = {1};
        int k3 = 1;
        System.out.println("Array: " + Arrays.toString(nums3) + ", k = " + k3);
        System.out.println("Result: " + solution.findKthLargestMinHeap(nums3, k3)); // 1
    }
} 