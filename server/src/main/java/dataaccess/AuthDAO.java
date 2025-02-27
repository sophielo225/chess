package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void clear();
    AuthData createAuth(String token, String username);
    AuthData getAuth(String username);
    void deleteAuth(String username);
}
