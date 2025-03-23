Feature: Track past orders and personalized meal plans

  Scenario: Customer Views Past Meal Orders
Given a customer has previously placed meal orders
When they navigate to their order history page
Then the system displays a list of their past meal orders
And the customer can reorder any past meal with a single click

Scenario: Chef Recommends a Personalized Meal Plan
Given a chef wants to suggest a meal plan,
When they access the customer’s order history,
Then they can view past preferences and suggest a suitable plan.

Scenario: System Administrator Stores and Retrieves Order History
Given the system stores customer order history
When an administrator requests customer order data
Then the system retrieves and displays order history trends
And the administrator can analyze data to improve service offerings