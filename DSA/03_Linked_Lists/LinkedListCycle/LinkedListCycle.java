import java.util.*;

/**
 * Problem: Linked List Cycle Detection
 * 
 * Given head, the head of a linked list, determine if the linked list has a cycle in it.
 * There is a cycle in a linked list if there is some node in the list that can be reached 
 * again by continuously following the next pointer. Internally, pos is used to denote 
 * the index of the node that tail's next pointer is connected to. Note that pos is not 
 * passed as a parameter.
 * 
 * Return true if there is a cycle in the linked list. Otherwise, return false.
 * 
 * Example:
 * Input: head = [3,2,0,-4], pos = 1
 * Output: true
 * Explanation: There is a cycle in the linked list, where the tail connects to the 1st node (0-indexed).
 * 
 * Example 2:
 * Input: head = [1,2], pos = 0
 * Output: true
 * Explanation: There is a cycle in the linked list, where the tail connects to the 0th node.
 * 
 * Example 3:
 * Input: head = [1], pos = -1
 * Output: false
 * Explanation: There is no cycle in the linked list.
 * 
 * Constraints:
 * - The number of the nodes in the list is in the range [0, 10^4].
 * - -10^5 <= Node.val <= 10^5
 * - pos is -1 or a valid index in the linked-list.
 * 
 * Follow up: Can you solve it using O(1) (i.e. constant) memory?
 */
public class LinkedListCycle {
    
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
     * APPROACH 1: FLOYD'S CYCLE DETECTION (Two Pointers - Optimal)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Use slow and fast pointers. If there's a cycle, they will meet.
     */
    public boolean hasCycleFloyd(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        ListNode slow = head;
        ListNode fast = head.next;
        
        while (slow != fast) {
            // If fast reaches end, no cycle
            if (fast == null || fast.next == null) {
                return false;
            }
            
            slow = slow.next;        // Move one step
            fast = fast.next.next;   // Move two steps
        }
        
        return true; // Pointers met, cycle detected
    }
    
    /**
     * APPROACH 2: FLOYD'S CYCLE DETECTION (Alternative Implementation)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Both pointers start from head, but fast moves two steps.
     */
    public boolean hasCycleFloydAlt(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        ListNode slow = head;
        ListNode fast = head;
        
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            
            if (slow == fast) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * APPROACH 3: USING HASHSET
     * Time Complexity: O(n)
     * Space Complexity: O(n)
     * 
     * Store visited nodes in HashSet. If we visit a node twice, there's a cycle.
     */
    public boolean hasCycleHashSet(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        Set<ListNode> visited = new HashSet<>();
        ListNode current = head;
        
        while (current != null) {
            if (visited.contains(current)) {
                return true; // Found cycle
            }
            visited.add(current);
            current = current.next;
        }
        
        return false; // Reached end without cycle
    }
    
    /**
     * APPROACH 4: MODIFY NODES (Destructive Method)
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Mark visited nodes by changing their values to a special marker.
     * Note: This modifies the original list.
     */
    public boolean hasCycleDestructive(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        final int VISITED_MARKER = Integer.MIN_VALUE;
        ListNode current = head;
        
        while (current != null) {
            if (current.val == VISITED_MARKER) {
                return true; // Found a marked node, cycle detected
            }
            
            current.val = VISITED_MARKER; // Mark as visited
            current = current.next;
        }
        
        return false;
    }
    
    /**
     * APPROACH 5: CYCLE DETECTION WITH CYCLE START FINDING
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Not only detect cycle but also find where the cycle starts.
     */
    public ListNode detectCycleStart(ListNode head) {
        if (head == null || head.next == null) {
            return null;
        }
        
        // Phase 1: Detect if cycle exists
        ListNode slow = head;
        ListNode fast = head;
        
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
            
            if (slow == fast) {
                break; // Cycle detected
            }
        }
        
        // No cycle found
        if (fast == null || fast.next == null) {
            return null;
        }
        
        // Phase 2: Find cycle start
        slow = head;
        while (slow != fast) {
            slow = slow.next;
            fast = fast.next;
        }
        
        return slow; // This is the start of the cycle
    }
    
