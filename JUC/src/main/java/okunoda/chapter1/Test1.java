package okunoda.chapter1;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/**
 * @author Okunoda 2024/3/18
 */
public class Test1 {
    public static void main(String[] args) {
        new Thread(()->{},"t1").start();
        Object o = new Object();
        new Thread(()->{
            synchronized (o) {

            }
        },"t2");

        Thread t3 = new Thread(() -> {
            Thread thread = Thread.currentThread();
            System.out.println(thread.getName() + ":    Thread.currentThread().isDaemon() = " + thread.isDaemon());

        }, "t3");
        t3.setDaemon(true);
        t3.start();

        Thread t4 = new Thread(() -> {
            Thread thread = Thread.currentThread();

            while(true){
                try {
                    Thread.sleep(2000);
                    System.out.println(thread.getName());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }, "t4");
        t4.start();

        System.out.println("\"main 结束\" = " + "main 结束");

    }


}
