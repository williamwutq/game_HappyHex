package GUI;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
    public GamePanel() {
        super();
        this.setBackground(Color.WHITE);
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
    public static boolean checkEnd(){
        for(int i = 0; i < GameEssentials.queue().length(); i ++){
            if(!GameEssentials.engine().checkPositions(GameEssentials.queue().get(i)).isEmpty()){
                return false;
            }
        }
        return true;
    }
}
