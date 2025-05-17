package cookingTest;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.InventoryService;
import org.Supplier;
import org.junit.Assert;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class IntegrateWithSuppliersForRealTimePricingAndOrdering {
    private static final Logger logger = Logger.getLogger(InventoryTrackingAndRestockingSuggestions.class.getName());
    private double price;
    private  String cheapest;
    private Supplier supplier = new Supplier();
    List<String> lowStock;
    private static InventoryService service;

    @Given("the kitchen manager is logged in")
    public void theKitchenManagerIsLoggedIn() {
  logger.info("the kitchen manager is logged in");
    }
    @When("the manager checks the price of {string}")
    public void theManagerChecksThePriceOf(String string) throws SQLException {
        supplier.addOrUpdateSupplierPrice(string ,"hiba",200.5);
        supplier.addOrUpdateSupplierPrice(string ,"raghad",300.5);

        price=supplier.getRealTimePrice(string);

    }
    @Then("the system shows the current price from the supplier")
    public void theSystemShowsTheCurrentPriceFromTheSupplier() {
        logger.info("The price for this ingredient is "+price);
    }


    @Given("the stock of {string} is below the minimum level")
    public void theStockOfIsBelowTheMinimumLevel(String string) {
        try {
            service=InventoryService.getInstance();
            lowStock = service.checkForLowStock();
            Assert.assertNotNull(" something wrong happened while loading low stock ingredients",lowStock);
            logger.info("loading low stock ingredients");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @When("the system checks inventory")
    public void theSystemChecksInventory() {
        try {
            List<String>  checkLow = service.checkForLowStock();
            assertEquals(checkLow,lowStock);
            logger.info("System checked stock levels.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    @Then("it creates a purchase order for {string}")
    public void itCreatesAPurchaseOrderFor(String string) throws SQLException {
        lowStock = service.checkForLowStock();
        assertTrue("Ingredient should be in low stock list", lowStock.contains(string));

        boolean created = supplier.createPurchaseOrder(string, 10, 1);
        assertTrue("Purchase order should be created", created);

        logger.info("Purchase order created successfully for: " + string);
    }
    @Then("sends it to the supplier")
    public void sendsItToTheSupplier() {
        logger.info("Sending the order to the supplier.");
    }





    @Given("{string} is available from multiple suppliers")
    public void isAvailableFromMultipleSuppliers(String string) throws SQLException {
        boolean available = false;
        try {
            supplier.addOrUpdateSupplierPrice(string ,"hiba",200.5);
            supplier.addOrUpdateSupplierPrice(string ,"raghad",500);
            available = supplier.isAvailableFromMultipleSuppliers(string);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        assertTrue("Ingredient " + string + " is not available from multiple suppliers", available);
    }
    @When("the manager wants to order {string}")
    public void theManagerWantsToOrder(String string) throws SQLException {
         cheapest = supplier.getCheapestSupplier(string);

    }
    @Then("the system recommends the supplier with the lowest price")
    public void theSystemRecommendsTheSupplierWithTheLowestPrice() {
        Assert.assertNotNull("the system must  recommends the supplier with the lowest price",cheapest);
        logger.info("The cheapest supplier for the ingredient is: " + cheapest);
    }


}

