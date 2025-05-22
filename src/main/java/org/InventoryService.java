package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class InventoryService
{
    Connection conn;
    private final  static int min =5;
    private static final Logger logger = Logger.getLogger(InventoryService.class.getName());


    private static InventoryService instance;

    public static synchronized InventoryService getInstance() throws SQLException {
        if (instance == null) {
            instance = new InventoryService();
        }
        return instance;
    }
   private InventoryService() throws SQLException {
        try {
    conn = DatabaseConnection.getConnection();
            DatabaseSetup.setupDatabase();
        } catch (SQLException e) {
            throw new SQLException(e);
        }


    }

    public  void addOrUpdateIngredient(Ingredient ingredient) throws SQLException{
        String query = "INSERT INTO inventory (name, status, dietary_category, quantity) VALUES (?, ?, ?, ?) ON CONFLICT(name) DO UPDATE SET quantity = excluded.quantity, status = excluded.status, dietary_category = excluded.dietary_category";
        try (
            Connection    conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ingredient.getName());
            stmt.setString(2, ingredient.getStatus());
            stmt.setString(3, ingredient.getDietaryCategory());
            stmt.setInt(4, ingredient.getQuantity());
            stmt.executeUpdate();
        }
    }


    public Map<String, Integer> getAllIngredientQuantities() throws SQLException {
        Map<String, Integer> quantities = new HashMap<>();
        String query = "SELECT name, quantity FROM inventory";

        try ( Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                quantities.put(rs.getString("name"), rs.getInt("quantity"));
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        return quantities;
    }

    public List<String> checkForLowStock() throws SQLException {
        List<String> lowStock = new ArrayList<>();
        String query = "SELECT name FROM inventory WHERE quantity < ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)
            ) {
            stmt.setInt(1, min);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lowStock .add(rs.getString("name"));

            }
        }
        return lowStock ;
    }
    public void  updateStatusToOutOfStock(String name) throws SQLException {
        String sql = "UPDATE inventory SET status = 'out of stock' WHERE name = ? and quantity < ?";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setInt(2, min);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
        finally {
            logger.info("the status updated yo out of stock");
        }



}
    public List<String> getAlters() throws SQLException {
        List<String> alters = new ArrayList<>();
        for (String ingredient:checkForLowStock()){
        alters.add("Low stock : "+ingredient);
            saveNotificationToKitchenManager( "Low stock : "+ingredient);
        }
        return alters;
    }
    public List<String> suggestRestocking() throws SQLException {
        List<String> suggestRestock = new ArrayList<>();
        for (String ingredient:checkForLowStock()) {
            suggestRestock.add("Suggest to restock " + ingredient);
        }
        return suggestRestock ;
    }
    public void saveNotificationToKitchenManager(String message) throws SQLException {
        String query = "INSERT INTO kitchen_notifications (message) VALUES (?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, message);
            stmt.executeUpdate();
        }
    }

}
