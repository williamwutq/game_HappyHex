package GUI;

import Hex.*;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    private HexEngine engine;

    public GamePanel(HexEngine engine) {
        super();
        // Variables
        this.engine = engine;
        this.setBackground(Color.WHITE);
        // Basic graphics
        this.setLayout(null); // Use absolute
        // Prepare
        HexButton.setSize(1);
        HexButton.setEngine(engine);
        // Construct buttons
        for (int i = 0; i < engine.length(); i++) {
            this.add(new HexButton(i));
        }
    }
    public void paint(java.awt.Graphics g) {
        // Calculate minimum size
        double horizontalCount = engine.getRadius() * 4 - 2;
        double verticalCount = engine.getRadius() * 3 - 1;
        double minSize = Math.min(this.getHeight() / verticalCount, this.getWidth() / horizontalCount / GameEssentials.sinOf60 / 2);
        // New code: set size.
        HexButton.setSize(minSize);
        // print component and children
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
    // Test: autoplay
    public static void autoplay(int size, int delay){
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
        GamePanel gamePanel = new GamePanel(engine);
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setVisible(true);
        // Autoplay
        int turns = 0;
        int score = 0;
        while (true) {
            Piece piece = Piece.generatePiece();
            try {
                engine.add(engine.checkPositions(piece).get((int) (Math.random() * engine.length())), piece);
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                try {
                    engine.add(engine.checkPositions(piece).get((int) (Math.random() * engine.length())), piece);
                } catch (IllegalArgumentException | IndexOutOfBoundsException e1) {
                    try {
                        engine.add(engine.checkPositions(piece).get((int) (Math.random() * engine.length())), piece);
                    } catch (IllegalArgumentException | IndexOutOfBoundsException e2) {
                        try {
                            engine.add(engine.checkPositions(piece).get(3), piece);
                        } catch (IllegalArgumentException | IndexOutOfBoundsException e3) {
                            try {
                                engine.add(engine.checkPositions(piece).get(0), piece);
                            } catch (IllegalArgumentException | IndexOutOfBoundsException e4) {
                                break;
                            }
                        }
                    }
                }
            }
            score += piece.length();
            gamePanel.repaint();
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {}
            score += engine.eliminate() * 3;
            gamePanel.repaint();
            turns++;
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {}
        }
        engine.reset();
        gamePanel.repaint();
        System.out.println("Auto Play: Game Over");
        System.out.println("This game lasted for " + turns + " turns.");
        System.out.println("The total score is " + score + " points.");
    }

    public static void main(String[] args) {
        autoplay(9, 200);
    }
}
