import java.util.*;

/**
 * Problem: Evaluate Reverse Polish Notation
 * 
 * Evaluate the value of an arithmetic expression in Reverse Polish Notation.
 * 
 * Valid operators are +, -, *, and /. Each operand may be an integer or another expression.
 * 
 * Note that division between two integers should truncate toward zero.
 * 
 * It is guaranteed that the given RPN expression is always valid. That means the expression 
 * would always evaluate to a result, and there will not be any division by zero operation.
 * 
 * Example:
 * Input: tokens = ["2","1","+","3","*"]
 * Output: 9
 * Explanation: ((2 + 1) * 3) = 9
 * 
 * Example 2:
 * Input: tokens = ["4","13","5","/","+"]
 * Output: 6
 * Explanation: (4 + (13 / 5)) = 6
 * 
 * Example 3:
 * Input: tokens = ["10","6","9","3","+","-11","*","/","*","17","+","5","+"]
 * Output: 22
 * Explanation: ((10 * (6 / ((9 + 3) * -11))) + 17) + 5 = 22
 * 
 * Constraints:
 * - 1 <= tokens.length <= 10^4
 * - tokens[i] is either an operator: "+", "-", "*", or "/", or an integer in the range [-200, 200].
 */
public class EvaluateReversePolishNotation {
    
