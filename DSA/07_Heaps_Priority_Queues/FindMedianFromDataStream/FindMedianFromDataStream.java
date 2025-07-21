import java.util.*;

/**
 * LeetCode 295: Find Median from Data Stream
 * 
 * Design a data structure that supports the following two operations:
 * - addNum(int num) - Add a number to the data structure
 * - findMedian() - Return the median of all elements so far
 * 
 * Time Complexity:
 * - addNum: O(log n)
 * - findMedian: O(1)
 * Space Complexity: O(n)
 */
public class FindMedianFromDataStream {
    
    static class MedianFinder {
        // Max heap for smaller half
        private PriorityQueue<Integer> maxHeap;
        // Min heap for larger half
        private PriorityQueue<Integer> minHeap;
        
        public MedianFinder() {
            maxHeap = new PriorityQueue<>((a, b) -> b - a); // Max heap
            minHeap = new PriorityQueue<>(); // Min heap
        }
        
        public void addNum(int num) {
            // Add to max heap first
            maxHeap.offer(num);
            
            // Move the largest from max heap to min heap
            minHeap.offer(maxHeap.poll());
            
            // Balance the heaps - max heap should have at most 1 more element
            if (maxHeap.size() < minHeap.size()) {
                maxHeap.offer(minHeap.poll());
            }
        }
        
        public double findMedian() {
            if (maxHeap.size() > minHeap.size()) {
                return maxHeap.peek();
            }
            return (maxHeap.peek() + minHeap.peek()) / 2.0;
        }
    }
    
    public static void main(String[] args) {
        MedianFinder medianFinder = new MedianFinder();
        
        // Test case 1
        medianFinder.addNum(1);
        medianFinder.addNum(2);
        System.out.println("Median after adding 1, 2: " + medianFinder.findMedian()); // 1.5
        
        medianFinder.addNum(3);
        System.out.println("Median after adding 3: " + medianFinder.findMedian()); // 2.0
        
        // Test case 2
        MedianFinder mf2 = new MedianFinder();
        mf2.addNum(6);
        System.out.println("Median: " + mf2.findMedian()); // 6.0
        mf2.addNum(10);
        System.out.println("Median: " + mf2.findMedian()); // 8.0
        mf2.addNum(2);
        System.out.println("Median: " + mf2.findMedian()); // 6.0
        mf2.addNum(6);
        System.out.println("Median: " + mf2.findMedian()); // 6.0
        mf2.addNum(5);
        System.out.println("Median: " + mf2.findMedian()); // 6.0
    }
} 