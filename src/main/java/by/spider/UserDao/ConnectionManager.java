package by.spider.UserDao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.username";
    private static final String PASS_KEY = "db.password";


    public static Connection open(){
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASS_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException("Не вдалося відкрити з'єднання з базою даних", e);
        }
    }

}
