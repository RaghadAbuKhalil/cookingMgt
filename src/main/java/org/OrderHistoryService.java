package org;

import org.database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class OrderHistoryService {
    private static OrderHistoryService instance;

    private OrderHistoryService() {
    }

    public static OrderHistoryService getInstance() {
        if (instance == null) {
            synchronized (OrderHistoryService.class) {
                if (instance == null) {
                    instance = new OrderHistoryService();
                }
            }
        }
        return instance;
    }

    public int storeOrder(int customerId, String mealName) {
        String sql = "INSERT INTO ORDERS (customer_id, meal_name) VALUES (?, ?)";
        int orderId=-1;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, mealName);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                       orderId = generatedKeys.getInt(1);
                        System.out.println("Order stored with ID: " + orderId);
                        TaskManager.getInstance().assignTaskToChef(orderId, mealName);
                    }
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderId;
    }

    public List<String> getOrderHistory(int customerId) {
        List<String> orderList = new ArrayList<>();
        String sql = "SELECT meal_name FROM ORDERS WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    orderList.add(rs.getString("meal_name"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    public int storeOrderAndGetId(int customerId, String mealName) {
        String sql = "INSERT INTO orders (customer_id, meal_name, status) VALUES (?, ?, 'Pending')";
        int orderId = -1;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, customerId);
            stmt.setString(2, mealName);
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return orderId;
    }

    public String reorderMeal(int customerId, String mealName) {
        if (getOrderHistory(customerId).contains(mealName)) {
            int newOrderId = storeOrderAndGetId(customerId, mealName);
            TaskManager.getInstance().assignTaskToChef(newOrderId, mealName);
            return "Meal reordered successfully " + mealName;
        }
        return "Meal not found in order history.";
    }

    public Map<String, Integer> orderHistoryTrends() {
        Map<String, Integer> trends = new HashMap<>();
        String sql = "SELECT meal_name, COUNT(*) as count FROM ORDERS GROUP BY meal_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                trends.put(rs.getString("meal_name"), rs.getInt("count"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trends;
    }
}
