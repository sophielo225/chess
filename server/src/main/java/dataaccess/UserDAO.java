package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface UserDAO {
    void clear() throws ResponseException;
    UserData createUser(String username, String password, String email) throws DataAccessException, ResponseException;
    UserData getUser(String username, String password) throws DataAccessException, ResponseException;

    int getUserSize() throws ResponseException;
}