    /**
     * APPROACH 1: USING STACK (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use stack to store operands, pop two operands for each operator.
     */
    public int evalRPNStack(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        
        for (String token : tokens) {
            if (isOperator(token)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                int result = performOperation(operand1, operand2, token);
                stack.push(result);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        
        return stack.pop();
    }
    
    private boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || 
               token.equals("*") || token.equals("/");
    }
    
    private int performOperation(int a, int b, String operator) {
        switch (operator) {
            case "+": return a + b;
            case "-": return a - b;
            case "*": return a * b;
            case "/": return a / b;  // Integer division truncates toward zero
            default: throw new IllegalArgumentException("Invalid operator: " + operator);
        }
    }
    
    /**
     * APPROACH 2: USING DEQUE
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use deque instead of stack for potentially better performance.
     */
    public int evalRPNDeque(String[] tokens) {
        Deque<Integer> deque = new ArrayDeque<>();
        
        for (String token : tokens) {
            if (isOperator(token)) {
                int operand2 = deque.pop();
                int operand1 = deque.pop();
                int result = performOperation(operand1, operand2, token);
                deque.push(result);
            } else {
                deque.push(Integer.parseInt(token));
            }
        }
        
        return deque.pop();
    }
    
    /**
     * APPROACH 3: USING ARRAYLIST AS STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use ArrayList to simulate stack behavior.
     */
    public int evalRPNArrayList(String[] tokens) {
        List<Integer> stack = new ArrayList<>();
        
        for (String token : tokens) {
            if (isOperator(token)) {
                int operand2 = stack.remove(stack.size() - 1);
                int operand1 = stack.remove(stack.size() - 1);
                int result = performOperation(operand1, operand2, token);
                stack.add(result);
            } else {
                stack.add(Integer.parseInt(token));
            }
        }
        
        return stack.get(0);
    }
    
    /**
     * APPROACH 4: USING ARRAY AS STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use array with top pointer to simulate stack.
     */
    public int evalRPNArray(String[] tokens) {
        int[] stack = new int[tokens.length];
        int top = -1;
        
        for (String token : tokens) {
            if (isOperator(token)) {
                int operand2 = stack[top--];
                int operand1 = stack[top--];
                int result = performOperation(operand1, operand2, token);
                stack[++top] = result;
            } else {
                stack[++top] = Integer.parseInt(token);
            }
        }
        
        return stack[0];
    }
    
    /**
     * APPROACH 5: RECURSIVE APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Due to recursion stack
     * 
     * Use recursion to evaluate RPN expression.
     */
    public int evalRPNRecursive(String[] tokens) {
        int[] index = {tokens.length - 1};
        return evaluateRecursive(tokens, index);
    }
    
    private int evaluateRecursive(String[] tokens, int[] index) {
        String token = tokens[index[0]--];
        
        if (isOperator(token)) {
            int operand2 = evaluateRecursive(tokens, index);
            int operand1 = evaluateRecursive(tokens, index);
            return performOperation(operand1, operand2, token);
        } else {
            return Integer.parseInt(token);
        }
    }
    
    /**
     * APPROACH 6: USING SWITCH EXPRESSION (Java 14+)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use modern switch expression for cleaner code.
     */
    public int evalRPNSwitchExpression(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        
        for (String token : tokens) {
            switch (token) {
                case "+":
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "-":
                    int b = stack.pop();
                    int a = stack.pop();
                    stack.push(a - b);
                    break;
                case "*":
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "/":
                    int divisor = stack.pop();
                    int dividend = stack.pop();
                    stack.push(dividend / divisor);
                    break;
                default:
                    stack.push(Integer.parseInt(token));
                    break;
            }
        }
        
        return stack.pop();
    }
    
    /**
     * APPROACH 7: USING HASHMAP FOR OPERATIONS
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use HashMap to store operation functions.
     */
    public int evalRPNHashMap(String[] tokens) {
        Stack<Integer> stack = new Stack<>();
        
        Map<String, java.util.function.BinaryOperator<Integer>> operations = new HashMap<>();
        operations.put("+", (a, b) -> a + b);
        operations.put("-", (a, b) -> a - b);
        operations.put("*", (a, b) -> a * b);
        operations.put("/", (a, b) -> a / b);
        
        for (String token : tokens) {
            if (operations.containsKey(token)) {
                int operand2 = stack.pop();
                int operand1 = stack.pop();
                int result = operations.get(token).apply(operand1, operand2);
                stack.push(result);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        
        return stack.pop();
    }
    
    /**
     * APPROACH 8: IN-PLACE EVALUATION (If modifying input is allowed)
     * Time Complexity: O(n)
     * Space Complexity: O(1) - Not counting input array
     * 
     * Modify the input array to store intermediate results.
     */
    public int evalRPNInPlace(String[] tokens) {
        int resultIndex = 0;
        
        for (String token : tokens) {
            if (isOperator(token)) {
                int operand2 = Integer.parseInt(tokens[--resultIndex]);
                int operand1 = Integer.parseInt(tokens[--resultIndex]);
                int result = performOperation(operand1, operand2, token);
                tokens[resultIndex++] = String.valueOf(result);
            } else {
                tokens[resultIndex++] = token;
            }
        }
        
        return Integer.parseInt(tokens[0]);
    }
    
    /**
     * APPROACH 9: USING LINKEDLIST AS STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use LinkedList with addFirst/removeFirst for stack operations.
     */
    public int evalRPNLinkedList(String[] tokens) {
        LinkedList<Integer> stack = new LinkedList<>();
        
        for (String token : tokens) {
            if (isOperator(token)) {
                int operand2 = stack.removeFirst();
                int operand1 = stack.removeFirst();
                int result = performOperation(operand1, operand2, token);
                stack.addFirst(result);
            } else {
                stack.addFirst(Integer.parseInt(token));
            }
        }
        
        return stack.getFirst();
    }
    
    // Helper methods for testing
    
    /**
     * Convert infix expression to RPN for testing
     */
    public static String[] infixToRPN(String[] infix) {
        // Simple implementation for basic expressions
        // This is a simplified version and doesn't handle all edge cases
        List<String> output = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        
        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("+", 1);
        precedence.put("-", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);
        
        for (String token : infix) {
            if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                operators.pop(); // Remove "("
            } else if (precedence.containsKey(token)) {
                while (!operators.isEmpty() && !operators.peek().equals("(") &&
                       precedence.getOrDefault(operators.peek(), 0) >= precedence.get(token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            } else {
                output.add(token);
            }
        }
        
        while (!operators.isEmpty()) {
            output.add(operators.pop());
        }
        
        return output.toArray(new String[0]);
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        EvaluateReversePolishNotation solution = new EvaluateReversePolishNotation();
        
        // Test case 1: Simple expression
        System.out.println("Test Case 1: [\"2\",\"1\",\"+\",\"3\",\"*\"] = ((2 + 1) * 3)");
        String[] tokens1 = {"2", "1", "+", "3", "*"};
        System.out.println("Tokens: " + Arrays.toString(tokens1));
        System.out.println("Stack: " + solution.evalRPNStack(tokens1));
        System.out.println("Deque: " + solution.evalRPNDeque(tokens1));
        System.out.println("ArrayList: " + solution.evalRPNArrayList(tokens1));
        System.out.println("Array: " + solution.evalRPNArray(tokens1));
        System.out.println("Recursive: " + solution.evalRPNRecursive(tokens1));
        System.out.println("Switch: " + solution.evalRPNSwitchExpression(tokens1));
        System.out.println("HashMap: " + solution.evalRPNHashMap(tokens1));
        System.out.println("LinkedList: " + solution.evalRPNLinkedList(tokens1));
        System.out.println();
        
        // Test case 2: Division
        System.out.println("Test Case 2: [\"4\",\"13\",\"5\",\"/\",\"+\"] = (4 + (13 / 5))");
        String[] tokens2 = {"4", "13", "5", "/", "+"};
        System.out.println("Tokens: " + Arrays.toString(tokens2));
        System.out.println("Result: " + solution.evalRPNStack(tokens2));
        System.out.println();
        
        // Test case 3: Complex expression
        System.out.println("Test Case 3: Complex expression");
        String[] tokens3 = {"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"};
        System.out.println("Tokens: " + Arrays.toString(tokens3));
        System.out.println("Result: " + solution.evalRPNStack(tokens3));
        System.out.println();
        
        // Test case 4: Single number
        System.out.println("Test Case 4: Single number");
        String[] tokens4 = {"42"};
        System.out.println("Tokens: " + Arrays.toString(tokens4));
        System.out.println("Result: " + solution.evalRPNStack(tokens4));
        System.out.println();
        
        // Test case 5: Negative numbers
        System.out.println("Test Case 5: Negative numbers");
        String[] tokens5 = {"-1", "2", "+"};
        System.out.println("Tokens: " + Arrays.toString(tokens5));
        System.out.println("Result: " + solution.evalRPNStack(tokens5));
        System.out.println();
        
        // Test case 6: Division with truncation
        System.out.println("Test Case 6: Division with truncation toward zero");
        String[] tokens6a = {"7", "-3", "/"};
        String[] tokens6b = {"-7", "3", "/"};
        System.out.println("7 / -3 = " + solution.evalRPNStack(tokens6a));  // Should be -2
        System.out.println("-7 / 3 = " + solution.evalRPNStack(tokens6b));  // Should be -2
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(EvaluateReversePolishNotation solution) {
        System.out.println("=== Performance Test ===");
        
        // Generate large RPN expression
        List<String> tokens = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            tokens.add(String.valueOf(i + 1));
            if (i > 0) {
                tokens.add("+");
            }
        }
        String[] largeTokens = tokens.toArray(new String[0]);
        
        long start, end;
        
        // Test Stack approach (most common)
        start = System.nanoTime();
        int result1 = solution.evalRPNStack(largeTokens);
        end = System.nanoTime();
        System.out.println("Stack: " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Array approach
        start = System.nanoTime();
        int result2 = solution.evalRPNArray(largeTokens);
        end = System.nanoTime();
        System.out.println("Array: " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Deque approach
        start = System.nanoTime();
        int result3 = solution.evalRPNDeque(largeTokens);
        end = System.nanoTime();
        System.out.println("Deque: " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test ArrayList approach
        start = System.nanoTime();
        int result4 = solution.evalRPNArrayList(largeTokens);
        end = System.nanoTime();
        System.out.println("ArrayList: " + result4 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test HashMap approach
        start = System.nanoTime();
        int result5 = solution.evalRPNHashMap(largeTokens);
        end = System.nanoTime();
        System.out.println("HashMap: " + result5 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Verify all approaches give same result
        System.out.println("All approaches give same result: " + 
                          (result1 == result2 && result2 == result3 && 
                           result3 == result4 && result4 == result5));
    }
    
    /**
     * Method to verify correctness with various test cases
     */
    public static boolean verifyCorrectness(EvaluateReversePolishNotation solution) {
        String[][] testCases = {
            {"2", "1", "+", "3", "*"},        // 9
            {"4", "13", "5", "/", "+"},       // 6
            {"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"}, // 22
            {"42"},                           // 42
            {"-1", "2", "+"},                 // 1
            {"15", "7", "1", "1", "+", "/", "/", "3", "*", "2", "1", "1", "+", "+", "-", "/", "1", "*"}, // 5
            {"3", "11", "5", "+", "-"},       // -13
            {"3", "11", "+", "5", "-"}        // 9
        };
        
        int[] expected = {9, 6, 22, 42, 1, 5, -13, 9};
        
        for (int i = 0; i < testCases.length; i++) {
            int result1 = solution.evalRPNStack(testCases[i]);
            int result2 = solution.evalRPNArray(testCases[i]);
            int result3 = solution.evalRPNDeque(testCases[i]);
            
            if (result1 != expected[i] || result2 != expected[i] || result3 != expected[i]) {
                System.out.println("Test case " + i + " failed. Expected: " + expected[i] + 
                                 ", Got: " + result1 + ", " + result2 + ", " + result3);
                return false;
            }
        }
        
        return true;
    }
} 