// src/view/CalendarEventHandler.java
package view;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CalendarEventHandler {

    private JDateChooser dateChooser;
    private JPanel centerPanelCenter;
    private JButton[] dateButtons;
    private Calendar currentCalendar;
    private TaskManager taskManager; // Thêm TaskManager
    private String currentTabTitle; // Thêm biến để lưu tên tab hiện tại

    public CalendarEventHandler(JDateChooser dateChooser, JPanel centerPanelCenter, TaskManager taskManager, String currentTabTitle) {
        this.dateChooser = dateChooser;
        this.centerPanelCenter = centerPanelCenter;
        this.dateButtons = new JButton[42]; // 6 hàng x 7 cột = 42 ngày
        this.currentCalendar = Calendar.getInstance(); // Lấy ngày giờ hiện tại
        this.taskManager = taskManager; // Khởi tạo TaskManager
        this.currentTabTitle = currentTabTitle; // Khởi tạo tabTitle

        // Khởi tạo các button ngày trong centerPanel_center
        initializeDateButtons();

        // Đặt ngày hiện tại vào JDateChooser
        dateChooser.setDate(new Date()); // Ngày hiện tại

        // Hiển thị lịch và cập nhật button ngày
        updateCalendar();

        // Lắng nghe sự thay đổi từ JDateChooser
        dateChooser.addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                updateCalendar(); // Cập nhật lại các button khi người dùng thay đổi ngày
            }
        });
    }

    // Khởi tạo các button cho ngày trong tháng
    private void initializeDateButtons() {
        centerPanelCenter.removeAll(); // Làm sạch panel trước khi thêm lại button
        centerPanelCenter.setLayout(new GridLayout(6, 7, 10, 10)); // Ma trận 6 hàng và 7 cột với khoảng cách

        for (int i = 0; i < 42; i++) {
            JButton button = new JButton();
            button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            button.setBackground(new Color(240, 240, 240)); // Màu nền sáng
            button.setForeground(Color.BLACK);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); // Thêm hiệu ứng chuột

            // Thêm hiệu ứng hover cho nút
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(230, 230, 230));
                    button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2)); // Border nhẹ khi hover
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(240, 240, 240));
                    button.setBorder(BorderFactory.createEmptyBorder()); // Remove border
                }
            });

            dateButtons[i] = button;
            centerPanelCenter.add(button);
        }
    }

    // Cập nhật ngày tháng trong các button
    public void updateCalendar() {
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
            dateButtons[i] = button; // Lưu button vào mảng để dễ dàng truy xuất

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
                LocalDate selectedLocalDate = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, day);
                if (selectedLocalDate.equals(LocalDate.of(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)))) {
                    button.setBackground(new Color(255, 165, 0)); // Màu nền cho ngày được chọn (cam)
                    button.setForeground(Color.WHITE);
                }

                // Thêm sự kiện click cho button ngày
                int selectedDay = day;
                button.addActionListener(e -> {
                    Calendar selectedCalendar = Calendar.getInstance();
                    selectedCalendar.set(year, month, selectedDay);
                    openDailyPlan(selectedCalendar.getTime());
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

    // Phương thức mới để mở DailyPlan với ngày được chọn và tabTitle hiện tại
    private void openDailyPlan(Date selectedDate) {
        SwingUtilities.invokeLater(() -> new DailyPlan(selectedDate, taskManager));
    }

    // Sự kiện cho nút "Tháng trước"
    public void onPreviousMonthClicked() {
        currentCalendar.add(Calendar.MONTH, -1); // Lùi lại một tháng
        dateChooser.setDate(currentCalendar.getTime()); // Cập nhật JDateChooser
        updateCalendar(); // Cập nhật lại các button
    }

    // Sự kiện cho nút "Tháng sau"
    public void onNextMonthClicked() {
        currentCalendar.add(Calendar.MONTH, 1); // Tiến tới một tháng
        dateChooser.setDate(currentCalendar.getTime()); // Cập nhật JDateChooser
        updateCalendar(); // Cập nhật lại các button
    }
}