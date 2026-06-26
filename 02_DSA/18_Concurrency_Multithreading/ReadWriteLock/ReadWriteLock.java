import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Read-Write Lock demo — multiple concurrent readers OR one writer.
 */
public class ReadWriteLockDemo {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int value = 0;

    public int read() {
        lock.readLock().lock();
        try {
            return value;
        } finally {
            lock.readLock().unlock();
        }
    }

    public void write(int newValue) {
        lock.writeLock().lock();
        try {
            value = newValue;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ReadWriteLockDemo demo = new ReadWriteLockDemo();

        Thread writer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                demo.write(i);
                System.out.println("Wrote: " + i);
                try { Thread.sleep(100); } catch (InterruptedException e) { break; }
            }
        });

        Thread reader1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Reader1: " + demo.read());
                try { Thread.sleep(50); } catch (InterruptedException e) { break; }
            }
        });

        Thread reader2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Reader2: " + demo.read());
                try { Thread.sleep(50); } catch (InterruptedException e) { break; }
            }
        });

        writer.start();
        reader1.start();
        reader2.start();
        writer.join();
        reader1.join();
        reader2.join();
    }
}
