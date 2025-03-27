package org;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class OrderHistoryService {
    private static OrderHistoryService instance;
    private static final String URL = "jdbc:h2:./database.db";  // استخدام وضع التخزين الدائم


    private static final String USER = "sa";
    private static final String PASSWORD = "";

    // Create Orders table if not exists
    public OrderHistoryService() {
        createOrdersTable();
        //System.out.println("11");
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
    private void createOrdersTable() {

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS ORDERS (" +
                    "order_id INT PRIMARY KEY AUTO_INCREMENT, " +
                    "customer_id VARCHAR(255), " +
                    "meal_name VARCHAR(255))";
            stmt.executeUpdate(sql);
            System.out.println("O");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Store customer order
    public void storeOrder(String customerId, String mealName) {
        String sql = "INSERT INTO ORDERS (customer_id, meal_name) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customerId);
            stmt.setString(2, mealName);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Order stored with ID: " + generatedId);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    // Retrieve order history of a customer
    public List<String> getOrderHistory(String customerId) {
        List<String> orderList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("SELECT meal_name FROM ORDERS WHERE customer_id = ?")) {
            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orderList.add(rs.getString("meal_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderList;
    }

    // Reorder a meal
    public String reorderMeal(String customerId, String mealName) {
        if (getOrderHistory(customerId).contains(mealName)) {
            storeOrder(customerId, mealName);
            return "Meal reordered successfully: " + mealName;
        }
        return "Meal not found in order history.";
    }



    public Map<String, Integer> orderHistoryTrends() {
        Map<String, Integer> trends = new HashMap<>();
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT meal_name, COUNT(*) as count FROM ORDERS GROUP BY meal_name")) {
            while (rs.next()) {
                trends.put(rs.getString("meal_name"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trends;
    }
}
