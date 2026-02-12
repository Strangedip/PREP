import java.util.*;

/**
 * Problem: Reverse Linked List
 * 
 * Given the head of a singly linked list, reverse the list, and return the reversed list.
 * 
 * Example:
 * Input: head = [1,2,3,4,5]
 * Output: [5,4,3,2,1]
 * 
 * Example 2:
 * Input: head = [1,2]
 * Output: [2,1]
 * 
 * Example 3:
 * Input: head = []
 * Output: []
 * 
 * Constraints:
 * - The number of nodes in the list is the range [0, 5000].
 * - -5000 <= Node.val <= 5000
 * 
 * Follow up: A linked list can be reversed either iteratively or recursively. 
 * Could you implement both?
 */
public class ReverseLinkedList {
    
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
     * APPROACH 1: ITERATIVE WITH THREE POINTERS (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use three pointers to reverse the links iteratively.
     */
    public ListNode reverseListIterative(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        
        ListNode prev = null;
        ListNode current = head;
        
        while (current != null) {
            ListNode nextTemp = current.next;  // Store next node
            current.next = prev;               // Reverse the link
            prev = current;                    // Move prev forward
            current = nextTemp;                // Move current forward
        }
        
        return prev; // prev is the new head
    }
    
    /**
     * APPROACH 2: RECURSIVE APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Due to recursion stack
     * 
     * Recursively reverse the list by returning the new head from the end.
     */
    public ListNode reverseListRecursive(ListNode head) {
        // Base case: empty list or single node
        if (head == null || head.next == null) {
            return head;
        }
        
        // Recursively reverse the rest of the list
        ListNode newHead = reverseListRecursive(head.next);
        
        // Reverse the current connection
        head.next.next = head;
        head.next = null;
        
        return newHead;
    }
    
    /**
     * APPROACH 3: USING STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Push all nodes to stack, then pop them to reverse order.
     */
    public ListNode reverseListStack(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        
        Stack<ListNode> stack = new Stack<>();
        ListNode current = head;
        
        // Push all nodes to stack
        while (current != null) {
            stack.push(current);
            current = current.next;
        }
        
        // Pop nodes and rebuild the list
        ListNode newHead = stack.pop();
        current = newHead;
        
        while (!stack.isEmpty()) {
            current.next = stack.pop();
            current = current.next;
        }
        
        current.next = null; // Important: set last node's next to null
        return newHead;
    }
    
    /**
     * APPROACH 4: RECURSIVE WITH HELPER
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use helper function to carry previous node information.
     */
    public ListNode reverseListRecursiveHelper(ListNode head) {
        return reverseHelper(head, null);
    }
    
    private ListNode reverseHelper(ListNode current, ListNode prev) {
        if (current == null) {
            return prev;
        }
        
        ListNode next = current.next;
        current.next = prev;
        return reverseHelper(next, current);
    }
    
    /**
     * APPROACH 5: USING ARRAYLIST (Convert and Rebuild)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Convert to list, reverse, then rebuild linked list.
     */
    public ListNode reverseListArrayList(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        
        List<Integer> values = new ArrayList<>();
        ListNode current = head;
        
        // Collect all values
        while (current != null) {
            values.add(current.val);
            current = current.next;
        }
        
        // Reverse the list
        Collections.reverse(values);
        
        // Rebuild linked list
        ListNode dummy = new ListNode(0);
        current = dummy;
        
        for (int val : values) {
            current.next = new ListNode(val);
            current = current.next;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 6: TWO-PASS APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Alternative iterative approach with different pointer management.
     */
    public ListNode reverseListTwoPass(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        
        // First pass: find the tail
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
        }
        
        // Second pass: reverse
        ListNode newHead = tail;
        reverseFrom(head, null);
        
        return newHead;
    }
    
    private ListNode reverseFrom(ListNode node, ListNode prev) {
        if (node == null) {
            return null;
        }
        
        if (node.next == null) {
            node.next = prev;
            return node;
        }
        
        ListNode next = node.next;
        node.next = prev;
        return reverseFrom(next, node);
    }
    
    /**
     * APPROACH 7: SWAP VALUES (Alternative - Changes values, not structure)
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Collect values and reassign in reverse order.
     * Note: This changes values, not the actual link structure.
     */
    public ListNode reverseListSwapValues(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }
        
        // Collect all values
        List<Integer> values = new ArrayList<>();
        ListNode current = head;
        
        while (current != null) {
            values.add(current.val);
            current = current.next;
        }
        
        // Reassign values in reverse order
        current = head;
        for (int i = values.size() - 1; i >= 0; i--) {
            current.val = values.get(i);
            current = current.next;
        }
        
        return head;
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
     * Convert linked list to array for easy comparison
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
        ReverseLinkedList solution = new ReverseLinkedList();
        
        // Test case 1: Normal list
        System.out.println("Test Case 1: [1,2,3,4,5]");
        ListNode head1 = createList(new int[]{1, 2, 3, 4, 5});
        System.out.print("Original: ");
        printList(head1);
        
        ListNode result1 = solution.reverseListIterative(copyList(head1));
        System.out.print("Iterative: ");
        printList(result1);
        
        ListNode result2 = solution.reverseListRecursive(copyList(head1));
        System.out.print("Recursive: ");
        printList(result2);
        
        ListNode result3 = solution.reverseListStack(copyList(head1));
        System.out.print("Stack: ");
        printList(result3);
        
        ListNode result4 = solution.reverseListRecursiveHelper(copyList(head1));
        System.out.print("Recursive Helper: ");
        printList(result4);
        
        ListNode result5 = solution.reverseListArrayList(copyList(head1));
        System.out.print("ArrayList: ");
        printList(result5);
        
        ListNode copy1 = copyList(head1);
        ListNode result7 = solution.reverseListSwapValues(copy1);
        System.out.print("Swap Values: ");
        printList(result7);
        
        System.out.println();
        
        // Test case 2: Two nodes
        System.out.println("Test Case 2: [1,2]");
        ListNode head2 = createList(new int[]{1, 2});
        System.out.print("Original: ");
        printList(head2);
        
        ListNode result2_1 = solution.reverseListIterative(copyList(head2));
        System.out.print("Reversed: ");
        printList(result2_1);
        System.out.println();
        
        // Test case 3: Single node
        System.out.println("Test Case 3: [1]");
        ListNode head3 = createList(new int[]{1});
        System.out.print("Original: ");
        printList(head3);
        
        ListNode result3_1 = solution.reverseListIterative(copyList(head3));
        System.out.print("Reversed: ");
        printList(result3_1);
        System.out.println();
        
        // Test case 4: Empty list
        System.out.println("Test Case 4: []");
        ListNode head4 = null;
        System.out.print("Original: ");
        printList(head4);
        
        ListNode result4_1 = solution.reverseListIterative(head4);
        System.out.print("Reversed: ");
        printList(result4_1);
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(ReverseLinkedList solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large linked list
        int size = 100000;
        int[] largeArray = new int[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = i;
        }
        ListNode largeList = createList(largeArray);
        
        long start, end;
        
        // Test Iterative approach
        start = System.nanoTime();
        ListNode result1 = solution.reverseListIterative(copyList(largeList));
        end = System.nanoTime();
        System.out.println("Iterative: " + (end - start) / 1000000.0 + " ms");
        
        // Test Stack approach
        start = System.nanoTime();
        ListNode result2 = solution.reverseListStack(copyList(largeList));
        end = System.nanoTime();
        System.out.println("Stack: " + (end - start) / 1000000.0 + " ms");
        
        // Test ArrayList approach
        start = System.nanoTime();
        ListNode result3 = solution.reverseListArrayList(copyList(largeList));
        end = System.nanoTime();
        System.out.println("ArrayList: " + (end - start) / 1000000.0 + " ms");
        
        // Test Recursive approach (with smaller list to avoid stack overflow)
        ListNode smallList = createList(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        start = System.nanoTime();
        ListNode result4 = solution.reverseListRecursive(smallList);
        end = System.nanoTime();
        System.out.println("Recursive (small list): " + (end - start) / 1000000.0 + " ms");
    }
} 