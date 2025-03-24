package GUI;

import Hex.*;
import javax.swing.*;
import java.awt.*;

public class PiecePanel extends JPanel{
    public PiecePanel() {
        super();
        // Variables
        this.setBackground(Color.GRAY);
        // Basic graphics
        this.setLayout(null); // Use absolute
        int width = (int) Math.round(20 * HexButton.getActiveSize() * GameEssentials.sinOf60);
        int height = (int) Math.round(10 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        // Prepare
        HexButton.setSize(1);
        // Construct buttons
        for (int i = 0; i < GameEssentials.queue().getFirst().length(); i++) {
            //this.add(new PieceButton(i));
        }
    }
    public void paint(java.awt.Graphics g) {
        int width = (int) Math.round(20 * HexButton.getActiveSize() * GameEssentials.sinOf60);
        int height = (int) Math.round(10 * HexButton.getActiveSize());
        this.setPreferredSize(new Dimension(width, height));
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
}
