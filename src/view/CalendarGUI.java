// src/view/CalendarGUI.java
package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.toedter.calendar.JDateChooser;

import model.TaskManager;

import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;
// import java.text.SimpleDateFormat;

public class CalendarGUI extends JFrame {

    private JPanel centerPanelCenter;
    private JDateChooser dateChooser;
    private JButton[] dayButtons = new JButton[42]; // Mảng lưu các button ngày
    private Calendar selectedDate = Calendar.getInstance(); // Lưu ngày được chọn
    private TaskManager taskManager; // Thêm TaskManager
    private JButton previouslySelectedButton = null; // Biến để lưu button đã được chọn trước đó

    public CalendarGUI() {
        // Khởi tạo TaskManager và tải dữ liệu từ tệp
        taskManager = new TaskManager();
        taskManager.loadFromFile("tasks_data.dat");
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

        // Tạo tiêu đề cho JFrame
        setTitle("Lịch của tôi");

        // Thiết lập layout chính
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15)); // Tăng khoảng cách xung quanh

        // Panel chứa các thành phần ở trên (Notify, DateTimePicker)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 25, 15));
        topPanel.setBackground(new Color(245, 245, 245)); // Màu nền nhẹ nhàng

        // Checkbox Notify
        JCheckBox notifyCheckBox = new JCheckBox("Thông báo");
        notifyCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // Dùng font Segoe UI
        notifyCheckBox.setForeground(new Color(60, 60, 60)); // Màu chữ xám đậm
        notifyCheckBox.setToolTipText("Chọn nếu bạn muốn nhận thông báo");
        topPanel.add(notifyCheckBox);

        // NumericUpDown (JSpinner) để điều chỉnh thời gian thông báo
        JLabel timeLabel = new JLabel("Thời gian (phút):");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        timeLabel.setForeground(new Color(60, 60, 60));
        topPanel.add(timeLabel);

        SpinnerNumberModel model = new SpinnerNumberModel(15, 1, 60, 1);  // Mặc định là 15 phút, giá trị từ 1 đến 60
        JSpinner timeSpinner = new JSpinner(model);
        timeSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        topPanel.add(timeSpinner);

        // DateTimePicker để chọn ngày tháng năm
        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 35));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setToolTipText("Chọn ngày để lập lịch");
        dateChooser.setDate(Calendar.getInstance().getTime()); // Hiển thị ngày hiện tại
        dateChooser.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                selectedDate.setTime((Date) evt.getNewValue());
                updateCalendar();
            }
        });
        topPanel.add(dateChooser);

        // Button "Hôm nay"
        JButton todayButton = new JButton("Hôm nay");
        todayButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        todayButton.setBackground(new Color(34, 193, 195)); // Màu xanh ngọc tươi sáng
        todayButton.setForeground(Color.WHITE);
        todayButton.setFocusPainted(false);
        todayButton.setBorder(BorderFactory.createEmptyBorder()); // Không viền cứng
        todayButton.setToolTipText("Chuyển đến ngày hôm nay");
        todayButton.setPreferredSize(new Dimension(110, 40));
        todayButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Thêm hiệu ứng chuột

        // Thêm hiệu ứng hover cho nút "Hôm nay"
        todayButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                todayButton.setBackground(new Color(45, 192, 194));
                todayButton.setBorder(BorderFactory.createLineBorder(new Color(45, 192, 194), 3)); // Thêm border nhẹ khi hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                todayButton.setBackground(new Color(34, 193, 195));
                todayButton.setBorder(BorderFactory.createEmptyBorder()); // Remove border
            }
        });

        // Thêm hiệu ứng shadow cho nút khi được nhấn
        todayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateChooser.setDate(Calendar.getInstance().getTime()); // Chuyển đến ngày hôm nay
                updateCalendar();
            }
        });

        topPanel.add(todayButton);
        add(topPanel, BorderLayout.NORTH);

        // Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());

        // Panel bao quanh các button, đặt kích thước cố định
        JPanel navPanelCenterWrapper = new JPanel();
        navPanelCenterWrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navPanelCenterWrapper.setPreferredSize(new Dimension(700, 40));

        // Các nút điều hướng tháng và ngày
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BorderLayout());
        navPanel.setBackground(new Color(255, 255, 255));

        JPanel navPanelCenter = new JPanel();
        navPanelCenter.setLayout(new GridLayout(1, 5, 10, 10)); // Điều chỉnh số cột

        String[] buttonLabels = {"Tháng trước", "Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7", "Chủ nhật", "Tháng sau"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setToolTipText(label);

            Dimension buttonSize = new Dimension(90, 40);
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(100, 150, 200));
                    button.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(70, 130, 180));
                    button.setBorder(BorderFactory.createEmptyBorder());
                }
            });

            if (label.equals("Tháng trước")) {
                navPanel.add(button, BorderLayout.WEST);
                button.setPreferredSize(new Dimension(100,40));
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveToPreviousMonth();
                    }
                });
            } else if (label.equals("Tháng sau")) {
                navPanel.add(button, BorderLayout.EAST);
                button.setPreferredSize(new Dimension(100,40));
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveToNextMonth();
                    }
                });
            } else {
                navPanelCenter.add(button);
            }
        }

        navPanelCenterWrapper.add(navPanelCenter);
        navPanel.add(navPanelCenterWrapper, BorderLayout.CENTER);
        centerPanel.add(navPanel, BorderLayout.NORTH);

        // Tạo panel con bao quanh centerPanel_center để thu hẹp chiều rộng
        JPanel centerPanelWrapper = new JPanel();
        centerPanelWrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        centerPanelWrapper.setBackground(new Color(255, 255, 255));

        // Panel chứa ma trận button với 6 hàng và 7 cột
        centerPanelCenter = new JPanel();
        centerPanelCenter.setLayout(new GridLayout(6, 7, 10, 10));
        centerPanelCenter.setPreferredSize(new Dimension(695, 300));
        centerPanelCenter.setBackground(new Color(255, 255, 255));

        // Thêm các button vào centerPanelCenter (Các ngày của tháng)
        centerPanelWrapper.add(centerPanelCenter);
        centerPanel.add(centerPanelWrapper, BorderLayout.CENTER);

        // Thêm panel chính vào JFrame
        add(centerPanel, BorderLayout.CENTER);

        // Thiết lập đóng chương trình khi nhấn nút close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Thiết lập kích thước mặc định
        setSize(1000, 600);
        setLocationRelativeTo(null); // Đặt cửa sổ giữa màn hình
        setVisible(true);

        // Thiết lập đóng cửa sổ để lưu dữ liệu
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                taskManager.saveToFile("tasks_data.dat");
                System.exit(0);
            }
        });

        // Cập nhật lịch ban đầu
        updateCalendar();
    }

    // Cập nhật lịch theo ngày trong JDateChooser
    private void updateCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateChooser.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Lấy ngày hiện tại để làm nổi bật
        Calendar today = Calendar.getInstance();
        int currentDay = today.get(Calendar.DAY_OF_MONTH);
        int currentMonth = today.get(Calendar.MONTH);
        int currentYear = today.get(Calendar.YEAR);

        // Cập nhật các ngày trong tháng vào các nút
        centerPanelCenter.removeAll();
        calendar.set(Calendar.DAY_OF_MONTH, 1);  // Đặt ngày đầu tháng
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // Thêm các button cho các ngày trong tháng
        int day = 1;
        for (int i = 0; i < 42; i++) { // 6 hàng * 7 cột = 42 button
            JButton button = new JButton();
            dayButtons[i] = button; // Lưu button vào mảng để dễ dàng truy xuất

            if (i >= firstDayOfWeek - 1 && day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                button.setText(String.valueOf(day));

                // Nếu là ngày hiện tại, thay đổi màu nền của button
                if (day == currentDay && currentMonth == month && currentYear == year) {
                    button.setBackground(new Color(34, 193, 195)); // Màu xanh ngọc cho ngày hiện tại
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(new Color(240, 240, 240)); // Màu nền nhạt cho các ngày khác
                    button.setForeground(Color.BLACK);
                }

                // Nếu là ngày được chọn từ JDateChooser, thay đổi màu nền của button
                if (day == selectedDate.get(Calendar.DAY_OF_MONTH) &&
                        month == selectedDate.get(Calendar.MONTH) &&
                        year == selectedDate.get(Calendar.YEAR)) {
                    button.setBackground(new Color(255, 165, 0)); // Màu nền cho ngày được chọn (cam)
                    button.setForeground(Color.WHITE);

                    // Reset màu nền của nút trước đó nếu có
                    if (previouslySelectedButton != null && previouslySelectedButton != button) {
                        previouslySelectedButton.setBackground(new Color(240, 240, 240));
                        previouslySelectedButton.setForeground(Color.BLACK);
                    }
                    previouslySelectedButton = button;
                }

                // Thêm sự kiện click cho button ngày
                int selectedDay = day;
                button.addActionListener(e -> {
                    selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);
                    dateChooser.setDate(selectedDate.getTime());
                    updateCalendar();
                    openDailyPlan(selectedDate.getTime());
                });

                day++;
            } else {
                button.setEnabled(false); // Disable các ngày không thuộc tháng hiện tại
            }

            // Cài đặt font cho các nút ngày
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            button.setFocusPainted(false);
            centerPanelCenter.add(button);
        }

        // Làm mới giao diện
        centerPanelCenter.revalidate();
        centerPanelCenter.repaint();
    }

    // Di chuyển đến tháng trước
    private void moveToPreviousMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateChooser.getDate());
        calendar.add(Calendar.MONTH, -1);  // Lùi 1 tháng
        dateChooser.setDate(calendar.getTime());
        updateCalendar();
    }

    // Di chuyển đến tháng sau
    private void moveToNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateChooser.getDate());
        calendar.add(Calendar.MONTH, 1);  // Tiến 1 tháng
        dateChooser.setDate(calendar.getTime());
        updateCalendar();
    }

    // Phương thức mở DailyPlan với ngày được chọn
    private void openDailyPlan(Date selectedDate) {
        SwingUtilities.invokeLater(() -> new DailyPlan(selectedDate, taskManager));
    }

    // public static void main(String[] args) {
    //     new CalendarGUI();
    // }
}