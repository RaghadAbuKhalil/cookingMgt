package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import java.sql.*;

public class DietaryAndAllergies {
    public static MealAllergyChecker mealAllergyChecker1 = new MealAllergyChecker();
    private static OrderHistoryService instance;
    private static final String URL = "jdbc:sqlite:database.db";  // استخدام وضع التخزين الدائم


    private static final String USER = "sa";
    private static final String PASSWORD = "";
    public DietaryAndAllergies() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false);
            String sql1 = "CREATE TABLE IF NOT EXISTS customers ( " +
                    "customer_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customer_name TEXT NOT NULL " +
                    ");";

            String sql2 = "CREATE TABLE IF NOT EXISTS customer_preferences (" +
                    "customer_id INTEGER PRIMARY KEY, " +
                    "dietary TEXT, " +
                    "allergies TEXT, " +
                    "FOREIGN KEY (customer_id) REFERENCES customers(customer_id)" +
                    ");";
            stmt.executeUpdate(sql1);
            stmt.executeUpdate(sql2);
            System.out.println("O");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public static void setCustomerPreferences(int customerId, String dietary, String allergies) {
        String sql = "INSERT INTO customer_preferences (customer_id, dietary, allergies) VALUES (?, ?, ?) " +
                "ON CONFLICT(customer_id) DO UPDATE SET dietary = excluded.dietary, allergies = excluded.allergies";

        try (Connection conn = DatabaseConnection.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            stmt.setInt(1, customerId);
            stmt.setString(2, dietary);
            stmt.setString(3, allergies);
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
        // Insert some test data
        setCustomerPreferences(1, "Vegan", "Nuts");
        setCustomerPreferences(2, "Halal", "None");

        // Check allergy warnings
        checkAllergies(1, "Pasta");
    }
}
