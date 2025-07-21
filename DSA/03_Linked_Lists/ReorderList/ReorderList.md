# Reorder List

## Problem Statement
Given a singly linked list, reorder it to: L0 → L1 → … → Ln-1 → Ln becomes L0 → Ln → L1 → Ln-1 → L2 → Ln-2 → …

## Example
```
Input: head = [1,2,3,4]
Output: [1,4,2,3]

Input: head = [1,2,3,4,5]
Output: [1,5,2,4,3]
```

## Approach: Three-Step Process

### Step 1: Find Middle of List
```java
// Use fast/slow pointers
ListNode slow = head;
ListNode fast = head;
while (fast.next != null && fast.next.next != null) {
    slow = slow.next;
    fast = fast.next.next;
}
```

### Step 2: Reverse Second Half
```java
ListNode secondHalf = reverseList(slow.next);
slow.next = null; // Break connection
```

### Step 3: Merge Two Halves Alternately
```java
ListNode first = head;
ListNode second = secondHalf;

while (second != null) {
    ListNode firstNext = first.next;
    ListNode secondNext = second.next;
    
    first.next = second;
    second.next = firstNext;
    
    first = firstNext;
    second = secondNext;
}
```

## Complete Solution:
```java
public void reorderList(ListNode head) {
    if (head == null || head.next == null) return;
    
    // Step 1: Find middle
    ListNode slow = head;
    ListNode fast = head;
    while (fast.next != null && fast.next.next != null) {
        slow = slow.next;
        fast = fast.next.next;
    }
    
    // Step 2: Reverse second half
    ListNode secondHalf = reverseList(slow.next);
    slow.next = null;
    
    // Step 3: Merge alternately
    ListNode first = head;
    ListNode second = secondHalf;
    
    while (second != null) {
        ListNode firstNext = first.next;
        ListNode secondNext = second.next;
        
        first.next = second;
        second.next = firstNext;
        
        first = firstNext;
        second = secondNext;
    }
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
```

### Time & Space Complexity:
- **Time:** O(n) - Three O(n) operations
- **Space:** O(1) - Only using pointers

## Visualization:
```
Original: 1 -> 2 -> 3 -> 4 -> 5

Step 1: Find middle
First half:  1 -> 2 -> 3
Second half:      4 -> 5

Step 2: Reverse second half
First half:  1 -> 2 -> 3
Second half:      5 -> 4

Step 3: Merge alternately
Result: 1 -> 5 -> 2 -> 4 -> 3
```

## Edge Cases:
1. **Empty list or single node**
2. **Two nodes:** [1,2] → [1,2]
3. **Even vs odd length lists**

## Alternative: Stack/Deque Approach

### How it works:
1. **Store all nodes** in a deque
2. **Alternately pop** from front and back
3. **Reconstruct list**

### Time & Space Complexity:
- **Time:** O(n)
- **Space:** O(n) - Deque storage

## LeetCode Similar Problems:
- [206. Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/)
- [876. Middle of the Linked List](https://leetcode.com/problems/middle-of-the-linked-list/)
- [234. Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list/)
- [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/)

## Interview Tips:
- Break problem into smaller subproblems
- This combines three common linked list techniques
- Practice each step separately first
- Handle edge cases for odd/even length lists
- Remember to break the connection in the middle 