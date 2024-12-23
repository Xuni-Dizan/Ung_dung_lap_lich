package view;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import java.awt.BorderLayout;

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
