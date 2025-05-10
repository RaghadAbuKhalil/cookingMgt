package cookingTest;

 import java.util.logging.Logger;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.Ingredient;
import org.InventoryService;
import org.junit.Assert;


import java.sql.SQLException;
import java.util.List;
import java.util.Map;


 import static org.junit.Assert.*;

public class InventoryTrackingAndRestockingSuggestions {
        private static InventoryService service;
    Map<String, Integer> inventory;
    List<String> lowStock;
    private static final Logger logger = Logger.getLogger(InventoryTrackingAndRestockingSuggestions.class.getName());
    @Given("the kitchen manager is logged into the system")
    public void the_kitchen_manager_is_logged_into_the_system() {

        service = new InventoryService().getInstance() ;
            Assert.assertNotNull("Something wrong happened while logging ",service);


           // service.addIngredient(new Ingredient("potato", "available", "vegetarian", 2));
            logger.info("Logging in into the system...");


    }
    @When("they open the inventory page")
    public void they_open_the_inventory_page() {
        inventory = service.getAllIngredientQuantities();
        Assert.assertNotNull("  Ingredients from inventory must be displayed ",inventory);
        logger.info("Inventory page opened.");

    }
    @Then("they should see the current quantity of each ingredient")
    public void they_should_see_the_current_quantity_of_each_ingredient() {

        assertEquals(Integer.valueOf(4), inventory.get("banana"));
        assertEquals(Integer.valueOf(15), inventory.get("onion"));
        assertEquals(Integer.valueOf(2), inventory.get("potato"));
        logger.info("Ingredients from inventory loaded successfully");
    }

    @Given("an ingredient quantity is below the minimum threshold")
    public void an_ingredient_quantity_is_below_the_minimum_threshold() {
        try {
             lowStock = service.checkForLowStock();
            Assert.assertNotNull(" something wrong happened while loading low stock ingredients",lowStock);
            logger.info("loading low stock ingredients");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @When("the system checks the stock levels")
    public void the_system_checks_the_stock_levels() {
        try {
            List<String>  checkLow = service.checkForLowStock();
            assertEquals(checkLow,lowStock);
            logger.info("System checked stock levels.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }
    @Then("it should send an alert to the kitchen manager")
    public void it_should_send_an_alert_to_the_kitchen_manager() {
        for (String ingredient : lowStock) {
            logger.warning(ingredient + " is below minimum threshold in stock levels.");
        }
        Assert.assertFalse("There should be at least one alert", lowStock.isEmpty());
    }

    @Given("an ingredient quantity is low")
    public void an_ingredient_quantity_is_low() {
        try {
            lowStock = service.getAlters();
            assertFalse("We should have some low stock ingredients",lowStock.isEmpty());
            logger.info(" We have some low stock ingredients");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @When("the kitchen manager opens the inventory page")
    public void the_kitchen_manager_opens_the_inventory_page() {
        inventory = service.getAllIngredientQuantities();
        assertNotNull("Inventory page must be opened",inventory);
        logger.info("Kitchen manager opened the inventory page.");
    }
    @Then("the system should suggest restocking that ingredient")
    public void the_system_should_suggest_restocking_that_ingredient() {
        try {
            List<String> restock = service.suggestRestocking();
          assertNotNull("The list of restock suggestions should be exist",restock);
            assertFalse("The list of restock suggestions should be filled",restock.isEmpty());
          for (String ingredient : restock) {
              logger.info( ingredient);
          }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

}
