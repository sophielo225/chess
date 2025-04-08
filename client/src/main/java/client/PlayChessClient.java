package client;

import client.websocket.ServerMessageObserver;
import exception.ResponseException;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Arrays;

import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;
import static ui.EscapeSequences.SET_TEXT_COLOR_RED;

public class PlayChessClient implements ChessClient, ServerMessageObserver {
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
                case "leave" -> leaveGame(params);
                case "move" -> makeMove(params);
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

    public String leaveGame(String... params) throws ResponseException {
        return "You left the game.";
    }

    public String makeMove(String... params) throws ResponseException {
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

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> {
                var notification = ((NotificationMessage) message).getMessage();
                System.out.println(SET_TEXT_COLOR_RED + notification);
            }
            case ERROR -> {
                var error = ((ErrorMessage) message).getErrorMessage();
                System.out.println(SET_TEXT_COLOR_RED + error);
            }
            case LOAD_GAME -> loadGame(((LoadGameMessage) message));
        }
    }

    private void loadGame(LoadGameMessage message) {

    }
}
