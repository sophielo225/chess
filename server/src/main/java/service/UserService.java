package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.ErrorResult;
import service.result.LoginResult;
import service.result.RegisterResult;

import java.util.UUID;

public class UserService {

    private final MemoryUserDAO memoryUserDAO;

    public UserService(MemoryUserDAO memoryUserDAO) {
        this.memoryUserDAO = memoryUserDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException, DataAccessException {
        try {
            memoryUserDAO.getUser(registerRequest.username(), registerRequest.password());
            throw new ResponseException(403, "Error: already taken");
        } catch (DataAccessException e) {
            UserData user = memoryUserDAO.createUser(registerRequest.username(), registerRequest.password(),
                    registerRequest.email());
            
            return new RegisterResult(user.username(), auth.authToken());
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws ResponseException {
        try {
            UserData user = memoryUserDAO.getUser(loginRequest.username(), loginRequest.password());
            MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
            AuthData auth = memoryAuthDAO.createAuth(UUID.randomUUID().toString(), user.username());
            return new LoginResult(user.username(), auth.authToken());
        } catch (DataAccessException e) {
            throw new ResponseException(401, "Error: unauthorized");
        }
    }

    public void logout(LogoutRequest logoutRequest) throws ResponseException {
        try {
            MemoryAuthDAO
        }
    }
}
