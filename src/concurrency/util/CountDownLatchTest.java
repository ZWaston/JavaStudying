package concurrency.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 并发工具类——闭锁的使用
 */
public class CountDownLatchTest {
    public static void main(String[] args) {
        //初始化闭锁，并设置资源个数
        CountDownLatch latch = new CountDownLatch(2);

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                //加载资源1的代码,假设需要5s
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("资源1加载完毕！！！");
                //资源1加载完毕之后，闭锁 -1
                latch.countDown();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                //加载资源2的代码,假设需要7s
                try {
                    TimeUnit.SECONDS.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("资源2加载完毕！！！");
                //资源1加载完毕之后，闭锁 -1
                latch.countDown();
            }
        });

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                //t3线程必须等待所有资源加载完毕之后才能执行
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //当闭锁数量为0时，线程才能从await()返回，执行接下来的任务
                System.out.println("地图资源加载完毕！！！");
            }
        });

        t1.start();
        t2.start();
        t3.start();
    }
}
