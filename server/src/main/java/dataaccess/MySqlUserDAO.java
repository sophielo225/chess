package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MySqlUserDAO implements SqlUserDAO, SqlDAO {

    public MySqlUserDAO() throws ResponseException {
        configureDatabase(CREATE_USERS);
    }

    @Override
    public void clear() throws ResponseException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    @Override
    public UserData createUser(String username, String password, String email) throws ResponseException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        executeUpdate(statement, username, hashedPassword, email);
        return new UserData(username, password, email);
    }

    private UserData readUser(ResultSet rs) throws SQLException {
        var username = rs.getString("username");
        var password = rs.getString("password");
        var email = rs.getString("email");
        return new UserData(username, password, email);
    }

    @Override
    public UserData getUser(String username, String password) throws ResponseException {
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement("SELECT username, password, email FROM users WHERE username=?")) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    var userData = readUser(rs);
                    var hashedPassword = userData.password();
                    if (BCrypt.checkpw(password, hashedPassword)) {
                        return userData;
                    } else {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public int getUserSize() throws ResponseException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT COUNT(*) FROM users";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return 0;
    }
}
