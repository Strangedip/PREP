# N-Queens Problem

## Problem Statement
The **N-Queens puzzle** is the problem of placing `N` chess queens on an `N×N` chessboard so that **no two queens attack each other**.

Given an integer `N`, return **all distinct solutions** to the N-Queens puzzle. Each solution contains a distinct board configuration where `'Q'` indicates a queen and `'.'` indicates an empty space.

**Examples:**
```
Input: n = 4
Output: [
  [".Q..",   // Solution 1
   "...Q",
   "Q...",
   "..Q."],
  
  ["..Q.",   // Solution 2  
   "Q...",
   "...Q",
   ".Q.."]
]

Input: n = 1
Output: [["Q"]]

Input: n = 2
Output: []  // No solution exists
Input: n = 3  
Output: []  // No solution exists
```

## Problem Analysis

### Core Insight
This is a **classic constraint satisfaction problem** solved using **backtracking**:
- **Constraints**: No two queens can attack each other
- **Attack patterns**: Same row, column, or diagonal
- **Search space**: N! possible arrangements (factorial complexity)

### Queen Attack Patterns
Queens attack in **4 directions**:
1. **Horizontal**: Same row
2. **Vertical**: Same column  
3. **Diagonal (/)**: Same `row - col` value
4. **Diagonal (\)**: Same `row + col` value

### Mathematical Properties
- **Solutions exist** only for N = 1 or N ≥ 4
- **Solution count** grows exponentially: 1, 0, 0, 2, 10, 4, 40, 92, 352...
- **Asymptotic growth**: Approximately N! / (e^π√N)

## Approaches

### Approach 1: Basic Backtracking ⭐ (Most Intuitive)

#### Key Insight
**Place queens row by row** and backtrack when conflicts arise.

#### Algorithm
1. **Start from row 0**: Try placing queen in each column
2. **Check safety**: Ensure no conflicts with previously placed queens
3. **Recursive placement**: Move to next row if placement is safe
4. **Backtrack**: Remove queen and try next position if no solution found
5. **Base case**: When all queens placed successfully, record solution

#### Conflict Detection
```java
private boolean isSafe(char[][] board, int row, int col) {
    // Check column conflicts
    for (int i = 0; i < row; i++) {
        if (board[i][col] == 'Q') return false;
    }
    
    // Check diagonal conflicts (/)
    for (int i = row-1, j = col-1; i >= 0 && j >= 0; i--, j--) {
        if (board[i][j] == 'Q') return false;
    }
    
    // Check diagonal conflicts (\)
    for (int i = row-1, j = col+1; i >= 0 && j < n; i--, j++) {
        if (board[i][j] == 'Q') return false;
    }
    
    return true;
}
```

#### Time Complexity
- **O(N!)** - In worst case, explore all permutations
- **Pruning reduces actual complexity** significantly

#### Space Complexity
- **O(N²)** for the board + **O(N)** for recursion stack

### Approach 2: Optimized with Sets ⭐ (Clean & Efficient)

#### Key Insight
**Track conflicts using sets** instead of scanning the board repeatedly.

#### Optimization Strategy
- **Column conflicts**: Set of occupied columns
- **Diagonal conflicts (/)**: Set of `row - col` values  
- **Diagonal conflicts (\)**: Set of `row + col` values
- **O(1) conflict checking** instead of O(N)

#### Algorithm
```java
private void backtrack(int row, Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2) {
    if (row == n) {
        // Found complete solution
        return;
    }
    
    for (int col = 0; col < n; col++) {
        if (cols.contains(col) || 
            diag1.contains(row - col) || 
            diag2.contains(row + col)) {
            continue; // Conflict detected
        }
        
        // Make choice
        queens.add(col);
        cols.add(col);
        diag1.add(row - col);
        diag2.add(row + col);
        
        // Recurse
        backtrack(row + 1, cols, diag1, diag2);
        
        // Backtrack
        queens.remove(queens.size() - 1);
        cols.remove(col);
        diag1.remove(row - col);
        diag2.remove(row + col);
    }
}
```

#### Time Complexity
- **O(N!)** with better constants due to O(1) conflict checking

#### Space Complexity
- **O(N)** for sets and recursion stack

### Approach 3: Bitmask Optimization (Advanced)

#### Key Insight
**Use bit manipulation** to represent conflicts more efficiently.

#### Bit Representation
- **Column mask**: Bit i set if column i occupied
- **Diagonal masks**: Track diagonal conflicts using bit shifts
- **Available positions**: Use bitwise operations to find valid placements

#### Algorithm
```java
private void backtrack(int row, int cols, int diag1, int diag2) {
    if (row == n) {
        // Solution found
        return;
    }
    
    // Calculate available positions
    int available = ((1 << n) - 1) & (~(cols | diag1 | diag2));
    
    while (available != 0) {
        int col = available & (-available);  // Get rightmost bit
        available -= col;  // Remove this bit
        
        backtrack(row + 1,
                 cols | col,           // Add column conflict
                 (diag1 | col) << 1,   // Add diagonal conflict
                 (diag2 | col) >> 1);  // Add diagonal conflict
    }
}
```

#### Time Complexity
- **O(N!)** but with **fastest constant factors**

#### Space Complexity
- **O(N)** for recursion stack only

## Implementation Strategies

### Board Representation Options

#### 1. **2D Character Array** (Most intuitive)
```java
char[][] board = new char[n][n];
// Easy to visualize and debug
```

#### 2. **List of Column Positions** (Space efficient)
```java
List<Integer> queens = new ArrayList<>();
// queens[i] = column position of queen in row i
```

#### 3. **Bitmasks** (Fastest)
```java
int cols, diag1, diag2;
// Bit operations for conflict tracking
```

