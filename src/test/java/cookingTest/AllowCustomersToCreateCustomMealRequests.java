package cookingTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.CustomMealService;

public class AllowCustomersToCreateCustomMealRequests {
    private CustomMealService customMealService;
    @Given("a customer wants to customize their meal")
    public void aCustomerWantsToCustomizeTheirMeal() {
        customMealService = new CustomMealService();
        System.out.println("customer is going to  customize a meal");
    }

    @When("they select ingredients and specify dietary preferences")
    public void theySelectIngredientsAndSpecifyDietaryPreferences() {
        customMealService.addIngredient("rice");
        System.out.println("customer choose ingredients he wants ");
    }

    @Then("the system stores their custom meal request and ensures it meets their dietary needs")
    public void theSystemStoresTheirCustomMealRequestAndEnsuresItMeetsTheirDietaryNeeds() {
    }

    @Given("a customer has selected ingredients for a custom meal")
    public void aCustomerHasSelectedIngredientsForACustomMeal() {
    }

    @When("they attempt to finalize their order")
    public void theyAttemptToFinalizeTheirOrder() {

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
