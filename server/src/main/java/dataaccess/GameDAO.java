package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear();
    GameData createGame(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game);
    GameData getGame(int gameID);
    Collection<GameData> listGames();
    GameData updateGame(int gameID, ChessGame game);
    void deleteGame(int gameID);
}
