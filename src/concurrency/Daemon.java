package concurrency;

import java.util.concurrent.TimeUnit;

public class Daemon {
    //创建实现了Runnable接口的Daemon线程类
    static class DaemonRunner implements Runnable {
        @Override
        public void run() {
            try{
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Daemon线程的finally()代码块正在运行！！！");
            }
        }
    }

    public static void main(String[] args) {
        Thread thread = new Thread(new DaemonRunner(), "DaemonThread");
        //设置线程为Daemon线程
        thread.setDaemon(true);
        thread.start();


        /*
        //暂停main线程（非Daemon线程）10s，可以看到finally代码块被执行
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */

    }
}

