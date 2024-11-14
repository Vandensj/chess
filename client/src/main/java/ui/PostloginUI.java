package ui;

import chess.ChessGame;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PostloginUI {

    private Client client;
    private Scanner scanner;
    private Map<Integer, String> gameList; // Maps displayed game numbers to game IDs for easy access
    private String authToken;

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
            System.out.print("> ");
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
            if (response.contains("success")) {  // Adjust based on actual response from server
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
            if (response.contains("success")) {  // Adjust based on actual response
                System.out.println("Game created successfully with name: " + gameName);
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

                // Reset game list map
                gameList.clear();

                // Parse and display games (pseudo-parsing shown here)
                // Format should be adjusted based on actual server response
                // Assuming response is JSON or a structured string containing game names and player info
                String[] games = response.split(","); // Simplify for example, parse JSON or similar structure
                int count = 1;
                for (String gameInfo : games) {
                    // Extracting game ID and details here (simplified)
                    String gameId = "parsedGameId"; // Parse actual game ID from response
                    String gameName = "parsedGameName"; // Parse actual game name
                    String players = "parsedPlayers"; // Parse actual player info

                    System.out.println(count + ". " + gameName + " - Players: " + players);
                    gameList.put(count, gameId);
                    count++;
                }
            } else {
                System.out.println("No games currently available.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while listing games: " + e.getMessage());
        }
    }

    private void handlePlayGame() {
        System.out.print("Enter the number of the game you want to join: ");
        int gameNumber = Integer.parseInt(scanner.nextLine());

        if (!gameList.containsKey(gameNumber)) {
            System.out.println("Invalid game number. Please list games and try again.");
            return;
        }

        System.out.print("Enter your color (e.g., 'white' or 'black'): ");
        ChessGame.TeamColor color = (scanner.nextLine() == "white") ? ChessGame.TeamColor.WHITE :
                ChessGame.TeamColor.BLACK;

        String gameId = gameList.get(gameNumber);
        try {
            String response = client.getServerFacade().joinGame(gameId,color, authToken);
            if (response.contains("success")) {  // Adjust based on actual server response
                System.out.println("Joined game successfully.");

                // Transition to GameUI for gameplay
                new GameUI(client, gameId, color).start();
            } else {
                System.out.println("Failed to join game. Please try another.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred while joining the game: " + e.getMessage());
        }
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
    }
}
