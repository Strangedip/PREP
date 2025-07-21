# Segment Tree

## Problem Statement
Segment Tree is a tree data structure used for storing information about array segments. It allows answering range queries and updating array elements efficiently.

**Common Operations:**
- **Range Query:** Find sum/min/max over array range [l, r] in O(log n)
- **Point Update:** Update single array element in O(log n)
- **Range Update:** Update all elements in range [l, r] in O(log n) with lazy propagation

## Example
```
Array: [1, 3, 5, 7, 9, 11]
Query: sum(1, 3) = 3 + 5 + 7 = 15
Update: arr[2] = 6
Query: sum(1, 3) = 3 + 6 + 7 = 16
```

## Approach 1: Basic Segment Tree (Sum)

### Tree Structure:
```
Array: [1, 3, 5, 7, 9, 11]
Segment Tree:
                36
              /    \
            9        27
          /  \      /  \
        4     5   16    11
       / \   / \ / \
      1   3 5   7 9  11
```

### Implementation:
```java
class SegmentTree {
    private int[] tree;
    private int n;
    
    public SegmentTree(int[] arr) {
        n = arr.length;
        tree = new int[4 * n]; // 4*n is safe upper bound
        build(arr, 0, 0, n - 1);
    }
    
    private void build(int[] arr, int node, int start, int end) {
        if (start == end) {
            tree[node] = arr[start]; // Leaf node
        } else {
            int mid = (start + end) / 2;
            int leftChild = 2 * node + 1;
            int rightChild = 2 * node + 2;
            
            build(arr, leftChild, start, mid);
            build(arr, rightChild, mid + 1, end);
            
            tree[node] = tree[leftChild] + tree[rightChild];
        }
    }
    
    public int query(int l, int r) {
        return query(0, 0, n - 1, l, r);
    }
    
    private int query(int node, int start, int end, int l, int r) {
        if (r < start || end < l) {
            return 0; // No overlap
        }
        
        if (l <= start && end <= r) {
            return tree[node]; // Complete overlap
        }
        
        // Partial overlap
        int mid = (start + end) / 2;
        int leftChild = 2 * node + 1;
        int rightChild = 2 * node + 2;
        
        int leftSum = query(leftChild, start, mid, l, r);
        int rightSum = query(rightChild, mid + 1, end, l, r);
        
        return leftSum + rightSum;
    }
    
    public void update(int idx, int value) {
        update(0, 0, n - 1, idx, value);
    }
    
    private void update(int node, int start, int end, int idx, int value) {
        if (start == end) {
            tree[node] = value; // Leaf node
        } else {
            int mid = (start + end) / 2;
            int leftChild = 2 * node + 1;
            int rightChild = 2 * node + 2;
            
            if (idx <= mid) {
                update(leftChild, start, mid, idx, value);
            } else {
                update(rightChild, mid + 1, end, idx, value);
            }
            
            tree[node] = tree[leftChild] + tree[rightChild];
        }
    }
}
```

### Time & Space Complexity:
- **Build:** O(n)
- **Query:** O(log n)
- **Update:** O(log n)
- **Space:** O(4n) = O(n)

## Approach 2: Generic Segment Tree

### Flexible Implementation:
```java
class SegmentTree<T> {
    private T[] tree;
    private int n;
    private BinaryOperator<T> combiner;
    private T identity;
    
    public SegmentTree(T[] arr, BinaryOperator<T> combiner, T identity) {
        this.n = arr.length;
        this.combiner = combiner;
        this.identity = identity;
        this.tree = (T[]) new Object[4 * n];
        build(arr, 0, 0, n - 1);
    }
    
    // For Min Segment Tree
    public static SegmentTree<Integer> minTree(Integer[] arr) {
        return new SegmentTree<>(arr, Integer::min, Integer.MAX_VALUE);
    }
    
    // For Max Segment Tree
    public static SegmentTree<Integer> maxTree(Integer[] arr) {
        return new SegmentTree<>(arr, Integer::max, Integer.MIN_VALUE);
    }
    
    // For Sum Segment Tree
    public static SegmentTree<Integer> sumTree(Integer[] arr) {
        return new SegmentTree<>(arr, Integer::sum, 0);
    }
}
```

## Approach 3: Lazy Propagation (Range Updates)

