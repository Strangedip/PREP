# Valid Parentheses

## Problem Statement

Given a string containing only parentheses characters `()`, `[]`, and `{}`, determine if the string is valid.

A string is valid if:
1. Every opening bracket has a corresponding closing bracket
2. Brackets are closed in the correct order
3. Every closing bracket has a corresponding opening bracket of the same type

## Example
```
Input: s = "()[]{}"
Output: true

Input: s = "([)]"
Output: false
Explanation: Brackets are not closed in correct order
```

## Approach 1: Stack (Optimal Solution!)

### The Big Idea (Simple Explanation):
Think of this like checking if your code editor's brackets are properly matched. As you type opening brackets, they need to be "remembered" until you type the matching closing bracket. A stack is perfect for this "last in, first out" behavior.

**Key Insight:** When you encounter a closing bracket, it must match the most recently opened bracket (top of stack).

### How it works:
1. **Opening bracket:** Push onto stack (remember it for later)
2. **Closing bracket:** Check if it matches the top of stack
   - If stack is empty → no opening bracket to match → invalid
   - If brackets don't match → wrong type → invalid
   - If they match → pop from stack and continue
3. **End of string:** Stack should be empty (all brackets matched)

### Step-by-step Walkthrough:
```
Input: s = "{[()]}"

Step 1: c = '{' → opening bracket → push to stack
        stack: ['{']

Step 2: c = '[' → opening bracket → push to stack  
        stack: ['{', '[']

Step 3: c = '(' → opening bracket → push to stack
        stack: ['{', '[', '(']

Step 4: c = ')' → closing bracket → check top of stack
        top = '(' → matches → pop from stack
        stack: ['{', '[']

Step 5: c = ']' → closing bracket → check top of stack
        top = '[' → matches → pop from stack
        stack: ['{']

Step 6: c = '}' → closing bracket → check top of stack  
        top = '{' → matches → pop from stack
        stack: []

Result: stack is empty → valid!
```

### Code Logic:
```java
Stack<Character> stack = new Stack<>();

for (char c : s.toCharArray()) {
    if (c == '(' || c == '[' || c == '{') {
        stack.push(c);  // Opening bracket
    } else {
        if (stack.isEmpty()) return false;  // No opening to match
        
        char top = stack.pop();
        if (!isMatchingPair(top, c)) return false;  // Wrong type
    }
}

return stack.isEmpty();  // All brackets should be matched
```

### Complexity:
- **Time:** O(n) - Single pass through string
- **Space:** O(n) - Stack can hold at most n/2 opening brackets

### When to use:
- **This is the standard interview solution**
- When you need to track nested structures
- Perfect for any "matching pairs" problem

## Approach 2: HashMap for Cleaner Code

### How it works:
Use a HashMap to map closing brackets to their corresponding opening brackets for cleaner code.

### Code Logic:
```java
Map<Character, Character> mapping = new HashMap<>();
mapping.put(')', '(');
mapping.put(']', '[');  
mapping.put('}', '{');

for (char c : s.toCharArray()) {
    if (mapping.containsKey(c)) {
        // Closing bracket
        char top = stack.isEmpty() ? '#' : stack.pop();
        if (top != mapping.get(c)) return false;
    } else {
        // Opening bracket
        stack.push(c);
    }
}
```

### Benefits:
- More maintainable code
- Easy to add new bracket types
- Cleaner logic flow

## Approach 3: Counter Method (Limited Use)

### How it works:
For a single type of bracket (like only parentheses), you can use a simple counter.

### Code Logic:
```java
int count = 0;
for (char c : s.toCharArray()) {
    if (c == '(') count++;
    else if (c == ')') {
        count--;
        if (count < 0) return false;  // More closing than opening
    }
}
return count == 0;
```

### Limitations:
- **Only works for single bracket type**
- Cannot handle nested different bracket types
- Example: `"([)]"` would incorrectly return true

## Key Insights & Optimizations

### Early Termination Optimization:
```java
// Odd length strings can never be valid
if (s.length() % 2 != 0) return false;
```

### Switch Statement for Performance:
```java
switch (c) {
    case '(': case '[': case '{':
        stack.push(c);
        break;
    case ')':
        if (stack.isEmpty() || stack.pop() != '(') return false;
        break;
    // ... similar for other closing brackets
}
```

