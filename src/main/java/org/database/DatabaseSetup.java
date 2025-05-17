package org.database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseSetup {
    public static void setupDatabase() {
        String customers = "CREATE TABLE IF NOT EXISTS customer_preferences (" +
                "customer_id INTEGER PRIMARY KEY, " +
                "dietary TEXT, " +
                "email TEXT, " +
                "allergies TEXT)";

        String custom = "CREATE TABLE IF NOT EXISTS custom_meals (" +
                "meal_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "customer_id INTEGER, " +
                "meal_name TEXT NOT NULL, " +
                "status TEXT CHECK (status IN ('draft', 'finalized')) DEFAULT 'draft', " +
                "FOREIGN KEY (customer_id) REFERENCES customers(customer_id))";



        String inventory = "CREATE TABLE IF NOT EXISTS inventory (" +
                "ingredient_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT UNIQUE NOT NULL, " +
                "status TEXT CHECK (status IN ('available', 'out of stock')) NOT NULL DEFAULT 'available', " +
                "dietary_category TEXT NOT NULL, " +
                "quantity INTEGER NOT NULL DEFAULT 0" +
                ")";

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




     String kitchenNotifications = "CREATE TABLE IF NOT EXISTS kitchen_notifications (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "message TEXT NOT NULL)";
        String customMealIngredients = "CREATE TABLE IF NOT EXISTS custom_meal_ingredients ("
                + "mealId INT NOT NULL, "
                + "ingredientName VARCHAR(255) NOT NULL, "
                + "PRIMARY KEY (mealId, ingredientName), "
                + "FOREIGN KEY (mealId) REFERENCES meals(mealId)"
                + ")";
            String suppliers ="CREATE TABLE IF NOT EXISTS suppliers (id INTEGER PRIMARY KEY AUTOINCREMENT,ingredient_name TEXT NOT NULL,supplier_name TEXT NOT NULL,price REAL,UNIQUE(ingredient_name, supplier_name));";
            String purchase_orders=  "CREATE TABLE IF NOT EXISTS purchase_orders (\n" +
                     "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                     "    ingredient_name TEXT,\n" +
                     "    quantity INTEGER,\n" +
                     "    supplier_id INTEGER,\n" +
                     "    status TEXT\n" +
                     ");\n";
            String menu = "CREATE TABLE  IF NOT EXISTS menu_items (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    name TEXT UNIQUE NOT NULL\n" +
                    ");\n";
            String meal_ingredients="CREATE TABLE  IF NOT EXISTS meal_ingredients (\n" +
                    "    id INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "    menu_item_id INTEGER,\n" +
                    "    ingredient TEXT NOT NULL,\n" +
                    "    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id)\n" +
                    ");\n";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {


            stmt.execute(customers);
            stmt.execute(custom);

           stmt.execute(inventory);
            stmt.execute(chef);
            stmt.execute(task);
            stmt.execute(orders);
            stmt.execute(notifications);
            stmt.execute(invoices);
            stmt.execute(kitchenNotifications);
           stmt.execute(incompatible_ingredients);
        stmt.executeUpdate(customMealIngredients);
         stmt.execute(suppliers);
         stmt.execute(purchase_orders);
   stmt.execute(menu);
   stmt.execute(meal_ingredients);

            stmt.execute("PRAGMA foreign_keys = ON;");
          //  System.out.println("Database created successfully");



        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
