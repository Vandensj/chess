package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard gameBoard;
    TeamColor turnTeam;

    public ChessGame() {
        gameBoard = new ChessBoard();
        gameBoard.resetBoard();
        turnTeam = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turnTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turnTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (startPosition == null || gameBoard.getPiece(startPosition) == null) {
            return null;
        }
        Collection<ChessMove> potentialMoves = gameBoard.getPiece(startPosition).pieceMoves(gameBoard, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        TeamColor team = gameBoard.getPiece(startPosition).getTeamColor();

        for (ChessMove move : potentialMoves) {
            ChessPiece capturedPiece = gameBoard.doMove(move);

            if (!isInCheck(team)) {
                validMoves.add(move);
            }

            gameBoard.undoMove(move, capturedPiece);
        }

        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = gameBoard.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != turnTeam) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
        if (validMoves == null || !validMoves.contains(move)) {
            throw new InvalidMoveException();
        }
        gameBoard.doMove(move);
        turnTeam = (turnTeam == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessPosition> enemyPositions = gameBoard.getPieces((teamColor == TeamColor.BLACK) ? TeamColor.WHITE : TeamColor.BLACK);
        for (ChessPosition position : enemyPositions) {
            if (gameBoard.getPiece(position).canKillKing(gameBoard, position))
                return true;
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> team = gameBoard.getPieces(teamColor);
        for (ChessPosition position : team) {
            Collection<ChessMove> moves = validMoves(position);
            for (ChessMove move : moves) {
                ChessPiece capturedPiece = gameBoard.doMove(move);
                if (!isInCheck(teamColor)) {
                    gameBoard.undoMove(move, capturedPiece);
                    return false;
                } else
                    gameBoard.undoMove(move, capturedPiece);
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        Collection<ChessPosition> team = gameBoard.getPieces(teamColor);
        for (ChessPosition position : team) {
            Collection<ChessMove> moves = validMoves(position);
            if (!moves.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }
}
