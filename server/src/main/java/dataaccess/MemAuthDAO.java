package dataaccess;

import dataaccess.datatypes.AuthData;

import java.util.HashMap;
import java.util.Map;

public class MemAuthDAO implements AuthDAO {
    private static Map<String, AuthData> authentications = new HashMap<String, AuthData>();

    @Override
    public void clear() {
        authentications.clear();
    }

    @Override
    public String getAuthToken(String username, String password) {
        return "";
    }

    @Override
    public String createAuthToken(String username) {
        return "";
    }

    @Override
    public void deleteAuthToken(String authToken) {
        authentications.remove(authToken);
    }

    @Override
    public Integer getSize() {
        return authentications.size();
    }
}
