package threadstudying;

/**
 * 抢票模拟 + 实现Runnable接口来实现线程
 * 涉及静态代理模式，可以回顾这个设计模式
 * 线程不安全的写法,会有线程抢到相同的票
 */
public class TicketUnsafe implements Runnable {
    private int num = 0; // 出票数
    private int count = 10; // 剩余票数

    @Override
    public void run() {
        while (true) {
            // 没有余票时，跳出循环
            if (count <= 0) {
                break;
            }
            num++;
            count--;

            try {
                Thread.sleep(500);// 模拟网络延时
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("显示出票信息：" + Thread.currentThread().getName()
                    + "抢到第" + num + "张票，剩余" + count + "张票");
        }
    }

    public static void main(String[] args) {
        //真实角色
        TicketUnsafe ticket=new TicketUnsafe();
        //代理角色
        Thread t1 = new Thread(ticket, "路人甲");
        Thread t2 = new Thread(ticket, "黄牛乙");
        Thread t3 = new Thread(ticket, "攻城狮");
        //启动线程
        t1.start();
        t2.start();
        t3.start();
    }
}
