import java.util.*;

/**
 * Problem: Valid Parentheses
 * 
 * Given a string containing just the characters '(', ')', '{', '}', '[' and ']', 
 * determine if the input string is valid.
 * 
 * An input string is valid if:
 * 1. Open brackets must be closed by the same type of brackets.
 * 2. Open brackets must be closed in the correct order.
 * 3. Every close bracket has a corresponding open bracket of the same type.
 * 
 * Example:
 * Input: s = "()[]{}"
 * Output: true
 * 
 * Input: s = "([)]"
 * Output: false
 */
public class ValidParentheses {
    
    /**
     * APPROACH 1: USING STACK (OPTIMAL)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use stack to keep track of opening brackets and match with closing brackets.
     */
    public boolean isValidStack(String s) {
        Stack<Character> stack = new Stack<>();
        
        for (char c : s.toCharArray()) {
            // If opening bracket, push to stack
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            }
            // If closing bracket, check if it matches top of stack
            else if (c == ')' || c == ']' || c == '}') {
                if (stack.isEmpty()) {
                    return false; // No opening bracket to match
                }
                
                char top = stack.pop();
                if (!isMatchingPair(top, c)) {
                    return false; // Brackets don't match
                }
            }
        }
        
        // Valid if all brackets are matched (stack is empty)
        return stack.isEmpty();
    }
    
    /**
     * Helper method to check if opening and closing brackets match
     */
    private boolean isMatchingPair(char open, char close) {
        return (open == '(' && close == ')') ||
               (open == '[' && close == ']') ||
               (open == '{' && close == '}');
    }
    
    /**
     * APPROACH 2: USING HASHMAP FOR CLEANER CODE
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public boolean isValidHashMap(String s) {
        Stack<Character> stack = new Stack<>();
        Map<Character, Character> mapping = new HashMap<>();
        
        // Map closing brackets to opening brackets
        mapping.put(')', '(');
        mapping.put(']', '[');
        mapping.put('}', '{');
        
        for (char c : s.toCharArray()) {
            if (mapping.containsKey(c)) {
                // It's a closing bracket
                char topElement = stack.isEmpty() ? '#' : stack.pop();
                if (topElement != mapping.get(c)) {
                    return false;
                }
            } else {
                // It's an opening bracket
                stack.push(c);
            }
        }
        
        return stack.isEmpty();
    }
    
    /**
     * APPROACH 3: COUNTER-BASED (ONLY FOR SINGLE TYPE OF BRACKETS)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Only works when dealing with single type of brackets like "()"
     */
    public boolean isValidCounter(String s) {
        int count = 0;
        
        for (char c : s.toCharArray()) {
            if (c == '(') {
                count++;
            } else if (c == ')') {
                count--;
                if (count < 0) {
                    return false; // More closing than opening
                }
            }
        }
        
        return count == 0;
    }
    
    /**
     * APPROACH 4: STRING REPLACEMENT (INEFFICIENT BUT INTERESTING)
     * Time Complexity: O(n²)
     * Space Complexity: O(n)
     * 
     * Keep removing valid pairs until no more pairs can be removed.
     */
    public boolean isValidStringReplacement(String s) {
        while (s.length() > 0) {
            int originalLength = s.length();
            
            // Remove all valid pairs
            s = s.replace("()", "");
            s = s.replace("[]", "");
            s = s.replace("{}", "");
            
            // If no change in length, we can't reduce further
            if (s.length() == originalLength) {
                break;
            }
        }
        
        return s.length() == 0;
    }
    
    /**
     * APPROACH 5: RECURSIVE SOLUTION
     * Time Complexity: O(2^n) in worst case
     * Space Complexity: O(n) for recursion stack
     * 
     * Not efficient but shows recursive thinking.
     */
    public boolean isValidRecursive(String s) {
        return isValidRecursiveHelper(s, 0, 0, 0, 0);
    }
    
    private boolean isValidRecursiveHelper(String s, int index, int round, int square, int curly) {
        // Base case: reached end of string
        if (index == s.length()) {
            return round == 0 && square == 0 && curly == 0;
        }
        
        char c = s.charAt(index);
        
        if (c == '(') {
            return isValidRecursiveHelper(s, index + 1, round + 1, square, curly);
        } else if (c == ')') {
            if (round <= 0) return false;
            return isValidRecursiveHelper(s, index + 1, round - 1, square, curly);
        } else if (c == '[') {
            return isValidRecursiveHelper(s, index + 1, round, square + 1, curly);
        } else if (c == ']') {
            if (square <= 0) return false;
            return isValidRecursiveHelper(s, index + 1, round, square - 1, curly);
        } else if (c == '{') {
            return isValidRecursiveHelper(s, index + 1, round, square, curly + 1);
        } else if (c == '}') {
            if (curly <= 0) return false;
            return isValidRecursiveHelper(s, index + 1, round, square, curly - 1);
        }
        
        return false;
    }
    
    /**
     * APPROACH 6: OPTIMIZED STACK WITH EARLY TERMINATION
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     */
    public boolean isValidOptimized(String s) {
        // Early termination: odd length can't be valid
        if (s.length() % 2 != 0) {
            return false;
        }
        
        Stack<Character> stack = new Stack<>();
        
        for (char c : s.toCharArray()) {
            switch (c) {
                case '(':
                case '[':
                case '{':
                    stack.push(c);
                    break;
                case ')':
                    if (stack.isEmpty() || stack.pop() != '(') return false;
                    break;
                case ']':
                    if (stack.isEmpty() || stack.pop() != '[') return false;
                    break;
                case '}':
                    if (stack.isEmpty() || stack.pop() != '{') return false;
                    break;
                default:
                    // Invalid character
                    return false;
            }
        }
        
        return stack.isEmpty();
    }
    
    // Test method
    public static void main(String[] args) {
        ValidParentheses solution = new ValidParentheses();
        
        // Test cases
        String[] testCases = {
            "()",           // true
            "()[]{}",       // true
            "(]",           // false
            "([)]",         // false
            "{[]}",         // true
            "",             // true (empty string is valid)
            "(((",          // false
            ")))",          // false
            "(){}}{",       // false
            "((()))",       // true
            "[({})]",       // true
            "[({}])",       // false
        };
        
        for (String test : testCases) {
            System.out.println("Input: \"" + test + "\"");
            System.out.println("Stack approach: " + solution.isValidStack(test));
            System.out.println("HashMap approach: " + solution.isValidHashMap(test));
            System.out.println("Optimized: " + solution.isValidOptimized(test));
            
            // Test counter approach only for single bracket type
            if (test.matches("[()]*")) {
                System.out.println("Counter approach: " + solution.isValidCounter(test));
            }
            
            System.out.println();
        }
    }
} 