    public boolean hasCycleWithStart(ListNode head) {
        return detectCycleStart(head) != null;
    }
    
    /**
     * APPROACH 6: USING RECURSION WITH VISITED SET
     * Time Complexity: O(n)
     * Space Complexity: O(n) - Due to recursion stack and HashSet
     * 
     * Recursive approach with memoization.
     */
    public boolean hasCycleRecursive(ListNode head) {
        if (head == null) {
            return false;
        }
        
        Set<ListNode> visited = new HashSet<>();
        return hasCycleHelper(head, visited);
    }
    
    private boolean hasCycleHelper(ListNode node, Set<ListNode> visited) {
        if (node == null) {
            return false;
        }
        
        if (visited.contains(node)) {
            return true;
        }
        
        visited.add(node);
        return hasCycleHelper(node.next, visited);
    }
    
    /**
     * APPROACH 7: STEP COUNTING METHOD
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Count steps and check if we exceed expected maximum.
     */
    public boolean hasCycleStepCount(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        ListNode current = head;
        int steps = 0;
        final int MAX_STEPS = 10001; // Based on constraint: max 10^4 nodes
        
        while (current != null && steps < MAX_STEPS) {
            current = current.next;
            steps++;
        }
        
        return current != null; // If we didn't reach null, there's a cycle
    }
    
    /**
     * APPROACH 8: BRENT'S CYCLE DETECTION
     * Time Complexity: O(n)
     * Space Complexity: O(1)
     * 
     * Alternative to Floyd's algorithm, sometimes faster in practice.
     */
    public boolean hasCycleBrent(ListNode head) {
        if (head == null || head.next == null) {
            return false;
        }
        
        ListNode tortoise = head;
        ListNode hare = head.next;
        int power = 1;
        int lambda = 1;
        
        while (tortoise != hare) {
            if (power == lambda) {
                tortoise = hare;
                power *= 2;
                lambda = 0;
            }
            
            if (hare == null) {
                return false;
            }
            
            hare = hare.next;
            lambda++;
        }
        
        return true;
    }
    
    // Helper methods for testing
    
    /**
     * Create linked list with cycle for testing
     */
    public static ListNode createListWithCycle(int[] values, int cyclePos) {
        if (values == null || values.length == 0) {
            return null;
        }
        
        // Create nodes
        ListNode[] nodes = new ListNode[values.length];
        for (int i = 0; i < values.length; i++) {
            nodes[i] = new ListNode(values[i]);
        }
        
        // Link nodes
        for (int i = 0; i < values.length - 1; i++) {
            nodes[i].next = nodes[i + 1];
        }
        
        // Create cycle if cyclePos is valid
        if (cyclePos >= 0 && cyclePos < values.length) {
            nodes[values.length - 1].next = nodes[cyclePos];
        }
        
        return nodes[0];
    }
    
    /**
     * Create regular linked list without cycle
     */
    public static ListNode createList(int[] values) {
        return createListWithCycle(values, -1);
    }
    
