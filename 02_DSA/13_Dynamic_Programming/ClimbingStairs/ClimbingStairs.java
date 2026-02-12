import java.util.*;

/**
 * Problem: Climbing Stairs
 * 
 * You are climbing a staircase. It takes n steps to reach the top.
 * Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?
 * 
 * Example:
 * Input: n = 3
 * Output: 3
 * Explanation: Three ways to climb to the top:
 * 1. 1 step + 1 step + 1 step
 * 2. 1 step + 2 steps  
 * 3. 2 steps + 1 step
 */
public class ClimbingStairs {
    
    /**
     * APPROACH 1: RECURSIVE (BRUTE FORCE)
     * Time Complexity: O(2^n)
     * Space Complexity: O(n) for recursion stack
     */
    public int climbStairsRecursive(int n) {
        if (n <= 1) return 1;
        return climbStairsRecursive(n - 1) + climbStairsRecursive(n - 2);
    }
    
    /**
     * APPROACH 2: MEMOIZATION (TOP-DOWN DP)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int climbStairsMemo(int n) {
        Map<Integer, Integer> memo = new HashMap<>();
        return climbStairsMemoHelper(n, memo);
    }
    
    private int climbStairsMemoHelper(int n, Map<Integer, Integer> memo) {
        if (n <= 1) return 1;
        
        if (memo.containsKey(n)) {
            return memo.get(n);
        }
        
        int result = climbStairsMemoHelper(n - 1, memo) + climbStairsMemoHelper(n - 2, memo);
        memo.put(n, result);
        return result;
    }
    
    /**
     * APPROACH 3: TABULATION (BOTTOM-UP DP)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public int climbStairsDP(int n) {
        if (n <= 1) return 1;
        
        int[] dp = new int[n + 1];
        dp[0] = 1;  // Base case: 0 steps
        dp[1] = 1;  // Base case: 1 step
        
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }
        
        return dp[n];
    }
    
    /**
     * APPROACH 4: SPACE OPTIMIZED (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     */
    public int climbStairsOptimal(int n) {
        if (n <= 1) return 1;
        
        int prev2 = 1;  // dp[i-2]
        int prev1 = 1;  // dp[i-1]
        
        for (int i = 2; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }
        
        return prev1;
    }
    
    /**
     * APPROACH 5: FIBONACCI FORMULA (MATHEMATICAL)
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public int climbStairsFibonacci(int n) {
        if (n <= 1) return 1;
        
        double sqrt5 = Math.sqrt(5);
        double phi = (1 + sqrt5) / 2;
        double psi = (1 - sqrt5) / 2;
        
        // F(n+1) = (phi^(n+1) - psi^(n+1)) / sqrt5
        return (int) Math.round((Math.pow(phi, n + 1) - Math.pow(psi, n + 1)) / sqrt5);
    }
    
    /**
     * APPROACH 6: MATRIX EXPONENTIATION
     * Time Complexity: O(log n)
     * Space Complexity: O(1)
     */
    public int climbStairsMatrix(int n) {
        if (n <= 1) return 1;
        
        int[][] base = {{1, 1}, {1, 0}};
        int[][] result = matrixPower(base, n);
        return result[0][0];
    }
    
    private int[][] matrixPower(int[][] matrix, int n) {
        int[][] result = {{1, 0}, {0, 1}}; // Identity matrix
        
        while (n > 0) {
            if (n % 2 == 1) {
                result = matrixMultiply(result, matrix);
            }
            matrix = matrixMultiply(matrix, matrix);
            n /= 2;
        }
        
        return result;
    }
    
    private int[][] matrixMultiply(int[][] a, int[][] b) {
        return new int[][]{
            {a[0][0] * b[0][0] + a[0][1] * b[1][0], a[0][0] * b[0][1] + a[0][1] * b[1][1]},
            {a[1][0] * b[0][0] + a[1][1] * b[1][0], a[1][0] * b[0][1] + a[1][1] * b[1][1]}
        };
    }
    
    // Test method
    public static void main(String[] args) {
        ClimbingStairs solution = new ClimbingStairs();
        
        // Test cases
        int[] testCases = {1, 2, 3, 4, 5, 10, 20};
        
        for (int n : testCases) {
            System.out.println("n = " + n);
            System.out.println("Recursive: " + (n <= 10 ? solution.climbStairsRecursive(n) : "Skip (too slow)"));
            System.out.println("Memoization: " + solution.climbStairsMemo(n));
            System.out.println("DP: " + solution.climbStairsDP(n));
            System.out.println("Optimal: " + solution.climbStairsOptimal(n));
            System.out.println("Fibonacci: " + solution.climbStairsFibonacci(n));
            System.out.println("Matrix: " + solution.climbStairsMatrix(n));
            System.out.println();
        }
    }
} 