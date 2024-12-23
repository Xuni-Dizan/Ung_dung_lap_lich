// src/view/CalendarGUI.java
package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import com.toedter.calendar.JDateChooser;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.awt.FontFormatException;
import javax.swing.border.Border;
import java.awt.geom.RoundRectangle2D;
import view.RoundedBorder;

public class CalendarGUI extends JFrame {

    private JPanel centerPanelCenter;
    private JDateChooser dateChooser;
    private JButton[] dayButtons = new JButton[42];
    private Calendar selectedDate = Calendar.getInstance();
    private TaskManager taskManager;
    private JButton previouslySelectedButton = null;
    private Font robotoFont;
    private boolean isDarkMode = false;

    // Định nghĩa bảng màu đồng bộ
    private static final Color PRIMARY_COLOR = new Color(34, 193, 195);
    private static final Color PRIMARY_HOVER_COLOR = new Color(45, 192, 194);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR_LIGHT = new Color(245, 245, 245);
    private static final Color BACKGROUND_COLOR_DARK = new Color(45, 45, 45);
    private static final Color BUTTON_TEXT_COLOR = Color.WHITE;

    public CalendarGUI() {
        try {
            robotoFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/fonts/Roboto-Regular.ttf")).deriveFont(14f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(robotoFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
            robotoFont = new Font("Roboto", Font.PLAIN, 14);
        }
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

        // Thiết lập Gradient toàn cục
        GradientBackgroundPanel gradientPanel = new GradientBackgroundPanel(new Color(173, 216, 230), new Color(216, 191, 216)); // Baby Blue sang Light Lavender
        setContentPane(gradientPanel);

        // Thiết lập layout chính
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(new EmptyBorder(15, 15, 15, 15));

        // Panel chứa các thành phần ở trên (Notify, DateTimePicker)
        JPanel topPanel = new GradientBackgroundPanel(new Color(173, 216, 230), new Color(216, 191, 216));
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 20)); // Tăng khoảng cách giữa các component
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Khoảng cách xung quanh

        // Checkbox Notify
        JCheckBox notifyCheckBox = new JCheckBox("Thông báo");
        notifyCheckBox.setFont(new Font("Roboto", Font.PLAIN, 16));
        notifyCheckBox.setForeground(isDarkMode ? Color.WHITE : new Color(60, 60, 60));
        notifyCheckBox.setBackground(isDarkMode ? BACKGROUND_COLOR_DARK : BACKGROUND_COLOR_LIGHT);
        notifyCheckBox.setToolTipText("Chọn nếu bạn muốn nhận thông báo");
        topPanel.add(notifyCheckBox);

        // NumericUpDown (JSpinner) để điều chỉnh thời gian thông báo
        JLabel timeLabel = new JLabel("Thời gian (phút):");
        timeLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        timeLabel.setForeground(isDarkMode ? Color.WHITE : new Color(60, 60, 60));
        topPanel.add(timeLabel);

        SpinnerNumberModel model = new SpinnerNumberModel(15, 1, 60, 1);
        JSpinner timeSpinner = new JSpinner(model);
        timeSpinner.setFont(new Font("Roboto", Font.PLAIN, 16));
        styleSpinner(timeSpinner);
        topPanel.add(timeSpinner);

        // DateTimePicker để chọn ngày tháng năm
        dateChooser = new JDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 35));
        dateChooser.setFont(new Font("Roboto", Font.PLAIN, 16));
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setToolTipText("Chọn ngày để lập lịch");
        dateChooser.setDate(Calendar.getInstance().getTime());
        dateChooser.addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                selectedDate.setTime((Date) evt.getNewValue());
                updateCalendar();
            }
        });
        topPanel.add(dateChooser);

        // Button "Hôm nay" với icon và bo góc mềm mại
        JButton todayButton = createRoundedButton("Hôm nay", PRIMARY_COLOR, BUTTON_TEXT_COLOR);
        todayButton.setPreferredSize(new Dimension(140, 50));
        todayButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                todayButton.setBackground(PRIMARY_HOVER_COLOR);
                todayButton.setBorder(new RoundedBorder(15));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                todayButton.setBackground(PRIMARY_COLOR);
                todayButton.setBorder(new RoundedBorder(15));
            }
        });

        todayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dateChooser.setDate(Calendar.getInstance().getTime());
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
        navPanelCenterWrapper.setBackground(isDarkMode ? BACKGROUND_COLOR_DARK : BACKGROUND_COLOR_LIGHT);

        // Các nút điều hướng tháng và ngày
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BorderLayout());
        navPanel.setBackground(isDarkMode ? BACKGROUND_COLOR_DARK : BACKGROUND_COLOR_LIGHT);

        JPanel navPanelCenter = new JPanel();
        navPanelCenter.setLayout(new GridLayout(1, 9, 10, 10)); // Điều chỉnh số cột
        navPanelCenter.setBackground(isDarkMode ? BACKGROUND_COLOR_DARK : BACKGROUND_COLOR_LIGHT);

        String[] buttonLabels = {"Previous Month", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday", "Sunday", "Next Month"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Roboto", Font.BOLD, 12));
            button.setBackground(new Color(70, 130, 180));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            button.setBorder(new RoundedBorder(10));
            button.setOpaque(true);
            
            // Tăng kích thước nút để chứa toàn bộ văn bản
            if (label.equals("Previous Month") || label.equals("Next Month")) {
                button.setPreferredSize(new Dimension(120, 40)); // Tăng chiều rộng từ 100 đến 120
                button.setFont(new Font("Roboto", Font.BOLD, 12)); // Giữ kích thước phông chữ hiện tại hoặc giảm nếu cần
            } else {
                button.setPreferredSize(new Dimension(90, 40));
            }

            // Bo góc
            button.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(10),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
            ));

            // Thêm hiệu ứng hover
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(100, 150, 200));
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(70, 130, 180));
                }
            });

            if (label.equals("Previous Month")) {
                navPanel.add(button, BorderLayout.WEST);
                button.setPreferredSize(new Dimension(120, 40)); // Tăng kích thước
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        moveToPreviousMonth();
                    }
                });
            } else if (label.equals("Next Month")) {
                navPanel.add(button, BorderLayout.EAST);
                button.setPreferredSize(new Dimension(120, 40)); // Tăng kích thước
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
        centerPanelWrapper.setBackground(isDarkMode ? BACKGROUND_COLOR_DARK : BACKGROUND_COLOR_LIGHT);

        // Panel chứa ma trận button với 6 hàng và 7 cột
        centerPanelCenter = new JPanel();
        centerPanelCenter.setLayout(new GridLayout(6, 7, 15, 15));
        centerPanelCenter.setPreferredSize(new Dimension(700, 350));
        centerPanelCenter.setBackground(isDarkMode ? BACKGROUND_COLOR_DARK : BACKGROUND_COLOR_LIGHT);

        // Thêm các button vào centerPanelCenter (Các ngày của tháng)
        centerPanelWrapper.add(centerPanelCenter);
        centerPanel.add(centerPanelWrapper, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Thiết lập đóng chương trình khi nhấn nút close
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Thiết lập kích thước mặc định
        setSize(1000, 600);
        setLocationRelativeTo(null);
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

    // Cậi tiến phương thức cập nhật lịch
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
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int day = 1;
        for (int i = 0; i < 42; i++) {
            JButton button = new JButton();
            dayButtons[i] = button;

            // Áp dụng bo góc và hiệu ứng
            styleDayButton(button);

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
                if (day == selectedDate.get(Calendar.DAY_OF_MONTH) &&
                        month == selectedDate.get(Calendar.MONTH) &&
                        year == selectedDate.get(Calendar.YEAR)) {
                    button.setBackground(new Color(255, 165, 0));
                    button.setForeground(Color.WHITE);

                    if (previouslySelectedButton != null && previouslySelectedButton != button) {
                        previouslySelectedButton.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
                        previouslySelectedButton.setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
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
                button.setEnabled(false);
            }

            centerPanelCenter.add(button);
        }

        centerPanelCenter.revalidate();
        centerPanelCenter.repaint();
    }

    // Phương thức di chuyển đến tháng trước
    private void moveToPreviousMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateChooser.getDate());
        calendar.add(Calendar.MONTH, -1);
        dateChooser.setDate(calendar.getTime());
        updateCalendar();
    }

    // Phương thức di chuyển đến tháng sau
    private void moveToNextMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateChooser.getDate());
        calendar.add(Calendar.MONTH, 1);
        dateChooser.setDate(calendar.getTime());
        updateCalendar();
    }

    // Mở DailyPlan với ngày được chọn
    private void openDailyPlan(Date selectedDate) {
        SwingUtilities.invokeLater(() -> new DailyPlan(selectedDate, taskManager));
    }

    // Lớp JPanel hỗ trợ Gradient
    class GradientPanel extends JPanel {
        private Color startColor;
        private Color endColor;

        public GradientPanel(Color start, Color end) {
            this.startColor = start;
            this.endColor = end;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    // Phương thức tạo JButton với bo tròn góc và hệ màu đồng bộ
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setBorder(new RoundedBorder(15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 40));
        button.setFont(new Font("Roboto", Font.PLAIN, 14));
        
        // Bo tròn góc bằng cách sử dụng Border
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        return button;
    }

    // Phương thức tạo JButton với icon và bo tròn góc
    private JButton createRoundedButtonWithIcon(String text, Color bgColor, Color fgColor, String iconPath) {
        JButton button = new JButton(text, new ImageIcon(iconPath));
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setHorizontalTextPosition(SwingConstants.RIGHT);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 40));
        button.setFont(new Font("Roboto", Font.PLAIN, 14));
        
        // Bo tròn góc
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        return button;
    }

    // Phương thức chuyển đổi giữa Light Mode và Dark Mode
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            setDarkTheme();
        } else {
            setLightTheme();
        }
        SwingUtilities.updateComponentTreeUI(this);
    }

    private void setDarkTheme() {
        // Đổi màu nền cho frame
        getContentPane().setBackground(BACKGROUND_COLOR_DARK);

        // Đổi màu cho các panel và button
        // Cập nhật màu cho GradientPanel
        GradientBackgroundPanel gradientPanel = (GradientBackgroundPanel) getContentPane();
        gradientPanel.setStartColor(new Color(30, 30, 30));
        gradientPanel.setEndColor(new Color(50, 50, 50));
        gradientPanel.repaint();

        // Thay đổi màu cho các thành phần khác
        // Ví dụ cho topPanel và các button
        // Bạn cần bổ sung các thay đổi màu sắc tương tự cho tất cả các thành phần
    }

    private void setLightTheme() {
        // Đổi lại màu nền cho frame
        getContentPane().setBackground(new Color(245, 245, 245));

        // Đổi màu cho các panel và button về màu ban đầu
        // Cập nhật màu cho GradientPanel
        GradientBackgroundPanel gradientPanel = (GradientBackgroundPanel) getContentPane();
        gradientPanel.setStartColor(new Color(173, 216, 230));
        gradientPanel.setEndColor(new Color(216, 191, 216));
        gradientPanel.repaint();

        // Thay đổi màu cho các thành phần khác
        // Bạn cần bổ sung các thay đổi màu sắc tương tự cho tất cả các thành phần
    }

    // Phương thức áp dụng kiểu cho JSpinner
    private void styleSpinner(JSpinner spinner) {
        spinner.setUI(new javax.swing.plaf.basic.BasicSpinnerUI());
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            ((JSpinner.DefaultEditor) editor).getTextField().setFont(new Font("Roboto", Font.PLAIN, 14));
            ((JSpinner.DefaultEditor) editor).getTextField().setForeground(isDarkMode ? Color.WHITE : Color.BLACK);
            ((JSpinner.DefaultEditor) editor).getTextField().setBackground(isDarkMode ? new Color(70, 70, 70) : Color.WHITE);
            ((JSpinner.DefaultEditor) editor).getTextField().setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        }
    }

    // Lớp RoundedBorder để sử dụng cho các component có bo góc
    class RoundedBorder implements Border {
        private int radius;

        RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(this.radius+1, this.radius+1, this.radius+2, this.radius);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(100, 100, 100));
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.draw(new RoundRectangle2D.Float(x, y, width-1, height-1, radius, radius));
        }
    }

    // Lớp JPanel hỗ trợ Gradient cho toàn bộ nền
    class GradientBackgroundPanel extends JPanel {
        private Color startColor;
        private Color endColor;

        public GradientBackgroundPanel(Color start, Color end) {
            this.startColor = start;
            this.endColor = end;
            setLayout(new BorderLayout());
            setOpaque(false);
        }

        public void setStartColor(Color start) {
            this.startColor = start;
        }

        public void setEndColor(Color end) {
            this.endColor = end;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gp = new GradientPaint(0, 0, startColor, 0, height, endColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }

    // Phương thức áp dụng kiểu cho các nút ngày
    private void styleDayButton(JButton button) {
        button.setFont(new Font("Roboto", Font.PLAIN, 14));
        button.setFocusPainted(false);
        button.setBorder(new RoundedBorder(10));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Bo góc và thêm shadow
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(isDarkMode ? new Color(100, 100, 100) : new Color(220, 220, 220));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    if (button.getBackground().equals(new Color(34, 193, 195))) {
                        button.setBackground(new Color(34, 193, 195));
                    } else if (button.getBackground().equals(new Color(255, 165, 0))) {
                        button.setBackground(new Color(255, 165, 0));
                    } else {
                        button.setBackground(isDarkMode ? new Color(70, 70, 70) : new Color(240, 240, 240));
                    }
                }
            }
        });
    }
}