package client;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Arrays;

public class PreLoginClient implements ChessClient {
    private final ServerFacade server;

    public PreLoginClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "clear" -> clear();
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String clear() throws ResponseException {
        server.clear();
        return "You cleared database.";
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            AuthData authData = server.register(new UserData(username, password, email));
            if (authData != null) {
                return String.format("You signed in as %s.", username);
            } else {
                return String.format("Failed to register %s.", username);
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            AuthData authData = server.login(new UserData(username, password, ""));
            if (authData != null) {
                return String.format("You signed in as %s.", username);
            } else {
                return String.format("Failed to sign in as %s.", username);
            }
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD>");
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                login <USERNAME> <PASSWORD> - to play chess
                quit - playing chess
                help - with possible commands
                """;
    }
}
