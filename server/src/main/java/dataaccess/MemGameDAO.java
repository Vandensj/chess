package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import dataaccess.datatypes.GameData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemGameDAO implements GameDAO {
    private static Integer newID = 0;
    private static Map<Integer, GameData> games = new HashMap<Integer, GameData>();

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public Integer createGame(String gameName) throws DataAccessException {
        newID++;
        games.put(newID, new GameData(newID, null, null, gameName, new ChessGame()));
        return newID;
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public List<GameData> listGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<GameData>();
        for(Map.Entry<Integer, GameData> entry : games.entrySet()) {
            gameList.add(entry.getValue());
        }
        return gameList;
    }

    @Override
    public void updateGame(ChessGame.TeamColor playerColor, Integer gameID, String username) throws DataAccessException {
        games.get(gameID).whiteUsername() = username;
    }

    @Override
    public ChessGame makeChessMove(ChessMove move, Integer gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Integer getSize() throws DataAccessException {
        return games.size();
    }
}
