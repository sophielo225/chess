package chess.piecemoves;

import chess.*;

import java.util.ArrayList;

public class KnightMovesCalculator extends PieceMovesCalculator {

    public KnightMovesCalculator(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition,
                                 ArrayList<ChessMove> moves) {
        super(board, color, myPosition, moves);
    }

    @Override
    public void pieceMoves() {
        super.pieceMoves();
        // Get current row and column
        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        // List of all possible positions
        ChessPosition[] p = {new ChessPosition(myRow + 2, myColumn - 1),
                            new ChessPosition(myRow + 2, myColumn + 1),
                            new ChessPosition(myRow - 2, myColumn - 1),
                            new ChessPosition(myRow - 2, myColumn + 1),
                            new ChessPosition(myRow + 1, myColumn + 2),
                            new ChessPosition(myRow + 1, myColumn - 2),
                            new ChessPosition(myRow - 1, myColumn + 2),
                            new ChessPosition(myRow - 1, myColumn - 2)};

        for (ChessPosition pos : p) {
            if (pos.getRow() < 1 || pos.getRow() > 8 || pos.getColumn() < 1 || pos.getColumn() > 8) {
                continue;  // Check if that position is valid or not
            }
            addMove(pos);  // Add move if the position is valid
        }
    }

    @Override
    public String toString() {
        return "KnightMovesCalculator{}";
    }
}
