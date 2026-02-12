# Master Theorem & Divide and Conquer

## Problem Statement
Master Theorem provides a method for solving recurrence relations of the form:
**T(n) = aT(n/b) + f(n)**

Where:
- **a ≥ 1:** Number of subproblems
- **b > 1:** Factor by which problem size is reduced
- **f(n):** Cost of work done outside recursive calls

## Master Theorem Cases

### Case 1: f(n) = O(n^(log_b(a) - ε)) for some ε > 0
**Result:** T(n) = Θ(n^log_b(a))
**Intuition:** Recursive work dominates

### Case 2: f(n) = Θ(n^log_b(a))
**Result:** T(n) = Θ(n^log_b(a) * log n)
**Intuition:** Recursive work and f(n) are balanced

### Case 3: f(n) = Ω(n^(log_b(a) + ε)) for some ε > 0
**Result:** T(n) = Θ(f(n))
**Intuition:** f(n) dominates recursive work
**Additional condition:** af(n/b) ≤ cf(n) for some c < 1

## Classic Examples

### Example 1: Merge Sort
```java
// T(n) = 2T(n/2) + O(n)
// a = 2, b = 2, f(n) = n
// log_b(a) = log_2(2) = 1
// f(n) = n = Θ(n^1) = Θ(n^log_b(a))
// Case 2: T(n) = Θ(n log n)

public void mergeSort(int[] arr, int left, int right) {
    if (left < right) {
        int mid = left + (right - left) / 2;
        
        mergeSort(arr, left, mid);      // T(n/2)
        mergeSort(arr, mid + 1, right); // T(n/2)
        merge(arr, left, mid, right);   // O(n)
    }
}

private void merge(int[] arr, int left, int mid, int right) {
    int[] temp = new int[right - left + 1];
    int i = left, j = mid + 1, k = 0;
    
    while (i <= mid && j <= right) {
        if (arr[i] <= arr[j]) {
            temp[k++] = arr[i++];
        } else {
            temp[k++] = arr[j++];
        }
    }
    
    while (i <= mid) temp[k++] = arr[i++];
    while (j <= right) temp[k++] = arr[j++];
    
    System.arraycopy(temp, 0, arr, left, temp.length);
}
```

### Example 2: Binary Search
```java
// T(n) = T(n/2) + O(1)
// a = 1, b = 2, f(n) = 1
// log_b(a) = log_2(1) = 0
// f(n) = 1 = Θ(n^0) = Θ(n^log_b(a))
// Case 2: T(n) = Θ(log n)

public int binarySearch(int[] arr, int target, int left, int right) {
    if (left <= right) {
        int mid = left + (right - left) / 2;
        
        if (arr[mid] == target) {
            return mid;
        } else if (arr[mid] > target) {
            return binarySearch(arr, target, left, mid - 1);
        } else {
            return binarySearch(arr, target, mid + 1, right);
        }
    }
    return -1;
}
```

### Example 3: Strassen's Matrix Multiplication
```java
// T(n) = 7T(n/2) + O(n^2)
// a = 7, b = 2, f(n) = n^2
// log_b(a) = log_2(7) ≈ 2.807
// f(n) = n^2 = O(n^(2.807 - ε)) for small ε
// Case 1: T(n) = Θ(n^2.807)

// Note: Standard matrix multiplication is O(n^3)
// Strassen's algorithm improves this using clever subproblem decomposition
```

## Advanced Divide and Conquer Algorithms

### 1. Maximum Subarray (Divide & Conquer)
```java
public int maxSubarrayDC(int[] nums, int left, int right) {
    if (left == right) {
        return nums[left];
    }
    
    int mid = left + (right - left) / 2;
    
    // Maximum subarray in left half
    int leftMax = maxSubarrayDC(nums, left, mid);
    
    // Maximum subarray in right half
    int rightMax = maxSubarrayDC(nums, mid + 1, right);
    
    // Maximum subarray crossing the middle
    int leftSum = Integer.MIN_VALUE;
    int sum = 0;
    for (int i = mid; i >= left; i--) {
        sum += nums[i];
        leftSum = Math.max(leftSum, sum);
    }
    
    int rightSum = Integer.MIN_VALUE;
    sum = 0;
    for (int i = mid + 1; i <= right; i++) {
        sum += nums[i];
        rightSum = Math.max(rightSum, sum);
    }
    
    int crossMax = leftSum + rightSum;
    
    return Math.max(Math.max(leftMax, rightMax), crossMax);
}
```

