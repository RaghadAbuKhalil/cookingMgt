package org;
import org.OrderHistoryService;
import org.TaskManager;
import org.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*public class InvoicesAndFinancial {
    OrderHistoryService order1;
    TaskManager taskmanager=new TaskManager();
    public InvoicesAndFinancial() {
    }

    public String addOrder(int customer_id,String mealname){
         int orderid=order1.storeOrder(customer_id,mealname);
        taskmanager.assignTaskToChef(orderid,mealname);
        String taskstatus = taskmanager.TaskStatus(mealname);
        taskmanager.updateTaskStatus(mealname,taskstatus);

return taskstatus;
    }
    public void generateInvoiceForCustomer(int customerId) {
        String sql = "SELECT meal_name, price FROM ORDERS WHERE customer_id = ? AND status = 'Completed'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String meal = rs.getString("meal_name");
                double price = rs.getDouble("price");
                System.out.println("Invoice for Customer " + customerId + ": " + meal + " - Price: $" + price);
                // يمكنك إضافة منطق إرسال الفاتورة عبر البريد الإلكتروني هنا
            } else {
                System.out.println("No completed order found for customer " + customerId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // الحصول على تقرير الإيرادات اليومي
    public double getDailyRevenue() {
        double totalRevenue = 0.0;
        String sql = "SELECT SUM(price) FROM ORDERS WHERE order_date = CURRENT_DATE AND status = 'Completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalRevenue = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRevenue;
    }*/
    /*  public double getDailyRevenue() {
        double totalRevenue = 0.0;
        String sql = "SELECT SUM(price) FROM ORDERS WHERE order_date = CURRENT_DATE AND status = 'Completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalRevenue = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRevenue;
    }

    // الحصول على تقرير الإيرادات الشهري
    public double getMonthlyRevenue() {
        double totalRevenue = 0.0;
        String sql = "SELECT SUM(price) FROM ORDERS WHERE strftime('%m', order_date) = strftime('%m', CURRENT_DATE) AND status = 'Completed'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                totalRevenue = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalRevenue;
    }

    // إرجاع تفاصيل المبيعات حسب نوع الوجبة
    public void getSalesByMealType() {
        String sql = "SELECT meal_name, SUM(price) FROM ORDERS WHERE status = 'Completed' GROUP BY meal_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String meal = rs.getString("meal_name");
                double sales = rs.getDouble(2);
                System.out.println("Meal: " + meal + ", Sales: $" + sales);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}*/


