package client;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

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

    @BeforeAll
    public static void clear() throws ResponseException {
        facade.clear();
    }

    @Test
    @Order(1)
    @DisplayName("Normal user registration")
    public void registerPositive() throws Exception {
        var authData = facade.register(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        authToken = authData.authToken();
        Assertions.assertTrue(authData.authToken().length() > 10 && authData.username().equals("TestPlayer1"));
    }

    @Test
    @Order(2)
    @DisplayName("Register with null password")
    void registerNegative() throws Exception {
        var authData = facade.register(new UserData("TestPlayer2", "", "Player2@chess.com"));
        Assertions.assertNull(authData);
    }

    @Test
    @Order(3)
    @DisplayName("Normal user login")
    void loginPositive() throws Exception {
        var authData = facade.login(new UserData("TestPlayer1", "TestPassword1", "Player1@chess.com"));
        authToken = authData.authToken();
        Assertions.assertTrue(authData.authToken().length() > 10 && authData.username().equals("TestPlayer1"));
    }

    @Test
    @Order(4)
    @DisplayName("Login with wrong password")
    void loginNegative() throws Exception {
        var authData = facade.login(new UserData("TestPlayer1", "test", null));
        Assertions.assertNull(authData);
    }

    @Test
    @Order(5)
    @DisplayName("Normal logout")
    void logout() throws Exception {
        facade.logout();
    }
}
