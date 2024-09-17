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
            if (board.getPiece(downOne) != null && !downOne.outsideBounds()) {
                //Check if piece can be promoted
                if (downOne.getRow() == 1)
                    validMoves.add(new ChessMove(myPosition, downOne, null));
                else
                    validMoves.add(new ChessMove(myPosition, downOne, null));
            }
            // Check if piece can go down two
            ChessPosition downTwo = new ChessPosition(myPosition.getRow()-2, myPosition.getColumn());
            if (myPosition.getRow() == 7 && board.getPiece(downOne) != null && !downOne.outsideBounds())
                validMoves.add(new ChessMove(myPosition, downTwo, null));
            // Check diagonal moves
            ChessPosition downRight = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()+1);
            if (!downRight.outsideBounds() && board.getPiece(downRight) != null && board.getPiece(downRight).getTeamColor() != pieceColor)
                validMoves.add(new ChessMove(myPosition, downRight, null));
            ChessPosition downLeft = new ChessPosition(myPosition.getRow()-1, myPosition.getColumn()-1);
            if (!downRight.outsideBounds() && board.getPiece(downLeft) != null && board.getPiece(downLeft).getTeamColor() != pieceColor)
                validMoves.add(new ChessMove(myPosition, downLeft, null));
        }

        // Determine move direction based on the pawn's color
        int direction = (pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;

        // One step forward

        return validMoves;
    }
}