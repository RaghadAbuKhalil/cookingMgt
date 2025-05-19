package org;

import org.database.DatabaseConnection;

import java.sql.*;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KitchenManagerService {
    private static KitchenManagerService instance;

    TaskManager  taskAssignment;
    private NotificationService notificationService;
  private Connection conn;
    public static synchronized KitchenManagerService getInstance() {
        if (instance == null) {
            instance = new KitchenManagerService();
        }
        return instance;
    }



    public KitchenManagerService() {
        taskAssignment =  new TaskManager();
        notificationService=new NotificationService();
        try {
            conn =   DatabaseConnection.getConnection() ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


public String getTaskStatusForKitchenManager(String taskName,int chefId){
        return taskAssignment.TaskStatus(taskName,chefId);
}


    public int assignTask(String taskName,  String requiredExpertise) {
        int chefid = taskAssignment.assignTaskToChefByExpertise(taskName, requiredExpertise);
        if (chefid == -1) System.out.println("No chef found with expertise: " + requiredExpertise);
        notificationService.sendNotification(chefid,taskName);

        return chefid;
    }


    public int insertOrder(int customerId, String mealName, String orderDate) {
        String fetchPriceSQL = "SELECT price FROM menu_items WHERE name = ?";
        String insertOrderSQL = "INSERT INTO orders (customer_id, meal_name, price, status, order_date) VALUES (?, ?, ?, ?, ?)";
        int orderId = -1;

        try (Connection conn = DatabaseConnection.getConnection()) {

            double price = 0.0;
            try (PreparedStatement fetchStmt = conn.prepareStatement(fetchPriceSQL)) {
                fetchStmt.setString(1, mealName);
                ResultSet rs = fetchStmt.executeQuery();

                if (rs.next()) {
                    price = rs.getDouble("price");
                } else {
                    System.out.println("Meal not found in menu_items.");
                    return -1;
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setInt(1, customerId);
                insertStmt.setString(2, mealName);
                insertStmt.setDouble(3, price);
                insertStmt.setString(4, "Pending");
                insertStmt.setString(5, orderDate);
                insertStmt.executeUpdate();

                try (ResultSet rs = insertStmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    }
                }

                taskAssignment.assignTaskToChef(orderId, mealName);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }



    public void updateOrderStatus(int orderId, String newStatus) {
        String updateStatusSQL = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateStatusSQL)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
            System.out.println("Order ID: " + orderId + " status updated to " + newStatus);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public String getOrderStatus(int orderId) {
        String query = "SELECT status FROM orders WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                System.out.println("Order ID: " + orderId + " Status: " + status);
                return status;
            } else {
                System.out.println("No order found with ID: " + orderId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        String query = "SELECT message FROM Kitchen_notifications ";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                notifications.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return notifications;
    }



    public  static String findAlternative(int customerId) {
        try (Connection conn = DatabaseConnection.getConnection()) {

            String allergyQuery = "SELECT allergies  FROM customer_preferences WHERE customer_id  = ?";
            String customerAllergy = null;

            try (PreparedStatement stmt = conn.prepareStatement(allergyQuery)) {
                stmt.setInt(1, customerId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        customerAllergy = rs.getString("allergies").toLowerCase();
                    }
                }
            }

            if (customerAllergy == null || customerAllergy.isEmpty()) {
                return null;
            }


            String query = """
            SELECT m.name
            FROM menu_items m
            WHERE NOT EXISTS (
                SELECT 1 FROM meal_ingredients i
                WHERE i.menu_item_id = m.id AND LOWER(i.ingredient) LIKE ?
            )
            LIMIT 1;
            """;

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, "%" + customerAllergy + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("name");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }



}




