package dataaccess;

import model.UserData;

public class SqlUserDAO implements UserDAO {
    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        return null;
    }

    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public Integer getSize() throws DataAccessException {
        return 0;
    }

    @Override
    public Boolean verifyUser(String username) throws DataAccessException {
        return null;
    }
}
