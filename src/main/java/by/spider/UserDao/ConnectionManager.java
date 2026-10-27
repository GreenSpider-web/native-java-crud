package by.spider.UserDao;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String USER_KEY = "db.username";
    private static final String PASS_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final int DEFAULT_POOL_SIZE = 10;
    private static BlockingDeque<Connection> connectionPool;


    private static Connection open(){
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USER_KEY),
                    PropertiesUtil.get(PASS_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException("db not found", e);
        }
    }

    static{
        initConnectionPool();
    }

    private static void initConnectionPool() {
        String poolSizeStr = PropertiesUtil.get(POOL_SIZE_KEY);
        int size = DEFAULT_POOL_SIZE;
        if (poolSizeStr != null && !poolSizeStr.isBlank()) {
            try {
                size = Integer.parseInt(poolSizeStr);
            } catch (NumberFormatException ignored) {
            }
        }

        connectionPool = new LinkedBlockingDeque<>(size);

        for (int i = 0; i < size; i++) {
            Connection physical = open();
            var connectionProxy = (Connection) Proxy.newProxyInstance(
                    ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> {
                        if ("close".equals(method.getName())) {
                            connectionPool.add((Connection) proxy);
                            return null;
                        }
                        return method.invoke(physical, args);
                    });
            connectionPool.add(connectionProxy);
        }
    }

    public static Connection getConnection(){
        try {
            return connectionPool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Сторінка тимчасово недоступна", e);
        }
    }

}
