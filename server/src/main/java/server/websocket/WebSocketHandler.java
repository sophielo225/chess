package server.websocket;

import com.google.gson.Gson;
import dataaccess.MySqlAuthDAO;
import exception.ResponseException;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
                case CONNECT -> connect(session, userName, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, userName, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, userName, (LeaveGameCommand) command);
                case RESIGN -> resign(session, userName, (ResignCommand) command);
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

    private void connect(Session session, String userName, ConnectCommand command) throws IOException {
        connections.add(userName, session);
        var message = String.format("%s is connected", userName);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(userName, notificationMessage);
    }

    public void makeMove(Session session, String userName, MakeMoveCommand command) throws IOException {
        var message = String.format("%s made a move", userName);
        var loadGameMessage = new LoadGameMessage(message);
        connections.broadcast("", loadGameMessage);
    }

    private void leaveGame(Session session, String userName, LeaveGameCommand command) throws IOException {
        connections.remove(userName);
        var message = String.format("%s left the shop", userName);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(userName, notificationMessage);
    }

    private void resign(Session session, String userName, ResignCommand command) throws IOException {
        connections.remove(userName);
        var message = String.format("%s left the shop", userName);
        var notificationMessage = new NotificationMessage(message);
        connections.broadcast(userName, notificationMessage);
    }
}
