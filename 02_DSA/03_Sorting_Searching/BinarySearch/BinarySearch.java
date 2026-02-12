import java.util.*;

/**
 * Binary Search Implementation
 * 
 * Binary Search is a search algorithm that finds the position of a target value 
 * within a sorted array by repeatedly dividing the search interval in half.
 * 
 * Time Complexity: O(log n)
 * Space Complexity: O(1) iterative, O(log n) recursive
 * 
 * Prerequisites: Array must be sorted
 */
public class BinarySearch {
    
    /**
     * APPROACH 1: ITERATIVE BINARY SEARCH
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Standard iterative implementation.
     */
    public int binarySearchIterative(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2; // Prevent overflow
            
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return -1; // Target not found
    }
    
    /**
     * APPROACH 2: RECURSIVE BINARY SEARCH
     * Time Complexity: O(log n)
     * Space Complexity: O(log n) for recursion stack
     * 
     * Recursive implementation for educational purposes.
     */
    public int binarySearchRecursive(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        return binarySearchRecursiveHelper(arr, target, 0, arr.length - 1);
    }
    
    private int binarySearchRecursiveHelper(int[] arr, int target, int left, int right) {
        if (left > right) {
            return -1;
        }
        
        int mid = left + (right - left) / 2;
        
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] < target) {
            return binarySearchRecursiveHelper(arr, target, mid + 1, right);
        } else {
            return binarySearchRecursiveHelper(arr, target, left, mid - 1);
        }
    }
    
    /**
     * APPROACH 3: FIND FIRST OCCURRENCE
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Finds the leftmost (first) occurrence of target in sorted array with duplicates.
     */
    public int findFirstOccurrence(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        
        int left = 0;
        int right = arr.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                result = mid;
                right = mid - 1; // Continue searching in left half
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 4: FIND LAST OCCURRENCE
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Finds the rightmost (last) occurrence of target in sorted array with duplicates.
     */
    public int findLastOccurrence(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        
        int left = 0;
        int right = arr.length - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                result = mid;
                left = mid + 1; // Continue searching in right half
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 5: LOWER BOUND (LEFTMOST INSERTION POINT)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Finds the leftmost position where target can be inserted to maintain sorted order.
     */
    public int lowerBound(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        
        int left = 0;
        int right = arr.length;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return left;
    }
    
    /**
     * APPROACH 6: UPPER BOUND (RIGHTMOST INSERTION POINT)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Finds the rightmost position where target can be inserted to maintain sorted order.
     */
    public int upperBound(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        
        int left = 0;
        int right = arr.length;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        
        return left;
    }
    
    /**
     * APPROACH 7: SEARCH IN ROTATED SORTED ARRAY
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Binary search in a rotated sorted array.
     */
    public int searchRotated(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        
        int left = 0;
        int right = arr.length - 1;
        
        while (left <= right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] == target) {
                return mid;
            }
            
            // Determine which half is sorted
            if (arr[left] <= arr[mid]) {
                // Left half is sorted
                if (target >= arr[left] && target < arr[mid]) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            } else {
                // Right half is sorted
                if (target > arr[mid] && target <= arr[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * APPROACH 8: FIND PEAK ELEMENT
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Finds a peak element in the array (element greater than its neighbors).
     */
    public int findPeakElement(int[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        
        if (arr.length == 1) {
            return 0;
        }
        
        int left = 0;
        int right = arr.length - 1;
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (arr[mid] > arr[mid + 1]) {
                // Peak is in left half (including mid)
                right = mid;
            } else {
                // Peak is in right half
                left = mid + 1;
            }
        }
        
        return left;
    }
    
    /**
     * APPROACH 9: SQUARE ROOT USING BINARY SEARCH
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Finds integer square root using binary search.
     */
    public int sqrtBinarySearch(int x) {
        if (x < 2) {
            return x;
        }
        
        long left = 2;
        long right = x / 2;
        
        while (left <= right) {
            long mid = left + (right - left) / 2;
            long square = mid * mid;
            
            if (square == x) {
                return (int) mid;
            } else if (square < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return (int) right;
    }
    
    /**
     * APPROACH 10: BINARY SEARCH ON ANSWER
     * Time Complexity: O(log(max-min) * O(check))
     * Space Complexity: O(1)
     * 
     * Template for binary search on answer pattern.
     */
    public int binarySearchOnAnswer(int[] arr, int target) {
        // Example: Find minimum capacity to ship packages within D days
        int left = Arrays.stream(arr).max().orElse(0); // Minimum possible answer
        int right = Arrays.stream(arr).sum(); // Maximum possible answer
        
        while (left < right) {
            int mid = left + (right - left) / 2;
            
            if (canShipWithCapacity(arr, mid, target)) {
                right = mid; // Try to find smaller capacity
            } else {
                left = mid + 1; // Need larger capacity
            }
        }
        
        return left;
    }
    
    private boolean canShipWithCapacity(int[] weights, int capacity, int days) {
        int currentWeight = 0;
        int daysNeeded = 1;
        
        for (int weight : weights) {
            if (currentWeight + weight > capacity) {
                daysNeeded++;
                currentWeight = weight;
            } else {
                currentWeight += weight;
            }
        }
        
        return daysNeeded <= days;
    }
    
    /**
     * APPROACH 11: TERNARY SEARCH (for unimodal functions)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     * 
     * Finds maximum of a unimodal function.
     */
    public double ternarySearch(double left, double right, int iterations) {
        // Example: Find maximum of f(x) = -(x-2)^2 + 4
        for (int i = 0; i < iterations; i++) {
            double m1 = left + (right - left) / 3;
            double m2 = right - (right - left) / 3;
            
            if (unimodalFunction(m1) < unimodalFunction(m2)) {
                left = m1;
            } else {
                right = m2;
            }
        }
        
        return (left + right) / 2;
    }
    
    private double unimodalFunction(double x) {
        return -(x - 2) * (x - 2) + 4; // f(x) = -(x-2)^2 + 4
    }
    
    /**
     * Utility method to print search results
     */
    public void printSearchResult(String method, int[] arr, int target, int result) {
        System.out.println(method + ":");
        System.out.print("Array: ");
        for (int i = 0; i < Math.min(arr.length, 15); i++) {
            System.out.print(arr[i] + " ");
        }
        if (arr.length > 15) System.out.print("...");
        System.out.println("\nTarget: " + target + ", Result: " + 
                          (result != -1 ? "Found at index " + result : "Not found"));
        System.out.println();
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        BinarySearch searcher = new BinarySearch();
        
        // Test Case 1: Standard binary search
        System.out.println("=== Test Case 1: Standard Binary Search ===");
        int[] arr1 = {1, 3, 5, 7, 9, 11, 13, 15, 17, 19};
        searcher.printSearchResult("Iterative", arr1, 7, 
                                 searcher.binarySearchIterative(arr1, 7));
        searcher.printSearchResult("Recursive", arr1, 7, 
                                 searcher.binarySearchRecursive(arr1, 7));
        
        // Test Case 2: Array with duplicates
        System.out.println("=== Test Case 2: Array with Duplicates ===");
        int[] arr2 = {1, 2, 2, 2, 3, 4, 4, 5, 5, 5, 5, 6};
        searcher.printSearchResult("First Occurrence", arr2, 5, 
                                 searcher.findFirstOccurrence(arr2, 5));
        searcher.printSearchResult("Last Occurrence", arr2, 5, 
                                 searcher.findLastOccurrence(arr2, 5));
        
        // Test Case 3: Bounds
        System.out.println("=== Test Case 3: Lower and Upper Bounds ===");
        int[] arr3 = {1, 3, 5, 5, 5, 7, 9};
        System.out.println("Array: " + Arrays.toString(arr3));
        System.out.println("Lower bound of 5: " + searcher.lowerBound(arr3, 5));
        System.out.println("Upper bound of 5: " + searcher.upperBound(arr3, 5));
        System.out.println("Lower bound of 6: " + searcher.lowerBound(arr3, 6));
        System.out.println();
        
        // Test Case 4: Rotated array
        System.out.println("=== Test Case 4: Rotated Sorted Array ===");
        int[] arr4 = {4, 5, 6, 7, 0, 1, 2};
        searcher.printSearchResult("Search Rotated", arr4, 0, 
                                 searcher.searchRotated(arr4, 0));
        
        // Test Case 5: Peak element
        System.out.println("=== Test Case 5: Peak Element ===");
        int[] arr5 = {1, 2, 3, 1};
        System.out.println("Array: " + Arrays.toString(arr5));
        System.out.println("Peak element at index: " + searcher.findPeakElement(arr5));
        System.out.println();
        
        // Test Case 6: Square root
        System.out.println("=== Test Case 6: Square Root ===");
        int x = 8;
        System.out.println("Square root of " + x + ": " + searcher.sqrtBinarySearch(x));
        System.out.println();
        
        // Test Case 7: Ternary search
        System.out.println("=== Test Case 7: Ternary Search ===");
        double max = searcher.ternarySearch(-10, 10, 100);
        System.out.println("Maximum of f(x) = -(x-2)^2 + 4 at x ≈ " + max);
        System.out.println();
        
        // Performance test
        performanceTest(searcher);
    }
    
    private static void performanceTest(BinarySearch searcher) {
        System.out.println("=== Performance Test ===");
        
        int[] sizes = {1000, 100000, 10000000};
        
        for (int size : sizes) {
            // Create sorted array
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = i * 2; // Even numbers
            }
            
            int target = size; // Target in middle
            long startTime, endTime;
            
            // Iterative binary search
            startTime = System.nanoTime();
            int result1 = searcher.binarySearchIterative(arr, target);
            endTime = System.nanoTime();
            
            // Linear search comparison
            startTime = System.nanoTime();
            int result2 = linearSearch(arr, target);
            endTime = System.nanoTime();
            
            System.out.println("Array size: " + size);
            System.out.println("Binary search: O(log n) - Found at index " + result1);
            System.out.println("Linear search: O(n) - Found at index " + result2);
            System.out.println("Binary search advantage: ~" + (size / (int)(Math.log(size) / Math.log(2))) + "x faster");
            System.out.println();
        }
    }
    
    private static int linearSearch(int[] arr, int target) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return i;
            }
        }
        return -1;
    }
} 