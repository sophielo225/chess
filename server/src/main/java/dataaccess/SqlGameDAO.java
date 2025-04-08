package dataaccess;

import exception.ResponseException;
import model.GameData;

import java.util.Collection;

public interface SqlGameDAO {
    void clear() throws ResponseException;
    GameData createGame(String gameName, String authToken) throws ResponseException;
    GameData getGame(int gameID) throws ResponseException;
    Collection<GameData> listGames() throws ResponseException;
    boolean joinGame(String color, String username, int gameID) throws ResponseException;
    int getGameSize() throws ResponseException;
    void updateGame(int gameID, String game) throws ResponseException;
    void leaveGame(String color, int gameID) throws ResponseException;
}
