package concurrency.threadpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultThreadPool<Job extends Runnable> implements ThreadPool<Job> {
    //线程池最大限制数
    private static final int MAX_WORKER_NUMBERS   =  10;
    //线程池默认的数量
    private static final int DEFAULT_WORKER_NUMBERS   =  5;
    //线程池最小的数量
    private static final int MIN_WORKER_NUMBERS   =  1;

    //工作列表，会在链表插入工作
    private final LinkedList<Job> jobs = new LinkedList<Job>();
    //工作者列表,ArrayList是线程不安全的，用 Collections.synchronizedList()将其包装成线程安全的
    private final List<Worker> workers = Collections.synchronizedList(new ArrayList<Worker>());
    //工作者线程的数量
    private int workerNum = DEFAULT_WORKER_NUMBERS;
    //线程编号生成,使用了原子变量AtomicLong,线程安全的
    private AtomicLong threadNum = new AtomicLong();

    //初始化线程工作者
    private void initializeWorkers(int num) {
        for(int i = 0; i < num; i++) {
            Worker worker = new Worker();
            workers.add(worker);
            Thread thread = new Thread(worker,"ThreadPool-Worker-" + threadNum.incrementAndGet());
            thread.start();
            System.out.println(thread+" starting..");
        }
    }

    public DefaultThreadPool(){
        initializeWorkers(DEFAULT_WORKER_NUMBERS);
    }

    public DefaultThreadPool(int num) {
        //workerNum在最大限制数和最小限制数之间
        workerNum = num > MAX_WORKER_NUMBERS ?
                MAX_WORKER_NUMBERS :
                num < MIN_WORKER_NUMBERS ? MIN_WORKER_NUMBERS : num;
        initializeWorkers(workerNum);
    }

    @Override
    public void execute(Job job) {
        if(job != null) {
            //添加一个工作,然后对处于等待状态的worker进行通知
            synchronized (jobs) {
                jobs.addLast(job);
                jobs.notify();
            }
        }
    }

    @Override
    public void shutdown() {
        //实际测试，并不能关闭线程池，原因应该是线程池的worker都是waiting状态，必须通知
        //但不知为何，notifyAll也不能正确关闭
        for(Worker worker : workers) {
            worker.shutdown();
        }
    }

    @Override
    public void addWorkers(int num) {
        synchronized (jobs) {
            //限制新增的Worker数量不能超过最大值
            if(num + this.workerNum > MAX_WORKER_NUMBERS) {
                num = MAX_WORKER_NUMBERS - this.workerNum;
            }
            initializeWorkers(num);
            this.workerNum += num;
        }
    }

    @Override
    public void removeWorkers(int num) {
            synchronized (jobs) {
                if(num >= this.workerNum) {
                    throw new IllegalArgumentException("Beyond workNum");
                }

                //按照给定的数量停止Worker
                int count = 0;
                while(count < num ) {
                    Worker worker = workers.get(count);
                    if(workers.remove(worker)) {
                        worker.shutdown();
                        count ++;
                    }
                }
                this.workerNum -= count;
            }
    }

    @Override
    public int getJobSize() {
        return jobs.size();
    }

    //工作者，负责消耗任务
    class Worker implements Runnable{

        //给定一个volatile标识位，判断这个工作者是否在工作
        volatile boolean running = true;
        @Override
        public void run() {
            while(running) {
                Job job = null;
                synchronized (jobs) {
                    //如果工作列表是空的，那么就wait
                    while(jobs.isEmpty()) {
                        try {
                            jobs.wait();
                        } catch (InterruptedException e) {
                            //感知到外部的中断操作，返回
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    //取出一个Job
                    job = jobs.removeFirst();
                }

                if(job != null) {
                    try {
                        job.run();
                    } catch (Exception ex) {
                        //忽略job执行的Exception
                    }

                }
            }
        }

        public void shutdown() {
            running = false;
        }
    }
}
