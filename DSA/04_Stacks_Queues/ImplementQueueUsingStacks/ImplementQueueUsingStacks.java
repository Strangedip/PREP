import java.util.*;

/**
 * Problem: Implement Queue using Stacks
 * 
 * Implement a first in first out (FIFO) queue using only two stacks. 
 * The implemented queue should support all the functions of a normal queue 
 * (push, peek, pop, and empty).
 * 
 * Implement the MyQueue class:
 * - void push(int x) Pushes element x to the back of the queue.
 * - int pop() Removes the element from the front of the queue and returns it.
 * - int peek() Returns the element at the front of the queue.
 * - boolean empty() Returns true if the queue is empty, false otherwise.
 * 
 * Notes:
 * - You must use only standard operations of a stack, which means only push to top, 
 *   peek/pop from top, size, and is empty operations are valid.
 * - Depending on your language, the stack may not be supported natively. You may 
 *   simulate a stack using a list or deque (double-ended queue) as long as you use 
 *   only a stack's standard operations.
 * 
 * Example:
 * Input:
 * ["MyQueue", "push", "push", "peek", "pop", "empty"]
 * [[], [1], [2], [], [], []]
 * Output:
 * [null, null, null, 1, 1, false]
 * 
 * Explanation:
 * MyQueue myQueue = new MyQueue();
 * myQueue.push(1); // queue is: [1]
 * myQueue.push(2); // queue is: [1, 2] (leftmost is front of the queue)
 * myQueue.peek(); // return 1
 * myQueue.pop(); // return 1, queue is [2]
 * myQueue.empty(); // return false
 * 
 * Constraints:
 * - 1 <= x <= 9
 * - At most 100 calls will be made to push, pop, peek, and empty.
 * - All the calls to pop and peek are valid.
 */
public class ImplementQueueUsingStacks {
    
    /**
     * APPROACH 1: TWO STACKS - TRANSFER ON POP/PEEK (Amortized O(1))
     * 
     * Push: O(1)
     * Pop: Amortized O(1), worst case O(n)
     * Peek: Amortized O(1), worst case O(n)
     * Empty: O(1)
     * 
     * Use input stack for push, output stack for pop/peek.
     * Transfer elements only when output stack is empty.
     */
    static class MyQueueTwoStacks {
        private Stack<Integer> inputStack;
        private Stack<Integer> outputStack;
        
        public MyQueueTwoStacks() {
            inputStack = new Stack<>();
            outputStack = new Stack<>();
        }
        
        public void push(int x) {
            inputStack.push(x);
        }
        
        public int pop() {
            prepareOutputStack();
            return outputStack.pop();
        }
        
        public int peek() {
            prepareOutputStack();
            return outputStack.peek();
        }
        
        public boolean empty() {
            return inputStack.isEmpty() && outputStack.isEmpty();
        }
        
        private void prepareOutputStack() {
            if (outputStack.isEmpty()) {
                while (!inputStack.isEmpty()) {
                    outputStack.push(inputStack.pop());
                }
            }
        }
    }
    
    /**
     * APPROACH 2: TWO STACKS - TRANSFER ON PUSH (O(n) push, O(1) others)
     * 
     * Push: O(n)
     * Pop: O(1)
     * Peek: O(1)
     * Empty: O(1)
     * 
     * Always maintain the queue order in the main stack.
     */
    static class MyQueueTransferOnPush {
        private Stack<Integer> mainStack;
        private Stack<Integer> tempStack;
        
        public MyQueueTransferOnPush() {
            mainStack = new Stack<>();
            tempStack = new Stack<>();
        }
        
        public void push(int x) {
            // Move all elements to temp stack
            while (!mainStack.isEmpty()) {
                tempStack.push(mainStack.pop());
            }
            
            // Push new element
            mainStack.push(x);
            
            // Move all elements back
            while (!tempStack.isEmpty()) {
                mainStack.push(tempStack.pop());
            }
        }
        
        public int pop() {
            return mainStack.pop();
        }
        
        public int peek() {
            return mainStack.peek();
        }
        
        public boolean empty() {
            return mainStack.isEmpty();
        }
    }
    
    /**
     * APPROACH 3: SINGLE STACK WITH RECURSION
     * 
     * Push: O(n) - Due to recursive calls
     * Pop: O(1)
     * Peek: O(1) 
     * Empty: O(1)
     * 
     * Use recursion to maintain FIFO order with single stack.
     */
    static class MyQueueSingleStackRecursive {
        private Stack<Integer> stack;
        
