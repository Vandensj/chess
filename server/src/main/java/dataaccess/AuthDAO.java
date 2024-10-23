package dataaccess;

public interface AuthDAO {
    void clear();

    String getAuthToken(String username, String password) throws DataAccessException;

    String createAuthToken(String username);

    void deleteAuthToken(String authToken);

    Integer getSize();
}
