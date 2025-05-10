package cookingTest;

import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.Ingredient;
import org.InventoryService;
import org.KitchenManagerService;
import org.database.DatabaseConnection;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;


public class NotifyUsersOfLowStockIngredients {
    private InventoryService service;
    private static final Logger logger = Logger.getLogger(InventoryTrackingAndRestockingSuggestions.class.getName());
    List<String> lowStock;
    List<String> alters;


    @Given("multiple ingredients are below the stock threshold")
    public void multiple_ingredients_are_below_the_stock_threshold() {
        service = InventoryService.getInstance();
       /* try {

         //   service.addOrUpdateIngredient(new Ingredient("tomato", "available", "vegetarian", 2));
           // service.addOrUpdateIngredient(new org.Ingredient("carrot", "available", "vegetarian", 1));


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
        logger.info("Low-stock ingredients added.");
    }

    @When("the system checks stock levels")
    public void the_system_checks_stock_levels() {
        try {
            lowStock = service.checkForLowStock();
            Assert.assertFalse(lowStock.isEmpty());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Then("it should generate an alert for each low-stock ingredient")
    public void it_should_generate_an_alert_for_each_low_stock_ingredient() throws SQLException {
        alters = service.getAlters();
        Assert.assertEquals(alters.size(), lowStock.size());
        for (String ingredient : alters) {
            logger.info(ingredient);
            Assert.assertTrue(ingredient.startsWith("Low stock : "));

        }
    }


    @Given("there are low-stock ingredients in the inventory")
    public void there_are_low_stock_ingredients_in_the_inventory() {
        service = InventoryService.getInstance();
      /*  try {

            service.addOrUpdateIngredient(new Ingredient("Salmon", "available", "Non-vegetarian", 4));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }*/
    }

    @When("the system generates alerts")
    public void the_system_generates_alerts() {
        try {

            alters = service.getAlters();
            for (String ingredient : alters) {

                Assert.assertTrue(ingredient.startsWith("Low stock : "));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Then("the kitchen manager should receive a notification about them")
    public void the_kitchen_manager_should_receive_a_notification_about_them() throws SQLException {
        KitchenManagerService manager = new KitchenManagerService();
        lowStock = service.checkForLowStock();
        for (String ingredient : lowStock) {
            boolean found = false;
            for (String notify : manager.getNotifications()) {
                if (notify.contains("Low stock : " + ingredient)) {
                    found = true;
                    logger.info(notify);
                    break;
                }
            }
            Assert.assertTrue("A notification must be send to kitchen manager that " + ingredient + " is low stock!", found);

        }

    }


    @Given("an ingredient quantity falls below the threshold")
    public void an_ingredient_quantity_falls_below_the_threshold() {
        service = InventoryService.getInstance();
        try {

            service.addOrUpdateIngredient(new Ingredient("tomato", "available", "vegetarian", 2));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @When("the system checks inventory levels")
    public void the_system_checks_inventory_levels() {
        try {
            lowStock = service.checkForLowStock();
            for (String ingredient : lowStock) {
                service.updateStatusToOutOfStock(ingredient);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Then("it should mark the ingredient as low in stock")
    public void it_should_mark_the_ingredient_as_low_in_stock() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
    PreparedStatement stmt = conn.prepareStatement("SELECT status FROM inventory WHERE name = ?");
        stmt.setString(1,"tomato");
    ResultSet rs = stmt.executeQuery();
        if(rs.next()){
        String status = rs.getString("status");
        Assert.assertEquals("out of stock", status);
        conn.close();
    }
        else {
            Assert.fail("Ingredient not found  in Inventory");
        }

}





}
