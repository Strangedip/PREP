import java.util.*;

/**
 * Quick Sort Implementation
 * 
 * QuickSort is a divide-and-conquer algorithm that picks an element as pivot
 * and partitions the array around the pivot.
 * 
 * Time Complexity: 
 * - Best/Average: O(n log n)
 * - Worst: O(n²)
 * Space Complexity: O(log n) for recursion stack
 * 
 * Characteristics:
 * - Not stable (can change relative order of equal elements)
 * - In-place sorting algorithm
 * - Cache-efficient
 * - Often faster than other O(n log n) algorithms in practice
 */
public class QuickSort {
    
    /**
     * APPROACH 1: LOMUTO PARTITION SCHEME
     * Time Complexity: O(n log n) average, O(n²) worst
     * Space Complexity: O(log n) for recursion stack
     * 
     * Uses last element as pivot and maintains two regions.
     */
    public void quickSortLomuto(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortLomutoHelper(arr, 0, arr.length - 1);
    }
    
    private void quickSortLomutoHelper(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = lomutoPartition(arr, low, high);
            
            quickSortLomutoHelper(arr, low, pivotIndex - 1);
            quickSortLomutoHelper(arr, pivotIndex + 1, high);
        }
    }
    
    private int lomutoPartition(int[] arr, int low, int high) {
        int pivot = arr[high]; // Choose last element as pivot
        int i = low - 1; // Index of smaller element
        
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        
        swap(arr, i + 1, high);
        return i + 1;
    }
    
    /**
     * APPROACH 2: HOARE PARTITION SCHEME
     * Time Complexity: O(n log n) average, O(n²) worst
     * Space Complexity: O(log n) for recursion stack
     * 
     * Original partition scheme by Tony Hoare. More efficient than Lomuto.
     */
    public void quickSortHoare(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortHoareHelper(arr, 0, arr.length - 1);
    }
    
    private void quickSortHoareHelper(int[] arr, int low, int high) {
        if (low < high) {
            int pivotIndex = hoarePartition(arr, low, high);
            
            quickSortHoareHelper(arr, low, pivotIndex);
            quickSortHoareHelper(arr, pivotIndex + 1, high);
        }
    }
    
    private int hoarePartition(int[] arr, int low, int high) {
        int pivot = arr[low]; // Choose first element as pivot
        int i = low - 1;
        int j = high + 1;
        
        while (true) {
            // Find element greater than or equal to pivot from left
            do {
                i++;
            } while (arr[i] < pivot);
            
            // Find element smaller than or equal to pivot from right
            do {
                j--;
            } while (arr[j] > pivot);
            
            if (i >= j) {
                return j;
            }
            
            swap(arr, i, j);
        }
    }
    
    /**
     * APPROACH 3: RANDOMIZED QUICKSORT
     * Time Complexity: O(n log n) expected
     * Space Complexity: O(log n) for recursion stack
     * 
     * Chooses random pivot to avoid worst-case behavior on sorted arrays.
     */
    public void quickSortRandomized(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortRandomizedHelper(arr, 0, arr.length - 1);
    }
    
    private void quickSortRandomizedHelper(int[] arr, int low, int high) {
        if (low < high) {
            // Randomly select pivot and swap with last element
            int randomIndex = low + new Random().nextInt(high - low + 1);
            swap(arr, randomIndex, high);
            
            int pivotIndex = lomutoPartition(arr, low, high);
            
            quickSortRandomizedHelper(arr, low, pivotIndex - 1);
            quickSortRandomizedHelper(arr, pivotIndex + 1, high);
        }
    }
    
    /**
     * APPROACH 4: THREE-WAY QUICKSORT (Dutch National Flag)
     * Time Complexity: O(n log n), O(n) for arrays with many duplicates
     * Space Complexity: O(log n) for recursion stack
     * 
     * Efficient for arrays with many duplicate elements.
     */
    public void quickSort3Way(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSort3WayHelper(arr, 0, arr.length - 1);
    }
    
    private void quickSort3WayHelper(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        
        int[] bounds = partition3Way(arr, low, high);
        int lt = bounds[0]; // Elements less than pivot
        int gt = bounds[1]; // Elements greater than pivot
        
        quickSort3WayHelper(arr, low, lt - 1);
        quickSort3WayHelper(arr, gt + 1, high);
    }
    
    private int[] partition3Way(int[] arr, int low, int high) {
        int pivot = arr[low];
        int lt = low;      // arr[low..lt-1] < pivot
        int i = low + 1;   // arr[lt..i-1] == pivot
        int gt = high;     // arr[gt+1..high] > pivot
        
        while (i <= gt) {
            if (arr[i] < pivot) {
                swap(arr, lt++, i++);
            } else if (arr[i] > pivot) {
                swap(arr, i, gt--);
            } else {
                i++;
            }
        }
        
        return new int[]{lt, gt};
    }
    
    /**
     * APPROACH 5: ITERATIVE QUICKSORT
     * Time Complexity: O(n log n) average, O(n²) worst
     * Space Complexity: O(log n) for explicit stack
     * 
     * Avoids recursion overhead using explicit stack.
     */
    public void quickSortIterative(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        
        Stack<Integer> stack = new Stack<>();
        stack.push(0);
        stack.push(arr.length - 1);
        
        while (!stack.isEmpty()) {
            int high = stack.pop();
            int low = stack.pop();
            
            if (low < high) {
                int pivotIndex = lomutoPartition(arr, low, high);
                
                // Push left subarray bounds
                stack.push(low);
                stack.push(pivotIndex - 1);
                
                // Push right subarray bounds
                stack.push(pivotIndex + 1);
                stack.push(high);
            }
        }
    }
    
    /**
     * APPROACH 6: OPTIMIZED QUICKSORT
     * Time Complexity: O(n log n) average
     * Space Complexity: O(log n)
     * 
     * Multiple optimizations:
     * - Median-of-three pivot selection
     * - Insertion sort for small subarrays
     * - Tail recursion optimization
     */
    public void quickSortOptimized(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        quickSortOptimizedHelper(arr, 0, arr.length - 1);
    }
    
    private void quickSortOptimizedHelper(int[] arr, int low, int high) {
        while (low < high) {
            // Use insertion sort for small subarrays
            if (high - low < 10) {
                insertionSort(arr, low, high);
                break;
            }
            
            // Median-of-three pivot selection
            int pivotIndex = medianOfThree(arr, low, high);
            swap(arr, pivotIndex, high);
            
            int partition = lomutoPartition(arr, low, high);
            
            // Recursively sort smaller partition, iterate on larger
            if (partition - low < high - partition) {
                quickSortOptimizedHelper(arr, low, partition - 1);
                low = partition + 1;
            } else {
                quickSortOptimizedHelper(arr, partition + 1, high);
                high = partition - 1;
            }
        }
    }
    
    private int medianOfThree(int[] arr, int low, int high) {
        int mid = low + (high - low) / 2;
        
        if (arr[mid] < arr[low]) {
            swap(arr, mid, low);
        }
        if (arr[high] < arr[low]) {
            swap(arr, high, low);
        }
        if (arr[high] < arr[mid]) {
            swap(arr, high, mid);
        }
        
        return mid;
    }
    
    private void insertionSort(int[] arr, int low, int high) {
        for (int i = low + 1; i <= high; i++) {
            int key = arr[i];
            int j = i - 1;
            
            while (j >= low && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
    
    /**
     * APPROACH 7: DUAL-PIVOT QUICKSORT
     * Time Complexity: O(n log n) average, better constant factors
     * Space Complexity: O(log n)
     * 
     * Uses two pivots to partition array into three parts.
     * Used in Java's Arrays.sort() for primitive types.
     */
    public void dualPivotQuickSort(int[] arr) {
        if (arr == null || arr.length <= 1) {
            return;
        }
        dualPivotQuickSortHelper(arr, 0, arr.length - 1);
    }
    
    private void dualPivotQuickSortHelper(int[] arr, int low, int high) {
        if (low >= high) {
            return;
        }
        
        // Ensure arr[low] <= arr[high]
        if (arr[low] > arr[high]) {
            swap(arr, low, high);
        }
        
        int pivot1 = arr[low];
        int pivot2 = arr[high];
        
        // Partition array into three parts
        int i = low + 1;
        int lt = low + 1;  // Elements < pivot1
        int gt = high - 1; // Elements > pivot2
        
        while (i <= gt) {
            if (arr[i] < pivot1) {
                swap(arr, i++, lt++);
            } else if (arr[i] > pivot2) {
                swap(arr, i, gt--);
            } else {
                i++;
            }
        }
        
        // Place pivots in correct positions
        swap(arr, low, --lt);
        swap(arr, high, ++gt);
        
        // Recursively sort three parts
        dualPivotQuickSortHelper(arr, low, lt - 1);
        dualPivotQuickSortHelper(arr, lt + 1, gt - 1);
        dualPivotQuickSortHelper(arr, gt + 1, high);
    }
    
    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
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
        QuickSort sorter = new QuickSort();
        
        // Test Case 1: Random array
        System.out.println("=== Test Case 1: Random Array ===");
        int[] arr1 = {64, 34, 25, 12, 22, 11, 90, 88, 76, 50, 42};
        sorter.printArray(arr1, "Original");
        
        int[] copy1 = arr1.clone();
        sorter.quickSortLomuto(copy1);
        sorter.printArray(copy1, "Lomuto QuickSort");
        System.out.println("Is sorted: " + sorter.isSorted(copy1));
        System.out.println();
        
        // Test Case 2: Already sorted (worst case for basic quicksort)
        System.out.println("=== Test Case 2: Already Sorted ===");
        int[] arr2 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        sorter.printArray(arr2, "Original");
        
        int[] copy2 = arr2.clone();
        sorter.quickSortRandomized(copy2);
        sorter.printArray(copy2, "Randomized QuickSort");
        System.out.println();
        
        // Test Case 3: Array with many duplicates
        System.out.println("=== Test Case 3: Many Duplicates ===");
        int[] arr3 = {5, 2, 5, 2, 5, 2, 5, 2, 5};
        sorter.printArray(arr3, "Original");
        
        int[] copy3 = arr3.clone();
        sorter.quickSort3Way(copy3);
        sorter.printArray(copy3, "3-Way QuickSort");
        System.out.println();
        
        // Test Case 4: Reverse sorted
        System.out.println("=== Test Case 4: Reverse Sorted ===");
        int[] arr4 = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        sorter.printArray(arr4, "Original");
        
        int[] copy4 = arr4.clone();
        sorter.quickSortOptimized(copy4);
        sorter.printArray(copy4, "Optimized QuickSort");
        System.out.println();
        
        // Test Case 5: Dual-pivot comparison
        System.out.println("=== Test Case 5: Dual-Pivot ===");
        int[] arr5 = {64, 34, 25, 12, 22, 11, 90, 88, 76, 50, 42, 77, 33, 55};
        sorter.printArray(arr5, "Original");
        
        int[] copy5 = arr5.clone();
        sorter.dualPivotQuickSort(copy5);
        sorter.printArray(copy5, "Dual-Pivot QuickSort");
        System.out.println();
        
        // Performance test
        performanceTest(sorter);
    }
    
    private static void performanceTest(QuickSort sorter) {
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
            
            // Lomuto QuickSort
            int[] arr1 = original.clone();
            startTime = System.nanoTime();
            sorter.quickSortLomuto(arr1);
            endTime = System.nanoTime();
            System.out.println("Lomuto: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Hoare QuickSort
            int[] arr2 = original.clone();
            startTime = System.nanoTime();
            sorter.quickSortHoare(arr2);
            endTime = System.nanoTime();
            System.out.println("Hoare: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Randomized QuickSort
            int[] arr3 = original.clone();
            startTime = System.nanoTime();
            sorter.quickSortRandomized(arr3);
            endTime = System.nanoTime();
            System.out.println("Randomized: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // 3-Way QuickSort
            int[] arr4 = original.clone();
            startTime = System.nanoTime();
            sorter.quickSort3Way(arr4);
            endTime = System.nanoTime();
            System.out.println("3-Way: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Optimized QuickSort
            int[] arr5 = original.clone();
            startTime = System.nanoTime();
            sorter.quickSortOptimized(arr5);
            endTime = System.nanoTime();
            System.out.println("Optimized: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Dual-Pivot QuickSort
            int[] arr6 = original.clone();
            startTime = System.nanoTime();
            sorter.dualPivotQuickSort(arr6);
            endTime = System.nanoTime();
            System.out.println("Dual-Pivot: " + (endTime - startTime) / 1_000_000.0 + " ms");
            
            // Verify all arrays are sorted
            System.out.println("All sorted correctly: " + 
                             (sorter.isSorted(arr1) && sorter.isSorted(arr2) && 
                              sorter.isSorted(arr3) && sorter.isSorted(arr4) && 
                              sorter.isSorted(arr5) && sorter.isSorted(arr6)));
        }
    }
} 