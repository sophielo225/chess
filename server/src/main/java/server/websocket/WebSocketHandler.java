package server.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import exception.ResponseException;
import model.GameData;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private static class GamePair {
        int gameID;
        GameData game;
    }

    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
            String userName = getUserName(command.getAuthToken());
            switch (command.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connectCommand = new Gson().fromJson(message, ConnectCommand.class);
                    connect(session, userName, connectCommand);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = new Gson().fromJson(message, MakeMoveCommand.class);
                    makeMove(session, userName, makeMoveCommand);
                }
                case LEAVE -> {
                    LeaveGameCommand leaveGameCommand = new Gson().fromJson(message, LeaveGameCommand.class);
                    leaveGame(session, userName, leaveGameCommand);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = new Gson().fromJson(message, ResignCommand.class);
                    resign(session, userName, resignCommand);
                }
            }
        } catch (ResponseException ex) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: unauthorized"));
        } catch (Exception ex) {
            sendMessage(session.getRemote(), new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private String getUserName(String authToken) throws ResponseException {
        MySqlAuthDAO mySqlAuthDAO = new MySqlAuthDAO();
        return mySqlAuthDAO.isAuthorized(authToken);
    }

    private void sendMessage(RemoteEndpoint endpoint, ServerMessage message) throws IOException {
        endpoint.sendString(message.toString());
    }

    private GamePair validateInput(Session session, String userName, UserGameCommand command) throws ResponseException, IOException {
        var gameID = command.getGameID();
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        GameData game = mySqlGameDAO.getGame(gameID);
        if (game == null) {
            var errorMessage = new  ErrorMessage("Error: bad gameID");
            sendMessage(session.getRemote(), errorMessage);
            return null;
        }
        if (userName == null) {
            var errorMessage = new  ErrorMessage("Error: bad userName");
            sendMessage(session.getRemote(), errorMessage);
            return null;
        }
        var authToken = command.getAuthToken();
        MySqlAuthDAO mySqlAuthDAO = new MySqlAuthDAO();
        if (!mySqlAuthDAO.isAuthorized(authToken).equals(userName)) {
            var errorMessage = new  ErrorMessage("Error: unauthorized");
            sendMessage(session.getRemote(), errorMessage);
            return null;
        }
        GamePair gamePair = new GamePair();
        gamePair.gameID = gameID;
        gamePair.game = game;
        return gamePair;
    }

    private void connect(Session session, String userName, ConnectCommand command) throws IOException, ResponseException {
        GamePair gamePair = validateInput(session, userName, command);
        if (gamePair == null) {
            return;
        }
        connections.add(gamePair.gameID, userName, session);
        var jsonGame = new Gson().toJson(gamePair.game.game());
        var loadGameMessage = new LoadGameMessage(jsonGame);
        sendMessage(session.getRemote(), loadGameMessage);
        String message;
        if (userName.equals(gamePair.game.whiteUsername())) {
            message = String.format("joined '%s' WHITE", userName);
        } else if (userName.equals(gamePair.game.blackUsername())) {
            message = String.format("joined '%s' BLACK", userName);
        } else {
            message = String.format("joined '%s' Observer", userName);
        }
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gamePair.gameID, userName, notificationMessage);
    }

    public void makeMove(Session session, String userName, MakeMoveCommand command) throws IOException, ResponseException, InvalidMoveException {
        GamePair gamePair = validateInput(session, userName, command);
        if (gamePair == null) {
            return;
        }
        if (!(userName.equals(gamePair.game.whiteUsername()) || userName.equals(gamePair.game.blackUsername()))) {
            var errorMessage = new  ErrorMessage("Error: observer cannot make move");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (gamePair.game.game().isEnded()) {
            var errorMessage = new  ErrorMessage("Error: the game is ended");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        ChessGame.TeamColor color = userName.equals(gamePair.game.whiteUsername()) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if (color != gamePair.game.game().getTeamTurn()) {
            var errorMessage = new  ErrorMessage("Error: wrong turn");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (gamePair.game.game().isInCheckmate(color)) {
            var errorString = String.format("Error: '%s' is in checkmate", (color == ChessGame.TeamColor.WHITE) ? "WHITE" : "BLACK");
            var errorMessage = new  ErrorMessage(errorString);
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        var move = command.getMove();
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        gamePair.game.game().makeMove(move);
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        mySqlGameDAO.updateGame(gamePair.gameID, new Gson().toJson(gamePair.game.game()));
        var jsonGame = new Gson().toJson(gamePair.game.game());
        var loadGameMessage = new LoadGameMessage(jsonGame);
        connections.broadcast(gamePair.gameID, "", loadGameMessage);
        String startStr = String.format("%c%d", 'a' + start.getColumn() - 1, start.getRow());
        String endStr = String.format("%c%d", 'a' + end.getColumn() - 1, end.getRow());
        String message = String.format("'%s' made a move from <%s> to <%s>", userName, startStr, endStr);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gamePair.gameID, userName, notificationMessage);
        String warning = null;
        if (gamePair.game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            warning = String.format("'%s' is in check", gamePair.game.whiteUsername());
        } else if (gamePair.game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            warning = String.format("'%s' is in check", gamePair.game.blackUsername());
        }
        if (warning != null) {
            var warningMessage = new NotificationMessage(warning);
            connections.broadcast(gamePair.gameID, "", warningMessage);
        }
    }

    private void leaveGame(Session session, String userName, LeaveGameCommand command) throws IOException, ResponseException {
        GamePair gamePair = validateInput(session, userName, command);
        if (gamePair == null) {
            return;
        }
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        if (userName.equals(gamePair.game.whiteUsername())) {
            mySqlGameDAO.leaveGame("WHITE", gamePair.gameID);
        } else if (userName.equals(gamePair.game.blackUsername())) {
            mySqlGameDAO.leaveGame("BLACK", gamePair.gameID);
        }
        connections.remove(userName);
        var message = String.format("'%s' left the game", userName);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gamePair.gameID, userName, notificationMessage);
    }

    private void resign(Session session, String userName, ResignCommand command) throws IOException, ResponseException {
        GamePair gamePair = validateInput(session, userName, command);
        if (gamePair == null) {
            return;
        }
        if (!(userName.equals(gamePair.game.whiteUsername()) || userName.equals(gamePair.game.blackUsername()))) {
            var errorMessage = new  ErrorMessage("Error: observer cannot resign the game");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (gamePair.game.game().isEnded()) {
            var errorMessage = new  ErrorMessage("Error: the game is ended");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        gamePair.game.game().setEnded();
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        mySqlGameDAO.updateGame(gamePair.gameID, new Gson().toJson(gamePair.game.game()));
        var message = String.format("'%s' resigned the game", userName);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gamePair.gameID, "", notificationMessage);
    }
}
