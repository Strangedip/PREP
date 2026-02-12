# Remove Nth Node From End

## Problem Statement
Given the head of a linked list, remove the nth node from the end and return its head.

## Example
```
Input: head = [1,2,3,4,5], n = 2
Output: [1,2,3,5]
Explanation: Remove the 2nd node from end (value 4)
```

## Approach 1: Two-Pass Solution

### How it works:
1. **First pass:** Count total length of list
2. **Calculate target position** from beginning (length - n)
3. **Second pass:** Remove node at target position

### Key Logic:
```java
// First pass: count length
int length = 0;
ListNode current = head;
while (current != null) {
    length++;
    current = current.next;
}

// Handle edge case: remove head
if (n == length) {
    return head.next;
}

// Second pass: find node before target
current = head;
for (int i = 0; i < length - n - 1; i++) {
    current = current.next;
}

// Remove target node
current.next = current.next.next;
return head;
```

### Time & Space Complexity:
- **Time:** O(n) - Two passes through list
- **Space:** O(1) - Only using a few variables

## Approach 2: One-Pass with Two Pointers (Optimal!)

### How it works:
1. **Create gap of n nodes** between two pointers
2. **Move both pointers** until fast reaches end
3. **Slow pointer is at node before target**

### Key Logic:
```java
ListNode dummy = new ListNode(0);
dummy.next = head;
ListNode fast = dummy;
ListNode slow = dummy;

// Create gap of n+1 between fast and slow
for (int i = 0; i <= n; i++) {
    fast = fast.next;
}

// Move both until fast reaches end
while (fast != null) {
    fast = fast.next;
    slow = slow.next;
}

// Remove target node
slow.next = slow.next.next;
return dummy.next;
```

### Time & Space Complexity:
- **Time:** O(n) - Single pass through list
- **Space:** O(1) - Only using two pointers

## Why Dummy Head is Important:

### Edge Case: Remove First Node
```
Input: [1], n = 1
Without dummy: Need special handling
With dummy: Uniform handling
```

### Visualization:
```
Original: dummy -> 1 -> 2 -> 3 -> 4 -> 5, n = 2
Step 1:   fast moves n+1=3 steps ahead
         dummy -> 1 -> 2 -> 3 -> 4 -> 5
         slow      fast
Step 2:   Move both until fast reaches null
         dummy -> 1 -> 2 -> 3 -> 4 -> 5 -> null
                      slow          fast
Step 3:   slow.next = slow.next.next (remove 4)
```

## Edge Cases:
1. **Remove head node** (n equals list length)
2. **Remove last node** (n = 1)
3. **Single node list**
4. **Invalid n** (larger than list length)

## Alternative: Stack Approach

### How it works:
1. **Push all nodes** onto stack
2. **Pop n nodes** to find target
3. **Remove target node**

### Time & Space Complexity:
- **Time:** O(n)
- **Space:** O(n) - Stack storage

## LeetCode Similar Problems:
- [237. Delete Node in a Linked List](https://leetcode.com/problems/delete-node-in-a-linked-list/)
- [83. Remove Duplicates from Sorted List](https://leetcode.com/problems/remove-duplicates-from-sorted-list/)
- [203. Remove Linked List Elements](https://leetcode.com/problems/remove-linked-list-elements/)
- [876. Middle of the Linked List](https://leetcode.com/problems/middle-of-the-linked-list/)

## Interview Tips:
- Start with two-pass approach to show understanding
- Optimize to one-pass with two pointers
- Use dummy head to handle edge cases elegantly
- Practice the gap creation technique
- Consider edge case where we remove the first node 