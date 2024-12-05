package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.UserGameCommand;

import java.io.PrintWriter;
import java.util.Scanner;

public class GameUI {

    private final Client client; // WebSocket client
    private final Scanner scanner;
    private final Integer gameID; // Game ID the user is connected to
    private final String authToken;
    private final ChessGame.TeamColor color;
    private WebSocketClient webSocketClient;

    public GameUI(Client client, Integer gameID, ChessGame.TeamColor teamColor, String authToken) throws Exception {
        this.client = client;
        this.authToken = authToken;
        this.gameID = gameID;
        this.scanner = new Scanner(System.in);
        this.color = teamColor;
        try {
            this.webSocketClient = client.getServerFacade().createWebSocketClient();
        } catch (Exception e) {
            throw new Exception("Error creating web socket client");
        }
    }

    public void start() {
        System.out.println("Welcome to the game! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("[IN_GAME] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "redraw":
                    redrawBoard();
                    break;
                case "leave":
                    leaveGame();
                    return; // Exit the game UI
                case "move":
                    makeMove();
                    break;
                case "resign":
                    resignGame();
                    break;
                case "highlight":
                    highlightLegalMoves();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("help       - Displays this help message.");
        System.out.println("redraw     - Redraws the chess board.");
        System.out.println("leave      - Leaves the game and returns to the main menu.");
        System.out.println("move       - Makes a move in the game.");
        System.out.println("resign     - Resigns from the game.");
        System.out.println("highlight  - Highlights legal moves for a selected piece.");
    }

    private void redrawBoard() {
        PrintBoard
    }

    private void leaveGame() {

    }

    private void makeMove() {
        System.out.print("Enter the start position (e.g., e2): ");
        String start = scanner.nextLine().trim();
        System.out.print("Enter the end position (e.g., e4): ");
        String end = scanner.nextLine().trim();

        try {
            ChessMove move = parseMove(start, end);
            UserGameCommand moveCommand = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID);
            moveCommand.setMove(move);

            client.sendMessage(new Gson().toJson(moveCommand));
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid move format: " + e.getMessage());
        }
    }

    private void resignGame() {
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        if (confirmation.equals("yes")) {
            System.out.println("You have resigned the game.");
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
            client.sendMessage(new Gson().toJson(resignCommand));
        } else {
            System.out.println("Resignation canceled.");
        }
    }

    private void highlightLegalMoves() {
        System.out.print("Enter the position of the piece to highlight (e.g., e2): ");
        String position = scanner.nextLine().trim();

        try {
            ChessMove start = parseSinglePosition(position);
            // Call a local method to highlight moves (no server communication needed)
            System.out.println("Highlighting legal moves for piece at " + position + "...");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position format: " + e.getMessage());
        }
    }

    // Helper to parse move input
    private ChessMove parseMove(String start, String end) {
        return new ChessMove(parsePosition(start), parsePosition(end));
    }

    private Position parsePosition(String input) {
        if (input.length() == 2) {
            int col = input.charAt(0) - 'a';
            int row = input.charAt(1) - '1';
            if (col >= 0 && col < 8 && row >= 0 && row < 8) {
                return new Position(row, col);
            }
        }
        throw new IllegalArgumentException("Position must be in format [a-h][1-8].");
    }

    private ChessMove parseSinglePosition(String input) {
        return new ChessMove(parsePosition(input), null); // Highlight only uses the start position
    }
}
