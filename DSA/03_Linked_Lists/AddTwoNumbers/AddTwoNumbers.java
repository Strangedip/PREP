import java.util.*;

/**
 * Problem: Add Two Numbers
 * 
 * You are given two non-empty linked lists representing two non-negative integers. 
 * The digits are stored in reverse order, and each of their nodes contains a single digit. 
 * Add the two numbers and return the sum as a linked list.
 * 
 * You may assume the two numbers do not contain any leading zero, except the number 0 itself.
 * 
 * Example:
 * Input: l1 = [2,4,3], l2 = [5,6,4]
 * Output: [7,0,8]
 * Explanation: 342 + 465 = 807.
 * 
 * Example 2:
 * Input: l1 = [0], l2 = [0]
 * Output: [0]
 * 
 * Example 3:
 * Input: l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
 * Output: [8,9,9,9,0,0,0,1]
 * 
 * Constraints:
 * - The number of nodes in each linked list is in the range [1, 100].
 * - 0 <= Node.val <= 9
 * - It is guaranteed that the list represents a number that does not have leading zeros.
 */
public class AddTwoNumbers {
    
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
     * APPROACH 1: ITERATIVE WITH CARRY (Optimal)
     * Time Complexity: O(max(m, n))
     * Space Complexity: O(max(m, n)) - For result list
     * 
     * Simulate manual addition with carry propagation.
     */
    public ListNode addTwoNumbersIterative(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        int carry = 0;
        
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }
            
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            
            carry = sum / 10;
            current.next = new ListNode(sum % 10);
            current = current.next;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 2: RECURSIVE APPROACH
     * Time Complexity: O(max(m, n))
     * Space Complexity: O(max(m, n)) - Due to recursion stack and result
     * 
     * Use recursion to handle carry and addition.
     */
    public ListNode addTwoNumbersRecursive(ListNode l1, ListNode l2) {
        return addHelper(l1, l2, 0);
    }
    
    private ListNode addHelper(ListNode l1, ListNode l2, int carry) {
        if (l1 == null && l2 == null && carry == 0) {
            return null;
        }
        
        int sum = carry;
        if (l1 != null) {
            sum += l1.val;
            l1 = l1.next;
        }
        if (l2 != null) {
            sum += l2.val;
            l2 = l2.next;
        }
        
        ListNode result = new ListNode(sum % 10);
        result.next = addHelper(l1, l2, sum / 10);
        
        return result;
    }
    
    /**
     * APPROACH 3: CONVERT TO NUMBERS THEN ADD (For Small Numbers)
     * Time Complexity: O(m + n)
     * Space Complexity: O(max(m, n))
     * 
     * Convert lists to numbers, add them, convert back to list.
     * Note: This approach has limitations with very large numbers.
     */
    public ListNode addTwoNumbersConvert(ListNode l1, ListNode l2) {
        long num1 = listToNumber(l1);
        long num2 = listToNumber(l2);
        long sum = num1 + num2;
        
        return numberToList(sum);
    }
    
    private long listToNumber(ListNode head) {
        long number = 0;
        long multiplier = 1;
        
        while (head != null) {
            number += head.val * multiplier;
            multiplier *= 10;
            head = head.next;
        }
        
        return number;
    }
    
