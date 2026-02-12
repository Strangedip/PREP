import java.util.*;

/**
 * Problem: Spiral Matrix
 * 
 * Given an m x n matrix, return all elements of the matrix in spiral order.
 * 
 * Example:
 * Input: matrix = [[1,2,3],[4,5,6],[7,8,9]]
 * Output: [1,2,3,6,9,8,7,4,5]
 * 
 * Example 2:
 * Input: matrix = [[1,2,3,4],[5,6,7,8],[9,10,11,12]]
 * Output: [1,2,3,4,8,12,11,10,9,5,6,7]
 */
public class SpiralMatrix {
    
    /**
     * APPROACH 1: LAYER BY LAYER (Most Intuitive)
     * Time Complexity: O(m * n)
     * Space Complexity: O(1) - not counting output array
     * 
     * We process the matrix layer by layer from outside to inside.
     * Each layer has 4 sides: top, right, bottom, left.
     */
    public List<Integer> spiralOrderLayerByLayer(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return result;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        
        int top = 0, bottom = m - 1;
        int left = 0, right = n - 1;
        
        while (top <= bottom && left <= right) {
            // Traverse top row (left to right)
            for (int col = left; col <= right; col++) {
                result.add(matrix[top][col]);
            }
            top++;
            
            // Traverse right column (top to bottom)
            for (int row = top; row <= bottom; row++) {
                result.add(matrix[row][right]);
            }
            right--;
            
            // Traverse bottom row (right to left) - if we still have rows
            if (top <= bottom) {
                for (int col = right; col >= left; col--) {
                    result.add(matrix[bottom][col]);
                }
                bottom--;
            }
            
            // Traverse left column (bottom to top) - if we still have columns
            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    result.add(matrix[row][left]);
                }
                left++;
            }
        }
        
        return result;
    }
    
    /**
     * APPROACH 2: DIRECTION SIMULATION
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n) - for visited array
     * 
     * We simulate the movement: right -> down -> left -> up, changing direction
     * when we hit boundaries or visited cells.
     */
    public List<Integer> spiralOrderDirection(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return result;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        boolean[][] visited = new boolean[m][n];
        
        // Direction vectors: right, down, left, up
        int[] dr = {0, 1, 0, -1};
        int[] dc = {1, 0, -1, 0};
        
        int row = 0, col = 0, direction = 0;
        
        for (int i = 0; i < m * n; i++) {
            result.add(matrix[row][col]);
            visited[row][col] = true;
            
            // Calculate next position
            int nextRow = row + dr[direction];
            int nextCol = col + dc[direction];
            
            // Check if we need to change direction
            if (nextRow < 0 || nextRow >= m || nextCol < 0 || nextCol >= n || 
                visited[nextRow][nextCol]) {
                direction = (direction + 1) % 4;
                nextRow = row + dr[direction];
                nextCol = col + dc[direction];
            }
            
            row = nextRow;
            col = nextCol;
        }
        
        return result;
    }
    
    /**
     * APPROACH 3: RECURSIVE SPIRAL
     * Time Complexity: O(m * n)
     * Space Complexity: O(min(m, n)) - recursion depth
     * 
     * Recursively process each layer of the spiral.
     */
    public List<Integer> spiralOrderRecursive(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return result;
        }
        
        spiralHelper(matrix, 0, matrix.length - 1, 0, matrix[0].length - 1, result);
        return result;
    }
    
    private void spiralHelper(int[][] matrix, int top, int bottom, int left, int right, 
                             List<Integer> result) {
        if (top > bottom || left > right) {
            return;
        }
        
        // Single row
        if (top == bottom) {
            for (int col = left; col <= right; col++) {
                result.add(matrix[top][col]);
            }
            return;
        }
        
        // Single column
        if (left == right) {
            for (int row = top; row <= bottom; row++) {
                result.add(matrix[row][left]);
            }
            return;
        }
        
        // Traverse the outer layer
        // Top row
        for (int col = left; col < right; col++) {
            result.add(matrix[top][col]);
        }
        
        // Right column
        for (int row = top; row < bottom; row++) {
            result.add(matrix[row][right]);
        }
        
        // Bottom row
        for (int col = right; col > left; col--) {
            result.add(matrix[bottom][col]);
        }
        
        // Left column
        for (int row = bottom; row > top; row--) {
            result.add(matrix[row][left]);
        }
        
        // Recursively process inner layer
        spiralHelper(matrix, top + 1, bottom - 1, left + 1, right - 1, result);
    }
    
    /**
     * APPROACH 4: OPTIMIZED BOUNDARY TRACKING
     * Time Complexity: O(m * n)
     * Space Complexity: O(1) - not counting output array
     * 
     * Similar to approach 1 but with optimized boundary conditions.
     */
    public List<Integer> spiralOrderOptimized(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return result;
        }
        
        int m = matrix.length;
        int n = matrix[0].length;
        int totalElements = m * n;
        
        int top = 0, bottom = m - 1;
        int left = 0, right = n - 1;
        
        while (result.size() < totalElements) {
            // Traverse top row
            for (int col = left; col <= right && result.size() < totalElements; col++) {
                result.add(matrix[top][col]);
            }
            top++;
            
            // Traverse right column
            for (int row = top; row <= bottom && result.size() < totalElements; row++) {
                result.add(matrix[row][right]);
            }
            right--;
            
            // Traverse bottom row
            for (int col = right; col >= left && result.size() < totalElements; col--) {
                result.add(matrix[bottom][col]);
            }
            bottom--;
            
            // Traverse left column
            for (int row = bottom; row >= top && result.size() < totalElements; row--) {
                result.add(matrix[row][left]);
            }
            left++;
        }
        
        return result;
    }
    
    // Helper method to print matrix in a readable format
    private void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        SpiralMatrix solution = new SpiralMatrix();
        
        // Test case 1: 3x3 matrix
        int[][] matrix1 = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println("Test Case 1: 3x3 matrix");
        solution.printMatrix(matrix1);
        System.out.println("Layer by Layer: " + solution.spiralOrderLayerByLayer(matrix1));
        System.out.println("Direction: " + solution.spiralOrderDirection(matrix1));
        System.out.println("Recursive: " + solution.spiralOrderRecursive(matrix1));
        System.out.println("Optimized: " + solution.spiralOrderOptimized(matrix1));
        System.out.println();
        
        // Test case 2: 3x4 matrix (different dimensions)
        int[][] matrix2 = {
            {1, 2, 3, 4},
            {5, 6, 7, 8},
            {9, 10, 11, 12}
        };
        System.out.println("Test Case 2: 3x4 matrix");
        solution.printMatrix(matrix2);
        System.out.println("Layer by Layer: " + solution.spiralOrderLayerByLayer(matrix2));
        System.out.println("Direction: " + solution.spiralOrderDirection(matrix2));
        System.out.println("Recursive: " + solution.spiralOrderRecursive(matrix2));
        System.out.println("Optimized: " + solution.spiralOrderOptimized(matrix2));
        System.out.println();
        
        // Test case 3: Single row
        int[][] matrix3 = {{1, 2, 3, 4}};
        System.out.println("Test Case 3: Single row");
        solution.printMatrix(matrix3);
        System.out.println("Layer by Layer: " + solution.spiralOrderLayerByLayer(matrix3));
        System.out.println("Direction: " + solution.spiralOrderDirection(matrix3));
        System.out.println("Recursive: " + solution.spiralOrderRecursive(matrix3));
        System.out.println("Optimized: " + solution.spiralOrderOptimized(matrix3));
        System.out.println();
        
        // Test case 4: Single column
        int[][] matrix4 = {{1}, {2}, {3}};
        System.out.println("Test Case 4: Single column");
        solution.printMatrix(matrix4);
        System.out.println("Layer by Layer: " + solution.spiralOrderLayerByLayer(matrix4));
        System.out.println("Direction: " + solution.spiralOrderDirection(matrix4));
        System.out.println("Recursive: " + solution.spiralOrderRecursive(matrix4));
        System.out.println("Optimized: " + solution.spiralOrderOptimized(matrix4));
        System.out.println();
        
        // Test case 5: Single element
        int[][] matrix5 = {{42}};
        System.out.println("Test Case 5: Single element");
        solution.printMatrix(matrix5);
        System.out.println("Layer by Layer: " + solution.spiralOrderLayerByLayer(matrix5));
        System.out.println("Direction: " + solution.spiralOrderDirection(matrix5));
        System.out.println("Recursive: " + solution.spiralOrderRecursive(matrix5));
        System.out.println("Optimized: " + solution.spiralOrderOptimized(matrix5));
    }
} 