package chess.piecemoves;

import chess.*;

import java.util.ArrayList;

public class QueenMovesCalculator extends PieceMovesCalculator  {
    public QueenMovesCalculator(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition,
                                ArrayList<ChessMove> moves) {
        super(board, color, myPosition, moves);
    }

    @Override
    public void pieceMoves() {
        super.pieceMoves();
        BishopMovesCalculator diagonalMoves = new BishopMovesCalculator(board, color, myPosition, moves);
        RookMovesCalculator orthogonalMoves = new RookMovesCalculator(board, color, myPosition, moves);
        diagonalMoves.pieceMoves();
        orthogonalMoves.pieceMoves();
    }

    @Override
    public String toString() {
        return "QueenMovesCalculator{}";
    }
}
