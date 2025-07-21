# LRU Cache Implementation

## Problem Statement
Design a data structure that follows the constraints of a **Least Recently Used (LRU) cache**.

Implement the `LRUCache` class:
- `LRUCache(int capacity)`: Initialize the LRU cache with positive size capacity
- `int get(int key)`: Return the value if key exists, otherwise return -1
- `void put(int key, int value)`: Update the value if key exists, otherwise add the key-value pair. If capacity is exceeded, evict the least recently used key

**Both get and put must run in O(1) average time complexity.**

## Example
```
LRUCache lruCache = new LRUCache(2);
lruCache.put(1, 1);   // cache is {1=1}
lruCache.put(2, 2);   // cache is {1=1, 2=2}
lruCache.get(1);      // return 1, cache is {2=2, 1=1}
lruCache.put(3, 3);   // evicts key 2, cache is {1=1, 3=3}
lruCache.get(2);      // returns -1 (not found)
lruCache.put(4, 4);   // evicts key 1, cache is {3=3, 4=4}
lruCache.get(1);      // return -1 (not found)
lruCache.get(3);      // return 3
lruCache.get(4);      // return 4
```

## Solution Approach

### Data Structures Used:
1. **HashMap**: For O(1) key-value lookup
2. **Doubly Linked List**: For O(1) insertion/deletion and maintaining order

### Key Design Decisions:

#### 1. Doubly Linked List Structure
```
[Head] ⟷ [Most Recent] ⟷ [Node] ⟷ [Least Recent] ⟷ [Tail]
```

- **Head**: Dummy node pointing to most recently used
- **Tail**: Dummy node pointing to least recently used
- **Middle nodes**: Actual cache entries

#### 2. HashMap Integration
- **Key**: Cache key
- **Value**: Reference to the corresponding node in linked list

### Core Operations:

#### GET Operation:
1. Look up node in HashMap: O(1)
2. If found, move node to head (mark as most recent): O(1)
3. Return value: O(1)

#### PUT Operation:
1. Check if key exists in HashMap: O(1)
2. If exists: Update value and move to head: O(1)
3. If new:
   - Check capacity: O(1)
   - If at capacity: Remove tail node and its HashMap entry: O(1)
   - Create new node, add to head and HashMap: O(1)

### Time & Space Complexity:
- **Time Complexity**: O(1) for both get and put operations
- **Space Complexity**: O(capacity) for storing cache entries

## Implementation Details

### Node Structure:
```java
private class Node {
    int key;      // For HashMap removal during eviction
    int value;    // Actual cached value
    Node prev;    // Previous node pointer
    Node next;    // Next node pointer
}
```

### Key Helper Methods:

#### moveToHead(Node node):
1. Remove node from current position
2. Add node right after head
3. Marks node as most recently used

#### removeTail():
1. Remove least recently used node
2. Return removed node for HashMap cleanup

## Why This Design?

### 1. O(1) Time Complexity:
- HashMap provides O(1) average lookup
- Doubly linked list provides O(1) insertion/deletion when you have node reference

### 2. Order Maintenance:
- Most recent items stay near head
- Least recent items move toward tail
- Natural LRU ordering

### 3. Efficient Eviction:
- Always remove from tail (least recently used)
- No need to search for LRU item

## Common Mistakes:

1. **Using ArrayList/Array**: O(n) for maintaining order
2. **Single LinkedList without HashMap**: O(n) for searching
3. **Forgetting to update HashMap**: Inconsistent state
4. **Not handling edge cases**: Empty cache, single capacity
5. **Incorrect pointer management**: Memory leaks or broken links

## Alternative Implementations:

### 1. Using LinkedHashMap:
```java
class LRUCacheSimple extends LinkedHashMap<Integer, Integer> {
    private final int capacity;
    
    public LRUCacheSimple(int capacity) {
        super(capacity, 0.75f, true); // Access order
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
}
```

### 2. Using OrderedDict (Python):
Python's OrderedDict provides similar functionality with built-in ordering.

## Applications:
- **CPU Cache**: Managing memory pages
- **Web Cache**: Browser/CDN caching
- **Database Buffer Pool**: Page replacement
- **Mobile Apps**: Image/data caching
- **Compiler Optimization**: Register allocation

## Related Problems:
- LFU (Least Frequently Used) Cache
- LRU Cache with TTL
- Multi-level cache systems
- Cache replacement algorithms

## LeetCode Similar Problems:
- [460. LFU Cache](https://leetcode.com/problems/lfu-cache/)
- [146. LRU Cache](https://leetcode.com/problems/lru-cache/) (this problem)
- [432. All O`one Data Structure](https://leetcode.com/problems/all-oone-data-structure/)
- [380. Insert Delete GetRandom O(1)](https://leetcode.com/problems/insert-delete-getrandom-o1/)
- [211. Design Add and Search Words Data Structure](https://leetcode.com/problems/design-add-and-search-words-data-structure/)

## Key Insights:
- **Combine data structures**: HashMap + LinkedList for different strengths
- **Dummy nodes**: Simplify edge case handling
- **Bi-directional pointers**: Enable efficient removal
- **Invariant maintenance**: Always keep HashMap and LinkedList consistent 