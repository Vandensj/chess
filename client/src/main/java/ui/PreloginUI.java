package ui;

import ui.Client;

import java.util.Scanner;

public class PreloginUI {

    private Client client;
    private Scanner scanner;

    public PreloginUI(Client client) {
        this.client = client;
        this.scanner = new Scanner(System.in);
    }

    // Main loop for handling user input
    public void start() {
        System.out.println("Welcome! Type 'help' for a list of commands.");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "help":
                    displayHelp();
                    break;
                case "quit":
                    System.out.println("Exiting program...");
                    System.exit(0);
                    break;
                case "login":
                    handleLogin();
                    break;
                case "register":
                    handleRegister();
                    break;
                default:
                    System.out.println("Unknown command. Type 'help' for available commands.");
                    break;
            }
        }
    }

    private void displayHelp() {
        System.out.println("Available commands:");
        System.out.println("help      - Displays this help message.");
        System.out.println("quit      - Exits the program.");
        System.out.println("login     - Logs in an existing user.");
        System.out.println("register  - Registers a new user account and logs in.");
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            String response = client.getServerFacade().login(username, password);

            if (response.contains("success")) {  // Adjust based on actual success response
                System.out.println("Login successful!");

                // Connect WebSocket for real-time communication after login
                client.connectWebSocket("ws://localhost:" + client.getServerFacade().getPort() + "/ws");

                // Transition to the post-login UI
                new PostloginUI(client).start();
            } else {
                System.out.println("Login failed. Please check your username and password.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during login: " + e.getMessage());
        }
    }

    private void handleRegister() {
        System.out.print("Enter new username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new password: ");
        String password = scanner.nextLine();

        try {
            String response = client.getServerFacade().sendRegisterRequest(username, password);

            if (response.contains("success")) {  // Adjust based on actual success response
                System.out.println("Registration successful!");

                // Connect WebSocket for real-time communication after registration
                client.connectWebSocket("ws://localhost:" + client.getServerFacade().getPort() + "/ws");

                // Transition to the post-login UI
                new PostloginUI(client).start();
            } else {
                System.out.println("Registration failed. Username might already be taken.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during registration: " + e.getMessage());
        }
    }
}
