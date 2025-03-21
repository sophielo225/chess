package dataaccess;

import chess.*;
import com.google.gson.*;
import exception.ResponseException;
import model.GameData;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class MySqlGameDAO implements SqlGameDAO, SqlDAO {

    public MySqlGameDAO() throws ResponseException {
        configureDatabase(CREATE_GAMES);
    }

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    private String isAlreadyTaken(String gameName) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameName FROM games WHERE gameName=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameName);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs).gameName();
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public GameData createGame(String gameName, String authToken) throws ResponseException {
        if (isAlreadyTaken(gameName) != null) {
            return null;
        }
        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        ChessGame chessGame = new ChessGame();
        var jsonGame = new  Gson().toJson(chessGame);
        var id = executeUpdate(statement, "", "", gameName, jsonGame);
        return new GameData(id, "", "", gameName, chessGame);
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("gameID");
        var whiteUsername = rs.getString("whiteUsername");
        if (whiteUsername.isEmpty()) {
            whiteUsername = null;
        }
        var blackUsername = rs.getString("blackUsername");
        if (blackUsername.isEmpty()) {
            blackUsername = null;
        }
        var gameName = rs.getString("gameName");
        var jsonGame = rs.getString("game");
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());
        Gson gson = gsonBuilder.create();
        var game = gson.fromJson(jsonGame, ChessGame.class);
        return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
    }

    @Override
    public GameData getGame(int gameID) throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws ResponseException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readGame(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    @Override
    public boolean joinGame(String color, String username, int gameID) throws ResponseException {
        GameData oldGame = getGame(gameID);
        if (oldGame == null) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (color.equals("WHITE")) {
            if (oldGame.whiteUsername() == null) {
                var statement = "UPDATE games SET whiteUsername=? WHERE gameID=?";
                executeUpdate(statement, username, gameID);
                return true;
            } else {
                throw new ResponseException(403, "Error: already taken");
            }
        } else if (color.equals("BLACK")) {
            if (oldGame.blackUsername() == null) {
                var statement = "UPDATE games SET blackUsername=? WHERE gameID=?";
                executeUpdate(statement, username, gameID);
                return true;
            } else {
                throw new ResponseException(403, "Error: already taken");
            }
        } else {
            throw new ResponseException(400, "Error: bad request");
        }
    }

    @Override
    public int getGameSize() throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return 0;
    }
}
