package org;

import java.sql.*;
import java.util.logging.Logger;


import org.database.DatabaseConnection;
import org.database.DatabaseSetup;


public class TaskManager {

    private static final Logger logger = Logger.getLogger(TaskManager.class.getName());

    private static TaskManager instance;

   private TaskManager() {
       DatabaseSetup.setupDatabase();
    }

    public static synchronized TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    public int assignTaskToChef(int orderId, String mealName) {
        try (Connection conn = DatabaseConnection.getConnection()) {


            String query = "SELECT chef_id FROM CHEFS ORDER BY jobload ASC LIMIT 1";


            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int chefId = rs.getInt("chef_id");





            String insertTaskQuery = "INSERT INTO tasks (order_id, chef_id, task_name, status) VALUES (?, ?, ?, 'Acknowledge')";
            try (PreparedStatement stmt2 = conn.prepareStatement(insertTaskQuery)) {
                stmt2.setInt(1, orderId);
                stmt2.setInt(2, chefId);
                stmt2.setString(3, mealName);
                stmt2.executeUpdate();
                NotificationService.getInstance().sendNotification(chefId, "New Task Assigned: " + mealName);
            }
            return chefId;
        }
            }

        }

                catch (SQLException e) {
           logger.info(e.getMessage());

        }

        return -1;
    }
    public String TaskStatus(String taskName, int chefid) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status FROM tasks WHERE task_name = ? AND chef_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, taskName);
                stmt.setInt(2, chefid);

               logger.info("Executing query with taskName: " + taskName + " and chefId: " + chefid);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {

                    String status = rs.getString("status");
                    logger.info("Found status: " + status);
                    return status;
                }
        }} catch (SQLException e) {
            logger.warning(e.getMessage());
            logger.warning("No matching task found for taskName: " + taskName + " and chefId: " + chefid);

            }
        return null;
    }

    public int assignTaskToChefByExpertise(String taskName, String requiredExpertise) {
        String selectSql = "SELECT chef_id, jobload FROM CHEFS WHERE expertise = ? ORDER BY jobload ASC LIMIT 1";
        String insertTaskSql = "INSERT INTO TASKS (task_name, chef_id, status) VALUES (?, ?, 'Acknowledge')";
        String updateLoadSql = "UPDATE CHEFS SET jobload = jobload + 1 WHERE chef_id = ?";

        int selectedChefId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {

            selectStmt.setString(1, requiredExpertise);

            try (ResultSet rs = selectStmt.executeQuery()) {
                if (rs.next()) {
                    selectedChefId = rs.getInt("chef_id");

                    try (PreparedStatement insertStmt = conn.prepareStatement(insertTaskSql)) {
                        insertStmt.setString(1, taskName);
                        insertStmt.setInt(2, selectedChefId);
                        insertStmt.executeUpdate();
                    }

                    try (PreparedStatement updateStmt = conn.prepareStatement(updateLoadSql)) {
                        updateStmt.setInt(1, selectedChefId);
                        updateStmt.executeUpdate();
                    }

                    System.out.println("Task '" + taskName + "' assigned to chef with ID: " + selectedChefId);
                } else {

                }
            }

        } catch (SQLException e) {
            logger.warning("Error assigning task: " + e.getMessage());
           logger.warning("No chef found with expertise: " + requiredExpertise);
            return -1;
        }

        logger.info("Assigning task to chef completed.");
        return selectedChefId;
    }


}

