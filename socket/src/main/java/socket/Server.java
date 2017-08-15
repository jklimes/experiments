package socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jan on 10.4.17.
 */
public class Server {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(8888)) {
            ExecutorService pool = Executors.newCachedThreadPool();
            int i = 1;
            while (true) {
                System.out.println("READY FOR ACTION " + i);
                try {
                    pool.submit(new ConversationalHandler(ss.accept()));
                    System.out.println("SUBMITTED FOR ACTION " + i);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private abstract static class SocketHandler implements Runnable {
        private final Socket socket;

        private SocketHandler(Socket socket) {
            this.socket = socket;
        }

        public Socket getSocket() {
            return socket;
        }

        @Override
        public void run() {

        }
    }

    private static class InputHandler extends SocketHandler {
        private InputHandler(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {

            try (Socket s = getSocket(); InputStreamReader isr = new InputStreamReader(s.getInputStream())) {
                int c = isr.read();
                while (c > 0) {
                    System.out.print((char) c);
                    c = isr.read();
                }
                System.out.println("CONSUMED");
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private static class ConversationalHandler extends SocketHandler {
        private ConversationalHandler(Socket socket) {
            super(socket);
        }

        @Override
        public void run() {
            try (Socket s = getSocket();
                 BufferedReader i = new BufferedReader(new InputStreamReader(s.getInputStream()));
                 PrintWriter o = new PrintWriter(new OutputStreamWriter(s.getOutputStream()))) {
                String line;
                while (!(line = i.readLine()).equals("BYE")) {
                    System.out.println(line);
                    o.println("ACCEPTED");
                    o.flush();
                }
                o.println("CLOSING");
                o.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
