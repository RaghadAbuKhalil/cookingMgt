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


    public void assignTaskToChef(int orderId, String mealName) {
        try (Connection conn = DatabaseConnection.getConnection()) {


            String query = "SELECT chef_id FROM CHEFS ORDER BY jobload ASC LIMIT 1";
            int chefId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    chefId = rs.getInt("chef_id");
                }

            }

            if (chefId == -1) {
                System.out.println("No available chefs to assign the task.");
              return;
            }

            String insertTaskQuery = "INSERT INTO tasks (order_id, chef_id, task_name, status) VALUES (?, ?, ?, 'Acknowledge')";
            try (PreparedStatement stmt = conn.prepareStatement(insertTaskQuery)) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, chefId);
                stmt.setString(3, mealName);
                stmt.executeUpdate();
                NotificationService.getInstance().sendNotification(chefId, "New Task Assigned: " + mealName);
            }

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

    }

    public String TaskStatus(String taskName, int chefid) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status FROM tasks WHERE task_name = ? AND chef_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, taskName);
                stmt.setInt(2, chefid);

                System.out.println("Executing query with taskName: " + taskName + " and chefId: " + chefid);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {

                    String status = rs.getString("status");
                    System.out.println("Found status: " + status);
                    return status;
                } else {

                    System.out.println("No matching task found for taskName: " + taskName + " and chefId: " + chefid);
                }
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        return null;
    }


        public int assignTaskToChefByExpertise(String taskName,  String requiredExpertise) {
            String selectSql = "SELECT chef_id, jobload FROM CHEFS WHERE expertise = ? ORDER BY jobload ASC LIMIT 1";
            String insertTaskSql = "INSERT INTO TASKS (task_name, chef_id, status) VALUES (?, ?, 'Acknowledge')";
            String updateLoadSql = "UPDATE CHEFS SET jobload = jobload + 1 WHERE chef_id = ?";
            int selectedChefId = -1;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                    stmt.setString(1, requiredExpertise);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        selectedChefId = rs.getInt("chef_id");
                        PreparedStatement    stmt1 = conn.prepareStatement(insertTaskSql);
                            stmt1.setString(1, taskName);
                            stmt1.setInt(2, selectedChefId);
                            stmt1.executeUpdate();



                        try (PreparedStatement stmt2 = conn.prepareStatement(updateLoadSql)) {
                            stmt2.setInt(1, selectedChefId);
                            stmt2.executeUpdate();
                        }


                        System.out.println("Task '" + taskName + "' assigned to chef with ID: " + selectedChefId);

                    } else {
                        System.out.println("No chef found with expertise: " + requiredExpertise);
                        return -1;
                    }




            } catch (SQLException e) {
                logger.warning(e.getMessage());
            }


            return selectedChefId;
        }

}

