import java.util.*;

/**
 * Problem: Sudoku Solver
 * 
 * Write a program to solve a Sudoku puzzle by filling the empty cells.
 * 
 * A sudoku solution must satisfy all of the following rules:
 * 1. Each of the digits 1-9 must occur exactly once in each row.
 * 2. Each of the digits 1-9 must occur exactly once in each column.
 * 3. Each of the digits 1-9 must occur exactly once in each of the 9 3x3 sub-boxes of the grid.
 * 
 * The '.' character indicates empty cells.
 * 
 * Example:
 * Input: board = 
 * [["5","3",".",".","7",".",".",".","."],
 *  ["6",".",".","1","9","5",".",".","."],
 *  [".","9","8",".",".",".",".","6","."],
 *  ["8",".",".",".","6",".",".",".","3"],
 *  ["4",".",".","8",".","3",".",".","1"],
 *  ["7",".",".",".","2",".",".",".","6"],
 *  [".","6",".",".",".",".","2","8","."],
 *  [".",".",".","4","1","9",".",".","5"],
 *  [".",".",".",".","8",".",".","7","9"]]
 * 
 * Output: Solve the puzzle in-place (modify the input board)
 */
public class SudokuSolver {
    
    /**
     * APPROACH 1: BASIC BACKTRACKING
     * Time Complexity: O(9^(empty cells)) - worst case 9^81
     * Space Complexity: O(1) for board modification + O(81) for recursion stack
     * 
     * Basic backtracking approach that tries all possible values.
     */
    public void solveSudokuBasic(char[][] board) {
        backtrackBasic(board);
    }
    
