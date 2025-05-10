Feature: Inventory Tracking and Restocking Suggestions

  Scenario: Display available quantity of an ingredient
    Given the kitchen manager is logged into the system
    When they open the inventory page
    Then they should see the current quantity of each ingredient

  Scenario: Alert manager when ingredient is low
    Given an ingredient quantity is below the minimum threshold
    When the system checks the stock levels
    Then it should send an alert to the kitchen manager

  Scenario: Suggest restocking automatically
    Given an ingredient quantity is low
    When the kitchen manager opens the inventory page
    Then the system should suggest restocking that ingredient
