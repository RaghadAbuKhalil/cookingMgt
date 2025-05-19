package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.DietaryAndAllergies;
import org.InvoicesAndFinancial;
import org.KitchenManagerService;
import org.junit.Assert;

import java.util.Map;
import java.util.logging.Logger;

public class generateInvoicesandTrackFinancialReports {
    InvoicesAndFinancial invoicesAndFinancial1;
   int orderId ;
    int customerId;
    String mealName="meet";
    String orderDate="2025-05-25";
    double monthlyRevenue = 0;
    Map<String, Double> revenueByMealType;
    Map<String, Integer> mostOrderedMeals;


    private static final Logger logger = Logger.getLogger(generateInvoicesandTrackFinancialReports.class.getName());

    String dietary="vagen";
    String  email="s12217034@stu.najah.edu";
    String allergies="meet";
    String date = "2025-05-25";
    double revenue = 0;
    Map<String, Integer> itemizedSales;
    int orderCount = 0;
    private KitchenManagerService kitchenManager1;
    @Before
    public void setUp() {
        kitchenManager1 = KitchenManagerService.getInstance();
        invoicesAndFinancial1 = new InvoicesAndFinancial();

        customerId= DietaryAndAllergies.addNewCustomer( dietary, allergies,  email);

    orderId=kitchenManager1.insertOrder(customerId,mealName,orderDate);
kitchenManager1.updateOrderStatus(orderId,"Completed");


    }
   @Given("a customer has placed an order and the order is completed")
    public void aCustomerHasPlacedAnOrderAndTheOrderIsCompleted() {
        kitchenManager1.insertOrder(customerId,mealName,orderDate);

    }

    @When("the order is completed")
    public void theOrderIsCompleted() {
kitchenManager1.updateOrderStatus(orderId,"Completed");
String status= kitchenManager1.getOrderStatus(orderId);
Assert.assertEquals("the task completed","Completed",status);
    }

    @Then("the system should generate an invoice for the customer which itemized details of the meal and the total price")
    public void theSystemShouldGenerateAnInvoiceForTheCustomer() {
invoicesAndFinancial1.generateInvoice(orderId);
    }



    @And("the system should send the invoice to the customer's email")
    public void theSystemShouldSendTheInvoiceToTheCustomerSEmail() {
        String email = invoicesAndFinancial1.getCustomerEmail(customerId);
        String subject = "Your Invoice";
        String body = invoicesAndFinancial1.displayInvoice(orderId);

        boolean result = InvoicesAndFinancial.sendInvoiceEmail(email, subject, body);
        Assert.assertTrue("Invoice should be sent to customer's email", result);
    }


    @Given("a system administrator wants to track the sales performance of the day")
    public void aSystemAdministratorWantsToTrackTheSalesPerformanceOfTheDay() {
        logger.info("System administrator requested daily sales tracking for date: " + date);
    }

    @When("the administrator requests a daily sales report")
    public void theAdministratorRequestsADailySalesReport() {
        revenue = invoicesAndFinancial1.calculateDailyRevenue(date);
        itemizedSales = invoicesAndFinancial1.getItemMealSales(date);
        orderCount = invoicesAndFinancial1.getOrderCountForDay(date);
        logger.info("Daily report data fetched successfully.");
    }

    @Then("the system should generate a report showing the total revenue for the day")
    public void theSystemShouldGenerateAReportShowingTheTotalRevenueForTheDay() {
        logger.info("Total Revenue: $" + revenue);
        Assert.assertTrue("Revenue should be greater than or equal to 0", revenue >= 0);
    }

    @And("the report should include itemized sales data for each meal type")
    public void theReportShouldIncludeItemizedSalesDataForEachMealType() {
        logger.info("Itemized Sales Data:");
        for (Map.Entry<String, Integer> entry : itemizedSales.entrySet()) {
            logger.info("Meal: " + entry.getKey() + ", Quantity Sold: " + entry.getValue());
        }
        Assert.assertFalse("Itemized sales should not be empty", itemizedSales.isEmpty());
    }

    @And("the system should display the number of orders and total revenue")
    public void theSystemShouldDisplayTheNumberOfOrdersAndTotalRevenue() {
        logger.info("Total Orders Completed: " + orderCount);
        logger.info("Total Revenue: $" + revenue);
        Assert.assertTrue("Order count should be >= 0", orderCount >= 0);

    }

    @Given("a system administrator wants to analyze sales for the current month")
    public void aSystemAdministratorWantsToAnalyzeSalesForTheCurrentMonth() {
        logger.info("Administrator requested monthly sales analysis.");
    }

    @When("the administrator requests a monthly financial report")
    public void theAdministratorRequestsAMonthlyFinancialReport() {
        String month = "may";
        String year = "2025";

        logger.info("Fetching monthly revenue for " + month + " " + year);
        monthlyRevenue = invoicesAndFinancial1.calculateMonthlyRevenue(month, year);

        logger.info("Fetching revenue breakdown by meal type...");
        revenueByMealType = invoicesAndFinancial1.getRevenueBreakdownByMealName(month, year);

        logger.info("Fetching most ordered meals...");
        mostOrderedMeals = invoicesAndFinancial1.getMostOrderedMeals(month, year);

        logger.info("Monthly financial report data loaded successfully.");
    }

    @Then("the system should generate a report showing the total revenue for the month")
    public void theSystemShouldGenerateAReportShowingTheTotalRevenueForTheMonth() {
        logger.info("Total Revenue for the Month: $" + monthlyRevenue);
        Assert.assertTrue("Revenue should be non-negative", monthlyRevenue >= 0);
    }

    @And("the report should include breakdowns of revenue by meal type and discounts applied")
    public void theReportShouldIncludeBreakdownsOfRevenueByMealTypeAndDiscountsApplied() {
        if (revenueByMealType.isEmpty()) {
            logger.warning("No revenue breakdown found for this month.");
        } else {
            logger.info("Revenue Breakdown by Meal Type:");
            for (Map.Entry<String, Double> entry : revenueByMealType.entrySet()) {
                logger.info("- " + entry.getKey() + ": $" + entry.getValue());
            }
        }
        Assert.assertFalse("Revenue breakdown should not be empty", revenueByMealType.isEmpty());

    }

    @And("the system should display trends in customer orders, including most ordered meals and revenue sources")
    public void theSystemShouldDisplayTrendsInCustomerOrdersIncludingMostOrderedMealsAndRevenueSources() {
        if (mostOrderedMeals.isEmpty()) {
            logger.warning("No customer order trends found for this month.");
        } else {
            logger.info("Most Ordered Meals:");
            for (Map.Entry<String, Integer> entry : mostOrderedMeals.entrySet()) {
                logger.info("- " + entry.getKey() + ": " + entry.getValue() + " orders");
            }
        }
        Assert.assertFalse("Most ordered meals should not be empty", mostOrderedMeals.isEmpty());

    }
}
