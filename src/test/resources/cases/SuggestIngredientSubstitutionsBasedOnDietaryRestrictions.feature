Feature: Suggest Ingredient Substitutions Based on Dietary Restrictions

  As a customer, I want the system to suggest alternative ingredients if an ingredient is unavailable or does not fit my dietary restrictions, so that I can enjoy my meal without compromising my health.

  As a chef, I want to receive an alert when an ingredient substitution is applied so that I can approve or adjust the final recipe.

  # Scenario 1: Suggest Ingredient Substitutes Based on Availability
  Scenario Outline: Suggest an alternative for an out-of-stock ingredient
    Given a customer selects "<ingredient>" for their custom meal
    And "<ingredient>" is out of stock
    When the system checks for a suitable alternative
    Then it should suggest  available alternative
    And display a message: "<ingredient> is unavailable, would you like to try "

    Examples:
       |ingredient|
       | tomato|
        |Chicken|
       |Cheese |


  # Scenario 2: Suggest Ingredient Substitutes Based on Dietary Restrictions
  Scenario Outline: Suggest alternative ingredients based on dietary restrictions
    Given a customer selects "<ingredient>" which is restricted for their dietary preferences
    When the system checks for a suitable alternative
    Then it should suggest  ingredient that matches their dietary preferences
    And display a message: "<ingredient> is unavailable, would you like to try "

    Examples:
      | ingredient     |
      | Fish           |
      | Cheese         |
      | Chicken        |


  # Scenario 3: Suggest Ingredient Substitutes for Allergic Reactions
  Scenario Outline: Suggest an alternative for an allergenic ingredient
    Given a customer selects "<ingredient>" which is an allergen for them
    When the system checks for a suitable alternative
    Then it should suggest "<alternative>" that does not trigger any allergic reactions
    And display a message: "<ingredient> is unavailable, would you like to try "

    Examples:
      | ingredient |
      | Strawberry   |
      | Peanuts      |
      | Milk         |

