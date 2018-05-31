package concurrency;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 创建了两个线程WaitThread和NotifyThread，
 * 前者检查flag是否为false，若是，进行后续操作，否则在lock上等待；
 * 后者在睡眠一段时间后对lock进行通知
 */
public class WaitNotify {
    //状态标识位，用来表示是否满足线程运行条件
    static boolean flag = true;
    //锁对象
    static Object lock = new Object();

    public static void main(String[] args) throws Exception{
        Thread waitThread = new Thread(new Wait(),"WaitThread");
        waitThread.start();
        //过1s中再启动Notify线程
        TimeUnit.SECONDS.sleep(1);
        Thread notifyThread = new Thread(new Notify(),"NotifyThread");
        notifyThread.start();
    }

    static class Wait implements Runnable {

        @Override
        public void run() {
            synchronized (lock) {
                //当条件不满足时，继wait，同时会释放lock的锁
                while(flag) {
                    try {
                        System.out.println(Thread.currentThread() + " flag is true. wait @"
                                + new SimpleDateFormat("HH:mm:ss").format(new Date()) );
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //当条件满足时，执行对应工作
                System.out.println(Thread.currentThread() + " flag is false. running @"
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()) );
            }
        }
    }

    static class Notify implements Runnable {

        @Override
        public void run() {
            //第一个同步块
            synchronized (lock) {
                //获取lock的锁之后，会对waitThread进行通知，但是不会释放锁,
                //直到当前线程释放了lock的锁后，waitThread才会从wait方法返回
                System.out.println(Thread.currentThread() + " hold block. notify @"
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()) );
                lock.notifyAll();
                flag = false;
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //此线程进入等待状态5s，确保WaitThread线程先获得锁
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //第二个同步块，继续加锁
            synchronized (lock) {
                System.out.println(Thread.currentThread() + " hold block again. sleep @"
                        + new SimpleDateFormat("HH:mm:ss").format(new Date()) );
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
