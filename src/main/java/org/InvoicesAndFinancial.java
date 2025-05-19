package org;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.logging.Logger;


public class InvoicesAndFinancial {
    private static final Logger logger = Logger.getLogger(InvoicesAndFinancial.class.getName());
String mealName="meal_name";
    public void generateInvoice(int orderId) {
        String query = "SELECT meal_name, price, status, order_date FROM ORDERS WHERE order_id = ?";
        String insertInvoice = "INSERT INTO INVOICES (order_id, meal_name, price, quantity, total_price, status, order_date) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt1 = conn.prepareStatement(query);
             PreparedStatement stmt2 = conn.prepareStatement(insertInvoice)) {

            stmt1.setInt(1, orderId);
            ResultSet rs = stmt1.executeQuery();

            if (rs.next()) {
                int quantity = 1;
                double price = rs.getDouble("price");
                double totalPrice = price * quantity;

                stmt2.setInt(1, orderId);
                stmt2.setString(2, rs.getString(mealName));
                stmt2.setDouble(3, price);
                stmt2.setInt(4, quantity);
                stmt2.setDouble(5, totalPrice);
                stmt2.setString(6, rs.getString("status"));
                stmt2.setString(7, rs.getString("order_date"));
                stmt2.executeUpdate();

                System.out.println("Invoice stored in database for order #" + orderId);
                displayInvoice(orderId);
            } else {
                System.out.println("Order not found!");
            }

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }



    public String displayInvoice(int orderId) {
        String query = "SELECT meal_name, price, quantity, total_price, status FROM INVOICES WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String mealName1 = rs.getString(mealName);
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("total_price");
                String status = rs.getString("status");

                String message = "=== Invoice ===\n"
                        + "Meal: " + mealName1 + "\n"
                        + "Price: " + price + "\n"
                        + "Quantity: " + quantity + "\n"
                        + "Total: " + totalPrice + "\n"
                        + "Status: " + status;

                return message;
            } else {

                System.out.println("No invoice found for orderId: " + orderId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }








    public String getCustomerEmail(int customerId) {
        String email = null;
        String qu = "SELECT email FROM customer_preferences WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(qu)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        return email;
    }




    public static boolean sendInvoiceEmail(String toEmail, String subject, String body) {

        final String fromEmail = "heba14.abu.soud@gmail.com";
        final String password = "cmvl lysr mynr avdb";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
            msg.setSubject(subject);
            msg.setText(body);

            Transport.send(msg);
            System.out.println("Email sent successfully to " + toEmail);
            return true;
        } catch (MessagingException e) {
            logger.warning(e.getMessage());
        }
        return false;
    }


    public double calculateDailyRevenue(String date) {

        double totalRevenue = 0;
        String query = "SELECT SUM(price * quantity) AS total FROM orders WHERE order_date = ? AND status = 'Completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalRevenue = rs.getDouble("total");
                logger.info("Total revenue for date " + date + " is: $" + totalRevenue);
            }
        } catch (SQLException e) {
            logger.severe("Error calculating daily revenue: " + e.getMessage());
        }

        return totalRevenue;


    }
    public Map<String, Integer> getItemMealSales(String date) {
        Map<String, Integer> mealSales = new HashMap<>();
        String query = "SELECT meal_name, SUM(quantity) AS total_sold FROM orders WHERE order_date = ? AND status = 'Completed' GROUP BY meal_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String mealName1 = rs.getString(mealName);
                int quantity = rs.getInt("total_sold");
                mealSales.put(mealName1, quantity);
                logger.info("Meal: " + mealName1 + ", Quantity Sold: " + quantity);
            }
        } catch (SQLException e) {
            logger.severe("Error fetching itemized meal sales: " + e.getMessage());
        }

        return mealSales;
    }
    public int getOrderCountForDay(String date) {
        int count = 0;
        String query = "SELECT COUNT(*) AS order_count FROM orders WHERE order_date = ? AND status = 'Completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                count = rs.getInt("order_count");
                logger.info("Total completed orders for " + date + ": " + count);
            }
        } catch (SQLException e) {
            logger.severe("Error counting orders: " + e.getMessage());
        }

        return count;
    }


    public double calculateMonthlyRevenue(String month, String year) {
        double totalRevenue = 0;
        String sql = "SELECT SUM(price * quantity) FROM orders " +
                "WHERE strftime('%m', order_date) = ? AND strftime('%Y', order_date) = ? AND status = 'Completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.format("%02d", Month.valueOf(month.toUpperCase()).getValue()));
            stmt.setString(2, year);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                totalRevenue = rs.getDouble(1);
            }
        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
            //JOptionPane.showMessageDialog(null, "Total Revenue for the Month: $" + totalRevenue, "Monthly Revenue", JOptionPane.INFORMATION_MESSAGE);
        System.out.println("Total Revenue for the Month: $" + totalRevenue);

        return totalRevenue;
    }

    public Map<String, Double> getRevenueBreakdownByMealName(String month, String year) {
        Map<String, Double> revenueByType = new HashMap<>();
        String sql = "SELECT meal_name, SUM(price * quantity) AS total FROM orders " +
                "WHERE strftime('%m', order_date) = ? AND strftime('%Y', order_date) = ? AND status = 'Completed' GROUP BY meal_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.format("%02d", Month.valueOf(month.toUpperCase()).getValue()));
            stmt.setString(2, year);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                revenueByType.put(rs.getString(mealName), rs.getDouble("total"));
            }

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
        System.out.println(revenueByType);
        return revenueByType;
    }

    public Map<String, Integer> getMostOrderedMeals(String month, String year) {
        Map<String, Integer> mealOrders = new HashMap<>();
        String sql = "SELECT meal_name, SUM(quantity) AS total_ordered FROM orders " +
                "WHERE strftime('%m', order_date) = ? AND strftime('%Y', order_date) = ? AND status = 'Completed' " +
                "GROUP BY meal_name ORDER BY total_ordered DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, String.format("%02d", Month.valueOf(month.toUpperCase()).getValue()));
            stmt.setString(2, year);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                mealOrders.put(rs.getString(mealName), rs.getInt("total_ordered"));
            }

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }

        return mealOrders;
    }

    public void checkAndSendDeliveryReminders() {
        String sql = "SELECT order_id, customer_id, order_date FROM orders WHERE status != 'Completed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("order_id");
                int custId = rs.getInt("customer_id");
                String dateStr = rs.getString("order_date");

                try {
                    LocalDate orderDate = LocalDate.parse(dateStr); // صيغة yyyy-MM-dd فقط
                    if (ChronoUnit.HOURS.between(LocalDate.now().atStartOfDay(), orderDate.atStartOfDay()) <= 24) {
                        String email = getCustomerEmail(custId);
                        sendInvoiceEmail(email, "Upcoming Delivery Reminder", "Your meal will be delivered tomorrow!");
                        recordReminderSent(custId);
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Invalid date format for order_id=" + id + ": " + dateStr);
                }
            }

        } catch (SQLException e) {
            logger.warning(e.getMessage());
        }
    }


    private final Set<Integer> remindedCustomers = new HashSet<>();

    public void recordReminderSent(int customerId) {
        remindedCustomers.add(customerId);
    }

    public boolean wasReminderSentToCustomer(int customerId) {
        return remindedCustomers.contains(customerId);
    }


}