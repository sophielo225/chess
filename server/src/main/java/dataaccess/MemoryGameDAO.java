package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    private final HashSet<GameData> games = new HashSet<>();
    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public GameData createGame(int gameID, String whiteUsername, String blackUsername,
                               String gameName, ChessGame game) {
        GameData newGame = new GameData(gameID, whiteUsername, blackUsername, gameName, game);
        if (games.contains(newGame)) {
            return null;
        } else {
            games.add(newGame);
        }
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return new ArrayList<>(games);
    }

    @Override
    public GameData updateGame(int gameID, ChessGame game) {
        return null;
    }

    @Override
    public void deleteGame(int gameID) {
        games.removeIf(game -> game.gameID() == gameID);
    }
}
