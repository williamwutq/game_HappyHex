package GUI;

import javax.swing.*;
import java.awt.*;

public class HexPanel extends JPanel {
    private final int size;
    private final Color color;

    public HexPanel(int size, Color color) {
        this.size = size;
        this.color = color;
        int width = (int) Math.round(Math.sqrt(3) * size);
        int height = 2 * size;
        setPreferredSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));
        setBackground(new Color(0,0,0,0));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        int[] xPoints = new int[6];
        int[] yPoints = new int[6];

        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i);
            xPoints[i] = (int) (width / 2 + size * Math.sin(angle));
            yPoints[i] = (int) (height / 2 + size * Math.cos(angle));
        }

        Polygon hexagon = new Polygon(xPoints, yPoints, 6);
        g.setColor(color);
        g.fillPolygon(hexagon);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Test: HexPanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new HexPanel(50, Color.BLUE));
        frame.setSize(100, 150);
        frame.setVisible(true);
    }
}
