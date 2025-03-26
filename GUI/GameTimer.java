package GUI;

import javax.swing.*;
import java.awt.event.*;

public class GameTimer extends Timer implements ActionListener {
    public GameTimer() {
        super(GameEssentials.getDelay(), null);
        this.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        GameEssentials.score += 5 * GameEssentials.engine().eliminate();
        GameEssentials.window().repaint();
    }
}
