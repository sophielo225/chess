package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.UserData;
import server.websocket.WebSocketHandler;
import service.UserService;
import spark.*;

public class Server {
    private final UserService userService;
    private final WebSocketHandler webSocketHandler;

    public Server(UserService userService) {
        this.userService = userService;
        webSocketHandler = new WebSocketHandler();
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.


        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }


}
