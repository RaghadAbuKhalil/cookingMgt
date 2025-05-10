package org.database;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DatabaseSetup {
    public static void setupDatabase() {
        String customers = "CREATE TABLE IF NOT EXISTS customer_preferences (" +
                "customer_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "dietary TEXT, " +
                "email TEXT, " +
                "allergies TEXT)";

        String custom = "CREATE TABLE IF NOT EXISTS custom_meals (" +
                "meal_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "meal_name TEXT NOT NULL, " +
                "status TEXT CHECK (status IN ('draft', 'finalized')) DEFAULT 'draft', " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id))";



        String ingredients = "CREATE TABLE IF NOT EXISTS custom_meal_ingredients (" +
                "meal_id INTEGER, " +
                "ingredient_id INTEGER, " +
                "quantity INTEGER DEFAULT 1, " +
                "PRIMARY KEY (meal_id, ingredient_id), " +
                "FOREIGN KEY (meal_id) REFERENCES custom_meals(meal_id), " +
                "FOREIGN KEY (ingredient_id) REFERENCES inventory(ingredient_id))";

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



        String notifications = "CREATE TABLE IF NOT EXISTS notifications (" +
                "notification_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "chef_id INTEGER, " +
                "task_name TEXT, " +
                "status TEXT DEFAULT 'unacknowledged', " +
                "FOREIGN KEY (chef_id) REFERENCES CHEFS(chef_id) ON DELETE SET NULL)";
        String invoices = "CREATE TABLE IF NOT EXISTS INVOICES (" +
                "invoice_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "order_id INTEGER, " +
                "meal_name TEXT, " +
                "price REAL, " +
                "quantity INTEGER DEFAULT 1,"+
                "total_price REAL,"+
                "status TEXT, " +
                "order_date TEXT, " +
                "FOREIGN KEY(order_id) REFERENCES ORDERS(order_id))";

        String orders = "CREATE TABLE IF NOT EXISTS ORDERS (" +
                "order_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "meal_name VARCHAR(255), " +
                "status VARCHAR(255),"+
                "price REAL NOT NULL,"+
                "quantity INTEGER DEFAULT 1,"+
                "order_date TEXT,"+
                "is_repeated BOOLEAN DEFAULT FALSE)";




        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {


            stmt.execute(customers);
            stmt.execute(custom);
            stmt.execute(ingredients);
            stmt.execute(inventory);
            stmt.execute(chef);
            stmt.execute(task);
            stmt.execute(orders);
            stmt.execute(notifications);
            stmt.execute(invoices);

            stmt.execute("PRAGMA foreign_keys = ON;");
            System.out.println("Database created successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
