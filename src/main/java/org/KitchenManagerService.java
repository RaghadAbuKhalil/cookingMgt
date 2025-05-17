package org;

import org.database.DatabaseConnection;

import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import javax.swing.JOptionPane;
import org.database.DatabaseConnection;

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
    public static KitchenManagerService getInstance() {
        if (instance == null) {
            synchronized (KitchenManagerService.class) {
                if (instance == null) {
                    instance = new KitchenManagerService();
                }
            }
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
    String status;
       return status=  taskAssignment.TaskStatus(taskName,chefId);
}


    public int assignTask(String taskName,  String requiredExpertise) {
        int chefid = taskAssignment.assignTaskToChefByExpertise(taskName, requiredExpertise);
        if (chefid == -1) System.out.println("No chef found with expertise: " + requiredExpertise);
        notificationService.sendNotification(chefid,taskName);

        return chefid;
    }


    public int insertOrder(int customerId, String mealName, int price, String orderDate) {

        String insertOrderSQL = "INSERT INTO orders (customer_id, meal_name, price, status, order_date) VALUES (?, ?, ?, ?, ?)";
        int orderId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, mealName);
            stmt.setInt(3, price);
            stmt.setString(4, "Pending");
            stmt.setString(5, orderDate);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    orderId = rs.getInt(1);
                    //   System.out.println("Generated Order ID: " + generatedOrderId);
                }
            }

            taskAssignment.assignTaskToChef(orderId, mealName);

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
/*if (newStatus.equals("Completed")){
generateInvoice(orderId);
}*/
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




    public static void addMenuItem(String name, List<String> ingredients) throws SQLException {
        String selectSQL = "SELECT id FROM menu_items WHERE name = ?";
        String insertMenuItemSQL = "INSERT INTO menu_items(name) VALUES(?)";
        String updateMenuItemSQL = "UPDATE menu_items SET name = ? WHERE id = ?";
        String deleteOldIngredientsSQL = "DELETE FROM meal_ingredients WHERE menu_item_id = ?";
        String insertIngredientSQL = "INSERT INTO meal_ingredients(menu_item_id, ingredient) VALUES(?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int menuItemId = -1;

            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setString(1, name);
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        menuItemId = rs.getInt("id");


                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteOldIngredientsSQL)) {
                            deleteStmt.setInt(1, menuItemId);
                            deleteStmt.executeUpdate();
                        }
                    } else {

                        try (PreparedStatement insertStmt = conn.prepareStatement(insertMenuItemSQL, Statement.RETURN_GENERATED_KEYS)) {
                            insertStmt.setString(1, name);
                            insertStmt.executeUpdate();

                            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                                if (generatedKeys.next()) {
                                    menuItemId = generatedKeys.getInt(1);
                                } else {
                                    throw new SQLException("Failed to get inserted menu item ID.");
                                }
                            }
                        }
                    }
                }
            }

            try (PreparedStatement insertIngredientStmt = conn.prepareStatement(insertIngredientSQL)) {
                for (String ingredient : ingredients) {
                    insertIngredientStmt.setInt(1, menuItemId);
                    insertIngredientStmt.setString(2, ingredient.trim().toLowerCase());
                    insertIngredientStmt.executeUpdate();
                }
            }

            conn.commit();


        }
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




