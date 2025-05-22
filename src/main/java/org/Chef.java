package org;

import org.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;



public class Chef {
    private static Chef instance;
    private static final Logger logger = Logger.getLogger(Chef.class.getName());

    private Chef() {
    }
    public static synchronized Chef getInstance() {
        if (instance == null) {
            instance = new Chef();
        }
        return instance;
    }


    public int addChef(String chefName, String expertise) {
        int chefId = -1;
        String sql = "INSERT INTO CHEFS (chef_name, expertise) VALUES (?, ?)";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {
            stmt.setString(1, chefName);
            stmt.setString(2, expertise);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    chefId = generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            logger.warning("Error adding chef: " + e.getMessage());
        }

        logger.info("Adding chef...");
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
                    logger.info("Jobload updated successfully for chef ID: " + chefId);
                 } else {
                     logger.info("Chef not found with ID: " + chefId);
                 }

             } catch (SQLException e) {
                 logger.warning(e.getMessage());
             }
             finally {
                 logger.info("setting chef Jobload ");
             }
         }


    public void taskInProgress(int chefid, String taskName) {
        String sql = "UPDATE tasks SET status = 'In Progress' WHERE task_name = ? AND chef_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            Chef.getInstance();

            stmt.setString(1, taskName);
            stmt.setInt(2, chefid);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.warning("Error updating task status: " + e.getMessage());
        }

        logger.info("The status changed to in progress");
    }


    public void completeTask(int chefId, String taskName) {
        String sql = "UPDATE tasks SET status = 'Completed' WHERE task_name = ? AND chef_id = ?";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            Chef.getInstance();

            stmt.setString(1, taskName);
            stmt.setInt(2, chefId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.warning("Error completing task: " + e.getMessage());
        }

        logger.info("Task '" + taskName + "' marked as Completed.");
    }







    public List<String> printAllChefs() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT chef_id, chef_name, expertise, jobload FROM CHEFS";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int chefId = rs.getInt("chef_id");
                String chefName = rs.getString("chef_name");
                String expertise = rs.getString("expertise");
                int jobload = rs.getInt("jobload");

                list.add("Chef ID: " + chefId + ", Name: " + chefName +
                        ", Expertise: " + expertise + ", Jobload: " + jobload);
            }

        } catch (SQLException e) {
            logger.warning("Error fetching chefs: " + e.getMessage());
        }

        logger.info("Getting chefs list");
        return list;
    }


}
