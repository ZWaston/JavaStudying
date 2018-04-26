package concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 线程优先级的示例，setPriority(int)方法可以调整线程优先级，1-10，默认5，
 * 但是不是所有系统都对这个操作生效，ubuntu14.04和mac os x10.10未生效，win10下生效。
 * 可以看到结果优先级为1的和优先级为10的线程计数结果相差很大。
 */
public class Priority {
    private static volatile boolean notStart = true;
    private static volatile boolean notEnd   = true;

    public static void main(String[] args) throws Exception {
        List<Job> jobs = new ArrayList<Job>();
        for (int i = 0; i < 10; i++) {
            int priority = i < 5 ? Thread.MIN_PRIORITY : Thread.MAX_PRIORITY;
            Job job = new Job(priority);
            jobs.add(job);
            Thread thread = new Thread(job, "Thread:" + i);
            //设置每个线程的优先级
            thread.setPriority(priority);
            //启动线程
            thread.start();
        }
        notStart = false;
        Thread.currentThread().setPriority(8);
        System.out.println("done.");
        //TimeUnit.SECONDS.sleep()对Thread.sleep方法的包装，实现是一样的，只是多了时间单位转换和验证，
        // 然而TimeUnit枚举成员的方法却提供更好的可读性
        TimeUnit.SECONDS.sleep(10);
        notEnd = false;

        for (Job job : jobs) {
            System.out.println("Job Priority : " + job.priority + ", Count : " + job.jobCount);
        }

    }

    static class Job implements Runnable {
        private int  priority;
        private long jobCount;

        public Job(int priority) {
            this.priority = priority;
        }

        public void run() {
            while (notStart) {
                //如果还未开始，使用yield()使线程放弃对当前CPU的占用，运行中状态-->就绪状态
                Thread.yield();
            }
            while (notEnd) {
                Thread.yield();
                jobCount++;
            }
        }
    }
}

