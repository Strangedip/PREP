import java.util.*;

/**
 * Problem: Merge Two Sorted Lists
 * 
 * You are given the heads of two sorted linked lists list1 and list2.
 * Merge the two lists in a sorted manner and return the head of the merged linked list.
 * 
 * The list should be made by splicing together the nodes of the first two lists.
 * 
 * Example:
 * Input: list1 = [1,2,4], list2 = [1,3,4]
 * Output: [1,1,2,3,4,4]
 * 
 * Example 2:
 * Input: list1 = [], list2 = []
 * Output: []
 * 
 * Example 3:
 * Input: list1 = [], list2 = [0]
 * Output: [0]
 * 
 * Constraints:
 * - The number of nodes in both lists is in the range [0, 50].
 * - -100 <= Node.val <= 100
 * - Both list1 and list2 are sorted in non-decreasing order.
 */
public class MergeTwoSortedLists {
    
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
     * APPROACH 1: ITERATIVE WITH DUMMY NODE (Optimal)
     * Time Complexity: O(m + n)
     * Space Complexity: O(1)
     * 
     * Use a dummy node to simplify the merging process.
     */
    public ListNode mergeTwoListsIterative(ListNode list1, ListNode list2) {
        // Create dummy node to simplify logic
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        // Compare nodes and link the smaller one
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }
        
        // Attach remaining nodes
        if (list1 != null) {
            current.next = list1;
        } else {
            current.next = list2;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 2: RECURSIVE APPROACH
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n) - Due to recursion stack
     * 
     * Recursively merge by choosing the smaller head each time.
     */
    public ListNode mergeTwoListsRecursive(ListNode list1, ListNode list2) {
        // Base cases
        if (list1 == null) return list2;
        if (list2 == null) return list1;
        
        // Recursive case: choose smaller head and recurse
        if (list1.val <= list2.val) {
            list1.next = mergeTwoListsRecursive(list1.next, list2);
            return list1;
        } else {
            list2.next = mergeTwoListsRecursive(list1, list2.next);
            return list2;
        }
    }
    
