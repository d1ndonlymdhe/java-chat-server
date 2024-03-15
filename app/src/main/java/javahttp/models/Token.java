package javahttp.models;

import javahttp.helpers.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Token {
    public String id;
    public String userId;
    public String value;

    public User user;

    public Token(String id, String userId, String value) {
        this.id = id;
        this.userId = userId;
        this.value = value;
    }

    public Token(String id, String userId, String value, User user) {
        this.id = id;
        this.userId = userId;
        this.value = value;
        this.user = user;
    }

    public static Boolean addToken(String userId) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO tokens (id,user_id) VALUES (?,?)");
        stmt.setString(1, java.util.UUID.randomUUID().toString());
        stmt.setString(2, userId);
        return stmt.executeUpdate() > 0;
    }

    public static Boolean deleteToken(String userId) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM tokens WHERE user_id = ?");
        stmt.setString(1, userId);
        return stmt.executeUpdate() > 0;
    }

    public static Boolean checkTokenExists(String token) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tokens WHERE id = ?");
        stmt.setString(1, token);
        return stmt.executeQuery().next();
    }

    public static String getUserId(String token) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM tokens WHERE id = ?");
        stmt.setString(1, token);
        return stmt.executeQuery().getString("user_id");
    }

    public static Token getToken(String token) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM tokens INNER JOIN users ON tokens.user_id = users.id WHERE tokens.id = ?");
        stmt.setString(1, token);
        var result = stmt.executeQuery();
        if (!result.next()) {
            return null;
        }
        return new Token(result.getString("id"), result.getString("user_id"), result.getString("value"), new User(result.getString("id"), result.getString("username"), result.getString("salt"), result.getString("hash")));
    }

}
