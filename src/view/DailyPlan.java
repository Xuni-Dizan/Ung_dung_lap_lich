package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import com.toedter.calendar.JDateChooser;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import static javax.swing.BorderFactory.*;

public class DailyPlan extends JFrame {

    private JDateChooser dateChooser;

    private void createAndShowGUI() {
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

        // Thêm JPanel chứa nút "Add" vào phía trên JTabbedPane
        JPanel addButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButtonPanel.setOpaque(false); // Đảm bảo rằng nó không có nền
        JButton btnAddTab = new JButton(new ImageIcon("path/to/plus-icon.png")); // Sử dụng icon dấu cộng
        btnAddTab.setPreferredSize(new Dimension(40, 40)); // Kích thước nút
        btnAddTab.setBackground(new Color(52, 152, 219)); // Màu nền đẹp
        btnAddTab.setForeground(Color.WHITE); // Màu chữ
        btnAddTab.setBorder(createLineBorder(Color.GRAY, 1, true)); // Viền tròn cho nút
        btnAddTab.setFocusPainted(false); // Bỏ viền focus
        btnAddTab.setToolTipText("Add New Tab"); // Thêm tooltip để hiển thị khi người dùng di chuột qua nút

        // Tạo sự kiện click cho nút "Add"
        btnAddTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int newTabIndex = tabbedPane.getTabCount() + 1; // Tạo index cho tab mới
                tabbedPane.addTab("New Tab " + newTabIndex, new JPanel()); // Thêm tab mới
                tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1); // Chọn tab mới
            }
        });

        addButtonPanel.add(btnAddTab);  // Thêm nút vào panel

        // Tạo nút "Delete" để xóa tab
//        JPanel deleteButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//        deleteButtonPanel.setOpaque(false); // Đảm bảo rằng nó không có nền
//        JButton btnDeleteTab = new JButton("Xóa Tab");
//        btnDeleteTab.setBackground(Color.RED); // Màu nền nút
//        btnDeleteTab.setForeground(Color.WHITE); // Màu chữ
//        btnDeleteTab.setFont(new Font("Arial", Font.PLAIN, 14)); // Kích thước chữ
//        btnDeleteTab.setBorder(createEmptyBorder(10, 20, 10, 20)); // Viền mỏng
//        btnDeleteTab.setFocusPainted(false); // Bỏ viền focus
//        btnDeleteTab.setToolTipText("Xóa tab hiện tại"); // Thêm tooltip

        // Tạo sự kiện click cho nút "Delete"
//        btnDeleteTab.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                // Xóa tab hiện tại
//                int selectedIndex = tabbedPane.getSelectedIndex();
//                if (selectedIndex != -1) {
//                    tabbedPane.removeTabAt(selectedIndex);
//                }
//            }
//        });

//        deleteButtonPanel.add(btnDeleteTab); // Thêm nút "Delete" vào panel
//
//        // Add cả hai nút vào phần trên của JTabbedPane
//        JPanel topPanel = new JPanel();
//        topPanel.setLayout(new BorderLayout());
//        topPanel.add(addButtonPanel, BorderLayout.WEST);
//        topPanel.add(deleteButtonPanel, BorderLayout.EAST);

        // Add top panel vào JFrame
//        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add the mainPanel to the JFrame
        add(mainPanel, BorderLayout.CENTER);

        // Make the frame visible
        setVisible(true);
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
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                switch (columnIndex) {
                    case 0: return Boolean.class; // Checkbox column
                    case 5: case 6: return JButton.class; // Edit and Delete buttons
                    default: return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Only the checkbox is not editable
            }
        };

        // Sample Data
        Object[][] rowData = {
                {false, "Test", "08:00", "09:00", "Doing", new JButton("Edit"), new JButton("Delete")},
                {false, "Kết thúc demo", "10:00", "12:00", "Doing", new JButton("Edit"), new JButton("Delete")},
                {false, "Việc đã bị trễ", "14:00", "15:00", "Coming", new JButton("Edit"), new JButton("Delete")}
        };

        for (Object[] row : rowData) {
            tableModel.addRow(row);
        }

        // Create Table
        JTable taskTable = new JTable(tableModel);
        taskTable.setRowHeight(30);

        // Customize Column Widths
        taskTable.getColumnModel().getColumn(0).setPreferredWidth(30); // Checkbox
        taskTable.getColumnModel().getColumn(1).setPreferredWidth(300); // Task Name
        taskTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Start Time
        taskTable.getColumnModel().getColumn(3).setPreferredWidth(100); // End Time
        taskTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        taskTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Edit Button
        taskTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Delete Button

        // Add ActionListener for Edit button
        taskTable.getColumn("Edit").setCellEditor(new ButtonEditor(new JButton("Edit")));
        taskTable.getColumn("Delete").setCellEditor(new ButtonEditor(new JButton("Delete")));

        // Add Button Action Listeners for "Edit" and "Delete"
        taskTable.getColumn("Edit").setCellRenderer(new ButtonRenderer("Edit"));
        taskTable.getColumn("Delete").setCellRenderer(new ButtonRenderer("Delete"));

        // Add Dropdown Editor for "Status"
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Done", "Missed", "Doing", "Coming"});
        taskTable.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Add Scroll Pane for Table
        JScrollPane scrollPane = new JScrollPane(taskTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Footer Panel
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblSummary = new JLabel("Total: 14 words | Done: 0 | Missed: 12 | Doing: 2 | Coming: 0");
        footerPanel.add(lblSummary);
        todayPanel.add(footerPanel, BorderLayout.SOUTH);

        // Add Panels to Frame
        todayPanel.add(tablePanel, BorderLayout.CENTER);

        // Add Button Actions for navigation
        btnYesterday.addActionListener(e -> JOptionPane.showMessageDialog(this, "Yesterday's tasks."));
        btnTomorrow.addActionListener(e -> JOptionPane.showMessageDialog(this, "Tomorrow's tasks."));

        return todayPanel;
    }

    // Renderer for JButton in JTable (Custom Button Renderer)
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        private String label;

        public ButtonRenderer(String label) {
            this.label = label;
            setText(label);
            setFocusPainted(false); // Không vẽ viền khi nhấn
            setBackground(Color.LIGHT_GRAY); // Thêm màu nền cho đẹp mắt
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this; // Trả về nút khi cần hiển thị trong bảng
        }
    }

    // ButtonEditor
    private class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JButton button;

        public ButtonEditor(JButton button) {
            this.button = button;
            this.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Xử lý sự kiện ngay lập tức khi nút được nhấn
                    if (button.getText().equals("Edit")) {
                        JOptionPane.showMessageDialog(null, "Edit Button Clicked");
                    } else if (button.getText().equals("Delete")) {
                        JOptionPane.showMessageDialog(null, "Delete Button Clicked");
                    }
                    stopCellEditing();  // Dừng chỉnh sửa ngay sau khi nhấn nút
                }
            });
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }
    }

    public static void main(String[] args) {
        // Call the method to create and show the GUI
        SwingUtilities.invokeLater(() -> new DailyPlan().createAndShowGUI());
    }
}
