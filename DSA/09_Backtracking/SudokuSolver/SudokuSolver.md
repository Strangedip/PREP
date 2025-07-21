# Sudoku Solver

## Problem Statement
Write a program to solve a **Sudoku puzzle** by filling the empty cells.

A Sudoku solution must satisfy **all of the following rules**:
1. Each of the digits **1-9** must occur exactly **once in each row**
2. Each of the digits **1-9** must occur exactly **once in each column** 
3. Each of the digits **1-9** must occur exactly **once in each of the 9 3×3 sub-boxes**

The `'.'` character indicates **empty cells**.

**Example:**
```
Input: board = 
[["5","3",".",".","7",".",".",".","."],
 ["6",".",".","1","9","5",".",".","."],
 [".","9","8",".",".",".",".","6","."],
 ["8",".",".",".","6",".",".",".","3"],
 ["4",".",".","8",".","3",".",".","1"],
 ["7",".",".",".","2",".",".",".","6"],
 [".","6",".",".",".",".","2","8","."],
 [".",".",".","4","1","9",".",".","5"],
 [".",".",".",".","8",".",".","7","9"]]

Output: Solve the puzzle in-place
[["5","3","4","6","7","8","9","1","2"],
 ["6","7","2","1","9","5","3","4","8"],
 ["1","9","8","3","4","2","5","6","7"],
 ["8","5","9","7","6","1","4","2","3"],
 ["4","2","6","8","5","3","7","9","1"],
 ["7","1","3","9","2","4","8","5","6"],
 ["9","6","1","5","3","7","2","8","4"],
 ["2","8","7","4","1","9","6","3","5"],
 ["3","4","5","2","8","6","1","7","9"]]
```

## Problem Analysis

### Core Insight
This is a **constraint satisfaction problem** solved using **backtracking**:
- **9 row constraints**: Each row must contain digits 1-9 exactly once
- **9 column constraints**: Each column must contain digits 1-9 exactly once  
- **9 box constraints**: Each 3×3 sub-box must contain digits 1-9 exactly once
- **Total constraints**: 27 overlapping constraints

### Sudoku Properties
- **Fixed grid size**: Always 9×9 (81 cells)
- **Box indexing**: Box number = `(row/3) * 3 + (col/3)`
- **Well-formed puzzles**: Have exactly one solution
- **Difficulty levels**: Based on solving techniques required

### Search Space Analysis
- **Empty cells**: Typically 40-60 in puzzles
- **Branching factor**: 1-9 possible values per cell
- **Worst case**: 9^81 possibilities without constraints
- **With pruning**: Much smaller effective search space

## Approaches

### Approach 1: Basic Backtracking ⭐ (Most Intuitive)

#### Key Insight
**Try each possible digit** for empty cells and backtrack when constraints are violated.

#### Algorithm
1. **Find next empty cell**: Scan left-to-right, top-to-bottom
2. **Try digits 1-9**: For each possible value
3. **Check validity**: Ensure no constraint violations
4. **Recursive placement**: Continue to next empty cell
5. **Backtrack**: Remove digit and try next if no solution found

#### Constraint Checking
```java
private boolean isValidPlacement(char[][] board, int row, int col, char digit) {
    // Check row constraint
    for (int c = 0; c < 9; c++) {
        if (board[row][c] == digit) return false;
    }
    
    // Check column constraint  
    for (int r = 0; r < 9; r++) {
        if (board[r][col] == digit) return false;
    }
    
    // Check 3x3 box constraint
    int boxRow = (row / 3) * 3;
    int boxCol = (col / 3) * 3;
    for (int r = boxRow; r < boxRow + 3; r++) {
        for (int c = boxCol; c < boxCol + 3; c++) {
            if (board[r][c] == digit) return false;
        }
    }
    
    return true;
}
```

#### Time Complexity
- **O(9^(empty cells))** in worst case
- **Heavy pruning** reduces practical complexity significantly

#### Space Complexity
- **O(1)** for in-place modification + **O(81)** recursion stack

### Approach 2: Optimized with Constraint Tracking ⭐ (Efficient)

#### Key Insight
**Pre-compute and maintain constraint sets** for O(1) validity checking.

#### Optimization Strategy
- **Row sets**: Track used digits in each row
- **Column sets**: Track used digits in each column
- **Box sets**: Track used digits in each 3×3 box
- **O(1) constraint checking** instead of O(27) scanning

#### Data Structures
```java
Set<Character>[] rows = new HashSet[9];    // rows[i] = digits used in row i
Set<Character>[] cols = new HashSet[9];    // cols[i] = digits used in col i  
Set<Character>[] boxes = new HashSet[9];   // boxes[i] = digits used in box i
```

