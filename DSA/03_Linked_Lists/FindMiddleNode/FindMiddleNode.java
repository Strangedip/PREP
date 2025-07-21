import java.util.*;

/**
 * Problem: Find Middle Node of Linked List
 * 
 * Given the head of a singly linked list, return the middle node of the linked list.
 * If there are two middle nodes, return the second middle node.
 * 
 * Example:
 * Input: head = [1,2,3,4,5]
 * Output: [3,4,5]
 * Explanation: The middle node of the list is node 3.
 * 
 * Example 2:
 * Input: head = [1,2,3,4,5,6]
 * Output: [4,5,6]
 * Explanation: Since the list has two middle nodes with values 3 and 4, we return the second one.
 * 
 * Constraints:
 * - The number of nodes in the list is in the range [1, 100].
 * - 1 <= Node.val <= 100
 */
public class FindMiddleNode {
    
    /**
     * Definition for singly-linked list.
     */
    public static class ListNode {
        int val;
        ListNode next;
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }
    
    /**
     * APPROACH 1: TWO POINTERS - TORTOISE AND HARE (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use slow and fast pointers. When fast reaches end, slow is at middle.
     */
    public ListNode middleNodeTwoPointers(ListNode head) {
        if (head == null) return null;
        
        ListNode slow = head;
        ListNode fast = head;
        
        // Fast moves 2 steps, slow moves 1 step
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        
        return slow; // slow is at middle when fast reaches end
    }
    
    /**
     * APPROACH 2: COUNT LENGTH THEN FIND MIDDLE
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Count total nodes, then traverse to middle position.
     */
    public ListNode middleNodeCountLength(ListNode head) {
        if (head == null) return null;
        
        // First pass: count nodes
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }
        
        // Second pass: go to middle
        int middlePos = length / 2; // 0-based index
        current = head;
        for (int i = 0; i < middlePos; i++) {
            current = current.next;
        }
        
