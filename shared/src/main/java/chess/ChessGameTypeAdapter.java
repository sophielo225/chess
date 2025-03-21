package chess;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ChessGameTypeAdapter implements JsonDeserializer<ChessGame>  {
    @Override
    public ChessGame deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
        String teamTurnStr = element.getAsJsonObject().get("teamTurn").getAsString();
        ChessGame.TeamColor teamTurn = (teamTurnStr.equals("WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        JsonObject boardObj = element.getAsJsonObject().get("board").getAsJsonObject();
        JsonArray boardArray = boardObj.get("board").getAsJsonArray();
        ChessBoard board = new ChessBoard();
        for (int row = 0; row < boardArray.size(); row++) {
            JsonArray pieceRow = boardArray.get(row).getAsJsonArray();
            for (int col = 0; col < pieceRow.size(); col++) {
                if (!pieceRow.get(col).isJsonObject()) {
                    continue;
                }
                JsonObject pieceObj = pieceRow.get(col).getAsJsonObject();
                String colorStr = pieceObj.get("pieceColor").getAsString();
                String typeStr = pieceObj.get("type").getAsString();
                ChessGame.TeamColor color = (colorStr.equals("WHITE")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
                ChessPiece.PieceType pieceType = getPieceType(typeStr);
                ChessPosition position = new ChessPosition(row + 1, col + 1);
                board.addPiece(position, new ChessPiece(color, pieceType));
            }
        }
        ChessGame newGame = new ChessGame();
        newGame.setBoard(board);
        newGame.setTeamTurn(teamTurn);
        return newGame;
    }

    private static ChessPiece.PieceType getPieceType(String typeStr) {
        ChessPiece.PieceType type = null;
        switch (typeStr) {
            case "KING" -> type = ChessPiece.PieceType.KING;
            case "QUEEN" -> type = ChessPiece.PieceType.QUEEN;
            case "BISHOP" -> type = ChessPiece.PieceType.BISHOP;
            case "KNIGHT" -> type = ChessPiece.PieceType.KNIGHT;
            case "ROOK" -> type = ChessPiece.PieceType.ROOK;
            case "PAWN" -> type = ChessPiece.PieceType.PAWN;
        }
        return type;
    }
}
