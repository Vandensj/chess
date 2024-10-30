package dataaccess;

public class SqlAuthDAO implements AuthDAO {
    @Override
    public void clear() throws DataAccessException {

    }

    @Override
    public String getUsername(String authToken) throws DataAccessException {
        return "";
    }

    @Override
    public String createAuthToken(String username) throws DataAccessException {
        return "";
    }

    @Override
    public void deleteAuthToken(String authToken) throws DataAccessException {

    }

    @Override
    public Integer getSize() throws DataAccessException {
        return 0;
    }

    @Override
    public Boolean verifyAuthToken(String authToken) throws DataAccessException {
        return null;
    }
}
