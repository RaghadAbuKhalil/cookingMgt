package org;

import org.database.DatabaseConnection;

import java.sql.*;


public class CustomMealService {

    public CustomMealService() {
    }

    public void addIngredient(int mealId, String ingr) {

        String check = "SELECT ingredient_id,dietary_category FROM inventory WHERE name = ? AND status = 'available'";
        String insert = "INSERT INTO custom_meal_ingredients (meal_id" +
                ", ingredient_id, quantity) VALUES (?, ?, 1)";
        String checkAllergies = "SELECT dietary, allergies FROM customer_preferences WHERE customer_id = (SELECT customer_id FROM custom_meals WHERE meal_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(check)) {
            checkStmt.setString(1, ingr);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int ingredientId = rs.getInt("ingredient_id");
                System.out.println("Ingredient available: " + ingr);
                String category = rs.getString("dietary_category");


                try (PreparedStatement insertStmt = conn.prepareStatement(checkAllergies)) {
                    insertStmt.setInt(1, mealId);
                    if (rs.next()) {
                        String dietary = rs.getString("dietary");
                        String allergy = rs.getString("allergies");
                        if (allergy != null && allergy.equals(ingr)) {
                            System.out.println("customer has an  allergy from this ingrediant");

                        }
                        if (dietary.equals("vagen") && category.equals("Non-vegetarian")) {
                            System.out.println("This ingredient is not suitable for vegetarians");
                        }
                    } else {
                        System.out.println(" cannot find customer allergies and dietary preferances ");
                        return;
                    }

                }


                try (PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                    insertStmt.setInt(1, mealId);
                    insertStmt.setInt(2, ingredientId);
                    insertStmt.executeUpdate();
                    System.out.println("Added: " + ingr);
                }
            } else {
                System.out.println("Ingredient unavailable: " + ingr);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public boolean finalizeMeal(int mealId1) {
        String sqlStmt = "UPDATE custom_meals SET status = 'finalized' WHERE meal_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlStmt)) {
            stmt.setInt(1, mealId1);
            int updated = stmt.executeUpdate();
            if (updated > 0) return true;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean ingretientIsAvailable(String ing) {
        String available = "SELECT ingredient_id FROM inventory WHERE name = ? AND status = 'available'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(available)) {
            stmt.setString(1, ing);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return true;

            } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public String suggestAlternetive(String ing) {
        String query = "SELECT name FROM inventory WHERE dietary_category = (SELECT dietary_category FROM inventory WHERE name = ?) AND status = 'available' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query )) {
            stmt.setString(1, ing);
            ResultSet rs = stmt.executeQuery();
        if (rs.next()){
            return rs.getString(1);
        }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int getPreviousMeal(int customerId) {
        String query = "SELECT meal_id FROM custom_meals WHERE customer_id = ? AND status = 'draft' LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
return -1;
    }
    }
