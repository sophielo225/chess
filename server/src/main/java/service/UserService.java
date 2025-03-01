package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
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

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        try {
            memoryUserDAO.getUser(registerRequest.username(), registerRequest.password());
            return new RegisterResult(null, null, "Error: already taken");
        } catch (DataAccessException e) {
            UserData user = memoryUserDAO.createUser(registerRequest.username(), registerRequest.password(),
                    registerRequest.email());
            memoryAuthDAO.createAuth(generateToken(), user.username());
            return new RegisterResult(user.username(), memoryAuthDAO.getAuth(user.username()).authToken(), "OK");
        }
    }

    public LoginResult login(LoginRequest loginRequest) {
        try {
            UserData user = memoryUserDAO.getUser(loginRequest.username(), loginRequest.password());
            memoryAuthDAO.createAuth(generateToken(), user.username());
            return new LoginResult(user.username(), memoryAuthDAO.getAuth(user.username()).authToken(), "OK");
        } catch (DataAccessException e) {
            return new LoginResult(null, null, "Error: unauthorized");
        }
    }

    public LogoutResult logout(LogoutRequest logoutRequest) {
        try {
            memoryAuthDAO.deleteAuth(logoutRequest.authToken);
            return new LogoutResult("OK");
        } catch (DataAccessException e) {
            return new LogoutResult("Error: unauthorized");
        }
    }
}
