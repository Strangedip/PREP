import java.util.*;

/**
 * LFU (Least Frequently Used) Cache — LeetCode 460
 *
 * O(1) get and put using HashMap + frequency buckets (doubly linked lists).
 * Pairs with LRUCache.java for Staff-level design interviews.
 */
public class LFUCache {

    private static class Node {
        int key, value, freq;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }

    private static class DoublyLinkedList {
        Node head = new Node(0, 0);
        Node tail = new Node(0, 0);
        int size = 0;

        DoublyLinkedList() {
            head.next = tail;
            tail.prev = head;
        }

        void addToFront(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
            size++;
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            size--;
        }

        Node removeLRU() {
            if (size == 0) return null;
            Node lru = tail.prev;
            remove(lru);
            return lru;
        }
    }

    private final int capacity;
    private int minFreq;
    private final Map<Integer, Node> keyToNode = new HashMap<>();
    private final Map<Integer, DoublyLinkedList> freqToList = new HashMap<>();

    public LFUCache(int capacity) {
        this.capacity = capacity;
    }

    public int get(int key) {
        if (!keyToNode.containsKey(key)) {
            return -1;
        }
        Node node = keyToNode.get(key);
        increaseFreq(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) {
            return;
        }
        if (keyToNode.containsKey(key)) {
            Node node = keyToNode.get(key);
            node.value = value;
            increaseFreq(node);
            return;
        }
        if (keyToNode.size() >= capacity) {
            DoublyLinkedList minList = freqToList.get(minFreq);
            Node evicted = minList.removeLRU();
            keyToNode.remove(evicted.key);
            if (minList.size == 0) {
                freqToList.remove(minFreq);
            }
        }
        Node newNode = new Node(key, value);
        keyToNode.put(key, newNode);
        freqToList.computeIfAbsent(1, k -> new DoublyLinkedList()).addToFront(newNode);
        minFreq = 1;
    }

    private void increaseFreq(Node node) {
        int freq = node.freq;
        DoublyLinkedList list = freqToList.get(freq);
        list.remove(node);
        if (list.size == 0 && freq == minFreq) {
            minFreq++;
        }
        node.freq++;
        freqToList.computeIfAbsent(node.freq, k -> new DoublyLinkedList()).addToFront(node);
    }

    public static void main(String[] args) {
        LFUCache cache = new LFUCache(2);
        cache.put(1, 1);
        cache.put(2, 2);
        System.out.println(cache.get(1));  // 1
        cache.put(3, 3);                   // evicts 2
        System.out.println(cache.get(2));  // -1
        System.out.println(cache.get(3));  // 3
    }
}
