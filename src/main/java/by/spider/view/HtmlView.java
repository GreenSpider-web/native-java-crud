package by.spider.view;

import by.spider.model.User;

import java.util.List;

public class HtmlView {

    public static String renderUserList(List<User> users) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<h1>Список користувачів</h1>");
        html.append("<ul>");

        for (User user : users) {
            html.append("<li>")
                    .append(user.getName())
                    .append("</li>");
            html.append("<li>")
                    .append(user.getLast_name())
                    .append("</li>");
            html.append("<li>")
                    .append(user.getEmail())
                    .append("</li>");
            html.append("<li>")
                    .append(user.getNumber())
                    .append("</li>");
        }


        return html.toString();
    }

    public static String successfullyRequest(){
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<h1>Реєстрація успішна</h1>");
        html.append("<p>Користувача успішно додано.</p>");
        html.append("<a href=\"/allUsers\">Переглянути список користувачів</a>");
        html.append("</body></html>");

        return html.toString();

    }
    public static String notFound() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<h1>Сторінку не знайдено</h1>");
        html.append("<p>Такого шляху не існує на сервері.</p>");
        html.append("<a href=\"/allUsers\">Переглянути список користувачів</a>");
        html.append("</body></html>");

        return html.toString();
    }
}
