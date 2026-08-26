package by.spider.UserDao;

import by.spider.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UserDao
{

    public static List<User> getListUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sqlRequest = """
                select * from users;
                """;
        try (var connection = ConnectionManager.open()) {
            var statement = connection.createStatement();
            statement.executeQuery(sqlRequest);
        }
        return users;
    }

}
