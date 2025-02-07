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
        ChessPiece whiteLeftRook = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        castlingPieces.put(new ChessPosition(1, 1), whiteLeftRook);
        ChessPiece whiteRightRook = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        castlingPieces.put(new ChessPosition(1, 8), whiteRightRook);
        ChessPiece whiteKing = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING);
        castlingPieces.put(new ChessPosition(1, 5), whiteKing);
        ChessPiece blackLeftRook = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        castlingPieces.put(new ChessPosition(8, 1), blackLeftRook);
        ChessPiece blackRightRook = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        castlingPieces.put(new ChessPosition(8, 8), blackRightRook);
        ChessPiece blackKing = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING);
        castlingPieces.put(new ChessPosition(8, 5), blackKing);
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
                ChessPosition leftRookPosition = new ChessPosition(myPosition.getRow(), 1);
                ChessPiece leftRook = board.getPiece(leftRookPosition);
                if (castlingPieces.get(leftRookPosition) != null &&
                        castlingPieces.get(leftRookPosition).equals(leftRook)) {
                    castlingPieces.remove(leftRookPosition, leftRook);
                }
                ChessPosition rightRookPosition = new ChessPosition(myPosition.getRow(), 8);
                ChessPiece rightRook = board.getPiece(rightRookPosition);
                if (castlingPieces.get(rightRookPosition) != null &&
                        castlingPieces.get(rightRookPosition).equals(rightRook)) {
                    castlingPieces.remove(rightRookPosition, rightRook);
                }
            } else {
                castlingPieces.remove(myPosition, piece);
            }
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int squares = newPosition.getColumn() - myPosition.getColumn();
            if (Math.abs(squares) == 2) {
                if (squares < 0) {
                    ChessPosition rookStartPosition = new ChessPosition(myPosition.getRow(), 1);
                    ChessPosition rookEndPosition = new ChessPosition(myPosition.getRow(), 4);
                    board.addPiece(rookStartPosition, null);
                    board.addPiece(rookEndPosition, new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                } else {
                    ChessPosition rookStartPosition = new ChessPosition(myPosition.getRow(), 8);
                    ChessPosition rookEndPosition = new ChessPosition(myPosition.getRow(), 6);
                    board.addPiece(rookStartPosition, null);
                    board.addPiece(rookEndPosition, new ChessPiece(piece.getTeamColor(), ChessPiece.PieceType.ROOK));
                }
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
            Map<ChessPosition, ChessPiece> myMap, enemyMap;
            if (teamColor == TeamColor.WHITE) {
                myMap = getPieceMapByColor(TeamColor.WHITE);
                enemyMap = getPieceMapByColor(TeamColor.BLACK);
            } else {
                myMap = getPieceMapByColor(TeamColor.BLACK);
                enemyMap = getPieceMapByColor(TeamColor.WHITE);
            }
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
        if (!castlingPieces.isEmpty()) {
            castlingPieces.clear();
        }
        ChessPosition whiteLeftRookPosition = new ChessPosition(1, 1);
        ChessPiece whiteLeftRook = board.getPiece(whiteLeftRookPosition);
        if (whiteLeftRook != null && whiteLeftRook.getPieceType() == ChessPiece.PieceType.ROOK) {
            castlingPieces.put(whiteLeftRookPosition, whiteLeftRook);
        }
        ChessPosition whiteRightRookPosition = new ChessPosition(1, 8);
        ChessPiece whiteRightRook = board.getPiece(whiteRightRookPosition);
        if (whiteRightRook != null && whiteRightRook.getPieceType() == ChessPiece.PieceType.ROOK) {
            castlingPieces.put(whiteRightRookPosition, whiteRightRook);
        }
        ChessPosition whiteKingPosition = new ChessPosition(1, 5);
        ChessPiece whiteKing = board.getPiece(whiteKingPosition);
        if (whiteKing != null && whiteKing.getPieceType() == ChessPiece.PieceType.KING) {
            castlingPieces.put(whiteKingPosition, whiteKing);
        }
        ChessPosition blackLeftRookPosition = new ChessPosition(8, 1);
        ChessPiece blackLeftRook = board.getPiece(blackLeftRookPosition);
        if (blackLeftRook != null && blackLeftRook.getPieceType() == ChessPiece.PieceType.ROOK) {
            castlingPieces.put(blackLeftRookPosition, blackLeftRook);
        }
        ChessPosition blackRightRookPosition = new ChessPosition(8, 8);
        ChessPiece blackRightRook = board.getPiece(blackRightRookPosition);
        if (blackRightRook != null && blackRightRook.getPieceType() == ChessPiece.PieceType.ROOK) {
            castlingPieces.put(blackRightRookPosition, blackRightRook);
        }
        ChessPosition blackKingPosition = new ChessPosition(8, 5);
        ChessPiece blackKing = board.getPiece(blackKingPosition);
        if (blackKing != null && blackKing.getPieceType() == ChessPiece.PieceType.KING) {
            castlingPieces.put(blackKingPosition, blackKing);
        }
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
