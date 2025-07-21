import java.util.*;

/**
 * Problem: Reorder List
 * 
 * You are given the head of a singly linked-list. The list can be represented as:
 * L0 → L1 → … → Ln - 1 → Ln
 * 
 * Reorder the list to be on the following form:
 * L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …
 * 
 * You may not modify the values in the list's nodes. Only nodes themselves may be changed.
 * 
 * Example:
 * Input: head = [1,2,3,4]
 * Output: [1,4,2,3]
 * 
 * Example 2:
 * Input: head = [1,2,3,4,5]
 * Output: [1,5,2,4,3]
 * 
 * Constraints:
 * - The number of nodes in the list is in the range [1, 5 * 10^4].
 * - 1 <= Node.val <= 1000
 */
public class ReorderList {
    
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
     * APPROACH 1: FIND MIDDLE + REVERSE + MERGE (Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * 1. Find middle of list
     * 2. Reverse second half
     * 3. Merge two halves alternately
     */
    public void reorderListOptimal(ListNode head) {
        if (head == null || head.next == null) return;
        
        // Step 1: Find middle node
        ListNode middle = findMiddle(head);
        ListNode secondHalf = middle.next;
        middle.next = null; // Split the list
        
        // Step 2: Reverse second half
        ListNode reversedSecond = reverseList(secondHalf);
        
        // Step 3: Merge alternately
        mergeLists(head, reversedSecond);
    }
    
    private ListNode findMiddle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        
        return slow;
    }
    
