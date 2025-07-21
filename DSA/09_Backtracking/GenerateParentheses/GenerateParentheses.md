# Generate Parentheses

## Problem Statement
Given `n` pairs of parentheses, write a function to generate **all combinations of well-formed parentheses**.

**Example:**
```
Input: n = 3
Output: ["((()))","(()())","(())()","()(())","()()()"]

Input: n = 1  
Output: ["()"]

Input: n = 0
Output: [""]
```

## Problem Analysis

### Core Insight
This is a **classic backtracking problem** where we build valid parentheses strings character by character, making choices and undoing them when they lead to invalid states.

### Key Constraints
1. **Balanced**: Equal number of opening '(' and closing ')' parentheses
2. **Well-formed**: At any point, number of closing ')' ≤ number of opening '('
3. **Complete**: Must use exactly `n` pairs

### Mathematical Background
The number of valid parentheses combinations is the **nth Catalan number**:
```
C(n) = (2n)! / ((n+1)! × n!) = (2n choose n) / (n+1)
```

**Catalan sequence**: 1, 1, 2, 5, 14, 42, 132, ...

## Approaches

### Approach 1: Backtracking with Open/Close Count ⭐ (Most Intuitive)

#### Key Insight
Track the count of opening and closing parentheses used so far. Make choices based on validity rules.

