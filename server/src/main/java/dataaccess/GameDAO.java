package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear();
    GameData createGame(String gameName, String authToken) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Collection<GameData> listGames();
    boolean joinGame(String color, String username, int gameID) throws DataAccessException;
    GameData updateGame(ChessGame.TeamColor color, String username, int gameID) throws DataAccessException;
}