#### Algorithm
```java
private boolean backtrack(int index, List<int[]> emptyCells) {
    if (index == emptyCells.size()) return true;  // All cells filled
    
    int[] cell = emptyCells.get(index);
    int row = cell[0], col = cell[1];
    int boxIndex = (row / 3) * 3 + (col / 3);
    
    for (char digit = '1'; digit <= '9'; digit++) {
        if (!rows[row].contains(digit) && 
            !cols[col].contains(digit) && 
            !boxes[boxIndex].contains(digit)) {
            
            // Make choice
            board[row][col] = digit;
            rows[row].add(digit);
            cols[col].add(digit);
            boxes[boxIndex].add(digit);
            
            // Recurse
            if (backtrack(index + 1, emptyCells)) return true;
            
            // Backtrack
            board[row][col] = '.';
            rows[row].remove(digit);
            cols[col].remove(digit);
            boxes[boxIndex].remove(digit);
        }
    }
    return false;
}
```

#### Time Complexity
- **O(9^(empty cells))** with **much better constants**

#### Space Complexity
- **O(243)** for constraint sets + **O(empty cells)** recursion

### Approach 3: Most Constrained Variable (MCV) Heuristic

#### Key Insight
**Choose the empty cell with fewest possible values first** to reduce branching factor.

#### MCV Strategy
- **Count possibilities**: For each empty cell, count valid digits
- **Choose minimum**: Select cell with fewest options
- **Early failure detection**: If any cell has 0 possibilities, backtrack immediately

#### Algorithm
```java
private int[] findMostConstrainedCell(char[][] board) {
    int[] bestCell = null;
    int minPossibilities = 10;
    
    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            if (board[row][col] == '.') {
                int possibilities = countPossibilities(row, col);
                
                if (possibilities < minPossibilities) {
                    minPossibilities = possibilities;
                    bestCell = new int[]{row, col};
                    
                    if (possibilities == 0) return bestCell;  // Early failure
                }
            }
        }
    }
    return bestCell;
}
```

#### Performance Improvement
- **Dramatic reduction** in search tree size
- **Better pruning** through intelligent choice ordering
- **Faster convergence** to solutions or failures

#### Time Complexity
- **O(9^(empty cells))** but with **significantly smaller branching factor**

### Approach 4: Preprocessing with Naked Singles

#### Key Insight
**Fill obvious cells first** using logical deduction before backtracking.

#### Naked Singles Strategy
- **Single possibility**: If a cell has only one valid digit, fill it
- **Iterative application**: Repeat until no more naked singles found
- **Reduced search space**: Fewer cells for backtracking

#### Algorithm
```java
private boolean fillNakedSingles(char[][] board) {
    boolean progress = false;
    
    for (int row = 0; row < 9; row++) {
        for (int col = 0; col < 9; col++) {
            if (board[row][col] == '.') {
                List<Character> possibilities = getPossibleValues(row, col);
                
                if (possibilities.size() == 1) {
                    board[row][col] = possibilities.get(0);
                    progress = true;
                }
            }
        }
    }
    return progress;
}

public void solveSudokuWithPreprocessing(char[][] board) {
    // Apply naked singles repeatedly
    boolean progress = true;
    while (progress) {
        progress = fillNakedSingles(board);
    }
    
    // Use backtracking for remaining cells
    if (!isSolved(board)) {
        backtrackMCV(board);
    }
}
```

#### Benefits
- **Fewer backtracking steps**: Many cells filled logically
- **Easier puzzles**: Often solved completely by preprocessing
- **Hybrid approach**: Combines logic and search

## Implementation Strategies

### Cell Ordering Strategies

#### 1. **Linear Scan** (Simple)
```java
// Left-to-right, top-to-bottom scanning
for (int row = 0; row < 9; row++) {
    for (int col = 0; col < 9; col++) {
        if (board[row][col] == '.') {
            // Process this cell
        }
    }
}
```

#### 2. **Pre-collected Empty Cells** (Efficient)
```java
List<int[]> emptyCells = new ArrayList<>();
// Collect all empty cells first, then process by index
```

#### 3. **Most Constrained First** (Optimal)
```java
// Always choose the cell with minimum possibilities
int[] nextCell = findMostConstrainedCell(board);
```

### Box Index Calculation
```java
// Method 1: Mathematical formula
int boxIndex = (row / 3) * 3 + (col / 3);

// Method 2: Lookup table (faster for repeated access)
int[][] boxLookup = {
    {0,0,0,1,1,1,2,2,2},
    {0,0,0,1,1,1,2,2,2},
    {0,0,0,1,1,1,2,2,2},
    {3,3,3,4,4,4,5,5,5},
    {3,3,3,4,4,4,5,5,5},
    {3,3,3,4,4,4,5,5,5},
    {6,6,6,7,7,7,8,8,8},
    {6,6,6,7,7,7,8,8,8},
    {6,6,6,7,7,7,8,8,8}
};
```

## Advanced Optimization Techniques

### 1. **Constraint Propagation**
- **Hidden singles**: Only one cell in row/col/box can have specific digit
- **Naked pairs/triples**: Advanced logical deduction
- **Intersection removal**: Box-line interactions

### 2. **Value Ordering Heuristics**
- **Least constraining value**: Choose digit that eliminates fewest options for other cells
- **Most frequent first**: Try commonly occurring digits first

### 3. **Symmetry Breaking**
- **Canonical form**: Transform equivalent puzzles to reduce search
- **Isomorphism detection**: Recognize equivalent board states

