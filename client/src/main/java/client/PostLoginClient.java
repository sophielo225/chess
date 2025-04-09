package client;

import exception.ResponseException;
import model.GameData;

import java.util.Arrays;
import java.util.HashMap;

public class PostLoginClient implements ChessClient {
    private final ServerFacade server;
    private String joinedColor;
    private int gameID = 0;
    private final HashMap<Integer, GameData> gameMap = new HashMap<>();

    public PostLoginClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list();
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout();
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public int getGameID() { return gameID; }

    public String getColor() { return joinedColor; }

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            gameID = server.create(gameName);
            if (gameID > 0) {
                return String.format("Game %s created.", gameName);
            } else {
                return String.format("Failed to create %s", gameName);
            }
        }
        throw new ResponseException(400, "Expected: <NAME>");
    }

    public String list() throws ResponseException {
        var games = server.list();
        var result = new StringBuilder();
        if (!games.isEmpty()) {
            gameMap.clear();
        }
        int i = 1;
        for (var game: games) {
            gameMap.put(i, game);
            result.append(String.format("%d Name: [%s] White Player: [%s] Black Player: [%s]", i,
                    game.gameName(), game.whiteUsername() != null ? game.whiteUsername() : "",
                    game.blackUsername() != null ? game.blackUsername() : "")).append('\n');
            i++;
        }
        return result.toString();
    }

    private int validateInputNumber(String number) throws ResponseException {
        int choice;
        try {
            choice = Integer.parseInt(number);
        } catch (NumberFormatException e) {
            throw new ResponseException(400, String.format("Wrong input %s", number));
        }
        if ((choice < 1) || (choice > gameMap.size())) {
            throw new ResponseException(400, "Please list games first.");
        }
        return choice;
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2) {
            int choice = validateInputNumber(params[0]);
            String teamColor = params[1];
            if (teamColor.equals("WHITE") || teamColor.equals("BLACK")) {
                joinedColor = teamColor;
                GameData gameData = gameMap.get(choice);
                if (gameData == null) {
                    return "Please list games first.";
                }
                gameID = gameData.gameID();
                server.join(teamColor, gameID);
                return String.format("You join game %d.", choice);
            }
            return String.format("Wrong color %s", teamColor);
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            int choice = validateInputNumber(params[0]);
            GameData gameData = gameMap.get(choice);
            if (gameData == null) {
                return "Please list games first.";
            }
            gameID = gameData.gameID();
            joinedColor = "WHITE";
            return String.format("You observe game %d.", choice);
        }
        throw new ResponseException(400, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
        gameID = 0;
        server.logout();
        return "You signed out.";
    }

    public String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - when you are done
                help - with possible commands
                """;
    }
}
