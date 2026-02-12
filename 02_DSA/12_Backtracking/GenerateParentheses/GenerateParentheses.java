import java.util.*;

/**
 * LeetCode 22: Generate Parentheses
 * 
 * Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses.
 * 
 * Example:
 * Input: n = 3
 * Output: ["((()))","(()())","(())()","()(())","()()()"]
 * 
 * Time Complexity: O(4^n / sqrt(n)) - Catalan number
 * Space Complexity: O(4^n / sqrt(n)) for result + O(n) for recursion stack
 */
public class GenerateParentheses {
    
    /**
     * Approach 1: Backtracking with Open/Close Count ⭐ (Most Intuitive)
     * Track count of open and close parentheses used so far
     */
    public List<String> generateParentheses(int n) {
        List<String> result = new ArrayList<>();
        backtrack(result, "", 0, 0, n);
        return result;
    }
    
    private void backtrack(List<String> result, String current, int open, int close, int n) {
        // Base case: if we've used all n pairs
        if (current.length() == 2 * n) {
            result.add(current);
            return;
        }
        
        // Add opening parenthesis if we haven't used all n
        if (open < n) {
            backtrack(result, current + "(", open + 1, close, n);
        }
        
        // Add closing parenthesis if it won't make string invalid
        if (close < open) {
            backtrack(result, current + ")", open, close + 1, n);
        }
    }
    
    /**
     * Approach 2: Backtracking with StringBuilder (Memory Optimized)
     * Use StringBuilder to avoid string concatenation overhead
     */
    public List<String> generateParenthesesOptimized(int n) {
        List<String> result = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        backtrackWithStringBuilder(result, sb, 0, 0, n);
        return result;
    }
    
    private void backtrackWithStringBuilder(List<String> result, StringBuilder sb, int open, int close, int n) {
        if (sb.length() == 2 * n) {
            result.add(sb.toString());
            return;
        }
        
        if (open < n) {
            sb.append('(');
            backtrackWithStringBuilder(result, sb, open + 1, close, n);
            sb.deleteCharAt(sb.length() - 1); // Backtrack
        }
        
        if (close < open) {
            sb.append(')');
            backtrackWithStringBuilder(result, sb, open, close + 1, n);
            sb.deleteCharAt(sb.length() - 1); // Backtrack
        }
    }
    
    /**
     * Approach 3: Backtracking with Balance Tracking
     * Track the balance (open - close) instead of separate counts
     */
    public List<String> generateParenthesesBalance(int n) {
        List<String> result = new ArrayList<>();
        backtrackBalance(result, "", 0, 0, n);
        return result;
    }
    
    private void backtrackBalance(List<String> result, String current, int balance, int used, int n) {
        if (used == 2 * n) {
            if (balance == 0) {
                result.add(current);
            }
            return;
        }
        
        // Add opening parenthesis (increase balance, but don't exceed n opening parens)
        if (balance + (2 * n - used) > 0 && (used + balance) / 2 < n) {
            backtrackBalance(result, current + "(", balance + 1, used + 1, n);
        }
        
        // Add closing parenthesis (decrease balance, only if balance > 0)
        if (balance > 0) {
            backtrackBalance(result, current + ")", balance - 1, used + 1, n);
        }
    }
    
    /**
     * Approach 4: Dynamic Programming / Closure Number
     * Build solutions using the recurrence: f(n) = "(" + f(i) + ")" + f(n-1-i) for i from 0 to n-1
     */
    public List<String> generateParenthesesDP(int n) {
        List<List<String>> dp = new ArrayList<>();
        
        // Base case: f(0) = [""]
        dp.add(Arrays.asList(""));
        
        for (int i = 1; i <= n; i++) {
            List<String> current = new ArrayList<>();
            
            for (int j = 0; j < i; j++) {
                List<String> first = dp.get(j);     // f(j)
                List<String> second = dp.get(i - 1 - j); // f(i-1-j)
                
                for (String f : first) {
                    for (String s : second) {
                        current.add("(" + f + ")" + s);
                    }
                }
            }
            
            dp.add(current);
        }
        
        return dp.get(n);
    }
    
    /**
     * Approach 5: Iterative BFS-style Generation
     * Build solutions level by level using queue
     */
    public List<String> generateParenthesesBFS(int n) {
        List<String> result = new ArrayList<>();
        if (n == 0) {
            result.add("");
            return result;
        }
        
        Queue<Node> queue = new LinkedList<>();
        queue.offer(new Node("", 0, 0));
        
        while (!queue.isEmpty()) {
            Node node = queue.poll();
            
            if (node.str.length() == 2 * n) {
                result.add(node.str);
                continue;
            }
            
            // Add opening parenthesis
            if (node.open < n) {
                queue.offer(new Node(node.str + "(", node.open + 1, node.close));
            }
            
            // Add closing parenthesis
            if (node.close < node.open) {
                queue.offer(new Node(node.str + ")", node.open, node.close + 1));
            }
        }
        
        return result;
    }
    
