// src/view/TaskManager.java
package view;

import java.io.*;
import java.util.*;

public class TaskManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<String, List<Task>> tasksByKey = new HashMap<>();

    // Tải dữ liệu từ tệp
    public void loadFromFile(String filePath) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            @SuppressWarnings("unchecked")
            Map<String, List<Task>> loadedMap = (Map<String, List<Task>>) ois.readObject();
            tasksByKey = loadedMap;
        } catch (Exception e) {
            tasksByKey = new HashMap<>();
        }
    }

    // Lưu dữ liệu vào tệp
    public void saveToFile(String filePath) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(tasksByKey);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Thêm công việc với khóa kết hợp
    public void addTask(String key, Task task) {
        tasksByKey.computeIfAbsent(key, k -> new ArrayList<>()).add(task);
    }

    // Lấy danh sách công việc với khóa kết hợp
    public List<Task> getTasksForKey(String key) {
        return tasksByKey.getOrDefault(key, new ArrayList<>());
    }

    // Xóa công việc với khóa kết hợp
    public void removeTask(String key, Task task) {
        List<Task> tasks = tasksByKey.get(key);
        if (tasks != null) {
            tasks.remove(task);
            if (tasks.isEmpty()) {
                tasksByKey.remove(key);
            }
        }
    }

    // Cập nhật công việc với khóa kết hợp
    public void updateTask(String key, int index, Task updatedTask) {
        List<Task> tasks = tasksByKey.get(key);
        if (tasks != null && index >= 0 && index < tasks.size()) {
            tasks.set(index, updatedTask);
        }
    }

    public Map<String, List<Task>> getTasksByKey() {
        return tasksByKey;
    }

    // Thêm phương thức để clear tất cả tasks (sử dụng cho "Reset All")
    public void clearAllTasks() {
        tasksByKey.clear();
    }

    /**
     * Kiểm tra xem tên công việc đã tồn tại trong khóa nhất định chưa
     *
     * @param key  Khóa kết hợp (ví dụ: "Daily_2023-10-05")
     * @param name Tên công việc cần kiểm tra
     * @param excludeIndex chỉ số của công việc cần loại trừ (sử dụng khi chỉnh sửa)
     * @return     true nếu tên đã tồn tại, false nếu chưa
     */
    public boolean isTaskNameDuplicate(String key, String name, int excludeIndex) {
        List<Task> tasks = tasksByKey.get(key);
        if (tasks != null) {
            for (int i = 0; i < tasks.size(); i++) {
                if (i == excludeIndex) continue; // Bỏ qua công việc hiện tại khi chỉnh sửa
                if (tasks.get(i).getName().equalsIgnoreCase(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Lấy danh sách các trạng thái hợp lệ cho công việc
     *
     * @return Danh sách các trạng thái hợp lệ
     */
    public List<String> getValidStatuses() {
        return Arrays.asList("Hoàn thành", "Đang tiến hành", "Chưa bắt đầu", "Quá hạn");
    }
}