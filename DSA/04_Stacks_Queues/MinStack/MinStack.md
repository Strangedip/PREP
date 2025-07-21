# Min Stack

## Problem Statement
Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.

## Example
```
MinStack minStack = new MinStack();
minStack.push(-2);
minStack.push(0);
minStack.push(-3);
minStack.getMin(); // return -3
minStack.pop();
minStack.top();    // return 0
minStack.getMin(); // return -2
```

## Approach 1: Two Stacks

### How it works:
1. **Main stack:** Stores all elements
2. **Min stack:** Stores minimum values
3. **Sync operations** between both stacks

### Implementation:
```java
class MinStack {
    private Stack<Integer> stack;
    private Stack<Integer> minStack;
    
    public MinStack() {
        stack = new Stack<>();
        minStack = new Stack<>();
    }
    
    public void push(int val) {
        stack.push(val);
        
        // Push to minStack if empty or val is smaller/equal
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        }
    }
    
    public void pop() {
        int val = stack.pop();
        
        // Pop from minStack if it's the minimum
        if (val == minStack.peek()) {
            minStack.pop();
        }
    }
    
    public int top() {
        return stack.peek();
    }
    
    public int getMin() {
        return minStack.peek();
    }
}
```

### Time & Space Complexity:
- **Time:** O(1) for all operations
- **Space:** O(n) - Two stacks

## Approach 2: Single Stack with Pairs

### How it works:
1. **Store pairs:** (value, currentMin)
2. **Each element knows** the minimum at that point
3. **Single stack** with more memory per element

### Implementation:
```java
class MinStack {
    private Stack<int[]> stack; // [value, min]
    
    public MinStack() {
        stack = new Stack<>();
    }
    
    public void push(int val) {
        if (stack.isEmpty()) {
            stack.push(new int[]{val, val});
        } else {
            int currentMin = Math.min(val, getMin());
            stack.push(new int[]{val, currentMin});
        }
    }
    
    public void pop() {
        stack.pop();
    }
    
    public int top() {
        return stack.peek()[0];
    }
    
    public int getMin() {
        return stack.peek()[1];
    }
}
```

## Approach 3: Single Stack with Difference

### How it works:
1. **Store differences** from current minimum
2. **When difference is negative**, it's a new minimum
3. **Space optimized** for mostly increasing sequences

### Implementation:
```java
class MinStack {
    private Stack<Long> stack;
    private long min;
    
    public void push(int val) {
        if (stack.isEmpty()) {
            stack.push(0L);
            min = val;
        } else {
            stack.push(val - min);
            if (val < min) {
                min = val;
            }
        }
    }
    
    public void pop() {
        long diff = stack.pop();
        if (diff < 0) {
            min = min - diff; // Restore previous min
        }
    }
    
    public int top() {
        long diff = stack.peek();
        if (diff < 0) {
            return (int) min;
        }
        return (int) (min + diff);
    }
    
    public int getMin() {
        return (int) min;
    }
}
```

## Comparison of Approaches:

### Two Stacks:
- **Pros:** Simple, clear logic
- **Cons:** Extra space for minStack

### Pairs:
- **Pros:** Single data structure
- **Cons:** Always stores minimum (even when not needed)

### Difference:
- **Pros:** Space efficient for some patterns
- **Cons:** More complex, potential overflow issues

## Edge Cases:
1. **Empty stack operations**
2. **Duplicate minimum values**
3. **Integer overflow** in difference approach
4. **Single element stack**

## LeetCode Similar Problems:
- [716. Max Stack](https://leetcode.com/problems/max-stack/)
- [1381. Design a Stack With Increment Operation](https://leetcode.com/problems/design-a-stack-with-increment-operation/)
- [895. Maximum Frequency Stack](https://leetcode.com/problems/maximum-frequency-stack/)

## Interview Tips:
- Two-stack approach is most intuitive to start with
- Handle duplicate minimums correctly (use <= not <)
- Consider space vs complexity trade-offs
- Test with duplicate values and edge cases
- Explain why O(1) is maintained for all operations 