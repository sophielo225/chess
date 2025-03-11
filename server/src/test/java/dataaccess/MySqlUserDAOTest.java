package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTest {
    private MySqlUserDAO userDAO;

    @BeforeEach
    public void setUp() throws ResponseException {
        userDAO = new MySqlUserDAO();
        userDAO.clear();
    }

    @Test
    public void createUserPositive() throws ResponseException {
        String username = "testuser";
        String password = "testpassword";
        String email = "testemail";
        UserData result = userDAO.createUser(username, password, email);

        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(password, result.password());
        assertEquals(email, result.email());
    }

    @Test
    public void createUserNegative() throws ResponseException {
        String username = "testuser";
        userDAO.createUser(username, "testpassword", "testemail");

        assertThrows(ResponseException.class, () ->
                userDAO.createUser(username, "anotherpassword", "anotheremail"));
    }

    @Test
    public void getUserPositive() throws ResponseException {
        String username = "testuser";
        String password = "testpassword";
        String email = "testemail";
        userDAO.createUser(username, password, email);

        UserData result = userDAO.getUser(username, password);

        assertNotNull(result);
        assertEquals(username, result.username());
        assertEquals(email, result.email());
    }

    @Test
    public void getUserNegative() throws ResponseException {
        UserData result = userDAO.getUser("nonexistinguser", "password");

        assertNull(result);
    }

    @Test
    public void getUserSizePositive() throws ResponseException {
        assertEquals(0, userDAO.getUserSize());

        userDAO.createUser("testuser1", "testpassword1", "testemail1");
        userDAO.createUser("testuser2", "testpassword2", "testemail2");

        assertEquals(2, userDAO.getUserSize());

    }

    @Test
    public void clearPositive() throws ResponseException {
        userDAO.createUser("testuser1", "testpassword1", "testemail1");
        userDAO.createUser("testuser2", "testpassword2", "testemail2");

        userDAO.clear();

        assertEquals(0, userDAO.getUserSize());
    }
}
