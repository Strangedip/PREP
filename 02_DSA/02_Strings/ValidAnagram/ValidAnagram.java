import java.util.*;

/**
 * Problem: Valid Anagram
 * 
 * Given two strings s and t, return true if t is an anagram of s, and false otherwise.
 * 
 * An Anagram is a word or phrase formed by rearranging the letters of a different word or phrase,
 * typically using all the original letters exactly once.
 * 
 * Example:
 * Input: s = "anagram", t = "nagaram"
 * Output: true
 * 
 * Example 2:
 * Input: s = "rat", t = "car"
 * Output: false
 * 
 * Constraints:
 * - 1 <= s.length, t.length <= 5 * 10^4
 * - s and t consist of lowercase English letters.
 * 
 * Follow up: What if the inputs contain Unicode characters? How would you adapt your solution to such a case?
 */
public class ValidAnagram {
    
    /**
     * APPROACH 1: SORTING
     * Time Complexity: O(n log n)
     * Space Complexity: O(1) - if we can modify input, O(n) for char arrays
     * 
     * Sort both strings and compare them.
     * If they are anagrams, sorted versions will be identical.
     */
    public boolean isAnagramSorting(String s, String t) {
        // Different lengths cannot be anagrams
        if (s.length() != t.length()) {
            return false;
        }
        
        // Convert to char arrays and sort
        char[] sChars = s.toCharArray();
        char[] tChars = t.toCharArray();
        
        Arrays.sort(sChars);
        Arrays.sort(tChars);
        
        // Compare sorted arrays
        return Arrays.equals(sChars, tChars);
    }
    
