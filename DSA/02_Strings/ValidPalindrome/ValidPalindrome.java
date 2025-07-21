import java.util.*;

/**
 * Problem: Valid Palindrome
 * 
 * A phrase is a palindrome if, after converting all uppercase letters into lowercase letters 
 * and removing all non-alphanumeric characters, it reads the same forward and backward. 
 * Alphanumeric characters include letters and numbers.
 * 
 * Given a string s, return true if it is a palindrome, or false otherwise.
 * 
 * Example:
 * Input: s = "A man, a plan, a canal: Panama"
 * Output: true
 * Explanation: "amanaplanacanalpanama" is a palindrome.
 * 
 * Example 2:
 * Input: s = "race a car"
 * Output: false
 * Explanation: "raceacar" is not a palindrome.
 * 
 * Example 3:
 * Input: s = " "
 * Output: true
 * Explanation: s is an empty string "" after removing non-alphanumeric characters.
 * Since an empty string reads the same forward and backward, it is a palindrome.
 * 
 * Constraints:
 * - 1 <= s.length <= 2 * 10^5
 * - s consists only of printable ASCII characters.
 */
public class ValidPalindrome {
    
    /**
     * APPROACH 1: TWO POINTERS (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use two pointers from both ends, skip non-alphanumeric characters.
     */
    public boolean isPalindromeOptimal(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        int left = 0;
        int right = s.length() - 1;
        
        while (left < right) {
            // Skip non-alphanumeric characters from left
            while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
                left++;
            }
            
            // Skip non-alphanumeric characters from right
            while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
                right--;
            }
            
