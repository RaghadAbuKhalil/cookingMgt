package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import java.sql.*;



public class CustomMealService {

    private static CustomMealService instance;

    public static CustomMealService getInstance() {
        if (instance == null) {
            synchronized (CustomMealService.class) {
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


        String check = "SELECT dietary_category ,ingredient_id FROM inventory WHERE name = ? AND status = 'available'";
        String insert = "INSERT INTO custom_meal_ingredients (mealId" +
                ", ingredientName) VALUES (?, ?)";
        String checkIncompatible = "SELECT COUNT(*) FROM incompatible_ingredients "
                + "WHERE ((ingredient1 = ? OR ingredient2 = ?) "
                + "AND (ingredient1 IN (SELECT ingredientName FROM custom_meal_ingredients WHERE mealId = ?) "
                + "OR ingredient2 IN (SELECT ingredientName FROM custom_meal_ingredients WHERE mealId = ?)))";




        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(check)) {
            conn.setAutoCommit(false);


            checkStmt.setString(1, ingr.toLowerCase());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {

                 int  ingredientId = rs.getInt("ingredient_id");

                String category = rs.getString("dietary_category");
                if (!checkAllergiesAndDietary(mealId, ingr, category)) {
                    conn.commit();
                    return false;
                }

else {
                    System.out.println("Ingredient available: " + ingr);
                    try (PreparedStatement stmt = conn.prepareStatement(checkIncompatible)) {
                        stmt.setString(1, ingr);
                        stmt.setString(2, ingr);
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
                        insertStmt.setString(2, ingr);
                        insertStmt.executeUpdate();
                        System.out.println("Added: " + ingr);
                        conn.commit();
                        return true;
                    }
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
                if (allergy != null && allergy.toLowerCase().equals(ingr.toLowerCase())) {
                    System.out.println("Customer has an  allergy to " + ingr);
                    return false;
                }
                if (dietary.equals("vagen") && category.toLowerCase().equals("Non-vegetarian".toLowerCase())) {
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

            customerStmt.setInt(1, mealId);
            ResultSet customerRs = customerStmt.executeQuery();
            if (!customerRs.next()) {
                System.out.println("No customer preferences found for meal ID: " + mealId);
                return null;
            }

            String dietaryPreference = customerRs.getString("dietary");
            String allergies = customerRs.getString("allergies");

            String dietaryCategory = switch (dietaryPreference.toLowerCase()) {
                case "vagen" -> "vegetarian";
                case "non-vagen" -> "Non-vegetarian";
                default -> throw new IllegalArgumentException("Unknown dietary preference: " + dietaryPreference);
            };

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
