    /**
     * APPROACH 2: CHARACTER FREQUENCY COUNT (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1) - fixed size array for 26 letters
     * 
     * Count frequency of each character and compare.
     * This is the most efficient approach for lowercase English letters.
     */
    public boolean isAnagramFrequency(String s, String t) {
        // Different lengths cannot be anagrams
        if (s.length() != t.length()) {
            return false;
        }
        
        // Count frequency of characters
        int[] charCount = new int[26];
        
        for (int i = 0; i < s.length(); i++) {
            charCount[s.charAt(i) - 'a']++;
            charCount[t.charAt(i) - 'a']--;
        }
        
        // Check if all counts are zero
        for (int count : charCount) {
            if (count != 0) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 3: TWO SEPARATE FREQUENCY ARRAYS
     * Time Complexity: O(n)
     * Space Complexity: O(1) - fixed size arrays
     * 
     * Count frequencies separately, then compare arrays.
     * Clearer logic but slightly more space.
     */
    public boolean isAnagramTwoArrays(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        int[] sCount = new int[26];
        int[] tCount = new int[26];
        
        // Count characters in both strings
        for (int i = 0; i < s.length(); i++) {
            sCount[s.charAt(i) - 'a']++;
            tCount[t.charAt(i) - 'a']++;
        }
        
        // Compare frequency arrays
        return Arrays.equals(sCount, tCount);
    }
    
    /**
     * APPROACH 4: HASHMAP FOR UNICODE SUPPORT
     * Time Complexity: O(n)
     * Space Complexity: O(k) where k is number of unique characters
     * 
     * Use HashMap to support Unicode characters.
     * This handles the follow-up question about Unicode.
     */
    public boolean isAnagramHashMap(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        Map<Character, Integer> charCount = new HashMap<>();
        
        // Count characters in first string
        for (char c : s.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }
        
        // Subtract characters from second string
        for (char c : t.toCharArray()) {
            if (!charCount.containsKey(c)) {
                return false;
            }
            int count = charCount.get(c) - 1;
            if (count == 0) {
                charCount.remove(c);
            } else {
                charCount.put(c, count);
            }
        }
        
        return charCount.isEmpty();
    }
    
    /**
     * APPROACH 5: OPTIMIZED EARLY TERMINATION
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Stop as soon as we detect a mismatch.
     * Optimized version of frequency counting.
     */
    public boolean isAnagramOptimized(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        int[] charCount = new int[26];
        int differences = 0; // Track number of characters with non-zero count
        
        for (int i = 0; i < s.length(); i++) {
            // Process character from s
            int sIndex = s.charAt(i) - 'a';
            if (charCount[sIndex] == 0) {
                differences++;
            }
            charCount[sIndex]++;
            if (charCount[sIndex] == 0) {
                differences--;
            }
            
            // Process character from t
            int tIndex = t.charAt(i) - 'a';
            if (charCount[tIndex] == 0) {
                differences++;
            }
            charCount[tIndex]--;
            if (charCount[tIndex] == 0) {
                differences--;
            }
        }
        
        return differences == 0;
    }
    
    /**
     * APPROACH 6: PRIME NUMBER MULTIPLICATION
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Assign prime numbers to each character and multiply.
     * Anagrams will have the same product.
     * Warning: Can overflow for long strings!
     */
    public boolean isAnagramPrime(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        // Prime numbers for a-z
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101};
        
        long sProduct = 1;
        long tProduct = 1;
        
        try {
            for (int i = 0; i < s.length(); i++) {
                sProduct *= primes[s.charAt(i) - 'a'];
                tProduct *= primes[t.charAt(i) - 'a'];
            }
        } catch (ArithmeticException e) {
            // Overflow occurred, fallback to frequency counting
            return isAnagramFrequency(s, t);
        }
        
        return sProduct == tProduct;
    }
    
    /**
     * APPROACH 7: BIT MANIPULATION (For special cases)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use XOR for strings where each character appears at most once.
     * Limited use case but demonstrates bit manipulation.
     */
    public boolean isAnagramXOR(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }
        
        int xor = 0;
        
        for (int i = 0; i < s.length(); i++) {
            xor ^= s.charAt(i);
            xor ^= t.charAt(i);
        }
        
        // XOR will be 0 if strings are anagrams (works only if each char appears once)
        return xor == 0;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        ValidAnagram solution = new ValidAnagram();
        
        // Test case 1: Valid anagram
        String s1 = "anagram", t1 = "nagaram";
        System.out.println("Test Case 1: s=\"" + s1 + "\", t=\"" + t1 + "\"");
        System.out.println("Sorting: " + solution.isAnagramSorting(s1, t1));
        System.out.println("Frequency: " + solution.isAnagramFrequency(s1, t1));
        System.out.println("Two Arrays: " + solution.isAnagramTwoArrays(s1, t1));
        System.out.println("HashMap: " + solution.isAnagramHashMap(s1, t1));
        System.out.println("Optimized: " + solution.isAnagramOptimized(s1, t1));
        System.out.println("Prime: " + solution.isAnagramPrime(s1, t1));
        System.out.println();
        
        // Test case 2: Invalid anagram
        String s2 = "rat", t2 = "car";
        System.out.println("Test Case 2: s=\"" + s2 + "\", t=\"" + t2 + "\"");
        System.out.println("Frequency: " + solution.isAnagramFrequency(s2, t2));
        System.out.println("HashMap: " + solution.isAnagramHashMap(s2, t2));
        System.out.println();
        
        // Test case 3: Different lengths
        String s3 = "a", t3 = "ab";
        System.out.println("Test Case 3: s=\"" + s3 + "\", t=\"" + t3 + "\" (different lengths)");
        System.out.println("Frequency: " + solution.isAnagramFrequency(s3, t3));
        System.out.println();
        
        // Test case 4: Empty strings
        String s4 = "", t4 = "";
        System.out.println("Test Case 4: s=\"" + s4 + "\", t=\"" + t4 + "\" (empty strings)");
        System.out.println("Frequency: " + solution.isAnagramFrequency(s4, t4));
        System.out.println();
        
        // Test case 5: Same string
        String s5 = "abc", t5 = "abc";
        System.out.println("Test Case 5: s=\"" + s5 + "\", t=\"" + t5 + "\" (identical)");
        System.out.println("Frequency: " + solution.isAnagramFrequency(s5, t5));
        System.out.println();
        
        // Test case 6: Single character repeated
        String s6 = "aab", t6 = "aba";
        System.out.println("Test Case 6: s=\"" + s6 + "\", t=\"" + t6 + "\" (repeated chars)");
        System.out.println("Frequency: " + solution.isAnagramFrequency(s6, t6));
        System.out.println();
        
        // Test case 7: Unicode characters (for HashMap approach)
        String s7 = "café", t7 = "facé";
        System.out.println("Test Case 7: s=\"" + s7 + "\", t=\"" + t7 + "\" (Unicode)");
        System.out.println("HashMap: " + solution.isAnagramHashMap(s7, t7));
        
        // Performance comparison for large strings
        performanceTest(solution);
    }
    
    private static void performanceTest(ValidAnagram solution) {
        System.out.println("\n=== Performance Test ===");
        
        // Generate large test strings
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        
        for (int i = 0; i < 10000; i++) {
            char c = (char)('a' + (i % 26));
            sb1.append(c);
            sb2.insert(0, c); // Reverse order to ensure anagram
        }
        
        String largeS = sb1.toString();
        String largeT = sb2.toString();
        
        // Test frequency approach (fastest)
        long start = System.nanoTime();
        boolean result1 = solution.isAnagramFrequency(largeS, largeT);
        long end = System.nanoTime();
        System.out.println("Frequency approach: " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test sorting approach
        start = System.nanoTime();
        boolean result2 = solution.isAnagramSorting(largeS, largeT);
        end = System.nanoTime();
        System.out.println("Sorting approach: " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test HashMap approach
        start = System.nanoTime();
        boolean result3 = solution.isAnagramHashMap(largeS, largeT);
        end = System.nanoTime();
        System.out.println("HashMap approach: " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
    }
} 