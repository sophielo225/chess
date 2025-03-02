package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;

public class GameService {

    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(ArrayList<GameData> games, String message) {}
    public record CreateGameRequest(String gameName) {}
    public record CreateGameResult(int gameID) {}
    public record JoinGameRequest(String color, int gameID) {}
    public record JoinGameResult(String message) {}

    private final MemoryAuthDAO memoryAuthDAO;
    private final MemoryGameDAO memoryGameDAO;

    public GameService(MemoryGameDAO memoryGameDAO, MemoryAuthDAO memoryAuthDAO) {
        this.memoryGameDAO = memoryGameDAO;
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public void clear() {
        memoryGameDAO.clear();
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ResponseException {
        if (memoryAuthDAO.isAuthorized(listGamesRequest.authToken()) != null) {
            return new ListGamesResult((ArrayList<GameData>) memoryGameDAO.listGames(), "OK");
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws ResponseException {
        if (memoryAuthDAO.isAuthorized(authToken) != null) {
            try {
                GameData newGame = memoryGameDAO.createGame(createGameRequest.gameName, authToken);
                return new CreateGameResult(newGame.gameID());
            } catch (DataAccessException e) {
                throw new ResponseException(400, "Error: bad request");
            }
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException {
        final String username = memoryAuthDAO.isAuthorized(authToken);
        if (username != null) {
            if (joinGameRequest.color == null)
                throw new ResponseException(400, "Error: bad request");
            try {
                if (memoryGameDAO.joinGame(joinGameRequest.color, username, joinGameRequest.gameID()))
                    return new JoinGameResult("OK");
                else
                    throw new ResponseException(400, "Error: bad request");
            } catch (DataAccessException e) {
                if (e.getMessage().equals("Already taken"))
                    throw new ResponseException(403, "Error: already taken");
                else
                    throw new ResponseException(400, "Error: bad request");
            }
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }
}
