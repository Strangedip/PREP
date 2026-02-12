import java.util.*;

/**
 * Merge Sort Implementation
 * 
 * Merge Sort is a divide-and-conquer algorithm that divides the input array 
 * into two halves, calls itself for the two halves, and then merges the two 
 * sorted halves.
 * 
 * Time Complexity: O(n log n) in all cases
 * Space Complexity: O(n) for auxiliary array
 * 
 * Characteristics:
 * - Stable sorting algorithm
 * - Not in-place (requires extra space)
 * - Divide and conquer approach
 * - Predictable performance
 */
public class MergeSort {
    
    /**
     * APPROACH 1: CLASSIC MERGE SORT (Top-Down)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Recursively divide array into halves and merge sorted halves.
     */
    public void mergeSortClassic(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        int[] temp = new int[arr.length];
        mergeSortHelper(arr, temp, 0, arr.length - 1);
    }
    
    private void mergeSortHelper(int[] arr, int[] temp, int left, int right) {
        if (left >= right) {
            return;
        }
        
        int mid = left + (right - left) / 2;
        
        // Recursively sort both halves
        mergeSortHelper(arr, temp, left, mid);
        mergeSortHelper(arr, temp, mid + 1, right);
        
        // Merge sorted halves
        merge(arr, temp, left, mid, right);
    }
    
    private void merge(int[] arr, int[] temp, int left, int mid, int right) {
        // Copy data to temp array
        for (int i = left; i <= right; i++) {
            temp[i] = arr[i];
        }
        
        int i = left;      // Pointer for left subarray
        int j = mid + 1;   // Pointer for right subarray
        int k = left;      // Pointer for merged array
        
        // Merge the two sorted subarrays
        while (i <= mid && j <= right) {
            if (temp[i] <= temp[j]) {
                arr[k++] = temp[i++];
            } else {
                arr[k++] = temp[j++];
            }
        }
        
        // Copy remaining elements from left subarray
        while (i <= mid) {
            arr[k++] = temp[i++];
        }
        
        // Copy remaining elements from right subarray
        while (j <= right) {
            arr[k++] = temp[j++];
        }
    }
    
    /**
     * APPROACH 2: BOTTOM-UP MERGE SORT (Iterative)
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Iterative version that merges subarrays of increasing sizes.
     */
    public void mergeSortBottomUp(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        int n = arr.length;
        int[] temp = new int[n];
        
        // Start with subarrays of size 1, then 2, 4, 8, ...
        for (int size = 1; size < n; size *= 2) {
            for (int left = 0; left < n - size; left += 2 * size) {
                int mid = left + size - 1;
                int right = Math.min(left + 2 * size - 1, n - 1);
                
                merge(arr, temp, left, mid, right);
            }
        }
    }
    
    /**
     * APPROACH 3: OPTIMIZED MERGE SORT
     * Time Complexity: O(n log n)
     * Space Complexity: O(n)
     * 
     * Multiple optimizations:
     * - Skip merge if already sorted
     * - Use insertion sort for small subarrays
     * - Eliminate copy back to original array
     */
    public void mergeSortOptimized(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        int[] temp = new int[arr.length];
        System.arraycopy(arr, 0, temp, 0, arr.length);
        
        mergeSortOptimizedHelper(temp, arr, 0, arr.length - 1);
    }
    
    private void mergeSortOptimizedHelper(int[] src, int[] dst, int left, int right) {
        // Use insertion sort for small subarrays
        if (right - left < 7) {
            insertionSort(dst, left, right);
            return;
        }
        
        int mid = left + (right - left) / 2;
        
        // Recursively sort both halves (swap src and dst)
        mergeSortOptimizedHelper(dst, src, left, mid);
        mergeSortOptimizedHelper(dst, src, mid + 1, right);
        
        // Skip merge if already sorted
        if (src[mid] <= src[mid + 1]) {
            System.arraycopy(src, left, dst, left, right - left + 1);
            return;
        }
        
        // Merge sorted halves
        mergeOptimized(src, dst, left, mid, right);
    }
    
    private void mergeOptimized(int[] src, int[] dst, int left, int mid, int right) {
        int i = left;      // Pointer for left subarray
        int j = mid + 1;   // Pointer for right subarray
        int k = left;      // Pointer for merged array
        
        // Merge the two sorted subarrays
        while (i <= mid && j <= right) {
            if (src[i] <= src[j]) {
                dst[k++] = src[i++];
            } else {
                dst[k++] = src[j++];
            }
        }
        
        // Copy remaining elements
        while (i <= mid) {
            dst[k++] = src[i++];
        }
        while (j <= right) {
            dst[k++] = src[j++];
        }
    }
    
