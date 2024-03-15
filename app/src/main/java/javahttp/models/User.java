package javahttp.models;

import javahttp.helpers.DBConn;
import javahttp.helpers.Hash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class User {
    public String id;
    public String username;
    public String salt;

    public String hash;

    public User(String id, String username, String salt, String hash) {
        this.id = id;
        this.username = username;
        this.salt = salt;
        this.hash = hash;
    }

    public static Boolean checkPassword(String username, String password) throws SQLException {
        Connection conn = DBConn.getConn();
        if (username.isEmpty() || password.isEmpty()) {
            return false;
        }
        PreparedStatement checkStmt = conn.prepareStatement("SELECT username,salt,hash FROM users WHERE username = ?");
        checkStmt.setString(1, username);
        ResultSet rs = checkStmt.executeQuery();
        if (!rs.next()) {
            return false;
        }
        String salt = rs.getString("salt");
        String hash = rs.getString("hash");
        String inputHash = Hash.sha256(password + salt);
        return hash.equals(inputHash);
    }

    public static Boolean checkUserExists(String username) throws SQLException {
        Connection conn = DBConn.getConn();
        if (username.isEmpty()) {
            return false;
        }
        PreparedStatement checkStmt = conn.prepareStatement("SELECT username FROM users WHERE username = ?");
        checkStmt.setString(1, username);
        return checkStmt.executeQuery().next();
    }

    public static Boolean addUser(String username, String password) throws SQLException {
        if (username.isEmpty() || password.isEmpty()) {
            System.err.println("Incomplete Input");
            return false;
        }

        String id = UUID.randomUUID().toString();
        String salt = Hash.sha256(username + Instant.now().toEpochMilli());
        String hash = Hash.sha256(password + salt);
        Connection conn = DBConn.getConn();
        String addUserQuery = "INSERT INTO users(id, username, salt, hash) VALUES  (?,?,?,?)";
        PreparedStatement addUserStmt = conn.prepareStatement(addUserQuery);
        addUserStmt.setString(1, id);
        addUserStmt.setString(2, username);
        addUserStmt.setString(3, salt);
        addUserStmt.setString(4, hash);
        return addUserStmt.executeUpdate() > 0;
    }

    public static List<User> searchUser(String username) throws SQLException {
        Connection conn = DBConn.getConn();
        PreparedStatement searchStmt = conn.prepareStatement("SELECT * FROM users WHERE username LIKE ?");
        searchStmt.setString(1, "%" + username + "%");
        ResultSet rs = searchStmt.executeQuery();
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            users.add(new User(rs.getString("id"), rs.getString("username"), null, null));
        }
        return users;
    }

}
