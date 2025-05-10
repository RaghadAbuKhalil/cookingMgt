package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import javax.swing.*;
import java.sql.*;
import java.util.Locale;

public class DietaryAndAllergies {
    public static MealAllergyChecker mealAllergyChecker1 = new MealAllergyChecker();
    private static OrderHistoryService instance;
    private static final String URL = "jdbc:sqlite:database.db";  // استخدام وضع التخزين الدائم

    public DietaryAndAllergies() {
     DatabaseSetup.setupDatabase();
    }



    public static void setCustomerPreferences(int customerId, String dietary, String allergies) {
        String sql = "INSERT INTO customer_preferences (customer_id, dietary, allergies) VALUES (?, ?, ?) " +
                "ON CONFLICT(customer_id) DO UPDATE SET dietary = excluded.dietary, allergies = excluded.allergies";

        try (Connection conn = DatabaseConnection.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            stmt.setInt(1, customerId);
            stmt.setString(2, dietary.toLowerCase());
            stmt.setString(3, allergies.toLowerCase());
            stmt.executeUpdate();
            System.out.println("Customer preferences saved successfully!");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Get allergies for a specific customer
    public static String getCustomerAllergies(int customerId) {
        String sql = "SELECT allergies FROM customer_preferences WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("allergies");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }



        return null;
    }
    public static String getCustomerPreferences(int customerId) {
        String sql = "SELECT dietary FROM customer_preferences WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("dietary");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if the meal contains allergens for the customer
    public static boolean checkAllergies(int customerId, String meal) {
        String allergies = getCustomerAllergies(customerId);

        if (meal == null || !mealAllergyChecker1.getMealIngredients().containsKey(meal)) {
            System.out.println("Error: Meal not found or null.");
            return false;
        }

        if (allergies == null || allergies.isEmpty()) {
            System.out.println("No allergies specified.");
            return false;
        }

        boolean containsAllergen = mealAllergyChecker1.getMealIngredients().get(meal).contains(allergies);
        if (containsAllergen) {
            System.out.println(" Warning: The meal contains an allergen: " + allergies);
        }
        return containsAllergen;
    }

    public static void main(String[] args) {

    }
}
