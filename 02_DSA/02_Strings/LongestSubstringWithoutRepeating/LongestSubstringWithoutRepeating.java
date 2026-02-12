import java.util.*;

/**
 * Problem: Longest Substring Without Repeating Characters
 * 
 * Given a string s, find the length of the longest substring without repeating characters.
 * 
 * Example:
 * Input: s = "abcabcbb"
 * Output: 3
 * Explanation: The answer is "abc", with the length of 3.
 * 
 * Example 2:
 * Input: s = "bbbbb"
 * Output: 1
 * Explanation: The answer is "b", with the length of 1.
 * 
 * Example 3:
 * Input: s = "pwwkew"
 * Output: 3
 * Explanation: The answer is "wke", with the length of 3.
 * Notice that the answer must be a substring, "pwke" is a subsequence and not a substring.
 * 
 * Constraints:
 * - 0 <= s.length <= 5 * 10^4
 * - s consists of English letters, digits, symbols and spaces.
 */
public class LongestSubstringWithoutRepeating {
    
    /**
     * APPROACH 1: SLIDING WINDOW WITH HASHSET (Most Intuitive)
     * Time Complexity: O(n)
     * Space Complexity: O(min(m, n)) where m is charset size
     * 
     * Use sliding window with HashSet to track characters in current window.
     */
    public int lengthOfLongestSubstringHashSet(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        Set<Character> charSet = new HashSet<>();
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            
            // Shrink window until no duplicate
            while (charSet.contains(rightChar)) {
                charSet.remove(s.charAt(left));
                left++;
            }
            
            charSet.add(rightChar);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
    
    /**
     * APPROACH 2: SLIDING WINDOW WITH HASHMAP (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(min(m, n))
     * 
     * Use HashMap to store character positions and jump directly when duplicate found.
     */
    public int lengthOfLongestSubstringHashMap(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        Map<Character, Integer> charIndex = new HashMap<>();
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            
            // If character is in current window, move left pointer
            if (charIndex.containsKey(rightChar) && charIndex.get(rightChar) >= left) {
                left = charIndex.get(rightChar) + 1;
            }
            
            charIndex.put(rightChar, right);
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
    
    /**
     * APPROACH 3: SLIDING WINDOW WITH ARRAY (Optimized for ASCII)
     * Time Complexity: O(n)
     * Space Complexity: O(1) - Fixed size array
     * 
     * Use array instead of HashMap for better performance with ASCII characters.
     */
    public int lengthOfLongestSubstringArray(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        // ASCII has 128 characters
        int[] charIndex = new int[128];
        Arrays.fill(charIndex, -1);
        
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            
            // If character is in current window, move left pointer
            if (charIndex[rightChar] >= left) {
                left = charIndex[rightChar] + 1;
            }
            
            charIndex[rightChar] = right;
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
    
    /**
     * APPROACH 4: BRUTE FORCE (For Learning)
     * Time Complexity: O(n³)
     * Space Complexity: O(min(m, n))
     * 
     * Check every possible substring to see if it has unique characters.
     */
    public int lengthOfLongestSubstringBruteForce(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        int maxLen = 0;
        
        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                if (hasUniqueChars(s, i, j)) {
                    maxLen = Math.max(maxLen, j - i + 1);
                }
            }
        }
        
        return maxLen;
    }
    
    /**
     * Helper method to check if substring has unique characters
     */
    private boolean hasUniqueChars(String s, int start, int end) {
        Set<Character> chars = new HashSet<>();
        for (int i = start; i <= end; i++) {
            if (chars.contains(s.charAt(i))) {
                return false;
            }
            chars.add(s.charAt(i));
        }
        return true;
    }
    
    /**
     * APPROACH 5: OPTIMIZED BRUTE FORCE
     * Time Complexity: O(n²)
     * Space Complexity: O(min(m, n))
     * 
     * Improve brute force by breaking early when duplicate found.
     */
    public int lengthOfLongestSubstringOptimizedBruteForce(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        int maxLen = 0;
        
        for (int i = 0; i < s.length(); i++) {
            Set<Character> chars = new HashSet<>();
            for (int j = i; j < s.length(); j++) {
                if (chars.contains(s.charAt(j))) {
                    break; // Found duplicate, no need to continue
                }
                chars.add(s.charAt(j));
                maxLen = Math.max(maxLen, j - i + 1);
            }
        }
        
        return maxLen;
    }
    
    /**
     * APPROACH 6: TWO POINTERS WITH FREQUENCY ARRAY
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use frequency array to track character counts in current window.
     */
    public int lengthOfLongestSubstringTwoPointers(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        int[] freq = new int[128]; // ASCII characters
        int left = 0, maxLen = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            freq[rightChar]++;
            
            // Shrink window while we have duplicates
            while (freq[rightChar] > 1) {
                freq[s.charAt(left)]--;
                left++;
            }
            
            maxLen = Math.max(maxLen, right - left + 1);
        }
        
        return maxLen;
    }
    
