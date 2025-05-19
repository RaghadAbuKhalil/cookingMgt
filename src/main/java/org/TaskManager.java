package org;

import java.sql.*;


import org.database.DatabaseConnection;


public class TaskManager {


    private static TaskManager instance;

    public TaskManager() {
         Chef chef = new Chef();

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
            int chefId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    chefId = rs.getInt("chef_id");
                }

            }

            if (chefId == -1) {
                System.out.println("No available chefs to assign the task.");
                return chefId;
            }

            String insertTaskQuery = "INSERT INTO tasks (order_id, chef_id, task_name, status) VALUES (?, ?, ?, 'Acknowledge')";
            try (PreparedStatement stmt = conn.prepareStatement(insertTaskQuery)) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, chefId);
                stmt.setString(3, mealName);
                stmt.executeUpdate();
                NotificationService.getInstance().sendNotification(chefId, "New Task Assigned: " + mealName);
            }
            return chefId;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
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
            e.printStackTrace();
        }
        return null;
    }


        public int assignTaskToChefByExpertise(String taskName,  String requiredExpertise) {
            String selectSql = "SELECT chef_id, jobload FROM CHEFS WHERE expertise = ? ORDER BY jobload ASC LIMIT 1";
            String insertTaskSql = "INSERT INTO TASKS (task_name, chef_id, status) VALUES (?, ?, 'Acknowledge')";
            String updateLoadSql = "UPDATE CHEFS SET jobload = jobload + 1 WHERE chef_id = ?";
            int selectedChefId = -1;
            try (Connection conn = DatabaseConnection.getConnection()) {


                try (PreparedStatement stmt = conn.prepareStatement(selectSql)) {
                    stmt.setString(1, requiredExpertise);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        selectedChefId = rs.getInt("chef_id");

                    } else {
                        System.out.println("No chef found with expertise: " + requiredExpertise);
                        return -1;
                    }
                }


                try (PreparedStatement stmt = conn.prepareStatement(insertTaskSql)) {
                    stmt.setString(1, taskName);
                    stmt.setInt(2, selectedChefId);
                    stmt.executeUpdate();
                }


                try (PreparedStatement stmt = conn.prepareStatement(updateLoadSql)) {
                    stmt.setInt(1, selectedChefId);
                    stmt.executeUpdate();
                }


                System.out.println("Task '" + taskName + "' assigned to chef with ID: " + selectedChefId);

            } catch (SQLException e) {
                e.printStackTrace();
            }


            return selectedChefId;
        }

}

