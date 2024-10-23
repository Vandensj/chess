package model;

public record GameData(Integer id, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game) {
}
