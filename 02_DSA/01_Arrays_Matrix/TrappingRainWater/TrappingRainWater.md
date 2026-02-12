# Trapping Rain Water

## Problem Statement
Given `n` non-negative integers representing an elevation map where the width of each bar is `1`, compute how much **water can be trapped** after raining.

**Examples:**
```
Input: height = [0,1,0,2,1,0,1,3,2,1,2,1]
Output: 6
Explanation: The above elevation map (black section) is represented by array [0,1,0,2,1,0,1,3,2,1,2,1]. 
In this case, 6 units of rain water (blue section) are being trapped.

Input: height = [4,2,0,3,2,5]
Output: 9
```

## Problem Analysis

### Core Insight
Water can be trapped at position `i` if there are **higher bars on both left and right sides**:
- **Water level**: Determined by the minimum of the maximum heights on left and right
- **Trapped water**: `min(leftMax, rightMax) - height[i]` (if positive)
- **Boundary conditions**: No water can be trapped at the edges

### Key Concepts
- **Elevation map**: Heights represent barriers that can hold water
- **Water physics**: Water flows to lowest level, trapped by surrounding barriers
- **Two-sided constraint**: Need support from both left AND right to trap water

## Approaches

### Approach 1: Brute Force

#### Key Insight
**For each position**, find the maximum height on left and right, then calculate trapped water.

#### Algorithm
1. **For each position i**: Find max height to the left and right
2. **Calculate water level**: `min(leftMax, rightMax)`
3. **Trapped water**: `max(0, waterLevel - height[i])`
4. **Sum all positions**: Total trapped water

#### Time Complexity
- **O(n²)** - For each position, scan left and right

#### Space Complexity
- **O(1)** - Only using variables

```java
public int trapBruteForce(int[] height) {
    int n = height.length;
    int totalWater = 0;
    
    for (int i = 1; i < n - 1; i++) {  // Skip edges
        // Find max height on left
        int leftMax = 0;
        for (int j = 0; j < i; j++) {
            leftMax = Math.max(leftMax, height[j]);
        }
        
        // Find max height on right
        int rightMax = 0;
        for (int j = i + 1; j < n; j++) {
            rightMax = Math.max(rightMax, height[j]);
        }
        
        // Calculate trapped water at position i
        int waterLevel = Math.min(leftMax, rightMax);
        if (waterLevel > height[i]) {
            totalWater += waterLevel - height[i];
        }
    }
    
    return totalWater;
}
```

### Approach 2: Dynamic Programming ⭐ (Pre-computation)

#### Key Insight
**Pre-compute left and right maximums** to avoid redundant calculations.

#### Algorithm
1. **Left max array**: `leftMax[i]` = maximum height from index `0` to `i`
2. **Right max array**: `rightMax[i]` = maximum height from index `i` to `n-1`
3. **Calculate water**: For each position, use pre-computed values

#### Time Complexity
- **O(n)** - Three linear passes

#### Space Complexity
- **O(n)** - Two auxiliary arrays

```java
public int trapDP(int[] height) {
    int n = height.length;
    if (n <= 2) return 0;
    
    // Pre-compute left maximums
    int[] leftMax = new int[n];
    leftMax[0] = height[0];
    for (int i = 1; i < n; i++) {
        leftMax[i] = Math.max(leftMax[i - 1], height[i]);
    }
    
    // Pre-compute right maximums
    int[] rightMax = new int[n];
    rightMax[n - 1] = height[n - 1];
    for (int i = n - 2; i >= 0; i--) {
        rightMax[i] = Math.max(rightMax[i + 1], height[i]);
    }
    
    // Calculate trapped water
    int totalWater = 0;
    for (int i = 1; i < n - 1; i++) {
        int waterLevel = Math.min(leftMax[i], rightMax[i]);
        totalWater += Math.max(0, waterLevel - height[i]);
    }
    
    return totalWater;
}
```

### Approach 3: Two Pointers ⭐ (Optimal)

#### Key Insight
**Use two pointers moving inward**, maintaining left and right maximums as we go.

