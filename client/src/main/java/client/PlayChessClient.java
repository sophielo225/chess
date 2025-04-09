package client;

import chess.*;
import client.websocket.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import exception.ResponseException;
import websocket.messages.*;

import java.util.ArrayList;
import java.util.Arrays;

import static ui.EscapeSequences.*;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PlayChessClient implements ChessClient, ServerMessageObserver {
    private int gameID = 0;
    private ChessGame game = null;
    private String authToken = null;
    private String joinedColor = "WHITE";
    private WebsocketCommunicator wsc;
    private final String serverUrl;
    Square[][] squares = new Square[8][8];

    private static class SquarePattern {
        String bgColor;
        String textColor;
    }

    private static class Square {
        SquarePattern pattern;
        ChessPiece piece;
    }

    public PlayChessClient(String serverUrl) {
        this.serverUrl = serverUrl;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col] = new Square();
            }
        }
    }

    public String eval(String input) {
        try {
            var tokens = input.split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "redraw" -> redraw(params);
                case "leave" -> leaveGame(params);
                case "move" -> makeMove(params);
                case "resign" -> resign(params);
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public void connect(String authToken, int gameID) throws ResponseException {
        this.authToken = authToken;
        this.gameID = gameID;
        wsc = new WebsocketCommunicator(serverUrl, this);
        wsc.connect(authToken, gameID);
    }

    public void setColor(String color) { joinedColor = color; }

    public String redraw(String... params) throws ResponseException {
        drawBoard();
        return "";
    }

    public String leaveGame(String... params) throws ResponseException {
        wsc.leaveGame(authToken, gameID);
        gameID = 0;
        authToken = null;
        return "You left the game";
    }

    public String makeMove(String... params) throws ResponseException {
        if (params.length == 2) {
            String startStr = params[0];
            char[] start = startStr.toCharArray();
            String endStr = params[1];
            char[] end = endStr.toCharArray();
            if ((start.length == 2) && (start[0] >= 'a') && (start[0] <= 'h') && (start[1] >= '1') && (start[1] <= '8')) {
                ChessPosition startPos = new ChessPosition(start[1] - '0', start[0] - 'a' + 1);
                ChessPosition endPos = new ChessPosition(end[1] - '0', end[0] - 'a' + 1);
                if (game.isEnded()) {
                    return "The game is ended";
                }
                wsc.makeMove(authToken, gameID, new ChessMove(startPos, endPos, null));
            } else {
                return "Wrong start/end position format";
            }
        }
        throw new ResponseException(400, "Expected: <start> <end>");
    }

    public String resign(String... params) throws ResponseException {
        wsc.resign(authToken, gameID);
        return "You resigned the game";
    }

    public String highlight(String... params) throws ResponseException {
        if (params.length == 1) {
            String startStr = params[0];
            char[] start = startStr.toCharArray();
            if ((start.length == 2) && (start[0] >= 'a') && (start[0] <= 'h') && (start[1] >= '1') && (start[1] <= '8')) {
                ChessPosition startPos = new ChessPosition(start[1] - '0', start[0] - 'a' + 1);
                if (game.getBoard().getPiece(startPos) == null) {
                    return "No piece at the position";
                }
                ArrayList<ChessMove> moves = (ArrayList<ChessMove>) game.validMoves(startPos);
                /* Set each move with highlight color */
                if (!moves.isEmpty()) {
                    String bgColor = squares[startPos.getRow() - 1][startPos.getColumn() - 1].pattern.bgColor;
                    if (bgColor.equals(SET_BG_COLOR_WHITE)) {
                        squares[startPos.getRow() - 1][startPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_YELLOW;
                    } else {
                        squares[startPos.getRow() - 1][startPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_DARK_YELLOW;
                    }
                }
                for (ChessMove move : moves) {
                    ChessPosition endPos = move.getEndPosition();
                    String bgColor = squares[endPos.getRow() - 1][endPos.getColumn() - 1].pattern.bgColor;
                    if (bgColor.equals(SET_BG_COLOR_WHITE)) {
                        squares[endPos.getRow() - 1][endPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_GREEN;
                    } else {
                        squares[endPos.getRow() - 1][endPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_DARK_GREEN;
                    }
                }
                drawBoard();
                /* Reset highlight color */
                if (!moves.isEmpty()) {
                    String bgColor = squares[startPos.getRow() - 1][startPos.getColumn() - 1].pattern.bgColor;
                    if (bgColor.equals(SET_BG_COLOR_YELLOW)) {
                        squares[startPos.getRow() - 1][startPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_WHITE;
                    } else if (bgColor.equals(SET_BG_COLOR_DARK_YELLOW)) {
                        squares[startPos.getRow() - 1][startPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_BLACK;
                    }
                }
                for (ChessMove move : moves) {
                    ChessPosition endPos = move.getEndPosition();
                    String bgColor = squares[endPos.getRow() - 1][endPos.getColumn() - 1].pattern.bgColor;
                    if (bgColor.equals(SET_BG_COLOR_GREEN)) {
                        squares[endPos.getRow() - 1][endPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_WHITE;
                    } else if (bgColor.equals(SET_BG_COLOR_DARK_GREEN)) {
                        squares[endPos.getRow() - 1][endPos.getColumn() - 1].pattern.bgColor = SET_BG_COLOR_BLACK;
                    }
                }
                return "";
            } else {
                return "Wrong start position format";
            }
        }
        throw new ResponseException(400, "Expected: <start>");
    }

    public String help() {
        return """
                - redraw
                - leave
                - move <start> <end>
                - resign
                - highlight <start>
                """;
    }

    /**
     * Draw the header or footer of the board
     * @param header header/footer text
     * @param reversed is it in reversed order
     */
    private void drawHeaderFooter(char[] header, boolean reversed) {
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

    /**
     * Prepare chess board in an 2D array
     */
    private void memDrawBoard() {
        ChessBoard board = game.getBoard();
        String bgColor = SET_BG_COLOR_BLACK;
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                squares[row][col].pattern = new SquarePattern();
                squares[row][col].pattern.bgColor = bgColor;
                ChessPosition newPos = new ChessPosition(row + 1, col + 1);
                squares[row][col].piece = board.getPiece(newPos);
                if (squares[row][col].piece != null) {
                    ChessGame.TeamColor color = squares[row][col].piece.getTeamColor();
                    squares[row][col].pattern.textColor =
                            (color == ChessGame.TeamColor.WHITE) ? SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                }
                bgColor = (bgColor.equals(SET_BG_COLOR_BLACK)) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
            }
            bgColor = (bgColor.equals(SET_BG_COLOR_BLACK)) ? SET_BG_COLOR_WHITE : SET_BG_COLOR_BLACK;
        }
    }

    /**
     * Draw a chess piece to UI
     * @param row board row number (0 ~ 7)
     * @param col board column number (0 ~ 7)
     */
    private void drawPiece(int row, int col) {
        if (squares[row][col].piece == null) {
            System.out.print(squares[row][col].pattern.bgColor + "   ");
            return;
        }
        String squarePattern = squares[row][col].pattern.bgColor + squares[row][col].pattern.textColor;
        switch (squares[row][col].piece.getPieceType()) {
            case KING -> System.out.print(squarePattern + " K ");
            case QUEEN -> System.out.print(squarePattern + " Q ");
            case BISHOP -> System.out.print(squarePattern + " B ");
            case KNIGHT -> System.out.print(squarePattern + " N ");
            case ROOK -> System.out.print(squarePattern + " R ");
            case PAWN -> System.out.print(squarePattern + " P ");
        }
    }

    /**
     * Draw the board to UI
     */
    private void drawBoard() {
        char[] header = { ' ', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', ' ' };
        System.out.println();
        if (joinedColor.equals("WHITE")) {
            drawHeaderFooter(header, false);
            for (int row = 7; row >= 0; row--) {
                String rowNo = String.format(" %d ", row + 1);
                System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + rowNo);
                for (int col = 0; col < 8; col++) {
                    drawPiece(row, col);
                }
                System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + rowNo);
                System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            }
            drawHeaderFooter(header, false);
        } else {
            drawHeaderFooter(header, true);
            for (int row = 0; row < 8; row++) {
                String rowNo = String.format(" %d ", row + 1);
                System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + rowNo);
                for (int col = 7; col >= 0; col--) {
                    drawPiece(row, col);
                }
                System.out.print(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + rowNo);
                System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
            }
            drawHeaderFooter(header, true);
        }
    }

    @Override
    public void notify(String message) {
        ServerMessage serverMessage = new Gson().fromJson(message, ServerMessage.class);
        switch (serverMessage.getServerMessageType()) {
            case NOTIFICATION -> {
                var notification = new Gson().fromJson(message, NotificationMessage.class);
                System.out.print("\n" + SET_TEXT_COLOR_RED + notification.getMessage());
            }
            case ERROR -> {
                var error = new Gson().fromJson(message, ErrorMessage.class);
                System.out.print("\n" + SET_TEXT_COLOR_RED + error.getErrorMessage());
            }
            case LOAD_GAME -> loadGame(new Gson().fromJson(message, LoadGameMessage.class));
        }
        System.out.print("\n" + RESET_TEXT_COLOR + "PLAY_CHESS >>> " + SET_TEXT_COLOR_GREEN);
    }

    private void loadGame(LoadGameMessage message) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessGame.class, new ChessGameTypeAdapter());
        Gson gson = gsonBuilder.create();
        game = gson.fromJson(message.getGame(), ChessGame.class);
        memDrawBoard();
        drawBoard();
    }
}
