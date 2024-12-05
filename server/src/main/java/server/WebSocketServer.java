package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.util.*;

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
                handleMakeMove((MakeMoveCommand) command, session);
                break;
            case LEAVE:
                handleLeave(command, session);
                break;
            case RESIGN:
                handleResign(command, session);
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

        String color = "observer";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            color = "white";
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            color = "black";
        }

        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username +
                " has joined the game as " + color);
        broadcastMessage(notification, gameID);
    }

    private void handleMakeMove(MakeMoveCommand command, Session session)
            throws DataAccessException {
        // Validate move, update game, notify players
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = authDAO.getUsername(authToken);
        GameData gameData = gameDAO.getGame(gameID);
        ChessMove move = command.getMove();

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
        if (gameData.game().isOver()) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "game is over");
            sendMessage(msg, session);
            return;
        }
        try {
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = game.getTeamTurn();
            ChessGame.TeamColor opponent = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            game.makeMove(move);
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, color +
                    " has made a move");
            broadcastMessage(msg, gameID);
            if (game.isInCheck(opponent)) {
                msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, opponent +
                        " is in check");
                broadcastMessage(msg, gameID);
            } else if (game.isInCheckmate(opponent)) {
                msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, opponent +
                        " is in checkmate, " + color + " wins");
                broadcastMessage(msg, gameID);
                game.setGameOver(true);
            } else if (game.isInStalemate(opponent) || game.isInStalemate(color)) {
                msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, "game ends in " +
                        "stalemate");
                broadcastMessage(msg, gameID);
                game.setGameOver(true);
            }
        } catch (InvalidMoveException e) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
            sendMessage(msg, session);
        }
    }

    private void handleLeave(UserGameCommand command, Session session) throws DataAccessException {
        // Notify other players and update the game state
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

        String color = "observer";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            color = "white";
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            color = "black";
        }

        ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                color + " user " + username + " has left the game");
        broadcastMessage(msg, gameID);
        sessions.get(gameID).remove(session);
    }

    private void handleResign(UserGameCommand command, Session session) throws DataAccessException {
        // End game and notify all connected clients
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
        if (gameData.game().isOver()) {
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "game is over");
            sendMessage(msg, session);
        } else {
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = game.getTeamTurn();
            ChessGame.TeamColor opponent = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            ServerMessage msg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, color + " has " +
                    "resigned, " + opponent + " wins");
            broadcastMessage(msg, gameID);
            game.setGameOver(true);
        }
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
