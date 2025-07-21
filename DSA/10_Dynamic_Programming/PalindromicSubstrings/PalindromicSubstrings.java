import java.util.*;

/**
 * Problem: Palindromic Substrings
 * 
 * Given a string s, return the number of palindromic substrings in it.
 * A string is a palindrome when it reads the same backward as forward.
 * A substring is a contiguous sequence of characters within the string.
 * 
 * Example 1:
 * Input: s = "abc"
 * Output: 3
 * Explanation: Three palindromic strings: "a", "b", "c".
 * 
 * Example 2:
 * Input: s = "aaa"
 * Output: 6
 * Explanation: Six palindromic strings: "a", "a", "a", "aa", "aa", "aaa".
 */
public class PalindromicSubstrings {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n^3) - Check all substrings O(n^2) and verify palindrome O(n)
     * Space Complexity: O(1)
     * 
     * Check every possible substring for palindrome property.
     */
    public int countSubstringsBruteForce(String s) {
        int count = 0;
        int n = s.length();
        
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (isPalindrome(s, i, j)) {
                    count++;
                }
            }
        }
        
        return count;
    }
    
    private boolean isPalindrome(String s, int left, int right) {
        while (left < right) {
            if (s.charAt(left) != s.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
    
    /**
     * APPROACH 2: EXPAND AROUND CENTERS
     * Time Complexity: O(n^2) - For each center, expand takes O(n) time
     * Space Complexity: O(1)
     * 
     * Expand around each possible center (characters and between characters).
     */
    public int countSubstringsExpandCenter(String s) {
        int count = 0;
        int n = s.length();
        
        for (int i = 0; i < n; i++) {
            // Odd length palindromes (center at i)
            count += expandAroundCenter(s, i, i);
            
            // Even length palindromes (center between i and i+1)
            count += expandAroundCenter(s, i, i + 1);
        }
        
        return count;
    }
    
    private int expandAroundCenter(String s, int left, int right) {
        int count = 0;
        
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            count++;
            left--;
            right++;
        }
        
        return count;
    }
    
    /**
     * APPROACH 3: DYNAMIC PROGRAMMING
     * Time Complexity: O(n^2)
     * Space Complexity: O(n^2)
     * 
     * Build DP table where dp[i][j] indicates if substring s[i...j] is palindrome.
     */
    public int countSubstringsDP(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        int count = 0;
        
        // Every single character is a palindrome
        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
            count++;
        }
        
        // Check for palindromes of length 2
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
                count++;
            }
        }
        
        // Check for palindromes of length 3 and more
        for (int len = 3; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                    count++;
                }
            }
        }
        
        return count;
    }
    
    /**
     * APPROACH 4: SPACE-OPTIMIZED DP
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     * 
     * Since we only need previous length results, optimize space usage.
     */
    public int countSubstringsOptimizedDP(String s) {
        int n = s.length();
        boolean[] prev = new boolean[n];
        boolean[] curr = new boolean[n];
        int count = 0;
        
        // Length 1 palindromes
        for (int i = 0; i < n; i++) {
            prev[i] = true;
            count++;
        }
        
        // Length 2 palindromes
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                curr[i] = true;
                count++;
            }
        }
        
        // Lengths 3 and above
        for (int len = 3; len <= n; len++) {
            boolean[] next = new boolean[n];
            
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                
                if (s.charAt(i) == s.charAt(j) && prev[i + 1]) {
                    next[i] = true;
                    count++;
                }
            }
            
            prev = curr;
            curr = next;
        }
        
        return count;
    }
    
    /**
     * APPROACH 5: MANACHER'S ALGORITHM (ADVANCED)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Linear time algorithm for finding all palindromes.
     */
    public int countSubstringsManacher(String s) {
        if (s == null || s.isEmpty()) return 0;
        
        // Preprocess string: "abc" -> "^#a#b#c#$"
        String processed = preprocess(s);
        int n = processed.length();
        int[] radius = new int[n];  // radius[i] = radius of palindrome centered at i
        
        int center = 0, right = 0;  // Current rightmost palindrome boundary
        
        for (int i = 1; i < n - 1; i++) {
            int mirror = 2 * center - i;  // Mirror of i with respect to center
            
            // If i is within current palindrome boundary
            if (i < right) {
                radius[i] = Math.min(right - i, radius[mirror]);
            }
            
            // Try to expand palindrome centered at i
            try {
                while (processed.charAt(i + radius[i] + 1) == processed.charAt(i - radius[i] - 1)) {
                    radius[i]++;
                }
            } catch (StringIndexOutOfBoundsException e) {
                // Reached boundary
            }
            
            // If palindrome centered at i extends past right, update center and right
            if (i + radius[i] > right) {
                center = i;
                right = i + radius[i];
            }
        }
        
        return countPalindromes(radius);
    }
    
    private String preprocess(String s) {
        StringBuilder sb = new StringBuilder("^");
        for (char c : s.toCharArray()) {
            sb.append("#").append(c);
        }
        sb.append("#$");
        return sb.toString();
    }
    
    private int countPalindromes(int[] radius) {
        int count = 0;
        for (int r : radius) {
            count += (r + 1) / 2;  // Each radius contributes (r+1)/2 palindromes
        }
        return count;
    }
    
    /**
     * VARIATION: LONGEST PALINDROMIC SUBSTRING
     * Time Complexity: O(n^2)
     * Space Complexity: O(1)
     * 
     * Returns the longest palindromic substring.
     */
    public String longestPalindromicSubstring(String s) {
        if (s == null || s.isEmpty()) return "";
        
        int start = 0, maxLen = 1;
        
        for (int i = 0; i < s.length(); i++) {
            // Check odd length palindromes
            int len1 = expandFromCenter(s, i, i);
            // Check even length palindromes
            int len2 = expandFromCenter(s, i, i + 1);
            
            int len = Math.max(len1, len2);
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }
        
        return s.substring(start, start + maxLen);
    }
    
    private int expandFromCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;  // Length of palindrome
    }
    
    /**
     * VARIATION: PALINDROMIC SUBSTRING COORDINATES
     * Time Complexity: O(n^2)
     * Space Complexity: O(k) where k is number of palindromes
     * 
     * Returns all palindromic substrings with their start and end indices.
     */
    public List<int[]> getAllPalindromicSubstrings(String s) {
        List<int[]> palindromes = new ArrayList<>();
        
        for (int i = 0; i < s.length(); i++) {
            // Odd length palindromes
            expandAndCollect(s, i, i, palindromes);
            // Even length palindromes
            expandAndCollect(s, i, i + 1, palindromes);
        }
        
        return palindromes;
    }
    
    private void expandAndCollect(String s, int left, int right, List<int[]> palindromes) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            palindromes.add(new int[]{left, right});
            left--;
            right++;
        }
    }
    
    /**
     * VARIATION: COUNT DISTINCT PALINDROMIC SUBSTRINGS
     * Time Complexity: O(n^2)
     * Space Complexity: O(n^2)
     * 
     * Returns count of distinct palindromic substrings.
     */
    public int countDistinctPalindromicSubstrings(String s) {
        Set<String> palindromes = new HashSet<>();
        
        for (int i = 0; i < s.length(); i++) {
            // Odd length palindromes
            expandAndAdd(s, i, i, palindromes);
            // Even length palindromes
            expandAndAdd(s, i, i + 1, palindromes);
        }
        
        return palindromes.size();
    }
    
    private void expandAndAdd(String s, int left, int right, Set<String> palindromes) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            palindromes.add(s.substring(left, right + 1));
            left--;
            right++;
        }
    }
    
    /**
     * VARIATION: MINIMUM CUTS FOR PALINDROME PARTITIONING
     * Time Complexity: O(n^2)
     * Space Complexity: O(n^2)
     * 
     * Returns minimum cuts needed to partition string into palindromes.
     */
    public int minCutsPalindromePartition(String s) {
        int n = s.length();
        boolean[][] isPalindrome = new boolean[n][n];
        
        // Build palindrome table
        for (int i = n - 1; i >= 0; i--) {
            for (int j = i; j < n; j++) {
                if (s.charAt(i) == s.charAt(j) && (j - i <= 2 || isPalindrome[i + 1][j - 1])) {
                    isPalindrome[i][j] = true;
                }
            }
        }
        
        // DP for minimum cuts
        int[] cuts = new int[n];
        for (int i = 0; i < n; i++) {
            cuts[i] = i;  // Maximum cuts needed
            
            for (int j = 0; j <= i; j++) {
                if (isPalindrome[j][i]) {
                    cuts[i] = (j == 0) ? 0 : Math.min(cuts[i], cuts[j - 1] + 1);
                }
            }
        }
        
        return cuts[n - 1];
    }
    
    /**
     * HELPER: Print all palindromic substrings
     */
    public void printAllPalindromes(String s) {
        System.out.println("All palindromic substrings in \"" + s + "\":");
        
        for (int i = 0; i < s.length(); i++) {
            // Odd length palindromes
            printExpandedPalindromes(s, i, i);
            // Even length palindromes
            printExpandedPalindromes(s, i, i + 1);
        }
    }
    
    private void printExpandedPalindromes(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            System.out.println("  \"" + s.substring(left, right + 1) + "\" [" + left + "," + right + "]");
            left--;
            right++;
        }
    }
    
    /**
     * HELPER: Visualize DP table
     */
    public void printDPTable(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        
        // Build DP table
        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
        }
        
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
            }
        }
        
        for (int len = 3; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                }
            }
        }
        
        // Print table
        System.out.println("Palindrome DP Table for \"" + s + "\":");
        System.out.print("    ");
        for (int j = 0; j < n; j++) {
            System.out.printf("%3d", j);
        }
        System.out.println();
        
        for (int i = 0; i < n; i++) {
            System.out.printf("%2d: ", i);
            for (int j = 0; j < n; j++) {
                if (j < i) {
                    System.out.print("  -");
                } else {
                    System.out.printf("%3s", dp[i][j] ? "T" : "F");
                }
            }
            System.out.println();
        }
    }
    
    /**
     * Test the implementations
     */
    public static void main(String[] args) {
        PalindromicSubstrings solution = new PalindromicSubstrings();
        
        // Test case 1
        String s1 = "aaa";
        System.out.println("=== Test Case 1: \"" + s1 + "\" ===");
        System.out.println("Brute Force: " + solution.countSubstringsBruteForce(s1));
        System.out.println("Expand Center: " + solution.countSubstringsExpandCenter(s1));
        System.out.println("DP: " + solution.countSubstringsDP(s1));
        System.out.println("Optimized DP: " + solution.countSubstringsOptimizedDP(s1));
        System.out.println("Manacher: " + solution.countSubstringsManacher(s1));
        System.out.println("Distinct palindromes: " + solution.countDistinctPalindromicSubstrings(s1));
        System.out.println("Longest palindrome: \"" + solution.longestPalindromicSubstring(s1) + "\"");
        System.out.println("Min cuts: " + solution.minCutsPalindromePartition(s1));
        System.out.println();
        solution.printAllPalindromes(s1);
        System.out.println();
        solution.printDPTable(s1);
        
        // Test case 2
        System.out.println("\n=== Test Case 2: \"abc\" ===");
        String s2 = "abc";
        System.out.println("Count: " + solution.countSubstringsExpandCenter(s2));
        System.out.println("Distinct: " + solution.countDistinctPalindromicSubstrings(s2));
        System.out.println("Longest: \"" + solution.longestPalindromicSubstring(s2) + "\"");
        
        // Test case 3: Complex example
        System.out.println("\n=== Test Case 3: \"abccba\" ===");
        String s3 = "abccba";
        System.out.println("Count: " + solution.countSubstringsExpandCenter(s3));
        System.out.println("Distinct: " + solution.countDistinctPalindromicSubstrings(s3));
        System.out.println("Longest: \"" + solution.longestPalindromicSubstring(s3) + "\"");
        solution.printAllPalindromes(s3);
        
        // Performance comparison
        System.out.println("\n=== Performance Test ===");
        String longString = "abcdefghijklmnopqrstuvwxyz".repeat(20);
        
        long startTime = System.currentTimeMillis();
        int result1 = solution.countSubstringsExpandCenter(longString);
        long endTime = System.currentTimeMillis();
        System.out.println("Expand Center: " + result1 + " (Time: " + (endTime - startTime) + " ms)");
        
        startTime = System.currentTimeMillis();
        int result2 = solution.countSubstringsDP(longString);
        endTime = System.currentTimeMillis();
        System.out.println("DP: " + result2 + " (Time: " + (endTime - startTime) + " ms)");
        
        startTime = System.currentTimeMillis();
        int result3 = solution.countSubstringsManacher(longString);
        endTime = System.currentTimeMillis();
        System.out.println("Manacher: " + result3 + " (Time: " + (endTime - startTime) + " ms)");
        
        System.out.println("Results match: " + (result1 == result2 && result2 == result3));
    }
} 