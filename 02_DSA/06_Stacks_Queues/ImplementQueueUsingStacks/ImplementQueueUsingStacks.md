# Implement Queue Using Stacks

## Problem Statement
Implement a first-in-first-out (FIFO) queue using only two stacks. The implemented queue should support all functions of a normal queue (push, peek, pop, and empty).

## Example
```
MyQueue queue = new MyQueue();
queue.push(1);
queue.push(2);
queue.peek();     // returns 1
queue.pop();      // returns 1
queue.empty();    // returns false
```

## Approach: Two Stacks

### Key Insight:
- **Stack 1 (input):** Receives new elements
- **Stack 2 (output):** Provides elements in FIFO order
- **Transfer between stacks** reverses order

### Implementation:
```java
class MyQueue {
    private Stack<Integer> inputStack;
    private Stack<Integer> outputStack;
    
    public MyQueue() {
        inputStack = new Stack<>();
        outputStack = new Stack<>();
    }
    
    public void push(int x) {
        inputStack.push(x);
    }
    
    public int pop() {
        peek(); // Ensure outputStack has elements
        return outputStack.pop();
    }
    
    public int peek() {
        if (outputStack.isEmpty()) {
            // Transfer all from input to output
            while (!inputStack.isEmpty()) {
                outputStack.push(inputStack.pop());
            }
        }
        return outputStack.peek();
    }
    
    public boolean empty() {
        return inputStack.isEmpty() && outputStack.isEmpty();
    }
}
```

## Operation Analysis:

### Push Operation:
- **Always add to inputStack**
- **Time:** O(1)

### Pop/Peek Operations:
- **If outputStack not empty:** O(1)
- **If outputStack empty:** O(n) to transfer elements
- **Amortized:** O(1) per operation

### How Transfer Works:
```
Initial: inputStack = [1,2,3] (top=3), outputStack = []

After transfer:
inputStack = [], outputStack = [3,2,1] (top=1)

Now pop() gives 1 (FIFO order)
```

## Visualization:
```
Operations: push(1), push(2), push(3), pop(), push(4), pop()

Step 1: push(1)
inputStack = [1], outputStack = []

Step 2: push(2)  
inputStack = [1,2], outputStack = []

Step 3: push(3)
inputStack = [1,2,3], outputStack = []

Step 4: pop() - need to transfer
inputStack = [], outputStack = [3,2,1]
Return 1

Step 5: push(4)
inputStack = [4], outputStack = [3,2]

Step 6: pop()
Return 2 from outputStack
```

## Time Complexity Analysis:

### Amortized O(1):
- **Each element moved at most twice:** input → output
- **Total operations over n elements:** O(n)
- **Average per operation:** O(1)

### Worst Case:
- **Single pop after n pushes:** O(n)
- **But subsequent pops:** O(1) until outputStack empty

## Space Complexity:
- **O(n)** - Two stacks store all elements

## Alternative: Single Stack + Recursion

### How it works:
1. **Use recursion** to reverse order
2. **Single stack** with recursive calls
3. **More complex** but uses one stack

### Time Complexity:
- **Push:** O(n) - Need to pop all, add element, push back
- **Pop:** O(1)

## LeetCode Similar Problems:
- [225. Implement Stack using Queues](https://leetcode.com/problems/implement-stack-using-queues/)
- [622. Design Circular Queue](https://leetcode.com/problems/design-circular-queue/)
- [641. Design Circular Deque](https://leetcode.com/problems/design-circular-deque/)

## Interview Tips:
- Explain the FIFO vs LIFO difference first
- Two-stack approach is standard and optimal
- Emphasize amortized O(1) analysis
- Consider when to transfer elements (lazy vs eager)
- Handle edge cases: empty queue operations 