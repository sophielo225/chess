package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
    private final MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
    private final MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
    private final MemoryGameDAO memoryGameDAO = new MemoryGameDAO();
    private final UserService userService = new UserService(memoryUserDAO, memoryAuthDAO);
    private final AuthService authService = new AuthService(memoryAuthDAO);
    private final GameService gameService = new GameService(memoryGameDAO, memoryAuthDAO);

    @BeforeEach
    void clear() throws ResponseException {
        userService.clear();
        authService.clear();
        gameService.clear();
    }

    @Test
    void clearTest() throws DataAccessException {
        memoryUserDAO.createUser("testuser1", "testpassword1", "testemail1");
        memoryAuthDAO.createAuth("1234", "testuser1");
        memoryGameDAO.createGame("Chess Game 1", "1234");
        userService.clear();
        authService.clear();
        gameService.clear();

        assertEquals(0, memoryUserDAO.getUserSize());
        assertEquals(0, memoryAuthDAO.getAuthSize());
        assertEquals(0, memoryGameDAO.getGameSize());
    }

    @Test
    void registerPositiveTest() throws ResponseException, DataAccessException {
        UserService.RegisterRequest request = new UserService.RegisterRequest(
                "testUser", "testPassword", "testEmail");
        UserService.RegisterResult testResult = userService.register(request);
        UserData testUser = memoryUserDAO.getUser("testUser", "testPassword");
        AuthData testAuth = memoryAuthDAO.getAuth("testUser");

        assertEquals("testUser", testResult.username());
        assertEquals("OK", testResult.message());

        assertEquals("testUser", testUser.username());
        assertEquals("testUser", testAuth.username());
    }

    @Test
    void registerNegativeTest() {
        UserService.RegisterRequest request = new UserService.RegisterRequest(
                "testUser", null, "testEmail");
        assertThrows(ResponseException.class, () -> userService.register(request));
    }

    @Test
    void loginPositiveTest() throws DataAccessException, ResponseException {
        userService.register(new UserService.RegisterRequest("existingUser",
                "existingPassword", "existingEmail"));
        UserService.LoginRequest request = new UserService.LoginRequest("existingUser",
                "existingPassword");
        UserService.LoginResult result = userService.login(request);

        assertEquals("existingUser", result.username());
        assertEquals("OK", result.message());
        assertEquals(2, memoryAuthDAO.getAuthSize());
    }

    @Test
    void loginNegativeTest() throws DataAccessException {
        memoryUserDAO.createUser("existingUser", "existingPassword", "existingEmail");
        UserService.LoginRequest request = new UserService.LoginRequest("existingUser",
                "wrongPassword");
        assertThrows(ResponseException.class, () -> userService.login(request));
    }

    @Test
    void logoutPositiveTest() throws DataAccessException, ResponseException {
        userService.register(new UserService.RegisterRequest("existingUser",
                "existingPassword", "existingEmail"));
        AuthData auth = memoryAuthDAO.getAuth("existingUser");
        String authToken = auth.authToken();
        userService.logout(new UserService.LogoutRequest(authToken));

        assertEquals(0, memoryAuthDAO.getAuthSize());
    }

    @Test
    void logoutNegativeTest() {
        UserService.LogoutRequest request = new UserService.LogoutRequest("1234");

        assertThrows(ResponseException.class, () -> userService.logout(request));
    }

    @Test
    void listGamesPositiveTest() throws DataAccessException, ResponseException {
        memoryGameDAO.createGame("Game 1", "1234");
        memoryGameDAO.createGame("Game 2", "2345");
        memoryAuthDAO.createAuth("1345", "testUser");
        GameService.ListGamesResult result = gameService.listGames(new GameService.ListGamesRequest("1345"));

        assertEquals(memoryGameDAO.listGames(), result.games());
        assertEquals(2, result.games().size());
    }

    @Test
    void listGamesNegativeTest() {
        GameService.ListGamesRequest request = new GameService.ListGamesRequest(null);

        assertThrows(ResponseException.class, () -> gameService.listGames(request));
    }

    @Test
    void createGamePositiveTest() throws ResponseException, DataAccessException {
        memoryAuthDAO.createAuth("1234", "testUser");
        GameService.CreateGameRequest request = new GameService.CreateGameRequest("Game 3");
        GameService.CreateGameResult result = gameService.createGame(request, "1234");

        assertEquals(1, memoryGameDAO.getGameSize());
        assertEquals(request.gameName(), memoryGameDAO.getGame(result.gameID()).gameName());
    }

    @Test
    void createGameNegativeTest() {
        GameService.CreateGameRequest request = new GameService.CreateGameRequest("Game 3");

        assertThrows(ResponseException.class, () -> gameService.createGame(request, null));
    }

    @Test
    void joinGamePositiveTest() throws DataAccessException, ResponseException {
        memoryAuthDAO.createAuth("1234", "testUser");
        memoryUserDAO.createUser("testUser", "testPassword", "testEmail");
        GameService.CreateGameRequest createGameRequest = new GameService.CreateGameRequest("Game 3");
        GameService.CreateGameResult createGameResult = gameService.createGame(createGameRequest, "1234");
        GameService.JoinGameRequest joinGameRequest = new GameService.JoinGameRequest("BLACK",
                createGameResult.gameID());
        gameService.joinGame(joinGameRequest, "1234");

        GameData game = memoryGameDAO.getGame(createGameResult.gameID());
        assertEquals("testUser", game.blackUsername());
    }

    @Test
    void joinGameNegativeTest() throws DataAccessException, ResponseException {
        memoryAuthDAO.createAuth("1234", "testUser");
        GameService.CreateGameRequest createGameRequest = new GameService.CreateGameRequest("Game 3");
        GameService.CreateGameResult createGameResult = gameService.createGame(createGameRequest, "1234");
        GameService.JoinGameRequest joinGameRequest = new GameService.JoinGameRequest(null,
                createGameResult.gameID());

        assertThrows(ResponseException.class, () -> gameService.joinGame(joinGameRequest, "1234"));
    }
}
