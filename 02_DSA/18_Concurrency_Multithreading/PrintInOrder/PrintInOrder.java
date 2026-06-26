import java.util.concurrent.CountDownLatch;

/**
 * Print In Order — LeetCode 1114
 * Threads call first(), second(), third() — output must be "123".
 */
public class PrintInOrder {
    private volatile int stage = 1;

    public void first(Runnable printFirst) throws InterruptedException {
        while (stage != 1) Thread.yield();
        printFirst.run();
        stage = 2;
    }

    public void second(Runnable printSecond) throws InterruptedException {
        while (stage != 2) Thread.yield();
        printSecond.run();
        stage = 3;
    }

    public void third(Runnable printThird) throws InterruptedException {
        while (stage != 3) Thread.yield();
        printThird.run();
    }

    /** Alternative: CountDownLatch approach */
    static class PrintInOrderLatch {
        private final CountDownLatch latch1 = new CountDownLatch(1);
        private final CountDownLatch latch2 = new CountDownLatch(1);

        public void first(Runnable printFirst) {
            printFirst.run();
            latch1.countDown();
        }

        public void second(Runnable printSecond) throws InterruptedException {
            latch1.await();
            printSecond.run();
            latch2.countDown();
        }

        public void third(Runnable printThird) throws InterruptedException {
            latch2.await();
            printThird.run();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        PrintInOrder p = new PrintInOrder();
        Runnable r1 = () -> System.out.print("1");
        Runnable r2 = () -> System.out.print("2");
        Runnable r3 = () -> System.out.print("3");

        Thread t2 = new Thread(() -> { try { p.second(r2); } catch (InterruptedException e) {} });
        Thread t3 = new Thread(() -> { try { p.third(r3); } catch (InterruptedException e) {} });
        t2.start();
        t3.start();
        p.first(r1);
        t2.join();
        t3.join();
        System.out.println();
    }
}
