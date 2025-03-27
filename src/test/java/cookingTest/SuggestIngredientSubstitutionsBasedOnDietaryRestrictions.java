package cookingTest;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SuggestIngredientSubstitutionsBasedOnDietaryRestrictions {
    @Given("a customer has a recorded allergy to nuts")
    public void aCustomerHasARecordedAllergyToNuts() {
    }

    @When("they try to add almonds to their custom meal")
    public void theyTryToAddAlmondsToTheirCustomMeal() {
    }

    @Then("the system should suggest sunflower seeds as an alternative")
    public void theSystemShouldSuggestSunflowerSeedsAsAnAlternative() {
    }

    @And("notify the chef about the substitution")
    public void notifyTheChefAboutTheSubstitution() {
    }

    @Given("a customer selects mushrooms for their pasta dish")
    public void aCustomerSelectsMushroomsForTheirPastaDish() {
    }

    @When("the system detects that mushrooms are out of stock")
    public void theSystemDetectsThatMushroomsAreOutOfStock() {
    }

    @Then("the system should suggest zucchini as an alternative")
    public void theSystemShouldSuggestZucchiniAsAnAlternative() {
    }

    @And("ask the customer to confirm the substitution before proceeding")
    public void askTheCustomerToConfirmTheSubstitutionBeforeProceeding() {
    }

    @Given("a customer follows a vegan diet")
    public void aCustomerFollowsAVeganDiet() {
    }

    @When("they select an ingredient that is not vegan, such as butter")
    public void theySelectAnIngredientThatIsNotVeganSuchAsButter() {
    }

    @Then("the system should suggest a vegan alternative like margarine or olive oil")
    public void theSystemShouldSuggestAVeganAlternativeLikeMargarineOrOliveOil() {
    }

    @And("confirm the change with the customer")
    public void confirmTheChangeWithTheCustomer() {
    }
}