    private ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        
        return prev;
    }
    
    private void mergeLists(ListNode first, ListNode second) {
        while (second != null) {
            ListNode nextFirst = first.next;
            ListNode nextSecond = second.next;
            
            first.next = second;
            second.next = nextFirst;
            
            first = nextFirst;
            second = nextSecond;
        }
    }
    
    /**
     * APPROACH 2: USING ARRAYLIST
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Store all nodes in array, then relink in required order.
     */
    public void reorderListArrayList(ListNode head) {
        if (head == null || head.next == null) return;
        
        // Store all nodes in list
        List<ListNode> nodes = new ArrayList<>();
        ListNode current = head;
        
        while (current != null) {
            nodes.add(current);
            current = current.next;
        }
        
        // Reorder using two pointers
        int left = 0, right = nodes.size() - 1;
        
        while (left < right) {
            nodes.get(left).next = nodes.get(right);
            left++;
            
            if (left >= right) break;
            
            nodes.get(right).next = nodes.get(left);
            right--;
        }
        
        nodes.get(left).next = null; // Important: terminate the list
    }
    
    /**
     * APPROACH 3: USING STACK
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use stack to get nodes from end, merge with nodes from beginning.
     */
    public void reorderListStack(ListNode head) {
        if (head == null || head.next == null) return;
        
        // Push all nodes to stack
        Stack<ListNode> stack = new Stack<>();
        ListNode current = head;
        int length = 0;
        
        while (current != null) {
            stack.push(current);
            current = current.next;
            length++;
        }
        
        // Merge nodes from beginning and end
        current = head;
        int count = 0;
        
        while (count < length / 2) {
            ListNode nodeFromEnd = stack.pop();
            ListNode nextNode = current.next;
            
            current.next = nodeFromEnd;
            nodeFromEnd.next = nextNode;
            
            current = nextNode;
            count++;
        }
        
        current.next = null; // Terminate the list
    }
    
    /**
     * APPROACH 4: RECURSIVE APPROACH
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Due to recursion stack
     * 
     * Use recursion to process nodes from both ends.
     */
    public void reorderListRecursive(ListNode head) {
        if (head == null || head.next == null) return;
        
        int length = getLength(head);
        reorderHelper(head, length);
    }
    
    private ListNode reorderHelper(ListNode head, int length) {
        if (length == 1) {
            ListNode tail = head.next;
            head.next = null;
            return tail;
        }
        
        if (length == 2) {
            ListNode tail = head.next.next;
            head.next.next = null;
            return tail;
        }
        
        ListNode tail = reorderHelper(head.next, length - 2);
        ListNode nextTail = tail.next;
        
        tail.next = head.next;
        head.next = tail;
        
        return nextTail;
    }
    
    private int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }
    
    /**
     * APPROACH 5: USING DEQUE
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Use deque to access nodes from both ends efficiently.
     */
    public void reorderListDeque(ListNode head) {
        if (head == null || head.next == null) return;
        
        // Add all nodes to deque
        Deque<ListNode> deque = new ArrayDeque<>();
        ListNode current = head;
        
        while (current != null) {
            deque.addLast(current);
            current = current.next;
        }
        
        // Remove head as it's already in position
        deque.removeFirst();
        ListNode prev = head;
        boolean takeFromEnd = true;
        
        while (!deque.isEmpty()) {
            ListNode node;
            if (takeFromEnd) {
                node = deque.removeLast();
            } else {
                node = deque.removeFirst();
            }
            
            prev.next = node;
            prev = node;
            takeFromEnd = !takeFromEnd;
        }
        
        prev.next = null;
    }
    
    /**
     * APPROACH 6: ITERATIVE WITH TWO LISTS
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Alternative implementation of optimal approach with clearer separation.
     */
    public void reorderListTwoLists(ListNode head) {
        if (head == null || head.next == null) return;
        
        // Find middle and split
        ListNode[] halves = splitList(head);
        ListNode firstHalf = halves[0];
        ListNode secondHalf = halves[1];
        
        // Reverse second half
        secondHalf = reverseListIterative(secondHalf);
        
        // Merge alternately
        mergeAlternately(firstHalf, secondHalf);
    }
    
    private ListNode[] splitList(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;
        ListNode prev = null;
        
        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }
        
        prev.next = null; // Split the list
        return new ListNode[]{head, slow};
    }
    
    private ListNode reverseListIterative(ListNode head) {
        ListNode prev = null;
        ListNode current = head;
        
        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }
        
        return prev;
    }
    
    private void mergeAlternately(ListNode first, ListNode second) {
        while (first != null && second != null) {
            ListNode firstNext = first.next;
            ListNode secondNext = second.next;
            
            first.next = second;
            second.next = firstNext;
            
            first = firstNext;
            second = secondNext;
        }
    }
    
    /**
     * APPROACH 7: IN-PLACE WITH NODE MOVEMENT
     * Time Complexity: O(n²) in worst case
     * Space Complexity: O(1)
     * 
     * Move nodes one by one from end to beginning positions.
     */
    public void reorderListInPlace(ListNode head) {
        if (head == null || head.next == null) return;
        
        int length = getLengthIterative(head);
        ListNode current = head;
        
        for (int i = 0; i < length / 2; i++) {
            // Find the node to move (from position length - 1 - i)
            ListNode prev = current;
            ListNode target = current.next;
            
            // Navigate to the target position
            for (int j = i + 1; j < length - 1 - i; j++) {
                prev = target;
                target = target.next;
            }
            
            if (target == current.next) {
                // Adjacent nodes, just move current
                current = current.next;
                continue;
            }
            
            // Remove target from its current position
            prev.next = target.next;
            
            // Insert target after current
            target.next = current.next;
            current.next = target;
            
            // Move current two positions ahead
            current = target.next;
        }
    }
    
    private int getLengthIterative(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
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
        ReorderList solution = new ReorderList();
        
        // Test case 1: Even length list
        System.out.println("Test Case 1: [1,2,3,4] (even length)");
        ListNode head1 = createList(new int[]{1, 2, 3, 4});
        
        System.out.print("Original: ");
        printList(head1);
        
        ListNode copy1 = copyList(head1);
        solution.reorderListOptimal(copy1);
        System.out.print("Optimal: ");
        printList(copy1);
        
        ListNode copy2 = copyList(head1);
        solution.reorderListArrayList(copy2);
        System.out.print("ArrayList: ");
        printList(copy2);
        
        ListNode copy3 = copyList(head1);
        solution.reorderListStack(copy3);
        System.out.print("Stack: ");
        printList(copy3);
        
        ListNode copy4 = copyList(head1);
        solution.reorderListRecursive(copy4);
        System.out.print("Recursive: ");
        printList(copy4);
        
        ListNode copy5 = copyList(head1);
        solution.reorderListDeque(copy5);
        System.out.print("Deque: ");
        printList(copy5);
        
        ListNode copy6 = copyList(head1);
        solution.reorderListTwoLists(copy6);
        System.out.print("Two Lists: ");
        printList(copy6);
        
        System.out.println();
        
        // Test case 2: Odd length list
        System.out.println("Test Case 2: [1,2,3,4,5] (odd length)");
        ListNode head2 = createList(new int[]{1, 2, 3, 4, 5});
        
        System.out.print("Original: ");
        printList(head2);
        
        ListNode copy2_1 = copyList(head2);
        solution.reorderListOptimal(copy2_1);
        System.out.print("Reordered: ");
        printList(copy2_1);
        System.out.println();
        
        // Test case 3: Single node
        System.out.println("Test Case 3: [1] (single node)");
        ListNode head3 = createList(new int[]{1});
        
        System.out.print("Original: ");
        printList(head3);
        
        solution.reorderListOptimal(head3);
        System.out.print("Reordered: ");
        printList(head3);
        System.out.println();
        
        // Test case 4: Two nodes
        System.out.println("Test Case 4: [1,2] (two nodes)");
        ListNode head4 = createList(new int[]{1, 2});
        
        System.out.print("Original: ");
        printList(head4);
        
        solution.reorderListOptimal(head4);
        System.out.print("Reordered: ");
        printList(head4);
        System.out.println();
        
        // Test case 5: Larger list
        System.out.println("Test Case 5: [1,2,3,4,5,6,7,8] (larger list)");
        ListNode head5 = createList(new int[]{1, 2, 3, 4, 5, 6, 7, 8});
        
        System.out.print("Original: ");
        printList(head5);
        
        solution.reorderListOptimal(head5);
        System.out.print("Reordered: ");
        printList(head5);
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(ReorderList solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large linked list
        int size = 100000;
        int[] largeArray = new int[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = i + 1;
        }
        
        long start, end;
        
        // Test Optimal approach
        ListNode largeList1 = createList(largeArray);
        start = System.nanoTime();
        solution.reorderListOptimal(largeList1);
        end = System.nanoTime();
        System.out.println("Optimal: " + (end - start) / 1000000.0 + " ms");
        
        // Test ArrayList approach
        ListNode largeList2 = createList(largeArray);
        start = System.nanoTime();
        solution.reorderListArrayList(largeList2);
        end = System.nanoTime();
        System.out.println("ArrayList: " + (end - start) / 1000000.0 + " ms");
        
        // Test Stack approach
        ListNode largeList3 = createList(largeArray);
        start = System.nanoTime();
        solution.reorderListStack(largeList3);
        end = System.nanoTime();
        System.out.println("Stack: " + (end - start) / 1000000.0 + " ms");
        
        // Test Two Lists approach
        ListNode largeList4 = createList(largeArray);
        start = System.nanoTime();
        solution.reorderListTwoLists(largeList4);
        end = System.nanoTime();
        System.out.println("Two Lists: " + (end - start) / 1000000.0 + " ms");
        
        // Test Recursive approach (with smaller input to avoid stack overflow)
        ListNode smallList = createList(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        start = System.nanoTime();
        solution.reorderListRecursive(smallList);
        end = System.nanoTime();
        System.out.println("Recursive (small list): " + (end - start) / 1000000.0 + " ms");
    }
} 