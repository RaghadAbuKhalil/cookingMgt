package cookingTest;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.CustomMealService;
import org.database.DatabaseConnection;
import org.junit.Assert;

import java.sql.*;

public class SuggestIngredientSubstitutionsBasedOnDietaryRestrictions {
    private static CustomMealService customMealService ;
private String ingredient;
    int mealId;
    private String suggestedAlternative;
    @BeforeAll
    public static void setup() {

        customMealService = CustomMealService.getInstance();

    }

    @Given("a customer selects {string} for their custom meal")
    public void aCustomerSelectsForTheirCustomMeal(String arg0) {
        mealId =customMealService.createCustomMeal(1,"test alternative");
        customMealService.addIngredient(mealId,arg0);
        ingredient=arg0;

    }

    @And("{string} is out of stock")
    public void isOutOfStock(String arg0) {
        String sql = "SELECT name FROM inventory WHERE name LIKE ?;\n";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
          ;
           pstmt.setString(1, "%" + arg0 + "%");


           ResultSet rs = pstmt.executeQuery();
           Assert.assertTrue(arg0+" is available or does not exist.",rs.next());
           System.out.println(arg0+" is out of stock.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @When("the system checks for a suitable alternative")
    public void theSystemChecksForASuitableAlternative() {
        suggestedAlternative = customMealService.suggestAlternetive(ingredient,mealId);
    }

    @Then("it should suggest  available alternative")
    public void itShouldSuggestAvailableAlternative() {
        Assert.assertNotNull(suggestedAlternative);
    }



    @And("display a message: {string}")
    public void displayAMessage(String arg0) {
        System.out.println(arg0 + suggestedAlternative+" ?");
    }

    @Given("a customer selects {string} which is restricted for their dietary preferences")
    public void aCustomerSelectsWhichIsRestrictedForTheirDietaryPreferences(String arg0) {
        mealId =customMealService.createCustomMeal(1,"test alternative1");
        customMealService.addIngredient(mealId,arg0);
        ingredient=arg0;

    }

    @Then("it should suggest  ingredient that matches their dietary preferences")
    public void itShouldSuggestThatMatchesTheirDietaryPreferences() {
        Assert.assertNotNull(suggestedAlternative);
    }


    @Given("a customer selects {string} which is an allergen for them")
    public void aCustomerSelectsWhichIsAnAllergenForThem(String arg0) {
        mealId =customMealService.createCustomMeal(1,"test alternative3");
        customMealService.addIngredient(mealId,arg0);
        ingredient=arg0;
    }

    @Then("it should suggest {string} that does not trigger any allergic reactions")
    public void itShouldSuggestThatDoesNotTriggerAnyAllergicReactions(String arg0) {
              Assert.assertNotNull(suggestedAlternative);
    }


}
