// src/view/TodayPanel.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import static javax.swing.BorderFactory.*;

public class TodayPanel extends JPanel {

    private String tabTitle;
    private JDateChooser dateChooser;
    private TaskManager taskManager;
    private LocalDate localDate;
    private DefaultTableModel tableModel;
    private Date selectedDate;

    public TodayPanel(String tabTitle, Date selectedDate, TaskManager taskManager) {
        this.tabTitle = tabTitle;
        this.taskManager = taskManager;
        this.selectedDate = selectedDate;
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        this.localDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Top Panel for Navigation and Date
        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel topPanel_center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel topPanel_right = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel topPanel_left = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(topPanel_center, BorderLayout.CENTER);
        topPanel.add(topPanel_right, BorderLayout.EAST);
        topPanel.add(topPanel_left, BorderLayout.WEST);
        JButton btnYesterday = new JButton("Yesterday");
        JButton btnToday = new JButton("Today");
        JButton btnTomorrow = new JButton("Tomorrow");
        JButton btnAddTask = new JButton("Add task");
        btnAddTask.addActionListener(e -> openAddTaskDialog());

        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 35));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setToolTipText("Chọn ngày để lập lịch");
        dateChooser.setDate(selectedDate);
        dateChooser.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.setTime((Date) evt.getNewValue());
                localDate = LocalDate.of(selectedCalendar.get(Calendar.YEAR),
                                         selectedCalendar.get(Calendar.MONTH) + 1,
                                         selectedCalendar.get(Calendar.DAY_OF_MONTH));
                refreshTaskTable();
            }
        });
        topPanel_center.add(btnYesterday);
        topPanel_center.add(dateChooser);
        topPanel_center.add(btnTomorrow);
        topPanel_right.add(btnToday);
        topPanel_left.add(btnAddTask);
        add(topPanel, BorderLayout.NORTH);

        // Task Table Panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(createTitledBorder("List Task"));

        // Create Table Columns
        String[] columnNames = {"", "Task", "Start Time", "End Time", "Status", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Boolean.class; 
                    case 5:
                    case 6:
                        return JButton.class; 
                    default:
                        return String.class; 
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; 
            }
        };

        String key = generateKey(tabTitle, localDate);
        List<Task> tasks = taskManager.getTasksForKey(key);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                false,
                task.getName(),
                task.getStartTime(),
                task.getEndTime(),
                task.getStatus(),
                "Edit",
                "Delete"
            });
        }

        JTable taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);

        taskTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JButton(), taskManager, key, tableModel));
        taskTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JButton(), taskManager, key, tableModel));

        taskTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        taskTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Done", "Missed", "Doing", "Coming"});
        taskTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        JScrollPane scrollPane = new JScrollPane(taskTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // JButton btnAddTaskTable = new JButton("Add Task");
        // btnAddTaskTable.addActionListener(e -> openAddTaskDialog());
        // tablePanel.add(btnAddTaskTable, BorderLayout.SOUTH);

        add(tablePanel, BorderLayout.CENTER);

        // Add Button Actions for navigation
        btnYesterday.addActionListener(e -> changeDateByOffset(-1));
        btnTomorrow.addActionListener(e -> changeDateByOffset(1));
        btnToday.addActionListener(e -> resetToToday());
    }

    // Phương thức mở dialog để thêm công việc mới
    private void openAddTaskDialog() {
        JTextField taskNameField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField endTimeField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Done", "Missed", "Doing", "Coming"});

        Object[] message = {
            "Task Name:", taskNameField,
            "Start Time:", startTimeField,
            "End Time:", endTimeField,
            "Status:", statusComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Add New Task", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = taskNameField.getText().trim();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();

            if (name.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin công việc.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Task newTask = new Task(name, startTime, endTime, status);
            taskManager.addTask(generateKey(tabTitle, localDate), newTask);
            tableModel.addRow(new Object[]{
                false,
                name,
                startTime,
                endTime,
                status,
                "Edit",
                "Delete"
            });
            taskManager.saveToFile("tasks_data.dat"); // Lưu ngay
        }
    }

    // Thay đổi ngày dựa trên offset (trước hoặc sau)
    private void changeDateByOffset(int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateChooser.getDate());
        cal.add(Calendar.DAY_OF_MONTH, offset);
        dateChooser.setDate(cal.getTime());
        localDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        refreshTaskTable();
    }

    // Đặt lại ngày về hôm nay
    private void resetToToday() {
        dateChooser.setDate(Calendar.getInstance().getTime());
        Calendar cal = Calendar.getInstance();
        localDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        refreshTaskTable();
    }

    // Làm mới bảng công việc
    private void refreshTaskTable() {
        tableModel.setRowCount(0); // Xóa tất cả row hiện tại
        String key = generateKey(tabTitle, localDate);
        List<Task> tasks = taskManager.getTasksForKey(key);
        for (Task task : tasks) {
            tableModel.addRow(new Object[]{
                false,
                task.getName(),
                task.getStartTime(),
                task.getEndTime(),
                task.getStatus(),
                "Edit",
                "Delete"
            });
        }
    }

    // Phương thức để tạo khóa kết hợp tabTitle và date
    private String generateKey(String tabTitle, LocalDate date) {
        return tabTitle + "_" + date.toString();
    }

    // Renderer for JButton in JTable (Custom Button Renderer)
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String label) {
            setText(label);
            setFocusPainted(false);
            setBackground(Color.LIGHT_GRAY);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this; // Trả về nút khi cần hiển thị trong bảng
        }
    }

    // Editor cho các nút trong JTable
    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String label;
        private TaskManager taskManager;
        private String key;
        private DefaultTableModel tableModel;
        private int row;

        public ButtonEditor(JButton button, TaskManager taskManager, String key, DefaultTableModel tableModel) {
            this.button = button;
            this.taskManager = taskManager;
            this.key = key;
            this.tableModel = tableModel;
            this.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    if (label.equals("Edit")) {
                        editTask(row);
                    } else if (label.equals("Delete")) {
                        deleteTask(row);
                    }
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.label = (String) value;
            this.row = row;
            button.setText(label);
            return button;
        }

        private void editTask(int row) {
            String currentName = (String) tableModel.getValueAt(row, 1);
            String currentStart = (String) tableModel.getValueAt(row, 2);
            String currentEnd = (String) tableModel.getValueAt(row, 3);
            String currentStatus = (String) tableModel.getValueAt(row, 4);

            JTextField taskNameField = new JTextField(currentName);
            JTextField startTimeField = new JTextField(currentStart);
            JTextField endTimeField = new JTextField(currentEnd);
            JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Done", "Missed", "Doing", "Coming"});
            statusComboBox.setSelectedItem(currentStatus);

            Object[] message = {
                "Task Name:", taskNameField,
                "Start Time:", startTimeField,
                "End Time:", endTimeField,
                "Status:", statusComboBox
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Edit Task", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = taskNameField.getText().trim();
                String startTime = startTimeField.getText().trim();
                String endTime = endTimeField.getText().trim();
                String status = (String) statusComboBox.getSelectedItem();

                if (name.isEmpty() || startTime.isEmpty() || endTime.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin công việc.", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Cập nhật Task trong TaskManager
                List<Task> tasks = taskManager.getTasksForKey(key);
                if(row >= tasks.size()){
                    JOptionPane.showMessageDialog(null, "Error: Task không tồn tại.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Task task = tasks.get(row);
                task.setName(name);
                task.setStartTime(startTime);
                task.setEndTime(endTime);
                task.setStatus(status);

                // Cập nhật Table Model
                tableModel.setValueAt(name, row, 1);
                tableModel.setValueAt(startTime, row, 2);
                tableModel.setValueAt(endTime, row, 3);
                tableModel.setValueAt(status, row, 4);

                taskManager.saveToFile("tasks_data.dat"); // Lưu ngay
            }
        }

        private void deleteTask(int row) {
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn xóa công việc này không?", "Delete Task", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Xóa Task từ TaskManager
                List<Task> tasks = taskManager.getTasksForKey(key);
                if(row >= tasks.size()){
                    JOptionPane.showMessageDialog(null, "Error: Task không tồn tại.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Task task = tasks.get(row);
                taskManager.removeTask(key, task);

                // Xóa row khỏi Table Model
                tableModel.removeRow(row);

                taskManager.saveToFile("tasks_data.dat"); // Lưu ngay
            }
        }
    }
}