### 2. Closest Pair of Points
```java
class Point {
    double x, y;
    Point(double x, double y) { this.x = x; this.y = y; }
}

public double closestPair(Point[] points) {
    Arrays.sort(points, (p1, p2) -> Double.compare(p1.x, p2.x));
    return closestPairRec(points, 0, points.length - 1);
}

private double closestPairRec(Point[] points, int left, int right) {
    if (right - left <= 3) {
        return bruteForce(points, left, right);
    }
    
    int mid = left + (right - left) / 2;
    Point midPoint = points[mid];
    
    double leftMin = closestPairRec(points, left, mid);
    double rightMin = closestPairRec(points, mid + 1, right);
    
    double d = Math.min(leftMin, rightMin);
    
    // Check points near the dividing line
    List<Point> strip = new ArrayList<>();
    for (int i = left; i <= right; i++) {
        if (Math.abs(points[i].x - midPoint.x) < d) {
            strip.add(points[i]);
        }
    }
    
    return Math.min(d, stripClosest(strip, d));
}

private double stripClosest(List<Point> strip, double d) {
    strip.sort((p1, p2) -> Double.compare(p1.y, p2.y));
    
    double min = d;
    for (int i = 0; i < strip.size(); i++) {
        for (int j = i + 1; j < strip.size() && 
             (strip.get(j).y - strip.get(i).y) < min; j++) {
            min = Math.min(min, distance(strip.get(i), strip.get(j)));
        }
    }
    return min;
}

private double distance(Point p1, Point p2) {
    return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + 
                     (p1.y - p2.y) * (p1.y - p2.y));
}
```

### 3. Inversion Count
```java
// Count number of inversions in array using divide and conquer
public int countInversions(int[] arr) {
    return mergeSortAndCount(arr, 0, arr.length - 1);
}

private int mergeSortAndCount(int[] arr, int left, int right) {
    int count = 0;
    if (left < right) {
        int mid = left + (right - left) / 2;
        
        count += mergeSortAndCount(arr, left, mid);
        count += mergeSortAndCount(arr, mid + 1, right);
        count += mergeAndCount(arr, left, mid, right);
    }
    return count;
}

private int mergeAndCount(int[] arr, int left, int mid, int right) {
    int[] leftArr = Arrays.copyOfRange(arr, left, mid + 1);
    int[] rightArr = Arrays.copyOfRange(arr, mid + 1, right + 1);
    
    int i = 0, j = 0, k = left, count = 0;
    
    while (i < leftArr.length && j < rightArr.length) {
        if (leftArr[i] <= rightArr[j]) {
            arr[k++] = leftArr[i++];
        } else {
            arr[k++] = rightArr[j++];
            count += (leftArr.length - i); // All remaining elements in left are inversions
        }
    }
    
    while (i < leftArr.length) arr[k++] = leftArr[i++];
    while (j < rightArr.length) arr[k++] = rightArr[j++];
    
    return count;
}
```

## Divide and Conquer Design Pattern

### 1. **Divide:** Break problem into smaller subproblems
### 2. **Conquer:** Solve subproblems recursively
### 3. **Combine:** Merge solutions to get final answer

### Template:
```java
public ResultType divideAndConquer(ProblemType problem) {
    // Base case
    if (problem.isSmallEnough()) {
        return solveDirect(problem);
    }
    
    // Divide
    List<ProblemType> subproblems = divide(problem);
    
    // Conquer
    List<ResultType> subresults = new ArrayList<>();
    for (ProblemType subproblem : subproblems) {
        subresults.add(divideAndConquer(subproblem));
    }
    
    // Combine
    return combine(subresults);
}
```

## Advanced Master Theorem Applications

### Karatsuba Multiplication
```java
// T(n) = 3T(n/2) + O(n)
// a = 3, b = 2, f(n) = n
// log_b(a) = log_2(3) ≈ 1.585
// f(n) = n = O(n^(1.585 - ε)) for small ε
// Case 1: T(n) = Θ(n^1.585)

public String karatsuba(String num1, String num2) {
    // Implementation of Karatsuba multiplication
    // Faster than traditional O(n^2) multiplication
}
```

### Fast Fourier Transform (FFT)
```java
// T(n) = 2T(n/2) + O(n)
// Case 2: T(n) = Θ(n log n)
// Used for polynomial multiplication
```

## When Master Theorem Doesn't Apply

### 1. Different problem sizes at each level
```java
// T(n) = T(n/3) + T(2n/3) + O(n)
// Use recursion tree method instead
```

### 2. Varying number of subproblems
```java
// T(n) = T(n-1) + T(n-2) + O(1) (Fibonacci)
// Use substitution method
```

### 3. Non-polynomial f(n)
```java
// T(n) = 2T(n/2) + O(n log n)
// Master theorem extended forms needed
```

## LeetCode Similar Problems:
- [215. Kth Largest Element in an Array](https://leetcode.com/problems/kth-largest-element-in-an-array/) (QuickSelect)
- [53. Maximum Subarray](https://leetcode.com/problems/maximum-subarray/) (Divide & Conquer)
- [148. Sort List](https://leetcode.com/problems/sort-list/) (Merge Sort on Linked List)
- [327. Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/) (Divide & Conquer)
- [493. Reverse Pairs](https://leetcode.com/problems/reverse-pairs/) (Modified Merge Sort)

## Interview Tips:
- **Memorize the three cases** of Master Theorem
- **Practice identifying** which case applies
- **Understand when D&C is better** than other approaches
- **Know classic algorithms** and their complexities
- **Essential for algorithm analysis** at senior level 