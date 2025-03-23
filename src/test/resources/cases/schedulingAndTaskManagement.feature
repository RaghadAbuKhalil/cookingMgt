Feature: Scheduling and Task Management

  Scenario: Assign a task to a chef based on workload and expertise
    Given there are available chefs in the kitchen
    And each chef has a defined workload and expertise level
    When the kitchen manager assigns the task "Prepare Grilled Salmon"
    Then the system should assign the task to the chef with the least workload and required expertise
    And the chef should receive a notification about the new task


  Scenario: Notify the chef of a newly assigned task
    Given a chef has been assigned a new task "Bake Chocolate Cake"
    When the system sends a notification to the chef
    Then the chef should see the task notification in their task list
    And they should be able to acknowledge the task


  Scenario: Update task status and track progress
    Given a chef has started working on the task "Chop Vegetables"
    When the chef marks the task as "In Progress"
    Then the system should update the task status in the kitchen dashboard
    And the kitchen manager should be able to see the updated progress