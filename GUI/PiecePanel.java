package GUI;

import Hex.HexEngine;
import Hex.Queue;

import javax.swing.*;
import java.awt.*;

public class PiecePanel extends JPanel{
    public PiecePanel() {
        super();
        // Variables
        this.setBackground(Color.GRAY);
        // Basic graphics
        this.setLayout(null); // Use absolute
        int width = (int) Math.round(10 * HexButton.getActiveSize() * GameEssentials.sinOf60);
        int height = (int) Math.round(5 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        // Construct buttons
        for(int p = 0; p < GameEssentials.queue().length(); p ++) {
            for (int i = 0; i < GameEssentials.queue().get(p).length(); i++) {
                this.add(new PieceButton(p, i));
            }
        }
    }
    public void paint(java.awt.Graphics g) {
        int width = (int) Math.round(10 * HexButton.getActiveSize() * GameEssentials.sinOf60);
        int height = (int) Math.round(5 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
    public static void main(String[] args){
        // Frame
        JFrame frame = new JFrame("Test: PiecePanel");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.WHITE);
        frame.setSize(new Dimension(800, 800));
        frame.setMinimumSize(new Dimension(200, 200));

        HexButton.setSize(50);
        GameEssentials.setQueue(new Queue(5));
        GameEssentials.setEngine(new HexEngine(5));
        PiecePanel piecePanel = new PiecePanel();
        frame.add(piecePanel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }
}
