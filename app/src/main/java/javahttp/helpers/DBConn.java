package javahttp.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConn {
    static Connection conn;

    private DBConn() {
        if (conn == null) {
            String host = System.getenv("DB_HOST");
            String user = System.getenv("DB_USER");
            String pass = System.getenv("DB_PASS");
            if (host.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                System.err.println("Environment not set");
                return;
            }
            try {
                conn = DriverManager.getConnection(host, user, pass);

            } catch (SQLException err) {
                System.err.println("Could not connect to DB");
            }
        }
    }

    public static Connection getConn() {
        if (conn == null) {
            new DBConn();
            return conn;
        }
        return conn;
    }

    public static void close() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Could not close connection to DB");

        }
    }

}
