package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;

public class KingMovesCalculator extends PieceMovesCalculator{

    public KingMovesCalculator(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        super(board, color, myPosition, moves);
    }

    @Override
    public void pieceMoves() {
        super.pieceMoves();
        // Get current row and column
        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        // List of all possible positions
        ChessPosition[] p = {new ChessPosition(myRow + 1, myColumn),  // Front square
                new ChessPosition(myRow - 1, myColumn),  // Back square
                new ChessPosition(myRow, myColumn - 1),  // Left square
                new ChessPosition(myRow, myColumn + 1),  // Right square
                new ChessPosition(myRow + 1, myColumn - 1),  // Front left square
                new ChessPosition(myRow + 1, myColumn + 1),  // Front right square
                new ChessPosition(myRow - 1, myColumn - 1),  // Back left square
                new ChessPosition(myRow - 1, myColumn + 1)};  // Back right square

        for (ChessPosition pos : p) {
            if (pos.getRow() < 1 || pos.getRow() > 8 || pos.getColumn() < 1 || pos.getColumn() > 8) {
                continue;  // Check if that position is valid or not
            }
            addMove(pos);  // Add move if the position is valid
        }
    }

    @Override
    public String toString() {
        return "KingMovesCalculator{}";
    }
}
