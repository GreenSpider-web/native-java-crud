package by.spider.UserHand;


import by.spider.UserDao.UserDao;
import by.spider.model.User;
import by.spider.view.HtmlView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
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
                case "GET" -> handleGet(outputStream);
                case "POST" -> handlePOST();
                default -> throw new RuntimeException("Unknown request type: " + requestType);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePOST(String name, String last_name, String email, int number) {


    }
    private void handlePOST(String name, String last_name, String email) {


    }
    private void handlePOST(String name, String last_name, int number) {


    }
    private void handlePOST(String name, String last_name) {


    }

    private void handleGet(OutputStream outputStream) {
        try {
            List<User> users = UserDao.getListUsers();
            String html = HtmlView.renderUserList(users);
            String status = "HTTP/1.1 200 OK";
            byte[] htmlBytes = html.getBytes();
            String responseHeader = status+"\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + htmlBytes.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(htmlBytes);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }


}
