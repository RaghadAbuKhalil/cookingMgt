package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.DietaryAndAllergies;
import org.KitchenManagerService;
import org.junit.Assert;

import java.sql.SQLException;


public class storeDietaryPreferencesAndAllergies {
    private int testId ;
    private boolean   warningMsg=false;
    private String  order;
    private String allergy;
    private String preferences ;
    private   DietaryAndAllergies dietaryAndAllergies1 ;

@Before
public  void setup(){
    if (dietaryAndAllergies1 == null) {
        dietaryAndAllergies1 = DietaryAndAllergies.getInstance();
        Assert.assertNotNull("the object of DietaryAndAllergies is not created ", dietaryAndAllergies1);
        System.out.println("Customer is personalizing meal selection");
        testId= DietaryAndAllergies.addNewCustomer("vegan","strawberry","raghadabukhalil90@gmail.com");
        allergy =DietaryAndAllergies.getCustomerAllergies(testId);
        preferences =DietaryAndAllergies.getCustomerPreferences(testId);

    }
}
    @Given("a customer wants to personalize their meal selection")
    public void aCustomerWantsToPersonalizeTheirMealSelection() {

       }
    @When("they enter their dietary preferences and allergies into the system")
    public void theyEnterTheirDietaryPreferencesAndAllergiesIntoTheSystem() {

        Assert.assertNotNull("the customer does not store his allergy",allergy);
        Assert.assertNotNull("the customer does not store his Preferences",preferences);
        System.out.println("Dietary preferences : " +preferences+ " and allergies :"+allergy);
    }
    @Then("the system stores the preferences and ensures future orders comply with them")
    public void theSystemStoresThePreferencesAndEnsuresFutureOrdersComplyWithThem() {
        Assert.assertTrue("Preferences should be stored",preferences.equals("vegan"));
        Assert.assertTrue("Allergies should be stored",allergy.equals("strawberry"));
        System.out.println("Preferences and Allergies successfully saved.");
    }


    @Given("a customer has saved their dietary preferences")
    public void aCustomerHasSavedTheirDietaryPreferences() {

        Assert.assertTrue("Preferences are empty",!preferences.isEmpty());
        Assert.assertTrue("allergy empty",!allergy.isEmpty());
        System.out.println("Customer preferences and allergy are exist");
    }

    @When("a chef prepares a meal for that customer")
    public void aChefPreparesAMealForThatCustomer() {
        System.out.println("Chef is preparing a meal");
    }

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

    @When("they try to order a dish containing his allergy")
    public void orderADishContainingHisAllergy() throws SQLException {
          order="Strawberry CAKE";
        warningMsg = DietaryAndAllergies.checkAllergies(testId ,order);
        System.out.println("Customer attempts to order: " + order);

    }



    @Then("the system warns them and suggests an alternative")
    public void system_warns_customer() {
        Assert.assertTrue("Warning should be displayed", warningMsg);
        String alternative = KitchenManagerService.findAlternative(testId);
        Assert.assertNotNull("Sorry! their is no alternative for this meal",alternative);
        Assert.assertFalse(alternative.isEmpty());
        System.out.println("Warning: This dessert contains allergens! Suggested alternative: "+alternative);
    }





}
