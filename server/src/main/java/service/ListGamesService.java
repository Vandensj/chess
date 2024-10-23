package service;

import dataaccess.*;
import model.GameData;

import java.util.List;

public class ListGamesService {
    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    public ListGamesService(UserDAO userDAO, GameDAO gameDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void verifyAuthToken(String authToken) throws DataAccessException {
        if(authDAO.verifyAuthToken(authToken)) {
            throw new DataAccessException("Invalid Auth Token");
        }
    }

    public List<GameData> listGames(String authToken) throws DataAccessException {
        return gameDAO.listGames();
    }
}
