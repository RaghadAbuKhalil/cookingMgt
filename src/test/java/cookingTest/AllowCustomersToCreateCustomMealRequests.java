package cookingTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.CustomMealService;
import org.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllowCustomersToCreateCustomMealRequests {
    private CustomMealService customMealService;
    private int mealId ;
    private  String mealName;
    private List<String> ingr = new ArrayList<>();
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

        for (String mealIng:ingr) {
            customMealService.addIngredient(mealId, mealIng);
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM custom_meals WHERE meal_id = ?")) {
            stmt.setInt(1, mealId);
            ResultSet rs = stmt.executeQuery();
            assertTrue(rs.next(), " Meal should be stored in the database!");

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
       //assertTrue(" meal could not finalize", customMealService.finalizeMeal(mealId));
    }

    @Then("the system verifies that the ingredients are available and compatible")
    public void theSystemVerifiesThatTheIngredientsAreAvailableAndCompatible() {
    }

    @And("if an ingredient is unavailable or incompatible, the system suggests an alternative")
    public void ifAnIngredientIsUnavailableOrIncompatibleTheSystemSuggestsAnAlternative() {
    }

    @Given("a customer has created a custom meal")
    public void aCustomerHasCreatedACustomMeal() {
    }

    @When("they review their selected ingredients")
    public void theyReviewTheirSelectedIngredients() {
    }

    @Then("they can edit, remove, or add new ingredients before confirming their order")
    public void theyCanEditRemoveOrAddNewIngredientsBeforeConfirmingTheirOrder() {
    }
}
