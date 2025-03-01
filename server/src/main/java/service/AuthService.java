package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import model.AuthData;

import java.util.UUID;

public class AuthService {
    private final MemoryAuthDAO memoryAuthDAO;

    public AuthService(MemoryAuthDAO memoryAuthDAO) {
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public AuthData addAuthToken(String username) throws DataAccessException {
        return memoryAuthDAO.createAuth(UUID.randomUUID().toString(), username);
    }

    public AuthData getAuthToken(String username) throws DataAccessException {
        return memoryAuthDAO.getAuth(username);
    }

    public void deleteAuthToken(String username) throws DataAccessException {
        memoryAuthDAO.deleteAuth(username);
    }
}
