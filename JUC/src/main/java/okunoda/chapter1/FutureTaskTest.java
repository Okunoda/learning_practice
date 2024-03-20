package okunoda.chapter1;


import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * @author Okunoda 2024/3/18
 */
public class FutureTaskTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask<String> callableTestFutureTask = new FutureTask<>(new CallableTest());
//        callableTestFutureTask.run();//用它本身开启则会使得main线程会等待它执行完之后才会往下执行
        Thread thread = new Thread(callableTestFutureTask);
        thread.start();

        int i = 10;
        while(i-->0){
            Thread.sleep(100);
            System.out.println("\"main working\" = " + "main working");
        }
        System.out.println("callableTestFutureTask.get() = " + callableTestFutureTask.get());
        System.out.println("main 线程死亡");

    }
}

class CallableTest implements Callable<String>{
    @Override
    public String call() throws Exception {
        int i = 10;
        while (i-- > 0) {
            Thread.sleep(500);
            System.out.println("callable class invoked");
        }
        return "hello callable interface";
    }

}
