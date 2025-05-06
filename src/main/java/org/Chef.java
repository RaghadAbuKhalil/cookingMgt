package org;

import org.database.DatabaseConnection;

import java.sql.*;

public class Chef {
    private static Chef instance;

    public Chef() {
       // createChefAndTaskTable();
    }
    public static Chef getInstance() {
        if (instance == null) {
            synchronized (Chef.class) {
                if (instance == null) {
                    instance = new Chef();
                }
            }
        }
        return instance;
    }


    public void addChef(String chefName, String Expertise) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO CHEFS (chef_name, expertise) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, chefName);
            stmt.setString(2, Expertise);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

         public void setChefJobload(int chefId, int jobload) {
             String sql = "UPDATE CHEFS SET jobload = ? WHERE chef_id = ?";

             try (Connection conn = DatabaseConnection.getConnection();
                  PreparedStatement stmt = conn.prepareStatement(sql)) {

                 stmt.setInt(1, jobload);
                 stmt.setInt(2, chefId);

                 int updated = stmt.executeUpdate();
                 if (updated > 0) {
                     System.out.println("Jobload updated successfully for chef ID: " + chefId);
                 } else {
                     System.out.println("Chef not found with ID: " + chefId);
                 }

             } catch (SQLException e) {
                 e.printStackTrace();
             }
         }


    public void beginTask(int chefid, String taskName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Chef.getInstance();

            String sql = "UPDATE tasks SET status = 'In Progress' WHERE task_name = ? AND chef_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, taskName);
            stmt.setString(2, String.valueOf(chefid));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public boolean completeTask(String chefName, String taskName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            Chef.getInstance();

            String sql = "UPDATE tasks SET status = 'Completed' WHERE task_name = ? AND chef_id = (SELECT chef_id FROM chefs WHERE chef_name = ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, taskName);
            stmt.setString(2, chefName);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void printAllChefs() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM CHEFS";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int chefId = rs.getInt("chef_id");
                String chefName = rs.getString("chef_name");
                String expertise = rs.getString("expertise");
                int jobload = rs.getInt("jobload");
                System.out.println("Chef ID: " + chefId + ", Name: " + chefName + ", Expertise: " + expertise + ", Jobload: " + jobload);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getChefNotifications(String chefName) {
        return NotificationService.getInstance().getChefNotifications(chefName);
    }

    public boolean chefExists(int chefId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT 1 FROM CHEFS WHERE chef_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, chefId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
