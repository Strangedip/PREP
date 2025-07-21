import java.util.*;

/**
 * Problem: Reverse String
 * 
 * Write a function that reverses a string. The input string is given as an array of characters s.
 * You must do this by modifying the input array in-place with O(1) extra memory.
 * 
 * Example:
 * Input: s = ["h","e","l","l","o"]
 * Output: ["o","l","l","e","h"]
 * 
 * Example 2:
 * Input: s = ["H","a","n","n","a","h"]
 * Output: ["h","a","n","n","a","H"]
 * 
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s[i] is a printable ascii character.
 */
public class ReverseString {
    
    /**
     * APPROACH 1: TWO POINTERS (Optimal In-Place)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use two pointers from both ends, swap characters while moving towards center.
     */
    public void reverseStringTwoPointers(char[] s) {
        if (s == null || s.length <= 1) {
            return;
        }
        
        int left = 0;
        int right = s.length - 1;
        
        while (left < right) {
            // Swap characters
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;
            
            left++;
            right--;
        }
    }
    
    /**
     * APPROACH 2: RECURSIVE APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Due to recursion stack
     * 
     * Recursively swap characters from both ends.
     */
    public void reverseStringRecursive(char[] s) {
        if (s == null || s.length <= 1) {
            return;
        }
        reverseHelper(s, 0, s.length - 1);
    }
    
    private void reverseHelper(char[] s, int left, int right) {
        if (left >= right) {
            return;
        }
        
        // Swap characters
        char temp = s[left];
        s[left] = s[right];
        s[right] = temp;
        
        // Recursive call
        reverseHelper(s, left + 1, right - 1);
    }
    
    /**
     * APPROACH 3: USING STACK (For Learning)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Push all characters to stack, then pop them back to array.
     */
    public void reverseStringStack(char[] s) {
        if (s == null || s.length <= 1) {
            return;
        }
        
        Stack<Character> stack = new Stack<>();
        
        // Push all characters to stack
        for (char c : s) {
            stack.push(c);
        }
        
        // Pop characters back to array
        for (int i = 0; i < s.length; i++) {
            s[i] = stack.pop();
        }
    }
    
    /**
     * APPROACH 4: USING COLLECTIONS.REVERSE (Not In-Place)
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Creates list
     * 
     * Convert to list, use Collections.reverse, then convert back.
     * Note: This doesn't meet the in-place requirement but good to know.
     */
    public void reverseStringCollections(char[] s) {
        if (s == null || s.length <= 1) {
            return;
        }
        
        List<Character> list = new ArrayList<>();
        for (char c : s) {
            list.add(c);
        }
        
        Collections.reverse(list);
        
        for (int i = 0; i < s.length; i++) {
            s[i] = list.get(i);
        }
    }
    
    /**
     * APPROACH 5: XOR SWAP (Advanced Bit Manipulation)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use XOR operation to swap without temporary variable.
     * Note: Only works if characters are different.
     */
    public void reverseStringXOR(char[] s) {
        if (s == null || s.length <= 1) {
            return;
        }
        
        int left = 0;
        int right = s.length - 1;
        
        while (left < right) {
            // XOR swap (only if characters are different)
            if (s[left] != s[right]) {
                s[left] ^= s[right];
                s[right] ^= s[left];
                s[left] ^= s[right];
            }
            
            left++;
            right--;
        }
    }
    
    /**
     * APPROACH 6: STRINGBUILDER APPROACH (String Version)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * For when input is String instead of char array.
     */
    public String reverseStringBuilder(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        
        StringBuilder sb = new StringBuilder(s);
        return sb.reverse().toString();
    }
    
    /**
     * APPROACH 7: MANUAL STRING REVERSAL
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Manual approach without using built-in reverse.
     */
    public String reverseStringManual(String s) {
        if (s == null || s.length() <= 1) {
            return s;
        }
        
        char[] chars = s.toCharArray();
        reverseStringTwoPointers(chars);
        return new String(chars);
    }
    
    /**
     * APPROACH 8: ITERATIVE WITH ARRAY COPYING
     * Time Complexity: O(n)
     * Space Complexity: O(1) for in-place version
     * 
     * Copy characters from end to beginning.
     */
    public void reverseStringIterative(char[] s) {
        if (s == null || s.length <= 1) {
            return;
        }
        
        int n = s.length;
        for (int i = 0; i < n / 2; i++) {
            char temp = s[i];
            s[i] = s[n - 1 - i];
            s[n - 1 - i] = temp;
        }
    }
    
