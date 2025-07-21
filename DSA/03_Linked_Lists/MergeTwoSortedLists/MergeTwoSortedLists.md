# Merge Two Sorted Lists

## Problem Statement
Merge two sorted linked lists and return it as a sorted list. The list should be made by splicing together the nodes of the first two lists.

## Example
```
Input: list1 = [1,2,4], list2 = [1,3,4]
Output: [1,1,2,3,4,4]
```

## Approach 1: Iterative with Dummy Head

### How it works:
1. **Use dummy head** to simplify edge cases
2. **Compare current nodes** from both lists
3. **Attach smaller node** to result
4. **Handle remaining nodes** when one list is exhausted

### Key Logic:
```java
ListNode dummy = new ListNode(0);
ListNode current = dummy;

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

return dummy.next;
```

### Time & Space Complexity:
- **Time:** O(m + n) where m, n are lengths of lists
- **Space:** O(1) - Only using a few pointers

## Approach 2: Recursive

### How it works:
1. **Base cases:** If one list is null, return the other
2. **Recursive case:** Choose smaller head, recursively merge rest
3. **Build result from bottom up**

### Key Logic:
```java
if (list1 == null) return list2;
if (list2 == null) return list1;

if (list1.val <= list2.val) {
    list1.next = mergeTwoLists(list1.next, list2);
    return list1;
} else {
    list2.next = mergeTwoLists(list1, list2.next);
    return list2;
}
```

### Time & Space Complexity:
- **Time:** O(m + n) - Each node processed once
- **Space:** O(m + n) - Recursion stack depth

## Edge Cases:
1. **One or both lists are empty**
2. **Lists have different lengths**
3. **All elements in one list are smaller**
4. **Duplicate values** (handle with <= or <)

## Alternative: In-place Modification

### When applicable:
- **Don't need to preserve original lists**
- **Want to reuse existing nodes**

### Benefits:
- **No extra space** for new nodes
- **More memory efficient**

## Follow-up Variations:
1. **Merge K sorted lists** → Use divide and conquer
2. **Merge in descending order** → Change comparison
3. **Remove duplicates while merging** → Skip equal values

## LeetCode Similar Problems:
- [23. Merge k Sorted Lists](https://leetcode.com/problems/merge-k-sorted-lists/)
- [88. Merge Sorted Array](https://leetcode.com/problems/merge-sorted-array/)
- [1669. Merge In Between Linked Lists](https://leetcode.com/problems/merge-in-between-linked-lists/)
- [148. Sort List](https://leetcode.com/problems/sort-list/)

## Interview Tips:
- Dummy head pattern simplifies linked list problems
- Handle edge cases (null lists) first
- Consider both iterative and recursive approaches
- Draw examples to visualize the merging process
- Practice with lists of different lengths 