    /**
     * Print linked list (safe for cycles)
     */
    public static void printListSafe(ListNode head, int maxNodes) {
        if (head == null) {
            System.out.println("[]");
            return;
        }
        
        System.out.print("[");
        ListNode current = head;
        int count = 0;
        
        while (current != null && count < maxNodes) {
            System.out.print(current.val);
            if (current.next != null && count < maxNodes - 1) {
                System.out.print(",");
            }
            current = current.next;
            count++;
        }
        
        if (count == maxNodes && current != null) {
            System.out.print("...(cycle detected)");
        }
        
        System.out.println("]");
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        LinkedListCycle solution = new LinkedListCycle();
        
        // Test case 1: List with cycle
        System.out.println("Test Case 1: [3,2,0,-4] with cycle at position 1");
        ListNode head1 = createListWithCycle(new int[]{3, 2, 0, -4}, 1);
        
        System.out.print("List (first 10 nodes): ");
        printListSafe(head1, 10);
        
        System.out.println("Floyd's (v1): " + solution.hasCycleFloyd(head1));
        System.out.println("Floyd's (v2): " + solution.hasCycleFloydAlt(head1));
        System.out.println("HashSet: " + solution.hasCycleHashSet(head1));
        System.out.println("Step Count: " + solution.hasCycleStepCount(head1));
        System.out.println("Recursive: " + solution.hasCycleRecursive(head1));
        System.out.println("Brent's: " + solution.hasCycleBrent(head1));
        System.out.println("With Start Detection: " + solution.hasCycleWithStart(head1));
        
        // Find cycle start
        ListNode cycleStart = solution.detectCycleStart(head1);
        if (cycleStart != null) {
            System.out.println("Cycle starts at node with value: " + cycleStart.val);
        }
        
        System.out.println();
        
        // Test case 2: List without cycle
        System.out.println("Test Case 2: [1,2,3,4,5] without cycle");
        ListNode head2 = createList(new int[]{1, 2, 3, 4, 5});
        
        System.out.print("List: ");
        printListSafe(head2, 10);
        
        System.out.println("Floyd's: " + solution.hasCycleFloyd(head2));
        System.out.println("HashSet: " + solution.hasCycleHashSet(head2));
        System.out.println();
        
        // Test case 3: Single node with self-cycle
        System.out.println("Test Case 3: [1] with self-cycle");
        ListNode head3 = createListWithCycle(new int[]{1}, 0);
        
        System.out.println("Floyd's: " + solution.hasCycleFloyd(head3));
        System.out.println("HashSet: " + solution.hasCycleHashSet(head3));
        System.out.println();
        
        // Test case 4: Empty list
        System.out.println("Test Case 4: Empty list");
        ListNode head4 = null;
        
        System.out.println("Floyd's: " + solution.hasCycleFloyd(head4));
        System.out.println("HashSet: " + solution.hasCycleHashSet(head4));
        System.out.println();
        
        // Test case 5: Two nodes with cycle
        System.out.println("Test Case 5: [1,2] with cycle at position 0");
        ListNode head5 = createListWithCycle(new int[]{1, 2}, 0);
        
        System.out.println("Floyd's: " + solution.hasCycleFloyd(head5));
        System.out.println("HashSet: " + solution.hasCycleHashSet(head5));
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(LinkedListCycle solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large list with cycle
        int size = 100000;
        int[] largeArray = new int[size];
        for (int i = 0; i < size; i++) {
            largeArray[i] = i;
        }
        
        // Test with cycle
        ListNode largeListWithCycle = createListWithCycle(largeArray, size / 2);
        
        long start, end;
        
        // Test Floyd's algorithm
        start = System.nanoTime();
        boolean result1 = solution.hasCycleFloyd(largeListWithCycle);
        end = System.nanoTime();
        System.out.println("Floyd's (with cycle): " + result1 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test Brent's algorithm
        start = System.nanoTime();
        boolean result2 = solution.hasCycleBrent(largeListWithCycle);
        end = System.nanoTime();
        System.out.println("Brent's (with cycle): " + result2 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test HashSet approach
        start = System.nanoTime();
        boolean result3 = solution.hasCycleHashSet(largeListWithCycle);
        end = System.nanoTime();
        System.out.println("HashSet (with cycle): " + result3 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        // Test with no cycle
        ListNode largeListNoCycle = createList(largeArray);
        
        start = System.nanoTime();
        boolean result4 = solution.hasCycleFloyd(largeListNoCycle);
        end = System.nanoTime();
        System.out.println("Floyd's (no cycle): " + result4 + " (Time: " + (end - start) / 1000000.0 + " ms)");
        
        start = System.nanoTime();
        boolean result5 = solution.hasCycleHashSet(largeListNoCycle);
        end = System.nanoTime();
        System.out.println("HashSet (no cycle): " + result5 + " (Time: " + (end - start) / 1000000.0 + " ms)");
    }
} 