## Interview Strategy

### Step-by-step Approach:
1. **Understand the problem:** "I need to check if brackets are properly matched and nested"
2. **Identify the pattern:** "This is a classic stack problem - LIFO behavior"
3. **Explain the approach:** "Use stack to remember opening brackets, match with closing brackets"
4. **Handle edge cases:** "Empty string, unmatched brackets, wrong order"
5. **Code the solution:** Implement the stack-based approach
6. **Test thoroughly:** Walk through examples

### What Interviewers Look For:
- Immediate recognition that this is a stack problem
- Understanding of why stack is the right data structure
- Correct handling of edge cases
- Clean, bug-free implementation

## Edge Cases

### Case 1: Empty String
```
Input: ""
Output: true (no brackets to mismatch)
```

### Case 2: Only Opening Brackets
```
Input: "((("
Output: false (no closing brackets)
```

### Case 3: Only Closing Brackets
```
Input: ")))"
Output: false (no opening brackets)
```

### Case 4: Wrong Order
```
Input: "([)]"
Output: false (brackets cross each other)
```

### Case 5: Mismatched Types
```
Input: "(]"
Output: false (different bracket types)
```

## Common Mistakes

1. **Forgetting empty stack check:** Not checking if stack is empty before popping
2. **Wrong matching logic:** Incorrect bracket pair validation
3. **Not checking final stack state:** Forgetting to verify stack is empty at the end
4. **Index out of bounds:** Improper string traversal

## Real-world Applications

1. **Code Editors:** Syntax highlighting and bracket matching
2. **Compilers:** Parsing nested structures in programming languages
3. **Mathematical Expressions:** Validating formula parentheses
4. **HTML/XML Parsing:** Checking properly nested tags
5. **Configuration Files:** Validating nested JSON/YAML structures

## Follow-up Questions & Variations

**Q: What if there are other characters in the string?**
A: Ignore non-bracket characters, only process brackets.

**Q: What if brackets can be escaped (like `\(`)?**  
A: Add logic to handle escape sequences.

**Q: What if we want to return the position of the first invalid bracket?**
A: Track index along with the character in the stack.

**Q: What if we want to fix the string by adding minimum brackets?**
A: Count unmatched brackets and add the required ones.

## Pattern Recognition

This problem teaches:
- **Stack applications** for nested/matching problems
- **LIFO (Last In, First Out)** data structure usage
- **State tracking** while parsing
- **Validation algorithms** design

## Variations You Might See

### Variation 1: Generate Valid Parentheses
Generate all valid combinations of n pairs of parentheses.

### Variation 2: Minimum Additions to Make Valid
Find minimum insertions needed to make string valid.

### Variation 3: Remove Invalid Parentheses
Remove minimum number of brackets to make string valid.

### Variation 4: Score of Parentheses
Calculate score based on nested parentheses structure.

## Note

**For Mid-Level Interviews (2+ years):**
- **Recognize stack pattern immediately:** This should be instant recognition
- **Code without bugs:** Stack operations should be error-free
- **Explain the LIFO concept:** Why stack works for this problem
- **Handle all edge cases:** Empty string, unmatched brackets, etc.
- **Know the complexity:** O(n) time, O(n) space

**Interview Red Flags:**
- Not recognizing this as a stack problem
- Making stack operation errors (empty stack pop)
- Forgetting to check final stack state
- Trying to solve with complex nested loops

**Remember:** This is one of the most fundamental stack problems. It appears in almost every coding interview and is the gateway to understanding more complex parsing problems.

## LeetCode Similar Problems:
- [22. Generate Parentheses](https://leetcode.com/problems/generate-parentheses/)
- [32. Longest Valid Parentheses](https://leetcode.com/problems/longest-valid-parentheses/)
- [301. Remove Invalid Parentheses](https://leetcode.com/problems/remove-invalid-parentheses/)
- [1541. Minimum Insertions to Balance a Parentheses String](https://leetcode.com/problems/minimum-insertions-to-balance-a-parentheses-string/)

**Pattern Connection:** Once you master this, you can easily solve problems like "Remove Invalid Parentheses", "Generate Parentheses", "Basic Calculator", and many parsing-related challenges!

**Golden Rule:** When you see "matching pairs", "nested structures", or "last opened, first closed" patterns, think STACK! 