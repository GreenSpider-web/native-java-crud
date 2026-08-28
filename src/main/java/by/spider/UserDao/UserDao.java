package by.spider.UserDao;

import by.spider.model.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static by.spider.UserDao.ConnectionManager.open;


public class UserDao {

    public static List<User> getListUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sqlRequest = """
                select * from users;
                """;
        try (var connection = open()) {
            var statement = connection.createStatement();
            var result = statement.executeQuery(sqlRequest);
            while (result.next()) {
                String name = result.getString("name");
                String last_name = result.getString("last_name");
                String email = result.getString("email");
                int number = result.getInt("number");
                User user = new User(name, last_name, email, number);
                users.add(user);
            }
        }
        return users;
    }

    public static void handleUserPOST(User user) {
        String slqRequest = "INSERT INTO users (name, last_name, email, number) VALUES (?, ?, ?, ?)";
        try (Connection connection = ConnectionManager.open();
             var statement = connection.prepareStatement(slqRequest);
        ) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getLast_name());
            statement.setString(3, user.getEmail());
            statement.setInt(4, user.getNumber());



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
