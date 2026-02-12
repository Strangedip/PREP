# Maximum Subarray — Divide and Conquer

## Problem Statement

Find the contiguous subarray within an array that has the largest sum. Solve it using the Divide and Conquer approach (as a contrast to Kadane's Algorithm which uses DP).

**Note**: This problem is also solvable via Kadane's Algorithm in O(n). The Divide and Conquer approach is O(n log n) but demonstrates a fundamental algorithmic paradigm and is commonly asked in interviews to test understanding of divide and conquer.

## Example
```
Input: nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]
Output: 6
Explanation: [4, -1, 2, 1] has the largest sum = 6
```

## Approach: Divide and Conquer

### Key Insight
For any array, the maximum subarray either:
1. Lies entirely in the **left half**.
2. Lies entirely in the **right half**.
3. **Crosses the midpoint** (starts in the left half and ends in the right half).

We recursively solve cases 1 and 2, and compute case 3 directly.

### Algorithm
1. **Divide**: Split the array at the midpoint.
2. **Conquer**: Recursively find the max subarray in the left and right halves.
3. **Combine**: Find the max subarray crossing the midpoint (must include the mid element and extend in both directions).
4. Return the maximum of all three cases.

### Finding the Crossing Subarray
Starting from `mid`, extend left as far as possible to maximize the left sum. Then extend right as far as possible to maximize the right sum. The crossing sum is `leftMax + rightMax`.

```java
public int maxSubArray(int[] nums) {
    return divideAndConquer(nums, 0, nums.length - 1);
}

private int divideAndConquer(int[] nums, int left, int right) {
    // Base case: single element
    if (left == right) {
        return nums[left];
    }
    
    int mid = left + (right - left) / 2;
    
    // Case 1: Max subarray in left half
    int leftMax = divideAndConquer(nums, left, mid);
    
    // Case 2: Max subarray in right half
    int rightMax = divideAndConquer(nums, mid + 1, right);
    
    // Case 3: Max subarray crossing the midpoint
    int crossMax = maxCrossingSum(nums, left, mid, right);
    
    return Math.max(Math.max(leftMax, rightMax), crossMax);
}

private int maxCrossingSum(int[] nums, int left, int mid, int right) {
    // Extend from mid to the left
    int leftSum = Integer.MIN_VALUE;
    int sum = 0;
    for (int i = mid; i >= left; i--) {
        sum += nums[i];
        leftSum = Math.max(leftSum, sum);
    }
    
    // Extend from mid+1 to the right
    int rightSum = Integer.MIN_VALUE;
    sum = 0;
    for (int i = mid + 1; i <= right; i++) {
        sum += nums[i];
        rightSum = Math.max(rightSum, sum);
    }
    
    return leftSum + rightSum;
}
```

### Walkthrough
```
nums = [-2, 1, -3, 4, -1, 2, 1, -5, 4]

Split at mid=4 (value -1):
  Left:  [-2, 1, -3, 4, -1]
  Right: [2, 1, -5, 4]

Left half: recursively splits further...
  Eventually: leftMax = 4 (just the element 4)

Right half: recursively splits further...
  Eventually: rightMax = 4 (just the element 4)

Crossing: extend left from mid=4: -1+4=3, -1+4-3=0, -1+4-3+1=1, -1+4-3+1-2=0 → max=3
          extend right from mid+1=5: 2, 2+1=3, 2+1-5=-2, 2+1-5+4=2 → max=3
          crossMax = 3 + 3 = 6

Result: max(4, 4, 6) = 6 ✓
```

### Recurrence Relation
```
T(n) = 2T(n/2) + O(n)
```
By the Master Theorem (Case 2): `a=2, b=2, f(n)=n, log_b(a)=1`
→ `f(n) = Θ(n^1) = Θ(n^log_b(a))` → **T(n) = Θ(n log n)**

**Time**: O(n log n)
**Space**: O(log n) — recursion stack depth.

## Comparison with Kadane's Algorithm

| Approach | Time | Space | Paradigm |
|----------|------|-------|----------|
| Kadane's Algorithm | O(n) | O(1) | Dynamic Programming |
| Divide and Conquer | O(n log n) | O(log n) | Divide and Conquer |

Kadane's is faster, but the D&C approach is important because:
1. It demonstrates the divide and conquer paradigm clearly.
2. It can be extended to parallel computation (left and right halves computed independently).
3. Interviewers may specifically ask for the D&C solution.

## Interview Tips

1. **Know both approaches**: Always mention Kadane's O(n) as the optimal, but be ready to implement D&C if asked.
2. **Explain the three cases**: "The max subarray is either entirely in the left half, entirely in the right half, or crosses the midpoint."
3. **Apply the Master Theorem**: Show you understand the recurrence `T(n) = 2T(n/2) + O(n)` → O(n log n).
4. **Follow-up**: "Can you parallelize this?" → Yes, the left and right recursive calls are independent and can run in parallel. The crossing computation requires the full array but is O(n).

## Related Problems
- Maximum Subarray (Kadane's — DP approach)
- Count of Smaller Numbers After Self (merge sort D&C)
- Closest Pair of Points (classic D&C)
- Inversion Count (merge sort D&C)

