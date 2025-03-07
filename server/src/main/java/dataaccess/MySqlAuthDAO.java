package dataaccess;

import model.AuthData;

public class MySqlAuthDAO implements AuthDAO{
    @Override
    public void clear() {

    }

    @Override
    public AuthData createAuth(String token, String username) throws DataAccessException {
        return null;
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String username) throws DataAccessException {

    }

    @Override
    public String isAuthorized(String authToken) {
        return "";
    }

    @Override
    public int getAuthSize() {
        return 0;
    }
}
