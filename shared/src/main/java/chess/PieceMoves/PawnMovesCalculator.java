package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;

public class PawnMovesCalculator extends PieceMovesCalculator {
    public PawnMovesCalculator(ChessBoard board, ChessGame.TeamColor color, ChessPosition myPosition, ArrayList<ChessMove> moves) {
        super(board, color, myPosition, moves);
    }

    private void addPromotedMove (ChessPosition endPosition) {
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN));
    }

    @Override
    public void pieceMoves() {
        super.pieceMoves();
        // Get current row and column
        int myRow = myPosition.getRow();
        int myColumn = myPosition.getColumn();
        if (color == ChessGame.TeamColor.WHITE) {
            if (myRow > 1 && myRow < 8) {  // Front one move
                ChessPosition front1Pos = new ChessPosition(myRow + 1, myColumn);
                ChessPiece front1Piece = board.getPiece(front1Pos);
                if (myRow == 7) {  // Promote white pawn if it's at row 7
                    addPromotedMove(front1Pos);
                } else if (front1Piece == null) {  // Move when there's no piece on the square
                    moves.add(new ChessMove(myPosition, front1Pos, null));
                }
                if (myRow == 2) {  // It's the first move of the white pawn
                    ChessPosition front2Pos = new ChessPosition(myRow + 2, myColumn);
                    ChessPiece front2Piece = board.getPiece(front2Pos);
                    if (front1Piece == null && front2Piece == null) {  // Move when front two squares are empty
                        moves.add(new ChessMove(myPosition, front2Pos, null));
                    }
                }
            }
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (myRow > 1 && myRow < 8) {  // Back one move
                ChessPosition back1Pos = new ChessPosition(myRow - 1, myColumn);
                ChessPiece back1Piece = board.getPiece(back1Pos);
                if (myRow == 2) {  // Promote black pawn if it's at row 2
                    addPromotedMove(back1Pos);
                } else if (back1Piece == null) {  // Move when there's no piece on the square
                    moves.add(new ChessMove(myPosition, back1Pos, null));
                }
                if (myRow == 7) {  // It's the first move of the black pawn
                    ChessPosition back2Pos = new ChessPosition(myRow - 2, myColumn);
                    ChessPiece back2Piece = board.getPiece(back2Pos);
                    if (back1Piece == null && back2Piece == null) {  // Move when front two squares are empty
                        moves.add(new ChessMove(myPosition, back2Pos, null));
                    }
                }
            }
        }
        // Capture an enemy piece diagonally
        if (color == ChessGame.TeamColor.WHITE) {
            if (myRow > 1 && myRow < 8) {
                if (myColumn > 1) {  // Front left square
                    ChessPosition frontLeftPosition = new ChessPosition(myRow + 1, myColumn - 1);
                    ChessPiece frontLeftPiece = board.getPiece(frontLeftPosition);
                    if (frontLeftPiece != null && frontLeftPiece.getTeamColor() != color) {
                        if (myRow == 7) {  // Promote white pawn
                            addPromotedMove(frontLeftPosition);
                        } else {
                            moves.add(new ChessMove(myPosition, frontLeftPosition, null));
                        }
                    }
                }

                if (myColumn < 8) {  // Front right square
                    ChessPosition frontRightPosition = new ChessPosition(myRow + 1, myColumn + 1);
                    ChessPiece frontRightPiece = board.getPiece(frontRightPosition);
                    if (frontRightPiece != null && frontRightPiece.getTeamColor() != color) {
                        if (myRow == 7) {  // Promote white pawn
                            addPromotedMove(frontRightPosition);
                        } else {
                            moves.add(new ChessMove(myPosition, frontRightPosition, null));
                        }
                    }
                }
            }
        } else if (color == ChessGame.TeamColor.BLACK) {
            if (myRow > 1 && myRow < 8) {
                if (myColumn > 1) {  // Back left square
                    ChessPosition backLeftPosition = new ChessPosition(myRow - 1, myColumn - 1);
                    ChessPiece backLeftPiece = board.getPiece(backLeftPosition);
                    if (backLeftPiece != null && backLeftPiece.getTeamColor() != color) {
                        if (myRow == 2) {  // Promote black pawn
                            addPromotedMove(backLeftPosition);
                        } else {
                            moves.add(new ChessMove(myPosition, backLeftPosition, null));
                        }
                    }
                }

                if (myColumn < 8) {  // Back right square
                    ChessPosition backRightPosition = new ChessPosition(myRow - 1, myColumn + 1);
                    ChessPiece backRightPiece = board.getPiece(backRightPosition);
                    if (backRightPiece != null && backRightPiece.getTeamColor() != color) {
                        if (myRow == 2) {  // Promote black pawn
                            addPromotedMove(backRightPosition);
                        } else {
                            moves.add(new ChessMove(myPosition, backRightPosition, null));
                        }
                    }
                }
            }
        }

    }

    @Override
    public String toString() {
        return "PawnMovesCalculator{}";
    }
}
