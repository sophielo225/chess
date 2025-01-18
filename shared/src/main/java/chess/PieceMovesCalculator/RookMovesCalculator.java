package chess.PieceMovesCalculator;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator {

    public Collection<ChessMove> calculateRookMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {

        Collection<ChessMove> moves = new ArrayList<>();
        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();
        ChessPosition startPosition = new ChessPosition(myRow, myColumn);

        // Left Move
        for (int col = myColumn - 1; col > 0; col--) {
            ChessPosition leftPosition = new ChessPosition(myRow, col);
            ChessPiece leftPiece = board.getPiece(leftPosition);
            if (leftPiece == null) {
                moves.add(new ChessMove(startPosition, leftPosition, null));
            }else{
                if (leftPiece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(startPosition, leftPosition, null));
                }
                break;
            }
        }
        // Right Move
        for (int col = myColumn + 1; col < 9; col++) {
            ChessPosition rightPosition = new ChessPosition(myRow, col);
            ChessPiece rightPiece = board.getPiece(rightPosition);
            if (rightPiece == null) {
                moves.add(new ChessMove(startPosition, rightPosition, null));
            }else{
                if (rightPiece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(startPosition, rightPosition, null));
                }
                break;
            }
        }
        // Front Move
        for (int row = myRow + 1; row < 9; row++) {
            ChessPosition frontPosition = new ChessPosition(row, myColumn);
            ChessPiece frontPiece = board.getPiece(frontPosition);
            if (frontPiece == null) {
                moves.add(new ChessMove(startPosition, frontPosition, null));
            }else{
                if (frontPiece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(startPosition, frontPosition, null));
                }
                break;
            }
        }
        // Back Move
        for (int row = myRow - 1; row > 0; row--) {
            ChessPosition backPosition = new ChessPosition(row, myColumn);
            ChessPiece backPiece = board.getPiece(backPosition);
            if (backPiece == null) {
                moves.add(new ChessMove(startPosition, backPosition, null));
            }else{
                if (backPiece.getTeamColor() != pieceColor) {
                    moves.add(new ChessMove(startPosition, backPosition, null));
                }
                break;
            }
        }
        return moves;
    }
}
