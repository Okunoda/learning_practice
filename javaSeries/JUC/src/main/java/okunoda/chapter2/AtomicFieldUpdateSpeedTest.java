package okunoda.chapter2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.LongAdder;

/**
 * @author Okunoda 2024/4/2
 */
public class AtomicFieldUpdateSpeedTest {

    public static void main(String[] args) throws InterruptedException {
        Demo demo = new Demo();
        //4000ms
//        synchronizedMethod(demo);
        //2000ms
//        atomicFieldUpdater(demo);
        //100ms
        longAdderMethod(demo);
    }

    private static void longAdderMethod(Demo demo) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        demo.incrLongAdder();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            }, i + "").start();
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("-----costTime:" + (endTime - startTime) + " 毫秒");
        System.out.println("demo.getVal() = " + demo.getValByLong());
    }

    private static void atomicFieldUpdater(Demo demo) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        demo.incr(demo);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            }, i + "").start();
        }
        countDownLatch.await();
        long endTime = System.currentTimeMillis();
        System.out.println("-----costTime:" + (endTime - startTime) + " 毫秒");
        System.out.println("demo.getVal() = " + demo.getVal());
    }

    private static void synchronizedMethod(Demo demo) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(100);
        long startTime = System.currentTimeMillis();


        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                try {
                    for (int i1 = 0; i1 < 1000000; i1++) {
                        demo.incrSync();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    countDownLatch.countDown();
                }
            }, i + "").start();
        }
        countDownLatch.await();
        // 暂停秒
//        try { TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
        long endTime = System.currentTimeMillis();
        System.out.println("-----costTime:" + (endTime - startTime) + " 毫秒");
        System.out.println("demo.getVal() = " + demo.getVal());
    }
}

class Demo {
    LongAdder longAdder = new LongAdder();

    AtomicIntegerFieldUpdater<Demo> updater = AtomicIntegerFieldUpdater.newUpdater(Demo.class, "val");
    private volatile int val = 0;

    public void incrLongAdder() {
        longAdder.increment();
    }

    public long getValByLong() {
        return longAdder.sum();
    }

    public void incr(Demo demo) {

        updater.getAndIncrement(demo);
    }

    public synchronized Integer incrSync() {
        return ++val;
    }

    public Integer getVal() {
        return this.val;
    }
}