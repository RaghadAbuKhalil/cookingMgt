package org;

import org.database.DatabaseConnection;
import org.database.DatabaseSetup;

import java.sql.*;


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
   private  CustomMealService() {
       DatabaseSetup.setupDatabase();
    }

    public boolean addIngredient(int mealId, String ingr) {


        String check = "SELECT ingredient_id,dietary_category FROM inventory WHERE name = ? AND status = 'available'";
        String insert = "INSERT INTO custom_meal_ingredients (meal_id" +
                ", ingredient_id, quantity) VALUES (?, ?, 1)";
        String checkAllergies = "SELECT dietary, allergies FROM customer_preferences WHERE customer_id = (SELECT customer_id FROM custom_meals WHERE meal_id = ?)";
        String checkIncompatabile =  "SELECT COUNT(*) FROM incompatible_ingredients WHERE (ingredient1 = ? OR ingredient2 = ?) " +
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



               try (PreparedStatement insertStmt = conn.prepareStatement(checkAllergies)) {
                    insertStmt.setInt(1, mealId);
                   ResultSet allergyResult = insertStmt.executeQuery();
                   if (allergyResult.next()) {
                        String dietary = allergyResult.getString(1);
                       String allergy = allergyResult.getString(2);
                        if (allergy != null && allergy.equals(ingr)) {
                            System.out.println("customer has an  allergy from this ingrediant");
                            conn.commit();
                            return false;

                        }
                        if (dietary.equals("vagen")&& category.equals("Non-vegetarian"))
                            {
                            System.out.println("This ingredient is not suitable for vegetarians");
                                conn.commit();
                                return false;
                        }

                   } else {
                        System.out.println(" cannot find customer allergies and dietary preferances ");
                    }}
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
               }}
           else {
               System.out.println("Ingredient unavailable: " + ingr);
               conn.commit();
               return false;
           }

       }
         catch (Exception e) {
        System.out.println("Ingredient unavailable: " + ingr);
            e.printStackTrace();
        }

        return false;
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





    public String suggestAlternetive(String ing) {

         String query = "SELECT name FROM inventory WHERE dietary_category = (SELECT dietary_category FROM inventory WHERE name = ?) AND status = 'available' LIMIT 1";
       // String query = "select* FROM inventory";
      try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query )) {
           stmt.setString(1, ing);
            ResultSet rs = stmt.executeQuery();
            /*while (rs.next()){
                System.out.println( rs.getString("name"));
            }*/
        if (rs.next()){

            return rs.getString(1);
        }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