### Solution Construction

#### From 2D Board
```java
private List<String> constructSolution(char[][] board) {
    List<String> solution = new ArrayList<>();
    for (char[] row : board) {
        solution.add(new String(row));
    }
    return solution;
}
```

#### From Position Array
```java
private List<String> constructSolution(List<Integer> queens) {
    List<String> solution = new ArrayList<>();
    for (int row = 0; row < n; row++) {
        StringBuilder sb = new StringBuilder();
        for (int col = 0; col < n; col++) {
            sb.append(queens.get(row) == col ? 'Q' : '.');
        }
        solution.add(sb.toString());
    }
    return solution;
}
```

## Optimization Techniques

### 1. **Early Pruning**
- Check conflicts immediately before recursive calls
- Maintain constraint sets for O(1) conflict detection

### 2. **Symmetry Breaking**
- Place first queen in first half of first row
- Multiply result by 2 (doesn't work for all N)

### 3. **Bit Manipulation**
- Use bitwise operations for faster conflict tracking
- Leverage CPU's native bit operations

### 4. **Memory Optimization**
- Store only queen positions, not entire board
- Use iterative deepening for memory-constrained environments

## Variations & Extensions

### 1. **N-Queens II** (Count Only)
```java
public int totalNQueens(int n) {
    return countSolutions(0, 0, 0, 0);
}
```
- **Space**: O(1) - no need to store solutions
- **Faster**: No solution construction overhead

### 2. **Generalized N-Queens**
- Different piece types (bishops, rooks, knights)
- 3D boards (3D N-Queens)
- Toroidal boards (wraparound edges)

### 3. **Constrained N-Queens**
- Pre-placed queens (partial solutions)
- Forbidden squares
- Different board shapes

## Common Mistakes

1. **Row conflicts**: Forgetting that placing row by row eliminates row conflicts
2. **Index errors**: Diagonal calculations with wrong formulas
3. **Backtracking bugs**: Not properly undoing state changes
4. **Base case**: Wrong termination condition
5. **Solution construction**: Building solution at wrong time

## Interview Tips

### Problem Recognition
- **Constraint satisfaction** → Think backtracking
- **"All solutions"** → Explore entire search space
- **Board games** → Often have geometric constraints

### Approach Strategy
1. **Start simple**: Basic backtracking with 2D board
2. **Optimize gradually**: Move to sets, then bitmasks
3. **Explain pruning**: Why certain branches are eliminated
4. **Discuss complexity**: Factorial nature and pruning effects

### Code Organization
```java
public List<List<String>> solveNQueens(int n) {
    // 1. Initialize data structures
    // 2. Call recursive backtracking
    // 3. Return results
}

private void backtrack(/* state parameters */) {
    // 1. Base case check
    // 2. Try all valid choices
    // 3. Make choice, recurse, backtrack
}
```

## LeetCode Problems

### Core Problems:
- **[LeetCode 51 - N-Queens](https://leetcode.com/problems/n-queens/)** ⭐ (This problem)
- **[LeetCode 52 - N-Queens II](https://leetcode.com/problems/n-queens-ii/)** (Count solutions only)

### Related Backtracking Problems:
- **[LeetCode 46 - Permutations](https://leetcode.com/problems/permutations/)** (Basic backtracking)
- **[LeetCode 78 - Subsets](https://leetcode.com/problems/subsets/)** (Generate all subsets)
- **[LeetCode 39 - Combination Sum](https://leetcode.com/problems/combination-sum/)** (Backtracking with constraints)
- **[LeetCode 37 - Sudoku Solver](https://leetcode.com/problems/sudoku-solver/)** (2D constraint satisfaction)
- **[LeetCode 22 - Generate Parentheses](https://leetcode.com/problems/generate-parentheses/)** (Balanced constraints)

### Advanced Variations:
- **[LeetCode 1222 - Queens That Can Attack the King](https://leetcode.com/problems/queens-that-can-attack-the-king/)** (Queen attack simulation)
- **[LeetCode 999 - Available Captures for Rook](https://leetcode.com/problems/available-captures-for-rook/)** (Chess piece mechanics)

### Difficulty Progression:
1. **Beginner**: LeetCode 46 (Permutations) - Learn backtracking basics
2. **Intermediate**: LeetCode 51 (N-Queens) - Geometric constraints
3. **Advanced**: LeetCode 37 (Sudoku Solver) - Complex constraint systems

## Complexity Analysis Summary

| Approach | Time | Space | Best For |
|----------|------|-------|----------|
| **Basic Board** | O(N!) | O(N²) | **Learning & debugging** |
| **Sets Optimization** | O(N!) | O(N) | **Clean code & interviews** |
| **Bitmask** | O(N!) | O(N) | **Performance optimization** |
| **Count Only** | O(N!) | O(1) | **Space-constrained scenarios** |

## Real-World Applications

1. **Resource allocation**: Assigning non-conflicting resources
2. **Scheduling**: Arranging tasks with mutual exclusions
3. **VLSI design**: Placing components without interference
4. **Graph coloring**: Assigning colors to vertices
5. **Cryptography**: Constraint-based key generation

## Mathematical Insights

### Solution Counts for Small N:
- N=1: 1 solution
- N=2: 0 solutions  
- N=3: 0 solutions
- N=4: 2 solutions
- N=5: 10 solutions
- N=6: 4 solutions
- N=7: 40 solutions
- N=8: 92 solutions

### Why No Solutions for N=2,3?
- **N=2**: Any placement creates immediate conflict
- **N=3**: Mathematical impossibility due to limited space

**Remember**: N-Queens is the **quintessential backtracking problem** that teaches constraint satisfaction, pruning strategies, and optimization techniques essential for solving complex combinatorial problems! 