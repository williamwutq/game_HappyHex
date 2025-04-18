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
        GameEssentials.incrementScore(5 * GameEssentials.engine().eliminate());
        // Check end after eliminate
        GameEssentials.checkEnd();
        GameEssentials.window().repaint();
    }
}