    private void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            
            while (j >= left && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
    
    /**
     * APPROACH 4: IN-PLACE MERGE SORT (Advanced)
     * Time Complexity: O(n log² n)
     * Space Complexity: O(log n) for recursion stack
     * 
     * Attempts to sort in-place but has worse time complexity.
     */
    public void mergeSortInPlace(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        mergeSortInPlaceHelper(arr, 0, arr.length - 1);
    }
    
    private void mergeSortInPlaceHelper(int[] arr, int left, int right) {
        if (left >= right) {
            return;
        }
        
        int mid = left + (right - left) / 2;
        
        mergeSortInPlaceHelper(arr, left, mid);
        mergeSortInPlaceHelper(arr, mid + 1, right);
        
        mergeInPlace(arr, left, mid, right);
    }
    
    private void mergeInPlace(int[] arr, int left, int mid, int right) {
        int start2 = mid + 1;
        
        // If already sorted
        if (arr[mid] <= arr[start2]) {
            return;
        }
        
        // Merge process
        while (left <= mid && start2 <= right) {
            if (arr[left] <= arr[start2]) {
                left++;
            } else {
                int value = arr[start2];
                int index = start2;
                
                // Shift elements to make room
                while (index != left) {
                    arr[index] = arr[index - 1];
                    index--;
                }
                arr[left] = value;
                
                left++;
                mid++;
                start2++;
            }
        }
    }
    
    /**
     * APPROACH 5: PARALLEL MERGE SORT (Multi-threaded)
     * Using ForkJoinPool for parallel processing
     */
    public void mergeSortParallel(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        java.util.concurrent.ForkJoinPool pool = new java.util.concurrent.ForkJoinPool();
        pool.invoke(new MergeSortTask(arr, new int[arr.length], 0, arr.length - 1));
        pool.shutdown();
    }
    
    private class MergeSortTask extends java.util.concurrent.RecursiveAction {
        private int[] arr;
        private int[] temp;
        private int left, right;
        private static final int THRESHOLD = 1000; // Switch to sequential below this size
        
        public MergeSortTask(int[] arr, int[] temp, int left, int right) {
            this.arr = arr;
            this.temp = temp;
            this.left = left;
            this.right = right;
        }
        
        @Override
        protected void compute() {
            if (left >= right) {
                return;
            }
            
            if (right - left < THRESHOLD) {
                // Use sequential merge sort for small arrays
                mergeSortHelper(arr, temp, left, right);
                return;
            }
            
            int mid = left + (right - left) / 2;
            
            // Fork tasks for both halves
            MergeSortTask leftTask = new MergeSortTask(arr, temp, left, mid);
            MergeSortTask rightTask = new MergeSortTask(arr, temp, mid + 1, right);
            
            invokeAll(leftTask, rightTask);
            
            // Merge results
            merge(arr, temp, left, mid, right);
        }
    }
    
    /**
     * Utility method to check if array is sorted
     */
    public boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Utility method to print array
     */
    public void printArray(int[] arr, String label) {
        System.out.print(label + ": ");
        for (int i = 0; i < Math.min(arr.length, 20); i++) {
            System.out.print(arr[i] + " ");
        }
        if (arr.length > 20) {
            System.out.print("...");
        }
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        MergeSort sorter = new MergeSort();
        
        // Test Case 1: Random array
        System.out.println("=== Test Case 1: Random Array ===");
        int[] arr1 = {64, 34, 25, 12, 22, 11, 90, 88, 76, 50, 42};
        sorter.printArray(arr1, "Original");
        
        int[] copy1 = arr1.clone();
        sorter.mergeSortClassic(copy1);
        sorter.printArray(copy1, "Classic MergeSort");
        System.out.println("Is sorted: " + sorter.isSorted(copy1));
        System.out.println();
        
        // Test Case 2: Already sorted
        System.out.println("=== Test Case 2: Already Sorted ===");
        int[] arr2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        sorter.printArray(arr2, "Original");
        
        int[] copy2 = arr2.clone();
        sorter.mergeSortOptimized(copy2);
        sorter.printArray(copy2, "Optimized MergeSort");
        System.out.println();
        
        // Test Case 3: Reverse sorted
        System.out.println("=== Test Case 3: Reverse Sorted ===");
        int[] arr3 = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        sorter.printArray(arr3, "Original");
        
        int[] copy3 = arr3.clone();
        sorter.mergeSortBottomUp(copy3);
        sorter.printArray(copy3, "Bottom-Up MergeSort");
        System.out.println();
        
        // Test Case 4: Array with duplicates
        System.out.println("=== Test Case 4: Array with Duplicates ===");
        int[] arr4 = {5, 2, 8, 2, 9, 1, 5, 5, 8};
        sorter.printArray(arr4, "Original");
        
        int[] copy4 = arr4.clone();
        sorter.mergeSortInPlace(copy4);
        sorter.printArray(copy4, "In-Place MergeSort");
        System.out.println();
        
        // Performance test
        performanceTest(sorter);
    }
    
    private static void performanceTest(MergeSort sorter) {
        System.out.println("=== Performance Test ===");
        
        int[] sizes = {1000, 10000, 100000};
        
        for (int size : sizes) {
            System.out.println("\nArray size: " + size);
            
            // Generate random array
            int[] original = new int[size];
            Random rand = new Random(42); // Fixed seed for reproducibility
            for (int i = 0; i < size; i++) {
                original[i] = rand.nextInt(size);
            }
            
            // Test different approaches
            long startTime, endTime;
            
            // Classic Merge Sort
            int[] arr1 = original.clone();
            startTime = System.nanoTime();
            sorter.mergeSortClassic(arr1);
            endTime = System.nanoTime();
            System.out.println("Classic: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Bottom-Up Merge Sort
            int[] arr2 = original.clone();
            startTime = System.nanoTime();
            sorter.mergeSortBottomUp(arr2);
            endTime = System.nanoTime();
            System.out.println("Bottom-Up: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Optimized Merge Sort
            int[] arr3 = original.clone();
            startTime = System.nanoTime();
            sorter.mergeSortOptimized(arr3);
            endTime = System.nanoTime();
            System.out.println("Optimized: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Parallel Merge Sort (for larger arrays)
            if (size >= 10000) {
                int[] arr4 = original.clone();
                startTime = System.nanoTime();
                sorter.mergeSortParallel(arr4);
                endTime = System.nanoTime();
                System.out.println("Parallel: " + (endTime - startTime) / 1_000_000.0 + " ms");
            }
            
            // Verify all arrays are sorted
            System.out.println("All sorted correctly: " + 
                             (sorter.isSorted(arr1) && sorter.isSorted(arr2) && sorter.isSorted(arr3)));
        }
    }
} 