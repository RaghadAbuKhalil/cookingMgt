package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.*;
import org.database.DatabaseSetup;
import org.junit.Assert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class schedulingAndTaskManagement {

   private TaskManager taskmanager1;
    private Chef chef1 ;
    private NotificationService notification1 = NotificationService.getInstance();
    private KitchenManagerService kitchenManager1  = KitchenManagerService.getInstance();
    int  chosenChef;
    String mealname;
    @Before
    public void setup() {
        DatabaseSetup.setupDatabase();
        this.chef1 = Chef.getInstance();
        this.taskmanager1= TaskManager.getInstance();// استخدام الكائن الوحيد (Singleton)
    }


    @Given("there are available chefs in the kitchen each chef has a defined workload and expertise level")
    public void thereAreAvailableChefsInTheKitchenEachChefHasADefinedWorkloadAndExpertiseLevel() {
        chef1.addChef("chef1", "Beginner");
        chef1.setChefJobload(1,4);
        chef1.addChef("chef2",  "Advanced Level");
        chef1.setChefJobload(2,3);
        chef1.printAllChefs();

    }

    @When("the kitchen manager assigns the task {string}")
    public void theKitchenManagerAssignsTheTask(String arg0) {
       mealname=arg0;

    }

    @Then("the system should assign the task to the chef with the least workload and required expertise {string}")
    public void theSystemShouldAssignTheTaskToTheChefWithTheLeastWorkloadAndRequiredExpertise(String arg0) {
         int chefid=kitchenManager1.assignTask(mealname,arg0);
        Assert.assertEquals( 8,chefid );

    }


    @And("the chef should receive a notification about the new task")
    public void theChefShouldReceiveANotificationAboutTheNewTask() {
      //  String notificationmsg = notification1.sendNotification(2, "Prepare Vegan Salad");


        assertNotNull(notificationmsg);
        assertTrue("Chef did not receive notification", notificationmsg.contains("Notification sent to Chef ID"));

        System.out.println("Notification message: " + notificationmsg);
    }

    @Given("a chef has been assigned a new task {string}")
    public void aChefHasBeenAssignedANewTask(String arg0) {

    }

    @When("the system sends a notification to the chef")
    public void theSystemSendsANotificationToTheChef() {

    }

    @Then("the chef should see the task notification in their task list")
    public void theChefShouldSeeTheTaskNotificationInTheirTaskList() {

        String taskNotificationForChef = chef1.getChefNotifications("chef2");
        assert(taskNotificationForChef.contains("Prepare Vegan Salad"));
    }

    @And("they should be able to acknowledge the task")
    public void theyShouldBeAbleToAcknowledgeTheTask() {
        boolean completeTask1= chef1.completeTask("chef2", "Prepare Vegan Salad");
        assert(completeTask1);

    }

    @Given("a chef has started working on the task {string}")
    public void aChefHasStartedWorkingOnTheTask(String arg0) {

        String taskStatus = taskmanager1.TaskStatus("meet");

        if (taskStatus == null || taskStatus.equals("Completed")) {
            System.out.println("Reassigning task: " + "meet");
            taskmanager1.giveChefTask("meet", 1, "Beginner"); // تعيين المهمة لشيف جديد
        }

        // تأكيد أن المهمة الآن في حالة "Assigned"
        taskStatus = taskmanager1.TaskStatus("meet");
      //  Assert.assertEquals("Task should be assigned", "Assigned", taskStatus);
        }

    @When("the chef marks the task as {string}")
    public void theChefMarksTheTaskAs(String arg0) {
        boolean isCompleted = chef1.completeTask("chef2", "meet");
        Assert.assertTrue("Task should be marked as completed", isCompleted);


        taskmanager1.updateTaskStatus("meet", "Completed");
    }

    @Then("the system should update the task status in the kitchen dashboard")
    public void theSystemShouldUpdateTheTaskStatusInTheKitchenDashboard(){
        String taskStatus = taskmanager1.TaskStatus("meet");
        Assert.assertEquals("Task status should be updated to 'Completed'", "Completed", taskStatus);
}

@And("the kitchen manager should be able to see the updated progress")
    public void theKitchenManagerShouldBeAbleToSeeTheUpdatedProgress() {
    String taskStatus = taskmanager1.TaskStatus("meet");
    Assert.assertEquals("Kitchen Manager should see 'Completed' status", "Completed", taskStatus);
    }


}

