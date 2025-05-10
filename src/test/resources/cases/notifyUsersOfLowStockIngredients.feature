Feature: Notify users of low-stock ingredients

  Scenario: System detects a low-stock ingredient
    Given an ingredient quantity falls below the threshold
    When the system checks inventory levels
    Then it should mark the ingredient as low in stock

  Scenario: Kitchen manager receives alert for low-stock
    Given there are low-stock ingredients in the inventory
    When the system generates alerts
    Then the kitchen manager should receive a notification about them

  Scenario: Multiple low-stock ingredients are reported
    Given multiple ingredients are below the stock threshold
    When the system checks stock levels
    Then it should generate an alert for each low-stock ingredient

