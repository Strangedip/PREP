import java.util.*;

/**
 * Problem: Longest Common Subsequence (LCS)
 * 
 * Given two strings text1 and text2, return the length of their longest common subsequence. 
 * If there is no common subsequence, return 0.
 * 
 * A subsequence of a string is a new string generated from the original string with some 
 * characters (can be none) deleted without changing the relative order of the remaining characters.
 * 
 * For example, "ace" is a subsequence of "abcde".
 * A common subsequence of two strings is a subsequence that is common to both strings.
 * 
 * Example:
 * Input: text1 = "abcde", text2 = "ace"
 * Output: 3
 * Explanation: The longest common subsequence is "ace" and its length is 3.
 * 
 * Example 2:
 * Input: text1 = "abc", text2 = "abc"
 * Output: 3
 * Explanation: The longest common subsequence is "abc" and its length is 3.
 * 
 * Example 3:
 * Input: text1 = "abc", text2 = "def"
 * Output: 0
 * Explanation: There is no such common subsequence, so the result is 0.
 */
public class LongestCommonSubsequence {
    
    /**
     * APPROACH 1: RECURSIVE (BRUTE FORCE)
     * Time Complexity: O(2^(m+n)) - Exponential due to overlapping subproblems
     * Space Complexity: O(m+n) - Recursion stack depth
     * 
     * Basic recursive solution that explores all possibilities.
     */
    public int longestCommonSubsequenceRecursive(String text1, String text2) {
        return lcsRecursive(text1, text2, 0, 0);
    }
    
    private int lcsRecursive(String text1, String text2, int i, int j) {
        // Base case: reached end of either string
        if (i == text1.length() || j == text2.length()) {
            return 0;
        }
        
        // If characters match, include in LCS
        if (text1.charAt(i) == text2.charAt(j)) {
            return 1 + lcsRecursive(text1, text2, i + 1, j + 1);
        }
        
        // If characters don't match, try both options
        int option1 = lcsRecursive(text1, text2, i + 1, j);     // Skip char from text1
        int option2 = lcsRecursive(text1, text2, i, j + 1);     // Skip char from text2
        
        return Math.max(option1, option2);
    }
    
    /**
     * APPROACH 2: MEMOIZATION (TOP-DOWN DP)
     * Time Complexity: O(m * n) - Each subproblem computed once
     * Space Complexity: O(m * n) - Memoization table + O(m+n) recursion stack
     * 
     * Optimizes recursive solution by caching results.
     */
    public int longestCommonSubsequenceMemo(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        Integer[][] memo = new Integer[m][n];
        return lcsMemo(text1, text2, 0, 0, memo);
    }
    
    private int lcsMemo(String text1, String text2, int i, int j, Integer[][] memo) {
        // Base case
        if (i == text1.length() || j == text2.length()) {
            return 0;
        }
        
        // Check memo
        if (memo[i][j] != null) {
            return memo[i][j];
        }
        
        int result;
        if (text1.charAt(i) == text2.charAt(j)) {
            result = 1 + lcsMemo(text1, text2, i + 1, j + 1, memo);
        } else {
            int option1 = lcsMemo(text1, text2, i + 1, j, memo);
            int option2 = lcsMemo(text1, text2, i, j + 1, memo);
            result = Math.max(option1, option2);
        }
        
        memo[i][j] = result;
        return result;
    }
    
