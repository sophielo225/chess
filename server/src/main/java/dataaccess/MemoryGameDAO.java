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
        getGame(gameID).game().setTeamTurn(game.getTeamTurn());
        getGame(gameID).game().setBoard(game.getBoard());
        getGame(gameID).game().setPieceEnPassant(game.getPieceEnPassant());
        getGame(gameID).game().setCastlingPieces(game.getCastlingPieces());
        return getGame(gameID);
    }
}
