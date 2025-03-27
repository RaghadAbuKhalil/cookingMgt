Feature: Suggest Ingredient Substitutions Based on Dietary Restrictions

  Scenario: Suggest alternative for an allergen
    Given a customer has a recorded allergy to nuts
    When they try to add almonds to their custom meal
    Then the system should suggest sunflower seeds as an alternative
    And notify the chef about the substitution

  Scenario: Recommend a substitute for an unavailable ingredient
    Given a customer selects mushrooms for their pasta dish
    When the system detects that mushrooms are out of stock
    Then the system should suggest zucchini as an alternative
    And ask the customer to confirm the substitution before proceeding

  Scenario: Ensure ingredient substitutions follow dietary restrictions
    Given a customer follows a vegan diet
    When they select an ingredient that is not vegan, such as butter
    Then the system should suggest a vegan alternative like margarine or olive oil
    And confirm the change with the customer

