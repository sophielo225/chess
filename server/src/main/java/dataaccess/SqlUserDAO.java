package dataaccess;

import exception.ResponseException;
import model.UserData;

public interface SqlUserDAO {
    void clear() throws ResponseException;
    UserData createUser(String username, String password, String email) throws ResponseException;
    UserData getUser(String username, String password) throws ResponseException;
    int getUserSize() throws ResponseException;
}