        public MyQueueSingleStackRecursive() {
            stack = new Stack<>();
        }
        
        public void push(int x) {
            pushRecursive(x);
        }
        
        private void pushRecursive(int x) {
            if (stack.isEmpty()) {
                stack.push(x);
                return;
            }
            
            int temp = stack.pop();
            pushRecursive(x);
            stack.push(temp);
        }
        
        public int pop() {
            return stack.pop();
        }
        
        public int peek() {
            return stack.peek();
        }
        
        public boolean empty() {
            return stack.isEmpty();
        }
    }
    
    /**
     * APPROACH 4: USING ARRAYLIST (Simulating Stack Behavior)
     * 
     * All operations: Amortized O(1) similar to approach 1
     * 
     * Use ArrayList to simulate stack behavior.
     */
    static class MyQueueArrayList {
        private List<Integer> inputList;
        private List<Integer> outputList;
        
        public MyQueueArrayList() {
            inputList = new ArrayList<>();
            outputList = new ArrayList<>();
        }
        
        public void push(int x) {
            inputList.add(x);
        }
        
        public int pop() {
            prepareOutputList();
            return outputList.remove(outputList.size() - 1);
        }
        
        public int peek() {
            prepareOutputList();
            return outputList.get(outputList.size() - 1);
        }
        
        public boolean empty() {
            return inputList.isEmpty() && outputList.isEmpty();
        }
        
        private void prepareOutputList() {
            if (outputList.isEmpty()) {
                while (!inputList.isEmpty()) {
                    outputList.add(inputList.remove(inputList.size() - 1));
                }
            }
        }
    }
    
    /**
     * APPROACH 5: USING DEQUE (Following Stack Constraints)
     * 
     * All operations: Amortized O(1)
     * 
     * Use Deque but restrict to stack operations only.
     */
    static class MyQueueDeque {
        private Deque<Integer> inputDeque;
        private Deque<Integer> outputDeque;
        
        public MyQueueDeque() {
            inputDeque = new ArrayDeque<>();
            outputDeque = new ArrayDeque<>();
        }
        
        public void push(int x) {
            inputDeque.push(x);  // Stack push operation
        }
        
        public int pop() {
            prepareOutputDeque();
            return outputDeque.pop();  // Stack pop operation
        }
        
        public int peek() {
            prepareOutputDeque();
            return outputDeque.peek();  // Stack peek operation
        }
        
        public boolean empty() {
            return inputDeque.isEmpty() && outputDeque.isEmpty();
        }
        
        private void prepareOutputDeque() {
            if (outputDeque.isEmpty()) {
                while (!inputDeque.isEmpty()) {
                    outputDeque.push(inputDeque.pop());
                }
            }
        }
    }
    
    /**
     * APPROACH 6: OPTIMIZED WITH FRONT TRACKING
     * 
     * Push: O(1)
     * Pop: Amortized O(1)
     * Peek: O(1)
     * Empty: O(1)
     * 
     * Track the front element separately for O(1) peek.
     */
    static class MyQueueOptimized {
        private Stack<Integer> inputStack;
        private Stack<Integer> outputStack;
        private int front;
        
        public MyQueueOptimized() {
            inputStack = new Stack<>();
            outputStack = new Stack<>();
        }
        
        public void push(int x) {
            if (inputStack.isEmpty()) {
                front = x;
            }
            inputStack.push(x);
        }
        
        public int pop() {
            if (outputStack.isEmpty()) {
                while (!inputStack.isEmpty()) {
                    outputStack.push(inputStack.pop());
                }
            }
            return outputStack.pop();
        }
        
        public int peek() {
            if (!outputStack.isEmpty()) {
                return outputStack.peek();
            }
            return front;
        }
        
        public boolean empty() {
            return inputStack.isEmpty() && outputStack.isEmpty();
        }
    }
    
    /**
     * APPROACH 7: USING LINKEDLIST AS STACK
     * 
     * All operations: Amortized O(1)
     * 
     * Use LinkedList restricted to stack operations.
     */
    static class MyQueueLinkedList {
        private LinkedList<Integer> inputList;
        private LinkedList<Integer> outputList;
        
        public MyQueueLinkedList() {
            inputList = new LinkedList<>();
            outputList = new LinkedList<>();
        }
        
