package concurrency.lock;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 使用读写锁，来保证本身是非线程安全的HashMap 的操作是线程安全的。
 */
public class ReentrantReadWriteLockTest {
    private static final Map<String, Object> map = new HashMap<String, Object>();
    private static final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    //获得读锁r
    private static final Lock r   = rwl.readLock();
    //获得写锁w
    private static final Lock  w   = rwl.writeLock();
    //利用读锁，实现线程安全的get：获取一个key的value
    public static final Object get(String key) {
        r.lock();
        try {
            return map.get(key);
        } finally {
            r.unlock();
        }
    }
    //利用写锁，实现线程安全的put：设置key对于的value，并返回旧的value
    public static final Object put(String key, Object value) {
        w.lock();
        try {
            return map.put(key, value);
        } finally {
            w.unlock();
        }
    }
    //利用写锁，实现线程安全的clear：情况map的所有内容
    public static final void clear() {
        w.lock();
        try {
            map.clear();
        } finally {
            w.unlock();
        }
    }
}
