package threadstudying;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * java.util.Timer工具类的使用，有定时启动任务功能
 */
public class TimerDemo1 {
    public static void main(String[] args) {
        Timer timer = new Timer();
        //1s之后开始运行，并且每隔200ms运行一次
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("该起床了！！！");
            }
        },new Date(System.currentTimeMillis() + 1000), 200);
    }
}
