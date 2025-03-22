package org.database;


import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void setupDatabase() {
        String customers = "CREATE TABLE IF NOT EXISTS customer_preferences (" +
                "customer_id INTEGER PRIMARY KEY, " +
                "dietary TEXT, " +
                "allergies TEXT)";
        String  custom  = "CREATE TABLE custom_meals ( meal_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER, meal_name TEXT NOT NULL, status TEXT CHECK (status IN ('draft', 'finalized')) DEFAULT 'draft', FOREIGN KEY (customer_id) REFERENCES customers(customer_id))";
String ingredients ="CREATE TABLE custom_meal_ingredients (meal_id INTEGER, ingredient_id INTEGER, quantity INTEGER DEFAULT 1, PRIMARY KEY (meal_id, ingredient_id), FOREIGN KEY (meal_id) REFERENCES custom_meals(meal_id), FOREIGN KEY (ingredient_id) REFERENCES inventory(ingredient_id))" ;
      String inventory ="CREATE TABLE inventory (ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL, status TEXT CHECK (status IN ('available', 'out of stock')) NOT NULL DEFAULT 'available', dietary_category TEXT NOT NULL);"  ;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(customers);
            stmt.execute(custom);
            stmt.execute(ingredients);
            stmt.execute(inventory);
            System.out.println("created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
