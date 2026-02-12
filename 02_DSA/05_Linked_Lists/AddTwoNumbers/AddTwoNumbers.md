# Add Two Numbers (LeetCode 2)

## Problem Statement

You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.

You may assume the two numbers do not contain any leading zero, except the number 0 itself.

**Example 1:**
```
Input: l1 = [2,4,3], l2 = [5,6,4]
Output: [7,0,8]
Explanation: 342 + 465 = 807
```

**Example 2:**
```
Input: l1 = [0], l2 = [0]
Output: [0]
```

**Example 3:**
```
Input: l1 = [9,9,9,9,9,9,9], l2 = [9,9,9,9]
Output: [8,9,9,9,0,0,0,1]
Explanation: 9999999 + 9999 = 10009998
```

**Constraints:**
- The number of nodes in each linked list is in the range `[1, 100]`.
- `0 <= Node.val <= 9`
- It is guaranteed that the list represents a number that does not have leading zeros.

---

## Why This Problem Is Important

LeetCode #2 is one of the most classic linked list problems and is extremely frequently asked at all FAANG companies. It tests:
- Linked list traversal and construction
- Carry handling (elementary math)
- Edge case handling (different lengths, final carry)
- Use of dummy head node pattern

---

## ListNode Definition

```java
public class ListNode {
    int val;
    ListNode next;
    
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}
```

---

## Approach: Elementary Math with Carry

**Time:** O(max(m, n)) where m and n are the lengths of the two lists
**Space:** O(max(m, n)) for the result list (O(1) extra space otherwise)

### Core Insight

Since the digits are stored in reverse order, the least significant digit is at the head of the list. This means we can add digits from left to right, which is exactly how we add numbers by hand (starting from the ones place).

### Algorithm

1. Initialize a dummy head node and a current pointer.
2. Initialize carry = 0.
3. Traverse both lists simultaneously:
   - Get the value from each list (0 if the list is exhausted).
   - Compute sum = val1 + val2 + carry.
   - Create a new node with value `sum % 10`.
   - Update carry = `sum / 10`.
4. After both lists are exhausted, if carry > 0, add one more node.

### Complete Implementation

```java
public class AddTwoNumbers {
    
    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode dummy = new ListNode(0); // Dummy head simplifies edge cases
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
}
```

### Dry Run Example

```
l1: 2 → 4 → 3  (represents 342)
l2: 5 → 6 → 4  (represents 465)

Iteration 1:
  sum = 0 + 2 + 5 = 7, carry = 0, digit = 7
  result: 7

Iteration 2:
  sum = 0 + 4 + 6 = 10, carry = 1, digit = 0
  result: 7 → 0

Iteration 3:
  sum = 1 + 3 + 4 = 8, carry = 0, digit = 8
  result: 7 → 0 → 8

Both lists exhausted, carry = 0, done.
Output: 7 → 0 → 8  (represents 807)

Verify: 342 + 465 = 807 ✓
```

### Dry Run with Carry Propagation

```
l1: 9 → 9 → 9 → 9 → 9 → 9 → 9  (9,999,999)
l2: 9 → 9 → 9 → 9                (9,999)

Iteration 1: sum=0+9+9=18, carry=1, digit=8. result: 8
Iteration 2: sum=1+9+9=19, carry=1, digit=9. result: 8→9
Iteration 3: sum=1+9+9=19, carry=1, digit=9. result: 8→9→9
Iteration 4: sum=1+9+9=19, carry=1, digit=9. result: 8→9→9→9
Iteration 5: sum=1+9+0=10, carry=1, digit=0. result: 8→9→9→9→0
Iteration 6: sum=1+9+0=10, carry=1, digit=0. result: 8→9→9→9→0→0
Iteration 7: sum=1+9+0=10, carry=1, digit=0. result: 8→9→9→9→0→0→0
Iteration 8: sum=1+0+0=1, carry=0, digit=1. result: 8→9→9→9→0→0→0→1

Output: [8,9,9,9,0,0,0,1]  (represents 10,009,998)
Verify: 9,999,999 + 9,999 = 10,009,998 ✓
```

---

## Why the Dummy Head Pattern

Without a dummy head, you would need special handling for the first node:

```java
// Without dummy head (more complex)
ListNode head = null, current = null;
if (head == null) {
    head = new ListNode(digit);
    current = head;
} else {
    current.next = new ListNode(digit);
    current = current.next;
}
return head;
```

With a dummy head, the code is cleaner because every node (including the first) is added the same way via `current.next`. The dummy node is never part of the result — we return `dummy.next`.

---

## Alternative: Recursive Approach

**Time:** O(max(m, n)), **Space:** O(max(m, n)) call stack

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    return addWithCarry(l1, l2, 0);
}

