import java.util.*;

/**
 * Problem: Min Stack
 * 
 * Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.
 * 
 * Implement the MinStack class:
 * - MinStack() initializes the stack object.
 * - void push(int val) pushes the element val onto the stack.
 * - void pop() removes the element on the top of the stack.
 * - int top() gets the top element of the stack.
 * - int getMin() retrieves the minimum element in the stack.
 * 
 * You must implement a solution with O(1) time complexity for each function.
 * 
 * Example:
 * Input:
 * ["MinStack","push","push","push","getMin","pop","top","getMin"]
 * [[],[-2],[0],[-3],[],[],[],[]]
 * 
 * Output:
 * [null,null,null,null,-3,null,0,-2]
 * 
 * Explanation:
 * MinStack minStack = new MinStack();
 * minStack.push(-2);
 * minStack.push(0);
 * minStack.push(-3);
 * minStack.getMin(); // return -3
 * minStack.pop();
 * minStack.top();    // return 0
 * minStack.getMin(); // return -2
 * 
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - Methods pop, top and getMin operations will always be called on non-empty stacks.
 * - At most 3 * 10^4 calls will be made to push, pop, top, and getMin.
 */
public class MinStack {
    
    /**
     * APPROACH 1: TWO STACKS (Most Common)
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n) worst case, O(1) best case for min stack
     * 
     * Use main stack for values and auxiliary stack for minimums.
     */
    static class MinStackTwoStacks {
        private Stack<Integer> stack;
        private Stack<Integer> minStack;
        
        public MinStackTwoStacks() {
            stack = new Stack<>();
            minStack = new Stack<>();
        }
        
        public void push(int val) {
            stack.push(val);
            
            // Push to min stack if it's empty or val is <= current min
            if (minStack.isEmpty() || val <= minStack.peek()) {
                minStack.push(val);
            }
        }
        
