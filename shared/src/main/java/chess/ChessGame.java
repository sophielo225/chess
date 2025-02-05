package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn = TeamColor.WHITE;
    private ChessBoard board;

    public ChessGame() {
    board = new ChessBoard();
    board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, board);
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return null;
        }
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            ChessPosition piecePosition = move.getStartPosition();
            ChessPosition newPosition = move.getEndPosition();
            ChessPiece tempPiece = board.getPiece(newPosition);
            board.addPiece(piecePosition, null);
            board.addPiece(newPosition, piece);
            if (!isInCheck(piece.getTeamColor())) {
                moves.add(move);
            }
            board.addPiece(newPosition, tempPiece);
            board.addPiece(piecePosition, piece);
        }
        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException("There is no piece to move");
        }
        if (piece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("Wrong turn");
        }
        ChessPosition myPosition = move.getStartPosition();
        ChessPosition newPosition = move.getEndPosition();
        ArrayList<ChessPosition> newPositions = new ArrayList<>();
        for (ChessMove newMove : piece.pieceMoves(board, myPosition)) {
            newPositions.add(newMove.getEndPosition());
        }
        if (!newPositions.contains(move.getEndPosition())) {
            throw new InvalidMoveException("Move is not allowed");
        }
        ChessPiece tempPiece = board.getPiece(newPosition);
        ChessPiece.PieceType newType;
        if (move.getPromotionPiece() != null) {
            newType = move.getPromotionPiece();
        } else {
            newType = piece.getPieceType();
        }
        ChessPiece newPiece = new ChessPiece(piece.getTeamColor(), newType);
        board.addPiece(myPosition, null);
        board.addPiece(newPosition, newPiece);
        if (isInCheck(piece.getTeamColor())) {
            board.addPiece(newPosition, tempPiece);
            board.addPiece(myPosition, piece);
            throw new InvalidMoveException("Move is not allowed");
        }
        if (piece.getTeamColor() == TeamColor.WHITE) {
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return !getInCheckPieceMap(teamColor).isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Get the map which specified enemy pieces are put in position/piece pairs
     *
     * @param teamColor which team to get the threatening pieces
     * @return Map of pieces which attack the king of the specified team
     */
    private Map<ChessPosition, ChessPiece> getInCheckPieceMap(TeamColor teamColor) {
        Map<ChessPosition, ChessPiece> map = new HashMap<>();
        Map<ChessPosition, ChessPiece> myMap, enemyMap;
        if (teamColor == TeamColor.WHITE) {
            myMap = getPieceMapByColor(TeamColor.WHITE);
            enemyMap = getPieceMapByColor(TeamColor.BLACK);
        } else {
            myMap = getPieceMapByColor(TeamColor.BLACK);
            enemyMap = getPieceMapByColor(TeamColor.WHITE);
        }
        ChessPosition kingPosition = new ChessPosition(0, 0);
        for (Map.Entry<ChessPosition, ChessPiece> item : myMap.entrySet()) {
            if (item.getValue().getPieceType() == ChessPiece.PieceType.KING) {
                kingPosition = item.getKey();
                break;
            }
        }
        for (Map.Entry<ChessPosition, ChessPiece> item : enemyMap.entrySet()) {
            ChessPosition position = item.getKey();
            for (ChessMove move : item.getValue().pieceMoves(board, position)) {
                if (move.getEndPosition().equals(kingPosition)) {
                    map.put(position, item.getValue());
                }
            }
        }
        return map;
    }

    /**
     * Get the map which specified team pieces are put in position/piece pairs
     *
     * @param teamColor which team to get the piece map
     * @return Piece map of the specified team
     */
    private Map<ChessPosition, ChessPiece> getPieceMapByColor(TeamColor teamColor) {
        Map<ChessPosition, ChessPiece> map = new HashMap<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() == teamColor) {
                        map.put(position, piece);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
