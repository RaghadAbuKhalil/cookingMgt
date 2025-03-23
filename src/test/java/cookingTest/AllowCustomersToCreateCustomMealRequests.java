package cookingTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.CustomMealService;
import org.database.DatabaseConnection;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllowCustomersToCreateCustomMealRequests {
    private CustomMealService customMealService;
    private int mealId ;
    private  String mealName;
    private List<String> ingr = new ArrayList<>();
    private List<String> notAvailableIngr = new ArrayList<>();

    @Given("a customer wants to customize their meal")
    public void aCustomerWantsToCustomizeTheirMeal() {

            customMealService = new CustomMealService();
            System.out.println("customer is going to  customize a meal");

        }

    @When("they select ingredients")
    public void theySelectIngredients() {
                /*try {
            mealId = customMealService.createCustomMeal(1, "new meal");
            fail("Expected NumberFormatException to be thrown");
        } catch (NumberFormatException e) {
            assertEquals("null", e.getMessage());
        }
        customMealService.addIngredient(mealId,"chicken");*/
        mealName = "new meal";
        ingr.add("chicken");
        ingr.add("rice");

        System.out.println("customer chose ingredients he wants");

    }

    @Then("the system stores their custom meal request and ensures it meets their dietary needs")
    public void theSystemStoresTheirCustomMealRequestAndEnsuresItMeetsTheirDietaryNeeds() {
        mealId = customMealService.createCustomMeal(1, "new meal");
        if (mealId == -1) {
            fail("Failed to create a custom meal");
        }
        for (String mealIng : ingr) {
            customMealService.addIngredient(mealId, mealIng);
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM custom_meals WHERE meal_id = ?")) {
            stmt.setInt(1, mealId);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), " Meal should be stored in the database!");
            System.out.println("custom meal stored successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        @Given("a customer has selected ingredients for a custom meal")
    public void aCustomerHasSelectedIngredientsForACustomMeal() {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM custom_meals WHERE meal_id = ?")) {
                stmt.setInt(1, mealId);
                ResultSet rs = stmt.executeQuery();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    @When("they attempt to finalize their order")
    public void theyAttemptToFinalizeTheirOrder() {
        assertTrue(customMealService.finalizeMeal(mealId), " meal could not finalize");
        System.out.println("meal finalized successfully");


    }

    @Then("the system verifies that the ingredients are available and compatible")
    public void theSystemVerifiesThatTheIngredientsAreAvailableAndCompatible() {
        for (String ing:ingr){
            boolean     available  = customMealService.ingretientIsAvailable(ing);
            notAvailableIngr.add(ing);
            assertTrue(available,"this ingredient not available ");
            System.out.println(" All added ingredient are available ");
        }

    }

    @And("if an ingredient is unavailable or incompatible, the system suggests an alternative")
    public void ifAnIngredientIsUnavailableOrIncompatibleTheSystemSuggestsAnAlternative() {
       for(String ing:notAvailableIngr) {
            assertNotNull("Sorry ! We don't have an alternetive for this ingredient", customMealService.suggestAlternetive(ing));
            System.out.println(" this ingredient is unavailable . We suggest "+customMealService.suggestAlternetive(ing) +"as a aiternative");
        }

    }

    @Given("a customer has created a custom meal")
    public void aCustomerHasCreatedACustomMeal() {
        mealId = customMealService.getPreviousMeal(1);
        assertTrue(mealId>=0,"customized meal not found");
        System.out.println("  customer's custom meal is exist");

    }

    @When("they review their selected ingredients")
    public void theyReviewTheirSelectedIngredients() {
    }

    @Then("they can edit, remove, or add new ingredients before confirming their order")
    public void theyCanEditRemoveOrAddNewIngredientsBeforeConfirmingTheirOrder() {
    }
}
