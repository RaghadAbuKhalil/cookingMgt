package cookingTest;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.*;
import org.database.DatabaseSetup;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class schedulingAndTaskManagement {

   private TaskManager taskmanager1;
    private Chef chef1 ;
    private NotificationService notification1 = NotificationService.getInstance();
    private KitchenManagerService kitchenManager1  = KitchenManagerService.getInstance();
    int  chosenChef;
    String mealname;
    List notificationList= new ArrayList<String>();
    int chefid1=1;
    int chefid2=2;
    int chefid;
    @Before
    public void setup() {
        DatabaseSetup.setupDatabase();
        this.chef1 = Chef.getInstance();
        this.taskmanager1= TaskManager.getInstance();


    }


    @Given("there are available chefs in the kitchen each chef has a defined workload and expertise level")
    public void thereAreAvailableChefsInTheKitchenEachChefHasADefinedWorkloadAndExpertiseLevel() {
        chef1.addChef("chef1", "Beginner");
        chef1.setChefJobload( chefid1,4);
        chef1.addChef("chef2",  "Advanced Level");
        chef1.setChefJobload(chefid2,3);
        chef1.printAllChefs();

    }

    @When("the kitchen manager assigns the task {string}")
    public void theKitchenManagerAssignsTheTask(String arg0) {
       mealname=arg0;

    }

    @Then("the system should assign the task to the chef with the least workload and required expertise {string}")
    public void theSystemShouldAssignTheTaskToTheChefWithTheLeastWorkloadAndRequiredExpertise(String arg0) {
         int chefid=kitchenManager1.assignTask(mealname,arg0);
        Assert.assertEquals( chefid,chefid2 );

    }


    @And("the chef should receive a notification about the new task")
    public void theChefShouldReceiveANotificationAboutTheNewTask() {
       String notificationmsg = notification1.getChefNotifications(chefid);

        Assert.assertEquals("Chef receive notification", notificationmsg, mealname);

        System.out.println("Notification message: " + notificationmsg);
    }

    @Given("a chef has been assigned a new task {string}")
    public void aChefHasBeenAssignedANewTask(String arg0) {

mealname=arg0;
    }

    @When("the system sends a notification to the chef")
    public void theSystemSendsANotificationToTheChef() {

    }

    @Then("the chef should see the task notification in their task list")
    public void theChefShouldSeeTheTaskNotificationInTheirTaskList() {

        notificationList= notification1.getNotificationsListForChef( chefid);
        Assert.assertNotNull("notification in their task list",notificationList.contains(mealname));

    }

    @And("they should be able to acknowledge the task")
    public void theyShouldBeAbleToAcknowledgeTheTask() {

        chef1.taskInProgress(chefid, mealname);
        String status = taskmanager1.TaskStatus(mealname,chefid);
        Assert.assertEquals("started working on the task",status, "In Progress");



    }

    @Given("a chef has started working on the task {string}")
    public void aChefHasStartedWorkingOnTheTask(String arg0) {
    mealname=arg0;
        chef1.taskInProgress(chefid, mealname);
        String status = taskmanager1.TaskStatus(mealname,chefid);
        Assert.assertEquals("chef started working on the task",status, "In Progress");


        }

    @When("the chef marks the task as {string}")
    public void theChefMarksTheTaskAs(String arg0) {
      chef1.completeTask(chefid, mealname);

    }

    @Then("the system should update the task status in the kitchen dashboard")
    public void theSystemShouldUpdateTheTaskStatusInTheKitchenDashboard(){
        String status = taskmanager1.TaskStatus(mealname,chefid);
        Assert.assertEquals("the chef marks the task as completed",status, "Completed");
}

@And("the kitchen manager should be able to see the updated progress")
    public void theKitchenManagerShouldBeAbleToSeeTheUpdatedProgress() {
    String taskStatus = kitchenManager1.getTaskStatusForKitchenManager(mealname,chefid);
    Assert.assertEquals("Kitchen Manager should see 'Completed' status", "Completed", taskStatus);
    }


}

