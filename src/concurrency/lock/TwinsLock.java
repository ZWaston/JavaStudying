package concurrency.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 要求实现一个自定义同步器:同一时刻最多只允许2个线程同时访问
 * 1.确定访问模式，共享式
 * 2.定义资源数量，为2
 */
public class TwinsLock implements Lock{
    Sync sync = new Sync(2);//资源数为2

    public static final class Sync extends AbstractQueuedSynchronizer {
        //自定义Sync，需要重写tryAcquireShared 和 tryReleaseShared
        public Sync(int count) {
            if(count <= 0) {
                throw new IllegalArgumentException("count must larger than zero");
            }
            setState(count);
        }

        @Override
        protected int tryAcquireShared(int reduceCount) {
            //用for循环，是因为是共享锁（如读锁），只有资源数不够（返回失败）、CAS获取成功（返回成功），才会返回。
            //若资源数够，但CAS失败（同一时刻只能有一个线程成功），由于是共享锁，所以需要循环再次获取
            //不用while（true）的原因是，其效率较低
            for(;;){
                int current = getState();//获取同步状态值
                int newCount = current - reduceCount;
                //若newCount < 0，直接返回newCount，负值表示获取失败；否则尝试CAS获取
                if(newCount < 0 || compareAndSetState(current,newCount)) {
                    return newCount;
                }
            }

        }

        @Override
        protected boolean tryReleaseShared(int returnCount) {
            for(;;) {
                int current = getState();//获取同步状态值
                int newCount = current + returnCount;
                if(compareAndSetState(current,newCount)) {
                    return true;
                }
            }
        }

        Condition newCondition() {
            return new ConditionObject();
        }
    }

    //将Sync代理到Lock上,使用Sync的模板方法，会调用重写的方法
    @Override
    public void lock() {
        sync.acquireShared(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquireShared(1) > 0;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(time));
    }

    @Override
    public void unlock() {
        sync.releaseShared(1);
    }

    @Override
    public Condition newCondition() {
        return sync.newCondition();
    }

}

/**
 * 测试使用上述的同步器，线程Worker执行过程中获得锁后睡眠一秒，然后打印当前线程名称，随后又睡眠一秒
 * 可以看到结果，每1s最多只有2个线程获得锁
 */
class TwinsLockTest {

    public void test() throws InterruptedException {
        final Lock lock = new TwinsLock();
        class Worker extends Thread {
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        try {
                            TimeUnit.SECONDS.sleep(1);
                            System.out.println(Thread.currentThread().getName());
                            TimeUnit.SECONDS.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        }
        // 启动10个线程
        for (int i = 0; i < 10; i++) {
            Worker w = new Worker();
            w.setDaemon(true);
            w.start();
        }
        // 每隔1秒换行
        for (int i = 0; i < 10; i++) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TwinsLockTest twinsLockTest = new TwinsLockTest();
        twinsLockTest.test();
    }
}
