import java.io.*;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;

public class Consumer {
    public static void main(String[] args) throws IOException, InterruptedException {
        while (true) {
            try (FileInputStream fis = new FileInputStream(new File("x"))) {
                int b;
                while ((b = fis.read()) != -1) {
                    System.out.print((char) b);
                }
            }
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }
}
