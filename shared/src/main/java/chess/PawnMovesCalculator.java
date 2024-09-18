package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends MovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        // If piece is black we go down
        if (pieceColor == ChessGame.TeamColor.BLACK) {
            // Check if piece can go down one
            ChessPosition downOne = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn());
            if (board.getPiece(downOne) == null && !downOne.outsideBounds()) {
                //Check if piece can be promoted
                if (downOne.getRow() == 1) {
                    // If yes return all possible promotions
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, downOne, ChessPiece.PieceType.ROOK));
                } else {
                    validMoves.add(new ChessMove(myPosition, downOne, null));
                    // Check if piece can go down two
                    ChessPosition downTwo = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                    if (myPosition.getRow() == 7 && board.getPiece(downTwo) == null)
                        validMoves.add(new ChessMove(myPosition, downTwo, null));
                }
            }
            // Check diagonal moves
            ChessPosition downRight = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            if (!downRight.outsideBounds() && board.getPiece(downRight) != null && board.getPiece(downRight).getTeamColor() != pieceColor) {
                // Check if piece can be promoted
                if (downRight.getRow() == 1) {
                    validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, downRight, ChessPiece.PieceType.ROOK));
                } else
                    validMoves.add(new ChessMove(myPosition, downRight, null));
            }
            ChessPosition downLeft = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            if (!downRight.outsideBounds() && board.getPiece(downLeft) != null && board.getPiece(downLeft).getTeamColor() != pieceColor) {
                // Check if piece can be promoted
                if (downLeft.getRow() == 1) {
                    validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, downLeft, ChessPiece.PieceType.ROOK));
                } else
                    validMoves.add(new ChessMove(myPosition, downLeft, null));
            }
        } else {
            // Check if piece can go up one
            ChessPosition upOne = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn());
            if (board.getPiece(upOne) == null && !upOne.outsideBounds()) {
                // Check if piece can be promoted
                if (upOne.getRow() == 8) {
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, upOne, ChessPiece.PieceType.ROOK));
                } else {
                    validMoves.add(new ChessMove(myPosition, upOne, null));// Check if piece can go up two
                    ChessPosition upTwo = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                    if (myPosition.getRow() == 2 && board.getPiece(upTwo) == null)
                        validMoves.add(new ChessMove(myPosition, upTwo, null));
                }
            }
            // Check diagonal moves
            ChessPosition upRight = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()+1);
            if (!upRight.outsideBounds() && board.getPiece(upRight) != null && board.getPiece(upRight).getTeamColor() != pieceColor) {
                // Check if piece can be promoted
                if (upRight.getRow() == 8) {
                    validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, upRight, ChessPiece.PieceType.ROOK));
                } else
                    validMoves.add(new ChessMove(myPosition, upRight, null));
            }
            ChessPosition upLeft = new ChessPosition(myPosition.getRow()+1, myPosition.getColumn()-1);
            if (!upLeft.outsideBounds() && board.getPiece(upLeft) != null && board.getPiece(upLeft).getTeamColor() != pieceColor) {
                // Check if piece can be promoted
                if (upLeft.getRow() == 8) {
                    validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.BISHOP));
                    validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.KNIGHT));
                    validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.QUEEN));
                    validMoves.add(new ChessMove(myPosition, upLeft, ChessPiece.PieceType.ROOK));
                } else
                    validMoves.add(new ChessMove(myPosition, upLeft, null));
            }
        }

        return validMoves;
    }
}