package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPosition;

public class BoardPrinter {

    private BoardPrinter() { }

    public static void printBoard(ChessBoard board, ChessGame.TeamColor bottomColor) {
        System.out.println("PRINTED BOARD");
    }

    public static void printHighlightedMoves(ChessBoard board, ChessGame.TeamColor bottomColor, ChessPosition position) {
        System.out.println("Highlighted moves");
    }
}
