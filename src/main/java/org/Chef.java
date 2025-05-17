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


    public int addChef(String chefName, String expertise) {
        int chefId = -1;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO CHEFS (chef_name, expertise) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, chefName);
            stmt.setString(2, expertise);

            stmt.executeUpdate();


            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                chefId = generatedKeys.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chefId;
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


    public void taskInProgress(int chefid, String taskName) {
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

    public void completeTask(int chefId, String taskName) {

        try (Connection conn = DatabaseConnection.getConnection()) {
            Chef.getInstance();

            String sql = "UPDATE tasks SET status = 'Completed' WHERE task_name = ? AND chef_id = ? ";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, taskName);
            stmt.setString(2, String.valueOf(chefId));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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


}
