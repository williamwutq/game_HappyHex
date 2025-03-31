package Launcher;

import java.awt.event.ActionEvent;

public class StartButton extends LaunchButton{
    public StartButton() {
        super("START");
    }

    public void actionPerformed(ActionEvent e) {
        LaunchEssentials.startGame();
    }
}