        public void pop() {
            if (stack.isEmpty()) return;
            
            int val = stack.pop();
            
            // Pop from min stack if the popped value was the minimum
            if (!minStack.isEmpty() && val == minStack.peek()) {
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
    
    /**
     * APPROACH 2: SINGLE STACK WITH NODE CLASS
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n)
     * 
     * Each node stores value and minimum up to that point.
     */
    static class MinStackSingleStack {
        private Stack<Node> stack;
        
        private static class Node {
            int val;
            int min;
            
            Node(int val, int min) {
                this.val = val;
                this.min = min;
            }
        }
        
        public MinStackSingleStack() {
            stack = new Stack<>();
        }
        
        public void push(int val) {
            int min = stack.isEmpty() ? val : Math.min(val, stack.peek().min);
            stack.push(new Node(val, min));
        }
        
        public void pop() {
            if (!stack.isEmpty()) {
                stack.pop();
            }
        }
        
        public int top() {
            return stack.peek().val;
        }
        
        public int getMin() {
            return stack.peek().min;
        }
    }
    
    /**
     * APPROACH 3: OPTIMIZED TWO STACKS (Less Space)
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n) for main stack, O(k) for min stack where k <= n
     * 
     * Only push to min stack when new minimum is found.
     */
    static class MinStackOptimized {
        private Stack<Integer> stack;
        private Stack<Integer> minStack;
        
        public MinStackOptimized() {
            stack = new Stack<>();
            minStack = new Stack<>();
        }
        
        public void push(int val) {
            stack.push(val);
            
            // Only push to min stack if it's a new minimum or equal to current min
            if (minStack.isEmpty() || val <= minStack.peek()) {
                minStack.push(val);
            }
        }
        
        public void pop() {
            if (stack.isEmpty()) return;
            
            int val = stack.pop();
            
            // Pop from min stack only if the popped value equals current min
            if (!minStack.isEmpty() && val == minStack.peek()) {
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
    
    /**
     * APPROACH 4: USING DIFFERENCE ENCODING
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n)
     * 
     * Store differences from minimum to save space and handle overflow.
     */
    static class MinStackDifference {
        private Stack<Long> stack;
        private long min;
        
        public MinStackDifference() {
            stack = new Stack<>();
        }
        
        public void push(int val) {
            if (stack.isEmpty()) {
                min = val;
                stack.push(0L);
            } else {
                long diff = (long) val - min;
                stack.push(diff);
                
                if (val < min) {
                    min = val;
                }
            }
        }
        
        public void pop() {
            if (stack.isEmpty()) return;
            
            long diff = stack.pop();
            
            if (diff < 0) {
                // The popped element was the minimum
                min = min - diff;
            }
        }
        
        public int top() {
            long diff = stack.peek();
            
            if (diff < 0) {
                return (int) min;
            } else {
                return (int) (min + diff);
            }
        }
        
        public int getMin() {
            return (int) min;
        }
    }
    
    /**
     * APPROACH 5: USING ARRAYLIST (Alternative Implementation)
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n)
     * 
     * Use ArrayList instead of Stack for storage.
     */
    static class MinStackArrayList {
        private List<Integer> stack;
        private List<Integer> minStack;
        
        public MinStackArrayList() {
            stack = new ArrayList<>();
            minStack = new ArrayList<>();
        }
        
        public void push(int val) {
            stack.add(val);
            
            if (minStack.isEmpty() || val <= minStack.get(minStack.size() - 1)) {
                minStack.add(val);
            }
        }
        
        public void pop() {
            if (stack.isEmpty()) return;
            
            int val = stack.remove(stack.size() - 1);
            
            if (!minStack.isEmpty() && val == minStack.get(minStack.size() - 1)) {
                minStack.remove(minStack.size() - 1);
            }
        }
        
        public int top() {
            return stack.get(stack.size() - 1);
        }
        
        public int getMin() {
            return minStack.get(minStack.size() - 1);
        }
    }
    
    /**
     * APPROACH 6: LINKED LIST IMPLEMENTATION
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n)
     * 
     * Use linked list for custom stack implementation.
     */
    static class MinStackLinkedList {
        private StackNode head;
        
        private static class StackNode {
            int val;
            int min;
            StackNode next;
            
            StackNode(int val, int min, StackNode next) {
                this.val = val;
                this.min = min;
                this.next = next;
            }
        }
        
        public MinStackLinkedList() {
            head = null;
        }
        
        public void push(int val) {
            int min = (head == null) ? val : Math.min(val, head.min);
            head = new StackNode(val, min, head);
        }
        
        public void pop() {
            if (head != null) {
                head = head.next;
            }
        }
        
        public int top() {
            return head.val;
        }
        
        public int getMin() {
            return head.min;
        }
    }
    
    /**
     * APPROACH 7: USING DEQUE
     * Time Complexity: O(1) for all operations
     * Space Complexity: O(n)
     * 
     * Use Deque as underlying data structure.
     */
    static class MinStackDeque {
        private Deque<Integer> stack;
        private Deque<Integer> minStack;
        
        public MinStackDeque() {
            stack = new ArrayDeque<>();
            minStack = new ArrayDeque<>();
        }
        
        public void push(int val) {
            stack.push(val);
            
            if (minStack.isEmpty() || val <= minStack.peek()) {
                minStack.push(val);
            }
        }
        
        public void pop() {
            if (stack.isEmpty()) return;
            
            int val = stack.pop();
            
            if (!minStack.isEmpty() && val == minStack.peek()) {
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
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        System.out.println("=== Testing MinStack Implementations ===");
        
        // Test case 1: Basic operations
        System.out.println("\nTest Case 1: Basic Operations");
        testBasicOperations();
        
        // Test case 2: Multiple minimums
        System.out.println("\nTest Case 2: Multiple Minimums");
        testMultipleMinimums();
        
        // Test case 3: Edge cases
        System.out.println("\nTest Case 3: Edge Cases");
        testEdgeCases();
        
        // Performance test
        System.out.println("\n=== Performance Test ===");
        performanceTest();
    }
    
    private static void testBasicOperations() {
        MinStackTwoStacks minStack = new MinStackTwoStacks();
        
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        
        System.out.println("After pushing -2, 0, -3:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return -3
        
        minStack.pop();
        System.out.println("After popping:");
        System.out.println("top(): " + minStack.top());       // Should return 0
        System.out.println("getMin(): " + minStack.getMin()); // Should return -2
    }
    
    private static void testMultipleMinimums() {
        MinStackSingleStack minStack = new MinStackSingleStack();
        
        minStack.push(1);
        minStack.push(2);
        minStack.push(0);  // New minimum
        minStack.push(0);  // Same minimum
        minStack.push(-1); // New minimum
        
        System.out.println("After pushing 1, 2, 0, 0, -1:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return -1
        
        minStack.pop(); // Remove -1
        System.out.println("After popping -1:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return 0
        
        minStack.pop(); // Remove 0
        System.out.println("After popping 0:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return 0
        
        minStack.pop(); // Remove 0
        System.out.println("After popping 0:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return 1
    }
    
    private static void testEdgeCases() {
        // Test with large numbers
        MinStackOptimized minStack = new MinStackOptimized();
        
        minStack.push(Integer.MAX_VALUE);
        minStack.push(Integer.MIN_VALUE);
        minStack.push(0);
        
        System.out.println("After pushing MAX_VALUE, MIN_VALUE, 0:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return MIN_VALUE
        
        minStack.pop(); // Remove 0
        System.out.println("After popping 0:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return MIN_VALUE
        
        minStack.pop(); // Remove MIN_VALUE
        System.out.println("After popping MIN_VALUE:");
        System.out.println("getMin(): " + minStack.getMin()); // Should return MAX_VALUE
    }
    
    private static void performanceTest() {
        int operations = 100000;
        
        // Test TwoStacks approach
        long start = System.nanoTime();
        MinStackTwoStacks minStack1 = new MinStackTwoStacks();
        
        for (int i = 0; i < operations; i++) {
            minStack1.push(i % 1000);
            if (i % 10 == 0) {
                minStack1.getMin();
            }
            if (i % 5 == 0 && i > 0) {
                minStack1.pop();
            }
        }
        long end = System.nanoTime();
        System.out.println("TwoStacks: " + (end - start) / 1000000.0 + " ms");
        
        // Test SingleStack approach
        start = System.nanoTime();
        MinStackSingleStack minStack2 = new MinStackSingleStack();
        
        for (int i = 0; i < operations; i++) {
            minStack2.push(i % 1000);
            if (i % 10 == 0) {
                minStack2.getMin();
            }
            if (i % 5 == 0 && i > 0) {
                minStack2.pop();
            }
        }
        end = System.nanoTime();
        System.out.println("SingleStack: " + (end - start) / 1000000.0 + " ms");
        
        // Test LinkedList approach
        start = System.nanoTime();
        MinStackLinkedList minStack3 = new MinStackLinkedList();
        
        for (int i = 0; i < operations; i++) {
            minStack3.push(i % 1000);
            if (i % 10 == 0) {
                minStack3.getMin();
            }
            if (i % 5 == 0 && i > 0) {
                minStack3.pop();
            }
        }
        end = System.nanoTime();
        System.out.println("LinkedList: " + (end - start) / 1000000.0 + " ms");
        
        // Test Deque approach
        start = System.nanoTime();
        MinStackDeque minStack4 = new MinStackDeque();
        
        for (int i = 0; i < operations; i++) {
            minStack4.push(i % 1000);
            if (i % 10 == 0) {
                minStack4.getMin();
            }
            if (i % 5 == 0 && i > 0) {
                minStack4.pop();
            }
        }
        end = System.nanoTime();
        System.out.println("Deque: " + (end - start) / 1000000.0 + " ms");
    }
    
    /**
     * Usage example demonstrating all implementations
     */
    public static void demonstrateAllImplementations() {
        System.out.println("=== Demonstrating All MinStack Implementations ===");
        
        // Test all implementations with same operations
        int[] values = {-2, 0, -3};
        
        System.out.println("\nTesting TwoStacks:");
        testImplementation(new MinStackTwoStacks(), values);
        
        System.out.println("\nTesting SingleStack:");
        testImplementation(new MinStackSingleStack(), values);
        
        System.out.println("\nTesting Optimized:");
        testImplementation(new MinStackOptimized(), values);
        
        System.out.println("\nTesting LinkedList:");
        testImplementation(new MinStackLinkedList(), values);
        
        System.out.println("\nTesting Deque:");
        testImplementation(new MinStackDeque(), values);
    }
    
    private static void testImplementation(Object minStack, int[] values) {
        try {
            for (int val : values) {
                if (minStack instanceof MinStackTwoStacks) {
                    ((MinStackTwoStacks) minStack).push(val);
                } else if (minStack instanceof MinStackSingleStack) {
                    ((MinStackSingleStack) minStack).push(val);
                } else if (minStack instanceof MinStackOptimized) {
                    ((MinStackOptimized) minStack).push(val);
                } else if (minStack instanceof MinStackLinkedList) {
                    ((MinStackLinkedList) minStack).push(val);
                } else if (minStack instanceof MinStackDeque) {
                    ((MinStackDeque) minStack).push(val);
                }
            }
            
            int min = 0;
            if (minStack instanceof MinStackTwoStacks) {
                min = ((MinStackTwoStacks) minStack).getMin();
            } else if (minStack instanceof MinStackSingleStack) {
                min = ((MinStackSingleStack) minStack).getMin();
            } else if (minStack instanceof MinStackOptimized) {
                min = ((MinStackOptimized) minStack).getMin();
            } else if (minStack instanceof MinStackLinkedList) {
                min = ((MinStackLinkedList) minStack).getMin();
            } else if (minStack instanceof MinStackDeque) {
                min = ((MinStackDeque) minStack).getMin();
            }
            
            System.out.println("Min after pushing " + Arrays.toString(values) + ": " + min);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
} 