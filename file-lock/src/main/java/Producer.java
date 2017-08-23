import java.io.*;
import java.nio.channels.FileLock;
import java.util.concurrent.TimeUnit;

public class Producer {
    public static void main(String[] args) throws IOException, InterruptedException {
        FileLock lock = null;
        int i = 0;
        while (true) {
            try (FileOutputStream fos = new FileOutputStream(new File("x"))) {
                lock = fos.getChannel().lock();
                i++;
                for (int j = 0; j <= i; j++) {
                    fos.write(String.valueOf(j + " ").getBytes());
                }
                fos.write('\n');
                lock.release();
                if (lock != null) {
                    lock.close();
                }
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
    }
}
