package by.spider.UserHand;

import by.spider.UserDao.UserDao;
import by.spider.model.User;
import by.spider.view.HtmlView;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Controller
{
    void handlePOST(String body, OutputStream outputStream) {
        User.UserBuilder builder = User.builder();

        for (String pair : body.split("&")) {
            if (pair.isBlank()) {
                continue;
            }
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length < 2) {
                continue;
            }
            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            switch (key) {
                case "name" -> builder.name(value);
                case "lastname" -> builder.lastname(value);
                case "email" -> builder.email(value);
                case "number" -> builder.number(Integer.parseInt(value));
            }
        }
        HttpResponseWriter writeResponse = new HttpResponseWriter();
        try {
            User user = builder.build();
            UserDao.handleUserPOST(user);
            writeResponse.sendHtmlResponse(outputStream, 200, HtmlView.successfullyRequest());
        } catch (Exception e) {
            try {
                writeResponse.sendHtmlResponse(outputStream, 500, "<html><body><h1>Помилка сервера</h1><p>Не вдалося зберегти користувача.</p></body></html>");
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
    }
    void handleGet(OutputStream outputStream, String path) {
        HttpResponseWriter writeResponse = new HttpResponseWriter();
        try {
            if (path.equalsIgnoreCase("/") || path.equalsIgnoreCase("/register")) {
                writeResponse.sendHtmlResponse(outputStream, 200, HtmlView.renderRegistrationForm());
                return;
            }

            if (path.equalsIgnoreCase("/allUsers")) {
                List<User> users = UserDao.getListUsers();
                writeResponse.sendHtmlResponse(outputStream, 200, HtmlView.renderUserList(users));
                return;
            }

            writeResponse.sendHtmlResponse(outputStream, 404, HtmlView.notFound());
        } catch (Exception e) {
            try {
                writeResponse.sendHtmlResponse(outputStream, 500, "<html><body><h1>Помилка сервера</h1><p>Не вдалося отримати дані.</p></body></html>");
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
        }
    }
}
