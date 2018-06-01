package threadstudying.practice;

import java.util.LinkedList;

/**
 * 多线程经典问题：生产者/消费者模型
 * 实现方式：使用wait、notify方法
 * 有一个共享资源仓库（大小有限），生产者一直在生成物品，但是一旦仓库满了，生产者停止生产，直到仓库资源被消费，才继续生产；
 * 消费者一直在消耗物品，但是当仓库没有资源时，停止消耗，直到生产者继续生产
 */
public class Storage {
    //仓库的最大存储量
    private final int MAX_SIZE = 5;
    //仓库存储的载体,LinkedList不是线程安全的类
    private LinkedList<Object> list = new LinkedList<Object>();

    // 生产产品
    public void produce(String producer)
    {
        synchronized (list)
        {
            // 如果仓库已满
            while (list.size() == MAX_SIZE)
            {
                System.out.println("仓库已满，【"+producer+"】： 暂时不能执行生产任务!");
                try
                {
                    // 由于条件不满足，生产阻塞
                    list.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            // 模拟生产产品
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            list.add(new Object());
            System.out.println("【"+producer+"】：生产了一个产品\t【现仓储量为】:" + list.size());
            //通知消费者，仓库已经生产了一个物品，仓库不是为空
            list.notifyAll();
        }
        // 获取锁之后，过一段时间后才能竞争锁
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 消费产品
    public void consume(String consumer)
    {
        synchronized (list)
        {
            //如果仓库存储量不足
            while (list.size()==0)
            {
                System.out.println("仓库已空，【"+consumer+"】： 暂时不能执行消费任务!");
                try
                {
                    // 由于条件不满足，消费阻塞
                    list.wait();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }

            //模拟消费者消费
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            list.remove();
            System.out.println("【"+consumer+"】：消费了一个产品\t【现仓储量为】:" + list.size());
            //通知生产者消费了一个产品，仓库此时未满
            list.notifyAll();
        }

        // 获取锁之后，过一段时间后才能竞争锁
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //共享资源
        Storage storage = new Storage();
        //创建多个生产者
        Thread p1 = new Thread(new Producer(storage,"生产者1"));
        Thread p2 = new Thread(new Producer(storage,"生产者2"));
        Thread p3 = new Thread(new Producer(storage,"生产者3"));
        //创建多个消费者
        Thread c1 = new Thread(new Consumer(storage,"消费者1"));
        Thread c2 = new Thread(new Consumer(storage,"消费者2"));

        p1.start();
        p2.start();
        p3.start();
        c1.start();
        c2.start();
    }
}

class Producer implements Runnable {
    //公共资源仓库
    private Storage storage;
    //生产者的名字
    private String name;

    public Producer(Storage storage, String name) {
        this.storage = storage;
        this.name = name;
    }

    @Override
    public void run() {
        while(true){
            storage.produce(name);
        }
    }
}

class Consumer implements Runnable {
    //公共资源仓库
    private Storage storage;
    //消费者的名字
    private String name;

    public Consumer(Storage storage, String name) {
        this.storage = storage;
        this.name = name;
    }

    @Override
    public void run() {
        while(true){
            storage.consume(name);
        }
    }
}
