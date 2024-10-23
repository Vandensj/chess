package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

public class RegisterService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public RegisterService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public String createUser(String username, String password, String email) throws DataAccessException {
        if (username == null || password == null || email == null || username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            throw new IllegalArgumentException("Username, password, and email are required");
        }
        if (userDAO.getUser(username, password) != null) {
            throw new DataAccessException("Username is already taken");
        }
        userDAO.createUser(username, password, email);
        return authDAO.createAuthToken(username);
    }

}
