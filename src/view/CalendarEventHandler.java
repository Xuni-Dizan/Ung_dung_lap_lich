// src/view/CalendarEventHandler.java
package view;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.util.Calendar;
import java.util.Date;

public class CalendarEventHandler {

    private JDateChooser dateChooser;
    private JPanel centerPanelCenter;
    private JButton[] dateButtons;
    private Calendar currentCalendar;
    private TaskManager taskManager;
    private String currentTabTitle;
    private boolean isDarkMode = false;
    private JButton previouslySelectedButton = null;

    public CalendarEventHandler(JDateChooser dateChooser, JPanel centerPanelCenter, TaskManager taskManager, String currentTabTitle) {
        this.dateChooser = dateChooser;
        this.centerPanelCenter = centerPanelCenter;
        this.dateButtons = new JButton[42];
        this.currentCalendar = Calendar.getInstance();
        this.taskManager = taskManager;
        this.currentTabTitle = currentTabTitle;

        // Khởi tạo các button ngày trong centerPanel_center
        initializeDateButtons();

        // Đặt ngày hiện tại vào JDateChooser
        dateChooser.setDate(new Date());

        // Hiển thị lịch và cập nhật button ngày
        updateCalendar();

        // Lắng nghe sự thay đổi từ JDateChooser
        dateChooser.addPropertyChangeListener(e -> {
            if ("date".equals(e.getPropertyName())) {
                updateCalendar();
            }
        });
    }

    // Khởi tạo các button cho ngày trong tháng
    private void initializeDateButtons() {
        centerPanelCenter.removeAll();
        centerPanelCenter.setLayout(new GridLayout(6, 7, 10, 10));

        for (int i = 0; i < 42; i++) {
            JButton button = new JButton();
            button.setFont(new Font("Roboto", Font.PLAIN, 14));
            button.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
            button.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setBorder(new RoundedBorder(10));
            button.setOpaque(true);
            
            // Tăng kích thước nút
            button.setPreferredSize(new Dimension(100, 50)); // Tăng kích thước từ mặc định nếu cần

            // Thêm hiệu ứng hover cho nút
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(isDarkMode ? new Color(100, 100, 100) : new Color(220, 220, 220));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    if (previouslySelectedButton != null && previouslySelectedButton != button) {
                        previouslySelectedButton.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
                        previouslySelectedButton.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
                    }
                    button.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
                }
            });

            dateButtons[i] = button;
            centerPanelCenter.add(button);
        }

        centerPanelCenter.revalidate();
        centerPanelCenter.repaint();
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
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int day = 1;
        for (int i = 0; i < 42; i++) {
            JButton button = dateButtons[i];
            if (i >= firstDayOfWeek - 1 && day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                button.setText(String.valueOf(day));

                // Nếu là ngày hiện tại, thay đổi màu nền của button
                if (day == currentDay && currentMonth == month && currentYear == year) {
                    button.setBackground(new Color(34, 193, 195));
                    button.setForeground(Color.WHITE);
                } else {
                    button.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
                    button.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
                }

                // Nếu là ngày được chọn từ JDateChooser, thay đổi màu nền của button
                if (day == currentCalendar.get(Calendar.DAY_OF_MONTH) &&
                        month == currentCalendar.get(Calendar.MONTH) &&
                        year == currentCalendar.get(Calendar.YEAR)) {
                    button.setBackground(new Color(255, 165, 0));
                    button.setForeground(Color.WHITE);

                    // Reset màu nền của nút trước đó nếu có
                    if (previouslySelectedButton != null && previouslySelectedButton != button) {
                        previouslySelectedButton.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
                        previouslySelectedButton.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
                    }
                    previouslySelectedButton = button;
                }

                // Thêm sự kiện click cho button ngày
                int selectedDay = day;
                button.addActionListener(e -> {
                    currentCalendar.set(Calendar.DAY_OF_MONTH, selectedDay);
                    dateChooser.setDate(currentCalendar.getTime());
                    updateCalendar();
                    openDailyPlan(currentCalendar.getTime());
                });

                day++;
            } else {
                button.setEnabled(false);
                button.setText("");
            }

            centerPanelCenter.add(button);
        }

        centerPanelCenter.revalidate();
        centerPanelCenter.repaint();
    }

    // Phương thức mới để mở DailyPlan với ngày được chọn và tabTitle hiện tại
    private void openDailyPlan(Date selectedDate) {
        SwingUtilities.invokeLater(() -> new DailyPlan(selectedDate, taskManager));
    }

    // Sự kiện cho nút "Tháng trước"
    public void onPreviousMonthClicked() {
        currentCalendar.add(Calendar.MONTH, -1);
        dateChooser.setDate(currentCalendar.getTime());
        updateCalendar();
    }

    // Sự kiện cho nút "Tháng sau"
    public void onNextMonthClicked() {
        currentCalendar.add(Calendar.MONTH, 1);
        dateChooser.setDate(currentCalendar.getTime());
        updateCalendar();
    }
}