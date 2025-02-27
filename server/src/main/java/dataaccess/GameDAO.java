package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Collection;

public interface GameDAO {
    void clear();
    GameData createGame(int gameId, String whiteUser, String blackUser, String name, ChessGame game);
    GameData getGame(int gameId);
    Collection<GameData> listGames();
    GameData updateGame(int gameId, ChessGame game);
    void deleteGame(int gameId);
}
