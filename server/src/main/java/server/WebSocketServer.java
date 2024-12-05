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
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

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
        System.out.println("New connection");
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("in onMessage");
        // Deserialize message into UserGameCommand
        if (message.contains("MAKE_MOVE")) {
            MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
            try {
                handleMakeMove(command, session);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
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
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            sendMessage(msg, session);
            return;
        }
        if (!gameDAO.verifyGame(gameID)) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
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

        ServerMessage notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, username +
                " has joined the game as " + color);
        broadcastMessageExclude(notification, gameID, session);

        ServerMessage gameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData.game());
        sendMessage(gameMessage, session);
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
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            sendMessage(msg, session);
            return;
        }
        if (!gameDAO.verifyGame(gameID)) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            sendMessage(msg, session);
            return;
        }
        if (gameData.game().isOver()) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game is over");
            sendMessage(msg, session);
            return;
        }
        try {
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = game.getTeamTurn();
            ChessGame.TeamColor opponent = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            game.makeMove(move);
            gameDAO.updateChessGame(game, gameID);
            ServerMessage msg = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game);
            broadcastMessage(msg, gameID);

            msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, color +
                    " has made a move");
            broadcastMessageExclude(msg, gameID, session);
            if (game.isInCheck(opponent)) {
                msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, opponent +
                        " is in check");
                broadcastMessage(msg, gameID);
            } else if (game.isInCheckmate(opponent)) {
                msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, opponent +
                        " is in checkmate, " + color + " wins");
                broadcastMessage(msg, gameID);
                game.setGameOver(true);
            } else if (game.isInStalemate(opponent) || game.isInStalemate(color)) {
                msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, "game ends in " +
                        "stalemate");
                broadcastMessage(msg, gameID);
                game.setGameOver(true);
            }
        } catch (InvalidMoveException e) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, e.getMessage());
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
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            sendMessage(msg, session);
            return;
        }
        if (!gameDAO.verifyGame(gameID)) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            sendMessage(msg, session);
            return;
        }

        String color = "observer";
        if (Objects.equals(gameData.whiteUsername(), username)) {
            color = "white";
        } else if (Objects.equals(gameData.blackUsername(), username)) {
            color = "black";
        }

        ServerMessage msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                color + " user " + username + " has left the game");
        broadcastMessageExclude(msg, gameID, session);
        if (!color.equals("observer")) {
            ChessGame.TeamColor teamColor = (color.equals("white")) ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
            gameDAO.updateGame(teamColor, gameID, null);
        }
        sessions.get(gameID).remove(session);
    }

    private void handleResign(UserGameCommand command, Session session) throws DataAccessException {
        // End game and notify all connected clients
        Integer gameID = command.getGameID();
        String authToken = command.getAuthToken();
        String username = authDAO.getUsername(authToken);
        GameData gameData = gameDAO.getGame(gameID);

        if (username == null) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "invalid authToken");
            sendMessage(msg, session);
            return;
        }
        if (!gameDAO.verifyGame(gameID)) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game not found");
            sendMessage(msg, session);
            return;
        }
        if (gameData.game().isOver()) {
            ServerMessage msg = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "game is over");
            sendMessage(msg, session);
        } else {
            ChessGame game = gameData.game();
            ChessGame.TeamColor color = game.getTeamTurn();
            ChessGame.TeamColor opponent = (game.getTeamTurn() == ChessGame.TeamColor.WHITE) ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            ServerMessage msg = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, color + " has " +
                    "resigned, " + opponent + " wins");
            broadcastMessage(msg, gameID);
            game.setGameOver(true);
            gameDAO.updateChessGame(game, gameID);
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error: " + throwable.getMessage());
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("Connection closed, reason: " + reason);
    }

    private void broadcastMessage(ServerMessage msg, Integer gameID) {
        List<Session> gameSessions = sessions.get(gameID);
        if (gameSessions != null) {
            for (Session session : gameSessions) {
                sendMessage(msg, session);
            }
        }
    }

    private void broadcastMessageExclude(ServerMessage msg, Integer gameID, Session exclude) {
        List<Session> gameSessions = sessions.get(gameID);
        if (gameSessions != null) {
            for (Session session : gameSessions) {
                if (session != exclude) {
                    sendMessage(msg, session);
                }
            }
        }
    }

    private void sendMessage(ServerMessage message, Session session) {
        try {
            String json = new Gson().toJson(message);
            session.getRemote().sendString(json);
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }
}
