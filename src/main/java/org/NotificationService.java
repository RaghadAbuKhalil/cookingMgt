package org;

import org.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class NotificationService {

    private static NotificationService instance;
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());

    private NotificationService() {

    }

    public static synchronized NotificationService getInstance() {
        if (instance == null) {
            instance = new NotificationService();
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
                return "Notification sent to Chef ID: " + chefId + " for task: " + taskName;
            }
        } catch (SQLException e) {
            logger.warning("Database error: " + e.getMessage());

        }
        return "Error sending notification";
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
            logger.warning(e.getMessage());
        }

        return notificationsList;
    }
    public  boolean sendChefRemindersForTomorrow()  {


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
            logger.warning(e.getMessage());
            return false ;
        }
    }


}


