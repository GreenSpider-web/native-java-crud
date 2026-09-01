package by.spider;


import by.spider.UserHand.UserHandler;

public class Main {
    static void main() {
        UserHandler server = new UserHandler(8080);
        server.run();



    }
}
