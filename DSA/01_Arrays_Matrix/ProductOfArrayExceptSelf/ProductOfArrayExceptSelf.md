# Product of Array Except Self

## Problem Statement

Given an integer array, return an array where each element is the product of all elements in the original array except the element at that position.

**Constraints:**
- Must run in O(n) time
- Cannot use division operator
- Product is guaranteed to fit in 32-bit integer

## Example
```
Input: nums = [1,2,3,4]
Output: [24,12,8,6]
Explanation:
- Position 0: 2×3×4 = 24
- Position 1: 1×3×4 = 12  
- Position 2: 1×2×4 = 8
- Position 3: 1×2×3 = 6
```

## Approach 1: Brute Force

### How it works:
For each position, multiply all other elements.

### Code Logic:
```java
for (int i = 0; i < n; i++) {
    int product = 1;
    for (int j = 0; j < n; j++) {
        if (i != j) {
            product *= nums[j];
        }
    }
    result[i] = product;
}
```

### Complexity:
- **Time:** O(n²) - For each element, iterate through all others
- **Space:** O(1) - Not counting output array

## Approach 2: Using Division (Not Allowed)

### The Intuition:
If division were allowed, we could multiply all elements and divide by current element.

### Why It's Problematic:
1. Division operator is explicitly forbidden
2. Zeros in array cause division by zero
3. Floating point precision issues

### Code Logic:
```java
int totalProduct = 1;
for (int num : nums) {
    totalProduct *= num;
}

for (int i = 0; i < nums.length; i++) {
    result[i] = totalProduct / nums[i];  // Not allowed!
}
```

## Approach 3: Left and Right Products (Intuitive Solution)

### The Big Idea (Simple Explanation):
Think of each position as splitting the array into two parts: everything to the left and everything to the right. The answer for each position is the product of the left part × product of the right part.

**Key Insight:** For position i, we need:
- Product of all elements to the left of i
- Product of all elements to the right of i
- Multiply these two products

### Step-by-step Walkthrough:
```
nums = [1,2,3,4]

Left products:
- Position 0: no elements to left → 1
- Position 1: elements [1] → 1
- Position 2: elements [1,2] → 2  
- Position 3: elements [1,2,3] → 6
left = [1,1,2,6]

Right products:
- Position 0: elements [2,3,4] → 24
- Position 1: elements [3,4] → 12
- Position 2: elements [4] → 4
- Position 3: no elements to right → 1
right = [24,12,4,1]

Final result:
result[i] = left[i] × right[i]
result = [1×24, 1×12, 2×4, 6×1] = [24,12,8,6]
```

### Code Logic:
```java
int[] left = new int[n];
int[] right = new int[n];

// Build left products
left[0] = 1;
for (int i = 1; i < n; i++) {
    left[i] = left[i-1] * nums[i-1];
}

// Build right products  
right[n-1] = 1;
for (int i = n-2; i >= 0; i--) {
    right[i] = right[i+1] * nums[i+1];
}

// Combine results
for (int i = 0; i < n; i++) {
    result[i] = left[i] * right[i];
}
```

### Complexity:
- **Time:** O(n) - Three passes through array
- **Space:** O(n) - Two extra arrays

## Approach 4: Space Optimized (Optimal!)

### The Optimization:
Instead of using two separate arrays, we can:
1. Use the result array to store left products first
2. Use a single variable to compute right products on the fly
3. Multiply right products with existing left products in result array

### How it works:
1. **First pass:** Store left products in result array
2. **Second pass:** Compute right products on the fly and multiply with result array

### Step-by-step Walkthrough:
```
nums = [1,2,3,4]

Pass 1 - Store left products in result:
result[0] = 1        (no left elements)
result[1] = 1        (left: 1)
result[2] = 1×2 = 2  (left: 1,2)  
result[3] = 1×2×3 = 6 (left: 1,2,3)
result = [1,1,2,6]

Pass 2 - Multiply with right products:
rightProduct = 1

i=3: result[3] = 6×1 = 6, rightProduct = 1×4 = 4
i=2: result[2] = 2×4 = 8, rightProduct = 4×3 = 12  
i=1: result[1] = 1×12 = 12, rightProduct = 12×2 = 24
i=0: result[0] = 1×24 = 24, rightProduct = 24×1 = 24

Final result = [24,12,8,6]
```

