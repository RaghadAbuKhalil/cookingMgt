package org;

import org.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class KitchenManagerService {
    private static KitchenManagerService instance;

    TaskManager  taskAssignment;
    private NotificationService notificationService;
  private Connection conn;
    public static KitchenManagerService getInstance() {
        if (instance == null) {
            synchronized (KitchenManagerService.class) {
                if (instance == null) {
                    instance = new KitchenManagerService();
                }
            }
        }
        return instance;
    }



    public KitchenManagerService() {
        taskAssignment =  new TaskManager();
        notificationService=new NotificationService();
        try {
            conn =   DatabaseConnection.getConnection() ;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }





    public int assignTask(String taskName,  String requiredExpertise) {
        int chefid = taskAssignment.assignTaskToChefByExpertise(taskName, requiredExpertise);
        if (chefid == -1) System.out.println("No chef found with expertise: " + requiredExpertise);
        notificationService.sendNotification(chefid,taskName);

        return chefid;
    }
    public List<String> getNotifications() {
        List<String> notifications = new ArrayList<>();
        String query = "SELECT message FROM Kitchen_notifications ";
        try (
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            while (rs.next()) {
                notifications.add(rs.getString(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return notifications;
    }



}
