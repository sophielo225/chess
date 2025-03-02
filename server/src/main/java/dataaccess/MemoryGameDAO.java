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
            throw new DataAccessException("Already taken");
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
    public boolean joinGame(String color, String username, int gameID) throws DataAccessException {
        GameData newGame, oldGame = getGame(gameID);
        if (color.equals("WHITE")) {
            if (oldGame.whiteUsername() == null)
                newGame = oldGame.withWhiteUsername(username);
            else
                throw new DataAccessException("Already taken");
        } else if (color.equals("BLACK")) {
            if (oldGame.blackUsername() == null)
                newGame = oldGame.withBlackUsername(username);
            else
                throw new DataAccessException("Already taken");
        } else
            throw new DataAccessException("Invalid color");
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                games.remove(game);
                games.add(newGame);
                return true;
            }
        }
        // wrong gameID
        return false;
    }

    @Override
    public GameData updateGame(ChessGame.TeamColor color, String username, int gameID) throws DataAccessException {
        GameData newGame;
        if (color == ChessGame.TeamColor.WHITE)
            newGame = getGame(gameID).withWhiteUsername(username);
        else
            newGame = getGame(gameID).withBlackUsername(username);
        for (GameData game : games) {
            if (game.gameID() == gameID) {
                games.remove(game);
                break;
            }
        }
        games.add(newGame);
/*        getGame(gameID).game().setTeamTurn(game.getTeamTurn());
        getGame(gameID).game().setBoard(game.getBoard());
        getGame(gameID).game().setPieceEnPassant(game.getPieceEnPassant());
        getGame(gameID).game().setCastlingPieces(game.getCastlingPieces());*/
        return getGame(gameID);
    }
}
