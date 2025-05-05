package org;

import java.sql.*;

import org.database.DatabaseConnection;

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

    public String TaskStatus(String taskName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT status FROM tasks WHERE task_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, taskName);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("status");
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
        }
    }
}
