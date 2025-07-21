import java.util.*;

/**
 * Problem: N-Queens
 * 
 * The n-queens puzzle is the problem of placing n queens on an n×n chessboard 
 * such that no two queens attack each other.
 * 
 * Given an integer n, return all distinct solutions to the n-queens puzzle. 
 * You may return the answer in any order.
 * 
 * Each solution contains a distinct board configuration of the n-queens' placement, 
 * where 'Q' and '.' both indicate a queen and an empty space, respectively.
 * 
 * Example:
 * Input: n = 4
 * Output: [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
 * Explanation: There exist two distinct solutions to the 4-queens puzzle
 */
public class NQueens {
    
    /**
     * APPROACH 1: BACKTRACKING WITH BASIC VALIDATION
     * Time Complexity: O(N!) - At most N! possibilities to explore
     * Space Complexity: O(N^2) for the board + O(N) for recursion stack
     * 
     * Basic backtracking approach with simple conflict checking.
     */
    public List<List<String>> solveNQueensBasic(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];
        
        // Initialize board with '.'
        for (int i = 0; i < n; i++) {
            Arrays.fill(board[i], '.');
        }
        
        backtrackBasic(board, 0, result);
        return result;
    }
    
    private void backtrackBasic(char[][] board, int row, List<List<String>> result) {
        int n = board.length;
        
        // Base case: all queens placed successfully
        if (row == n) {
            result.add(constructSolution(board));
            return;
        }
        
        // Try placing queen in each column of current row
        for (int col = 0; col < n; col++) {
            if (isSafe(board, row, col)) {
                board[row][col] = 'Q';  // Make choice
                backtrackBasic(board, row + 1, result);  // Recurse
                board[row][col] = '.';  // Backtrack
            }
        }
    }
    
    /**
     * Check if placing queen at (row, col) is safe
     */
    private boolean isSafe(char[][] board, int row, int col) {
        int n = board.length;
        
        // Check column (no queen in same column above)
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q') {
                return false;
            }
        }
        
        // Check diagonal (top-left to bottom-right)
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 'Q') {
                return false;
            }
        }
        
        // Check diagonal (top-right to bottom-left)
        for (int i = row - 1, j = col + 1; i >= 0 && j < n; i--, j++) {
            if (board[i][j] == 'Q') {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 2: OPTIMIZED BACKTRACKING WITH BITMASKS
     * Time Complexity: O(N!) but with better pruning
     * Space Complexity: O(N) - No board needed, just track conflicts
     * 
     * Use bit manipulation to track column and diagonal conflicts efficiently.
     */
    public List<List<String>> solveNQueensOptimized(int n) {
        List<List<String>> result = new ArrayList<>();
        List<Integer> queens = new ArrayList<>();  // Store column positions
        
        backtrackOptimized(n, 0, 0, 0, 0, queens, result);
        return result;
    }
    
    private void backtrackOptimized(int n, int row, int cols, int diag1, int diag2,
                                  List<Integer> queens, List<List<String>> result) {
        if (row == n) {
            result.add(constructSolutionFromPositions(queens, n));
            return;
        }
        
        // Available positions (bitwise OR of all conflicts)
        int available = ((1 << n) - 1) & (~(cols | diag1 | diag2));
        
        while (available != 0) {
            // Get rightmost available position
            int col = available & (-available);
            available -= col;  // Remove this position
            
            queens.add(Integer.numberOfTrailingZeros(col));  // Convert bit to column index
            
            backtrackOptimized(n, row + 1,
                             cols | col,                    // Add column conflict
                             (diag1 | col) << 1,          // Add diagonal conflict (/)
                             (diag2 | col) >> 1,          // Add diagonal conflict (\)
                             queens, result);
            
            queens.remove(queens.size() - 1);  // Backtrack
        }
    }
    
    /**
     * APPROACH 3: BACKTRACKING WITH SETS (CLEAN AND INTUITIVE)
     * Time Complexity: O(N!)
     * Space Complexity: O(N) for the sets
     * 
     * Use sets to track occupied columns and diagonals for cleaner code.
     */
    public List<List<String>> solveNQueensSets(int n) {
        List<List<String>> result = new ArrayList<>();
        List<Integer> queens = new ArrayList<>();
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>();  // row - col
        Set<Integer> diag2 = new HashSet<>();  // row + col
        
        backtrackSets(n, 0, queens, cols, diag1, diag2, result);
        return result;
    }
    
    private void backtrackSets(int n, int row, List<Integer> queens,
                              Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2,
                              List<List<String>> result) {
        if (row == n) {
            result.add(constructSolutionFromPositions(queens, n));
            return;
        }
        
        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || diag1.contains(row - col) || diag2.contains(row + col)) {
                continue;  // Position conflicts
            }
            
            // Make choice
            queens.add(col);
            cols.add(col);
            diag1.add(row - col);
            diag2.add(row + col);
            
            // Recurse
            backtrackSets(n, row + 1, queens, cols, diag1, diag2, result);
            
            // Backtrack
            queens.remove(queens.size() - 1);
            cols.remove(col);
            diag1.remove(row - col);
            diag2.remove(row + col);
        }
    }
    
    /**
     * VARIATION: COUNT ONLY (N-Queens II)
     * Time Complexity: O(N!)
     * Space Complexity: O(N)
     * 
     * Just count the number of solutions without storing them.
     */
    public int totalNQueens(int n) {
        return countSolutions(n, 0, 0, 0, 0);
    }
    
    private int countSolutions(int n, int row, int cols, int diag1, int diag2) {
        if (row == n) {
            return 1;  // Found a solution
        }
        
        int count = 0;
        int available = ((1 << n) - 1) & (~(cols | diag1 | diag2));
        
        while (available != 0) {
            int col = available & (-available);
            available -= col;
            
            count += countSolutions(n, row + 1,
                                  cols | col,
                                  (diag1 | col) << 1,
                                  (diag2 | col) >> 1);
        }
        
        return count;
    }
    
    /**
     * Helper method to construct solution from board
     */
    private List<String> constructSolution(char[][] board) {
        List<String> solution = new ArrayList<>();
        for (char[] row : board) {
            solution.add(new String(row));
        }
        return solution;
    }
    
    /**
     * Helper method to construct solution from queen positions
     */
    private List<String> constructSolutionFromPositions(List<Integer> queens, int n) {
        List<String> solution = new ArrayList<>();
        
        for (int row = 0; row < n; row++) {
            StringBuilder sb = new StringBuilder();
            for (int col = 0; col < n; col++) {
                if (queens.get(row) == col) {
                    sb.append('Q');
                } else {
                    sb.append('.');
                }
            }
            solution.add(sb.toString());
        }
        
        return solution;
    }
    
    /**
     * HELPER: Print board in readable format
     */
    public void printSolution(List<String> solution) {
        System.out.println("Solution:");
        for (String row : solution) {
            System.out.println(row);
        }
        System.out.println();
    }
    
    /**
     * HELPER: Print all solutions
     */
    public void printAllSolutions(List<List<String>> solutions) {
        System.out.println("Total solutions found: " + solutions.size());
        for (int i = 0; i < solutions.size(); i++) {
            System.out.println("Solution " + (i + 1) + ":");
            printSolution(solutions.get(i));
        }
    }
    
    /**
     * Test the implementations
     */
    public static void main(String[] args) {
        NQueens solution = new NQueens();
        
        // Test with N = 4
        System.out.println("=== N-Queens for N = 4 ===");
        
        List<List<String>> result1 = solution.solveNQueensBasic(4);
        System.out.println("Basic approach - Solutions found: " + result1.size());
        solution.printAllSolutions(result1);
        
        List<List<String>> result2 = solution.solveNQueensOptimized(4);
        System.out.println("Optimized approach - Solutions found: " + result2.size());
        
        List<List<String>> result3 = solution.solveNQueensSets(4);
        System.out.println("Sets approach - Solutions found: " + result3.size());
        
        System.out.println("Count only: " + solution.totalNQueens(4));
        
        // Test with different values of N
        System.out.println("\n=== Solution counts for different N ===");
        for (int n = 1; n <= 8; n++) {
            long startTime = System.currentTimeMillis();
            int count = solution.totalNQueens(n);
            long endTime = System.currentTimeMillis();
            System.out.printf("N = %d: %d solutions (Time: %d ms)%n", 
                            n, count, endTime - startTime);
        }
    }
} 