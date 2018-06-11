package collection.unsafe;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * 模拟线程不安全的HashMap死循环
 * 注意：JDK1.7之前的put操作会造成死循环，但是JDK1.8之后不会造成死循环，但会造成其他问题，如数据丢失。
 */
public class HashMapTest1 {
    public static void main(String[] args) {
        final HashMap<String, String> map = new HashMap<>(2);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //JDK1.7的情况下，创建10000个线程对HashMap进行增加元素，扩容时会引起链表形成环形数据结构，造成死循环
                for(int i = 0; i < 10000; i++) {
                    Thread ti = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String key = UUID.randomUUID().toString();
                            String value = "value";
                            map.put(key, value);
                            System.out.println(Thread.currentThread().getName() + "在运行..." + "  插入的数据<key,value>为: "
                            + key + ":" + value);
                        }
                    }, "线程：" + i);
                    ti.start();
                    try {
                        ti.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, "线程t");
        t.start();
        //主线程等待t线程完成后才继续执行
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("插入数据后的数据：");
        int sum = 0;
        Iterator<Map.Entry<String,String> > itr = map.entrySet().iterator();
        while(itr.hasNext()) {
            sum++;
            Map.Entry<String,String> entry = itr.next();
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("map对的总个数为: " + sum);
    }

}
