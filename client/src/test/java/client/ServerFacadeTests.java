package client;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {
    private static Server server;
    static ServerFacade facade;
    private String authToken;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws ResponseException {
        facade.clear();
    }

    @Test
    @DisplayName("Normal user registration")
    public void registerPositive() throws Exception {
        var authData = facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        authToken = authData.authToken();
        Assertions.assertTrue(authData.authToken().length() > 10 && authData.username().equals("TestPlayer1"));
    }

    @Test
    @DisplayName("Register with null password")
    void registerNegative() throws Exception {
        var authData = facade.register(new UserData("TestPlayer2", "", "Player2@chess.com"));
        Assertions.assertNull(authData);
    }

    @Test
    @DisplayName("Normal user login")
    void loginPositive() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        var authData = facade.login(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        authToken = authData.authToken();
        Assertions.assertTrue(authData.authToken().length() > 10 && authData.username().equals("TestPlayer1"));
    }

    @Test
    @DisplayName("Login with wrong password")
    void loginNegative() throws Exception {
        var authData = facade.login(new UserData("TestPlayer1", "test", null));
        Assertions.assertNull(authData);
    }

    @Test
    @DisplayName("Clear database successfully")
    void clearPositive() throws Exception {
        facade.clear();
        var authData = facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        Assertions.assertTrue(authData.authToken().length() > 10 && authData.username().equals("TestPlayer1"));
    }

    @Test
    @DisplayName("Logout successfully")
    void logoutPositive() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        var authData = facade.login(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        Assertions.assertNotNull(authData);

        facade.logout();

        authData = facade.login(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        Assertions.assertNotNull(authData);
    }

    @Test
    @DisplayName("Logout with invalid authorization")
    void logoutNegative() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        facade.logout();
        Assertions.assertThrows(ResponseException.class, () -> facade.logout());
    }

    @Test
    @DisplayName("Create game successfully")
    void createPositive() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        var authData = facade.login(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        Assertions.assertNotNull(authData);

        int gameID = facade.create("TestGame1");
        Assertions.assertTrue(gameID > 0);
    }

    @Test
    @DisplayName("Create game with invalid authorization")
    void createNegative() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        var authData = facade.login(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        Assertions.assertNotNull(authData);

        facade.logout();

        int gameID = facade.create("TestGame2");
        Assertions.assertEquals(0, gameID);
    }

    @Test
    @DisplayName("List games successfully")
    void listPositive() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));

        int gameID = facade.create("TestGame1");
        List<GameData> games = facade.list();

        Assertions.assertFalse(games.isEmpty());

        boolean found = false;
        for (GameData game : games) {
            if (game.gameID() == gameID && game.gameName().equals("TestGame1")) {
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found);
    }

    @Test
    @DisplayName("List games with invalid authorization")
    void listNegative() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));

        facade.logout();

        Assertions.assertThrows(ResponseException.class, () -> facade.list());
    }

    @Test
    @DisplayName("Join game successfully")
    void joinPositive() throws Exception {
        var authData = facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        Assertions.assertNotNull(authData);

        int gameID = facade.create("TestGame2");
        facade.join("WHITE", gameID);
        List<GameData> games = facade.list();

        boolean correctlyJoined = false;
        for (GameData game : games) {
            if (game.gameID() == gameID && "TestPlayer1".equals(game.whiteUsername())) {
                correctlyJoined = true;
                break;
            }
        }
        Assertions.assertTrue(correctlyJoined);
    }

    @Test
    @DisplayName("Join nonexistent game")
    void joinNegative() throws Exception {
        facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        int invalidGameID = 99999;
        Assertions.assertThrows(ResponseException.class, () -> facade.join("WHITE", invalidGameID));
    }
}
