# Spiral Matrix

## Problem Statement

Given an `m x n` matrix, return all elements of the matrix in **spiral order**.

**Spiral order** means:
1. Start from the top-left corner
2. Move right across the top row
3. Move down the right column
4. Move left across the bottom row
5. Move up the left column
6. Repeat for inner layers

## Example
```
Input: matrix = [[1,2,3],
                 [4,5,6],
                 [7,8,9]]
Output: [1,2,3,6,9,8,7,4,5]

Visual path:
1 → 2 → 3
        ↓
4 → 5   6
↑       ↓
7 ← 8 ← 9
```

## Understanding the Problem

### Key Insights:
1. **Layer-by-layer traversal**: Process the matrix from outer layer to inner layer
2. **Four directions**: Right → Down → Left → Up (clockwise)
3. **Boundary management**: Keep track of current boundaries and shrink them after each side
4. **Edge cases**: Handle single row, single column, and single element matrices

### Visual Example (3x4 matrix):
```
[1,  2,  3,  4]    Layer 1: 1→2→3→4→8→12→11→10→9→5
[5,  6,  7,  8]    Layer 2: 6→7
[9, 10, 11, 12]    
```

## Approach 1: Layer by Layer (Most Intuitive) ⭐

### How it works:
Think of the matrix as concentric rectangles (layers). We traverse each layer completely before moving to the inner layer.

### Algorithm Steps:
1. Define boundaries: `top`, `bottom`, `left`, `right`
2. For each layer:
   - Traverse top row (left to right)
   - Traverse right column (top to bottom)
   - Traverse bottom row (right to left) - if still have rows
   - Traverse left column (bottom to top) - if still have columns
3. Shrink boundaries and repeat

### Code Logic:
```java
while (top <= bottom && left <= right) {
    // Top row: left to right
    for (int col = left; col <= right; col++) {
        result.add(matrix[top][col]);
    }
    top++;
    
    // Right column: top to bottom
    for (int row = top; row <= bottom; row++) {
        result.add(matrix[row][right]);
    }
    right--;
    
    // Bottom row: right to left (check if still have rows)
    if (top <= bottom) {
        for (int col = right; col >= left; col--) {
            result.add(matrix[bottom][col]);
        }
        bottom--;
    }
    
    // Left column: bottom to top (check if still have columns)
    if (left <= right) {
        for (int row = bottom; row >= top; row--) {
            result.add(matrix[row][left]);
        }
        left++;
    }
}
```

### Time & Space Complexity:
- **Time:** O(m × n) - Visit each element exactly once
- **Space:** O(1) - Only use constant extra space (not counting output)

### When to use:
- **This is the preferred interview solution**
- Most intuitive and easy to explain
- Handles all edge cases naturally

## Approach 2: Direction Simulation

### How it works:
Simulate walking through the matrix following the spiral path. Change direction when hitting boundaries or visited cells.

### Algorithm Steps:
1. Start at (0,0) moving right
2. Keep a visited array to track processed cells
3. Move in current direction until blocked
4. Change direction: Right → Down → Left → Up
5. Repeat until all cells visited

### Direction Vectors:
```java
// Direction order: right, down, left, up
int[] dr = {0, 1, 0, -1};  // row changes
int[] dc = {1, 0, -1, 0};  // column changes
```

### Time & Space Complexity:
- **Time:** O(m × n) - Visit each element exactly once
- **Space:** O(m × n) - Need visited array

### When to use:
- When you want to show alternative thinking
- Good for understanding the movement pattern
- Useful for similar path-finding problems

## Approach 3: Recursive Spiral

### How it works:
Recursively process each layer of the spiral from outside to inside.

### Algorithm Steps:
1. Process the current layer boundaries
2. Recursively call for the inner layer
3. Base case: no more elements to process

### Advantages:
- Clean and elegant code
- Natural layer separation
- Good for understanding the problem structure

### Disadvantages:
- Uses recursion stack space
- Slightly more complex edge case handling

### Time & Space Complexity:
- **Time:** O(m × n) - Visit each element exactly once
- **Space:** O(min(m, n)) - Recursion depth equals number of layers

### When to use:
- When interviewer asks for recursive solution
- If you're comfortable with recursion
- For educational understanding of the layer concept

## Approach 4: Optimized Boundary Tracking

### How it works:
Similar to Approach 1 but uses total element count to avoid complex boundary checks.

