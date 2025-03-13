package cookingTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;

public class storeDietaryPreferencesAndAllergies {
    private static final  Map<String, String> customerPreferences = new HashMap<>();
    private String allergy;
    private boolean   warningMsg=false;
    private String  order;

    @Given("a customer wants to personalize their meal selection")
    public void aCustomerWantsToPersonalizeTheirMealSelection() {
        System.out.println("Customer is personalizing meal selection");

    }
    @When("they enter their dietary preferences and allergies into the system")
    public void theyEnterTheirDietaryPreferencesAndAllergiesIntoTheSystem() {
        customerPreferences.put("Diet", "Vegan");
        customerPreferences.put("Allergy", "Strawberry");
        System.out.println("Dietary preferences : " + customerPreferences);
    }
    @Then("the system stores the preferences and ensures future orders comply with them")
    public void theSystemStoresThePreferencesAndEnsuresFutureOrdersComplyWithThem() {
        Assert.assertTrue("Preferences should be stored", customerPreferences.containsKey("Diet"));
        System.out.println("Preferences successfully saved."+customerPreferences);
    }




    @Given("a customer has saved their dietary preferences")
    public void aCustomerHasSavedTheirDietaryPreferences() {
       Assert.assertFalse("Preferences not  exist", customerPreferences.isEmpty());
        System.out.println("Customer preferences exist   " + customerPreferences);
    }
    @When("a chef prepares a meal for that customer")
    public void aChefPreparesAMealForThatCustomer() {
        System.out.println("Chef is preparing a meal");
    }
    @Then("the system displays their preferences to the chef for customization")
    public void theSystemDisplaysTheirPreferencesToTheChefForCustomization() {
        Assert.assertNotNull("Preferencesare not available to the chef", customerPreferences.get("Diet"));
        System.out.println("Preferences displayed to chef: " + customerPreferences);
    }



    @Given("a customer has an allergy")
    public void aCustomerHasAnAllergy() {
        allergy = customerPreferences.get("Allergy");
        System.out.println("Customer allergy: " + allergy);
    }

    @When("they try to order a dish containing his allergy")
    public void orderADishContainingHisAllergy() {
        order = "Strawberry";
        if (order.contains(allergy)) {
            warningMsg = true;
        }
        System.out.println("Customer attempts to order: " + order);
    }


    @Then("the system warns them and suggests an alternative")
    public void system_warns_customer() {
        Assert.assertTrue("Warning should be displayed", warningMsg);
        System.out.println("Warning: This dessert contains allergens! Suggested alternative: chocolate cupcake.");
    }
    public void theSystemWarnsThemAndSuggestsAnAlternative() {

    }
}
