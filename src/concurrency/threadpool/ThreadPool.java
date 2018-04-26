package concurrency.threadpool;

public interface ThreadPool<Job extends Runnable> {
    //执行一个job，这个job需要实现Runnable接口,所以可以job.run()
    void execute(Job job);
    //关闭线程池
    void shutdown();
    //增加工作者线程
    void addWorkers(int sum);
    //减少工作者线程
    void removeWorkers(int sum);
    //得到正在等待执行的任务数量
    int getJobSize();
}
