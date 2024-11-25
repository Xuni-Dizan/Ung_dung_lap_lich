package view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DailyPlan extends JFrame {
    public DailyPlan() {
        // Thiết lập tiêu đề cửa sổ
        setTitle("Lịch trong ngày");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Panel chứa thanh điều hướng
        JPanel navigationPanel = new JPanel();
        navigationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Các nút và combo box ngày tháng
        JButton btnYesterday = new JButton("Hôm qua");
        JComboBox<String> cbDay = new JComboBox<>();
        JComboBox<String> cbMonth = new JComboBox<>();
        JComboBox<String> cbYear = new JComboBox<>();
        JButton btnTomorrow = new JButton("Ngày mai");

        // Thêm các lựa chọn ngày tháng vào combo box
        for (int i = 1; i <= 31; i++) {
            cbDay.addItem(String.valueOf(i));
        }
        for (int i = 1; i <= 12; i++) {
            cbMonth.addItem("Tháng " + i);
        }
        for (int i = 2000; i <= 2100; i++) {
            cbYear.addItem(String.valueOf(i));
        }

        // Đặt ngày hiện tại làm mặc định
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String[] currentDate = dateFormat.format(new Date()).split("-");
        cbDay.setSelectedItem(currentDate[0]);
        cbMonth.setSelectedItem("Tháng " + Integer.parseInt(currentDate[1]));
        cbYear.setSelectedItem(currentDate[2]);

        // Thêm các nút và combo box vào panel
        navigationPanel.add(btnYesterday);
        navigationPanel.add(cbDay);
        navigationPanel.add(cbMonth);
        navigationPanel.add(cbYear);
        navigationPanel.add(btnTomorrow);

        // Panel chính chứa nội dung
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JLabel emptyLabel = new JLabel(); // Nhãn trống để giữ bố cục
        mainPanel.add(emptyLabel, BorderLayout.CENTER);

        // Thêm panel vào cửa sổ chính
        add(navigationPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Tạo giao diện và hiển thị
        SwingUtilities.invokeLater(() -> {
            DailyPlan dailyPlan = new DailyPlan();
            dailyPlan.setVisible(true);
        });
    }
}
