package GUI;

import Hex.HexEngine;
import Hex.Queue;

import javax.swing.*;
import java.awt.*;

public class PiecePanel extends JPanel{
    public PiecePanel() {
        super();
        this.setBackground(GameEssentials.gamePiecePanelColor);
        this.setLayout(null); // Use absolute
        int width = (int) Math.round(5 * HexButton.getActiveSize() * GameEssentials.sinOf60);
        int height = (int) Math.round(5 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        // Construct buttons
        for(int p = 0; p < GameEssentials.queue().length(); p ++) {
            for (int i = 0; i < 7; i++) {
                this.add(new PieceButton(p, i));
            }
        }
    }
    public void paint(java.awt.Graphics g) {
        int width = (int) Math.round(5 * HexButton.getActiveSize() * GameEssentials.sinOf60);
        int height = (int) Math.round(5 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
}
