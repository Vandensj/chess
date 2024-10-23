package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

public class RegisterService {

    private UserDAO userDAO;
    private AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String createUser(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null) {
            throw new IllegalArgumentException("Username, password, and email are required");
        }
        if (userDAO.getUser(username, password) != null) {
            throw new DataAccessException("Username is already taken");
        }
        userDAO.createUser(username, password, email);
        String authToken = authDAO.createAuthToken(username);
        return authToken;
    }

}
