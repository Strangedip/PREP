import java.util.LinkedList;
import java.util.Queue;

/**
 * Producer-Consumer using wait/notify and BlockingQueue pattern.
 */
public class ProducerConsumer {

    /** Manual implementation with synchronized block */
    static class BoundedBuffer {
        private final Queue<Integer> queue = new LinkedList<>();
        private final int capacity;

        BoundedBuffer(int capacity) { this.capacity = capacity; }

        public synchronized void produce(int item) throws InterruptedException {
            while (queue.size() == capacity) wait();
            queue.offer(item);
            notifyAll();
        }

        public synchronized int consume() throws InterruptedException {
            while (queue.isEmpty()) wait();
            int item = queue.poll();
            notifyAll();
            return item;
        }
    }

    /** Using java.util.concurrent.BlockingQueue */
    static void runWithBlockingQueue() throws InterruptedException {
        java.util.concurrent.BlockingQueue<Integer> queue =
                new java.util.concurrent.ArrayBlockingQueue<>(5);

        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    int item = queue.take();
                    System.out.println("Consumed: " + item);
                    Thread.sleep(200);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

    public static void main(String[] args) throws InterruptedException {
        runWithBlockingQueue();
    }
}
