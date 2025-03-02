package server;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Map;

public class Server {
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private final UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
    private final GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);
    private final AuthService authService = new AuthService(memoryAuthDAO);
    private static class GameName {
        @SerializedName("gameName")
        private String gameName;
    }
    private static class JoinGame {
        @SerializedName("playerColor")
        private String color;
        @SerializedName("gameID")
        private int gameID;
    }
    private record ClearResult(String message) {}

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void exceptionHandler(ResponseException ex, Request req, Response res) {
        res.status(ex.StatusCode());
        res.body(ex.toJson());
    }

    private Object clear(Request req, Response res) {
        userService.clear();
        gameService.clear();
        authService.clear();
        var clearResult = new ClearResult("OK");
        return new Gson().toJson(clearResult);
    }

    private Object register(Request req, Response res) throws DataAccessException, ResponseException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        var registerRequest = new UserService.RegisterRequest(user.username(), user.password(), user.email());
        var registerResult = userService.register(registerRequest);
        return new Gson().toJson(registerResult);
    }

    private Object login(Request req, Response res) throws ResponseException {
        var user = new Gson().fromJson(req.body(), UserData.class);
        var loginRequest = new UserService.LoginRequest(user.username(), user.password());
        var loginResult = userService.login(loginRequest);
        return new Gson().toJson(loginResult);
    }

    private Object logout(Request req, Response res) throws ResponseException, DataAccessException {
        var logoutRequest = new UserService.LogoutRequest(req.headers("Authorization"));
        var logoutResult = userService.logout(logoutRequest);
        return new Gson().toJson(logoutResult);
    }

    private Object listGames(Request req, Response res) throws ResponseException {
        var listGamesRequest = new GameService.ListGamesRequest(req.headers("Authorization"));
        var listGamesResult = gameService.listGames(listGamesRequest);
        res.type("application/json");
        var list = listGamesResult.games().toArray();
        return new Gson().toJson(Map.of("games", list));
    }

    private Object createGame(Request req, Response res) throws ResponseException {
        var gameName = new Gson().fromJson(req.body(), GameName.class);
        var createGameRequest = new GameService.CreateGameRequest(gameName.gameName);
        var createGameResult = gameService.createGame(createGameRequest, req.headers("Authorization"));
        return new Gson().toJson(createGameResult);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        var joinGame = new Gson().fromJson(req.body(), JoinGame.class);
        var joinGameRequest = new GameService.JoinGameRequest(joinGame.color, joinGame.gameID);
        var joinGameResult = gameService.joinGame(joinGameRequest, req.headers("Authorization"));
        return new Gson().toJson(joinGameResult);
    }
}
