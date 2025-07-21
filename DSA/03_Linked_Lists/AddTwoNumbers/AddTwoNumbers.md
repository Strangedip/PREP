# Add Two Numbers

## Problem Statement
Given two non-empty linked lists representing two non-negative integers stored in reverse order, add the two numbers and return the sum as a linked list.

## Example
```
Input: l1 = [2,4,3], l2 = [5,6,4]
Output: [7,0,8]
Explanation: 342 + 465 = 807
```

## Approach: Elementary Math Simulation

### How it works:
1. **Traverse both lists simultaneously**
2. **Add corresponding digits + carry**
3. **Handle carry propagation**
4. **Create new node for each digit**

### Key Logic:
```java
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
```

### Time & Space Complexity:
- **Time:** O(max(m, n)) where m, n are lengths of input lists
- **Space:** O(max(m, n)) for the result list

## Edge Cases:
1. **Different length lists:** [9,9] + [1] = [0,0,1]
2. **Carry at the end:** [5] + [5] = [0,1]
3. **One list is null**
4. **Both lists represent 0**

## Alternative Approach: In-place Modification

### When applicable:
- **Allowed to modify input lists**
- **Want to save space**

### Key Logic:
```java
// Reuse longer list for result
// Modify nodes in place while traversing
```

## Follow-up Variations:
1. **Numbers stored in forward order** → Reverse lists or use stack
2. **Add three numbers** → Similar logic with three pointers
3. **Subtract two numbers** → Handle borrowing

## LeetCode Similar Problems:
- [445. Add Two Numbers II](https://leetcode.com/problems/add-two-numbers-ii/)
- [67. Add Binary](https://leetcode.com/problems/add-binary/)
- [43. Multiply Strings](https://leetcode.com/problems/multiply-strings/)
- [415. Add Strings](https://leetcode.com/problems/add-strings/)
- [989. Add to Array-Form of Integer](https://leetcode.com/problems/add-to-array-form-of-integer/)

## Interview Tips:
- Handle carry carefully - check after both lists are exhausted
- Use dummy head to simplify edge cases
- Test with examples that have different lengths
- Consider overflow scenarios
- Remember that numbers are stored in reverse order 