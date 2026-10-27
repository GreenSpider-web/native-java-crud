package by.spider.UserHand;


import by.spider.view.HtmlView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserHandler
{
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final int port;
    public UserHandler(int port)
    {
        this.port = port;
    }

    public void run()
    {
            try (var ServerSocket = new ServerSocket(port)
                 ) {
                while(true) {
                    var socket = ServerSocket.accept();
                    executor.submit(() -> processSocket(socket));
                }

            } catch (IOException e) {
                throw new RuntimeException("Не вдалося запустити сервер на порту " + port, e);
            }

    }

    private void processSocket(Socket socket) {
        HttpResponseWriter writeResponse = new HttpResponseWriter();
        try(
                socket;
                var inputStream = new DataInputStream(socket.getInputStream());
                var outputStream = new DataOutputStream(socket.getOutputStream())
        ) {
            byte[] buffer = new byte[10240];
            int len = inputStream.read(buffer);

            if (len == -1) {
                return;
            }
            String requestText = new String(buffer, 0, len);
            if (requestText.isEmpty()) {
                return;
            }

            String[] requestLines = requestText.split("\r\n");
            if (requestLines.length == 0) {
                return;
            }

            String firstLine = requestLines[0];
            String[] requestParts = firstLine.split(" ");
            if (requestParts.length < 2) {
                writeResponse.sendHtmlResponse(outputStream, 400, "<html><body><h1>Bad request</h1></body></html>");
                return;
            }

            String requestType = requestParts[0];
            String requestPath = requestParts[1];
            String path = requestPath.split("\\?")[0];

            Controller controller = new Controller();

            if ("GET".equalsIgnoreCase(requestType)) {
                controller.handleGet(outputStream, path);
                return;
            }

            if ("POST".equalsIgnoreCase(requestType)) {

                String body = "";
                int bodyIndex = requestText.indexOf("\r\n\r\n");
                if (bodyIndex != -1) {
                    body = requestText.substring(bodyIndex + 4);
                }
                controller.handlePOST(body, outputStream);
                return;
            }

            writeResponse.sendHtmlResponse(outputStream, 404, HtmlView.notFound());
        } catch (IOException e) {
            throw new RuntimeException("Unknown request type: ", e);
        }
    }



}
