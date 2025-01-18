package chess;

import java.util.ArrayList;

public class RookMovesCalculator {

    private ChessGame.TeamColor pieceColor;
    private ArrayList<ChessMove> moves;

    public RookMovesCalculator(ChessGame.TeamColor pieceColor) {
        this.pieceColor = pieceColor;
        this.moves = new ArrayList<ChessMove>();
    }

    public void leftMove(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentColumn = position.getColumn();
        ChessPosition startPosition = new ChessPosition(currentRow, currentColumn);
        for (int col = currentColumn - 1; col > 0; col--) {
            ChessPosition leftPosition = new ChessPosition(currentRow, col);
            ChessPiece leftPiece = board.getPiece(leftPosition);
            if (leftPiece != null) {
                if (leftPiece.getTeamColor() == pieceColor) {
                    moves.add(new ChessMove(startPosition, leftPosition, null));
                break;
                }
            }
        }
    }

    public void rightMove(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentColumn = position.getColumn();
        ChessPosition startPosition = new ChessPosition(currentRow, currentColumn);
        for (int col = currentColumn + 1; col < 9; col++) {
            ChessPosition rightPosition = new ChessPosition(currentRow, col);
            ChessPiece rightPiece = board.getPiece(rightPosition);
            if (rightPiece != null) {
                if (rightPiece.getTeamColor() == pieceColor) {
                    moves.add(new ChessMove(startPosition, rightPosition, null));
                    break;
                }
            }
        }
    }

    public void frontMove(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentColumn = position.getColumn();
        ChessPosition startPosition = new ChessPosition(currentRow, currentColumn);
        for (int row = currentRow + 1; row < 9; row++) {
            ChessPosition frontPosition = new ChessPosition(row, currentColumn);
            ChessPiece frontPiece = board.getPiece(frontPosition);
            if (frontPiece != null) {
                if (frontPiece.getTeamColor() == pieceColor) {
                    moves.add(new ChessMove(startPosition, frontPosition, null));
                    break;
                }
            }
        }
    }

    public void backMove(ChessBoard board, ChessPosition position) {
        int currentRow = position.getRow();
        int currentColumn = position.getColumn();
        ChessPosition startPosition = new ChessPosition(currentRow, currentColumn);
        for (int row = currentRow - 1; row > 0; row--) {
            ChessPosition backPosition = new ChessPosition(row, currentColumn);
            ChessPiece backPiece = board.getPiece(backPosition);
            if (backPiece != null) {
                if (backPiece.getTeamColor() == pieceColor) {
                    moves.add(new ChessMove(startPosition, backPosition, null));
                    break;
                }
            }
        }
    }

    public ArrayList<ChessMove> rookMove(ChessBoard board, ChessPosition position) {
        leftMove(board, position);
        rightMove(board, position);
        frontMove(board, position);
        backMove(board, position);
        return moves;
    }
}