#### Decision Rules
1. **Add '('**: If `open < n` (haven't used all opening parentheses)
2. **Add ')'**: If `close < open` (won't violate well-formed property)
3. **Backtrack**: When current path leads to complete valid string

#### Algorithm
```java
public List<String> generateParentheses(int n) {
    List<String> result = new ArrayList<>();
    backtrack(result, "", 0, 0, n);
    return result;
}

private void backtrack(List<String> result, String current, int open, int close, int n) {
    // Base case: complete string
    if (current.length() == 2 * n) {
        result.add(current);
        return;
    }
    
    // Choice 1: Add opening parenthesis
    if (open < n) {
        backtrack(result, current + "(", open + 1, close, n);
    }
    
    // Choice 2: Add closing parenthesis  
    if (close < open) {
        backtrack(result, current + ")", open, close + 1, n);
    }
}
```

#### Time Complexity
- **O(4ⁿ / √n)** - nth Catalan number (exponential but bounded)

#### Space Complexity  
- **O(4ⁿ / √n)** - result storage + O(n) recursion stack

### Approach 2: StringBuilder Optimization

#### Key Insight
Use StringBuilder to avoid string concatenation overhead and demonstrate explicit backtracking.

```java
private void backtrackWithStringBuilder(List<String> result, StringBuilder sb, int open, int close, int n) {
    if (sb.length() == 2 * n) {
        result.add(sb.toString());
        return;
    }
    
    if (open < n) {
        sb.append('(');                                    // Make choice
        backtrackWithStringBuilder(result, sb, open + 1, close, n);
        sb.deleteCharAt(sb.length() - 1);                 // Backtrack!
    }
    
    if (close < open) {
        sb.append(')');                                    // Make choice  
        backtrackWithStringBuilder(result, sb, open, close + 1, n);
        sb.deleteCharAt(sb.length() - 1);                 // Backtrack!
    }
}
```

**Key Learning**: Explicit backtracking - undo the choice after exploring that path.

### Approach 3: Dynamic Programming / Closure Number

#### Key Insight
Use mathematical recurrence: Every valid parentheses string can be written as:
```
"(" + [valid string with i pairs] + ")" + [valid string with n-1-i pairs]
```

#### Algorithm
```java
public List<String> generateParenthesesDP(int n) {
    List<List<String>> dp = new ArrayList<>();
    dp.add(Arrays.asList(""));  // f(0) = [""]
    
    for (int i = 1; i <= n; i++) {
        List<String> current = new ArrayList<>();
        
        for (int j = 0; j < i; j++) {
            List<String> first = dp.get(j);           // f(j)
            List<String> second = dp.get(i - 1 - j);  // f(i-1-j)
            
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
```

#### Mathematical Foundation
**Recurrence relation**: `f(n) = Σ[j=0 to n-1] f(j) × f(n-1-j)`

This is exactly the **Catalan number recurrence**!

### Approach 4: BFS (Iterative)

#### Key Insight
Build solutions level by level using a queue, treating each character addition as a level.

```java
public List<String> generateParenthesesBFS(int n) {
    Queue<Node> queue = new LinkedList<>();
    queue.offer(new Node("", 0, 0));
    
    List<String> result = new ArrayList<>();
    
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
```

## Example Trace (n = 2)

**Backtracking Tree:**
```
                ""
               /    \
           "("(1,0)   X (can't start with ')')
           /        \  
      "(("(2,0)   "()"(1,1)
       /     \       /      \
    "(()"(2,1) X   "()("(2,1) "()"(1,2) X
     /              /             |
  "(())"(2,2)  "()("(2,1)       END 
                   |
                "()("(2,2)
                   |
                  END
```

**Result**: ["(())", "()()"]

### Step-by-step Trace:
1. **Start**: `""`, open=0, close=0
2. **Add '('**: `"("`, open=1, close=0
3. **Branch 1 - Add '('**: `"(("`, open=2, close=0
4. **Add ')'**: `"(()"`, open=2, close=1  
5. **Add ')'**: `"(())"`, open=2, close=2 → **VALID RESULT**
6. **Backtrack** to step 2
7. **Branch 2 - Add ')'**: `"()"`, open=1, close=1
8. **Add '('**: `"()("`, open=2, close=1
9. **Add ')'**: `"()()"`, open=2, close=2 → **VALID RESULT**

## Comparison of Approaches

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Backtracking | O(4ⁿ/√n) | O(4ⁿ/√n) | Intuitive, standard pattern | String concatenation overhead |
| StringBuilder | O(4ⁿ/√n) | O(4ⁿ/√n) | More efficient, explicit backtrack | Slightly more complex |
| DP/Closure | O(4ⁿ/√n) | O(4ⁿ/√n) | Mathematical elegance | Less intuitive |
| BFS | O(4ⁿ/√n) | O(4ⁿ/√n) | Iterative, level-by-level | More memory usage per level |

## Key Backtracking Principles Demonstrated

### 1. **Decision Space**
At each step, we have **at most 2 choices**: add '(' or add ')'

### 2. **Constraints/Pruning**
- **Feasibility**: `open ≤ n` and `close ≤ open`
- **Early termination**: Invalid paths are pruned immediately

### 3. **State Representation**  
- **Current solution**: String being built
- **State variables**: `open`, `close` counts
- **Goal**: `length == 2×n` and `open == close == n`

### 4. **Backtracking Template**
```java
void backtrack(state) {
    if (isGoal(state)) {
        addToResult(state);
        return;
    }
    
    for (choice in getValidChoices(state)) {
        makeChoice(choice);           // Modify state
        backtrack(newState);          // Recurse
        undoChoice(choice);           // Backtrack (undo)
    }
}
```

## Catalan Numbers Connection

### Why Catalan Numbers?
The problem of generating valid parentheses is equivalent to:
- **Binary trees** with n internal nodes
- **Triangulations** of convex (n+2)-gon  
- **Monotonic lattice paths** that don't cross diagonal
- **Ways to multiply** n+1 factors with parentheses

### Computing Catalan Numbers
```java
public int catalanNumber(int n) {
    int[] catalan = new int[n + 1];
    catalan[0] = 1;
    
    for (int i = 1; i <= n; i++) {
        for (int j = 0; j < i; j++) {
            catalan[i] += catalan[j] * catalan[i - 1 - j];
        }
    }
    
    return catalan[n];
}
```

## Interview Tips

1. **Start with constraints**: Explain the validity rules clearly
2. **Draw the recursion tree**: Visual representation helps understanding  
3. **Discuss pruning**: Why certain branches are eliminated early
4. **Mention optimizations**: StringBuilder vs string concatenation
5. **Connect to Catalan numbers**: Shows mathematical depth

## Common Mistakes

1. **Not pruning invalid paths**: Allowing `close > open`
2. **Wrong base case**: Checking balance at wrong time
3. **Forgetting backtracking**: In StringBuilder approach, not undoing changes
4. **Off-by-one errors**: Wrong termination condition

## Extensions

### 1. **Remove Invalid Parentheses** (LeetCode 301)
Remove minimum number of parentheses to make string valid.

### 2. **Valid Parentheses String** (LeetCode 678)  
Handle wildcard '*' that can be '(', ')', or empty.

### 3. **Different Bracket Types**
Generate valid combinations with [], {}, () brackets.

The Generate Parentheses problem is **fundamental to backtracking** because it clearly demonstrates choice-making, constraint-checking, and the backtracking pattern in a mathematically elegant context! 