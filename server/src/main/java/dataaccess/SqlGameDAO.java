package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class SqlGameDAO implements GameDAO {
    static int id = 0;

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing game: " + e.getMessage());
        }
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        id++;
        String sql = "INSERT INTO game(gameId, gameName, chessGame) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, gameName);
            String chessGameJson = new Gson().toJson(new ChessGame());
            preparedStatement.setString(3, chessGameJson);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error creating game: " + e.getMessage());
        }
        return id;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        String sql = "SELECT gameId, gameName, whiteUsername, blackUsername, chessGame FROM chess_game WHERE gameId = ?";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, gameID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    int GameID = resultSet.getInt("gameId");
                    String GameName = resultSet.getString("gameName");
                    String WhiteUsername = resultSet.getString("whiteUsername");
                    String BlackUsername = resultSet.getString("blackUsername");
                    ChessGame chessGame = new Gson().fromJson(resultSet.getString("chessGame"), ChessGame.class);
                    return new GameData(GameID, WhiteUsername, BlackUsername, GameName, chessGame);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        String sql = "SELECT gameId, gameName, whiteUsername, blackUsername, chessGame FROM game";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int GameID = resultSet.getInt("gameId");
                String GameName = resultSet.getString("gameName");
                String WhiteUsername = resultSet.getString("whiteUsername");
                String BlackUsername = resultSet.getString("blackUsername");
                ChessGame chessGame = new Gson().fromJson(resultSet.getString("chessGame"), ChessGame.class);
                games.add(new GameData(GameID, WhiteUsername, BlackUsername, GameName, chessGame));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games: " + e.getMessage());
        }
        return games;
    }

    @Override
    public void updateGame(ChessGame.TeamColor playerColor, Integer gameID, String username)
            throws DataAccessException {
        String sql;
        if (playerColor == ChessGame.TeamColor.BLACK) {
            sql = "UPDATE chess_game SET blackUsername = ? WHERE gameId = ?";
        } else {
            sql = "UPDATE chess_game SET whiteUsername = ? WHERE gameId = ?";
        }
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            preparedStatement.setInt(2, gameID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    @Override
    public Integer getSize() throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM game";
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting size: " + e.getMessage());
        }
        return 0;
    }

    @Override
    public Boolean verifyGame(Integer gameID) throws DataAccessException {
        String sql = "SELECT 1 FROM game WHERE gameId = ? LIMIT 1";
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, gameID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error verifying user: " + e.getMessage());
        }
        return false;
    }
}