            // Compare characters (case insensitive)
            if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
                return false;
            }
            
            left++;
            right--;
        }
        
        return true;
    }
    
    /**
     * APPROACH 2: PREPROCESS THEN COMPARE
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Clean the string first, then check if it equals its reverse.
     */
    public boolean isPalindromePreprocess(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        // Clean the string
        StringBuilder cleaned = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                cleaned.append(Character.toLowerCase(c));
            }
        }
        
        String cleanStr = cleaned.toString();
        String reverseStr = cleaned.reverse().toString();
        
        return cleanStr.equals(reverseStr);
    }
    
    /**
     * APPROACH 3: RECURSIVE APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Due to recursion stack
     * 
     * Recursively check palindrome property.
     */
    public boolean isPalindromeRecursive(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        return isPalindromeHelper(s, 0, s.length() - 1);
    }
    
    private boolean isPalindromeHelper(String s, int left, int right) {
        // Base case: pointers crossed
        if (left >= right) {
            return true;
        }
        
        // Skip non-alphanumeric from left
        if (!Character.isLetterOrDigit(s.charAt(left))) {
            return isPalindromeHelper(s, left + 1, right);
        }
        
        // Skip non-alphanumeric from right
        if (!Character.isLetterOrDigit(s.charAt(right))) {
            return isPalindromeHelper(s, left, right - 1);
        }
        
        // Compare current characters
        if (Character.toLowerCase(s.charAt(left)) != Character.toLowerCase(s.charAt(right))) {
            return false;
        }
        
        // Recursive call for inner substring
        return isPalindromeHelper(s, left + 1, right - 1);
    }
    
    /**
     * APPROACH 4: USING REGEX AND REVERSE
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use regex to clean string, then compare with reverse.
     */
    public boolean isPalindromeRegex(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        // Remove non-alphanumeric and convert to lowercase
        String cleaned = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
        
        // Compare with reverse
        String reversed = new StringBuilder(cleaned).reverse().toString();
        return cleaned.equals(reversed);
    }
    
    /**
     * APPROACH 5: SINGLE PASS WITH ARRAYLIST
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Extract valid characters to list, then check palindrome.
     */
    public boolean isPalindromeList(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        List<Character> chars = new ArrayList<>();
        
        // Extract alphanumeric characters
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                chars.add(Character.toLowerCase(c));
            }
        }
        
        // Check palindrome
        int left = 0, right = chars.size() - 1;
        while (left < right) {
            if (!chars.get(left).equals(chars.get(right))) {
                return false;
            }
            left++;
            right--;
        }
        
        return true;
    }
    
    /**
     * APPROACH 6: CUSTOM CHARACTER VALIDATION
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use custom methods instead of Character class methods.
     */
    public boolean isPalindromeCustom(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        int left = 0;
        int right = s.length() - 1;
        
        while (left < right) {
            // Skip non-alphanumeric from left
            while (left < right && !isAlphanumeric(s.charAt(left))) {
                left++;
            }
            
            // Skip non-alphanumeric from right
            while (left < right && !isAlphanumeric(s.charAt(right))) {
                right--;
            }
            
            // Compare characters
            if (toLowerCase(s.charAt(left)) != toLowerCase(s.charAt(right))) {
                return false;
            }
            
            left++;
            right--;
        }
        
        return true;
    }
    
    // Custom helper methods
    private boolean isAlphanumeric(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }
    
    private char toLowerCase(char c) {
        if (c >= 'A' && c <= 'Z') {
            return (char)(c + 32);
        }
        return c;
    }
    
    /**
     * APPROACH 7: STACK-BASED APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use stack to store first half, compare with second half.
     */
    public boolean isPalindromeStack(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        // Extract alphanumeric characters
        List<Character> chars = new ArrayList<>();
        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                chars.add(Character.toLowerCase(c));
            }
        }
        
        if (chars.size() <= 1) {
            return true;
        }
        
        Stack<Character> stack = new Stack<>();
        int mid = chars.size() / 2;
        
        // Push first half to stack
        for (int i = 0; i < mid; i++) {
            stack.push(chars.get(i));
        }
        
        // Compare second half with stack
        int start = chars.size() % 2 == 0 ? mid : mid + 1;
        for (int i = start; i < chars.size(); i++) {
            if (stack.isEmpty() || !stack.pop().equals(chars.get(i))) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * APPROACH 8: USING STREAMS (Java 8+)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use Java 8 streams for functional programming approach.
     */
    public boolean isPalindromeStreams(String s) {
        if (s == null || s.length() <= 1) {
            return true;
        }
        
        String cleaned = s.chars()
                .filter(Character::isLetterOrDigit)
                .map(Character::toLowerCase)
                .collect(StringBuilder::new, 
                        StringBuilder::appendCodePoint, 
                        StringBuilder::append)
                .toString();
        
        return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        ValidPalindrome solution = new ValidPalindrome();
        
        // Test case 1: Standard palindrome
        String s1 = "A man, a plan, a canal: Panama";
        System.out.println("Test Case 1: \"" + s1 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s1));
        System.out.println("Preprocess: " + solution.isPalindromePreprocess(s1));
        System.out.println("Recursive: " + solution.isPalindromeRecursive(s1));
        System.out.println("Regex: " + solution.isPalindromeRegex(s1));
        System.out.println("List: " + solution.isPalindromeList(s1));
        System.out.println("Custom: " + solution.isPalindromeCustom(s1));
        System.out.println("Stack: " + solution.isPalindromeStack(s1));
        System.out.println("Streams: " + solution.isPalindromeStreams(s1));
        System.out.println();
        
        // Test case 2: Not a palindrome
        String s2 = "race a car";
        System.out.println("Test Case 2: \"" + s2 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s2));
        System.out.println("Preprocess: " + solution.isPalindromePreprocess(s2));
        System.out.println();
        
        // Test case 3: Empty after cleaning
        String s3 = " ";
        System.out.println("Test Case 3: \"" + s3 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s3));
        System.out.println();
        
        // Test case 4: Single character
        String s4 = "a";
        System.out.println("Test Case 4: \"" + s4 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s4));
        System.out.println();
        
        // Test case 5: Numbers and letters
        String s5 = "Madam, I'm Adam";
        System.out.println("Test Case 5: \"" + s5 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s5));
        System.out.println();
        
        // Test case 6: Only punctuation
        String s6 = ".,";
        System.out.println("Test Case 6: \"" + s6 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s6));
        System.out.println();
        
        // Test case 7: Mixed alphanumeric
        String s7 = "Was it a car or a cat I saw?";
        System.out.println("Test Case 7: \"" + s7 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s7));
        System.out.println();
        
        // Test case 8: Numbers
        String s8 = "12321";
        System.out.println("Test Case 8: \"" + s8 + "\"");
        System.out.println("Optimal: " + solution.isPalindromeOptimal(s8));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(ValidPalindrome solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large palindrome string
        StringBuilder sb = new StringBuilder();
        String pattern = "A man, a plan, a canal: Panama. ";
        for (int i = 0; i < 1000; i++) {
            sb.append(pattern);
        }
        String largeString = sb.toString();
        
        long start, end;
        
        // Test optimal approach
        start = System.nanoTime();
        boolean result1 = solution.isPalindromeOptimal(largeString);
        end = System.nanoTime();
        System.out.println("Optimal: " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test preprocess approach
        start = System.nanoTime();
        boolean result2 = solution.isPalindromePreprocess(largeString);
        end = System.nanoTime();
        System.out.println("Preprocess: " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test regex approach
        start = System.nanoTime();
        boolean result3 = solution.isPalindromeRegex(largeString);
        end = System.nanoTime();
        System.out.println("Regex: " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test custom approach
        start = System.nanoTime();
        boolean result4 = solution.isPalindromeCustom(largeString);
        end = System.nanoTime();
        System.out.println("Custom: " + result4 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test streams approach
        start = System.nanoTime();
        boolean result5 = solution.isPalindromeStreams(largeString);
        end = System.nanoTime();
        System.out.println("Streams: " + result5 + " (Time: " + (end - start) / 1000000.0 + " ms)");
    }
} 