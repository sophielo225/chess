package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import server.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;

import static ui.EscapeSequences.*;

public class PostLoginClient implements ChessClient {
    private final ServerFacade server;
    private String joinedColor;
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

    public String create(String... params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            int gameID = server.create(gameName);
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

    private void drawRow(ChessBoard board, int row, ChessGame.TeamColor color) {
        String rowNo = String.format(" %d ", row);
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + rowNo);
        String bgColor;
        if (color == ChessGame.TeamColor.WHITE) {
            bgColor = ((row % 2) == 0) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
        } else {
            bgColor = ((row % 2) == 0) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
        }
        for (int col = 1; col <= 8; col++) {
            ChessPosition position = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(position);
            if (piece == null) {
                System.out.print(bgColor + "   ");
                bgColor = bgColor.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
                continue;
            }
            String textColor = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
            String squarePattern = bgColor + textColor;
            switch (piece.getPieceType()) {
                case KING -> System.out.print(squarePattern + " K ");
                case QUEEN -> System.out.print(squarePattern + " Q ");
                case BISHOP -> System.out.print(squarePattern + " B ");
                case KNIGHT -> System.out.print(squarePattern + " N ");
                case ROOK -> System.out.print(squarePattern + " R ");
                case PAWN -> System.out.print(squarePattern + " P ");
            }
            bgColor = bgColor.equals(SET_BG_COLOR_WHITE) ? SET_BG_COLOR_BLACK : SET_BG_COLOR_WHITE;
        }
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + rowNo);
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void drawHeader(char[] header, boolean reversed) {
        System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK);
        if (reversed) {
            for (int i = header.length - 1; i >= 0; i--) {
                System.out.print(' ');
                System.out.print(header[i]);
                System.out.print(' ');
            }
        } else {
            for (char c : header) {
                System.out.print(' ');
                System.out.print(c);
                System.out.print(' ');
            }
        }
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private void drawBoard(int choice) {
        GameData gameData = gameMap.get(choice);
        ChessBoard board = gameData.game().getBoard();
        char[] header = { ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', ' ' };
        if (joinedColor.equals("WHITE")) {
            drawHeader(header, false);
            for (int row = 8; row > 0; row--) {
                drawRow(board, row, ChessGame.TeamColor.WHITE);
            }
            drawHeader(header, false);
        } else {
            drawHeader(header, true);
            for (int row = 1; row <= 8; row++) {
                drawRow(board, row, ChessGame.TeamColor.BLACK);
            }
            drawHeader(header, true);
        }
    }

    public String join(String... params) throws ResponseException {
        if (params.length == 2) {
            var choice = Integer.parseInt(params[0]);
            if (choice == 0) {
                return "Please list games first.";
            }
            String teamColor = params[1];
            joinedColor = teamColor;
            GameData gameData = gameMap.get(choice);
            if (gameData == null) {
                return "Please list games first.";
            }
            var gameID = gameData.gameID();
            server.join(teamColor, gameID);
            drawBoard(choice);
            return String.format("You join game %d.", choice);
        }
        throw new ResponseException(400, "Expected: <ID> [WHITE|BLACK]");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            var choice = Integer.parseInt(params[0]);
            if (choice == 0) {
                return "Please list games first.";
            }
            GameData gameData = gameMap.get(choice);
            if (gameData == null) {
                return "Please list games first.";
            }
            joinedColor = "WHITE";
            drawBoard(choice);
            return String.format("You observe game %d.", choice);
        }
        throw new ResponseException(400, "Expected: <ID>");
    }

    public String logout() throws ResponseException {
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
