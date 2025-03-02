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
    private ChessPosition pieceEnPassant = null;
    private Map<ChessPosition, ChessPiece> castlingPieces = new HashMap<>();

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        addCastlingPieces(TeamColor.WHITE);
        addCastlingPieces(TeamColor.BLACK);
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

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    private boolean canMakeEnPassantMove (ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            return startPosition.getRow() == pieceEnPassant.getRow() &&
                    (Math.abs(startPosition.getColumn() - pieceEnPassant.getColumn()) == 1);
        }
        return false;
    }

    private ChessPosition getEnPassantPosition (ChessPiece piece) {
        if (piece == null) {
            return null;
        }
        if (piece.getTeamColor() == TeamColor.WHITE) {
            return new ChessPosition(pieceEnPassant.getRow() + 1, pieceEnPassant.getColumn());
        } else {
            return new ChessPosition(pieceEnPassant.getRow() - 1, pieceEnPassant.getColumn());
        }
    }

    private boolean isEnPassant (ChessPiece pawn, ChessPosition newPosition) {
        if (pawn == null) {
            return false;
        }
        Map<ChessPosition, ChessPiece> enemyMap;
        if (pawn.getTeamColor() == TeamColor.WHITE) {
            enemyMap = getPieceMapByColor(TeamColor.BLACK);
        } else {
            enemyMap = getPieceMapByColor(TeamColor.WHITE);
        }
        for (Map.Entry<ChessPosition, ChessPiece> item : enemyMap.entrySet()) {
            ChessPiece piece = item.getValue();
            ChessPosition piecePosition = item.getKey();
            if ((piece.getPieceType() == ChessPiece.PieceType.PAWN) &&
                    (Math.abs(piecePosition.getColumn() - newPosition.getColumn()) == 1) &&
                    (piecePosition.getRow() == newPosition.getRow())) {
                    return true;
            }
        }
        return false;
    }

    private boolean kingCannotMoveTo(ChessPosition startPosition, ChessPosition endPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        ChessPiece midPiece = board.getPiece(endPosition);
        if (midPiece != null) {
            return true;
        } else {
            ChessPiece tempPiece = board.getPiece(endPosition);
            board.addPiece(startPosition, null);
            board.addPiece(endPosition, piece);
            if (isInCheck(piece.getTeamColor())) {
                board.addPiece(endPosition, tempPiece);
                board.addPiece(startPosition, piece);
                return true;
            }
            board.addPiece(endPosition, tempPiece);
            board.addPiece(startPosition, piece);
        }
        return false;
    }

    private ArrayList<ChessMove> getCastlingMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        boolean leftRookFound = false;
        boolean rightRookFound = false;
        for (Map.Entry<ChessPosition, ChessPiece> map : castlingPieces.entrySet()) {
            if (map.getValue().getTeamColor() == piece.getTeamColor() &&
            map.getValue().getPieceType() == ChessPiece.PieceType.ROOK) {
                if (map.getKey().getColumn() == 1) {
                    leftRookFound = true;
                }
                if (map.getKey().getColumn() == 8) {
                    rightRookFound = true;
                }
            }
        }
        if (!leftRookFound && !rightRookFound) {
            return null;
        }
        ArrayList<ChessMove> moves = new ArrayList<>();
        if (castlingPieces.get(startPosition).equals(piece)) {
            if (leftRookFound) {
                boolean isQueenSideWorking = true;
                for (int i = startPosition.getColumn() - 1; i > 1; i--) {
                    ChessPosition newPosition = new ChessPosition(startPosition.getRow(), i);
                    if (kingCannotMoveTo(startPosition, newPosition)) {
                        isQueenSideWorking = false;
                        break;
                    }
                }
                if (isQueenSideWorking) {
                    ChessPosition newPosition = new ChessPosition(startPosition.getRow(), 3);
                    if (!kingCannotMoveTo(startPosition, newPosition)) {
                        ChessMove newMove = new ChessMove(startPosition, newPosition, null);
                        moves.add(newMove);
                    }
                }
            }
            if (rightRookFound) {
                boolean isKingSideWorking = true;
                for (int i = startPosition.getColumn() + 1; i < 8; i++) {
                    ChessPosition newPosition = new ChessPosition(startPosition.getRow(), i);
                    if (kingCannotMoveTo(startPosition, newPosition)) {
                        isKingSideWorking = false;
                        break;
                    }
                }
                if (isKingSideWorking) {
                    ChessPosition newPosition = new ChessPosition(startPosition.getRow(), 7);
                    if (!kingCannotMoveTo(startPosition, newPosition)) {
                        ChessMove newMove = new ChessMove(startPosition, newPosition, null);
                        moves.add(newMove);
                    }
                }
            }
        }
        return moves;
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
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            ArrayList<ChessMove> castleMoves = getCastlingMoves(startPosition);
            if (castleMoves != null) {
                moves.addAll(castleMoves);
            }
        }
        if (pieceEnPassant != null) {
            if (canMakeEnPassantMove(startPosition)) {
                ChessPosition newPosition = getEnPassantPosition(piece);
                ChessMove newMove = new ChessMove(startPosition, newPosition, null);
                moves.add(newMove);
            }
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
        if (move == null) {
            return;
        }
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
        if (pieceEnPassant != null) {
            if (canMakeEnPassantMove(myPosition)) {
                newPosition = getEnPassantPosition(piece);
                board.addPiece(pieceEnPassant, null);
                pieceEnPassant = null;
                newPositions.add(newPosition);
            } else {
                pieceEnPassant = null;
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            ArrayList<ChessMove> castleMoves = getCastlingMoves(myPosition);
            if (castleMoves != null) {
                for (ChessMove newMove : castleMoves) {
                    newPositions.add(newMove.getEndPosition());
                }
            }
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
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
            Math.abs(myPosition.getRow() - newPosition.getRow()) == 2) {
            if (isEnPassant(piece, newPosition)) {
                pieceEnPassant = newPosition;
            }
        }
        ChessPiece castlePiece = castlingPieces.get(myPosition);
        if (castlePiece != null && castlePiece.equals(piece)) {
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                castlingPieces.remove(myPosition, piece);
                for (int col : Arrays.asList(1, 8)){
                    ChessPosition rookPosition = new ChessPosition(myPosition.getRow(), col);
                    ChessPiece rookPiece = board.getPiece(rookPosition);
                    if (castlingPieces.get(rookPosition) != null &&
                    castlingPieces.get(rookPosition).equals(rookPiece)) {
                        castlingPieces.remove(rookPosition, rookPiece);
                    }
                }
            } else {
                castlingPieces.remove(myPosition, piece);
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int squares = newPosition.getColumn() - myPosition.getColumn();
            if (Math.abs(squares) == 2) {
                final int rookStartColumn = (squares < 0) ? 1 : 8;
                final ChessPosition rookStartPosition = new ChessPosition(myPosition.getRow(), rookStartColumn);
                final int rookEndColumn = (squares < 0) ? 4 : 6;
                final ChessPosition rookEndPosition = new ChessPosition(myPosition.getRow(), rookEndColumn);
                board.addPiece(rookStartPosition, null);
                board.addPiece(rookEndPosition, new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
            }
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
     * Determines if the given piece can be captured
     *
     * @param teamColor which team to check for check
     * @param enemyPosition position of the enemy piece
     * @return True if the specified enemy can be captured
     */
    private boolean enemyCanBeCaptured(TeamColor teamColor, ChessPosition enemyPosition) {
        Map<ChessPosition, ChessPiece> map = getPieceMapByColor(teamColor);
        for (Map.Entry<ChessPosition, ChessPiece> teamItem : map.entrySet()) {
            ChessPiece piece = teamItem.getValue();
            if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                continue;
            }
            for (ChessMove move : piece.pieceMoves(board, teamItem.getKey())) {
                if (move.getEndPosition().equals(enemyPosition)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Map<ChessPosition, ChessPiece> map = getInCheckPieceMap(teamColor);
        if (!map.isEmpty()) {
            Iterator<Map.Entry<ChessPosition, ChessPiece>> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ChessPosition, ChessPiece> enemyPiece = it.next();
                if (enemyCanBeCaptured(teamColor, enemyPiece.getKey())) {
                    it.remove();
                    break;
                }
            }
            return !map.isEmpty();
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        Set<ChessPosition> enemyEndPositions = new HashSet<>();
        ArrayList<ChessPosition> kingEndPositions = new ArrayList<>();
        if (!isInCheck(teamColor)) {
            final Map<ChessPosition, ChessPiece> myMap = getPieceMapByColor(teamColor);
            final Map<ChessPosition, ChessPiece> enemyMap = (teamColor == TeamColor.WHITE) ?
                                            getPieceMapByColor(TeamColor.BLACK) : getPieceMapByColor(TeamColor.WHITE);
            for (Map.Entry<ChessPosition, ChessPiece> item : myMap.entrySet()) {
                ChessPiece piece = item.getValue();
                if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                    for (ChessMove move : piece.pieceMoves(board, item.getKey())) {
                        kingEndPositions.add(move.getEndPosition());
                    }
                }
            }
            for (Map.Entry<ChessPosition, ChessPiece> item : enemyMap.entrySet()) {
                ChessPiece piece = item.getValue();
                for (ChessMove move : piece.pieceMoves(board, item.getKey())) {
                    enemyEndPositions.add(move.getEndPosition());
                }

            }
            if (kingEndPositions.isEmpty()) {
                return false;
            } else {
                return enemyEndPositions.containsAll(kingEndPositions);
            }
        }
        return false;
    }

    /**
     * Get the map which specified enemy pieces are put in position/piece pairs
     *
     * @param teamColor which team to get the threatening pieces
     * @return Map of pieces which attack the king of the specified team
     */
    private Map<ChessPosition, ChessPiece> getInCheckPieceMap(TeamColor teamColor) {
        Map<ChessPosition, ChessPiece> map = new HashMap<>();
        final Map<ChessPosition, ChessPiece> myMap = getPieceMapByColor(teamColor);
        final Map<ChessPosition, ChessPiece> enemyMap = (teamColor == TeamColor.WHITE) ?
                                            getPieceMapByColor(TeamColor.BLACK) : getPieceMapByColor(TeamColor.WHITE);
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
     * @param teamColor which team to get the piece map
     * @return Piece map of the specified team
     */
    private Map<ChessPosition, ChessPiece> getPieceMapByColor(TeamColor teamColor) {
        Map<ChessPosition, ChessPiece> map = new HashMap<>();
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    map.put(position, piece);
                }
            }
        }
        return map;
    }

    private void addCastlingPieces(TeamColor team) {
        final int row = (team == TeamColor.WHITE) ? 1 : 8;
        for (int col : Arrays.asList(1, 5, 8)) {
            ChessPosition piecePosition = new ChessPosition(row, col);
            ChessPiece piece = board.getPiece(piecePosition);
            ChessPiece.PieceType type = (col == 5) ? ChessPiece.PieceType.KING : ChessPiece.PieceType.ROOK;
            if (piece != null && piece.getPieceType() == type) {
                castlingPieces.put(piecePosition, piece);
            }
        }
    }

    /**
     * Sets this game's chessboard with a given board
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        if (!castlingPieces.isEmpty()) {
            castlingPieces.clear();
        }
        this.board = board;
        addCastlingPieces(TeamColor.WHITE);
        addCastlingPieces(TeamColor.BLACK);
    }

    /**
     * Gets the current chessboard
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
