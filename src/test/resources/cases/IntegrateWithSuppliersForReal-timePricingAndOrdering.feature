Feature: Integrate with suppliers for real-time pricing and ordering

  Scenario: View real-time price of an ingredient
    Given the kitchen manager is logged in
    When the manager checks the price of "tomato"
    Then the system shows the current price from the supplier

  Scenario: Auto-generate purchase order when stock is low
    Given the stock of "tomato" is below the minimum level
    When the system checks inventory
    Then it creates a purchase order for "tomato"
    And sends it to the supplier

  Scenario: Recommend cheapest supplier
    Given "Olive Oil" is available from multiple suppliers
    When the manager wants to order "Olive Oil"
    Then the system recommends the supplier with the lowest price
