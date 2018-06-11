package concurrency.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Lock的基本使用测试
 */
public class LockUseCase {
    public static void main(String[] args) {
        Lock lockA = new ReentrantLock();
        Lock lockB = new ReentrantLock();
        //构造线程1，获得锁B --> 释放锁B后等待10s，尝试获取锁A --> 获取锁A后，释放锁A
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                lockB.lock();
                System.out.println("线程t1: 获得锁B！");
                System.out.println("线程t1: 获得锁B后正在运行...");
                //释放锁B，让线程2能获得锁B运行
                lockB.unlock();
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("线程t1:释放锁B，尝试获取锁A...");
                lockA.lock();
                System.out.println("线程t1:获取锁A后正在运行...");
                lockA.unlock();
            }
        });
        t1.start();

        try {
            //使线程t1先运行
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //构造线程t2，尝试获取锁B，等待t1释放锁B --> 获取锁B之后获取锁A --> 运行5s后释放锁A、锁B
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                lockB.lock();
                System.out.println("线程t2: 获得锁B！");
                //获取锁B之后立马尝试获取锁A
                lockA.lock();
                System.out.println("线程t2: 获得锁A，正在运行...");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lockA.unlock();
                lockB.unlock();
            }
        });
        t2.start();
    }
}
