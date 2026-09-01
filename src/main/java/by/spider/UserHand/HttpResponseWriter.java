package by.spider.UserHand;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpResponseWriter {
    protected void sendHtmlResponse(OutputStream outputStream, int statusCode, String html) throws IOException {
        byte[] htmlBytes = html.getBytes(StandardCharsets.UTF_8);
        String statusText = switch (statusCode) {
            case 200 -> "200 OK";
            case 404 -> "404 Not Found";
            case 500 -> "500 Internal Server Error";
            default -> statusCode + " " + "Unknown";
        };

        String responseHeader = "HTTP/1.1 " + statusText + "\r\n" +
                "Content-Type: text/html; charset=UTF-8\r\n" +
                "Content-Length: " + htmlBytes.length + "\r\n" +
                "Connection: close\r\n\r\n";
        outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
        outputStream.write(htmlBytes);
        outputStream.flush();
    }
}
