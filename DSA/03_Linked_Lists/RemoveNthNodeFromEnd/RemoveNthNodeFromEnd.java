import java.util.*;

/**
 * Problem: Remove Nth Node From End of List
 * 
 * Given the head of a linked list, remove the nth node from the end of the list and return its head.
 * 
 * Example:
 * Input: head = [1,2,3,4,5], n = 2
 * Output: [1,2,3,5]
 * 
 * Example 2:
 * Input: head = [1], n = 1
 * Output: []
 * 
 * Example 3:
 * Input: head = [1,2], n = 1
 * Output: [1]
 * 
 * Constraints:
 * - The number of nodes in the list is sz.
 * - 1 <= sz <= 30
 * - 0 <= Node.val <= 100
 * - 1 <= n <= sz
 * 
 * Follow up: Could you do this in one pass?
 */
public class RemoveNthNodeFromEnd {
    
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
     * APPROACH 1: TWO PASS - COUNT THEN REMOVE
     * Time Complexity: O(L) where L is length of list
     * Space Complexity: O(1)
     * 
     * First pass to count nodes, second pass to remove the target node.
     */
    public ListNode removeNthFromEndTwoPass(ListNode head, int n) {
        if (head == null) return null;
        
        // First pass: count total nodes
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }
        
        // Edge case: remove head
        if (n == length) {
            return head.next;
        }
        
        // Second pass: find the node before the target
        int targetFromStart = length - n;
        current = head;
        
        for (int i = 0; i < targetFromStart - 1; i++) {
            current = current.next;
        }
        
        // Remove the target node
        current.next = current.next.next;
        
