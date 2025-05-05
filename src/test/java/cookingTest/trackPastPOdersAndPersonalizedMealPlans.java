package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.OrderHistoryService;
import org.junit.Assert;

import java.util.List;
import java.util.Map;

public class trackPastPOdersAndPersonalizedMealPlans {

    private OrderHistoryService orderService; // هذا هو الكائن الذي سيحمل الخدمة
    private List<String> orderedlist;
    String reorderMessage;
    Map<String, Integer> thedemand;
    // تهيئة الكائن باستخدام Singleton
    @Before
    public void setup() {
        this.orderService = OrderHistoryService.getInstance(); // استخدام الكائن الوحيد (Singleton)
    }

    @Given("a customer has previously placed meal orders")
    public void a_customer_has_previously_placed_meal_orders() {

        orderService.storeOrder(1, "Vegan Salad");
        orderService.storeOrder(1, "Pasta");
    }

    @When("they navigate to their order history page")
    public void they_navigate_to_their_order_history_page() {
        orderedlist = orderService.getOrderHistory(1);
    }

    @Then("the system displays a list of their past meal orders")
    public void the_system_displays_a_list_of_their_past_meal_orders() {
        Assert.assertFalse("Order history should not be empty", orderedlist.isEmpty());
        System.out.println("Ordered list: " + orderedlist);
    }

    @And("the customer can reorder any past meal with a single click")
    public void the_customer_can_reorder_any_past_meal_with_a_single_click() {
        reorderMessage = orderService.reorderMeal(1, "Pasta");
        Assert.assertEquals("Meal reordered successfully Pasta", reorderMessage);
        System.out.println(reorderMessage);
    }

//******************************************************
    @Given("a chef wants to suggest a meal plan,")
    public void aChefWantsToSuggestAMealPlan() {

    }

    @When("they access the customer’s order history,")
    public void theyAccessTheCustomerSOrderHistory() {
        orderedlist = orderService.getOrderHistory(1);
    }

    @Then("they can view past preferences and suggest a suitable plan.")
    public void theyCanViewPastPreferencesAndSuggestASuitablePlan() {
        System.out.println("Customer's past orders: " + orderedlist);

        if (orderedlist.contains("Vegan Salad")) {
            System.out.println("Suggested meal plan: Vegan meal options");
        } else {
            System.out.println("Suggested meal plan: Mixed diet meal options");
        }
    }

    @Given("the system stores customer order history")
    public void theSystemStoresCustomerOrderHistory() {

        System.out.println("Customer order history stored.");
    }

    @When("an administrator requests customer order data")
    public void anAdministratorRequestsCustomerOrderData() {
        System.out.println("Administrator is retrieving customer order data.");

    }

    @Then("the system retrieves and displays order history trends")
    public void theSystemRetrievesAndDisplaysOrderHistoryTrends() {
        thedemand = orderService.orderHistoryTrends();
        System.out.println("Order trends: " + thedemand);
    }

    @And("the administrator can analyze data to improve service offerings")
    public void theAdministratorCanAnalyzeDataToImproveServiceOfferings() {
       // Map<String, Integer> trends = orderService.analyzeOrderTrends();
        if (thedemand.get("Vegan Salad") > 3) {  // إذا كانت هناك وجبة Vegan Salad تم طلبها أكثر من مرتين، نقترح زيادة توفرها
            System.out.println("Suggested action: Increase availability of vegan meals.");
        }

    }}