package chess;

import java.util.Collection;

public class KingMovesCalculator extends MovesCalculator {

    public static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        // King moves in all straight lines and diagonals once
        int[][] allDirections = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1},  // Diagonal directions
                {1, 0}, {-1, 0}, {0, 1}, {0, -1}     // Straight directions
        };

        return calculateLinearMoves(false, board, myPosition, pieceColor, allDirections);
    }
}
