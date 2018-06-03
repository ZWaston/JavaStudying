package concurrency.util;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 同步工具使用——信号量
 */
public class SemaphoreTest {
    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(3);

        long start = System.currentTimeMillis();
        //开启num条线程
        int num = 9;
        Thread[] threads = new Thread[num];
        for(int i = 0; i < num; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    //获取资源，若资源被用光，则会被阻塞，直到有线程归还资源
                    try {
                        semaphore.acquire();
                        //任务代码，假设需要执行5s
                        TimeUnit.SECONDS.sleep(5);
                        //释放资源
                        semaphore.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            threads[i].start();
        }
        //是主线程在num条线程执行完毕之后，在输出运行时间
        for(int i = 0; i < num; i++) {
            threads[i].join();
        }
        long end = System.currentTimeMillis();
        //观察输出时间
        System.out.println((end - start)/1000.0 + "s");
    }
}
