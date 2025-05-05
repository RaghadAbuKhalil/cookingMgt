package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.DietaryAndAllergies;
import org.junit.Assert;




public class storeDietaryPreferencesAndAllergies {
    private final int testId = 1;
    private boolean   warningMsg=false;
    private String  order;
    private String allergy =dietaryAndAllergies1.getCustomerAllergies(testId);
    private String preferences =dietaryAndAllergies1.getCustomerPreferences(testId);
    private  static DietaryAndAllergies dietaryAndAllergies1;
    @BeforeAll
   private void setup(){

 dietaryAndAllergies1 = new DietaryAndAllergies();
   }

    @Given("a customer wants to personalize their meal selection")
    public void aCustomerWantsToPersonalizeTheirMealSelection() {
        Assert.assertNotNull("the object of DietaryAndAllergies is not created ",dietaryAndAllergies1); // if obj not created msg appear if obj created 2nd msg aprrear
        System.out.println("Customer is personalizing meal selection");

    }
    @When("they enter their dietary preferences and allergies into the system")
    public void theyEnterTheirDietaryPreferencesAndAllergiesIntoTheSystem() {
        dietaryAndAllergies1. setCustomerPreferences(testId,"vagen","strawberry");
        Assert.assertNotNull("the customer does not store his allergy",allergy);
        Assert.assertNotNull("the customer does not store his Preferences",preferences);
        System.out.println("Dietary preferences : " +preferences+ " and allergies :"+allergy);
    }
    @Then("the system stores the preferences and ensures future orders comply with them")
    public void theSystemStoresThePreferencesAndEnsuresFutureOrdersComplyWithThem() {
        Assert.assertTrue("Preferences should be stored",preferences.equals("vagen"));
        Assert.assertTrue("Allergies should be stored",allergy.equals("strawberry"));
        System.out.println("Preferences and Allergies successfully saved.");
    }


    @Given("a customer has saved their dietary preferences")
    public void aCustomerHasSavedTheirDietaryPreferences() {

        Assert.assertTrue("Preferences are empty",!preferences.isEmpty());
        Assert.assertTrue("allergy empty",!allergy.isEmpty());
        System.out.println("Customer preferences and allergy are exist");
    }
    //*********************************************************************************************
    @When("a chef prepares a meal for that customer")
    public void aChefPreparesAMealForThatCustomer() {
        System.out.println("Chef is preparing a meal");
    }
//***************************************************************************************************
    @Then("the system displays their preferences to the chef for customization")
    public void theSystemDisplaysTheirPreferencesToTheChefForCustomization() {
        Assert.assertNotNull("Preferencesare not available to the chef",allergy );
        System.out.println("Preferences displayed to chef: " +allergy);
    }




    @Given("a customer has an allergy")
    public void aCustomerHasAnAllergy() {
   if ( allergy ==null || allergy.isEmpty())
       System.out.println("Customer does not have any allergy")   ;
      else    System.out.println("Customer allergy: " +allergy);

    }
//**********************************تعديل رغد
    @When("they try to order a dish containing his allergy")
    public void orderADishContainingHisAllergy() {
        order = "Strawberry CAKE";
        warningMsg = dietaryAndAllergies1.checkAllergies(1,order);
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
