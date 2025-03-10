package dataaccess;

import exception.ResponseException;
import model.AuthData;

public interface SqlAuthDAO {
    void clear() throws ResponseException;
    AuthData createAuth(String token, String username) throws ResponseException;
    AuthData getAuth(String username) throws ResponseException;
    void deleteAuth(String username) throws ResponseException;
    String isAuthorized(String authToken) throws ResponseException;
    int getAuthSize() throws ResponseException;
}
