package org;

import org.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {

    private static NotificationService instance;

    public NotificationService() {

    }

    public static NotificationService getInstance() {
        if (instance == null) {
            synchronized (NotificationService.class) {
                if (instance == null) {
                    instance = new NotificationService();
                }
            }
        }
        return instance;
    }
    public String sendNotification(int chefId, String taskName) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtInsert = conn.prepareStatement("INSERT INTO notifications (chef_id, task_name) VALUES (?, ?)")) {

            stmtInsert.setInt(1, chefId);
            stmtInsert.setString(2, taskName);

            int rowsInserted = stmtInsert.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Notification sent to Chef ID: " + chefId + " for task: " + taskName);
                return "Notification sent to Chef ID: " + chefId + " for task: " + taskName;
            } else {
                System.out.println("Chef ID '" + chefId + "' not found in database.");
                return "Error: Chef ID '" + chefId + "' not found.";
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return "Error sending notification";
        }
    }


    public String getChefNotifications(int chefid) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT task_name FROM notifications WHERE chef_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, String.valueOf(chefid));
            ResultSet rs = stmt.executeQuery();

            StringBuilder notifications = new StringBuilder();
            while (rs.next()) {
                notifications.append(rs.getString("task_name"));
            }
            return notifications.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No notifications";
    }

    public List<String> getNotificationsListForChef(int chefId) {
        List<String> notificationsList = new ArrayList<>();
        String sql = "SELECT task_name FROM notifications WHERE chef_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, chefId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                notificationsList.add(rs.getString("task_name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notificationsList;
    }
    public  boolean sendChefRemindersForTomorrow() throws InterruptedException {


        String query = "SELECT t.task_name, t.chef_id " +
                "FROM tasks t " +
                "JOIN orders o ON t.order_id = o.order_id " +
                "WHERE o.order_date = date('now', '+1 day') AND t.status = 'Acknowledge'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {


            while (rs.next()) {
                String taskName = rs.getString("task_name");
                int chefId = rs.getInt("chef_id");
                sendNotification(chefId, taskName);

            }
            return true ;
        } catch (SQLException e) {
            e.printStackTrace();
            return false ;
        }
    }


}