        public void push(int x) {
            inputList.push(x);  // addFirst - stack push
        }
        
        public int pop() {
            prepareOutputList();
            return outputList.pop();  // removeFirst - stack pop
        }
        
        public int peek() {
            prepareOutputList();
            return outputList.peek();  // peekFirst - stack peek
        }
        
        public boolean empty() {
            return inputList.isEmpty() && outputList.isEmpty();
        }
        
        private void prepareOutputList() {
            if (outputList.isEmpty()) {
                while (!inputList.isEmpty()) {
                    outputList.push(inputList.pop());
                }
            }
        }
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        System.out.println("=== Testing Queue Implementations ===");
        
        // Test case 1: Basic operations
        System.out.println("\nTest Case 1: Basic Operations");
        testBasicOperations();
        
        // Test case 2: Multiple pushes and pops
        System.out.println("\nTest Case 2: Multiple Operations");
        testMultipleOperations();
        
        // Test case 3: Edge cases
        System.out.println("\nTest Case 3: Edge Cases");
        testEdgeCases();
        
        // Performance test
        System.out.println("\n=== Performance Test ===");
        performanceTest();
    }
    
    private static void testBasicOperations() {
        MyQueueTwoStacks queue = new MyQueueTwoStacks();
        
        queue.push(1);
        queue.push(2);
        
        System.out.println("After pushing 1, 2:");
        System.out.println("peek(): " + queue.peek());  // Should return 1
        System.out.println("pop(): " + queue.pop());    // Should return 1
        System.out.println("empty(): " + queue.empty()); // Should return false
        System.out.println("pop(): " + queue.pop());    // Should return 2
        System.out.println("empty(): " + queue.empty()); // Should return true
    }
    
    private static void testMultipleOperations() {
        MyQueueOptimized queue = new MyQueueOptimized();
        
        // Push multiple elements
        for (int i = 1; i <= 5; i++) {
            queue.push(i);
        }
        
        System.out.println("After pushing 1, 2, 3, 4, 5:");
        
        // Pop and peek alternately
        System.out.println("peek(): " + queue.peek());  // 1
        System.out.println("pop(): " + queue.pop());    // 1
        
        queue.push(6);
        
        System.out.println("After pushing 6:");
        System.out.println("peek(): " + queue.peek());  // 2
        System.out.println("pop(): " + queue.pop());    // 2
        System.out.println("pop(): " + queue.pop());    // 3
        
        System.out.println("Remaining elements by popping:");
        while (!queue.empty()) {
            System.out.print(queue.pop() + " ");
        }
        System.out.println();
    }
    
    private static void testEdgeCases() {
        // Test single element
        MyQueueTransferOnPush queue1 = new MyQueueTransferOnPush();
        queue1.push(42);
        System.out.println("Single element - peek: " + queue1.peek());
        System.out.println("Single element - pop: " + queue1.pop());
        System.out.println("Single element - empty: " + queue1.empty());
        
        // Test alternating push/pop
        MyQueueSingleStackRecursive queue2 = new MyQueueSingleStackRecursive();
        queue2.push(1);
        System.out.println("Alternating - pop: " + queue2.pop());
        queue2.push(2);
        queue2.push(3);
        System.out.println("Alternating - pop: " + queue2.pop());
        System.out.println("Alternating - pop: " + queue2.pop());
    }
    
