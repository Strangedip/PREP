# Quick Sort

## Problem Statement
Implement the Quick Sort algorithm to sort an array in ascending order. Quick Sort is a divide-and-conquer algorithm that works by selecting a 'pivot' element and partitioning the array around this pivot.

## Example
```
Input: [3,6,8,10,1,2,1]
Output: [1,1,2,3,6,8,10]

Input: [5,2,3,1]
Output: [1,2,3,5]
```

## Algorithm Steps:
1. **Choose a pivot** element from the array
2. **Partition** the array so elements smaller than pivot are on left, larger on right
3. **Recursively apply** Quick Sort to left and right subarrays
4. **Base case:** Array with 0 or 1 element is already sorted

## Approach: Lomuto Partition Scheme

### Key Logic:
```java
public void quickSort(int[] nums) {
    quickSort(nums, 0, nums.length - 1);
}

private void quickSort(int[] nums, int low, int high) {
    if (low < high) {
        // Partition and get pivot index
        int pivotIndex = partition(nums, low, high);
        
        // Recursively sort left and right subarrays
        quickSort(nums, low, pivotIndex - 1);
        quickSort(nums, pivotIndex + 1, high);
    }
}

private int partition(int[] nums, int low, int high) {
    // Choose last element as pivot
    int pivot = nums[high];
    int i = low - 1; // Index of smaller element
    
    for (int j = low; j < high; j++) {
        // If current element is smaller than or equal to pivot
        if (nums[j] <= pivot) {
            i++;
            swap(nums, i, j);
        }
    }
    
    // Place pivot in correct position
    swap(nums, i + 1, high);
    return i + 1;
}

private void swap(int[] nums, int i, int j) {
    int temp = nums[i];
    nums[i] = nums[j];
    nums[j] = temp;
}
```

### Time & Space Complexity:
- **Average Time:** O(n log n) - Good pivot selection
- **Worst Time:** O(n²) - Poor pivot selection (already sorted)
- **Best Time:** O(n log n) - Pivot always divides array in half
- **Space:** O(log n) - Recursion stack (average case)

## Alternative: Hoare Partition Scheme

### Key Logic:
```java
private int hoarePartition(int[] nums, int low, int high) {
    int pivot = nums[low]; // First element as pivot
    int i = low - 1;
    int j = high + 1;
    
    while (true) {
        // Find element on left that should be on right
        do {
            i++;
        } while (nums[i] < pivot);
        
        // Find element on right that should be on left
        do {
            j--;
        } while (nums[j] > pivot);
        
        if (i >= j) {
            return j;
        }
        
        swap(nums, i, j);
    }
}
```

## Pivot Selection Strategies:

### 1. First Element:
```java
int pivot = nums[low];
```

### 2. Last Element (Lomuto):
```java
int pivot = nums[high];
```

### 3. Random Element:
```java
int randomIndex = low + new Random().nextInt(high - low + 1);
swap(nums, randomIndex, high);
int pivot = nums[high];
```

### 4. Median-of-Three:
```java
int mid = low + (high - low) / 2;
if (nums[mid] < nums[low]) swap(nums, low, mid);
if (nums[high] < nums[low]) swap(nums, low, high);
if (nums[high] < nums[mid]) swap(nums, mid, high);
swap(nums, mid, high); // Move median to end
int pivot = nums[high];
```

## Step-by-Step Example:
```
Array: [3,6,8,10,1,2,1], pivot = 1

Partition: [1,1,2] | 1 | [3,6,8,10]
           ↓              ↓
    Sort recursively   Sort recursively

Left subarray [1,1,2]: already sorted
Right subarray [3,6,8,10]: pivot = 10
Partition: [3,6,8] | 10 | []

Continue until all subarrays are sorted...
```

## Optimizations:

### 1. Tail Recursion Elimination:
```java
while (low < high) {
    int pivotIndex = partition(nums, low, high);
    
    // Recurse on smaller subarray, iterate on larger
    if (pivotIndex - low < high - pivotIndex) {
        quickSort(nums, low, pivotIndex - 1);
        low = pivotIndex + 1;
    } else {
        quickSort(nums, pivotIndex + 1, high);
        high = pivotIndex - 1;
    }
}
```

### 2. Hybrid with Insertion Sort:
```java
if (high - low + 1 < 10) {
    insertionSort(nums, low, high);
    return;
}
```

### 3. Three-Way Partitioning (Duplicates):
```java
// Handle arrays with many duplicate elements
// Partition into: [< pivot] [= pivot] [> pivot]
```

## Quick Select Algorithm:

### Find Kth Smallest Element:
```java
public int quickSelect(int[] nums, int k) {
    return quickSelect(nums, 0, nums.length - 1, k - 1);
}

private int quickSelect(int[] nums, int low, int high, int k) {
    if (low == high) return nums[low];
    
    int pivotIndex = partition(nums, low, high);
    
    if (k == pivotIndex) {
        return nums[k];
    } else if (k < pivotIndex) {
        return quickSelect(nums, low, pivotIndex - 1, k);
    } else {
        return quickSelect(nums, pivotIndex + 1, high, k);
    }
}
```

## When to Use Quick Sort:
1. **General-purpose sorting** (good average case)
2. **In-place sorting** required (low memory)
3. **Large datasets** where average case matters
4. **Not stable sorting** required

## Comparison with Other Sorts:
- **vs Merge Sort:** In-place but not stable, worse worst-case
- **vs Heap Sort:** Better average case, simpler implementation
- **vs Insertion Sort:** Much faster for large arrays

## LeetCode Similar Problems:
- [912. Sort an Array](https://leetcode.com/problems/sort-an-array/)
- [215. Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/)
- [973. K Closest Points to Origin](https://leetcode.com/problems/k-closest-points-to-origin/)
- [75. Sort Colors](https://leetcode.com/problems/sort-colors/)

## Interview Tips:
- Understand both Lomuto and Hoare partition schemes
- Explain time complexity analysis (average vs worst case)
- Discuss pivot selection strategies
- Know Quick Select for finding Kth element
- Compare with other sorting algorithms 