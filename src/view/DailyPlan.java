// src/view/DailyPlan.java
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import com.toedter.calendar.JDateChooser;

import model.TabInfo;
import model.TaskManager;

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

import static javax.swing.BorderFactory.*;

public class DailyPlan extends JFrame {

    private JTabbedPane tabbedPane;
    private TaskManager taskManager;
    private List<TodayPanel> todayPanels;
    private List<TabInfo> tabInfos; // Danh sách TabInfo

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
        JButton btnAddTab = new JButton("+"); 
        btnAddTab.setPreferredSize(new Dimension(40, 40)); 
        btnAddTab.setBackground(new Color(52, 152, 219)); 
        btnAddTab.setForeground(Color.WHITE); 
        btnAddTab.setBorder(createLineBorder(Color.GRAY, 1, true));
        btnAddTab.setFocusPainted(false);
        btnAddTab.setToolTipText("Add New Tab"); 

        // Tạo nút "Reset All"
        JButton btnResetAll = new JButton("Reset All");
        btnResetAll.setPreferredSize(new Dimension(100, 40));
        btnResetAll.setBackground(new Color(231, 76, 60)); 
        btnResetAll.setForeground(Color.WHITE); 
        btnResetAll.setBorder(createLineBorder(Color.GRAY, 1, true));
        btnResetAll.setFocusPainted(false);
        btnResetAll.setToolTipText("Reset tất cả dữ liệu");

        // Tạo sự kiện click cho nút "Add"
        btnAddTab.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tabTitle = JOptionPane.showInputDialog("Nhập tiêu đề cho tab mới:");
                if (tabTitle != null && !tabTitle.trim().isEmpty()) {
                    Date currentDate = Calendar.getInstance().getTime();
                    addTodayPanel(tabTitle, currentDate);
                    TabInfo newTab = new TabInfo(tabTitle, currentDate);
                    tabInfos.add(newTab);
                    saveTabsData(); // Lưu danh sách tab sau khi thêm
                }
            }
        });

        // Tạo sự kiện click cho nút "Reset All"
        btnResetAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc chắn muốn reset tất cả dữ liệu?", "Xác nhận Reset", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Xóa dữ liệu trong TaskManager
                    taskManager.getTasksByKey().clear();
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

        // Thêm các nút vào panel
        addResetButtonPanel.add(btnResetAll);
        addResetButtonPanel.add(btnAddTab);  // Thêm nút "Add" vào panel

        mainPanel.add(addResetButtonPanel, BorderLayout.NORTH);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        // Add the mainPanel to the JFrame
        add(mainPanel, BorderLayout.CENTER);

        // Make the frame visible
        setVisible(true);
    }

    // Phương thức để thêm một TodayPanel mới
    private void addTodayPanel(String tabTitle, Date selectedDate) {
        TodayPanel newPanel = new TodayPanel(tabTitle, selectedDate, taskManager);
        todayPanels.add(newPanel);
        tabbedPane.addTab(tabTitle, newPanel); // Chỉ sử dụng tabTitle làm tiêu đề tab
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
}