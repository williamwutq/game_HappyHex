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
    }

    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        // Calculate minimum size
        double horizontalCount = engine.getRadius() * 4 - 2;
        double verticalCount = engine.getRadius() * 3 - 1;
        double minSize = Math.min(this.getHeight() / verticalCount, this.getWidth() / horizontalCount / GameEssentials.sinOf60 / 2);
        // paint every hexagon
        for (Block block : engine.blocks()) {
            GameEssentials.paintHexagon(g, block.color(), block.X(), block.Y() - 1 + verticalCount / 4, minSize, 0.9);
        }
    }

    public static void main(String[] args) {
        // Frame
        JFrame frame = new JFrame("Test: GamePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.BLACK);
        frame.setMaximumSize(new Dimension(800, 800));
        frame.setSize(new Dimension(500, 500));
        frame.setMinimumSize(new Dimension(200, 200));

        // Engine
        HexEngine engine = new HexEngine(7);
        GamePanel gamePanel = new GamePanel(engine);
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setVisible(true);
        // Autoplay
        int turns = 0;
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
            gamePanel.repaint();
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            engine.eliminate();
            gamePanel.repaint();
            turns++;
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
        }
        engine.reset();
        gamePanel.repaint();
        System.out.println("Auto Play: Game Over");
        System.out.println("This game lasted for " + turns + " turns.");
    }
}
