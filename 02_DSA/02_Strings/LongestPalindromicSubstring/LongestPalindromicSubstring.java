import java.util.*;

/**
 * Problem: Longest Palindromic Substring
 * 
 * Given a string s, return the longest palindromic substring in s.
 * 
 * Example:
 * Input: s = "babad"
 * Output: "bab"
 * Note: "aba" is also a valid answer.
 * 
 * Example 2:
 * Input: s = "cbbd"
 * Output: "bb"
 * 
 * Constraints:
 * - 1 <= s.length <= 1000
 * - s consist of only digits and English letters.
 */
public class LongestPalindromicSubstring {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n³)
     * Space Complexity: O(1)
     * 
     * Check every possible substring to see if it's a palindrome.
     */
    public String longestPalindromeBruteForce(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        
        String longest = "";
        
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                String substring = s.substring(i, j + 1);
                if (isPalindrome(substring) && substring.length() > longest.length()) {
                    longest = substring;
                }
            }
        }
        
        return longest;
    }
    
    /**
     * Helper method to check if a string is palindrome
     */
    private boolean isPalindrome(String str) {
        int left = 0, right = str.length() - 1;
        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    
    /**
     * APPROACH 2: EXPAND AROUND CENTERS (Optimal for most cases)
     * Time Complexity: O(n²)
     * Space Complexity: O(1)
     * 
     * For each possible center, expand outward to find the longest palindrome.
     * Handle both odd and even length palindromes.
     */
    public String longestPalindromeExpandCenter(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        
        int start = 0, maxLen = 1;
        
        for (int i = 0; i < s.length(); i++) {
            // Check for odd length palindromes (center at i)
            int len1 = expandAroundCenter(s, i, i);
            
            // Check for even length palindromes (center between i and i+1)
            int len2 = expandAroundCenter(s, i, i + 1);
            
            int len = Math.max(len1, len2);
            
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }
        
        return s.substring(start, start + maxLen);
    }
    
    /**
     * Helper method to expand around center and return palindrome length
     */
    private int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }
    
    /**
     * APPROACH 3: DYNAMIC PROGRAMMING
     * Time Complexity: O(n²)
     * Space Complexity: O(n²)
     * 
     * Build a table where dp[i][j] represents whether substring from i to j is palindrome.
     */
    public String longestPalindromeDP(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int start = 0, maxLen = 1;
        
        // Every single character is a palindrome
        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }
        
        // Check for palindromes of length 2
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
                start = i;
                maxLen = 2;
            }
        }
        
        // Check for palindromes of length 3 and more
        for (int len = 3; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                    if (len > maxLen) {
                        start = i;
                        maxLen = len;
                    }
                }
            }
        }
        
        return s.substring(start, start + maxLen);
    }
    
    /**
     * APPROACH 4: MANACHER'S ALGORITHM (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Linear time algorithm that transforms the string to handle even/odd cases uniformly.
     */
    public String longestPalindromeManacher(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        
        // Transform string to handle even length palindromes
        String transformed = preprocess(s);
        int n = transformed.length();
        int[] P = new int[n]; // Array to store palindrome lengths
        int center = 0, right = 0; // Center and right boundary of current palindrome
        
        int maxLen = 0, centerIndex = 0;
        
        for (int i = 0; i < n; i++) {
            // Mirror of i with respect to center
            int mirror = 2 * center - i;
            
            // If i is within the right boundary, we can use previously computed values
            if (i < right) {
                P[i] = Math.min(right - i, P[mirror]);
            }
            
            // Try to expand palindrome centered at i
            try {
                while (i + P[i] + 1 < n && i - P[i] - 1 >= 0 &&
                       transformed.charAt(i + P[i] + 1) == transformed.charAt(i - P[i] - 1)) {
                    P[i]++;
                }
            } catch (StringIndexOutOfBoundsException e) {
                // Do nothing, P[i] remains as is
            }
            
            // If palindrome centered at i extends past right, adjust center and right
            if (i + P[i] > right) {
                center = i;
                right = i + P[i];
            }
            
            // Update maximum length palindrome
            if (P[i] > maxLen) {
                maxLen = P[i];
                centerIndex = i;
            }
        }
        
        // Extract the longest palindrome from original string
        int start = (centerIndex - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }
    
    /**
     * Helper method to preprocess string for Manacher's algorithm
     */
    private String preprocess(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append('^'); // Start marker
        for (char c : s.toCharArray()) {
            sb.append('#').append(c);
        }
        sb.append('#').append('$'); // End marker
        return sb.toString();
    }
    
    /**
     * APPROACH 5: REVERSE AND FIND LCS (Alternative)
     * Time Complexity: O(n²)
     * Space Complexity: O(n²)
     * 
     * Find longest common substring between string and its reverse.
     * Note: This approach can give incorrect results for some cases.
     */
    public String longestPalindromeLCS(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        
        String reversed = new StringBuilder(s).reverse().toString();
        
        // Find LCS between s and reversed
        int n = s.length();
        int[][] dp = new int[n + 1][n + 1];
        int maxLen = 0, endPos = 0;
        
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                if (s.charAt(i - 1) == reversed.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    
                    // Check if this forms a valid palindrome
                    if (dp[i][j] > maxLen && isValidPalindrome(s, i - dp[i][j], i - 1)) {
                        maxLen = dp[i][j];
                        endPos = i - 1;
                    }
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        
        return s.substring(endPos - maxLen + 1, endPos + 1);
    }
    
    /**
     * Helper method to validate palindrome for LCS approach
     */
    private boolean isValidPalindrome(String s, int start, int end) {
        while (start < end) {
            if (s.charAt(start) != s.charAt(end)) {
                return false;
            }
            start++;
            end--;
        }
        return true;
    }
    
    /**
     * APPROACH 6: RECURSIVE WITH MEMOIZATION
     * Time Complexity: O(n²)
     * Space Complexity: O(n²)
     * 
     * Recursive approach with memoization to avoid redundant calculations.
     */
    public String longestPalindromeRecursive(String s) {
        if (s == null || s.length() < 2) {
            return s;
        }
        
        int n = s.length();
        Boolean[][] memo = new Boolean[n][n];
        int[] result = {0, 0}; // start, length
        
        // Find longest palindrome
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (isPalindromeRecursive(s, i, j, memo)) {
                    if (j - i + 1 > result[1]) {
                        result[0] = i;
                        result[1] = j - i + 1;
                    }
                }
            }
        }
        
        return s.substring(result[0], result[0] + result[1]);
    }
    
    /**
     * Helper method for recursive palindrome check with memoization
     */
    private boolean isPalindromeRecursive(String s, int left, int right, Boolean[][] memo) {
        if (left >= right) {
            return true;
        }
        
        if (memo[left][right] != null) {
            return memo[left][right];
        }
        
        boolean result = s.charAt(left) == s.charAt(right) && 
                        isPalindromeRecursive(s, left + 1, right - 1, memo);
        
        memo[left][right] = result;
        return result;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        LongestPalindromicSubstring solution = new LongestPalindromicSubstring();
        
        // Test case 1: Standard example
        String s1 = "babad";
        System.out.println("Test Case 1: \"" + s1 + "\"");
        System.out.println("Brute Force: \"" + solution.longestPalindromeBruteForce(s1) + "\"");
        System.out.println("Expand Center: \"" + solution.longestPalindromeExpandCenter(s1) + "\"");
        System.out.println("DP: \"" + solution.longestPalindromeDP(s1) + "\"");
        System.out.println("Manacher: \"" + solution.longestPalindromeManacher(s1) + "\"");
        System.out.println("Recursive: \"" + solution.longestPalindromeRecursive(s1) + "\"");
        System.out.println();
        
        // Test case 2: Even length palindrome
        String s2 = "cbbd";
        System.out.println("Test Case 2: \"" + s2 + "\"");
        System.out.println("Expand Center: \"" + solution.longestPalindromeExpandCenter(s2) + "\"");
        System.out.println("Manacher: \"" + solution.longestPalindromeManacher(s2) + "\"");
        System.out.println();
        
        // Test case 3: Single character
        String s3 = "a";
        System.out.println("Test Case 3: \"" + s3 + "\"");
        System.out.println("Expand Center: \"" + solution.longestPalindromeExpandCenter(s3) + "\"");
        System.out.println();
        
        // Test case 4: Entire string is palindrome
        String s4 = "racecar";
        System.out.println("Test Case 4: \"" + s4 + "\"");
        System.out.println("Expand Center: \"" + solution.longestPalindromeExpandCenter(s4) + "\"");
        System.out.println();
        
        // Test case 5: No palindrome longer than 1
        String s5 = "abcdef";
        System.out.println("Test Case 5: \"" + s5 + "\"");
        System.out.println("Expand Center: \"" + solution.longestPalindromeExpandCenter(s5) + "\"");
        System.out.println();
        
        // Performance comparison
        performanceTest(solution);
    }
    
    private static void performanceTest(LongestPalindromicSubstring solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate test string with known palindrome
        String testString = "abcdefghijklmnopqrstuvwxyzzyxwvutsrqponmlkjihgfedcba" +
                           "abcdefghijklmnopqrstuvwxyz";
        
        // Test expand around center (usually fastest for medium strings)
        long start = System.nanoTime();
        String result1 = solution.longestPalindromeExpandCenter(testString);
        long end = System.nanoTime();
        System.out.println("Expand Center: \"" + result1 + "\" (Time: " + 
                          (end - start) / 1000000.0 + " ms)");
        
        // Test Manacher's algorithm (fastest for long strings)
        start = System.nanoTime();
        String result2 = solution.longestPalindromeManacher(testString);
        end = System.nanoTime();
        System.out.println("Manacher: \"" + result2 + "\" (Time: " + 
                          (end - start) / 1000000.0 + " ms)");
        
        // Test DP approach
        start = System.nanoTime();
        String result3 = solution.longestPalindromeDP(testString);
        end = System.nanoTime();
        System.out.println("DP: \"" + result3 + "\" (Time: " + 
                          (end - start) / 1000000.0 + " ms)");
    }
} 