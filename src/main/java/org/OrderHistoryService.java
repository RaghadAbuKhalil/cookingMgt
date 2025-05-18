package org;

import org.database.DatabaseConnection;
import java.sql.*;
import java.util.*;

public class OrderHistoryService {
    private static OrderHistoryService instance;
private KitchenManagerService kitchenManagerService;
    private OrderHistoryService() {
        kitchenManagerService= new KitchenManagerService();
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



    public String reorderMeal(int customerId, String mealName,String Date) {
        if (getOrderHistory(customerId).contains(mealName)) {
            int newOrderId = kitchenManagerService.insertOrder(customerId, mealName,Date);
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
