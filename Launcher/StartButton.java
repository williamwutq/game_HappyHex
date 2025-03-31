package Launcher;

import java.awt.*;

public class StartButton extends LaunchButton{
    public StartButton() {
        super("START");
    }

    @Override
    protected void clicked() {
        LaunchEssentials.startGame();
    }

    protected Color fetchColor() {
        return LaunchEssentials.launchStartButtonBackgroundColor;
    }
}