        return current;
    }
    
    /**
     * APPROACH 3: USING ARRAYLIST
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Store all nodes in array, return middle index.
     */
    public ListNode middleNodeArrayList(ListNode head) {
        if (head == null) return null;
        
        List<ListNode> nodes = new ArrayList<>();
        ListNode current = head;
        
        // Store all nodes
        while (current != null) {
            nodes.add(current);
            current = current.next;
        }
        
        // Return middle node
        return nodes.get(nodes.size() / 2);
    }
    
    /**
     * APPROACH 4: RECURSIVE WITH COUNTING
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Due to recursion stack
     * 
     * Use recursion to count and find middle.
     */
    public ListNode middleNodeRecursive(ListNode head) {
        if (head == null) return null;
        
        int length = getLength(head);
        int middlePos = length / 2;
        return getNodeAt(head, middlePos);
    }
    
    private int getLength(ListNode node) {
        if (node == null) return 0;
        return 1 + getLength(node.next);
    }
    
    private ListNode getNodeAt(ListNode node, int position) {
        if (position == 0) return node;
        return getNodeAt(node.next, position - 1);
    }
    
    /**
     * APPROACH 5: USING STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Push all nodes to stack, pop half to reach middle.
     */
    public ListNode middleNodeStack(ListNode head) {
        if (head == null) return null;
        
        Stack<ListNode> stack = new Stack<>();
        ListNode current = head;
        
        // Push all nodes to stack
        while (current != null) {
            stack.push(current);
            current = current.next;
        }
        
        int totalNodes = stack.size();
        int nodesToPop = totalNodes / 2;
        
        // Pop nodes to reach middle
        for (int i = 0; i < nodesToPop; i++) {
            stack.pop();
        }
        
        return stack.peek();
    }
    
    /**
     * APPROACH 6: ITERATIVE WITH ALTERNATING MOVEMENT
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Move middle pointer every two steps of current pointer.
     */
    public ListNode middleNodeAlternating(ListNode head) {
        if (head == null) return null;
        
        ListNode current = head;
        ListNode middle = head;
        int step = 0;
        
        while (current != null) {
            current = current.next;
            step++;
            
            // Move middle pointer every 2 steps
            if (step % 2 == 0) {
                middle = middle.next;
            }
        }
        
        return middle;
    }
    
    /**
     * APPROACH 7: USING DEQUE
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use deque to store nodes and access middle element.
     */
    public ListNode middleNodeDeque(ListNode head) {
        if (head == null) return null;
        
        Deque<ListNode> deque = new ArrayDeque<>();
        ListNode current = head;
        
        // Add all nodes to deque
        while (current != null) {
            deque.addLast(current);
            current = current.next;
        }
        
        int totalNodes = deque.size();
        int middleIndex = totalNodes / 2;
        
        // Remove nodes from front to reach middle
        for (int i = 0; i < middleIndex; i++) {
            deque.removeFirst();
        }
        
        return deque.peekFirst();
    }
    
    /**
     * APPROACH 8: FIND BOTH MIDDLE NODES (For Even Length)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * For even length lists, return both middle nodes.
     */
    public ListNode[] findBothMiddleNodes(ListNode head) {
        if (head == null) return new ListNode[]{null, null};
        if (head.next == null) return new ListNode[]{head, head};
        
        ListNode slow = head;
        ListNode fast = head;
        ListNode prevSlow = null;
        
        while (fast != null && fast.next != null) {
            prevSlow = slow;
            slow = slow.next;
            fast = fast.next.next;
        }
        
        // If odd length, both middle nodes are the same
        if (fast != null) {
            return new ListNode[]{slow, slow};
        } else {
            // Even length, return both middle nodes
            return new ListNode[]{prevSlow, slow};
        }
    }
    
    public ListNode middleNodeSecondOfTwo(ListNode head) {
        ListNode[] both = findBothMiddleNodes(head);
        return both[1]; // Return second middle node
    }
    
    /**
     * APPROACH 9: ONE-PASS WITH POSITION TRACKING
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Track position and update middle reference at right times.
     */
    public ListNode middleNodePositionTracking(ListNode head) {
        if (head == null) return null;
        
        ListNode current = head;
        ListNode middle = head;
        int position = 0;
        int middlePosition = 0;
        
        while (current != null) {
            // When we've seen enough nodes to move middle pointer
            if (position >= middlePosition * 2 + 1) {
                middle = middle.next;
                middlePosition++;
            }
            
            current = current.next;
            position++;
        }
        
        return middle;
    }
    
    // Helper methods for testing
    
    /**
     * Create linked list from array
     */
    public static ListNode createList(int[] values) {
        if (values == null || values.length == 0) {
            return null;
        }
        
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        for (int val : values) {
            current.next = new ListNode(val);
            current = current.next;
        }
        
        return dummy.next;
    }
    
    /**
     * Print linked list starting from given node
     */
    public static void printListFromNode(ListNode head) {
        if (head == null) {
            System.out.println("[]");
            return;
        }
        
        System.out.print("[");
        ListNode current = head;
        while (current != null) {
            System.out.print(current.val);
            if (current.next != null) {
                System.out.print(",");
            }
            current = current.next;
        }
        System.out.println("]");
    }
    
    /**
     * Print full linked list
     */
    public static void printList(ListNode head) {
        printListFromNode(head);
    }
    
    /**
     * Get length of linked list
     */
    public static int getListLength(ListNode head) {
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }
        return length;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        FindMiddleNode solution = new FindMiddleNode();
        
        // Test case 1: Odd length list
        System.out.println("Test Case 1: [1,2,3,4,5] (odd length)");
        ListNode head1 = createList(new int[]{1, 2, 3, 4, 5});
        
        System.out.print("Original: ");
        printList(head1);
        
        ListNode middle1 = solution.middleNodeTwoPointers(head1);
        System.out.print("Two Pointers: ");
        printListFromNode(middle1);
        System.out.println("Middle value: " + middle1.val);
        
        ListNode middle2 = solution.middleNodeCountLength(head1);
        System.out.print("Count Length: ");
        printListFromNode(middle2);
        System.out.println("Middle value: " + middle2.val);
        
        ListNode middle3 = solution.middleNodeArrayList(head1);
        System.out.print("ArrayList: ");
        printListFromNode(middle3);
        System.out.println("Middle value: " + middle3.val);
        
        ListNode middle4 = solution.middleNodeRecursive(head1);
        System.out.print("Recursive: ");
        printListFromNode(middle4);
        System.out.println("Middle value: " + middle4.val);
        
        ListNode middle5 = solution.middleNodeStack(head1);
        System.out.print("Stack: ");
        printListFromNode(middle5);
        System.out.println("Middle value: " + middle5.val);
        
        System.out.println();
        
        // Test case 2: Even length list
        System.out.println("Test Case 2: [1,2,3,4,5,6] (even length)");
        ListNode head2 = createList(new int[]{1, 2, 3, 4, 5, 6});
        
        System.out.print("Original: ");
        printList(head2);
        
        ListNode middle2_1 = solution.middleNodeTwoPointers(head2);
        System.out.print("Two Pointers: ");
        printListFromNode(middle2_1);
        System.out.println("Middle value: " + middle2_1.val);
        
        // Show both middle nodes for even length
        ListNode[] bothMiddle = solution.findBothMiddleNodes(head2);
        System.out.println("Both middle nodes: " + bothMiddle[0].val + " and " + bothMiddle[1].val);
        System.out.println("Second middle (returned): " + bothMiddle[1].val);
        
        System.out.println();
        
        // Test case 3: Single node
        System.out.println("Test Case 3: [1] (single node)");
        ListNode head3 = createList(new int[]{1});
        
        System.out.print("Original: ");
        printList(head3);
        
        ListNode middle3_1 = solution.middleNodeTwoPointers(head3);
        System.out.print("Middle: ");
        printListFromNode(middle3_1);
        System.out.println("Middle value: " + middle3_1.val);
        
        System.out.println();
        
        // Test case 4: Two nodes
        System.out.println("Test Case 4: [1,2] (two nodes)");
        ListNode head4 = createList(new int[]{1, 2});
        
        System.out.print("Original: ");
        printList(head4);
        
        ListNode middle4_1 = solution.middleNodeTwoPointers(head4);
        System.out.print("Middle: ");
        printListFromNode(middle4_1);
        System.out.println("Middle value: " + middle4_1.val);
        
        System.out.println();
        
        // Test case 5: Large odd list
        System.out.println("Test Case 5: [1,2,3,4,5,6,7,8,9] (large odd)");
        ListNode head5 = createList(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});
        
        System.out.print("Original: ");
        printList(head5);
        
        ListNode middle5_1 = solution.middleNodeTwoPointers(head5);
        System.out.print("Middle: ");
        printListFromNode(middle5_1);
        System.out.println("Middle value: " + middle5_1.val);
        
        System.out.println();
        
        // Test case 6: Large even list
        System.out.println("Test Case 6: [1,2,3,4,5,6,7,8] (large even)");
        ListNode head6 = createList(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        
        System.out.print("Original: ");
        printList(head6);
        
        ListNode middle6_1 = solution.middleNodeTwoPointers(head6);
        System.out.print("Middle: ");
        printListFromNode(middle6_1);
        System.out.println("Middle value: " + middle6_1.val);
        
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(FindMiddleNode solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large linked list
        int size = 1000000;
        int[] largeArray = new int[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = i + 1;
        }
        ListNode largeList = createList(largeArray);
        
        long start, end;
        
        // Test Two Pointers approach (optimal)
        start = System.nanoTime();
        ListNode result1 = solution.middleNodeTwoPointers(largeList);
        end = System.nanoTime();
        System.out.println("Two Pointers: " + result1.val + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Count Length approach
        start = System.nanoTime();
        ListNode result2 = solution.middleNodeCountLength(largeList);
        end = System.nanoTime();
        System.out.println("Count Length: " + result2.val + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Alternating approach
        start = System.nanoTime();
        ListNode result3 = solution.middleNodeAlternating(largeList);
        end = System.nanoTime();
        System.out.println("Alternating: " + result3.val + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test ArrayList approach
        start = System.nanoTime();
        ListNode result4 = solution.middleNodeArrayList(largeList);
        end = System.nanoTime();
        System.out.println("ArrayList: " + result4.val + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Verify all methods return the same result
        System.out.println("All methods return same result: " + 
                          (result1.val == result2.val && 
                           result2.val == result3.val && 
                           result3.val == result4.val));
        
        System.out.println("Expected middle value for size " + size + ": " + (size / 2 + 1));
    }
} 