    /**
     * APPROACH 3: ITERATIVE WITHOUT DUMMY NODE
     * Time Complexity: O(m + n)
     * Space Complexity: O(1)
     * 
     * Handle the head separately without using a dummy node.
     */
    public ListNode mergeTwoListsNoDummy(ListNode list1, ListNode list2) {
        if (list1 == null) return list2;
        if (list2 == null) return list1;
        
        ListNode head, current;
        
        // Determine the head
        if (list1.val <= list2.val) {
            head = current = list1;
            list1 = list1.next;
        } else {
            head = current = list2;
            list2 = list2.next;
        }
        
        // Merge the rest
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = list1;
                list1 = list1.next;
            } else {
                current.next = list2;
                list2 = list2.next;
            }
            current = current.next;
        }
        
        // Attach remaining nodes
        current.next = (list1 != null) ? list1 : list2;
        
        return head;
    }
    
    /**
     * APPROACH 4: USING PRIORITY QUEUE
     * Time Complexity: O((m + n) log 2) = O(m + n)
     * Space Complexity: O(1) - Only 2 elements in queue at most
     * 
     * Use priority queue to always get the smaller element.
     */
    public ListNode mergeTwoListsPriorityQueue(ListNode list1, ListNode list2) {
        if (list1 == null) return list2;
        if (list2 == null) return list1;
        
        // Priority queue to store nodes by value
        PriorityQueue<ListNode> pq = new PriorityQueue<>((a, b) -> a.val - b.val);
        
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        // Add initial nodes
        if (list1 != null) pq.offer(list1);
        if (list2 != null) pq.offer(list2);
        
        while (!pq.isEmpty()) {
            ListNode node = pq.poll();
            current.next = node;
            current = current.next;
            
            // Add next node from the same list
            if (node.next != null) {
                pq.offer(node.next);
            }
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 5: MERGE WITH NEW NODES
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     * 
     * Create new nodes instead of reusing existing ones.
     */
    public ListNode mergeTwoListsNewNodes(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        while (list1 != null && list2 != null) {
            if (list1.val <= list2.val) {
                current.next = new ListNode(list1.val);
                list1 = list1.next;
            } else {
                current.next = new ListNode(list2.val);
                list2 = list2.next;
            }
            current = current.next;
        }
        
        // Copy remaining nodes
        while (list1 != null) {
            current.next = new ListNode(list1.val);
            current = current.next;
            list1 = list1.next;
        }
        
        while (list2 != null) {
            current.next = new ListNode(list2.val);
            current = current.next;
            list2 = list2.next;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 6: STACK-BASED APPROACH
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     * 
     * Use two stacks to reverse and merge from the end.
     */
    public ListNode mergeTwoListsStack(ListNode list1, ListNode list2) {
        if (list1 == null) return copyList(list2);
        if (list2 == null) return copyList(list1);
        
        Stack<Integer> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();
        
        // Push all values to stacks
        ListNode current = list1;
        while (current != null) {
            stack1.push(current.val);
            current = current.next;
        }
        
        current = list2;
        while (current != null) {
            stack2.push(current.val);
            current = current.next;
        }
        
        // Merge from the largest values
        Stack<Integer> result = new Stack<>();
        
        while (!stack1.isEmpty() && !stack2.isEmpty()) {
            if (stack1.peek() <= stack2.peek()) {
                result.push(stack2.pop());
            } else {
                result.push(stack1.pop());
            }
        }
        
        while (!stack1.isEmpty()) {
            result.push(stack1.pop());
        }
        
        while (!stack2.isEmpty()) {
            result.push(stack2.pop());
        }
        
        // Build result list
        if (result.isEmpty()) return null;
        
        ListNode dummy = new ListNode(0);
        current = dummy;
        
        while (!result.isEmpty()) {
            current.next = new ListNode(result.pop());
            current = current.next;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 7: TAIL RECURSION STYLE
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     * 
     * Use helper function with accumulator for tail recursion style.
     */
    public ListNode mergeTwoListsTailRecursion(ListNode list1, ListNode list2) {
        ListNode dummy = new ListNode(0);
        mergeHelper(list1, list2, dummy);
        return dummy.next;
    }
    
    private void mergeHelper(ListNode list1, ListNode list2, ListNode current) {
        if (list1 == null && list2 == null) {
            return;
        }
        
        if (list1 == null) {
            current.next = list2;
            return;
        }
        
        if (list2 == null) {
            current.next = list1;
            return;
        }
        
        if (list1.val <= list2.val) {
            current.next = list1;
            mergeHelper(list1.next, list2, current.next);
        } else {
            current.next = list2;
            mergeHelper(list1, list2.next, current.next);
        }
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
        MergeTwoSortedLists solution = new MergeTwoSortedLists();
        
        // Test case 1: Normal case
        System.out.println("Test Case 1: [1,2,4] + [1,3,4]");
        ListNode list1 = createList(new int[]{1, 2, 4});
        ListNode list2 = createList(new int[]{1, 3, 4});
        
        System.out.print("List 1: ");
        printList(list1);
        System.out.print("List 2: ");
        printList(list2);
        
        ListNode result1 = solution.mergeTwoListsIterative(copyList(list1), copyList(list2));
        System.out.print("Iterative: ");
        printList(result1);
        
        ListNode result2 = solution.mergeTwoListsRecursive(copyList(list1), copyList(list2));
        System.out.print("Recursive: ");
        printList(result2);
        
        ListNode result3 = solution.mergeTwoListsNoDummy(copyList(list1), copyList(list2));
        System.out.print("No Dummy: ");
        printList(result3);
        
        ListNode result4 = solution.mergeTwoListsPriorityQueue(copyList(list1), copyList(list2));
        System.out.print("Priority Queue: ");
        printList(result4);
        
        ListNode result5 = solution.mergeTwoListsNewNodes(copyList(list1), copyList(list2));
        System.out.print("New Nodes: ");
        printList(result5);
        
        ListNode result6 = solution.mergeTwoListsStack(copyList(list1), copyList(list2));
        System.out.print("Stack: ");
        printList(result6);
        
        ListNode result7 = solution.mergeTwoListsTailRecursion(copyList(list1), copyList(list2));
        System.out.print("Tail Recursion: ");
        printList(result7);
        
        System.out.println();
        
        // Test case 2: Empty lists
        System.out.println("Test Case 2: [] + []");
        ListNode empty1 = null;
        ListNode empty2 = null;
        
        ListNode result2_1 = solution.mergeTwoListsIterative(empty1, empty2);
        System.out.print("Result: ");
        printList(result2_1);
        System.out.println();
        
        // Test case 3: One empty list
        System.out.println("Test Case 3: [] + [0]");
        ListNode empty = null;
        ListNode single = createList(new int[]{0});
        
        ListNode result3_1 = solution.mergeTwoListsIterative(empty, copyList(single));
        System.out.print("Result: ");
        printList(result3_1);
        System.out.println();
        
        // Test case 4: Different lengths
        System.out.println("Test Case 4: [1,3,5,7,9] + [2,4,6]");
        ListNode long1 = createList(new int[]{1, 3, 5, 7, 9});
        ListNode short1 = createList(new int[]{2, 4, 6});
        
        System.out.print("Long list: ");
        printList(long1);
        System.out.print("Short list: ");
        printList(short1);
        
        ListNode result4_1 = solution.mergeTwoListsIterative(copyList(long1), copyList(short1));
        System.out.print("Merged: ");
        printList(result4_1);
        System.out.println();
        
        // Test case 5: No overlap
        System.out.println("Test Case 5: [1,2,3] + [4,5,6]");
        ListNode lower = createList(new int[]{1, 2, 3});
        ListNode higher = createList(new int[]{4, 5, 6});
        
        ListNode result5_1 = solution.mergeTwoListsIterative(copyList(lower), copyList(higher));
        System.out.print("Merged: ");
        printList(result5_1);
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(MergeTwoSortedLists solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large sorted lists
        int size = 50000;
        int[] array1 = new int[size];
        int[] array2 = new int[size];
        
        for (int i = 0; i < size; i++) {
            array1[i] = i * 2;      // Even numbers
            array2[i] = i * 2 + 1;  // Odd numbers
        }
        
        ListNode largeList1 = createList(array1);
        ListNode largeList2 = createList(array2);
        
        long start, end;
        
        // Test Iterative approach
        start = System.nanoTime();
        ListNode result1 = solution.mergeTwoListsIterative(copyList(largeList1), copyList(largeList2));
        end = System.nanoTime();
        System.out.println("Iterative: " + (end - start) / 1000000.0 + " ms");
        
        // Test No Dummy approach
        start = System.nanoTime();
        ListNode result2 = solution.mergeTwoListsNoDummy(copyList(largeList1), copyList(largeList2));
        end = System.nanoTime();
        System.out.println("No Dummy: " + (end - start) / 1000000.0 + " ms");
        
        // Test Priority Queue approach
        start = System.nanoTime();
        ListNode result3 = solution.mergeTwoListsPriorityQueue(copyList(largeList1), copyList(largeList2));
        end = System.nanoTime();
        System.out.println("Priority Queue: " + (end - start) / 1000000.0 + " ms");
        
        // Test New Nodes approach
        start = System.nanoTime();
        ListNode result4 = solution.mergeTwoListsNewNodes(copyList(largeList1), copyList(largeList2));
        end = System.nanoTime();
        System.out.println("New Nodes: " + (end - start) / 1000000.0 + " ms");
        
        // Test Recursive approach (with smaller lists to avoid stack overflow)
        ListNode smallList1 = createList(new int[]{1, 3, 5, 7, 9});
        ListNode smallList2 = createList(new int[]{2, 4, 6, 8, 10});
        start = System.nanoTime();
        ListNode result5 = solution.mergeTwoListsRecursive(smallList1, smallList2);
        end = System.nanoTime();
        System.out.println("Recursive (small lists): " + (end - start) / 1000000.0 + " ms");
    }
} 