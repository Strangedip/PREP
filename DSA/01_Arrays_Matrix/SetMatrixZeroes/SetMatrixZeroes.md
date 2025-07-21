# Set Matrix Zeroes

## Problem Statement

Given an `m x n` integer matrix, if an element is **0**, set its entire **row and column** to **0's**.

**Important constraint:** You must do it **in place**.

## Example
```
Input: matrix = [[1,1,1],
                 [1,0,1],
                 [1,1,1]]
Output: [[1,0,1],
         [0,0,0],
         [1,0,1]]

Explanation: The 0 at position (1,1) causes:
- Row 1 → all zeros: [0,0,0]
- Column 1 → all zeros
```

## Understanding the Problem

### Key Challenges:
1. **In-place requirement**: Cannot use extra matrix to store results
2. **Order dependency**: Setting zeros early can affect detection of original zeros
3. **Information preservation**: Need to remember which rows/columns had original zeros

### Visual Example:
```
Original:     After processing:
[1,1,1]       [1,0,1]
[1,0,1]  →    [0,0,0]
[1,1,1]       [1,0,1]
     ↑             ↑
  Original 0    Entire row & column → 0
```

## Approach 1: Extra Space (Straightforward)

### How it works:
Store the indices of rows and columns that need to be zeroed, then apply the changes.

### Algorithm Steps:
1. First pass: Scan matrix and record which rows/columns have zeros
2. Second pass: Set entire rows/columns to zero based on recorded indices

### Code Logic:
```java
Set<Integer> zeroRows = new HashSet<>();
Set<Integer> zeroCols = new HashSet<>();

// First pass: find zeros
for (int i = 0; i < m; i++) {
    for (int j = 0; j < n; j++) {
        if (matrix[i][j] == 0) {
            zeroRows.add(i);
            zeroCols.add(j);
        }
    }
}

// Second pass: set zeros
for (int i = 0; i < m; i++) {
    for (int j = 0; j < n; j++) {
        if (zeroRows.contains(i) || zeroCols.contains(j)) {
            matrix[i][j] = 0;
        }
    }
}
```

### Time & Space Complexity:
- **Time:** O(m × n) - Two complete matrix scans
- **Space:** O(m + n) - Store row and column indices

### When to use:
- When space is not a concern
- As a starting point to understand the problem
- For very small matrices where space optimization isn't critical

## Approach 2: Constant Space Using First Row/Column ⭐

### How it works (Simple explanation):
Use the first row and first column of the matrix itself as storage to remember which rows and columns need to be zeroed. It's like using the matrix as its own notebook!

### Key Insight:
Instead of using extra arrays, repurpose the first row and column:
- `matrix[i][0] = 0` means row `i` should be all zeros
- `matrix[0][j] = 0` means column `j` should be all zeros

### Algorithm Steps:
1. **Special handling**: Check if first row/column originally had zeros
2. **Marking phase**: Use first row/column to mark which rows/columns need zeros
3. **Zeroing phase**: Set zeros based on markers
4. **Cleanup**: Handle first row/column separately

### Example Walkthrough:
```
Original:          After marking:      After zeroing:
[1,1,1]           [1,0,1]            [1,0,1]
[1,0,1]     →     [0,0,1]      →     [0,0,0]
[1,1,1]           [1,0,1]            [1,0,1]

Step 1: Found 0 at (1,1)
Step 2: Mark matrix[1][0]=0 and matrix[0][1]=0
Step 3: Process based on markers
```

### Code Logic:
```java
// Check if first row/column originally have zeros
boolean firstRowZero = /* check first row */;
boolean firstColZero = /* check first column */;

// Use first row/column as markers
for (int i = 1; i < m; i++) {
    for (int j = 1; j < n; j++) {
        if (matrix[i][j] == 0) {
            matrix[i][0] = 0;  // Mark row
            matrix[0][j] = 0;  // Mark column
        }
    }
}

// Set zeros based on markers
for (int i = 1; i < m; i++) {
    for (int j = 1; j < n; j++) {
        if (matrix[i][0] == 0 || matrix[0][j] == 0) {
            matrix[i][j] = 0;
        }
    }
}

// Handle first row/column separately
if (firstRowZero) /* zero first row */;
if (firstColZero) /* zero first column */;
```

### Time & Space Complexity:
- **Time:** O(m × n) - Multiple passes but still linear
- **Space:** O(1) - Only use constant extra space

### When to use:
- **This is the preferred interview solution**
- When space optimization is required
- Demonstrates advanced in-place manipulation skills

## Approach 3: Optimized Constant Space

### How it works:
Similar to Approach 2 but uses `matrix[0][0]` cleverly to handle both first row and first column markers.

### Key Optimization:
- Use `matrix[0][0]` to indicate if first row needs zeros
- Use a separate boolean variable for first column
- Process from bottom-right to top-left to avoid conflicts

### When to use:
- When you want to show optimization skills
- To minimize the number of variables used
- Advanced implementation for experienced developers

## Approach 4: Sentinel Values (Alternative)

### How it works:
Use a special value (sentinel) to mark cells that should become zero, then convert all sentinels to zeros.