private ListNode addWithCarry(ListNode l1, ListNode l2, int carry) {
    // Base case: both lists exhausted and no carry
    if (l1 == null && l2 == null && carry == 0) {
        return null;
    }
    
    int sum = carry;
    if (l1 != null) sum += l1.val;
    if (l2 != null) sum += l2.val;
    
    ListNode node = new ListNode(sum % 10);
    node.next = addWithCarry(
        l1 != null ? l1.next : null,
        l2 != null ? l2.next : null,
        sum / 10
    );
    
    return node;
}
```

This approach is elegant but uses O(n) stack space. The iterative approach is preferred.

---

## Follow-Up: Add Two Numbers II (LeetCode 445)

In this variant, the most significant digit comes first (forward order). You cannot simply reverse and apply the same algorithm in an interview because the interviewer wants a different approach.

### Approach: Stack-Based

```java
public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
    Stack<Integer> s1 = new Stack<>();
    Stack<Integer> s2 = new Stack<>();
    
    // Push all digits onto stacks
    while (l1 != null) { s1.push(l1.val); l1 = l1.next; }
    while (l2 != null) { s2.push(l2.val); l2 = l2.next; }
    
    int carry = 0;
    ListNode head = null;
    
    while (!s1.isEmpty() || !s2.isEmpty() || carry != 0) {
        int sum = carry;
        if (!s1.isEmpty()) sum += s1.pop();
        if (!s2.isEmpty()) sum += s2.pop();
        
        carry = sum / 10;
        
        // Build the list in reverse (prepend each new node)
        ListNode node = new ListNode(sum % 10);
        node.next = head;
        head = node;
    }
    
    return head;
}
```

---

## Approach Comparison

| Approach | Time | Space | Pros | Cons |
|----------|------|-------|------|------|
| Iterative (dummy head) | O(max(m,n)) | O(1) extra | Clean, efficient | None |
| Recursive | O(max(m,n)) | O(max(m,n)) stack | Elegant | Stack overflow risk |
| Stack-based (for forward order) | O(m+n) | O(m+n) | Handles forward order | Extra space for stacks |

---

## Common Mistakes

1. **Forgetting the final carry**: If the last addition produces a carry (e.g., 999 + 1 = 1000), you need to add an extra node. The `carry != 0` condition in the while loop handles this.
2. **Not handling different lengths**: One list may be longer than the other. Treat exhausted list values as 0.
3. **Modifying input lists**: The problem expects you to create new nodes. Do not modify the input lists unless explicitly allowed.
4. **Integer overflow attempt**: Some candidates try to convert lists to integers, add them, and convert back. This fails for lists with more than 19 digits (exceeds `long` range).
5. **Returning dummy instead of dummy.next**: The dummy node is not part of the result.

---

## Edge Cases

| Case | l1 | l2 | Expected | Explanation |
|------|----|----|----------|-------------|
| Both zeros | [0] | [0] | [0] | 0 + 0 = 0 |
| Different lengths | [9,9] | [1] | [0,0,1] | 99 + 1 = 100 |
| Carry propagation | [9,9,9] | [1] | [0,0,0,1] | 999 + 1 = 1000 |
| Single digits | [5] | [5] | [0,1] | 5 + 5 = 10 |
| One very long | [1] | [9,9,9,9,9,9,9,9,9] | [0,0,0,0,0,0,0,0,0,1] | 1 + 999999999 |

---

## LeetCode Similar Problems

- [445. Add Two Numbers II](https://leetcode.com/problems/add-two-numbers-ii/) — Forward order variant
- [67. Add Binary](https://leetcode.com/problems/add-binary/) — Same pattern with strings
- [415. Add Strings](https://leetcode.com/problems/add-strings/) — Add digit strings
- [43. Multiply Strings](https://leetcode.com/problems/multiply-strings/) — Multiply digit strings
- [989. Add to Array-Form of Integer](https://leetcode.com/problems/add-to-array-form-of-integer/)
- [66. Plus One](https://leetcode.com/problems/plus-one/) — Add 1 to a number in array form
- [369. Plus One Linked List](https://leetcode.com/problems/plus-one-linked-list/)

---

## Interview Tips

1. **Use the dummy head pattern**: State it upfront: "I'll use a dummy head node to simplify edge cases."
2. **Handle carry in the loop condition**: "My while loop continues while either list has nodes OR there is a carry remaining."
3. **Don't try to convert to integers**: For very long numbers, this overflows. The linked list approach handles arbitrarily large numbers.
4. **Be ready for the forward-order variant**: LeetCode 445 is a common follow-up. Know the stack-based approach.
5. **Mention the pattern**: "This is the same addition-with-carry pattern used in Add Binary and Add Strings. The only difference is the data structure."
6. **Test with carry propagation**: Walk through `[9,9,9] + [1] = [0,0,0,1]` to show you handle the final carry.
