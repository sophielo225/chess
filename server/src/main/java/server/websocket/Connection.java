package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Connection {
    public int gameID;
    public String userName;
    public Session session;

    public Connection(int gameID, String userName, Session session) {
        this.gameID = gameID;
        this.userName = userName;
        this.session = session;
    }

    public void send(String msg) throws IOException {
        session.getRemote().sendString(msg);
    }
}
