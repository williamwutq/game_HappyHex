package GUI;

import Hex.*;
import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel{
    private HexEngine engine;
    public GamePanel(HexEngine engine){
        super();
        // Variables
        this.engine = engine;
        this.setBackground(Color.WHITE);
    }
    public void paintComponent(java.awt.Graphics g){
        super.paintComponent(g);
        // Calculate minimum size
        double horizontalCount = engine.getRadius() * 4 - 2;
        double verticalCount = engine.getRadius() * 3 - 1;
        double minSize = Math.min(this.getHeight()/verticalCount, this.getWidth()/horizontalCount/GameEssentials.sinOf60/2);
        // paint every hexagon
        for(Block block : engine.blocks()){
            GameEssentials.paintHexagon(g, block.color(), block.X(), block.Y() + engine.getRadius() * GameEssentials.sinOf60, minSize, 0.9);
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
        HexEngine engine = new HexEngine(4);
        Piece piece1 = Piece.generatePiece();
        Piece piece2 = Piece.generatePiece();
        engine.add(engine.checkPositions(piece1).get(1), piece1);
        engine.add(engine.checkPositions(piece2).getFirst(), piece2);
        System.out.println("Pieces: " + piece1 + " and " + piece2);
        System.out.println("Engine: " + engine);

        // GamePanel
        GamePanel gamePanel = new GamePanel(engine);
        frame.add(gamePanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
