package org;

public class KitchenManagerService {
    private static KitchenManagerService instance;
    private TaskManager taskAssignment;


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



    public KitchenManagerService(TaskManager taskAssignment) {
        this.taskAssignment = taskAssignment;
    }

    public KitchenManagerService() {
    }

    // الحصول على تقدم المهمة في لوحة تحكم المطبخ
    public String getTaskProgress(String taskName) {
        return taskAssignment.TaskStatus(taskName);
    }
}
