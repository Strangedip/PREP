# Binary Search

## Problem Statement
Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums. If target exists, return its index. Otherwise, return -1.

## Example
```
Input: nums = [-1,0,3,5,9,12], target = 9
Output: 4

Input: nums = [-1,0,3,5,9,12], target = 2
Output: -1
```

## Approach: Classic Binary Search

### How it works:
1. **Maintain search range** with left and right pointers
2. **Calculate middle** and compare with target
3. **Eliminate half** of search space each iteration
4. **Continue until** target found or range exhausted

### Key Logic:
```java
public int search(int[] nums, int target) {
    int left = 0;
    int right = nums.length - 1;
    
    while (left <= right) {
        int mid = left + (right - left) / 2; // Avoid overflow
        
        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            left = mid + 1; // Search right half
        } else {
            right = mid - 1; // Search left half
        }
    }
    
    return -1; // Target not found
}
```

### Time & Space Complexity:
- **Time:** O(log n) - Halve search space each iteration
- **Space:** O(1) - Only using pointers

## Template Variations:

### Template 1: Exact Match (Basic)
```java
while (left <= right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] == target) return mid;
    else if (nums[mid] < target) left = mid + 1;
    else right = mid - 1;
}
return -1;
```

### Template 2: Find Left Boundary
```java
while (left < right) {
    int mid = left + (right - left) / 2;
    if (nums[mid] < target) {
        left = mid + 1;
    } else {
        right = mid;
    }
}
return left;
```

### Template 3: Find Right Boundary
```java
while (left < right) {
    int mid = left + (right - left + 1) / 2; // Avoid infinite loop
    if (nums[mid] > target) {
        right = mid - 1;
    } else {
        left = mid;
    }
}
return left;
```

## Common Pitfalls:

### 1. Integer Overflow:
```java
// WRONG: Can overflow for large values
int mid = (left + right) / 2;

// CORRECT: Prevents overflow
int mid = left + (right - left) / 2;
```

### 2. Infinite Loops:
```java
// For finding right boundary, use:
int mid = left + (right - left + 1) / 2;
// This ensures mid > left when left + 1 == right
```

### 3. Off-by-One Errors:
```java
// Check termination conditions carefully
while (left <= right) // vs while (left < right)
left = mid + 1       // vs left = mid
right = mid - 1      // vs right = mid
```

## Binary Search Pattern Applications:

### 1. Find First/Last Occurrence:
- **Modify comparison** to find boundaries
- **Continue searching** even after finding target

### 2. Search in Rotated Array:
- **Determine which half is sorted**
- **Apply binary search** on sorted half

### 3. Find Peak Element:
- **Compare with neighbors**
- **Move toward larger neighbor**

### 4. Search 2D Matrix:
- **Treat as 1D array** with coordinate conversion
- **Or search row then column**

## Step-by-Step Example:
```
nums = [-1,0,3,5,9,12], target = 9

Initial: left=0, right=5
mid = 0 + (5-0)/2 = 2, nums[2]=3
3 < 9, so left = mid+1 = 3

Iteration 2: left=3, right=5
mid = 3 + (5-3)/2 = 4, nums[4]=9
9 == 9, return 4
```

## Edge Cases:
1. **Empty array** → Return -1
2. **Single element** → Check if matches target
3. **Target smaller than all elements**
4. **Target larger than all elements**
5. **Duplicate elements** → Specify which index to return

## When to Use Binary Search:
1. **Sorted array** (or rotated sorted)
2. **Monotonic function** (can determine search direction)
3. **Search space can be eliminated** systematically
4. **Need O(log n) time** complexity

## LeetCode Similar Problems:
- [704. Binary Search](https://leetcode.com/problems/binary-search/) (this problem)
- [34. Find First and Last Position of Element in Sorted Array](https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/)
- [33. Search in Rotated Sorted Array](https://leetcode.com/problems/search-in-rotated-sorted-array/)
- [162. Find Peak Element](https://leetcode.com/problems/find-peak-element/)
- [74. Search a 2D Matrix](https://leetcode.com/problems/search-a-2d-matrix/)

## Interview Tips:
- Master the basic template first
- Handle edge cases carefully
- Understand when to use <= vs < in loop condition
- Practice with different binary search variants
- This is a fundamental algorithm used in many advanced problems 