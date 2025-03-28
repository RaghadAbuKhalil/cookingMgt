package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import java.sql.*;

import static io.cucumber.core.gherkin.StepType.THEN;


public class CustomMealService {

    private static CustomMealService instance;

    public static CustomMealService getInstance() {
        if (instance == null) {  // Check if instance is already created
            synchronized (CustomMealService.class) {  // Thread safety
                if (instance == null) {
                    instance = new CustomMealService();
                }
            }
        }
        return instance;
    }

    private CustomMealService() {
        DatabaseSetup.setupDatabase();
    }

    public boolean addIngredient(int mealId, String ingr) {


        String check = "SELECT ingredient_id,dietary_category FROM inventory WHERE name = ? AND status = 'available'";
        String insert = "INSERT INTO custom_meal_ingredients (meal_id" +
                ", ingredient_id, quantity) VALUES (?, ?, 1)";
        String checkIncompatabile = "SELECT COUNT(*) FROM incompatible_ingredients WHERE (ingredient1 = ? OR ingredient2 = ?) " +
                "AND (ingredient1 IN (SELECT ingredient_id FROM custom_meal_ingredients WHERE meal_id = ?) " +
                "OR ingredient2 IN (SELECT ingredient_id FROM custom_meal_ingredients WHERE meal_id = ?))";


        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(check)) {
            conn.setAutoCommit(false);


            checkStmt.setString(1, ingr);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int ingredientId = rs.getInt("ingredient_id");
                System.out.println("Ingredient available: " + ingr);
                String category = rs.getString("dietary_category");
                if (!checkAllergiesAndDietary(mealId, ingr, category)) {
                    conn.commit();
                    return false;
                }


                try (PreparedStatement stmt = conn.prepareStatement(checkIncompatabile)) {
                    stmt.setInt(1, ingredientId);
                    stmt.setInt(2, ingredientId);
                    stmt.setInt(3, mealId);
                    stmt.setInt(4, mealId);
                    ResultSet rs1 = stmt.executeQuery();

                    if (rs1.next() && rs1.getInt(1) > 0) {
                        System.out.println("This ingredient is incompatible with another ingredient in the meal.");
                        conn.commit();
                        return false;
                    }
                }

                try (
                        PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                    insertStmt.setInt(1, mealId);
                    insertStmt.setInt(2, ingredientId);
                    insertStmt.executeUpdate();
                    System.out.println("Added: " + ingr);
                    conn.commit();
                    return true;
                }
            } else {
                System.out.println("Ingredient unavailable: " + ingr);
                conn.commit();
                return false;
            }

        } catch (Exception e) {
            System.out.println("Sorry! This ingredient is currently unavailable. Please choose another one" + ingr);
            e.printStackTrace();
        }

        return false;
    }

    private boolean checkAllergiesAndDietary(int mealId, String ingr, String category) {
        String checkAllergies = "SELECT dietary, allergies FROM customer_preferences WHERE customer_id = (SELECT customer_id FROM custom_meals WHERE meal_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(checkAllergies)) {
            conn.setAutoCommit(false);
            insertStmt.setInt(1, mealId);
            ResultSet allergyResult = insertStmt.executeQuery();
            if (allergyResult.next()) {
                String dietary = allergyResult.getString(1);
                String allergy = allergyResult.getString(2);
                if (allergy != null && allergy.equals(ingr)) {
                    System.out.println("Customer has an  allergy to " + ingr);
                    return false;
                }
                if (dietary.equals("vagen") && category.equals("Non-vegetarian")) {
                    System.out.println(ingr + " is not suitable a vegen diet.");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println(" Error!  Cannot find customer Allergies and Dietary preferences.");
            throw new RuntimeException(e);
        }
        return true;
    }

    public int createCustomMeal(int custID, String meal) {
        String insertQuery = "INSERT INTO custom_meals (customer_id, meal_name, status) VALUES (?, ?, 'draft')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {  // to return the primary key

            stmt.setInt(1, custID);
            stmt.setString(2, meal);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }


    public String suggestAlternetive( String ingredientName,int mealId) {
        // Step 1: Query to retrieve the customer's dietary preferences and allergies
        String customerQuery = """
                    SELECT dietary, allergies
                    FROM customer_preferences
                    WHERE customer_id = (
                        SELECT customer_id 
                        FROM custom_meals 
                        WHERE meal_id = ?
                    )
                """;

        // Step 2: Query to find an alternative ingredient
        String inventoryQuery = """
                    SELECT name
                    FROM inventory
                    WHERE status = 'available'
                      AND dietary_category = ?
                      AND name NOT IN (
                          SELECT name
                          FROM inventory
                          WHERE ? LIKE '%' || name || '%'
                      )
                    ORDER BY RANDOM()
                    LIMIT 1
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement customerStmt = conn.prepareStatement(customerQuery);
             PreparedStatement inventoryStmt = conn.prepareStatement(inventoryQuery)) {

            // Step 3: Retrieve customer preferences
            customerStmt.setInt(1, mealId);
            ResultSet customerRs = customerStmt.executeQuery();
            if (!customerRs.next()) {
                System.out.println("No customer preferences found for meal ID: " + mealId);
                return null;
            }

            String dietaryPreference = customerRs.getString("dietary");
            String allergies = customerRs.getString("allergies");

            // Map dietary preference to inventory category
            String dietaryCategory = switch (dietaryPreference.toLowerCase()) {
                case "vagen" -> "Vegetarian";
                case "non-vagen" -> "Non-Vegetarian";
                default -> throw new IllegalArgumentException("Unknown dietary preference: " + dietaryPreference);
            };

            // Step 4: Find an alternative ingredient
            inventoryStmt.setString(1, dietaryCategory);
            inventoryStmt.setString(2, allergies != null ? allergies : ""); // Handle null allergies
            ResultSet inventoryRs = inventoryStmt.executeQuery();

            if (inventoryRs.next()) {
                String suggested = inventoryRs.getString("name");
                System.out.println("Suggested Alternative: " + suggested);
                return suggested;
            } else {
                System.out.println("No suitable alternative found for " + ingredientName);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            throw new RuntimeException("Error in suggesting an alternative", e);
        }
    }
}

















         /*   String query = """
        SELECT name
        FROM inventory
        WHERE status = 'available' 
        AND dietary_category = (
            SELECT 
                CASE dietary
                    WHEN 'vegan' THEN 'Vegetarian'
                    WHEN 'Non-vegan' THEN 'Non-Vegetarian'
                END
            FROM customer_preferences
            WHERE customer_id = (
                SELECT customer_id 
                FROM custom_meals 
                WHERE meal_id = ?
            )
        )
        AND ingredient_id NOT IN (
            SELECT ingredient_id 
            FROM inventory
            WHERE name IN (
                SELECT allergies
                FROM customer_preferences
                WHERE customer_id = (
                    SELECT customer_id 
                    FROM custom_meals 
                    WHERE meal_id = ?
                )
            )
        )
        ORDER BY RANDOM()
        LIMIT 1;
    """;

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                // Set the mealId for dietary preferences and allergies
                stmt.setInt(1, mealId);
                stmt.setInt(2, mealId);

                // Execute the query
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Get the suggested ingredient
                    String suggested = rs.getString("name");
                    System.out.println("Suggested alternative for " + ing + ": " + suggested);
                    return suggested;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Error suggesting alternative ingredient.", e);
            }

            // If no alternative is found
            System.out.println("Unable to suggest an alternative for " + ing + " at this time.");
            return null;
        }*/