#### Algorithm
1. **Two pointers**: Start from both ends
2. **Track maximums**: Maintain `leftMax` and `rightMax` 
3. **Move the smaller side**: Process the side with smaller maximum
4. **Calculate water**: When we know one side is limiting factor

#### Why This Works
- If `leftMax < rightMax`, then water at left pointer is limited by `leftMax`
- We don't need to know the exact `rightMax` at left position, just that it's ≥ `leftMax`

#### Time Complexity
- **O(n)** - Single pass

#### Space Complexity
- **O(1)** - Only using pointers and variables

```java
public int trapTwoPointers(int[] height) {
    int left = 0, right = height.length - 1;
    int leftMax = 0, rightMax = 0;
    int totalWater = 0;
    
    while (left < right) {
        if (height[left] < height[right]) {
            // Process left side
            if (height[left] >= leftMax) {
                leftMax = height[left];
            } else {
                totalWater += leftMax - height[left];
            }
            left++;
        } else {
            // Process right side
            if (height[right] >= rightMax) {
                rightMax = height[right];
            } else {
                totalWater += rightMax - height[right];
            }
            right--;
        }
    }
    
    return totalWater;
}
```

### Approach 4: Stack-Based Solution

#### Key Insight
**Use stack to track potential water containers** and calculate water when we find closing heights.

#### Algorithm
1. **Stack stores indices**: Indices of heights in increasing order
2. **Pop when higher found**: When current height > stack top
3. **Calculate trapped water**: Between the popped element and current height
4. **Width calculation**: Distance between boundaries

#### Time Complexity
- **O(n)** - Each element pushed and popped at most once

#### Space Complexity
- **O(n)** - Stack space in worst case

```java
public int trapStack(int[] height) {
    Stack<Integer> stack = new Stack<>();
    int totalWater = 0;
    
    for (int i = 0; i < height.length; i++) {
        while (!stack.isEmpty() && height[i] > height[stack.peek()]) {
            int bottom = stack.pop();
            
            if (stack.isEmpty()) break;
            
            int distance = i - stack.peek() - 1;
            int boundedHeight = Math.min(height[i], height[stack.peek()]) - height[bottom];
            totalWater += distance * boundedHeight;
        }
        stack.push(i);
    }
    
    return totalWater;
}
```

## Visualization

### Example: `[0,1,0,2,1,0,1,3,2,1,2,1]`
```
                    ████
        ████        ████    ████
    ████████    ████████████████
████████████████████████████████
0 1 0 2 1 0 1 3 2 1 2 1

Water trapped:
    ░                
░░░░████░░░░████░░░░████
████████████████████████████████
0 1 0 2 1 0 1 3 2 1 2 1
```

### Two Pointers Example
```
Step 1: left=0, right=11, leftMax=0, rightMax=1
Step 2: left=1, right=11, leftMax=1, rightMax=1
...
Water calculated incrementally as pointers move inward
```

## Common Mistakes

1. **Edge case handling**: Not checking for arrays with length < 3
2. **Index bounds**: Accessing array out of bounds in brute force
3. **Negative water**: Forgetting to use `max(0, waterLevel - height[i])`
4. **Two pointers logic**: Incorrectly determining which pointer to move
5. **Stack approach**: Wrong calculation of width or bounded height

## Edge Cases

1. **Empty array**: Return 0
2. **Single/Two elements**: No water can be trapped
3. **All decreasing**: `[5,4,3,2,1]` - no water trapped
4. **All increasing**: `[1,2,3,4,5]` - no water trapped
5. **Flat array**: `[2,2,2,2]` - no water trapped

## Interview Tips

### Problem Recognition
- **"Trapped water"** → Think about left/right boundaries
- **"Elevation map"** → 2D visualization helps
- **"Rain water"** → Classic DP or two-pointers problem

### Approach Strategy
1. **Start with brute force**: Show understanding of water trapping logic
2. **Optimize with DP**: Demonstrate pre-computation concept
3. **Present two pointers**: Show optimal space complexity solution
4. **Mention stack approach**: For completeness (if time permits)

