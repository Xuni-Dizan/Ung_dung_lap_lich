// src/view/DropShadowBorder.java
package view;

import javax.swing.border.Border;
import java.awt.*;

public class DropShadowBorder implements Border {
    private int size;
    private Color color;

    public DropShadowBorder(int size, Color color) {
        this.size = size;
        this.color = color;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(0, 0, size, 0);
    }

    @Override
    public boolean isBorderOpaque() {
        return false;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        for(int i = 0; i < size; i++) {
            g2.drawRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1);
        }
    }
}