package cookingTest;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.CustomMealService;

import org.NotificationService;
import org.database.DatabaseConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.DietaryAndAllergies.addNewCustomer;
import static org.junit.Assert.*;

public class CustomMealRequest {
    private static CustomMealService customMealService;
    private int mealId ;
    private List<String> ingr = new ArrayList<>();
    private List<String> notAvailableIngr = new ArrayList<>();
    boolean checkAddingIng=true;
    private  int testCustomerId =1 ;
    private   String suggestedAlternative;

    @BeforeAll
    public static void setup() {
        customMealService = CustomMealService.getInstance();
    }
    @Given("a customer wants to create a custom meal")
    public void aCustomerWantsToCreateACustomMeal() {


        mealId = customMealService.createCustomMeal(testCustomerId,"new meal");

        String checkQuery = "SELECT EXISTS (SELECT 1 FROM custom_meals WHERE meal_id = ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {

            stmt.setInt(1, mealId);
            ResultSet rs = stmt.executeQuery();
assertTrue(" meal does not  created", rs.next()&& mealId>0);
        }

        catch (SQLException e) {
            e.printStackTrace();
            fail("Database error occurred.");
        }
            System.out.println("customer is going to  customize a meal");



    }

    @When("they select available ingredients like grilled chicken, brown rice, and broccoli")
    public void theySelectAvailableIngredientsLikeGrilledChickenBrownRiceAndBroccoli()  {


        ingr.clear();
        ingr.add("broccoli");
        ingr.add("rice");

        System.out.println("Customer chose ingredients: " + ingr);

    }

        @Then("the system should validate the selection and allow the order to proceed")
    public void theSystemShouldValidateTheSelectionAndAllowTheOrderToProceed() {

        for (String mealIng : ingr) {
            boolean addIng = customMealService.addIngredient(mealId, mealIng);
            if (!addIng) {
                checkAddingIng = false;
            }

        }
String sql = "SELECT inventory.name " +
                "FROM custom_meal_ingredients " +
                "JOIN inventory ON custom_meal_ingredients.ingredientName = inventory.name " +
                "WHERE custom_meal_ingredients.mealId = ?";

        try (Connection conn = DatabaseConnection.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql);) {
            conn.setAutoCommit(false);
            stmt.setInt(1, mealId);
            ResultSet rs = stmt.executeQuery();

            List<String> storedIngredients = new ArrayList<>();
            while (rs.next()) {
                storedIngredients.add(rs.getString("name"));
            }

            assertTrue(storedIngredients.containsAll(ingr) &&
                    ingr.containsAll(storedIngredients));

            System.out.println("Custom meal and ingredients stored successfully!");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            fail("Database error occurred.");
        }
    }



    @And("display a confirmation message")
    public void displayAConfirmationMessage() {
        assertTrue("not all ingredients is suitable to add to customer's meal",checkAddingIng);
        System.out.println("Your custom meal request has been successfully placed!");
    }


    @Given("a customer wants to customize their meal")
    public void aCustomerWantsToCustomizeTheirMeal() {
        mealId = customMealService.createCustomMeal(testCustomerId,"custom meal 2");
        String checkQuery = "SELECT EXISTS (SELECT 1 FROM custom_meals WHERE meal_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {


            stmt.setInt(1, mealId);
            ResultSet rs = stmt.executeQuery();


            assertTrue(" meal does not  created", rs.next()&& mealId>0);
        }

        catch (SQLException e) {
            e.printStackTrace();
            fail("Database error occurred.");
        }
        System.out.println("customer is going to  customize a meal ");


    }
    @When("they select incompatible ingredients")
    public void theySelectIncompatibleIngredients() throws SQLException {
       if (mealId<=0)  mealId = customMealService.createCustomMeal(2,"custom meal 2");

        String checkQuery = "SELECT COUNT(*) FROM incompatible_ingredients "
                + "WHERE ((ingredient1 = ? OR ingredient2 = ?) "
                + "AND (ingredient1 IN (SELECT ingredientName FROM custom_meal_ingredients WHERE mealId = ?) "
                + "OR ingredient2 IN (SELECT ingredientName FROM custom_meal_ingredients WHERE mealId = ?)))";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            ResultSet rs = stmt.executeQuery();
            assertTrue(" meal does not  created", rs.next()&& mealId>0);}
        catch (SQLException e) {
            e.printStackTrace();
            fail("Database error occurred.");
        }

        boolean add1 = customMealService.addIngredient(mealId, "rice");
        assertTrue("First ingredient should be added", add1);

        boolean add2 = customMealService.addIngredient(mealId, "cheese");
        assertFalse("Second ingredient should not be added", add2);
      }

    @When("they select an ingredient that is an allergen for them")
    public void theySelectAnIngredientThatIsAnAllergenForThem() {
        ingr.clear();
        ingr.add("strawberry");

        for (String addIng :ingr){
            boolean check = customMealService.addIngredient(2,addIng);

            if (!check) checkAddingIng=check;
    }}

    @When("they select an ingredient that does not match their dietary preferences")
    public void theySelectAnIngredientThatDoesNotMatchTheirDietaryPreferences() {
            ingr.clear();
            ingr.add("chicken");
            for (String addIng :ingr){
                boolean check = customMealService.addIngredient(2,addIng);

                if (!check) checkAddingIng=check;

    }}

    @Then("the system should display an error message")
    public void theSystemShouldDisplayAnErrorMessage() {
if (!checkAddingIng)    System.out.println("An attempt to add uncompilable  or unsuitable ingredients")   ;

    }


    @Given("a customer selects an ingredient for their custom meal")
    public void aCustomerSelectsAnIngredientForTheirCustomMeal() {
        mealId = customMealService.createCustomMeal(3, " meal3 ");

        ingr.clear();
        ingr.add("tomato");
    }

    @When("the system detects that the ingredient is out of stock")
    public void theSystemDetectsThatTheIngredientIsOutOfStock() {
        notAvailableIngr.clear();
        for (String ing1:ingr ) {
            boolean check = customMealService.addIngredient(mealId, ing1);
            if (!check) {
                notAvailableIngr.add(ing1);
            }
        }}

    @Then("it should notify the customer")
    public void itShouldNotifyTheCustomer() {
        if (!notAvailableIngr.isEmpty()){  System.out.println(" Sorry ! some added ingredient are out of stock ")   ;}

    }


    @Given("a customer created a custom meal")
    public void aCustomerWithIDCreatedACustomMeal() {
       testCustomerId= addNewCustomer("vegan","strawberry","raghadabukhalil90@gmail.com");
        mealId = customMealService.createCustomMeal(testCustomerId, "Vegan Delight");
        assertTrue("Meal should be created", mealId > 0);
    }

    @And("the customer tries to add {string} which is not suitable")
    public void theCustomerTriesToAddWhichIsNotSuitable(String arg0)  {

        checkAddingIng = customMealService.addIngredient(mealId, arg0);
        assertFalse("Incompatible ingredient should not be added", checkAddingIng);
    }

    @When("the system suggests an alternative ingredient")
    public void theSystemSuggestsAnAlternativeIngredient() throws SQLException {
        mealId = customMealService.createCustomMeal(testCustomerId, "test meal");
        suggestedAlternative = customMealService.suggestAlternetive("chicken", mealId);
        assertNotNull("A suggested alternative should be displayed ", suggestedAlternative);
    }

    @Then("the chef is alerted with the substitution")
    public void theChefIsAlertedWithTheSubstitution() {
        String msg = "Ingredient 'chicken' was rejected. Suggested instead: " + suggestedAlternative;
        String result = NotificationService.getInstance().sendNotification(1, msg);
        assertTrue(result.contains("Notification sent "));
        List<String> notifications = NotificationService.getInstance().getNotificationsListForChef(1);
        assertTrue(notifications.contains(msg));
    }
}
