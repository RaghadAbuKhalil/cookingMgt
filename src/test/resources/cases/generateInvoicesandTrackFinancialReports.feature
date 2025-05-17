Feature: Generate Invoices and Track Financial Reports

  Scenario: Customer receives an invoice for a completed order
    Given a customer has placed an order and the order is completed
    When the order is completed
    Then the system should generate an invoice for the customer which itemized details of the meal and the total price

    And the system should send the invoice to the customer's email

  Scenario: Generate a financial report for daily sales
    Given a system administrator wants to track the sales performance of the day
    When the administrator requests a daily sales report
    Then the system should generate a report showing the total revenue for the day
    And the report should include itemized sales data for each meal type
    And the system should display the number of orders and total revenue

  Scenario: Generate a financial report for monthly sales analysis
    Given a system administrator wants to analyze sales for the current month
    When the administrator requests a monthly financial report
    Then the system should generate a report showing the total revenue for the month
    And the report should include breakdowns of revenue by meal type and discounts applied
    And the system should display trends in customer orders, including most ordered meals and revenue sources
