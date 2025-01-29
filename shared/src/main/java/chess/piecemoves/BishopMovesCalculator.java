package chess.piecemoves;

import chess.*;

import java.util.ArrayList;

public class BishopMovesCalculator extends PieceMovesCalculator {

    public BishopMovesCalculator(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition,
                               ArrayList<ChessMove> moves) {
        super(board, color, myPosition, moves);
    }

    @Override
    public void pieceMoves() {
        super.pieceMoves();
        // Get current row and column
        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();

        // Left front squares
        for (int col = myColumn - 1, row = myRow + 1; col > 0 && row < 9; col--, row++) {
            ChessPosition leftFrontPosition = new ChessPosition(row, col);
            if (addMove(leftFrontPosition)) {
                break;
            }
        }

        // Right front squares
        for (int col = myColumn + 1, row = myRow + 1; col < 9 && row < 9; col++, row++) {
            ChessPosition rightFrontPosition = new ChessPosition(row, col);
            if (addMove(rightFrontPosition)) {
                break;
            }
        }

        // Left back squares
        for (int col = myColumn - 1, row = myRow - 1; col > 0 && row > 0; col--, row--) {
            ChessPosition leftBackPosition = new ChessPosition(row, col);
            if (addMove(leftBackPosition)) {
                break;
            }
        }

        // Right back squares
        for (int col = myColumn + 1, row = myRow - 1; col < 9 && row > 0; col++, row--) {
            ChessPosition rightBackPosition = new ChessPosition(row, col);
            if (addMove(rightBackPosition)) {
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "BishopMovesCalculator{}";
    }
}
