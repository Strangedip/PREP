import java.util.*;

public class LongestSubstringKDistinct {
    
    /**
     * Find the length of the longest substring with at most k distinct characters
     * Time Complexity: O(n)
     * Space Complexity: O(k)
     */
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (s == null || s.length() == 0 || k == 0) {
            return 0;
        }
        
        Map<Character, Integer> charCount = new HashMap<>();
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            char rightChar = s.charAt(right);
            charCount.put(rightChar, charCount.getOrDefault(rightChar, 0) + 1);
            
            // Shrink window until we have at most k distinct characters
            while (charCount.size() > k) {
                char leftChar = s.charAt(left);
                charCount.put(leftChar, charCount.get(leftChar) - 1);
                if (charCount.get(leftChar) == 0) {
                    charCount.remove(leftChar);
                }
                left++;
            }
            
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    /**
     * Alternative approach using character frequency array for ASCII characters
     */
    public int lengthOfLongestSubstringKDistinctOptimized(String s, int k) {
        if (s == null || s.length() == 0 || k == 0) {
            return 0;
        }
        
        int[] charCount = new int[256]; // ASCII characters
        int distinctCount = 0;
        int left = 0;
        int maxLength = 0;
        
        for (int right = 0; right < s.length(); right++) {
            if (charCount[s.charAt(right)] == 0) {
                distinctCount++;
            }
            charCount[s.charAt(right)]++;
            
            while (distinctCount > k) {
                charCount[s.charAt(left)]--;
                if (charCount[s.charAt(left)] == 0) {
                    distinctCount--;
                }
                left++;
            }
            
            maxLength = Math.max(maxLength, right - left + 1);
        }
        
        return maxLength;
    }
    
    // Test cases
    public static void main(String[] args) {
        LongestSubstringKDistinct solution = new LongestSubstringKDistinct();
        
        // Test case 1: "eceba", k = 2 -> Expected: 3 ("ece")
        System.out.println("Test 1: " + solution.lengthOfLongestSubstringKDistinct("eceba", 2));
        
        // Test case 2: "aa", k = 1 -> Expected: 2 ("aa")
        System.out.println("Test 2: " + solution.lengthOfLongestSubstringKDistinct("aa", 1));
        
        // Test case 3: "abaccc", k = 2 -> Expected: 4 ("accc")
        System.out.println("Test 3: " + solution.lengthOfLongestSubstringKDistinct("abaccc", 2));
        
        // Test optimized version
        System.out.println("Optimized Test 1: " + solution.lengthOfLongestSubstringKDistinctOptimized("eceba", 2));
    }
} 