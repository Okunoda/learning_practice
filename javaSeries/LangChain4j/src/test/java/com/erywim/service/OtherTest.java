package com.erywim.service;

import org.junit.Test;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * @Date 2026/1/2
 */
public class OtherTest {
    @Test
    public void testCyclicBarrier() throws InterruptedException, BrokenBarrierException {
        // 创建一个需要3个线程的屏障
        CyclicBarrier barrier = new CyclicBarrier(4, () -> {
            System.out.println("所有线程已到达屏障，开始下一步操作");
        });

        // 创建并启动3个线程
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " 到达屏障");
                    barrier.await(); // 等待其他线程
                    System.out.println(Thread.currentThread().getName() + " 继续执行");
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        barrier.await();
        System.out.println("next");
    }
}
