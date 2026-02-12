# Container With Most Water

## Problem Statement

You are given an array of heights representing vertical lines. Find two lines that together with the x-axis form a container that can hold the most water.

**Key Points:**
- Each element represents the height of a vertical line
- The distance between lines determines the width
- Area = width × min(height1, height2)
- Return the maximum area possible

## Example
```
Input: height = [1,8,6,2,5,4,8,3,7]
Output: 49
Explanation: Lines at index 1 (height=8) and index 8 (height=7) 
            form container with area = (8-1) × min(8,7) = 7 × 7 = 49
```

## Approach 1: Brute Force (Understanding the Problem)

### How it works:
Check every possible pair of lines to find the maximum area.

1. Try every pair of indices (i, j) where i < j
2. Calculate area = (j - i) × min(height[i], height[j])
3. Keep track of maximum area

### Code Logic:
```java
for (int i = 0; i < height.length; i++) {
    for (int j = i + 1; j < height.length; j++) {
        int width = j - i;
        int containerHeight = Math.min(height[i], height[j]);
        int area = width * containerHeight;
        maxArea = Math.max(maxArea, area);
    }
}
```

### Complexity:
- **Time:** O(n²) - Check all pairs
- **Space:** O(1)

### When to use:
- Small arrays (< 100 elements)
- To demonstrate problem understanding

## Approach 2: Two Pointers (Optimal!)

### The Big Idea (Simple Explanation):
Think of this like adjusting a telescope. Start with the telescope fully extended (widest possible). Now you need to make it shorter, but you want to keep the "view quality" (area) as high as possible.

**Key Insight:** If you have two lines of different heights, moving the **taller line** inward will only decrease the area (because width decreases and height is still limited by the shorter line). So always move the **shorter line** inward.

### Why This Works:
1. Start with maximum width (leftmost and rightmost lines)
2. The area is limited by the shorter of the two lines
3. Moving the taller line can't improve area (width decreases, height stays same)
4. Moving the shorter line might find a taller line, potentially improving area

### Step-by-step Walkthrough:
```
height = [1,8,6,2,5,4,8,3,7]
left = 0, right = 8

Step 1: left=0(1), right=8(7) → area = 8 × min(1,7) = 8 × 1 = 8
        height[0] < height[8], so move left++

Step 2: left=1(8), right=8(7) → area = 7 × min(8,7) = 7 × 7 = 49  
        height[1] > height[8], so move right--

Step 3: left=1(8), right=7(3) → area = 6 × min(8,3) = 6 × 3 = 18
        height[1] > height[7], so move right--

Continue until left >= right...
Maximum area found: 49
```

### Code Logic:
```java
int left = 0, right = height.length - 1;
int maxArea = 0;

while (left < right) {
    int width = right - left;
    int containerHeight = Math.min(height[left], height[right]);
    int area = width * containerHeight;
    maxArea = Math.max(maxArea, area);
    
    // Move the pointer with smaller height
    if (height[left] < height[right]) {
        left++;
    } else {
        right--;
    }
}
```

### Complexity:
- **Time:** O(n) - Single pass through array
- **Space:** O(1) - Only using two pointers

### When to use:
- **This is the standard interview solution**
- All array sizes
- When you need optimal performance

## Key Insights & Optimizations

### Why Move the Shorter Line?
Consider lines at positions i and j with height[i] < height[j]:
- Current area = (j - i) × height[i]
- If we move j to j-1: area = (j-1-i) × min(height[i], height[j-1])
- Since height[i] is the limiting factor, new area ≤ (j-1-i) × height[i] < current area
- So moving the taller line can never improve the area

### Optimization: Skip Duplicates
```java
// After finding current area, skip duplicate heights
if (height[left] < height[right]) {
    while (left < right && height[left] <= currentLeftHeight) {
        left++;
    }
} else {
    while (left < right && height[right] <= currentRightHeight) {
        right--;
    }
}
```

## Interview Strategy

### Step-by-step Approach:
1. **Clarify the problem:** "I need to find two lines that form the largest container, right?"
2. **Start with brute force:** "I could check every pair in O(n²)..."
3. **Explain the insight:** "But I can optimize using two pointers..."
4. **Key realization:** "Moving the taller line can never improve the area because..."
5. **Code the solution:** Implement the two pointers approach
6. **Test with examples:** Walk through the given example

### What Interviewers Look For:
- Understanding that area is limited by the shorter line
- Recognition of the two pointers optimization
- Clear explanation of why we move the shorter pointer
- Correct implementation without off-by-one errors

## Edge Cases

1. **Two lines only:** `[1, 2]` → area = 1 × min(1,2) = 1
2. **All same height:** `[3, 3, 3, 3]` → area = 3 × 3 = 9 (first and last)
3. **Decreasing heights:** `[5, 4, 3, 2, 1]` → area = 4 × min(5,1) = 4
4. **One very tall line:** `[1, 100, 1]` → area = 2 × min(1,1) = 2

## Common Mistakes

1. **Moving both pointers:** Only move one pointer at a time
2. **Moving the wrong pointer:** Always move the shorter line
3. **Incorrect area calculation:** area = width × min(height1, height2)
4. **Off-by-one errors:** Make sure width = right - left

## Real-world Applications

1. **Water Storage Systems:** Designing optimal water tanks
2. **Resource Allocation:** Maximizing capacity with constraints
3. **Architecture:** Optimal building layouts for space utilization
4. **Manufacturing:** Container design optimization
5. **Network Design:** Bandwidth allocation between nodes

## Follow-up Questions

**Q: What if we can use at most K lines?**
A: Extend to K-dimensional problem using dynamic programming.

**Q: What if lines have different widths?**
A: Modify area calculation to account for line thickness.

**Q: What if we want the actual indices, not just the area?**
A: Track the best indices along with the maximum area.

## Pattern Recognition

This problem teaches:
- **Two pointers technique** on arrays
- **Greedy optimization** (always make the locally optimal choice)
- **Geometric intuition** in algorithm design
- **Constraint elimination** (why moving tall line is useless)

## Note

**For Mid-Level Interviews (2+ years):**
- **Master the two pointers approach:** This is the expected solution
- **Explain the greedy choice:** Why moving the shorter line makes sense
- **Know the complexity:** O(n) time, O(1) space
- **Handle edge cases:** Arrays with 2 elements, equal heights
- **Understand the geometry:** Visualize the problem as containers

**Interview Red Flags:**
- Not recognizing the two pointers optimization
- Moving both pointers simultaneously
- Incorrect area calculation
- Unable to explain why the greedy choice works

**Remember:** This problem is a classic example of how geometric intuition can lead to elegant algorithmic solutions. The insight about moving the shorter line is the key breakthrough that transforms an O(n²) problem into O(n)!

**Pattern Connection:** This two pointers technique appears in many other problems like Trapping Rain Water, Three Sum, and more! 