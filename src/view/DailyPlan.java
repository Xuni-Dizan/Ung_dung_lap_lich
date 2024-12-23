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
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import view.RoundedBorder;
import view.DropShadowBorder;

import static javax.swing.BorderFactory.*;

public class DailyPlan extends JFrame {

    private JTabbedPane tabbedPane;
    private TaskManager taskManager;
    private List<TodayPanel> todayPanels;
    private List<TabInfo> tabInfos; // Danh sách TabInfo
    private boolean isDarkMode; // Thêm biến để kiểm soát chế độ Dark Mode

    public DailyPlan(Date selectedDate, TaskManager taskManager) {
        this.taskManager = taskManager;
        this.todayPanels = new ArrayList<>();
        this.tabInfos = new ArrayList<>();
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
        mainPanel.setOpaque(false);

        // Thanh tab
        tabbedPane = new JTabbedPane();
        addTodayPanel("Daily", selectedDate); // Tab mặc định
        TabInfo defaultTab = new TabInfo("Daily", selectedDate);
        tabInfos.add(defaultTab);

        // Đọc danh sách tab và tiêu đề từ tệp
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("tabs_data.dat"))) {
            @SuppressWarnings("unchecked")
            List<TabInfo> savedTabInfos = (List<TabInfo>) ois.readObject();
            for (TabInfo tabInfo : savedTabInfos) {
                addTodayPanel(tabInfo.getTitle(), tabInfo.getSelectedDate());
                tabInfos.add(tabInfo);
            }
        } catch (Exception ex) {
            // Nếu tệp không tồn tại hoặc lỗi, thêm tab "Daily" mặc định
            addTodayPanel("Daily", selectedDate);
        }

        // Thêm JPanel chứa nút "Add" và "Reset All" vào phía trên JTabbedPane
        JPanel addResetButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addResetButtonPanel.setOpaque(false); 
        JButton btnAddTab = createRoundedButton("+", new Color(52, 152, 219), Color.WHITE);
        btnAddTab.setPreferredSize(new Dimension(40, 40));
        btnAddTab.setToolTipText("Thêm Tab mới"); 

        // Tạo nút "Reset All"
        JButton btnResetAll = createRoundedButton("Reset All", new Color(231, 76, 60), Color.WHITE);
        btnResetAll.setPreferredSize(new Dimension(120, 40));
        btnResetAll.setToolTipText("Reset tất cả dữ liệu");

        // Thêm các nút vào panel
        addResetButtonPanel.add(btnResetAll);
        addResetButtonPanel.add(btnAddTab); 

        mainPanel.add(addResetButtonPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add the mainPanel to the JFrame
        add(mainPanel, BorderLayout.CENTER);

        // Make the frame visible
        setVisible(true);

        // Sự kiện cho nút "Add Tab"
        btnAddTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tabTitle = JOptionPane.showInputDialog("Nhập tiêu đề cho tab mới:");
                if (tabTitle != null && !tabTitle.trim().isEmpty()) {
                    Date currentDate = Calendar.getInstance().getTime();
                    addTodayPanel(tabTitle, currentDate);
                    TabInfo newTab = new TabInfo(tabTitle, currentDate);
                    tabInfos.add(newTab);
                    saveTabsData(); 
                }
            }
        });

        // Sự kiện cho nút "Reset All"
        btnResetAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn reset tất cả dữ liệu?", "Xác nhận Reset", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Xóa dữ liệu trong TaskManager
                    taskManager.clearAllTasks();
                    taskManager.saveToFile("tasks_data.dat");

                    // Xóa tất cả tab và thêm lại tab mặc định
                    tabbedPane.removeAll();
                    tabInfos.clear();
                    Date today = Calendar.getInstance().getTime();
                    addTodayPanel("Daily", today);
                    TabInfo defaultTab = new TabInfo("Daily", today);
                    tabInfos.add(defaultTab);
                    saveTabsData();

                    // Thông báo thành công
                    JOptionPane.showMessageDialog(null, "Đã reset tất cả dữ liệu thành công.", "Reset Thành Công", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    // Phương thức tạo JButton bo góc
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(new RoundedBorder(15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
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

    // Phương thức để thêm một TodayPanel mới
    private void addTodayPanel(String tabTitle, Date selectedDate) {
        TodayPanel newPanel = new TodayPanel(tabTitle, selectedDate, taskManager);
        todayPanels.add(newPanel);
        tabbedPane.addTab(tabTitle, newPanel);
    }

    // Lưu danh sách TabInfo vào tệp
    private void saveTabsData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("tabs_data.dat"))) {
            oos.writeObject(tabInfos);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Khi đóng cửa sổ, lưu danh sách tab
    @Override
    public void dispose() {
        saveTabsData();
        super.dispose();
    }

    public void toggleDarkMode(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        // Cập nhật màu nền
        getContentPane().setBackground(isDarkMode ? new Color(30, 30, 30) : new Color(245, 245, 245));
        // Cập nhật các panel và tab
        for (Component comp : tabbedPane.getComponents()) {
            if (comp instanceof TodayPanel) {
                ((TodayPanel) comp).toggleDarkMode(isDarkMode);
            }
        }
        SwingUtilities.updateComponentTreeUI(this);
    }
}