package dataaccess;

import model.AuthData;
import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO{
    private final HashSet<AuthData> auths = new HashSet<>();

    @Override
    public void clear() {
        auths.clear();
    }

    @Override
    public AuthData createAuth(String token, String username) throws DataAccessException {
        AuthData newAuth = new AuthData(token, username);
        if (auths.contains(newAuth)) {
            throw new DataAccessException("Authtoken already taken");
        } else {
            auths.add(newAuth);
        }
        return newAuth;
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        for (AuthData auth : auths) {
            if (auth.username().equals(username)) {
                return auth;
            }
        }
        throw new DataAccessException("Invalid authtoken");
    }

    @Override
    public void deleteAuth(String username) {
        auths.removeIf(auth -> auth.username().equals(username));
    }
}
