package view;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new CalendarGUI();  // Tạo và hiển thị cửa sổ CalendarGUI
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
