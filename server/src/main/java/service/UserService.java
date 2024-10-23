package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String loginUser(String username, String password) throws DataAccessException {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        if (userDAO.getUser(username, password) == null) {
            throw new DataAccessException("No user found");
        }
        return authDAO.createAuthToken(username);
    }

    public void logoutUser(String token) throws DataAccessException {
        authDAO.deleteAuthToken(token);
    }
}
