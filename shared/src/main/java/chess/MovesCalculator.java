package chess;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MovesCalculator {

    // Abstract method that must be implemented by specific piece calculators
    public abstract Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor);

    protected Collection<ChessMove> calculateLinearMoves(Boolean iterative, ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor pieceColor, int[][] directions) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (int[] direction : directions) {
            int dx = direction[0];
            int dy = direction[1];
            int depth = 1;

            do {
                ChessPosition endPosition = new ChessPosition(startPosition.getRow() + (dy*depth), startPosition.getColumn() + (dx*depth));
                // Check if move is out of bounds
                if (endPosition.outsideBounds())
                    break;
                // Check if there is a piece in the new position
                if (board.getPiece(endPosition) == null) {
                    validMoves.add(new ChessMove(startPosition, endPosition, null));
                    depth++;
                } else {
                    // Check if piece in new location is same team
                    if (board.getPiece(endPosition).getTeamColor() != pieceColor) {
                        validMoves.add(new ChessMove(startPosition, endPosition, null));
                        break;
                    } else
                        break;
                }
            } while (iterative);
        }

        return validMoves;
    }
}
