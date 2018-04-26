package concurrency.threadpool;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.TimeUnit;

public class ThreadPoolTest {
    public static void main(String[] args) throws InterruptedException {
        DefaultThreadPool<Job> pool = new DefaultThreadPool<Job>();
        /*for(int i = 0; i < 100000; i++) {
            Job job = new Job();
            pool.execute(job);
        }*/
        TimeUnit.SECONDS.sleep(10);
        //shutdown之前的状态
        //获取Java线程管理MXBean
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        //不需要获取同步的monitor和synchronizer信息，仅获取线程和线程堆栈信息
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false,false);
        //遍历打印
        for(ThreadInfo threadInfo:threadInfos) {
            System.out.println("["+threadInfo.getThreadId()+"]"+
                    threadInfo.getThreadName() + " state: " + threadInfo.getThreadState());
        }

        pool.shutdown();
        //线程池shutdown之后的状态
        TimeUnit.SECONDS.sleep(5);
        System.out.println("job size: " + pool.getJobSize());
        threadInfos = threadMXBean.dumpAllThreads(false,false);
        //遍历打印
        for(ThreadInfo threadInfo:threadInfos) {
            System.out.println("["+threadInfo.getThreadId()+"]"+
                    threadInfo.getThreadName() + " state: " + threadInfo.getThreadState());
        }
    }

    static class Job implements Runnable {

        @Override
        public void run() {
            int i = 10;
            while(i > 0){
                i--;
                System.out.println(i);
            }
        }
    }
}
