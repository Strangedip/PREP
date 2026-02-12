# Reverse Linked List

## Problem Statement

Given the `head` of a singly linked list, reverse the list, and return the reversed list.

**LeetCode**: [206. Reverse Linked List](https://leetcode.com/problems/reverse-linked-list/)

### Examples

```
Input:  head = [1,2,3,4,5]
Output: [5,4,3,2,1]

Input:  head = [1,2]
Output: [2,1]

Input:  head = []
Output: []
```

### Constraints
- The number of nodes in the list is in the range `[0, 5000]`
- `-5000 <= Node.val <= 5000`

---

## Approach 1: Iterative (Optimal)

**Time**: O(n), **Space**: O(1)

The idea is to traverse the list once, reversing each node's `next` pointer as we go. We need three pointers: `prev`, `curr`, and `next`.

### Visual Walkthrough

```
Original: 1 → 2 → 3 → 4 → 5 → null
          ^
         curr
prev = null

Step 1: Save next = curr.next (2)
        Reverse: curr.next = prev (null)
        Move: prev = curr (1), curr = next (2)

null ← 1    2 → 3 → 4 → 5 → null
       ^    ^
      prev curr

Step 2: Save next = curr.next (3)
        Reverse: curr.next = prev (1)
        Move: prev = curr (2), curr = next (3)

null ← 1 ← 2    3 → 4 → 5 → null
            ^    ^
           prev curr

Step 3: Save next = curr.next (4)
        Reverse: curr.next = prev (2)
        Move: prev = curr (3), curr = next (4)

null ← 1 ← 2 ← 3    4 → 5 → null
                 ^    ^
                prev curr

Step 4: Save next = curr.next (5)
        Reverse: curr.next = prev (3)
        Move: prev = curr (4), curr = next (5)

null ← 1 ← 2 ← 3 ← 4    5 → null
                      ^    ^
                     prev curr

Step 5: Save next = curr.next (null)
        Reverse: curr.next = prev (4)
        Move: prev = curr (5), curr = next (null)

null ← 1 ← 2 ← 3 ← 4 ← 5    null
                           ^    ^
                          prev curr

curr == null → STOP
Return prev (node 5 = new head)

Result: 5 → 4 → 3 → 2 → 1 → null ✓
```

### Java Implementation

```java
/**
 * Definition for singly-linked list.
 * public class ListNode {
 *     int val;
 *     ListNode next;
 *     ListNode() {}
 *     ListNode(int val) { this.val = val; }
 *     ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 * }
 */
class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode curr = head;

        while (curr != null) {
            ListNode next = curr.next;  // 1. Save next node
            curr.next = prev;           // 2. Reverse the link
            prev = curr;               // 3. Move prev forward
            curr = next;               // 4. Move curr forward
        }

        return prev; // prev is now the new head
    }
}
```

### The Four-Line Pattern (Memorize This)

```java
while (curr != null) {
    next = curr.next;    // Save
    curr.next = prev;    // Reverse
    prev = curr;         // Advance prev
    curr = next;         // Advance curr
}
```

This four-line pattern is the foundation for many linked list problems. It appears in Reverse Linked List II, Reverse Nodes in k-Group, Reorder List, and Palindrome Linked List.

---

## Approach 2: Recursive

**Time**: O(n), **Space**: O(n) — recursion stack

The recursive approach reverses the rest of the list first, then fixes the current node's pointers.

### How the Recursion Works

```
reverseList(1 → 2 → 3 → 4 → 5)
  reverseList(2 → 3 → 4 → 5)
    reverseList(3 → 4 → 5)
      reverseList(4 → 5)
        reverseList(5)
          return 5 (base case: single node)
        // Now head=4, head.next=5
        // 5.next = 4, 4.next = null
        // Return 5
      // Now head=3, head.next=4
      // 4.next = 3, 3.next = null
      // Return 5
    // Now head=2, head.next=3
    // 3.next = 2, 2.next = null
    // Return 5
  // Now head=1, head.next=2
  // 2.next = 1, 1.next = null
  // Return 5

Result: 5 → 4 → 3 → 2 → 1 → null
```

### Java Implementation

```java
class Solution {
    public ListNode reverseList(ListNode head) {
        // Base case: empty list or single node
        if (head == null || head.next == null) {
            return head;
        }

        // Recursively reverse the rest of the list
        ListNode newHead = reverseList(head.next);

        // head.next is now the LAST node of the reversed sublist
        // Make it point back to head
        head.next.next = head;

        // Remove the old forward link (head now points to null = end of reversed list)
        head.next = null;

        return newHead; // newHead is always the original tail node
    }
}
```

> **Important**: The recursive solution uses O(n) stack space. For a list with 5000 nodes, this is fine. For lists with 100,000+ nodes, the iterative approach is mandatory to avoid StackOverflowError.

---

## Complexity Analysis

| Approach | Time | Space | Notes |
|----------|------|-------|-------|
| Iterative | O(n) | O(1) | Optimal — always preferred |
| Recursive | O(n) | O(n) | Elegant but uses stack space |

Where n = number of nodes in the linked list.

---

## Edge Cases

| Case | Input | Output | Why It Matters |
|------|-------|--------|---------------|
| Empty list | `null` | `null` | Return null immediately |
| Single node | `[1]` | `[1]` | Already reversed |
| Two nodes | `[1,2]` | `[2,1]` | Minimal non-trivial case |
| Already sorted | `[1,2,3,4,5]` | `[5,4,3,2,1]` | Standard case |

---

## Interview Tips

1. **This is THE fundamental linked list problem.** You must solve it in under 5 minutes. It is asked as a warm-up or as a building block for harder problems.
2. **Draw the pointers on a whiteboard.** Even if you know the solution, drawing the before/after state of the three pointers (prev, curr, next) for 2-3 steps shows strong communication.
3. **Both approaches are valid.** Mention both iterative and recursive. Say: "I'll use iterative because it's O(1) space. The recursive approach is also possible but uses O(n) stack space."
4. **State the invariant**: "After each iteration of the loop, all nodes before `curr` are already reversed."

### Common Follow-Up Questions

| Follow-Up | Problem | Approach |
|-----------|---------|----------|
| "Reverse nodes between positions m and n" | [Reverse Linked List II](https://leetcode.com/problems/reverse-linked-list-ii/) | Find node at m, reverse m-to-n section, reattach |
| "Reverse in groups of k" | [Reverse Nodes in k-Group](https://leetcode.com/problems/reverse-nodes-in-k-group/) | Count k, reverse k, recurse on rest |
| "Check if palindrome" | [Palindrome Linked List](https://leetcode.com/problems/palindrome-linked-list/) | Find middle, reverse second half, compare |
| "Reorder list" | [Reorder List](https://leetcode.com/problems/reorder-list/) | Find middle, reverse second half, merge alternating |
| "Swap nodes in pairs" | [Swap Nodes in Pairs](https://leetcode.com/problems/swap-nodes-in-pairs/) | Reverse every 2 nodes |

---

## Related Problems

| Problem | Connection | LeetCode |
|---------|-----------|----------|
| Reverse Linked List II | Reverse a sublist from position m to n | [92](https://leetcode.com/problems/reverse-linked-list-ii/) |
| Reverse Nodes in k-Group | Reverse in groups of k (uses this as subroutine) | [25](https://leetcode.com/problems/reverse-nodes-in-k-group/) |
| Palindrome Linked List | Reverse second half, then compare | [234](https://leetcode.com/problems/palindrome-linked-list/) |
| Reorder List | Find middle, reverse second half, merge | [143](https://leetcode.com/problems/reorder-list/) |
| Swap Nodes in Pairs | Reverse in groups of 2 | [24](https://leetcode.com/problems/swap-nodes-in-pairs/) |

---

## Real-World Applications

- **Undo/Redo**: Reversing a linked list of operations allows implementing undo/redo functionality.
- **Browser history**: Navigating back through browser history is essentially traversing a reversed list.
- **Stack implementation**: A reversed linked list effectively converts a queue-like structure to stack-like behavior.
- **Palindrome checking**: Many palindrome algorithms for linked lists use list reversal as a subroutine.

---

**Pattern**: In-Place Linked List Reversal
**Difficulty**: Easy
**Must-Know**: Yes — this is the #1 most important linked list problem
