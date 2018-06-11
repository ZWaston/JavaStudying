package concurrency.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * 利用StampedLock实现 自身是非线程安全的HashMap 的线程安全操作
 */
public class StampedLockDemo {
    Map<String,String> map = new HashMap<>();
    private StampedLock lock = new StampedLock();

    public void put(String key, String value){
        //获得StampedLock的写锁，并返回一个标记位stamp
        long stamp = lock.writeLock();
        try {
            map.put(key, value);
        } finally {
            //释放写锁
            lock.unlockWrite(stamp);
        }
    }
    public String get(String key) throws InterruptedException {
        //获得StampedLock的读锁，并返回一个标记位stamp，此读锁是悲观锁，
        //即读的时候会阻塞写线程。
        long stamp = lock.readLock();
        try {
            return map.get(key);
        } finally {
            //释放读锁
            lock.unlockRead(stamp);
        }
    }

    //还实现了乐观锁，解决了写锁饥饿的问题
    public String readWithOptimisticLock(String key) {
        //获取乐观锁，并返回标记位stamp，乐观锁的读是不会阻塞写线程的
        long stamp = lock.tryOptimisticRead();
        String value = map.get(key);
        //如果读的时候发生了写（通过stamp判断），则进行重读，这里重读代码使用的是
        //悲观锁（会阻塞写线程），也可以使用乐观锁（这样就类似CAS循环了）。
        if(!lock.validate(stamp)) {
            stamp = lock.readLock();
            try {
                return map.get(key);
            } finally {
                lock.unlock(stamp);
            }
        }
        return value;
    }
}