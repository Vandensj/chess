package ui;

import chess.ChessGame;

public class GameUI {

    private final Client client;
    private final int gameId;
    private final ChessGame.TeamColor color;
    private final String authToken;

    public GameUI(Client WSClient, int gameId, ChessGame.TeamColor teamColor, String token) {
        this.client = WSClient;
        this.gameId = gameId;
        this.color = teamColor;
        this.authToken = token;
    }

    public void start() {

    }
}

