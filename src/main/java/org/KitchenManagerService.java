package org;

public class KitchenManagerService {
    private static KitchenManagerService instance;

    TaskManager  taskAssignment;
    private NotificationService notificationService;

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
    }


    public String getTaskProgress(String taskName) {
        return taskAssignment.TaskStatus(taskName);
    }


    public int assignTask(String taskName,  String requiredExpertise) {
        int chefid = taskAssignment.assignTaskToChefByExpertise(taskName, requiredExpertise);
        if (chefid == -1) System.out.println("No chef found with expertise: " + requiredExpertise);
        notificationService.sendNotification(chefid,taskName);

        return chefid;
    }}
