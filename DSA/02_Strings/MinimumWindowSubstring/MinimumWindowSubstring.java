import java.util.*;

/**
 * Problem: Minimum Window Substring
 * 
 * Given two strings s and t of lengths m and n respectively, return the minimum window 
 * substring of s such that every character in t (including duplicates) is included in the window. 
 * If there is no such window in s that covers all characters in t, return the empty string "".
 * 
 * Note that If there is such a window, it is guaranteed that there will always be only one 
 * unique minimum window in s.
 * 
 * Example:
 * Input: s = "ADOBECODEBANC", t = "ABC"
 * Output: "BANC"
 * Explanation: The minimum window substring "BANC" includes 'A', 'B', and 'C' from string t.
 * 
 * Example 2:
 * Input: s = "a", t = "a"
 * Output: "a"
 * 
 * Example 3:
 * Input: s = "a", t = "aa"
 * Output: ""
 * Explanation: Both 'a's from t must be included in the window.
 * Since the largest window of s only has one 'a', return empty string.
 * 
 * Constraints:
 * - m == s.length
 * - n == t.length
 * - 1 <= m, n <= 10^5
 * - s and t consist of uppercase and lowercase English letters.
 */
public class MinimumWindowSubstring {
    
    /**
     * APPROACH 1: SLIDING WINDOW (Optimal)
     * Time Complexity: O(|s| + |t|)
     * Space Complexity: O(|s| + |t|) in worst case
     * 
     * Use sliding window technique with two pointers.
     * Expand right pointer to include characters, contract left pointer when valid.
     */
    public String minWindowOptimal(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }
        
