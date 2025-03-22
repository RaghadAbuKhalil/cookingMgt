Feature: Allow Customers to Create Custom Meal Requests

  Scenario : Customer Creates a Custom Meal
    Given a customer wants to customize their meal
    When they select ingredients
    Then the system stores their custom meal request and ensures it meets their dietary needs
  Scenario : System Validates Ingredient Combinations
    Given a customer has selected ingredients for a custom meal
    When they attempt to finalize their order
    Then the system verifies that the ingredients are available and compatible
    And if an ingredient is unavailable or incompatible, the system suggests an alternative
  Scenario : Customer Reviews and Edits a Custom Meal Before Ordering
    Given a customer has created a custom meal
    When they review their selected ingredients
    Then they can edit, remove, or add new ingredients before confirming their order