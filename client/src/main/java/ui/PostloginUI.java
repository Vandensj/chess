package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class PostloginUI {

    private final Client client;
    private final Scanner scanner;
    private final Map<Integer, String> gameList; // Maps displayed game numbers to game IDs for easy access
    private final String authToken;

    public PostloginUI(Client client, String token) {
        this.client = client;
        this.scanner = new Scanner(System.in);
        this.gameList = new HashMap<>();
        this.authToken = token;
    }

    // Main loop for handling user input
    public void start() {
        System.out.println("You are logged in! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("[LOGGED_IN] >>> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "logout":
                    handleLogout();
                    return; // Exit to go back to PreloginUI
                case "create game":
                    handleCreateGame();
                    break;
                case "list games":
                    handleListGames();
                    break;
                case "play game":
                    handlePlayGame();
                    break;
                case "observe game":
                    handleObserveGame();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("help         - Displays this help message.");
        System.out.println("logout       - Logs out and returns to the login screen.");
        System.out.println("create game  - Creates a new game (does not join the player).");
        System.out.println("list games   - Lists all active games on the server.");
        System.out.println("play game    - Joins a game as a player.");
        System.out.println("observe game - Observes a game.");
    }

    private void handleLogout() {
        try {
            String response = client.getServerFacade().logout(authToken);
            if (response.isEmpty()) {
                System.out.println("Logout successful. Returning to login screen.");
                new PreloginUI(client).start();
            } else {
                System.out.println("Logout failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during logout: " + e.getMessage());
        }
    }

    private void handleCreateGame() {
        System.out.print("Enter a name for the new game: ");
        String gameName = scanner.nextLine();

        try {
            String response = client.getServerFacade().createGame(gameName, authToken);
            if (response.contains("gameID")) {
                JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                System.out.println("Game created successfully with ID: " + jsonObject.get("gameID").getAsInt());
            } else {
                System.out.println("Failed to create game. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while creating the game: " + e.getMessage());
        }
    }

    private void handleListGames() {
        try {
            String response = client.getServerFacade().listGames(authToken);
            if (response.contains("games")) {  // Assuming response structure contains games
                System.out.println("Available games:");

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

                JsonArray gamesArray = jsonObject.getAsJsonArray("games");

                // Reset game list map
                gameList.clear();

                for (int i = 0; i < gamesArray.size(); i++) {
                    JsonObject game = gamesArray.get(i).getAsJsonObject();

                    // Extract and print game properties
                    int gameID = game.get("gameID").getAsInt();
                    String gameName = game.get("gameName").getAsString();

                    gameList.put(gameID, gameName);

                    System.out.println("Game ID: " + gameID + ", Game Name: " + gameName);
                }
            } else {
                System.out.println("No games currently available.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while listing games: " + e.getMessage());
        }
    }

    private void handlePlayGame() {
        System.out.print("Enter the ID of the game you want to join: ");
        int gameId = Integer.parseInt(scanner.nextLine());

        if (!gameList.containsKey(gameId)) {
            System.out.println("Invalid game number. Please list games and try again.");
            return;
        }

        System.out.print("Enter your color (e.g., 'white' or 'black'): ");
        ChessGame.TeamColor color = (Objects.equals(scanner.nextLine(), "white")) ? ChessGame.TeamColor.WHITE :
                ChessGame.TeamColor.BLACK;

        try {
            String response = client.getServerFacade().playGame(String.valueOf(gameId), color, authToken);
            if (response.isEmpty()) {  // Adjust based on actual server response
                System.out.println("Joined game successfully.");

                // Transition to GameUI for gameplay
                new GameUI(client, gameId, color, authToken).start();
            } else {
                System.out.println("Failed to join game. Please try another.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while joining the game: " + e.getMessage());
        }
        printBoard();
    }


    private void handleObserveGame() {
        System.out.print("Enter the number of the game you want to observe: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());

        if (!gameList.containsKey(gameNumber)) {
            System.out.println("Invalid game number. Please list games and try again.");
            return;
        }

        String gameId = gameList.get(gameNumber);
        try {
            // Placeholder for observe game request
            System.out.println("Observing game " + gameId + ". Functionality to be implemented in Phase 6.");
            // Add WebSocket or other real-time observer setup here in future phase
        } catch (Exception e) {
            System.out.println("An error occurred while attempting to observe the game: " + e.getMessage());
        }
        printBoard();
    }

    private void printBoard() {
        String[][] board = {
                {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_KING, EscapeSequences.BLACK_BISHOP, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK},
                {EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN, EscapeSequences.BLACK_PAWN},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY, EscapeSequences.EMPTY},
                {EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN, EscapeSequences.WHITE_PAWN},
                {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_KING, EscapeSequences.WHITE_BISHOP, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK}
        };
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println("Initial Board State (White on top):");
        printNormalBoard(board);

        System.out.println("\nInitial Board State (White on bottom):");
        printFlippedBoard(board);
    }

    private void printNormalBoard(String[][] board) {
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.EMPTY  + EscapeSequences.RESET_BG_COLOR);
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + " " + col + " "  + EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print(EscapeSequences.EMPTY +EscapeSequences.RESET_BG_COLOR);
        System.out.println();
        for (int i = 0; i < board.length; i++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + " " + i + " " + EscapeSequences.RESET_BG_COLOR);
            for (int j = 0; j < board[i].length; j++) {
                String backgroundColor = (i + j) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                System.out.print(backgroundColor + board[i][j] + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + " " + i + " " + EscapeSequences.RESET_BG_COLOR);
            System.out.println();
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.EMPTY  + EscapeSequences.RESET_BG_COLOR);
        for (char col = 'a'; col <= 'h'; col++) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLUE + " " + col + " "  + EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print(EscapeSequences.EMPTY +EscapeSequences.RESET_BG_COLOR);
        System.out.println();
    }

    private void printFlippedBoard(String[][] board) {
        for (int i = board.length - 1; i >= 0; i--) {
            for (int j = board[i].length - 1; j >= 0; j--) {
                String backgroundColor = (i + j) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;
                System.out.print(backgroundColor + board[i][j] + EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }
    }
}
