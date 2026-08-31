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
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserHandler
{
    ExecutorService executor = Executors.newFixedThreadPool(5);
    private final int port;
    public UserHandler(int port)
    {
        this.port = port;
    }

    public void run()
    {
        while(true) {
            try (var ServerSocket = new ServerSocket(port)
                 ) {
                var socket = ServerSocket.accept();
                executor.submit(() -> processSocket(socket));

            } catch (IOException e) {
                throw new RuntimeException("Не вдалося запустити сервер на порту " + port, e);
            }
        }
    }

    private void processSocket(Socket socket) {
        try(
                socket;
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

            String body;
            int bodyIndex = requestText.indexOf("\r\n\r\n");
            if(bodyIndex != -1){
                body = requestText.substring(bodyIndex +4);
            }
            else
                return;
            switch (requestType) {
                case "GET" -> handleGet(outputStream, requestPath);
                case "POST" -> handlePOST(body, outputStream);
                default -> throw new RuntimeException("Unknown request type: " + requestType);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlePOST(String body, OutputStream outputStream) {
        User.UserBuilder builder = User.builder();

        for(String pair : body.split("&")){
            String[] keyValue = pair.split("=");
            if(keyValue.length<2) continue;
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            switch (key){
                case "name" -> builder.name(value);
                case "last_name" -> builder.last_name(value);
                case "email" -> builder.email(value);
                case "number" -> builder.number(Integer.parseInt(value));
            }

        }
        User user = builder.build();
        UserDao.handleUserPOST(user);
        try {
            String response = HtmlView.successfullyRequest();
            byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
            String responseHeader = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + responseBytes.length + "\r\n" +
                    "Connection: close\r\n\r\n";
            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBytes);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void handleGet(OutputStream outputStream, String path) {
        if (path.equals("/allUsers")) {
            try {
                List<User> users = UserDao.getListUsers();
                String html = HtmlView.renderUserList(users);
                String status = "HTTP/1.1 200 OK";
                byte[] htmlBytes = html.getBytes();
                String responseHeader = status + "\r\n" +
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
        else{
            String response = HtmlView.notFound();
            byte[] responseByte = response.getBytes(StandardCharsets.UTF_8);
            String status = "HTTP/1.1 404 Not Found";
            String responseHeader = status + "\r\n" +
                    "Content-Type: text/html; charset=UTF-8\r\n" +
                    "Content-Length: " + responseByte.length + "\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";
            try {
                outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                outputStream.write(responseByte);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }


}
