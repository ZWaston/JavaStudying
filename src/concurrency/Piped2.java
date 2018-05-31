package concurrency;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.concurrent.TimeUnit;

public class Piped2 {

    public static void main(String[] args) throws IOException {
        // 创建输入流与输出流对象
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();
        // 连接输入输出流
        out.connect(in);

        //创建写线程
        WriteThread writeThread = new WriteThread(out);
        //创建读线程
        ReaderThread readerThread = new ReaderThread(in);

        writeThread.start();
        readerThread.start();
    }


    // 写线程
    static class WriteThread extends Thread{
        private PipedWriter out;

        public WriteThread(PipedWriter out){
            this.out = out;
        }

        public void run(){
            try {
                while(true) {
                    //每个5s写线程发送一个句子给读线程读
                    TimeUnit.SECONDS.sleep(5);
                    out.write("hello concurrent world!\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 读线程
    static class ReaderThread extends Thread{
        private PipedReader in;

        public ReaderThread(PipedReader in){
            this.in = in;
        }

        public void run(){
            try {
                int recieve = 0;
                while( (recieve = in.read()) != -1 ) {
                    System.out.print((char)recieve);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
