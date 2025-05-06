package org;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.database.DatabaseConnection;

import javax.swing.*;

public class TaskManager {
    private Chef chef1;

    private static TaskManager instance;

    public TaskManager() {
        chef1 = new Chef();

    }

    public static TaskManager getInstance() {
        if (instance == null) {
            synchronized (TaskManager.class) {
                if (instance == null) {
                    instance = new TaskManager();
                }
            }
        }
        return instance;
    }

    public void giveChefTask(String taskName, int chefId, String expertiseRequired) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Unable to establish a database connection.");
                return;
            }

            if (!chef1.chefExists(chefId)) {
                System.out.println("Chef with ID " + chefId + " not found.");
                return;
            }

            if (taskExists(taskName, conn)) {
                System.out.println("Task '" + taskName + "' already exists in the database.");
                return;
            }

            String taskStatus = getTaskStatus(taskName, conn);
            if ("Completed".equals(taskStatus) || "Assigned".equals(taskStatus)) {
                updateTaskStatus(taskName, "Pending", conn);
            }

            assignTaskToChef(taskName, chefId, expertiseRequired, conn);
            NotificationService.getInstance().sendNotification(chefId, taskName);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean taskExists(String taskName, Connection conn) throws SQLException {
        String query = "SELECT COUNT(*) FROM tasks WHERE task_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, taskName);
            ResultSet rs = stmt.executeQuery();
            return rs.getInt(1) > 0;
        }
    }

    private String getTaskStatus(String taskName, Connection conn) throws SQLException {
        String query = "SELECT status FROM tasks WHERE task_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, taskName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() ? rs.getString("status") : "Not Found";
        }
    }

    private void updateTaskStatus(String taskName, String status, Connection conn) throws SQLException {
        String updateQuery = "UPDATE tasks SET status = ? WHERE task_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, status);
            stmt.setString(2, taskName);
            stmt.executeUpdate();
        }
    }

    private void assignTaskToChef(String taskName, int chefId, String expertiseRequired, Connection conn) throws SQLException {
        String query = "INSERT INTO tasks (task_name, chef_id, status, expertise_required) VALUES (?, ?, 'Assigned', ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, taskName);
            stmt.setInt(2, chefId);
            stmt.setString(3, expertiseRequired);
            stmt.executeUpdate();
        }
    }

    public String choosenChef(String taskName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT chef_name FROM CHEFS c JOIN tasks t ON t.expertise_required = c.expertise WHERE t.task_name = ? ORDER BY c.jobload ASC LIMIT 1";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, taskName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {

                    return rs.getString("chef_name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void assignTaskToChef(int orderId, String mealName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.out.println("Unable to establish a database connection.");
                return;
            }

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

            String insertTaskQuery = "INSERT INTO tasks (order_id, chef_id, task_name, status) VALUES (?, ?, ?, 'Assigned')";
            try (PreparedStatement stmt = conn.prepareStatement(insertTaskQuery)) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, chefId);
                stmt.setString(3, mealName);
                stmt.executeUpdate();
                NotificationService.getInstance().sendNotification(chefId, "New Task Assigned: " + mealName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String TaskStatus(String taskName, int chefid) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status FROM tasks WHERE task_name = ? AND chef_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                // تعيين المعاملات بشكل صحيح
                stmt.setString(1, taskName);  // إزالة الفراغات الزائدة إن وجدت
                stmt.setInt(2, chefid);  // استخدام setInt بدلاً من setString للـ chef_id

                System.out.println("Executing query with taskName: " + taskName + " and chefId: " + chefid);

                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {

                    String status = rs.getString("status");
                    System.out.println("Found status: " + status);
                    return status;
                } else {
                    // إذا لم نجد أي نتائج
                    System.out.println("No matching task found for taskName: " + taskName + " and chefId: " + chefid);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateTaskStatus(String taskName, String status) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "UPDATE tasks SET status = ? WHERE task_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, status);
                stmt.setString(2, taskName);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }}

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
            List<String> taskList = new ArrayList<>();
            String query = "SELECT task_name FROM task WHERE chef_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, selectedChefId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    String taskName1 = rs.getString("task_name");
                    taskList.add(taskName1);
                }
                JOptionPane.showMessageDialog(null,taskList);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return selectedChefId;
        }
    }

