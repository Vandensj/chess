package chess;

import java.util.Collection;

public class BishopMovesCalculator extends MovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        // Bishop moves diagonally, so we pass the diagonal directions
        int[][] diagonalDirections = {
                {1, 1},   // Down-right
                {1, -1},  // Down-left
                {-1, 1},  // Up-right
                {-1, -1}  // Up-left
        };

        // Use the shared calculateLinearMoves method to calculate all diagonal moves
        return calculateLinearMoves(true, board, myPosition, pieceColor, diagonalDirections);
    }
}