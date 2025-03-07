package dataaccess;

import model.UserData;

public class MySqlUserDAO implements UserDAO{
    @Override
    public void clear() {

    }

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        return null;
    }

    @Override
    public int getUserSize() {
        return 0;
    }
}
