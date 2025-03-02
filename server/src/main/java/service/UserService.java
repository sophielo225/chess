package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String username, String authToken, String message) {}
    public record LoginRequest(String username, String password){}
    public record LoginResult(String username, String authToken, String message) {}
    public record LogoutRequest(String authToken) {}
    public record LogoutResult(String message) {}

    private final MemoryUserDAO memoryUserDAO;
    private final MemoryAuthDAO memoryAuthDAO;

    public UserService(MemoryUserDAO memoryUserDAO, MemoryAuthDAO memoryAuthDAO) {
        this.memoryUserDAO = memoryUserDAO;
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public void clear() {
        memoryUserDAO.clear();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException, DataAccessException {
        if ((registerRequest.username() == null) || (registerRequest.password() == null) ||
                (registerRequest.email() == null)) {
            throw new ResponseException(400, "Error: bad request");
        } try {
            memoryUserDAO.getUser(registerRequest.username(), registerRequest.password());
            throw new ResponseException(403, "Error: already taken");
        } catch (DataAccessException e) {
            UserData user = memoryUserDAO.createUser(registerRequest.username(), registerRequest.password(),
                                                                                             registerRequest.email());
            memoryAuthDAO.createAuth(generateToken(), user.username());
            return new RegisterResult(user.username(), memoryAuthDAO.getAuth(user.username()).authToken(), "OK");
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        try {
            UserData user = memoryUserDAO.getUser(loginRequest.username(), loginRequest.password());
            AuthData existingAuth = memoryAuthDAO.getAuth(user.username());
            AuthData authData = memoryAuthDAO.createAuth(generateToken(), user.username());
            if (existingAuth.equals(authData)) {
                throw new ResponseException(401, "Error: unauthorized");
            }
            return new LoginResult(user.username(), authData.authToken(), "OK");
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) throws ResponseException, DataAccessException {
        if (memoryAuthDAO.isAuthorized(logoutRequest.authToken()) == null) {
            throw new ResponseException(401, "Error: unauthorized");
        }
        memoryAuthDAO.deleteAuth(logoutRequest.authToken);
        return new LogoutResult("OK");
    }
}
