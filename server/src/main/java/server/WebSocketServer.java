package server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebSocket
public class WebSocketServer {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private static final Map<Integer, List<Session>> sessions = new HashMap<>();

    public WebSocketServer(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    @OnWebSocketConnect
    public void onOpen(Session session) {
        System.out.println("New connection: " + session.getId());
    }

    @OnWebSocketMessage
    public void onMessage(String message, Session session) {
        // Deserialize message into UserGameCommand
        UserGameCommand command = parseCommand(message);
        try {
            handleCommand(command, session);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private UserGameCommand parseCommand(String message) {
        return new Gson().fromJson(message, UserGameCommand.class);
    }

    private void handleCommand(UserGameCommand command, Session session) throws DataAccessException {
        // Process CONNECT, MAKE_MOVE, LEAVE, and RESIGN commands
        switch (command.getCommandType()) {
            case CONNECT:
                handleConnect(command, session);
                break;
            case MAKE_MOVE:
                handleMakeMove(command);
                break;
            case LEAVE:
                handleLeave(command);
                break;
            case RESIGN:
                handleResign(command);
                break;
            default:
                System.out.println("Unknown command type: " + command.getCommandType());
                break;
        }
    }

    private void handleConnect(UserGameCommand command, Session session) throws DataAccessException {
        // Load game, notify players, etc.
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = authDAO.getUsername(authToken);
        GameData gameData = gameDAO.getGame(gameID);

        if (username == null) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            sendMessage(msg, session);
            return;
        }
        if (!gameDAO.verifyGame(gameID)) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            sendMessage(msg, session);
            return;
        }

        sessions.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username +
                " has joined the game");
        broadcastMessage(notification, gameID);
    }

    private void handleMakeMove(UserGameCommand command) {
        // Validate move, update game, notify players
    }

    private void handleLeave(UserGameCommand command) {
        // Notify other players and update the game state
    }

    private void handleResign(UserGameCommand command) {
        // End game and notify all connected clients
    }

    private void broadcastMessage(ServerMessage msg, Integer gameID) {
        List<Session> gameSessions = sessions.get(gameID);
        if (gameSessions != null) {
            for (Session session : gameSessions) {
                sendMessage(msg, session);
            }
        }
    }

    private void sendMessage(ServerMessage message, Session session) {
        try {
            String json = new Gson().toJson(message);
            session.getBasicRemote().sendText(json);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}
