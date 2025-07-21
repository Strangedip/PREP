import java.util.*;

/**
 * Problem: Set Matrix Zeroes
 * 
 * Given an m x n integer matrix, if an element is 0, set its entire row and column to 0's.
 * You must do it in place.
 * 
 * Example:
 * Input: matrix = [[1,1,1],[1,0,1],[1,1,1]]
 * Output: [[1,0,1],[0,0,0],[1,0,1]]
 * 
 * Example 2:
 * Input: matrix = [[0,1,2,0],[3,4,5,2],[1,3,1,5]]
 * Output: [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
 */
public class SetMatrixZeroes {
    
    /**
     * APPROACH 1: EXTRA SPACE WITH SETS
     * Time Complexity: O(m * n)
     * Space Complexity: O(m + n)
     * 
     * Store the indices of rows and columns that need to be zeroed.
     * Then iterate through the matrix and set elements to zero.
     */
    public void setZeroesExtraSpace(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        
        Set<Integer> zeroRows = new HashSet<>();
        Set<Integer> zeroCols = new HashSet<>();
        
        // First pass: find all rows and columns that should be zero
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    zeroRows.add(i);
                    zeroCols.add(j);
                }
            }
        }
        
        // Second pass: set rows and columns to zero
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (zeroRows.contains(i) || zeroCols.contains(j)) {
                    matrix[i][j] = 0;
                }
            }
        }
    }
    
    /**
     * APPROACH 2: EXTRA SPACE WITH ARRAYS
     * Time Complexity: O(m * n)
     * Space Complexity: O(m + n)
     * 
     * Similar to approach 1 but using boolean arrays instead of sets.
     * Often more efficient due to better cache locality.
     */
    public void setZeroesExtraSpaceArrays(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        
        boolean[] zeroRows = new boolean[m];
        boolean[] zeroCols = new boolean[n];
        
        // First pass: mark rows and columns that should be zero
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    zeroRows[i] = true;
                    zeroCols[j] = true;
                }
            }
        }
        
        // Second pass: set marked rows and columns to zero
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (zeroRows[i] || zeroCols[j]) {
                    matrix[i][j] = 0;
                }
            }
        }
    }
    
    /**
     * APPROACH 3: CONSTANT SPACE USING FIRST ROW AND COLUMN
     * Time Complexity: O(m * n)
     * Space Complexity: O(1)
     * 
     * Use the first row and first column as markers to indicate which
     * rows and columns should be zeroed. Need special handling for
     * first row and first column themselves.
     */
    public void setZeroesConstantSpace(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        
        // Check if first row and first column need to be zeroed
        boolean firstRowZero = false;
        boolean firstColZero = false;
        
        // Check first row
        for (int j = 0; j < n; j++) {
            if (matrix[0][j] == 0) {
                firstRowZero = true;
                break;
            }
        }
        
        // Check first column
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) {
                firstColZero = true;
                break;
            }
        }
        
        // Use first row and column as markers
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;  // Mark row
                    matrix[0][j] = 0;  // Mark column
                }
            }
        }
        
        // Set zeros based on markers (skip first row and column)
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }
        
        // Handle first row
        if (firstRowZero) {
            for (int j = 0; j < n; j++) {
                matrix[0][j] = 0;
            }
        }
        
        // Handle first column
        if (firstColZero) {
            for (int i = 0; i < m; i++) {
                matrix[i][0] = 0;
            }
        }
    }
    
    /**
     * APPROACH 4: OPTIMIZED CONSTANT SPACE
     * Time Complexity: O(m * n)
     * Space Complexity: O(1)
     * 
     * Similar to approach 3 but uses a single variable to track
     * whether the first column should be zeroed.
     */
    public void setZeroesOptimized(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        
        // Use matrix[0][0] for first row, and a separate variable for first column
        boolean firstColZero = false;
        
        // Check if first column needs to be zeroed
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) {
                firstColZero = true;
                break;
            }
        }
        
        // Use first row and column as markers
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;  // Mark row
                    matrix[0][j] = 0;  // Mark column
                }
            }
        }
        
        // Set zeros based on markers (process from bottom-right to avoid conflicts)
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }
        
        // Handle first column
        if (firstColZero) {
            for (int i = 0; i < m; i++) {
                matrix[i][0] = 0;
            }
        }
    }
    
    /**
     * APPROACH 5: USING SENTINEL VALUES (Alternative)
     * Time Complexity: O(m * n)
     * Space Complexity: O(1)
     * 
     * Mark zeros with a sentinel value, then convert sentinels to zeros.
     * This approach assumes the matrix contains only positive integers.
     */
    public void setZeroesSentinel(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        int sentinel = Integer.MIN_VALUE;  // Assuming this value doesn't exist in matrix
        
        // First pass: mark cells that should be zero with sentinel value
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    // Mark entire row
                    for (int k = 0; k < n; k++) {
                        if (matrix[i][k] != 0) {
                            matrix[i][k] = sentinel;
                        }
                    }
                    // Mark entire column
                    for (int k = 0; k < m; k++) {
                        if (matrix[k][j] != 0) {
                            matrix[k][j] = sentinel;
                        }
                    }
                }
            }
        }
        
        // Second pass: convert sentinel values to zeros
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == sentinel) {
                    matrix[i][j] = 0;
                }
            }
        }
    }
    
    // Helper method to print matrix
    private void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
    
    // Helper method to copy matrix for testing
    private int[][] copyMatrix(int[][] original) {
        int m = original.length;
        int n = original[0].length;
        int[][] copy = new int[m][n];
        for (int i = 0; i < m; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, n);
        }
        return copy;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        SetMatrixZeroes solution = new SetMatrixZeroes();
        
        // Test case 1: 3x3 matrix with zero in middle
        int[][] matrix1 = {
            {1, 1, 1},
            {1, 0, 1},
            {1, 1, 1}
        };
        
        System.out.println("Test Case 1: Original matrix");
        solution.printMatrix(matrix1);
        
        int[][] copy1a = solution.copyMatrix(matrix1);
        solution.setZeroesExtraSpace(copy1a);
        System.out.println("Extra Space (Sets): ");
        solution.printMatrix(copy1a);
        
        int[][] copy1b = solution.copyMatrix(matrix1);
        solution.setZeroesExtraSpaceArrays(copy1b);
        System.out.println("Extra Space (Arrays): ");
        solution.printMatrix(copy1b);
        
        int[][] copy1c = solution.copyMatrix(matrix1);
        solution.setZeroesConstantSpace(copy1c);
        System.out.println("Constant Space: ");
        solution.printMatrix(copy1c);
        
        int[][] copy1d = solution.copyMatrix(matrix1);
        solution.setZeroesOptimized(copy1d);
        System.out.println("Optimized: ");
        solution.printMatrix(copy1d);
        System.out.println();
        
        // Test case 2: 3x4 matrix with multiple zeros
        int[][] matrix2 = {
            {0, 1, 2, 0},
            {3, 4, 5, 2},
            {1, 3, 1, 5}
        };
        
        System.out.println("Test Case 2: Original matrix");
        solution.printMatrix(matrix2);
        
        int[][] copy2a = solution.copyMatrix(matrix2);
        solution.setZeroesConstantSpace(copy2a);
        System.out.println("Constant Space Result: ");
        solution.printMatrix(copy2a);
        System.out.println();
        
        // Test case 3: Edge case - single element
        int[][] matrix3 = {{0}};
        System.out.println("Test Case 3: Single zero element");
        solution.printMatrix(matrix3);
        solution.setZeroesConstantSpace(matrix3);
        System.out.println("After processing: ");
        solution.printMatrix(matrix3);
        System.out.println();
        
        // Test case 4: Edge case - first row and column have zeros
        int[][] matrix4 = {
            {0, 1, 2},
            {3, 0, 5},
            {1, 3, 1}
        };
        
        System.out.println("Test Case 4: Zeros in first row and column");
        solution.printMatrix(matrix4);
        solution.setZeroesConstantSpace(matrix4);
        System.out.println("After processing: ");
        solution.printMatrix(matrix4);
    }
} 