    private boolean backtrackBasic(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    // Try digits 1-9
                    for (char digit = '1'; digit <= '9'; digit++) {
                        if (isValidPlacement(board, row, col, digit)) {
                            board[row][col] = digit;  // Make choice
                            
                            if (backtrackBasic(board)) {  // Recurse
                                return true;  // Solution found
                            }
                            
                            board[row][col] = '.';  // Backtrack
                        }
                    }
                    return false;  // No valid digit found
                }
            }
        }
        return true;  // All cells filled successfully
    }
    
    /**
     * Check if placing digit at (row, col) is valid
     */
    private boolean isValidPlacement(char[][] board, int row, int col, char digit) {
        // Check row
        for (int c = 0; c < 9; c++) {
            if (board[row][c] == digit) {
                return false;
            }
        }
        
        // Check column
        for (int r = 0; r < 9; r++) {
            if (board[r][col] == digit) {
                return false;
            }
        }
        
        // Check 3x3 sub-box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int r = boxRow; r < boxRow + 3; r++) {
            for (int c = boxCol; c < boxCol + 3; c++) {
                if (board[r][c] == digit) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 2: OPTIMIZED BACKTRACKING WITH CONSTRAINT TRACKING
     * Time Complexity: O(9^(empty cells)) but with better pruning
     * Space Complexity: O(1) for board + O(243) for constraint sets
     * 
     * Use sets to track available numbers for each row, column, and box.
     */
    public void solveSudokuOptimized(char[][] board) {
        // Initialize constraint tracking
        Set<Character>[] rows = new HashSet[9];
        Set<Character>[] cols = new HashSet[9];
        Set<Character>[] boxes = new HashSet[9];
        
        for (int i = 0; i < 9; i++) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            boxes[i] = new HashSet<>();
        }
        
        // Populate initial constraints
        List<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    emptyCells.add(new int[]{row, col});
                } else {
                    char digit = board[row][col];
                    rows[row].add(digit);
                    cols[col].add(digit);
                    boxes[getBoxIndex(row, col)].add(digit);
                }
            }
        }
        
        backtrackOptimized(board, emptyCells, 0, rows, cols, boxes);
    }
    
    private boolean backtrackOptimized(char[][] board, List<int[]> emptyCells, int index,
                                     Set<Character>[] rows, Set<Character>[] cols, Set<Character>[] boxes) {
        if (index == emptyCells.size()) {
            return true;  // All empty cells filled
        }
        
        int[] cell = emptyCells.get(index);
        int row = cell[0], col = cell[1];
        int boxIndex = getBoxIndex(row, col);
        
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
                if (backtrackOptimized(board, emptyCells, index + 1, rows, cols, boxes)) {
                    return true;
                }
                
                // Backtrack
                board[row][col] = '.';
                rows[row].remove(digit);
                cols[col].remove(digit);
                boxes[boxIndex].remove(digit);
            }
        }
        
        return false;
    }
    
    /**
     * APPROACH 3: MOST CONSTRAINED VARIABLE (MCV) HEURISTIC
     * Time Complexity: O(9^(empty cells)) with improved pruning
     * Space Complexity: O(1) for board + O(243) for constraints
     * 
     * Choose the empty cell with fewest possible values first.
     */
    public void solveSudokuMCV(char[][] board) {
        Set<Character>[] rows = new HashSet[9];
        Set<Character>[] cols = new HashSet[9];
        Set<Character>[] boxes = new HashSet[9];
        
        for (int i = 0; i < 9; i++) {
            rows[i] = new HashSet<>();
            cols[i] = new HashSet<>();
            boxes[i] = new HashSet<>();
        }
        
        // Initialize constraints
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] != '.') {
                    char digit = board[row][col];
                    rows[row].add(digit);
                    cols[col].add(digit);
                    boxes[getBoxIndex(row, col)].add(digit);
                }
            }
        }
        
        backtrackMCV(board, rows, cols, boxes);
    }
    
    private boolean backtrackMCV(char[][] board, Set<Character>[] rows, 
                                Set<Character>[] cols, Set<Character>[] boxes) {
        // Find the empty cell with minimum possible values
        int[] bestCell = findMostConstrainedCell(board, rows, cols, boxes);
        
        if (bestCell == null) {
            return true;  // No empty cells left
        }
        
        int row = bestCell[0], col = bestCell[1];
        int boxIndex = getBoxIndex(row, col);
        
        // Get possible values for this cell
        List<Character> possibleValues = getPossibleValues(row, col, rows, cols, boxes);
        
        for (char digit : possibleValues) {
            // Make choice
            board[row][col] = digit;
            rows[row].add(digit);
            cols[col].add(digit);
            boxes[boxIndex].add(digit);
            
            // Recurse
            if (backtrackMCV(board, rows, cols, boxes)) {
                return true;
            }
            
            // Backtrack
            board[row][col] = '.';
            rows[row].remove(digit);
            cols[col].remove(digit);
            boxes[boxIndex].remove(digit);
        }
        
        return false;
    }
    
    /**
     * Find the empty cell with minimum possible values (Most Constrained Variable)
     */
    private int[] findMostConstrainedCell(char[][] board, Set<Character>[] rows, 
                                        Set<Character>[] cols, Set<Character>[] boxes) {
        int[] bestCell = null;
        int minPossibilities = 10;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    List<Character> possibilities = getPossibleValues(row, col, rows, cols, boxes);
                    
                    if (possibilities.size() < minPossibilities) {
                        minPossibilities = possibilities.size();
                        bestCell = new int[]{row, col};
                        
                        if (minPossibilities == 0) {
                            return bestCell;  // No possibilities - early failure detection
                        }
                    }
                }
            }
        }
        
        return bestCell;
    }
    
    /**
     * Get possible values for a cell
     */
    private List<Character> getPossibleValues(int row, int col, Set<Character>[] rows, 
                                            Set<Character>[] cols, Set<Character>[] boxes) {
        List<Character> possibilities = new ArrayList<>();
        int boxIndex = getBoxIndex(row, col);
        
        for (char digit = '1'; digit <= '9'; digit++) {
            if (!rows[row].contains(digit) && 
                !cols[col].contains(digit) && 
                !boxes[boxIndex].contains(digit)) {
                possibilities.add(digit);
            }
        }
        
        return possibilities;
    }
    
    /**
     * APPROACH 4: NAKED SINGLES PREPROCESSING
     * Time Complexity: O(81) for preprocessing + O(9^(remaining cells))
     * Space Complexity: O(1)
     * 
     * Fill cells that have only one possible value before backtracking.
     */
    public void solveSudokuWithPreprocessing(char[][] board) {
        // Apply naked singles repeatedly
        boolean progress = true;
        while (progress) {
            progress = fillNakedSingles(board);
        }
        
        // If not completely solved, use backtracking
        if (!isSolved(board)) {
            solveSudokuMCV(board);
        }
    }
    
    /**
     * Fill cells that have only one possible value
     */
    private boolean fillNakedSingles(char[][] board) {
        boolean progress = false;
        
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    List<Character> possibilities = new ArrayList<>();
                    
                    for (char digit = '1'; digit <= '9'; digit++) {
                        if (isValidPlacement(board, row, col, digit)) {
                            possibilities.add(digit);
                        }
                    }
                    
                    if (possibilities.size() == 1) {
                        board[row][col] = possibilities.get(0);
                        progress = true;
                    }
                }
            }
        }
        
        return progress;
    }
    
    /**
     * Check if the board is completely solved
     */
    private boolean isSolved(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Get box index (0-8) for given row and column
     */
    private int getBoxIndex(int row, int col) {
        return (row / 3) * 3 + (col / 3);
    }
    
    /**
     * HELPER: Print the Sudoku board
     */
    public void printBoard(char[][] board) {
        for (int row = 0; row < 9; row++) {
            if (row % 3 == 0 && row != 0) {
                System.out.println("------+-------+------");
            }
            for (int col = 0; col < 9; col++) {
                if (col % 3 == 0 && col != 0) {
                    System.out.print("| ");
                }
                System.out.print(board[row][col] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * HELPER: Validate if the current board state is valid
     */
    public boolean isValidBoard(char[][] board) {
        // Check rows
        for (int row = 0; row < 9; row++) {
            Set<Character> seen = new HashSet<>();
            for (int col = 0; col < 9; col++) {
                char digit = board[row][col];
                if (digit != '.' && !seen.add(digit)) {
                    return false;
                }
            }
        }
        
        // Check columns
        for (int col = 0; col < 9; col++) {
            Set<Character> seen = new HashSet<>();
            for (int row = 0; row < 9; row++) {
                char digit = board[row][col];
                if (digit != '.' && !seen.add(digit)) {
                    return false;
                }
            }
        }
        
        // Check 3x3 boxes
        for (int box = 0; box < 9; box++) {
            Set<Character> seen = new HashSet<>();
            int boxRow = (box / 3) * 3;
            int boxCol = (box % 3) * 3;
            
            for (int row = boxRow; row < boxRow + 3; row++) {
                for (int col = boxCol; col < boxCol + 3; col++) {
                    char digit = board[row][col];
                    if (digit != '.' && !seen.add(digit)) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * HELPER: Create a copy of the board
     */
    private char[][] copyBoard(char[][] board) {
        char[][] copy = new char[9][9];
        for (int i = 0; i < 9; i++) {
            copy[i] = board[i].clone();
        }
        return copy;
    }
    
    /**
     * Test the implementations
     */
    public static void main(String[] args) {
        SudokuSolver solver = new SudokuSolver();
        
        // Test case 1: Easy puzzle
        char[][] board1 = {
            {'5','3','.','.','7','.','.','.','.'},
            {'6','.','.','1','9','5','.','.','.'},
            {'.','9','8','.','.','.','.','6','.'},
            {'8','.','.','.','6','.','.','.','3'},
            {'4','.','.','8','.','3','.','.','1'},
            {'7','.','.','.','2','.','.','.','6'},
            {'.','6','.','.','.','.','2','8','.'},
            {'.','.','.','4','1','9','.','.','5'},
            {'.','.','.','.','8','.','.','7','9'}
        };
        
        System.out.println("Original board:");
        solver.printBoard(board1);
        
        // Test basic approach
        char[][] testBoard1 = solver.copyBoard(board1);
        long startTime = System.currentTimeMillis();
        solver.solveSudokuBasic(testBoard1);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Solution (Basic): " + (endTime - startTime) + " ms");
        solver.printBoard(testBoard1);
        
        // Test optimized approach
        char[][] testBoard2 = solver.copyBoard(board1);
        startTime = System.currentTimeMillis();
        solver.solveSudokuOptimized(testBoard2);
        endTime = System.currentTimeMillis();
        
        System.out.println("Solution (Optimized): " + (endTime - startTime) + " ms");
        
        // Test MCV approach
        char[][] testBoard3 = solver.copyBoard(board1);
        startTime = System.currentTimeMillis();
        solver.solveSudokuMCV(testBoard3);
        endTime = System.currentTimeMillis();
        
        System.out.println("Solution (MCV): " + (endTime - startTime) + " ms");
        
        // Test preprocessing approach
        char[][] testBoard4 = solver.copyBoard(board1);
        startTime = System.currentTimeMillis();
        solver.solveSudokuWithPreprocessing(testBoard4);
        endTime = System.currentTimeMillis();
        
        System.out.println("Solution (Preprocessing): " + (endTime - startTime) + " ms");
        
        // Verify solution
        System.out.println("Solution is valid: " + solver.isValidBoard(testBoard1));
    }
} 