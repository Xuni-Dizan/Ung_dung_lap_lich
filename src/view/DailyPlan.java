// src/view/DailyPlan.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static javax.swing.BorderFactory.*;

public class DailyPlan extends JFrame {

    private JDateChooser dateChooser;
    private TaskManager taskManager; // Thêm TaskManager
    private LocalDate localDate; // Ngày hiện tại
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane; // Thêm JTabbedPane
    private List<TodayPanel> todayPanels; // Danh sách các TodayPanel

    public DailyPlan(Date selectedDate, TaskManager taskManager) {
        this.taskManager = taskManager;
        Calendar cal = Calendar.getInstance();
        cal.setTime(selectedDate);
        this.localDate = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        createAndShowGUI(selectedDate);
    }

    private void createAndShowGUI(Date selectedDate) {
        // Set Look and Feel to "Nimbus" for better aesthetics
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create Main Frame
        setTitle("Daily Plan");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Sử dụng DISPOSE để không đóng toàn bộ ứng dụng
        setSize(800, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Tạo panel chính
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Thanh tab
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Daily", createTodayPanel()); // Tab
        tabbedPane.addTab("Word", new JPanel()); // Tab

        // Đọc danh sách tab và tiêu đề từ tệp
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tabs_data.dat"))) {
            @SuppressWarnings("unchecked")
            List<String> tabTitles = (List<String>) ois.readObject();
            for (String title : tabTitles) {
                addTodayPanel(title, selectedDate);
            }
        } catch (Exception ex) {
            // Nếu tệp không tồn tại hoặc lỗi, thêm tab "Daily" mặc định
            addTodayPanel("Daily", selectedDate);
        }

        // Thêm JPanel chứa nút "Add" vào phía trên JTabbedPane
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButtonPanel.setOpaque(false); 
        JButton btnAddTab = new JButton("+"); 
        btnAddTab.setPreferredSize(new Dimension(40, 40)); 
        btnAddTab.setBackground(new Color(52, 152, 219)); 
        btnAddTab.setForeground(Color.WHITE); 
        btnAddTab.setBorder(createLineBorder(Color.GRAY, 1, true));
        btnAddTab.setFocusPainted(false);
        btnAddTab.setToolTipText("Add New Tab"); 

        // Tạo sự kiện click cho nút "Add"
        btnAddTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tabTitle = JOptionPane.showInputDialog("Nhập tiêu đề cho tab mới:");
                if (tabTitle != null && !tabTitle.trim().isEmpty()) {
                    addTodayPanel(tabTitle, selectedDate);
                }
            }
        });

        // Thêm chức năng thay đổi title tab khi nhấn chuột phải
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int tabIndex = tabbedPane.indexAtLocation(e.getX(), e.getY());
                    if (tabIndex >= 0) {
                        String newTitle = JOptionPane.showInputDialog("Nhập tiêu đề mới:", tabbedPane.getTitleAt(tabIndex));
                        if (newTitle != null && !newTitle.trim().isEmpty()) {
                            tabbedPane.setTitleAt(tabIndex, newTitle);
                        }
                    }
                }
            }
        });

        addButtonPanel.add(btnAddTab);  // Thêm nút vào panel

        mainPanel.add(addButtonPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add the mainPanel to the JFrame
        add(mainPanel, BorderLayout.CENTER);

        // Make the frame visible
        setVisible(true);
    }

    // Phương thức để thêm một TodayPanel mới
    private void addTodayPanel(String tabTitle, Date selectedDate) {
        TodayPanel newPanel = new TodayPanel(selectedDate, taskManager);
        todayPanels.add(newPanel);
        tabbedPane.addTab(tabTitle, newPanel);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
    }

    

    private JPanel createTodayPanel() {
        JPanel todayPanel = new JPanel();
        todayPanel.setLayout(new BorderLayout());

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

        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 35));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setToolTipText("Chọn ngày để lập lịch");
        dateChooser.setDate(Calendar.getInstance().getTime()); // Hiển thị ngày hiện tại
        topPanel_center.add(btnYesterday);
        topPanel_center.add(dateChooser);
        topPanel_center.add(btnTomorrow);
        topPanel_right.add(btnToday);
        topPanel_left.add(btnAddTask);
        todayPanel.add(topPanel, BorderLayout.NORTH);

        // Task Table Panel
        JPanel tablePanel = new JPanel();
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(createTitledBorder("List word"));

        // Create Table Columns
        String[] columnNames = {"", "Task", "Start Time", "End Time", "Status", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Boolean.class; // Checkbox column
                    case 5:
                    case 6:
                        return JButton.class; // Edit và Delete buttons
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Checkbox không editable
            }
        };

        // Load công việc từ TaskManager
        List<Task> tasks = taskManager.getTasksForDate(localDate);
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

        // Tạo JTable với model đã thiết lập
        JTable taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);

        // Cài đặt các renderer và editor cho các button
        taskTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JButton(), taskManager, localDate, tableModel));
        taskTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JButton(), taskManager, localDate, tableModel));

        taskTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        taskTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

        // Cài đặt combobox cho cột "Status"
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Done", "Missed", "Doing", "Coming"});
        taskTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Thêm JScrollPane cho bảng
        JScrollPane scrollPane = new JScrollPane(taskTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Thêm nút "Add Task" để thêm công việc mới
        JButton btnAddTaskTable = new JButton("Add Task");
        btnAddTaskTable.addActionListener(e -> openAddTaskDialog());
        todayPanel.add(btnAddTaskTable, BorderLayout.SOUTH);

        todayPanel.add(tablePanel, BorderLayout.CENTER);

        // Add Button Actions for navigation
        btnYesterday.addActionListener(e -> changeDateByOffset(-1));
        btnTomorrow.addActionListener(e -> changeDateByOffset(1));
        btnToday.addActionListener(e -> resetToToday());

        return todayPanel;
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
            taskManager.addTask(localDate, newTask);
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
        List<Task> tasks = taskManager.getTasksForDate(localDate);
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
        private LocalDate date;
        private DefaultTableModel tableModel;
        private int row;

        public ButtonEditor(JButton button, TaskManager taskManager, LocalDate date, DefaultTableModel tableModel) {
            this.button = button;
            this.taskManager = taskManager;
            this.date = date;
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
                List<Task> tasks = taskManager.getTasksForDate(date);
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
                List<Task> tasks = taskManager.getTasksForDate(date);
                Task task = tasks.get(row);
                taskManager.removeTask(date, task);

                // Xóa row khỏi Table Model
                tableModel.removeRow(row);

                taskManager.saveToFile("tasks_data.dat"); // Lưu ngay
            }
        }
    }
}