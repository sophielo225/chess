package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import model.GameData;

import java.util.ArrayList;

public class GameService {

    public record ListGamesRequest(String username, String authToken) {}
    public record ListGamesResult(ArrayList<GameData> games, String message) {}
    public record CreateGameRequest(String username, String authToken, String gameName) {}
    public record CreateGameResult(int gameID, String message) {}
    public record JoinGameRequest(String username, String authToken, ChessGame.TeamColor color, int gameID) {}
    public record JoinGameResult(String message) {}

    private final MemoryAuthDAO memoryAuthDAO;
    private final MemoryGameDAO memoryGameDAO;

    public GameService(MemoryGameDAO memoryGameDAO, MemoryAuthDAO memoryAuthDAO) {
        this.memoryGameDAO = memoryGameDAO;
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException {
        if (memoryAuthDAO.getAuth(listGamesRequest.username).authToken().equals(listGamesRequest.authToken)) {
            return new ListGamesResult((ArrayList<GameData>) memoryGameDAO.listGames(), "OK");
        } else {
            return new ListGamesResult(null, "Error: unauthorized");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        if (memoryAuthDAO.getAuth(createGameRequest.username).authToken().equals(createGameRequest.authToken)) {
            GameData newGame = memoryGameDAO.createGame(createGameRequest.gameName, createGameRequest.authToken);
            return new CreateGameResult(newGame.gameID(), "OK");
        } else {
            return new CreateGameResult(0, "Error: unauthorized");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        if (memoryAuthDAO.getAuth(joinGameRequest.username).authToken().equals(joinGameRequest.authToken)) {
            memoryGameDAO.setPlayer(joinGameRequest.color, joinGameRequest.username, joinGameRequest.gameID());
            return new JoinGameResult("OK");
        } else {
            return new JoinGameResult("Error: unauthorized");
        }
    }
}
