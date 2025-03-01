package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO{
    private final HashSet<GameData> games = new HashSet<>();
    private int gameID = 1000;

    @Override
    public void clear() {
        games.clear();
    }

    @Override
    public GameData createGame(String gameName, String authToken) throws DataAccessException{
        GameData newGame = new GameData(gameID++, null, null, gameName, new ChessGame());
        if (games.contains(newGame)) {
            throw new DataAccessException("Game already taken");
        } else {
            games.add(newGame);
        }
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Invalid gameID");
    }

    @Override
    public Collection<GameData> listGames() {
        return new ArrayList<>(games);
    }

    @Override
    public GameData updateGame(int gameID, ChessGame game) throws DataAccessException {
        getGame(gameID).game().setTeamTurn(game.getTeamTurn());
        getGame(gameID).game().setBoard(game.getBoard());
        getGame(gameID).game().setPieceEnPassant(game.getPieceEnPassant());
        getGame(gameID).game().setCastlingPieces(game.getCastlingPieces());
        return getGame(gameID);
    }

}
