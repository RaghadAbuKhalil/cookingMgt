Feature:Send reminders for upcoming orders and deliveries

  Scenario: Customer receives a reminder for an upcoming delivery
    Given a customer has a scheduled meal delivery for tomorrow
    And the customer's email is registered in the system
    When the system checks for upcoming deliveries within 24 hours
    Then the customer should receive an email reminder about the delivery

  Scenario: Chef receives a notification for scheduled cooking task
    Given a cooking task is scheduled for tomorrow
    And the chef is assigned to that task
    When the system runs the daily reminder job
    Then the chef should receive a notification with the meal details and preparation time

  Scenario: No reminder is sent if there are no upcoming tasks or deliveries
    Given there are no deliveries or cooking tasks scheduled in the next 24 hours
    When the system performs the reminder check
    Then no notifications or emails should be sent to customers or chefs