    private static void performanceTest() {
        int operations = 10000;
        
        // Test TwoStacks approach (most common)
        long start = System.nanoTime();
        MyQueueTwoStacks queue1 = new MyQueueTwoStacks();
        
        for (int i = 0; i < operations; i++) {
            queue1.push(i);
            if (i % 3 == 0 && i > 0) {
                queue1.pop();
            }
            if (i % 5 == 0) {
                queue1.peek();
            }
        }
        
        long end = System.nanoTime();
        System.out.println("TwoStacks: " + (end - start) / 1000000.0 + " ms");
        
        // Test TransferOnPush approach
        start = System.nanoTime();
        MyQueueTransferOnPush queue2 = new MyQueueTransferOnPush();
        
        for (int i = 0; i < operations; i++) {
            queue2.push(i);
            if (i % 3 == 0 && i > 0) {
                queue2.pop();
            }
            if (i % 5 == 0) {
                queue2.peek();
            }
        }
        
        end = System.nanoTime();
        System.out.println("TransferOnPush: " + (end - start) / 1000000.0 + " ms");
        
        // Test Optimized approach
        start = System.nanoTime();
        MyQueueOptimized queue3 = new MyQueueOptimized();
        
        for (int i = 0; i < operations; i++) {
            queue3.push(i);
            if (i % 3 == 0 && i > 0) {
                queue3.pop();
            }
            if (i % 5 == 0) {
                queue3.peek();
            }
        }
        
        end = System.nanoTime();
        System.out.println("Optimized: " + (end - start) / 1000000.0 + " ms");
        
        // Test ArrayList approach
        start = System.nanoTime();
        MyQueueArrayList queue4 = new MyQueueArrayList();
        
        for (int i = 0; i < operations; i++) {
            queue4.push(i);
            if (i % 3 == 0 && i > 0) {
                queue4.pop();
            }
            if (i % 5 == 0) {
                queue4.peek();
            }
        }
        
        end = System.nanoTime();
        System.out.println("ArrayList: " + (end - start) / 1000000.0 + " ms");
        
        // Test Deque approach
        start = System.nanoTime();
        MyQueueDeque queue5 = new MyQueueDeque();
        
        for (int i = 0; i < operations; i++) {
            queue5.push(i);
            if (i % 3 == 0 && i > 0) {
                queue5.pop();
            }
            if (i % 5 == 0) {
                queue5.peek();
            }
        }
        
        end = System.nanoTime();
        System.out.println("Deque: " + (end - start) / 1000000.0 + " ms");
    }
    
    /**
     * Method to test all implementations with same operations
     */
    public static void compareAllImplementations() {
        System.out.println("=== Comparing All Implementations ===");
        
        int[] operations = {1, 2, 3, 4, 5};
        
        System.out.println("\nTesting TwoStacks:");
        testImplementation(new MyQueueTwoStacks(), operations);
        
        System.out.println("\nTesting TransferOnPush:");
        testImplementation(new MyQueueTransferOnPush(), operations);
        
        System.out.println("\nTesting Optimized:");
        testImplementation(new MyQueueOptimized(), operations);
        
        System.out.println("\nTesting ArrayList:");
        testImplementation(new MyQueueArrayList(), operations);
        
        System.out.println("\nTesting Deque:");
        testImplementation(new MyQueueDeque(), operations);
    }
    
    private static void testImplementation(Object queue, int[] values) {
        try {
            // Push all values
            for (int val : values) {
                if (queue instanceof MyQueueTwoStacks) {
                    ((MyQueueTwoStacks) queue).push(val);
                } else if (queue instanceof MyQueueTransferOnPush) {
                    ((MyQueueTransferOnPush) queue).push(val);
                } else if (queue instanceof MyQueueOptimized) {
                    ((MyQueueOptimized) queue).push(val);
                } else if (queue instanceof MyQueueArrayList) {
                    ((MyQueueArrayList) queue).push(val);
                } else if (queue instanceof MyQueueDeque) {
                    ((MyQueueDeque) queue).push(val);
                }
            }
            
            // Pop all values
            System.out.print("Push " + Arrays.toString(values) + ", then pop: ");
            while (true) {
                boolean isEmpty = false;
                if (queue instanceof MyQueueTwoStacks) {
                    isEmpty = ((MyQueueTwoStacks) queue).empty();
                    if (!isEmpty) System.out.print(((MyQueueTwoStacks) queue).pop() + " ");
                } else if (queue instanceof MyQueueTransferOnPush) {
                    isEmpty = ((MyQueueTransferOnPush) queue).empty();
                    if (!isEmpty) System.out.print(((MyQueueTransferOnPush) queue).pop() + " ");
                } else if (queue instanceof MyQueueOptimized) {
                    isEmpty = ((MyQueueOptimized) queue).empty();
                    if (!isEmpty) System.out.print(((MyQueueOptimized) queue).pop() + " ");
                } else if (queue instanceof MyQueueArrayList) {
                    isEmpty = ((MyQueueArrayList) queue).empty();
                    if (!isEmpty) System.out.print(((MyQueueArrayList) queue).pop() + " ");
                } else if (queue instanceof MyQueueDeque) {
                    isEmpty = ((MyQueueDeque) queue).empty();
                    if (!isEmpty) System.out.print(((MyQueueDeque) queue).pop() + " ");
                }
                
                if (isEmpty) break;
            }
            System.out.println();
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} 