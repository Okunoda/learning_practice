package okunoda.chapter2;

import java.util.concurrent.CompletableFuture;

import static okunoda.chapter1.CompletableFutureTest.test1;

/**
 * @author Okunoda 2024/3/20
 */
public class SynchronizedTest {
    public static void main(String[] args) {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("\"hello completable\" = " + "hello completable");
            return "123";
        });
    }
    public synchronized void method1() {
        System.out.println("hello method1");
    }

    public void method2() {
        Object obj = new Object();
        synchronized (obj) {
            System.out.println("\"hello\" = " + "hello");
            int i = 10 / 0;
        }
    }
}