    // Helper method to print char array
    private void printArray(char[] arr) {
        System.out.print("[");
        for (int i = 0; i < arr.length; i++) {
            System.out.print("\"" + arr[i] + "\"");
            if (i < arr.length - 1) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }
    
    // Helper method to copy array
    private char[] copyArray(char[] original) {
        char[] copy = new char[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        ReverseString solution = new ReverseString();
        
        // Test case 1: Normal string
        char[] s1 = {'h', 'e', 'l', 'l', 'o'};
        System.out.println("Test Case 1: Original array");
        solution.printArray(s1);
        
        char[] copy1 = solution.copyArray(s1);
        solution.reverseStringTwoPointers(copy1);
        System.out.print("Two Pointers: ");
        solution.printArray(copy1);
        
        char[] copy2 = solution.copyArray(s1);
        solution.reverseStringRecursive(copy2);
        System.out.print("Recursive: ");
        solution.printArray(copy2);
        
        char[] copy3 = solution.copyArray(s1);
        solution.reverseStringStack(copy3);
        System.out.print("Stack: ");
        solution.printArray(copy3);
        
        char[] copy4 = solution.copyArray(s1);
        solution.reverseStringXOR(copy4);
        System.out.print("XOR Swap: ");
        solution.printArray(copy4);
        
        char[] copy5 = solution.copyArray(s1);
        solution.reverseStringIterative(copy5);
        System.out.print("Iterative: ");
        solution.printArray(copy5);
        
        System.out.println();
        
        // Test case 2: Palindrome
        char[] s2 = {'H', 'a', 'n', 'n', 'a', 'h'};
        System.out.println("Test Case 2: Palindrome-like");
        solution.printArray(s2);
        solution.reverseStringTwoPointers(s2);
        System.out.print("Reversed: ");
        solution.printArray(s2);
        System.out.println();
        
        // Test case 3: Single character
        char[] s3 = {'a'};
        System.out.println("Test Case 3: Single character");
        solution.printArray(s3);
        solution.reverseStringTwoPointers(s3);
        System.out.print("Reversed: ");
        solution.printArray(s3);
        System.out.println();
        
        // Test case 4: Two characters
        char[] s4 = {'a', 'b'};
        System.out.println("Test Case 4: Two characters");
        solution.printArray(s4);
        solution.reverseStringTwoPointers(s4);
        System.out.print("Reversed: ");
        solution.printArray(s4);
        System.out.println();
        
        // Test case 5: String version methods
        String str = "hello";
        System.out.println("Test Case 5: String versions");
        System.out.println("Original: \"" + str + "\"");
        System.out.println("StringBuilder: \"" + solution.reverseStringBuilder(str) + "\"");
        System.out.println("Manual: \"" + solution.reverseStringManual(str) + "\"");
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(ReverseString solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large char array
        int size = 1000000;
        char[] largeArray = new char[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = (char)('a' + (i % 26));
        }
        
        long start, end;
        
        // Test Two Pointers approach
        char[] copy1 = solution.copyArray(largeArray);
        start = System.nanoTime();
        solution.reverseStringTwoPointers(copy1);
        end = System.nanoTime();
        System.out.println("Two Pointers: " + (end - start) / 1000000.0 + " ms");
        
        // Test Iterative approach
        char[] copy2 = solution.copyArray(largeArray);
        start = System.nanoTime();
        solution.reverseStringIterative(copy2);
        end = System.nanoTime();
        System.out.println("Iterative: " + (end - start) / 1000000.0 + " ms");
        
        // Test XOR approach
        char[] copy3 = solution.copyArray(largeArray);
        start = System.nanoTime();
        solution.reverseStringXOR(copy3);
        end = System.nanoTime();
        System.out.println("XOR Swap: " + (end - start) / 1000000.0 + " ms");
        
        // Test Stack approach
        char[] copy4 = solution.copyArray(largeArray);
        start = System.nanoTime();
        solution.reverseStringStack(copy4);
        end = System.nanoTime();
        System.out.println("Stack: " + (end - start) / 1000000.0 + " ms");
        
        // Test Recursive approach (with smaller array to avoid stack overflow)
        char[] smallArray = new char[10000];
        System.arraycopy(largeArray, 0, smallArray, 0, 10000);
        start = System.nanoTime();
        solution.reverseStringRecursive(smallArray);
        end = System.nanoTime();
        System.out.println("Recursive (smaller array): " + (end - start) / 1000000.0 + " ms");
    }
} 