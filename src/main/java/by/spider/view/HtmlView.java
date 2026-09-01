package by.spider.view;

import by.spider.model.User;

import java.util.List;

public class HtmlView {

    public static String renderRegistrationForm() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<h1>Реєстрація користувача</h1>");
        html.append("<form method=\"POST\" action=\"/users\">")
                .append("<label>Ім'я: <input type=\"text\" name=\"name\"></label><br>")
                .append("<label>Прізвище: <input type=\"text\" name=\"lastname\"></label><br>")
                .append("<label>Email: <input type=\"email\" name=\"email\"></label><br>")
                .append("<label>Номер: <input type=\"number\" name=\"number\"></label><br>")
                .append("<button type=\"submit\">Відправити</button>")
                .append("</form>")
                .append("<p><a href=\"/allUsers\">Переглянути список користувачів</a></p>");
        html.append("</body></html>");
        return html.toString();
    }

    public static String renderUserList(List<User> users) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><body>");
        html.append("<h1>Список користувачів</h1>");
        html.append("<ul>");

        for (User user : users) {
            html.append("<li>")
                    .append(user.getName())
                    .append(" ")
                    .append(user.getLastname())
                    .append(" | ")
                    .append(user.getEmail())
                    .append(" | ")
                    .append(user.getNumber())
                    .append("</li>");
        }
        html.append("</ul>");
        html.append("<p><a href=\"/\">До реєстрації</a></p>");
        html.append("</body></html>");
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
        html.append("<a href=\"/\">На сторінку реєстрації</a>");
        html.append("</body></html>");

        return html.toString();
    }
}
