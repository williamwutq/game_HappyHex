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
        // Construct labels
        GameInfoPanel turnLabel = new GameInfoPanel();
        GameInfoPanel scoreLabel = new GameInfoPanel();
        GameInfoPanel playerLabel = new GameInfoPanel();
        turnLabel.setTitle("TURN");
        scoreLabel.setTitle("SCORE");
        playerLabel.setTitle("PLAYER");
        turnLabel.setBounds(0, 0, 100, 100);
        scoreLabel.setBounds(300, 0, 100, 100);
        playerLabel.setBounds(300, 300, 100, 100);
        this.add(turnLabel);
        this.add(scoreLabel);
        this.add(playerLabel);
    }
    public void paint(java.awt.Graphics g) {
        GameEssentials.calculateButtonSize();
        // print component and children
        g.setColor(this.getBackground());
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintChildren(g);
    }
}
