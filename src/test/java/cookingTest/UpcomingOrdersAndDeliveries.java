package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.InvoicesAndFinancial;
import org.KitchenManagerService;
import org.NotificationService;
import org.TaskManager;
import org.database.DatabaseConnection;
import org.junit.Assert;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UpcomingOrdersAndDeliveries {
    KitchenManagerService kitchenManager1;
    TaskManager manager;
    InvoicesAndFinancial invoicesAndFinancial1;
    NotificationService notification;
    int orderId ;
    int customerId;
    int chefid;
    String dietary="vagen";
    String  email="s12217034@stu.najah.edu";
    String allergies="meet";
    String mealName="meet";
    int quantity=1;
    int price=19;
    String orderDate="2025-05-10";

    List notificationList= new ArrayList<String>();
    @Before()
    public void setup(){


        kitchenManager1 = KitchenManagerService.getInstance();
        invoicesAndFinancial1 = new InvoicesAndFinancial();
        notification = new NotificationService();
        manager=new TaskManager();
        String insertSQL = "INSERT INTO customer_preferences (dietary, email, allergies) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertSQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, dietary);
            stmt.setString(2, email);
            stmt.setString(3, allergies);

            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                customerId = rs.getInt(1); // تحديث قيمة customerId المتغيرة
                System.out.println("Customer inserted successfully. Generated ID: " + customerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Given("a customer has a scheduled meal delivery for tomorrow")
    public void aCustomerHasAScheduledMealDeliveryForTomorrow() {
        String futureDate = LocalDate.now().plusDays(1).toString();

        orderId = kitchenManager1.insertOrder(customerId, mealName, price, futureDate);
    }

    @And("the customer's email is registered in the system")
    public void theCustomerSEmailIsRegisteredInTheSystem() {

        email= invoicesAndFinancial1.getCustomerEmail(customerId);

        System.out.println("email" + email);
    }

    @When("the system checks for upcoming deliveries within {int} hours")
    public void theSystemChecksForUpcomingDeliveriesWithinHours(int arg0) {
        invoicesAndFinancial1.checkAndSendDeliveryReminders();

    }

    @Then("the customer should receive an email reminder about the delivery")
    public void theCustomerShouldReceiveAnEmailReminderAboutTheDelivery() {
        Assert.assertTrue(invoicesAndFinancial1.wasReminderSentToCustomer(customerId));
    }

    @Given("a cooking task is scheduled for tomorrow")
    public void aCookingTaskIsScheduledForTomorrow() {
        String taskDate = LocalDate.now().plusDays(1).toString();
       chefid=  manager.assignTaskToChef(orderId,mealName);
    }

    @And("the chef is assigned to that task")
    public void theChefIsAssignedToThatTask() {

    }

    @When("the system runs the daily reminder job")
    public void theSystemRunsTheDailyReminderJob() throws InterruptedException {
        synchronized (this) {
        notification.sendChefRemindersForTomorrow();
    }}

    @Then("the chef should receive a notification with the meal details and preparation time")
    public void theChefShouldReceiveANotificationWithTheMealDetailsAndPreparationTime() {
     notificationList=   notification.getNotificationsListForChef(chefid);
        Assert.assertNotNull("notification in their task list",notificationList.contains(mealName));
    }

    @Given("there are no deliveries or cooking tasks scheduled in the next {int} hours")
    public void thereAreNoDeliveriesOrCookingTasksScheduledInTheNextHours(int arg0) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
        stmt.executeUpdate("DELETE FROM tasks");
        stmt.executeUpdate("DELETE FROM orders");
        stmt.executeUpdate("DELETE FROM notifications");}
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @When("the system performs the reminder check")
    public void theSystemPerformsTheReminderCheck() {
        try {
            NotificationService.getInstance().sendChefRemindersForTomorrow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Then("no notifications or emails should be sent to customers or chefs")
    public void noNotificationsOrEmailsShouldBeSentToCustomersOrChefs() {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM notifications")) {

            if (rs.next()) {
                int count = rs.getInt("total");
                Assert.assertEquals(0, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}