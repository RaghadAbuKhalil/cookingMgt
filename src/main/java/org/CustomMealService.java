package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import java.sql.*;
import java.util.logging.Logger;


public class CustomMealService {
    private static final Logger logger = Logger.getLogger(CustomMealService.class.getName());

    private static CustomMealService instance;

    public static synchronized CustomMealService getInstance() {
        if (instance == null) {
            instance = new CustomMealService();
        }
        return instance;
    }

    private CustomMealService() {
        DatabaseSetup.setupDatabase();

    }

    public boolean addIngredient(int mealId, String ingr) {


        String check = "SELECT dietary_category FROM inventory WHERE name = ? AND status = 'available'";
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
                String category = rs.getString("dietary_category");
                if (!checkAllergiesAndDietary(mealId, ingr, category)) {
                    conn.commit();
                    return false;
                } else {
                   logger.info("Ingredient available: " + ingr);
                    try (PreparedStatement stmt = conn.prepareStatement(checkIncompatible)) {
                        stmt.setString(1, ingr);
                        stmt.setString(2, ingr);
                        stmt.setInt(3, mealId);
                        stmt.setInt(4, mealId);
                        ResultSet rs1 = stmt.executeQuery();

                        if (rs1.next() && rs1.getInt(1) > 0) {
                           logger.info("This ingredient is incompatible with another ingredient in the meal.");
                            conn.commit();
                            return false;
                        }
                    }

                    try (
                            PreparedStatement insertStmt = conn.prepareStatement(insert)) {
                        insertStmt.setInt(1, mealId);
                        insertStmt.setString(2, ingr);
                        insertStmt.executeUpdate();
                        logger.info("Added: " + ingr);
                        conn.commit();
                        return true;
                    }
                }
            } else {
                logger.info("Ingredient unavailable: " + ingr);
                suggestAlternetive(ingr, mealId);
                conn.commit();
                return false;
            }

        } catch (Exception e) {
             logger.warning(e.getMessage());
        }

        return false;
    }

    private boolean checkAllergiesAndDietary(int mealId, String ingr, String category) throws SQLException {
        String checkAllergies = "SELECT dietary, allergies FROM customer_preferences WHERE customer_id = (SELECT customer_id FROM custom_meals WHERE meal_id = ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(checkAllergies)) {
            conn.setAutoCommit(false);
            insertStmt.setInt(1, mealId);
            ResultSet allergyResult = insertStmt.executeQuery();
            if (allergyResult.next()) {
                String dietary = allergyResult.getString(1);
                String allergy = allergyResult.getString(2);
                if (allergy != null && !allergy.equalsIgnoreCase("none") && allergy.equalsIgnoreCase(ingr)) {
                  logger.info("Customer has an  allergy to " + ingr);
                    suggestAlternetive(ingr, mealId);
                    return false;
                }
                if (dietary.equalsIgnoreCase("vegan") && category.equalsIgnoreCase("Non-vegetarian")) {
                    logger.info(ingr + " is not suitable a vegan diet.");
                    suggestAlternetive(ingr, mealId);
                    return false;
                }

            }
        } catch (SQLException e) {
          logger.info(" Error!  Cannot find customer Allergies and Dietary preferences.");
            throw new SQLException(e);
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
            logger.warning(e.getMessage());
        }
        finally {
            logger.info("creating custom meal for customer ");
        }

        return -1;

    }


    public String suggestAlternetive(String ingredientName, int mealId) throws SQLException {
        String customerQuery = """
                    SELECT dietary, allergies
                    FROM customer_preferences
                    WHERE customer_id = (SELECT customer_id  FROM custom_meals WHERE meal_id = ?)""";

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
               logger.info("No customer preferences found for meal ID: " + mealId);
                return null;
            }

            String dietaryPreference = customerRs.getString("dietary");
            String allergies = customerRs.getString("allergies");

            String dietaryCategory = switch (dietaryPreference.toLowerCase()) {
                case "vegan" -> "vegetarian";
                case "non-vegan" -> "Non-vegetarian";
                default -> throw new IllegalArgumentException("Unknown dietary preference: " + dietaryPreference);
            };

            inventoryStmt.setString(1, dietaryCategory);
            inventoryStmt.setString(2, allergies != null ? allergies : ""); // Handle null allergies
            ResultSet inventoryRs = inventoryStmt.executeQuery();

            if (inventoryRs.next()) {
                String suggested = inventoryRs.getString("name");
                logger.info("Suggested Alternative: " + suggested);
                return suggested;
            } else {
               logger.info("No suitable alternative found for " + ingredientName);
                return null;
            }
        } catch (SQLException e) {
            logger.warning("SQL Error: " + e.getMessage());
            throw new SQLException("Error in suggesting an alternative", e);
        }
        finally {
            logger.info("suggesting an alternative");
        }
    }
}















