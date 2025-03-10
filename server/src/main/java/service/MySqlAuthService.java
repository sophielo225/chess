package service;

import dataaccess.MySqlAuthDAO;
import exception.ResponseException;

public class MySqlAuthService {
    private final MySqlAuthDAO mySqlAuthDAO;

    public MySqlAuthService(MySqlAuthDAO mySqlAuthDAO) {
        this.mySqlAuthDAO = mySqlAuthDAO;
    }

    public void clear() throws ResponseException {
        mySqlAuthDAO.clear();
    }
}
