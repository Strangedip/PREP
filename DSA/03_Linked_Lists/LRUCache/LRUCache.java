import java.util.*;

/**
 * LRU (Least Recently Used) Cache Implementation
 * 
 * Design a data structure that follows the constraints of a Least Recently Used (LRU) cache.
 * 
 * Implement the LRUCache class:
 * - LRUCache(int capacity) Initialize the LRU cache with positive size capacity.
 * - int get(int key) Return the value of the key if the key exists, otherwise return -1.
 * - void put(int key, int value) Update the value of the key if exists. Otherwise, 
 *   add the key-value pair to the cache. If the number of keys exceeds the capacity 
 *   from this operation, evict the least recently used key.
 * 
 * The functions get and put must each run in O(1) average time complexity.
 */
public class LRUCache {
    
    // Doubly linked list node
    private class Node {
        int key;
        int value;
        Node prev;
        Node next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head;  // Dummy head (most recently used)
    private final Node tail;  // Dummy tail (least recently used)
    
    /**
     * Initialize LRU Cache with given capacity
     * Time Complexity: O(1)
     * Space Complexity: O(capacity)
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        
        // Create dummy head and tail nodes
        this.head = new Node(-1, -1);
        this.tail = new Node(-1, -1);
        head.next = tail;
        tail.prev = head;
    }
    
    /**
     * Get value for given key
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        
        // Move accessed node to head (mark as most recently used)
        moveToHead(node);
        return node.value;
    }
    
    /**
     * Put key-value pair into cache
     * Time Complexity: O(1)
     * Space Complexity: O(1)
     */
    public void put(int key, int value) {
        Node node = cache.get(key);
        
        if (node != null) {
            // Update existing node
            node.value = value;
            moveToHead(node);
        } else {
            // Add new node
            Node newNode = new Node(key, value);
            
            if (cache.size() >= capacity) {
                // Remove least recently used node (from tail)
                Node lru = removeTail();
                cache.remove(lru.key);
            }
            
            // Add new node to head and cache
            addToHead(newNode);
            cache.put(key, newNode);
        }
    }
    
    /**
     * Move node to head (mark as most recently used)
     */
    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }
    
    /**
     * Add node right after head
     */
    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        
        head.next.prev = node;
        head.next = node;
    }
    
    /**
     * Remove a node from doubly linked list
     */
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    /**
     * Remove node from tail (least recently used)
     */
    private Node removeTail() {
        Node lru = tail.prev;
        removeNode(lru);
        return lru;
    }
    
    /**
     * Get current size of cache
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Check if cache is empty
     */
    public boolean isEmpty() {
        return cache.isEmpty();
    }
    
    /**
     * Display current state of cache (for debugging)
     */
    public void display() {
        System.out.print("LRU Cache: [");
        Node current = head.next;
        while (current != tail) {
            System.out.print("(" + current.key + "," + current.value + ")");
            current = current.next;
            if (current != tail) {
                System.out.print(" -> ");
            }
        }
        System.out.println("]");
    }
    
    // Test cases and demonstration
    public static void main(String[] args) {
        System.out.println("=== LRU Cache Test Cases ===\n");
        
        // Test Case 1: Basic operations
        System.out.println("Test Case 1: Basic Operations");
        LRUCache lru1 = new LRUCache(2);
        
        lru1.put(1, 1);
        lru1.display(); // [(1,1)]
        
        lru1.put(2, 2);
        lru1.display(); // [(2,2) -> (1,1)]
        
        System.out.println("get(1): " + lru1.get(1)); // 1, moves (1,1) to head
        lru1.display(); // [(1,1) -> (2,2)]
        
        lru1.put(3, 3); // Evicts (2,2)
        lru1.display(); // [(3,3) -> (1,1)]
        
        System.out.println("get(2): " + lru1.get(2)); // -1 (not found)
        
        lru1.put(4, 4); // Evicts (1,1)
        lru1.display(); // [(4,4) -> (3,3)]
        
        System.out.println("get(1): " + lru1.get(1)); // -1 (not found)
        System.out.println("get(3): " + lru1.get(3)); // 3
        System.out.println("get(4): " + lru1.get(4)); // 4
        System.out.println();
        
        // Test Case 2: Update existing key
        System.out.println("Test Case 2: Update Existing Key");
        LRUCache lru2 = new LRUCache(2);
        
        lru2.put(1, 1);
        lru2.put(2, 2);
        lru2.display(); // [(2,2) -> (1,1)]
        
        lru2.put(1, 10); // Update value for key 1
        lru2.display(); // [(1,10) -> (2,2)]
        
        System.out.println("get(1): " + lru2.get(1)); // 10
        System.out.println("get(2): " + lru2.get(2)); // 2
        System.out.println();
        
        // Test Case 3: Single capacity
        System.out.println("Test Case 3: Single Capacity");
        LRUCache lru3 = new LRUCache(1);
        
        lru3.put(1, 1);
        lru3.display(); // [(1,1)]
        
        lru3.put(2, 2); // Evicts (1,1)
        lru3.display(); // [(2,2)]
        
        System.out.println("get(1): " + lru3.get(1)); // -1
        System.out.println("get(2): " + lru3.get(2)); // 2
        System.out.println();
        
        // Test Case 4: Large capacity
        System.out.println("Test Case 4: Larger Capacity");
        LRUCache lru4 = new LRUCache(3);
        
        lru4.put(1, 1);
        lru4.put(2, 2);
        lru4.put(3, 3);
        lru4.display(); // [(3,3) -> (2,2) -> (1,1)]
        
        System.out.println("get(2): " + lru4.get(2)); // 2, moves to head
        lru4.display(); // [(2,2) -> (3,3) -> (1,1)]
        
        lru4.put(4, 4); // Evicts (1,1)
        lru4.display(); // [(4,4) -> (2,2) -> (3,3)]
        
        System.out.println("get(1): " + lru4.get(1)); // -1
        System.out.println("get(3): " + lru4.get(3)); // 3
        System.out.println("get(2): " + lru4.get(2)); // 2
        System.out.println("get(4): " + lru4.get(4)); // 4
        
        // Performance test
        performanceTest();
    }
    
    /**
     * Performance test for LRU Cache
     */
    private static void performanceTest() {
        System.out.println("\n=== Performance Test ===");
        
        int capacity = 10000;
        int operations = 100000;
        LRUCache cache = new LRUCache(capacity);
        
        long startTime = System.nanoTime();
        
        // Perform mixed operations
        for (int i = 0; i < operations; i++) {
            if (i % 3 == 0) {
                // Get operation
                cache.get(i % capacity);
            } else {
                // Put operation
                cache.put(i, i * 2);
            }
        }
        
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
        
        System.out.println("Performed " + operations + " operations in " + duration + " ms");
        System.out.println("Average time per operation: " + (duration / operations) + " ms");
        System.out.println("Current cache size: " + cache.size());
    }
}

/**
 * Alternative Implementation using LinkedHashMap
 * This is a simpler but less educational approach
 */
class LRUCacheSimple extends LinkedHashMap<Integer, Integer> {
    private final int capacity;
    
    public LRUCacheSimple(int capacity) {
        super(capacity, 0.75f, true); // Access order
        this.capacity = capacity;
    }
    
    public int get(int key) {
        return super.getOrDefault(key, -1);
    }
    
    public void put(int key, int value) {
        super.put(key, value);
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, Integer> eldest) {
        return size() > capacity;
    }
} 