package view;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

public class CalendarEventHandler {

    private JDateChooser dateChooser;
    private JPanel centerPanelCenter;
    private JButton[] dateButtons;
    private Calendar currentCalendar;

    public CalendarEventHandler(JDateChooser dateChooser, JPanel centerPanelCenter) {
        this.dateChooser = dateChooser;
        this.centerPanelCenter = centerPanelCenter;
        this.dateButtons = new JButton[42]; // 6 hàng x 7 cột = 42 ngày
        this.currentCalendar = Calendar.getInstance(); // Lấy ngày giờ hiện tại

        // Khởi tạo các button ngày trong centerPanel_center
        initializeDateButtons();

        // Đặt ngày hiện tại vào JDateChooser
        dateChooser.setDate(new Date()); // Ngày hiện tại

        // Hiển thị lịch và cập nhật button ngày
        updateCalendar();

        // Lắng nghe sự thay đổi từ JDateChooser
        dateChooser.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("date")) {
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
        Calendar calendar = (Calendar) currentCalendar.clone();
        calendar.setTime(dateChooser.getDate()); // Lấy ngày từ JDateChooser

        // Lấy tháng và năm hiện tại
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);

        // Lấy ngày đầu tiên trong tháng và xác định thứ của ngày đầu tiên
        calendar.set(year, month, 1);
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK); // Thứ của ngày đầu tiên trong tháng
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH); // Số ngày trong tháng

        // Cập nhật các button với ngày trong tháng
        for (int i = 0; i < 42; i++) {
            JButton button = dateButtons[i];
            if (i >= firstDayOfMonth - 1 && i < firstDayOfMonth - 1 + daysInMonth) {
                int day = i - (firstDayOfMonth - 1) + 1;
                button.setText(String.valueOf(day));
                button.setEnabled(true); // Kích hoạt button ngày
                button.setBackground(new Color(240, 240, 240)); // Màu nền sáng

                // Đánh dấu ngày hiện tại
                if (calendar.get(Calendar.DAY_OF_MONTH) == day) {
                    button.setBackground(new Color(34, 193, 195)); // Màu nền ngày hiện tại
                    button.setForeground(Color.WHITE); // Màu chữ trắng
                }
            } else {
                button.setText(""); // Làm trống button ngoài tháng
                button.setEnabled(false); // Không kích hoạt button ngoài tháng
            }
        }
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
