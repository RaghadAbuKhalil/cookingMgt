package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Supplier {
   Connection conn;
    public Supplier()  {
        try {
            conn = DatabaseConnection.getConnection();
            DatabaseSetup.setupDatabase();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public double getRealTimePrice(String ingredient) throws SQLException {
        String sql = "SELECT price FROM suppliers WHERE ingredient_name = ? LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ingredient);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            } else {
                throw new RuntimeException("No price available for ingredient: " + ingredient);
            }
        }
    }

    public String getCheapestSupplier(String ingredient) throws SQLException {
        String sql = "SELECT supplier_name FROM suppliers WHERE ingredient_name = ? ORDER BY price ASC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ingredient);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("supplier_name");
            } else {
                return "No suppliers found for: " ;
            }
        }
    }
    public boolean isAvailableFromMultipleSuppliers(String ingredientName) throws SQLException {
        String query = "SELECT COUNT(DISTINCT supplier_name) FROM suppliers WHERE ingredient_name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ingredientName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 1;
            }
        }
        return false;
    }
    public boolean createPurchaseOrder(String ingredient, int quantity, int supplierId) {
        String sql = "INSERT INTO purchase_orders (ingredient_name, quantity, supplier_id, status) VALUES (?, ?, ?, 'pending')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ingredient);
            stmt.setInt(2, quantity);
            stmt.setInt(3, supplierId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create purchase order", e);
        }
    }
    }


