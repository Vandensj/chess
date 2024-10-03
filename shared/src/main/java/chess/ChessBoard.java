package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import static java.lang.Math.abs;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private ChessPosition capturedPosition = null;

    ChessPiece[][] squares;

    public ChessBoard() {
        squares = new ChessPiece[8][8];
    }

    public ChessPiece doMove(ChessMove move) {
        ChessPiece piece;
        ChessPiece capturedPiece = getPiece(move.getEndPosition());
        capturedPosition = move.getEndPosition();
        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(this.getPiece(move.getStartPosition()).getTeamColor(), move.getPromotionPiece());
        } else {
            piece = this.getPiece(move.getStartPosition());
        }
        // Check if en pessant or castling
        switch (this.getPiece(move.getStartPosition()).getPieceType()) {
            case PAWN:
                if (abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) > 0 && this.getPiece(move.getEndPosition()) == null) {
                    capturedPosition = new ChessPosition(move.getStartPosition().getRow(), move.getEndPosition().getColumn());
                    capturedPiece = this.getPiece(capturedPosition);
                    this.addPiece(capturedPosition, null);
                }
                break;
            case KING:
                if (abs(move.getStartPosition().getColumn() - move.getEndPosition().getColumn()) > 1) {
                    if (this.getPiece(move.getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE) {

                    } else {

                    }
                }
                break;
        }

        this.addPiece(move.getEndPosition(), piece);
        this.addPiece(move.getStartPosition(), null);
        return capturedPiece;
    }

    public void undoMove(ChessMove move, ChessPiece capturedPiece) {
        ChessPiece piece = this.getPiece(move.getEndPosition());
        this.addPiece(move.getStartPosition(), piece);
        this.addPiece(capturedPosition, capturedPiece);
    }

    public Collection<ChessPosition> getPieces(ChessGame.TeamColor teamColor) {
        Collection<ChessPosition> pieces = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j] != null && teamColor == squares[i][j].getTeamColor()) {
                    pieces.add(new ChessPosition(i+1, j+1));
                }
            }
        }
        return pieces;
    }

    public ChessPosition getKing(ChessGame.TeamColor teamColor) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (squares[i][j] != null && teamColor == squares[i][j].getTeamColor() && squares[i][j].getPieceType() == ChessPiece.PieceType.KING) {
                    return new ChessPosition(i+1, j+1);
                }
            }
        }
        return null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        squares = new ChessPiece[8][8];
        squares[0][0] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        squares[0][1] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][2] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][3] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        squares[0][4] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        squares[0][5] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        squares[0][6] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        squares[0][7] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            squares[1][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        squares[7][0] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        squares[7][1] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][2] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][3] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        squares[7][4] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        squares[7][5] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        squares[7][6] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        squares[7][7] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        for (int i = 0; i < 8; i++) {
            squares[6][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 7; i > -1; i--) {
            sb.append("|");
            for (int j = 0; j < 8; j++) {
                sb.append(pieceString(squares[i][j]));
                sb.append("|");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    private String pieceString(ChessPiece piece) {
        if (piece == null) {
            return " ";
        }
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case ROOK -> "R";
                case KNIGHT -> "N";
                case BISHOP -> "B";
                case QUEEN -> "Q";
                case KING -> "K";
                case PAWN -> "P";
                default -> "";
            };
        } else {
            return switch (piece.getPieceType()) {
                case ROOK -> "r";
                case KNIGHT -> "n";
                case BISHOP -> "b";
                case QUEEN -> "q";
                case KING -> "k";
                case PAWN -> "p";
                default -> "";
            };
        }
    }
}
