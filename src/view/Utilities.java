package view;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class Utilities {

    /**
     * Áp dụng màu sắc và kích thước cho JButton dựa trên chế độ Dark Mode.
     *
     * @param button      JButton cần áp dụng màu sắc.
     * @param isDarkMode  Trạng thái Dark Mode (true: Dark Mode, false: Light Mode).
     */
    public static void applyButtonStyle(JButton button, boolean isDarkMode) {
        if (isDarkMode) {
            button.setBackground(new Color(66, 66, 66));
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
        } else {
            button.setBackground(new Color(240, 240, 240));
            button.setForeground(Color.BLACK);
            button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        }

        // Áp dụng font nếu cần thiết
        button.setFont(new Font("Roboto", Font.PLAIN, 14));

        // Bo góc
        button.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(10),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        // Tăng kích thước nút nếu cần
        button.setPreferredSize(new Dimension(120, 40)); // Thay đổi kích thước theo nhu cầu
    }

    /**
     * Áp dụng màu sắc cho JLabel dựa trên chế độ Dark Mode.
     *
     * @param label        JLabel cần áp dụng màu sắc.
     * @param isDarkMode   Trạng thái Dark Mode (true: Dark Mode, false: Light Mode).
     */
    public static void applyLabelStyle(JLabel label, boolean isDarkMode) {
        if (isDarkMode) {
            label.setForeground(new Color(230, 230, 230));
        } else {
            label.setForeground(new Color(60, 60, 60));
        }

        // Áp dụng font nếu cần thiết
        label.setFont(new Font("Roboto", Font.PLAIN, 14));
    }

    /**
     * Áp dụng màu sắc cho JTable dựa trên chế độ Dark Mode.
     *
     * @param table        JTable cần áp dụng màu sắc.
     * @param isDarkMode   Trạng thái Dark Mode (true: Dark Mode, false: Light Mode).
     */
    public static void applyTableStyle(JTable table, boolean isDarkMode) {
        if (isDarkMode) {
            table.setBackground(new Color(33, 33, 33));
            table.setForeground(Color.WHITE);
            table.setGridColor(new Color(66, 66, 66));
            table.setSelectionBackground(new Color(75, 75, 75));
            table.setSelectionForeground(Color.WHITE);

            JTableHeader header = table.getTableHeader();
            header.setBackground(new Color(45, 45, 45));
            header.setForeground(Color.WHITE);
            header.setFont(new Font("Roboto", Font.BOLD, 14));
            header.setOpaque(false);
        } else {
            table.setBackground(Color.WHITE);
            table.setForeground(Color.BLACK);
            table.setGridColor(new Color(200, 200, 200));
            table.setSelectionBackground(new Color(220, 220, 220));
            table.setSelectionForeground(Color.BLACK);

            JTableHeader header = table.getTableHeader();
            header.setBackground(new Color(220, 220, 220));
            header.setForeground(Color.BLACK);
            header.setFont(new Font("Roboto", Font.BOLD, 14));
            header.setOpaque(false);
        }

        // Áp dụng font và chỉnh sửa các thuộc tính khác nếu cần
        table.setFont(new Font("Roboto", Font.PLAIN, 14));
        table.getTableHeader().setReorderingAllowed(false);
    }

    /**
     * Áp dụng màu sắc cho JPanel dựa trên chế độ Dark Mode.
     *
     * @param panel        JPanel cần áp dụng màu sắc.
     * @param isDarkMode   Trạng thái Dark Mode (true: Dark Mode, false: Light Mode).
     */
    public static void applyPanelStyle(JPanel panel, boolean isDarkMode) {
        if (isDarkMode) {
            panel.setBackground(new Color(45, 45, 45));
        } else {
            panel.setBackground(Color.WHITE);
        }

        // Áp dụng bo góc
        panel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(15),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    // Các phương thức tiện ích khác...

}
