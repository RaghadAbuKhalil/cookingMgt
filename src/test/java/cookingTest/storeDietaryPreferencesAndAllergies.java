package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.DietaryAndAllergies;
import org.junit.Assert;




public class storeDietaryPreferencesAndAllergies {

    private String allergy;
    private boolean   warningMsg=false;
    private String  order;

    public DietaryAndAllergies dietaryAndAllergies1;



    @Given("a customer wants to personalize their meal selection")
    public void aCustomerWantsToPersonalizeTheirMealSelection() {
        dietaryAndAllergies1 = new DietaryAndAllergies();
        Assert.assertNotNull("the object of DietaryAndAllergies is not created ",dietaryAndAllergies1); // if obj not created msg appear if obj created 2nd msg aprrear
        System.out.println("Customer is personalizing meal selection");

    }
    @When("they enter their dietary preferences and allergies into the system")
    public void theyEnterTheirDietaryPreferencesAndAllergiesIntoTheSystem() {
        dietaryAndAllergies1.setCustomerPreferences("vagen","strawberry");
Assert.assertTrue("the map is empty",!dietaryAndAllergies1. getCustomerPreferences().isEmpty());
        System.out.println("Dietary preferences : " +dietaryAndAllergies1. getCustomerPreferences());
    }
    @Then("the system stores the preferences and ensures future orders comply with them")
    public void theSystemStoresThePreferencesAndEnsuresFutureOrdersComplyWithThem() {
        Assert.assertTrue("Preferences should be stored",dietaryAndAllergies1. getCustomerPreferences().containsKey("Diet"));
        System.out.println("Preferences successfully saved."+dietaryAndAllergies1. getCustomerPreferences());
    }




    @Given("a customer has saved their dietary preferences")
    public void aCustomerHasSavedTheirDietaryPreferences() {
       Assert.assertFalse("Preferences not  exist", dietaryAndAllergies1. getCustomerPreferences().isEmpty());
        System.out.println("Customer preferences exist   " + dietaryAndAllergies1. getCustomerPreferences());
    }
    @When("a chef prepares a meal for that customer")
    public void aChefPreparesAMealForThatCustomer() {
        System.out.println("Chef is preparing a meal");
    }
    @Then("the system displays their preferences to the chef for customization")
    public void theSystemDisplaysTheirPreferencesToTheChefForCustomization() {
        Assert.assertNotNull("Preferencesare not available to the chef", dietaryAndAllergies1. getCustomerPreferences().get("Diet"));
        System.out.println("Preferences displayed to chef: " +dietaryAndAllergies1. getCustomerPreferences());
    }




    @Given("a customer has an allergy")
    public void aCustomerHasAnAllergy() {
   if (  dietaryAndAllergies1. getCustomerPreferences().get("Allergy")==null)
       System.out.println("Customer does not have any allergy")   ;
      else    System.out.println("Customer allergy: " + dietaryAndAllergies1. getCustomerPreferences().get("Allergy"));

    }

    @When("they try to order a dish containing his allergy")
    public void orderADishContainingHisAllergy() {
        order = "Strawberry CAKE";
        warningMsg = dietaryAndAllergies1.checkAllergies(order);
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
