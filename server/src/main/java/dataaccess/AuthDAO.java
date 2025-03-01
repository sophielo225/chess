package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear();
    AuthData createAuth(String token, String username) throws DataAccessException;
    AuthData getAuth(String username) throws DataAccessException;
    void deleteAuth(String username) throws DataAccessException;
}
