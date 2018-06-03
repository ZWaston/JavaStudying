package concurrency.util;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程间交换数据——Exchanger
 */
public class ExchangerTest {
    public static void main(String[] args) {
        //创建Exchanger，并指定交换的数据类型是String
        Exchanger<String> exchanger = new Exchanger<>();
        //利用Executors工具类生成一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(2);

        pool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //将 A 记录的流水 交换给 另一个线程，并当2个线程都执行了exchange(str)方法后，会将str传递给对方
                    String B = exchanger.exchange("银行流水A");
                    //打印从另一个线程B获得的数据
                    System.out.println("B的记录是:" + B);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        pool.execute(new Runnable() {
            @Override
            public void run() {
                String B = "银行流水B";
                try {
                    //获得另外一个线程传递过来的数据，并把自己的
                    String A = exchanger.exchange(B);
                    //打印从另一个线程B获得的数据
                    System.out.println("A的记录是:" + A);
                    //若A和B记录的数据一致，则提交，否则失败
                    if(A.equals(B)) {
                        System.out.println("匹配，提交数据库！！！");
                    } else {
                        System.out.println("匹配失败，请再次核对！！！");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        pool.shutdown();
    }
}
