// src/view/TodayPanel.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import com.toedter.calendar.JDateChooser;

import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import view.DropShadowBorder;
import java.time.LocalDate;
import java.awt.geom.RoundRectangle2D;

import static javax.swing.BorderFactory.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TodayPanel extends JPanel {

    private String tabTitle;
    private JDateChooser dateChooser;
    private TaskManager taskManager;
    private LocalDate localDate;
    private DefaultTableModel tableModel;
    private Date selectedDate;
    private boolean isDarkMode = false; // Biến để kiểm soát chế độ Dark Mode

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
        topPanel.setOpaque(false);
        topPanel_right.setOpaque(false);
        topPanel_left.setOpaque(false);
        topPanel_center.setOpaque(false);
        topPanel.add(topPanel_center, BorderLayout.CENTER);
        topPanel.add(topPanel_right, BorderLayout.EAST);
        topPanel.add(topPanel_left, BorderLayout.WEST);

        JButton btnYesterday = createRoundedButton("Yesterday", new Color(70, 130, 180), Color.WHITE);
        JButton btnToday = createRoundedButton("Today", new Color(70, 130, 180), Color.WHITE);
        JButton btnTomorrow = createRoundedButton("Tomorrow", new Color(70, 130, 180), Color.WHITE);
        JButton btnAddTask = createRoundedButton("Add Task", new Color(39, 174, 96), Color.WHITE);

        btnAddTask.addActionListener(e -> openAddTaskDialog());

        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 35));
        dateChooser.setFont(new Font("Roboto", Font.PLAIN, 16));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setToolTipText("Select Date to Plan");
        dateChooser.setDate(selectedDate);
        styleDateChooser(dateChooser);
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
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Áp dụng DropShadowBorder cho tablePanel
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            new DropShadowBorder(5, new Color(0, 0, 0, 50)),
            createTitledBorder("List Task")
        ));

        // Create Table Columns
        String[] columnNames = {"Completed", "Task", "Start Time", "End Time", "Status", "Edit", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0:
                        return Boolean.class; 
                    case 5:
                    case 6:
                        return String.class; 
                    case 4:
                        return String.class; 
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
        Utilities.applyTableStyle(taskTable, isDarkMode);

        // Áp dụng màu sắc thể hiện trạng thái
        taskTable.setDefaultRenderer(String.class, new StatusRenderer());

        // Sử dụng các Renderer riêng cho "Edit" và "Delete"
        taskTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        taskTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

        // Sử dụng các Editor riêng cho "Edit" và "Delete"
        taskTable.getColumn("Edit").setCellEditor(new EditButtonEditor());
        taskTable.getColumn("Delete").setCellEditor(new DeleteButtonEditor());

        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Hoàn thành", "Đang tiến hành", "Chưa bắt đầu", "Quá hạn"});
        taskTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        JScrollPane scrollPane = new JScrollPane(taskTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Add Button Actions for navigation
        btnYesterday.addActionListener(e -> changeDateByOffset(-1));
        btnTomorrow.addActionListener(e -> changeDateByOffset(1));
        btnToday.addActionListener(e -> resetToToday());

        // Tăng kích thước các nút
        btnYesterday.setPreferredSize(new Dimension(120, 35));
        btnToday.setPreferredSize(new Dimension(120, 35));
        btnTomorrow.setPreferredSize(new Dimension(120, 35));
        btnAddTask.setPreferredSize(new Dimension(120, 35));
    }

    /**
     * Phương thức mở dialog để chỉnh sửa công việc
     *
     * @param row Hàng trong bảng tương ứng với công việc cần chỉnh sửa
     */
    private void editTask(int row) {
        String key = generateKey(tabTitle, localDate);
        List<Task> tasks = taskManager.getTasksForKey(key);
        if (row < 0 || row >= tasks.size()) {
            JOptionPane.showMessageDialog(this, "Lỗi: Công việc không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task task = tasks.get(row);

        JTextField taskNameField = new JTextField(task.getName());
        JTextField startTimeField = new JTextField(task.getStartTime());
        JTextField endTimeField = new JTextField(task.getEndTime());
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Hoàn thành", "Đang tiến hành", "Chưa bắt đầu", "Quá hạn"});

        statusComboBox.setSelectedItem(task.getStatus());

        Object[] message = {
            "Tên công việc:", taskNameField,
            "Thời gian bắt đầu (HH:mm):", startTimeField,
            "Thời gian kết thúc (HH:mm):", endTimeField,
            "Trạng thái:", statusComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Chỉnh sửa Công việc", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = taskNameField.getText().trim();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();

            // Kiểm tra dữ liệu đầu vào
            String errorMessage = validateTaskInput(name, startTime, endTime, key, row);
            if (!errorMessage.isEmpty()) {
                JOptionPane.showMessageDialog(this, errorMessage, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Cập nhật công việc
            task.setName(name);
            task.setStartTime(startTime);
            task.setEndTime(endTime);
            task.setStatus(status);

            // Cập nhật TaskManager
            taskManager.updateTask(key, row, task);
            taskManager.saveToFile("tasks_data.dat");

            // Cập nhật Table Model
            tableModel.setValueAt(name, row, 1);
            tableModel.setValueAt(startTime, row, 2);
            tableModel.setValueAt(endTime, row, 3);
            tableModel.setValueAt(status, row, 4);

            JOptionPane.showMessageDialog(this, "Công việc đã được cập nhật thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Phương thức mở dialog để thêm công việc mới
     */
    private void openAddTaskDialog() {
        JTextField taskNameField = new JTextField();
        JTextField startTimeField = new JTextField();
        JTextField endTimeField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Hoàn thành", "Đang tiến hành", "Chưa bắt đầu", "Quá hạn"});

        Object[] message = {
            "Tên công việc:", taskNameField,
            "Thời gian bắt đầu (HH:mm):", startTimeField,
            "Thời gian kết thúc (HH:mm):", endTimeField,
            "Trạng thái:", statusComboBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Thêm Công việc Mới", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String name = taskNameField.getText().trim();
            String startTime = startTimeField.getText().trim();
            String endTime = endTimeField.getText().trim();
            String status = (String) statusComboBox.getSelectedItem();

            // Kiểm tra dữ liệu đầu vào
            String errorMessage = validateTaskInput(name, startTime, endTime, generateKey(tabTitle, localDate), -1);
            if (!errorMessage.isEmpty()) {
                JOptionPane.showMessageDialog(this, errorMessage, "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
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
                "Chỉnh sửa",
                "Xóa"
            });
            taskManager.saveToFile("tasks_data.dat"); // Lưu ngay

            JOptionPane.showMessageDialog(this, "Đã thêm công việc thành công.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Phương thức kiểm tra dữ liệu đầu vào của công việc
     * 
     * @param name        Tên công việc
     * @param startTime   Thời gian bắt đầu
     * @param endTime     Thời gian kết thúc
     * @param key         Khóa để kiểm tra trùng lặp
     * @param currentRow  Hàng hiện tại (để bỏ qua khi chỉnh sửa)
     * @return            Thông điệp lỗi nếu có, ngược lại trả về chuỗi rỗng
     */
    private String validateTaskInput(String name, String startTime, String endTime, String key, int currentRow) {
        StringBuilder error = new StringBuilder();

        // Kiểm tra tên công việc không được trống
        if (name.isEmpty()) {
            error.append("- Tên công việc không được để trống.\n");
        }

        // Kiểm tra định dạng thời gian (HH:mm)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setLenient(false);
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(startTime);
        } catch (ParseException e) {
            error.append("- Thời gian bắt đầu không hợp lệ. Vui lòng nhập theo định dạng HH:mm.\n");
        }

        try {
            endDate = sdf.parse(endTime);
        } catch (ParseException e) {
            error.append("- Thời gian kết thúc không hợp lệ. Vui lòng nhập theo định dạng HH:mm.\n");
        }

        // Kiểm tra thời gian bắt đầu nhỏ hơn thời gian kết thúc
        if (startDate != null && endDate != null && !startDate.before(endDate)) {
            error.append("- Thời gian bắt đầu phải nhỏ hơn thời gian kết thúc.\n");
        }

        // Kiểm tra tên công việc không trùng lặp
        List<Task> tasks = taskManager.getTasksForKey(key);
        for (int i = 0; i < tasks.size(); i++) {
            if (i == currentRow) continue; // Bỏ qua công việc hiện tại khi chỉnh sửa
            if (tasks.get(i).getName().equalsIgnoreCase(name)) {
                error.append("- Tên công việc đã tồn tại. Vui lòng chọn tên khác.\n");
                break;
            }
        }

        return error.toString();
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

    /**
     * Renderer cho Trạng thái trong JTable
     */
    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel();
            label.setText((String) value);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Roboto", Font.BOLD, 14));

            switch ((String) value) {
                case "Hoàn thành":
                    label.setForeground(new Color(46, 204, 113)); // Green
                    break;
                case "Đang tiến hành":
                    label.setForeground(new Color(52, 152, 219)); // Blue
                    break;
                case "Chưa bắt đầu":
                    label.setForeground(new Color(149, 165, 166)); // Gray
                    break;
                case "Quá hạn":
                    label.setForeground(new Color(231, 76, 60)); // Red
                    break;
                default:
                    label.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
                    break;
            }

            if (isSelected) {
                label.setOpaque(true);
                label.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(220, 220, 220));
            } else {
                label.setOpaque(true);
                label.setBackground(isDarkMode ? new Color(45, 45, 45) : Color.WHITE);
            }

            return label;
        }
    }

    /**
     * Renderer cho JButton trong JTable
     */
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String label) {
            setText(label);
            setFocusPainted(false);
            setBackground(new Color(52, 152, 219)); // Màu nền xanh cho nút "Edit"
            setForeground(Color.WHITE);
            setFont(new Font("Roboto", Font.PLAIN, 12));
            setBorder(new RoundedBorder(10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * Editor cho nút "Edit" trong JTable
     */
    private class EditButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String label;
        private int row;

        public EditButtonEditor() {
            this.button = new JButton();
            this.button.setFocusPainted(false);
            this.button.setBackground(new Color(52, 152, 219));
            this.button.setForeground(Color.WHITE);
            this.button.setFont(new Font("Roboto", Font.PLAIN, 12));
            this.button.setBorder(new RoundedBorder(10));
            this.button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Thêm sự kiện cho nút
            this.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    editTask(row);
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.label = (String) value;
            this.row = row;
            button.setText(label);
            return button;
        }
    }

    /**
     * Editor cho nút "Delete" trong JTable
     */
    private class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;
        private String label;
        private int row;

        public DeleteButtonEditor() {
            this.button = new JButton();
            this.button.setFocusPainted(false);
            this.button.setBackground(new Color(231, 76, 60)); // Màu nền đỏ cho nút "Delete"
            this.button.setForeground(Color.WHITE);
            this.button.setFont(new Font("Roboto", Font.PLAIN, 12));
            this.button.setBorder(new RoundedBorder(10));
            this.button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            // Thêm sự kiện cho nút
            this.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                    deleteTask(row);
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return label;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.label = (String) value;
            this.row = row;
            button.setText(label);
            return button;
        }
    }

    /**
     * Phương thức để xóa công việc
     *
     * @param row Hàng trong bảng tương ứng với công việc cần xóa
     */
    private void deleteTask(int row) {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa công việc này không?", "Xóa công việc", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            String key = generateKey(tabTitle, localDate);

            List<Task> tasks = taskManager.getTasksForKey(key);
            if(row >= tasks.size()){
                JOptionPane.showMessageDialog(this, "Lỗi: Công việc không tồn tại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Task task = tasks.get(row);
            taskManager.removeTask(key, task);

            // Xóa row khỏi Table Model
            tableModel.removeRow(row);

            // Lưu lại dữ liệu
            taskManager.saveToFile("tasks_data.dat");

            JOptionPane.showMessageDialog(this, "Đã xóa công việc thành công.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    // Phương thức tạo JButton bo góc
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(new RoundedBorder(15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        button.setFont(new Font("Roboto", Font.PLAIN, 14));

        // Bo góc
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));

        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    /**
     * Áp dụng kiểu cho JDateChooser dựa trên chế độ Dark Mode.
     *
     * @param chooser JDateChooser cần áp dụng kiểu.
     */
    private void styleDateChooser(JDateChooser chooser) {
        chooser.getDateEditor().getUiComponent().setFont(new Font("Roboto", Font.PLAIN, 14));
        chooser.getDateEditor().getUiComponent().setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
        chooser.getDateEditor().getUiComponent().setBackground(isDarkMode ? new Color(70, 70, 70) : Color.WHITE);
        chooser.getDateEditor().getUiComponent().setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
    }

    /**
     * Phương thức tạo border với tiêu đề
     */
    private javax.swing.border.Border createTitledBorder(String title) {
        javax.swing.border.TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleFont(new Font("Roboto", Font.BOLD, 16));
        titledBorder.setTitleJustification(javax.swing.border.TitledBorder.LEFT);
        return titledBorder;
    }

    /**
     * Phương thức chuyển đổi giữa Light Mode và Dark Mode
     */
    public void toggleDarkMode(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        setBackground(isDarkMode ? new Color(45, 45, 45) : Color.WHITE);
        // Cập nhật các thành phần bên trong
        for (Component comp : getComponents()) {
            if (comp instanceof JPanel) {
                Utilities.applyPanelStyle((JPanel) comp, isDarkMode);
                for (Component innerComp : ((JPanel) comp).getComponents()) {
                    if (innerComp instanceof JButton) {
                        Utilities.applyButtonStyle((JButton) innerComp, isDarkMode);
                    } else if (innerComp instanceof JLabel) {
                        Utilities.applyLabelStyle((JLabel) innerComp, isDarkMode);
                    } else if (innerComp instanceof JDateChooser) {
                        styleDateChooser((JDateChooser) innerComp);
                    }
                }
            }
        }
        // Cập nhật bảng
        JTable taskTable = (JTable) ((JScrollPane)((JPanel)getComponent(1)).getComponent(0)).getViewport().getView();
        Utilities.applyTableStyle(taskTable, isDarkMode);
        SwingUtilities.updateComponentTreeUI(this);
    }
}