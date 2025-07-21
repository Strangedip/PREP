# Evaluate Reverse Polish Notation

## Problem Statement
Evaluate the value of an arithmetic expression in Reverse Polish Notation (RPN). Valid operators are +, -, *, and /. Each operand may be an integer or another expression.

## Example
```
Input: tokens = ["2","1","+","3","*"]
Output: 9
Explanation: ((2 + 1) * 3) = 9

Input: tokens = ["4","13","5","/","+"]
Output: 6
Explanation: (4 + (13 / 5)) = 6
```

## RPN Rules:
1. **Operators come after operands**
2. **Each operator takes two most recent operands**
3. **Result becomes new operand**
4. **Process left to right**

## Approach: Stack-Based Evaluation

### How it works:
1. **Push numbers** onto stack
2. **When operator found**, pop two operands
3. **Apply operation** and push result back
4. **Final stack top** is the answer

### Key Logic:
```java
Stack<Integer> stack = new Stack<>();

for (String token : tokens) {
    if (isOperator(token)) {
        int b = stack.pop(); // Second operand
        int a = stack.pop(); // First operand
        
        int result = applyOperation(a, b, token);
        stack.push(result);
    } else {
        stack.push(Integer.parseInt(token));
    }
}

return stack.pop();

private boolean isOperator(String token) {
    return token.equals("+") || token.equals("-") || 
           token.equals("*") || token.equals("/");
}

private int applyOperation(int a, int b, String operator) {
    switch (operator) {
        case "+": return a + b;
        case "-": return a - b;
        case "*": return a * b;
        case "/": return a / b;
        default: throw new IllegalArgumentException("Invalid operator");
    }
}
```

### Time & Space Complexity:
- **Time:** O(n) - Single pass through tokens
- **Space:** O(n) - Stack storage in worst case

## Step-by-Step Example:
```
tokens = ["2","1","+","3","*"]

Step 1: "2" → stack = [2]
Step 2: "1" → stack = [2, 1]
Step 3: "+" → pop 1, 2 → 2+1=3 → stack = [3]
Step 4: "3" → stack = [3, 3]
Step 5: "*" → pop 3, 3 → 3*3=9 → stack = [9]

Result: 9
```

## Important Notes:

### Order of Operands:
```java
int b = stack.pop(); // Second operand (right)
int a = stack.pop(); // First operand (left)
// For a - b or a / b, order matters!
```

### Division Handling:
- **Integer division** truncates toward zero
- **Negative results** need special handling in some languages

## Edge Cases:
1. **Single number:** ["42"] → 42
2. **Negative numbers:** ["-3", "4", "+"] → 1
3. **Division by negative:** ["4", "-2", "/"] → -2
4. **Complex expressions** with multiple operations

## Alternative: Array-Based Stack

### For better performance:
```java
int[] stack = new int[tokens.length];
int top = -1;

// Use array instead of Stack object
// Slightly better performance for this use case
```

## LeetCode Similar Problems:
- [224. Basic Calculator](https://leetcode.com/problems/basic-calculator/)
- [227. Basic Calculator II](https://leetcode.com/problems/basic-calculator-ii/)
- [772. Basic Calculator III](https://leetcode.com/problems/basic-calculator-iii/)
- [394. Decode String](https://leetcode.com/problems/decode-string/)

## Interview Tips:
- Understand RPN format first (postfix notation)
- Stack is the natural data structure for RPN
- Pay attention to operand order for subtraction/division
- Handle edge cases like single operand
- Consider integer division rules in your language 