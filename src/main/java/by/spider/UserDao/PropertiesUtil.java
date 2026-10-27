package by.spider.UserDao;

import java.io.IOException;
import java.util.Properties;

public class PropertiesUtil {
    static Properties PROPERTIES = new Properties();

    static{
        loadProperties();
    }

    private static void loadProperties() {
        try (var inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (inputStream == null) {
                throw new IllegalStateException("Проблеми з доступом");
            }
            PROPERTIES.load(inputStream);

        } catch (IOException e) {
            throw new RuntimeException("Проблеми з доступом", e);
        }
    }

    public static String get(String key){
        return PROPERTIES.getProperty(key);
    }



    private PropertiesUtil(){}
}
