package okunoda.chapter1;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.print.Book;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author Okunoda 2024/3/19
 */
public class CompletableFutureTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        test1();
//        test2();
//        test3();
//        test4();
//        test5();//线程串行执行任务
//        test6();//比价案例


    }


    private static void test6() {
        @Data
        @AllArgsConstructor
        class Book{
            private String name;
            private Double getPrice(){
                // 暂停秒
                try {
                    TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
                return ThreadLocalRandom.current().nextDouble() * 2  + name.charAt(0);
            }
        }

        List<Book> bookList = Arrays.asList(new Book("jd"),new Book("tb"),new Book("PDD"));

        long startTime = System.currentTimeMillis();
        List<String> bookPriceList = bookList.stream()
                .map(book -> CompletableFuture.supplyAsync(() -> book.getName() + ">>>>>>>>" + book.getPrice()))
                .collect(Collectors.toList())
                .stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        for (String s : bookPriceList) {
            System.out.println(s);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("-----costTime:" + (endTime - startTime) + " 毫秒");



    }

    private static void test5() {

        ExecutorService pool = Executors.newFixedThreadPool(3);
        CompletableFuture.supplyAsync(()->{
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            System.out.println("\"线程执行第一个方法\" = " + "线程执行第一个方法");
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            return 1;
        },pool).thenApply(f->{
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            System.out.println("\"线程执行第二个方法\" = " + "线程执行第二个方法");
            // 暂停秒
//            int i = 10/0;
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            return ++f;
        }).thenApply(f -> {
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            System.out.println("\"第二个thenApply执行了！！\" = " + "第二个thenApply执行了！！");
            // 暂停秒
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            return ++f;
        }).handle((f,v)->{
            System.out.println("v = " + v);
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            System.out.println("\"线程执行第三个方法\" = " + "线程执行第三个方法");
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
            return ++f;
        }).whenComplete((result,exception)->{
            if(exception == null){
                System.out.println("\"执行结果没有异常，结果为\"+result = " + result);
            }else{
                System.out.println("\"执行结果异常\" = " + "执行结果异常"+result);
            }
            try { TimeUnit.MILLISECONDS.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
        }).exceptionally(e ->{
            System.out.println("e.getCause() = " + e.getCause());
            System.out.println("e.getMessage() = " + e.getMessage());
            return null;
        });
        System.out.println("\"主线程结束\" = " + "主线程结束");
    }

    private static void test4() throws ExecutionException, InterruptedException {
        ExecutorService threadP = Executors.newFixedThreadPool(3);
//        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
//            return 123;
//                });
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            int i = 10;
            while (i-- > 0) {
                System.out.println("线程内循环次数   " + i);
                // 暂停秒
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return "线程执行完成";
        }, threadP).thenApply((f)->{
            return "123";
        });
        System.out.println("主线程阻塞》》》》》》》》》》》》》》》》》》");
        // 暂停秒
        try {
            TimeUnit.SECONDS.sleep(2); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("主线程阻塞完毕》》》》》》》》》》》》");
        System.out.println("future.complete(\"complete方法执行了\") = " + future.complete("complete方法执行了"));
        System.out.println("future.join() = " + future.join());

        // 暂停秒
        try { TimeUnit.SECONDS.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }
        System.out.println("10秒之后的 future.join() = " + future.get());
        System.out.println("主线程结束》》》》》》》》》》》》》》》》》》》》");
        threadP.shutdown();
    }

    private static void test3() {
        ExecutorService executorService = Executors.newFixedThreadPool(3);

        CompletableFuture.runAsync(()->{
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            // 暂停秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) { e.printStackTrace(); }

        },executorService).whenComplete((value,exception)->{
            System.out.println("value = " + value);
            if(exception == null){
                System.out.println(Thread.currentThread().getName() + "没有异常");
            }
        }).exceptionally((e) -> {
            System.out.println(e.getCause() + e.getMessage());
            return null;
        });

        System.out.println("\"main线程死亡\" = " + "main线程死亡");
        executorService.shutdown();
    }

    private static void test2() {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        CompletableFuture.supplyAsync(()->{
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            // 暂停秒
            try {
                TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            throw new RuntimeException();
//            return 1;
        }).whenComplete((result,exception)->{
            if(exception == null){
                System.out.println("\"线程执行完成\" = " + "线程执行完成，返回结果为：" + result);
            }
        }).exceptionally((e)->{
            System.out.println("e.getCause() + e.getMessage() = " + e.getCause() + e.getMessage());
            return null;
        });

        System.out.println("\"main线程执行中。。。\" = " + "main线程执行中。。。");
        threadPool.shutdown();
    }


    private static void test1() throws ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(3);
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            // 暂停秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Thread.currentThread().getName() = " + Thread.currentThread().getName());
            // 暂停秒
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return "hello async thread !!!!";
        },threadPool);



        System.out.println("main线程进行其他操作");
        System.out.println("future.get() = " + future.get());
        System.out.println("future2.get() = " + future2.get());

        threadPool.shutdown();
    }
}
