package org.database;


import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
   public static void setupDatabase() {
        String customers = "CREATE TABLE IF NOT EXISTS customer_preferences (" +
                "customer_id INTEGER PRIMARY KEY, " +
                "dietary TEXT, " +
                "allergies TEXT)";
        String  custom  = "CREATE TABLE IF NOT EXISTS  custom_meals ( meal_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER, meal_name TEXT NOT NULL, status TEXT CHECK (status IN ('draft', 'finalized')) DEFAULT 'draft', FOREIGN KEY (customer_id) REFERENCES customers(customer_id))";
String ingredients ="CREATE TABLE IF NOT EXISTS custom_meal_ingredients (meal_id INTEGER, ingredient_id INTEGER, quantity INTEGER DEFAULT 1, PRIMARY KEY (meal_id, ingredient_id), FOREIGN KEY (meal_id) REFERENCES custom_meals(meal_id), FOREIGN KEY (ingredient_id) REFERENCES inventory(ingredient_id))" ;
      String inventory ="CREATE TABLE IF NOT EXISTS  inventory (ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL, status TEXT CHECK (status IN ('available', 'out of stock')) NOT NULL DEFAULT 'available', dietary_category TEXT NOT NULL);"  ;

       String invItems="INSERT   OR IGNORE INTO inventory (name, status, dietary_category) VALUES"+
               " ('broccoli', 'available', 'Vegetarian'),"+
               " ('chicken', 'available', 'Non-vegetarian'),"+
               " ('rice', 'available', 'Vegetarian'),"+
               " ('fish', 'available', 'Non-Vegetarian'),"+
               " ('tomato', 'out of stock', 'Vegetarian'),"+
               " ('cheese', 'available', 'Vegetarian'),"+
               "('strawberry', 'available', 'Non-vegetarian');";
       String incompatableItems ="INSERT INTO incompatible_ingredients (ingredient1, ingredient2) VALUES \n" +
               "    ((SELECT ingredient_id FROM inventory WHERE name = 'fish'), \n" +
               "     (SELECT ingredient_id FROM inventory WHERE name = 'cheese'));\n" ;
       String sql = "CREATE TABLE IF NOT EXISTS custom_meals ( " +
               "meal_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
               "customer_id INTEGER, " +
               "meal_name TEXT NOT NULL, " +
               "status TEXT CHECK (status IN ('draft', 'finalized')) DEFAULT 'draft', " +
               "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) " +
               ");";
       String sql2 ="CREATE TABLE IF NOT EXISTS custom_meal_ingredients (meal_id INTEGER, ingredient_id INTEGER, quantity INTEGER DEFAULT 1, PRIMARY KEY (meal_id, ingredient_id), FOREIGN KEY (meal_id) REFERENCES custom_meals(meal_id), FOREIGN KEY (ingredient_id) REFERENCES inventory(ingredient_id))" ;
       String sql3 ="CREATE TABLE IF NOT EXISTS  inventory (ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE NOT NULL, status TEXT CHECK (status IN ('available', 'out of stock')) NOT NULL DEFAULT 'available', dietary_category TEXT NOT NULL);"  ;
       String sql4="CREATE TABLE incompatible_ingredients (\n" +
               "    ingredient1 INT,\n" +
               "    ingredient2 INT,\n" +
               "    PRIMARY KEY (ingredient1, ingredient2),\n" +
               "    FOREIGN KEY (ingredient1) REFERENCES inventory(ingredient_id),\n" +
               "    FOREIGN KEY (ingredient2) REFERENCES inventory(ingredient_id)\n" +
               ");" ;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            conn.setAutoCommit(false );
            stmt.execute(customers);
            stmt.execute(custom);
            stmt.execute(ingredients);
            stmt.execute(inventory);
            stmt.executeUpdate(sql);
            stmt.executeUpdate(sql2);
            stmt.executeUpdate(sql3);
            stmt.executeUpdate(sql4);
            stmt.execute(invItems);
            stmt.execute(incompatableItems);

            conn.commit();
            System.out.println("created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
