package dataaccess;

import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    private final HashSet<UserData> users = new HashSet<>();

    @Override
    public void clear() {
        users.clear();
    }

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        UserData newUser = new UserData(username, password, email);
        if (users.contains(newUser)) {
            throw new DataAccessException("User already taken");
        }
        else {
            users.add(newUser);
        }
        return newUser;
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        for (UserData user : users) {
            if (user.username().equals(username) && user.password().equals(password)) {
                return user;
            }
        }
        throw new DataAccessException("Invalid username or password");
    }

    @Override
    public int getUserSize() {
        return users.size();
    }
}
