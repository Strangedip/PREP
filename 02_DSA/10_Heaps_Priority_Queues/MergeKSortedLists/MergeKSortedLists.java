import java.util.*;

/**
 * LeetCode 23: Merge k Sorted Lists
 * 
 * You are given an array of k linked-lists lists, each sorted in ascending order.
 * Merge all the linked-lists into one sorted linked-list and return it.
 * 
 * Time Complexity: O(N log k) where N is total number of nodes, k is number of lists
 * Space Complexity: O(k) for the heap
 */
public class MergeKSortedLists {
    
    static class ListNode {
        int val;
        ListNode next;
        
        ListNode() {}
        ListNode(int val) { this.val = val; }
        ListNode(int val, ListNode next) { this.val = val; this.next = next; }
    }
    
    /**
     * Approach 1: Using Min Heap (Priority Queue)
     * Put all list heads in a min heap, then keep extracting minimum and adding next nodes
     */
    public ListNode mergeKLists(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        
        // Min heap to store nodes based on their values
        PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> a.val - b.val);
        
        // Add all non-null list heads to heap
        for (ListNode list : lists) {
            if (list != null) {
                minHeap.offer(list);
            }
        }
        
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        // Extract minimum node and add its next to heap
        while (!minHeap.isEmpty()) {
            ListNode minNode = minHeap.poll();
            current.next = minNode;
            current = current.next;
            
            // Add next node from the same list if exists
            if (minNode.next != null) {
                minHeap.offer(minNode.next);
            }
        }
        
        return dummy.next;
    }
    
    /**
     * Approach 2: Divide and Conquer (More efficient for large k)
     * Merge lists in pairs repeatedly until only one list remains
     */
    public ListNode mergeKListsDivideConquer(ListNode[] lists) {
        if (lists == null || lists.length == 0) {
            return null;
        }
        
        while (lists.length > 1) {
            List<ListNode> mergedLists = new ArrayList<>();
            
            // Merge lists in pairs
            for (int i = 0; i < lists.length; i += 2) {
                ListNode l1 = lists[i];
                ListNode l2 = (i + 1 < lists.length) ? lists[i + 1] : null;
                mergedLists.add(mergeTwoLists(l1, l2));
            }
            
            lists = mergedLists.toArray(new ListNode[0]);
        }
        
        return lists[0];
    }
    
    private ListNode mergeTwoLists(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        while (l1 != null && l2 != null) {
            if (l1.val <= l2.val) {
                current.next = l1;
                l1 = l1.next;
            } else {
                current.next = l2;
                l2 = l2.next;
            }
            current = current.next;
        }
        
        // Attach remaining nodes
        current.next = (l1 != null) ? l1 : l2;
        
        return dummy.next;
    }
    
    // Helper method to create a linked list from array
    public static ListNode createList(int[] arr) {
        if (arr.length == 0) return null;
        
        ListNode head = new ListNode(arr[0]);
        ListNode current = head;
        
        for (int i = 1; i < arr.length; i++) {
            current.next = new ListNode(arr[i]);
            current = current.next;
        }
        
        return head;
    }
    
    // Helper method to print a linked list
    public static void printList(ListNode head) {
        List<Integer> result = new ArrayList<>();
        while (head != null) {
            result.add(head.val);
            head = head.next;
        }
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        MergeKSortedLists solution = new MergeKSortedLists();
        
        // Test case 1: [[1,4,5],[1,3,4],[2,6]]
        ListNode[] lists1 = {
            createList(new int[]{1, 4, 5}),
            createList(new int[]{1, 3, 4}),
            createList(new int[]{2, 6})
        };
        
        System.out.println("Test case 1 - Heap approach:");
        ListNode result1 = solution.mergeKLists(lists1);
        printList(result1); // [1,1,2,3,4,4,5,6]
        
        // Test case 2: Divide and Conquer
        ListNode[] lists2 = {
            createList(new int[]{1, 4, 5}),
            createList(new int[]{1, 3, 4}),
            createList(new int[]{2, 6})
        };
        
        System.out.println("Test case 2 - Divide and Conquer:");
        ListNode result2 = solution.mergeKListsDivideConquer(lists2);
        printList(result2); // [1,1,2,3,4,4,5,6]
        
        // Test case 3: Empty lists
        ListNode[] lists3 = {};
        ListNode result3 = solution.mergeKLists(lists3);
        System.out.println("Empty lists result: " + result3); // null
    }
} 