    /**
     * APPROACH 3: TABULATION (BOTTOM-UP DP)
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     * 
     * Classic 2D DP approach building from smaller subproblems.
     */
    public int longestCommonSubsequenceDP(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        
        // dp[i][j] = LCS length of text1[0...i-1] and text2[0...j-1]
        int[][] dp = new int[m + 1][n + 1];
        
        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];  // Characters match
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);  // Take max
                }
            }
        }
        
        return dp[m][n];
    }
    
    /**
     * APPROACH 4: SPACE-OPTIMIZED DP
     * Time Complexity: O(m * n)
     * Space Complexity: O(min(m, n))
     * 
     * Since we only need previous row, we can optimize space.
     */
    public int longestCommonSubsequenceOptimized(String text1, String text2) {
        // Ensure text2 is the shorter string for optimization
        if (text1.length() < text2.length()) {
            return longestCommonSubsequenceOptimized(text2, text1);
        }
        
        int m = text1.length(), n = text2.length();
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    curr[j] = 1 + prev[j - 1];
                } else {
                    curr[j] = Math.max(prev[j], curr[j - 1]);
                }
            }
            // Swap arrays for next iteration
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }
        
        return prev[n];
    }
    
    /**
     * APPROACH 5: SINGLE ROW OPTIMIZATION
     * Time Complexity: O(m * n)
     * Space Complexity: O(n)
     * 
     * Further optimize by using only one array.
     */
    public int longestCommonSubsequenceSingleRow(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        
        // Ensure we iterate over the longer string
        if (m < n) {
            return longestCommonSubsequenceSingleRow(text2, text1);
        }
        
        int[] dp = new int[n + 1];
        
        for (int i = 1; i <= m; i++) {
            int prev = 0;  // This represents dp[i-1][j-1]
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];  // Store current dp[j] before updating
                
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[j] = 1 + prev;
                } else {
                    dp[j] = Math.max(dp[j], dp[j - 1]);
                }
                
                prev = temp;  // Update prev for next iteration
            }
        }
        
        return dp[n];
    }
    
    /**
     * VARIATION: GET ACTUAL LCS STRING
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     * 
     * Returns the actual longest common subsequence string.
     */
    public String getLongestCommonSubsequence(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        
        // Build DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        // Reconstruct LCS string by backtracking
        StringBuilder lcs = new StringBuilder();
        int i = m, j = n;
        
        while (i > 0 && j > 0) {
            if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                lcs.append(text1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        
        return lcs.reverse().toString();
    }
    
    /**
     * VARIATION: GET ALL LCS STRINGS
     * Time Complexity: O(m * n * 2^(min(m,n)))
     * Space Complexity: O(m * n + number of LCS)
     * 
     * Returns all possible longest common subsequences.
     */
    public List<String> getAllLCS(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        
        // Build DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        // Get all LCS using backtracking
        List<String> result = new ArrayList<>();
        getAllLCSHelper(text1, text2, m, n, dp, new StringBuilder(), result);
        return result;
    }
    
    private void getAllLCSHelper(String text1, String text2, int i, int j, 
                                int[][] dp, StringBuilder current, List<String> result) {
        if (i == 0 || j == 0) {
            result.add(current.reverse().toString());
            current.reverse(); // Restore for backtracking
            return;
        }
        
        if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
            current.append(text1.charAt(i - 1));
            getAllLCSHelper(text1, text2, i - 1, j - 1, dp, current, result);
            current.deleteCharAt(current.length() - 1); // Backtrack
        } else {
            if (dp[i - 1][j] == dp[i][j]) {
                getAllLCSHelper(text1, text2, i - 1, j, dp, current, result);
            }
            if (dp[i][j - 1] == dp[i][j]) {
                getAllLCSHelper(text1, text2, i, j - 1, dp, current, result);
            }
        }
    }
    
    /**
     * VARIATION: SHORTEST COMMON SUPERSEQUENCE LENGTH
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     * 
     * Length of shortest string that contains both strings as subsequences.
     */
    public int shortestCommonSupersequence(String text1, String text2) {
        int lcsLength = longestCommonSubsequenceDP(text1, text2);
        return text1.length() + text2.length() - lcsLength;
    }
    
    /**
     * VARIATION: EDIT DISTANCE USING LCS
     * Time Complexity: O(m * n)
     * Space Complexity: O(m * n)
     * 
     * Minimum operations to transform text1 to text2 using only insertions and deletions.
     */
    public int editDistanceUsingLCS(String text1, String text2) {
        int lcsLength = longestCommonSubsequenceDP(text1, text2);
        int deletions = text1.length() - lcsLength;
        int insertions = text2.length() - lcsLength;
        return deletions + insertions;
    }
    
    /**
     * HELPER: Print DP table for visualization
     */
    public void printDPTable(String text1, String text2) {
        int m = text1.length(), n = text2.length();
        int[][] dp = new int[m + 1][n + 1];
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        
        System.out.println("DP Table for LCS:");
        System.out.print("    ");
        for (char c : text2.toCharArray()) {
            System.out.printf("%3c", c);
        }
        System.out.println();
        
        for (int i = 0; i <= m; i++) {
            if (i == 0) {
                System.out.print("  ");
            } else {
                System.out.printf("%c ", text1.charAt(i - 1));
            }
            for (int j = 0; j <= n; j++) {
                System.out.printf("%3d", dp[i][j]);
            }
            System.out.println();
        }
    }
    
    /**
     * Test the implementations
     */
    public static void main(String[] args) {
        LongestCommonSubsequence solution = new LongestCommonSubsequence();
        
        // Test case 1
        String text1 = "abcde";
        String text2 = "ace";
        
        System.out.println("Text1: " + text1);
        System.out.println("Text2: " + text2);
        System.out.println();
        
        System.out.println("Recursive: " + solution.longestCommonSubsequenceRecursive(text1, text2));
        System.out.println("Memoization: " + solution.longestCommonSubsequenceMemo(text1, text2));
        System.out.println("DP (2D): " + solution.longestCommonSubsequenceDP(text1, text2));
        System.out.println("Space Optimized: " + solution.longestCommonSubsequenceOptimized(text1, text2));
        System.out.println("Single Row: " + solution.longestCommonSubsequenceSingleRow(text1, text2));
        
        System.out.println("Actual LCS: \"" + solution.getLongestCommonSubsequence(text1, text2) + "\"");
        
        System.out.println("All LCS: " + solution.getAllLCS(text1, text2));
        
        System.out.println("Shortest Common Supersequence Length: " + 
                          solution.shortestCommonSupersequence(text1, text2));
        
        System.out.println("Edit Distance (Insert/Delete only): " + 
                          solution.editDistanceUsingLCS(text1, text2));
        
        System.out.println();
        solution.printDPTable(text1, text2);
        
        // Test case 2
        System.out.println("\n=== Test Case 2 ===");
        String text3 = "abc";
        String text4 = "def";
        
        System.out.println("Text1: " + text3);
        System.out.println("Text2: " + text4);
        System.out.println("LCS Length: " + solution.longestCommonSubsequenceDP(text3, text4));
        System.out.println("Actual LCS: \"" + solution.getLongestCommonSubsequence(text3, text4) + "\"");
        
        // Performance comparison
        System.out.println("\n=== Performance Test ===");
        String longText1 = "AGGTAB".repeat(100);
        String longText2 = "GXTXAYB".repeat(100);
        
        long startTime = System.currentTimeMillis();
        int result1 = solution.longestCommonSubsequenceDP(longText1, longText2);
        long endTime = System.currentTimeMillis();
        System.out.println("2D DP: " + result1 + " (Time: " + (endTime - startTime) + " ms)");
        
        startTime = System.currentTimeMillis();
        int result2 = solution.longestCommonSubsequenceOptimized(longText1, longText2);
        endTime = System.currentTimeMillis();
        System.out.println("Optimized: " + result2 + " (Time: " + (endTime - startTime) + " ms)");
    }
} 