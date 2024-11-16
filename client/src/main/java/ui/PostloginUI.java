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

                    String blackUser;
                    String whiteUser;
                    if (game.toString().contains("blackUsername")) {
                        blackUser = game.get("blackUsername").getAsString();
                    } else {
                        blackUser = "none";
                    }
                    if (game.toString().contains("whiteUsername")) {
                        whiteUser = game.get("whiteUsername").getAsString();
                    } else {
                        whiteUser = "none";
                    }

                    gameList.put(gameID, gameName);

                    System.out.println("Game ID: " + gameID + ", Game Name: " + gameName + ", White Username: " + whiteUser + ", Black Username: " + blackUser);
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
        // Column labels (a-h) for the board, queen on the right for black perspective
        String columnLabels[] = {"\u2003A ", "\u2003B ", "\u2003C ", "\u2003D ", "\u2003E ", "\u2003F ",
                "\u2003G ", "\u2003H "};

        String whiteRow [] = {EscapeSequences.WHITE_ROOK, EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_BISHOP,
                EscapeSequences.WHITE_KING, EscapeSequences.WHITE_QUEEN, EscapeSequences.WHITE_BISHOP,
                EscapeSequences.WHITE_KNIGHT, EscapeSequences.WHITE_ROOK};

        String blackRow [] = {EscapeSequences.BLACK_ROOK, EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_BISHOP,
                EscapeSequences.BLACK_KING, EscapeSequences.BLACK_QUEEN, EscapeSequences.BLACK_BISHOP,
                EscapeSequences.BLACK_KNIGHT, EscapeSequences.BLACK_ROOK};

        System.out.println(EscapeSequences.ERASE_SCREEN);

        int startRow = whiteOnTop ? 1 : 8;
        int endRow = whiteOnTop ? 8 : 1;
        int rowStep = whiteOnTop ? 1 : -1;
        int startCol = whiteOnTop ? 0 : 7;
        int endCol = whiteOnTop ? 7 : 0;
        int colStep = whiteOnTop ? 1 : -1;

        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY);
        for (int i = endCol; i + colStep != startCol; i -= colStep) {
            System.out.print(columnLabels[i]);
        }
        System.out.println(EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
        for (int i = startRow; i - rowStep != endRow; i += rowStep) {
            System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + "\u2003" + i + " ");
            switch (i) {
                case 1:
                    for (int j = startCol; j - colStep != endCol; j += colStep) {
                        String backgroundColor = (j % 2 == 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREY :
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                        System.out.print(backgroundColor + blackRow[j]);
                    }
                    break;
                case 2:
                    for (int j = startCol; j - colStep != endCol; j += colStep) {
                        String backgroundColor = (j % 2 != 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREY :
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                        System.out.print(backgroundColor + EscapeSequences.BLACK_PAWN);
                    }
                    break;
                case 7:
                    for (int j = startCol; j - colStep != endCol; j += colStep) {
                        String backgroundColor = (j % 2 == 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREY :
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                        System.out.print(backgroundColor + EscapeSequences.WHITE_PAWN);
                    }
                    break;
                case 8:
                    for (int j = startCol; j - colStep != endCol; j += colStep) {
                        String backgroundColor = (j % 2 != 0) ? EscapeSequences.SET_BG_COLOR_DARK_GREY :
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                        System.out.print(backgroundColor + whiteRow[j]);
                    }
                    break;
                default:
                    int even = (i % 2 == 0) ? 1 : 0;
                    for (int j = startCol; j - colStep != endCol; j += colStep) {
                        String backgroundColor = (j % 2 == even) ? EscapeSequences.SET_BG_COLOR_DARK_GREY :
                                EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
                        System.out.print(backgroundColor + EscapeSequences.EMPTY);
                    }
                    break;
            }
            System.out.println(EscapeSequences.SET_BG_COLOR_BLACK + "\u2003" + i + " " + EscapeSequences.RESET_BG_COLOR);
        }
        System.out.print(EscapeSequences.SET_BG_COLOR_BLACK + EscapeSequences.EMPTY);
        for (int i = endCol; i + colStep != startCol; i -= colStep) {
            System.out.print(columnLabels[i]);
        }
        System.out.println(EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR);
        
    }
}
