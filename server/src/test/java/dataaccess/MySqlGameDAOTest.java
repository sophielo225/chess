package dataaccess;

import exception.ResponseException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTest {
    private MySqlGameDAO gameDAO;

    @BeforeEach
    public void setUp() throws ResponseException {
        gameDAO = new MySqlGameDAO();
        gameDAO.clear();
    }

    @Test
    public void createGamePositive() throws ResponseException {
        String gameName = "testgame";
        String authToken = "testtoken";

        GameData result = gameDAO.createGame(gameName, authToken);

        assertNotNull(result);
        assertEquals(gameName, result.gameName());
        assertEquals("", result.whiteUsername());
        assertEquals("", result.blackUsername());
        assertNotNull(result.game());
    }

    @Test
    public void createGameNegative() throws ResponseException {
        String gameName = "testgame";
        gameDAO.createGame(gameName, "testtoken1");
        assertThrows(ResponseException.class, () -> {
            gameDAO.createGame(gameName, "testtoken2");
        });
    }

    @Test
    public void getGamePositive() throws ResponseException {
        String gameName = "testgame";
        GameData createdGame = gameDAO.createGame(gameName, "testtoken1");

        GameData result = gameDAO.getGame(createdGame.gameID());

        assertNotNull(result);
        assertEquals(createdGame.gameID(), result.gameID());
        assertEquals(gameName, result.gameName());
    }

    @Test
    public void getGameNegative() throws ResponseException {
        GameData result = gameDAO.getGame(999);

        assertNull(result);
    }

    @Test
    public void listGamesPositive() throws ResponseException {
        gameDAO.createGame("testgame1", "testtoken1");
        gameDAO.createGame("testgame2", "testtoken2");

        Collection<GameData> result = gameDAO.listGames();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void joinGamePositive() throws ResponseException {
        GameData game = gameDAO.createGame("testgame", "testtoken1");
        String username = "testusername";

        boolean result = gameDAO.joinGame("WHITE", username, game.gameID());

        assertTrue(result);

        GameData updatedGame = gameDAO.getGame(game.gameID());
        assertEquals(username, updatedGame.whiteUsername());
    }

    @Test
    public void joinGameNegative() throws ResponseException {
        GameData game = gameDAO.createGame("testgame", "testtoken1");

        gameDAO.joinGame("WHITE", "testusername1", game.gameID());

        assertThrows(ResponseException.class, () ->
                gameDAO.joinGame("WHITE", "testusername2", game.gameID())
        );
    }

    @Test
    public void getGameSizePositive() throws ResponseException {
        assertEquals(0, gameDAO.getGameSize());

        gameDAO.createGame("testgame1", "testtoken1");
        gameDAO.createGame("testgame2", "testtoken2");

        assertEquals(2, gameDAO.getGameSize());
    }

    @Test
    public void clearGamesPositive() throws ResponseException {
        gameDAO.createGame("testgame1", "testtoken1");
        gameDAO.createGame("testgame2", "testtoken2");

        gameDAO.clear();

        assertEquals(0, gameDAO.getGameSize());
    }
}
