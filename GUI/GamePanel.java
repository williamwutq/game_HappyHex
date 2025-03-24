package GUI;

import Hex.*;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    public GamePanel() {
        super();
        // Variables
        this.setBackground(Color.WHITE);
        // Basic graphics
        this.setLayout(null); // Use absolute
        // Prepare
        HexButton.setSize(1);
        // Construct buttons
        for (int i = 0; i < GameEssentials.engine().length(); i++) {
            this.add(new EngineButton(i));
        }
    }
    public void paint(java.awt.Graphics g) {
        // Calculate minimum size
        double horizontalCount = GameEssentials.engine().getRadius() * 4 - 2;
        double verticalCount = GameEssentials.engine().getRadius() * 3 - 1;
        double minSize = Math.min(this.getHeight() / verticalCount, this.getWidth() / horizontalCount / GameEssentials.sinOf60 / 2);
        // New code: set size.
        HexButton.setSize(minSize);
        // print component and children
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
    public static void play (int size, int queueSize, int delay) {
        // Frame
        JFrame frame = new JFrame("Test: GamePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);
        frame.setAlwaysOnTop(true);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        // Engine
        HexEngine engine = new HexEngine(size);
        Queue queue = new Queue(queueSize);
        GameEssentials.setEngine(engine);
        GameEssentials.setQueue(queue);
        GameEssentials.setWindow(frame);
        GameEssentials.setDelay(delay);
        GamePanel gamePanel = new GamePanel();
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
