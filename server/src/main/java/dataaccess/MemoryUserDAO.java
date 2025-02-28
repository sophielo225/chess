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
    public UserData createUser(String username, String password, String email) {
        UserData newUser = new UserData(username, password, email);
        if (users.contains(newUser)) {
            return null;
        }
        else {
            users.add(newUser);
        }
        return newUser;
    }

    @Override
    public UserData getUser(String username, String password) {
        for (UserData user : users) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        return null;
    }
}
