package GUI;

import javax.swing.*;
import java.awt.*;

public class HexPanel extends JPanel {
    private final int size;
    private final Color color;

    public HexPanel(int size, Color color) {
        this.size = size;
        this.color = color;
        Dimension dimension = new Dimension((int) Math.round(Math.sqrt(3) * size), 2 * size);
        setSize(dimension);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setBackground(new Color(0,0,0,0));
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        GameEssentials.paintHexagon(g, this.color, size);
    }

    public static void main(String[] args) {
        // Basic testing
        JFrame frame = new JFrame("Test: HexPanel");
        JPanel panel = new HexPanel(50, Color.BLUE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Sizes
        Dimension size = new Dimension(panel.getWidth(), panel.getHeight() + 28);
        frame.setSize(size);
        frame.setMinimumSize(size);
        frame.add(panel);
        // Show
        frame.setVisible(true);
    }
}
