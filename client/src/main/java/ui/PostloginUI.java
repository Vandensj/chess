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

        try {
            updateGames();
        } catch (Exception e) {
            System.out.println("Error getting games from server: " + e.getMessage());
        }

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

    private void updateGames() throws Exception {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(client.getServerFacade().listGames(authToken), JsonObject.class);

        JsonArray gamesArray = jsonObject.getAsJsonArray("games");

        // Reset game list map
        gameList.clear();

        for (int i = 0; i < gamesArray.size(); i++) {
            JsonObject game = gamesArray.get(i).getAsJsonObject();

            // Extract and print game properties
            int gameID = game.get("gameID").getAsInt();
            String gameName = game.get("gameName").getAsString();

            gameList.put(gameID, gameName);
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
        try {
            updateGames();
        } catch (Exception e) {
            System.out.println("Error getting games from server: " + e.getMessage());
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
        printChessBoard(true);
        printChessBoard(false);
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
        printChessBoard(true);
        printChessBoard(false);
    }

    private static void printChessBoard(boolean whiteOnTop) {
        // Column labels (a-h) for the board
        String columnLabels = "   a   b  c   d   e  f   g   h";

        // Print the column labels at the top
        System.out.println(EscapeSequences.ERASE_SCREEN);
        System.out.println(columnLabels);

        // Determine row order based on whiteOnTop preference
        int startRow = whiteOnTop ? 8 : 1;
        int endRow = whiteOnTop ? 1 : 8;
        int rowStep = whiteOnTop ? -1 : 1;

        // Loop through each row based on whiteOnTop preference
        for (int row = startRow; row != endRow + rowStep; row += rowStep) {
            // Print row label on the left side
            System.out.print(row + " ");

            // Loop through each column
            for (char col = 'a'; col <= 'h'; col++) {
                String piece = getInitialPiece(row, col);
                String backgroundColor = (row + col) % 2 == 0 ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY : EscapeSequences.SET_BG_COLOR_DARK_GREY;

                // Print each cell with background color and piece symbol
                System.out.print(backgroundColor + piece + EscapeSequences.RESET_BG_COLOR);
            }

            // Print row label on the right side
            System.out.print(" " + row);
            System.out.println(EscapeSequences.RESET_TEXT_COLOR);
        }

        // Print the column labels at the bottom
        System.out.println(columnLabels);
    }

    // Helper function to get the initial piece placement based on the row and column
    private static String getInitialPiece(int row, char col) {
        switch (row) {
            case 8:
                return getPieceForRow8(col); // Black major pieces row
            case 7:
                return EscapeSequences.BLACK_PAWN; // Black pawns row
            case 2:
                return EscapeSequences.WHITE_PAWN; // White pawns row
            case 1:
                return getPieceForRow1(col); // White major pieces row
            default:
                return EscapeSequences.EMPTY; // Empty squares
        }
    }

    // Returns the black major pieces for row 8 based on column
    private static String getPieceForRow8(char col) {
        return switch (col) {
            case 'a', 'h' -> EscapeSequences.BLACK_ROOK;
            case 'b', 'g' -> EscapeSequences.BLACK_KNIGHT;
            case 'c', 'f' -> EscapeSequences.BLACK_BISHOP;
            case 'd' -> EscapeSequences.BLACK_QUEEN;
            case 'e' -> EscapeSequences.BLACK_KING;
            default -> EscapeSequences.EMPTY;
        };
    }

    // Returns the white major pieces for row 1 based on column
    private static String getPieceForRow1(char col) {
        return switch (col) {
            case 'a', 'h' -> EscapeSequences.WHITE_ROOK;
            case 'b', 'g' -> EscapeSequences.WHITE_KNIGHT;
            case 'c', 'f' -> EscapeSequences.WHITE_BISHOP;
            case 'd' -> EscapeSequences.WHITE_QUEEN;
            case 'e' -> EscapeSequences.WHITE_KING;
            default -> EscapeSequences.EMPTY;
        };
    }
}
