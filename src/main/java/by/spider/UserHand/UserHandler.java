package by.spider.UserHand;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserHandler
{
    ExecutorService executor = Executors.newFixedThreadPool(5);
    private final int port;
    UserHandler(int port)
    {
        this.port = port;
    }

    public void run()
    {
        try(var ServerSocket = new ServerSocket(port))
        {
            var socket = ServerSocket.accept();
            executor.submit(() -> processSocket(socket));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void processSocket(Socket socket) {
        try(
                var inputStream = new DataInputStream(socket.getInputStream());
                var outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            byte[] buffer = new byte[1024];
            int len = inputStream.read(buffer);

            if (len == -1) {
                return;
            }
            String requestText = new String(buffer, 0, len);
            if(requestText.isEmpty()) {
                return;
            }
            String firstLine = requestText.split("\r\n")[0];
            String requestType = firstLine.split(" ")[0];
            String requestPath = firstLine.split(" ")[1];
            switch (requestType) {
                case "GET" -> handleGet(requestPath, outputStream);
                case "POST" -> handlePOST();
                default -> throw new RuntimeException("Unknown request type: " + requestType);


            }



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePOST() {

    }

    private void handleGet(String requestPath, OutputStream outputStream) {

    }


}
