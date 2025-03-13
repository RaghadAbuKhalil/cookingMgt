Feature: Store dietary preferences and allergies

  Scenario: Customer Adds Dietary Preferences
    Given a customer wants to personalize their meal selection
    When they enter their dietary preferences and allergies into the system
    Then the system stores the preferences and ensures future orders comply with them

  Scenario: Chef Views Customer Preferences
    Given a customer has saved their dietary preferences
    When a chef prepares a meal for that customer
    Then the system displays their preferences to the chef for customization

  Scenario: System Prevents Unwanted Ingredients
    Given a customer has an allergy
    When they try to order a dish containing his allergy
    Then the system warns them and suggests an alternative