### Code Structure
```java
public int trap(int[] height) {
    if (height.length < 3) return 0;
    
    int left = 0, right = height.length - 1;
    int leftMax = 0, rightMax = 0;
    int totalWater = 0;
    
    while (left < right) {
        if (height[left] < height[right]) {
            // Process left side logic
        } else {
            // Process right side logic
        }
    }
    
    return totalWater;
}
```

## LeetCode Problems

### Core Problems:
- **[LeetCode 42 - Trapping Rain Water](https://leetcode.com/problems/trapping-rain-water/)** ⭐ (This problem)
- **[LeetCode 407 - Trapping Rain Water II](https://leetcode.com/problems/trapping-rain-water-ii/)** (2D version - much harder)

### Related Array Problems:
- **[LeetCode 11 - Container With Most Water](https://leetcode.com/problems/container-with-most-water/)** (Similar two-pointers technique)
- **[LeetCode 84 - Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)** (Stack-based solution)
- **[LeetCode 238 - Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/)** (Left/right preprocessing)

### Two Pointers Pattern:
- **[LeetCode 15 - 3Sum](https://leetcode.com/problems/3sum/)** (Two pointers after sorting)
- **[LeetCode 125 - Valid Palindrome](https://leetcode.com/problems/valid-palindrome/)** (Two pointers from ends)
- **[LeetCode 167 - Two Sum II](https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/)** (Two pointers technique)

### Stack-Based Problems:
- **[LeetCode 739 - Daily Temperatures](https://leetcode.com/problems/daily-temperatures/)** (Monotonic stack)
- **[LeetCode 496 - Next Greater Element I](https://leetcode.com/problems/next-greater-element-i/)** (Stack for next greater)

### Difficulty Progression:
1. **Start with**: LeetCode 11 (Container With Most Water) - Simpler two pointers
2. **Core problem**: LeetCode 42 (Trapping Rain Water) - Classic problem
3. **Advanced**: LeetCode 84 (Largest Rectangle) - Similar stack technique  
4. **Expert**: LeetCode 407 (Trapping Rain Water II) - 2D generalization

## Complexity Analysis Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Brute Force** | O(n²) | O(1) | **Understanding problem** |
| **Dynamic Programming** | O(n) | O(n) | **Clear logic, interview friendly** |
| **Two Pointers** | O(n) | O(1) | **Optimal solution** |
| **Stack-Based** | O(n) | O(n) | **Alternative perspective** |

## Real-World Applications

1. **Civil Engineering**: Designing drainage systems, flood control
2. **Architecture**: Roof water collection, gutter design
3. **Agriculture**: Irrigation channel design
4. **Urban Planning**: Stormwater management systems
5. **Game Development**: Water physics simulation
6. **3D Graphics**: Fluid simulation algorithms

## Mathematical Insights

### Water Calculation Formula
```
Water at position i = max(0, min(leftMax[i], rightMax[i]) - height[i])
```

### Total Water Bound
```
Maximum possible water = (n-2) × (max_height - min_height)
Actual water ≤ Maximum possible water
```

### Two Pointers Invariant
At any point during two pointers execution:
- Water at left pointer ≤ leftMax
- Water at right pointer ≤ rightMax
- We process the side with smaller max (limiting factor)

## LeetCode Similar Problems:
- [11. Container With Most Water](https://leetcode.com/problems/container-with-most-water/)
- [407. Trapping Rain Water II](https://leetcode.com/problems/trapping-rain-water-ii/)
- [238. Product of Array Except Self](https://leetcode.com/problems/product-of-array-except-self/)
- [84. Largest Rectangle in Histogram](https://leetcode.com/problems/largest-rectangle-in-histogram/)
- [85. Maximal Rectangle](https://leetcode.com/problems/maximal-rectangle/)

**Remember**: Trapping Rain Water demonstrates the power of **two pointers technique** and shows how **pre-computation can optimize brute force solutions**. The key insight is recognizing that water level is determined by the **minimum of left and right maximums**! 