    static class Node {
        String str;
        int open;
        int close;
        
        Node(String str, int open, int close) {
            this.str = str;
            this.open = open;
            this.close = close;
        }
    }
    
    /**
     * Approach 6: Generate All and Filter (Brute Force - for comparison)
     * Generate all possible combinations and filter valid ones
     */
    public List<String> generateParenthesesBruteForce(int n) {
        List<String> result = new ArrayList<>();
        generateAll(new char[2 * n], 0, result);
        return result;
    }
    
    private void generateAll(char[] current, int pos, List<String> result) {
        if (pos == current.length) {
            if (isValid(current)) {
                result.add(new String(current));
            }
            return;
        }
        
        current[pos] = '(';
        generateAll(current, pos + 1, result);
        
        current[pos] = ')';
        generateAll(current, pos + 1, result);
    }
    
    private boolean isValid(char[] chars) {
        int balance = 0;
        for (char c : chars) {
            if (c == '(') {
                balance++;
            } else {
                balance--;
            }
            if (balance < 0) {
                return false;
            }
        }
        return balance == 0;
    }
    
    /**
     * Helper method to validate parentheses string
     */
    public boolean isValidParentheses(String s) {
        int balance = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') {
                balance++;
            } else if (c == ')') {
                balance--;
                if (balance < 0) {
                    return false;
                }
            }
        }
        return balance == 0;
    }
    
    /**
     * Extension: Count number of valid parentheses combinations (Catalan number)
     */
    public int countValidParentheses(int n) {
        // Catalan number: C(n) = (2n)! / ((n+1)! * n!)
        // or C(n) = C(0)*C(n-1) + C(1)*C(n-2) + ... + C(n-1)*C(0)
        int[] catalan = new int[n + 1];
        catalan[0] = 1;
        
        for (int i = 1; i <= n; i++) {
            catalan[i] = 0;
            for (int j = 0; j < i; j++) {
                catalan[i] += catalan[j] * catalan[i - 1 - j];
            }
        }
        
        return catalan[n];
    }
    
    public static void main(String[] args) {
        GenerateParentheses solution = new GenerateParentheses();
        
        // Test case 1: n = 3
        int n1 = 3;
        System.out.println("n = " + n1);
        System.out.println("Backtracking: " + solution.generateParentheses(n1));
        System.out.println("Optimized: " + solution.generateParenthesesOptimized(n1));
        System.out.println("Balance: " + solution.generateParenthesesBalance(n1));
        System.out.println("DP: " + solution.generateParenthesesDP(n1));
        System.out.println("BFS: " + solution.generateParenthesesBFS(n1));
        System.out.println("Brute Force: " + solution.generateParenthesesBruteForce(n1));
        System.out.println("Count (Catalan): " + solution.countValidParentheses(n1));
        System.out.println();
        
        // Test case 2: n = 1
        int n2 = 1;
        System.out.println("n = " + n2);
        System.out.println("Result: " + solution.generateParentheses(n2));
        System.out.println("Count: " + solution.countValidParentheses(n2));
        System.out.println();
        
        // Test case 3: n = 0
        int n3 = 0;
        System.out.println("n = " + n3);
        System.out.println("Result: " + solution.generateParentheses(n3));
        System.out.println("Count: " + solution.countValidParentheses(n3));
        System.out.println();
        
        // Test validation
        System.out.println("Validation tests:");
        System.out.println("'((()))' is valid: " + solution.isValidParentheses("((()))"));
        System.out.println("'()()()' is valid: " + solution.isValidParentheses("()()()"));
        System.out.println("'(((' is valid: " + solution.isValidParentheses("((("));
        System.out.println("'())' is valid: " + solution.isValidParentheses("())"));
        
        // Performance comparison for larger n
        int n4 = 4;
        System.out.println("\nPerformance test for n = " + n4 + ":");
        
        long start = System.nanoTime();
        List<String> backtrackResult = solution.generateParentheses(n4);
        long backtrackTime = System.nanoTime() - start;
        
        start = System.nanoTime();
        List<String> dpResult = solution.generateParenthesesDP(n4);
        long dpTime = System.nanoTime() - start;
        
        System.out.println("Backtracking time: " + backtrackTime / 1000000.0 + " ms");
        System.out.println("DP time: " + dpTime / 1000000.0 + " ms");
        System.out.println("Results match: " + backtrackResult.equals(dpResult));
    }
} 