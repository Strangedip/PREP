# Merge K Sorted Lists

## Problem Statement
Given an array of `k` linked-lists, each sorted in ascending order, merge all the linked-lists into one sorted linked-list and return it.

**Example:**
```
Input: lists = [[1,4,5],[1,3,4],[2,6]]
Output: [1,1,2,3,4,4,5,6]
```

## Approaches

### Approach 1: Min Heap (Priority Queue)

#### Key Insight
Use a min heap to always extract the smallest element from all list heads.

#### Algorithm
1. Add all non-null list heads to a min heap
2. While heap is not empty:
   - Extract the minimum node
   - Add it to result list
   - If the extracted node has a next node, add it to heap

#### Time Complexity
- **O(N log k)** where N = total nodes, k = number of lists
- Each node is added/removed from heap once: O(log k) per operation

#### Space Complexity
- **O(k)** - Heap stores at most k nodes

```java
public ListNode mergeKLists(ListNode[] lists) {
    PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> a.val - b.val);
    
    // Add all list heads
    for (ListNode list : lists) {
        if (list != null) {
            minHeap.offer(list);
        }
    }
    
    ListNode dummy = new ListNode(0);
    ListNode current = dummy;
    
    while (!minHeap.isEmpty()) {
        ListNode minNode = minHeap.poll();
        current.next = minNode;
        current = current.next;
        
        if (minNode.next != null) {
            minHeap.offer(minNode.next);
        }
    }
    
    return dummy.next;
}
```

### Approach 2: Divide and Conquer

#### Key Insight
Merge lists in pairs repeatedly until only one list remains.

#### Algorithm
1. While there are more than 1 list:
   - Merge lists in pairs: [l0,l1], [l2,l3], [l4,l5], ...
   - Replace original array with merged results
2. Return the final merged list

#### Time Complexity
- **O(N log k)** where N = total nodes, k = number of lists
- Each level merges all N nodes, and there are log k levels

#### Space Complexity
- **O(1)** - No extra space for heap, only for recursion stack

```java
public ListNode mergeKListsDivideConquer(ListNode[] lists) {
    if (lists == null || lists.length == 0) return null;
    
    while (lists.length > 1) {
        List<ListNode> mergedLists = new ArrayList<>();
        
        for (int i = 0; i < lists.length; i += 2) {
            ListNode l1 = lists[i];
            ListNode l2 = (i + 1 < lists.length) ? lists[i + 1] : null;
            mergedLists.add(mergeTwoLists(l1, l2));
        }
        
        lists = mergedLists.toArray(new ListNode[0]);
    }
    
    return lists[0];
}
```

## Comparison

| Approach | Time | Space | When to Use |
|----------|------|-------|-------------|
| Min Heap | O(N log k) | O(k) | When k is small, or when you need to process nodes one by one |
| Divide & Conquer | O(N log k) | O(1) | When space is a concern, or k is large |

## Example Trace (Min Heap)

Lists: [[1,4,5], [1,3,4], [2,6]]

1. **Initial heap**: [1(list1), 1(list2), 2(list3)]
2. **Extract 1(list1)**: heap = [1(list2), 2(list3)], add 4 → heap = [1(list2), 2(list3), 4]
3. **Extract 1(list2)**: heap = [2(list3), 4], add 3 → heap = [2(list3), 3, 4]
4. **Extract 2(list3)**: heap = [3, 4], add 6 → heap = [3, 4, 6]
5. **Extract 3**: heap = [4, 6], add 4 → heap = [4, 4, 6]
6. **Continue until heap is empty**

**Result**: [1, 1, 2, 3, 4, 4, 5, 6]

## Key Points
- Min heap approach is intuitive and easy to implement
- Divide and conquer is more space-efficient
- Both have the same optimal time complexity
- The merge two lists function is a fundamental building block 