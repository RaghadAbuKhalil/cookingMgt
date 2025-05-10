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
public class KitchenManagerService {
    private static KitchenManagerService instance;

    TaskManager  taskAssignment;
    private NotificationService notificationService;

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
        String orderDate1 = fixDateFormatIfNeeded( orderDate);
        String insertOrderSQL = "INSERT INTO orders (customer_id, meal_name, price, status, order_date) VALUES (?, ?, ?, ?, ?)";
        int orderId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, mealName);
            stmt.setInt(3, price);
            stmt.setString(4, "Pending");
            stmt.setString(5, orderDate1);
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
    public String fixDateFormatIfNeeded(String inputDate) {
        DateTimeFormatter desiredFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter[] acceptedFormats = {
                DateTimeFormatter.ofPattern("dd-MM-yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("MM-dd-yyyy"),
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),
                DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.ENGLISH),
                DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)
        };


        try {
            LocalDate validDate = LocalDate.parse(inputDate, desiredFormat);
            return validDate.format(desiredFormat);
        } catch (DateTimeParseException ignored) {}


        for (DateTimeFormatter fmt : acceptedFormats) {
            try {
                LocalDate correctedDate = LocalDate.parse(inputDate, fmt);
                return correctedDate.format(desiredFormat);
            } catch (DateTimeParseException ignored) {}
        }

        return null;
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





}


