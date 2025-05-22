/*package org;


import org.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static Connection conn;
    static Scanner scanner = new Scanner(System.in);

    static {
        try {
            conn = DatabaseConnection.getConnection();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Main() {


    }

    public static int loginCustomer(String email) {
        String sql = "SELECT customer_id FROM customer_preferences WHERE email = ?";
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("customer_id");
                return id;
            } else {
                System.out.println("No customer account found with this email.");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Customer login error: " + e.getMessage());
            return -1;
        }
    }

    public static void showMenu() {
        String sql = "SELECT m.id, m.name AS meal_name, m.price, mi.ingredient " +
                "FROM menu_items m " +
                "LEFT JOIN meal_ingredients mi ON m.id = mi.menu_item_id " +
                "ORDER BY m.id";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            int currentMealId = -1;
            String currentMealName = "";
            double currentPrice = 0.0;
            StringBuilder ingredients = new StringBuilder();

            while (rs.next()) {
                int mealId = rs.getInt("id");
                String mealName = rs.getString("meal_name");
                double price = rs.getDouble("price");
                String ingredient = rs.getString("ingredient");
                if (mealId != currentMealId) {
                    if (currentMealId != -1) {
                        System.out.printf("\nID: %d  Meal: %s | Price: %.2f\nIngredients: %s\n",
                                currentMealId,
                                currentMealName,
                                currentPrice,
                                ingredients.length() > 0 ? ingredients.substring(0, ingredients.length() - 2) : "None");
                    }


                    currentMealId = mealId;
                    currentMealName = mealName;
                    currentPrice = price;
                    ingredients = new StringBuilder();
                }

                if (ingredient != null) {
                    ingredients.append(ingredient).append(", ");
                }
            }

            if (currentMealId != -1) {
                System.out.printf("\nID: %d  Meal: %s | Price: %.2f\nIngredients: %s\n",
                        currentMealId,
                        currentMealName,
                        currentPrice,
                        ingredients.length() > 0 ? ingredients.substring(0, ingredients.length() - 2) : "None");
            }


        } catch (SQLException e) {
            System.out.println("Error fetching menu items: " + e.getMessage());
        }
    }


    public static int loginChef(String name) {

        String sql = "SELECT chef_id FROM CHEFS WHERE chef_name = ?";
        try (
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("chef_id");
                System.out.println("Chef login successful. Chef ID: " + id);
                return id;
            } else {
                System.out.println("No chef account found with this name.");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Chef login error: " + e.getMessage());
            return -1;
        }
    }


    public static void main(String[] args) throws SQLException {

        KitchenManagerService kitchenManagerService = KitchenManagerService.getInstance();
        while (true) {
            System.out.println("\nWelcome to Special Cook Project Management System");
            System.out.println("Select user type:");
            System.out.println("1. Customer");
            System.out.println("2. Chef");
            System.out.println("3. Kitchen Manager");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            String userType = scanner.nextLine();

            if (userType.equals("4")) {
                System.out.println("Thank you for using our system❤️✨");
                break;
            }
            switch (userType) {
                case "1":
                    System.out.println("1. Sign up");
                    System.out.println("2. Login");
                    System.out.print("Choose action: ");
                    String action = scanner.nextLine();
                    if (action.equals("1")) {
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine().trim();
                        System.out.print("Choose your dietary preferences: " + "\n 1.vegan" + "\n 2.non-vegan \n");

                        String dietary;
                        while (true) {
                            String chosen = scanner.nextLine().trim();
                            if (chosen.equals("1")) {
                                dietary = "vegan";
                                break;
                            } else if (chosen.equals("2")) {
                                dietary = "non-vegan";
                                break;
                            } else System.out.print("Please enter valid input!");
                        }
                        System.out.print("Enter allergies (or none): ");
                        String allergies = scanner.nextLine().trim();

                        int cusid = DietaryAndAllergies.addNewCustomer(dietary, allergies, email);
                        LoginAsCustomer(cusid);
                    } else if (action.equals("2")) {

                        System.out.print("Enter email: ");
                        String email = scanner.nextLine().trim();
                        int customerId = loginCustomer(email);
                        if (customerId != -1) {
                            LoginAsCustomer(customerId);
                        }
                    } else {
                        System.out.println("Invalid action.");
                    }
                    break;
                case "2":
                    System.out.print("Enter chef name: ");
                    String name = scanner.nextLine().trim();
                    int chefId = loginChef(name);
                    if (chefId != -1) {
                        loginAsChef(chefId);

                    }
                    break;
                case "3":
                    System.out.print("Enter kitchen manager name: ");
                    String name1 = scanner.nextLine().trim();
                    if (name1.toLowerCase().equals("kitchen manager")) {
                        kitchenManagerMenu();
                    } else {
                        System.out.println("Invalid kitchen manager name!");
                    }
                    break;

                default:
                    System.out.println("Invalid user type.");
            }
        }
        scanner.close();
    }

    public static void LoginAsCustomer(int customerId) throws SQLException {
        CustomMealService customMealService = CustomMealService.getInstance();
        OrderHistoryService orderHistoryService = OrderHistoryService.getInstance();
        KitchenManagerService kitchenManagerService =  KitchenManagerService.getInstance();
        InvoicesAndFinancial invoicesAndFinancial = new InvoicesAndFinancial();
        System.out.println("Welcome back! We’re happy to serve you.❤️🍽️");
        while (true) {
            System.out.println("\nCustomer Menu:");
            System.out.println("1. Show my dietary preferences and allergies");
            System.out.println("2. Show my custom meals");
            System.out.println("3. Add a custom meal");
            System.out.println("4. Show my orders history");
            System.out.println("5. Reorder from past orders");
            System.out.println("6. Order from menu");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    System.out.println(" Your Dietary Preferences: " + DietaryAndAllergies.getCustomerPreferences(customerId));
                    System.out.println(" Your Allergy: " + DietaryAndAllergies.getCustomerAllergies(customerId));
                    break;
                case "2":
                  showCustomMeals(customerId);
                    break;
                case "3":
                    System.out.print("Enter name for the new custom meal: ");
                    String mealName = scanner.nextLine();
                    int mealId = customMealService.createCustomMeal(customerId, mealName);
                    System.out.println("Enter ingredients one by one (enter  'done' when finished):");
                    while (true) {
                        System.out.print("Ingredient: ");
                        String ingredient = scanner.nextLine().trim();
                        if (ingredient.equalsIgnoreCase("done") || ingredient.isEmpty()) {
                            break;
                        }
                        customMealService.addIngredient(mealId, ingredient);
                    }
                    break;
                case "4":
                    System.out.println("Your orders history:");
                    System.out.println(orderHistoryService.getOrderHistory(customerId));
                case "5":
                    System.out.print("Enter meal name to order: ");
                    String selectedMeal = scanner.nextLine();
                    LocalDate today = LocalDate.now();
                    String date = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    orderHistoryService.reorderMeal(customerId, selectedMeal, date);
                    break;
                case "6":
                    showMenu();

                    Scanner scanner = new Scanner(System.in);
                    System.out.print("\nEnter the ID of the meal you want to order: ");
                    mealId = scanner.nextInt();

                    System.out.print("Enter the quantity: ");
                    int quantity = scanner.nextInt();

                    double price = 0.0;

                    String selectMealSql = "SELECT name, price FROM menu_items WHERE id = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(selectMealSql)) {
                        stmt.setInt(1, mealId);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            mealName = rs.getString("name");
                            price = rs.getDouble("price");
                        } else {
                            System.out.println("Meal not found.");
                            return;
                        }
                    } catch (SQLException e) {
                        System.out.println("Error fetching meal info: " + e.getMessage());
                        return;
                    }
                    if (DietaryAndAllergies.checkAllergies(customerId, mealName)) {
                        System.out.printf("Customer has an allergy to an ingredient in this meal.%nWe suggest %s as an alternative.%n",
                                KitchenManagerService.findAlternative(customerId));
                    } else {
                        today = LocalDate.now();
                        date = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        int orderId = kitchenManagerService.insertOrder(customerId, mealName, date);

                        invoicesAndFinancial.generateInvoice(orderId);
                        System.out.println(invoicesAndFinancial.displayInvoice(orderId));
                    }
                    break;
                case "7":
                    System.out.println("Thank you for visiting! Have a great day!");
                    return;
            }
        }
    }

    public static void loginAsChef(int chefId) {
        NotificationService notificationService = NotificationService.getInstance();
        Chef chef = Chef.getInstance();
        System.out.println("Welcome, Chef! let’s make some magic happen!👨‍🍳🔥");

        while (true) {
            System.out.println("\n Chef List:");
            System.out.println("Please choose an action:");
            System.out.println("1. View my notifications");
            System.out.println("2. View my assigned tasks");
            System.out.println("3. Update task status");
            System.out.println("4. Logout");

            System.out.print("Your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:

                    System.out.println("Your Notifications List :" + notificationService.getNotificationsListForChef(chefId));

                    break;
                case 2:

                    System.out.println("Your Task List :");
                    showTasksForChef(chefId);
                    break;
                case 3:
                    System.out.println("Your Task List :");
                    showTasksForChef(chefId);
                    scanner = new Scanner(System.in);
                    System.out.print("Enter task name to update: ");
                    String taskName = scanner.nextLine();

                    System.out.println("Choose status:");
                    System.out.println("1. In Progress");
                    System.out.println("2. Completed");
                    String status = scanner.nextLine();

                    if (status.equals("1")) {
                        chef.taskInProgress(chefId, taskName);
                        System.out.println("Task marked as In Progress");
                    } else if (status.equals("2")) {

                            chef.completeTask(chefId, taskName);

                        System.out.println("Task marked as Completed");
                    } else {
                        System.out.println("Invalid choice");
                    }

                    break;
                case 4:
                    System.out.println("Thank you for your work!");
                    return;


            }

        }
    }

    public static void kitchenManagerMenu() throws SQLException {
        Chef chef = Chef.getInstance();
        KitchenManagerService manager = KitchenManagerService.getInstance();
        Supplier supplier = new Supplier();
        InventoryService inventory = InventoryService.getInstance();
        System.out.println("Welcome back, Chef Boss! 👨‍🍳 The kitchen awaits your command.");
        while (true) {
            System.out.println("\nKitchen Manager List:");
            System.out.println("1. View all chefs");
            System.out.println("2. View all  tasks ");
            System.out.println("3.Check task status for a chef");
            System.out.println("4. Add new chef");
            System.out.println("5. View low stock ingredients");
            System.out.println("6. Select best supplier for an ingredient");
            System.out.println("7. View ingredients stock");
            System.out.println("8. Add new meal to the menu");
            System.out.println("9. Logout");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    chef.printAllChefs();
                    break;
                case "2":
                    printAllTasks();
                    break;
                case "3":
                    System.out.print("Enter chef ID: ");
                    int chefId = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter task name: ");
                    String taskName = scanner.nextLine();

                    String status = manager.getTaskStatusForKitchenManager(taskName, chefId);
                    System.out.println("Task Status: " + status);
                    break;
                case "4":
                    System.out.print("Enter chef name: ");
                    String chefName = scanner.nextLine();
                    System.out.print("Enter chef's experiment: ");
                    String experiment = scanner.nextLine();
                    chef.addChef(chefName, experiment);
                    System.out.println("New chef on board! Welcome to the team. 👨‍🍳✨");

                    break;
                case "5":
                    System.out.println("-------Low stock ingredients-------");
                    List<String> list;
                    try {
                        list = inventory.checkForLowStock();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    for (String ingredient : list) {
                        System.out.print(ingredient + ", ");
                    }
                    break;
                case "6":
                    System.out.print("Enter ingredient name: ");
                    String ingredientName = scanner.nextLine();
                    try {
                        System.out.print("\nThe best supplier for " + ingredientName + "is :");
                        System.out.print(supplier.getCheapestSupplier(ingredientName) + "\n");
                        System.out.println("Do you want to order " + ingredientName + "?");
                        String answer = scanner.nextLine();
                        if (answer.equalsIgnoreCase("yes")) {
                            int supplierId = getSupplierIdByName(ingredientName, supplier.getCheapestSupplier(ingredientName));

                            System.out.println("Enter quantity :");
                            int quantity = Integer.valueOf(scanner.nextLine());
                            supplier.createPurchaseOrder(ingredientName, quantity, supplierId);
                            System.out.println("Your order created successfully");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    break;
                case "7":
                    System.out.println("------- ingredients Stock-------");
                    HashMap map = (HashMap) inventory.getAllIngredientQuantities();
                    map.forEach((key, value) -> System.out.println("Ingredient : " + key + ", Quantity: " + value));
                    break;
                case "8":
                    Scanner scanner = new Scanner(System.in);

                    System.out.println("\n--- Add New Meal to Menu ---");

                    System.out.print("Enter meal name: ");
                    String mealName = scanner.nextLine().trim();

                    System.out.print("Enter meal price: ");
                    double price = Double.parseDouble(scanner.nextLine().trim());

                    List<String> ingredients = new ArrayList<>();
                    System.out.println("Enter ingredients one by one (type 'done' to finish):");
                    while (true) {
                        String ingredient = scanner.nextLine().trim();
                        if (ingredient.equalsIgnoreCase("done")) {
                            break;
                        }
                        if (!ingredient.isEmpty()) {
                            ingredients.add(ingredient);
                        }
                    }

                    if (ingredients.isEmpty()) {
                        System.out.println("No ingredients entered. Meal was not added.");
                    } else {
                        addMealWithIngredients(mealName, price, ingredients);
                    }

                    break;
                case "9":
                    System.out.println("Great leadership today.See you soon!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    public static void showTasksForChef(int chefId) {
        String query = "SELECT task_id, task_name, status FROM tasks WHERE chef_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, chefId);

            try (ResultSet rs = stmt.executeQuery()) {


                boolean hasTasks = false;
                while (rs.next()) {
                    hasTasks = true;
                    int taskId = rs.getInt("task_id");
                    String taskName = rs.getString("task_name");
                    String status = rs.getString("status");

                    System.out.printf("Task ID: %d | Name: %s | Status: %s%n", taskId, taskName, status);
                }

                if (!hasTasks) {
                    System.out.println("You have no assigned tasks currently.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static int getSupplierIdByName(String ingredientName, String supplierName) {
        String sql = "SELECT id FROM suppliers WHERE ingredient_name = ? AND supplier_name = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, ingredientName);
            pstmt.setString(2, supplierName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                System.out.println("Supplier not found for given ingredient and name.");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("Error fetching supplier ID: " + e.getMessage());
            return -1;
        }
    }
    public static void addMealWithIngredients(String mealName, double price, List<String> ingredients) {
        String insertMealSQL = "INSERT INTO menu_items (name, price) VALUES (?, ?)";
        String insertIngredientSQL = "INSERT INTO meal_ingredients (menu_item_id, ingredient) VALUES (?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int mealId;

            try (PreparedStatement mealStmt = conn.prepareStatement(insertMealSQL, Statement.RETURN_GENERATED_KEYS)) {
                mealStmt.setString(1, mealName);
                mealStmt.setDouble(2, price);
                mealStmt.executeUpdate();

                try (ResultSet generatedKeys = mealStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mealId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve meal ID.");
                    }
                }
            }


            try (PreparedStatement ingredientStmt = conn.prepareStatement(insertIngredientSQL)) {
                for (String ingredient : ingredients) {
                    ingredientStmt.setInt(1, mealId);
                    ingredientStmt.setString(2, ingredient);
                    ingredientStmt.addBatch();
                }
                ingredientStmt.executeBatch();
            }

            conn.commit();
            System.out.println("Meal and ingredients added successfully!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void printAllTasks(){
          String sql = "SELECT  task_name, status, expertise_required FROM tasks";

          try (Connection conn = DatabaseConnection.getConnection();
               PreparedStatement pstmt = conn.prepareStatement(sql);
               ResultSet rs = pstmt.executeQuery()) {

              System.out.println("---- Tasks Summary ----");
              while (rs.next()) {

                  String name = rs.getString("task_name");
                  String status = rs.getString("status");
                  String expertise = rs.getString("expertise_required");

                  System.out.println( "Name: " + name +
                          " | Status: " + status +
                          " | Expertise: " + (expertise != null ? expertise : "None"));
              }

          } catch (SQLException e) {
              System.out.println( e.getMessage());
          }
      }
          public static void showCustomMeals(int customerId) {
        String sql = "SELECT meal_name FROM custom_meals WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            System.out.println("\n--- Custom Meals for Customer ID: " + customerId + " ---");
            boolean found = false;
            while (rs.next()) {
                found = true;
                String mealName = rs.getString("meal_name");
                System.out.println("- " + mealName);
            }

            if (!found) {
                System.out.println("No custom meals found for this customer.");
            }

        } catch (SQLException e) {
            System.out.println("Error while retrieving custom meals: " + e.getMessage());
        }
    }

}*/
