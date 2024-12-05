package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClient {
    private Session session;
    private final Gson gson = new Gson();
    private ChessGame game;

    // Constructor: Connects to the server
    public WebSocketClient(String serverUri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(serverUri));
        } catch (Exception e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
        }
    }

    // Called when the WebSocket connection is established
    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Connected to server.");
        this.session = session;
    }

    // Called when a message is received from the server
    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received: " + message);

        // Deserialize the message into a ServerMessage object
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        // Handle the message based on its type
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME:
                LoadGameMessage gameMessage = (LoadGameMessage) serverMessage;
                handleLoadGame(gameMessage.getGame());
                break;
            case ERROR:
                ErrorMessage errorMessage = (ErrorMessage) serverMessage;
                handleError(errorMessage.getErrorMessage());
                break;
            case NOTIFICATION:
                NotificationMessage notificationMessage = (NotificationMessage) serverMessage;
                handleNotification(notificationMessage.getMessage());
                break;
            default:
                System.err.println("Unknown server message type.");
        }
    }

    // Called when the WebSocket connection is closed
    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("Connection closed: " + closeReason);
    }

    // Called when an error occurs
    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error: " + throwable.getMessage());
    }

    // Send a message to the server
    public void sendMessage(UserGameCommand command) {
        try {
            if (session != null && session.isOpen()) {
                String json = gson.toJson(command);
                session.getBasicRemote().sendText(json);
            } else {
                System.err.println("Cannot send message: WebSocket session is closed.");
            }
        } catch (Exception e) {
            System.err.println("Failed to send message: " + e.getMessage());
        }
    }

    // Close the WebSocket connection
    public void close() {
        try {
            if (session != null && session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            System.err.println("Failed to close WebSocket: " + e.getMessage());
        }
    }

    // Handle LOAD_GAME messages
    private void handleLoadGame(ChessGame game) {
        this.game = game;
        drawBoard(game.getBoard());
    }

    // Handle ERROR messages
    private void handleError(String errorMessage) {
        System.err.println("Error from server: " + errorMessage);
        // Handle the error appropriately (e.g., show a user-facing message)
    }

    // Handle NOTIFICATION messages
    private void handleNotification(String message) {
        System.out.println("Notification: " + message);
        // Display the notification in the UI or console
    }

    public ChessGame getGame() {
        return game;
    }
}