### Key Optimization:
```java
while (result.size() < totalElements) {
    // Process each direction with size check
    for (int col = left; col <= right && result.size() < totalElements; col++) {
        result.add(matrix[top][col]);
    }
    // ... continue for other directions
}
```

### When to use:
- When you want to minimize boundary condition complexity
- Slightly more robust against edge cases
- Good for coding interviews where clarity is important

## Interview Tips

### What to say in an interview:
1. **Understand the pattern:** "I need to traverse the matrix in a clockwise spiral..."
2. **Choose approach:** "I'll use the layer-by-layer approach as it's most intuitive..."
3. **Explain boundaries:** "I'll keep track of top, bottom, left, right boundaries..."
4. **Handle edge cases:** "Let me make sure to handle single row/column cases..."

### Common Follow-up Questions:
1. **"Can you do it in-place?"** → Discuss modifying the matrix vs. using visited array
2. **"What about counter-clockwise?"** → Modify the direction order
3. **"Generate spiral matrix?"** → Reverse the problem - fill matrix in spiral order
4. **"Handle negative numbers?"** → Same algorithm works

## Edge Cases to Consider

1. **Empty matrix:** `matrix = []` → Return empty list
2. **Single element:** `matrix = [[1]]` → Return `[1]`
3. **Single row:** `matrix = [[1,2,3]]` → Return `[1,2,3]`
4. **Single column:** `matrix = [[1],[2],[3]]` → Return `[1,2,3]`
5. **Different dimensions:** m ≠ n matrices
6. **Large matrix:** Test performance with large inputs

## Common Mistakes to Avoid

### Boundary Issues:
1. **Forgetting boundary checks:** Always check if boundaries are valid before traversing
2. **Off-by-one errors:** Be careful with `<=` vs `<` in loop conditions
3. **Duplicate processing:** Ensure each element is visited exactly once

### Direction Changes:
1. **Wrong order:** Maintain clockwise order (right → down → left → up)
2. **Missing direction change:** Change direction when hitting boundary or visited cell
3. **Infinite loops:** Ensure progress is made in each iteration

### Edge Case Handling:
1. **Single row/column:** Special handling needed to avoid duplicate processing
2. **Empty input:** Check for null/empty matrix early
3. **Boundary shrinking:** Update boundaries correctly after each side

## Optimization Notes

### Performance Considerations:
- Layer-by-layer approach is generally fastest (O(1) space)
- Direction simulation is cleaner but uses O(mn) space
- Avoid unnecessary boundary checks where possible

### Code Clarity:
- Use meaningful variable names (`top`, `bottom`, `left`, `right`)
- Comment each direction clearly
- Handle edge cases early in the function

## Real-world Applications

1. **Image processing:** Spiral scanning of images
2. **Game development:** Character movement patterns
3. **Data visualization:** Spiral layouts for displaying data
4. **Memory access patterns:** Cache-efficient matrix traversal
5. **Printing/Display:** Spiral printing of 2D data

## Related Problems

1. **Spiral Matrix II:** Generate spiral matrix (reverse problem)
2. **Rotate Image:** Matrix rotation problems
3. **Set Matrix Zeroes:** Matrix modification problems
4. **Word Search:** Matrix traversal with path tracking

## Complexity Analysis Comparison

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| Layer by Layer | O(mn) | O(1) | **Interview favorite** |
| Direction Simulation | O(mn) | O(mn) | Learning movement patterns |
| Recursive | O(mn) | O(min(m,n)) | Recursive preference |
| Optimized Boundary | O(mn) | O(1) | Robust edge case handling |

## Implementation Tips

### Debugging Strategy:
1. **Trace manually:** Walk through small examples by hand
2. **Boundary logging:** Print boundary values in each iteration
3. **Step-by-step:** Add logging for each direction traversal
4. **Edge case testing:** Test single row/column cases first

### Code Structure:
```java
// 1. Input validation
if (matrix == null || matrix.length == 0) return new ArrayList<>();

// 2. Initialize boundaries
int top = 0, bottom = m-1, left = 0, right = n-1;

// 3. Main spiral loop
while (boundaries_valid) {
    // Process each direction with proper checks
}

// 4. Return result
```

## Note

**For mid-level interviews (2+ years experience):**
- Start with the layer-by-layer approach (most intuitive and efficient)
- Explain the boundary management clearly
- Handle edge cases proactively
- Code it cleanly without bugs on first attempt
- Be ready to trace through an example step by step

**Remember:** This problem tests your ability to handle complex iteration patterns and edge cases. The key is maintaining clear boundary management and ensuring each element is visited exactly once in the correct order! 