### For Range Update + Range Query:
```java
class LazySegmentTree {
    private long[] tree;
    private long[] lazy;
    private int n;
    
    public LazySegmentTree(int[] arr) {
        n = arr.length;
        tree = new long[4 * n];
        lazy = new long[4 * n];
        build(arr, 0, 0, n - 1);
    }
    
    private void push(int node, int start, int end) {
        if (lazy[node] != 0) {
            tree[node] += lazy[node] * (end - start + 1);
            
            if (start != end) { // Not a leaf
                lazy[2 * node + 1] += lazy[node];
                lazy[2 * node + 2] += lazy[node];
            }
            
            lazy[node] = 0;
        }
    }
    
    public void rangeUpdate(int l, int r, int value) {
        rangeUpdate(0, 0, n - 1, l, r, value);
    }
    
    private void rangeUpdate(int node, int start, int end, int l, int r, int value) {
        push(node, start, end);
        
        if (start > r || end < l) {
            return; // No overlap
        }
        
        if (start >= l && end <= r) {
            lazy[node] += value;
            push(node, start, end);
            return;
        }
        
        // Partial overlap
        int mid = (start + end) / 2;
        rangeUpdate(2 * node + 1, start, mid, l, r, value);
        rangeUpdate(2 * node + 2, mid + 1, end, l, r, value);
        
        push(2 * node + 1, start, mid);
        push(2 * node + 2, mid + 1, end);
        
        tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
    }
    
    public long rangeQuery(int l, int r) {
        return rangeQuery(0, 0, n - 1, l, r);
    }
    
    private long rangeQuery(int node, int start, int end, int l, int r) {
        if (start > r || end < l) {
            return 0; // No overlap
        }
        
        push(node, start, end);
        
        if (start >= l && end <= r) {
            return tree[node]; // Complete overlap
        }
        
        // Partial overlap
        int mid = (start + end) / 2;
        long leftSum = rangeQuery(2 * node + 1, start, mid, l, r);
        long rightSum = rangeQuery(2 * node + 2, mid + 1, end, l, r);
        
        return leftSum + rightSum;
    }
}
```

## Common Applications:

### 1. Range Sum Queries:
```java
// Find sum of elements from index l to r
int sum = segTree.query(l, r);
```

### 2. Range Minimum/Maximum Queries:
```java
// Find minimum element in range [l, r]
int min = minSegTree.query(l, r);
```

### 3. Count Inversions:
```java
// Use coordinate compression + segment tree
// Count elements smaller than current element to the right
```

### 4. Longest Increasing Subsequence:
```java
// Use segment tree to maintain maximum LIS length ending at each value
```

## Advanced Variations:

### 1. 2D Segment Tree:
```java
// For 2D range queries (rectangle sum/min/max)
class SegmentTree2D {
    // Implementation for matrix range queries
}
```

### 2. Persistent Segment Tree:
```java
// Maintains history of all versions
// Useful for answering queries on different versions of array
```

### 3. Dynamic Segment Tree:
```java
// Creates nodes only when needed
// Useful when coordinate range is large but few elements
```

## When to Use Segment Trees:

### ✅ Use When:
- **Multiple range queries** on static/dynamic array
- **Need O(log n) updates** and queries
- **Complex range operations** (sum, min, max, etc.)
- **Range updates** with lazy propagation

### ❌ Don't Use When:
- **Simple single queries** (use prefix sums)
- **Only point queries** (use array/map)
- **Very sparse data** (consider other structures)

## LeetCode Similar Problems:
- [307. Range Sum Query - Mutable](https://leetcode.com/problems/range-sum-query-mutable/)
- [315. Count of Smaller Numbers After Self](https://leetcode.com/problems/count-of-smaller-numbers-after-self/)
- [327. Count of Range Sum](https://leetcode.com/problems/count-of-range-sum/)
- [493. Reverse Pairs](https://leetcode.com/problems/reverse-pairs/)
- [715. Range Module](https://leetcode.com/problems/range-module/)

## Interview Tips:
- **Master basic sum segment tree** first
- **Understand lazy propagation** for range updates
- **Know when to use** vs simpler alternatives
- **Practice coordinate compression** for large ranges
- **Essential for advanced array problems** at SDE2 level 