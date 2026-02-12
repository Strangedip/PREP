import java.util.*;

/**
 * Problem: Implement strStr() / Substring Search
 * 
 * Given two strings needle and haystack, return the index of the first occurrence 
 * of needle in haystack, or -1 if needle is not part of haystack.
 * 
 * Clarification: What should we return when needle is an empty string? 
 * For the purpose of this problem, we will return 0 when needle is an empty string. 
 * This is consistent with C's strstr() and Java's indexOf().
 * 
 * Example:
 * Input: haystack = "hello", needle = "ll"
 * Output: 2
 * 
 * Example 2:
 * Input: haystack = "aaaaa", needle = "bba"
 * Output: -1
 */
public class ImplementStrStr {
    
    /**
     * APPROACH 1: BRUTE FORCE
     * Time Complexity: O(n*m) where n = haystack length, m = needle length
     * Space Complexity: O(1)
     * 
     * Check every possible starting position in haystack.
     */
    public int strStrBruteForce(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (haystack.length() < needle.length()) return -1;
        
        for (int i = 0; i <= haystack.length() - needle.length(); i++) {
            int j = 0;
            while (j < needle.length() && haystack.charAt(i + j) == needle.charAt(j)) {
                j++;
            }
            if (j == needle.length()) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
     * APPROACH 2: USING BUILT-IN METHOD (For Reference)
     * Time Complexity: O(n*m) typically, but optimized internally
     * Space Complexity: O(1)
     */
    public int strStrBuiltIn(String haystack, String needle) {
        return haystack.indexOf(needle);
    }
    
    /**
     * APPROACH 3: KMP ALGORITHM (Optimal)
     * Time Complexity: O(n + m)
     * Space Complexity: O(m)
     * 
     * Knuth-Morris-Pratt algorithm with failure function to avoid redundant comparisons.
     */
    public int strStrKMP(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (haystack.length() < needle.length()) return -1;
        
        // Build failure function (LPS array)
        int[] lps = buildLPSArray(needle);
        
        int i = 0; // index for haystack
        int j = 0; // index for needle
        
        while (i < haystack.length()) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;
            }
            
            if (j == needle.length()) {
                return i - j; // Found match
            } else if (i < haystack.length() && haystack.charAt(i) != needle.charAt(j)) {
                if (j > 0) {
                    j = lps[j - 1]; // Use failure function
                } else {
                    i++;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * Helper method to build LPS (Longest Proper Prefix which is also Suffix) array
     */
    private int[] buildLPSArray(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0; // length of the previous longest prefix suffix
        int i = 1;
        
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        
        return lps;
    }
    
    /**
     * APPROACH 4: RABIN-KARP ALGORITHM (Rolling Hash)
     * Time Complexity: O(n + m) average, O(n*m) worst case
     * Space Complexity: O(1)
     * 
     * Use rolling hash to compare substrings efficiently.
     */
    public int strStrRabinKarp(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (haystack.length() < needle.length()) return -1;
        
        int base = 256;
        int mod = 101; // A prime number
        int m = needle.length();
        int n = haystack.length();
        
        long needleHash = 0;
        long windowHash = 0;
        long h = 1;
        
        // Calculate h = base^(m-1) % mod
        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % mod;
        }
        
        // Calculate hash for needle and first window
        for (int i = 0; i < m; i++) {
            needleHash = (base * needleHash + needle.charAt(i)) % mod;
            windowHash = (base * windowHash + haystack.charAt(i)) % mod;
        }
        
        // Slide the window over haystack
        for (int i = 0; i <= n - m; i++) {
            // Check if hash values match
            if (needleHash == windowHash) {
                // Verify character by character to handle hash collisions
                if (haystack.substring(i, i + m).equals(needle)) {
                    return i;
                }
            }
            
            // Calculate hash for next window
            if (i < n - m) {
                windowHash = (base * (windowHash - haystack.charAt(i) * h) + haystack.charAt(i + m)) % mod;
                if (windowHash < 0) {
                    windowHash += mod;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * APPROACH 5: BOYER-MOORE ALGORITHM (Bad Character Heuristic)
     * Time Complexity: O(n*m) worst case, O(n/m) best case
     * Space Complexity: O(1) or O(alphabet_size)
     * 
     * Skip characters based on bad character heuristic.
     */
    public int strStrBoyerMoore(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (haystack.length() < needle.length()) return -1;
        
        int[] badChar = buildBadCharTable(needle);
        int n = haystack.length();
        int m = needle.length();
        
        int skip = 0;
        while (skip <= n - m) {
            int j = m - 1;
            
            // Compare from right to left
            while (j >= 0 && needle.charAt(j) == haystack.charAt(skip + j)) {
                j--;
            }
            
            if (j < 0) {
                return skip; // Pattern found
            } else {
                // Skip based on bad character heuristic
                skip += Math.max(1, j - badChar[haystack.charAt(skip + j)]);
            }
        }
        
        return -1;
    }
    
    /**
     * Helper method to build bad character table for Boyer-Moore
     */
    private int[] buildBadCharTable(String pattern) {
        int[] badChar = new int[256]; // ASCII characters
        Arrays.fill(badChar, -1);
        
        for (int i = 0; i < pattern.length(); i++) {
            badChar[pattern.charAt(i)] = i;
        }
        
        return badChar;
    }
    
    /**
     * APPROACH 6: TWO-POINTER TECHNIQUE
     * Time Complexity: O(n*m) worst case
     * Space Complexity: O(1)
     * 
     * Optimized brute force with early termination.
     */
    public int strStrTwoPointer(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (haystack.length() < needle.length()) return -1;
        
        int n = haystack.length();
        int m = needle.length();
        
        for (int i = 0; i <= n - m; i++) {
            if (haystack.charAt(i) == needle.charAt(0)) {
                int j = 0;
                while (j < m && i + j < n && haystack.charAt(i + j) == needle.charAt(j)) {
                    j++;
                }
                if (j == m) {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    /**
     * APPROACH 7: SUBSTRING WITH OPTIMIZATION
     * Time Complexity: O(n*m) worst case, but with early optimizations
     * Space Complexity: O(1)
     * 
     * Use substring comparison with length check optimization.
     */
    public int strStrOptimized(String haystack, String needle) {
        if (needle.isEmpty()) return 0;
        if (haystack.length() < needle.length()) return -1;
        
        int needleLen = needle.length();
        char firstChar = needle.charAt(0);
        char lastChar = needle.charAt(needleLen - 1);
        
        for (int i = 0; i <= haystack.length() - needleLen; i++) {
            // Quick checks before full comparison
            if (haystack.charAt(i) == firstChar && 
                haystack.charAt(i + needleLen - 1) == lastChar) {
                
                if (haystack.substring(i, i + needleLen).equals(needle)) {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        ImplementStrStr solution = new ImplementStrStr();
        
        // Test case 1: Normal case
        String haystack1 = "hello", needle1 = "ll";
        System.out.println("Test Case 1: haystack=\"" + haystack1 + "\", needle=\"" + needle1 + "\"");
        System.out.println("Brute Force: " + solution.strStrBruteForce(haystack1, needle1));
        System.out.println("KMP: " + solution.strStrKMP(haystack1, needle1));
        System.out.println("Rabin-Karp: " + solution.strStrRabinKarp(haystack1, needle1));
        System.out.println("Boyer-Moore: " + solution.strStrBoyerMoore(haystack1, needle1));
        System.out.println("Two Pointer: " + solution.strStrTwoPointer(haystack1, needle1));
        System.out.println("Optimized: " + solution.strStrOptimized(haystack1, needle1));
        System.out.println();
        
        // Test case 2: Not found
        String haystack2 = "aaaaa", needle2 = "bba";
        System.out.println("Test Case 2: haystack=\"" + haystack2 + "\", needle=\"" + needle2 + "\"");
        System.out.println("KMP: " + solution.strStrKMP(haystack2, needle2));
        System.out.println();
        
        // Test case 3: Empty needle
        String haystack3 = "hello", needle3 = "";
        System.out.println("Test Case 3: haystack=\"" + haystack3 + "\", needle=\"" + needle3 + "\" (empty)");
        System.out.println("Brute Force: " + solution.strStrBruteForce(haystack3, needle3));
        System.out.println();
        
        // Test case 4: Needle longer than haystack
        String haystack4 = "ab", needle4 = "abc";
        System.out.println("Test Case 4: haystack=\"" + haystack4 + "\", needle=\"" + needle4 + "\" (needle longer)");
        System.out.println("KMP: " + solution.strStrKMP(haystack4, needle4));
        System.out.println();
        
        // Test case 5: Repeated pattern
        String haystack5 = "mississippi", needle5 = "issip";
        System.out.println("Test Case 5: haystack=\"" + haystack5 + "\", needle=\"" + needle5 + "\"");
        System.out.println("KMP: " + solution.strStrKMP(haystack5, needle5));
        System.out.println("Boyer-Moore: " + solution.strStrBoyerMoore(haystack5, needle5));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(ImplementStrStr solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large test strings
        StringBuilder haystackBuilder = new StringBuilder();
        for (int i = 0; i < 100000; i++) {
            haystackBuilder.append((char)('a' + (i % 26)));
        }
        String largeHaystack = haystackBuilder.toString();
        String largeNeedle = "xyza"; // Pattern not in haystack for worst case
        
        // Test KMP (should be fastest for large inputs)
        long start = System.nanoTime();
        int result1 = solution.strStrKMP(largeHaystack, largeNeedle);
        long end = System.nanoTime();
        System.out.println("KMP: " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Rabin-Karp
        start = System.nanoTime();
        int result2 = solution.strStrRabinKarp(largeHaystack, largeNeedle);
        end = System.nanoTime();
        System.out.println("Rabin-Karp: " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Boyer-Moore
        start = System.nanoTime();
        int result3 = solution.strStrBoyerMoore(largeHaystack, largeNeedle);
        end = System.nanoTime();
        System.out.println("Boyer-Moore: " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Built-in (for comparison)
        start = System.nanoTime();
        int result4 = solution.strStrBuiltIn(largeHaystack, largeNeedle);
        end = System.nanoTime();
        System.out.println("Built-in indexOf: " + result4 + " (Time: " + (end - start) / 1000000.0 + " ms)");
    }
} 