Feature: AI Recipe Recommendation

  Scenario: Recommend a vegan recipe based on available ingredients and time
    Given the customer has dietary restriction "Vegan"
    And the available ingredients are "Tomatoes", "Basil", "Pasta"
    And the available time is 30 minutes
    When the recipe assistant is asked to recommend a recipe
    Then the assistant should recommend "Spaghetti with Tomato Sauce"
