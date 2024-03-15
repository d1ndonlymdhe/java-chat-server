package javahttp.models;


import javahttp.helpers.DBConn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Notification {
    public String id;
    public String content;
    public String receiverId;

    public Notification(String id, String message, String userId) {
        this.id = id;
        this.content = message;
        this.receiverId = userId;
    }

    static public List<Notification> getNotifications(String userId) {
        Connection conn = DBConn.getConn();
        if (userId == null || userId.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            List<Notification> notifications = new ArrayList<>();
            PreparedStatement getNotificationsStmt = conn.prepareStatement("SELECT * FROM notifications WHERE receiver_id = ?");
            getNotificationsStmt.setString(1, userId);
            var results = getNotificationsStmt.executeQuery();
            while (results.next()) {
                notifications.add(new Notification(results.getString("id"), results.getString("content"), results.getString("receiver_id")));
            }
            return notifications;
        } catch (SQLException e) {
            return new ArrayList<>();
        }
    }

}
