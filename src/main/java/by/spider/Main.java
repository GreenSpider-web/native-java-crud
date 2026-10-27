package by.spider;


import by.spider.UserHand.UserHandler;

public class Main {
    public static void main(String[] args) {

        UserHandler server = new UserHandler(8080);
        System.out.println("Server host: http://localhost:8080");
        server.run();

    }
}
