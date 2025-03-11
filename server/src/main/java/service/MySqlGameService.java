package service;

import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlGameDAO;
import exception.ResponseException;
import model.GameData;

import java.util.ArrayList;

public class MySqlGameService {

    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(ArrayList<GameData> games, String message) {}
    public record CreateGameRequest(String gameName) {}
    public record CreateGameResult(int gameID) {}
    public record JoinGameRequest(String color, int gameID) {}
    public record JoinGameResult(String message) {}

    private final MySqlAuthDAO mySqlAuthDAO;
    private final MySqlGameDAO mySqlGameDAO;

    public MySqlGameService(MySqlGameDAO mySqlGameDAO, MySqlAuthDAO mySqlAuthDAO) {
        this.mySqlGameDAO = mySqlGameDAO;
        this.mySqlAuthDAO = mySqlAuthDAO;
    }

    public void clear() throws ResponseException {
        mySqlGameDAO.clear();
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws ResponseException {
        if (mySqlAuthDAO.isAuthorized(listGamesRequest.authToken()) != null) {
            return new ListGamesResult((ArrayList<GameData>) mySqlGameDAO.listGames(), "OK");
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest, String authToken) throws ResponseException {
        if (mySqlAuthDAO.isAuthorized(authToken) != null) {
            GameData newGame = mySqlGameDAO.createGame(createGameRequest.gameName, authToken);
            if (newGame == null) {
                throw new ResponseException(400, "Error: bad request");
            }
            return new CreateGameResult(newGame.gameID());
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest, String authToken) throws ResponseException {
        final String username = mySqlAuthDAO.isAuthorized(authToken);
        if (username != null) {
            if (joinGameRequest.color == null) {
                throw new ResponseException(400, "Error: bad request");
            }
            if (mySqlGameDAO.joinGame(joinGameRequest.color, username, joinGameRequest.gameID())) {
                return new JoinGameResult("OK");
            } else {
                throw new ResponseException(400, "Error: bad request");
            }
        } else {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }
}
