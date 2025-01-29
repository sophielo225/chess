package chess;

import chess.piecemoves.*;

import java.util.Collection;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;
    private final ArrayList<ChessMove> moves = new ArrayList<>();

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type && Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type, moves);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                ", moves=" + moves +
                '}';
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case ROOK:
                RookMovesCalculator rookMoves = new RookMovesCalculator(board, pieceColor, myPosition, moves);
                rookMoves.pieceMoves();
                break;
            case BISHOP:
                BishopMovesCalculator bishopMoves = new BishopMovesCalculator(board, pieceColor, myPosition, moves);
                bishopMoves.pieceMoves();
                break;
            case KNIGHT:
                KnightMovesCalculator knightMoves = new KnightMovesCalculator(board, pieceColor, myPosition, moves);
                knightMoves.pieceMoves();
                break;
            case QUEEN:
                QueenMovesCalculator queenMoves = new QueenMovesCalculator(board, pieceColor, myPosition, moves);
                queenMoves.pieceMoves();
                break;
            case KING:
                KingMovesCalculator kingMoves = new KingMovesCalculator(board, pieceColor, myPosition, moves);
                kingMoves.pieceMoves();
                break;
            case PAWN:
                PawnMovesCalculator pawnMoves = new PawnMovesCalculator(board, pieceColor, myPosition, moves);
                pawnMoves.pieceMoves();
                break;
        }
        return moves;
    }
}
