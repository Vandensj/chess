package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public class Main {
    public static void main(String args[]) {
        ChessGame game = new ChessGame();
        BoardPrinter.printBoard(game.getBoard(), ChessGame.TeamColor.WHITE);
        BoardPrinter.printBoard(game.getBoard(), ChessGame.TeamColor.BLACK);
        Collection<ChessMove> validMoves = game.validMoves(new ChessPosition(1,2));
        BoardPrinter.printHighlightedMoves(game.getBoard(), ChessGame.TeamColor.WHITE, validMoves);
    }
}
