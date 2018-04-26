package concurrency;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

/**
 * 管道输入/输出流，主要用于线程之间的数据传输，传输的媒介为内存，
 * 与文件输入/输出流、网络输入/输出流不同,
 * 有四种实现，PipedOutputStream、PipedInputStream、PipedReader和PipeWriter，
 * 前两种面向字节，有两种面向字符
 */
public class Piped {
    public static void main(String[] args) throws Exception{
        PipedWriter out = new PipedWriter();
        PipedReader in = new PipedReader();
        //将输入流与输出流连接，否则使用时会抛出IOException
        out.connect(in);
        Thread printThread = new Thread(new Print(in), "PrintThread");
        printThread.start();
        int recieve = 0;
        try {
            //Ctrl+D结束读取，System.in.read()是字节流
            while( (recieve = System.in.read()) != -1 ) {
                out.write(recieve);
            }
        } finally {
            out.close();
        }
    }

    static class Print implements Runnable {
        private PipedReader in;
        public Print(PipedReader in) {
            this.in = in;
        }

        @Override
        public void run() {
            int recieve = 0;
            try {
                while( (recieve = in.read()) != -1 ) {
                    System.out.print((char)recieve);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
