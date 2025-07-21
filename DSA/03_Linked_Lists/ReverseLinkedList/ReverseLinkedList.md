# Reverse Linked List

## Problem Statement
Reverse a singly linked list and return the new head.

## Approach 1: Iterative (Optimal!)
**Time:** O(n), **Space:** O(1)

### The Technique:
1. Keep track of prev, current, and next pointers
2. For each node: save next, reverse current.next to prev
3. Move all pointers one step forward
4. Return prev (new head)

### Key Pattern:
```java
while (current != null) {
    next = current.next;    // Save next
    current.next = prev;    // Reverse link
    prev = current;         // Move prev
    current = next;         // Move current
}
```

## LeetCode Similar Problems:
- [92. Reverse Linked List II](https://leetcode.com/problems/reverse-linked-list-ii/)
- [25. Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/)
- [24. Swap Nodes in Pairs](https://leetcode.com/problems/swap-nodes-in-pairs/)
- [143. Reorder List](https://leetcode.com/problems/reorder-list/)

## Note
**For Mid-Level:** This is THE fundamental linked list problem. Master the iterative approach. 