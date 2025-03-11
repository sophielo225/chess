package dataaccess;

import exception.ResponseException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTest {
    private MySqlAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws ResponseException {
        authDAO = new MySqlAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAuthPositive() throws ResponseException {
        String token = "testtoken";
        String username = "testusername";

        AuthData result = authDAO.createAuth(token, username);

        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(token, result.authToken());
    }

    @Test
    public void createUserNegative() throws ResponseException {
        String username = "testusername";
        String token = "testtoken";
        authDAO.createAuth(token, username);

        assertThrows(ResponseException.class, () ->
                authDAO.createAuth(token, username));
    }

    @Test
    public void getAuthPositive() throws ResponseException {
        String token = "testtoken";
        String username = "testusername";
        authDAO.createAuth(token,username);

        AuthData result = authDAO.getAuth(username);

        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(token, result.authToken());
    }

    @Test
    public void getAuthNegative() throws ResponseException {
        AuthData result = authDAO.getAuth("nonexistinguser");

        assertNull(result);
    }

    @Test
    public void deleteAuthPositive() throws ResponseException {
        String token = "testtoken";
        String username = "testusername";
        authDAO.createAuth(token, username);

        authDAO.deleteAuth(token);

        AuthData result = authDAO.getAuth(username);
        assertNull(result);
    }

    @Test
    public void deleteAuthNegative() throws ResponseException {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistingtoken"));
    }

    @Test
    public void isAuthorizedPositive() throws ResponseException {
        String token = "testtoken";
        String username = "testusername";
        authDAO.createAuth(token, username);

        String result = authDAO.isAuthorized(token);

        assertEquals(username, result);
    }

    @Test
    public void isAuthorizedNegative() throws ResponseException {
        String result = authDAO.isAuthorized("invalidtoken");

        assertNull(result);
    }

    @Test
    public void getAuthSizePositive() throws ResponseException {
        assertEquals(0, authDAO.getAuthSize());

        authDAO.createAuth("testtoken1", "testusername1");
        authDAO.createAuth("testtoken2", "testusername2");

        assertEquals(2, authDAO.getAuthSize());
    }

    @Test
    public void clearPositive() throws ResponseException {
        authDAO.createAuth("testtoken1", "testusername1");
        authDAO.createAuth("testtoken2", "testusername2");

        authDAO.clear();

        assertEquals(0, authDAO.getAuthSize());
    }
}
