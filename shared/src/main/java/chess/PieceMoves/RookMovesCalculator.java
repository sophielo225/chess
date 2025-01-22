package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;

public class RookMovesCalculator extends PieceMovesCalculator{

    public RookMovesCalculator(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition,
                               ArrayList<ChessMove> moves) {
        super(board, color, myPosition, moves);
    }

    @Override
    public void pieceMoves() {
        super.pieceMoves();
        // Get current row and column
        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        // Left squares
        for (int col = myColumn - 1; col > 0; col--) {
            ChessPosition leftPosition = new ChessPosition(myRow, col);
            if (addMove(leftPosition)) {
                break;
            }
        }

        // Right squares
        for (int col = myColumn + 1; col < 9; col++) {
            ChessPosition rightPosition = new ChessPosition(myRow, col);
            if (addMove(rightPosition)) {
                break;
            }
        }

        // Front squares
        for (int row = myRow + 1; row < 9; row++) {
            ChessPosition frontPosition = new ChessPosition(row, myColumn);
            if (addMove(frontPosition)) {
                break;
            }
        }

        // Back squares
        for (int row = myRow - 1; row > 0; row--) {
            ChessPosition backPosition = new ChessPosition(row, myColumn);
            if (addMove(backPosition)) {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "RookMovesCalculator{}";
    }
}
