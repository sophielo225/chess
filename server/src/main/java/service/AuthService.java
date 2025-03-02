package service;

import dataaccess.MemoryAuthDAO;

public class AuthService {
    private final MemoryAuthDAO memoryAuthDAO;

    public AuthService(MemoryAuthDAO memoryAuthDAO) {
        this.memoryAuthDAO = memoryAuthDAO;
    }

    public void clear() {
        memoryAuthDAO.clear();
    }
}
