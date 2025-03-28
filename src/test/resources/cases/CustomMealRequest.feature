Feature: Allow Customers to Create Custom Meal Requests

  Scenario: Successful custom meal creation
    Given a customer wants to create a custom meal
    When they select available ingredients like grilled chicken, brown rice, and broccoli
    Then the system should validate the selection and allow the order to proceed
    And display a confirmation message

  Scenario: Invalid or restricted ingredient selection
    Given a customer wants to customize their meal
    When they select incompatible ingredients
    When they select an ingredient that is an allergen for them
    When they select an ingredient that does not match their dietary preferences
    Then the system should display an error message


  Scenario: Selecting an out-of-stock ingredient
    Given a customer selects an ingredient for their custom meal
    When the system detects that the ingredient is out of stock
    Then it should notify the customer