        return head;
    }
    
    /**
     * APPROACH 2: ONE PASS WITH TWO POINTERS (Optimal)
     * Time Complexity: O(L)
     * Space Complexity: O(1)
     * 
     * Use two pointers with n gap between them.
     */
    public ListNode removeNthFromEndOnePass(ListNode head, int n) {
        // Use dummy node to handle edge cases
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        ListNode first = dummy;
        ListNode second = dummy;
        
        // Move first pointer n+1 steps ahead
        for (int i = 0; i <= n; i++) {
            first = first.next;
        }
        
        // Move both pointers until first reaches end
        while (first != null) {
            first = first.next;
            second = second.next;
        }
        
        // Remove the target node
        second.next = second.next.next;
        
        return dummy.next;
    }
    
    /**
     * APPROACH 3: USING ARRAYLIST (Convert to Array)
     * Time Complexity: O(L)
     * Space Complexity: O(L)
     * 
     * Convert to array, remove element, rebuild list.
     */
    public ListNode removeNthFromEndArrayList(ListNode head, int n) {
        if (head == null) return null;
        
        // Convert to list
        List<Integer> values = new ArrayList<>();
        ListNode current = head;
        
        while (current != null) {
            values.add(current.val);
            current = current.next;
        }
        
        // Remove nth from end
        int removeIndex = values.size() - n;
        values.remove(removeIndex);
        
        // Rebuild linked list
        if (values.isEmpty()) return null;
        
        ListNode dummy = new ListNode(0);
        current = dummy;
        
        for (int val : values) {
            current.next = new ListNode(val);
            current = current.next;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 4: USING STACK
     * Time Complexity: O(L)
     * Space Complexity: O(L)
     * 
     * Push all nodes to stack, pop n nodes, remove the target.
     */
    public ListNode removeNthFromEndStack(ListNode head, int n) {
        if (head == null) return null;
        
        // Push all nodes to stack
        Stack<ListNode> stack = new Stack<>();
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        ListNode current = dummy;
        while (current != null) {
            stack.push(current);
            current = current.next;
        }
        
        // Pop n nodes to reach the node before target
        for (int i = 0; i < n; i++) {
            stack.pop();
        }
        
        // Remove target node
        ListNode prevNode = stack.peek();
        prevNode.next = prevNode.next.next;
        
        return dummy.next;
    }
    
    /**
     * APPROACH 5: RECURSIVE APPROACH
     * Time Complexity: O(L)
     * Space Complexity: O(L) - Due to recursion stack
     * 
     * Use recursion to count from the end and remove when counter matches.
     */
    public ListNode removeNthFromEndRecursive(ListNode head, int n) {
        int[] count = {0}; // Use array to pass by reference
        return removeHelper(head, n, count);
    }
    
    private ListNode removeHelper(ListNode node, int n, int[] count) {
        if (node == null) {
            return null;
        }
        
        node.next = removeHelper(node.next, n, count);
        count[0]++;
        
        // If this is the nth node from end, remove it
        if (count[0] == n) {
            return node.next;
        }
        
        return node;
    }
    
    /**
     * APPROACH 6: THREE POINTERS APPROACH
     * Time Complexity: O(L)
     * Space Complexity: O(1)
     * 
     * Use three pointers: prev, current, and fast.
     */
    public ListNode removeNthFromEndThreePointers(ListNode head, int n) {
        if (head == null) return null;
        
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        ListNode prev = dummy;
        ListNode current = head;
        ListNode fast = head;
        
        // Move fast pointer n steps ahead
        for (int i = 0; i < n; i++) {
            fast = fast.next;
        }
        
        // Move all pointers until fast reaches end
        while (fast != null) {
            prev = current;
            current = current.next;
            fast = fast.next;
        }
        
        // Remove current node
        prev.next = current.next;
        
        return dummy.next;
    }
    
    /**
     * APPROACH 7: USING DEQUE
     * Time Complexity: O(L)
     * Space Complexity: O(L)
     * 
     * Use deque to maintain a sliding window of n+1 nodes.
     */
    public ListNode removeNthFromEndDeque(ListNode head, int n) {
        if (head == null) return null;
        
        Deque<ListNode> deque = new ArrayDeque<>();
        ListNode dummy = new ListNode(0);
        dummy.next = head;
        
        ListNode current = dummy;
        
        // Fill deque with first n+1 nodes
        for (int i = 0; i <= n && current != null; i++) {
            deque.offerLast(current);
            current = current.next;
        }
        
        // Slide the window
        while (current != null) {
            deque.pollFirst();
            deque.offerLast(current);
            current = current.next;
        }
        
        // Remove the target node
        ListNode prevNode = deque.peekFirst();
        prevNode.next = prevNode.next.next;
        
        return dummy.next;
    }
    
    /**
     * APPROACH 8: ITERATIVE WITH LENGTH CALCULATION
     * Time Complexity: O(L)
     * Space Complexity: O(1)
     * 
     * Alternative implementation of two-pass approach.
     */
    public ListNode removeNthFromEndIterative(ListNode head, int n) {
        if (head == null) return null;
        
        // Calculate length
        int length = getLength(head);
        
        // Handle edge case: removing head
        if (n == length) {
            return head.next;
        }
        
        // Find the node before target
        ListNode current = head;
        int stepsToTarget = length - n - 1;
        
        for (int i = 0; i < stepsToTarget; i++) {
            current = current.next;
        }
        
        // Remove target node
        current.next = current.next.next;
        
        return head;
    }
    
    private int getLength(ListNode head) {
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }
        return length;
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
     * Convert linked list to array
     */
    public static int[] listToArray(ListNode head) {
        List<Integer> values = new ArrayList<>();
        ListNode current = head;
        
        while (current != null) {
            values.add(current.val);
            current = current.next;
        }
        
        return values.stream().mapToInt(i -> i).toArray();
    }
    
    /**
     * Print linked list
     */
    public static void printList(ListNode head) {
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
     * Create a copy of the linked list
     */
    public static ListNode copyList(ListNode head) {
        if (head == null) {
            return null;
        }
        
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        ListNode original = head;
        
        while (original != null) {
            current.next = new ListNode(original.val);
            current = current.next;
            original = original.next;
        }
        
        return dummy.next;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        RemoveNthNodeFromEnd solution = new RemoveNthNodeFromEnd();
        
        // Test case 1: Normal case
        System.out.println("Test Case 1: [1,2,3,4,5], remove 2nd from end");
        ListNode head1 = createList(new int[]{1, 2, 3, 4, 5});
        
        System.out.print("Original: ");
        printList(head1);
        
        ListNode result1 = solution.removeNthFromEndTwoPass(copyList(head1), 2);
        System.out.print("Two Pass: ");
        printList(result1);
        
        ListNode result2 = solution.removeNthFromEndOnePass(copyList(head1), 2);
        System.out.print("One Pass: ");
        printList(result2);
        
        ListNode result3 = solution.removeNthFromEndArrayList(copyList(head1), 2);
        System.out.print("ArrayList: ");
        printList(result3);
        
        ListNode result4 = solution.removeNthFromEndStack(copyList(head1), 2);
        System.out.print("Stack: ");
        printList(result4);
        
        ListNode result5 = solution.removeNthFromEndRecursive(copyList(head1), 2);
        System.out.print("Recursive: ");
        printList(result5);
        
        ListNode result6 = solution.removeNthFromEndThreePointers(copyList(head1), 2);
        System.out.print("Three Pointers: ");
        printList(result6);
        
        ListNode result7 = solution.removeNthFromEndDeque(copyList(head1), 2);
        System.out.print("Deque: ");
        printList(result7);
        
        ListNode result8 = solution.removeNthFromEndIterative(copyList(head1), 2);
        System.out.print("Iterative: ");
        printList(result8);
        
        System.out.println();
        
        // Test case 2: Remove head (single node)
        System.out.println("Test Case 2: [1], remove 1st from end");
        ListNode head2 = createList(new int[]{1});
        
        System.out.print("Original: ");
        printList(head2);
        
        ListNode result2_1 = solution.removeNthFromEndOnePass(copyList(head2), 1);
        System.out.print("Result: ");
        printList(result2_1);
        System.out.println();
        
        // Test case 3: Remove head (multiple nodes)
        System.out.println("Test Case 3: [1,2,3], remove 3rd from end (head)");
        ListNode head3 = createList(new int[]{1, 2, 3});
        
        System.out.print("Original: ");
        printList(head3);
        
        ListNode result3_1 = solution.removeNthFromEndOnePass(copyList(head3), 3);
        System.out.print("Result: ");
        printList(result3_1);
        System.out.println();
        
        // Test case 4: Remove tail
        System.out.println("Test Case 4: [1,2,3,4], remove 1st from end (tail)");
        ListNode head4 = createList(new int[]{1, 2, 3, 4});
        
        System.out.print("Original: ");
        printList(head4);
        
        ListNode result4_1 = solution.removeNthFromEndOnePass(copyList(head4), 1);
        System.out.print("Result: ");
        printList(result4_1);
        System.out.println();
        
        // Test case 5: Two nodes
        System.out.println("Test Case 5: [1,2], remove 1st from end");
        ListNode head5 = createList(new int[]{1, 2});
        
        System.out.print("Original: ");
        printList(head5);
        
        ListNode result5_1 = solution.removeNthFromEndOnePass(copyList(head5), 1);
        System.out.print("Result: ");
        printList(result5_1);
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(RemoveNthNodeFromEnd solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large linked list
        int size = 100000;
        int[] largeArray = new int[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = i;
        }
        ListNode largeList = createList(largeArray);
        
        int n = size / 2; // Remove middle element
        
        long start, end;
        
        // Test One Pass approach
        start = System.nanoTime();
        ListNode result1 = solution.removeNthFromEndOnePass(copyList(largeList), n);
        end = System.nanoTime();
        System.out.println("One Pass: " + (end - start) / 1000000.0 + " ms");
        
        // Test Two Pass approach
        start = System.nanoTime();
        ListNode result2 = solution.removeNthFromEndTwoPass(copyList(largeList), n);
        end = System.nanoTime();
        System.out.println("Two Pass: " + (end - start) / 1000000.0 + " ms");
        
        // Test Three Pointers approach
        start = System.nanoTime();
        ListNode result3 = solution.removeNthFromEndThreePointers(copyList(largeList), n);
        end = System.nanoTime();
        System.out.println("Three Pointers: " + (end - start) / 1000000.0 + " ms");
        
        // Test Stack approach
        start = System.nanoTime();
        ListNode result4 = solution.removeNthFromEndStack(copyList(largeList), n);
        end = System.nanoTime();
        System.out.println("Stack: " + (end - start) / 1000000.0 + " ms");
        
        // Test Recursive approach (with smaller list to avoid stack overflow)
        ListNode smallList = createList(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        start = System.nanoTime();
        ListNode result5 = solution.removeNthFromEndRecursive(smallList, 3);
        end = System.nanoTime();
        System.out.println("Recursive (small list): " + (end - start) / 1000000.0 + " ms");
    }
} 