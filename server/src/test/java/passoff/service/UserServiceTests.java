package passoff.service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.ClearService;
import service.GameService;
import service.RegisterService;
import service.UserService;

public class UserServiceTests {

    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;

    static ClearService clearService;
    static RegisterService registerService;
    static UserService userService;
    static GameService gameService;

    @BeforeAll
    public static void init() {
        userDAO = new MemUserDAO();
        authDAO = new MemAuthDAO();
        gameDAO = new MemGameDAO();

        clearService = new ClearService(gameDAO, authDAO, userDAO);
        registerService = new RegisterService(userDAO, authDAO);
        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(userDAO, gameDAO, authDAO);
    }

    @Test
    public void registerSuccess() throws Exception {
        registerService.createUser("Sam", "password", "me@gmail.com");

        Assertions.assertNotNull(userDAO.getUser("Sam", "password"));
    }

    @Test
    public void registerFail() throws Exception {
        RegisterService registerService = new RegisterService(new MemUserDAO(), new MemAuthDAO());
        registerService.createUser("Sam", "password", "me@gmail.com");
        try {
            registerService.createUser("Sam", "password1", "me2@gmail.com");
        } catch (Exception e) {
            Assertions.assertInstanceOf(DataAccessException.class, e);
        }
    }

    @Test
    public void loginSuccess() throws Exception {
        registerService.createUser("Sam", "password", "me@gmail.com");
        Assertions.assertNotNull(userDAO.getUser("Sam", "password"));
        Assertions.assertTrue(authDAO.verifyAuthToken(userService.loginUser("Sam", "password")));
    }

    @Test
    public void loginFail() throws Exception {
        Assertions.assertFalse(authDAO.verifyAuthToken(userService.loginUser("Sam", "password")));
    }

}