    private ListNode numberToList(long number) {
        if (number == 0) {
            return new ListNode(0);
        }
        
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        while (number > 0) {
            current.next = new ListNode((int)(number % 10));
            current = current.next;
            number /= 10;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 4: USING STACKS (Alternative for Forward Order)
     * Time Complexity: O(m + n)
     * Space Complexity: O(m + n)
     * 
     * Use stacks to handle addition (useful if digits were in forward order).
     * For this problem, we adapt it to work with reverse order.
     */
    public ListNode addTwoNumbersStack(ListNode l1, ListNode l2) {
        Stack<Integer> stack1 = new Stack<>();
        Stack<Integer> stack2 = new Stack<>();
        
        // Push all digits to stacks (reverse order becomes forward)
        while (l1 != null) {
            stack1.push(l1.val);
            l1 = l1.next;
        }
        
        while (l2 != null) {
            stack2.push(l2.val);
            l2 = l2.next;
        }
        
        // Add from most significant digit
        Stack<Integer> resultStack = new Stack<>();
        int carry = 0;
        
        while (!stack1.isEmpty() || !stack2.isEmpty() || carry != 0) {
            int sum = carry;
            
            if (!stack1.isEmpty()) {
                sum += stack1.pop();
            }
            
            if (!stack2.isEmpty()) {
                sum += stack2.pop();
            }
            
            resultStack.push(sum % 10);
            carry = sum / 10;
        }
        
        // Build result list from stack
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        while (!resultStack.isEmpty()) {
            current.next = new ListNode(resultStack.pop());
            current = current.next;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 5: USING ARRAYLIST
     * Time Complexity: O(m + n)
     * Space Complexity: O(max(m, n))
     * 
     * Convert to arrays, perform addition, convert back.
     */
    public ListNode addTwoNumbersArrayList(ListNode l1, ListNode l2) {
        List<Integer> digits1 = listToArray(l1);
        List<Integer> digits2 = listToArray(l2);
        
        List<Integer> result = new ArrayList<>();
        int carry = 0;
        int i = 0;
        
        while (i < digits1.size() || i < digits2.size() || carry != 0) {
            int sum = carry;
            
            if (i < digits1.size()) {
                sum += digits1.get(i);
            }
            
            if (i < digits2.size()) {
                sum += digits2.get(i);
            }
            
            result.add(sum % 10);
            carry = sum / 10;
            i++;
        }
        
        return arrayToList(result);
    }
    
    private List<Integer> listToArray(ListNode head) {
        List<Integer> digits = new ArrayList<>();
        while (head != null) {
            digits.add(head.val);
            head = head.next;
        }
        return digits;
    }
    
    private ListNode arrayToList(List<Integer> digits) {
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        for (int digit : digits) {
            current.next = new ListNode(digit);
            current = current.next;
        }
        
        return dummy.next;
    }
    
    /**
     * APPROACH 6: IN-PLACE MODIFICATION (Modifies l1)
     * Time Complexity: O(max(m, n))
     * Space Complexity: O(1) - Only for carry, reuses l1
     * 
     * Modify l1 in place to store the result.
     * Note: This destroys the original l1.
     */
    public ListNode addTwoNumbersInPlace(ListNode l1, ListNode l2) {
        if (l1 == null) return l2;
        if (l2 == null) return l1;
        
        ListNode result = l1;
        ListNode prev = null;
        int carry = 0;
        
        while (l1 != null || l2 != null || carry != 0) {
            int sum = carry;
            
            if (l1 != null) {
                sum += l1.val;
            } else {
                // Extend l1 if needed
                prev.next = new ListNode(0);
                l1 = prev.next;
            }
            
            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }
            
            l1.val = sum % 10;
            carry = sum / 10;
            prev = l1;
            l1 = l1.next;
        }
        
        return result;
    }
    
    /**
     * APPROACH 7: PADDING WITH ZEROS
     * Time Complexity: O(max(m, n))
     * Space Complexity: O(max(m, n))
     * 
     * Pad shorter list with zeros to make them same length.
     */
    public ListNode addTwoNumbersPadding(ListNode l1, ListNode l2) {
        // Make copies to avoid modifying originals
        ListNode list1 = copyList(l1);
        ListNode list2 = copyList(l2);
        
        // Pad to same length
        int len1 = getLength(list1);
        int len2 = getLength(list2);
        
        if (len1 < len2) {
            list1 = padWithZeros(list1, len2 - len1);
        } else if (len2 < len1) {
            list2 = padWithZeros(list2, len1 - len2);
        }
        
        // Add with same length
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        int carry = 0;
        
        while (list1 != null && list2 != null) {
            int sum = list1.val + list2.val + carry;
            current.next = new ListNode(sum % 10);
            carry = sum / 10;
            
            current = current.next;
            list1 = list1.next;
            list2 = list2.next;
        }
        
        if (carry != 0) {
            current.next = new ListNode(carry);
        }
        
        return dummy.next;
    }
    
    private int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }
    
    private ListNode padWithZeros(ListNode head, int count) {
        ListNode current = head;
        while (current.next != null) {
            current = current.next;
        }
        
        for (int i = 0; i < count; i++) {
            current.next = new ListNode(0);
            current = current.next;
        }
        
        return head;
    }
    
    private ListNode copyList(ListNode head) {
        if (head == null) return null;
        
        ListNode dummy = new ListNode(0);
        ListNode current = dummy;
        
        while (head != null) {
            current.next = new ListNode(head.val);
            current = current.next;
            head = head.next;
        }
        
        return dummy.next;
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
     * Convert list to number for verification
     */
    public static long listToNumberForDisplay(ListNode head) {
        long number = 0;
        long multiplier = 1;
        
        while (head != null) {
            number += head.val * multiplier;
            multiplier *= 10;
            head = head.next;
        }
        
        return number;
    }
    
    // Test method to demonstrate all approaches
    public static void main(String[] args) {
        AddTwoNumbers solution = new AddTwoNumbers();
        
        // Test case 1: Normal addition
        System.out.println("Test Case 1: [2,4,3] + [5,6,4] = 342 + 465");
        ListNode l1 = createList(new int[]{2, 4, 3});
        ListNode l2 = createList(new int[]{5, 6, 4});
        
        System.out.print("L1: ");
        printList(l1);
        System.out.println("L1 as number: " + listToNumberForDisplay(l1));
        
        System.out.print("L2: ");
        printList(l2);
        System.out.println("L2 as number: " + listToNumberForDisplay(l2));
        
        ListNode result1 = solution.addTwoNumbersIterative(l1, l2);
        System.out.print("Iterative result: ");
        printList(result1);
        System.out.println("Result as number: " + listToNumberForDisplay(result1));
        
        ListNode result2 = solution.addTwoNumbersRecursive(l1, l2);
        System.out.print("Recursive result: ");
        printList(result2);
        
        ListNode result3 = solution.addTwoNumbersConvert(l1, l2);
        System.out.print("Convert result: ");
        printList(result3);
        
        ListNode result4 = solution.addTwoNumbersStack(l1, l2);
        System.out.print("Stack result: ");
        printList(result4);
        
        ListNode result5 = solution.addTwoNumbersArrayList(l1, l2);
        System.out.print("ArrayList result: ");
        printList(result5);
        
        System.out.println();
        
        // Test case 2: Different lengths with carry
        System.out.println("Test Case 2: [9,9,9,9,9,9,9] + [9,9,9,9]");
        ListNode l3 = createList(new int[]{9, 9, 9, 9, 9, 9, 9});
        ListNode l4 = createList(new int[]{9, 9, 9, 9});
        
        System.out.print("L3: ");
        printList(l3);
        System.out.print("L4: ");
        printList(l4);
        
        ListNode result2_1 = solution.addTwoNumbersIterative(l3, l4);
        System.out.print("Result: ");
        printList(result2_1);
        System.out.println();
        
        // Test case 3: Zero cases
        System.out.println("Test Case 3: [0] + [0]");
        ListNode l5 = createList(new int[]{0});
        ListNode l6 = createList(new int[]{0});
        
        ListNode result3_1 = solution.addTwoNumbersIterative(l5, l6);
        System.out.print("Result: ");
        printList(result3_1);
        System.out.println();
        
        // Test case 4: Single digits
        System.out.println("Test Case 4: [5] + [5]");
        ListNode l7 = createList(new int[]{5});
        ListNode l8 = createList(new int[]{5});
        
        ListNode result4_1 = solution.addTwoNumbersIterative(l7, l8);
        System.out.print("Result: ");
        printList(result4_1);
        System.out.println();
        
        // Test case 5: One number much larger
        System.out.println("Test Case 5: [1,8] + [0] = 81 + 0");
        ListNode l9 = createList(new int[]{1, 8});
        ListNode l10 = createList(new int[]{0});
        
        ListNode result5_1 = solution.addTwoNumbersIterative(l9, l10);
        System.out.print("Result: ");
        printList(result5_1);
        System.out.println("Expected: 81");
        System.out.println();
        
        // Performance test
        performanceTest(solution);
    }
    
    private static void performanceTest(AddTwoNumbers solution) {
        System.out.println("=== Performance Test ===");
        
        // Create large numbers
        int size = 100;
        int[] largeArray1 = new int[size];
        int[] largeArray2 = new int[size];
        
        for (int i = 0; i < size; i++) {
            largeArray1[i] = 9;
            largeArray2[i] = 9;
        }
        
        ListNode largeList1 = createList(largeArray1);
        ListNode largeList2 = createList(largeArray2);
        
        long start, end;
        
        // Test Iterative approach (optimal)
        start = System.nanoTime();
        ListNode result1 = solution.addTwoNumbersIterative(largeList1, largeList2);
        end = System.nanoTime();
        System.out.println("Iterative: " + (end - start) / 1000000.0 + " ms");
        
        // Test ArrayList approach
        start = System.nanoTime();
        ListNode result2 = solution.addTwoNumbersArrayList(largeList1, largeList2);
        end = System.nanoTime();
        System.out.println("ArrayList: " + (end - start) / 1000000.0 + " ms");
        
        // Test Stack approach
        start = System.nanoTime();
        ListNode result3 = solution.addTwoNumbersStack(largeList1, largeList2);
        end = System.nanoTime();
        System.out.println("Stack: " + (end - start) / 1000000.0 + " ms");
        
        // Test Recursive approach (with smaller input to avoid stack overflow)
        ListNode smallList1 = createList(new int[]{9, 9, 9, 9, 9});
        ListNode smallList2 = createList(new int[]{9, 9, 9, 9, 9});
        
        start = System.nanoTime();
        ListNode result4 = solution.addTwoNumbersRecursive(smallList1, smallList2);
        end = System.nanoTime();
        System.out.println("Recursive (small input): " + (end - start) / 1000000.0 + " ms");
    }
} 