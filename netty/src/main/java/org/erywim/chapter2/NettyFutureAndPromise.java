package org.erywim.chapter2;

import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Erywim 2024/7/15
 */
@Slf4j
public class NettyFutureAndPromise {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        nettyFuture();
        nettyPromise();
    }

    private static void nettyPromise() throws ExecutionException, InterruptedException {

        EventLoop next = new NioEventLoopGroup().next();
        DefaultPromise<Integer> promise = new DefaultPromise<>(next);
        new Thread(() -> {
            log.info("开始计算");
            // 暂停秒
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

            try {
                int i = 1/0;

            }catch (Exception e){
                promise.setFailure(e);
            }
            promise.setSuccess(1);
        },"t1").start();

        log.info("主线程继续运行");
        Integer i = promise.get();
        System.out.println(promise.cause());
        log.info("线程执行结果{}",i);
    }


    private static void nettyFuture() throws InterruptedException, ExecutionException {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoop next = group.next();

        Future<Integer> submit = next.submit(() -> {
            // 暂停秒
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            log.info("等待callable 执行。。。。");
            return 70;
        });
        log.info("main线程执行中");
        Integer i = submit.get();
        log.info("callable 执行结果{}", i);
        group.shutdownGracefully();
    }
}
