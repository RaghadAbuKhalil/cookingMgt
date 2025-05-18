package org;


import org.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Scanner;

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
                System.out.println("Customer login successful. Customer ID: " + id);
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


    public static void main(String[] args) {

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
                System.out.println("Goodbye!");
                break;
            }
            switch (userType) {
                case "1":
                    System.out.println("1. Register");
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

                        DietaryAndAllergies.addNewCustomer(dietary, allergies, email);
                    } else if (action.equals("2")) {

                        System.out.print("Enter email: ");
                        String email = scanner.nextLine().trim();
                        int customerId = loginCustomer(email);
                        if (customerId != -1) {
                            System.out.println("Welcome, Customer!");
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
                        System.out.println("Welcome, Chef!");
                        // ********************
                    }
                    break;
                case "3":
                    System.out.print("Enter kitchen manager name: ");
                    String name1 = scanner.nextLine().trim();
                    if (name1.toLowerCase().equals("kitchen manager")) {
                        System.out.println("Welcome, Kitchen Manager!");
                        //*************************
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

    public static void LoginAsCustomer(int customerId) {
        CustomMealService customMealService =  CustomMealService.getInstance();
        OrderHistoryService orderHistoryService = OrderHistoryService.getInstance();
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
                    CustomMealService.showCustomMeals(customerId);
                    break;
                case "3":
                    System.out.print("Enter name for the new custom meal: ");
                    String mealName = scanner.nextLine();
                     int mealId= customMealService.createCustomMeal(customerId, mealName);
                    System.out.println("Enter ingredients one by one (enter  'done' when finished):");
                    while (true) {
                        System.out.print("Ingredient: ");
                        String ingredient = scanner.nextLine().trim();
                        if (ingredient.equalsIgnoreCase("done") || ingredient.isEmpty()) {
                            break;
                        }
                        customMealService.addIngredient(  mealId,ingredient);
                    }
                    break;
                case "4":
                    System.out.println("Your orders history:");
                    System.out.println( orderHistoryService.getOrderHistory(customerId));
                case "5":
                    System.out.print("Enter meal name to order: ");
                    String selectedMeal = scanner.nextLine();
                    LocalDate today = LocalDate.now();
                    String date = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    orderHistoryService.reorderMeal( customerId,  selectedMeal,date);



                    break;
            }
        }
    }
}























