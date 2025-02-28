package service;

import dataaccess.MemoryUserDAO;
import model.UserData;
import service.request.LoginRequest;
import service.request.LogoutRequest;
import service.request.RegisterRequest;
import service.result.ErrorResult;
import service.result.LoginResult;
import service.result.RegisterResult;

public class UserService {

    private final MemoryUserDAO memoryUserDAO;

    public UserService(MemoryUserDAO memoryUserDAO) {
        this.memoryUserDAO = memoryUserDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData user = memoryUserDAO.createUser(registerRequest.username(), registerRequest.password(),
                registerRequest.email());

    }
    public LoginResult login(LoginRequest loginRequest) {}
    public void logout(LogoutRequest logoutRequest) {}
}
