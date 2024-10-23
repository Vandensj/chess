package dataaccess;

import dataaccess.datatypes.UserData;

public interface UserDAO {

    void createUser(String username, String password, String email) throws DataAccessException;

    UserData getUser(String username, String password) throws DataAccessException;

    void clear() throws DataAccessException;

    Integer getSize() throws DataAccessException;
}