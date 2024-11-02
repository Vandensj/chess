package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.List;
import java.sql.*;

public class SqlGameDAO implements GameDAO {
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(ChessGame.TeamColor playerColor, Integer gameID, String username)
            throws DataAccessException {

    }

    @Override
    public Integer getSize() throws DataAccessException {
        return 0;
    }

    @Override
    public Boolean verifyGame(Integer gameID) throws DataAccessException {
        return null;
    }
}
