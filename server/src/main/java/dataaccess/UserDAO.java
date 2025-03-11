package dataaccess;

import model.UserData;

public interface UserDAO {
    void clear();
    UserData createUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username, String password) throws DataAccessException;
    int getUserSize();
}