        // Count characters in t
        Map<Character, Integer> targetCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
        }
        
        // Sliding window variables
        int left = 0, right = 0;
        int required = targetCount.size(); // Number of unique characters in t
        int formed = 0; // Number of unique characters in current window with desired frequency
        
        Map<Character, Integer> windowCount = new HashMap<>();
        
        // Result variables
        int minLen = Integer.MAX_VALUE;
        int minLeft = 0, minRight = 0;
        
        while (right < s.length()) {
            // Expand window by moving right pointer
            char rightChar = s.charAt(right);
            windowCount.put(rightChar, windowCount.getOrDefault(rightChar, 0) + 1);
            
            // Check if current character contributes to the desired count
            if (targetCount.containsKey(rightChar) && 
                windowCount.get(rightChar).intValue() == targetCount.get(rightChar).intValue()) {
                formed++;
            }
            
            // Contract window from left while it's valid
            while (left <= right && formed == required) {
                // Update minimum window if current is smaller
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minLeft = left;
                    minRight = right;
                }
                
                // Contract window by moving left pointer
                char leftChar = s.charAt(left);
                windowCount.put(leftChar, windowCount.get(leftChar) - 1);
                
                if (targetCount.containsKey(leftChar) && 
                    windowCount.get(leftChar) < targetCount.get(leftChar)) {
                    formed--;
                }
                
                left++;
            }
            
            right++;
        }
        
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minLeft, minRight + 1);
    }
    
    /**
     * APPROACH 2: SLIDING WINDOW WITH ARRAYS (Optimized for ASCII)
     * Time Complexity: O(|s| + |t|)
     * Space Complexity: O(1) - Fixed size arrays
     * 
     * Use arrays instead of HashMap for better performance with ASCII characters.
     */
    public String minWindowArray(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }
        
        // Count characters in t using array
        int[] targetCount = new int[128]; // ASCII characters
        int uniqueChars = 0;
        
        for (char c : t.toCharArray()) {
            if (targetCount[c] == 0) {
                uniqueChars++;
            }
            targetCount[c]++;
        }
        
        int[] windowCount = new int[128];
        int left = 0, right = 0;
        int formed = 0;
        
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        
        while (right < s.length()) {
            char rightChar = s.charAt(right);
            windowCount[rightChar]++;
            
            if (targetCount[rightChar] > 0 && windowCount[rightChar] == targetCount[rightChar]) {
                formed++;
            }
            
            while (left <= right && formed == uniqueChars) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }
                
                char leftChar = s.charAt(left);
                windowCount[leftChar]--;
                
                if (targetCount[leftChar] > 0 && windowCount[leftChar] < targetCount[leftChar]) {
                    formed--;
                }
                
                left++;
            }
            
            right++;
        }
        
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }
    
    /**
     * APPROACH 3: BRUTE FORCE (For Learning)
     * Time Complexity: O(|s|^2 * |t|)
     * Space Complexity: O(|t|)
     * 
     * Check every possible substring of s to see if it contains all characters of t.
     */
    public String minWindowBruteForce(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }
        
        String minWindow = "";
        int minLen = Integer.MAX_VALUE;
        
        for (int i = 0; i < s.length(); i++) {
            for (int j = i + t.length() - 1; j < s.length(); j++) {
                String substring = s.substring(i, j + 1);
                if (containsAllChars(substring, t) && substring.length() < minLen) {
                    minLen = substring.length();
                    minWindow = substring;
                }
            }
        }
        
        return minWindow;
    }
    
    /**
     * Helper method to check if window contains all characters of target
     */
    private boolean containsAllChars(String window, String target) {
        Map<Character, Integer> targetCount = new HashMap<>();
        for (char c : target.toCharArray()) {
            targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
        }
        
        Map<Character, Integer> windowCount = new HashMap<>();
        for (char c : window.toCharArray()) {
            windowCount.put(c, windowCount.getOrDefault(c, 0) + 1);
        }
        
        for (Map.Entry<Character, Integer> entry : targetCount.entrySet()) {
            char c = entry.getKey();
            int required = entry.getValue();
            if (windowCount.getOrDefault(c, 0) < required) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 4: SLIDING WINDOW WITH FILTERED STRING
     * Time Complexity: O(|s| + |t|)
     * Space Complexity: O(|s| + |t|)
     * 
     * Pre-filter s to only include characters that are in t.
     * This can be faster when t is much smaller than s.
     */
    public String minWindowFiltered(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }
        
        Map<Character, Integer> targetCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            targetCount.put(c, targetCount.getOrDefault(c, 0) + 1);
        }
        
        // Filter s to only include characters in t
        List<Pair> filteredS = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (targetCount.containsKey(c)) {
                filteredS.add(new Pair(i, c));
            }
        }
        
        if (filteredS.size() < t.length()) {
            return "";
        }
        
        int left = 0, right = 0;
        int required = targetCount.size();
        int formed = 0;
        
        Map<Character, Integer> windowCount = new HashMap<>();
        int minLen = Integer.MAX_VALUE;
        int minLeft = 0, minRight = 0;
        
        while (right < filteredS.size()) {
            char rightChar = filteredS.get(right).character;
            windowCount.put(rightChar, windowCount.getOrDefault(rightChar, 0) + 1);
            
            if (windowCount.get(rightChar).intValue() == targetCount.get(rightChar).intValue()) {
                formed++;
            }
            
            while (left <= right && formed == required) {
                int currentLen = filteredS.get(right).index - filteredS.get(left).index + 1;
                if (currentLen < minLen) {
                    minLen = currentLen;
                    minLeft = filteredS.get(left).index;
                    minRight = filteredS.get(right).index;
                }
                
                char leftChar = filteredS.get(left).character;
                windowCount.put(leftChar, windowCount.get(leftChar) - 1);
                
                if (windowCount.get(leftChar) < targetCount.get(leftChar)) {
                    formed--;
                }
                
                left++;
            }
            
            right++;
        }
        
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minLeft, minRight + 1);
    }
    
    /**
     * Helper class for filtered approach
     */
    private static class Pair {
        int index;
        char character;
        
        Pair(int index, char character) {
            this.index = index;
            this.character = character;
        }
    }
    
    /**
     * APPROACH 5: OPTIMIZED WITH EARLY TERMINATION
     * Time Complexity: O(|s| + |t|)
     * Space Complexity: O(|t|)
     * 
     * Add optimizations like early termination and better variable tracking.
     */
    public String minWindowOptimized(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }
        
        int[] targetCount = new int[128];
        int requiredChars = 0;
        
        // Count characters in t
        for (char c : t.toCharArray()) {
            if (targetCount[c] == 0) {
                requiredChars++;
            }
            targetCount[c]++;
        }
        
        int[] windowCount = new int[128];
        int left = 0, formedChars = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            windowCount[rightChar]++;
            
            if (targetCount[rightChar] > 0 && windowCount[rightChar] == targetCount[rightChar]) {
                formedChars++;
            }
            
            // Try to contract window
            while (formedChars == requiredChars) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                    
                    // Early termination: if we found a window of size |t|, it's optimal
                    if (minLen == t.length()) {
                        return s.substring(minStart, minStart + minLen);
                    }
                }
                
                char leftChar = s.charAt(left);
                windowCount[leftChar]--;
                
                if (targetCount[leftChar] > 0 && windowCount[leftChar] < targetCount[leftChar]) {
                    formedChars--;
                }
                
                left++;
            }
        }
        
        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        MinimumWindowSubstring solution = new MinimumWindowSubstring();
        
        // Test case 1: Standard example
        String s1 = "ADOBECODEBANC", t1 = "ABC";
        System.out.println("Test Case 1: s=\"" + s1 + "\", t=\"" + t1 + "\"");
        System.out.println("Optimal: \"" + solution.minWindowOptimal(s1, t1) + "\"");
        System.out.println("Array: \"" + solution.minWindowArray(s1, t1) + "\"");
        System.out.println("Filtered: \"" + solution.minWindowFiltered(s1, t1) + "\"");
        System.out.println("Optimized: \"" + solution.minWindowOptimized(s1, t1) + "\"");
        System.out.println("Brute Force: \"" + solution.minWindowBruteForce(s1, t1) + "\"");
        System.out.println();
        
        // Test case 2: Single character
        String s2 = "a", t2 = "a";
        System.out.println("Test Case 2: s=\"" + s2 + "\", t=\"" + t2 + "\"");
        System.out.println("Optimal: \"" + solution.minWindowOptimal(s2, t2) + "\"");
        System.out.println();
        
        // Test case 3: No valid window
        String s3 = "a", t3 = "aa";
        System.out.println("Test Case 3: s=\"" + s3 + "\", t=\"" + t3 + "\"");
        System.out.println("Optimal: \"" + solution.minWindowOptimal(s3, t3) + "\"");
        System.out.println();
        
        // Test case 4: Complex example with duplicates
        String s4 = "ADOBECODEBANC", t4 = "AABC";
        System.out.println("Test Case 4: s=\"" + s4 + "\", t=\"" + t4 + "\"");
        System.out.println("Optimal: \"" + solution.minWindowOptimal(s4, t4) + "\"");
        System.out.println();
        
        // Test case 5: Entire string is minimum window
        String s5 = "ABC", t5 = "ABC";
        System.out.println("Test Case 5: s=\"" + s5 + "\", t=\"" + t5 + "\"");
        System.out.println("Optimal: \"" + solution.minWindowOptimal(s5, t5) + "\"");
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(MinimumWindowSubstring solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large test strings
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sBuilder.append((char)('A' + (i % 26)));
        }
        String largeS = sBuilder.toString();
        String largeT = "ABCDEFGHIJ";
        
        long start, end;
        
        // Test optimal approach
        start = System.nanoTime();
        String result1 = solution.minWindowOptimal(largeS, largeT);
        end = System.nanoTime();
        System.out.println("Optimal: \"" + result1.substring(0, Math.min(10, result1.length())) + 
                          "...\" (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test array approach
        start = System.nanoTime();
        String result2 = solution.minWindowArray(largeS, largeT);
        end = System.nanoTime();
        System.out.println("Array: \"" + result2.substring(0, Math.min(10, result2.length())) + 
                          "...\" (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test filtered approach
        start = System.nanoTime();
        String result3 = solution.minWindowFiltered(largeS, largeT);
        end = System.nanoTime();
        System.out.println("Filtered: \"" + result3.substring(0, Math.min(10, result3.length())) + 
                          "...\" (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test optimized approach
        start = System.nanoTime();
        String result4 = solution.minWindowOptimized(largeS, largeT);
        end = System.nanoTime();
        System.out.println("Optimized: \"" + result4.substring(0, Math.min(10, result4.length())) + 
                          "...\" (Time: " + (end - start) / 1000000.0 + " ms)");
    }
} 