package client;

import exception.ResponseException;
import server.ServerFacade;

import java.util.Arrays;

public class PlayChessClient implements ChessClient {
    private final ServerFacade server;

    public PlayChessClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw(params);
                case "leave" -> leave(params);
                case "move" -> move(params);
                case "resign" -> resign(params);
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String redraw(String... params) throws ResponseException {
        throw new ResponseException(400, "Function unimplemented!");
    }

    public String leave(String... params) throws ResponseException {
        return "You left the game.";
    }

    public String move(String... params) throws ResponseException {
        throw new ResponseException(400, "Function unimplemented!");
    }

    public String resign(String... params) throws ResponseException {
        throw new ResponseException(400, "Function unimplemented!");
    }

    public String highlight(String... params) throws ResponseException {
        throw new ResponseException(400, "Function unimplemented!");
    }

    public String help() {
        return """
                - redraw
                - leave
                - move <start> <end>
                - resign
                - highlight <start>
                """;
    }
}
