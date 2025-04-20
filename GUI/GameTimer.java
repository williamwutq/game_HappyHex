package GUI;

import javax.swing.*;
import java.awt.event.*;

public class GameTimer extends Timer implements ActionListener {
    public GameTimer() {
        super(GameEssentials.getDelay(), null);
        this.setRepeats(false);
        this.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        // Run animation
        hex.Block[] eliminated = GameEssentials.engine().eliminate();
        // Add animation
        for(hex.Block block : eliminated){
            GameEssentials.addAnimation(GameEssentials.createDisappearEffect(block));
            GameEssentials.addAnimation(GameEssentials.createCenterEffect(new hex.Block(block, GameEssentials.gameBlockDefaultColor)));
        }
        // Add score
        GameEssentials.incrementScore(5 * eliminated.length);
        // Check end after eliminate
        GameEssentials.checkEnd();
        GameEssentials.window().repaint();
    }
}
