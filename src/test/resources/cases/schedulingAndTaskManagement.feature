Feature: Scheduling and Task Management

  Scenario: Assign tasks to chefs based on workload and expertise
    Given there are available chefs in the kitchen each chef has a defined workload and expertise level
    When the kitchen manager assigns the task "Prepare Vegan Salad"
    Then the system should assign the task to the chef with the least workload and required expertise "Advanced Level"
    And the chef should receive a notification about the new task

  Scenario: Chef receives a notification about the assigned task
    Given a chef has been assigned a new task "Prepare Vegan Salad"
    When the system sends a notification to the chef
    Then the chef should see the task notification in their task list
    And they should be able to acknowledge the task

  Scenario: Chef marks the task as completed
    Given a chef has started working on the task "Prepare Vegan Salad"
    When the chef marks the task as "Completed"
    Then the system should update the task status in the kitchen dashboard
    And the kitchen manager should be able to see the updated progress
