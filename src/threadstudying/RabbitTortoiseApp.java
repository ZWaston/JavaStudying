package threadstudying;

/**
 * 模拟龟兔赛跑，使用继承的方式实现线程
 * 1、创建多线程 继承Thread + 重写run()方法
 * 2、创建子类对象，调用 对象.start() 方法，这个方法会启动线程,不要自己调用run()
 */
public class RabbitTortoiseApp {
    public static void main(String[] args) {
        Rabbit rabbit = new Rabbit();
        Tortoise tortoise = new Tortoise();

        rabbit.start();
        tortoise.start();
    }
}

class Rabbit extends Thread{
    @Override
    public void run() {
        //线程体
        for(int i = 0; i <= 100; i++) {
            System.out.println("兔子跑了" + i + "步");
        }
    }
}

class Tortoise extends Thread{
    @Override
    public void run() {
        //线程体
        for(int i = 0; i <= 100; i++) {
            System.out.println("乌龟跑了" + i + "步");
        }
    }
}
