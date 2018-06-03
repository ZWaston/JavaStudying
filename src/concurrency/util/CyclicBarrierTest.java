package concurrency.util;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/**
 * 并发工具类的使用——同步屏障
 */
public class CyclicBarrierTest {
    public static void main(String[] args) {
        //创建同步屏障对象，设定 需要等待的线程个数 和 打开屏障时需要先执行的任务
        CyclicBarrier barrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                //当所有线程准备完毕之后，优先执行此任务
            }
        });
        //启动3条线程
        for(int i = 0; i < 3; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                        //等待，每执行一次barrier.wait()，同步屏障数量-1，直到为0，打开屏障
                        barrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    //任务
                    System.out.println("执行任务完毕！！！");
                }
            }).start();
        }
    }
}