## Common Mistakes

1. **Wrong box calculation**: Incorrect formula for 3×3 box indexing
2. **Constraint violations**: Not checking all three constraint types
3. **Backtracking bugs**: Not properly undoing state changes
4. **Base case errors**: Wrong termination condition
5. **In-place modifications**: Not handling board state correctly

## Edge Cases

1. **Invalid puzzles**: Multiple solutions or no solution
2. **Empty board**: All cells are '.'
3. **Already solved**: No empty cells
4. **Single empty cell**: Trivial case
5. **Contradictory constraints**: Impossible configurations

## Extensions & Variations

### 1. **Sudoku Validator** (LeetCode 36)
Check if a partially filled board is valid.

### 2. **Different Grid Sizes**
- **4×4 Sudoku**: 2×2 boxes with digits 1-4
- **16×16 Sudoku**: 4×4 boxes with digits 1-16
- **Irregular Sudoku**: Non-square regions

### 3. **Sudoku Variants**
- **Diagonal Sudoku**: Additional diagonal constraints
- **X-Sudoku**: Main diagonals must contain 1-9
- **Killer Sudoku**: Cage sum constraints

## Interview Tips

### Problem Recognition
- **Constraint satisfaction** → Think backtracking
- **Grid-based puzzles** → Often have geometric constraints
- **"Fill the grid"** → Consider systematic search

### Approach Strategy
1. **Start with basic backtracking**: Show understanding of core algorithm
2. **Discuss optimizations**: Constraint tracking, MCV heuristic
3. **Mention preprocessing**: Naked singles for easier puzzles
4. **Code cleanly**: Clear structure and meaningful names

### Code Organization
```java
public void solveSudoku(char[][] board) {
    // 1. Input validation
    // 2. Preprocessing (optional)
    // 3. Call backtracking
}

private boolean backtrack(/* parameters */) {
    // 1. Base case check
    // 2. Choose next cell
    // 3. Try valid values
    // 4. Recurse and backtrack
}

private boolean isValid(/* parameters */) {
    // Check row, column, and box constraints
}
```

## LeetCode Problems

### Core Problems:
- **[LeetCode 37 - Sudoku Solver](https://leetcode.com/problems/sudoku-solver/)** ⭐ (This problem)
- **[LeetCode 36 - Valid Sudoku](https://leetcode.com/problems/valid-sudoku/)** (Validation only)

### Related Constraint Satisfaction:
- **[LeetCode 51 - N-Queens](https://leetcode.com/problems/n-queens/)** (2D constraint satisfaction)
- **[LeetCode 52 - N-Queens II](https://leetcode.com/problems/n-queens-ii/)** (Count solutions)
- **[LeetCode 79 - Word Search](https://leetcode.com/problems/word-search/)** (2D grid backtracking)
- **[LeetCode 22 - Generate Parentheses](https://leetcode.com/problems/generate-parentheses/)** (Constraint-based generation)

### Advanced Grid Problems:
- **[LeetCode 1239 - Maximum Length of Concatenated String](https://leetcode.com/problems/maximum-length-of-a-concatenated-string-with-unique-characters/)** (Constraint optimization)
- **[LeetCode 980 - Unique Paths III](https://leetcode.com/problems/unique-paths-iii/)** (Grid traversal with constraints)

### Difficulty Progression:
1. **Start with**: LeetCode 36 (Valid Sudoku) - Learn constraint checking
2. **Core problem**: LeetCode 37 (Sudoku Solver) - Full backtracking
3. **Advanced**: LeetCode 51 (N-Queens) - Different constraint types
4. **Expert**: Custom variants with additional constraints

## Complexity Analysis Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Basic Backtracking** | O(9^k) | O(1) | **Learning, simple implementation** |
| **Constraint Tracking** | O(9^k) | O(243) | **Better performance, cleaner code** |
| **MCV Heuristic** | O(9^k) | O(243) | **Optimal search ordering** |
| **With Preprocessing** | O(81) + O(9^k) | O(243) | **Real-world efficiency** |

*where k = number of empty cells*

## Real-World Applications

1. **Puzzle games**: Sudoku apps and generators
2. **Constraint programming**: General CSP solvers
3. **Resource allocation**: Scheduling with constraints
4. **Circuit design**: Component placement with rules
5. **Timetabling**: Course/exam scheduling systems

## Mathematical Insights

### Sudoku Mathematics
- **Total valid Sudokus**: ~6.67 × 10^21 (completed grids)
- **Minimum clues**: 17 clues needed for unique solution
- **Symmetry groups**: 3,359,232 essentially different puzzles
- **Complexity class**: NP-complete for generalized n×n Sudoku

### Search Statistics
- **Average branching factor**: ~3-4 with good heuristics
- **Solution depth**: Number of empty cells
- **Pruning effectiveness**: >99% of search tree eliminated

**Remember**: Sudoku Solver demonstrates the power of **backtracking with intelligent constraint management** and shows how **domain-specific optimizations** can dramatically improve performance in constraint satisfaction problems! 