### Code Logic:
```java
int[] result = new int[n];

// First pass: store left products
result[0] = 1;
for (int i = 1; i < n; i++) {
    result[i] = result[i-1] * nums[i-1];
}

// Second pass: multiply with right products
int rightProduct = 1;
for (int i = n-1; i >= 0; i--) {
    result[i] = result[i] * rightProduct;
    rightProduct *= nums[i];
}
```

### Complexity:
- **Time:** O(n) - Two passes through array
- **Space:** O(1) - Not counting output array

### When to use:
- **This is the standard interview solution**
- When you need optimal space complexity
- When division is not allowed

## Handling Edge Cases

### Case 1: Array with Zeros
```
Input: [1,0,3,4]
Left:  [1,1,0,0]
Right: [0,12,4,1]  
Output: [0,12,0,0]
```

### Case 2: Multiple Zeros
```
Input: [1,0,0,4]
Output: [0,0,0,0] (more than one zero means all results are 0)
```

### Case 3: Negative Numbers
```
Input: [-1,2,-3,4]
Output: [-24,12,-8,6] (works the same way)
```

## Interview Strategy

### Step-by-step Approach:
1. **Understand the constraint:** "I can't use division, and need O(n) time"
2. **Explain the insight:** "For each position, I need left product × right product"
3. **Start with clear solution:** "I can use two arrays for left and right products"
4. **Optimize space:** "But I can optimize space by using the result array"
5. **Code the optimized solution:** Implement the space-optimized version
6. **Test edge cases:** Handle zeros and negative numbers

### What Interviewers Look For:
- Recognition that division approach won't work
- Understanding of left/right products concept
- Ability to optimize from O(n) space to O(1) space
- Correct handling of edge cases like zeros

## Common Mistakes

1. **Using division:** Forgetting that division is not allowed
2. **Wrong boundary handling:** Off-by-one errors in left/right calculations
3. **Not handling zeros:** Missing edge cases with zero elements
4. **Space complexity confusion:** Counting output array in space complexity

## Advanced Optimizations

### Optimization 1: Early Zero Detection
```java
// If more than one zero, all results are 0
int zeroCount = 0;
for (int num : nums) {
    if (num == 0) zeroCount++;
}
if (zeroCount > 1) {
    return new int[n]; // All zeros
}
```

### Optimization 2: Single Zero Handling
```java
// If exactly one zero, only that position has non-zero result
if (zeroCount == 1) {
    int product = 1;
    int zeroIndex = -1;
    for (int i = 0; i < n; i++) {
        if (nums[i] == 0) {
            zeroIndex = i;
        } else {
            product *= nums[i];
        }
    }
    int[] result = new int[n];
    result[zeroIndex] = product;
    return result;
}
```

## Real-world Applications

1. **Statistics:** Computing partial products in data analysis
2. **Finance:** Calculating portfolio returns excluding specific assets
3. **Machine Learning:** Feature importance calculations
4. **Image Processing:** Convolution operations with exclusions
5. **Database:** Aggregate calculations with exceptions

## Pattern Recognition

This problem teaches:
- **Prefix/Suffix computation** patterns
- **Space optimization** techniques
- **Array preprocessing** strategies
- **Constraint-driven algorithm design**

## Follow-up Questions

**Q: What if we could use division?**
A: Compute total product, then divide by each element (handle zeros separately).

**Q: What if we want the actual indices that contribute to each product?**
A: Extend to track contributing indices along with products.

**Q: What if the array is very large and doesn't fit in memory?**
A: Use streaming approach with multiple passes.

## Note

**For Mid-Level Interviews (2+ years):**
- **Master the space-optimized solution:** This shows advanced thinking
- **Explain the left/right products insight:** This is the key breakthrough
- **Handle edge cases systematically:** Zeros, negatives, single elements
- **Know the complexity:** O(n) time, O(1) space (excluding output)
- **Understand why division doesn't work:** Constraint awareness

**Interview Red Flags:**
- Suggesting division-based approach as final solution
- Not optimizing space from O(n) to O(1)
- Missing edge cases with zeros
- Incorrect complexity analysis

**Remember:** This problem is a classic example of how constraints (no division) can lead to elegant algorithmic insights. The left/right products pattern appears in many other array problems!

**Pattern Connection:** The prefix/suffix computation technique used here is fundamental in many algorithms like range sum queries, sliding window maximums, and more! 