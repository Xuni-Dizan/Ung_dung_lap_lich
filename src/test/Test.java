package test;
import javax.swing.SwingUtilities;
import java.util.Date;
import java.util.List;
import view.TaskManager;
import view.Task;

public class Test {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Tạo một instance của TaskManager
                TaskManager taskManager = new TaskManager();

                // Kiểm tra tải dữ liệu từ file
                taskManager.loadFromFile("tasks_data.dat");
                System.out.println("Dữ liệu công việc ban đầu:");
                printTasks(taskManager);

                // Kiểm tra thêm công việc
                System.out.println("\nThêm công việc mới:");
                Task newTask = new Task("Làm bài tập Java", "14:00", "16:00", "Doing");
                String key = "Daily_" + new Date(); // Key cho tab "Daily"
                taskManager.addTask(key, newTask);
                taskManager.saveToFile("tasks_data.dat");
                printTasks(taskManager);

                // Kiểm tra sửa công việc
                System.out.println("\nSửa công việc:");
                Task updatedTask = new Task("Làm bài tập Java (chỉnh sửa)", "15:00", "17:00", "Done");
                taskManager.updateTask(key, 0, updatedTask);
                taskManager.saveToFile("tasks_data.dat");
                printTasks(taskManager);

                // Kiểm tra xóa công việc
                System.out.println("\nXóa công việc:");
                taskManager.removeTask(key, updatedTask);
                taskManager.saveToFile("tasks_data.dat");
                printTasks(taskManager);

                // Mở giao diện GUI
                new CalendarGUI();

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void printTasks(TaskManager taskManager) {
        taskManager.getTasksByKey().forEach((key, tasks) -> {
            System.out.println("Key: " + key);
            for (Task task : tasks) {
                System.out.println("- " + task.getName() + " [" + task.getStartTime() + " - " + task.getEndTime() + "] " + task.getStatus());
            }
        });
    }
}
