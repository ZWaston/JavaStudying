package concurrency.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class LockSupportDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread t1  = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程t1 开始运行...");
                //阻塞当前线程t1
                LockSupport.park(Thread.currentThread());
                try {
                    TimeUnit.SECONDS.sleep(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程t1 运行结束！！！");
            }
        });
        t1.start();
        System.out.println("主线程main 开始运行... ，此时线程t1 状态为： " + t1.getState());
        TimeUnit.SECONDS.sleep(2);
        System.out.println("主线程main 正在运行... ，此时线程t1 状态为： " + t1.getState());
        //唤醒线程t1
        LockSupport.unpark(t1);
        TimeUnit.SECONDS.sleep(6);
        //t1运行结束，主线程还未运行结束
        System.out.println("主线程main 运行结束！！！ ，此时线程t1 状态为： " + t1.getState());
    }
}
