package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemUserDOA implements UserDAO {
    protected static Map<String, UserData> users = new HashMap<>();

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        UserData user = new UserData(username, password, email);
        users.put(username, user);
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public Integer getSize() throws DataAccessException {
        return users.size();
    }
}
