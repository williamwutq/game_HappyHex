package GUI;

import javax.swing.*;

public class GamePanel extends JPanel {
    public GamePanel() {
        super();
        this.setBackground(GameEssentials.gameBackGroundColor);
        this.setLayout(null);
        // Construct buttons
        for (int i = 0; i < GameEssentials.engine().length(); i++) {
            this.add(new EngineButton(i));
        }
    }
    public void paint(java.awt.Graphics g) {
        GameEssentials.calculateButtonSize();
        // print component and children
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
}
