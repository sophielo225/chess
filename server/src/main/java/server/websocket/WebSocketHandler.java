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

    private void connect(Session session, String userName, ConnectCommand command) throws IOException, ResponseException {
        var gameID = command.getGameID();
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        GameData game = mySqlGameDAO.getGame(gameID);
        if (game == null) {
            var errorMessage = new  ErrorMessage("Error: bad gameID");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (userName == null) {
            var errorMessage = new  ErrorMessage("Error: bad userName");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        var authToken = command.getAuthToken();
        MySqlAuthDAO mySqlAuthDAO = new MySqlAuthDAO();
        if (!mySqlAuthDAO.isAuthorized(authToken).equals(userName)) {
            var errorMessage = new  ErrorMessage("Error: unauthorized");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        connections.add(gameID, userName, session);
        var jsonGame = new Gson().toJson(game.game());
        var loadGameMessage = new LoadGameMessage(jsonGame);
        sendMessage(session.getRemote(), loadGameMessage);
        String message;
        if (userName.equals(game.whiteUsername())) {
            message = String.format("joined '%s' WHITE", userName);
        } else if (userName.equals(game.blackUsername())) {
            message = String.format("joined '%s' BLACK", userName);
        } else {
            message = String.format("joined '%s' Observer", userName);
        }
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gameID, userName, notificationMessage);
    }

    public void makeMove(Session session, String userName, MakeMoveCommand command) throws IOException, ResponseException, InvalidMoveException {
        var gameID = command.getGameID();
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        GameData game = mySqlGameDAO.getGame(gameID);
        if (game == null) {
            var errorMessage = new  ErrorMessage("Error: bad gameID");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (userName == null) {
            var errorMessage = new  ErrorMessage("Error: bad userName");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        var authToken = command.getAuthToken();
        MySqlAuthDAO mySqlAuthDAO = new MySqlAuthDAO();
        if (!mySqlAuthDAO.isAuthorized(authToken).equals(userName)) {
            var errorMessage = new  ErrorMessage("Error: unauthorized");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (!(userName.equals(game.whiteUsername()) || userName.equals(game.blackUsername()))) {
            var errorMessage = new  ErrorMessage("Error: observer cannot make move");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (game.game().isEnded()) {
            var errorMessage = new  ErrorMessage("Error: the game is ended");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        ChessGame.TeamColor color = userName.equals(game.whiteUsername()) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        if (color != game.game().getTeamTurn()) {
            var errorMessage = new  ErrorMessage("Error: wrong turn");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (game.game().isInCheckmate(color)) {
            var errorString = String.format("Error: '%s' is in checkmate", (color == ChessGame.TeamColor.WHITE) ? "WHITE" : "BLACK");
            var errorMessage = new  ErrorMessage(errorString);
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        var move = command.getMove();
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        game.game().makeMove(move);
        mySqlGameDAO.updateGame(gameID, new Gson().toJson(game.game()));
        var jsonGame = new Gson().toJson(game.game());
        var loadGameMessage = new LoadGameMessage(jsonGame);
        connections.broadcast(gameID, "", loadGameMessage);
        String startStr = String.format("%c%d", 'a' + start.getColumn() - 1, start.getRow());
        String endStr = String.format("%c%d", 'a' + end.getColumn() - 1, end.getRow());
        String message = String.format("'%s' made a move from <%s> to <%s>", userName, startStr, endStr);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gameID, userName, notificationMessage);
        String warning = null;
        if (game.game().isInCheck(ChessGame.TeamColor.WHITE)) {
            warning = String.format("'%s' is in check", game.whiteUsername());
        } else if (game.game().isInCheck(ChessGame.TeamColor.BLACK)) {
            warning = String.format("'%s' is in check", game.blackUsername());
        }
        if (warning != null) {
            var warningMessage = new NotificationMessage(warning);
            connections.broadcast(gameID, "", warningMessage);
        }
    }

    private void leaveGame(Session session, String userName, LeaveGameCommand command) throws IOException, ResponseException {
        var gameID = command.getGameID();
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        GameData game = mySqlGameDAO.getGame(gameID);
        if (game == null) {
            var errorMessage = new  ErrorMessage("Error: bad gameID");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (userName == null) {
            var errorMessage = new  ErrorMessage("Error: bad userName");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        var authToken = command.getAuthToken();
        MySqlAuthDAO mySqlAuthDAO = new MySqlAuthDAO();
        if (!mySqlAuthDAO.isAuthorized(authToken).equals(userName)) {
            var errorMessage = new  ErrorMessage("Error: unauthorized");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (userName.equals(game.whiteUsername())) {
            mySqlGameDAO.leaveGame("WHITE", gameID);
        } else if (userName.equals(game.blackUsername())) {
            mySqlGameDAO.leaveGame("BLACK", gameID);
        }
        connections.remove(userName);
        var message = String.format("'%s' left the game", userName);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gameID, userName, notificationMessage);
    }

    private void resign(Session session, String userName, ResignCommand command) throws IOException, ResponseException {
        var gameID = command.getGameID();
        MySqlGameDAO mySqlGameDAO = new MySqlGameDAO();
        GameData game = mySqlGameDAO.getGame(gameID);
        if (game == null) {
            var errorMessage = new  ErrorMessage("Error: bad gameID");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (userName == null) {
            var errorMessage = new  ErrorMessage("Error: bad userName");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        var authToken = command.getAuthToken();
        MySqlAuthDAO mySqlAuthDAO = new MySqlAuthDAO();
        if (!mySqlAuthDAO.isAuthorized(authToken).equals(userName)) {
            var errorMessage = new  ErrorMessage("Error: unauthorized");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (!(userName.equals(game.whiteUsername()) || userName.equals(game.blackUsername()))) {
            var errorMessage = new  ErrorMessage("Error: observer cannot resign the game");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        if (game.game().isEnded()) {
            var errorMessage = new  ErrorMessage("Error: the game is ended");
            sendMessage(session.getRemote(), errorMessage);
            return;
        }
        game.game().setEnded();
        mySqlGameDAO.updateGame(gameID, new Gson().toJson(game.game()));
        var message = String.format("'%s' resigned the game", userName);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(gameID, "", notificationMessage);
    }
}
