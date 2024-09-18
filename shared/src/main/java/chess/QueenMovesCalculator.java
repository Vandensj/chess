package chess;

import java.util.Collection;

public class QueenMovesCalculator extends MovesCalculator {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        // Queen moves in all straight lines and diagonals
        int[][] allDirections = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1},  // Diagonal directions
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}     // Straight directions
        };

        return calculateLinearMoves(true, board, myPosition, pieceColor, allDirections);
    }
}
