// src/view/TaskManager.java
package view;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class TaskManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<LocalDate, List<Task>> tasksByDate = new HashMap<>();

    // Tải dữ liệu từ tệp
    public void loadFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            tasksByDate = (Map<LocalDate, List<Task>>) ois.readObject();
        } catch (Exception e) {
            tasksByDate = new HashMap<>(); // Nếu tệp không tồn tại hoặc lỗi
        }
    }

    // Lưu dữ liệu vào tệp
    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(tasksByDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thêm công việc
    public void addTask(LocalDate date, Task task) {
        tasksByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(task);
    }

    // Lấy danh sách công việc
    public List<Task> getTasksForDate(LocalDate date) {
        return tasksByDate.getOrDefault(date, new ArrayList<>());
    }

    // Xóa công việc
    public void removeTask(LocalDate date, Task task) {
        List<Task> tasks = tasksByDate.get(date);
        if (tasks != null) {
            tasks.remove(task);
            if (tasks.isEmpty()) {
                tasksByDate.remove(date);
            }
        }
    }

    // Cập nhật công việc
    public void updateTask(LocalDate date, int index, Task updatedTask) {
        List<Task> tasks = tasksByDate.get(date);
        if (tasks != null && index >= 0 && index < tasks.size()) {
            tasks.set(index, updatedTask);
        }
    }
}