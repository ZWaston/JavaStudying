package concurrency;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * 一个死锁的例子，两个线程t1和t2，线程t1占用资源A而请求访问资源B，
 * 线程t2占用资源B而请求访问资源A，这就会使t1和t2相互等待释放锁，导致死锁。
 */
class DeadLock {
    // 锁A
    private String lockA = "A";
    // 锁B
    private String lockB = "B";

    private void deadLock(){
        // 第一条线程
        Thread t1 = new Thread(new Runnable(){
            @Override
            public void run() {
                synchronized (lockA) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lockB) {
                        System.out.println("线程1");
                    }
                }
            }
        });

        // 第二条线程
        Thread t2 = new Thread(new Runnable(){

            @Override
            public void run () {
                synchronized (lockB) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    synchronized (lockA) {
                        System.out.println("线程2");
                    }
                }
            }
        });

        t1.start();
        t2.start();
    }

    public static void main(String[] args) {
        new DeadLock().deadLock();

        //过5s打印线程状态信息
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //获取Java线程管理MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //不需要获取同步的monitor和synchronizer信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false,false);
        //遍历打印
        for(ThreadInfo threadInfo:threadInfos) {
            System.out.println("["+threadInfo.getThreadId()+"]"+
                    threadInfo.getThreadName() + "    state:" + threadInfo.getThreadState());
        }
    }

}