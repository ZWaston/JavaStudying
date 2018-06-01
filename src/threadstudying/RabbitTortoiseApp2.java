package threadstudying;

import java.util.concurrent.*;

/**
 * 使用Callable创建线程，较为繁琐，但是可以有返回值和抛异常
 * 模拟龟兔赛跑
 *
 * 使用流程：
 * 1）创建Callable实现类 + 重写call
 * 2）借助 执行调度服务 ExecutorService，获取Future对象
 * ExecutorService ser = Executors.newFixedThreadPool(1);
 * 3）获取值result.get()
 * 4）停止服务 ser.shutdownNow()
 */
public class RabbitTortoiseApp2 {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建线程
        ExecutorService ser = Executors.newFixedThreadPool(2);
        Race tortoise = new Race("乌龟",100);
        Race rabbit = new Race("兔子",50);

        //将两个实例对象交给ExecutorService服务启动线程
        Future<Integer> result1 = ser.submit(tortoise);
        Future<Integer> result2 = ser.submit(rabbit);

        //停止线程,2s后停止线程
        Thread.sleep(2000);
        //停止线程体的循环
        tortoise.setFlag(false);
        rabbit.setFlag(false);

        //两个线程停止后，获取call方法返回的值
        int num1 = result1.get();
        int num2 = result2.get();
        System.out.println("乌龟跑了-->" + num1 + "步");
        System.out.println("兔子跑了-->" + num2 + "步");
        //停止服务
        ser.shutdownNow();
    }
}

class Race implements Callable<Integer> {
    private String name;    //名称
    private long time;       //每走一步的延迟
    private boolean flag = true;    //表明是否停止线程
    private int step = 0;   //走了多少步

    public Race() {
    }

    public Race(String name) {
        this.name = name;
    }

    public Race(String name, long time) {
        this.name = name;
        this.time = time;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setStep(int step) {
        this.step = step;
    }

    @Override
    public Integer call() throws Exception {
        while(flag) {
            Thread.sleep(time);  //延时
            step++;
        }
        return step;
    }
}
