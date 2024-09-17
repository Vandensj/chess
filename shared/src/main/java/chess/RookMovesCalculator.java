package chess;

import java.util.Collection;

public class RookMovesCalculator extends MovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        // Rook moves in straight lines, so we pass the horizontal and vertical directions
        int[][] straightDirections = {
                {1, 0},   // Down
                {-1, 0},  // Up
                {0, 1},   // Right
                {0, -1}   // Left
        };

        return calculateLinearMoves(true, board, myPosition, pieceColor, straightDirections);
    }
}