    /**
     * APPROACH 7: RETURN ACTUAL SUBSTRING (Bonus)
     * Time Complexity: O(n)
     * Space Complexity: O(min(m, n))
     * 
     * Return the actual longest substring, not just its length.
     */
    public String longestSubstringWithoutRepeating(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        
        Map<Character, Integer> charIndex = new HashMap<>();
        int left = 0, maxLen = 0, maxStart = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            
            if (charIndex.containsKey(rightChar) && charIndex.get(rightChar) >= left) {
                left = charIndex.get(rightChar) + 1;
            }
            
            charIndex.put(rightChar, right);
            
            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                maxStart = left;
            }
        }
        
        return s.substring(maxStart, maxStart + maxLen);
    }
    
    /**
     * APPROACH 8: USING DEQUE (Alternative Data Structure)
     * Time Complexity: O(n)
     * Space Complexity: O(min(m, n))
     * 
     * Use Deque to maintain sliding window of characters.
     */
    public int lengthOfLongestSubstringDeque(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        
        Deque<Character> deque = new ArrayDeque<>();
        Set<Character> charSet = new HashSet<>();
        int maxLen = 0;
        
        for (char c : s.toCharArray()) {
            while (charSet.contains(c)) {
                char removed = deque.pollFirst();
                charSet.remove(removed);
            }
            
            deque.offerLast(c);
            charSet.add(c);
            maxLen = Math.max(maxLen, deque.size());
        }
        
        return maxLen;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        LongestSubstringWithoutRepeating solution = new LongestSubstringWithoutRepeating();
        
        // Test case 1: Mixed characters
        String s1 = "abcabcbb";
        System.out.println("Test Case 1: \"" + s1 + "\"");
        System.out.println("HashSet: " + solution.lengthOfLongestSubstringHashSet(s1));
        System.out.println("HashMap: " + solution.lengthOfLongestSubstringHashMap(s1));
        System.out.println("Array: " + solution.lengthOfLongestSubstringArray(s1));
        System.out.println("Brute Force: " + solution.lengthOfLongestSubstringBruteForce(s1));
        System.out.println("Optimized BF: " + solution.lengthOfLongestSubstringOptimizedBruteForce(s1));
        System.out.println("Two Pointers: " + solution.lengthOfLongestSubstringTwoPointers(s1));
        System.out.println("Actual substring: \"" + solution.longestSubstringWithoutRepeating(s1) + "\"");
        System.out.println("Deque: " + solution.lengthOfLongestSubstringDeque(s1));
        System.out.println();
        
        // Test case 2: All same characters
        String s2 = "bbbbb";
        System.out.println("Test Case 2: \"" + s2 + "\"");
        System.out.println("HashMap: " + solution.lengthOfLongestSubstringHashMap(s2));
        System.out.println("Actual substring: \"" + solution.longestSubstringWithoutRepeating(s2) + "\"");
        System.out.println();
        
        // Test case 3: Complex pattern
        String s3 = "pwwkew";
        System.out.println("Test Case 3: \"" + s3 + "\"");
        System.out.println("HashMap: " + solution.lengthOfLongestSubstringHashMap(s3));
        System.out.println("Actual substring: \"" + solution.longestSubstringWithoutRepeating(s3) + "\"");
        System.out.println();
        
        // Test case 4: Empty string
        String s4 = "";
        System.out.println("Test Case 4: \"" + s4 + "\" (empty)");
        System.out.println("HashMap: " + solution.lengthOfLongestSubstringHashMap(s4));
        System.out.println();
        
        // Test case 5: Single character
        String s5 = "a";
        System.out.println("Test Case 5: \"" + s5 + "\"");
        System.out.println("HashMap: " + solution.lengthOfLongestSubstringHashMap(s5));
        System.out.println();
        
        // Test case 6: All unique characters
        String s6 = "abcdef";
        System.out.println("Test Case 6: \"" + s6 + "\" (all unique)");
        System.out.println("HashMap: " + solution.lengthOfLongestSubstringHashMap(s6));
        System.out.println("Actual substring: \"" + solution.longestSubstringWithoutRepeating(s6) + "\"");
        System.out.println();
        
        // Test case 7: Special characters and spaces
        String s7 = "a b!@# c";
        System.out.println("Test Case 7: \"" + s7 + "\" (special chars)");
        System.out.println("HashMap: " + solution.lengthOfLongestSubstringHashMap(s7));
        System.out.println("Actual substring: \"" + solution.longestSubstringWithoutRepeating(s7) + "\"");
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(LongestSubstringWithoutRepeating solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large test string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            sb.append((char)('a' + (i % 26)));
        }
        String largeString = sb.toString();
        
        long start, end;
        
        // Test HashMap approach (optimal)
        start = System.nanoTime();
        int result1 = solution.lengthOfLongestSubstringHashMap(largeString);
        end = System.nanoTime();
        System.out.println("HashMap: " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Array approach (fastest for ASCII)
        start = System.nanoTime();
        int result2 = solution.lengthOfLongestSubstringArray(largeString);
        end = System.nanoTime();
        System.out.println("Array: " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test HashSet approach
        start = System.nanoTime();
        int result3 = solution.lengthOfLongestSubstringHashSet(largeString);
        end = System.nanoTime();
        System.out.println("HashSet: " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Two Pointers approach
        start = System.nanoTime();
        int result4 = solution.lengthOfLongestSubstringTwoPointers(largeString);
        end = System.nanoTime();
        System.out.println("Two Pointers: " + result4 + " (Time: " + (end - start) / 1000000.0 + " ms)");
    }
} 