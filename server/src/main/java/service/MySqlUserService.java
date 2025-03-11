package service;

import dataaccess.MySqlAuthDAO;
import dataaccess.MySqlUserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class MySqlUserService {

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken, String message) {}
    public record LoginRequest(String username, String password){}
    public record LoginResult(String username, String authToken, String message) {}
    public record LogoutRequest(String authToken) {}
    public record LogoutResult(String message) {}

    private final MySqlUserDAO mySqlUserDAO;
    private final MySqlAuthDAO mySqlAuthDAO;

    public MySqlUserService(MySqlUserDAO mySqlUserDAO, MySqlAuthDAO mySqlAuthDAO) {
        this.mySqlUserDAO = mySqlUserDAO;
        this.mySqlAuthDAO = mySqlAuthDAO;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void clear() throws ResponseException {
        mySqlUserDAO.clear();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException {
        if ((registerRequest.username() == null) || (registerRequest.password() == null) ||
                (registerRequest.email() == null)) {
            throw new ResponseException(400, "Error: bad request");
        }
        if (mySqlUserDAO.getUser(registerRequest.username(), registerRequest.password()) != null) {
            throw new ResponseException(403, "Error: already taken");
        }
        UserData user = mySqlUserDAO.createUser(registerRequest.username(), registerRequest.password(),
                registerRequest.email());
        AuthData authData = mySqlAuthDAO.createAuth(generateToken(), user.username());
        return new RegisterResult(user.username(), authData.authToken(), "OK");
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        UserData user = mySqlUserDAO.getUser(loginRequest.username(), loginRequest.password());
        if (user == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        AuthData authData = mySqlAuthDAO.createAuth(generateToken(), user.username());
        return new LoginResult(user.username(), authData.authToken(), "OK");
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws ResponseException {
        if (mySqlAuthDAO.isAuthorized(logoutRequest.authToken()) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        mySqlAuthDAO.deleteAuth(logoutRequest.authToken);
        return new LogoutResult("OK");
    }
}
