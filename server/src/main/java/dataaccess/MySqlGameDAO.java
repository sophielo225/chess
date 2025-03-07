package dataaccess;

import model.GameData;

import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO{
    @Override
    public void clear() {

    }

    @Override
    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public boolean joinGame(String color, String username, int gameID) throws DataAccessException {
        return false;
    }

    @Override
    public int getGameSize() {
        return 0;
    }
}
