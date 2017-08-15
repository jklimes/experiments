package socket;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

/**
 * Created by jan on 10.4.17.
 */
public class Client {
    public static void main(String[] args) {
        final String id = args[0];
        try (Socket s = new Socket("localhost", 8888);
             OutputStream outputStream = s.getOutputStream();
             PrintWriter pw = new PrintWriter(outputStream);
             BufferedReader bfr = new BufferedReader(new InputStreamReader(s.getInputStream()))) {

            int i = 0;
            while (true) {
                String request = id + " ahoj " + i;
                pw.println(i == 10 ? "BYE" : request);
                pw.flush();
                TimeUnit.MILLISECONDS.sleep(1000);
                String response = bfr.readLine();
                System.out.println(response);
                response = "CLOSING";
                if (response == null) {
                    System.out.println("SERVER IS NOT RESPONDING. CLOSING THE CONNECTION DOWN NOW.");
                    break;
                } else if ("CLOSING".equals(response)) {
                    System.out.println("CLOSING DOWN");
                    break;
                }
                i++;
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
