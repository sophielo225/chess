package chess.piecemoves;

import chess.*;
import java.util.ArrayList;
import java.util.Objects;

public class PieceMovesCalculator {
    protected ChessBoard board;
    protected ChessGame.TeamColor color;
    protected ChessPosition myPosition;
    protected ArrayList<ChessMove> moves;

    public PieceMovesCalculator(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition,
                                ArrayList<ChessMove> moves) {
        this.board = board;
        this.color = color;
        this.myPosition = myPosition;
        this.moves = moves;
    }

     // Adds a single move
     // Return if it's capturing or blocked by a piece or not

    protected boolean addMove(ChessPosition endPosition) {
        ChessPiece endPiece = board.getPiece(endPosition);
        if (endPiece != null) {  // If there's a piece on the end position
            if (endPiece.getTeamColor() != color) {  // If the end piece is not in the same team
                moves.add(new ChessMove(myPosition, endPosition, null));
            }
            return true;
        }
        // If there's no piece on the end position
        moves.add(new ChessMove(myPosition, endPosition, null));
        return false;
    }

    public void pieceMoves() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PieceMovesCalculator that = (PieceMovesCalculator) o;
        return Objects.equals(board, that.board) && color == that.color
                && Objects.equals(myPosition, that.myPosition) && Objects.equals(moves, that.moves);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, color, myPosition, moves);
    }

    @Override
    public String toString() {
        return "PieceMovesCalculator{" +
                "board=" + board +
                ", color=" + color +
                ", myPosition=" + myPosition +
                ", moves=" + moves +
                '}';
    }
}
