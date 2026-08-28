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
}
