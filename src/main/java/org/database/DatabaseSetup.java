package org.database;

import org.Ingredient;
import org.InventoryService;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSetup {
    public static void setupDatabase() {
        String customers = "CREATE TABLE IF NOT EXISTS customer_preferences (" +
                "customer_id INTEGER PRIMARY KEY, " +
                "dietary TEXT, " +
                "allergies TEXT)";

        String custom = "CREATE TABLE IF NOT EXISTS custom_meals (" +
                "meal_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "meal_name TEXT NOT NULL, " +
                "status TEXT CHECK (status IN ('draft', 'finalized')) DEFAULT 'draft', " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id))";

        String orders = "CREATE TABLE IF NOT EXISTS ORDERS (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "meal_name VARCHAR(255), " +
                "status VARCHAR(255),"+
                "price REAL NOT NULL,"+
                "order_date DATE DEFAULT CURRENT_DATE,"+
                "is_repeated BOOLEAN DEFAULT FALSE)";



        String inventory = "CREATE TABLE IF NOT EXISTS inventory (" +
                "ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE NOT NULL, " +
                "status TEXT CHECK (status IN ('available', 'out of stock')) NOT NULL DEFAULT 'available', " +
                "dietary_category TEXT NOT NULL)";

        String chef = "CREATE TABLE IF NOT EXISTS CHEFS (" +
                "chef_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "chef_name VARCHAR(255) NOT NULL, " +
                "expertise VARCHAR(255) NOT NULL, " +
                "jobload INT NOT NULL DEFAULT 0)";

        String task = "CREATE TABLE IF NOT EXISTS tasks (" +
                "task_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "task_name VARCHAR(255) NOT NULL, " +
                "chef_id INT, " +
                "order_id INT, " +
                "status VARCHAR(255) NOT NULL, " +
                "expertise_required VARCHAR(255), " +
                "FOREIGN KEY (chef_id) REFERENCES CHEFS(chef_id) ON DELETE SET NULL, " +
                "FOREIGN KEY (order_id) REFERENCES ORDERS(order_id) ON DELETE SET NULL)";

        String incompatible_ingredients   = "CREATE TABLE IF NOT EXISTS incompatible_ingredients ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "ingredient1 TEXT NOT NULL, "
                + "ingredient2 TEXT NOT NULL"
                + ")";


        String notifications = "CREATE TABLE IF NOT EXISTS notifications (" +
                "notification_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "chef_id INTEGER, " +
                "task_name TEXT, " +
                "status TEXT DEFAULT 'unacknowledged', " +
                "FOREIGN KEY (chef_id) REFERENCES CHEFS(chef_id) ON DELETE SET NULL)";
     String kitchenNotifications = "CREATE TABLE IF NOT EXISTS kitchen_notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "message TEXT NOT NULL)";
        String customMealIngredients = "CREATE TABLE IF NOT EXISTS custom_meal_ingredients ("
                + "mealId INT NOT NULL, "
                + "ingredientName VARCHAR(255) NOT NULL, "
                + "PRIMARY KEY (mealId, ingredientName), "
                + "FOREIGN KEY (mealId) REFERENCES meals(mealId)"
                + ")";
            String suppliers ="CREATE TABLE IF NOT EXISTS suppliers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "ingredient_name TEXT NOT NULL," +
                    "supplier_name TEXT NOT NULL," +
                    "price REAL NOT NULL)";
            String purchase_orders=  "CREATE TABLE IF NOT EXISTS purchase_orders (\n" +
                     "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                     "    ingredient_name TEXT,\n" +
                     "    quantity INTEGER,\n" +
                     "    supplier_id INTEGER,\n" +
                     "    status TEXT\n" +
                     ");\n";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // تنفيذ الجمل الخاصة بإنشاء الجداول
            stmt.execute(customers);
            stmt.execute(custom);
            stmt.execute(inventory);
            stmt.execute(chef); // يجب أن يتم إنشاء الجدول قبل "tasks"
            stmt.execute(task);
            stmt.execute(orders);
            stmt.execute(notifications);
            stmt.execute(kitchenNotifications);
           stmt.execute(incompatible_ingredients);
        stmt.executeUpdate(customMealIngredients);
         stmt.execute(suppliers);
         stmt.execute(purchase_orders);

            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Database created successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        try {
           // InventoryService.addOrUpdateIngredient(new Ingredient("Olive Oil", "available", "vegetarian", 15));
          /*  InventoryService.addOrUpdateIngredient(new Ingredient("chicken", "available", "Non-vegetarian", 15));
            InventoryService.addOrUpdateIngredient(new Ingredient("rice", "available", "vegetarian", 15));
            InventoryService.addOrUpdateIngredient(new Ingredient("fish", "available", "Non-vegetarian", 15));
            InventoryService.addOrUpdateIngredient(new Ingredient("Cheese", "available", "vegetarian", 15));
            InventoryService.addOrUpdateIngredient(new Ingredient("Strawberry", "available", "vegetarian", 15));
            InventoryService.addOrUpdateIngredient(new Ingredient("onion", "available", "vegetarian", 15));
            InventoryService.addOrUpdateIngredient(new Ingredient("salmon", "available", "Non-vegetarian", 15));
            InventoryService.addOrUpdateIngredient(new Ingredient("tomato", "out of stock", "vegetarian", 3));
            InventoryService.addOrUpdateIngredient(new Ingredient("banana", "out of stock", "vegetarian", 4));
            InventoryService.addOrUpdateIngredient(new Ingredient("carrot", "out of stock", "vegetarian", 1));
            InventoryService.addOrUpdateIngredient(new Ingredient("potato", "out of stock", "vegetarian", 2));



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("PRAGMA table_info(custom_meal_ingredients)");
        while (rs.next()) {
            System.out.println(rs.getString("name") + " - " + rs.getString("type"));
        }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/


       // String sql = "DROP TABLE IF EXISTS custom_meal_ingredients";



    }
}