### Limitations:
- Requires knowledge about the value range in the matrix
- May not work if the sentinel value can naturally occur
- More complex logic for marking entire rows/columns

### When to use:
- When the matrix has a limited value range
- As an alternative thinking approach
- Educational purposes to show different problem-solving strategies

## Interview Tips

### What to say in an interview:
1. **Understand constraints:** "I need to do this in-place, so I can't use an extra matrix..."
2. **Start simple:** "Let me first think about using extra space, then optimize..."
3. **Identify the insight:** "I can use the matrix itself to store marking information..."
4. **Handle edge cases:** "I need special handling for the first row and column..."

### Common Follow-up Questions:
1. **"Can you do it without extra space?"** → Show Approach 2
2. **"What if the matrix is very large?"** → Discuss memory and cache considerations
3. **"What about multithreading?"** → Discuss race conditions and synchronization
4. **"Can you make it faster?"** → Discuss bit manipulation or SIMD optimizations

## Edge Cases to Consider

1. **Empty matrix:** `matrix = []` → Return immediately
2. **Single element zero:** `matrix = [[0]]` → Should remain `[[0]]`
3. **Single element non-zero:** `matrix = [[1]]` → Should remain `[[1]]`
4. **Single row:** `matrix = [[1,0,3]]` → Should become `[[0,0,0]]`
5. **Single column:** `matrix = [[1],[0],[3]]` → Should become `[[0],[0],[0]]`
6. **All zeros:** `matrix = [[0,0],[0,0]]` → Should remain all zeros
7. **No zeros:** `matrix = [[1,2],[3,4]]` → Should remain unchanged
8. **First row/column have zeros:** Special handling required

## Common Mistakes to Avoid

### Space Optimization Pitfalls:
1. **Overwriting markers:** Setting zeros before reading all markers
2. **First row/column confusion:** Not handling the overlap at `matrix[0][0]`
3. **Order dependency:** Processing in wrong order and losing information

### Logic Errors:
1. **Forgetting special cases:** Not checking if first row/column originally had zeros
2. **Boundary conditions:** Wrong loop bounds when skipping first row/column
3. **Marker conflicts:** Using the same cell for multiple purposes

### Implementation Issues:
1. **Missing null checks:** Not validating input matrix
2. **Incomplete zeroing:** Only zeroing part of a row/column
3. **Performance issues:** Unnecessary repeated scans

## Optimization Notes

### Performance Considerations:
- **Cache locality:** Process row-wise for better cache performance
- **Early termination:** If entire matrix becomes zero, can stop early
- **Bit manipulation:** For boolean arrays, can use bit vectors for space efficiency

### Memory Efficiency:
- Constant space approach uses only 2 extra boolean variables
- Can be further optimized to use just 1 bit of information
- Consider register allocation for frequently accessed variables

## Real-world Applications

1. **Image processing:** Masking pixels based on certain conditions
2. **Spreadsheet software:** Conditional formatting and cell clearing
3. **Game development:** Grid-based game state updates
4. **Database operations:** Bulk updates with cascading effects
5. **Scientific computing:** Matrix operations with sparse patterns

## Related Problems

1. **Game of Life:** Similar in-place matrix updates
2. **Rotate Image:** In-place matrix transformations
3. **Spiral Matrix:** Matrix traversal patterns
4. **Valid Sudoku:** Matrix validation with constraints

## Complexity Analysis Comparison

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| Extra Space (Sets) | O(mn) | O(m+n) | Learning/Simple implementation |
| Extra Space (Arrays) | O(mn) | O(m+n) | Better cache performance |
| **Constant Space** | O(mn) | **O(1)** | **Interview favorite** |
| Optimized Constant | O(mn) | O(1) | Advanced optimization |
| Sentinel Values | O(mn) | O(1) | Alternative approach |

## Implementation Tips

### Code Structure:
```java
public void setZeroes(int[][] matrix) {
    // 1. Input validation
    if (matrix == null || matrix.length == 0) return;
    
    // 2. Initialize variables
    int m = matrix.length, n = matrix[0].length;
    boolean firstRowZero = false, firstColZero = false;
    
    // 3. Check first row/column
    // ... check logic
    
    // 4. Mark using first row/column
    // ... marking logic
    
    // 5. Set zeros based on markers
    // ... zeroing logic
    
    // 6. Handle first row/column
    // ... cleanup logic
}
```

### Debugging Strategy:
1. **Test with small matrices:** Start with 2x2 or 3x3 examples
2. **Trace the marking:** Print matrix after marking phase
3. **Check boundary cases:** Verify first row/column handling
4. **Visual verification:** Draw out the process step by step

## Note

**For mid-level interviews (2+ years experience):**
- Start by discussing the extra space approach briefly
- Quickly move to the constant space solution (this is what they want to see)
- Explain the clever use of first row/column as storage
- Handle all edge cases, especially first row/column scenarios
- Code it efficiently without bugs

**Remember:** This problem tests your ability to think creatively about space optimization and handle complex edge cases. The key insight is repurposing parts of the input as temporary storage! 