package chess;

import java.util.Collection;

public class KnightMovesCalculator extends MovesCalculator {

    @Override
    public Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition myPosition, ChessGame.TeamColor pieceColor) {
        // Knight moves in an L shape
        int[][] knightDirections = {
                {1, 2},
                {2, 1},
                {2, -1},
                {1, -2},
                {-1, 2},
                {-2, 1},
                {-2, -1},
                {-1, -2}
        };

        // Use the shared calculateLinearMoves method to calculate all L shape moves
        return calculateLinearMoves(false, board, myPosition, pieceColor, knightDirections);
    }
}